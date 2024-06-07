package org.tnmk.git_analysis.analyze_effort.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberEffort {
  private final String name;
  private int commits;
  private int totalFiles;
  private int totalLines;
  private int pullRequests;

  public MemberEffort(String name) {
    this.name = name;
  }

  public String toString() {
    return """
      %s, commits: %s, avgFiles: %.02f, avgLines: %.02f, totalFiles: %s, totalLines: %s"""
      .formatted(name, commits, avgFilesPerCommit(), avgLinesPerCommit(), totalFiles, totalLines);
  }

  public double avgFilesPerCommit() {
    return totalFiles / (double) commits;
  }

  public double avgLinesPerCommit() {
    return totalLines / (double) commits;
  }

  public void addChangedFilesCount(int changedFilesCount) {
    totalFiles += changedFilesCount;
  }

  public void addChangedLinesCount(int lines) {
    totalLines += lines;
  }
}
