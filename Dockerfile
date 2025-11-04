# 안정적인 Java 21 환경
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app

# gradle wrapper 실행에 필요한 권한 부여 및 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./
COPY src src

RUN chmod +x gradlew

# ✅ /app/files 디렉토리 미리 생성
RUN mkdir -p /app/files

RUN ./gradlew build -x test --no-daemon

COPY build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]