FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/statistikdienst.jar statistikdienst.jar
EXPOSE 5004
ENTRYPOINT ["java", "-jar", "statistikdienst.jar"]
