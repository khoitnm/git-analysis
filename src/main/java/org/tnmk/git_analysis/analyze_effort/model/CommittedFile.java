package org.tnmk.git_analysis.analyze_effort.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.tnmk.git_analysis.analyze_effort.GitCommitHelper;

import java.time.LocalDateTime;

@Getter
@Builder
public class CommittedFile {
  @NonNull
  private final GitRepo gitRepo;
  /**
   * If the file is deleted, the newPath will be the path of the deleted file.
   */
  @NonNull
  private final String newPath;
  @NonNull
  private final int changedLines;
  @NonNull
  private final int changedWords;
  @NonNull
  private final String commitRevision;
  @NonNull
  private final LocalDateTime commitDateTime;

  public String getCommitUrl() {
    return GitCommitHelper.getCommitUrl(gitRepo, commitRevision);
  }
}
