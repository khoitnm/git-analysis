package org.tnmk.git_analysis.analyze_effort.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommitChanges {
  private final int files;
  private final int lines;
}
