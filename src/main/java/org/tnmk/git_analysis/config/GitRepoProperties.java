package org.tnmk.git_analysis.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "git-repo")
public class GitRepoProperties {
  private List<GitRepoConfigProperties> repositories;
}
