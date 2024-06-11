package org.tnmk.git_analysis.analyze_effort;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.tnmk.git_analysis.analyze_effort.model.CommitDiffs;
import org.tnmk.git_analysis.analyze_effort.model.CommitType;
import org.tnmk.tech_common.utils.StringUtils;

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
      String implementor = parents[0].getAuthorIdent().getName();
      CommitType commitType;
      if (checkIsPullRequest(commit)) {
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
      throw new IllegalStateException("A commit %s should not have more than 2 parent commits: %s".formatted(commit, parents));
    }
  }

  private static boolean checkIsPullRequest(RevCommit commit) {
    return StringUtils.isStartWithOneOfPrefixes(commit.getFullMessage(), "Merge pull request", "Pull request");
  }

  public static List<DiffEntry> findConflictWhenMerging(DiffFormatter diffFormatter, RevCommit commit, RevCommit parent1, RevCommit parent2) throws IOException {
    // TODO For now, we just return empty because when merging, developers rarely change some code.
    // In some rare cases, they'll need to resolve conflicts, but most of the time, it's just a small effort.
    return Collections.emptyList();
  }
}
