package seedu.address.logic.parser;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.RemarkCommand;
import seedu.address.testutil.TypicalIndexes;

public class RemarkCommandParserTest {
    private RemarkCommandParser parser = new RemarkCommandParser();
    private final String nonEmptyRemark = "Some remark.";

    @Test
    public void parse_indexSpecified_success() {
        // have remark
        Index targetIndex = TypicalIndexes.INDEX_FIRST_PERSON;
        String userInput = targetIndex.getOneBased() + " " + CliSyntax.PREFIX_REMARK + this.nonEmptyRemark;
        RemarkCommand expectedCommand = new RemarkCommand(TypicalIndexes.INDEX_FIRST_PERSON, this.nonEmptyRemark);
        CommandParserTestUtil.assertParseSuccess(this.parser, userInput, expectedCommand);

        // no remark
        userInput = targetIndex.getOneBased() + " " + CliSyntax.PREFIX_REMARK;
        expectedCommand = new RemarkCommand(TypicalIndexes.INDEX_FIRST_PERSON, "");
        CommandParserTestUtil.assertParseSuccess(this.parser, userInput, expectedCommand);
    }

    @Test
    public void parse_missingCompulsoryField_failure() {
        String expectedMessage = String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, RemarkCommand.MESSAGE_USAGE);

        // no parameters
        CommandParserTestUtil.assertParseFailure(this.parser, RemarkCommand.COMMAND_WORD, expectedMessage);

        // no index
        CommandParserTestUtil.assertParseFailure(this.parser, RemarkCommand.COMMAND_WORD + " " + this.nonEmptyRemark,
                expectedMessage);
    }
}
