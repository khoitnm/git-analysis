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
    Comparator<MemberEffort> memberComparator = Comparator.<MemberEffort>comparingInt(
      // compare by the number of commits
      member -> member.getCommits().size()
    ).reversed();
    List<MemberEffort> sortedMemberEfforts = memberEfforts.stream().sorted(memberComparator).toList();

    StringBuilder report = new StringBuilder("Members' efforts:\n");
    sortedMemberEfforts.forEach(memberEffort -> report.append(memberEffort.toString()).append("\n"));
    log.info(report.toString());
  }
}
