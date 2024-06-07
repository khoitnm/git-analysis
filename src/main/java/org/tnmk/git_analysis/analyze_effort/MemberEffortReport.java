package org.tnmk.git_analysis.analyze_effort;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.tnmk.git_analysis.analyze_effort.model.MemberEffort;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
public class MemberEffortReport {
  public void report(Collection<MemberEffort> memberEfforts) {
    Comparator<MemberEffort> comparator = Comparator.comparingInt(MemberEffort::getCommits);
    List<MemberEffort> sortedMemberEfforts = memberEfforts.stream().sorted(comparator).toList();

    StringBuilder report = new StringBuilder("Members' efforts:");
    sortedMemberEfforts.forEach(memberEffort -> report.append(memberEffort.toString()).append("\n"));
    log.error(report.toString());
  }
}
