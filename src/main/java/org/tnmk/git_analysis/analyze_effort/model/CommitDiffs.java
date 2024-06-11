package org.tnmk.git_analysis.analyze_effort.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.eclipse.jgit.diff.DiffEntry;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class CommitDiffs {
  private final CommitType commitType;
  private final List<DiffEntry> diffEntries;
}
