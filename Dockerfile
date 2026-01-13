# 1단계: 빌드 전용 이미지 - Build Stage
FROM eclipse-temurin:21-jdk-jammy AS builder
WORKDIR /app

# Gradle wrapper 및 설정 파일 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./
COPY src src

# 실행 권한 부여
RUN chmod +x gradlew

# Gradle 빌드 (테스트는 제외)
RUN ./gradlew clean build -x test --no-daemon

# 2단계: 실행 전용 이미지 -  Run Stage
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app

# ✅ ADD: docker CLI 설치 (ProcessBuilder("docker", ...) 실행 목적)
RUN apt-get update \
 && apt-get install -y docker.io \
 && rm -rf /var/lib/apt/lists/*

# 1단계에서 빌드된 JAR 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# 파일 업로드 폴더 생성
RUN mkdir -p /app/files

# 포트 오픈
EXPOSE 8080

# Spring Boot 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]