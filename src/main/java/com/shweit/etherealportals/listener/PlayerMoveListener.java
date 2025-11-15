package com.shweit.etherealportals.listener;

import com.shweit.etherealportals.EtherealPortals;
import com.shweit.etherealportals.manager.CooldownManager;
import com.shweit.etherealportals.manager.PortalManager;
import com.shweit.etherealportals.model.Portal;
import com.shweit.etherealportals.model.PortalGroup;
import com.shweit.etherealportals.util.MessageUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.Sound;
import com.shweit.etherealportals.manager.IconManager;
import com.shweit.etherealportals.model.PortalIcon;
import com.shweit.etherealportals.util.SkullUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/** Detects portal entry when player moves. */
public class PlayerMoveListener implements Listener {
  private final EtherealPortals plugin;
  private final Set<UUID> insidePortal = new HashSet<>();

  public PlayerMoveListener(EtherealPortals plugin) {
    this.plugin = plugin;
  }

  @EventHandler
  public void onMove(PlayerMoveEvent event) {
    Player player = event.getPlayer();
    PortalManager pm = plugin.getPortalManager();
    PortalManager.PortalResult result = pm.findPortalAt(event.getTo(), plugin.getHitboxWidth(), plugin.getHitboxDepth(), plugin.getHitboxHeight());
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
    if (group == null) return;
    int count = group.getPortals().size();
    if (count == 2) {
      // Direct teleport to other portal
      Portal target = group.getPortals().stream().filter(p -> !p.getName().equals(source.getName())).findFirst().orElse(null);
      if (target != null) teleport(player, target);
    } else if (count >= 3) {
      openSelectionInventory(player, group, source);
    }
  }

  private void openSelectionInventory(Player player, PortalGroup group, Portal source) {
    int options = group.getPortals().size() - 1;
    int rows = Math.min(6, Math.max(1, (int) Math.ceil(options / 9.0)));
    int size = rows * 9;
    String title = ChatColor.DARK_PURPLE + "Select Portal";
    org.bukkit.inventory.Inventory inv = org.bukkit.Bukkit.createInventory(player, size, title);
    IconManager im = plugin.getIconManager();
    group.getPortals().stream().filter(p -> !p.getName().equals(source.getName())).forEach(portal -> {
      org.bukkit.inventory.ItemStack item;
      String iconName = portal.getIconName();
      if (iconName != null) {
        PortalIcon icon = im.getIcon(iconName);
        if (icon != null) {
          item = SkullUtils.createHead(icon.getBase64(), ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + portal.getName());
        } else {
          item = SkullUtils.createDefaultIcon(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + portal.getName());
        }
      } else {
        item = SkullUtils.createDefaultIcon(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + portal.getName());
      }
      org.bukkit.inventory.meta.ItemMeta meta = item.getItemMeta();
      if (meta != null) {
        if (!meta.hasDisplayName()) meta.setDisplayName(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + portal.getName());
        java.util.List<String> lore = new java.util.ArrayList<>();
        lore.add(" ");
        lore.add(ChatColor.GRAY + MessageUtils.formatCoords(portal.getBaseLocation()));
        lore.add(ChatColor.GRAY + portal.getBaseLocation().getWorld().getName());
        lore.add(" ");
        lore.add(ChatColor.GREEN + "Click to teleport!");
        meta.setLore(lore);
        if (!item.setItemMeta(meta)) {
          plugin.getLogger().warning("Failed to set item meta for portal: " + portal.getName());
        }
      }
      inv.addItem(item);
    });
    player.openInventory(inv);
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
    player.getWorld().spawnParticle(org.bukkit.Particle.PORTAL, player.getLocation(), 40, 0.5, 0.5, 0.5, 0.2);
    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
    org.bukkit.Location targetLoc = target.getCenterLocation();
    org.bukkit.Bukkit.getScheduler().runTaskLater(plugin, () -> {
      player.teleportAsync(targetLoc).thenRun(() -> {
        targetLoc.getWorld().spawnParticle(org.bukkit.Particle.PORTAL, targetLoc, 50, 0.5, 0.5, 0.5, 0.25);
        targetLoc.getWorld().playSound(targetLoc, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
        MessageUtils.teleport(player, target.getName());
        cm.triggerTeleport(player.getUniqueId());
      });
    }, 10L); // 0.5s delay
  }
}
