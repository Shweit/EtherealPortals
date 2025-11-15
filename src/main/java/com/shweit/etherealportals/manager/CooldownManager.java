package com.shweit.etherealportals.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Tracks teleport and message cooldowns.
 */
public class CooldownManager {
  private final Map<UUID, Long> teleportCooldownEnds = new HashMap<>();
  private final Map<UUID, Long> messageCooldownEnds = new HashMap<>();
  private int teleportSeconds;
  private int messageSeconds;

  public CooldownManager(int teleportSeconds, int messageSeconds) {
    this.teleportSeconds = teleportSeconds;
    this.messageSeconds = messageSeconds;
  }

  public void updateConfig(int teleportSeconds, int messageSeconds) {
    this.teleportSeconds = teleportSeconds;
    this.messageSeconds = messageSeconds;
  }

  public boolean canTeleport(UUID uuid) {
    long now = System.currentTimeMillis();
    Long end = teleportCooldownEnds.get(uuid);
    return end == null || end <= now;
  }

  public int remainingTeleport(UUID uuid) {
    Long end = teleportCooldownEnds.get(uuid);
    if (end == null) return 0;
    long diff = end - System.currentTimeMillis();
    return diff <= 0 ? 0 : (int) Math.ceil(diff / 1000.0);
  }

  public void triggerTeleport(UUID uuid) {
    teleportCooldownEnds.put(uuid, System.currentTimeMillis() + teleportSeconds * 1000L);
  }

  public boolean canMessage(UUID uuid) {
    long now = System.currentTimeMillis();
    Long end = messageCooldownEnds.get(uuid);
    return end == null || end <= now;
  }

  public void triggerMessage(UUID uuid) {
    messageCooldownEnds.put(uuid, System.currentTimeMillis() + messageSeconds * 1000L);
  }
}
