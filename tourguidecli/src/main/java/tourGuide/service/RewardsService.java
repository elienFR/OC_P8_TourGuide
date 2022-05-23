package tourGuide.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gpsUtil.GpsUtil;
import rewardCentral.RewardCentral;
import tourGuide.beans.Attraction;
import tourGuide.beans.Location;
import tourGuide.beans.VisitedLocation;
import tourGuide.proxies.GpsUtilProxy;
import tourGuide.proxies.RewardCentralProxy;
import tourGuide.user.User;
import tourGuide.user.UserReward;

@Service
public class RewardsService {

  @Autowired
  private GpsUtilProxy gpsUtilProxy;
  @Autowired
  private RewardCentralProxy rewardCentralProxy;

  private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;
  public static ExecutorService executorService = Executors.newFixedThreadPool(8333);
  public static List<Future> futures = new ArrayList<>();
  // proximity in miles
  private int defaultProximityBuffer = 10;
  private int proximityBuffer = defaultProximityBuffer;
  private int attractionProximityRange = 200;

  public static ExecutorService getExecutorService() {
    return executorService;
  }

  public static List<Future> getFutures() {
    return futures;
  }

  public void setProximityBuffer(int proximityBuffer) {
    this.proximityBuffer = proximityBuffer;
  }

  public void setDefaultProximityBuffer() {
    proximityBuffer = defaultProximityBuffer;
  }

  public void calculateRewardsMultitasking(User u) {
    futures.add(executorService.submit(
      () -> calculateRewards(u)
    ));
  }

  public void calculateRewards(User user) {
    List<VisitedLocation> userLocations = user.getVisitedLocations();
    List<Attraction> attractions = gpsUtilProxy.getAttractions();
    for (VisitedLocation visitedLocation : userLocations) {
      for (Attraction attraction : attractions) {
        if (user.getUserRewards().stream().filter(r -> r.attraction.attractionName.equals(attraction.attractionName)).count() == 0) {
          if (nearAttraction(visitedLocation, attraction)) {
            user.addUserReward(new UserReward(visitedLocation, attraction, getRewardPoints(attraction, user)));
          }
        }
      }
    }
  }

  public boolean isWithinAttractionProximity(Attraction attraction, Location location) {
    return getDistance(attraction, location) > attractionProximityRange ? false : true;
  }

  private boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction) {
    return getDistance(attraction, visitedLocation.location) > proximityBuffer ? false : true;
  }

  private int getRewardPoints(Attraction attraction, User user) {
    return rewardCentralProxy.getAttractionRewardPoints(attraction.attractionId.toString(), user.getUserId().toString());
  }

  public double getDistance(Location loc1, Location loc2) {
    double lat1 = Math.toRadians(loc1.latitude);
    double lon1 = Math.toRadians(loc1.longitude);
    double lat2 = Math.toRadians(loc2.latitude);
    double lon2 = Math.toRadians(loc2.longitude);

    double angle = Math.acos(Math.sin(lat1) * Math.sin(lat2)
      + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

    double nauticalMiles = 60 * Math.toDegrees(angle);
    double statuteMiles = STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
    return statuteMiles;
  }

}
