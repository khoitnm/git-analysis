package org.tnmk.git_analysis.analyze_effort;

import org.tnmk.tech_common.utils.FilePathUtils;

public class CheckTestFilePolicy {
  public static boolean isTestFile(String filePath) {
    return filePath.contains("src/test/") || isTestInFE(filePath);
  }

  private static boolean isTestInFE(String filePath) {
    String fileName = FilePathUtils.getLastPathPart(filePath);
    return fileName.contains(".test.");
  }
}
