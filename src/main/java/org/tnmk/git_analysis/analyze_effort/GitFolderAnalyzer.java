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

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.tnmk.git_analysis.analyze_effort.GitCommitHelper.getCommitDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class GitFolderAnalyzer {
  private final AnalysisIgnore analysisIgnore;

  public Map<String, Member> analyzeOneRepo(LocalDateTime startTimeToAnalyze, String repoPath) throws GitAPIException, IOException {
    try (
      Git git = Git.open(new File(repoPath));
      Repository repository = git.getRepository();
    ) {
      // TODO fetch repo before analysis.

      // key: member name
      Map<String, Member> members = new HashMap<>();

      LogCommand logCommand = git.log();
      for (RevCommit commit : logCommand.call()) {
        LocalDateTime commitDateTime = getCommitDateTime(commit);

        if (commitDateTime.isAfter(startTimeToAnalyze)) {
          String authorName = commit.getAuthorIdent().getName();
          CommitResult commitResult = GitCommitAnalyzeHelper.analyzeCommit(repository, commit, analysisIgnore);

          Member member = members.getOrDefault(authorName, new Member(authorName, repoPath));
          member.addCommit(commitResult);
          members.put(authorName, member);
        }
      }

      return members;
    }
  }

}
