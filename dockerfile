FROM openjdk:17-slim
COPY fullstack-tech-champs-p2-backend.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]