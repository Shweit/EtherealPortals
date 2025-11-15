package com.shweit.etherealportals.model;

/**
 * Represents a custom skull icon definition (base64 texture value).
 */
public class PortalIcon {
  private final String name;
  private final String base64;

  public PortalIcon(String name, String base64) {
    this.name = name.toLowerCase();
    this.base64 = base64;
  }

  public String getName() {
    return name;
  }

  public String getBase64() {
    return base64;
  }
}
