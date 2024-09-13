package org.tnmk.git_analysis.analyze_effort;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.springframework.util.CollectionUtils;
import org.tnmk.git_analysis.analyze_effort.model.*;
import org.tnmk.git_analysis.config.GitAnalysisIgnoreProperties;
import org.tnmk.tech_common.utils.PathMatcherUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.tnmk.git_analysis.analyze_effort.GitCommitHelper.getCommitDateTime;
import static org.tnmk.git_analysis.analyze_effort.GitDiffHelper.createDiffFormatter;
import static org.tnmk.git_analysis.analyze_effort.GitDiffHelper.findDiff;

public class GitCommitAnalyzeHelper {
  /**
   * If the implementor of the commit should be ignored, return empty.
   * <strong>Note that we don't return empty in any other cases.</strong>
   * <p/>
   * We want this method, not the parent method to decide whether we should ignore the commit or not
   * is because we don't want this method spend too much effort to analyze the commit if it should be ignored.
   */
  public static CommitResultByAuthor analyzeCommit(Repository repository, GitRepo gitRepo, RevCommit commit, GitAnalysisIgnoreProperties gitAnalysisIgnoreProperties, Set<String> onlyIncludeMembers) throws IOException, GitAPIException {
    try (DiffFormatter diffFormatter = createDiffFormatter(repository)) {
      LocalDateTime commitDateTime = getCommitDateTime(commit);
      String commitRevision = commit.getName();
      CommitDiffs commitDiffs = findDiff(diffFormatter, commit);

      // Note that if onlyIncludeMembers is empty, we'll analyze all members.
      String implementor = commitDiffs.getImplementor().toLowerCase().trim();
      String committer = commitDiffs.getCommitter().toLowerCase().trim();
      String author = CommitAuthorPolicy.getCommitAuthor(committer, implementor);
      if (!CollectionUtils.isEmpty(onlyIncludeMembers) && !onlyIncludeMembers.contains(author)) {
        return CommitResultByAuthor.builder()
          .implementor(implementor)
          .committer(committer)
          .commitResult(Optional.empty())
          .build();
      }

      String mergeTargetBranch = null;
      if (commitDiffs.getCommitType() == CommitType.PULL_REQUEST) {
        mergeTargetBranch = GitBranchHelper.getTargetBranchOfMerge(repository, commit);
      }
      Optional<String> ticketId = GitCommitTicketHelper.extractTicketId(commit.getFullMessage());

      // TODO In some repos such as SC, must exclude merged to `master` branch because it's duplicated with PRs to dev.
      List<DiffEntry> diffEntries = commitDiffs.getDiffEntries();

      // This is the list of different files in the commit.
      List<CommitFile> files = new ArrayList<>();
      for (DiffEntry diffEntry : diffEntries) {
        if (diffEntry.getChangeType() == DiffEntry.ChangeType.DELETE) {
          continue;
        }
        if (PathMatcherUtils.matchAnyPattern(diffEntry.getNewPath(), gitAnalysisIgnoreProperties.getPathPatterns())) {
          continue;
        }

        int changedLines = diffFormatter.toFileHeader(diffEntry).toEditList().stream()
          .mapToInt(edit -> edit.getEndB() - edit.getBeginB())
          .sum();

        int changedWords = GitDiffWordHelper.countWordsChangedInFile(repository, diffFormatter, diffEntry);

        CommitFile file = CommitFile.builder()
          .gitRepo(gitRepo)
          .newFileId(diffEntry.getNewId().toObjectId().getName())
          .newPath(diffEntry.getNewPath())
          .changedLines(changedLines)
          .changedWords(changedWords)
          .ticketId(ticketId.orElse(null))
          .commitRevision(commitRevision)
          .commitDateTime(commitDateTime)
          .build();
        files.add(file);
      }

      Optional<CommitResult> commitResult = Optional.of(CommitResult.builder()
        .gitRepo(gitRepo)
        .committer(commitDiffs.getCommitter())
        .implementor(commitDiffs.getImplementor())
        .commitRevision(commit.getName())
        .commitDateTime(commitDateTime)
        .commitType(commitDiffs.getCommitType())
        .mergedTargetBranch(mergeTargetBranch)
        .files(files)
        .commitMessage(commit.getFullMessage())
        .ticketId(ticketId.orElse(null))
        .build()
      );
      return CommitResultByAuthor.builder()
        .implementor(implementor)
        .committer(committer)
        .commitResult(commitResult)
        .build();
    }
  }
}
