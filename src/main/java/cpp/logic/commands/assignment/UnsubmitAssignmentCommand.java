package cpp.logic.commands.assignment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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
import cpp.model.assignment.exceptions.ContactAssignmentNotFoundException;
import cpp.model.assignment.exceptions.ContactAssignmentNotSubmittedException;
import cpp.model.classgroup.ClassGroup;
import cpp.model.classgroup.ClassGroupName;
import cpp.model.contact.Contact;
import cpp.model.util.AssignmentUtil;
import cpp.model.util.ClassGroupUtil;

/**
 * Marks an assignment as submitted by contact(s) or class group. The assignment
 * must have been allocated to the contact(s) before it can be submitted.
 */
public class UnsubmitAssignmentCommand extends Command {

    public static final String COMMAND_WORD = "unsubmitass";

    public static final String MESSAGE_USAGE = UnsubmitAssignmentCommand.COMMAND_WORD
            + ": Marks an assignment as unsubmitted by contact(s) or class group. "
            + "Also removes any grade status for the contact(s) for the assignment. "
            + "Parameters: "
            + CliSyntax.PREFIX_ASSIGNMENT + "ASSIGNMENT NAME "
            + "[" + CliSyntax.PREFIX_CLASS + "CLASS NAME] "
            + "[" + CliSyntax.PREFIX_CONTACT + "CONTACT INDICES...]\n"
            + "At least one of " + CliSyntax.PREFIX_CLASS + " or " + CliSyntax.PREFIX_CONTACT + " must be provided.\n"
            + "Example: " + UnsubmitAssignmentCommand.COMMAND_WORD + " "
            + CliSyntax.PREFIX_ASSIGNMENT + "Assignment 1 "
            + CliSyntax.PREFIX_CLASS + "CS2103T10 "
            + CliSyntax.PREFIX_CONTACT + "1 2 3";

    public static final String MESSAGE_SUCCESS = """
            Marked assignment: %1$s as unsubmitted by %2$s contacts.
            Contacts marked unsubmitted: %3$s
            Contacts not marked unsubmitted (not submitted): %4$s
            Contacts not marked unsubmitted (not allocated the assignment): %5$s""";
    public static final String MESSAGE_SUBMISSION_FAILED = "No contacts were marked as unsubmitted for the assignment";

    private final AssignmentName assignmentName;
    private final List<Index> contactIndices;
    private final Set<Contact> contactsToUnmark;
    private final ClassGroupName classGroupName;
    private int unmarkedCount = 0;
    private int alreadyUnmarkedCount = 0;
    private int notAllocatedCount = 0;
    private StringBuilder unmarkedContacts;
    private StringBuilder alreadyUnmarkedContacts;
    private StringBuilder notAllocatedContacts;

    /**
     * Creates an UnsubmitAssignmentCommand to mark the specified assignment as
     * unsubmitted by the specified contacts.
     */
    public UnsubmitAssignmentCommand(AssignmentName assignmentName, List<Index> contactIndices) {
        Objects.requireNonNull(assignmentName);
        Objects.requireNonNull(contactIndices);
        this.assignmentName = assignmentName;
        this.contactIndices = new ArrayList<>(contactIndices);
        this.classGroupName = null;
        this.contactsToUnmark = new HashSet<>();
        this.unmarkedContacts = new StringBuilder();
        this.alreadyUnmarkedContacts = new StringBuilder();
        this.notAllocatedContacts = new StringBuilder();
    }

