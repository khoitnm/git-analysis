package org.tnmk.git_analysis.analyze_effort.report.commit_per_day_chart;

import org.tnmk.git_analysis.analyze_effort.model.CommitResult;
import org.tnmk.git_analysis.analyze_effort.report.commit_per_day_chart.model.CommitsInDay;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GitCommitPerDayChartHelper {
  public List<CommitsInDay> organizeCommitsByDay(LocalDateTime startDateTime, LocalDateTime endDateTime, List<CommitResult> commits) {
    long days = Duration.between(startDateTime, endDateTime.plusDays(1)).toDays();
    if (days > Integer.MAX_VALUE) throw new IllegalArgumentException("Days in the report shouldn't be bigger than maximum int: " + days);

    List<CommitsInDay> commitsInDays = new ArrayList<>((int) days);
    LocalDate startDate = startDateTime.toLocalDate();
    for (int i = 0; i < days; i++) {
      LocalDate currentDate = startDate.plusDays(i);
      List<CommitResult> commitsInDay = findCommitsInDay(commits, currentDate);
      commitsInDays.add(CommitsInDay.builder()
        .localDate(currentDate)
        .commits(commitsInDay)
        .build());
    }
    return commitsInDays;
  }

  private List<CommitResult> findCommitsInDay(List<CommitResult> commits, LocalDate currentDate) {
    return commits.stream().filter(commit -> {
      LocalDateTime commitDateTime = commit.getCommitDateTime();
      LocalDate commitDate = commitDateTime.toLocalDate();
      return commitDate.equals(currentDate);
    }).toList();
  }


}
