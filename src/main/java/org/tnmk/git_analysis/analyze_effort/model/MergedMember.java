package org.tnmk.git_analysis.analyze_effort.model;

import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor
public class MergedMember {
  private final List<String> nameAliasesLowerCase = Collections.synchronizedList(new ArrayList<>());
  private final List<Member> members = Collections.synchronizedList(new ArrayList<>());

  public void mergeMember(Member member) {
    String memberNameLowerCase = member.getName().toLowerCase();
    if (!nameAliasesLowerCase.contains(memberNameLowerCase)) {
      nameAliasesLowerCase.add(memberNameLowerCase);
    }
    members.add(member);
  }

  /**
   * This method always return a new list, so the original list won't be impacted.
   */
  public List<String> getNameAliasesLowerCases() {
    return nameAliasesLowerCase.stream().map(String::toLowerCase).toList();
  }

  public List<CommitResult> commits() {
    return members.stream().flatMap(member -> member.getCommits().stream()).toList();
  }

  public int commitsSize() {
    return members.stream().mapToInt(member -> member.getCommits().size()).sum();
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
