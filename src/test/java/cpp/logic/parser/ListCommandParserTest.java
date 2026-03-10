package cpp.logic.parser;

import org.junit.jupiter.api.Test;

import cpp.logic.commands.ListAssignmentCommand;
import cpp.logic.commands.ListClassCommand;
import cpp.logic.commands.ListCommand;
import cpp.logic.commands.ListContactCommand;

public class ListCommandParserTest {
    private ListCommandParser parser = new ListCommandParser();

    @Test
    public void parse_emptyArgs_throwsParseException() {
        CommandParserTestUtil.assertParseFailure(this.parser, "     ", ListCommand.MESSAGE_TAB_EMPTY);
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
    public void parse_classesArgs_returnsListCommand() {
        ListCommand expectedListCommand = new ListClassCommand();
        CommandParserTestUtil.assertParseSuccess(this.parser, "classes", expectedListCommand);
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        CommandParserTestUtil.assertParseFailure(this.parser, "invalid", ListCommand.MESSAGE_TAB_INVALID);
    }
}
