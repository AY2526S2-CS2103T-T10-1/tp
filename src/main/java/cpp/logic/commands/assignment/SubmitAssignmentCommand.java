package cpp.logic.commands.assignment;

import java.time.LocalDateTime;
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
import cpp.logic.parser.ParserUtil;
import cpp.model.Model;
import cpp.model.assignment.Assignment;
import cpp.model.assignment.AssignmentName;
import cpp.model.assignment.exceptions.ContactAssignmentAlreadySubmittedException;
import cpp.model.assignment.exceptions.ContactAssignmentNotFoundException;
import cpp.model.classgroup.ClassGroup;
import cpp.model.classgroup.ClassGroupName;
import cpp.model.contact.Contact;
import cpp.model.util.AssignmentUtil;
import cpp.model.util.ClassGroupUtil;

/**
 * Marks an assignment as submitted by contact(s) or class group. The assignment
 * must have been allocated to the contact(s) before it can be submitted.
 */
public class SubmitAssignmentCommand extends Command {

    public static final String COMMAND_WORD = "submit";

    public static final String MESSAGE_USAGE = SubmitAssignmentCommand.COMMAND_WORD
            + ": Marks an assignment as submitted by contact(s). "
            + "If no submission date is provided, the current date and time will be used.\n"
            + "Parameters: "
            + CliSyntax.PREFIX_ASSIGNMENT + "ASSIGNMENT_NAME "
            + "[" + CliSyntax.PREFIX_CLASS + "CLASS_NAME] "
            + "[" + CliSyntax.PREFIX_CONTACT + "CONTACT_INDICES...] "
            + "[" + CliSyntax.PREFIX_DATETIME + "SUBMISSION_DATE]\n"
            + "At least one of " + CliSyntax.PREFIX_CLASS + " or " + CliSyntax.PREFIX_CONTACT + " must be provided.\n"
            + "Example: " + SubmitAssignmentCommand.COMMAND_WORD + " "
            + CliSyntax.PREFIX_ASSIGNMENT + "Assignment 1 "
            + CliSyntax.PREFIX_CLASS + "CS2103T10 "
            + CliSyntax.PREFIX_CONTACT + "1 2 3 "
            + CliSyntax.PREFIX_DATETIME + "21-02-2026 23:50";

    public static final String MESSAGE_SUCCESS = """
            Marked assignment: %1$s as submitted on %2$s by %3$s contact(s).
            Contacts marked submitted: %4$s
            Contacts not marked submitted (already submitted): %5$s
            Contacts not marked submitted (not allocated the assignment): %6$s""";
    public static final String MESSAGE_SUBMISSION_FAILED = """
            No contacts were marked as submitted for the assignment.
            Contacts not marked submitted (already submitted): %1$s
            Contacts not marked submitted (not allocated the assignment): %2$s""";

    private final AssignmentName assignmentName;
    private final List<Index> contactIndices;
    private final Set<Contact> contactsToMark;
    private final ClassGroupName classGroupName;
    private final LocalDateTime submissionDate;

    private int markedCount = 0;
    private int alreadyMarkedCount = 0;
    private int notAllocatedCount = 0;
    private StringBuilder markedContacts;
    private StringBuilder alreadyMarkedContacts;
    private StringBuilder notAllocatedContacts;

    private final Logger logger = LogsCenter.getLogger(LogicManager.class);

    /**
     * Creates a SubmitAssignmentCommand to mark the specified assignment as
     * submitted by the specified contacts.
     */
    public SubmitAssignmentCommand(AssignmentName assignmentName, List<Index> contactIndices,
            LocalDateTime submissionDate) {
        Objects.requireNonNull(assignmentName);
        Objects.requireNonNull(contactIndices);
        Objects.requireNonNull(submissionDate);
        this.assignmentName = assignmentName;
        this.contactIndices = new ArrayList<>(contactIndices);
        this.classGroupName = null;
        this.contactsToMark = new HashSet<>();
        this.markedContacts = new StringBuilder();
        this.alreadyMarkedContacts = new StringBuilder();
        this.notAllocatedContacts = new StringBuilder();
        this.submissionDate = submissionDate;
    }

