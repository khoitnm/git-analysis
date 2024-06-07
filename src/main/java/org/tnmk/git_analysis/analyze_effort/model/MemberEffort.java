package org.tnmk.git_analysis.analyze_effort.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberEffort {
  private final String name;
  private int commits;
  private int totalChangedFiles;
  private int totalChangedLines;
  private int pullRequests;

  public MemberEffort(String name) {
    this.name = name;
  }

  public String toString() {
    return """
      %s, commits: %s, avgFiles: %.02f, avgLines: %.02f, totalChangedFiles: %s""".formatted(name, commits, avgChangedFiles(), avgChangedFiles(), totalChangedFiles);
  }

  public double avgChangedFiles() {
    return totalChangedFiles / (double) commits;
  }

  public double avgChangedLines() {
    return totalChangedLines / (double) commits;
  }

  public void addChangedFilesCount(int changedFilesCount) {
    totalChangedFiles += changedFilesCount;
  }

  public void addChangedLinesCount(int lines) {
    totalChangedLines += lines;
  }
}
