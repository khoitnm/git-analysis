package org.tnmk.git_analysis.analyze_effort;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.springframework.stereotype.Service;
import org.tnmk.git_analysis.analyze_effort.model.MemberEffort;
import org.tnmk.git_analysis.config.GitFolderProperties;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberEffortAnalyzer {
  private final GitFolderProperties gitFolderProperties;
  private final MemberEffortReport memberEffortReport;

  public void start() throws GitAPIException, IOException {
    try (
      Git git = Git.open(new File(gitFolderProperties.getPath()));
      Repository repository = git.getRepository();
    ) {
      // key: member name
      Map<String, MemberEffort> memberEfforts = new HashMap<>();

      LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);

      LogCommand logCommand = git.log();
      for (RevCommit commit : logCommand.call()) {
        LocalDateTime commitDateTime = LocalDateTime.ofInstant(commit.getAuthorIdent().getWhen().toInstant(), ZoneId.systemDefault());

        if (commitDateTime.isAfter(oneMonthAgo)) {
          String authorName = commit.getAuthorIdent().getName();
          MemberEffort memberEffort = memberEfforts.getOrDefault(authorName, new MemberEffort(authorName));
          memberEffort.setCommits(memberEffort.getCommits() + 1);
          int changedFilesCount = calculateChangedFilesCount(repository, commit);
          memberEffort.addChangedFilesCount(changedFilesCount);
          memberEfforts.put(authorName, memberEffort);
        }
      }

      memberEffortReport.report(memberEfforts.values());
    }
  }

  private static int calculateChangedFilesCount(Repository repository, RevCommit commit) throws IOException {
    try (DiffFormatter diffFormatter = new DiffFormatter(null)) {
      diffFormatter.setRepository(repository);
      diffFormatter.setDiffComparator(RawTextComparator.DEFAULT);
      diffFormatter.setDetectRenames(true);

      RevCommit parentCommit = commit.getParent(0);
      if (parentCommit == null) {
        return 0; // Skip the initial commit
      }

      // Calculate the number of changed files in the commit
      int changedFilesCount = 0;
      for (DiffEntry diffEntry : diffFormatter.scan(parentCommit, commit)) {
        changedFilesCount++;
      }

      return changedFilesCount;
    }
  }
}
