package org.tnmk.git_analysis.analyze_effort;

import lombok.RequiredArgsConstructor;
import org.eclipse.jgit.lib.Repository;
import org.springframework.stereotype.Service;
import org.tnmk.git_analysis.analyze_effort.model.GitService;
import org.tnmk.git_analysis.config.GitRepoConfigProperties;
import org.tnmk.git_analysis.config.GitRepoProperties;

@Service
@RequiredArgsConstructor
public class GitRepoService {
  private final GitRepoProperties gitRepoProperties;

  public String getGitSourceCodeUrl(Repository repository) {

    String cloneUrl = GitRepoHelper.getGitCloneUrl(repository);
    String host = GitRepoHelper.getHostFromCloneUrl(cloneUrl);
    String projectName = GitRepoHelper.getProjectName(cloneUrl);
    String repoName = GitRepoHelper.getRepoName(cloneUrl);

    GitRepoConfigProperties gitRepoConfigProperties = gitRepoProperties.getRepositories().stream()
      .filter(repoConfig -> repoConfig.getHost().equals(host))
      .findAny()
      .orElseThrow(() -> new IllegalArgumentException("No configuration match the host " + host));

    if (gitRepoConfigProperties.getServiceType() == GitService.BITBUCKET) {
      return "https://" + host + "/projects/" + projectName + "/repos/" + repoName + "/browse";
    } else if (gitRepoConfigProperties.getServiceType() == GitService.GITHUB) {
      return "https://" + host + "/" + projectName + "/" + repoName;
    } else {
      throw new IllegalArgumentException("Unsupported service type: " + gitRepoConfigProperties.getServiceType());
    }
  }
}
