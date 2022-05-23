package com.elienferre.rewardscentralms.controller;

import com.elienferre.rewardscentralms.service.RewardCentralService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rewardCentral")
public class ApiController {

  private static final Logger LOGGER = LogManager.getLogger(ApiController.class);
  @Autowired
  private RewardCentralService rewardCentralService;

  @GetMapping("/attractionRewardPoint")
  public int getAttractionRewardPoints(
    @RequestParam(name = "attId") String attractionId,
    @RequestParam String userId) {
    LOGGER.info("GET Request : /rewardCentral/attractionRewardPoint?" +
      "attId=" + attractionId +
      "&userId=" + userId
    );
    return rewardCentralService.getAttractionRewardPoints(attractionId, userId);
  }

}
