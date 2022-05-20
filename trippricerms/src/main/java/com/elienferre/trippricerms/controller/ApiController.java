package com.elienferre.trippricerms.controller;

import com.elienferre.trippricerms.service.TripPricerService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tripPricer.Provider;

import java.util.List;

@RestController
@RequestMapping("/tripPricer")
public class ApiController {

  private static final Logger LOGGER = LogManager.getLogger(ApiController.class);
  @Autowired
  private TripPricerService tripPricerService;

  @GetMapping("/getPrice")
  public List<Provider> getPrice(
    @RequestParam String apiKey,
    @RequestParam(name = "attId") String attractionId,
    @RequestParam(name = "adts") String adults,
    @RequestParam(name = "chldn") String children,
    @RequestParam(name = "nS") String nightsStay,
    @RequestParam(name = "rP") String rewardsPoints) {
    LOGGER.info("GET request on /tripPricer/getPrice?" +
      "apikey=" + apiKey +
      "&attId=" + attractionId +
      "&adts=" + adults +
      "&chldn=" + children +
      "&nS=" + nightsStay +
      "&rP=" + rewardsPoints
    );
    return tripPricerService.getPrice(apiKey, attractionId, adults, children, nightsStay, rewardsPoints);
  }

}
