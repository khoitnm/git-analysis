package org.tnmk.git_analysis.analyze_effort.model;

import lombok.Builder;
import lombok.Getter;
import org.eclipse.jgit.diff.DiffEntry;

import java.util.List;

@Getter
@Builder
public class CommitDiffs {
  /**
   * @see CommitResult#getImplementor()
   */
  private final String implementor;
  /**
   * @see CommitResult#getCommitter()
   */
  private final String committer;
  private final CommitType commitType;
  private final List<DiffEntry> diffEntries;

  private String getAuthor() {
    return implementor;
  }
}
