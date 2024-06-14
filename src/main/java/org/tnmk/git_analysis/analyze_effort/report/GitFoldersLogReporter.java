package org.tnmk.git_analysis.analyze_effort.report;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.tnmk.git_analysis.analyze_effort.model.AliasMemberInManyRepos;
import org.tnmk.git_analysis.analyze_effort.model.AliasMemberInRepo;
import org.tnmk.git_analysis.analyze_effort.model.CommittedFile;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @deprecated This class is used to write report into log message.
 * However, it's being replaced by {@link GitFoldersHtmlReporter} which provides much more convenient UI for the end user.
 */
@Deprecated
@Slf4j
@Service
public class GitFoldersLogReporter {
  private static final DateTimeFormatter commitDateTimeFormatter = DateTimeFormatter.ofPattern("yy/MM/dd hh:mm a");
  private static final DecimalFormat decimalFormat = new DecimalFormat("#,###");

  public void report(Collection<AliasMemberInManyRepos> members) {
    List<AliasMemberInManyRepos> sortedMembers = GitFoldersReportHelper.sortMembersByTotalWords(members);

    StringBuilder report = new StringBuilder("Members' efforts:\n");
    sortedMembers.forEach(oneMemberInManyRepos -> report.append(reportOneMemberInManyRepos(oneMemberInManyRepos)).append("\n"));
    log.info(report.toString());
  }

  private String reportOneMemberInManyRepos(AliasMemberInManyRepos memberInManyRepos) {
    String memberOverviewReport = ("%s" +
      "\ntotalWords: %s, totalLines: %s, totalFiles: %s, commits: %s, words/commit: %.01f, lines/commit: %.01f, files/commit: %.01f")
      .formatted(
        memberInManyRepos.getAliases(),
        decimalFormat.format(memberInManyRepos.totalWords()),
        decimalFormat.format(memberInManyRepos.totalLines()),
        decimalFormat.format(memberInManyRepos.totalFiles()),
        memberInManyRepos.commitsSize(),
        memberInManyRepos.avgWordsPerCommit(),
        memberInManyRepos.avgLinesPerCommit(),
        memberInManyRepos.avgFilesPerCommit()
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
    String result =
      "\t" + member.getRepoPath() + "\n"
        + "\t\ttotalWords: %s, totalLines: %s, totalFiles: %s, commits: %s, words/commit: %.01f, lines/commit: %.01f, files/commit: %.01f"
        .formatted(
          decimalFormat.format(member.getAliasMember().totalWords()),
          decimalFormat.format(member.getAliasMember().totalLines()),
          decimalFormat.format(member.getAliasMember().totalFiles()),
          member.getAliasMember().commitsSize(),
          member.getAliasMember().avgWordsPerCommit(),
          member.getAliasMember().avgLinesPerCommit(),
          member.getAliasMember().avgFilesPerCommit()
        );
    return result;
  }

  private String reportTopChangedFilesOfMember(AliasMemberInManyRepos member) {
    List<CommittedFile> sortedFiles = GitFoldersReportHelper.sortCommitsByTotalWords(member);
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
