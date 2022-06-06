package tourGuide;


import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tourGuide.model.UserLocationDTO;
import tourGuide.model.beans.Attraction;
import tourGuide.model.beans.Location;
import tourGuide.model.beans.Provider;
import tourGuide.model.beans.VisitedLocation;
import tourGuide.helper.InternalTestHelper;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tourGuide.user.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

//TODO EFE : some test fails here. Correct them
@SpringBootTest
public class TestTourGuideService {

	@Autowired
	private TourGuideService tourGuideService;
	@Autowired
	private RewardsService rewardsService;

	private List<User> usersAtTest;
	private User user1;
	private UUID uuid1 = UUID.fromString("1-1-1-1-1");
	private UUID uuid2 = UUID.fromString("2-2-2-2-2");
	private Location location11;
	private Location location12;
	private Location location21;
	private Location location22;
	private User user2;
	@BeforeEach
	public void initSamples(){
		usersAtTest = new ArrayList<>();
		location11 = new Location(1,1);
		location12 = new Location(1,2);
		location21 = new Location(2,1);
		location22 = new Location(2,2);
		VisitedLocation visitedLocation11 = new VisitedLocation(uuid1,location11, Date.valueOf(LocalDate.of(2022,1,1)));
		VisitedLocation visitedLocation12 = new VisitedLocation(uuid1,location12, Date.valueOf(LocalDate.of(2022,1,2)));
		VisitedLocation visitedLocation21 = new VisitedLocation(uuid2,location21, Date.valueOf(LocalDate.of(2022,2,1)));
		VisitedLocation visitedLocation22 = new VisitedLocation(uuid2,location22, Date.valueOf(LocalDate.of(2022,2,2)));

		user1 = new User(UUID.fromString("1-1-1-1-1"),"user1","1","some1@mail.com");
		user1.addToVisitedLocations(visitedLocation11);
		user1.addToVisitedLocations(visitedLocation12);

		user2 = new User(UUID.fromString("2-2-2-2-2"),"user2","2","some2@mail.com");
		user2.addToVisitedLocations(visitedLocation21);
		user2.addToVisitedLocations(visitedLocation22);

		usersAtTest.add(user1);
		usersAtTest.add(user2);
	}

	@Test
	public void getUserLocation() {
		InternalTestHelper.setInternalUserNumber(0);
		
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);
		tourGuideService.tracker.stopTracking();
		assertTrue(visitedLocation.userId.equals(user.getUserId()));
	}
	
	@Test
	public void addUser() {
		InternalTestHelper.setInternalUserNumber(0);
		
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

		tourGuideService.addUser(user);
		tourGuideService.addUser(user2);
		
		User retrivedUser = tourGuideService.getUser(user.getUserName());
		User retrivedUser2 = tourGuideService.getUser(user2.getUserName());

		tourGuideService.tracker.stopTracking();
		
		assertEquals(user, retrivedUser);
		assertEquals(user2, retrivedUser2);
	}
	
	@Test
	public void getAllUsers() {
		InternalTestHelper.setInternalUserNumber(0);
		
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

		tourGuideService.addUser(user);
		tourGuideService.addUser(user2);
		
		List<User> allUsers = tourGuideService.getAllUsers();

		tourGuideService.tracker.stopTracking();
		
		assertTrue(allUsers.contains(user));
		assertTrue(allUsers.contains(user2));
	}
	
	@Test
	public void trackUser() {
		InternalTestHelper.setInternalUserNumber(0);
		
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);
		
		tourGuideService.tracker.stopTracking();
		
		assertEquals(user.getUserId(), visitedLocation.userId);
	}
	
	@Ignore // Not yet implemented
	@Test
	public void getNearbyAttractions() {
		InternalTestHelper.setInternalUserNumber(0);
		
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);
		
		List<Attraction> attractions = tourGuideService.getNearByAttractions(visitedLocation);
		
		tourGuideService.tracker.stopTracking();
		
		assertEquals(5, attractions.size());
	}
	
	public void getTripDeals() {
		InternalTestHelper.setInternalUserNumber(0);
		
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

		List<Provider> providers = tourGuideService.getTripDeals(user);
		
		tourGuideService.tracker.stopTracking();
		
		assertEquals(10, providers.size());
	}

	//TODO EFE : finish this test
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

		List<UserLocationDTO> result = tourGuideService.getAllCurrentLocations(usersAtTest);
		assertThat(result).isEqualTo(expected);
	}
	
	
}
