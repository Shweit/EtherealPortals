package com.shweit.etherealportals.manager;

import com.shweit.etherealportals.model.Portal;
import com.shweit.etherealportals.model.PortalGroup;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.*;

/**
 * Manages portal groups and portals in memory.
 */
public class PortalManager {
  private final Map<String, PortalGroup> groups = new LinkedHashMap<>();

  /**
   * Result class that contains both a portal and its parent group.
   */
  public static class PortalResult {
    private final Portal portal;
    private final PortalGroup group;

    public PortalResult(Portal portal, PortalGroup group) {
      this.portal = portal;
      this.group = group;
    }

    public Portal getPortal() {
      return portal;
    }

    public PortalGroup getGroup() {
      return group;
    }
  }

  public Collection<PortalGroup> getGroups() {
    return Collections.unmodifiableCollection(groups.values());
  }

  public PortalGroup getGroup(String name) {
    return groups.get(name.toLowerCase());
  }

  public PortalGroup createGroupIfAbsent(String name) {
    return groups.computeIfAbsent(name.toLowerCase(), k -> new PortalGroup(name));
  }

  public boolean deleteGroup(String name) {
    return groups.remove(name.toLowerCase()) != null;
  }

  public boolean addPortal(String groupName, String portalName, Location loc, String icon) {
    PortalGroup group = createGroupIfAbsent(groupName);
    return group.addPortal(new Portal(portalName, loc, icon));
  }

  public boolean removePortal(String groupName, String portalName) {
    PortalGroup group = getGroup(groupName);
    if (group == null) return false;
    boolean removed = group.removePortal(portalName);
    if (removed && group.isEmpty()) {
      // keep group unless explicitly deleted
    }
    return removed;
  }

  public PortalResult findPortalAt(Location playerLoc, double width, double depth, double height) {
    World world = playerLoc.getWorld();
    for (PortalGroup group : groups.values()) {
      for (Portal portal : group.getPortals()) {
        Location base = portal.getBaseLocation();
        if (!Objects.equals(world, base.getWorld())) continue;
        double minX = base.getX() - (width - 1) / 2.0;
        double maxX = base.getX() + (width + 1) / 2.0;
        double minY = base.getY();
        double maxY = base.getY() + height;
        double minZ = base.getZ() - (depth - 1) / 2.0;
        double maxZ = base.getZ() + (depth + 1) / 2.0;
        double x = playerLoc.getX();
        double y = playerLoc.getY();
        double z = playerLoc.getZ();
        if (x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ) {
          return new PortalResult(portal, group);
        }
      }
    }
    return null;
  }
}
