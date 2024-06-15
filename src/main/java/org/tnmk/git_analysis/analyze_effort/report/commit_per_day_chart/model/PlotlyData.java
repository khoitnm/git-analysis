package org.tnmk.git_analysis.analyze_effort.report.commit_per_day_chart.model;

import lombok.Getter;
import lombok.Setter;
import org.tnmk.git_analysis.analyze_effort.report.GitFoldersHtmlReporter;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class PlotlyData {
  private List<LocalDate> x;  // Dates
  private List<String> y;  // Days of the week
  private int[][] z;  // Number of commits

  public String getXAsString() {
    String result = x.stream().map(date -> "'" + date.format(GitFoldersHtmlReporter.chartDateTimeFormatter) + "'").collect(Collectors.joining(",", "[", "]"));
    return result;
  }

  public String getYAsString() {
    String result = y.stream().map(day -> "'" + day + "'").collect(Collectors.joining(",", "[", "]"));
    return result;
  }

  public String getZAsString() {
    StringBuilder result = new StringBuilder("[");
    for (int i = 0; i < z.length; i++) {
      result.append("[");
      for (int j = 0; j < z[i].length; j++) {
        result.append(z[i][j]);
        if (j < z[i].length - 1) {
          result.append(",");
        }
      }
      result.append("]");
      if (i < z.length - 1) {
        result.append(",");
      }
    }
    result.append("]");
    return result.toString();
  }
}
