package com.shweit.etherealportals.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Location;

/** Utility for formatted messages. */
public final class MessageUtils {
  private static final String PREFIX = ChatColor.DARK_PURPLE + "[" + ChatColor.LIGHT_PURPLE + "Ethereal Portals" + ChatColor.DARK_PURPLE + "]" + ChatColor.RESET + " ";

  private MessageUtils() {}

  public static void send(CommandSender sender, String msg) {
    sender.sendMessage(PREFIX + ChatColor.translateAlternateColorCodes('&', msg));
  }

  public static void success(CommandSender sender, String msg) {
    sender.sendMessage(PREFIX + ChatColor.GREEN + ChatColor.translateAlternateColorCodes('&', msg));
  }

  public static void error(CommandSender sender, String msg) {
    sender.sendMessage(PREFIX + ChatColor.RED + ChatColor.translateAlternateColorCodes('&', msg));
  }

  public static void info(CommandSender sender, String msg) {
    sender.sendMessage(PREFIX + ChatColor.GRAY + ChatColor.translateAlternateColorCodes('&', msg));
  }

  public static void warning(CommandSender sender, String msg) {
    sender.sendMessage(PREFIX + ChatColor.YELLOW + ChatColor.translateAlternateColorCodes('&', msg));
  }

  public static void teleport(CommandSender sender, String portalName) {
    sender.sendMessage(PREFIX + ChatColor.GRAY + "Teleporting to " + ChatColor.LIGHT_PURPLE + portalName + ChatColor.GRAY + "...");
  }

  public static void cooldown(CommandSender sender, long seconds) {
    sender.sendMessage(PREFIX + ChatColor.YELLOW + "Please wait " + ChatColor.GOLD + seconds + "s" + ChatColor.YELLOW + " before teleporting again.");
  }

  public static String formatCoords(Location loc) {
    return String.format("x=%.1f y=%.1f z=%.1f", loc.getX(), loc.getY(), loc.getZ());
  }

  public static double parseRelative(Player player, String token, double base) {
    if (token.startsWith("~")) {
      if (token.length() == 1) return base;
      try { return base + Double.parseDouble(token.substring(1)); } catch (NumberFormatException e) { return base; }
    }
    try { return Double.parseDouble(token); } catch (NumberFormatException e) { return base; }
  }
}
