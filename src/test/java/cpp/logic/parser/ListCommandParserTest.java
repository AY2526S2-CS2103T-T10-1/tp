package cpp.logic.parser;

import org.junit.jupiter.api.Test;

import cpp.logic.Messages;
import cpp.logic.commands.ListAssignmentCommand;
import cpp.logic.commands.ListClassCommand;
import cpp.logic.commands.ListCommand;
import cpp.logic.commands.ListContactCommand;

public class ListCommandParserTest {
    private ListCommandParser parser = new ListCommandParser();

    @Test
    public void parse_emptyArgs_throwsParseException() {
        CommandParserTestUtil.assertParseFailure(this.parser, "     ",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT,
                        ListCommand.MESSAGE_TAB_EMPTY + "\n" + ListCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_emptyString_throwsParseException() {
        CommandParserTestUtil.assertParseFailure(this.parser, "",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT,
                        ListCommand.MESSAGE_TAB_EMPTY + "\n" + ListCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_validArgs_returnsListCommand() {
        // no leading and trailing whitespaces
        ListCommand expectedListCommand = new ListContactCommand();
        CommandParserTestUtil.assertParseSuccess(this.parser, "contacts", expectedListCommand);

        // multiple whitespaces between keywords
        CommandParserTestUtil.assertParseSuccess(this.parser, " \n contacts \n \t ", expectedListCommand);
    }

    @Test
    public void parse_assignmentsArgs_returnsListCommand() {
        ListCommand expectedListCommand = new ListAssignmentCommand();
        CommandParserTestUtil.assertParseSuccess(this.parser, "assignments", expectedListCommand);
    }

    @Test
    public void parse_assignmentsArgsWithWhitespace_returnsListCommand() {
        ListCommand expectedListCommand = new ListAssignmentCommand();
        CommandParserTestUtil.assertParseSuccess(this.parser, "  assignments  ", expectedListCommand);
        CommandParserTestUtil.assertParseSuccess(this.parser, "\t\nassignments\t\n", expectedListCommand);
    }

    @Test
    public void parse_classesArgs_returnsListCommand() {
        ListCommand expectedListCommand = new ListClassCommand();
        CommandParserTestUtil.assertParseSuccess(this.parser, "classes", expectedListCommand);
    }

    @Test
    public void parse_classesArgsWithWhitespace_returnsListCommand() {
        ListCommand expectedListCommand = new ListClassCommand();
        CommandParserTestUtil.assertParseSuccess(this.parser, "  classes  ", expectedListCommand);
        CommandParserTestUtil.assertParseSuccess(this.parser, "\n\tclasses\n\t", expectedListCommand);
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        CommandParserTestUtil.assertParseFailure(this.parser, "invalid",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_partialMatch_throwsParseException() {
        CommandParserTestUtil.assertParseFailure(this.parser, "contact",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
        CommandParserTestUtil.assertParseFailure(this.parser, "assign",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
        CommandParserTestUtil.assertParseFailure(this.parser, "class",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_caseInsensitive_contacts() {
        ListCommand expectedListCommand = new ListContactCommand();
        CommandParserTestUtil.assertParseSuccess(this.parser, "CONTACTS", expectedListCommand);
        CommandParserTestUtil.assertParseSuccess(this.parser, "Contacts", expectedListCommand);
        CommandParserTestUtil.assertParseSuccess(this.parser, "CoNtAcTs", expectedListCommand);
        CommandParserTestUtil.assertParseSuccess(this.parser, "cOnTaCtS", expectedListCommand);
        CommandParserTestUtil.assertParseSuccess(this.parser, "Ct", expectedListCommand);
        CommandParserTestUtil.assertParseSuccess(this.parser, "cT", expectedListCommand);
        CommandParserTestUtil.assertParseSuccess(this.parser, "CT", expectedListCommand);
        CommandParserTestUtil.assertParseSuccess(this.parser, "ct", expectedListCommand);
    }

    @Test
    public void parse_caseInsensitive_assignments() {
        ListCommand expectedListCommand = new ListAssignmentCommand();
        CommandParserTestUtil.assertParseSuccess(this.parser, "ASSIGNMENTS", expectedListCommand);
        CommandParserTestUtil.assertParseSuccess(this.parser, "Assignments", expectedListCommand);
        CommandParserTestUtil.assertParseSuccess(this.parser, "AsSiGnMeNtS", expectedListCommand);
        CommandParserTestUtil.assertParseSuccess(this.parser, "ass", expectedListCommand);
        CommandParserTestUtil.assertParseSuccess(this.parser, "ASS", expectedListCommand);
        CommandParserTestUtil.assertParseSuccess(this.parser, "aSs", expectedListCommand);
    }

    @Test
    public void parse_caseInsensitive_classes() {
        ListCommand expectedListCommand = new ListClassCommand();
        CommandParserTestUtil.assertParseSuccess(this.parser, "CLASSES", expectedListCommand);
        CommandParserTestUtil.assertParseSuccess(this.parser, "Classes", expectedListCommand);
        CommandParserTestUtil.assertParseSuccess(this.parser, "ClAsSeS", expectedListCommand);
        CommandParserTestUtil.assertParseSuccess(this.parser, "c", expectedListCommand);
        CommandParserTestUtil.assertParseSuccess(this.parser, "C", expectedListCommand);
    }

    @Test
    public void parse_caseInsensitiveWithWhitespace_contacts() {
        ListCommand expectedListCommand = new ListContactCommand();
        CommandParserTestUtil.assertParseSuccess(this.parser, "  CONTACTS  ", expectedListCommand);
        CommandParserTestUtil.assertParseSuccess(this.parser, "\t Contacts \n", expectedListCommand);
    }

    @Test
    public void parse_caseInsensitiveWithWhitespace_assignments() {
        ListCommand expectedListCommand = new ListAssignmentCommand();
        CommandParserTestUtil.assertParseSuccess(this.parser, "  ASSIGNMENTS  ", expectedListCommand);
        CommandParserTestUtil.assertParseSuccess(this.parser, "\t Assignments \n", expectedListCommand);
    }

    @Test
    public void parse_caseInsensitiveWithWhitespace_classes() {
        ListCommand expectedListCommand = new ListClassCommand();
        CommandParserTestUtil.assertParseSuccess(this.parser, "  CLASSES  ", expectedListCommand);
        CommandParserTestUtil.assertParseSuccess(this.parser, "\t Classes \n", expectedListCommand);
    }

    @Test
    public void parse_singleCharacter_throwsParseException() {
        CommandParserTestUtil.assertParseFailure(this.parser, "d",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
        CommandParserTestUtil.assertParseFailure(this.parser, "a",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_longInvalidInput_throwsParseException() {
        String longInput = "this is a very long invalid input that should not match any list command";
        CommandParserTestUtil.assertParseFailure(this.parser, longInput,
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_specialCharacters_throwsParseException() {
        CommandParserTestUtil.assertParseFailure(this.parser, "contacts!",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
        CommandParserTestUtil.assertParseFailure(this.parser, "@contacts",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
        CommandParserTestUtil.assertParseFailure(this.parser, "con@tacts",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_numbersOnly_throwsParseException() {
        CommandParserTestUtil.assertParseFailure(this.parser, "123",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
        CommandParserTestUtil.assertParseFailure(this.parser, "1",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_mixedCaseWithNumbers_throwsParseException() {
        CommandParserTestUtil.assertParseFailure(this.parser, "contacts123",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
        CommandParserTestUtil.assertParseFailure(this.parser, "123assignments",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_tabs_treatAsWhitespace() {
        ListCommand expectedListCommand = new ListContactCommand();
        CommandParserTestUtil.assertParseSuccess(this.parser, "\t\tcontacts\t\t", expectedListCommand);
    }

    @Test
    public void parse_newlines_treatAsWhitespace() {
        ListCommand expectedListCommand = new ListAssignmentCommand();
        CommandParserTestUtil.assertParseSuccess(this.parser, "\n\nassignments\n\n", expectedListCommand);
    }

    @Test
    public void parse_mixedWhitespace_treatAsWhitespace() {
        ListCommand expectedListCommand = new ListClassCommand();
        CommandParserTestUtil.assertParseSuccess(this.parser, " \t \n classes \n \t ", expectedListCommand);
    }

    @Test
    public void parse_similarButDifferentWords_throwsParseException() {
        CommandParserTestUtil.assertParseFailure(this.parser, "contactss",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
        CommandParserTestUtil.assertParseFailure(this.parser, "assigments",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
        CommandParserTestUtil.assertParseFailure(this.parser, "classe",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_extraSpace_throwsParseException() {
        CommandParserTestUtil.assertParseFailure(this.parser, "contacts extra",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
        CommandParserTestUtil.assertParseFailure(this.parser, "assignments multiple words",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
    }

    // ========== EDGE CASES FOR UNORTHODOX USER ACTIONS ==========

    @Test
    public void parse_unicodeCharacters_throwsParseException() {
        // Chinese characters
        CommandParserTestUtil.assertParseFailure(this.parser, "联系人",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
        // Arabic characters
        CommandParserTestUtil.assertParseFailure(this.parser, "جهات",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
        // Japanese characters
        CommandParserTestUtil.assertParseFailure(this.parser, "連絡先",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_emojiCharacters_throwsParseException() {
        CommandParserTestUtil.assertParseFailure(this.parser, "😀contacts",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
        CommandParserTestUtil.assertParseFailure(this.parser, "contacts🎉",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
        CommandParserTestUtil.assertParseFailure(this.parser, "👨‍👩‍👧",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_accentedCharacters_throwsParseException() {
        CommandParserTestUtil.assertParseFailure(this.parser, "côñtäcts",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
        CommandParserTestUtil.assertParseFailure(this.parser, "àssignmènts",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
        CommandParserTestUtil.assertParseFailure(this.parser, "clässës",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_unicodeWhitespace_throwsParseException() {
        // Non-breaking space (U+00A0) is NOT trimmed by Java's trim()
        // So the string becomes "\u00A0contacts\u00A0" which doesn't match "contacts"
        CommandParserTestUtil.assertParseFailure(this.parser, "\u00A0contacts\u00A0",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_controlCharacters_succeeds() {
        // Null character is trimmed by trim() (codes <= U+0020 are trimmed)
        ListCommand expectedListCommand = new ListContactCommand();
        CommandParserTestUtil.assertParseSuccess(this.parser, "contacts\u0000", expectedListCommand);
    }

    @Test
    public void parse_controlCharactersInMiddle_throwsParseException() {
        // Tab character in the middle of word is NOT trimmed, so exact match fails
        CommandParserTestUtil.assertParseFailure(this.parser, "co\u0009ntacts",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_extremelyLongInput_throwsParseException() {
        StringBuilder longInput = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            longInput.append("a");
        }
        CommandParserTestUtil.assertParseFailure(this.parser, longInput.toString(),
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_repeatedCharacters_throwsParseException() {
        CommandParserTestUtil.assertParseFailure(this.parser, "cccccccccccc",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
        CommandParserTestUtil.assertParseFailure(this.parser, "aaaaaaaaaaaa",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_typoOneCharacterOff_throwsParseException() {
        // One character off
        CommandParserTestUtil.assertParseFailure(this.parser, "contacs",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
        CommandParserTestUtil.assertParseFailure(this.parser, "aassignments",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
        CommandParserTestUtil.assertParseFailure(this.parser, "classs",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_transposedCharacters_throwsParseException() {
        // Transposed characters
        CommandParserTestUtil.assertParseFailure(this.parser, "cnotacts",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
        CommandParserTestUtil.assertParseFailure(this.parser, "asignments",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_reverseSpelling_throwsParseException() {
        CommandParserTestUtil.assertParseFailure(this.parser, "stcatnoc",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
        CommandParserTestUtil.assertParseFailure(this.parser, "sessamgissa",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_allNumbers_throwsParseException() {
        CommandParserTestUtil.assertParseFailure(this.parser, "00000000000",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
        CommandParserTestUtil.assertParseFailure(this.parser, "999999999999",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_mixedSpecialCharacters_throwsParseException() {
        CommandParserTestUtil.assertParseFailure(this.parser, "con@#$%^&*()",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
        CommandParserTestUtil.assertParseFailure(this.parser, "!!!contacts!!!",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_underscoreAndDash_throwsParseException() {
        CommandParserTestUtil.assertParseFailure(this.parser, "con_tacts",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
        CommandParserTestUtil.assertParseFailure(this.parser, "assign-ments",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
        CommandParserTestUtil.assertParseFailure(this.parser, "clas.ses",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_parenthesesAndBrackets_throwsParseException() {
        CommandParserTestUtil.assertParseFailure(this.parser, "(contacts)",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
        CommandParserTestUtil.assertParseFailure(this.parser, "[assignments]",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
        CommandParserTestUtil.assertParseFailure(this.parser, "{classes}",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_quotesAndApostrophes_throwsParseException() {
        CommandParserTestUtil.assertParseFailure(this.parser, "\"contacts\"",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
        CommandParserTestUtil.assertParseFailure(this.parser, "'assignments'",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
        CommandParserTestUtil.assertParseFailure(this.parser, "`classes`",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_slashCharacters_throwsParseException() {
        CommandParserTestUtil.assertParseFailure(this.parser, "contacts/",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
        CommandParserTestUtil.assertParseFailure(this.parser, "/assignments",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
        CommandParserTestUtil.assertParseFailure(this.parser, "cla\\sses",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_leadingZeros_throwsParseException() {
        CommandParserTestUtil.assertParseFailure(this.parser, "00001",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
        CommandParserTestUtil.assertParseFailure(this.parser, "000",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_scientificNotation_throwsParseException() {
        CommandParserTestUtil.assertParseFailure(this.parser, "1e5",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
        CommandParserTestUtil.assertParseFailure(this.parser, "1.5e-10",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_hexadecimalInput_throwsParseException() {
        CommandParserTestUtil.assertParseFailure(this.parser, "0x1A",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
        CommandParserTestUtil.assertParseFailure(this.parser, "0xFF",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_binaryInput_throwsParseException() {
        CommandParserTestUtil.assertParseFailure(this.parser, "0b1010",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_htmlTags_throwsParseException() {
        CommandParserTestUtil.assertParseFailure(this.parser, "<contacts>",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
        CommandParserTestUtil.assertParseFailure(this.parser, "<script>alert('xss')</script>",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_sqlInjectionPatterns_throwsParseException() {
        CommandParserTestUtil.assertParseFailure(this.parser, "'; DROP TABLE--",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
        CommandParserTestUtil.assertParseFailure(this.parser, "' OR '1'='1",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_veryManySpaces_succeeds() {
        // Trailing spaces are trimmed by trim(), so "contacts" + many spaces becomes
        // just "contacts"
        String manySpaces = "contacts" + " ".repeat(100);
        ListCommand expectedListCommand = new ListContactCommand();
        CommandParserTestUtil.assertParseSuccess(this.parser, manySpaces, expectedListCommand);
    }

    @Test
    public void parse_carriageReturnCharacter_treatAsWhitespace() {
        ListCommand expectedListCommand = new ListContactCommand();
        CommandParserTestUtil.assertParseSuccess(this.parser, "\rcontacts\r", expectedListCommand);
    }

    @Test
    public void parse_formFeedAndVerticalTab_treatAsWhitespace() {
        ListCommand expectedListCommand = new ListAssignmentCommand();
        CommandParserTestUtil.assertParseSuccess(this.parser, "\fassignments\u000B", expectedListCommand);
    }

    @Test
    public void parse_zeroWidthSpace_throwsParseException() {
        // Zero-width space (U+200B) is NOT trimmed by Java's trim(), so exact match
        // fails
        CommandParserTestUtil.assertParseFailure(this.parser, "\u200Bcontacts\u200B",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_rightToLeftOverride_throwsParseException() {
        // Right-to-left override character
        CommandParserTestUtil.assertParseFailure(this.parser, "\u202Estnactnoc",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_byteOrderMark_throwsParseException() {
        // Byte-order mark (U+FEFF) is NOT trimmed by Java's trim(), so exact match
        // fails
        CommandParserTestUtil.assertParseFailure(this.parser, "\uFEFFcontacts",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_partiallySpelledWords_throwsParseException() {
        CommandParserTestUtil.assertParseFailure(this.parser, "cont",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
        CommandParserTestUtil.assertParseFailure(this.parser, "assign",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
        CommandParserTestUtil.assertParseFailure(this.parser, "cla",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_almostCorrectSpelling_throwsParseException() {
        CommandParserTestUtil.assertParseFailure(this.parser, "contactz",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
        CommandParserTestUtil.assertParseFailure(this.parser, "assignmunts",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
        CommandParserTestUtil.assertParseFailure(this.parser, "classez",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_doubleVowels_throwsParseException() {
        CommandParserTestUtil.assertParseFailure(this.parser, "coontacts",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
        CommandParserTestUtil.assertParseFailure(this.parser, "aassiignmeents",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_missingVowels_throwsParseException() {
        CommandParserTestUtil.assertParseFailure(this.parser, "cntcts",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
        CommandParserTestUtil.assertParseFailure(this.parser, "sgnmnts",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_camelCase_caseInsensitive() {
        // camelCase should work with case-insensitive parsing
        ListCommand expectedListCommand = new ListContactCommand();
        CommandParserTestUtil.assertParseSuccess(this.parser, "ContactS", expectedListCommand);
    }

    @Test
    public void parse_alternatingCasePattern_works() {
        ListCommand expectedListCommand = new ListContactCommand();
        CommandParserTestUtil.assertParseSuccess(this.parser, "CoNtAcTs", expectedListCommand);
    }
}
