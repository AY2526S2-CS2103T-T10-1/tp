package cpp.logic.commands;

import java.util.Objects;

import cpp.commons.util.ToStringBuilder;
import cpp.logic.Messages;
import cpp.model.Model;
import cpp.model.classgroup.ClassNameContainsKeywordsPredicate;

/**
 * Finds and lists all class groups in address book whose name contains any of
 * the argument keywords.
 * Keyword matching is case insensitive.
 */
public class FindClassCommand extends Command {

    public static final String COMMAND_WORD = "findclass";

    public static final String MESSAGE_USAGE = FindClassCommand.COMMAND_WORD
            + ": Finds all class groups whose names contain any of "
            + "the specified keywords (case-insensitive) and displays them as a list with index numbers.\n"
            + "Parameters: KEYWORD [MORE_KEYWORDS]...\n"
            + "Example: " + FindClassCommand.COMMAND_WORD + " cs2103 cs2102";

    private final ClassNameContainsKeywordsPredicate predicate;

    public FindClassCommand(ClassNameContainsKeywordsPredicate predicate) {
        this.predicate = predicate;
    }

    @Override
    public CommandResult execute(Model model) {
        Objects.requireNonNull(model);
        model.updateFilteredClassGroupList(this.predicate);
        return new CommandResult(
                String.format(Messages.MESSAGE_CLASS_GROUPS_LISTED_OVERVIEW, model.getFilteredClassGroupList().size()),
                CommandResult.ListView.CLASSGROUPS);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof FindClassCommand)) {
            return false;
        }

        FindClassCommand otherFindClassCommand = (FindClassCommand) other;
        return this.predicate.equals(otherFindClassCommand.predicate);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("predicate", this.predicate)
                .toString();
    }
}
