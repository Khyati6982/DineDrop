# Use a lightweight Java image with JDK 17
FROM openjdk:17-jdk-slim

# Set a working directory inside the container
WORKDIR /app

# Copy the JAR file built by Maven
COPY target/DineDrop-0.0.1-SNAPSHOT.jar dinedrop.jar

# Expose port 9091
EXPOSE 9091

# Activate the 'docker' Spring profile
ENV SPRING_PROFILES_ACTIVE=docker

# Run the application
ENTRYPOINT ["java", "-jar", "dinedrop.jar"]
