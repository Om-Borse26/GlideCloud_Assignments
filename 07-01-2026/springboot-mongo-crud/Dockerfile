# 'Dockerfile'
# Multi-stage: build with Gradle, then run on a slim JRE.
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

COPY gradlew gradlew.bat ./
COPY gradle ./gradle
RUN chmod +x ./gradlew

COPY settings.gradle* build.gradle* ./
COPY src ./src

# Build the fat jar and pick the non-plain artifact
RUN ./gradlew --no-daemon clean bootJar -x test && \
    JAR_FILE=$(ls build/libs | grep -E '\.jar$' | grep -v 'plain' | head -n 1) && \
    cp build/libs/$JAR_FILE app.jar

FROM eclipse-temurin:21-jre
WORKDIR /app
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0"
COPY --from=build /app/app.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]

