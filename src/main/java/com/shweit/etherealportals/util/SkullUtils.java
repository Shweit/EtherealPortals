package com.shweit.etherealportals.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.UUID;

/** Utility to create a custom textured player head from base64 texture value. */
public final class SkullUtils {
  // Default portal icon texture (purple portal-like design)
  private static final String DEFAULT_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjBiZmMyNTc3ZjZlMjZjNmM2ZjczNjVjMmM0MDc2YmNjZWU2NTMxMjQ5ODkzODJjZTkzYmNhNGZjOWUzOWIifX19";

  private SkullUtils() {}

  /**
   * Creates a default portal icon with the standard texture.
   */
  public static ItemStack createDefaultIcon(String displayName) {
    return createHead(DEFAULT_TEXTURE, displayName);
  }

  public static ItemStack createHead(String base64, String displayName) {
    ItemStack head = new ItemStack(Material.PLAYER_HEAD);
    SkullMeta meta = (SkullMeta) head.getItemMeta();

    if (displayName != null) {
      meta.setDisplayName(displayName);
    }

    if (base64 != null && !base64.isEmpty()) {
      try {
        // Create a player profile with the texture
        PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID(), "");
        PlayerTextures textures = profile.getTextures();

        // Decode base64 to get the texture URL
        String textureUrl = getTextureUrlFromBase64(base64);
        if (textureUrl != null) {
          textures.setSkin(new URL(textureUrl));
          profile.setTextures(textures);
          meta.setOwnerProfile(profile);
        }
      } catch (MalformedURLException e) {
        System.err.println("Invalid texture URL: " + e.getMessage());
      }
    }

    if (!head.setItemMeta(meta)) {
      // This should never happen for valid SkullMeta, but handle return value for SpotBugs
      System.err.println("Failed to set skull meta");
    }
    return head;
  }

  /**
   * Extracts the texture URL from a base64-encoded texture value.
   * The base64 typically contains a JSON with the texture URL.
   */
  private static String getTextureUrlFromBase64(String base64) {
    try {
      // Decode the base64 string
      String decoded = new String(Base64.getDecoder().decode(base64));

      // Extract URL from JSON (simple parsing)
      // Format: {"textures":{"SKIN":{"url":"http://textures.minecraft.net/texture/..."}}}
      int urlStart = decoded.indexOf("\"url\":\"") + 7;
      int urlEnd = decoded.indexOf("\"", urlStart);

      if (urlStart > 6 && urlEnd > urlStart) {
        return decoded.substring(urlStart, urlEnd);
      }
    } catch (Exception e) {
      System.err.println("Failed to parse base64 texture: " + e.getMessage());
    }
    return null;
  }
}
