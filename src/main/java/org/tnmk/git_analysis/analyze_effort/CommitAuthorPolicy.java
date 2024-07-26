package org.tnmk.git_analysis.analyze_effort;

public class CommitAuthorPolicy {
  public static String getCommitAuthor(String committer, String implementor) {
    // This method shows define the constraints clearly what should be the author of a commit.
    // In a commit, normally, the committer is exactly the implementor, they are the same. And hence that's also the author of the commit.
    // However, in a merge commit or a PR, the committer is the person who merged the code, and the implementor is the person who originally wrote the code.
    // In this case, the author of the commit should be the implementor, not the committer.
    return implementor;
  }
}
