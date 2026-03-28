package cpp.logic.parser.assignment;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import cpp.logic.Messages;
import cpp.logic.commands.CommandTestUtil;
import cpp.logic.commands.assignment.SubmitAssignmentCommand;
import cpp.logic.parser.CliSyntax;
import cpp.logic.parser.CommandParserTestUtil;
import cpp.logic.parser.ParserUtil;
import cpp.model.assignment.Assignment;
import cpp.model.assignment.AssignmentName;
import cpp.testutil.AssignmentBuilder;
import cpp.testutil.TypicalAssignments;
import cpp.testutil.TypicalClassGroups;

public class SubmitAssignmentCommandParserTest {

    private SubmitAssignmentCommandParser parser = new SubmitAssignmentCommandParser();

    @Test
    public void parse_allFieldsPresent_success() throws Exception {
        Assignment expectedAssignment = new AssignmentBuilder(TypicalAssignments.ASSIGNMENT_ONE).build();

        // explicit contact indices and datetime
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime submissionDate = LocalDateTime.parse(now.format(ParserUtil.DATETIME_FORMATTER),
                ParserUtil.DATETIME_FORMATTER);

        String userInput = CommandTestUtil.PREAMBLE_WHITESPACE + " " + CliSyntax.PREFIX_ASSIGNMENT
                + expectedAssignment.getName().fullName + " " + CommandTestUtil.CONTACT_INDICES_MULTIPLE + " "
                + CliSyntax.PREFIX_DATETIME + submissionDate.format(ParserUtil.DATETIME_FORMATTER);

        CommandParserTestUtil.assertParseSuccess(this.parser, userInput,
                new SubmitAssignmentCommand(expectedAssignment.getName(),
                        ParserUtil.parseContactIndices(CommandTestUtil.VALID_CONTACT_INDICES), submissionDate));

        // with class group instead of contacts
        String userInputWithClass = CommandTestUtil.PREAMBLE_WHITESPACE + " " + CliSyntax.PREFIX_ASSIGNMENT
                + expectedAssignment.getName().fullName + " " + CliSyntax.PREFIX_CLASS
                + TypicalClassGroups.CLASS_GROUP_ONE.getName().fullName + " " + CliSyntax.PREFIX_DATETIME
                + submissionDate.format(ParserUtil.DATETIME_FORMATTER);

        CommandParserTestUtil.assertParseSuccess(this.parser, userInputWithClass,
                new SubmitAssignmentCommand(expectedAssignment.getName(), List.of(),
                        TypicalClassGroups.CLASS_GROUP_ONE.getName(), submissionDate));
    }

    @Test
    public void parse_compulsoryFieldMissing_failure() {
        String expectedMessage = String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT,
                SubmitAssignmentCommand.MESSAGE_USAGE);

        // missing assignment prefix
        CommandParserTestUtil.assertParseFailure(this.parser,
                TypicalAssignments.ASSIGNMENT_ONE.getName().fullName + " " + CliSyntax.PREFIX_DATETIME
                        + "21-02-2026 23:50",
                expectedMessage);

        // missing both contact and class
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
    public void parse_invalidValue_failure() {
        // invalid assignment name
        String invalidAssignment = CommandTestUtil.PREAMBLE_WHITESPACE + " " + CliSyntax.PREFIX_ASSIGNMENT
                + "ass@ssignment1 " + CliSyntax.PREFIX_CONTACT + "1";
        CommandParserTestUtil.assertParseFailure(this.parser, invalidAssignment,
                AssignmentName.MESSAGE_CONSTRAINTS);

        // invalid datetime
        String invalidDatetime = CommandTestUtil.PREAMBLE_WHITESPACE + " " + CliSyntax.PREFIX_ASSIGNMENT
                + TypicalAssignments.ASSIGNMENT_ONE.getName().fullName + " " + CommandTestUtil.CONTACT_INDICES_MULTIPLE
                + " " + CliSyntax.PREFIX_DATETIME + "12-13-2020 10:00";
        CommandParserTestUtil.assertParseFailure(this.parser, invalidDatetime,
                "Invalid date and time format. Please use the format: dd-MM-yyyy HH:mm");

        // two invalid values -> should fail on assignment name first
        String twoInvalid = CommandTestUtil.PREAMBLE_WHITESPACE + " " + CliSyntax.PREFIX_ASSIGNMENT
                + "ass@ssignment1 " + CliSyntax.PREFIX_CONTACT + " " + CliSyntax.PREFIX_DATETIME
                + "12-13-2020 10:00";
        CommandParserTestUtil.assertParseFailure(this.parser, twoInvalid, AssignmentName.MESSAGE_CONSTRAINTS);
    }

}
