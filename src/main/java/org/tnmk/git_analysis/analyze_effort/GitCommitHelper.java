package org.tnmk.git_analysis.analyze_effort;

import org.eclipse.jgit.revwalk.RevCommit;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class GitCommitHelper {
  public static LocalDateTime getCommitDateTime(RevCommit commit) {
    LocalDateTime commitDateTime = LocalDateTime.ofInstant(commit.getAuthorIdent().getWhen().toInstant(), ZoneId.systemDefault());
    return commitDateTime;
  }

}
