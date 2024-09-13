package org.tnmk.git_analysis.analyze_effort.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import org.tnmk.git_analysis.analyze_effort.AliasMemberHelper;
import org.tnmk.git_analysis.analyze_effort.GitCommitTicketHelper;
import org.tnmk.git_analysis.analyze_effort.report.ReportCommitHelper;

import java.util.List;

@Getter
@Builder
public class AliasMemberInManyRepos {
  @NotEmpty
  private final List<@NotNull AliasMemberInRepo> memberInRepos;

  /**
   * @return this value is used to identify unique member across many repos
   * Note that authors with the same aliases are considered as single one unique member. It means such authors will have the same memberKey.
   */
  public String getMemberKey() {
    return AliasMemberHelper.getMemberKey(getAliases());
  }

  public List<String> getAliases() {
    /** We always has at least one item in {@link memberInRepos}.*/
    return memberInRepos.get(0).getAliasMember().getNameAliasesLowerCases();
  }

  public List<CommitResult> pullRequests() {
    return memberInRepos.stream().flatMap(memberInOneRepo -> memberInOneRepo.getAliasMember().pullRequests().stream()).toList();
  }

  public List<CommitResult> commits() {
    return memberInRepos.stream().flatMap(memberInOneRepo -> memberInOneRepo.getAliasMember().commits().stream()).toList();
  }

  public int commitsSize() {
    return memberInRepos.stream().mapToInt(memberInOneRepo -> memberInOneRepo.getAliasMember().commitsSize()).sum();
  }

  public List<CommitTask> commitTasks() {
    return GitCommitTicketHelper.toCommitTasks(this);
  }

  public int totalFiles() {
    int total = commits().stream().mapToInt(CommitResult::getFilesCount).sum();
    return total;
  }

  public int totalLines() {
    int total = commits().stream().mapToInt(CommitResult::getLinesCount).sum();
    return total;
  }

  public int totalWords() {
    int total = commits().stream().mapToInt(CommitResult::getWordsCount).sum();
    return total;
  }

  public String reportTestWords() {
    return ReportCommitHelper.reportTest(totalTestWords(), totalWords());
  }

  public String reportTestFiles() {
    return ReportCommitHelper.reportTest(totalTestFiles(), totalFiles());
  }

  public int totalTestWords() {
    return commits().stream().mapToInt(CommitResult::getTestWordsCount).sum();
  }

  public int totalTestLines() {
    return commits().stream().mapToInt(CommitResult::getTestLinesCount).sum();
  }

  public int totalTestFiles() {
    return commits().stream().mapToInt(CommitResult::getTestFilesCount).sum();
  }

  public double avgFilesPerCommit() {
    return totalFiles() / (double) commitsSize();
  }

  public double avgLinesPerCommit() {
    return totalLines() / (double) commitsSize();
  }

  public double avgWordsPerCommit() {
    return totalWords() / (double) commitsSize();
  }
}
