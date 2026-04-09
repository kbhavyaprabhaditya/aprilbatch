# ========================================
# MULTISTAGE DOCKERFILE FOR JAVA APPLICATION
# ========================================

# Stage 1: Build Stage
# Use Maven image to build the application
FROM maven:3.9-eclipse-temurin-11 AS builder

# Set working directory
WORKDIR /app

# Copy pom.xml first (for better layer caching)
COPY pom.xml .

# Download dependencies (this layer will be cached)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application (skip tests for faster build)
RUN mvn clean package -DskipTests

# List the target directory to verify JAR creation
RUN ls -lah /app/target/

# ========================================
# Stage 2: Runtime Stage
# Use a minimal JRE image for running the application
FROM eclipse-temurin:11-jre-alpine

# Add metadata labels
LABEL maintainer="demo@example.com"
LABEL version="1.0"
LABEL description="Multistage Docker build for Java application"

# Set working directory
WORKDIR /app

# Copy only the built JAR from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Create a non-root user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Change ownership of the application
RUN chown -R appuser:appgroup /app

# Switch to non-root user
USER appuser

# Expose port (if your app uses one)
# EXPOSE 8080

# Set environment variables
ENV JAVA_OPTS="-Xms128m -Xmx256m"

# Health check (optional)
# HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
#   CMD java -version || exit 1

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

# Alternative CMD if you want to specify the main class directly
# CMD ["java", "-cp", "app.jar", "com.example.app.HelloWorld"]
