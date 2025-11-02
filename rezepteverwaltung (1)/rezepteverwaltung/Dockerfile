# build image runs JAR produced by maven
FROM eclipse-temurin:17-jre-alpine
ARG JAR_FILE=target/rezepteverwaltung-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} /app/app.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","/app/app.jar"]
