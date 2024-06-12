package org.tnmk.git_analysis.analyze_effort;

import com.jcraft.jsch.JSchException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.springframework.stereotype.Service;
import org.tnmk.git_analysis.analyze_effort.model.CommitResult;
import org.tnmk.git_analysis.analyze_effort.model.CommitType;
import org.tnmk.git_analysis.analyze_effort.model.Member;
import org.tnmk.git_analysis.config.GitAnalysisIgnoreProperties;
import org.tnmk.git_analysis.git_connection.GitSshHelper;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.tnmk.git_analysis.analyze_effort.GitBranchHelper.getMergedCommits;
import static org.tnmk.git_analysis.analyze_effort.GitCommitHelper.getCommitDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class GitFolderAnalyzer {
  private final GitAnalysisIgnoreProperties gitAnalysisIgnoreProperties;

  /**
   * @return Map of members: key: member name, value: member.
   */
  public Map<String, Member> analyzeOneRepo(LocalDateTime startTimeToAnalyze, LocalDateTime endTimeToAnalyze, String repoPath, boolean fetch, Set<String> onlyIncludeMembers) throws GitAPIException, IOException, JSchException {
    try (
      Git git = Git.open(new File(repoPath));
      Repository repository = git.getRepository();
    ) {
      if (fetch) {
        git.fetch()
          .setTransportConfigCallback(GitSshHelper.createTransportConfigCallback())
//          .setCredentialsProvider(new NetRCCredentialsProvider())
          // .setRefSpecs(new RefSpec(remoteBranchName + ":" + remoteBranchName))
          .setTimeout(60) // Increase timeout to 60 seconds
          .call();
        log.info("Fetched {}!", repoPath);
      }
      log.info("Analyzing {}...", repoPath);
      // key: member name
      Map<String, Member> members = new HashMap<>();

      LogCommand logCommand = git.log();
      Set<String> ignoredMembers = new HashSet<>();

      List<RevCommit> commitsInMainBranch = getMergedCommits(startTimeToAnalyze, git);
      log.info("\tCommits in main branch: \n" + commitsInMainBranch.stream().map(c -> c.getName()).collect(Collectors.joining(", \n")));

      for (RevCommit commit : logCommand.call()) {
        LocalDateTime commitDateTime = getCommitDateTime(commit);
//        log.info("Commit: {}, time: {}, author: {}", commit.getName(), commitDateTime, commit.getAuthorIdent().getName());
        if (commitDateTime.isBefore(startTimeToAnalyze)) {
          // All next commits in the loop will just have before time, so we can break the loop.
          break;
        }
        if (commitDateTime.isAfter(endTimeToAnalyze)) {
          // We want it to continue to analyze commits in previous time.
          continue;
        }
        if (commitDateTime.isAfter(startTimeToAnalyze)) {

          Optional<CommitResult> foundCommitResult = GitCommitAnalyzeHelper.analyzeCommit(repository, commit, gitAnalysisIgnoreProperties, onlyIncludeMembers);
          if (foundCommitResult.isEmpty()) {
            ignoredMembers.add(commit.getAuthorIdent().getName());
            continue;
          }
          CommitResult commitResult = foundCommitResult.get();

          // We are counting the effort of each member, so in a PR or a merge commit,
          // the person who spent the effort is the implementor, not the committer.
          String implementor = commitResult.getImplementor();

          Member member = members.getOrDefault(implementor, new Member(implementor, repoPath));
          if (commitResult.getCommitType() == CommitType.PULL_REQUEST) {
            member.addPullRequest(commitResult);
          } else {
            member.addCommit(commitResult);
          }
          members.put(implementor, member);
        }
      }
      log.info("\tIgnored members: " + ignoredMembers);
      return members;
    }
  }

}
