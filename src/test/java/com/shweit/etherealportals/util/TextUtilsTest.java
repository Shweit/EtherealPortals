package com.shweit.etherealportals.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Tests for legacy text conversion. */
public class TextUtilsTest {

  @Test
  public void convertsAmpersandColorCodes() {
    assertEquals("Colored text", TextUtils.plain(TextUtils.fromLegacy("&dColored &ltext")));
  }

  @Test
  public void convertsSectionColorCodes() {
    assertEquals("Portal Crystal", TextUtils.plain(TextUtils.itemText("§d§lPortal Crystal")));
  }
}
