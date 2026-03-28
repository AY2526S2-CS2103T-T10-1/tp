package cpp.logic.parser;

import java.util.Arrays;

import cpp.logic.Messages;
import cpp.logic.commands.FindContactCommand;
import cpp.logic.parser.exceptions.ParseException;
import cpp.model.contact.ContactNameContainsKeywordsPredicate;

/**
 * Parses input arguments and creates a new FindContactCommand object
 */
public class FindContactCommandParser implements Parser<FindContactCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the
     * FindContactCommand
     * and returns a FindContactCommand object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    @Override
    public FindContactCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(
                    String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, FindContactCommand.MESSAGE_USAGE));
        }

        String[] nameKeywords = trimmedArgs.split("\\s+");

        return new FindContactCommand(new ContactNameContainsKeywordsPredicate(Arrays.asList(nameKeywords)));
    }

}
