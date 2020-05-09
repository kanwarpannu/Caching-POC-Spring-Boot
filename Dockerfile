FROM adoptopenjdk/openjdk8:alpine
ARG JAR_FILE=target/cache-demo-*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
EXPOSE 9101