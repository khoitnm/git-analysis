package org.tnmk.git_analysis.analyze_effort.model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class AliasMemberInRepo {
  private final String repoPath;
  private final AliasMember aliasMember;

  /**
   * @see AliasMemberHelper#getMemberKey(List)
   */
  public String getMemberKey() {
    return AliasMemberHelper.getMemberKey(getAliases());
  }

  private List<String> getAliases() {
    return aliasMember.getNameAliasesLowerCases();
  }
}
