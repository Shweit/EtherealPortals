package com.shweit.etherealportals.util;

import java.util.regex.Pattern;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

/** Utility methods for converting legacy configuration text to Adventure components. */
public final class TextUtils {
  private static final Pattern AMPERSAND_COLOR_CODE =
      Pattern.compile("(?i)&([0-9A-FK-ORX])");
  private static final LegacyComponentSerializer LEGACY_SECTION =
      LegacyComponentSerializer.legacySection();
  private static final PlainTextComponentSerializer PLAIN_TEXT =
      PlainTextComponentSerializer.plainText();

  private TextUtils() {}

  /**
   * Converts legacy section-sign and ampersand color codes to a component.
   *
   * @param text legacy-formatted text
   * @return the parsed component
   */
  public static Component fromLegacy(String text) {
    if (text == null || text.isEmpty()) {
      return Component.empty();
    }
    String normalized = AMPERSAND_COLOR_CODE.matcher(text).replaceAll("§$1");
    return LEGACY_SECTION.deserialize(normalized);
  }

  /**
   * Converts legacy item text and explicitly disables Minecraft's default italics.
   *
   * @param text legacy-formatted item text
   * @return the parsed item component
   */
  public static Component itemText(String text) {
    return fromLegacy(text).decoration(TextDecoration.ITALIC, false);
  }

  /**
   * Extracts plain text from a component.
   *
   * @param component the component to serialize
   * @return the component's plain text, or an empty string for null
   */
  public static String plain(Component component) {
    return component == null ? "" : PLAIN_TEXT.serialize(component);
  }
}
