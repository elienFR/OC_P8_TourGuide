package tourGuide.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tourGuide.model.beans.Attraction;
import tourGuide.model.beans.VisitedLocation;
import tourGuide.proxies.GpsUtilProxy;

import java.util.List;
import java.util.UUID;

/**
 * This Service manages gpsUtil microservice
 */
@Service
public class GpsUtilService {

  @Autowired
  private GpsUtilProxy gpsUtilProxy;

  /**
   * This method return a list of attraction from gpsUtil proxy.
   * @return A list of attractions.
   */
  public List<Attraction> getAttractions() {
    return gpsUtilProxy.getAttractions();
  }

  /**
   * This method returns the visited location of a specific user.
   * @param userId is the UUID of the concerned user
   * @return a visited location object
   */
  public VisitedLocation getVisitedLocation(UUID userId) {
    return gpsUtilProxy.getVisitedLocation(userId.toString());
  }
}
