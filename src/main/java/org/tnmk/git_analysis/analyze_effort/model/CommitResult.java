package org.tnmk.git_analysis.analyze_effort.model;

import jakarta.annotation.Nullable;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.tnmk.git_analysis.analyze_effort.CommitAuthorPolicy;
import org.tnmk.git_analysis.analyze_effort.GitCommitHelper;

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
  @NonNull
  private final String implementor;
  /**
   * @see #implementor
   */
  @NonNull
  private final String committer;
  /**
   * Aka. 'commitName' / 'commitId'
   */
  @NonNull
  private final String commitRevision;
  @NonNull
  private final LocalDateTime commitDateTime;
  @NonNull
  private final CommitType commitType;
  @NonNull
  private final List<CommittedFile> files;
  @NonNull
  private final GitRepo gitRepo;

  @Nullable
  private final String mergedTargetBranch;

  /**
   * A commit may not link to any ticket:
   * - It could be because the developer forgot put any ticket into the commit message.
   * - Or it could be because the developer simply just change some code without needing any ticket.
   */
  @Nullable
  private final String ticketId;

  private final String commitMessage;

  public int getFilesCount() {
    return files.size();
  }

  public int getLinesCount() {
    return files.stream().mapToInt(CommittedFile::getChangedLines).sum();
  }

  public int getWordsCount() {
    return files.stream().mapToInt(CommittedFile::getChangedWords).sum();
  }

  public int getTestWordsCount() {
    return files.stream().filter(CommittedFile::isTestFile).mapToInt(CommittedFile::getChangedWords).sum();
  }

  public int getTestLinesCount() {
    return files.stream().filter(CommittedFile::isTestFile).mapToInt(CommittedFile::getChangedLines).sum();
  }

  public int getTestFilesCount() {
    return (int) files.stream().filter(CommittedFile::isTestFile).count();
  }

  public String getCommitUrl() {
    return GitCommitHelper.getCommitUrl(gitRepo, commitRevision);
  }

  public String getAuthor() {
    return CommitAuthorPolicy.getCommitAuthor(committer, implementor);
  }
}
