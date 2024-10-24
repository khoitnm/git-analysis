package org.tnmk.git_analysis.analyze_effort.model;

import jakarta.annotation.Nullable;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Builder
@Getter
public class GitFetchResult {
  @NonNull
  private final String repoPath;

  /**
   * If there's no error, it will be null.
   */
  @Nullable
  private final Throwable error;

  public boolean hasError() {
    return error != null;
  }
}
