package org.tnmk.git_analysis.analyze_effort.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class CommitResult {
  /**
   * Aka. 'commitName'
   */
  private final String commitRevision;
  private final LocalDateTime commitDateTime;
  private final List<CommittedFile> files;

  public int getFilesCount() {
    return files.size();
  }

  public int getLinesCount() {
    return files.stream().mapToInt(CommittedFile::getChangedLines).sum();
  }
}
