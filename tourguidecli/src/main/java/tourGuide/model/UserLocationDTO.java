package tourGuide.model;

import tourGuide.model.beans.Location;

import java.util.Objects;
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
    return "UserLocationDTO{"
      + "userId=" + userId.toString()
      + ", "
      + location.toString()
      + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    UserLocationDTO that = (UserLocationDTO) o;
    return getUserId().equals(that.getUserId()) && getLocation().equals(that.getLocation());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getUserId(), getLocation());
  }
}
