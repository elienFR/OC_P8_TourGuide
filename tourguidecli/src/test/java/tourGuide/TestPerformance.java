package tourGuide;


import java.util.*;
import java.util.concurrent.*;

import org.apache.commons.lang3.time.StopWatch;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tourGuide.model.beans.Attraction;
import tourGuide.model.beans.VisitedLocation;
import tourGuide.helper.InternalTestHelper;
import tourGuide.service.GpsUtilService;
import tourGuide.service.MultiTaskService;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tourGuide.user.User;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TestPerformance {

  @Autowired
  private GpsUtilService gpsUtilService;
  @Autowired
  private RewardsService rewardsService;
  @Autowired
  private TourGuideService tourGuideServiceAutowired;

  private Integer receivedInfo;
  private Integer userNumber;

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
  @Order(1)
  public void highVolumeTrackLocation() {
    // Users should be incremented up to 100,000, and test finishes within 15 minutes
    InternalTestHelper.setInternalUserNumber(100000);
    List<User> allUsers = new ArrayList<>();
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    TourGuideService tourGuideServiceToCreateUsers = new TourGuideService();
    allUsers = tourGuideServiceToCreateUsers.getAllUsers();

    allUsers.forEach(
      u -> MultiTaskService.submit(
        () -> tourGuideServiceAutowired.trackUserLocation(u)
      )
    );

    receivedInfo = 0;
    List<Future> futures = MultiTaskService.futures;
    for (Future f : futures) {
      try {
        f.get();
        System.out.println("received info : " + receivedInfo);
        receivedInfo = receivedInfo + 1;
      } catch (Exception e) {
        e.getMessage();
      }
    }

    //we don't shutdown the executor service her because the other test needs to be realised with the same executor service.
    //The shutdown is executed in the second test

    MultiTaskService.clearFutures();

    stopWatch.stop();
    tourGuideServiceAutowired.tracker.stopTracking();

    System.out.println("highVolumeTrackLocation: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
    assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
  }

  @Test
  @Order(2)
  public void highVolumeGetRewards() {
    // Users should be incremented up to 100,000, and test finishes within 20 minutes
    InternalTestHelper.setInternalUserNumber(100000);
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    TourGuideService tourGuideServiceToCreateUsers = new TourGuideService();

    Attraction attraction = gpsUtilService.getAttractions().get(0);
    List<User> allUsers = new ArrayList<>(tourGuideServiceToCreateUsers.getAllUsers());
    allUsers.forEach(u -> u.addToVisitedLocations(new VisitedLocation(u.getUserId(), attraction, new Date())));

    allUsers.forEach(
      u->MultiTaskService.submit(
        ()->rewardsService.calculateRewards(u)
      )
    );

    receivedInfo = 0;
    List<Future> futures = MultiTaskService.futures;
    for (Future f : futures) {
      try {
          f.get();
        System.out.println("received info : " + receivedInfo);
        receivedInfo = receivedInfo + 1;
      } catch (Exception e) {
        e.getMessage();
      }
    }

    MultiTaskService.executorService.shutdown();
    try {
      if (!MultiTaskService.executorService.awaitTermination(20, TimeUnit.MINUTES)) {
        MultiTaskService.executorService.shutdownNow();
      }
    } catch (InterruptedException e) {
      MultiTaskService.executorService.shutdownNow();
    }

    MultiTaskService.clearFutures();

    userNumber = 0;
    for (User user : allUsers) {
      if (!(user.getUserRewards().size() > 0)) {
        System.out.println(
          "------ userNumber = " +
            userNumber +
            " : " +
            (user.getUserRewards().size() > 0)
        );
        userNumber = userNumber + 1;
      }
      assertTrue(user.getUserRewards().size() > 0);
    }


    stopWatch.stop();
    tourGuideServiceAutowired.tracker.stopTracking();

    System.out.println("highVolumeGetRewards: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
    assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
  }

}
