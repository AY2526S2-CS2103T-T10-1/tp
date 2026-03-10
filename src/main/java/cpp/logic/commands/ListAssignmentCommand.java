package cpp.logic.commands;

import java.util.Objects;

import cpp.model.Model;

/**
 * Lists all assignments in the address book to the user.
 */
public class ListAssignmentCommand extends ListCommand {

    public ListAssignmentCommand() {
        super();
    }

    @Override
    public CommandResult execute(Model model) {
        Objects.requireNonNull(model);
        model.updateFilteredContactList(Model.PREDICATE_SHOW_ALL_CONTACTS);
        return new CommandResult(ListCommand.MESSAGE_ASSIGNMENTS, "assignments");
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ListAssignmentCommand)) {
            return false;
        }

        return true;
    }
}
