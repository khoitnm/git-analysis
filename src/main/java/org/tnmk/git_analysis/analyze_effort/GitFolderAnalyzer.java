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
import java.util.stream.Collectors;

import static org.tnmk.git_analysis.analyze_effort.GitCommitHelper.getCommitDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class GitFolderAnalyzer {
  private final GitAnalysisIgnoreProperties gitAnalysisIgnoreProperties;
  private final GitPullRequestService gitPullRequestService;

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

      List<RevCommit> allPullRequests = new ArrayList<>();
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
            allPullRequests.add(commit);
          } else {
            member.addCommit(commitResult);
          }
          members.put(implementor, member);
        }
      }
      filterPullRequests(git, repository, allPullRequests);
//      log.info("Pull Requests on Master: \n" + pullRequestsOnMaster.stream().map(c -> c.getName()).collect(Collectors.joining(", \n")));
      log.info("\tIgnored members: " + ignoredMembers);
      return members;
    }
  }

  private void filterPullRequests(Git git, Repository repository, List<RevCommit> allPullRequests) throws GitAPIException, IOException {

//      List<RevCommit> commitsInMainBranch = getMergedCommits(startTimeToAnalyze, git);
//      log.info("\tCommits in main branch: \n" + commitsInMainBranch.stream().map(c -> c.getName()).collect(Collectors.joining(", \n")));
    List<RevCommit> pullRequestsOnDev = gitPullRequestService.getPullRequestsOnBranch(git, repository, "dev");
    List<String> pullRequestNamesOnDev = pullRequestsOnDev.stream().map(AnyObjectId::getName).toList();
//      log.info("PRs on dev branch: \n" + pullRequestsOnDev.stream().map(c -> c.getName()).collect(Collectors.joining(", \n")));
//      String pullRequests = gitPullRequestService.getPullRequestsFromBitBucket(repository);
//      log.info("PullRequests: \n" + pullRequests);

    List<RevCommit> pullRequestsOnMaster = allPullRequests.stream().filter(
      c -> !pullRequestNamesOnDev.contains(c.getName())
    ).collect(Collectors.toList());
    log.info("Pull Requests on Master: \n" + pullRequestsOnMaster.stream().map(c -> c.getName()).collect(Collectors.joining(", \n")) + "\n\n");
    log.info("Pull Requests on Dev: \n" + pullRequestsOnDev.stream().map(c -> c.getName()).collect(Collectors.joining(", \n")));
  }

}
