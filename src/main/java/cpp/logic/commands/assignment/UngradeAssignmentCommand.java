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
import cpp.model.assignment.exceptions.ContactAssignmentNotGradedException;
import cpp.model.assignment.exceptions.ContactAssignmentNotSubmittedException;
import cpp.model.classgroup.ClassGroup;
import cpp.model.classgroup.ClassGroupName;
import cpp.model.contact.Contact;
import cpp.model.util.AssignmentUtil;
import cpp.model.util.ClassGroupUtil;

/**
 * Ungrades an assignment for a contact. The assignment must have been submitted
 * and graded before it can be ungraded.
 */
public class UngradeAssignmentCommand extends Command {

    public static final String COMMAND_WORD = "ungrade";

    public static final String MESSAGE_USAGE = UngradeAssignmentCommand.COMMAND_WORD
            + ": Ungrades an assignment for contact(s)."
            + " Parameters: "
            + CliSyntax.PREFIX_ASSIGNMENT + "ASSIGNMENT_NAME "
            + "[" + CliSyntax.PREFIX_CLASS + "CLASS_NAME] "
            + "[" + CliSyntax.PREFIX_CONTACT + "CONTACT_INDICES...]\n"
            + "At least one of " + CliSyntax.PREFIX_CLASS + " or " + CliSyntax.PREFIX_CONTACT + " must be provided.\n"
            + "Example: " + UngradeAssignmentCommand.COMMAND_WORD + " "
            + CliSyntax.PREFIX_ASSIGNMENT + "Assignment 1 "
            + CliSyntax.PREFIX_CLASS + "CS2103T10 "
            + CliSyntax.PREFIX_CONTACT + "1 2 3";

    public static final String MESSAGE_SUCCESS = """
            Ungraded assignment: %1$s for %2$s contact(s).
            Contacts ungraded: %3$s
            Contacts not ungraded (not graded yet): %4$s
            Contacts not ungraded (not submitted yet): %5$s
            Contacts not ungraded (not allocated the assignment): %6$s""";
    public static final String MESSAGE_GRADE_FAILED = """
            Failed to ungrade any contacts for the assignment.
            Contacts not ungraded (not graded yet): %1$s
            Contacts not ungraded (not submitted yet): %2$s
            Contacts not ungraded (not allocated the assignment): %3$s""";

    private final AssignmentName assignmentName;
    private final ClassGroupName classGroupName;
    private final List<Index> contactIndices;
    private final Set<Contact> contactsToUngrade = new HashSet<>();

    private int ungradedCount = 0;
    private int notGradedCount = 0;
    private int notSubmittedCount = 0;
    private int notAllocatedCount = 0;
    private StringBuilder ungradedContacts = new StringBuilder();
    private StringBuilder notGradedContacts = new StringBuilder();
    private StringBuilder notSubmittedContacts = new StringBuilder();
    private StringBuilder notAllocatedContacts = new StringBuilder();

    private final Logger logger = LogsCenter.getLogger(LogicManager.class);

    /**
     * Creates a UngradeAssignmentCommand to ungrade the specified assignment for
     * the specified contacts.
     */
    public UngradeAssignmentCommand(AssignmentName assignmentName, List<Index> contactIndices) {
        Objects.requireNonNull(assignmentName);
        Objects.requireNonNull(contactIndices);
        this.assignmentName = assignmentName;
        this.contactIndices = new ArrayList<>(contactIndices);
        this.classGroupName = null;
    }

