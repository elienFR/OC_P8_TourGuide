package tourGuide.proxies;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="rewardcentralms", url="localhost:8181/rewardCentral")
public interface RewardCentralProxy {

  @GetMapping("/attractionRewardPoint")
  public int getAttractionRewardPoints(
    @RequestParam(name = "attId") String attractionId,
    @RequestParam String userId);

}
