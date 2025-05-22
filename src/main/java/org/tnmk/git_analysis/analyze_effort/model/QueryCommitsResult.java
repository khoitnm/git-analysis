package org.tnmk.git_analysis.analyze_effort.model;

import org.eclipse.jgit.revwalk.RevCommit;
import java.util.List;

public class QueryCommitsResult {
    private List<RevCommit> commits;
    private int totalLinesAdded; // Example field
    private int totalLinesDeleted; // Example field

    public QueryCommitsResult(List<RevCommit> commits, int totalLinesAdded, int totalLinesDeleted) {
        this.commits = commits;
        this.totalLinesAdded = totalLinesAdded;
        this.totalLinesDeleted = totalLinesDeleted;
    }

    // Getters and Setters
    public List<RevCommit> getCommits() { return commits; }
    public void setCommits(List<RevCommit> commits) { this.commits = commits; }
    public int getTotalLinesAdded() { return totalLinesAdded; }
    public void setTotalLinesAdded(int totalLinesAdded) { this.totalLinesAdded = totalLinesAdded; }
    public int getTotalLinesDeleted() { return totalLinesDeleted; }
    public void setTotalLinesDeleted(int totalLinesDeleted) { this.totalLinesDeleted = totalLinesDeleted; }
}
