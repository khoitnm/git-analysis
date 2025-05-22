package org.tnmk.git_analysis.analyze_effort.model;

import org.eclipse.jgit.diff.DiffEntry;

public class CommitDiff {
    private String filePath;
    private DiffEntry.ChangeType changeType;
    private int linesAdded;
    private int linesDeleted;

    public CommitDiff(String filePath, DiffEntry.ChangeType changeType, int linesAdded, int linesDeleted) {
        this.filePath = filePath;
        this.changeType = changeType;
        this.linesAdded = linesAdded;
        this.linesDeleted = linesDeleted;
    }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public DiffEntry.ChangeType getChangeType() { return changeType; }
    public void setChangeType(DiffEntry.ChangeType changeType) { this.changeType = changeType; }
    public int getLinesAdded() { return linesAdded; }
    public void setLinesAdded(int linesAdded) { this.linesAdded = linesAdded; }
    public int getLinesDeleted() { return linesDeleted; }
    public void setLinesDeleted(int linesDeleted) { this.linesDeleted = linesDeleted; }
}
