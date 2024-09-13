package org.tnmk.git_analysis.config;

import lombok.Getter;
import lombok.Setter;
import org.tnmk.git_analysis.analyze_effort.model.GitServiceType;

@Getter
@Setter
public class GitRepoConfigProperties {
  private String host;
  private GitServiceType serviceType;
  private String accessToken;
  private String ticketUrlPrefix;
}
