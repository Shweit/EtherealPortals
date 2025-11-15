package com.shweit.etherealportals.manager;

import com.shweit.etherealportals.model.PortalIcon;

import java.util.*;

/**
 * Manages custom icons.
 */
public class IconManager {
  private final Map<String, PortalIcon> icons = new LinkedHashMap<>();

  public boolean addIcon(String name, String base64) {
    String key = name.toLowerCase();
    if (icons.containsKey(key)) return false;
    icons.put(key, new PortalIcon(key, base64));
    return true;
  }

  public boolean removeIcon(String name) {
    return icons.remove(name.toLowerCase()) != null;
  }

  public PortalIcon getIcon(String name) {
    return icons.get(name.toLowerCase());
  }

  public Collection<PortalIcon> getIcons() {
    return Collections.unmodifiableCollection(icons.values());
  }
}
