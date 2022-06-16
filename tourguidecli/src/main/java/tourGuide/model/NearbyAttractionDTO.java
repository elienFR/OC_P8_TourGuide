package tourGuide.model;

import tourGuide.model.beans.Location;

public class NearbyAttractionDTO {

  private String attractionName;
  private Location attractionLocation;
  private double distanceInKm;
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

  public double getDistanceInKm() {
    return distanceInKm;
  }

  public void setDistanceInKm(double distanceInKm) {
    this.distanceInKm = distanceInKm;
  }

  public int getRewardPoints() {
    return rewardPoints;
  }

  public void setRewardPoints(int rewardPoints) {
    this.rewardPoints = rewardPoints;
  }
}
