package cpp.logic.commands;

import java.util.Objects;

import cpp.model.Model;

/**
 * Lists all classes in the address book to the user.
 */
public class ListClassCommand extends ListCommand {

    public ListClassCommand() {
        super();
    }

    @Override
    public CommandResult execute(Model model) {
        Objects.requireNonNull(model);
        model.updateFilteredContactList(Model.PREDICATE_SHOW_ALL_CONTACTS);
        return new CommandResult(ListCommand.MESSAGE_CLASSES, "classes");
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ListClassCommand)) {
            return false;
        }

        return true;
    }
}
