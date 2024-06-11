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

  public Member(String name, String repoPath) {
    this.name = name;
    this.repoPath = repoPath;
    this.commits = Collections.synchronizedList(new ArrayList<>());
  }

  public List<CommitResult> pullRequests() {
    return this.commits.stream().filter(commit -> commit.getCommitType() == CommitType.PULL_REQUEST).toList();
  }

  public void addCommit(CommitResult commit) {
    this.commits.add(commit);
  }
}
