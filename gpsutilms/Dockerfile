FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY build/libs/*.jar gpsUtilMsApp.jar
ENTRYPOINT ["java","-jar","/gpsUtilMsApp.jar"]