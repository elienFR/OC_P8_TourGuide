package tourGuide;


import java.util.*;
import java.util.concurrent.*;

import jdk.nashorn.internal.ir.annotations.Ignore;
import org.apache.commons.lang3.time.StopWatch;


import gpsUtil.GpsUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tourGuide.beans.Attraction;
import tourGuide.beans.VisitedLocation;
import tourGuide.helper.InternalTestHelper;
import tourGuide.service.GpsUtilService;
import tourGuide.service.MultiTaskService;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tourGuide.user.User;
import tourGuide.user.UserReward;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TestPerformance {

  @Autowired
  private GpsUtilService gpsUtilService;
  @Autowired
  private RewardsService rewardsService;
  @Autowired
  private TourGuideService tourGuideService;

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
  @Ignore
  @Test
  public void highVolumeTrackLocation() {
    Locale.setDefault(Locale.US);
    GpsUtil gpsUtil = new GpsUtil();
    // Users should be incremented up to 100,000, and test finishes within 15 minutes
    InternalTestHelper.setInternalUserNumber(100000);
    List<User> allUsers = new ArrayList<>();
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    TourGuideService tourGuideService = new TourGuideService();
    allUsers = tourGuideService.getAllUsers();

    allUsers.stream().forEach(u -> tourGuideService.trackUserLocationMultitasking(u));
    TourGuideService.futures.stream().parallel().forEach(
      f -> {
        try {
          f.get(20,TimeUnit.MINUTES);
        } catch (Exception e){
          e.getMessage();
        }
      }
    );
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
    TourGuideService.executorService.shutdown();

    stopWatch.stop();
    tourGuideService.tracker.stopTracking();

    System.out.println("highVolumeTrackLocation: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
    assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
  }

  @Test
  public void highVolumeGetRewards() {
    Locale.setDefault(Locale.FRENCH);

    // Users should be incremented up to 100,000, and test finishes within 20 minutes
    InternalTestHelper.setInternalUserNumber(100000);
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    TourGuideService tourGuideService = new TourGuideService();

    Attraction attraction = gpsUtilService.getAttractions().get(0);
    List<User> allUsers = new ArrayList<>(tourGuideService.getAllUsers());
    allUsers.forEach(u -> u.addToVisitedLocations(new VisitedLocation(u.getUserId(), attraction, new Date())));

    for (User user : allUsers) {
      MultiTaskService.submit(
        () -> {
          return rewardsService.calculateRewards(user);
        }
      );
    }
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

    //TODO : cette assertion passe parfois et parfois non à 30000, à 40000 et au dessus elle ne passe jamais utilisateurs depuis que l'on est séparé en micro services
    //TODO : L'assertion de temps passe bien mais l'assertion rewards>1 ne passe pas
    //TODO : il compte une size de 0 sur le userRewards du premier user de la list allUsers ?!!
    //TODO : c'est improbable.

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
    tourGuideService.tracker.stopTracking();

    System.out.println("highVolumeGetRewards: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
    assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
  }

}
