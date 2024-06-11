package org.tnmk.git_analysis.analyze_effort;

import org.tnmk.git_analysis.analyze_effort.model.AliasMemberInManyRepos;
import org.tnmk.git_analysis.analyze_effort.model.CommitResult;
import org.tnmk.git_analysis.analyze_effort.model.CommittedFile;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GitFoldersReportHelper {
  private static final int TOP_FILES_TO_REPORT_PER_MEMBER = 1000;

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

  public static List<CommittedFile> sortCommitsByTotalWords(AliasMemberInManyRepos member) {
    Stream<CommittedFile> files = member.commits().stream().flatMap(commit -> commit.getFiles().stream());
    List<CommittedFile> sortedFiles = files.sorted(
      Comparator.comparingInt(CommittedFile::getChangedWords).reversed()
    ).limit(TOP_FILES_TO_REPORT_PER_MEMBER).toList();
    return sortedFiles;
  }
}
