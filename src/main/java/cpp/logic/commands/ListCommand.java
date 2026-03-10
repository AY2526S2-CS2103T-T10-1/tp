package cpp.logic.commands;

import java.util.Objects;

import cpp.model.Model;

/**
 * Lists all contacts in the address book to the user.
 */
public class ListCommand extends Command {

    public static final String COMMAND_WORD = "list";
    public static final String MESSAGE_SUCCESS = "Listed all contacts";
    public static final String MESSAGE_ASSIGNMENTS = "Listed all assignments";
    public static final String MESSAGE_CLASSES = "Listed all classes";
    public static final String MESSAGE_TAB_EMPTY = "Tab cannot be empty!";
    public static final String MESSAGE_TAB_INVALID = "Tab must be one of the following: contacts, classes, assignments";
    private String tab;

    public ListCommand(String tab) {
        this.tab = tab;
    }

    @Override
    public CommandResult execute(Model model) {
        Objects.requireNonNull(model);
        model.updateFilteredContactList(Model.PREDICATE_SHOW_ALL_CONTACTS);

        if (this.tab.equals("assignments")) {
            return new CommandResult(ListCommand.MESSAGE_ASSIGNMENTS, "assignments");
        } else if (this.tab.equals("contacts")) {
            return new CommandResult(ListCommand.MESSAGE_SUCCESS, "contacts");
        } else if (this.tab.equals("classes")) {
            return new CommandResult(ListCommand.MESSAGE_CLASSES, "classes");
        }
        return new CommandResult(ListCommand.MESSAGE_SUCCESS);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ListCommand)) {
            return false;
        }

        ListCommand otherListCommand = (ListCommand) other;
        return this.tab.equals(otherListCommand.tab);
    }
}
