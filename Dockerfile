FROM amazoncorretto:17
LABEL authors="ASak1104"

ARG JAR_FILE=./build/libs/*-SNAPSHOT.jar
ARG PROFILE

ENV SPRING_PROFILE_ACTICE=${PROFILE}

COPY ${JAR_FILE} app.jar

RUN echo ${SPRING_PROFILE_ACTICE}

ENTRYPOINT ["java", "-Dspring.profiles.active=${SPRING_PROFILE_ACTICE}", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app.jar"]
