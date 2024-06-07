package org.tnmk.git_analysis.analyze_effort.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MemberEffort {
    private final String name;
    private int commits;
    private int pullRequests;

    public MemberEffort(String name) {
        this.name = name;
    }
}
