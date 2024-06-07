package org.tnmk.git_analysis.analyze_effort;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.tnmk.git_analysis.analyze_effort.model.CommitResult;
import org.tnmk.git_analysis.analyze_effort.model.CommittedFile;
import org.tnmk.git_analysis.config.AnalysisIgnore;
import org.tnmk.tech_common.path_matcher.PathMatcherUtils;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class GitHelper {
  public static CommitResult analyzeCommit(Repository repository, RevCommit commit, AnalysisIgnore analysisIgnore) throws IOException {
    try (DiffFormatter diffFormatter = new DiffFormatter(null)) {
      diffFormatter.setRepository(repository);
      diffFormatter.setDiffComparator(RawTextComparator.DEFAULT);
      diffFormatter.setDetectRenames(true);

      RevCommit parentCommit = commit.getParent(0);
      LocalDateTime commitDateTime = getCommitDateTime(commit);
      String commitRevision = commit.getName();
      if (parentCommit == null) {
        // Skip the initial commit
        return CommitResult.builder()
          .commitRevision(commitRevision)
          .commitDateTime(commitDateTime)
          .files(new ArrayList<>())
          .build();
      }

      List<CommittedFile> files = new ArrayList<>();
      for (DiffEntry diffEntry : diffFormatter.scan(parentCommit, commit)) {
        if (PathMatcherUtils.matchAnyPattern(diffEntry.getNewPath(), analysisIgnore.getPathPatterns())) {
          continue;
        }

        int changedLines = diffFormatter.toFileHeader(diffEntry).toEditList().stream()
          .mapToInt(edit -> edit.getEndB() - edit.getBeginB())
          .sum();

        CommittedFile file = CommittedFile.builder()
          .newPath(diffEntry.getNewPath())
          .changedLines(changedLines)

          .commitRevision(commitRevision)
          .commitDateTime(commitDateTime)
          .build();
        files.add(file);
      }

      return CommitResult.builder()
        .commitRevision(commit.getName())
        .commitDateTime(commitDateTime)
        .files(files)
        .build();
    }


  }

  private static LocalDateTime getCommitDateTime(RevCommit commit) {
    return Instant.ofEpochSecond(commit.getCommitTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
  }

}
