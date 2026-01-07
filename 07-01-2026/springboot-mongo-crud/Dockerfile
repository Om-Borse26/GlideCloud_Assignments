# syntax=docker/dockerfile:1

# Build stage: use JDK and the committed Gradle wrapper
FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace

# 1) Copy Gradle wrapper first (required by ./gradlew)
COPY gradlew gradlew.bat ./
COPY gradle/wrapper ./gradle/wrapper
RUN chmod +x ./gradlew

# 2) Copy build files and sources
COPY settings.gradle* build.gradle* gradle.properties* ./
COPY src ./src

# 3) Build the fat JAR and rename it deterministically to app.jar
RUN ./gradlew --no-daemon clean bootJar -x test \
 && JAR_FILE="$(ls build/libs | grep -E '\.jar$' | grep -v 'plain' | head -n 1)" \
 && cp "build/libs/${JAR_FILE}" /workspace/app.jar

# Runtime stage: slim JRE
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/app.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
