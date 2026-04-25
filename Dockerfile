# Stage 1: Build the JAR (Maven + JDK 21 bundled — no mvnw wrapper in this repo)
# maven:3.9-eclipse-temurin-21-alpine includes Maven 3.9 and JDK 21 in a single image.
FROM maven:3.9-eclipse-temurin-21-alpine AS builder
WORKDIR /app

# Copy only pom.xml first to cache the dependency download layer.
# If only source changes (not pom.xml), Docker skips the expensive dependency:go-offline step.
COPY pom.xml .
RUN mvn dependency:go-offline -B -q

# Copy source and build the fat JAR, skipping tests (tests run in CI separately).
COPY src ./src
RUN mvn clean package -DskipTests -B -q

# Stage 2: Minimal JRE runtime (~200 MB vs ~400 MB with JDK).
# eclipse-temurin:21-jre-alpine supports linux/arm64 natively.
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

# Cap JVM heap at 75% of container memory to leave headroom for Nginx and OS on t4g.micro (1 GB).
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
