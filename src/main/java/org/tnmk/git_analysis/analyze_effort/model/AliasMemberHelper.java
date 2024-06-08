package org.tnmk.git_analysis.analyze_effort.model;

import java.util.List;

public class AliasMemberHelper {

  /**
   * @return this value is used to identify unique member across many repos
   * Note that authors with the same aliases are considered as single one unique member. It means such authors will have the same memberKey.
   */
  protected static String getMemberKey(List<String> aliases) {
    return aliases.get(0).toString();
  }
}
