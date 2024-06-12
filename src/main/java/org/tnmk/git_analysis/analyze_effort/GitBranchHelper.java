package org.tnmk.git_analysis.analyze_effort;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.tnmk.git_analysis.analyze_effort.GitCommitHelper.getCommitDateTime;

public class GitBranchHelper {

  private static final Map<RevCommit, List<String>> cache = new HashMap<>();

  public static String getTargetBranchOfMerge(Repository repository, RevCommit mergeCommit) throws IOException, GitAPIException {
    try (Git git = new Git(repository)) {
      List<Ref> refs = git.branchList().call();
      try (RevWalk revWalk = new RevWalk(repository)) {
        RevCommit firstParent = revWalk.parseCommit(mergeCommit.getParent(0).getId());
        for (Ref ref : refs) {
          revWalk.reset();
          RevCommit branchTipCommit = revWalk.parseCommit(ref.getObjectId());
          if (branchTipCommit.equals(firstParent)) {
            return ref.getName();
          }
        }
      }
    }
    return null;
  }

  public static List<RevCommit> getMergedCommits(LocalDateTime startTimeToAnalyze, Git git) throws IOException {
    Repository repository = git.getRepository();
    Ref mainBranch = repository.exactRef("refs/remotes/origin/master");
    if (mainBranch == null) {
      mainBranch = repository.exactRef("refs/remotes/origin/main");
    }

    List<RevCommit> mergedCommits = new ArrayList<>();
    try (RevWalk revWalk = new RevWalk(repository)) {
      revWalk.markStart(revWalk.parseCommit(mainBranch.getObjectId()));

      for (RevCommit commit : revWalk) {
        if (getCommitDateTime(commit).isBefore(startTimeToAnalyze)) {
          break;
        }
        if (commit.getParentCount() > 1) { // Check if it's a merge commit
          mergedCommits.add(commit);
        }
      }
    }

    return mergedCommits;
  }

  public static List<RevCommit> getMainBranchCommits(Git git)
    throws GitAPIException, IOException {
    List<Ref> branches = git.branchList().call();
    Ref mainBranch = branches.stream()
      .filter(branch -> branch.getName().endsWith("main"))
      .findFirst()
      .orElseThrow(() -> new RuntimeException("Main branch not found"));

    LogCommand logCommand = git.log();
    Iterable<RevCommit> commits = logCommand.add(mainBranch.getObjectId()).call();

    List<RevCommit> mainBranchCommits = new ArrayList<>();
    try (RevWalk revWalk = new RevWalk(git.getRepository())) {
      for (RevCommit commit : commits) {
        revWalk.markStart(commit);
        mainBranchCommits.add(commit);
      }
      revWalk.dispose();
    }

    return mainBranchCommits;
  }
}
