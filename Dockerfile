FROM eclipse-temurin:21-jdk

ARG GRADLE_VERSION=8.5

RUN apt-get update && apt-get install -yq make unzip

WORKDIR /

COPY . .

RUN ./gradlew installDist

ENV SPRING_PROFILES_ACTIVE=production

CMD java -jar build/libs/app-0.0.1-SNAPSHOT.jar

EXPOSE 8080