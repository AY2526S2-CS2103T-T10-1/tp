package cpp.logic.commands;

import java.util.Objects;

import cpp.model.Model;

/**
 * Lists all contacts in the address book to the user.
 */
public class ListContactCommand extends ListCommand {

    public ListContactCommand() {
        super();
    }

    @Override
    public CommandResult execute(Model model) {
        Objects.requireNonNull(model);
        model.updateFilteredContactList(Model.PREDICATE_SHOW_ALL_CONTACTS);
        return new CommandResult(ListCommand.MESSAGE_SUCCESS, "contacts");
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ListContactCommand)) {
            return false;
        }

        return true;
    }
}
