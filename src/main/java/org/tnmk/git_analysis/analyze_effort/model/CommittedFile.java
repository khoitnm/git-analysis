package org.tnmk.git_analysis.analyze_effort.model;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class CommittedFile {
  private final String newPath;
  private final int changedLines;
  private final String commitRevision;
  private final Instant commitDateTime;
}
