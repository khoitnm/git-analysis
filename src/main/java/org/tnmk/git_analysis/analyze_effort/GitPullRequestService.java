package org.tnmk.git_analysis.analyze_effort;

import lombok.RequiredArgsConstructor;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.springframework.stereotype.Service;
import org.tnmk.git_analysis.config.GitRepoApiConnectionProperties;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.tnmk.git_analysis.analyze_effort.GitPullRequestHelper.isPullRequest;

@Service
@RequiredArgsConstructor
public class GitPullRequestService {
  private final GitApiConnectionService gitApiConnectionService;

  public List<RevCommit> getPullRequestsOnBranch(Git git, Repository repository, String branch) throws GitAPIException, IOException {
    List<RevCommit> mergeCommits = new ArrayList<>();

    ObjectId objectId = repository.resolve("refs/remotes/origin/" + branch);
    Iterable<RevCommit> commits = git.log().add(objectId).call();

    for (RevCommit commit : commits) {
      if (isPullRequest(commit)) {
        mergeCommits.add(commit);
      }
    }

    return mergeCommits;
  }

  public String getPullRequestsFromBitBucket(Repository repository) throws IOException {
    String remoteUrl = repository.getConfig().getString("remote", "origin", "url");
    String[] urlParts = remoteUrl.split("/");
    String repoSlug = urlParts[urlParts.length - 1];
    if (repoSlug.endsWith(".git")) {
      repoSlug = repoSlug.substring(0, repoSlug.length() - 4);
    }
    String projectKey = urlParts[urlParts.length - 2];

    GitRepoApiConnectionProperties gitRepoApiConnectionProperties = gitApiConnectionService.findApiConnection(repository)
      .orElseThrow(() -> new IllegalArgumentException("Cannot find the Git API connection for the repository: " + repository.getDirectory().getAbsolutePath()));
    return getPullRequestsFromBitBucket(gitRepoApiConnectionProperties, projectKey, repoSlug, gitRepoApiConnectionProperties.getAccessToken());
  }

  private String getPullRequestsFromBitBucket(GitRepoApiConnectionProperties gitRepoApiConnectionProperties, String projectKey, String repoSlug, String accessToken) throws IOException {
    String url = "https://" + gitRepoApiConnectionProperties.getUrlPart() + "/rest/api/2.0/projects/" + projectKey + "/repos/" + repoSlug + "/pull-requests?at=refs/heads/master&state=OPEN";

    URL obj = new URL(url);
    HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

    // Add request header
//    String userCredentials = username + ":" + password;
//    String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userCredentials.getBytes()));
//    connection.setRequestProperty("Authorization", basicAuth);
    String bearerAuth = "Bearer " + accessToken;
    connection.setRequestProperty("Authorization", bearerAuth);

    int responseCode = connection.getResponseCode();

    try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
      String inputLine;
      StringBuilder response = new StringBuilder();

      while ((inputLine = in.readLine()) != null) {
        response.append(inputLine);
      }

      // Return the response body
      return response.toString();
    }
  }
}
