package com.shweit.etherealportals.listener;

import com.shweit.etherealportals.EtherealPortals;
import com.shweit.etherealportals.manager.CooldownManager;
import com.shweit.etherealportals.manager.IconManager;
import com.shweit.etherealportals.manager.PortalManager;
import com.shweit.etherealportals.model.Portal;
import com.shweit.etherealportals.model.PortalGroup;
import com.shweit.etherealportals.model.PortalIcon;
import com.shweit.etherealportals.util.MessageUtils;
import com.shweit.etherealportals.util.SkullUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

/** Detects portal entry when player moves. */
public class PlayerMoveListener implements Listener {
  private final EtherealPortals plugin;
  private final Set<UUID> insidePortal = new HashSet<>();

  /**
   * Creates a new player move listener.
   *
   * @param plugin the plugin instance
   */
  public PlayerMoveListener(EtherealPortals plugin) {
    this.plugin = plugin;
  }

  /**
   * Handles player movement events to detect portal entry.
   *
   * @param event the player move event
   */
  @EventHandler
  public void onMove(PlayerMoveEvent event) {
    Player player = event.getPlayer();
    PortalManager pm = plugin.getPortalManager();
    PortalManager.PortalResult result = pm.findPortalAt(event.getTo(),
        plugin.getHitboxWidth(), plugin.getHitboxDepth(), plugin.getHitboxHeight());
    UUID uuid = player.getUniqueId();
    if (result != null) {
      if (!insidePortal.contains(uuid)) {
        insidePortal.add(uuid);
        handlePortalEnter(player, result.getPortal(), result.getGroup());
      }
    } else {
      insidePortal.remove(uuid);
    }
  }

  private void handlePortalEnter(Player player, Portal source, PortalGroup group) {
    if (group == null) {
      return;
    }
    int count = group.getPortals().size();
    if (count == 2) {
      // Direct teleport to other portal
      Portal target = group.getPortals().stream()
          .filter(p -> !p.getName().equals(source.getName()))
          .findFirst().orElse(null);
      if (target != null) {
        teleport(player, target);
      }
    } else if (count >= 3) {
      openSelectionInventory(player, group, source);
    }
  }

  private void openSelectionInventory(Player player, PortalGroup group, Portal source) {
    int options = group.getPortals().size() - 1;
    int rows = Math.min(6, Math.max(1, (int) Math.ceil(options / 9.0)));
    int size = rows * 9;
    String title = ChatColor.DARK_PURPLE + "Select Portal";
    Inventory inv = Bukkit.createInventory(player, size, title);
    IconManager im = plugin.getIconManager();
    group.getPortals().stream()
        .filter(p -> !p.getName().equals(source.getName()))
        .forEach(portal -> {
          // Create nice display name (e.g., "Home #1" instead of just "1")
          String displayName = formatPortalDisplayName(group.getName(), portal.getName());
          String coloredDisplayName = ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + displayName;

          ItemStack item;
          String iconName = portal.getIconName();
          if (iconName != null) {
            PortalIcon icon = im.getIcon(iconName);
            if (icon != null) {
              item = SkullUtils.createHead(icon.getBase64(), coloredDisplayName);
            } else {
              item = SkullUtils.createHead(plugin.getDefaultPortalTexture(), coloredDisplayName);
            }
          } else {
            item = SkullUtils.createHead(plugin.getDefaultPortalTexture(), coloredDisplayName);
          }
          ItemMeta meta = item.getItemMeta();
          if (meta != null) {
            if (!meta.hasDisplayName()) {
              meta.setDisplayName(coloredDisplayName);
            }

            // Store group and portal name in NBT for reliable lookup
            NamespacedKey groupKey = new NamespacedKey(plugin, "portal_group");
            NamespacedKey portalKey = new NamespacedKey(plugin, "portal_name");
            meta.getPersistentDataContainer().set(groupKey, PersistentDataType.STRING,
                group.getName());
            meta.getPersistentDataContainer().set(portalKey, PersistentDataType.STRING,
                portal.getName());

            List<String> lore = new ArrayList<>();
            lore.add(" ");
            lore.add(ChatColor.GRAY + MessageUtils.formatCoords(portal.getBaseLocation()));
            lore.add(ChatColor.GRAY + portal.getBaseLocation().getWorld().getName());
            lore.add(" ");
            lore.add(ChatColor.GREEN + "Click to teleport!");
            meta.setLore(lore);
            if (!item.setItemMeta(meta)) {
              plugin.getLogger().warning(
                  "Failed to set item meta for portal: " + portal.getName());
            }
          }
          inv.addItem(item);
        });
    player.openInventory(inv);
  }

  /**
   * Formats a portal name for display in the GUI.
   * For player-created portals (e.g., "playername:home" with portal "1"),
   * returns "Home #1".
   * For command-created portals, returns the portal name as-is.
   *
   * @param groupName the full group name
   * @param portalName the portal name (usually a number for player-created portals)
   * @return the formatted display name
   */
  private String formatPortalDisplayName(String groupName, String portalName) {
    // Check if this is a player-created portal (format: "playername:basename")
    int colonIndex = groupName.indexOf(':');
    if (colonIndex != -1 && colonIndex < groupName.length() - 1) {
      // Extract base name after colon
      String baseName = groupName.substring(colonIndex + 1);
      // Capitalize first letter
      if (!baseName.isEmpty()) {
        baseName = Character.toUpperCase(baseName.charAt(0)) + baseName.substring(1);
      }
      // Return formatted name like "Home #1"
      return baseName + " #" + portalName;
    }
    // Command-created portal: return portal name as-is
    return portalName;
  }

  private void teleport(Player player, Portal target) {
    CooldownManager cm = plugin.getCooldownManager();
    if (!cm.canTeleport(player.getUniqueId())) {
      if (cm.canMessage(player.getUniqueId())) {
        MessageUtils.cooldown(player, cm.remainingTeleport(player.getUniqueId()));
        cm.triggerMessage(player.getUniqueId());
      }
      return;
    }
    // animation: burst + delay
    player.getWorld().spawnParticle(Particle.PORTAL,
        player.getLocation(), 40, 0.5, 0.5, 0.5, 0.2);
    player.getWorld().playSound(player.getLocation(),
        Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
    Location targetLoc = target.getCenterLocation();
    Bukkit.getScheduler().runTaskLater(plugin, () -> {
      player.teleportAsync(targetLoc).thenRun(() -> {
        targetLoc.getWorld().spawnParticle(Particle.PORTAL,
            targetLoc, 50, 0.5, 0.5, 0.5, 0.25);
        targetLoc.getWorld().playSound(targetLoc,
            Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
        MessageUtils.teleport(player, target.getName());
        cm.triggerTeleport(player.getUniqueId());
      });
    }, 10L); // 0.5s delay
  }
}
