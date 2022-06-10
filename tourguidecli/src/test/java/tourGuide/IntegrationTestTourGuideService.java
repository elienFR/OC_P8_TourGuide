package tourGuide;


import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tourGuide.model.NearbyAttractionsDTO;
import tourGuide.model.UserLocationDTO;
import tourGuide.model.beans.Location;
import tourGuide.model.beans.Provider;
import tourGuide.model.beans.VisitedLocation;
import tourGuide.helper.InternalTestHelper;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tourGuide.user.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@Disabled
@SpringBootTest
public class IntegrationTestTourGuideService {

  /* --------------- IMPORTANT ----------------------

  You must start gspUtil service, Reward service and TripPricer service before launching tests.
  As docker microservices or directly from your IDE.

  ------------------------------------------------ */


  @Autowired
  private TourGuideService tourGuideService;
  @Autowired
  private RewardsService rewardsService;

  private List<User> usersAtTest;
  private User userSample1;
  private UUID uuid1 = UUID.fromString("1-1-1-1-1");
  private UUID uuid2 = UUID.fromString("2-2-2-2-2");
  private Location location11;
  private Location location12;
  private Location location21;
  private Location location22;
  private User userSample2;

  @BeforeEach
  public void initSamples() {
    InternalTestHelper.setInternalUserNumber(0);
    tourGuideService.initializeInternalUsers();

    usersAtTest = new ArrayList<>();
    location11 = new Location(1, 1);
    location12 = new Location(1, 2);
    location21 = new Location(2, 1);
    location22 = new Location(2, 2);
    VisitedLocation visitedLocation11 = new VisitedLocation(uuid1, location11, Date.valueOf(LocalDate.of(2022, 1, 1)));
    VisitedLocation visitedLocation12 = new VisitedLocation(uuid1, location12, Date.valueOf(LocalDate.of(2022, 1, 2)));
    VisitedLocation visitedLocation21 = new VisitedLocation(uuid2, location21, Date.valueOf(LocalDate.of(2022, 2, 1)));
    VisitedLocation visitedLocation22 = new VisitedLocation(uuid2, location22, Date.valueOf(LocalDate.of(2022, 2, 2)));

    userSample1 = new User(UUID.fromString("1-1-1-1-1"), "user1", "1", "some1@mail.com");
    userSample1.addToVisitedLocations(visitedLocation11);
    userSample1.addToVisitedLocations(visitedLocation12);

    userSample2 = new User(UUID.fromString("2-2-2-2-2"), "user2", "2", "some2@mail.com");
    userSample2.addToVisitedLocations(visitedLocation21);
    userSample2.addToVisitedLocations(visitedLocation22);

    usersAtTest.add(userSample1);
    usersAtTest.add(userSample2);
  }


  @Test
  public void getUserLocation() {
    User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
    VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);
    tourGuideService.tracker.stopTracking();
    assertTrue(visitedLocation.userId.equals(user.getUserId()));
  }





  @Test
  public void trackUser() {
    User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
    VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);

    tourGuideService.tracker.stopTracking();

    assertEquals(user.getUserId(), visitedLocation.userId);
  }

  @Test
  public void getNearbyAttractions() {
    User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
    VisitedLocation userLocation = tourGuideService.trackUserLocation(user);

    NearbyAttractionsDTO attractions = tourGuideService.getNearbyAttractions(user);

    tourGuideService.tracker.stopTracking();

    assertEquals(5, attractions.getNearbyAttractions().size());
  }

  @Test
  public void getTripDeals() {
    User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

    List<Provider> providers = tourGuideService.getTripDeals(user);

    tourGuideService.tracker.stopTracking();

    assertEquals(5, providers.size());
  }


}
