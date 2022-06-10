package tourGuide;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import tourGuide.helper.InternalTestHelper;
import tourGuide.model.NearbyAttractionsDTO;
import tourGuide.model.UserLocationDTO;
import tourGuide.model.beans.Attraction;
import tourGuide.model.beans.Location;
import tourGuide.model.beans.Provider;
import tourGuide.model.beans.VisitedLocation;
import tourGuide.service.GpsUtilService;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tourGuide.service.TripPricerService;
import tourGuide.user.User;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UnitTestTourGuide {

  @Autowired
  private TourGuideService tourGuideServiceUnderTest;
  @MockBean
  private RewardsService rewardsServiceMocked;
  @MockBean
  private GpsUtilService gpsUtilServiceMocked;
  @MockBean
  private TripPricerService tripPricerServiceMocked;


  private List<User> usersAtTest;
  private User user;
  private VisitedLocation visitedLocationMockedForUser;
  private User userSample1;
  private UUID uuid1 = UUID.fromString("1-1-1-1-1");
  private UUID uuid2 = UUID.fromString("2-2-2-2-2");
  private VisitedLocation visitedLocation11;
  private VisitedLocation visitedLocation12;
  private VisitedLocation visitedLocation21;
  private VisitedLocation visitedLocation22;
  private Location location11;
  private Location location12;
  private Location location21;
  private Location location22;
  private User userSample2;

  @BeforeEach
  public void initSamples() {
    InternalTestHelper.setInternalUserNumber(0);
    tourGuideServiceUnderTest.initializeInternalUsers();

    usersAtTest = new ArrayList<>();
    location11 = new Location(1, 1);
    location12 = new Location(1, 2);
    location21 = new Location(2, 1);
    location22 = new Location(2, 2);
    visitedLocation11 = new VisitedLocation(uuid1, location11, Date.valueOf(LocalDate.of(2022, 1, 1)));
    visitedLocation12 = new VisitedLocation(uuid1, location12, Date.valueOf(LocalDate.of(2022, 1, 2)));
    visitedLocation21 = new VisitedLocation(uuid2, location21, Date.valueOf(LocalDate.of(2022, 2, 1)));
    visitedLocation22 = new VisitedLocation(uuid2, location22, Date.valueOf(LocalDate.of(2022, 2, 2)));

    userSample1 = new User(UUID.fromString("1-1-1-1-1"), "user1", "1", "some1@mail.com");
    userSample1.addToVisitedLocations(visitedLocation11);
    userSample1.addToVisitedLocations(visitedLocation12);

    userSample2 = new User(UUID.fromString("2-2-2-2-2"), "user2", "2", "some2@mail.com");
    userSample2.addToVisitedLocations(visitedLocation21);
    userSample2.addToVisitedLocations(visitedLocation22);

    usersAtTest.add(userSample1);
    usersAtTest.add(userSample2);

    user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
    visitedLocationMockedForUser = new VisitedLocation(
      user.getUserId(),
      new Location(1.0d, 1.0d),
      Date.from(LocalDateTime.now().toInstant(ZoneOffset.UTC))
    );
  }

  @Test
  public void getUserLocation() {
    when(gpsUtilServiceMocked.getVisitedLocation(user.getUserId())).thenReturn(visitedLocationMockedForUser);
    VisitedLocation visitedLocation = tourGuideServiceUnderTest.trackUserLocation(user);
    tourGuideServiceUnderTest.tracker.stopTracking();
    assertTrue(visitedLocation.userId.equals(user.getUserId()));
  }

  @Test
  public void addUser() {
    User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
    User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");


    tourGuideServiceUnderTest.addUser(user);
    tourGuideServiceUnderTest.addUser(user2);

    User retrievedUser = tourGuideServiceUnderTest.getUser(user.getUserName());
    User retrievedUser2 = tourGuideServiceUnderTest.getUser(user2.getUserName());

    tourGuideServiceUnderTest.tracker.stopTracking();

    assertEquals(user, retrievedUser);
    assertEquals(user2, retrievedUser2);
  }

  @Test
  public void getAllCurrentLocationsTest() {
    List<UserLocationDTO> expected = new ArrayList<>();
    UserLocationDTO userLocationDTO1 = new UserLocationDTO();
    userLocationDTO1.setUserId(uuid1);
    userLocationDTO1.setLocation(location12);
    UserLocationDTO userLocationDTO2 = new UserLocationDTO();
    userLocationDTO2.setUserId(uuid2);
    userLocationDTO2.setLocation(location22);
    expected.add(userLocationDTO1);
    expected.add(userLocationDTO2);

    List<UserLocationDTO> result = tourGuideServiceUnderTest.getAllCurrentLocations(usersAtTest);
    assertThat(result).isEqualTo(expected);
  }

  @Test
  public void getAllUsers() {
    User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
    User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

    tourGuideServiceUnderTest.addUser(user);
    tourGuideServiceUnderTest.addUser(user2);

    List<User> allUsers = tourGuideServiceUnderTest.getAllUsers();

    tourGuideServiceUnderTest.tracker.stopTracking();

    assertTrue(allUsers.contains(user));
    assertTrue(allUsers.contains(user2));
  }

  @Test
  public void getTripDeals() {
    List<Provider> providerMocked = new ArrayList<>();
    providerMocked.add(new Provider(UUID.randomUUID(), "provider1", 1.0d));
    providerMocked.add(new Provider(UUID.randomUUID(), "provider2", 1.0d));
    providerMocked.add(new Provider(UUID.randomUUID(), "provider3", 1.0d));
    providerMocked.add(new Provider(UUID.randomUUID(), "provider4", 1.0d));
    providerMocked.add(new Provider(UUID.randomUUID(), "provider5", 1.0d));
    when(tripPricerServiceMocked.getPrice("test-server-api-key", user, 0)).thenReturn(providerMocked);

    List<Provider> providers = tourGuideServiceUnderTest.getTripDeals(user);

    tourGuideServiceUnderTest.tracker.stopTracking();

    assertEquals(5, providers.size());
  }

  @Test
  public void trackUser() {
    when(gpsUtilServiceMocked.getVisitedLocation(user.getUserId())).thenReturn(visitedLocationMockedForUser);

    VisitedLocation visitedLocation = tourGuideServiceUnderTest.trackUserLocation(user);

    tourGuideServiceUnderTest.tracker.stopTracking();

    assertEquals(user.getUserId(), visitedLocation.userId);
  }

  @Test
  public void getNearbyAttractions() {
    List<Attraction> attractions = new ArrayList<>();
    Attraction attraction1 = new Attraction("attraction1", "city1", "state1", 1.0d, 1.0d);
    Attraction attraction2 = new Attraction("attraction2", "city1", "state1", 2.0d, 1.0d);
    Attraction attraction3 = new Attraction("attraction3", "city1", "state1", 3.0d, 1.0d);
    Attraction attraction4 = new Attraction("attraction4", "city1", "state1", 4.0d, 1.0d);
    Attraction attraction5 = new Attraction("attraction5", "city1", "state1", 5.0d, 1.0d);
    attractions.add(attraction1);
    attractions.add(attraction2);
    attractions.add(attraction3);
    attractions.add(attraction4);
    attractions.add(attraction5);

    when(gpsUtilServiceMocked.getVisitedLocation(user.getUserId())).thenReturn(visitedLocationMockedForUser);
    when(rewardsServiceMocked.getDistance(attraction1, visitedLocationMockedForUser.getLocation())).thenReturn(1.0d);
    when(rewardsServiceMocked.getDistance(attraction2, visitedLocationMockedForUser.getLocation())).thenReturn(2.0d);
    when(rewardsServiceMocked.getDistance(attraction3, visitedLocationMockedForUser.getLocation())).thenReturn(3.0d);
    when(rewardsServiceMocked.getDistance(attraction4, visitedLocationMockedForUser.getLocation())).thenReturn(4.0d);
    when(rewardsServiceMocked.getDistance(attraction5, visitedLocationMockedForUser.getLocation())).thenReturn(5.0d);
    when(gpsUtilServiceMocked.getAttractions()).thenReturn(attractions);

    NearbyAttractionsDTO nearbyAttractions = tourGuideServiceUnderTest.getNearbyAttractions(user);

    tourGuideServiceUnderTest.tracker.stopTracking();

    assertEquals(5, nearbyAttractions.getNearbyAttractions().size());
  }


}
