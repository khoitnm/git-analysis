package org.tnmk.git_analysis.analyze_effort;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.revwalk.RevCommit;
import org.springframework.stereotype.Service;
import org.tnmk.git_analysis.config.GitFolderProperties;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberEffortAnalyzer {
    private final GitFolderProperties gitFolderProperties;

    public void start() {

        try (Git git = Git.open(new File(gitFolderProperties.getPath()))) {
            Map<String, Integer> commitCountMap = new HashMap<>();

            LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);

            LogCommand logCommand = git.log();
            for (RevCommit commit : logCommand.call()) {
                LocalDateTime commitDateTime = LocalDateTime.ofInstant(commit.getAuthorIdent().getWhen().toInstant(), ZoneId.systemDefault());

                if (commitDateTime.isAfter(oneMonthAgo)) {
                    String authorName = commit.getAuthorIdent().getName();
                    commitCountMap.put(authorName, commitCountMap.getOrDefault(authorName, 0) + 1);
                }
            }

            System.out.println("Commit Counts:");
            for (Map.Entry<String, Integer> entry : commitCountMap.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}  
