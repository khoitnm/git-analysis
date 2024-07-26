package org.tnmk.git_analysis.analyze_effort.model;

import org.springframework.util.CollectionUtils;
import org.tnmk.git_analysis.analyze_effort.AliasMemberHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AliasMember {
  /**
   * The data in this list will never be changed.
   * And if you configured the aliases in application.properties, it would the same values (and convert to lower case).
   * If no configuration in application.properties, the alias and {@link #members} will have only 1 element
   * with the same value as that single member's name.
   */
  private final List<String> nameAliasesLowerCases;
  private final List<Member> members;

  public AliasMember(List<String> aliases) {

    this.nameAliasesLowerCases = aliases.stream()
      .map(String::toLowerCase)
      .collect(Collectors.toUnmodifiableList());
    this.members = Collections.synchronizedList(new ArrayList<>());
  }

  /**
   * @see AliasMemberHelper#getMemberKey(List)
   */
  public String getMemberKey() {
    return AliasMemberHelper.getMemberKey(this.nameAliasesLowerCases);
  }

  public boolean matchAliases(List<String> aliases) {
    return CollectionUtils.containsAny(this.nameAliasesLowerCases, aliases);
  }

  public void mergeMember(Member member) {
    String memberNameLowerCase = member.getName().toLowerCase();
    if (!nameAliasesLowerCases.contains(memberNameLowerCase)) {
      throw new IllegalStateException("Member name '%s' doesn't match alias: '%s'".formatted(member.getName(), nameAliasesLowerCases));
    }
    members.add(member);
  }

  /**
   * This method always return a new list, so the original list won't be impacted.
   */
  public List<String> getNameAliasesLowerCases() {
    return nameAliasesLowerCases.stream().map(String::toLowerCase).toList();
  }

  public List<CommitResult> pullRequests() {
    return members.stream().flatMap(member -> member.getPullRequests().stream()).toList();
  }

  public List<CommitResult> commits() {
    return members.stream().flatMap(member -> member.getCommits().stream()).toList();
  }

  public int commitsSize() {
    return commits().size();
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
