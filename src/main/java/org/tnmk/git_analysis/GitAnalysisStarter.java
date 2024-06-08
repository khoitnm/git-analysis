package org.tnmk.git_analysis;

import lombok.RequiredArgsConstructor;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.tnmk.git_analysis.analyze_effort.GitFoldersAnalyzer;
import org.tnmk.git_analysis.config.GitFolderProperties;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class GitAnalysisStarter {
  private final GitFolderProperties gitFolderProperties;
  private final GitFoldersAnalyzer gitFoldersAnalyzer;

  @EventListener(ApplicationStartedEvent.class)
  public void start() throws GitAPIException, IOException {
    gitFoldersAnalyzer.analyzeManyRepos(gitFolderProperties.getPaths(), false);
  }
}
