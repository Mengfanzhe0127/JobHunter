# 使用基础镜像（例如：Python 3.9）
FROM python:3.8
# 配置清华源下载加速器
RUN pip config set global.index-url https://pypi.tuna.tsinghua.edu.cn/simple
# 设置工作目录
WORKDIR /app

# 复制应用文件到容器中
COPY . .

# 安装所需依赖
RUN pip install -r requirements.txt

# 暴露端口（如果需要）
EXPOSE 5002

# 启动应用
CMD ["python", "construct_user_graph.py"]