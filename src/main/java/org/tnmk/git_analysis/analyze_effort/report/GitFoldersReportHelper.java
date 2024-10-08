package org.tnmk.git_analysis.analyze_effort.report;

import org.tnmk.git_analysis.analyze_effort.model.AliasMemberInManyRepos;
import org.tnmk.git_analysis.analyze_effort.model.CommitFile;
import org.tnmk.git_analysis.analyze_effort.model.CommitResult;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GitFoldersReportHelper {
  public static final int TOP_FILES_TO_REPORT_PER_MEMBER = 1000;

  public static double avgWords(Collection<CommitResult> commitResults) {
    return commitResults.stream().mapToInt(CommitResult::getWordsCount).average().orElse(0);
  }

  public static double avgLines(Collection<CommitResult> commitResults) {
    return commitResults.stream().mapToInt(CommitResult::getLinesCount).average().orElse(0);
  }

  public static double avgFiles(Collection<CommitResult> commitResults) {
    return commitResults.stream().mapToInt(CommitResult::getFilesCount).average().orElse(0);
  }

  public static String getRepoName(String repoPath) {
    String[] split = repoPath.split("/");
    return split[split.length - 1];
  }

  public static String getRepoNameFromMemberInRepo(AliasMemberInManyRepos memberInManyRepos) {
    return memberInManyRepos.getMemberInRepos().stream()
      .map(repo -> getRepoName(repo.getRepoPath()))
      .collect(Collectors.joining(", "));
  }

  public static List<AliasMemberInManyRepos> sortMembersByTotalWords(Collection<AliasMemberInManyRepos> members) {
    Comparator<AliasMemberInManyRepos> memberComparator = Comparator.<AliasMemberInManyRepos>comparingInt(
      AliasMemberInManyRepos::totalWords
    ).reversed();
    return members.stream().sorted(memberComparator).toList();
  }

  public static List<CommitFile> sortCommitsByTotalWords(AliasMemberInManyRepos member) {
    Stream<CommitFile> files = member.commits().stream().flatMap(commit -> commit.getFiles().stream());
    List<CommitFile> sortedFiles = files.sorted(
      Comparator.comparingInt(CommitFile::getChangedWords).reversed()
    ).limit(TOP_FILES_TO_REPORT_PER_MEMBER).toList();
    return sortedFiles;
  }

  public static List<CommitResult> sortPullRequestsByWords(AliasMemberInManyRepos member) {
    List<CommitResult> sortedCommits = member.pullRequests().stream().sorted(
      Comparator.comparingInt(CommitResult::getWordsCount).reversed()
    ).toList();
    return sortedCommits;
  }
}
