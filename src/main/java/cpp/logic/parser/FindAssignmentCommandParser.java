package cpp.logic.parser;

import cpp.logic.Messages;
import cpp.logic.commands.FindAssignmentCommand;
import cpp.logic.parser.exceptions.ParseException;
import cpp.model.assignment.AssignmentDeadlineContainsKeywordPredicate;
import cpp.model.assignment.AssignmentNameContainsKeywordsPredicate;
import cpp.model.assignment.AssignmentSearchPredicate;

/**
 * Parses input arguments and creates a new FindAssignmentCommand object
 */
public class FindAssignmentCommandParser implements Parser<FindAssignmentCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the
     * FindAssignmentCommand
     * and returns a FindAssignmentCommand object for execution.
     *
     * Supports finding by name (default) or deadline (d/DEADLINE)
     * Examples: findass CS2103 project
     * findass d/31-12-2024
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    @Override
    public FindAssignmentCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, CliSyntax.PREFIX_DATETIME);

        argMultimap.verifyNoDuplicatePrefixesFor(CliSyntax.PREFIX_DATETIME);

        AssignmentSearchPredicate predicate;
        String preamble = argMultimap.getPreamble().trim();

        if (argMultimap.getValue(CliSyntax.PREFIX_DATETIME).isPresent()) {
            // If using d/ prefix, no other text should be present
            if (!preamble.isEmpty()) {
                throw new ParseException(
                        String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, FindAssignmentCommand.MESSAGE_USAGE));
            }
            String deadlineValue = argMultimap.getValue(CliSyntax.PREFIX_DATETIME).get().trim();
            if (deadlineValue.isEmpty()) {
                throw new ParseException(
                        String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, FindAssignmentCommand.MESSAGE_USAGE));
            }
            predicate = new AssignmentDeadlineContainsKeywordPredicate(deadlineValue);
        } else {
            // Default to name search using preamble
            if (preamble.isEmpty()) {
                throw new ParseException(
                        String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, FindAssignmentCommand.MESSAGE_USAGE));
            }
            // Reject unrecognized prefixes (e.g., p/, e/, c/, etc.)
            if (preamble.contains("/")) {
                throw new ParseException(
                        String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, FindAssignmentCommand.MESSAGE_USAGE));
            }
            predicate = new AssignmentNameContainsKeywordsPredicate(preamble);
        }

        return new FindAssignmentCommand(predicate);
    }

}
