package org.tnmk.git_analysis.config;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "git-alias")
public class GitAliasProperties {
  private static final String ALIAS_DELIMITER = "||";
  /**
   * All authors' names exist in the same list of alias is considered the same member.
   * <p>
   * This field is a list of aliases groups of many members.
   * Each item is a group of aliases of one member, separate by {@link #ALIAS_DELIMITER}.
   */
  private List<String> aliasesOfMembers;

  public List<List<String>> parseAliasesOfMembers() {
    if (aliasesOfMembers == null) return Collections.emptyList();
    List<List<String>> result = aliasesOfMembers.stream()
      .map(this::parseAliasesOfOneMember)
      .toList();
    return result;
  }

  private List<String> parseAliasesOfOneMember(String aliasesAsString) {
    String[] aliasesArray = StringUtils.split(aliasesAsString, ALIAS_DELIMITER);
    return Arrays.stream(aliasesArray)
      .map(String::toLowerCase)
      .toList();
  }
}
