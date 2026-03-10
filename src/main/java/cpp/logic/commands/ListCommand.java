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
    public static final String MESSAGE_USAGE = ListCommand.COMMAND_WORD
            + ": Lists all contacts or assignments in the address book.\n"
            + "Parameters: [contacts/assignments]\n"
            + "Example: " + ListCommand.COMMAND_WORD + " contacts";
    private String listType;

    @Override
    public CommandResult execute(Model model) {
        Objects.requireNonNull(model);
        model.updateFilteredContactList(Model.PREDICATE_SHOW_ALL_CONTACTS);
        return new CommandResult(ListCommand.MESSAGE_SUCCESS);
    }

    public ListCommand(String listType) {
        this.listType = listType;
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
        return this.listType.equals(otherListCommand.listType);
    }
}
