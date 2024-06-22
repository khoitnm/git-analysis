package org.tnmk.git_analysis.analyze_effort;

import lombok.RequiredArgsConstructor;
import org.eclipse.jgit.lib.Repository;
import org.springframework.stereotype.Service;
import org.tnmk.git_analysis.config.GitRepoConfigProperties;
import org.tnmk.git_analysis.config.GitRepoProperties;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GitApiConnectionService {

  private final GitRepoProperties gitRepoProperties;

  public Optional<GitRepoConfigProperties> findApiConnection(Repository repository) {
    String remoteUrl = repository.getConfig().getString("remote", "origin", "url");

    for (GitRepoConfigProperties connection : gitRepoProperties.getRepositories()) {
      if (remoteUrl.contains(connection.getHost())) {
        return Optional.of(connection);
      }
    }
    return Optional.empty();
  }
}
