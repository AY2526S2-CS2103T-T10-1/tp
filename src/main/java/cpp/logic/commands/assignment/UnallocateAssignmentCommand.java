package cpp.logic.commands.assignment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;

import cpp.commons.core.LogsCenter;
import cpp.commons.core.index.Index;
import cpp.commons.util.ToStringBuilder;
import cpp.logic.LogicManager;
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
import cpp.model.assignment.exceptions.ContactAssignmentNotFoundException;
import cpp.model.classgroup.ClassGroup;
import cpp.model.classgroup.ClassGroupName;
import cpp.model.contact.Contact;
import cpp.model.util.AssignmentUtil;
import cpp.model.util.ClassGroupUtil;

/**
 * Unallocates an existing assignment from contact(s) by their displayed indices
 * or class group.
 */
public class UnallocateAssignmentCommand extends Command {

    public static final String COMMAND_WORD = "unallocass";

    public static final String MESSAGE_USAGE = UnallocateAssignmentCommand.COMMAND_WORD
            + ": Unallocates an assignment from contact(s).\n"
            + "Parameters: "
            + CliSyntax.PREFIX_ASSIGNMENT + "ASSIGNMENT_NAME "
            + "[" + CliSyntax.PREFIX_CLASS + "CLASS_NAME] "
            + "[" + CliSyntax.PREFIX_CONTACT + "CONTACT_INDICES...]\n"
            + "At least one of " + CliSyntax.PREFIX_CLASS + " or " + CliSyntax.PREFIX_CONTACT + " must be provided.\n"
            + "Example: " + UnallocateAssignmentCommand.COMMAND_WORD + " "
            + CliSyntax.PREFIX_ASSIGNMENT + "Assignment 1 "
            + CliSyntax.PREFIX_CLASS + "CS2103T10 "
            + CliSyntax.PREFIX_CONTACT + "1 2 3";

    public static final String MESSAGE_SUCCESS = """
            Unallocated assignment: %1$s from %2$s contact(s).
            Contacts unallocated: %3$s
            Contacts not unallocated (not allocated this assignment initially): %4$s""";
    public static final String MESSAGE_UNALLOCATION_FAILED = """
            No contacts were unallocated the assignment.
            Contacts not unallocated (not allocated this assignment initially): %1$s""";

    private final AssignmentName assignmentName;
    private final List<Index> contactIndices;
    private final Set<Contact> contactsToUnallocate;
    private final ClassGroupName classGroupName;
    private int successfulUnallocationCount;
    private StringBuilder successfulContactUnallocations;
    private int unsuccessfulUnallocationCount;
    private StringBuilder unsuccessfulContactUnallocations;

    private final Logger logger = LogsCenter.getLogger(LogicManager.class);

    /**
     * Creates an UnallocateAssignmentCommand with the specified assignment and
     * contact indices.
     */
    public UnallocateAssignmentCommand(AssignmentName assignmentName, List<Index> contactIndices) {
        Objects.requireNonNull(assignmentName);
        Objects.requireNonNull(contactIndices);
        this.assignmentName = assignmentName;
        this.contactIndices = new ArrayList<>(contactIndices);
        this.contactsToUnallocate = new HashSet<>();
        this.classGroupName = null;
        this.successfulUnallocationCount = 0;
        this.successfulContactUnallocations = new StringBuilder();
        this.unsuccessfulUnallocationCount = 0;
        this.unsuccessfulContactUnallocations = new StringBuilder();
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
        this.contactsToUnallocate = new HashSet<>();
        this.classGroupName = classGroupName;
        this.successfulUnallocationCount = 0;
        this.successfulContactUnallocations = new StringBuilder();
        this.unsuccessfulUnallocationCount = 0;
        this.unsuccessfulContactUnallocations = new StringBuilder();
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        Objects.requireNonNull(model);

        List<Assignment> assignmentList = model.getAddressBook().getAssignmentList();

        Assignment assignmentToUnallocate = AssignmentUtil.findAssignment(assignmentList, this.assignmentName);

        if (assignmentToUnallocate == null) {
            throw new CommandException(Messages.MESSAGE_ASSIGNMENT_NOT_FOUND);
        }

        List<Contact> lastShownContactList = model.getFilteredContactList();

        CommandUtil.checkContactIndices(lastShownContactList, this.contactIndices);

        ClassGroup classGroupToUnallocate = ClassGroupUtil.findClassGroup(model.getAddressBook().getClassGroupList(),
                this.classGroupName);
        if (this.classGroupName != null && classGroupToUnallocate == null) {
            throw new CommandException(Messages.MESSAGE_CLASS_GROUP_NOT_FOUND);
        }

        if (classGroupToUnallocate != null && classGroupToUnallocate.getContactIdSet().isEmpty()) {
            throw new CommandException(Messages.MESSAGE_CLASS_GROUP_NO_CONTACTS);
        }

        this.unallocateFromContactsByContactIndices(model, assignmentToUnallocate, lastShownContactList);
        if (classGroupToUnallocate != null) {
            this.unallocateFromContactsByClassGroup(model, assignmentToUnallocate, classGroupToUnallocate);
        }

        if (this.unsuccessfulUnallocationCount == 0) {
            this.unsuccessfulContactUnallocations.append("None");
        }

        if (this.successfulUnallocationCount == 0) {
            throw new CommandException(String.format(UnallocateAssignmentCommand.MESSAGE_UNALLOCATION_FAILED,
                    this.unsuccessfulContactUnallocations.toString()));
        }

        return new CommandResult(String.format(UnallocateAssignmentCommand.MESSAGE_SUCCESS,
                Messages.format(assignmentToUnallocate), this.successfulUnallocationCount,
                this.successfulContactUnallocations.toString(),
                this.unsuccessfulContactUnallocations.toString()));
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
        if (this.contactsToUnallocate.contains(contact)) {
            // Skip contacts that have already been unallocated the assignment through
            // contact indices in the same command
            return;
        }

        ContactAssignment ca = new ContactAssignment(assignment.getId(), contact.getId());

        try {
            model.removeContactAssignment(ca);
            this.successfulUnallocationCount++;
            this.buildSuccessfulUnallocationString(contact.getName().fullName);
        } catch (ContactAssignmentNotFoundException e) {
            this.unsuccessfulUnallocationCount++;
            this.buildUnsuccessfulUnallocationString(contact.getName().fullName);
            this.logger.info(
                    "Failed to unallocate assignment from contact (not allocated initially): "
                            + contact.getName().fullName);
        }

        this.contactsToUnallocate.add(contact);
    }

    private void buildSuccessfulUnallocationString(String contactName) {
        if (this.successfulContactUnallocations.length() > 0) {
            this.successfulContactUnallocations.append("; ");
        }
        this.successfulContactUnallocations.append(contactName);
    }

    private void buildUnsuccessfulUnallocationString(String contactName) {
        if (this.unsuccessfulContactUnallocations.length() > 0) {
            this.unsuccessfulContactUnallocations.append("; ");
        }
        this.unsuccessfulContactUnallocations.append(contactName);
    }

}
