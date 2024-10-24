package org.tnmk.git_analysis.analyze_effort;

import com.jcraft.jsch.JSchException;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
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

    // Update the current branch with the latest changes from Git remote server.
    // We need this check to avoid pulling code from remote server for too many repositories (which will cause connection error).
    if (isCurrentBranchUpToDate(repository)) {
      log.info("\tThe current branch is already up-to-date: {}", repository.getBranch());
    } else {
      git.pull()
        .setTransportConfigCallback(GitSshHelper.createTransportConfigCallback())
        .setTimeout(60)
        .call();
      log.info("Merged latest changes into branch: {}", repository.getBranch());
    }

  }

  static boolean isCurrentBranchUpToDate(Repository repository) throws IOException, GitAPIException, JSchException {
    // Get the current branch
    String currentBranch = repository.getBranch();
    Ref localRef = repository.findRef(currentBranch);
    ObjectId localCommit = localRef.getObjectId();

    // Get the remote branch
    Ref remoteRef = repository.findRef("refs/remotes/origin/" + currentBranch);
    ObjectId remoteCommit = remoteRef.getObjectId();

    // Compare the latest commits
    return localCommit.equals(remoteCommit);
  }
}
