package com.elienferre.gpsutilms.controller;

import com.elienferre.gpsutilms.service.GpsUtilService;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/gpsUtil")
public class ApiController {

  @Autowired
  private GpsUtilService gpsUtilService;

  private static final Logger LOGGER = LogManager.getLogger(ApiController.class);

  @GetMapping("/visitedLoc")
  public VisitedLocation getVisitedLocation(@RequestParam String userId){
    LOGGER.info("GET call on /gpsUtil/visitedLoc?userId="+userId);
    return gpsUtilService.getUserLocation(userId);
  }

  @GetMapping("/attractions")
  public List<Attraction> getAttractions() {
    LOGGER.info("GET call on /gpsUtil/attractions");
    return gpsUtilService.getAttractions();
  }


}
