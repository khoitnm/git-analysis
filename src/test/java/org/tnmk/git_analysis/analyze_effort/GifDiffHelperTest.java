package org.tnmk.git_analysis.analyze_effort;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
class GifDiffHelperTest {
  @Test
  public void test() throws IOException {
    try (Git git = Git.open(new File("somePath"))) {
      Repository repository = git.getRepository();

      DiffFormatter diffFormatter = GitDiffHelper.createDiffFormatter(repository);
      RevCommit commit = repository.parseCommit(repository.resolve("someCommitRevision"));
      List<DiffEntry> diffEntries = GitDiffHelper.findDiff(diffFormatter, commit);
      log.info("Diffs:\n" +
        diffEntries.stream().map(DiffEntry::toString).collect(Collectors.joining("\n"))
      );
    }
  }
}
