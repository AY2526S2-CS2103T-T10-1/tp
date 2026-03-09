package cpp.logic.commands.assignment;

import java.util.Objects;

import cpp.commons.util.ToStringBuilder;
import cpp.logic.Messages;
import cpp.logic.commands.Command;
import cpp.logic.commands.CommandResult;
import cpp.logic.commands.exceptions.CommandException;
import cpp.logic.parser.CliSyntax;
import cpp.model.Model;
import cpp.model.assignment.Assignment;

/**
 * Adds an assignment to the assignment list.
 */
public class AddAssignmentCommand extends Command {

    public static final String COMMAND_WORD = "addass";

    public static final String MESSAGE_USAGE = AddAssignmentCommand.COMMAND_WORD
            + ": Adds an assignment to the assignment list. "
            + "Parameters: "
            + CliSyntax.PREFIX_ASSIGNMENT + "ASSIGNMENT NAME "
            + CliSyntax.PREFIX_DEADLINE + "DEADLINE "
            + "[" + CliSyntax.PREFIX_CLASS + "CLASS NAME] "
            + "[" + CliSyntax.PREFIX_CONTACT + "CONTACT INDICES...]\n"
            + "Example: " + AddAssignmentCommand.COMMAND_WORD + " "
            + CliSyntax.PREFIX_ASSIGNMENT + "Assignment 1 "
            + CliSyntax.PREFIX_DEADLINE + "21-02-2026 23:59 "
            + CliSyntax.PREFIX_CLASS + "CS2103T10 "
            + CliSyntax.PREFIX_CONTACT + "1 2 3";

    public static final String MESSAGE_SUCCESS = "New assignment added: %1$s";
    public static final String MESSAGE_DUPLICATE_ASSIGNMENT = "This assignment already exists in the assignment list";

    private final Assignment toAdd;

    /**
     * Creates an AddAssignmentCommand to add the specified {@code Assignment}
     */
    public AddAssignmentCommand(Assignment assignment) {
        Objects.requireNonNull(assignment);
        this.toAdd = assignment;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        Objects.requireNonNull(model);

        if (model.hasAssignment(this.toAdd)) {
            throw new CommandException(AddAssignmentCommand.MESSAGE_DUPLICATE_ASSIGNMENT);
        }

        model.addAssignment(this.toAdd);
        return new CommandResult(String.format(AddAssignmentCommand.MESSAGE_SUCCESS, Messages.format(this.toAdd)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof AddAssignmentCommand)) {
            return false;
        }
        AddAssignmentCommand o = (AddAssignmentCommand) other;
        return this.toAdd.getName().equals(o.toAdd.getName())
                && this.toAdd.getDeadline().equals(o.toAdd.getDeadline());
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("toAddAssignment", this.toAdd)
                .toString();
    }
}
