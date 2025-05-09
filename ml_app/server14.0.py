# coding=UTF-8
import pandas as pd
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity
from flask import Flask, request, jsonify, make_response, Response, stream_with_context
from flask_cors import CORS
import time
from zhipuai import ZhipuAI
import pymysql
import json
import re
import jieba
import numpy as np


app = Flask(__name__)
cors = CORS(app)


def record_time(func):
    """
    用于记录函数运行时间的装饰器函数
    """

    def wrapper(*args, **kwargs):
        # 获取函数名
        func_name = func.__name__
        # 记录函数开始时间
        start_time = time.time()
        # 调用原函数
        result = func(*args, **kwargs)
        # 记录函数结束时间
        end_time = time.time()
        # 计算函数运行时间
        elapsed_time = end_time - start_time
        print(f"{func_name} 函数运行时间: {elapsed_time:.4f} 秒")
        return result

    return wrapper


def tokenize(text):
    # 将换行符替换为空格
    text = text.replace('\n', ' ')
    return " ".join(jieba.cut(text))


def parse_salary(salary_str):
    # 处理薪资字符串，返回最小和最大薪资
    if '面议' in salary_str:
        return None, None  # 面议情况返回 None

    match = re.findall(r'\d+', salary_str)

    if len(match) == 2:
        min_salary, max_salary = map(int, match)
        return min_salary, max_salary  # Convert K to actual values
    elif len(match) == 1:
        # Handle case like '10K' (single value)
        min_salary = int(match[0])
        return min_salary, min_salary
    else:
        return None, None


@record_time
def match_salary(data, matched_ids):
    if "salary" in data:
        header_salary=data['salary']
        min_header_salary, max_header_salary = parse_salary(header_salary)
        if min_header_salary is None and max_header_salary is None:
            # If header_salary is '面议' or similar, all IDs are valid
            return matched_ids

        filtered_ids = []
        for id in matched_ids:
            salary_y_str = df[df['id'] == id]['salary_y'].values[0]
            min_salary, max_salary = parse_salary(salary_y_str)

            if min_salary is None and max_salary is None:
                # If salary_y is '面议', consider it a match
                filtered_ids.append(id)
            elif min_header_salary is not None and max_header_salary is not None and min_salary is not None and max_salary is not None:
                # Check if the header_salary range is within salary_y range
                if min_header_salary <= min_salary and max_header_salary >= max_salary:
                    filtered_ids.append(id)

        return filtered_ids
    elif 'expected_salary' in data:
        expect_salary = data['expect_salary']
        if expect_salary == '面议':
            return matched_ids
        elif '-' in expect_salary:  # 形如10-20K
            min_salary, max_salary = map(int, expect_salary[:-1].split('-'))
            return [id for id in matched_ids if
                    any(min_salary <= salary <= max_salary for salary in df[df['id'] == id]['salary'])]
        elif 'K' in expect_salary:  # 形如10K
            min_salary = int(expect_salary[:-1])
            return [id for id in matched_ids if any(salary >= min_salary for salary in df[df['id'] == id]['salary'])]
        else:
            return matched_ids
    else:
        return matched_ids

@record_time
def match_education(data):
    matched_ids = []
    if 'degree' in data:
        degree = data['degree']
        degree_index = degree_order.index(degree)
        for index, row in df.iterrows():
            if degree_order.index(row['education']) <= degree_index:
                matched_ids.append(row['id'])
        return matched_ids
    else:
        return df['id'].tolist()


@record_time
def match_city(data, matched_ids):
    if "ideal_city" in data:
        header_cities=data['ideal_city']
        # 处理理想城市列表
        ideal_cities = header_cities.strip('[]').split(',')
        ideal_cities = [city.strip().strip('"') for city in ideal_cities]

        # 匹配城市
        filtered_ids = []
        for id in matched_ids:
            city = df[df['id'] == id]['city'].values[0]
            dizhi = df[df['id'] == id]['dizhi'].values[0] if 'dizhi' in df.columns else None

            # 如果城市是 'dizhi' 或在理想城市中，则保留该 ID
            if dizhi == 'dizhi' or city in ideal_cities:
                filtered_ids.append(id)

        return filtered_ids
    else:
        return matched_ids


