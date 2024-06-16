package org.tnmk.git_analysis.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "git-analysis.path-ratings")
public class GitPathRatingsProperties {
  private List<GitPathRatingProperties> ratings;
}
