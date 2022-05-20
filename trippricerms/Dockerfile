FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY build/libs/*.jar tripPricerMsApp.jar
ENTRYPOINT ["java","-jar","/tripPricerMsApp.jar"]