@record_time
def find_top_matching_ids(match_ids, data,cursor,uid):
    sql_check_existence = "SELECT uid FROM user_vector WHERE uid = %s"
    cursor.execute(sql_check_existence, (int(uid),))
    # 生成输入文本
    if "cont_my_desc" in data and "cont_job_exp" in data and "cont_job_skill" in data:
        input_text = data["cont_my_desc"] + data["cont_job_exp"] + data["cont_job_skill"]
    elif "cont_my_desc" in data and "cont_job_exp" in data:
        input_text = data["cont_my_desc"] + data["cont_job_exp"]
    elif "cont_my_desc" in data and "cont_job_skill" in data:
        input_text = data["cont_my_desc"] + data["cont_job_skill"]
    elif "cont_job_exp" in data and "cont_job_skill" in data:
        input_text = data["cont_job_exp"] + data["cont_job_skill"]
    elif "cont_my_desc" in data:
        input_text = data["cont_my_desc"]
    elif "cont_job_exp" in data:
        input_text = data["cont_job_exp"]
    elif "cont_job_skill" in data:
        input_text = data["cont_job_skill"]
    else:
        input_text = data.get("raw_text", "")

    # Add handling for "proj_content" if available
    if "proj_exp_objs" in data and "cont_job_exp" not in data and isinstance(data["proj_exp_objs"], list) and len(
            data["proj_exp_objs"]) > 0:
        proj_contents = [proj["proj_content"] for proj in data["proj_exp_objs"]]
        proj_contents_text = "\n".join(proj_contents)
        input_text += "\n" + proj_contents_text

    input_word = tokenize(input_text)

    # 计算输入文本与所有职位描述的相似度
    input_tfidf = vectorizer.transform([input_word])
    input_tfidf_dense = input_tfidf.toarray().tolist()
    input_tfidf_json = json.dumps(input_tfidf_dense)
    if cursor.rowcount == 0:
        uid_int = int(uid)
        sql_insert = "INSERT INTO user_vector (uid, user_vector) VALUES (%s, %s)"
        # 插入数据库
        cursor.execute(sql_insert, (uid_int, input_tfidf_json))
    similarity_scores = cosine_similarity(input_tfidf, tfidf_matrix)

    # 获取所有匹配ID及其相似度分数
    matched_scores = []
    for idx in range(len(df)):
        job_id = df.iloc[idx]['id']
        if job_id in match_ids:
            score = similarity_scores[0, idx]
            matched_scores.append((job_id, score))

    # 按照匹配度进行排序
    matched_scores.sort(key=lambda x: x[1], reverse=True)

    # 对匹配度进行评分转换
    def scale_score(score):
        if score > 0.5:
            return min(95 + (score - 0.5) * 100, 100)  # 高匹配度
        elif 0.4 < score <= 0.5:
            return 85 + (score - 0.4) * 100  # 中高匹配度
        elif 0.3 < score <= 0.4:
            return 75 + (score - 0.3) * 100  # 中匹配度
        elif 0.2 < score <= 0.3:
            return 65 + (score - 0.2) * 100  # 中低匹配度
        elif 0.1 < score <= 0.2:
            return 55 + (score - 0.1) * 100  # 低匹配度
        else:
            return score * 100  # 极低匹配度

    # 将评分转换并返回前20个结果
    scored_results = [(job_id, scale_score(score)) for job_id, score in matched_scores]

    # 按评分排序
    scored_results.sort(key=lambda x: x[1], reverse=True)

    return scored_results[:20]

