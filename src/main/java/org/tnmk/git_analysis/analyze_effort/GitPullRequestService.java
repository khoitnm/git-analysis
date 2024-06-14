package org.tnmk.git_analysis.analyze_effort;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.springframework.stereotype.Service;
import org.tnmk.git_analysis.config.GitRepoApiConnectionProperties;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;

import static org.tnmk.git_analysis.analyze_effort.GitCommitHelper.getCommitDateTime;
import static org.tnmk.git_analysis.analyze_effort.GitPullRequestHelper.isPullRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class GitPullRequestService {
  private final GitApiConnectionService gitApiConnectionService;

  public List<RevCommit> getPullRequestsOnDev(Repository repository) throws GitAPIException, IOException {
    List<RevCommit> mergeCommits = new ArrayList<>();

    ObjectId objectId = repository.resolve("refs/remotes/origin/dev");
    RevCommit commit;
    try (RevWalk revWalk = new RevWalk(repository)) {
      commit = revWalk.parseCommit(objectId);

      do {
        if (isPullRequest(commit)) {
          mergeCommits.add(commit);
        }
        RevCommit[] parents = commit.getParents();
        if (parents.length == 0) {
          commit = null;
        } else if (parents.length == 1) {
          commit = parents[0];
        } else {
          Optional<RevCommit> suitableParentCommit = findParentCommitFromDev(revWalk, parents);
          commit = suitableParentCommit.orElse(null);
        }
      } while (commit != null);
    }
    return mergeCommits;
  }

  /**
   * @return Find the parent commit that has the closest PR in the parents.
   * It could be empty in one of these cases:
   * - If there's no parent that has PR.
   * - Or the array of parents is empty.
   */
  private Optional<RevCommit> findParentCommitFromDev(RevWalk revWalk, RevCommit[] parents) throws IOException {
    // key: parent, value: closest PR in the parent (which could be the parent itself)
    Map<RevCommit, RevCommit> parentsMapToClosestPrInParents = new HashMap<>();
    for (RevCommit parent : parents) {
      // We need to parse the parent commit to get the full information.
      // Otherwise, parent.getParents() will return null;
      parent = revWalk.parseCommit(parent);
      Optional<RevCommit> foundClosestPullRequest = findClosestPullRequest(revWalk, parent);
      if (foundClosestPullRequest.isPresent()) {
        RevCommit pullRequestInParent = foundClosestPullRequest.get();
        parentsMapToClosestPrInParents.put(parent, pullRequestInParent);
      }
    }
    if (parentsMapToClosestPrInParents.isEmpty()) {
      log.warn("Cannot find any PR in the parents: {}", Arrays.toString(parents));
      return Optional.empty();
    } else {
      Map.Entry<RevCommit, RevCommit> result = findCommitThatHasClosestPullRequest(parentsMapToClosestPrInParents);
      return Optional.of(result.getKey());
    }
  }

  private Optional<RevCommit> findClosestPullRequest(RevWalk revWalk, RevCommit commit) throws IOException {
    if (isPullRequest(commit)) {
      return Optional.of(commit);
    }
    RevCommit[] parents = commit.getParents();
    if (parents == null) {
      return Optional.empty();
    }
    // key: parent, value: closest PR in the parent (which could be the parent itself)
    Map<RevCommit, RevCommit> parentsMapToClosestPrInParents = new HashMap<>();
    for (RevCommit parent : parents) {
      parent = revWalk.parseCommit(parent);
      Optional<RevCommit> foundClosestPullRequest = findClosestPullRequest(revWalk, parent);
      if (foundClosestPullRequest.isPresent()) {
        RevCommit closestPr = revWalk.parseCommit(foundClosestPullRequest.get());
        parentsMapToClosestPrInParents.put(parent, closestPr);
      }
    }
    if (parentsMapToClosestPrInParents.isEmpty()) {
      return Optional.empty();
    } else {
      Map.Entry<RevCommit, RevCommit> result = findCommitThatHasClosestPullRequest(parentsMapToClosestPrInParents);
      return Optional.of(result.getValue());
    }
  }

  private Map.Entry<RevCommit, RevCommit> findCommitThatHasClosestPullRequest(Map<RevCommit, RevCommit> parentsMapToClosestPrInParents) {
    Map.Entry<RevCommit, RevCommit> closestPr = null;
    for (Map.Entry<RevCommit, RevCommit> revCommitRevCommitEntry : parentsMapToClosestPrInParents.entrySet()) {
      if (closestPr == null) {
        closestPr = revCommitRevCommitEntry;
      } else {
        LocalDateTime closestPrDateTime = getCommitDateTime(closestPr.getValue());
        LocalDateTime commitDateTime = getCommitDateTime(revCommitRevCommitEntry.getValue());
        if (commitDateTime.isAfter(closestPrDateTime)) {
          closestPr = revCommitRevCommitEntry;
        }
      }
    }
    return closestPr;
  }

  /**
   * This doesn't work because we don't really know the URL to trigger the API (or don't really have permission)
   */
  public String getPullRequestsFromBitBucket(Repository repository) throws IOException {
    String remoteUrl = repository.getConfig().getString("remote", "origin", "url");
    String[] urlParts = remoteUrl.split("/");
    String repoSlug = urlParts[urlParts.length - 1];
    if (repoSlug.endsWith(".git")) {
      repoSlug = repoSlug.substring(0, repoSlug.length() - 4);
    }
    String projectKey = urlParts[urlParts.length - 2];

    GitRepoApiConnectionProperties gitRepoApiConnectionProperties = gitApiConnectionService.findApiConnection(repository).orElseThrow(() -> new IllegalArgumentException("Cannot find the Git API connection for the repository: " + repository.getDirectory().getAbsolutePath()));
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
