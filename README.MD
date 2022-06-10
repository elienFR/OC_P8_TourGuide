# README

This app is an exercice about improving the performance an API on an existing app called TourGuide.


## Technical:

1. TourGuide client :
   1. Framework: Spring Boot v2.6.8
   2. Framework: Spring Web
   3. FrameWork: Open-Feign (SpringBootStarter)
   4. Java 1.8
   5. Gradle 7.4.1
   6. Libs :
       1. gpsUtil
       2. RewardCentral
       3. TripPriver
2. GpsUtilMs :
   1. Framework: Spring Boot v2.6.8
   2. Framework: Spring Web
   3. Java 1.8
   4. Gradle 7.4.1
   5. Libs :
      1. gpsUtil
3. RewardCentralMs :
   1. Framework: Spring Boot v2.6.8
   2. Framework: Spring Web
   3. Java 1.8
   4. Gradle 7.4.1
   5. Libs :
      2. RewardCentral
4. TripPricerMs :
   1. Framework: Spring Boot v2.6.8
   2. Framework: Spring Web
   3. Java 1.8
   4. Gradle 7.4.1
   5. Libs :
      3. TripPricer


## Instructions

1. Create image docker for *trippricerms*, *rewardcentralsms*, and *gpsutilms*. And run each of them through containers. <br> More info here : https://spring.io/guides/gs/spring-boot-docker/
2. Run *tourguidecli* via your IDE or via the built jar.

## Info

Here are the exposed port on each app and microservices :
- ***tourguidecli*** --> port 8080
- ***gpsutilms*** --> port 8081
- ***rewardcentralms*** --> port 8181
- ***trippricerms*** --> port 8182

## Tourguide API Endpoints :
- host address :
  - ***localhost:8080***
- list of endpoints :
  - ***/*** : This endpoint is the front page of the api.
  - ***/getLocation*** : This endpoint is used to track one user location
  - ***/getNearbyAttractions*** : This endpoint is used to display for a specific user, the five closest attractions from this user, the longitude and latitude of each of these attractions, the longitude and latitude of the user, the distance in miles between the user and each attraction, and eventually the reward points given to the user if all these attractions are visited. 
  - ***/getRewards*** : This endpoint calculates reward for a specific user.
  - ***/getAllCurrentLocations*** : This endpoint is used to give you all the last location of each user in the app's database.
  - ***/getTripDeals*** : This endpoint is used to fetch trip deals from a user, thanks to its reward points and its parameters.
