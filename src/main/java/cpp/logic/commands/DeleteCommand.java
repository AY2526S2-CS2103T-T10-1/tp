package cpp.logic.commands;

/**
 * Represents a delete command with hidden internal logic and the ability to be executed.
 */
public abstract class DeleteCommand extends Command {

    public static final String COMMAND_WORD = "delete";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes a contact or assignment. Exactly one prefix must be specified.\n"
            + "Parameters: ct/INDEX [MORE_INDICES]... or ass/ASSIGNMENT_NAME\n"
            + "Examples: " + COMMAND_WORD + " ct/1 2 3, " + COMMAND_WORD + " ass/Assignment 1";

}
