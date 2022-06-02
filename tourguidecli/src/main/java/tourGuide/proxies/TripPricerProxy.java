package tourGuide.proxies;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import tourGuide.beans.Provider;

import java.util.List;

@FeignClient(name="trippricerms", url="localhost:8182/tripPricer")
public interface TripPricerProxy {

  @GetMapping("/getPrice")
  public List<Provider> getPrice(
    @RequestParam String apiKey,
    @RequestParam(name = "attId") String attractionId,
    @RequestParam(name = "adts") String adults,
    @RequestParam(name = "chldn") String children,
    @RequestParam(name = "nS") String nightsStay,
    @RequestParam(name = "rP") String rewardsPoints);

}
