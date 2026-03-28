package cpp.logic.parser.classgroup;

import org.junit.jupiter.api.Test;

import cpp.commons.core.index.Index;
import cpp.logic.Messages;
import cpp.logic.commands.classgroup.EditClassGroupCommand;
import cpp.logic.parser.CliSyntax;
import cpp.logic.parser.CommandParserTestUtil;
import cpp.model.classgroup.ClassGroupName;
import cpp.testutil.TypicalIndexes;

public class EditClassGroupCommandParserTest {

    private EditClassGroupCommandParser parser = new EditClassGroupCommandParser();

    @Test
    public void parse_allFieldsPresent_success() {
        Index targetIndex = TypicalIndexes.INDEX_FIRST_CONTACT;
        String newName = "CS2103TNewName";
        String userInput = targetIndex.getOneBased() + " " + CliSyntax.PREFIX_CLASS + newName;

        EditClassGroupCommand expectedCommand = new EditClassGroupCommand(targetIndex, new ClassGroupName(newName));
        CommandParserTestUtil.assertParseSuccess(this.parser, userInput, expectedCommand);
    }

    @Test
    public void parse_missingIndex_failure() {
        String expectedMessage = String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT,
                EditClassGroupCommand.MESSAGE_USAGE);

        // no index
        CommandParserTestUtil.assertParseFailure(this.parser,
                " " + CliSyntax.PREFIX_CLASS + "CS2103T", expectedMessage);
    }

    @Test
    public void parse_missingClassPrefix_failure() {
        String expectedMessage = String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT,
                EditClassGroupCommand.MESSAGE_USAGE);

        // index present but no c/ prefix
        CommandParserTestUtil.assertParseFailure(this.parser, "1 CS2103T", expectedMessage);
    }

    @Test
    public void parse_duplicatePrefix_failure() {
        String expectedMessage = Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_CLASS);

        // duplicate c/ prefix
        CommandParserTestUtil.assertParseFailure(this.parser,
                "1 " + CliSyntax.PREFIX_CLASS + "CS2103T " + CliSyntax.PREFIX_CLASS + "CS2103T2",
                expectedMessage);
    }

    @Test
    public void parse_invalidName_failure() {
        // invalid class name (contains special characters)
        CommandParserTestUtil.assertParseFailure(this.parser,
                "1 " + CliSyntax.PREFIX_CLASS + "CS2103T&",
                ClassGroupName.MESSAGE_CONSTRAINTS);
    }
}
