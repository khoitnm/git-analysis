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
import org.tnmk.git_analysis.config.GitAnalysisIgnoreProperties;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.tnmk.git_analysis.analyze_effort.GitCommitHelper.getCommitDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class GitFolderAnalyzer {
  private final GitAnalysisIgnoreProperties gitAnalysisIgnoreProperties;

  /**
   * @return Map of members: key: member name, value: member.
   */
  public Map<String, Member> analyzeOneRepo(LocalDateTime startTimeToAnalyze, String repoPath, boolean fetch, Set<String> onlyIncludeMembers) throws GitAPIException, IOException {
    try (
      Git git = Git.open(new File(repoPath));
      Repository repository = git.getRepository();
    ) {
      if (fetch) {
        git.fetch().call();
        log.info("Fetched {}!", repoPath);
      }
      log.info("Analyzing {}...", repoPath);
      // key: member name
      Map<String, Member> members = new HashMap<>();

      LogCommand logCommand = git.log();
      Set<String> ignoredMembers = new HashSet<>();
      for (RevCommit commit : logCommand.call()) {
        LocalDateTime commitDateTime = getCommitDateTime(commit);
//        log.info("Commit: {}, time: {}, author: {}", commit.getName(), commitDateTime, commit.getAuthorIdent().getName());

        if (commitDateTime.isAfter(startTimeToAnalyze)) {
          String authorName = commit.getAuthorIdent().getName();
          if (!onlyIncludeMembers.contains(authorName.toLowerCase().trim())) {
            ignoredMembers.add(authorName);
            continue;
          }
          CommitResult commitResult = GitCommitAnalyzeHelper.analyzeCommit(repository, commit, gitAnalysisIgnoreProperties);

          Member member = members.getOrDefault(authorName, new Member(authorName, repoPath));
          member.addCommit(commitResult);
          members.put(authorName, member);
        }
      }
      log.info("\tIgnored members: " + ignoredMembers);
      return members;
    }
  }

}
