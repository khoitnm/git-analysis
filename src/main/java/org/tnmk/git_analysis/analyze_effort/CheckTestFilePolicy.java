package org.tnmk.git_analysis.analyze_effort;

import org.tnmk.tech_common.utils.FilePathUtils;

public class CheckTestFilePolicy {
  public static boolean isTestFile(String filePath) {
    return isTestInJvmBaseLanguages(filePath) || isTestInFE(filePath) || isTestE2E(filePath)
      || isTestJMeter(filePath)
      || isTestInPython(filePath) || isTestInRuby(filePath) || isTestInPHP(filePath)
      || isTestIniOS(filePath);
  }

  /**
   * This is applicable for Java, Groovy, Kotlin, Android, etc.
   */
  private static boolean isTestInJvmBaseLanguages(String filePath) {
    return filePath.contains("src/test/") || filePath.contains("-test/") || filePath.contains("test-") || filePath.contains("Test.java");
  }

  private static boolean isTestInFE(String filePath) {
    String fileName = FilePathUtils.getLastPathPart(filePath);
    return fileName.contains(".test.") || fileName.contains(".spec.");
  }

  private static boolean isTestE2E(String filePath) {
    return filePath.contains("cypress") || filePath.contains("e2e");
  }

  private static boolean isTestInPython(String filePath) {
    String fileName = FilePathUtils.getLastPathPart(filePath);
    return fileName.startsWith("test_") || fileName.endsWith("_test.py");
  }


  private static boolean isTestInRuby(String filePath) {
    String fileName = FilePathUtils.getLastPathPart(filePath);
    return fileName.endsWith("_test.rb") || fileName.endsWith("_spec.rb");
  }

  private static boolean isTestInPHP(String filePath) {
    String fileName = FilePathUtils.getLastPathPart(filePath);
    return fileName.endsWith("Test.php");
  }

  private static boolean isTestJMeter(String filePath) {
    return filePath.endsWith("jmx");
  }

  private static boolean isTestIniOS(String filePath) {
    return filePath.contains("src/test/swift/") || filePath.endsWith("Test.swift") || filePath.endsWith("Spec.swift");
  }
//  private static boolean isTestInGroovy(String filePath) {
//    return
//      filePath.contains("src/test/groovy/") ||
//      filePath.endsWith("Test.groovy") || filePath.endsWith("Spec.groovy");
//  }
//
//  private static boolean isTestInKotlin(String filePath) {
//    return
//      filePath.contains("src/test/kotlin/") ||
//      filePath.endsWith("Test.kt") || filePath.endsWith("Spec.kt");
//  }
}
