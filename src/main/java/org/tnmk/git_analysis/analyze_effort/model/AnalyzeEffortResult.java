package org.tnmk.git_analysis.analyze_effort.model;

import java.util.List;

public class AnalyzeEffortResult {
    private String repoName;
    private String repoUrl; // Added this field based on usage in GitFoldersHtmlReporterTest
    private List<AnalyzedCommitGroup> analyzedCommitGroups;
    private List<FolderAnalyzeResult> folderAnalyzeResults;
    private int totalLinesAdded;
    private int totalLinesDeleted;
    private int totalFiles;


    public AnalyzeEffortResult(String repoName, String repoUrl, List<AnalyzedCommitGroup> analyzedCommitGroups, List<FolderAnalyzeResult> folderAnalyzeResults, int totalLinesAdded, int totalLinesDeleted, int totalFiles) {
        this.repoName = repoName;
        this.repoUrl = repoUrl;
        this.analyzedCommitGroups = analyzedCommitGroups;
        this.folderAnalyzeResults = folderAnalyzeResults;
        this.totalLinesAdded = totalLinesAdded;
        this.totalLinesDeleted = totalLinesDeleted;
        this.totalFiles = totalFiles;
    }

    // Getters and Setters
    public String getRepoName() { return repoName; }
    public void setRepoName(String repoName) { this.repoName = repoName; }
    public String getRepoUrl() { return repoUrl; }
    public void setRepoUrl(String repoUrl) { this.repoUrl = repoUrl; }
    public List<AnalyzedCommitGroup> getAnalyzedCommitGroups() { return analyzedCommitGroups; }
    public void setAnalyzedCommitGroups(List<AnalyzedCommitGroup> analyzedCommitGroups) { this.analyzedCommitGroups = analyzedCommitGroups; }
    public List<FolderAnalyzeResult> getFolderAnalyzeResults() { return folderAnalyzeResults; }
    public void setFolderAnalyzeResults(List<FolderAnalyzeResult> folderAnalyzeResults) { this.folderAnalyzeResults = folderAnalyzeResults; }
    public int getTotalLinesAdded() { return totalLinesAdded; }
    public void setTotalLinesAdded(int totalLinesAdded) { this.totalLinesAdded = totalLinesAdded; }
    public int getTotalLinesDeleted() { return totalLinesDeleted; }
    public void setTotalLinesDeleted(int totalLinesDeleted) { this.totalLinesDeleted = totalLinesDeleted; }
    public int getTotalFiles() { return totalFiles; }
    public void setTotalFiles(int totalFiles) { this.totalFiles = totalFiles; }
}
