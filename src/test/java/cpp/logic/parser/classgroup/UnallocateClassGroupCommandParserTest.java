package cpp.logic.parser.classgroup;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import cpp.logic.Messages;
import cpp.logic.commands.CommandTestUtil;
import cpp.logic.commands.classgroup.UnallocateClassGroupCommand;
import cpp.logic.parser.CommandParserTestUtil;
import cpp.logic.parser.ParserUtil;
import cpp.model.classgroup.ClassGroup;
import cpp.model.classgroup.ClassGroupName;
import cpp.testutil.ClassGroupBuilder;
import cpp.testutil.ClassGroupUtil;
import cpp.testutil.TypicalClassGroups;
import cpp.testutil.TypicalIndexes;

public class UnallocateClassGroupCommandParserTest {

    private UnallocateClassGroupCommandParser parser = new UnallocateClassGroupCommandParser();

    @Test
    public void parse_allFieldsPresent_success() throws Exception {
        ClassGroup sampleClassGroup = new ClassGroupBuilder(TypicalClassGroups.CLASS_GROUP_ONE).build();
        // whitespace only preamble
        CommandParserTestUtil.assertParseSuccess(this.parser,
                CommandTestUtil.PREAMBLE_WHITESPACE + ClassGroupUtil.getClassGroupDetails(sampleClassGroup)
                        + CommandTestUtil.CONTACT_INDICES_MULTIPLE,
                new UnallocateClassGroupCommand(sampleClassGroup.getName(), new ArrayList<>(Arrays.asList(
                        TypicalIndexes.INDEX_FIRST_CONTACT, TypicalIndexes.INDEX_SECOND_CONTACT,
                        TypicalIndexes.INDEX_THIRD_CONTACT))));
    }

    @Test
    public void parse_compulsoryFieldMissing_failure() {
        String expectedMessage = String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT,
                UnallocateClassGroupCommand.MESSAGE_USAGE);

        // missing name prefix
        CommandParserTestUtil.assertParseFailure(this.parser,
                TypicalClassGroups.CLASS_GROUP_ONE.getName().fullName + CommandTestUtil.CONTACT_INDICES_MULTIPLE,
                expectedMessage);

        // missing contact prefix
        CommandParserTestUtil.assertParseFailure(this.parser,
                CommandTestUtil.PREAMBLE_WHITESPACE
                        + ClassGroupUtil.getClassGroupDetails(TypicalClassGroups.CLASS_GROUP_ONE)
                        + String.format(" %s", CommandTestUtil.VALID_CONTACT_INDICES),
                expectedMessage);

        // non-empty preamble
        CommandParserTestUtil.assertParseFailure(this.parser,
                CommandTestUtil.PREAMBLE_NON_EMPTY, expectedMessage);
    }

    @Test
    public void parse_invalidValue_failure() {
        // invalid name
        CommandParserTestUtil.assertParseFailure(this.parser,
                CommandTestUtil.INVALID_CLASS_NAME_DESC + CommandTestUtil.CONTACT_INDICES_MULTIPLE,
                ClassGroupName.MESSAGE_CONSTRAINTS);

        // invalid contact indices
        CommandParserTestUtil.assertParseFailure(this.parser,
                ClassGroupUtil.getClassGroupDetails(TypicalClassGroups.CLASS_GROUP_ONE)
                        + CommandTestUtil.INVALID_CONTACT_INDICES_DESC,
                ParserUtil.MESSAGE_INVALID_INDEX);
    }
}
