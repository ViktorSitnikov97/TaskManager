FROM eclipse-temurin:21-jdk

ARG GRADLE_VERSION=8.5

RUN apt-get update && apt-get install -yq make unzip

WORKDIR /

COPY ./ .

RUN ./gradlew installDist

ENV SPRING_PROFILES_ACTIVE=production

CMD ./build/install/app/bin/app

EXPOSE 8080