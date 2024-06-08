package org.tnmk.git_analysis.analyze_effort;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.springframework.stereotype.Service;
import org.tnmk.git_analysis.analyze_effort.model.CommitResult;
import org.tnmk.git_analysis.analyze_effort.model.Member;
import org.tnmk.git_analysis.config.AnalysisIgnore;
import org.tnmk.git_analysis.config.GitFolderProperties;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.tnmk.git_analysis.analyze_effort.GitCommitHelper.getCommitDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberEffortAnalyzer {
  private static final int ANALYZE_IN_MONTHS = 1;
  private final GitFolderProperties gitFolderProperties;
  private final MemberEffortReport memberEffortReport;
  private final AnalysisIgnore analysisIgnore;

  public void start() throws GitAPIException, IOException {
    try (
      Git git = Git.open(new File(gitFolderProperties.getPath()));
      Repository repository = git.getRepository();
    ) {
      // key: member name
      Map<String, Member> memberEfforts = new HashMap<>();

      LocalDateTime startTimeToAnalyze = LocalDateTime.now().minusMonths(ANALYZE_IN_MONTHS);

      LogCommand logCommand = git.log();
      for (RevCommit commit : logCommand.call()) {
        LocalDateTime commitDateTime = getCommitDateTime(commit);

        if (commitDateTime.isAfter(startTimeToAnalyze)) {
          String authorName = commit.getAuthorIdent().getName();
          CommitResult commitResult = GitHelper.analyzeCommit(repository, commit, analysisIgnore);

          Member member = memberEfforts.getOrDefault(authorName, new Member(authorName));
          member.addCommit(commitResult);
          memberEfforts.put(authorName, member);
        }
      }

      memberEffortReport.report(memberEfforts.values());
    }
  }

}
