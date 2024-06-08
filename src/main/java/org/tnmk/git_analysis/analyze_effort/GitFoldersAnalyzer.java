package org.tnmk.git_analysis.analyze_effort;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.stereotype.Service;
import org.tnmk.git_analysis.analyze_effort.model.Member;
import org.tnmk.git_analysis.analyze_effort.model.MergedMember;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GitFoldersAnalyzer {
  private final MemberEffortReport memberEffortReport;
  private final MemberMerger mergeMembers;
  private final GitFolderAnalyzer gitFolderAnalyzer;

  public void analyzeManyRepos(List<String> repoPaths) throws GitAPIException, IOException {
    List<MergedMember> membersInAllRepos = new ArrayList<>();
    for (String repositoryPath : repoPaths) {
      Map<String, Member> membersInOneRepo = gitFolderAnalyzer.analyzeOneRepo(repositoryPath);
      mergeMembers.mergeMembers(membersInAllRepos, membersInOneRepo.values());
    }
    memberEffortReport.report(membersInAllRepos);
  }
}
