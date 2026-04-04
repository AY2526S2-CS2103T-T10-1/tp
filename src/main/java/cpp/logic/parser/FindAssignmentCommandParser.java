package cpp.logic.parser;

import java.time.LocalDateTime;

import cpp.logic.Messages;
import cpp.logic.commands.FindAssignmentCommand;
import cpp.logic.parser.exceptions.ParseException;
import cpp.model.assignment.AssignmentDeadlineInRangePredicate;
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
     * Examples: findass ass/CS2103 project
     * findass d/31-12-2024
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    @Override
    public FindAssignmentCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.untrimmedTokenize(args, CliSyntax.PREFIX_ASSIGNMENT,
                CliSyntax.PREFIX_DATETIME_START, CliSyntax.PREFIX_DATETIME_END);

        argMultimap.verifyNoDuplicatePrefixesFor(CliSyntax.PREFIX_ASSIGNMENT, CliSyntax.PREFIX_DATETIME_START,
                CliSyntax.PREFIX_DATETIME_END);

        AssignmentSearchPredicate predicate;

        boolean hasAssignmentPrefix = argMultimap.getValue(CliSyntax.PREFIX_ASSIGNMENT).isPresent();
        boolean hasDatetimeStartPrefix = argMultimap.getValue(CliSyntax.PREFIX_DATETIME_START).isPresent();
        boolean hasDatetimeEndPrefix = argMultimap.getValue(CliSyntax.PREFIX_DATETIME_END).isPresent();

        // Check for conflicting prefixes
        int prefixCount = (hasAssignmentPrefix ? 1 : 0) + ((hasDatetimeStartPrefix || hasDatetimeEndPrefix) ? 1 : 0);
        if (prefixCount > 1) {
            throw new ParseException(
                    String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, FindAssignmentCommand.MESSAGE_USAGE));
        }

        if (!argMultimap.getPreamble().trim().isEmpty()) {
            throw new ParseException(
                    String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, FindAssignmentCommand.MESSAGE_USAGE));
        }

        if (hasDatetimeStartPrefix && hasDatetimeEndPrefix) {
            String deadlineStartValue = argMultimap.getValue(CliSyntax.PREFIX_DATETIME_START).get().trim()
                    .replaceAll("\\s+", " ");
            String deadlineEndValue = argMultimap.getValue(CliSyntax.PREFIX_DATETIME_END).get().trim()
                    .replaceAll("\\s+", " ");
            LocalDateTime deadlineStart = this.parseDeadlineDateTime(deadlineStartValue, true);
            LocalDateTime deadlineEnd = this.parseDeadlineDateTime(deadlineEndValue, false);
            if (deadlineStart.isAfter(deadlineEnd)) {
                throw new ParseException(Messages.MESSAGE_DEADLINE_START_AFTER_END);
            }
            predicate = new AssignmentDeadlineInRangePredicate(deadlineStart, deadlineEnd);

        } else if (hasDatetimeStartPrefix) {
            String deadlineValue = argMultimap.getValue(CliSyntax.PREFIX_DATETIME_START).get().trim()
                    .replaceAll("\\s+", " ");
            LocalDateTime deadline = this.parseDeadlineDateTime(deadlineValue, true);
            predicate = new AssignmentDeadlineInRangePredicate(deadline, LocalDateTime.MAX);

        } else if (hasDatetimeEndPrefix) {
            String deadlineValue = argMultimap.getValue(CliSyntax.PREFIX_DATETIME_END).get().trim()
                    .replaceAll("\\s+", " ");
            LocalDateTime deadline = this.parseDeadlineDateTime(deadlineValue, false);
            predicate = new AssignmentDeadlineInRangePredicate(LocalDateTime.MIN, deadline);

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
     * If the time component is missing, it defaults to 00:00 for start date and
     * 23:59 for end date.
     *
     * @param deadlineValue the deadline value to parse and validate
     * @param isStartDate   indicates whether the deadline is a start date (true) or
     *                      end date (false)
     * @throws ParseException if the deadline format is invalid
     */
    private LocalDateTime parseDeadlineDateTime(String deadlineValue, boolean isStartDate) throws ParseException {
        LocalDateTime deadline;

        try {
            deadline = ParserUtil.parseDeadline(deadlineValue);
        } catch (ParseException e1) {
            // Try parsing as date-only format (dd-MM-yyyy)
            deadline = this.parseDeadlineDate(deadlineValue, isStartDate);
        }

        return deadline;
    }

    /**
     * Parses the deadline value as a date-only format (dd-MM-yyyy) and sets the
     * time component to 00:00 for start date and 23:59 for end date.
     */
    private LocalDateTime parseDeadlineDate(String deadlineValue, boolean isStartDate) throws ParseException {
        LocalDateTime deadline;

        try {
            deadline = ParserUtil.parseDeadline(deadlineValue + (isStartDate ? " 00:00" : " 23:59"));
        } catch (ParseException e1) {
            throw new ParseException(ParserUtil.MESSAGE_INVALID_DATE_OR_DATETIME);
        }

        return deadline;
    }

}
