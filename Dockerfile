# Stage 1: Build the application
# NOTE:  The dockerfile so built is designed for deployment on AWS.  Otherwise, it would need AWS credentials to
# set when invoked.
FROM gradle:jdk23 AS builder
COPY --chown=gradle:gradle . /Users/mhb/dev/VotingPrototype
WORKDIR /Users/mhb/dev/VotingPrototype
RUN gradle build --no-daemon -x test

# Stage 2: Create the runtime image
FROM eclipse-temurin:23-alpine
RUN mkdir /app
COPY --from=builder /Users/mhb/dev/VotingPrototype/build/libs/*.jar /app/app.jar
CMD ["java", "-jar", "/app/app.jar"]