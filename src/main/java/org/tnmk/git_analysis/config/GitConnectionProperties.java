package org.tnmk.git_analysis.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "git-repo-connection")
public class GitConnectionProperties {
    private String url;
    private String username;
    private String password;
}
