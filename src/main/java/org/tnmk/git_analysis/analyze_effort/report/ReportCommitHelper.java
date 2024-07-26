package org.tnmk.git_analysis.analyze_effort.report;

public class ReportCommitHelper {
  public static String reportTest(int testCount, int totalCount) {
    double percentageTest = totalCount != 0 ? testCount * 100.0 / totalCount : 0.0;
    return "%s (%.1f%%)".formatted(testCount, percentageTest);
  }
}