@record_time
def find_top_matching_ids_n(match_ids, data,cursor,uid):
    sql_check_existence = "SELECT uid FROM user_vector WHERE uid = %s"
    cursor.execute(sql_check_existence, (int(uid),))
    # 生成输入文本
    if "work_experience" in data and "awards" in data and "skills" in data and "self_judge" in data:
        input_text = data["work_experience"] + data["awards"] + data["skills"] + data["self_judge"]
    elif "work_experience" in data and "awards" in data and "skills" in data:
        input_text = data["work_experience"] + data["awards"] + data["skills"]
    elif "work_experience" in data and "awards" in data and "self_judge" in data:
        input_text = data["work_experience"] + data["awards"] + data["self_judge"]
    elif "work_experience" in data and "skills" in data and "self_judge" in data:
        input_text = data["work_experience"] + data["skills"] + data["self_judge"]
    elif "awards" in data and "skills" in data and "self_judge" in data:
        input_text = data["awards"] + data["skills"] + data["self_judge"]
    elif "work_experience" in data and "awards" in data:
        input_text = data["work_experience"] + data["awards"]
    elif "work_experience" in data and "skills" in data:
        input_text = data["work_experience"] + data["skills"]
    elif "work_experience" in data and "self_judge" in data:
        input_text = data["work_experience"] + data["self_judge"]
    elif "awards" in data and "skills" in data:
        input_text = data["awards"] + data["skills"]
    elif "awards" in data and "self_judge" in data:
        input_text = data["awards"] + data["self_judge"]
    elif "skills" in data and "self_judge" in data:
        input_text = data["skills"] + data["self_judge"]
    elif "work_experience" in data:
        input_text = data["work_experience"]
    elif "awards" in data:
        input_text = data["awards"]
    elif "skills" in data:
        input_text = data["skills"]
    elif "self_judge" in data:
        input_text = data["self_judge"]
    else:
        input_text = data.get("self_info", "")

    input_word = tokenize(input_text)

    # 计算输入文本与所有职位描述的相似度
    input_tfidf = vectorizer.transform([input_word])
    input_tfidf_dense = input_tfidf.toarray().tolist()
    input_tfidf_json = json.dumps(input_tfidf_dense)
    if cursor.rowcount == 0:
        uid_int = int(uid)
        sql_insert = "INSERT INTO user_vector (uid, user_vector) VALUES (%s, %s)"
        # 插入数据库
        cursor.execute(sql_insert, (uid_int, input_tfidf_json))
    similarity_scores = cosine_similarity(input_tfidf, tfidf_matrix)

    # 获取所有匹配ID及其相似度分数
    matched_scores = []
    for idx in range(len(df)):
        job_id = df.iloc[idx]['id']
        if job_id in match_ids:
            score = similarity_scores[0, idx]
            matched_scores.append((job_id, score))

    # 按照匹配度进行排序
    matched_scores.sort(key=lambda x: x[1], reverse=True)

    # 对匹配度进行评分转换
    def scale_score(score):
        if score > 0.5:
            return min(95 + (score - 0.5) * 10, 100)  # 高匹配度
        elif 0.4 < score <= 0.5:
            return 85 + (score - 0.4) * 100  # 中高匹配度
        elif 0.3 < score <= 0.4:
            return 75 + (score - 0.3) * 100  # 中匹配度
        elif 0.2 < score <= 0.3:
            return 65 + (score - 0.2) * 100  # 中低匹配度
        elif 0.1 < score <= 0.2:
            return 55 + (score - 0.1) * 100  # 低匹配度
        else:
            return score * 100  # 极低匹配度

    # 将评分转换并返回前20个结果
    scored_results = [(job_id, scale_score(score)) for job_id, score in matched_scores]

    # 按评分排序
    scored_results.sort(key=lambda x: x[1], reverse=True)

    return scored_results[:20]

@record_time
def user_recommendation(result,data):
    # 对输入文本进行分词和向量化
    input_word = tokenize(data['description'])
    input_tfidf = vectorizer.transform([input_word])
    # 计算输入文本与数据库中每个用户向量的相似度
    similarities = []
    for row in result:
        uid, vector_json = row
        vector_data = json.loads(vector_json)
        user_vector = vector_data[0]  # 直接使用向量列表
        similarity = cosine_similarity(input_tfidf, [user_vector])
        similarities.append((uid, similarity[0][0]))

    # 对相似度进行排序，并返回前四个最相似的uid
    similarities.sort(key=lambda x: x[1], reverse=True)
    top_similar_uids = [uid for uid, _ in similarities[:4]]
    # 输出结果
    print("前四相似度最高的uid：", top_similar_uids)

    return top_similar_uids

