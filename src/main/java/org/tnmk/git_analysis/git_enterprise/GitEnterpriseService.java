package org.tnmk.git_analysis.git_enterprise;

import org.tnmk.git_analysis.analyze_effort.model.GitPullRequest;
import java.io.IOException;
import java.util.List;

public interface GitEnterpriseService {
    List<GitPullRequest> findPullRequests(String repoName, String personalAccessToken) throws IOException;
}
