package org.tnmk.git_analysis.analyze_effort.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class GitRepo {
  @NonNull
  private final String folderPath;
  @NonNull
  private final String cloneUrl;
  @NonNull
  private final String host;
  @NonNull
  private final String projectName;
  @NonNull
  private final String repoName;
  @NonNull
  private final GitServiceType serviceType;
}
