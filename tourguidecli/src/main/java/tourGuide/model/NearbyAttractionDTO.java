package tourGuide.model;

import tourGuide.model.beans.Location;

public class NearbyAttractionDTO {

  private String attractionName;
  private Location attractionLocation;
  private double distanceInMiles;
  private int rewardPoints;

  public String getAttractionName() {
    return attractionName;
  }

  public void setAttractionName(String attractionName) {
    this.attractionName = attractionName;
  }

  public Location getAttractionLocation() {
    return attractionLocation;
  }

  public void setAttractionLocation(Location attractionLocation) {
    this.attractionLocation = attractionLocation;
  }

  public double getDistanceInMiles() {
    return distanceInMiles;
  }

  public void setDistanceInMiles(double distanceInMiles) {
    this.distanceInMiles = distanceInMiles;
  }

  public int getRewardPoints() {
    return rewardPoints;
  }

  public void setRewardPoints(int rewardPoints) {
    this.rewardPoints = rewardPoints;
  }
}
