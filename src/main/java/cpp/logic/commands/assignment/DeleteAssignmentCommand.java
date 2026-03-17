package cpp.logic.commands.assignment;

import java.util.List;
import java.util.Objects;

import cpp.commons.util.ToStringBuilder;
import cpp.logic.Messages;
import cpp.logic.commands.CommandResult;
import cpp.logic.commands.DeleteCommand;
import cpp.logic.commands.exceptions.CommandException;
import cpp.model.Model;
import cpp.model.assignment.Assignment;
import cpp.model.assignment.AssignmentName;
import cpp.model.util.AssignmentUtil;

/**
 * Deletes an assignment identified using its name from the address book.
 */
public class DeleteAssignmentCommand extends DeleteCommand {

    public static final String MESSAGE_DELETE_ASSIGNMENT_SUCCESS = "Deleted Assignment: %1$s";

    private final AssignmentName targetName;

    public DeleteAssignmentCommand(AssignmentName targetName) {
        this.targetName = targetName;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        Objects.requireNonNull(model);
        List<Assignment> assignmentList = model.getAddressBook().getAssignmentList();

        Assignment assignmentToDelete = AssignmentUtil.findAssignment(assignmentList, this.targetName);

        if (assignmentToDelete == null) {
            throw new CommandException(Messages.MESSAGE_ASSIGNMENT_NOT_FOUND);
        }

        model.deleteAssignment(assignmentToDelete);
        return new CommandResult(
                String.format(DeleteAssignmentCommand.MESSAGE_DELETE_ASSIGNMENT_SUCCESS,
                        Messages.format(assignmentToDelete)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof DeleteAssignmentCommand)) {
            return false;
        }

        DeleteAssignmentCommand otherDeleteAssignmentCommand = (DeleteAssignmentCommand) other;
        return this.targetName.equals(otherDeleteAssignmentCommand.targetName);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetName", this.targetName)
                .toString();
    }
}
