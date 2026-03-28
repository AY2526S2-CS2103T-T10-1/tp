package cpp.logic.parser.assignment;

import java.util.List;

import org.junit.jupiter.api.Test;

import cpp.logic.Messages;
import cpp.logic.commands.CommandTestUtil;
import cpp.logic.commands.assignment.UngradeAssignmentCommand;
import cpp.logic.parser.CliSyntax;
import cpp.logic.parser.CommandParserTestUtil;
import cpp.logic.parser.ParserUtil;
import cpp.model.assignment.Assignment;
import cpp.model.assignment.AssignmentName;
import cpp.testutil.AssignmentBuilder;
import cpp.testutil.TypicalAssignments;

public class UngradeAssignmentCommandParserTest {

    private UngradeAssignmentCommandParser parser = new UngradeAssignmentCommandParser();

    @Test
    public void parse_allFieldsPresent_success() throws Exception {
        Assignment expectedAssignment = new AssignmentBuilder(TypicalAssignments.ASSIGNMENT_ONE).build();

        String userInput = CommandTestUtil.PREAMBLE_WHITESPACE + " " + CliSyntax.PREFIX_ASSIGNMENT
                + expectedAssignment.getName().fullName + " " + CommandTestUtil.CONTACT_INDICES_MULTIPLE;

        CommandParserTestUtil.assertParseSuccess(this.parser, userInput,
                new UngradeAssignmentCommand(expectedAssignment.getName(),
                        ParserUtil.parseContactIndices(CommandTestUtil.VALID_CONTACT_INDICES)));
    }

    @Test
    public void parse_classGroupPresent_success() throws Exception {
        Assignment expectedAssignment = new AssignmentBuilder(TypicalAssignments.ASSIGNMENT_ONE).build();

        String userInput = CommandTestUtil.PREAMBLE_WHITESPACE + " " + CliSyntax.PREFIX_ASSIGNMENT
                + expectedAssignment.getName().fullName + " " + CommandTestUtil.CLASS_NAME_DESC;

        CommandParserTestUtil.assertParseSuccess(this.parser, userInput,
                new UngradeAssignmentCommand(expectedAssignment.getName(), List.of(),
                        ParserUtil.parseClassGroupName(CommandTestUtil.VALID_CLASS_NAME_CS2103T)));
    }

    @Test
    public void parse_compulsoryFieldMissing_failure() {
        String expectedMessage = String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT,
                UngradeAssignmentCommand.MESSAGE_USAGE);

        // missing assignment prefix
        CommandParserTestUtil.assertParseFailure(this.parser, CommandTestUtil.CONTACT_INDICES_MULTIPLE,
                expectedMessage);

        // missing contact
        CommandParserTestUtil.assertParseFailure(this.parser,
                CommandTestUtil.PREAMBLE_WHITESPACE + CliSyntax.PREFIX_ASSIGNMENT
                        + TypicalAssignments.ASSIGNMENT_ONE.getName().fullName,
                expectedMessage);

        // non-empty preamble
        CommandParserTestUtil.assertParseFailure(this.parser,
                CommandTestUtil.PREAMBLE_NON_EMPTY + " " + CliSyntax.PREFIX_ASSIGNMENT
                        + TypicalAssignments.ASSIGNMENT_ONE.getName().fullName + " "
                        + CommandTestUtil.CONTACT_INDICES_MULTIPLE,
                expectedMessage);
    }

    @Test
    public void parse_invalidValue_failure() throws Exception {
        // invalid assignment name
        String invalidAssignment = CommandTestUtil.PREAMBLE_WHITESPACE + " " + CliSyntax.PREFIX_ASSIGNMENT
                + "ass@ssignment1 " + CliSyntax.PREFIX_CONTACT + "1";
        CommandParserTestUtil.assertParseFailure(this.parser, invalidAssignment, AssignmentName.MESSAGE_CONSTRAINTS);

        // invalid contact indices
        String invalidContacts = CommandTestUtil.PREAMBLE_WHITESPACE + " " + CliSyntax.PREFIX_ASSIGNMENT
                + TypicalAssignments.ASSIGNMENT_ONE.getName().fullName + " "
                + CommandTestUtil.INVALID_CONTACT_INDICES_DESC;
        CommandParserTestUtil.assertParseFailure(this.parser, invalidContacts, ParserUtil.MESSAGE_INVALID_INDEX);

        // two invalid values -> should fail on assignment name first
        String twoInvalid = CommandTestUtil.PREAMBLE_WHITESPACE + " " + CliSyntax.PREFIX_ASSIGNMENT
                + "ass@ssignment1 " + CliSyntax.PREFIX_CONTACT + "a 0";
        CommandParserTestUtil.assertParseFailure(this.parser, twoInvalid, AssignmentName.MESSAGE_CONSTRAINTS);
    }

}
