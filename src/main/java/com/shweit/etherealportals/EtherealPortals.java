package com.shweit.etherealportals;

import com.shweit.etherealportals.command.EpDebugCommand;
import com.shweit.etherealportals.command.PortalCommand;
import com.shweit.etherealportals.listener.InventoryClickListener;
import com.shweit.etherealportals.listener.InventoryCloseListener;
import com.shweit.etherealportals.listener.PlayerMoveListener;
import com.shweit.etherealportals.listener.PortalItemListener;
import com.shweit.etherealportals.manager.CooldownManager;
import com.shweit.etherealportals.manager.DataManager;
import com.shweit.etherealportals.manager.IconManager;
import com.shweit.etherealportals.manager.PortalManager;
import com.shweit.etherealportals.util.MessageUtils;
import com.shweit.etherealportals.util.PortalItemUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.PluginCommand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
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
  private boolean craftablePortalsEnabled;
  private String defaultPortalTexture;
  private String portalItemName;
  private java.util.List<String> portalItemLore;

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

  /**
   * Checks if craftable portals are enabled.
   *
   * @return true if craftable portals are enabled
   */
  public boolean isCraftablePortalsEnabled() {
    return craftablePortalsEnabled;
  }

  /**
   * Gets the default portal texture (base64).
   *
   * @return the default portal texture
   */
  public String getDefaultPortalTexture() {
    return defaultPortalTexture;
  }

  /**
   * Gets the portal item display name.
   *
   * @return the portal item name
   */
  public String getPortalItemName() {
    return portalItemName;
  }

  /**
   * Gets the portal item lore.
   *
   * @return the portal item lore
   */
  public java.util.List<String> getPortalItemLore() {
    return portalItemLore;
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
    registerCraftingRecipe();
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
    craftablePortalsEnabled = getConfig().getBoolean("portal.craftablePortals.enabled", true);
    defaultPortalTexture = getConfig().getString("portal.defaultTexture",
        "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3Rle"
        + "HR1cmUvYjBiZmMyNTc3ZjZlMjZjNmM2ZjczNjVjMmM0MDc2YmNjZWU2NTMxMjQ5ODkzODJjZTkzYmNhNGZj"
        + "OWUzOWIifX19");
    portalItemName = getConfig().getString("portal.craftablePortals.item.name",
        "§d§lPortal Crystal");
    portalItemLore = getConfig().getStringList("portal.craftablePortals.item.lore");
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
    PluginCommand debugCmd = getCommand("epdebug");
    if (debugCmd != null) {
      EpDebugCommand debugExecutor = new EpDebugCommand(this);
      debugCmd.setExecutor(debugExecutor);
      debugCmd.setTabCompleter(debugExecutor);
    }
  }

  private void registerListeners() {
    PluginManager pm = getServer().getPluginManager();
    pm.registerEvents(new PlayerMoveListener(this), this);
    pm.registerEvents(new InventoryClickListener(this), this);
    pm.registerEvents(new InventoryCloseListener(), this);
    pm.registerEvents(new PortalItemListener(this), this);
  }

  private void registerCraftingRecipe() {
    if (!craftablePortalsEnabled) {
      return;
    }

    // Create the portal item result
    ItemStack result = PortalItemUtils.createPortalItem(
        this, defaultPortalTexture, portalItemName, portalItemLore);

    // Create shaped recipe
    NamespacedKey key = new NamespacedKey(this, "portal_crystal");
    ShapedRecipe recipe = new ShapedRecipe(key, result);

    // Set recipe shape (all 9 slots)
    // [Ender Pearl] [Crying Obsidian] [Ender Pearl]
    // [Amethyst Shard] [Nether Star] [Amethyst Shard]
    // [Ender Pearl] [Crying Obsidian] [Ender Pearl]
    recipe.shape("ECE", "ASA", "ECE");
    recipe.setIngredient('E', Material.ENDER_PEARL);
    recipe.setIngredient('C', Material.CRYING_OBSIDIAN);
    recipe.setIngredient('A', Material.AMETHYST_SHARD);
    recipe.setIngredient('S', Material.NETHER_STAR);

    // Register recipe
    getServer().addRecipe(recipe);
  }
}
