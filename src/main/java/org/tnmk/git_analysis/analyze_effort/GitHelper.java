package org.tnmk.git_analysis.analyze_effort;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.tnmk.git_analysis.analyze_effort.model.CommitResult;
import org.tnmk.git_analysis.analyze_effort.model.CommittedFile;
import org.tnmk.git_analysis.config.AnalysisIgnore;
import org.tnmk.tech_common.path_matcher.PathMatcherUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.tnmk.git_analysis.analyze_effort.GitCommitHelper.getCommitDateTime;
import static org.tnmk.git_analysis.analyze_effort.GitDiffHelper.createDiffFormatter;
import static org.tnmk.git_analysis.analyze_effort.GitDiffHelper.findDiff;

public class GitHelper {
  public static CommitResult analyzeCommit(Repository repository, RevCommit commit, AnalysisIgnore analysisIgnore) throws IOException, GitAPIException {
    try (DiffFormatter diffFormatter = createDiffFormatter(repository)) {
      LocalDateTime commitDateTime = getCommitDateTime(commit);
      String commitRevision = commit.getName();

      List<DiffEntry> diffEntries = findDiff(diffFormatter, commit);

      // This is the list of different files in the commit.
      List<CommittedFile> files = new ArrayList<>();
      for (DiffEntry diffEntry : diffEntries) {
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
}
