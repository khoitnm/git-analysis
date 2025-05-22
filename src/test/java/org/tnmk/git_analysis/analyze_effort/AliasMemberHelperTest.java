package org.tnmk.git_analysis.analyze_effort;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.tnmk.git_analysis.analyze_effort.model.AliasMember;
import org.tnmk.git_analysis.analyze_effort.model.GitCommitter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class AliasMemberHelperTest {

    @ParameterizedTest
    @CsvSource({
            "Main Name1, Main Name1, alias1@example.com, alias2@example.com", // Member name is a main name
            "alias1@example.com, Main Name1, alias1@example.com, alias2@example.com", // Member name is an alias
            "Alias2@example.com, Main Name1, alias1@example.com, ALIAS2@EXAMPLE.COM", // Member name is an alias with different capitalization
            "NonExistentName, NonExistentName, alias1@example.com, alias2@example.com", // Member name not in alias configuration
            "Main Name2, Main Name2, alias3@example.com", // Another main name
            "alias3@example.com, Main Name2, alias3@example.com", // Alias for the second main name
    })
    void testGetMemberMainName(String memberName, String expectedMainName, String... aliases) {
        AliasMember aliasMember = new AliasMember("Main Name1", Arrays.asList("alias1@example.com", "alias2@example.com", "ALIAS2@EXAMPLE.COM"));
        AliasMember aliasMember2 = new AliasMember("Main Name2", Arrays.asList("alias3@example.com"));
        List<AliasMember> aliasMembers = Arrays.asList(aliasMember, aliasMember2);
        String actualMainName = AliasMemberHelper.getMemberMainName(aliasMembers, memberName);
        assertEquals(expectedMainName, actualMainName);
    }

    @Test
    void testGetMemberMainName_emptyAliasConfig() {
        String memberName = "Test Name";
        String actualMainName = AliasMemberHelper.getMemberMainName(Collections.emptyList(), memberName);
        assertEquals(memberName, actualMainName);
    }

    @ParameterizedTest
    @CsvSource({
            "Main Name1, email@example.com, Main Name1", // Matches main name
            "alias1@example.com, email@example.com, Main Name1", // Matches alias by name
            "othername, alias1@example.com, Main Name1", // Matches alias by email
            "Main Name2, email2@example.com, Main Name2", // Matches another main name
            "alias3@example.com, email2@example.com, Main Name2", // Matches another alias by name
            "othername2, alias3@example.com, Main Name2", // Matches another alias by email
            "NonExistentName, nonexist@example.com, ", // No match, expect null
    })
    void testFindAliasMember(String committerName, String committerEmail, String expectedMainName) {
        AliasMember aliasMember1 = new AliasMember("Main Name1", Arrays.asList("alias1@example.com", "aliasA"));
        AliasMember aliasMember2 = new AliasMember("Main Name2", Arrays.asList("alias3@example.com", "aliasB"));
        List<AliasMember> aliasMembers = Arrays.asList(aliasMember1, aliasMember2);

        GitCommitter committer = new GitCommitter(committerName, committerEmail);
        AliasMember foundAliasMember = AliasMemberHelper.findAliasMember(aliasMembers, committer);

        if (expectedMainName == null || expectedMainName.isEmpty()) {
            assertNull(foundAliasMember);
        } else {
            assertEquals(expectedMainName, foundAliasMember.getMainName());
        }
    }

    @Test
    void testFindAliasMember_emptyAliasList() {
        GitCommitter committer = new GitCommitter("Test Name", "test@example.com");
        AliasMember foundAliasMember = AliasMemberHelper.findAliasMember(Collections.emptyList(), committer);
        assertNull(foundAliasMember);
    }

    @ParameterizedTest
    @CsvSource({
            "main name with space, email@example.com, Main Name With Space", // GitCommitter's name matches a main name in alias list (case-insensitive for main name)
            "ALIAS WITH SPACE, email@example.com, Main Name For Alias With Space", // GitCommitter's name matches an alias (case-insensitive for alias)
            "othername, main name with space@example.com, Main Name With Space", // GitCommitter's email matches a main name in alias list (case-insensitive for main name)
            "othername, alias with space@example.com, Main Name For Alias With Space" // GitCommitter's email matches an alias (case-insensitive for alias)
    })
    void testFindAliasMember_nameOrEmailMatchingAliasOrMainName_caseInsensitive(
            String committerName, String committerEmail, String expectedMainName) {
        AliasMember aliasMember1 = new AliasMember("Main Name With Space", Arrays.asList("main name with space@example.com", "somealias@example.com"));
        AliasMember aliasMember2 = new AliasMember("Main Name For Alias With Space", Arrays.asList("alias with space@example.com", "Alias With Space"));
        List<AliasMember> aliasMembers = Arrays.asList(aliasMember1, aliasMember2);

        GitCommitter committer = new GitCommitter(committerName, committerEmail);
        AliasMember foundAliasMember = AliasMemberHelper.findAliasMember(aliasMembers, committer);
        assertEquals(expectedMainName, foundAliasMember.getMainName());
    }
}
