package com.shweit.etherealportals.visual;

import com.shweit.etherealportals.EtherealPortals;
import com.shweit.etherealportals.model.PortalGroup;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TextDisplay;

/** Periodic task spawning particle spirals. */
public class VisualEffectTask implements Runnable {
  private final EtherealPortals plugin;
  private volatile int taskId = -1;

  /**
   * Creates a new visual effect task.
   *
   * @param plugin the plugin instance
   */
  public VisualEffectTask(EtherealPortals plugin) {
    this.plugin = plugin;
  }

  /**
   * Starts the visual effect task.
   */
  public void start() {
    if (taskId != -1) {
      return;
    }
    // One-time sync: create missing TextDisplays and ArmorStands for existing portals
    syncMissingTextDisplays();
    syncMissingArmorStands();
    taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 40L, 20L);
  }

  /**
   * Stops the visual effect task.
   */
  public void stop() {
    if (taskId != -1) {
      Bukkit.getScheduler().cancelTask(taskId);
      taskId = -1;
    }
  }

  @Override
  public void run() {
    double t = (System.currentTimeMillis() % 5000) / 5000.0; // progress
    plugin.getPortalManager().getGroups().forEach(group ->
        group.getPortals().forEach(portal -> {
          Location center = portal.getCenterLocation().clone().add(0, 0.1, 0);
          spawnSpiral(center, t);
        }));
  }

  private void spawnSpiral(Location center, double progress) {
    for (int i = 0; i < 24; i++) {
      double angle = (progress * 2 * Math.PI) + (i * Math.PI / 6);
      double radius = 0.7;
      double y = (i / 24.0) * 2.5;
      double x = center.getX() + Math.cos(angle) * radius;
      double z = center.getZ() + Math.sin(angle) * radius;
      center.getWorld().spawnParticle(Particle.END_ROD, x, center.getY() + y, z,
          1, 0, 0, 0, 0);
    }
  }

  /**
   * Removes the text display and armor stand marker for a specific portal.
   *
   * @param groupName The name of the group
   * @param portalName The name of the portal whose display should be removed
   */
  public void removeTextDisplay(String groupName, String portalName) {
    String textTag = "ep_portal:" + groupName.toLowerCase() + ":" + portalName.toLowerCase();
    String markerTag = "ep_portal_marker:" + groupName.toLowerCase()
        + ":" + portalName.toLowerCase();
    Bukkit.getWorlds().forEach(world -> {
      world.getEntitiesByClass(TextDisplay.class).stream()
          .filter(td -> td.getScoreboardTags().contains(textTag))
          .forEach(Entity::remove);
      world.getEntitiesByClass(ArmorStand.class).stream()
          .filter(as -> as.getScoreboardTags().contains(markerTag))
          .forEach(Entity::remove);
    });
  }

  /**
   * Removes all text displays for all portals in a group.
   *
   * @param group The portal group whose displays should be removed
   */
  public void removeGroupTextDisplays(PortalGroup group) {
    group.getPortals().forEach(portal ->
        removeTextDisplay(group.getName(), portal.getName()));
  }

  /**
   * Creates a text display for a newly added portal.
   * Called when a portal is added via command.
   * TextDisplays are persistent entities and will survive server restarts.
   *
   * @param loc The location for the text display
   * @param groupName The portal group name
   * @param portalName The portal name
   */
  public void createTextDisplay(Location loc, String groupName, String portalName) {
    createTextDisplay(loc, groupName, portalName, portalName);
  }

  /**
   * Creates a text display for a newly added portal with custom display text.
   *
   * @param loc The location for the text display
   * @param groupName The portal group name
   * @param portalName The portal name (used for tag)
   * @param displayText The text to display above the portal
   */
  public void createTextDisplay(Location loc, String groupName,
      String portalName, String displayText) {
    String tag = "ep_portal:" + groupName.toLowerCase() + ":" + portalName.toLowerCase();
    loc.getWorld().spawn(loc, TextDisplay.class, d -> {
      d.text(Component.text(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + displayText));
      d.setBillboard(Display.Billboard.CENTER);
      d.setSeeThrough(true);
      d.setShadowed(true);
      d.setViewRange(50);
      d.addScoreboardTag(tag);
    });
  }

  /**
   * Syncs missing TextDisplays for existing portals on startup.
   * Only creates TextDisplays that don't already exist (e.g., portals created before this update).
   * Loads chunks if needed to check for existing TextDisplays.
   */
  private void syncMissingTextDisplays() {
    int created = 0;
    for (PortalGroup group : plugin.getPortalManager().getGroups()) {
      for (com.shweit.etherealportals.model.Portal portal : group.getPortals()) {
        Location loc = portal.getBaseLocation().clone().add(0.5, 3, 0.5);
        String tag = "ep_portal:" + group.getName().toLowerCase()
            + ":" + portal.getName().toLowerCase();

        // Load chunk to check for existing TextDisplays
        if (!loc.isChunkLoaded()) {
          loc.getChunk().load();
        }

        // Check if TextDisplay already exists
        boolean exists = loc.getWorld().getEntitiesByClass(TextDisplay.class).stream()
            .anyMatch(td -> td.getScoreboardTags().contains(tag));

        if (!exists) {
          createTextDisplay(loc, group.getName(), portal.getName());
          created++;
        }
      }
    }
    if (created > 0) {
      plugin.getLogger().info("Created " + created + " missing portal TextDisplay(s)");
    }
  }

  /**
   * Creates an invisible armor stand marker at the portal center.
   * This armor stand is used to detect when a player breaks a breakable portal.
   * Only created for portals with breakable=true.
   *
   * @param loc The center location for the armor stand
   * @param groupName The portal group name
   * @param portalName The portal name
   */
  public void createArmorStandMarker(Location loc, String groupName, String portalName) {
    String tag = "ep_portal_marker:" + groupName.toLowerCase() + ":" + portalName.toLowerCase();
    loc.getWorld().spawn(loc, ArmorStand.class, as -> {
      as.setVisible(false);
      as.setSmall(false); // Normal size for easier targeting
      as.setGravity(false);
      as.setMarker(false); // Keep hitbox so it can be damaged
      as.setInvulnerable(false); // Make sure it can be damaged
      as.setCustomName(ChatColor.GRAY + "(Portal - Punch to break)");
      as.setCustomNameVisible(false); // Hidden by default, but shows on hover
      as.addScoreboardTag(tag);
    });
  }

  /**
   * Syncs missing ArmorStand markers for existing breakable portals on startup.
   * Only creates markers for portals with breakable=true.
   * Loads chunks if needed to check for existing markers.
   */
  private void syncMissingArmorStands() {
    int created = 0;
    for (PortalGroup group : plugin.getPortalManager().getGroups()) {
      for (com.shweit.etherealportals.model.Portal portal : group.getPortals()) {
        // Only create armor stand for breakable portals
        if (!portal.isBreakable()) {
          continue;
        }

        Location loc = portal.getCenterLocation();
        String tag = "ep_portal_marker:" + group.getName().toLowerCase()
            + ":" + portal.getName().toLowerCase();

        // Load chunk to check for existing ArmorStands
        if (!loc.isChunkLoaded()) {
          loc.getChunk().load();
        }

        // Check if ArmorStand already exists
        boolean exists = loc.getWorld().getEntitiesByClass(ArmorStand.class).stream()
            .anyMatch(as -> as.getScoreboardTags().contains(tag));

        if (!exists) {
          createArmorStandMarker(loc, group.getName(), portal.getName());
          created++;
        }
      }
    }
    if (created > 0) {
      plugin.getLogger().info("Created " + created + " missing portal ArmorStand marker(s)");
    }
  }
}
