package cpp.logic.commands;

import java.util.Objects;

import cpp.commons.util.ToStringBuilder;
import cpp.logic.Messages;
import cpp.model.Model;
import cpp.model.assignment.AssignmentSearchPredicate;

/**
 * Finds and lists all assignments in address book whose name contains the
 * specified search string.
 * Matching is case insensitive and exact (substring match).
 */
public class FindAssignmentCommand extends Command {

    public static final String COMMAND_WORD = "findass";

    public static final String MESSAGE_USAGE = FindAssignmentCommand.COMMAND_WORD
            + ": Finds all assignments whose names or deadlines contain the specified keywords "
            + "(case-insensitive) and displays them as a list with index numbers.\n"
            + "Parameters: SEARCH_STRING | d/DEADLINE\n"
            + "Default search: name\n"
            + "Deadline formats: dd-MM-yyyy or dd-MM-yyyy HH:mm\n"
            + "Example: " + FindAssignmentCommand.COMMAND_WORD + " Assignment 1\n"
            + "Example: " + FindAssignmentCommand.COMMAND_WORD + " d/31-12-2024";

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
