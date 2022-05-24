package com.elienferre.rewardscentralms.service;

import org.springframework.stereotype.Service;
import rewardCentral.RewardCentral;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

@Service
public class RewardCentralService {

  private static final RewardCentral rewardCentral = new RewardCentral();

  public Integer getAttractionRewardPointsThread(String attractionId, String userId) {
    return rewardCentral.getAttractionRewardPoints(
      UUID.fromString(attractionId),
      UUID.fromString(userId)
    );
  }
}
