# Build stage
FROM gradle:7.6.1-jdk17 AS builder
WORKDIR /build

# 그래들 파일들을 먼저 복사하여 의존성을 캐시
COPY build.gradle.kts settings.gradle.kts /build/
COPY gradle /build/gradle
RUN gradle dependencies --no-daemon

# 소스 복사 및 빌드
COPY src /build/src
RUN gradle build -x test --no-daemon

# Runtime stage
FROM eclipse-temurin:17-jre-focal
WORKDIR /app

# 타임존 설정
ENV TZ=Asia/Seoul
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 보안을 위한 비root 유저 생성
RUN groupadd -r spring && useradd -r -g spring spring

# 실행에 필요한 파일만 복사
COPY --from=builder /build/build/libs/*.jar app.jar

# 권한 설정
RUN chown -R spring:spring /app
USER spring

# 컨테이너 헬스체크
HEALTHCHECK --interval=10s --timeout=3s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# 환경변수 설정
ENV JAVA_OPTS="-XX:+UseG1GC -XX:MaxGCPauseMillis=100 -XX:+UseStringDeduplication -Dserver.port=8080"

# 애플리케이션 실행
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar"]