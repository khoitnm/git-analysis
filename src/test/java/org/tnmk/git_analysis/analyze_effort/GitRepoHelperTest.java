package org.tnmk.git_analysis.analyze_effort;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GitRepoHelperTest {
  @ParameterizedTest
  @CsvSource({
    "https://git.customdomain.com/projects/SOP/repos/my-sample-repository.git, git.customdomain.com",
    "git@git.customdomain.com:SOP/my-sample-repository.git, git.customdomain.com", // This URL happens in Github
    "ssh://git@git.customdomain.com:7999/sop/my-sample-repository.git, git.customdomain.com",
    "https://github.com/khoitnm/git-analysis.git, github.com",
  })
  public void testGetHostFromCloneUrl(String cloneUrl, String expectedHost) {
    String actualHost = GitRepoHelper.getHostFromCloneUrl(cloneUrl);
    assertEquals(expectedHost, actualHost);
  }

  @ParameterizedTest
  @CsvSource({
    "https://git.customdomain.com/scm/sop/my-sample-repository.git, sop",
    "git@git.customdomain.com:SOP/my-sample-repository.git, SOP",
    "ssh://git@git.customdomain.com:7999/sop/my-sample-repository.git, sop",
    "https://github.com/khoitnm/git-analysis.git, khoitnm",
  })
  public void testGetProjectName(String cloneUrl, String expectedProjectName) {
    String actualProjectName = GitRepoHelper.getProjectName(cloneUrl);
    assertEquals(expectedProjectName, actualProjectName);
  }

  @ParameterizedTest
  @CsvSource({
    "https://git.customdomain.com/projects/SOP/repos/my-sample-repository.git, my-sample-repository",
    "git@git.customdomain.com:SOP/my-sample-repository.git, my-sample-repository",
    "ssh://git@git.customdomain.com:7999/sop/my-sample-repository.git, my-sample-repository",
    "https://github.com/khoitnm/git-analysis.git, git-analysis",
  })
  public void testGetRepoName(String cloneUrl, String expectedRepoName) {
    String actualRepoName = GitRepoHelper.getRepoName(cloneUrl);
    assertEquals(expectedRepoName, actualRepoName);
  }
}
