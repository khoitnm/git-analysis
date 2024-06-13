package org.tnmk.git_analysis.analyze_effort;

import lombok.RequiredArgsConstructor;
import org.eclipse.jgit.lib.Repository;
import org.springframework.stereotype.Service;
import org.tnmk.git_analysis.config.GitRepoApiConnectionProperties;
import org.tnmk.git_analysis.config.GitRepoApiProperties;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GitApiConnectionService {

  private final GitRepoApiProperties gitRepoApiProperties;

  public Optional<GitRepoApiConnectionProperties> findApiConnection(Repository repository) {
    String remoteUrl = repository.getConfig().getString("remote", "origin", "url");

    for (GitRepoApiConnectionProperties connection : gitRepoApiProperties.getConnections()) {
      if (remoteUrl.contains(connection.getUrlPart())) {
        return Optional.of(connection);
      }
    }
    return Optional.empty();
  }
}
