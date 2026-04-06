package cpp.logic.commands.view;

import java.util.List;
import java.util.Objects;

import cpp.commons.util.ToStringBuilder;
import cpp.logic.Messages;
import cpp.logic.commands.CommandResult;
import cpp.logic.commands.exceptions.CommandException;
import cpp.model.Model;
import cpp.model.assignment.Assignment;
import cpp.model.assignment.AssignmentName;
import cpp.model.util.AssignmentUtil;

/**
 * Displays the details of an assignment identified by its name in the address
 * book.
 */
public class ViewAssignmentCommand extends ViewCommand {

    public static final String MESSAGE_SUCCESS = "Viewing details of Assignment: %1$s";

    private final AssignmentName targetName;

    public ViewAssignmentCommand(AssignmentName targetName) {
        this.targetName = targetName;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        Objects.requireNonNull(model);
        List<Assignment> assignmentList = model.getAddressBook().getAssignmentList();

        Assignment assignmentToView = AssignmentUtil.findAssignment(assignmentList, this.targetName);

        if (assignmentToView == null) {
            throw new CommandException(Messages.MESSAGE_ASSIGNMENT_NOT_FOUND);
        }

        model.viewAssignment(assignmentToView);

        return new CommandResult(
                String.format(ViewAssignmentCommand.MESSAGE_SUCCESS, Messages.format(assignmentToView)),
                CommandResult.ViewType.ASSIGNMENT);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof ViewAssignmentCommand)) {
            return false;
        }

        ViewAssignmentCommand otherViewAssignmentCommand = (ViewAssignmentCommand) other;
        return this.targetName.equals(otherViewAssignmentCommand.targetName);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetName", this.targetName)
                .toString();
    }
}
