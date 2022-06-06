package tourGuide.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tourGuide.model.UserLocationDTO;
import tourGuide.model.beans.Attraction;
import tourGuide.model.beans.Location;
import tourGuide.model.beans.Provider;
import tourGuide.model.beans.VisitedLocation;
import tourGuide.helper.InternalTestHelper;
import tourGuide.tracker.Tracker;
import tourGuide.user.User;
import tourGuide.user.UserReward;

@Service
public class TourGuideService {

  @Autowired
  private GpsUtilService gpsUtilService;
  @Autowired
  private RewardsService rewardsService;
  @Autowired
  private TripPricerService tripPricerService;

  /**********************************************************************************
   *
   * Methods Below: For Internal Testing
   *
   **********************************************************************************/
  private static final String tripPricerApiKey = "test-server-api-key";
  public final Tracker tracker;
  // Database connection will be used for external users, but for testing purposes internal users are provided and stored in memory
  private final Map<String, User> internalUserMap = new HashMap<>();
  boolean testMode = true;

  private Logger logger = LoggerFactory.getLogger(TourGuideService.class);

  public TourGuideService() {

    if (testMode) {
      logger.info("TestMode enabled");
      logger.debug("Initializing users");
      initializeInternalUsers();
      logger.debug("Finished initializing users");
    }
    tracker = new Tracker(this);
    addShutDownHook();
  }

  public List<UserReward> getUserRewards(User user) {
    return user.getUserRewards();
  }

  public VisitedLocation getUserLocation(User user) {
    VisitedLocation visitedLocation = (user.getVisitedLocations().size() > 0) ?
      user.getLastVisitedLocation() :
      trackUserLocation(user);
    return visitedLocation;
  }

  public User getUser(String userName) {
    return internalUserMap.get(userName);
  }

  public List<User> getAllUsers() {
    return internalUserMap.values().stream().collect(Collectors.toList());
  }

  public void addUser(User user) {
    if (!internalUserMap.containsKey(user.getUserName())) {
      internalUserMap.put(user.getUserName(), user);
    }
  }

  public List<Provider> getTripDeals(User user) {
    int cumulatativeRewardPoints = user.getUserRewards().stream().mapToInt(i -> i.getRewardPoints()).sum();
    List<Provider> providers = tripPricerService.getPrice(
      tripPricerApiKey,
      user,
      cumulatativeRewardPoints
    );
    user.setTripDeals(providers);
    return providers;
  }

  public VisitedLocation trackUserLocation(User user) {
    VisitedLocation visitedLocation = gpsUtilService.getVisitedLocation(user.getUserId());
    user.addToVisitedLocations(visitedLocation);
    rewardsService.calculateRewards(user);
    return visitedLocation;
  }

  public List<Attraction> getNearByAttractions(VisitedLocation visitedLocation) {
    List<Attraction> nearbyAttractions = new ArrayList<>();
    for (Attraction attraction : gpsUtilService.getAttractions()) {
      if (rewardsService.isWithinAttractionProximity(attraction, visitedLocation.location)) {
        nearbyAttractions.add(attraction);
      }
    }

    return nearbyAttractions;
  }

  private void addShutDownHook() {
    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        tracker.stopTracking();
      }
    });
  }

  private void initializeInternalUsers() {
    IntStream.range(0, InternalTestHelper.getInternalUserNumber()).forEach(i -> {
      String userName = "internalUser" + i;
      String phone = "000";
      String email = userName + "@tourGuide.com";
      User user = new User(UUID.randomUUID(), userName, phone, email);
      generateUserLocationHistory(user);

      internalUserMap.put(userName, user);
    });
    logger.debug("Created " + InternalTestHelper.getInternalUserNumber() + " internal test users.");
  }

  private void generateUserLocationHistory(User user) {
    IntStream.range(0, 3).forEach(i -> {
      user.addToVisitedLocations(new VisitedLocation(user.getUserId(), new Location(generateRandomLatitude(), generateRandomLongitude()), getRandomTime()));
    });
  }

  private double generateRandomLongitude() {
    double leftLimit = -180;
    double rightLimit = 180;
    return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
  }

  private double generateRandomLatitude() {
    double leftLimit = -85.05112878;
    double rightLimit = 85.05112878;
    return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
  }

  private Date getRandomTime() {
    LocalDateTime localDateTime = LocalDateTime.now().minusDays(new Random().nextInt(30));
    return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
  }

  /**
   * This method returns all last visited location for every user in DB.
   *
   * @return a list of each user with ist last visited location.
   */
  public List<UserLocationDTO> getAllCurrentLocations(List<User> users) {
    List<UserLocationDTO> usersLastLocations = new ArrayList<>();
    for (User user : users) {
      VisitedLocation lastVisitedLocation = new VisitedLocation(
        UUID.fromString("0-0-0-0-0"),
        new Location(generateRandomLatitude(), generateRandomLongitude()),
        Date.from(LocalDateTime.of(0, 1, 1, 0, 0).toInstant(ZoneOffset.UTC))
      );

      for (VisitedLocation visitedLocation : user.getVisitedLocations()) {
        if (visitedLocation.getTimeVisited().after(lastVisitedLocation.getTimeVisited())) {
          lastVisitedLocation = visitedLocation;
        }
      }
      UserLocationDTO lastUserLocationDTO = new UserLocationDTO();
      lastUserLocationDTO.setLocation(lastVisitedLocation.getLocation());
      lastUserLocationDTO.setUserId(user.getUserId());

      usersLastLocations.add(lastUserLocationDTO);
    }

    return usersLastLocations;
  }

  private UserLocationDTO getLatestLocationFromList(List<VisitedLocation> visitedLocations) {
    return null;
  }
}
