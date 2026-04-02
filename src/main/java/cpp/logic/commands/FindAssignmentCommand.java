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
 * - Deadline search: finds assignments whose deadline matches the specified
 * value exactly (supports dd-MM-yyyy and dd-MM-yyyy HH:mm
 * formats).
 */
public class FindAssignmentCommand extends Command {

    public static final String COMMAND_WORD = "findass";

    public static final String MESSAGE_USAGE = FindAssignmentCommand.COMMAND_WORD
            + ": Finds all assignments whose names contain the specified search string (case-insensitive) or "
            + "whose deadlines match exactly and displays them as a list with index numbers.\n"
            + "Parameters: ["
            + CliSyntax.PREFIX_ASSIGNMENT + "ASSIGNMENT_NAME_SEARCH_STRING] ["
            + CliSyntax.PREFIX_DATETIME + "DEADLINE]\n"
            + "Exactly one of ["
            + CliSyntax.PREFIX_ASSIGNMENT + "ASSIGNMENT_NAME_SEARCH_STRING] or ["
            + CliSyntax.PREFIX_DATETIME + "DEADLINE] must be provided.\n"
            + "Example: " + FindAssignmentCommand.COMMAND_WORD + " " + CliSyntax.PREFIX_ASSIGNMENT + "Assignment 1\n"
            + "Example: " + FindAssignmentCommand.COMMAND_WORD + " " + CliSyntax.PREFIX_DATETIME + "31-12-2024";

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
