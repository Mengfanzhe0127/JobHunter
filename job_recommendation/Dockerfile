# 基础镜像
FROM openjdk:17
# FROM registry.cn-hangzhou.aliyuncs.com/openjdk:17

# 作者
MAINTAINER mfz

# 标签信息
LABEL author = mfz
# 设定时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 拷贝jar包
COPY job_recommendation-0.0.1-SNAPSHOT.jar /app.jar

# 暴露端口
EXPOSE 9996

# 入口
ENTRYPOINT ["java", "-jar", "/app.jar"]

#启动时命令
CMD ["java", "-jar", "/app.jar", "-DredisIp=10.7.136.173"]