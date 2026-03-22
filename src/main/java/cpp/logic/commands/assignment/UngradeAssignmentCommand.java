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
import cpp.model.assignment.exceptions.ContactAssignmentNotFoundException;
import cpp.model.assignment.exceptions.ContactAssignmentNotGradedException;
import cpp.model.assignment.exceptions.ContactAssignmentNotSubmittedException;
import cpp.model.contact.Contact;
import cpp.model.util.AssignmentUtil;

/**
 * Grades an assignment for a contact. The assignment must have been submitted
 * before it can be graded.
 */
public class UngradeAssignmentCommand extends Command {

    public static final String COMMAND_WORD = "ungradeass";

    public static final String MESSAGE_USAGE = UngradeAssignmentCommand.COMMAND_WORD
            + ": Ungrades an assignment for a contact."
            + "Parameters: "
            + CliSyntax.PREFIX_ASSIGNMENT + "ASSIGNMENT NAME "
            + CliSyntax.PREFIX_CONTACT + "CONTACT INDICES... "
            + "Example: " + UngradeAssignmentCommand.COMMAND_WORD + " "
            + CliSyntax.PREFIX_ASSIGNMENT + "Assignment 1 "
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
    private final List<Index> contactIndices;

    private int ungradedCount = 0;
    private int notGradedCount = 0;
    private int notSubmittedCount = 0;
    private int notAllocatedCount = 0;
    private StringBuilder ungradedContacts = new StringBuilder();
    private StringBuilder notGradedContacts = new StringBuilder();
    private StringBuilder notSubmittedContacts = new StringBuilder();
    private StringBuilder notAllocatedContacts = new StringBuilder();

    /**
     * Creates a UngradeAssignmentCommand to ungrade the specified assignment for
     * the specified contacts.
     */
    public UngradeAssignmentCommand(AssignmentName assignmentName, List<Index> contactIndices) {
        this.assignmentName = assignmentName;
        this.contactIndices = new ArrayList<>(contactIndices);
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

        this.ungradeByContactIndices(model, assignmentToUngrade, lastShownContactList);

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
                    String.format(GradeAssignmentCommand.MESSAGE_GRADE_FAILED, this.notGradedContacts.toString(),
                            this.notSubmittedContacts.toString(), this.notAllocatedContacts.toString()));
        }

        return new CommandResult(
                String.format(GradeAssignmentCommand.MESSAGE_SUCCESS, Messages.format(assignmentToUngrade),
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
                && this.contactIndices.equals(o.contactIndices);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("assignmentName", this.assignmentName)
                .add("contactIndices", this.contactIndices)
                .toString();
    }

    private void ungradeByContactIndices(Model model, Assignment assignmentToUnallocate,
            List<Contact> lastShownContactList) {

        for (Index idx : this.contactIndices) {
            Contact contact = lastShownContactList.get(idx.getZeroBased());

            this.ungradeByContact(model, assignmentToUnallocate, contact);
        }
    }

    private void ungradeByContact(Model model, Assignment assignment, Contact contact) {
        try {
            model.ungrade(assignment, contact);
            this.ungradedCount++;
            this.buildSuccessfulUngradeString(contact.getName().fullName);

        } catch (ContactAssignmentNotFoundException e) {
            // Skip contacts that don't have the assignment allocated.
            this.notAllocatedCount++;
            this.buildNotAllocatedString(contact.getName().fullName);
        } catch (ContactAssignmentNotSubmittedException e) {
            // Skip contacts that have not submitted the assignment.
            this.notSubmittedCount++;
            this.buildNotSubmittedString(contact.getName().fullName);

        } catch (ContactAssignmentNotGradedException e) {
            // Skip contacts that are already not graded.
            this.notGradedCount++;
            this.buildNotGradedString(contact.getName().fullName);
        }
    }

    private void buildSuccessfulUngradeString(String contactName) {
        if (this.ungradedContacts.length() > 0) {
            this.ungradedContacts.append(", ");
        }
        this.ungradedContacts.append(contactName);
    }

    private void buildNotGradedString(String contactName) {
        if (this.notGradedContacts.length() > 0) {
            this.notGradedContacts.append(", ");
        }
        this.notGradedContacts.append(contactName);
    }

    private void buildNotSubmittedString(String contactName) {
        if (this.notSubmittedContacts.length() > 0) {
            this.notSubmittedContacts.append(", ");
        }
        this.notSubmittedContacts.append(contactName);
    }

    private void buildNotAllocatedString(String contactName) {
        if (this.notAllocatedContacts.length() > 0) {
            this.notAllocatedContacts.append(", ");
        }
        this.notAllocatedContacts.append(contactName);
    }
}
