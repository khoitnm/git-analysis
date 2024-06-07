package org.tnmk.git_analysis.analyze_effort;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.tnmk.git_analysis.analyze_effort.model.CommittedFile;
import org.tnmk.git_analysis.analyze_effort.model.Member;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class MemberEffortReport {
  public void report(Collection<Member> members) {
    Comparator<Member> memberComparator = Comparator.<Member>comparingInt(
      // compare by the number of commits
      member -> member.getCommits().size()
    ).reversed();
    List<Member> sortedMembers = members.stream().sorted(memberComparator).toList();

    StringBuilder report = new StringBuilder("Members' efforts:\n");
    sortedMembers.forEach(member -> report.append(reportOneMember(member)).append("\n"));
    log.info(report.toString());
  }

  private String reportOneMember(Member member) {
    String memberOverviewReport = "%s, commits: %s, avgFiles: %.02f, avgLines: %.02f, totalFiles: %s, totalLines: %s"
      .formatted(
        member.getName(),
        member.getCommits().size(),
        member.avgFilesPerCommit(),
        member.avgLinesPerCommit(),
        member.totalFiles(),
        member.totalLines());
    String memberTopChangedFiles = reportTopChangedFilesOfMember(member);
    return memberOverviewReport + memberTopChangedFiles;
  }

  private String reportTopChangedFilesOfMember(Member member) {
    Stream<CommittedFile> files = member.getCommits().stream().flatMap(commit -> commit.getFiles().stream());
    Comparator<CommittedFile> fileComparator = Comparator.<CommittedFile>comparingInt(file -> file.getChangedLines());
    List<CommittedFile> sortedFiles = files.sorted(
      Comparator.comparingInt(CommittedFile::getChangedLines).reversed()
    ).limit(10).toList();
    return reportFiles(sortedFiles);
  }

  private String reportFiles(List<CommittedFile> sortedFiles) {
    return sortedFiles.stream().map(this::reportFile).collect(Collectors.joining("\n"));
  }

  private String reportFile(CommittedFile file) {
    String report = "\tlines: %s, commit: %s, date: %s, file: %s"
      .formatted(
        file.getChangedLines(),
        file.getCommitRevision(),
        file.getCommitDateTime(),
        file.getNewPath()
      );
    return report;
  }
}
