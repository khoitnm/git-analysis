package org.tnmk.git_analysis.analyze_effort.model;

import jakarta.annotation.Nullable;
import lombok.Builder;
import lombok.Getter;
import org.tnmk.git_analysis.analyze_effort.GitCommitTicketHelper;

import java.util.List;

@Builder
@Getter
public class CommitTask {
  @Nullable
  private final String ticketId;
  private final List<CommitResult> commits;

  public GitRepo getGitRepo() {
    return commits.get(0).getGitRepo();
  }

  public String getTicketUrl() {
    return GitCommitTicketHelper.getTicketUrl(getGitRepo(), ticketId);
  }

  public long commitsSize() {
    return commits.size();
  }

  public long getWords() {
    return commits.stream().mapToLong(CommitResult::getWordsCount).sum();
  }

  public long getLines() {
    return commits.stream().mapToLong(CommitResult::getLinesCount).sum();
  }

  public long getFiles() {
    return commits.stream().mapToLong(CommitResult::getFilesCount).sum();
  }

  public boolean isUnknownTicket() {
    return GitCommitTicketHelper.isUnknownTicket(ticketId);
  }
}
