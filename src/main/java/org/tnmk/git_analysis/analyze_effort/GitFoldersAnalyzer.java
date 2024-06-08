package org.tnmk.git_analysis.analyze_effort;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.stereotype.Service;
import org.tnmk.git_analysis.analyze_effort.model.AliasMember;
import org.tnmk.git_analysis.analyze_effort.model.AliasMemberInManyRepos;
import org.tnmk.git_analysis.analyze_effort.model.AliasMemberInRepo;
import org.tnmk.git_analysis.analyze_effort.model.Member;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GitFoldersAnalyzer {
  private static final int ANALYZE_IN_WEEKS = 24;

  private final MemberEffortReport memberEffortReport;
  private final MemberMergerByAlias mergeMembers;
  private final GitFolderAnalyzer gitFolderAnalyzer;

  public void analyzeManyRepos(List<String> repoPaths, boolean fetch) throws GitAPIException, IOException {
    LocalDateTime startTimeToAnalyze = LocalDateTime.now().minusWeeks(ANALYZE_IN_WEEKS);
    log.info("StartTimeToAnalyze: " + startTimeToAnalyze);

    List<AliasMemberInRepo> aliasMembersInManyRepos = new ArrayList<>();

    for (String repositoryPath : repoPaths) {
      Map<String, Member> membersInOneRepo = gitFolderAnalyzer.analyzeOneRepo(startTimeToAnalyze, repositoryPath, fetch);
      List<AliasMember> members = mergeMembers.mergeMembersWithSameAlias(membersInOneRepo.values());
      List<AliasMemberInRepo> aliasMembersInOneRepo = members.stream()
        .map(member ->
          AliasMemberInRepo.builder()
            .aliasMember(member)
            .repoPath(repositoryPath)
            .build()
        ).toList();
      aliasMembersInManyRepos.addAll(aliasMembersInOneRepo);
    }
    /** key: member's key in {@link  AliasMemberInManyRepos#getMemberKey()} .*/
    List<AliasMemberInManyRepos> membersInAllRepos = groupMembersFromManyReposByAliases(aliasMembersInManyRepos);

    memberEffortReport.report(membersInAllRepos);
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
