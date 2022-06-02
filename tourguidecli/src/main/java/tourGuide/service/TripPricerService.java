package tourGuide.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tourGuide.beans.Provider;
import tourGuide.proxies.TripPricerProxy;
import tourGuide.user.User;

import java.util.List;


@Service
public class TripPricerService {
  @Autowired
  private TripPricerProxy tripPricerProxy;

  public List<Provider> getPrice(String tripPricerApiKey, User user, int cumulatativeRewardPoints) {
    return tripPricerProxy.getPrice(
      tripPricerApiKey,
      user.getUserId().toString(),
      String.valueOf(user.getUserPreferences().getNumberOfAdults()),
      String.valueOf(user.getUserPreferences().getNumberOfChildren()),
      String.valueOf(user.getUserPreferences().getTripDuration()),
      String.valueOf(cumulatativeRewardPoints)
    );
  }
}
