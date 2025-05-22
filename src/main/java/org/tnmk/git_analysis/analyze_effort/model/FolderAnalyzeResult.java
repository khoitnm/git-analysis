package org.tnmk.git_analysis.analyze_effort.model;

import java.util.List;

public class FolderAnalyzeResult {
    private String folderPath;
    private int linesAdded;
    private int linesDeleted;
    private int totalFiles;
    private List<CommitDiff> commitDiffs; // Assuming it might hold diffs, adjust if needed

    public FolderAnalyzeResult(String folderPath, int linesAdded, int linesDeleted, int totalFiles, List<CommitDiff> commitDiffs) {
        this.folderPath = folderPath;
        this.linesAdded = linesAdded;
        this.linesDeleted = linesDeleted;
        this.totalFiles = totalFiles;
        this.commitDiffs = commitDiffs;
    }

    // Getters and Setters
    public String getFolderPath() { return folderPath; }
    public void setFolderPath(String folderPath) { this.folderPath = folderPath; }
    public int getLinesAdded() { return linesAdded; }
    public void setLinesAdded(int linesAdded) { this.linesAdded = linesAdded; }
    public int getLinesDeleted() { return linesDeleted; }
    public void setLinesDeleted(int linesDeleted) { this.linesDeleted = linesDeleted; }
    public int getTotalFiles() { return totalFiles; }
    public void setTotalFiles(int totalFiles) { this.totalFiles = totalFiles; }
    public List<CommitDiff> getCommitDiffs() { return commitDiffs; }
    public void setCommitDiffs(List<CommitDiff> commitDiffs) { this.commitDiffs = commitDiffs; }
}
