package com.elienferre.trippricerms.service;

import org.springframework.stereotype.Service;
import tripPricer.Provider;
import tripPricer.TripPricer;

import java.util.List;
import java.util.UUID;

@Service
public class TripPricerService {

  private static final TripPricer tripPricer = new TripPricer();


  public List<Provider> getPrice(String apiKey, String attractionId, String adults, String children, String nightsStay, String rewardsPoints) {
    return tripPricer.getPrice(
      apiKey,
      UUID.fromString(attractionId),
      Integer.parseInt(adults),
      Integer.parseInt(children),
      Integer.parseInt(nightsStay),
      Integer.parseInt(rewardsPoints)
    );
  }
}
