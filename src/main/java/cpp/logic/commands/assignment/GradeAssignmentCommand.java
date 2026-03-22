package cpp.logic.commands.assignment;

import java.time.LocalDateTime;
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
import cpp.logic.parser.ParserUtil;
import cpp.model.Model;
import cpp.model.assignment.Assignment;
import cpp.model.assignment.AssignmentName;
import cpp.model.assignment.exceptions.ContactAssignmentAlreadyGradedException;
import cpp.model.assignment.exceptions.ContactAssignmentNotFoundException;
import cpp.model.assignment.exceptions.ContactAssignmentNotSubmittedException;
import cpp.model.contact.Contact;
import cpp.model.util.AssignmentUtil;

/**
 * Grades an assignment for a contact. The assignment must have been submitted
 * before it can be graded.
 */
public class GradeAssignmentCommand extends Command {

    public static final String COMMAND_WORD = "gradeass";

    public static final String MESSAGE_USAGE = GradeAssignmentCommand.COMMAND_WORD
            + ": Grades an assignment for a contact with a score. "
            + "If no grading date is provided, the current date and time will be used.\n"
            + "Parameters: "
            + CliSyntax.PREFIX_ASSIGNMENT + "ASSIGNMENT NAME "
            + CliSyntax.PREFIX_CONTACT + "CONTACT INDICES... "
            + CliSyntax.PREFIX_SCORE + "SCORE "
            + "[" + CliSyntax.PREFIX_DATETIME + "GRADING DATE]\n"
            + "Example: " + GradeAssignmentCommand.COMMAND_WORD + " "
            + CliSyntax.PREFIX_ASSIGNMENT + "Assignment 1 "
            + CliSyntax.PREFIX_CONTACT + "1 2 3 "
            + CliSyntax.PREFIX_SCORE + "67.9 "
            + CliSyntax.PREFIX_DATETIME + "21-02-2026 23:50";

    public static final String MESSAGE_SUCCESS = """
            Graded assignment: %1$s on %2$s for %3$s contact(s) with score %4$.2f.
            Contacts graded: %5$s
            Contacts not graded (already graded): %6$s
            Contacts not graded (not submitted yet): %7$s
            Contacts not graded (not allocated the assignment): %8$s""";
    public static final String MESSAGE_GRADE_FAILED = """
            Failed to grade any contacts for the assignment.
            Contacts not graded (already graded): %1$s
            Contacts not graded (not submitted yet): %2$s
            Contacts not graded (not allocated the assignment): %3$s""";

    private final AssignmentName assignmentName;
    private final List<Index> contactIndices;
    private final float score;
    private final LocalDateTime gradingDate;

    private int gradedCount = 0;
    private int alreadyGradedCount = 0;
    private int notSubmittedCount = 0;
    private int notAllocatedCount = 0;
    private StringBuilder gradedContacts = new StringBuilder();
    private StringBuilder alreadyGradedContacts = new StringBuilder();
    private StringBuilder notSubmittedContacts = new StringBuilder();
    private StringBuilder notAllocatedContacts = new StringBuilder();

    /**
     * Creates a GradeAssignmentCommand to grade the specified assignment for the
     * specified contacts with the given grade info.
     */
    public GradeAssignmentCommand(AssignmentName assignmentName, List<Index> contactIndices, float score,
            LocalDateTime gradingDate) {
        this.assignmentName = assignmentName;
        this.contactIndices = new ArrayList<>(contactIndices);
        this.score = score;
        this.gradingDate = gradingDate;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        Objects.requireNonNull(model);

        List<Assignment> assignmentList = model.getAddressBook().getAssignmentList();

        Assignment assignmentToGrade = AssignmentUtil.findAssignment(assignmentList, this.assignmentName);

        if (assignmentToGrade == null) {
            throw new CommandException(Messages.MESSAGE_ASSIGNMENT_NOT_FOUND);
        }

        List<Contact> lastShownContactList = model.getFilteredContactList();

        CommandUtil.checkContactIndices(lastShownContactList, this.contactIndices);

        this.gradeByContactIndices(model, assignmentToGrade, lastShownContactList);

        if (this.alreadyGradedCount == 0) {
            this.alreadyGradedContacts.append("None");
        }

        if (this.notSubmittedCount == 0) {
            this.notSubmittedContacts.append("None");
        }

        if (this.notAllocatedCount == 0) {
            this.notAllocatedContacts.append("None");
        }

        if (this.gradedCount == 0) {
            throw new CommandException(
                    String.format(GradeAssignmentCommand.MESSAGE_GRADE_FAILED, this.alreadyGradedContacts.toString(),
                            this.notSubmittedContacts.toString(), this.notAllocatedContacts.toString()));
        }

        return new CommandResult(
                String.format(GradeAssignmentCommand.MESSAGE_SUCCESS, Messages.format(assignmentToGrade),
                        this.gradingDate.format(ParserUtil.DATETIME_FORMATTER), this.gradedCount, this.score,
                        this.gradedContacts.toString(), this.alreadyGradedContacts.toString(),
                        this.notSubmittedContacts.toString(), this.notAllocatedContacts.toString()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof GradeAssignmentCommand)) {
            return false;
        }
        GradeAssignmentCommand o = (GradeAssignmentCommand) other;
        return this.assignmentName.equals(o.assignmentName)
                && this.contactIndices.equals(o.contactIndices)
                && Float.compare(this.score, o.score) == 0;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("assignmentName", this.assignmentName)
                .add("contactIndices", this.contactIndices)
                .add("score", this.score)
                .toString();
    }

    private void gradeByContactIndices(Model model, Assignment assignmentToUnallocate,
            List<Contact> lastShownContactList) {

        for (Index idx : this.contactIndices) {
            Contact contact = lastShownContactList.get(idx.getZeroBased());

            this.gradeByContact(model, assignmentToUnallocate, contact);
        }
    }

    private void gradeByContact(Model model, Assignment assignment, Contact contact) {
        try {
            model.grade(assignment, contact, this.score, this.gradingDate);
            this.gradedCount++;
            this.buildSuccessfulGradeString(contact.getName().fullName);

        } catch (ContactAssignmentNotFoundException e) {
            // Skip contacts that don't have the assignment allocated.
            this.notAllocatedCount++;
            this.buildNotAllocatedString(contact.getName().fullName);
        } catch (ContactAssignmentNotSubmittedException e) {
            // Skip contacts that have not submitted the assignment.
            this.notSubmittedCount++;
            this.buildNotSubmittedString(contact.getName().fullName);

        } catch (ContactAssignmentAlreadyGradedException e) {
            // Skip contacts that have already been marked as graded.
            this.alreadyGradedCount++;
            this.buildAlreadyGradedString(contact.getName().fullName);
        }
    }

    private void buildSuccessfulGradeString(String contactName) {
        if (this.gradedContacts.length() > 0) {
            this.gradedContacts.append(", ");
        }
        this.gradedContacts.append(contactName);
    }

    private void buildAlreadyGradedString(String contactName) {
        if (this.alreadyGradedContacts.length() > 0) {
            this.alreadyGradedContacts.append(", ");
        }
        this.alreadyGradedContacts.append(contactName);
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
