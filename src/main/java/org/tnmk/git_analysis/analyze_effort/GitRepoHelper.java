package org.tnmk.git_analysis.analyze_effort;

import org.eclipse.jgit.lib.Config;
import org.eclipse.jgit.lib.Repository;

public class GitRepoHelper {
  public static String getGitCloneUrl(Repository repository) {
    Config storedConfig = repository.getConfig();
    String remoteUrl = storedConfig.getString("remote", "origin", "url");
    return remoteUrl;
  }

  public static String getGitSourceCodeUrl(Repository repository) {
    String cloneUrl = getGitCloneUrl(repository);
    String host = getHostFromCloneUrl(cloneUrl);
    String projectName = getProjectName(cloneUrl);
    String repoName = getRepoName(cloneUrl);
    // TODO The pattern will be different for Github, Gitlab, Bitbucket, etc.
    // This is the pattern for Bitbucket
    return "https://" + host + "/projects/" + projectName + "/repos/" + repoName + "/browse";
  }

  public static String getHostFromCloneUrl(String cloneUrl) {
    if (cloneUrl.startsWith("https://")) {
      int firstSlashAfterHttps = cloneUrl.indexOf('/', 8); // 8 is the length of "https://"
      return cloneUrl.substring(8, firstSlashAfterHttps);
    } else if (cloneUrl.startsWith("git@")) {
      int firstColon = cloneUrl.indexOf(':');
      return cloneUrl.substring(4, firstColon); // 4 is the length of "git@"
    } else if (cloneUrl.startsWith("ssh://")) {
      int lastAt = cloneUrl.lastIndexOf('@');
      return cloneUrl.substring(6, lastAt); // 6 is the length of "ssh://"
    } else {
      throw new IllegalArgumentException("Unsupported URL format: " + cloneUrl);
    }
  }

  public static String getProjectName(String cloneUrl) {
    String[] urlParts = cloneUrl.split("/");
    // The project name is usually the second last part of the URL
    return urlParts[urlParts.length - 2];
  }


  public static String getProjectName(Repository repository) {
    String remoteUrl = getGitCloneUrl(repository);
    return getProjectName(remoteUrl);
  }

  public static String getRepoName(String cloneUrl) {
    String[] urlParts = cloneUrl.split("/");
    // The repository name is usually the last part of the URL, we also remove the .git extension
    return urlParts[urlParts.length - 1].replace(".git", "");
  }

  public static String getRepoName(Repository repository) {
    String remoteUrl = getGitCloneUrl(repository);
    return getRepoName(remoteUrl);
  }
}
