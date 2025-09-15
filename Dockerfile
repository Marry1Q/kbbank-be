FROM openjdk:17-jdk-slim

# Gradle wrapper와 소스 코드 복사
COPY . /app
WORKDIR /app

# Gradle wrapper 실행 권한 부여
RUN chmod +x gradlew

# 의존성 다운로드 및 애플리케이션 빌드 (더 안전한 방식)
RUN ./gradlew clean build -x test --no-daemon

# 빌드 결과 확인 (디버깅용)
RUN ls -la build/libs/

# Railway 호환성을 위한 JVM 옵션
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# 환경변수를 통한 TNS_ADMIN 설정
ENV ORACLE_TNS_ADMIN=/app/wallet

# 빌드된 실행 가능한 JAR 파일 실행
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Doracle.net.tns_admin=${ORACLE_TNS_ADMIN} -jar build/libs/kbbank-backend-0.0.1-SNAPSHOT.jar"]