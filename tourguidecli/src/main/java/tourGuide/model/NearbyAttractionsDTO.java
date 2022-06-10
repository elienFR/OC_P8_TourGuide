package tourGuide.model;

import tourGuide.model.beans.Location;

import java.util.List;

public class NearbyAttractionsDTO {

  private Location userLocation;
  private List<NearbyAttractionDTO> nearbyAttractions;

  public Location getUserLocation() {
    return userLocation;
  }

  public void setUserLocation(Location userLocation) {
    this.userLocation = userLocation;
  }

  public List<NearbyAttractionDTO> getNearbyAttractions() {
    return nearbyAttractions;
  }

  public void setNearbyAttractions(List<NearbyAttractionDTO> nearbyAttractions) {
    this.nearbyAttractions = nearbyAttractions;
  }
}
