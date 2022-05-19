package com.elienferre.rewardscentralms.service;

import org.springframework.stereotype.Service;
import rewardCentral.RewardCentral;

import java.util.UUID;

@Service
public class RewardCentralService {

  private static final RewardCentral rewardCentral = new RewardCentral();

  public int getAttractionRewardPoints(String attractionId, String userId) {
    return rewardCentral.getAttractionRewardPoints(
      UUID.fromString(attractionId),
      UUID.fromString(userId)
    );
  }
}
