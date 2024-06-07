package org.tnmk.git_analysis;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.tnmk.git_analysis.analyze_effort.MemberEffortAnalyzer;

@Service
@RequiredArgsConstructor
public class GitAnalysisStarter {

    private final MemberEffortAnalyzer memberEffortAnalyzer;

    @EventListener(ApplicationStartedEvent.class)
    public void start() {
        memberEffortAnalyzer.start();
    }
}
