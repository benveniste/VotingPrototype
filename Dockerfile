#
# Stage 1: Build the application
#
FROM gradle:jdk23 AS builder
COPY --chown=gradle:gradle . /Users/mhb/dev/VotingPrototype
WORKDIR /Users/mhb/dev/VotingPrototype
RUN gradle build --no-daemon -x test

#
# Stage 2: Create the runtime image
# NOTE:  The dockerfile so built is designed for deployment on AWS.  Otherwise, it would need a set of AWS credentials
# Example:
# docker run -t -p9000:9000 -v $HOME/.aws/credentials:/root/.aws/credentials:ro voting:First
#
FROM eclipse-temurin:23-alpine
RUN mkdir /app
COPY --from=builder /Users/mhb/dev/VotingPrototype/build/libs/VotingPrototype-all.jar /app/app.jar
CMD ["java", "-jar", "/app/app.jar"]