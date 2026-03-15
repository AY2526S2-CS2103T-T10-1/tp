package cpp.logic.commands.assignment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cpp.commons.core.index.Index;
import cpp.commons.util.ToStringBuilder;
import cpp.logic.Messages;
import cpp.logic.commands.Command;
import cpp.logic.commands.CommandResult;
import cpp.logic.commands.CommandUtil;
import cpp.logic.commands.exceptions.CommandException;
import cpp.logic.parser.CliSyntax;
import cpp.model.Model;
import cpp.model.assignment.Assignment;
import cpp.model.assignment.AssignmentName;
import cpp.model.assignment.ContactAssignment;
import cpp.model.assignment.exceptions.ContactAlreadyAllocatedAssignmentException;
import cpp.model.contact.Contact;
import cpp.model.util.AssignmentUtil;

/**
 * Allocates an existing assignment to a contact by their displayed indices or
 * class group.
 */
public class AllocateAssignmentCommand extends Command {

    public static final String COMMAND_WORD = "allocass";

    public static final String MESSAGE_USAGE = AllocateAssignmentCommand.COMMAND_WORD
            + ": Allocates an assignment to a contact. "
            + "Parameters: "
            + CliSyntax.PREFIX_ASSIGNMENT + "ASSIGNMENT NAME "
            + "[" + CliSyntax.PREFIX_CLASS + "CLASS NAME] "
            + "[" + CliSyntax.PREFIX_CONTACT + "CONTACT INDICES...]\n"
            + "At least one of " + CliSyntax.PREFIX_CLASS + " or " + CliSyntax.PREFIX_CONTACT + " must be provided.\n"
            + "Example: " + AllocateAssignmentCommand.COMMAND_WORD + " "
            + CliSyntax.PREFIX_ASSIGNMENT + "Assignment 1 "
            + CliSyntax.PREFIX_CLASS + "CS2103T10 "
            + CliSyntax.PREFIX_CONTACT + "1 2 3";

    public static final String MESSAGE_SUCCESS = """
            Allocated assignment: %1$s to %2$s contacts.\nContacts allocated: %3$s
            """;
    public static final String MESSAGE_INVALID_ASSIGNMENT_NAME = "The assignment name provided is invalid";
    public static final String MESSAGE_ALLOCATION_FAILED = "No contacts were allocated the assignment.";

    private final AssignmentName assignmentName;
    private final List<Index> contactIndices;

    /**
     * Creates an AllocateAssignmentCommand with the specified assignment and
     * contact indices.
     */
    public AllocateAssignmentCommand(AssignmentName assignmentName, List<Index> contactIndices) {
        Objects.requireNonNull(assignmentName);
        Objects.requireNonNull(contactIndices);
        this.assignmentName = assignmentName;
        this.contactIndices = new ArrayList<>(contactIndices);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        Objects.requireNonNull(model);

        List<Assignment> assignmentList = model.getAddressBook().getAssignmentList();

        Assignment assignmentToAllocate = AssignmentUtil.findAssignment(assignmentList, this.assignmentName);

        if (assignmentToAllocate == null) {
            throw new CommandException(AllocateAssignmentCommand.MESSAGE_INVALID_ASSIGNMENT_NAME);
        }

        List<Contact> lastShownContactList = model.getFilteredContactList();

        CommandUtil.checkContactIndices(lastShownContactList, this.contactIndices);

        return this.allocateToContacts(model, assignmentToAllocate, lastShownContactList);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof AllocateAssignmentCommand)) {
            return false;
        }
        AllocateAssignmentCommand o = (AllocateAssignmentCommand) other;
        return this.assignmentName.equals(o.assignmentName) && this.contactIndices.equals(o.contactIndices);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("assignmentName", this.assignmentName)
                .add("contactIndices", this.contactIndices)
                .toString();
    }

    private CommandResult allocateToContacts(Model model, Assignment assignmentToAllocate,
            List<Contact> lastShownContactList) throws CommandException {
        StringBuilder allocatedContacts = new StringBuilder();
        boolean anyAllocated = false;
        int allocatedCount = 0;

        for (Index idx : this.contactIndices) {
            Contact contact = lastShownContactList.get(idx.getZeroBased());
            ContactAssignment ca = new ContactAssignment(assignmentToAllocate.getId(), contact.getId());

            try {
                model.addContactAssignment(ca);

            } catch (ContactAlreadyAllocatedAssignmentException e) {
                // Skip already allocated contacts without failing the entire command.
                continue;
            }

            if (allocatedContacts.length() > 0) {
                allocatedContacts.append("; ");
            }
            allocatedContacts.append(contact.getName().fullName);
            anyAllocated = true;
            allocatedCount++;
        }

        if (!anyAllocated) {
            throw new CommandException(AllocateAssignmentCommand.MESSAGE_ALLOCATION_FAILED);
        }

        return new CommandResult(String.format(AllocateAssignmentCommand.MESSAGE_SUCCESS,
                Messages.format(assignmentToAllocate), allocatedCount, allocatedContacts.toString()));
    }

}
