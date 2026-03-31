package cpp.logic.commands.assignment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import cpp.commons.core.index.Index;
import cpp.commons.util.CollectionUtil;
import cpp.commons.util.ToStringBuilder;
import cpp.logic.Messages;
import cpp.logic.commands.Command;
import cpp.logic.commands.CommandResult;
import cpp.logic.commands.exceptions.CommandException;
import cpp.logic.parser.CliSyntax;
import cpp.model.Model;
import cpp.model.assignment.Assignment;
import cpp.model.assignment.AssignmentName;

/**
 * Edits the details of an existing assignment in the address book.
 */
public class EditAssignmentCommand extends Command {

    public static final String COMMAND_WORD = "editass";

    public static final String MESSAGE_USAGE = EditAssignmentCommand.COMMAND_WORD
            + ": Edits the details of the assignment identified "
            + "by the index number used in the displayed assignment list. "
            + "Existing values will be overwritten by the input values.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "[" + CliSyntax.PREFIX_ASSIGNMENT + "ASSIGNMENT_NAME] "
            + "[" + CliSyntax.PREFIX_DATETIME + "DEADLINE]\n"
            + "Example: " + EditAssignmentCommand.COMMAND_WORD + " 1 "
            + CliSyntax.PREFIX_ASSIGNMENT + "Assignment 1234 "
            + CliSyntax.PREFIX_DATETIME + "20-12-2026 23:59";

    public static final String MESSAGE_EDIT_ASSIGNMENT_SUCCESS = "Edited Assignment: %1$s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.\n"
            + EditAssignmentCommand.MESSAGE_USAGE;
    public static final String MESSAGE_DUPLICATE_ASSIGNMENT = "This assignment already exists in the address book.";

    private final Index index;
    private final EditAssignmentDescriptor editAssignmentDescriptor;

    /**
     * @param index                    of the assignment in the filtered assignment
     *                                 list to edit
     * @param editAssignmentDescriptor details to edit the assignment with
     */
    public EditAssignmentCommand(Index index, EditAssignmentDescriptor editAssignmentDescriptor) {
        Objects.requireNonNull(index);
        Objects.requireNonNull(editAssignmentDescriptor);
        this.index = index;
        this.editAssignmentDescriptor = new EditAssignmentDescriptor(editAssignmentDescriptor);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        Objects.requireNonNull(model);
        List<Assignment> lastShownList = model.getFilteredAssignmentList();

        if (this.index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_ASSIGNMENT_DISPLAYED_INDEX);
        }

        Assignment assignmentToEdit = lastShownList.get(this.index.getZeroBased());

        AssignmentName newName = this.editAssignmentDescriptor.getName()
                .orElse(assignmentToEdit.getName());
        LocalDateTime newDeadline = this.editAssignmentDescriptor.getDeadline()
                .orElse(assignmentToEdit.getDeadline());

        Assignment editedAssignment = new Assignment(assignmentToEdit.getId(), newName, newDeadline);

        boolean isDuplicate = model.getAddressBook().getAssignmentList().stream()
                .anyMatch(a -> !a.getId().equals(assignmentToEdit.getId())
                        && a.getName().equals(newName));
        if (isDuplicate) {
            throw new CommandException(EditAssignmentCommand.MESSAGE_DUPLICATE_ASSIGNMENT);
        }

        model.setAssignment(assignmentToEdit, editedAssignment);
        return new CommandResult(
                String.format(EditAssignmentCommand.MESSAGE_EDIT_ASSIGNMENT_SUCCESS,
                        Messages.format(editedAssignment)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof EditAssignmentCommand)) {
            return false;
        }
        EditAssignmentCommand otherCmd = (EditAssignmentCommand) other;
        return this.index.equals(otherCmd.index)
                && this.editAssignmentDescriptor.equals(otherCmd.editAssignmentDescriptor);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", this.index)
                .add("editAssignmentDescriptor", this.editAssignmentDescriptor)
                .toString();
    }

    /**
     * Stores the details to edit the assignment with. Each non-empty field value
     * will
     * replace the corresponding field value of the assignment.
     */
    public static class EditAssignmentDescriptor {
        private AssignmentName name;
        private LocalDateTime deadline;

        /**
         * Creates an empty EditAssignmentDescriptor.
         */
        public EditAssignmentDescriptor() {
        }

        /**
         * Copy constructor.
         */
        public EditAssignmentDescriptor(EditAssignmentDescriptor toCopy) {
            this.setName(toCopy.name);
            this.setDeadline(toCopy.deadline);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(this.name, this.deadline);
        }

        /**
         * Sets the name field.
         */
        public void setName(AssignmentName name) {
            this.name = name;
        }

        /**
         * Returns an Optional containing the name, or empty if not set.
         */
        public Optional<AssignmentName> getName() {
            return Optional.ofNullable(this.name);
        }

        /**
         * Sets the deadline field.
         */
        public void setDeadline(LocalDateTime deadline) {
            this.deadline = deadline;
        }

        /**
         * Returns an Optional containing the deadline, or empty if not set.
         */
        public Optional<LocalDateTime> getDeadline() {
            return Optional.ofNullable(this.deadline);
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }
            if (!(other instanceof EditAssignmentDescriptor)) {
                return false;
            }
            EditAssignmentDescriptor o = (EditAssignmentDescriptor) other;
            return Objects.equals(this.name, o.name)
                    && Objects.equals(this.deadline, o.deadline);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .add("name", this.name)
                    .add("deadline", this.deadline)
                    .toString();
        }
    }
}
