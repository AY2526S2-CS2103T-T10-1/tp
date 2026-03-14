package cpp.logic.parser;

import java.util.List;

import org.junit.jupiter.api.Test;

import cpp.logic.Messages;
import cpp.logic.commands.DeleteCommand;
import cpp.logic.commands.DeleteContactCommand;
import cpp.logic.commands.assignment.DeleteAssignmentCommand;
import cpp.model.assignment.AssignmentName;
import cpp.testutil.TypicalIndexes;

public class DeleteCommandParserTest {

    private DeleteCommandParser parser = new DeleteCommandParser();

    @Test
    public void parse_validContactArgs_returnsDeleteCommand() {
        CommandParserTestUtil.assertParseSuccess(this.parser, " ct/1",
                new DeleteContactCommand(List.of(TypicalIndexes.INDEX_FIRST_CONTACT)));
    }

    @Test
    public void parse_validMultipleContactArgs_returnsDeleteCommand() {
        CommandParserTestUtil.assertParseSuccess(this.parser, " ct/1 2",
                new DeleteContactCommand(List.of(TypicalIndexes.INDEX_FIRST_CONTACT,
                        TypicalIndexes.INDEX_SECOND_CONTACT)));
    }

    @Test
    public void parse_validAssignmentArgs_returnsDeleteAssignmentCommand() {
        CommandParserTestUtil.assertParseSuccess(this.parser, " ass/Assignment 1",
                new DeleteAssignmentCommand(new AssignmentName("Assignment 1")));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        CommandParserTestUtil.assertParseFailure(this.parser, "1",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_invalidContactIndex_throwsParseException() {
        CommandParserTestUtil.assertParseFailure(this.parser, " ct/abc",
                ParserUtil.MESSAGE_INVALID_INDEX);
    }

    @Test
    public void parse_bothPrefixes_throwsParseException() {
        CommandParserTestUtil.assertParseFailure(this.parser, " ct/1 ass/Assignment 1",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_emptyContactIndices_throwsParseException() {
        CommandParserTestUtil.assertParseFailure(this.parser, " ct/",
                ParserUtil.MESSAGE_EMPTY_INDICES);
    }
}
