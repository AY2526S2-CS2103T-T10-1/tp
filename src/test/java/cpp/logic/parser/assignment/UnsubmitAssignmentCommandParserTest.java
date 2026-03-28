package cpp.logic.parser.assignment;

import java.util.List;

import org.junit.jupiter.api.Test;

import cpp.logic.Messages;
import cpp.logic.commands.CommandTestUtil;
import cpp.logic.commands.assignment.UnsubmitAssignmentCommand;
import cpp.logic.parser.CliSyntax;
import cpp.logic.parser.CommandParserTestUtil;
import cpp.logic.parser.ParserUtil;
import cpp.model.assignment.Assignment;
import cpp.model.assignment.AssignmentName;
import cpp.testutil.AssignmentBuilder;
import cpp.testutil.TypicalAssignments;
import cpp.testutil.TypicalClassGroups;

public class UnsubmitAssignmentCommandParserTest {

    private UnsubmitAssignmentCommandParser parser = new UnsubmitAssignmentCommandParser();

    @Test
    public void parse_allFieldsPresent_success() throws Exception {
        Assignment expectedAssignment = new AssignmentBuilder(TypicalAssignments.ASSIGNMENT_ONE).build();

        // with contact indices
        String userInput = CommandTestUtil.PREAMBLE_WHITESPACE + " " + CliSyntax.PREFIX_ASSIGNMENT
                + expectedAssignment.getName().fullName + " " + CommandTestUtil.CONTACT_INDICES_MULTIPLE;

        CommandParserTestUtil.assertParseSuccess(this.parser, userInput,
                new UnsubmitAssignmentCommand(expectedAssignment.getName(),
                        ParserUtil.parseContactIndices(CommandTestUtil.VALID_CONTACT_INDICES)));

        // with class group
        String userInputWithClass = CommandTestUtil.PREAMBLE_WHITESPACE + " " + CliSyntax.PREFIX_ASSIGNMENT
                + expectedAssignment.getName().fullName + " " + CliSyntax.PREFIX_CLASS
                + TypicalClassGroups.CLASS_GROUP_ONE.getName().fullName;

        CommandParserTestUtil.assertParseSuccess(this.parser, userInputWithClass,
                new UnsubmitAssignmentCommand(expectedAssignment.getName(), List.of(),
                        TypicalClassGroups.CLASS_GROUP_ONE.getName()));
    }

    @Test
    public void parse_compulsoryFieldMissing_failure() {
        String expectedMessage = String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT,
                UnsubmitAssignmentCommand.MESSAGE_USAGE);

        // missing assignment prefix
        CommandParserTestUtil.assertParseFailure(this.parser,
                TypicalAssignments.ASSIGNMENT_ONE.getName().fullName + "",
                expectedMessage);

        // missing both contact and class
        CommandParserTestUtil.assertParseFailure(this.parser,
                CommandTestUtil.PREAMBLE_WHITESPACE + " " + CliSyntax.PREFIX_ASSIGNMENT
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
    public void parse_invalidValue_failure() {
        // invalid assignment name
        String invalidAssignment = CommandTestUtil.PREAMBLE_WHITESPACE + " " + CliSyntax.PREFIX_ASSIGNMENT
                + "ass@ssignment1 " + CliSyntax.PREFIX_CONTACT + "1";
        CommandParserTestUtil.assertParseFailure(this.parser, invalidAssignment,
                AssignmentName.MESSAGE_CONSTRAINTS);

        // two invalid values -> should fail on assignment name first
        String twoInvalid = CommandTestUtil.PREAMBLE_WHITESPACE + " " + CliSyntax.PREFIX_ASSIGNMENT
                + "ass@ssignment1 " + CliSyntax.PREFIX_CLASS + "CS2103T&";
        CommandParserTestUtil.assertParseFailure(this.parser, twoInvalid, AssignmentName.MESSAGE_CONSTRAINTS);
    }

}
