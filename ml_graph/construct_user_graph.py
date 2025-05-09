# -*- coding: utf-8 -*-
from zhipuai import ZhipuAI
from flask import Flask, request, jsonify
from datetime import datetime
import json


def extract_personality_phrases(input_text):
    # 初始化智谱AI客户端
    api_key = "39d75eb1ab7b151f8dcb7d240e8420e5.nR4anZMJSfeM0l2F"
    client = ZhipuAI(api_key=api_key)
    response = client.chat.completions.create(
        model="glm-4",  # 填写需要调用的模型名称
        messages=[
            {"role": "user", "content": "作为一名信息提取专家，请提取出我给你的一段文字中的性格短语"},
            {"role": "assistant", "content": "当然，请告诉我这段文字是什么"},
            {"role": "user", "content": input_text},
            {"role": "assistant", "content": "还有其他要求吗"},
            {"role": "user", "content": "提取出这些关于个人性格的短语后，将他们以字符串列表的格式返回"},
            {"role": "user", "content": "注意：只需要输出字符串列表，其余信息都不需要,不要有相关的解释说明!!!!"}
        ],
    )

    # 返回提取的短语列表
    return json.loads(response.choices[0].message.content)


app = Flask(__name__)


@app.route('/neo4j_endpoint', methods=['POST'])
def neo4j_endpoint():
    # 获取请求头中的tag字段
    tag = request.headers.get('tag')

    # 获取POST请求中的JSON数据
    json_data = request.json

    # 初始化data，node，relation
    data = {
        'nodes': {
            'persons': [],
            'education': [],
            'skill': [],
            'award': [],
            'personality': [],
            'work_experience': []
        },
        'relationships': {
            'has_degree': [],
            'has_skill': [],
            'has_award': [],
            'has_personality': [],
            'has_work_experience': []
        }
    }

    if tag == '0':
        node_id = 1

        # node
        birth_date_str = json_data.get('birth')
        current_date = datetime.now()
        birth_date = datetime.strptime(birth_date_str, "%Y-%m-%d")
        age = current_date.year - birth_date.year - (
                (current_date.month, current_date.day) < (birth_date.month, birth_date.day))
        person_node = {
            "id": node_id,
            "label": json_data.get("user", "张三")["name"],
            "properties": {
                "age": age,
                "city": json_data.get("user", "无")["idealCity"],
                "contact": json_data.get("user", "无")["phone"]
            }
        }
        data['nodes']['persons'].append(person_node)
        node_id += 1

        education_node = {
            "id": node_id,
            "label": json_data.get("user", "无")["education"],
            "properties": {
                "school": json_data.get("user", "无")["school"],
                "major": json_data.get("user", "无")["major"]
            }
        }
        data['nodes']['education'].append(education_node)
        node_id += 1

        for sk in json_data.get("skills", "").split(",") if json_data.get("skills") else []:
            skill_node = {
                "id": node_id,
                "label": sk,
            }
            data['nodes']['skill'].append(skill_node)
            node_id += 1

        for aw in json_data.get("awards", "").split(",") if json_data.get("awards") else []:
            award_node = {
                "id": node_id,
                "label": aw,
            }
            data['nodes']['award'].append(award_node)
            node_id += 1

        input_text = json_data.get("selfJudge", "")
        selfjudge = extract_personality_phrases(input_text)
        for per in selfjudge:
            personality_node = {
                "id": node_id,
                "label": per,
            }
            data['nodes']['personality'].append(personality_node)
            node_id += 1

        work_experience_node = {
            "id": node_id,
            "label": "work_experience",
            "properties": {
                "description": json_data.get("workExperience", ""),
            }
        }
        data['nodes']['work_experience'].append(work_experience_node)
        node_id += 1

        # relation
        has_degree_relation = {
            "id": node_id,
            "type": "HAS_DEGREE",
            "startNode": 1,
            "endNode": 2
        }
        data['relationships']['has_degree'].append(has_degree_relation)
        node_id += 1

        for index in range(3, 3 + len(data['nodes']['skill'])):
            has_skill_relation = {
                "id": node_id,
                "type": "HAS_SKILL",
                "startNode": 1,
                "endNode": index
            }
            data['relationships']['has_skill'].append(has_skill_relation)
            node_id += 1

        for index in range(3 + len(data['nodes']['skill']),
                           3 + len(data['nodes']['skill']) + len(data['nodes']['award'])):
            has_award_relation = {
                "id": node_id,
                "type": "HAS_AWARD",
                "startNode": 1,
                "endNode": index
            }
            data['relationships']['has_award'].append(has_award_relation)
            node_id += 1

        for index in range(3 + len(data['nodes']['skill']) + len(data['nodes']['award']),
                           3 + len(data['nodes']['skill']) + len(data['nodes']['award']) + len(
                               data['nodes']['personality'])):
            has_personality_relation = {
                "id": node_id,
                "type": "HAS_PERSONALITY",
                "startNode": 1,
                "endNode": index,
            }
            data['relationships']['has_personality'].append(has_personality_relation)
            node_id += 1

        has_work_experience_relation = {
            "id": node_id,
            "type": "HAS_WORK_EXPERIENCE",
            "startNode": 1,
            "endNode": 3 + len(data['nodes']['skill']) + len(data['nodes']['award']) + len(
                               data['nodes']['personality']),
        }
        data['relationships']['has_work_experience'].append(has_work_experience_relation)
        node_id += 1

        return jsonify(data)

    elif tag == '1':
        node_id = 1

        # node
        person_node = {
            "id": node_id,
            "label": json_data.get("cont_basic_info", "张三").split('\n', 1)[0],
            "properties": {
                "age": json_data.get("age", "无"),
                "city": json_data.get("living_address", "无"),
                "contact": json_data.get("phone", "无")
            }
        }
        data['nodes']['persons'].append(person_node)
        node_id += 1

        education_node = {
            "id": node_id,
            "label": json_data.get("degree", "无"),
            "properties": {
                "school": json_data.get("education_objs", [{"edu_college": "无"}])[0]["edu_college"],
                "major": json_data.get("education_objs", [{"edu_major": "无"}])[0]["edu_major"]
            }
        }
        data['nodes']['education'].append(education_node)
        node_id += 1

        for sk in json_data.get("skills_objs", []):
            skill_node = {
                "id": node_id,
                "label": sk["skills_name"],
            }
            data['nodes']['skill'].append(skill_node)
            node_id += 1

        for aw in json_data.get("all_cert_objs", []):
            award_node = {
                "id": node_id,
                "label": aw["cert_name"],
            }
            data['nodes']['award'].append(award_node)
            node_id += 1

        input_text = json_data.get("cont_my_desc", "")
        selfjudge = extract_personality_phrases(input_text)
        for per in selfjudge:
            personality_node = {
                "id": node_id,
                "label": per,
            }
            data['nodes']['personality'].append(personality_node)
            node_id += 1

        for ex in json_data.get("job_exp_objs", []):
            work_experience_node = {
                "id": node_id,
                "label": ex["job_pos_type_p"],
                "properties": {
                    "company": ex.get("job_cpy", "无"),
                    "duration": ex.get("job_duration", "无"),
                    "position": ex.get("job_position", "无"),
                    "content": ex.get("job_content", "无")
                }
            }
            data['nodes']['work_experience'].append(work_experience_node)
            node_id += 1

        # relation
        has_degree_relation = {
            "id": node_id,
            "type": "HAS_DEGREE",
            "startNode": 1,
            "endNode": 2
        }
        data['relationships']['has_degree'].append(has_degree_relation)
        node_id += 1

        for index in range(3, 3 + len(data['nodes']['skill'])):
            has_skill_relation = {
                "id": node_id,
                "type": "HAS_SKILL",
                "startNode": 1,
                "endNode": index
            }
            data['relationships']['has_skill'].append(has_skill_relation)
            node_id += 1

        for index in range(3 + len(data['nodes']['skill']),
                           3 + len(data['nodes']['skill']) + len(data['nodes']['award'])):
            has_award_relation = {
                "id": node_id,
                "type": "HAS_AWARD",
                "startNode": 1,
                "endNode": index
            }
            data['relationships']['has_award'].append(has_award_relation)
            node_id += 1

        for index in range(3 + len(data['nodes']['skill']) + len(data['nodes']['award']),
                           3 + len(data['nodes']['skill']) + len(data['nodes']['award']) + len(
                               data['nodes']['personality'])):
            has_personality_relation = {
                "id": node_id,
                "type": "HAS_PERSONALITY",
                "startNode": 1,
                "endNode": index,
            }
            data['relationships']['has_personality'].append(has_personality_relation)
            node_id += 1

        for index in range(
                3 + len(data['nodes']['skill']) + len(data['nodes']['award']) + len(data['nodes']['personality']),
                3 + len(data['nodes']['skill']) + len(data['nodes']['award']) + len(data['nodes']['personality']) + len(
                    data['nodes']['work_experience'])):
            has_work_experience_relation = {
                "id": None,
                "type": "HAS_WORK_EXPERIENCE",
                "startNode": 1,
                "endNode": index,
            }
            data['relationships']['has_work_experience'].append(has_work_experience_relation)
            node_id += 1

        return jsonify(data)

    else:
        return jsonify({'error': 'Invalid tag value'}), 400


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5002)
