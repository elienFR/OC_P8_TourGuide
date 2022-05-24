package com.elienferre.gpsutilms.service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class GpsUtilService {

  private final GpsUtil gpsUtil = new GpsUtil();

  public VisitedLocation getUserLocationThread(String userId) {
    return gpsUtil.getUserLocation(UUID.fromString(userId));
  }

  public List<Attraction> getAttractions() {
    return gpsUtil.getAttractions();
  }

}
