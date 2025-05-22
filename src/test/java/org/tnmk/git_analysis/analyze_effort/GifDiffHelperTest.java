package org.tnmk.git_analysis.analyze_effort;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.jupiter.api.Test;
import org.tnmk.git_analysis.analyze_effort.model.CommitDiffs;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Slf4j
class GifDiffHelperTest {
  @Test
  public void test() throws IOException {
    try (Git git = Git.open(new File("C:\\Projects\\Personal\\git-analysis"))) {
      Repository repository = git.getRepository();

      DiffFormatter diffFormatter = GitDiffHelper.createDiffFormatter(repository);
      RevCommit commit = repository.parseCommit(repository.resolve("b7ac8349cceeda329513b64dc3ee815e74b661ff"));
      CommitDiffs commitDiffs = GitDiffHelper.findDiff(diffFormatter, commit);
      log.info("Diffs:\n" +
        commitDiffs.getDiffEntries().stream().map(DiffEntry::toString).collect(Collectors.joining("\n"))
      );
    }
  }

  @Test
  public void test2() {
    LocalDateTime startDateTime = LocalDateTime.of(2024, 11, 1, 0, 0, 0);
    LocalDateTime endDateTime = LocalDateTime.of(2024, 11, 1, 23, 0, 0).plusDays(1);
    long days = Duration.between(startDateTime, endDateTime).toDays();
    assertThat(days).isEqualTo(1);
  }
}
