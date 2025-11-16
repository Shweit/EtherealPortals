package com.shweit.etherealportals.listener;

import com.shweit.etherealportals.EtherealPortals;
import com.shweit.etherealportals.manager.CooldownManager;
import com.shweit.etherealportals.manager.PortalManager;
import com.shweit.etherealportals.model.Portal;
import com.shweit.etherealportals.model.PortalGroup;
import com.shweit.etherealportals.util.MessageUtils;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/** Blocks item interaction in plugin GUIs (simple protection). */
public class InventoryClickListener implements Listener {
  private final EtherealPortals plugin;

  /**
   * Creates a new inventory click listener.
   *
   * @param plugin the plugin instance
   */
  public InventoryClickListener(EtherealPortals plugin) {
    this.plugin = plugin;
  }

  /**
   * Handles inventory click events for plugin GUIs.
   *
   * @param event the inventory click event
   */
  @EventHandler
  public void onClick(InventoryClickEvent event) {
    String title = event.getView().getTitle();
    if (title.contains("Icons")) {
      event.setCancelled(true);
    } else if (title.contains("Select Portal")) {
      event.setCancelled(true);
      if (!(event.getWhoClicked() instanceof Player)) {
        return;
      }
      Player player = (Player) event.getWhoClicked();
      ItemStack current = event.getCurrentItem();
      if (current == null || !current.hasItemMeta()) {
        return;
      }
      String displayName = current.getItemMeta().getDisplayName();
      String display = ChatColor.stripColor(displayName);
      if (display == null || display.isEmpty()) {
        return;
      }
      teleportByName(player, display.toLowerCase());
    }
  }

  private void teleportByName(Player player, String portalName) {
    PortalManager pm = plugin.getPortalManager();
    Portal target = null;
    for (PortalGroup group : pm.getGroups()) {
      Portal p = group.getPortal(portalName);
      if (p != null) {
        target = p;
        break;
      }
    }
    if (target == null) {
      MessageUtils.error(player, "Could not find portal &d" + portalName + "&c.");
      return;
    }
    CooldownManager cm = plugin.getCooldownManager();
    if (!cm.canTeleport(player.getUniqueId())) {
      if (cm.canMessage(player.getUniqueId())) {
        MessageUtils.cooldown(player, cm.remainingTeleport(player.getUniqueId()));
        cm.triggerMessage(player.getUniqueId());
      }
      return;
    }
    player.closeInventory();
    player.getWorld().spawnParticle(Particle.PORTAL, player.getLocation(),
        40, 0.5, 0.5, 0.5, 0.2);
    player.getWorld().playSound(player.getLocation(),
        Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
    final Portal finalTarget = target;
    org.bukkit.Location targetLoc = finalTarget.getCenterLocation();
    org.bukkit.Bukkit.getScheduler().runTaskLater(plugin, () -> {
      player.teleportAsync(targetLoc).thenRun(() -> {
        targetLoc.getWorld().spawnParticle(Particle.PORTAL, targetLoc,
            50, 0.5, 0.5, 0.5, 0.25);
        targetLoc.getWorld().playSound(targetLoc,
            Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
        MessageUtils.teleport(player, finalTarget.getName());
        cm.triggerTeleport(player.getUniqueId());
      });
    }, 10L);
  }
}
