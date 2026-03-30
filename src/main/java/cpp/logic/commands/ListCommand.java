package cpp.logic.commands;

import cpp.logic.commands.exceptions.CommandException;
import cpp.model.Model;

/**
 * Abstract base class for list commands that display all items from one of the
 * following:
 * Contacts, Classes, or Assignments.
 */
public abstract class ListCommand extends Command {

    public static final String COMMAND_WORD = "list";
    public static final String MESSAGE_CONTACTS = "Listed all contacts";
    public static final String MESSAGE_ASSIGNMENTS = "Listed all assignments";
    public static final String MESSAGE_CLASSES = "Listed all classes";
    public static final String MESSAGE_TAB_EMPTY = "Tab cannot be empty!";
    public static final String MESSAGE_TAB_INVALID = "Tab must be one of the following: contacts, classes, assignments";

    public static final String MESSAGE_USAGE = ListCommand.COMMAND_WORD
            + ": Lists all items in the specified tab.\n"
            + "Parameters: TAB\n"
            + "Example: " + ListCommand.COMMAND_WORD + " contacts\n"
            + "Example: " + ListCommand.COMMAND_WORD + " assignments\n"
            + "Example: " + ListCommand.COMMAND_WORD + " classes";

    protected ListCommand() {
    }

    @Override
    public abstract CommandResult execute(Model model) throws CommandException;

    @Override
    public abstract boolean equals(Object other);
}
