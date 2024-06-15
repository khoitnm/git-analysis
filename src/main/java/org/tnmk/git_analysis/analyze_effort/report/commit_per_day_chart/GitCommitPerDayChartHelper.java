package org.tnmk.git_analysis.analyze_effort.report.commit_per_day_chart;

import org.tnmk.git_analysis.analyze_effort.model.AliasMemberInManyRepos;
import org.tnmk.git_analysis.analyze_effort.model.CommitResult;
import org.tnmk.git_analysis.analyze_effort.report.GitFoldersHtmlReporter;
import org.tnmk.git_analysis.analyze_effort.report.commit_per_day_chart.model.CommitsInDay;
import org.tnmk.git_analysis.analyze_effort.report.commit_per_day_chart.model.PlotlyData;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GitCommitPerDayChartHelper {

  public static PlotlyData getMemberCommitsEachDayChartData(LocalDateTime startDateTime, LocalDateTime endDateTime, AliasMemberInManyRepos member) {
    List<CommitsInDay> commitsInDays = getCommitsEachDay(startDateTime, endDateTime, member.commits());
    return convertCommitsInDaysToPlotlyData(startDateTime.toLocalDate(), endDateTime.toLocalDate(), commitsInDays);
  }

  public static PlotlyData convertCommitsInDaysToPlotlyData(LocalDate startDate, LocalDate endDate, List<CommitsInDay> commitsInDays) {
    PlotlyData plotlyData = new PlotlyData();

    DayOfWeek startDayOfWeek = startDate.getDayOfWeek();
    String[] y = new String[7];
    Map<DayOfWeek, Integer> daysOfWeekMapToIndex = new HashMap<>();
    for (int i = 0; i < 7; i++) {
      DayOfWeek dayOfWeek = startDayOfWeek.plus(i);
      int dayIndex = y.length - i - 1;
      y[dayIndex] = dayOfWeek.toString();
      daysOfWeekMapToIndex.put(dayOfWeek, dayIndex);
    }

    List<LocalDate> x = new ArrayList<>();
    int days = (int) Duration.between(startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay()).toDays();
    int weeks = (int) Math.ceil(days / 7d);
    for (int i = 0; i < weeks; i++) {
      x.add(startDate.plusDays(i * 7L));
    }

    int[][] z = new int[7][weeks];
    String[][] texts = new String[7][weeks];
    for (CommitsInDay commitsInDay : commitsInDays) {
      LocalDate date = commitsInDay.getLocalDate();
      DayOfWeek dayOfWeek = date.getDayOfWeek();
      int dayOfWeekIndex = daysOfWeekMapToIndex.get(dayOfWeek);
      int numCommits = commitsInDay.getCommits().size();

      int weekIndex = (int) Math.ceil(Duration.between(startDate.atStartOfDay(), date.atStartOfDay().plusDays(1)).toDays() / 7d) - 1;
      z[dayOfWeekIndex][weekIndex] = numCommits;
      texts[dayOfWeekIndex][weekIndex] = date.format(GitFoldersHtmlReporter.chartDateTimeFormatter) + ": " + numCommits + " commits.";
    }

    plotlyData.setX(x);
    plotlyData.setY(List.of(y));
    plotlyData.setZ(z);
    plotlyData.setTexts(texts);
    return plotlyData;
  }

  public static List<CommitsInDay> getCommitsEachDay(LocalDateTime startDateTime, LocalDateTime endDateTime, List<CommitResult> commits) {
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

  private static List<CommitResult> findCommitsInDay(List<CommitResult> commits, LocalDate currentDate) {
    return commits.stream().filter(commit -> {
      LocalDateTime commitDateTime = commit.getCommitDateTime();
      LocalDate commitDate = commitDateTime.toLocalDate();
      return commitDate.equals(currentDate);
    }).toList();
  }


}
