package org.tnmk.git_analysis.analyze_effort;

import lombok.RequiredArgsConstructor;
import org.eclipse.jgit.lib.Repository;
import org.springframework.stereotype.Service;
import org.tnmk.git_analysis.analyze_effort.model.GitRepo;
import org.tnmk.git_analysis.config.GitRepoConfigProperties;
import org.tnmk.git_analysis.config.GitRepoProperties;

@Service
@RequiredArgsConstructor
public class GitRepoService {
  private final GitRepoProperties gitRepoProperties;

  public GitRepo createGitRepo(Repository repository) {
    String cloneUrl = GitRepoHelper.getGitCloneUrl(repository);
    String host = GitRepoHelper.getHostFromCloneUrl(cloneUrl);
    String projectName = GitRepoHelper.getProjectName(cloneUrl);
    String repoName = GitRepoHelper.getRepoName(cloneUrl);

    GitRepoConfigProperties gitRepoConfigProperties = getGitRepoConfigProperties(host);

    return GitRepo.builder()
      .folderPath(repository.getDirectory().getParent())
      .cloneUrl(cloneUrl)
      .host(host)
      .projectName(projectName)
      .repoName(repoName)
      .serviceType(gitRepoConfigProperties.getServiceType())
      .ticketUrlPrefix(gitRepoConfigProperties.getTicketUrlPrefix())
      .build();
  }

  private GitRepoConfigProperties getGitRepoConfigProperties(String host) {
    return gitRepoProperties.getRepositories().stream()
      .filter(repoConfig -> repoConfig.getHost().equals(host))
      .findAny()
      .orElseThrow(() -> new IllegalArgumentException("No GitRepoConfigProperties match the host '" + host + "'"));
  }


}
