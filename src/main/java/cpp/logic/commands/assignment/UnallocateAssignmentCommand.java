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
import cpp.logic.commands.classgroup.AllocateClassGroupCommand;
import cpp.logic.commands.exceptions.CommandException;
import cpp.logic.parser.CliSyntax;
import cpp.model.Model;
import cpp.model.assignment.Assignment;
import cpp.model.assignment.AssignmentName;
import cpp.model.assignment.ContactAssignment;
import cpp.model.assignment.exceptions.ContactAssignmentNotFoundException;
import cpp.model.classgroup.ClassGroup;
import cpp.model.classgroup.ClassGroupName;
import cpp.model.contact.Contact;
import cpp.model.util.AssignmentUtil;
import cpp.model.util.ClassGroupUtil;

/**
 * Unallocates an existing assignment from a contact by their displayed indices
 * or class group.
 */
public class UnallocateAssignmentCommand extends Command {

    public static final String COMMAND_WORD = "unallocass";

    public static final String MESSAGE_USAGE = UnallocateAssignmentCommand.COMMAND_WORD
            + ": Unallocates an assignment from a contact. "
            + "Parameters: "
            + CliSyntax.PREFIX_ASSIGNMENT + "ASSIGNMENT NAME "
            + "[" + CliSyntax.PREFIX_CLASS + "CLASS NAME] "
            + "[" + CliSyntax.PREFIX_CONTACT + "CONTACT INDICES...]\n"
            + "At least one of " + CliSyntax.PREFIX_CLASS + " or " + CliSyntax.PREFIX_CONTACT + " must be provided.\n"
            + "Example: " + UnallocateAssignmentCommand.COMMAND_WORD + " "
            + CliSyntax.PREFIX_ASSIGNMENT + "Assignment 1 "
            + CliSyntax.PREFIX_CLASS + "CS2103T10 "
            + CliSyntax.PREFIX_CONTACT + "1 2 3";

    public static final String MESSAGE_SUCCESS = """
            Unallocated assignment: %1$s from %2$s contacts.\nContacts unallocated: %3$s
            """;
    public static final String MESSAGE_UNALLOCATION_FAILED = "No contacts were unallocated the assignment.";

    private final AssignmentName assignmentName;
    private final List<Index> contactIndices;
    private final ClassGroupName classGroupName;
    private int unallocatedCount; // Tracks the number of contacts successfully unallocated.
    private StringBuilder unallocatedContacts; // Tracks the names of contacts successfully unallocated for feedback.

    /**
     * Creates an UnallocateAssignmentCommand with the specified assignment and
     * contact indices.
     */
    public UnallocateAssignmentCommand(AssignmentName assignmentName, List<Index> contactIndices) {
        Objects.requireNonNull(assignmentName);
        Objects.requireNonNull(contactIndices);
        this.assignmentName = assignmentName;
        this.contactIndices = new ArrayList<>(contactIndices);
        this.classGroupName = null;
        this.unallocatedCount = 0;
        this.unallocatedContacts = new StringBuilder();
    }

    /**
     * Creates an UnallocateAssignmentCommand with the specified assignment, contact
     * indices, and class group.
     */
    public UnallocateAssignmentCommand(AssignmentName assignmentName, List<Index> contactIndices,
            ClassGroupName classGroupName) {
        Objects.requireNonNull(assignmentName);
        Objects.requireNonNull(contactIndices);
        Objects.requireNonNull(classGroupName);
        this.assignmentName = assignmentName;
        this.contactIndices = new ArrayList<>(contactIndices);
        this.classGroupName = classGroupName;
        this.unallocatedCount = 0;
        this.unallocatedContacts = new StringBuilder();
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        Objects.requireNonNull(model);

        List<Assignment> assignmentList = model.getAddressBook().getAssignmentList();

        Assignment assignmentToUnallocate = AssignmentUtil.findAssignment(assignmentList, this.assignmentName);

        if (assignmentToUnallocate == null) {
            throw new CommandException(AllocateAssignmentCommand.MESSAGE_INVALID_ASSIGNMENT_NAME);
        }

        List<Contact> lastShownContactList = model.getFilteredContactList();

        CommandUtil.checkContactIndices(lastShownContactList, this.contactIndices);

        ClassGroup classGroupToUnallocate = ClassGroupUtil.findClassGroup(model.getAddressBook().getClassGroupList(),
                this.classGroupName);
        if (this.classGroupName != null && classGroupToUnallocate == null) {
            throw new CommandException(AllocateClassGroupCommand.MESSAGE_INVALID_CLASS_GROUP_NAME);
        }

        this.unallocateFromContactsByContactIndices(model, assignmentToUnallocate, lastShownContactList);
        if (classGroupToUnallocate != null) {
            this.unallocateFromContactsByClassGroup(model, assignmentToUnallocate, classGroupToUnallocate);
        }

        if (this.unallocatedCount == 0) {
            throw new CommandException(UnallocateAssignmentCommand.MESSAGE_UNALLOCATION_FAILED);
        }

        return new CommandResult(String.format(UnallocateAssignmentCommand.MESSAGE_SUCCESS,
                Messages.format(assignmentToUnallocate), this.unallocatedCount, this.unallocatedContacts.toString()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof UnallocateAssignmentCommand)) {
            return false;
        }
        UnallocateAssignmentCommand o = (UnallocateAssignmentCommand) other;
        return this.assignmentName.equals(o.assignmentName)
                && this.contactIndices.equals(o.contactIndices)
                && Objects.equals(this.classGroupName, o.classGroupName);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("assignmentName", this.assignmentName)
                .add("contactIndices", this.contactIndices)
                .add("classGroupName", this.classGroupName)
                .toString();
    }

    private void unallocateFromContactsByContactIndices(Model model, Assignment assignmentToUnallocate,
            List<Contact> lastShownContactList) {

        for (Index idx : this.contactIndices) {
            Contact contact = lastShownContactList.get(idx.getZeroBased());

            this.unallocateFromContact(model, assignmentToUnallocate, contact);
        }
    }

    private void unallocateFromContactsByClassGroup(Model model, Assignment assignmentToUnallocate,
            ClassGroup classGroupToUnallocate) {
        List<Contact> contactList = model.getAddressBook().getContactList();

        for (String contactId : classGroupToUnallocate.getContactIdSet()) {
            Contact contact = contactList.stream()
                    .filter(c -> c.getId().equals(contactId))
                    .findAny()
                    .orElse(null);

            if (contact != null) {
                this.unallocateFromContact(model, assignmentToUnallocate, contact);
            }
        }
    }

    private void unallocateFromContact(Model model, Assignment assignment, Contact contact) {
        ContactAssignment ca = new ContactAssignment(assignment.getId(), contact.getId());

        try {
            model.removeContactAssignment(ca);

            if (this.unallocatedContacts.length() > 0) {
                this.unallocatedContacts.append("; ");
            }
            this.unallocatedContacts.append(contact.getName().fullName);
            this.unallocatedCount++;

        } catch (ContactAssignmentNotFoundException e) {
            // Skip contacts that don't have the assignment allocated.
        }
    }

}
