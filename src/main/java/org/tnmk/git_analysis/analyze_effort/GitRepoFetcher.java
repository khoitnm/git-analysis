package org.tnmk.git_analysis.analyze_effort;

import com.jcraft.jsch.JSchException;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.springframework.stereotype.Service;
import org.tnmk.git_analysis.analyze_effort.model.GitFetchResult;

import java.io.File;
import java.io.IOException;

@Slf4j
@Service
public class GitRepoFetcher {
  /**
   * @return the result is the error message.
   */
  public GitFetchResult getLatestCode(String repoPath) {
    try (
      Git git = Git.open(new File(repoPath));
      Repository repository = git.getRepository();
    ) {
      GitUpdateHelper.fetchLatestContent(repoPath, git, repository);
      return GitFetchResult.builder()
        .repoPath(repoPath)
        .build();
    } catch (GitAPIException | JSchException | IOException e) {
      return GitFetchResult.builder()
        .repoPath(repoPath)
        .error(e)
        .build();
    }
  }
}
