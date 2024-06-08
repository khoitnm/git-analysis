package org.tnmk.git_analysis.analyze_effort.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
public class Member {
  private final String name;
  private final String repoPath;
  private final List<CommitResult> commits;
  private int pullRequests;

  public Member(String name, String repoPath) {
    this.name = name;
    this.repoPath = repoPath;
    this.commits = Collections.synchronizedList(new ArrayList<>());
  }

  public int totalFiles() {
    int total = commits.stream().mapToInt(CommitResult::getFilesCount).sum();
    return total;
  }

  public int totalLines() {
    int total = commits.stream().mapToInt(CommitResult::getLinesCount).sum();
    return total;
  }

  public int totalWords() {
    int total = commits.stream().mapToInt(CommitResult::getWordsCount).sum();
    return total;
  }

  public double avgFilesPerCommit() {
    return totalFiles() / (double) commits.size();
  }

  public double avgLinesPerCommit() {
    return totalLines() / (double) commits.size();
  }

  public double avgWordsPerCommit() {
    return totalWords() / (double) commits.size();
  }

  public void addCommit(CommitResult commit) {
    this.commits.add(commit);
  }
}
