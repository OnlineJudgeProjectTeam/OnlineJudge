# 基于openjdk 镜像
FROM openjdk:19
# 将本地文件夹挂在到当前容器
VOLUME /usr/local/java/JavaProject/OnlineJudge/docker-project
# 复制文件到容器
ADD OnlineJudge-0.0.1-SNAPSHOT.jar app.jar
# 声明需要暴露的端口
EXPOSE 8070
# 配置容器启动后执行的命令
ENTRYPOINT ["sh", "-c", "java $PARAMS -Djava.security.egd=file:/dev/urandom -jar /app.jar"]