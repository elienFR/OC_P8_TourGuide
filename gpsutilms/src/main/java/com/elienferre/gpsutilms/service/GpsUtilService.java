package com.elienferre.gpsutilms.service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GpsUtilService {

  private final GpsUtil gpsUtil = new GpsUtil();

  public VisitedLocation getUserLocation(UUID userId) {
    return gpsUtil.getUserLocation(userId);
  }

  public List<Attraction> getAttractions() {
    return gpsUtil.getAttractions();
  }

}
