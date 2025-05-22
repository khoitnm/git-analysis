package org.tnmk.git_analysis.config;

public class GitRepoConfig {
    private String repoUrl;
    private String repoName;
    private String gitPersonalAccessToken;

    // Getter and Setter methods
    public String getRepoUrl() { return repoUrl; }
    public void setRepoUrl(String repoUrl) { this.repoUrl = repoUrl; }
    public String getRepoName() { return repoName; }
    public void setRepoName(String repoName) { this.repoName = repoName; }
    public String getGitPersonalAccessToken() { return gitPersonalAccessToken; }
    public void setGitPersonalAccessToken(String gitPersonalAccessToken) { this.gitPersonalAccessToken = gitPersonalAccessToken; }
}
