package cpp.logic.parser;

import org.junit.jupiter.api.Test;

import cpp.logic.Messages;
import cpp.logic.commands.FindAssignmentCommand;
import cpp.model.assignment.AssignmentNameContainsKeywordsPredicate;

public class FindAssignmentCommandParserTest {

    private FindAssignmentCommandParser parser = new FindAssignmentCommandParser();

    @Test
    public void parse_emptyArg_throwsParseException() {
        CommandParserTestUtil.assertParseFailure(this.parser, "     ",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, FindAssignmentCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_validArgs_returnsFindCommand() {
        // no trailing whitespaces
        FindAssignmentCommand expectedFindCommand = new FindAssignmentCommand(
                new AssignmentNameContainsKeywordsPredicate("CS2103"));
        CommandParserTestUtil.assertParseSuccess(this.parser, " ass/CS2103", expectedFindCommand);

        // single search string
        CommandParserTestUtil.assertParseSuccess(this.parser, " ass/Assignment", new FindAssignmentCommand(
                new AssignmentNameContainsKeywordsPredicate("Assignment")));
    }

    @Test
    public void parse_deadlinePrefixWithPreamble_throwsParseException() {
        // deadline prefix should not have preamble (text before prefix)
        CommandParserTestUtil.assertParseFailure(this.parser, "CS2103 d/31-12-2024",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, FindAssignmentCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_emptyDeadlineValue_throwsParseException() {
        // empty deadline value should throw error
        CommandParserTestUtil.assertParseFailure(this.parser, " ds/",
                ParserUtil.MESSAGE_INVALID_DATE_OR_DATETIME);
        CommandParserTestUtil.assertParseFailure(this.parser, " de/",
                ParserUtil.MESSAGE_INVALID_DATE_OR_DATETIME);
    }

    @Test
    public void parse_unrecognizedPrefix_throwsParseException() {
        // unrecognized prefixes in preamble should throw error
        CommandParserTestUtil.assertParseFailure(this.parser, " p/Contact",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, FindAssignmentCommand.MESSAGE_USAGE));

        CommandParserTestUtil.assertParseFailure(this.parser, " e/email@test.com",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, FindAssignmentCommand.MESSAGE_USAGE));

        CommandParserTestUtil.assertParseFailure(this.parser, " c/ClassName",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, FindAssignmentCommand.MESSAGE_USAGE));
    }

}
