package org.tnmk.git_analysis.analyze_effort;

import org.eclipse.jgit.revwalk.RevCommit;
import org.tnmk.tech_common.utils.StringUtils;

public class GitPullRequestHelper {
  public static boolean isPullRequest(RevCommit commit) {
    return commit.getParentCount() > 1 &&
      StringUtils.isStartWithOneOfPrefixes(commit.getFullMessage(), "Merge pull request", "Pull request");
  }
}
