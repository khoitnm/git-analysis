package org.tnmk.git_analysis.analyze_effort.model;

import lombok.Builder;
import lombok.Getter;
import org.tnmk.git_analysis.analyze_effort.CommitAuthorPolicy;

import java.util.Optional;

@Builder
@Getter
public class CommitResultByAuthor {
  private final String committer;
  private final String implementor;
  private final Optional<CommitResult> commitResult;

  public String getAuthor() {
    return CommitAuthorPolicy.getCommitAuthor(committer, implementor);
  }
}