    /**
     * Creates an UnsubmitAssignmentCommand to mark the specified assignment as
     * unsubmitted by the specified contacts or class group.
     */
    public UnsubmitAssignmentCommand(AssignmentName assignmentName, List<Index> contactIndices,
            ClassGroupName classGroupName) {
        this.assignmentName = assignmentName;
        this.contactIndices = new ArrayList<>(contactIndices);
        this.classGroupName = classGroupName;
        this.contactsToUnmark = new HashSet<>();
        this.unmarkedContacts = new StringBuilder();
        this.alreadyUnmarkedContacts = new StringBuilder();
        this.notAllocatedContacts = new StringBuilder();
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

        this.markUnsubmittedByContactIndices(model, assignmentToUnallocate, lastShownContactList);
        if (classGroupToUnallocate != null) {
            this.markUnsubmittedByClassGroup(model, assignmentToUnallocate, classGroupToUnallocate);
        }

        if (this.unmarkedCount == 0) {
            throw new CommandException(UnsubmitAssignmentCommand.MESSAGE_SUBMISSION_FAILED);
        }

        if (this.alreadyUnmarkedCount == 0) {
            this.alreadyUnmarkedContacts.append("None");
        }

        if (this.notAllocatedCount == 0) {
            this.notAllocatedContacts.append("None");
        }

        return new CommandResult(String.format(UnsubmitAssignmentCommand.MESSAGE_SUCCESS,
                Messages.format(assignmentToUnallocate), this.unmarkedCount, this.unmarkedContacts.toString(),
                this.alreadyUnmarkedContacts.toString(), this.notAllocatedContacts.toString()));

    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof UnsubmitAssignmentCommand)) {
            return false;
        }

        UnsubmitAssignmentCommand otherCommand = (UnsubmitAssignmentCommand) other;
        return this.assignmentName.equals(otherCommand.assignmentName)
                && this.contactIndices.equals(otherCommand.contactIndices)
                && Objects.equals(this.classGroupName, otherCommand.classGroupName);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("assignmentName", this.assignmentName)
                .add("contactIndices", this.contactIndices)
                .add("classGroupName", this.classGroupName)
                .toString();
    }

    private void markUnsubmittedByContactIndices(Model model, Assignment assignmentToUnallocate,
            List<Contact> lastShownContactList) {

        for (Index idx : this.contactIndices) {
            Contact contact = lastShownContactList.get(idx.getZeroBased());

            this.markUnsubmittedByContact(model, assignmentToUnallocate, contact);
        }
    }

    private void markUnsubmittedByClassGroup(Model model, Assignment assignmentToUnallocate,
            ClassGroup classGroupToUnallocate) {
        List<Contact> contactList = model.getAddressBook().getContactList();

        for (String contactId : classGroupToUnallocate.getContactIdSet()) {
            Contact contact = contactList.stream()
                    .filter(c -> c.getId().equals(contactId))
                    .findAny()
                    .orElse(null);

            if (contact != null) {
                this.markUnsubmittedByContact(model, assignmentToUnallocate, contact);
            }
        }
    }

    private void markUnsubmittedByContact(Model model, Assignment assignment, Contact contact) {
        if (this.contactsToUnmark.contains(contact)) {
            // Skip contacts that have already been marked as submitted through
            // contact indices in the same command
            return;
        }

        try {
            model.markUnsubmitted(assignment, contact);
            this.unmarkedCount++;
            this.buildSuccessfulUnmarkString(contact.getName().fullName);
            this.contactsToUnmark.add(contact);

        } catch (ContactAssignmentNotFoundException e) {
            // Skip contacts that don't have the assignment allocated.
            this.notAllocatedCount++;
            this.buildNotAllocatedString(contact.getName().fullName);
        } catch (ContactAssignmentNotSubmittedException e) {
            // Skip contacts that are not currently marked as submitted.
            this.alreadyUnmarkedCount++;
            this.buildAlreadyUnmarkedString(contact.getName().fullName);
        }
    }

    private void buildSuccessfulUnmarkString(String contactName) {
        if (this.unmarkedContacts.length() > 0) {
            this.unmarkedContacts.append("; ");
        }
        this.unmarkedContacts.append(contactName);
    }

    private void buildNotAllocatedString(String contactName) {
        if (this.notAllocatedContacts.length() > 0) {
            this.notAllocatedContacts.append("; ");
        }
        this.notAllocatedContacts.append(contactName);
    }

    private void buildAlreadyUnmarkedString(String contactName) {
        if (this.alreadyUnmarkedContacts.length() > 0) {
            this.alreadyUnmarkedContacts.append("; ");
        }
        this.alreadyUnmarkedContacts.append(contactName);
    }
}
