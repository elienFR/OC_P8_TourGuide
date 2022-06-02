package tourGuide.model;

import tourGuide.model.beans.Location;

import java.util.UUID;

public class UserLocationDTO {
  private UUID userId;
  private Location location;

  public UUID getUserId() {
    return userId;
  }

  public void setUserId(UUID userId) {
    this.userId = userId;
  }

  public Location getLocation() {
    return location;
  }

  public void setLocation(Location location) {
    this.location = location;
  }

  @Override
  public String toString() {
    return "UserLocationDTO{" +
      "userId=" + userId.toString() +
      ", location="
      + "{"
      + "longitude:" + getLocation().longitude
      + ", latitude:" + getLocation().latitude
      + "}"
      + "}";
  }
}