    /**
     * Creates a UngradeAssignmentCommand to ungrade the specified assignment for
     * the specified contacts or class group.
     */
    public UngradeAssignmentCommand(AssignmentName assignmentName, List<Index> contactIndices,
            ClassGroupName classGroupName) {
        Objects.requireNonNull(assignmentName);
        Objects.requireNonNull(contactIndices);
        Objects.requireNonNull(classGroupName);
        this.assignmentName = assignmentName;
        this.contactIndices = new ArrayList<>(contactIndices);
        this.classGroupName = classGroupName;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        Objects.requireNonNull(model);

        List<Assignment> assignmentList = model.getAddressBook().getAssignmentList();

        Assignment assignmentToUngrade = AssignmentUtil.findAssignment(assignmentList, this.assignmentName);

        if (assignmentToUngrade == null) {
            throw new CommandException(Messages.MESSAGE_ASSIGNMENT_NOT_FOUND);
        }

        List<Contact> lastShownContactList = model.getFilteredContactList();

        CommandUtil.checkContactIndices(lastShownContactList, this.contactIndices);

        ClassGroup classGroupToUngrade = ClassGroupUtil.findClassGroup(model.getAddressBook().getClassGroupList(),
                this.classGroupName);
        if (this.classGroupName != null && classGroupToUngrade == null) {
            throw new CommandException(Messages.MESSAGE_CLASS_GROUP_NOT_FOUND);
        }

        if (classGroupToUngrade != null && classGroupToUngrade.getContactIdSet().isEmpty()) {
            throw new CommandException(Messages.MESSAGE_CLASS_GROUP_NO_CONTACTS);
        }

        this.ungradeByContactIndices(model, assignmentToUngrade, lastShownContactList);
        if (classGroupToUngrade != null) {
            this.ungradeByClassGroup(model, assignmentToUngrade, classGroupToUngrade);
        }

        if (this.notGradedCount == 0) {
            this.notGradedContacts.append("None");
        }

        if (this.notSubmittedCount == 0) {
            this.notSubmittedContacts.append("None");
        }

        if (this.notAllocatedCount == 0) {
            this.notAllocatedContacts.append("None");
        }

        if (this.ungradedCount == 0) {
            throw new CommandException(
                    String.format(UngradeAssignmentCommand.MESSAGE_GRADE_FAILED, this.notGradedContacts.toString(),
                            this.notSubmittedContacts.toString(), this.notAllocatedContacts.toString()));
        }

        return new CommandResult(
                String.format(UngradeAssignmentCommand.MESSAGE_SUCCESS, Messages.format(assignmentToUngrade),
                        this.ungradedCount, this.ungradedContacts.toString(), this.notGradedContacts.toString(),
                        this.notSubmittedContacts.toString(), this.notAllocatedContacts.toString()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof UngradeAssignmentCommand)) {
            return false;
        }
        UngradeAssignmentCommand o = (UngradeAssignmentCommand) other;
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

    private void ungradeByContactIndices(Model model, Assignment assignmentToUnallocate,
            List<Contact> lastShownContactList) {

        for (Index idx : this.contactIndices) {
            Contact contact = lastShownContactList.get(idx.getZeroBased());

            this.ungradeByContact(model, assignmentToUnallocate, contact);
        }
    }

    private void ungradeByClassGroup(Model model, Assignment assignmentToUngrade, ClassGroup classGroupToUngrade) {
        List<Contact> contactList = model.getAddressBook().getContactList();

        for (String contactId : classGroupToUngrade.getContactIdSet()) {
            Contact contact = contactList.stream()
                    .filter(c -> c.getId().equals(contactId))
                    .findAny()
                    .orElse(null);

            if (contact != null) {
                this.ungradeByContact(model, assignmentToUngrade, contact);
            }
        }
    }

    private void ungradeByContact(Model model, Assignment assignment, Contact contact) {
        if (this.contactsToUngrade.contains(contact)) {
            // Skip contacts that have already been marked as ungraded through
            // contact indices in the same command
            return;
        }

        try {
            model.ungrade(assignment, contact);
            this.ungradedCount++;
            this.buildSuccessfulUngradeString(contact.getName().fullName);

        } catch (ContactAssignmentNotFoundException e) {
            this.notAllocatedCount++;
            this.buildNotAllocatedString(contact.getName().fullName);
            this.logger.info(
                    "Contact not marked as ungraded (not allocated to assignment): " + contact.getName().fullName);
        } catch (ContactAssignmentNotSubmittedException e) {
            this.notSubmittedCount++;
            this.buildNotSubmittedString(contact.getName().fullName);
            this.logger.info("Contact not marked as ungraded (not submitted): " + contact.getName().fullName);

        } catch (ContactAssignmentNotGradedException e) {
            this.notGradedCount++;
            this.buildNotGradedString(contact.getName().fullName);
            this.logger.info("Contact not marked as ungraded (not graded): " + contact.getName().fullName);
        }

        this.contactsToUngrade.add(contact);
    }

    private void buildSuccessfulUngradeString(String contactName) {
        if (this.ungradedContacts.length() > 0) {
            this.ungradedContacts.append("; ");
        }
        this.ungradedContacts.append(contactName);
    }

    private void buildNotGradedString(String contactName) {
        if (this.notGradedContacts.length() > 0) {
            this.notGradedContacts.append("; ");
        }
        this.notGradedContacts.append(contactName);
    }

    private void buildNotSubmittedString(String contactName) {
        if (this.notSubmittedContacts.length() > 0) {
            this.notSubmittedContacts.append("; ");
        }
        this.notSubmittedContacts.append(contactName);
    }

    private void buildNotAllocatedString(String contactName) {
        if (this.notAllocatedContacts.length() > 0) {
            this.notAllocatedContacts.append("; ");
        }
        this.notAllocatedContacts.append(contactName);
    }
}
