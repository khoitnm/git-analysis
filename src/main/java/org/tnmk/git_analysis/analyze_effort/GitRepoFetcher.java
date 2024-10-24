package org.tnmk.git_analysis.analyze_effort;

import com.jcraft.jsch.JSchException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.stereotype.Service;
import org.tnmk.git_analysis.analyze_effort.model.GitFetchResult;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class GitRepoFetcher {
  private final GitRepoFetchHelper gitRepoFetchHelper;

  /**
   * @return the result is the error message.
   */
  public GitFetchResult getLatestCode(String repoPath) {
    try {
      gitRepoFetchHelper.fetchLatestContent(repoPath);
      return GitFetchResult.builder().repoPath(repoPath).build();
    } catch (RuntimeException | GitAPIException | JSchException | IOException e) {
      return GitFetchResult.builder().repoPath(repoPath).error(e).build();
    }
  }
}
