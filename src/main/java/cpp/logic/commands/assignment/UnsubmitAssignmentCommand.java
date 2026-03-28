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
import cpp.model.assignment.exceptions.ContactAssignmentNotFoundException;
import cpp.model.assignment.exceptions.ContactAssignmentNotSubmittedException;
import cpp.model.classgroup.ClassGroup;
import cpp.model.classgroup.ClassGroupName;
import cpp.model.contact.Contact;
import cpp.model.util.AssignmentUtil;
import cpp.model.util.ClassGroupUtil;

/**
 * Marks an assignment as unsubmitted by contact(s) or class group. The
 * assignment must have been allocated and submitted by the contact(s) before it
 * can be unsubmitted.
 */
public class UnsubmitAssignmentCommand extends Command {

    public static final String COMMAND_WORD = "unsubmit";

    public static final String MESSAGE_USAGE = UnsubmitAssignmentCommand.COMMAND_WORD
            + ": Marks an assignment as unsubmitted by contact(s). "
            + "Also removes any grade status for the contact(s) for the assignment.\n"
            + "Parameters: "
            + CliSyntax.PREFIX_ASSIGNMENT + "ASSIGNMENT_NAME "
            + "[" + CliSyntax.PREFIX_CLASS + "CLASS_NAME] "
            + "[" + CliSyntax.PREFIX_CONTACT + "CONTACT_INDICES...]\n"
            + "At least one of " + CliSyntax.PREFIX_CLASS + " or " + CliSyntax.PREFIX_CONTACT + " must be provided.\n"
            + "Example: " + UnsubmitAssignmentCommand.COMMAND_WORD + " "
            + CliSyntax.PREFIX_ASSIGNMENT + "Assignment 1 "
            + CliSyntax.PREFIX_CLASS + "CS2103T10 "
            + CliSyntax.PREFIX_CONTACT + "1 2 3";

    public static final String MESSAGE_SUCCESS = """
            Marked assignment: %1$s as unsubmitted by %2$s contact(s).
            Contacts marked unsubmitted: %3$s
            Contacts not marked unsubmitted (not submitted): %4$s
            Contacts not marked unsubmitted (not allocated the assignment): %5$s""";
    public static final String MESSAGE_SUBMISSION_FAILED = """
            No contacts were marked as unsubmitted for the assignment.
            Contacts not marked unsubmitted (not submitted): %1$s
            Contacts not marked unsubmitted (not allocated the assignment): %2$s""";

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

    private final Logger logger = LogsCenter.getLogger(LogicManager.class);

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
        Objects.requireNonNull(assignmentName);
        Objects.requireNonNull(contactIndices);
        Objects.requireNonNull(classGroupName);
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

        Assignment assignmentToUnsubmit = AssignmentUtil.findAssignment(assignmentList, this.assignmentName);

        if (assignmentToUnsubmit == null) {
            throw new CommandException(Messages.MESSAGE_ASSIGNMENT_NOT_FOUND);
        }

        List<Contact> lastShownContactList = model.getFilteredContactList();

        CommandUtil.checkContactIndices(lastShownContactList, this.contactIndices);

        ClassGroup classGroupToUnmark = ClassGroupUtil.findClassGroup(model.getAddressBook().getClassGroupList(),
                this.classGroupName);
        if (this.classGroupName != null && classGroupToUnmark == null) {
            throw new CommandException(Messages.MESSAGE_CLASS_GROUP_NOT_FOUND);
        }

        if (classGroupToUnmark != null && classGroupToUnmark.getContactIdSet().isEmpty()) {
            throw new CommandException(Messages.MESSAGE_CLASS_GROUP_NO_CONTACTS);
        }

        this.markUnsubmittedByContactIndices(model, assignmentToUnsubmit, lastShownContactList);
        if (classGroupToUnmark != null) {
            this.markUnsubmittedByClassGroup(model, assignmentToUnsubmit, classGroupToUnmark);
        }

        if (this.alreadyUnmarkedCount == 0) {
            this.alreadyUnmarkedContacts.append("None");
        }

        if (this.notAllocatedCount == 0) {
            this.notAllocatedContacts.append("None");
        }

        if (this.unmarkedCount == 0) {
            throw new CommandException(String.format(UnsubmitAssignmentCommand.MESSAGE_SUBMISSION_FAILED,
                    this.alreadyUnmarkedContacts.toString(), this.notAllocatedContacts.toString()));
        }

        return new CommandResult(String.format(UnsubmitAssignmentCommand.MESSAGE_SUCCESS,
                Messages.format(assignmentToUnsubmit), this.unmarkedCount, this.unmarkedContacts.toString(),
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

    private void markUnsubmittedByContactIndices(Model model, Assignment assignmentToUnsubmit,
            List<Contact> lastShownContactList) {

        for (Index idx : this.contactIndices) {
            Contact contact = lastShownContactList.get(idx.getZeroBased());

            this.markUnsubmittedByContact(model, assignmentToUnsubmit, contact);
        }
    }

    private void markUnsubmittedByClassGroup(Model model, Assignment assignmentToUnsubmit,
            ClassGroup classGroupToUnmark) {
        List<Contact> contactList = model.getAddressBook().getContactList();

        for (String contactId : classGroupToUnmark.getContactIdSet()) {
            Contact contact = contactList.stream()
                    .filter(c -> c.getId().equals(contactId))
                    .findAny()
                    .orElse(null);

            if (contact != null) {
                this.markUnsubmittedByContact(model, assignmentToUnsubmit, contact);
            }
        }
    }

    private void markUnsubmittedByContact(Model model, Assignment assignment, Contact contact) {
        if (this.contactsToUnmark.contains(contact)) {
            // Skip contacts that have already been marked as unsubmitted through
            // contact indices in the same command
            return;
        }

        try {
            model.markUnsubmitted(assignment, contact);
            this.unmarkedCount++;
            this.buildSuccessfulUnmarkString(contact.getName().fullName);

        } catch (ContactAssignmentNotFoundException e) {
            this.notAllocatedCount++;
            this.buildNotAllocatedString(contact.getName().fullName);
            this.logger.info(
                    "Contact not marked as unsubmitted (not allocated to assignment): " + contact.getName().fullName);
        } catch (ContactAssignmentNotSubmittedException e) {
            this.alreadyUnmarkedCount++;
            this.buildAlreadyUnmarkedString(contact.getName().fullName);
            this.logger.info(
                    "Contact not marked as unsubmitted (not submitted): " + contact.getName().fullName);
        }

        this.contactsToUnmark.add(contact);
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
