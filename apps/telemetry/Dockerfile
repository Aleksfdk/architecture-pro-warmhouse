FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY Server.java .
RUN javac Server.java

EXPOSE 8083

CMD ["java", "Server"]
