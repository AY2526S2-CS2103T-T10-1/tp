package cpp.logic.parser;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import cpp.logic.Messages;
import cpp.logic.commands.FindContactCommand;
import cpp.model.contact.ContactNameContainsKeywordsPredicate;

public class FindContactCommandParserTest {

    private FindContactCommandParser parser = new FindContactCommandParser();

    @Test
    public void parse_emptyArg_throwsParseException() {
        CommandParserTestUtil.assertParseFailure(this.parser, "     ",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, FindContactCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_validArgs_returnsFindCommand() {
        // no trailing whitespaces
        FindContactCommand expectedFindCommand = new FindContactCommand(
                new ContactNameContainsKeywordsPredicate(Arrays.asList("Alice", "Bob")));
        CommandParserTestUtil.assertParseSuccess(this.parser, " n/Alice Bob", expectedFindCommand);

        // multiple whitespaces between keywords
        CommandParserTestUtil.assertParseSuccess(this.parser, " \n n/Alice \n \t Bob  \t", expectedFindCommand);
    }

    @Test
    public void parse_unrecognizedPrefix_throwsParseException() {
        // unrecognized prefixes in preamble should throw error
        CommandParserTestUtil.assertParseFailure(this.parser, "c/Contact",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, FindContactCommand.MESSAGE_USAGE));

        CommandParserTestUtil.assertParseFailure(this.parser, "ass/Assignment",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, FindContactCommand.MESSAGE_USAGE));
    }

}
