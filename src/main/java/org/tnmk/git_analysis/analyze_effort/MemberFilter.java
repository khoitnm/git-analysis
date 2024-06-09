package org.tnmk.git_analysis.analyze_effort;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.tnmk.git_analysis.config.GitAnalysisOnlyIncludeProperties;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MemberFilter {
  private final GitAnalysisOnlyIncludeProperties onlyIncludeProperties;

  public Set<String> getOnlyIncludeMembers(List<List<String>> aliasesOfMembers) {
    List<String> members = onlyIncludeProperties.getMembers();
    Set<String> membersAliases = new HashSet<>();
    for (String member : members) {
      Optional<List<String>> foundAliasesMatchMember = matchAnyAlias(member, aliasesOfMembers);
      List<String> aliasesMatchMember = foundAliasesMatchMember.orElse(List.of(member));
      membersAliases.addAll(aliasesMatchMember);
    }
    return membersAliases;
  }

  private Optional<List<String>> matchAnyAlias(String member, List<List<String>> aliasesOfMembers) {
    String memberLowerCase = member.toLowerCase().trim();
    for (List<String> aliasesOfMember : aliasesOfMembers) {
      if (aliasesOfMember.contains(memberLowerCase)) {
        return Optional.of(aliasesOfMember);
      }
    }
    return Optional.empty();
  }
}
