package cpp.logic.parser.assignment;

import org.junit.jupiter.api.Test;

import cpp.logic.Messages;
import cpp.logic.commands.CommandTestUtil;
import cpp.logic.commands.assignment.AddAssignmentCommand;
import cpp.logic.parser.CommandParserTestUtil;
import cpp.model.assignment.Assignment;
import cpp.model.assignment.AssignmentName;
import cpp.testutil.AssignmentBuilder;
import cpp.testutil.AssignmentUtil;
import cpp.testutil.TypicalAssignments;

public class AddAssignmentCommandParserTest {

    private AddAssignmentCommandParser parser = new AddAssignmentCommandParser();

    @Test
    public void parse_allFieldsPresent_success() throws Exception {
        Assignment expectedAssignment = new AssignmentBuilder(TypicalAssignments.ASSIGNMENT_ONE).build();
        // whitespace only preamble
        CommandParserTestUtil.assertParseSuccess(this.parser,
                CommandTestUtil.PREAMBLE_WHITESPACE + AssignmentUtil.getAssignmentDetails(expectedAssignment),
                new AddAssignmentCommand(expectedAssignment));
    }

    @Test
    public void parse_compulsoryFieldMissing_failure() {
        String expectedMessage = String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT,
                AddAssignmentCommand.MESSAGE_USAGE);

        // missing name prefix
        CommandParserTestUtil.assertParseFailure(this.parser,
                TypicalAssignments.ASSIGNMENT_ONE.getName().fullName + " " + "d/13-12-2020 10:00",
                expectedMessage);

        // missing deadline prefix
        CommandParserTestUtil.assertParseFailure(this.parser,
                "n/" + TypicalAssignments.ASSIGNMENT_ONE.getName().fullName + " " + "13-12-2020 10:00",
                expectedMessage);

        // all prefixes missing
        CommandParserTestUtil.assertParseFailure(this.parser,
                TypicalAssignments.ASSIGNMENT_ONE.getName().fullName + " " + "13-12-2020 10:00",
                expectedMessage);

        // non-empty preamble
        CommandParserTestUtil.assertParseFailure(this.parser,
                CommandTestUtil.PREAMBLE_NON_EMPTY, expectedMessage);
    }

    @Test
    public void parse_invalidValue_failure() {
        // invalid name
        CommandParserTestUtil.assertParseFailure(this.parser,
                " ass/@ssignment 1 d/13-12-2020 10:00",
                AssignmentName.MESSAGE_CONSTRAINTS);

        // invalid deadline
        String invalidDeadlineDetails = AssignmentUtil.getAssignmentDetails(TypicalAssignments.ASSIGNMENT_ONE)
                .replace("13-12-2020 10:00", "12-13-2020 10:00");
        CommandParserTestUtil.assertParseFailure(this.parser,
                invalidDeadlineDetails,
                "Invalid date and time format. Please use the format: dd-MM-yyyy HH:mm");

        // two invalid values
        CommandParserTestUtil.assertParseFailure(this.parser,
                " ass/@ssignment 1 d/12-13-2020 10:00",
                AssignmentName.MESSAGE_CONSTRAINTS);
    }

}