@record_time
def ability_evaluation(uid,aid,cursor):
    # 执行SQL查询
    sql_query = "SELECT capacity FROM user_capacity WHERE uid = " + uid
    cursor.execute(sql_query)

    # 获取查询结果
    result = cursor.fetchone()
    # 打印结果
    if result:
        data = json.loads(result[0])
        if "cont_my_desc" in data and "cont_job_exp" in data and "cont_job_skill" in data:
            input_text = data["cont_my_desc"] + data["cont_job_exp"] + data["cont_job_skill"]
        elif "cont_my_desc" in data and "cont_job_exp" in data:
            input_text = data["cont_my_desc"] + data["cont_job_exp"]
        elif "cont_my_desc" in data and "cont_job_skill" in data:
            input_text = data["cont_my_desc"] + data["cont_job_skill"]
        elif "cont_job_exp" in data and "cont_job_skill" in data:
            input_text = data["cont_job_exp"] + data["cont_job_skill"]
        elif "cont_my_desc" in data:
            input_text = data["cont_my_desc"]
        elif "cont_job_exp" in data:
            input_text = data["cont_job_exp"]
        elif "cont_job_skill" in data:
            input_text = data["cont_job_skill"]
        else:
            input_text = data["raw_text"]
    else:
        print("No data found for uid = " + uid)
        return "No data found for uid = "+uid
    print(aid)
    description = df[df['id'] == int(aid)]['description'].values[0]

    return input_text,description

def fill_resume(uid,cursor):
    # 执行SQL查询
    query = "SELECT self_info FROM user_no_resume WHERE id = %s"
    cursor.execute(query, (uid,))
    # 获取查询结果
    result = cursor.fetchone()
    if result:
        self_info = result[0]
        return self_info
    else:
        print(f"No self_info found for ID {uid}")
        return None

def convert_int64_to_int(data):
    return [(int(job_id), int(score)) for job_id, score in data]

