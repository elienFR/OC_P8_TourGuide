FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY build/libs/*.jar rewardCentralMsApp.jar
ENTRYPOINT ["java","-jar","/rewardCentralMsApp.jar"]