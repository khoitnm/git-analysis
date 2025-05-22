package org.tnmk.git_analysis.git_enterprise.github;

import org.tnmk.git_analysis.analyze_effort.model.GitPullRequest;
import org.tnmk.git_analysis.git_enterprise.GitEnterpriseService;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class GithubService implements GitEnterpriseService {
    @Override
    public List<GitPullRequest> findPullRequests(String repoName, String personalAccessToken) throws IOException {
        // Placeholder implementation
        return Collections.emptyList();
    }
}
