package cpp.logic.commands.view;

import cpp.logic.commands.Command;
import cpp.logic.parser.CliSyntax;

/**
 * Represents a command to view details of a contact, assignment, or class
 * group.
 */
public abstract class ViewCommand extends Command {

    public static final String COMMAND_WORD = "view";

    public static final String MESSAGE_USAGE = ViewCommand.COMMAND_WORD
            + ": Views all details of a contact, assignment, or class.\n"
            + "Parameters: "
            + CliSyntax.PREFIX_CONTACT + "CONTACT_INDEX "
            + "or " + CliSyntax.PREFIX_ASSIGNMENT + "ASSIGNMENT_NAME "
            + "or " + CliSyntax.PREFIX_CLASS + "CLASS_NAME\n"
            + "Exactly one prefix must be specified.\n"
            + "Examples: " + ViewCommand.COMMAND_WORD + " " + CliSyntax.PREFIX_CONTACT + "1, "
            + ViewCommand.COMMAND_WORD + " " + CliSyntax.PREFIX_ASSIGNMENT + "Assignment 1, "
            + ViewCommand.COMMAND_WORD + " " + CliSyntax.PREFIX_CLASS + "CS2103T T10 1";
}
