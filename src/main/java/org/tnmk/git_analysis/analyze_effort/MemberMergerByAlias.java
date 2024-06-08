package org.tnmk.git_analysis.analyze_effort;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.tnmk.git_analysis.analyze_effort.model.AliasMember;
import org.tnmk.git_analysis.analyze_effort.model.Member;
import org.tnmk.git_analysis.config.GitAliasProperties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberMergerByAlias {
  private final GitAliasProperties gitAliasProperties;

  public List<AliasMember> mergeMembersWithSameAlias(Collection<Member> members) {
    List<AliasMember> mergedMembers = new ArrayList<>();
    List<List<String>> configAliasesOfMembers = gitAliasProperties.parseAliasesOfMembers();
    for (Member member : members) {
      List<String> aliasesForMember = getAliasesForMember(configAliasesOfMembers, member);
      Optional<AliasMember> foundMergedMemberMatchAliases = findMergedMemberMatchAliases(aliasesForMember, mergedMembers);

      if (foundMergedMemberMatchAliases.isEmpty()) {
        AliasMember aliasMember = new AliasMember(aliasesForMember);
        aliasMember.mergeMember(member);

        mergedMembers.add(aliasMember);
      } else {
        AliasMember aliasMember = foundMergedMemberMatchAliases.get();
        aliasMember.mergeMember(member);
      }
    }
    return mergedMembers;
  }

  private Optional<AliasMember> findMergedMemberMatchAliases(List<String> aliases, List<AliasMember> mergedMembers) {
    return mergedMembers.stream().filter(mergedMember -> mergedMember.matchAliases(aliases)).findAny();
  }

  private Optional<List<String>> findConfigAliasForMember(List<List<String>> configAliasesOfMembers, Member member) {
    return configAliasesOfMembers.stream().filter(aliasesForOneMember -> aliasesForOneMember.contains(member.getName().toLowerCase())).findAny();
  }

  private List<String> getAliasesForMember(List<List<String>> configAliasesOfMembers, Member member) {
    Optional<List<String>> foundConfigAliasForMember = findConfigAliasForMember(configAliasesOfMembers, member);
    List<String> aliasForMember = foundConfigAliasForMember.orElse(List.of(member.getName().toLowerCase()));
    return aliasForMember;
  }
}
