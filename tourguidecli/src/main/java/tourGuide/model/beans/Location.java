package tourGuide.model.beans;

import java.util.Objects;

public class Location {
  public final double longitude;
  public final double latitude;

  public Location(double longitude, double latitude) {
    this.latitude = latitude;
    this.longitude = longitude;
  }

  @Override
  public String toString() {
    return "Location{" +
      "longitude=" + longitude +
      ", latitude=" + latitude +
      '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Location location = (Location) o;
    return Double.compare(location.longitude, longitude) == 0 && Double.compare(location.latitude, latitude) == 0;
  }

  @Override
  public int hashCode() {
    return Objects.hash(longitude, latitude);
  }
}
