package org.tnmk.git_analysis.analyze_effort;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.tnmk.git_analysis.analyze_effort.model.CommitDiffs;
import org.tnmk.git_analysis.analyze_effort.model.CommitType;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class GitDiffHelper {
  public static DiffFormatter createDiffFormatter(Repository repository) {
    DiffFormatter diffFormatter = new DiffFormatter(null);
    diffFormatter.setRepository(repository);
    diffFormatter.setDiffComparator(RawTextComparator.DEFAULT);
    diffFormatter.setDetectRenames(true);
    return diffFormatter;
  }

  public static CommitDiffs findDiff(DiffFormatter diffFormatter, RevCommit commit) throws IOException {
    RevCommit[] parents = commit.getParents();
    List<DiffEntry> diffEntries;
    // Parent commits are the direct previous commits.
    // If parentCommit is null, it means the commit is the first ever commit of the project.
    if (parents.length == 0) {
      String implementor = commit.getAuthorIdent().getName();
      diffEntries = Collections.emptyList();
      return CommitDiffs.builder()
        .committer(implementor).implementor(implementor).commitType(CommitType.INIT_COMMIT).diffEntries(diffEntries)
        .build();
    } else if (parents.length == 1) {
      String implementor = commit.getAuthorIdent().getName();
      diffEntries = diffFormatter.scan(parents[0], commit);
      return CommitDiffs.builder()
        .committer(implementor).implementor(implementor).commitType(CommitType.REGULAR_COMMIT).diffEntries(diffEntries)
        .build();
    } else if (parents.length == 2) {
      // If there are 2 parents, it means the current commit is the merge commit (which could be a regular merge or pull request).
      // And the conflicts when merging are actually the different between 2 parents.

      // The author of the commit is the person who merged it.
      String committer = commit.getAuthorIdent().getName();

      // The implementor is the person who actual made the changes in the previous commit that changed the code.
      // TODO A PR may be implemented by many people, so to simplify the logic for now,
      //  we just get the latest person who made the commit before creating the PR.
      //  In the future, we may get the person who made the most changes in the PR,
      //  or even report many authors for one PR (which is more accurate, but also more complicated to implement).
      //
      // As you see in the bellow logic, diff data for the PR is calculated based on difference between parents[0] vs. commit.
      // The thing is: that difference was caused by parents[1], hence the author of PR should be parents[1].author
      String implementor = parents[1].getAuthorIdent().getName();
      CommitType commitType;
      if (GitPullRequestHelper.isPullRequest(commit)) {
        diffEntries = diffFormatter.scan(parents[0], commit);
        commitType = CommitType.PULL_REQUEST;
      } else {
        diffEntries = findConflictWhenMerging(diffFormatter, commit, parents[0], parents[1]);
        commitType = CommitType.REGULAR_MERGE;
      }
      return CommitDiffs.builder()
        .committer(committer)
        .implementor(implementor)
        .commitType(commitType)
        .diffEntries(diffEntries)
        .build();
    } else {
      throw new IllegalStateException("Commit %s has more than 2 parent commits, please recheck your logic here for this special case: %s".formatted(commit, parents));
    }
  }

  public static List<DiffEntry> findConflictWhenMerging(DiffFormatter diffFormatter, RevCommit commit, RevCommit parent1, RevCommit parent2) throws IOException {
    // TODO For now, we just return empty because when merging, developers rarely change some code.
    // In some rare cases, they'll need to resolve conflicts, but most of the time, it's just a small effort.
    return Collections.emptyList();
  }
}
