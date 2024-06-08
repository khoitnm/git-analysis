package org.tnmk.git_analysis.analyze_effort;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.IOException;
import java.util.List;

public class GitDiffWordHelper {
  public static int countWordsChangedInFile(Repository repository, DiffFormatter formatter, DiffEntry entry) throws IOException {
    List<Edit> edits = formatter.toFileHeader(entry).toEditList();

    int changedWords = 0;

    // Iterate through the edits in the file
    for (Edit edit : edits) {
      int beginA = edit.getBeginA();
      int endA = edit.getEndA();
      int beginB = edit.getBeginB();
      int endB = edit.getEndB();

      // Get the content of the old and new versions of the file
      boolean hasOldFile = hasOldFile(entry);
      RawText oldText = hasOldFile
        ? new RawText(repository.open(entry.getOldId().toObjectId()).getBytes())
        : null;
      boolean hasNewFile = hasNewFile(entry);
      RawText newText = hasNewFile
        ? new RawText(repository.open(entry.getNewId().toObjectId()).getBytes())
        : null;

      // Calculate the number of changed words in the edit
      for (int iNewLine = beginB; iNewLine < endB; iNewLine++) {
        if (hasNewFile) {
          if (hasOldFile && (iNewLine >= beginA && iNewLine < endA)) {
            // Modified line
            String oldLine = oldText.getString(iNewLine);
            String newLine = newText.getString(iNewLine);
            changedWords += calculateWordDiff(oldLine, newLine);
          } else {
            // Added line
            String newLine = newText.getString(iNewLine);
            String[] words = splitWords(newLine);
            changedWords += words.length;
          }
        } else { // the file is deleted, consider changedWords is 1
          changedWords += 1;
        }
      }
    }

    return changedWords;
  }

  private static boolean hasNewFile(DiffEntry entry) {
    return entry.getChangeType() != DiffEntry.ChangeType.DELETE;
  }

  private static boolean hasOldFile(DiffEntry diffEntry) {
    return diffEntry.getChangeType() != DiffEntry.ChangeType.ADD;
  }

  private static int calculateWordDiff(String oldLine, String newLine) {
    String[] oldWords = splitWords(oldLine);
    String[] newWords = splitWords(newLine);
//    return Math.abs(oldWords.length - newWords.length);

    int changedWords = 0;
    int maxLength = Math.max(oldWords.length, newWords.length);

    for (int i = 0; i < maxLength; i++) {
      String oldWord = i < oldWords.length ? oldWords[i] : "";
      String newWord = i < newWords.length ? newWords[i] : "";

      if (!oldWord.equals(newWord)) {
        changedWords++;
      }
    }

    return changedWords;
  }

  // Implement this method to retrieve a RevCommit representing a commit
  private static RevCommit getCommit() {
    // Your implementation here
    return null;
  }

  private static String[] splitWords(String line) {
    return line.split("\\W+");
  }
}
