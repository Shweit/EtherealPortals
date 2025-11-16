package com.shweit.etherealportals;

import com.shweit.etherealportals.command.PortalCommand;
import com.shweit.etherealportals.listener.InventoryClickListener;
import com.shweit.etherealportals.listener.InventoryCloseListener;
import com.shweit.etherealportals.listener.PlayerMoveListener;
import com.shweit.etherealportals.manager.CooldownManager;
import com.shweit.etherealportals.manager.DataManager;
import com.shweit.etherealportals.manager.IconManager;
import com.shweit.etherealportals.manager.PortalManager;
import com.shweit.etherealportals.util.MessageUtils;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * EtherealPortals - A mystical portal plugin for Minecraft.
 * Allows players to create custom portal groups for fast travel.
 */
public class EtherealPortals extends JavaPlugin {
  private PortalManager portalManager;
  private IconManager iconManager;
  private DataManager dataManager;
  private CooldownManager cooldownManager;
  private com.shweit.etherealportals.visual.VisualEffectTask visualTask;
  private double hitboxWidth;
  private double hitboxDepth;
  private double hitboxHeight;

  /**
   * Gets the portal manager instance.
   *
   * @return the portal manager
   */
  public PortalManager getPortalManager() {
    return portalManager;
  }

  /**
   * Gets the icon manager instance.
   *
   * @return the icon manager
   */
  public IconManager getIconManager() {
    return iconManager;
  }

  /**
   * Gets the cooldown manager instance.
   *
   * @return the cooldown manager
   */
  public CooldownManager getCooldownManager() {
    return cooldownManager;
  }

  /**
   * Gets the data manager instance.
   *
   * @return the data manager
   */
  public DataManager getDataManager() {
    return dataManager;
  }

  /**
   * Gets the visual effect task instance.
   *
   * @return the visual effect task
   */
  public com.shweit.etherealportals.visual.VisualEffectTask getVisualTask() {
    return visualTask;
  }

  /**
   * Gets the portal hitbox width.
   *
   * @return the hitbox width
   */
  public double getHitboxWidth() {
    return hitboxWidth;
  }

  /**
   * Gets the portal hitbox depth.
   *
   * @return the hitbox depth
   */
  public double getHitboxDepth() {
    return hitboxDepth;
  }

  /**
   * Gets the portal hitbox height.
   *
   * @return the hitbox height
   */
  public double getHitboxHeight() {
    return hitboxHeight;
  }

  @Override
  public void onEnable() {
    saveDefaultConfig();
    reloadLocalConfig();
    portalManager = new PortalManager();
    iconManager = new IconManager();
    cooldownManager = new CooldownManager(
        getConfig().getInt("portal.teleport.cooldownSeconds", 3),
        getConfig().getInt("portal.teleport.messageCooldownSeconds", 1));
    dataManager = new DataManager(this, portalManager, iconManager);
    registerCommands();
    registerListeners();
    visualTask = new com.shweit.etherealportals.visual.VisualEffectTask(this);
    visualTask.start();
    MessageUtils.send(getServer().getConsoleSender(), "Plugin enabled.");
  }

  @Override
  public void onDisable() {
    dataManager.saveGroups();
    dataManager.saveIcons();
    if (visualTask != null) {
      visualTask.stop();
    }
  }

  /**
   * Reloads the plugin configuration.
   */
  public void reloadLocalConfig() {
    reloadConfig();
    hitboxWidth = getConfig().getDouble("portal.hitbox.width", 2.0);
    hitboxDepth = getConfig().getDouble("portal.hitbox.depth", 2.0);
    hitboxHeight = getConfig().getDouble("portal.hitbox.height", 2.0);
    if (cooldownManager != null) {
      cooldownManager.updateConfig(
          getConfig().getInt("portal.teleport.cooldownSeconds", 3),
          getConfig().getInt("portal.teleport.messageCooldownSeconds", 1));
    }
  }

  private void registerCommands() {
    PluginCommand portalCmd = getCommand("portal");
    if (portalCmd != null) {
      PortalCommand executor = new PortalCommand(this);
      portalCmd.setExecutor(executor);
      portalCmd.setTabCompleter(executor);
    }
  }

  private void registerListeners() {
    PluginManager pm = getServer().getPluginManager();
    pm.registerEvents(new PlayerMoveListener(this), this);
    pm.registerEvents(new InventoryClickListener(this), this);
    pm.registerEvents(new InventoryCloseListener(), this);
  }
}
