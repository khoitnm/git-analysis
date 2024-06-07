package org.tnmk.git_analysis.analyze_effort.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberEffort {
  private final String name;
  private int commits;
  private int totalChangedFiles;
  private int pullRequests;

  public MemberEffort(String name) {
    this.name = name;
  }

  public String toString() {
    return """
      %s, commits: %s, avgChangedFiles: %.02f, totalChangedFiles: %s
      """.formatted(name, commits, getAvgChangedFilesPerCommit(), totalChangedFiles);
  }

  public double getAvgChangedFilesPerCommit() {
    return totalChangedFiles / (double) commits;
  }

  public void addChangedFilesCount(int changedFilesCount) {
    totalChangedFiles += changedFilesCount;
  }
}
