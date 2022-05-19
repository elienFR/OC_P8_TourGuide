package tourGuide;

import static org.junit.Assert.assertTrue;

import java.util.*;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.junit.Ignore;
import org.junit.Test;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import tourGuide.helper.InternalTestHelper;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tourGuide.user.User;
import tourGuide.user.UserReward;

public class TestPerformance {

  /*
   * A note on performance improvements:
   *
   *     The number of users generated for the high volume tests can be easily adjusted via this method:
   *
   *     		InternalTestHelper.setInternalUserNumber(100000);
   *
   *
   *     These tests can be modified to suit new solutions, just as long as the performance metrics
   *     at the end of the tests remains consistent.
   *
   *     These are performance metrics that we are trying to hit:
   *
   *     highVolumeTrackLocation: 100,000 users within 15 minutes:
   *     		assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
   *
   *     highVolumeGetRewards: 100,000 users within 20 minutes:
   *          assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
   */

  @Test
  public void highVolumeTrackLocation() {
    Locale.setDefault(Locale.US);
    GpsUtil gpsUtil = new GpsUtil();
    RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
    // Users should be incremented up to 100,000, and test finishes within 15 minutes
    InternalTestHelper.setInternalUserNumber(100000);
    TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

    List<User> allUsers = new ArrayList<>();
    allUsers = tourGuideService.getAllUsers();

    StopWatch stopWatch = new StopWatch();
    stopWatch.start();

    allUsers.stream().forEach(u -> tourGuideService.trackUserLocationMultitasking(u));
    TourGuideService.futures.stream().parallel().forEach(
      f -> {
        try{
          f.get();
        } catch (Exception e) {
          e.getMessage();
        }
      }
    );
    RewardsService.futures.stream().parallel().forEach(
      f -> {
        try{
          f.get();
        } catch (Exception e) {
          e.getMessage();
        }
      }
    );
    RewardsService.executorService.shutdown();
    TourGuideService.executorService.shutdown();

    stopWatch.stop();
    tourGuideService.tracker.stopTracking();

    System.out.println("highVolumeTrackLocation: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
    assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
  }

  @Test
  public void highVolumeGetRewards() {
    Locale.setDefault(Locale.FRENCH);
    GpsUtil gpsUtil = new GpsUtil();
    RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());

    // Users should be incremented up to 100,000, and test finishes within 20 minutes
    InternalTestHelper.setInternalUserNumber(100000);
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

    Attraction attraction = gpsUtil.getAttractions().get(0);
    List<User> allUsers = new ArrayList<>();
    allUsers = tourGuideService.getAllUsers();
    allUsers.forEach(u -> u.addToVisitedLocations(new VisitedLocation(u.getUserId(), attraction, new Date())));

    allUsers.forEach(u -> rewardsService.calculateRewardsMultitasking(u));
    RewardsService.futures.stream().parallel().forEach(
      f -> {
        try {
          f.get();
        } catch (Exception e) {
          e.getMessage();
        }
      }
    );
    RewardsService.executorService.shutdown();

    for (User user : allUsers) {
      assertTrue(user.getUserRewards().size() > 0);
    }
    stopWatch.stop();
    tourGuideService.tracker.stopTracking();

    System.out.println("highVolumeGetRewards: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
    assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
  }

}
