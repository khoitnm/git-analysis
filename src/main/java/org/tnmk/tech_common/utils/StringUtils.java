package org.tnmk.tech_common.utils;

public class StringUtils {
  public static boolean isStartWithOneOfPrefixes(String text, String... prefixes) {
    for (String prefix : prefixes) {
      if (text.startsWith(prefix)) {
        return true;
      }
    }
    return false;
  }
}
