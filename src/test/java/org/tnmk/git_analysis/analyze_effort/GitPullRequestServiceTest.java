package org.tnmk.git_analysis.analyze_effort;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.tnmk.git_analysis.analyze_effort.model.GitPullRequest;
import org.tnmk.git_analysis.analyze_effort.model.GitRepo;
import org.tnmk.git_analysis.config.GitAnalysisConfig;
import org.tnmk.git_analysis.config.GitRepoConfig;
import org.tnmk.git_analysis.git_enterprise.GitEnterpriseService;
import org.tnmk.git_analysis.git_enterprise.github.GithubService;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GitPullRequestServiceTest {

    @Mock
    private GitRepoHelper gitRepoHelper;

    @Mock
    private GitEnterpriseService gitEnterpriseService; // Assuming a generic enterprise service

    @Mock
    private GithubService githubService; // Or specific services like GithubService, GitlabService

    @Mock
    private GitAnalysisConfig gitAnalysisConfig;

    @InjectMocks
    private GitPullRequestService gitPullRequestService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findPullRequests_shouldReturnListOfPullRequests_whenRepoExistsAndPrsFound() throws IOException {
        // Given
        GitRepoConfig repoConfig = new GitRepoConfig();
        repoConfig.setRepoUrl("https://github.com/test/repo.git");
        repoConfig.setRepoName("test-repo");
        // Assume it's a GitHub repo for this test, so GithubService will be used.
        repoConfig.setGitPersonalAccessToken("test-token"); // Needed for enterprise services

        GitRepo gitRepoMock = mock(GitRepo.class);
        Git gitAPI = mock(Git.class); // Mock JGit Git API object
        Repository repositoryMock = mock(Repository.class); // Mock JGit Repository

        when(gitRepoMock.getGit()).thenReturn(gitAPI);
        when(gitAPI.getRepository()).thenReturn(repositoryMock);
        when(repositoryMock.getDirectory()).thenReturn(new java.io.File(".")); // Dummy file path

        when(gitRepoHelper.openOrCloneRepo(eq(repoConfig), anyBoolean())).thenReturn(gitRepoMock);
        when(gitAnalysisConfig.isCleanReposWhenAnalyzing()).thenReturn(false);
        when(gitAnalysisConfig.getGitEnterpriseService(repoConfig.getRepoUrl())).thenReturn(githubService);


        GitPullRequest pr1 = new GitPullRequest();
        pr1.setId("1");
        pr1.setTitle("PR 1");
        GitPullRequest pr2 = new GitPullRequest();
        pr2.setId("2");
        pr2.setTitle("PR 2");
        List<GitPullRequest> expectedPullRequests = Arrays.asList(pr1, pr2);

        when(githubService.findPullRequests(repoConfig.getRepoName(), repoConfig.getGitPersonalAccessToken()))
            .thenReturn(expectedPullRequests);

        // When
        List<GitPullRequest> actualPullRequests = gitPullRequestService.findPullRequests(repoConfig);

        // Then
        assertNotNull(actualPullRequests);
        assertEquals(2, actualPullRequests.size());
        assertEquals("PR 1", actualPullRequests.get(0).getTitle());
        assertEquals("PR 2", actualPullRequests.get(1).getTitle());

        verify(gitRepoHelper).openOrCloneRepo(repoConfig, false);
        verify(gitAnalysisConfig).getGitEnterpriseService(repoConfig.getRepoUrl());
        verify(githubService).findPullRequests(repoConfig.getRepoName(), repoConfig.getGitPersonalAccessToken());
        verify(gitRepoMock).close(); // Ensure repo is closed
    }

    @Test
    void findPullRequests_whenNoEnterpriseServiceConfigured_shouldReturnEmptyList() throws IOException {
        // Given
        GitRepoConfig repoConfig = new GitRepoConfig();
        repoConfig.setRepoUrl("https://unsupported.example.com/test/repo.git");
        repoConfig.setRepoName("test-repo");

        GitRepo gitRepoMock = mock(GitRepo.class);
        when(gitRepoHelper.openOrCloneRepo(eq(repoConfig), anyBoolean())).thenReturn(gitRepoMock);
        when(gitAnalysisConfig.isCleanReposWhenAnalyzing()).thenReturn(true);
        when(gitAnalysisConfig.getGitEnterpriseService(repoConfig.getRepoUrl())).thenReturn(null); // No service for this URL

        // When
        List<GitPullRequest> actualPullRequests = gitPullRequestService.findPullRequests(repoConfig);

        // Then
        assertNotNull(actualPullRequests);
        assertEquals(0, actualPullRequests.size());

        verify(gitRepoHelper).openOrCloneRepo(repoConfig, true);
        verify(gitAnalysisConfig).getGitEnterpriseService(repoConfig.getRepoUrl());
        verify(githubService, never()).findPullRequests(any(), any()); // GithubService should not be called
        verify(gitEnterpriseService, never()).findPullRequests(any(), any()); // Generic service should not be called
        verify(gitRepoMock).close();
    }

    @Test
    void findPullRequests_whenEnterpriseServiceReturnsEmptyList_shouldReturnEmptyList() throws IOException {
        // Given
        GitRepoConfig repoConfig = new GitRepoConfig();
        repoConfig.setRepoUrl("https://github.com/test/empty-repo.git");
        repoConfig.setRepoName("empty-repo");
        repoConfig.setGitPersonalAccessToken("test-token");


        GitRepo gitRepoMock = mock(GitRepo.class);
        when(gitRepoHelper.openOrCloneRepo(eq(repoConfig), anyBoolean())).thenReturn(gitRepoMock);
        when(gitAnalysisConfig.isCleanReposWhenAnalyzing()).thenReturn(false);
        when(gitAnalysisConfig.getGitEnterpriseService(repoConfig.getRepoUrl())).thenReturn(githubService);

        when(githubService.findPullRequests(repoConfig.getRepoName(), repoConfig.getGitPersonalAccessToken()))
            .thenReturn(Collections.emptyList()); // Service returns no PRs

        // When
        List<GitPullRequest> actualPullRequests = gitPullRequestService.findPullRequests(repoConfig);

        // Then
        assertNotNull(actualPullRequests);
        assertEquals(0, actualPullRequests.size());

        verify(githubService).findPullRequests(repoConfig.getRepoName(), repoConfig.getGitPersonalAccessToken());
        verify(gitRepoMock).close();
    }

    @Test
    void findPullRequests_whenOpenOrCloneRepoFails_shouldThrowExceptionAndNotCallEnterpriseService() throws Exception {
        // Given
        GitRepoConfig repoConfig = new GitRepoConfig();
        repoConfig.setRepoUrl("https://github.com/test/fail-repo.git");
        repoConfig.setRepoName("fail-repo");

        when(gitRepoHelper.openOrCloneRepo(any(GitRepoConfig.class), anyBoolean())).thenThrow(new RuntimeException("Cloning failed"));
        when(gitAnalysisConfig.isCleanReposWhenAnalyzing()).thenReturn(true);

        // When & Then
        Exception exception = null;
        try {
            gitPullRequestService.findPullRequests(repoConfig);
        } catch (RuntimeException e) {
            exception = e;
        }

        assertNotNull(exception);
        assertEquals("Cloning failed", exception.getMessage());
        verify(gitAnalysisConfig, never()).getGitEnterpriseService(any());
        verify(githubService, never()).findPullRequests(any(), any());
    }

    @Test
    void findPullRequests_whenEnterpriseServiceThrowsException_shouldPropagateExceptionAndCloseRepo() throws IOException {
        // Given
        GitRepoConfig repoConfig = new GitRepoConfig();
        repoConfig.setRepoUrl("https://github.com/test/error-repo.git");
        repoConfig.setRepoName("error-repo");
        repoConfig.setGitPersonalAccessToken("test-token");

        GitRepo gitRepoMock = mock(GitRepo.class);
        when(gitRepoHelper.openOrCloneRepo(eq(repoConfig), anyBoolean())).thenReturn(gitRepoMock);
        when(gitAnalysisConfig.isCleanReposWhenAnalyzing()).thenReturn(false);
        when(gitAnalysisConfig.getGitEnterpriseService(repoConfig.getRepoUrl())).thenReturn(githubService);

        when(githubService.findPullRequests(repoConfig.getRepoName(), repoConfig.getGitPersonalAccessToken()))
            .thenThrow(new RuntimeException("API error"));

        // When & Then
        Exception exception = null;
        try {
            gitPullRequestService.findPullRequests(repoConfig);
        } catch (RuntimeException e) {
            exception = e;
        }

        assertNotNull(exception);
        assertEquals("API error", exception.getMessage());

        verify(githubService).findPullRequests(repoConfig.getRepoName(), repoConfig.getGitPersonalAccessToken());
        verify(gitRepoMock).close(); // Crucially, ensure repo is closed even if service fails
    }
}
