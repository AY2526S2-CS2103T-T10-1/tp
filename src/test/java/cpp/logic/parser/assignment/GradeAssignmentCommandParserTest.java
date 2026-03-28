package cpp.logic.parser.assignment;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import cpp.logic.Messages;
import cpp.logic.commands.CommandTestUtil;
import cpp.logic.commands.assignment.GradeAssignmentCommand;
import cpp.logic.parser.CliSyntax;
import cpp.logic.parser.CommandParserTestUtil;
import cpp.logic.parser.ParserUtil;
import cpp.model.assignment.Assignment;
import cpp.model.assignment.AssignmentName;
import cpp.model.assignment.GradeInfo;
import cpp.testutil.AssignmentBuilder;
import cpp.testutil.TypicalAssignments;

public class GradeAssignmentCommandParserTest {

    private GradeAssignmentCommandParser parser = new GradeAssignmentCommandParser();

    @Test
    public void parse_allFieldsPresent_success() throws Exception {
        Assignment expectedAssignment = new AssignmentBuilder(TypicalAssignments.ASSIGNMENT_ONE).build();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime gradingDate = LocalDateTime.parse(now.format(ParserUtil.DATETIME_FORMATTER),
                ParserUtil.DATETIME_FORMATTER);

        String userInput = CommandTestUtil.PREAMBLE_WHITESPACE + " " + CliSyntax.PREFIX_ASSIGNMENT
                + expectedAssignment.getName().fullName + " " + CommandTestUtil.CONTACT_INDICES_MULTIPLE + " "
                + CliSyntax.PREFIX_SCORE + "85" + " " + CliSyntax.PREFIX_DATETIME
                + gradingDate.format(ParserUtil.DATETIME_FORMATTER);

        CommandParserTestUtil.assertParseSuccess(this.parser, userInput,
                new GradeAssignmentCommand(expectedAssignment.getName(),
                        ParserUtil.parseContactIndices(CommandTestUtil.VALID_CONTACT_INDICES), 85f, gradingDate));
    }

    @Test
    public void parse_classGroupPresent_success() throws Exception {
        Assignment expectedAssignment = new AssignmentBuilder(TypicalAssignments.ASSIGNMENT_ONE).build();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime gradingDate = LocalDateTime.parse(now.format(ParserUtil.DATETIME_FORMATTER),
                ParserUtil.DATETIME_FORMATTER);

        String userInput = CommandTestUtil.PREAMBLE_WHITESPACE + " " + CliSyntax.PREFIX_ASSIGNMENT
                + expectedAssignment.getName().fullName + " " + CommandTestUtil.CLASS_NAME_DESC + " "
                + CliSyntax.PREFIX_SCORE + "85" + " " + CliSyntax.PREFIX_DATETIME
                + gradingDate.format(ParserUtil.DATETIME_FORMATTER);

        CommandParserTestUtil.assertParseSuccess(this.parser, userInput,
                new GradeAssignmentCommand(expectedAssignment.getName(),
                        List.of(), ParserUtil.parseClassGroupName(CommandTestUtil.VALID_CLASS_NAME_CS2103T), 85f,
                        gradingDate));
    }

    @Test
    public void parse_compulsoryFieldMissing_failure() {
        String expectedMessage = String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT,
                GradeAssignmentCommand.MESSAGE_USAGE);

        // missing assignment prefix
        CommandParserTestUtil.assertParseFailure(this.parser,
                CommandTestUtil.CONTACT_INDICES_MULTIPLE + " " + CliSyntax.PREFIX_SCORE + "85",
                expectedMessage);

        // missing contact prefix
        CommandParserTestUtil.assertParseFailure(this.parser,
                CommandTestUtil.PREAMBLE_WHITESPACE + CliSyntax.PREFIX_ASSIGNMENT
                        + TypicalAssignments.ASSIGNMENT_ONE.getName().fullName + " " + CliSyntax.PREFIX_SCORE + "85",
                expectedMessage);

        // missing score prefix
        CommandParserTestUtil.assertParseFailure(this.parser,
                CommandTestUtil.PREAMBLE_WHITESPACE + CliSyntax.PREFIX_ASSIGNMENT
                        + TypicalAssignments.ASSIGNMENT_ONE.getName().fullName + " "
                        + CommandTestUtil.CONTACT_INDICES_MULTIPLE,
                expectedMessage);

        // non-empty preamble
        CommandParserTestUtil.assertParseFailure(this.parser,
                CommandTestUtil.PREAMBLE_NON_EMPTY + " " + CliSyntax.PREFIX_ASSIGNMENT
                        + TypicalAssignments.ASSIGNMENT_ONE.getName().fullName + " "
                        + CommandTestUtil.CONTACT_INDICES_MULTIPLE + " " + CliSyntax.PREFIX_SCORE + "85",
                expectedMessage);
    }

    @Test
    public void parse_invalidValue_failure() throws Exception {
        // invalid assignment name
        String invalidAssignment = CommandTestUtil.PREAMBLE_WHITESPACE + " " + CliSyntax.PREFIX_ASSIGNMENT
                + "ass@ssignment1 " + CliSyntax.PREFIX_CONTACT + "1 " + CliSyntax.PREFIX_SCORE + "85";
        CommandParserTestUtil.assertParseFailure(this.parser, invalidAssignment,
                AssignmentName.MESSAGE_CONSTRAINTS);

        // invalid score (non-numeric)
        String invalidScore = CommandTestUtil.PREAMBLE_WHITESPACE + " " + CliSyntax.PREFIX_ASSIGNMENT
                + TypicalAssignments.ASSIGNMENT_ONE.getName().fullName + " " + CommandTestUtil.CONTACT_INDICES_MULTIPLE
                + " " + CliSyntax.PREFIX_SCORE + "abc";
        CommandParserTestUtil.assertParseFailure(this.parser, invalidScore, GradeInfo.INVALID_SCORE_STRING);

        // invalid datetime
        String invalidDatetime = CommandTestUtil.PREAMBLE_WHITESPACE + " " + CliSyntax.PREFIX_ASSIGNMENT
                + TypicalAssignments.ASSIGNMENT_ONE.getName().fullName + " " + CommandTestUtil.CONTACT_INDICES_MULTIPLE
                + " " + CliSyntax.PREFIX_SCORE + "85" + " " + CliSyntax.PREFIX_DATETIME + "12-13-2020 10:00";
        CommandParserTestUtil.assertParseFailure(this.parser, invalidDatetime,
                "Invalid date and time format. Please use the format: dd-MM-yyyy HH:mm");

        // two invalid values -> should fail on assignment name first
        String twoInvalid = CommandTestUtil.PREAMBLE_WHITESPACE + " " + CliSyntax.PREFIX_ASSIGNMENT
                + "ass@ssignment1 " + CliSyntax.PREFIX_CONTACT + " " + CliSyntax.PREFIX_SCORE + "abc";
        CommandParserTestUtil.assertParseFailure(this.parser, twoInvalid, AssignmentName.MESSAGE_CONSTRAINTS);
    }

}