    /**
     * Creates a SubmitAssignmentCommand to mark the specified assignment as
     * submitted by the specified contacts or class group.
     */
    public SubmitAssignmentCommand(AssignmentName assignmentName, List<Index> contactIndices,
            ClassGroupName classGroupName, LocalDateTime submissionDate) {
        Objects.requireNonNull(assignmentName);
        Objects.requireNonNull(contactIndices);
        Objects.requireNonNull(classGroupName);
        Objects.requireNonNull(submissionDate);
        this.assignmentName = assignmentName;
        this.contactIndices = new ArrayList<>(contactIndices);
        this.classGroupName = classGroupName;
        this.contactsToMark = new HashSet<>();
        this.markedContacts = new StringBuilder();
        this.alreadyMarkedContacts = new StringBuilder();
        this.notAllocatedContacts = new StringBuilder();
        this.submissionDate = submissionDate;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        Objects.requireNonNull(model);

        List<Assignment> assignmentList = model.getAddressBook().getAssignmentList();

        Assignment assignmentToSubmit = AssignmentUtil.findAssignment(assignmentList, this.assignmentName);

        if (assignmentToSubmit == null) {
            throw new CommandException(Messages.MESSAGE_ASSIGNMENT_NOT_FOUND);
        }

        List<Contact> lastShownContactList = model.getFilteredContactList();

        CommandUtil.checkContactIndices(lastShownContactList, this.contactIndices);

        ClassGroup classGroupToMark = ClassGroupUtil.findClassGroup(model.getAddressBook().getClassGroupList(),
                this.classGroupName);
        if (this.classGroupName != null && classGroupToMark == null) {
            throw new CommandException(Messages.MESSAGE_CLASS_GROUP_NOT_FOUND);
        }

        if (classGroupToMark != null && classGroupToMark.getContactIdSet().isEmpty()) {
            throw new CommandException(Messages.MESSAGE_CLASS_GROUP_NO_CONTACTS);
        }

        this.markSubmittedByContactIndices(model, assignmentToSubmit, lastShownContactList);
        if (classGroupToMark != null) {
            this.markSubmittedByClassGroup(model, assignmentToSubmit, classGroupToMark);
        }

        if (this.alreadyMarkedCount == 0) {
            this.alreadyMarkedContacts.append("None");
        }

        if (this.notAllocatedCount == 0) {
            this.notAllocatedContacts.append("None");
        }

        if (this.markedCount == 0) {
            throw new CommandException(String.format(SubmitAssignmentCommand.MESSAGE_SUBMISSION_FAILED,
                    this.alreadyMarkedContacts.toString(), this.notAllocatedContacts.toString()));
        }

        return new CommandResult(String.format(SubmitAssignmentCommand.MESSAGE_SUCCESS,
                Messages.format(assignmentToSubmit), this.submissionDate.format(ParserUtil.DATETIME_FORMATTER),
                this.markedCount,
                this.markedContacts.toString(), this.alreadyMarkedContacts.toString(),
                this.notAllocatedContacts.toString()));

    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof SubmitAssignmentCommand)) {
            return false;
        }

        SubmitAssignmentCommand otherCommand = (SubmitAssignmentCommand) other;
        return this.assignmentName.equals(otherCommand.assignmentName)
                && this.contactIndices.equals(otherCommand.contactIndices)
                && Objects.equals(this.classGroupName, otherCommand.classGroupName)
                && this.submissionDate.equals(otherCommand.submissionDate);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("assignmentName", this.assignmentName)
                .add("contactIndices", this.contactIndices)
                .add("classGroupName", this.classGroupName)
                .add("submissionDate", this.submissionDate.format(ParserUtil.DATETIME_FORMATTER))
                .toString();
    }

    private void markSubmittedByContactIndices(Model model, Assignment assignmentToSubmit,
            List<Contact> lastShownContactList) {

        for (Index idx : this.contactIndices) {
            Contact contact = lastShownContactList.get(idx.getZeroBased());

            this.markSubmittedByContact(model, assignmentToSubmit, contact);
        }
    }

    private void markSubmittedByClassGroup(Model model, Assignment assignmentToSubmit,
            ClassGroup classGroupToMark) {
        List<Contact> contactList = model.getAddressBook().getContactList();

        for (String contactId : classGroupToMark.getContactIdSet()) {
            Contact contact = contactList.stream()
                    .filter(c -> c.getId().equals(contactId))
                    .findAny()
                    .orElse(null);

            if (contact != null) {
                this.markSubmittedByContact(model, assignmentToSubmit, contact);
            }
        }
    }

    private void markSubmittedByContact(Model model, Assignment assignment, Contact contact) {
        if (this.contactsToMark.contains(contact)) {
            // Skip contacts that have already been marked as submitted through
            // contact indices in the same command
            return;
        }

        try {
            model.markSubmitted(assignment, contact, this.submissionDate);
            this.markedCount++;
            this.buildSuccessfulMarkString(contact.getName().fullName);

        } catch (ContactAssignmentNotFoundException e) {
            this.notAllocatedCount++;
            this.buildNotAllocatedString(contact.getName().fullName);
            this.logger.info(
                    "Contact not marked as submitted (not allocated to assignment): " + contact.getName().fullName);
        } catch (ContactAssignmentAlreadySubmittedException e) {
            this.alreadyMarkedCount++;
            this.buildAlreadyMarkedString(contact.getName().fullName);
            this.logger.info("Contact not marked as submitted for assignment (already submitted): "
                    + contact.getName().fullName);
        }

        this.contactsToMark.add(contact);
    }

    private void buildSuccessfulMarkString(String contactName) {
        if (this.markedContacts.length() > 0) {
            this.markedContacts.append("; ");
        }
        this.markedContacts.append(contactName);
    }

    private void buildNotAllocatedString(String contactName) {
        if (this.notAllocatedContacts.length() > 0) {
            this.notAllocatedContacts.append("; ");
        }
        this.notAllocatedContacts.append(contactName);
    }

    private void buildAlreadyMarkedString(String contactName) {
        if (this.alreadyMarkedContacts.length() > 0) {
            this.alreadyMarkedContacts.append("; ");
        }
        this.alreadyMarkedContacts.append(contactName);
    }
}
