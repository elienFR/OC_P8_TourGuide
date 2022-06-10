package tourGuide;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import tourGuide.model.beans.Attraction;
import tourGuide.model.beans.Location;
import tourGuide.model.beans.VisitedLocation;
import tourGuide.proxies.RewardCentralProxy;
import tourGuide.service.GpsUtilService;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tourGuide.user.User;
import tourGuide.user.UserReward;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UnitTestRewardsService {

  @Autowired
  private RewardsService rewardsServiceUnderTest;

  @MockBean
  private TourGuideService tourGuideServiceMocked;

  @MockBean
  private GpsUtilService gpsUtilServiceMocked;

  @MockBean
  private RewardCentralProxy rewardCentralProxyMocked;

  private List<Attraction> attractionsListSample;

  private Attraction attractionSample1;
  private User userSample1;

  @BeforeEach
  public void initTest(){
    userSample1 = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
    userSample1.addToVisitedLocations(new VisitedLocation(
      userSample1.getUserId(),
      new Location(1.0d,1.0d),
      Date.from(LocalDateTime.now().toInstant(ZoneOffset.UTC))
    ));
    // Mocking gpsUtil.getAttractions()
    attractionsListSample = new ArrayList<>();
    attractionSample1 = new Attraction("attraction1","city1","state1",1.0d,1.0d);
    attractionsListSample.add(attractionSample1);
    when(gpsUtilServiceMocked.getAttractions()).thenReturn(attractionsListSample);

  }

  @Test
  public void userGetRewards() {
    when(rewardCentralProxyMocked.getAttractionRewardPoints(attractionSample1.attractionId.toString(), userSample1.getUserId().toString()))
      .thenReturn(28);

    Attraction attraction = gpsUtilServiceMocked.getAttractions().get(0);
    userSample1.addToVisitedLocations(new VisitedLocation(userSample1.getUserId(), attraction, new Date()));
    rewardsServiceUnderTest.calculateRewards(userSample1);

    List<UserReward> resultsOfUserRewards = userSample1.getUserRewards();

    assertTrue(resultsOfUserRewards.size() == 1);
  }

  @Test
  public void isWithinAttractionProximity() {
    Attraction attraction = gpsUtilServiceMocked.getAttractions().get(0);
    assertTrue(rewardsServiceUnderTest.isWithinAttractionProximity(attraction, attraction));
  }

  @Test
  public void nearAllAttractions() {
    rewardsServiceUnderTest.setProximityBuffer(Integer.MAX_VALUE);

    Attraction attractionSample2 = new Attraction("attraction2","city1","state1",2.0d,1.0d);
    Attraction attractionSample3 = new Attraction("attraction3","city1","state1",3.0d,1.0d);
    Attraction attractionSample4 = new Attraction("attraction4","city1","state1",4.0d,1.0d);
    Attraction attractionSample5 = new Attraction("attraction5","city1","state1",5.0d,1.0d);
    attractionsListSample.add(attractionSample2);
    attractionsListSample.add(attractionSample3);
    attractionsListSample.add(attractionSample4);
    attractionsListSample.add(attractionSample5);

    when(rewardCentralProxyMocked.getAttractionRewardPoints(attractionSample1.attractionId.toString(), userSample1.getUserId().toString())).thenReturn(1);
    when(rewardCentralProxyMocked.getAttractionRewardPoints(attractionSample2.attractionId.toString(), userSample1.getUserId().toString())).thenReturn(2);
    when(rewardCentralProxyMocked.getAttractionRewardPoints(attractionSample3.attractionId.toString(), userSample1.getUserId().toString())).thenReturn(3);
    when(rewardCentralProxyMocked.getAttractionRewardPoints(attractionSample4.attractionId.toString(), userSample1.getUserId().toString())).thenReturn(4);
    when(rewardCentralProxyMocked.getAttractionRewardPoints(attractionSample5.attractionId.toString(), userSample1.getUserId().toString())).thenReturn(5);

    rewardsServiceUnderTest.calculateRewards(userSample1);
    List<UserReward> results = userSample1.getUserRewards();
    List<Attraction> attractionsComparison = gpsUtilServiceMocked.getAttractions();

    assertEquals(attractionsComparison.size(), results.size());
  }

}