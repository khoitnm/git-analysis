package org.tnmk.git_analysis.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "git-analysis.only-include")
@Getter
@Setter
public class GitAnalysisOnlyIncludeProperties {
  private List<String> members;
}
