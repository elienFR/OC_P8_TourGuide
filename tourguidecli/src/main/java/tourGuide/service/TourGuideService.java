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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tourGuide.beans.Attraction;
import tourGuide.beans.Location;
import tourGuide.beans.VisitedLocation;
import tourGuide.helper.InternalTestHelper;
import tourGuide.proxies.GpsUtilProxy;
import tourGuide.proxies.TripPricerProxy;
import tourGuide.tracker.Tracker;
import tourGuide.user.User;
import tourGuide.user.UserReward;
import tripPricer.Provider;
import tripPricer.TripPricer;

@Service
public class TourGuideService {

  /**********************************************************************************
   *
   * Methods Below: For Internal Testing
   *
   **********************************************************************************/
  private static final String tripPricerApiKey = "test-server-api-key";
  public static ExecutorService executorService = Executors.newFixedThreadPool(8333);
  public static List<Future> futures = new ArrayList<>();
  public final Tracker tracker;
  // Database connection will be used for external users, but for testing purposes internal users are provided and stored in memory
  private final Map<String, User> internalUserMap = new HashMap<>();
  boolean testMode = true;
  @Autowired
  private GpsUtilProxy gpsUtilProxy;
  @Autowired
  private RewardsService rewardsService;
  @Autowired
  private TripPricerProxy tripPricerProxy;
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
    List<Provider> providers = tripPricerProxy.getPrice(
      tripPricerApiKey,
      user.getUserId().toString(),
      String.valueOf(user.getUserPreferences().getNumberOfAdults()),
      String.valueOf(user.getUserPreferences().getNumberOfChildren()),
      String.valueOf(user.getUserPreferences().getTripDuration()),
      String.valueOf(cumulatativeRewardPoints)
    );
    user.setTripDeals(providers);
    return providers;
  }

  public void trackUserLocationMultitasking(User user) {
    futures.add(executorService.submit(
        () -> trackUserLocation(user)
      )
    );
  }

  public VisitedLocation trackUserLocation(User user) {
    VisitedLocation visitedLocation = gpsUtilProxy.getVisitedLocation(user.getUserId().toString());
    user.addToVisitedLocations(visitedLocation);
    rewardsService.calculateRewardsMultitasking(user);
    return visitedLocation;
  }

  public List<Attraction> getNearByAttractions(VisitedLocation visitedLocation) {
    List<Attraction> nearbyAttractions = new ArrayList<>();
    for (Attraction attraction : gpsUtilProxy.getAttractions()) {
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

}
