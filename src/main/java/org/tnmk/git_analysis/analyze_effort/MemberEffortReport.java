package org.tnmk.git_analysis.analyze_effort;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.tnmk.git_analysis.analyze_effort.model.AliasMemberInManyRepos;
import org.tnmk.git_analysis.analyze_effort.model.AliasMemberInRepo;
import org.tnmk.git_analysis.analyze_effort.model.CommittedFile;

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

  public void report(Collection<AliasMemberInManyRepos> members) {
    Comparator<AliasMemberInManyRepos> memberComparator = Comparator.<AliasMemberInManyRepos>comparingInt(
//      member -> member.getCommits().size()
      AliasMemberInManyRepos::commitsSize
    ).reversed();
    List<AliasMemberInManyRepos> sortedMembers = members.stream().sorted(memberComparator).toList();

    StringBuilder report = new StringBuilder("Members' efforts:\n");
    sortedMembers.forEach(oneMemberInManyRepos -> report.append(reportOneMemberInManyRepos(oneMemberInManyRepos)).append("\n"));
    log.info(report.toString());
  }

  private String reportOneMemberInManyRepos(AliasMemberInManyRepos memberInManyRepos) {
    String memberOverviewReport = ("%s" +
      "\ncommits: %s, files/commit: %.01f, lines/commit: %.01f, words/commit: %.01f, totalFiles: %s, totalLines: %s, totalWords: %s.")
      .formatted(
        memberInManyRepos.getAliases(),
        memberInManyRepos.commitsSize(),
        memberInManyRepos.avgFilesPerCommit(),
        memberInManyRepos.avgLinesPerCommit(),
        memberInManyRepos.avgWordsPerCommit(),
        decimalFormat.format(memberInManyRepos.totalFiles()),
        decimalFormat.format(memberInManyRepos.totalLines()),
        decimalFormat.format(memberInManyRepos.totalWords())
      );

    String effortInAllRepos = memberInManyRepos.getMemberInRepos().stream().map(
      this::reportOneMemberInOneRepo
    ).collect(Collectors.joining("\n"));

    String topChangedFilesAcrossRepos = reportTopChangedFilesOfMember(memberInManyRepos);

    return memberOverviewReport + "\n"
      + "Numbers in each repo:\n"
      + effortInAllRepos + "\n"
      + "Top biggest changed files:\n"
      + topChangedFilesAcrossRepos + "\n";
  }

  private String reportOneMemberInOneRepo(AliasMemberInRepo member) {
    String result = (
      "\t" + member.getRepoPath() + "\n"
        + "\t\tcommits: %s, files/commit: %.01f, lines/commit: %.01f, words/commit: %.01f, totalFiles: %s, totalLines: %s, totalWords: %s.")
      .formatted(
        member.getAliasMember().commitsSize(),
        member.getAliasMember().avgFilesPerCommit(),
        member.getAliasMember().avgLinesPerCommit(),
        member.getAliasMember().avgWordsPerCommit(),
        decimalFormat.format(member.getAliasMember().totalFiles()),
        decimalFormat.format(member.getAliasMember().totalLines()),
        decimalFormat.format(member.getAliasMember().totalWords())
      );
    return result;
  }

  private String reportTopChangedFilesOfMember(AliasMemberInManyRepos member) {
    Stream<CommittedFile> files = member.commits().stream().flatMap(commit -> commit.getFiles().stream());
    List<CommittedFile> sortedFiles = files.sorted(
      Comparator.comparingInt(CommittedFile::getChangedWords).reversed()
    ).limit(TOP_FILES_TO_REPORT_PER_MEMBER).toList();
    return reportFiles(sortedFiles);
  }

  private String reportFiles(List<CommittedFile> sortedFiles) {
    return sortedFiles.stream().map(this::reportFile).collect(Collectors.joining("\n"));
  }

  private String reportFile(CommittedFile file) {
    String report = "\twords: %s, commit: %s, date: %s, file: %s"
      .formatted(
        file.getChangedWords(),
        file.getCommitRevision(),
        commitDateTimeFormatter.format(file.getCommitDateTime()),
        file.getNewPath()
      );
    return report;
  }
}
