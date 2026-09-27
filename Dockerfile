FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY lib/mysql-connector-j-26.7.0/mysql-connector-j-26.7.0.jar lib/mysql-connector.jar
COPY src src

RUN mkdir out && javac -cp lib/mysql-connector.jar -d out $(find src -name "*.java" ! -name "Main.java")

EXPOSE 8080

CMD ["java", "-cp", "out:lib/mysql-connector.jar", "com.bank.ApiServer"]