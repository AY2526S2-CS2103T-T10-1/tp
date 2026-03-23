package cpp.logic.parser.assignment;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import cpp.logic.Messages;
import cpp.logic.commands.CommandTestUtil;
import cpp.logic.commands.assignment.UnallocateAssignmentCommand;
import cpp.logic.parser.CliSyntax;
import cpp.logic.parser.CommandParserTestUtil;
import cpp.logic.parser.ParserUtil;
import cpp.model.assignment.Assignment;
import cpp.model.assignment.AssignmentName;
import cpp.model.classgroup.ClassGroupName;
import cpp.testutil.AssignmentBuilder;
import cpp.testutil.TypicalAssignments;
import cpp.testutil.TypicalClassGroups;
import cpp.testutil.TypicalIndexes;

public class UnallocateAssignmentCommandParserTest {

    private UnallocateAssignmentCommandParser parser = new UnallocateAssignmentCommandParser();

    @Test
    public void parse_allFieldsPresent_success() throws Exception {
        Assignment sampleAssignment = new AssignmentBuilder(TypicalAssignments.ASSIGNMENT_ONE).build();
        // whitespace only preamble
        CommandParserTestUtil.assertParseSuccess(this.parser,
                CommandTestUtil.PREAMBLE_WHITESPACE + " " + CliSyntax.PREFIX_ASSIGNMENT
                        + sampleAssignment.getName().fullName + CommandTestUtil.CONTACT_INDICES_MULTIPLE,
                new UnallocateAssignmentCommand(sampleAssignment.getName(), new ArrayList<>(Arrays.asList(
                        TypicalIndexes.INDEX_FIRST_CONTACT, TypicalIndexes.INDEX_SECOND_CONTACT,
                        TypicalIndexes.INDEX_THIRD_CONTACT))));

        // with class group
        CommandParserTestUtil.assertParseSuccess(this.parser,
                CommandTestUtil.PREAMBLE_WHITESPACE + " " + CliSyntax.PREFIX_ASSIGNMENT
                        + sampleAssignment.getName().fullName + " " + CliSyntax.PREFIX_CLASS
                        + TypicalClassGroups.CLASS_GROUP_ONE.getName().fullName,
                new UnallocateAssignmentCommand(sampleAssignment.getName(), new ArrayList<>(),
                        TypicalClassGroups.CLASS_GROUP_ONE.getName()));
    }

    @Test
    public void parse_compulsoryFieldMissing_failure() {
        String expectedMessage = String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT,
                UnallocateAssignmentCommand.MESSAGE_USAGE);

        // missing assignment prefix
        CommandParserTestUtil.assertParseFailure(this.parser,
                TypicalAssignments.ASSIGNMENT_ONE.getName().fullName + CommandTestUtil.CONTACT_INDICES_MULTIPLE,
                expectedMessage);

        // missing contact prefix
        CommandParserTestUtil.assertParseFailure(this.parser,
                CommandTestUtil.PREAMBLE_WHITESPACE
                        + " " + CliSyntax.PREFIX_ASSIGNMENT + TypicalAssignments.ASSIGNMENT_ONE.getName().fullName
                        + String.format(" %s", CommandTestUtil.VALID_CONTACT_INDICES),
                expectedMessage);

        // missing class group prefix
        CommandParserTestUtil.assertParseFailure(this.parser,
                CommandTestUtil.PREAMBLE_WHITESPACE
                        + " " + CliSyntax.PREFIX_ASSIGNMENT + TypicalAssignments.ASSIGNMENT_ONE.getName().fullName
                        + String.format(" %s", CommandTestUtil.VALID_CLASS_NAME_CS2103T),
                expectedMessage);

        // non-empty preamble
        CommandParserTestUtil.assertParseFailure(this.parser,
                CommandTestUtil.PREAMBLE_NON_EMPTY, expectedMessage);
    }

    @Test
    public void parse_invalidValue_failure() {
        // invalid name
        CommandParserTestUtil.assertParseFailure(this.parser,
                " " + CliSyntax.PREFIX_ASSIGNMENT + "ass/@ssignment" + CommandTestUtil.CONTACT_INDICES_MULTIPLE,
                AssignmentName.MESSAGE_CONSTRAINTS);

        // invalid class name
        CommandParserTestUtil.assertParseFailure(this.parser,
                CommandTestUtil.PREAMBLE_WHITESPACE
                        + " " + CliSyntax.PREFIX_ASSIGNMENT + TypicalAssignments.ASSIGNMENT_ONE.getName().fullName
                        + CommandTestUtil.INVALID_CLASS_NAME_DESC,
                ClassGroupName.MESSAGE_CONSTRAINTS);

        // invalid contact indices
        CommandParserTestUtil.assertParseFailure(this.parser,
                " " + CliSyntax.PREFIX_ASSIGNMENT + TypicalAssignments.ASSIGNMENT_ONE.getName().fullName
                        + CommandTestUtil.INVALID_CONTACT_INDICES_DESC,
                ParserUtil.MESSAGE_INVALID_INDEX);
    }

}
