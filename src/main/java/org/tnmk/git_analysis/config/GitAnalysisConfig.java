package org.tnmk.git_analysis.config;

import org.tnmk.git_analysis.analyze_effort.model.AliasMember;
import org.tnmk.git_analysis.git_enterprise.GitEnterpriseService; // Placeholder for now

import java.time.LocalDateTime;
import java.util.List;

public class GitAnalysisConfig {
    private LocalDateTime commitsFromDateTime;
    private LocalDateTime commitsToDateTime;
    private List<String> ignoreAuthors;
    private List<AliasMember> aliasMembers;
    private boolean cleanReposWhenAnalyzing;
    private String reportPath;
    private String reportFileGitFolders;

    // Getter and Setter methods to allow tests to compile
    public LocalDateTime getCommitsFromDateTime() { return commitsFromDateTime; }
    public void setCommitsFromDateTime(LocalDateTime commitsFromDateTime) { this.commitsFromDateTime = commitsFromDateTime; }
    public LocalDateTime getCommitsToDateTime() { return commitsToDateTime; }
    public void setCommitsToDateTime(LocalDateTime commitsToDateTime) { this.commitsToDateTime = commitsToDateTime; }
    public List<String> getIgnoreAuthors() { return ignoreAuthors; }
    public void setIgnoreAuthors(List<String> ignoreAuthors) { this.ignoreAuthors = ignoreAuthors; }
    public List<AliasMember> getAliasMembers() { return aliasMembers; }
    public void setAliasMembers(List<AliasMember> aliasMembers) { this.aliasMembers = aliasMembers; }
    public boolean isCleanReposWhenAnalyzing() { return cleanReposWhenAnalyzing; }
    public void setCleanReposWhenAnalyzing(boolean cleanReposWhenAnalyzing) { this.cleanReposWhenAnalyzing = cleanReposWhenAnalyzing; }

    public GitEnterpriseService getGitEnterpriseService(String repoUrl) { return null; /* Placeholder */ }

    public String getReportPath() { return reportPath; }
    public void setReportPath(String reportPath) { this.reportPath = reportPath; }
    public String getReportFileGitFolders() { return reportFileGitFolders; }
    public void setReportFileGitFolders(String reportFileGitFolders) { this.reportFileGitFolders = reportFileGitFolders; }
}
