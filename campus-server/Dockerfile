FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY target/campus-server.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=docker"]
