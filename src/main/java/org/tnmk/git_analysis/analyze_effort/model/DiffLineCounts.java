package org.tnmk.git_analysis.analyze_effort.model;

public class DiffLineCounts {
    private int linesAdded;
    private int linesDeleted;

    public DiffLineCounts(int linesAdded, int linesDeleted) {
        this.linesAdded = linesAdded;
        this.linesDeleted = linesDeleted;
    }

    public int getLinesAdded() { return linesAdded; }
    public void setLinesAdded(int linesAdded) { this.linesAdded = linesAdded; }
    public int getLinesDeleted() { return linesDeleted; }
    public void setLinesDeleted(int linesDeleted) { this.linesDeleted = linesDeleted; }
}
