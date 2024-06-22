package org.tnmk.git_analysis.analyze_effort;

import com.jcraft.jsch.JSchException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.AnyObjectId;
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

import static org.tnmk.git_analysis.analyze_effort.GitCommitHelper.getCommitDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class GitFolderAnalyzer {
  private final GitAnalysisIgnoreProperties gitAnalysisIgnoreProperties;
  private final GitPullRequestService gitPullRequestService;
  private final GitRepoService gitRepoService;

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

      String sourceCodeUrl = gitRepoService.getGitSourceCodeUrl(repository);

      // key: member name
      Map<String, Member> members = new HashMap<>();

      LogCommand logCommand = git.log();
      Set<String> ignoredMembers = new HashSet<>();

      Optional<List<RevCommit>> pullRequestsOnDevOptional = gitPullRequestService.getPullRequestsOnDev(startTimeToAnalyze, repository);
      Optional<List<String>> pullRequestNamesOnDev = pullRequestsOnDevOptional.map(prsOnDev -> prsOnDev.stream().map(AnyObjectId::getName).toList());
      for (RevCommit commit : logCommand.call()) {
        LocalDateTime commitDateTime = getCommitDateTime(commit);
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
            // We don't want to count the PR that's used to deploy (let's call them 'deploymentPR):
            // Those PRs has a lot of code from different branches that's contributed from many people.
            // But the merger may not the contributor of those code.
            //
            // So we only count the PRs that are merged to the dev branch which are not the deploymentPRs, they are the real implementationPRs.
            // If there's no dev branch, it means all implementationPRs are on master.
            // So just add them to the member's contribution.
            if (pullRequestNamesOnDev.isEmpty() || pullRequestNamesOnDev.get().contains(commit.getName())) {
              member.addPullRequestOnDev(commitResult);
            }
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
