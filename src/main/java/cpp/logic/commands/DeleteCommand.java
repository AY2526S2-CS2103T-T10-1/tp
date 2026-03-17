package cpp.logic.commands;

import cpp.logic.parser.CliSyntax;

/**
 * Represents a delete command with hidden internal logic and the ability to be
 * executed.
 */
public abstract class DeleteCommand extends Command {

    public static final String COMMAND_WORD = "delete";

    public static final String MESSAGE_USAGE = DeleteCommand.COMMAND_WORD
            + ": Deletes contact(s) based on index specified, an assignment, or a class group.\n"
            + "Parameters: "
            + CliSyntax.PREFIX_CONTACT + "CONTACT INDICES... or "
            + CliSyntax.PREFIX_ASSIGNMENT + "ASSIGNMENT NAME or "
            + CliSyntax.PREFIX_CLASS + "CLASS NAME\n"
            + "Exactly one prefix must be specified.\n"
            + "Examples: " + DeleteCommand.COMMAND_WORD + " " + CliSyntax.PREFIX_CONTACT + "1 2 3, "
            + DeleteCommand.COMMAND_WORD + " " + CliSyntax.PREFIX_ASSIGNMENT + "Assignment 1, "
            + DeleteCommand.COMMAND_WORD + " " + CliSyntax.PREFIX_CLASS + "CS2103T10";

}
