package org.tnmk.git_analysis.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "git-repo-folder")
public class GitFolderProperties {
    private String path;
}
