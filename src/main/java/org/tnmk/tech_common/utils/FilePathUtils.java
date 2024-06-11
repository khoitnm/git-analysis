package org.tnmk.tech_common.utils;

public class FilePathUtils {
  public static String getLastPathPart(String filePath) {
    int lastSlashIndex = filePath.lastIndexOf('/');
    int lastBackSlashIndex = filePath.lastIndexOf('\\');
    int lastSeparatorIndex = Math.max(lastSlashIndex, lastBackSlashIndex);
    if (lastSeparatorIndex < 0) {
      return filePath;
    }
    return filePath.substring(lastSeparatorIndex + 1);
  }
}
