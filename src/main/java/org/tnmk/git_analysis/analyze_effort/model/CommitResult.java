package org.tnmk.git_analysis.analyze_effort.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * A commit, is simply a commit in Git.
 * And a commit in Git could be a regular commit, or a merge commit, or a pull request.
 * You can distinguish them by {@link CommitResult#commitType}.
 */
@Getter
@Builder
public class CommitResult {

  /**
   * The 'committer' is the individual who performed the commit. This is often the same person as the {@link #implementor}.
   * However, in the case of merge commits or pull requests, the 'committer' is the person who merged the code,
   * while the 'implementor' is the person who originally wrote the code.
   * Determining the 'implementor' is not always straightforward, as the code in a pull request or a branch
   * could have been written by multiple contributors.
   */
  private final String implementor;
  /**
   * @see #implementor
   */
  private final String committer;
  /**
   * Aka. 'commitName' / 'commitId'
   */
  private final String commitRevision;
  private final LocalDateTime commitDateTime;
  private final CommitType commitType;
  private final List<CommittedFile> files;
  private final String repoPath;

  public int getFilesCount() {
    return files.size();
  }

  public int getLinesCount() {
    return files.stream().mapToInt(CommittedFile::getChangedLines).sum();
  }

  public int getWordsCount() {
    return files.stream().mapToInt(CommittedFile::getChangedWords).sum();
  }
}
