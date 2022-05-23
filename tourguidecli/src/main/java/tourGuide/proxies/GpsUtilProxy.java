package tourGuide.proxies;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import tourGuide.beans.Attraction;
import tourGuide.beans.VisitedLocation;

import java.util.List;

@FeignClient(name="gpsutilms", url="localhost:8081/gpsUtil")
public interface GpsUtilProxy {

  @GetMapping("/visitedLoc")
  VisitedLocation getVisitedLocation(@RequestParam String userId);

  @GetMapping("/attractions")
  List<Attraction> getAttractions();

}
