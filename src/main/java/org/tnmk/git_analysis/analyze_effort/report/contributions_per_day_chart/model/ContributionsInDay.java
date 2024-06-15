package org.tnmk.git_analysis.analyze_effort.report.contributions_per_day_chart.model;

import lombok.Builder;
import lombok.Getter;
import org.tnmk.git_analysis.analyze_effort.model.CommitResult;

import java.time.LocalDate;
import java.util.List;

@Builder
@Getter
public class ContributionsInDay {
  private final LocalDate localDate;
  private final List<CommitResult> commits;

  public int getTotalWords() {
    return commits.stream().mapToInt(commit -> commit.getWordsCount()).sum();
  }
}
