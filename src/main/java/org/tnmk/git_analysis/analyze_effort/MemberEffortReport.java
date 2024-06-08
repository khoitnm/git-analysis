package org.tnmk.git_analysis.analyze_effort;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.tnmk.git_analysis.analyze_effort.model.CommittedFile;
import org.tnmk.git_analysis.analyze_effort.model.MergedMember;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class MemberEffortReport {
  private static final int TOP_FILES_TO_REPORT_PER_MEMBER = 5;
  private static final DateTimeFormatter commitDateTimeFormatter = DateTimeFormatter.ofPattern("yy/MM/dd hh:mm a");
  private static final DecimalFormat decimalFormat = new DecimalFormat("#,###");

  public void report(Collection<MergedMember> members) {
    Comparator<MergedMember> memberComparator = Comparator.<MergedMember>comparingInt(
//      member -> member.getCommits().size()
      MergedMember::totalWords
    ).reversed();
    List<MergedMember> sortedMembers = members.stream().sorted(memberComparator).toList();

    StringBuilder report = new StringBuilder("Members' efforts:\n");
    sortedMembers.forEach(member -> report.append(reportOneMember(member)).append("\n"));
    log.info(report.toString());
  }

  private String reportOneMember(MergedMember member) {
    String memberOverviewReport = ("%s" +
      "\ncommits: %s, files/commit: %.01f, lines/commit: %.01f, words/commit: %.01f, totalFiles: %s, totalLines: %s, totalWords: %s.")
      .formatted(
        member.getNameAliasesLowerCases(),
        member.commitsSize(),
        member.avgFilesPerCommit(),
        member.avgLinesPerCommit(),
        member.avgWordsPerCommit(),
        decimalFormat.format(member.totalFiles()),
        decimalFormat.format(member.totalLines()),
        decimalFormat.format(member.totalWords())
      );
    String memberTopChangedFiles = reportTopChangedFilesOfMember(member);
    return memberOverviewReport + " Top commits:\n" +
      memberTopChangedFiles + "\n";
  }

  private String reportTopChangedFilesOfMember(MergedMember member) {
    Stream<CommittedFile> files = member.commits().stream().flatMap(commit -> commit.getFiles().stream());
    List<CommittedFile> sortedFiles = files.sorted(
      Comparator.comparingInt(CommittedFile::getChangedLines).reversed()
    ).limit(TOP_FILES_TO_REPORT_PER_MEMBER).toList();
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
        commitDateTimeFormatter.format(file.getCommitDateTime()),
        file.getNewPath()
      );
    return report;
  }
}
