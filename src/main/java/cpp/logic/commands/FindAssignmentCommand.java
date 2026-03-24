package cpp.logic.commands;

import java.util.Objects;

import cpp.commons.util.ToStringBuilder;
import cpp.logic.Messages;
import cpp.model.Model;
import cpp.model.assignment.AssignmentNameContainsKeywordsPredicate;

/**
 * Finds and lists all assignments in address book whose name contains any of
 * the argument keywords.
 * Keyword matching is case insensitive.
 */
public class FindAssignmentCommand extends Command {

    public static final String COMMAND_WORD = "findassignment";

    public static final String MESSAGE_USAGE = FindAssignmentCommand.COMMAND_WORD
            + ": Finds all assignments whose names contain any of "
            + "the specified keywords (case-insensitive) and displays them as a list with index numbers.\n"
            + "Parameters: KEYWORD [MORE_KEYWORDS]...\n"
            + "Example: " + FindAssignmentCommand.COMMAND_WORD + " Lab Homework";

    private final AssignmentNameContainsKeywordsPredicate predicate;

    public FindAssignmentCommand(AssignmentNameContainsKeywordsPredicate predicate) {
        this.predicate = predicate;
    }

    @Override
    public CommandResult execute(Model model) {
        Objects.requireNonNull(model);
        model.updateFilteredAssignmentList(this.predicate);
        return new CommandResult(
                String.format(Messages.MESSAGE_ASSIGNMENTS_LISTED_OVERVIEW, model.getFilteredAssignmentList().size()));
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
