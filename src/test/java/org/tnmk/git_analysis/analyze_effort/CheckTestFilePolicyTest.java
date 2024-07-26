package org.tnmk.git_analysis.analyze_effort;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CheckTestFilePolicyTest {

  @ParameterizedTest
  @CsvSource({
    "src/test/java/org/tnmk/git_analysis/analyze_effort/CheckTestFilePolicyTest.java, true",
    "src/main/java/org/tnmk/git_analysis/analyze_effort/CheckTestFilePolicy.java, false",
    "src/test/resources/test-data.json, true",
    "src/main/resources/test-data.test.json, true",
    "src/main/resources/data.json, false",
    "some-folder/some-file.tsx, false",
    "some-folder/some-file.ts, false",
    "some-folder/some-file.test.tsx, true",
    "some-folder/some-file.test.ts, true",
    "some-folder/some-file.test.jsx, true",
    "some-folder/some-file.test.js, true",
    "e2e/some-file.js, true",
    "src/e2e/some-file.js, true",
    "cypress/some-file.js, true",
    "src/cypress/some-file.js, true",
  })
  void testIsTestFile(String filePath, boolean expected) {
    boolean result = CheckTestFilePolicy.isTestFile(filePath);
    assertEquals(expected, result);
  }
}
