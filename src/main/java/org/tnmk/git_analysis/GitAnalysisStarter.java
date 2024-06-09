package org.tnmk.git_analysis;

import lombok.RequiredArgsConstructor;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.tnmk.git_analysis.analyze_effort.GitFoldersAnalyzer;
import org.tnmk.git_analysis.config.GitFolderProperties;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class GitAnalysisStarter {
  private static final int ANALYZE_IN_WEEKS = 2;
  private final GitFolderProperties gitFolderProperties;
  private final GitFoldersAnalyzer gitFoldersAnalyzer;

  @EventListener(ApplicationStartedEvent.class)
  public void start() throws GitAPIException, IOException {
    LocalDateTime startTimeToAnalyze = LocalDateTime.now().minusWeeks(ANALYZE_IN_WEEKS);
    gitFoldersAnalyzer.analyzeManyRepos(startTimeToAnalyze, gitFolderProperties.getPaths(), false);
  }
}
