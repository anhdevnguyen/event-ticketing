# Dockerfile — Backend (multi-stage build)
# Stage 1: Maven build  →  Stage 2: JRE runtime (image nhỏ gọn, không mang toolchain build)
#
# Build thủ công (local):   docker build -t event-ticketing-backend ./backend
# Chạy thử:                 docker run -p 8080:8080 --env-file .env event-ticketing-backend
#
# Production: image được build và push lên GHCR qua GitHub Actions (xem docs/11-DEPLOYMENT.md mục 3+4).

# ─── Stage 1: Build ──────────────────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy pom.xml trước (tận dụng Docker layer cache cho dependencies)
# → Nếu chỉ đổi code (src/) mà không đổi pom.xml, bước `mvn dependency:go-offline` không cần re-run
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source và build
COPY src ./src
RUN mvn clean package -DskipTests
# -DskipTests: test đã chạy ở GitHub Actions step trước khi tới bước docker build

# ─── Stage 2: Runtime ────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Tạo non-root user để chạy app (security best practice)
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

# Giới hạn JVM heap để tránh chiếm hết RAM VPS (1 Core / 2GB, chạy song song 2 backend)
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseSerialGC -XX:MaxMetaspaceSize=128m"

# Khởi động Spring Boot
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
