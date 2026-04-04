package cpp.logic.commands;

import java.util.Objects;

import cpp.commons.util.ToStringBuilder;
import cpp.logic.Messages;
import cpp.logic.parser.CliSyntax;
import cpp.model.Model;
import cpp.model.assignment.AssignmentSearchPredicate;

/**
 * Finds and lists all assignments in the address book using the {@code findass}
 * command.
 * <p>
 * Supports two search modes:
 * <br>
 * - Name search: finds assignments whose names contain the specified search
 * string (case-insensitive, substring match).<br>
 * - Deadline search: finds assignments whose deadline falls within the
 * specified date/time range.
 */
public class FindAssignmentCommand extends Command {

    public static final String COMMAND_WORD = "findass";

    public static final String MESSAGE_USAGE = FindAssignmentCommand.COMMAND_WORD
            + ": Finds all assignments whose names contain the specified search string (case-insensitive) or "
            + "whose deadlines fall within the specified range (inclusive) "
            + "and displays them as a list with index numbers.\n"
            + "Deadline can be of the format dd-MM-yyyy or dd-MM-yyyy HH:mm.\n"
            + "Parameters (Option 1): " + CliSyntax.PREFIX_ASSIGNMENT + "ASSIGNMENT_NAME_SEARCH_STRING\n"
            + "Parameters (Option 2): [" + CliSyntax.PREFIX_DATETIME_START + "DEADLINE_START] ["
            + CliSyntax.PREFIX_DATETIME_END + "DEADLINE_END]\n"
            + "Only one option can be used at a time. For Option 2, at least one of ["
            + CliSyntax.PREFIX_DATETIME_START + "DEADLINE_START] or ["
            + CliSyntax.PREFIX_DATETIME_END + "DEADLINE_END] must be provided.\n"
            + "Example: " + FindAssignmentCommand.COMMAND_WORD + " " + CliSyntax.PREFIX_ASSIGNMENT + "Assignment 1\n"
            + "Example: " + FindAssignmentCommand.COMMAND_WORD + " " + CliSyntax.PREFIX_DATETIME_START + "01-01-2025 "
            + CliSyntax.PREFIX_DATETIME_END + "31-12-2025 23:59";

    private final AssignmentSearchPredicate predicate;

    public FindAssignmentCommand(AssignmentSearchPredicate predicate) {
        this.predicate = predicate;
    }

    @Override
    public CommandResult execute(Model model) {
        Objects.requireNonNull(model);
        model.updateFilteredAssignmentList(this.predicate);
        return new CommandResult(
                String.format(Messages.MESSAGE_ASSIGNMENTS_LISTED_OVERVIEW, model.getFilteredAssignmentList().size()),
                CommandResult.ListView.ASSIGNMENTS);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof FindAssignmentCommand)) {
            return false;
        }

        FindAssignmentCommand otherFindAssignmentCommand = (FindAssignmentCommand) other;
        return this.predicate.equals(otherFindAssignmentCommand.predicate);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("predicate", this.predicate)
                .toString();
    }
}
