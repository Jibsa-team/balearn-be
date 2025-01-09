# Build stage
FROM openjdk:17-jdk-slim AS builder
WORKDIR /build

# 그래들 래퍼 및 소스 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY src src

# gradlew 실행 권한 부여 및 의존성 설치
RUN chmod +x ./gradlew
RUN ./gradlew dependencies --no-daemon

# 애플리케이션 빌드
RUN ./gradlew bootJar --no-daemon

# Runtime stage
FROM eclipse-temurin:17-jre
WORKDIR /app

# 타임존 설정
ENV TZ=Asia/Seoul
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 필요한 패키지 설치
RUN apt-get update && \
    apt-get install -y curl && \
    rm -rf /var/lib/apt/lists/*

# 보안을 위한 비root 유저 생성
RUN groupadd -r spring && useradd -r -g spring spring

# 빌드된 JAR 파일 복사
COPY --from=builder /build/build/libs/*.jar app.jar

# 권한 설정
RUN chown -R spring:spring /app
USER spring

# 컨테이너 헬스체크
HEALTHCHECK --interval=10s --timeout=3s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# JVM 옵션 설정
ENV JAVA_OPTS="-XX:+UseG1GC \
               -XX:MaxGCPauseMillis=100 \
               -XX:+UseStringDeduplication \
               -Dserver.port=8080 \
               -Dfile.encoding=UTF-8 \
               -Djava.security.egd=file:/dev/./urandom"

# 애플리케이션 실행
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]