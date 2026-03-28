package cpp.logic.parser;

import cpp.logic.Messages;
import cpp.logic.commands.FindAssignmentCommand;
import cpp.logic.parser.exceptions.ParseException;
import cpp.model.assignment.AssignmentNameContainsKeywordsPredicate;

/**
 * Parses input arguments and creates a new FindAssignmentCommand object
 */
public class FindAssignmentCommandParser implements Parser<FindAssignmentCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the
     * FindAssignmentCommand
     * and returns a FindAssignmentCommand object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    @Override
    public FindAssignmentCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim().replaceAll("\\s+", " ");
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(
                    String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, FindAssignmentCommand.MESSAGE_USAGE));
        }

        return new FindAssignmentCommand(new AssignmentNameContainsKeywordsPredicate(trimmedArgs));
    }

}
