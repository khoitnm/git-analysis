package org.tnmk.tech_common.path_matcher;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class PathMatcherUtilsTest {

  @ParameterizedTest
  @CsvSource({
    // path                                   // pattern          // expectMatch
    //    "target                             , glob:**/target    , true",
    "SomeFile.class                           , glob:**/*.class   , false",
    "SomeFile.class                           , glob:*.class      , true",
    "somefolder/SomeFile.class                , glob:**/*.class   , true",
    "somefolder/child-folder/SomeFile.class   , glob:**/*.class   , true",
    "package-lock.json                        , glob:package-lock.json   , true",
    "someFolder/package-lock.json             , glob:**/package-lock.json   , true",
    "someFolder/child-folder/package-lock.json, glob:**/package-lock.json   , true",
  })
  void match(String path, String pattern, boolean expectMatch) {
    boolean actualResult = PathMatcherUtils.match(path, pattern);
    assertThat(actualResult).isEqualTo(expectMatch);
  }
}
