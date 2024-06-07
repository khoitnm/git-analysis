package org.tnmk.git_analysis.analyze_effort;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.tnmk.git_analysis.analyze_effort.model.CommitChanges;

import java.io.IOException;

public class GitHelper {
  public static CommitChanges changedInCommit(Repository repository, RevCommit commit) throws IOException {
    try (DiffFormatter diffFormatter = new DiffFormatter(null)) {
      diffFormatter.setRepository(repository);
      diffFormatter.setDiffComparator(RawTextComparator.DEFAULT);
      diffFormatter.setDetectRenames(true);

      RevCommit parentCommit = commit.getParent(0);
      if (parentCommit == null) {
        return CommitChanges.builder().lines(0).files(0).build(); // Skip the initial commit
      }

      int changedFilesCount = 0;
      int linesChanged = 0;

      for (DiffEntry diffEntry : diffFormatter.scan(parentCommit, commit)) {
        changedFilesCount++;

        linesChanged += diffFormatter.toFileHeader(diffEntry).toEditList().stream()
          .mapToInt(edit -> edit.getEndB() - edit.getBeginB())
          .sum();
      }

      return CommitChanges.builder()
        .lines(linesChanged)
        .files(changedFilesCount)
        .build();
    }
  }
}
