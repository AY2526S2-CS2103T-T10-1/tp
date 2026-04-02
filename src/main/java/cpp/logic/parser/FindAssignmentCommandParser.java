package cpp.logic.parser;

import java.time.LocalDateTime;

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
     * FindAssignmentCommand and returns a FindAssignmentCommand object for
     * execution.
     *
     * Supports finding by name (ass/ASSIGNMENT_NAME_SUBSTRING) or deadline
     * (d/DEADLINE)
     * Examples: findass CS2103 project
     * findass d/31-12-2024
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    @Override
    public FindAssignmentCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.untrimmedTokenize(args, CliSyntax.PREFIX_ASSIGNMENT,
                CliSyntax.PREFIX_DATETIME);

        argMultimap.verifyNoDuplicatePrefixesFor(CliSyntax.PREFIX_ASSIGNMENT, CliSyntax.PREFIX_DATETIME);

        AssignmentSearchPredicate predicate;

        boolean hasAssignmentPrefix = argMultimap.getValue(CliSyntax.PREFIX_ASSIGNMENT).isPresent();
        boolean hasDatetimePrefix = argMultimap.getValue(CliSyntax.PREFIX_DATETIME).isPresent();

        // Check for conflicting prefixes
        int prefixCount = (hasAssignmentPrefix ? 1 : 0) + (hasDatetimePrefix ? 1 : 0);
        if (prefixCount > 1) {
            throw new ParseException(
                    String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, FindAssignmentCommand.MESSAGE_USAGE));
        }

        if (!argMultimap.getPreamble().trim().isEmpty()) {
            throw new ParseException(
                    String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, FindAssignmentCommand.MESSAGE_USAGE));
        }

        if (hasDatetimePrefix) {
            String deadlineValue = argMultimap.getValue(CliSyntax.PREFIX_DATETIME).get().trim()
                    .replaceAll("\\s+", " ");
            boolean isDateOnly = !deadlineValue.contains(" ");
            LocalDateTime deadline = this.parseDeadlineMatcher(deadlineValue);
            predicate = new AssignmentDeadlineContainsKeywordPredicate(deadline, isDateOnly);
        } else if (hasAssignmentPrefix) {
            String assignmentSubstring = argMultimap.getValue(CliSyntax.PREFIX_ASSIGNMENT).get().replaceAll("\\s+",
                    " ");
            ParserUtil.parseAssignmentName(assignmentSubstring);
            predicate = new AssignmentNameContainsKeywordsPredicate(assignmentSubstring);
        } else {
            throw new ParseException(
                    String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, FindAssignmentCommand.MESSAGE_USAGE));
        }

        return new FindAssignmentCommand(predicate);
    }

    /**
     * Parses the deadline value and validates that it conforms to one of the
     * supported formats:
     * dd-MM-yyyy or dd-MM-yyyy HH:mm
     *
     * @param deadlineValue the deadline value to parse and validate
     * @throws ParseException if the deadline format is invalid
     */
    private LocalDateTime parseDeadlineMatcher(String deadlineValue) throws ParseException {
        LocalDateTime deadline;

        try {
            deadline = ParserUtil.parseDeadline(deadlineValue);
        } catch (ParseException e1) {
            // Try parsing as date-only format (dd-MM-yyyy)
            deadline = ParserUtil.parseDeadline(deadlineValue + " 00:00");
        }

        return deadline;
    }

}
