package com.shweit.etherealportals.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/** Utility for formatted messages. */
public final class MessageUtils {
  private static final Component PREFIX = Component.text()
      .append(Component.text("[", NamedTextColor.DARK_PURPLE))
      .append(Component.text("Ethereal Portals", NamedTextColor.LIGHT_PURPLE))
      .append(Component.text("] ", NamedTextColor.DARK_PURPLE))
      .build();

  private MessageUtils() {}

  /**
   * Sends a message with the plugin prefix.
   *
   * @param sender the command sender
   * @param msg the message (supports color codes with &)
   */
  public static void send(CommandSender sender, String msg) {
    sender.sendMessage(PREFIX.append(TextUtils.fromLegacy(msg)));
  }

  /**
   * Sends a success message (green).
   *
   * @param sender the command sender
   * @param msg the message (supports color codes with &)
   */
  public static void success(CommandSender sender, String msg) {
    sender.sendMessage(coloredMessage(msg, NamedTextColor.GREEN));
  }

  /**
   * Sends an error message (red).
   *
   * @param sender the command sender
   * @param msg the message (supports color codes with &)
   */
  public static void error(CommandSender sender, String msg) {
    sender.sendMessage(coloredMessage(msg, NamedTextColor.RED));
  }

  /**
   * Sends an info message (gray).
   *
   * @param sender the command sender
   * @param msg the message (supports color codes with &)
   */
  public static void info(CommandSender sender, String msg) {
    sender.sendMessage(coloredMessage(msg, NamedTextColor.GRAY));
  }

  /**
   * Sends a warning message (yellow).
   *
   * @param sender the command sender
   * @param msg the message (supports color codes with &)
   */
  public static void warning(CommandSender sender, String msg) {
    sender.sendMessage(coloredMessage(msg, NamedTextColor.YELLOW));
  }

  /**
   * Sends a teleportation message.
   *
   * @param sender the command sender
   * @param portalName the portal name
   */
  public static void teleport(CommandSender sender, String portalName) {
    sender.sendMessage(PREFIX
        .append(Component.text("Teleporting to ", NamedTextColor.GRAY))
        .append(Component.text(portalName, NamedTextColor.LIGHT_PURPLE))
        .append(Component.text("...", NamedTextColor.GRAY)));
  }

  /**
   * Sends a cooldown message.
   *
   * @param sender the command sender
   * @param seconds remaining cooldown seconds
   */
  public static void cooldown(CommandSender sender, long seconds) {
    sender.sendMessage(PREFIX
        .append(Component.text("Please wait ", NamedTextColor.YELLOW))
        .append(Component.text(seconds + "s", NamedTextColor.GOLD))
        .append(Component.text(" before teleporting again.", NamedTextColor.YELLOW)));
  }

  private static Component coloredMessage(String msg, NamedTextColor color) {
    return PREFIX.append(Component.text().color(color).append(TextUtils.fromLegacy(msg)));
  }

  /**
   * Formats location coordinates.
   *
   * @param loc the location
   * @return formatted coordinate string
   */
  public static String formatCoords(Location loc) {
    return String.format("x=%.1f y=%.1f z=%.1f", loc.getX(), loc.getY(), loc.getZ());
  }

  /**
   * Parses a relative coordinate token.
   *
   * @param player the player for relative coordinates
   * @param token the coordinate token (~ for relative)
   * @param base the base coordinate
   * @return the parsed coordinate value
   */
  public static double parseRelative(Player player, String token, double base) {
    if (token.startsWith("~")) {
      if (token.length() == 1) {
        return base;
      }
      try {
        return base + Double.parseDouble(token.substring(1));
      } catch (NumberFormatException e) {
        return base;
      }
    }
    try {
      return Double.parseDouble(token);
    } catch (NumberFormatException e) {
      return base;
    }
  }
}
