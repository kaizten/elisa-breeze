# Base image
FROM openjdk:17-alpine

LABEL maintainer="info@kaizten.com"
LABEL category="algorithm"

# Volume
VOLUME /data

# Copy JAR
COPY target/pmr-optimization-1.0-SNAPSHOT-jar-with-dependencies.jar app.jar

# Entry point
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
