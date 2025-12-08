# Multi-stage build for optimized production image

# Stage 1: Build
FROM maven:3-eclipse-temurin-25 AS builder

WORKDIR /app

# Copy pom.xml and download dependencies (cached layer)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests -B

# Stage 2: Extract layers
FROM eclipse-temurin:21-jre AS extractor

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract

# Stage 3: Production image with distroless
FROM gcr.io/distroless/java21-debian12:nonroot

WORKDIR /app

# Copy application layers from extractor
COPY --from=extractor /app/dependencies/ ./
COPY --from=extractor /app/spring-boot-loader/ ./
COPY --from=extractor /app/snapshot-dependencies/ ./
COPY --from=extractor /app/application/ ./

# Run as non-root user (distroless already uses nonroot)
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
    CMD ["java", "-cp", "/app", "org.springframework.boot.actuator.health.HttpHealthIndicatorHealthCheck"]

# Use exec form for ENTRYPOINT
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
