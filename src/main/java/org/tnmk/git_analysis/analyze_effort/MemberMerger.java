package org.tnmk.git_analysis.analyze_effort;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.tnmk.git_analysis.analyze_effort.model.Member;
import org.tnmk.git_analysis.analyze_effort.model.MergedMember;
import org.tnmk.git_analysis.config.GitAliasProperties;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberMerger {
  private final GitAliasProperties gitAliasProperties;

  public void mergeMembers(List<MergedMember> source, Collection<Member> newMembers) {
    List<List<String>> configAliasesOfMembers = gitAliasProperties.parseAliasesOfMembers();
    for (Member newMember : newMembers) {
      Optional<MergedMember> mergedMemberFinding = findMergedMemberMatchNewMember(configAliasesOfMembers, source, newMember);

      MergedMember mergedMember;
      if (mergedMemberFinding.isEmpty()) {
        mergedMember = new MergedMember();
        source.add(mergedMember);
      } else {
        mergedMember = mergedMemberFinding.get();
      }
      mergedMember.mergeMember(newMember);
    }

  }

  private Optional<MergedMember> findMergedMemberMatchNewMember(List<List<String>> configAliasesOfMembers, List<MergedMember> source, Member member) {
    return source.stream().filter(mergedMember -> matchMember(configAliasesOfMembers, mergedMember, member)).findAny();
  }

  private boolean matchMember(List<List<String>> configAliasesOfMembers, MergedMember mergedMember, Member member) {
    Optional<List<String>> configAliasesOfMember = configAliasesOfMembers.stream().filter(aliasesForOneMember -> aliasesForOneMember.contains(member.getName().toLowerCase())).findAny();
    List<String> aliasesOfMember = configAliasesOfMember.orElse(List.of(member.getName().toLowerCase()));
    List<String> aliasesOfMergedMember = mergedMember.getNameAliasesLowerCases();
    return CollectionUtils.containsAny(aliasesOfMember, aliasesOfMergedMember);
  }
}