@app.route('/<endpoint_path>', methods=['POST'])
def process_text(endpoint_path):
    # 检查请求头是否包含X-ML-Request并设置为true，用于后端过滤器放行
    if request.headers.get('X-ML-Request') != 'true':
        response = make_response(jsonify({"error": "Invalid ML Request Header"}))
        response.headers['X-ML-Request'] = 'true'
        return response
    userId = request.headers.get('userId')
    abilityId = request.headers.get('abilityId')
    print(userId)
    # 根据 endpoint_path 进行不同处理
    if endpoint_path == 'ml_service_endpoint':
        conn = pymysql.connect(
            host='172.27.23.51',
            port=3306,
            user='root',
            password='8508359',
            database='job_recommendation'
        )
        # 创建游标对象
        cursor = conn.cursor()
        data = request.get_json()
        matched_ids = match_education(data)
        filtered_ids = match_salary(data, matched_ids)
        city_filtered_ids = match_city(data, filtered_ids)

        # 获取 top_20_ids
        top_20_ids_with_scores = find_top_matching_ids(city_filtered_ids, data,cursor,userId)

        # 从 scored_results 提取 job_id 列表
        top_20_ids = [job_id for job_id, score in top_20_ids_with_scores]

        # 活跃度排序并调整推荐度
        matching_rows = df[df['id'].isin(top_20_ids)]

        activity_scores = {
            '在线': 5, '刚刚活跃': 5, '今日活跃': 5, '3日内活跃': 5, '本周活跃': 5,
            '2周内活跃': 4, '本月活跃': 4,
            '2月内活跃': 3, '3月内活跃': 3, '4月内活跃': 3,
            '5月内活跃': 2, '近半年活跃': 2,
            '半年前活跃': 1, '其他': 1
        }

        # 增加推荐度并取整
        scored_results_with_activity = [(job_id, min(int(score) + activity_scores[
            matching_rows.loc[matching_rows['id'] == job_id, 'last_active'].values[0]], 100)) for job_id, score in
                                        top_20_ids_with_scores]

        # 按推荐度排序
        scored_results_with_activity.sort(key=lambda x: x[1], reverse=True)

        sorted_top_20_ids = [job_id for job_id, score in scored_results_with_activity]
        print(sorted_top_20_ids)
        scored_results_with_activity = convert_int64_to_int(scored_results_with_activity)
        # 创建响应对象
        response = make_response(jsonify({"result": scored_results_with_activity}))
        response.headers['X-ML-Request'] = 'true'
        conn.commit()
        cursor.close()
        conn.close()
        return response
    elif endpoint_path == 'ability_evaluation':
        conn = pymysql.connect(
            host='172.27.23.51',
            port=3306,
            user='root',
            password='8508359',
            database='job_recommendation'
        )
        # 创建游标对象
        cursor = conn.cursor()
        input_text, description=ability_evaluation(userId,abilityId,cursor)
        response = client.chat.completions.create(
            model="glm-4",  # 填写需要调用的模型名称
            messages=[
                {"role": "system",
                 "content": "你需要做的是给出一位求职者的能力评价，分析出该求职者与该职位要求之间的差距，指出他的不足并且给出建议帮助这位求职者得到这份工作，将给你一段结构如“该职位的职位描述”+“||”+“求职者简历字段”的信息，“||”作为二者之间的分隔符。请综合评价求职者，给出书面的中文表达，请注意你直接对话的对象就是求职者，我将使用流式传输，请保证完整性"},
                {"role": "user", "content": description + "||" + input_text},

            ],
            stream=True,
        )

        @stream_with_context
        def generate():
            for chunk in response:
                content =str(chunk.choices[0].delta)
                if "''" in content:
                    pairs = re.findall(r'(\w+)\s*=\s*("[^"]*"|\S+)', content)
                else:
                    pairs = re.findall(r'(\w+)\s*=\s*([^,]+)', content)
                # 创建一个空字典
                data = {}

                # 遍历键值对，将其添加到字典中
                for key, value in pairs:
                    # 去除值的引号
                    value = value.strip("'")
                    data[key] = value

                # 将字典转换成你想要的字符串格式
                result = str(data).replace("'", '"') + ','
                yield  result  # 将每个块作为流输出

                print(result)

        cursor.close()
        conn.close()
        return Response(generate(), mimetype='text/event-stream')
    elif endpoint_path == 'fill':
        conn = pymysql.connect(
            host='172.27.23.51',
            port=3306,
            user='root',
            password='8508359',
            database='job_recommendation'
        )
        # 创建游标对象
        cursor = conn.cursor()
        input_text = fill_resume(str(userId),  cursor)
        response = client.chat.completions.create(
            model="glm-4",  # 填写需要调用的模型名称
            messages=[
                {"role": "system",
                 "content": "接下来我将会传给你一段信息，你的任务是请从中提取有效信息，分别为姓名、年龄、性别、出生日期、现居城市、需求薪资、需求职位、联系方式、学历、学校、专业、教育背景、工作经历、工作类型、工作公司、工作时间、岗位、工作内容、奖项荣誉、个人技能、自我评价。这些信息均是为了作为简历信息使用，请进行适当的填充删改使其贴合简历形式，最后将你处理后得到的信息按上述类别传回，格式示例：'''plaintext\n姓名：张三|出生日期：2024/1/1|现居地址：请提供更多有效信息\n'''   即使用|作为分隔符分开每一类，若给出的信息中不包含上述类别的某一类，则在该类返回“请提供更多有效信息”，注意如工作经历自我评价这两类，可以根据获取的信息，用你的话扩充一下内容"},
                {"role": "user", "content": input_text},

            ],
        )
        response_content = response.choices[0].message.content
        plaintext_match = re.search(r"```plaintext\n(.*?)\n```", response_content, re.DOTALL)

        if plaintext_match:
            plaintext = plaintext_match.group(1)
            # 使用正则表达式提取键值对，确保处理换行符
            data = {}
            lines = re.split(r'[\n|]', plaintext)
            for line in lines:
                if '：' in line:
                    key, value = line.split('：', 1)
                    data[key.strip()] = value.strip()

            print("提取的键值对：")
            print(data)
        else:
            print("没有找到 `plaintext` 内容")
            data = None
        response = make_response(jsonify({"result": data}))
        response.headers['X-ML-Request'] = 'true'
        cursor.close()
        conn.close()
        return response
    elif endpoint_path == 'user_recommendation':

        conn = pymysql.connect(
            host='172.27.23.51',
            port=3306,
            user='root',
            password='8508359',
            database='job_recommendation'
        )

        # 创建游标对象
        cursor = conn.cursor()

        # 读取user_vector表中的数据
        sql_query = "SELECT uid, user_vector FROM user_vector"
        cursor.execute(sql_query)
        result = cursor.fetchall()
        data = request.get_json()
        top_similar_uids=user_recommendation(result,data)
        cursor.close()
        conn.close()
        response = make_response(jsonify({"result": top_similar_uids}))
        response.headers['X-ML-Request'] = 'true'
        return response
    elif endpoint_path == 'no_resume_service_endpoint':
        conn = pymysql.connect(
            host='172.27.23.51',
            port=3306,
            user='root',
            password='8508359',
            database='job_recommendation'
        )
        # 创建游标对象
        cursor = conn.cursor()
        data = request.get_json()
        matched_ids = match_education(data)
        filtered_ids = match_salary(data, matched_ids)
        city_filtered_ids = match_city(data, filtered_ids)
        # 获取 top_20_ids
        top_20_ids_with_scores = find_top_matching_ids_n(city_filtered_ids, data, cursor, userId)

        # 从 scored_results 提取 job_id 列表
        top_20_ids = [job_id for job_id, score in top_20_ids_with_scores]

        # 活跃度排序并调整推荐度
        matching_rows = df[df['id'].isin(top_20_ids)]

        activity_scores = {
            '在线': 5, '刚刚活跃': 5, '今日活跃': 5, '3日内活跃': 5, '本周活跃': 5,
            '2周内活跃': 4, '本月活跃': 4,
            '2月内活跃': 3, '3月内活跃': 3, '4月内活跃': 3,
            '5月内活跃': 2, '近半年活跃': 2,
            '半年前活跃': 1, '其他': 1
        }

        # 增加推荐度并取整
        scored_results_with_activity = [(job_id, min(int(score) + activity_scores[
            matching_rows.loc[matching_rows['id'] == job_id, 'last_active'].values[0]], 100)) for job_id, score in
                                        top_20_ids_with_scores]

        # 按推荐度排序
        scored_results_with_activity.sort(key=lambda x: x[1], reverse=True)

        sorted_top_20_ids = [job_id for job_id, score in scored_results_with_activity]
        print(sorted_top_20_ids)
        scored_results_with_activity = convert_int64_to_int(scored_results_with_activity)
        # 创建响应对象
        response = make_response(jsonify({"result": scored_results_with_activity}))
        response.headers['X-ML-Request'] = 'true'
        conn.commit()
        cursor.close()
        conn.close()
        return response

    else:
        response = make_response(jsonify({"error": "Invalid endpoint"}))
        response.headers['X-ML-Request'] = 'true'
        return response


if __name__ == '__main__':
    degree_order = ['大专', '本科', '硕士', '博士']
    df = pd.read_csv('final_5.csv')
    client = ZhipuAI(api_key="de99fbc052eb15a609b5d32809f700d4.uBvABcSX9ulv8xbm")
    vectorizer = TfidfVectorizer()

    vectorizer.set_params(
        stop_words=None,  # 不删除任何停用词，包括英文停用词
        max_df=0.8,  # 忽略出现在80%以上文档中的词语
        min_df=2,  # 忽略在少于2个文档中出现的词语
        sublinear_tf=True,  # 应用对数缩放，可能有助于放大非匹配部分的影响
        smooth_idf=True  # 平滑IDF权重，防止除以零错误
    )

    tfidf_matrix = vectorizer.fit_transform(df['tokenized_text'].apply(tokenize))

    app.run(host='0.0.0.0', port=5000)
