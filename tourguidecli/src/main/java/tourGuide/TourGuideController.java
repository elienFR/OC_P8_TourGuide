package tourGuide;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jsoniter.output.JsonStream;

import tourGuide.model.UserLocationDTO;
import tourGuide.model.beans.Provider;
import tourGuide.model.beans.VisitedLocation;
import tourGuide.service.TourGuideService;
import tourGuide.user.User;

@RestController
public class TourGuideController {

	@Autowired
	private TourGuideService tourGuideService;

  /**
   *
   * This endpoint is the front page of the api.
   *
   * @return a welcoming message.
   */
    @RequestMapping("/")
    public String index() {
        return "Greetings from TourGuide!";
    }

  /**
   * This endpoint is used to track one user location
   *
   * @param userName is the username of the tracked user
   * @return a string containing the last longitude and latitude of a user.
   */
  @RequestMapping("/getLocation")
    public String getLocation(@RequestParam String userName) {
    	VisitedLocation visitedLocation = tourGuideService.getUserLocation(getUser(userName));
		return JsonStream.serialize(visitedLocation.location);
    }
    
    //  TODO: Change this method to no longer return a List of Attractions.
 	//  Instead: Get the closest five tourist attractions to the user - no matter how far away they are.
 	//  Return a new JSON object that contains:
    	// Name of Tourist attraction, 
        // Tourist attractions lat/long, 
        // The user's location lat/long, 
        // The distance in miles between the user's location and each of the attractions.
        // The reward points for visiting each Attraction.
        //    Note: Attraction reward points can be gathered from RewardsCentral

  /**
   * This endpoint is used to display for a specific user, the five closest attraction from this user,
   * the longitude and latitude of each of these attractions, the longitude and latitude of the user,
   * the distance in miles between the user and the attraction, and eventually the reward points given
   * to the user if all these attractions are visited.
   *
   * @param userName is the username of the concerned user you want to get the nearest attractions.
   * @return see description above to know what the JSON contains.
   */
    @RequestMapping("/getNearbyAttractions") 
    public String getNearbyAttractions(@RequestParam String userName) {
    	VisitedLocation visitedLocation = tourGuideService.getUserLocation(getUser(userName));
    	return JsonStream.serialize(tourGuideService.getNearbyAttractions(visitedLocation));
    }

  /**
   * This endpoint calculates reward for a specific user.
   *
   * @param userName is the username of the user you want to calculate the reward points
   * @return an integer corresponding of the reward points owned by a user.
   */
  @RequestMapping("/getRewards")
    public String getRewards(@RequestParam String userName) {
    	return JsonStream.serialize(tourGuideService.getUserRewards(getUser(userName)));
    }

  /**
   * This endpoint is used to give you all the last location of each user in the app's database.
   *
   * @return a JSON list of all user's last location from DB.
   */
  @RequestMapping("/getAllCurrentLocations")
    public List<UserLocationDTO> getAllCurrentLocations() {
    	return tourGuideService.getAllCurrentLocations(tourGuideService.getAllUsers());
    }

  /**
   * This endpoint is used to fetch trip deals from a user, thanks to its reward points and its parameters.
   *
   * @param userName is the username of the user you want to create a trip deal.
   * @return a list of providers with their name, the price they give you their service, and the uuid of the trip deal.
   */
  @RequestMapping("/getTripDeals")
    public String getTripDeals(@RequestParam String userName) {
    	List<Provider> providers = tourGuideService.getTripDeals(getUser(userName));
    	return JsonStream.serialize(providers);
    }
    
    private User getUser(String userName) {
    	return tourGuideService.getUser(userName);
    }
   

}