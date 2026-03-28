package cpp.logic.parser.assignment;

import org.junit.jupiter.api.Test;

import cpp.commons.core.index.Index;
import cpp.logic.Messages;
import cpp.logic.commands.assignment.EditAssignmentCommand;
import cpp.logic.commands.assignment.EditAssignmentCommand.EditAssignmentDescriptor;
import cpp.logic.parser.CliSyntax;
import cpp.logic.parser.CommandParserTestUtil;
import cpp.model.assignment.AssignmentName;
import cpp.testutil.TypicalIndexes;

public class EditAssignmentCommandParserTest {

    private EditAssignmentCommandParser parser = new EditAssignmentCommandParser();

    @Test
    public void parse_nameFieldPresent_success() {
        Index targetIndex = TypicalIndexes.INDEX_FIRST_CONTACT;
        String newName = "New Assignment";
        String userInput = targetIndex.getOneBased() + " " + CliSyntax.PREFIX_ASSIGNMENT + newName;

        EditAssignmentDescriptor descriptor = new EditAssignmentDescriptor();
        descriptor.setName(new AssignmentName(newName));
        EditAssignmentCommand expectedCommand = new EditAssignmentCommand(targetIndex, descriptor);

        CommandParserTestUtil.assertParseSuccess(this.parser, userInput, expectedCommand);
    }

    @Test
    public void parse_deadlineFieldPresent_success() {
        Index targetIndex = TypicalIndexes.INDEX_FIRST_CONTACT;
        String deadline = "01-01-2025 23:59";
        String userInput = targetIndex.getOneBased() + " " + CliSyntax.PREFIX_DATETIME + deadline;

        EditAssignmentDescriptor descriptor = new EditAssignmentDescriptor();
        try {
            descriptor.setDeadline(cpp.logic.parser.ParserUtil.parseDeadline(deadline));
        } catch (cpp.logic.parser.exceptions.ParseException e) {
            throw new AssertionError("Deadline should be valid", e);
        }
        EditAssignmentCommand expectedCommand = new EditAssignmentCommand(targetIndex, descriptor);

        CommandParserTestUtil.assertParseSuccess(this.parser, userInput, expectedCommand);
    }

    @Test
    public void parse_missingIndex_failure() {
        String expectedMessage = String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT,
                EditAssignmentCommand.MESSAGE_USAGE);

        CommandParserTestUtil.assertParseFailure(this.parser,
                " " + CliSyntax.PREFIX_ASSIGNMENT + "Assignment 1", expectedMessage);
    }

    @Test
    public void parse_noFieldEdited_failure() {
        CommandParserTestUtil.assertParseFailure(this.parser, "1",
                EditAssignmentCommand.MESSAGE_NOT_EDITED);
    }
}
