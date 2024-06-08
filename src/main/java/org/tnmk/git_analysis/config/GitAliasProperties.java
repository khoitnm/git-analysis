package org.tnmk.git_analysis.config;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "git-alias")
public class GitAliasProperties {
  private static final String ALIAS_DELIMITER = "||";
  /**
   * Each item is a list of aliases of one member, separate by {@link #ALIAS_DELIMITER}
   */
  private List<String> aliasesOfMembers;

  public List<List<String>> parseAliasesOfMembers() {
    List<List<String>> result = aliasesOfMembers.stream()
      .map(aliasesOfOneMember ->
        Arrays.stream(StringUtils.split(aliasesOfOneMember, ALIAS_DELIMITER)).map(String::toLowerCase).toList()
      ).toList();
    return result;
  }
}
