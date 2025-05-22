package org.tnmk.git_analysis.analyze_effort;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.tnmk.git_analysis.analyze_effort.model.AnalyzeEffortResult;
import org.tnmk.git_analysis.analyze_effort.model.AnalyzedCommitGroup;
import org.tnmk.git_analysis.analyze_effort.model.GitRepo;
import org.tnmk.git_analysis.analyze_effort.model.QueryCommitsResult;
import org.tnmk.git_analysis.config.GitAnalysisConfig;
import org.tnmk.git_analysis.config.GitRepoConfig;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock; // Added for mocking GitRepo

public class GitRepoServiceTest {

    @Mock
    private GitRepoHelper gitRepoHelper;

    @Mock
    private GitHelper gitHelper;

    @Mock
    private GitRepoAnalyzer gitRepoAnalyzer;

    @Mock
    private GitAnalysisConfig gitAnalysisConfig;

    @InjectMocks
    private GitRepoService gitRepoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void analyzeFeasibility_shouldReturnAnalyzeEffortResult() throws Exception {
        // Given
        GitRepoConfig repoConfig = new GitRepoConfig();
        repoConfig.setRepoUrl("https://github.com/test/repo.git");
        repoConfig.setRepoName("test-repo");

        GitRepo gitRepoMock = mock(GitRepo.class);
        when(gitRepoHelper.openOrCloneRepo(any(GitRepoConfig.class), anyBoolean())).thenReturn(gitRepoMock);

        QueryCommitsResult queryCommitsResultMock = new QueryCommitsResult(Collections.emptyList(), 0, 0);
        when(gitHelper.queryCommits(
            any(GitRepo.class),
            any(LocalDateTime.class),
            any(LocalDateTime.class),
            anyList())
        ).thenReturn(queryCommitsResultMock);

        List<AnalyzedCommitGroup> analyzedCommitGroupsMock = Collections.singletonList(mock(AnalyzedCommitGroup.class));
        when(gitRepoAnalyzer.analyzeEfforts(eq(queryCommitsResultMock), anyList())).thenReturn(analyzedCommitGroupsMock);

        LocalDateTime fromDateTime = LocalDateTime.now().minusDays(7);
        LocalDateTime toDateTime = LocalDateTime.now();
        List<String> ignoreAuthors = Arrays.asList("bot1", "bot2");

        when(gitAnalysisConfig.getCommitsFromDateTime()).thenReturn(fromDateTime);
        when(gitAnalysisConfig.getCommitsToDateTime()).thenReturn(toDateTime);
        when(gitAnalysisConfig.getIgnoreAuthors()).thenReturn(ignoreAuthors);
        when(gitAnalysisConfig.isCleanReposWhenAnalyzing()).thenReturn(true);

        // When
        AnalyzeEffortResult result = gitRepoService.analyzeFeasibility(repoConfig);

        // Then
        assertNotNull(result);
        assertEquals(repoConfig.getRepoName(), result.getRepoName());
        assertEquals(analyzedCommitGroupsMock, result.getAnalyzedCommitGroups());

        verify(gitRepoHelper).openOrCloneRepo(repoConfig, true);
        verify(gitHelper).queryCommits(gitRepoMock, fromDateTime, toDateTime, ignoreAuthors);
        verify(gitRepoAnalyzer).analyzeEfforts(queryCommitsResultMock, gitAnalysisConfig.getAliasMembers());
        verify(gitRepoMock).close();
    }

    @Test
    void analyzeFeasibility_whenOpenOrCloneRepoFails_shouldThrowExceptionAndNotCloseRepo() throws Exception {
        // Given
        GitRepoConfig repoConfig = new GitRepoConfig();
        repoConfig.setRepoUrl("https://github.com/test/repo.git");
        repoConfig.setRepoName("test-repo");

        when(gitRepoHelper.openOrCloneRepo(any(GitRepoConfig.class), anyBoolean())).thenThrow(new RuntimeException("Cloning failed"));
        when(gitAnalysisConfig.isCleanReposWhenAnalyzing()).thenReturn(true);


        // When & Then
        Exception exception = null;
        try {
            gitRepoService.analyzeFeasibility(repoConfig);
        } catch (RuntimeException e) {
            exception = e;
        }

        assertNotNull(exception);
        assertEquals("Cloning failed", exception.getMessage());
        // verify(gitRepoMock, never()).close(); // gitRepoMock is not even created in this path
    }


    @Test
    void analyzeFeasibility_withSpecificDateTimesAndIgnoreAuthorsInConfig_shouldUseThem() throws Exception {
        // Given
        GitRepoConfig repoConfig = new GitRepoConfig(); // Details don't matter for this specific test path as much
        GitRepo gitRepoMock = mock(GitRepo.class);
        QueryCommitsResult queryCommitsResultMock = new QueryCommitsResult(Collections.emptyList(),0 ,0);
        List<AnalyzedCommitGroup> analyzedCommitGroupsMock = Collections.emptyList();

        LocalDateTime specificFrom = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime specificTo = LocalDateTime.of(2023, 1, 31, 23, 59);
        List<String> specificIgnores = Collections.singletonList("specific-bot");
        List<org.tnmk.git_analysis.analyze_effort.model.AliasMember> aliasMembers = Collections.emptyList();


        when(gitRepoHelper.openOrCloneRepo(repoConfig, false)).thenReturn(gitRepoMock);
        when(gitAnalysisConfig.getCommitsFromDateTime()).thenReturn(specificFrom);
        when(gitAnalysisConfig.getCommitsToDateTime()).thenReturn(specificTo);
        when(gitAnalysisConfig.getIgnoreAuthors()).thenReturn(specificIgnores);
        when(gitAnalysisConfig.getAliasMembers()).thenReturn(aliasMembers);
        when(gitAnalysisConfig.isCleanReposWhenAnalyzing()).thenReturn(false); // Test this path

        when(gitHelper.queryCommits(gitRepoMock, specificFrom, specificTo, specificIgnores)).thenReturn(queryCommitsResultMock);
        when(gitRepoAnalyzer.analyzeEfforts(queryCommitsResultMock, aliasMembers)).thenReturn(analyzedCommitGroupsMock);

        // When
        AnalyzeEffortResult result = gitRepoService.analyzeFeasibility(repoConfig);

        // Then
        assertNotNull(result);
        verify(gitRepoHelper).openOrCloneRepo(repoConfig, false); // ensure cleanReposWhenAnalyzing=false is passed
        verify(gitHelper).queryCommits(gitRepoMock, specificFrom, specificTo, specificIgnores);
        verify(gitRepoAnalyzer).analyzeEfforts(queryCommitsResultMock, aliasMembers);
        verify(gitRepoMock).close();
    }
}
