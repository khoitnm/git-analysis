package org.tnmk.git_analysis.analyze_effort;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GitCommitTicketHelperTest {

  @ParameterizedTest
  @CsvSource({
    "'A-1 Fixed bug in the system', 'A-1'",
    "'GH-1 Fixed bug in the system', 'GH-1'",
    "'GH-12 Fixed bug in the system', 'GH-12'",
    "'ABC-987 Another example with ticket ', 'ABC-987'",
    "'DEF-1234 Fixed bug in the system', 'DEF-1234'",
    "'DEF-1234\nFixed bug in the system', 'DEF-1234'",
    "'DEF-1234\tFixed bug in the system', 'DEF-1234'",
    "'DEF-1234Fixed bug in the system', ''",//There must be a space after the number of the ticket ID.
    "'Fixed bug in the system, DEF-1234', ''",//The ticket ID must be at the beginning of the line.
    "'Multiple lines\nJIRA-5678\nAnother line', ''",//The ticket ID must be at the beginning of the first line.
    "'No ticket mentioned here', ''",
  })
  void testExtractTicketId(String fullMessage, String expectedTicketId) {
    Optional<String> result = GitCommitTicketHelper.extractTicketId(fullMessage);
    assertEquals(Optional.ofNullable(expectedTicketId.isEmpty() ? null : expectedTicketId), result);
  }
}
