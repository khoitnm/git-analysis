package org.tnmk.git_analysis.analyze_effort;

import com.jcraft.jsch.JSchException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.stereotype.Service;
import org.tnmk.git_analysis.analyze_effort.model.*;
import org.tnmk.git_analysis.analyze_effort.report.GitFoldersHtmlReporter;
import org.tnmk.git_analysis.config.GitAliasProperties;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class GitReposAnalyzer {
  private final GitAliasProperties gitAliasProperties;
  //  private final GitFoldersLogReporter logReporter;
  private final GitFoldersHtmlReporter htmlReporter;
  private final MemberFilter memberFilter;
  private final MemberMergerByAlias mergeMembers;
  private final GitRepoAnalyzer gitRepoAnalyzer;
  private final GitRepoFetcher gitRepoFetcher;

  public void analyzeManyRepos(LocalDateTime startTimeToAnalyze, LocalDateTime endTimeToAnalyze, List<String> repoPaths, boolean fetch)
    throws IOException {
    log.info("StartTimeToAnalyze: " + startTimeToAnalyze);

    List<AliasMemberInRepo> aliasMembersInManyRepos = new ArrayList<>();
    List<List<String>> aliasesOfMembers = gitAliasProperties.parseAliasesOfMembers();
    Set<String> onlyIncludeMembers = memberFilter.getOnlyIncludeMembers(aliasesOfMembers);

    // This cannot be run in parallel.
    List<GitFetchResult> errors = repoPaths.stream()
      .map(gitRepoFetcher::getLatestCode)
      .filter(GitFetchResult::hasError).toList();
    if (!errors.isEmpty()) {
      log.warn("Cannot fetch latest code of some repos: {}",
        errors.stream()
          .filter(result -> result.getError() != null)
          .map(result -> "\n\t" + result.getRepoPath() + ": " + StringUtils.substring(result.getError().getMessage(), 0, 300)).toList()
      );
    }

    // This process can be run in parallel.
    repoPaths.stream().parallel().forEach(repositoryPath -> {
      try {
        Map<String, Member> membersInOneRepo = gitRepoAnalyzer.analyzeOneRepo(startTimeToAnalyze, endTimeToAnalyze, repositoryPath, fetch, onlyIncludeMembers);

        List<AliasMember> members = mergeMembers.mergeMembersWithSameAlias(membersInOneRepo.values());
        List<AliasMemberInRepo> aliasMembersInOneRepo = members.stream()
          .map(member ->
            AliasMemberInRepo.builder()
              .aliasMember(member)
              .repoPath(repositoryPath)
              .build()
          ).toList();
        aliasMembersInManyRepos.addAll(aliasMembersInOneRepo);
      } catch (GitAPIException | IOException | JSchException e) {
        log.warn("Cannot analyze repo: " + repositoryPath, e);
      }
    });

    /** key: member's key in {@link  AliasMemberInManyRepos#getMemberKey()} .*/
    List<AliasMemberInManyRepos> membersInAllRepos = groupMembersFromManyReposByAliases(aliasMembersInManyRepos);

    //    logReporter.report(membersInAllRepos);
    htmlReporter.report(startTimeToAnalyze, endTimeToAnalyze, membersInAllRepos);
  }

  private List<AliasMemberInManyRepos> groupMembersFromManyReposByAliases(List<AliasMemberInRepo> aliasMembersInManyRepos) {
    /** key: member's key in {@link  AliasMemberInRepo#getMemberKey()} .*/
    Map<String, List<AliasMemberInRepo>> membersInAllRepos = new HashMap<>();
    for (AliasMemberInRepo memberInOneRepo : aliasMembersInManyRepos) {
      List<AliasMemberInRepo> reposOfThisMember = membersInAllRepos.getOrDefault(memberInOneRepo.getMemberKey(), new ArrayList<>());
      reposOfThisMember.add(memberInOneRepo);
      membersInAllRepos.put(memberInOneRepo.getMemberKey(), reposOfThisMember);
    }
    List<AliasMemberInManyRepos> result = membersInAllRepos.values().stream().map(reposOfMember -> AliasMemberInManyRepos.builder()
      .memberInRepos(reposOfMember)
      .build()
    ).toList();
    return result;
  }
}
