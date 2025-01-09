# Base image
FROM openjdk:17-jdk-slim

# Set environment variables
ENV APP_HOME=/app

# Create application directory
WORKDIR $APP_HOME

# Copy JAR file into container
COPY build/libs/*.jar app.jar

# Expose the port that the application runs on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]