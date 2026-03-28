package cpp.logic.parser;

import java.util.Arrays;

import cpp.logic.Messages;
import cpp.logic.commands.FindClassCommand;
import cpp.logic.parser.exceptions.ParseException;
import cpp.model.classgroup.ClassNameContainsKeywordsPredicate;

/**
 * Parses input arguments and creates a new FindClassCommand object
 */
public class FindClassCommandParser implements Parser<FindClassCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the
     * FindClassCommand
     * and returns a FindClassCommand object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    @Override
    public FindClassCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(
                    String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, FindClassCommand.MESSAGE_USAGE));
        }

        String[] nameKeywords = trimmedArgs.split("\\s+");

        return new FindClassCommand(new ClassNameContainsKeywordsPredicate(Arrays.asList(nameKeywords)));
    }

}
