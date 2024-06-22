package org.tnmk.git_analysis.config;

import lombok.Getter;
import lombok.Setter;
import org.tnmk.git_analysis.analyze_effort.model.GitService;

@Getter
@Setter
public class GitRepoConfigProperties {
  private String host;
  private GitService serviceType;
  private String accessToken;
}
