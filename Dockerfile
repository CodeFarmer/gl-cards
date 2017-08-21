FROM openjdk:8-alpine
MAINTAINER Joel Gluth <joel.gluth@gmail.com>

COPY target/gl-cards.jar /home/gl-cards/gl-cards.jar
COPY config.json /home/gl-cards/config.json

WORKDIR /home/gl-cards
EXPOSE 8080
CMD ["java","-jar","gl-cards.jar","8080"]