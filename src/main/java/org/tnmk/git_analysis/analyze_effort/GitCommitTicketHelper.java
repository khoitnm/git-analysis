package org.tnmk.git_analysis.analyze_effort;

import org.apache.commons.lang3.StringUtils;
import org.tnmk.git_analysis.analyze_effort.model.AliasMemberInManyRepos;
import org.tnmk.git_analysis.analyze_effort.model.CommitResult;
import org.tnmk.git_analysis.analyze_effort.model.CommitTask;
import org.tnmk.git_analysis.analyze_effort.model.GitRepo;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class GitCommitTicketHelper {
  private static final Pattern JIRA_TICKET_PATTERN = Pattern.compile("\\b[A-Z]{1,}-\\d+\\b");

  public static Optional<String> extractTicketId(String fullMessage) {
    String[] lines = fullMessage.split("\n");
    if (lines.length > 0) {
      String firstLine = lines[0];
      String[] words = firstLine.split("\\s+");
      if (words.length > 0) {
        String firstWord = words[0];
        Matcher matcher = JIRA_TICKET_PATTERN.matcher(firstWord);
        if (matcher.find()) {
          return Optional.of(matcher.group());
        }
      }
    }
    return Optional.empty();
  }

  public static String getTicketUrl(GitRepo repo, String ticketId) {
    String ticketUrlPrefix = repo.getTicketUrlPrefix();
    if (isUnknownTicket(ticketId)) {
      return "#";
    }
    if (StringUtils.isBlank(ticketUrlPrefix)) {
      return "#" + ticketId;
    } else {
      return ticketUrlPrefix + "/" + ticketId;
    }
  }

  public static boolean isUnknownTicket(String ticketId) {
    return StringUtils.isBlank(ticketId);
  }

  public static List<CommitTask> toCommitTasks(AliasMemberInManyRepos member) {
    // key: ticketId;
    // value: the list of commits in that ticket.
    Map<String, List<CommitResult>> commitResults = member.commits().stream()
      // DON'T COUNT MERGE COMMIT
      .filter(commit -> commit.isNotMergeCommit())
      .collect(Collectors.groupingBy(
        // key of a map cannot be null.
        commit -> commit.getTicketId() == null ? "" : commit.getTicketId())
      );
    return commitResults.entrySet().stream()
      .map(entry -> CommitTask.builder()
        .ticketId(entry.getKey())
        .commits(entry.getValue())
        .build())
      //.sorted(compareTaskByTicketId())
      .sorted(compareTasksByWords().reversed())
      .toList();
  }

  private static Comparator<CommitTask> compareTasksByWords() {
    return (task1, task2) -> Math.toIntExact(task1.getWords() - task2.getWords());
  }

  /**
   * @return Sort tasks by ticketId, so the unknown ticket will be at the end.
   */
  private static Comparator<CommitTask> compareTasksByTicketId() {

    return (task1, task2) -> {
      if (StringUtils.isBlank(task1.getTicketId())) {
        return 1;
      }
      if (StringUtils.isBlank(task2.getTicketId())) {
        return -1;
      }
      return task1.getTicketId().compareTo(task2.getTicketId());
    };
  }
}
