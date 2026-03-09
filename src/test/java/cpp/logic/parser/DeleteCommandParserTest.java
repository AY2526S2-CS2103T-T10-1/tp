package cpp.logic.parser;

import org.junit.jupiter.api.Test;

import cpp.logic.Messages;
import cpp.logic.commands.DeleteCommand;
import cpp.logic.commands.assignment.DeleteAssignmentCommand;
import cpp.testutil.TypicalIndexes;

public class DeleteCommandParserTest {

    private DeleteCommandParser parser = new DeleteCommandParser();

    @Test
    public void parse_validContactArgs_returnsDeleteCommand() {
        CommandParserTestUtil.assertParseSuccess(this.parser, " ct/1",
                new DeleteCommand(TypicalIndexes.INDEX_FIRST_CONTACT));
    }

    @Test
    public void parse_validAssignmentArgs_returnsDeleteAssignmentCommand() {
        CommandParserTestUtil.assertParseSuccess(this.parser, " ass/1",
                new DeleteAssignmentCommand(TypicalIndexes.INDEX_FIRST_CONTACT));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        CommandParserTestUtil.assertParseFailure(this.parser, "1",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_invalidContactIndex_throwsParseException() {
        CommandParserTestUtil.assertParseFailure(this.parser, " ct/abc",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_invalidAssignmentIndex_throwsParseException() {
        CommandParserTestUtil.assertParseFailure(this.parser, " ass/abc",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, DeleteAssignmentCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_bothPrefixes_throwsParseException() {
        CommandParserTestUtil.assertParseFailure(this.parser, " ct/1 ass/1",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
    }
}
