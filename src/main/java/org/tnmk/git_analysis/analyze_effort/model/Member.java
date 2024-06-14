package org.tnmk.git_analysis.analyze_effort.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class Member {
  private final String name;
  private final String repoPath;
  private final List<CommitResult> commits;
  private final List<CommitResult> pullRequests;

  public Member(String name, String repoPath) {
    this.name = name;
    this.repoPath = repoPath;
    this.commits = Collections.synchronizedList(new ArrayList<>());
    this.pullRequests = Collections.synchronizedList(new ArrayList<>());
  }

  public void addCommit(CommitResult commit) {
    this.commits.add(commit);
  }

  public void addPullRequestOnDev(CommitResult pullRequest) {
    this.pullRequests.add(pullRequest);
  }

}
