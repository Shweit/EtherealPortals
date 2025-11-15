package com.shweit.etherealportals.model;

import java.util.*;

/**
 * Represents a logical grouping of portals. Teleportation only allowed within group.
 */
public class PortalGroup {
  private final String name;
  private final Map<String, Portal> portals = new LinkedHashMap<>();

  public PortalGroup(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public Collection<Portal> getPortals() {
    return Collections.unmodifiableCollection(portals.values());
  }

  public Portal getPortal(String portalName) {
    return portals.get(portalName.toLowerCase());
  }

  public boolean addPortal(Portal portal) {
    String key = portal.getName().toLowerCase();
    if (portals.containsKey(key)) {
      return false;
    }
    portals.put(key, portal);
    return true;
  }

  public boolean removePortal(String portalName) {
    return portals.remove(portalName.toLowerCase()) != null;
  }

  public boolean isEmpty() {
    return portals.isEmpty();
  }
}
