package org.tnmk.git_analysis.analyze_effort;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
}
