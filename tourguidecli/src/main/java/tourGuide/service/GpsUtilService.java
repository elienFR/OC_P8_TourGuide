package tourGuide.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tourGuide.beans.Attraction;
import tourGuide.proxies.GpsUtilProxy;

import java.nio.ByteBuffer;
import java.util.List;

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
}
