package tourGuide.model.beans;

import java.util.Date;
import java.util.UUID;

public class VisitedLocation {
  public final UUID userId;
  public final Location location;
  public final Date timeVisited;


  public VisitedLocation(UUID userId, Location location, Date timeVisited) {
    this.userId = userId;
    this.location = location;
    this.timeVisited = timeVisited;
  }


  public UUID getUserId() {
    return userId;
  }

  public Location getLocation() {
    return location;
  }

  public Date getTimeVisited() {
    return timeVisited;
  }
}