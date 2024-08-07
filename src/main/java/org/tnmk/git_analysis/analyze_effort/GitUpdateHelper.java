package org.tnmk.git_analysis.analyze_effort;

import com.jcraft.jsch.JSchException;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.tnmk.git_analysis.git_connection.GitSshHelper;

import java.io.IOException;
import java.util.List;

@Slf4j
public class GitUpdateHelper {
  private static final List<String> possibleMainBranches = List.of("main", "master", "dev", "develop", "development", "qa", "sp");

  static void fetchLatestContent(String repoPath, Git git, Repository repository)
    throws GitAPIException, JSchException, IOException {
    git.fetch()
      .setTransportConfigCallback(GitSshHelper.createTransportConfigCallback())
//          .setCredentialsProvider(new NetRCCredentialsProvider())
      // .setRefSpecs(new RefSpec(remoteBranchName + ":" + remoteBranchName))
      .setTimeout(60) // Increase timeout to 60 seconds
      .call();
    log.info("Fetched {}!", repoPath);


    // Merge the first existing branch from the possible branches list into the current branch
    for (String branchName : possibleMainBranches) {
      Ref branchRef = repository.findRef(branchName);
      if (branchRef != null) {
        git.merge()
          .include(branchRef)
          .call();
        log.info("Update '" + branchName + "' branch with the latest changes from " + branchRef.getName());
        break;
      }
    }
  }
}
