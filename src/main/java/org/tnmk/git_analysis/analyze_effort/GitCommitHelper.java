package org.tnmk.git_analysis.analyze_effort;

import org.eclipse.jgit.revwalk.RevCommit;
import org.tnmk.git_analysis.analyze_effort.model.GitRepo;
import org.tnmk.git_analysis.analyze_effort.model.GitServiceType;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class GitCommitHelper {
  public static LocalDateTime getCommitDateTime(RevCommit commit) {
    try {
      LocalDateTime commitDateTime = LocalDateTime.ofInstant(commit.getAuthorIdent().getWhen().toInstant(), ZoneId.systemDefault());
      return commitDateTime;
    } catch (RuntimeException ex) {
      throw new IllegalStateException("Cannot get commit date time of commit: " + commit.getName(), ex);
    }
  }

  public static String getCommitUrl(GitRepo gitRepo, String commitRevision) {
    if (gitRepo.getServiceType() == GitServiceType.BITBUCKET) {
      return "https://" + gitRepo.getHost() + "/projects/" + gitRepo.getProjectName() + "/repos/" + gitRepo.getRepoName() + "/commits/" + commitRevision;
    } else if (gitRepo.getServiceType() == GitServiceType.GITHUB) {
      return "https://" + gitRepo.getHost() + "/" + gitRepo.getProjectName() + "/" + gitRepo.getRepoName() + "/commit/" + commitRevision;
    } else {
      throw new IllegalArgumentException("Unsupported service type: " + gitRepo.getServiceType());
    }
  }
}
