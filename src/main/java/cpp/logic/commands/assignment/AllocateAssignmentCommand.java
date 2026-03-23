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
import cpp.model.classgroup.ClassGroup;
import cpp.model.classgroup.ClassGroupName;
import cpp.model.contact.Contact;
import cpp.model.util.AssignmentUtil;
import cpp.model.util.ClassGroupUtil;

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
            Allocated assignment: %1$s to %2$s contact(s)
            Contacts allocated: %3$s""";
    public static final String MESSAGE_ALLOCATION_FAILED = "No contacts were allocated the assignment";

    private final AssignmentName assignmentName;
    private final List<Index> contactIndices;
    private final ClassGroupName classGroupName;
    private int allocatedCount;
    private StringBuilder allocatedContacts;

    /**
     * Creates an AllocateAssignmentCommand with the specified assignment and
     * contact indices.
     */
    public AllocateAssignmentCommand(AssignmentName assignmentName, List<Index> contactIndices) {
        Objects.requireNonNull(assignmentName);
        Objects.requireNonNull(contactIndices);
        this.assignmentName = assignmentName;
        this.contactIndices = new ArrayList<>(contactIndices);
        this.classGroupName = null;
        this.allocatedCount = 0;
        this.allocatedContacts = new StringBuilder();
    }

    /**
     * Creates an AllocateAssignmentCommand with the specified assignment, contact
     * indices, and class group.
     */
    public AllocateAssignmentCommand(AssignmentName assignmentName, List<Index> contactIndices,
            ClassGroupName classGroupName) {
        Objects.requireNonNull(assignmentName);
        Objects.requireNonNull(contactIndices);
        Objects.requireNonNull(classGroupName);
        this.assignmentName = assignmentName;
        this.contactIndices = new ArrayList<>(contactIndices);
        this.classGroupName = classGroupName;
        this.allocatedCount = 0;
        this.allocatedContacts = new StringBuilder();
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        Objects.requireNonNull(model);

        List<Assignment> assignmentList = model.getAddressBook().getAssignmentList();

        Assignment assignmentToAllocate = AssignmentUtil.findAssignment(assignmentList, this.assignmentName);

        if (assignmentToAllocate == null) {
            throw new CommandException(Messages.MESSAGE_ASSIGNMENT_NOT_FOUND);
        }

        List<Contact> lastShownContactList = model.getFilteredContactList();

        CommandUtil.checkContactIndices(lastShownContactList, this.contactIndices);

        ClassGroup classGroupToAllocate = ClassGroupUtil.findClassGroup(model.getAddressBook().getClassGroupList(),
                this.classGroupName);
        if (this.classGroupName != null && classGroupToAllocate == null) {
            throw new CommandException(Messages.MESSAGE_CLASS_GROUP_NOT_FOUND);
        }

        this.allocateToContactsByContactIndices(model, assignmentToAllocate, lastShownContactList);
        if (classGroupToAllocate != null) {
            this.allocateToContactsByClassGroup(model, assignmentToAllocate, classGroupToAllocate);
        }

        if (this.allocatedCount == 0) {
            throw new CommandException(AllocateAssignmentCommand.MESSAGE_ALLOCATION_FAILED);
        }

        return new CommandResult(String.format(AllocateAssignmentCommand.MESSAGE_SUCCESS,
                Messages.format(assignmentToAllocate), this.allocatedCount, this.allocatedContacts.toString()));
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
        return this.assignmentName.equals(o.assignmentName) && this.contactIndices.equals(o.contactIndices)
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

    private void allocateToContactsByContactIndices(Model model, Assignment assignmentToAllocate,
            List<Contact> lastShownContactList) {

        for (Index idx : this.contactIndices) {
            Contact contact = lastShownContactList.get(idx.getZeroBased());

            this.allocateToContact(model, assignmentToAllocate, contact);
        }
    }

    private void allocateToContactsByClassGroup(Model model, Assignment assignmentToAllocate,
            ClassGroup classGroupToAllocate) {
        List<Contact> contactList = model.getAddressBook().getContactList();

        for (String contactId : classGroupToAllocate.getContactIdSet()) {
            Contact contact = contactList.stream()
                    .filter(c -> c.getId().equals(contactId))
                    .findAny()
                    .orElse(null);

            if (contact != null) {
                this.allocateToContact(model, assignmentToAllocate, contact);
            }
        }
    }

    private void allocateToContact(Model model, Assignment assignment, Contact contact) {
        ContactAssignment ca = new ContactAssignment(assignment.getId(), contact.getId());

        try {
            model.addContactAssignment(ca);

            if (this.allocatedContacts.length() > 0) {
                this.allocatedContacts.append("; ");
            }
            this.allocatedContacts.append(contact.getName().fullName);
            this.allocatedCount++;

        } catch (ContactAlreadyAllocatedAssignmentException e) {
            // Skip contacts that already have the assignment allocated.
        }
    }

}
