package org.tnmk.git_analysis.analyze_effort;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

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

  public static List<DiffEntry> findDiff(DiffFormatter diffFormatter, RevCommit commit) throws IOException {
    RevCommit[] parents = commit.getParents();
    List<DiffEntry> diffEntries;
    // If parentCommit is null, it means the commit is the first ever commit of the project.
    if (parents.length == 0) {
      diffEntries = Collections.emptyList();
    } else if (parents.length == 1) {
      // parent commit is actually the previous commit.
      diffEntries = diffFormatter.scan(parents[0], commit);
    } else if (parents.length == 2) {
      // If there are 2 parents, it means the current commit is the merge commit.
      // And the conflicts when merging are actually the different between 2 parents.
      diffEntries = findConflictWhenMerging(diffFormatter, commit, parents[0], parents[1]);
    } else {
      throw new IllegalStateException("A commit %s should not have more than 2 parent commits: %s".formatted(commit, parents));
    }
    return diffEntries;
  }

  public static List<DiffEntry> findConflictWhenMerging(DiffFormatter diffFormatter, RevCommit commit, RevCommit parent1, RevCommit parent2) throws IOException {
    // TODO For now, we just return empty because when merging, developers rarely change some code.
    // In some rare cases, they'll need to resolve conflicts, but most of the time, it's just a small effort.
    return Collections.emptyList();
  }
}
