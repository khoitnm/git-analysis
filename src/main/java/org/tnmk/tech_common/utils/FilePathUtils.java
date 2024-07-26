package org.tnmk.tech_common.utils;

public class FilePathUtils {
  /**
   * The last part of the path is usually the file name.
   * But it could also be the folder name.
   */
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
