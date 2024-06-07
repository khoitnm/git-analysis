package org.tnmk.git_analysis.analyze_effort.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
public class MemberEffort {
  private final String name;
  private final List<CommitResult> commits;
  private int pullRequests;

  public MemberEffort(String name) {
    this.name = name;
    this.commits = Collections.synchronizedList(new ArrayList<>());
  }

  public String toString() {
    return """
      %s, commits: %s, avgFiles: %.02f, avgLines: %.02f, totalFiles: %s, totalLines: %s"""
      .formatted(name, commits.size(), avgFilesPerCommit(), avgLinesPerCommit(), totalFiles(), totalLines());
  }

  public int totalFiles() {
    int total = commits.stream().mapToInt(CommitResult::getFilesCount).sum();
    return total;
  }

  public int totalLines() {
    int total = commits.stream().mapToInt(CommitResult::getLinesCount).sum();
    return total;
  }

  public double avgFilesPerCommit() {
    return totalFiles() / (double) commits.size();
  }

  public double avgLinesPerCommit() {
    return totalLines() / (double) commits.size();
  }

  public void addCommit(CommitResult commit) {
    this.commits.add(commit);
  }
}
