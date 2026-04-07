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
import cpp.model.assignment.exceptions.ContactAssignmentAlreadyGradedException;
import cpp.model.assignment.exceptions.ContactAssignmentGradedBeforeSubmissionException;
import cpp.model.assignment.exceptions.ContactAssignmentNotFoundException;
import cpp.model.assignment.exceptions.ContactAssignmentNotSubmittedException;
import cpp.model.classgroup.ClassGroup;
import cpp.model.classgroup.ClassGroupName;
import cpp.model.contact.Contact;
import cpp.model.util.AssignmentUtil;
import cpp.model.util.ClassGroupUtil;

/**
 * Grades an assignment for a contact. The assignment must have been submitted
 * before it can be graded.
 */
public class GradeAssignmentCommand extends Command {

    public static final String COMMAND_WORD = "grade";

    public static final String MESSAGE_USAGE = GradeAssignmentCommand.COMMAND_WORD
            + ": Grades an assignment for contact(s) with a score (decimal places allowed) from 0 to 100, "
            + "rounded to 1 decimal place.\n"
            + "If no grading date is provided, the current date and time will be used.\n"
            + "Parameters: "
            + CliSyntax.PREFIX_ASSIGNMENT + "ASSIGNMENT_NAME "
            + "[" + CliSyntax.PREFIX_CLASS + "CLASS_NAME] "
            + "[" + CliSyntax.PREFIX_CONTACT + "CONTACT_INDICES...] "
            + CliSyntax.PREFIX_SCORE + "SCORE "
            + "[" + CliSyntax.PREFIX_DATETIME + "GRADING_DATE]\n"
            + "At least one of " + CliSyntax.PREFIX_CLASS + " or " + CliSyntax.PREFIX_CONTACT + " must be provided.\n"
            + "Example: " + GradeAssignmentCommand.COMMAND_WORD + " "
            + CliSyntax.PREFIX_ASSIGNMENT + "Assignment 1 "
            + CliSyntax.PREFIX_CLASS + "CS2103T T10 1 "
            + CliSyntax.PREFIX_CONTACT + "1 2 3 "
            + CliSyntax.PREFIX_SCORE + "67.9 "
            + CliSyntax.PREFIX_DATETIME + "21-02-2026 23:50";

    public static final String MESSAGE_SUCCESS = """
            Graded assignment: %1$s on %2$s for %3$s contact(s) with score %4$.1f.
            Contacts graded: %5$s
            Contacts not graded (already graded): %6$s
            Contacts not graded (not submitted yet): %7$s
            Contacts not graded (not allocated the assignment): %8$s
            Contacts not graded (grade time before submission time): %9$s""";
    public static final String MESSAGE_GRADE_FAILED = """
            Failed to grade any contacts for the assignment.
            Contacts not graded (already graded): %1$s
            Contacts not graded (not submitted yet): %2$s
            Contacts not graded (not allocated the assignment): %3$s
            Contacts not graded (grade time before submission time): %4$s""";

    private final AssignmentName assignmentName;
    private final ClassGroupName classGroupName;
    private final List<Index> contactIndices;
    private final Set<Contact> contactsToGrade = new HashSet<>();
    private final float score;
    private final LocalDateTime gradingDate;

    private int gradedCount = 0;
    private int alreadyGradedCount = 0;
    private int notSubmittedCount = 0;
    private int notAllocatedCount = 0;
    private int gradeTimeBeforeSubmissionTimeCount = 0;
    private StringBuilder gradedContacts = new StringBuilder();
    private StringBuilder alreadyGradedContacts = new StringBuilder();
    private StringBuilder notSubmittedContacts = new StringBuilder();
    private StringBuilder notAllocatedContacts = new StringBuilder();
    private StringBuilder gradeTimeBeforeSubmissionTimeContacts = new StringBuilder();

    private final Logger logger = LogsCenter.getLogger(LogicManager.class);

    /**
     * Creates a GradeAssignmentCommand to grade the specified assignment for the
     * specified contacts or class group with the given grade info.
     */
    public GradeAssignmentCommand(AssignmentName assignmentName, List<Index> contactIndices,
            ClassGroupName classGroupName, float score, LocalDateTime gradingDate) {
        Objects.requireNonNull(assignmentName);
        Objects.requireNonNull(contactIndices);
        Objects.requireNonNull(classGroupName);
        Objects.requireNonNull(gradingDate);
        this.assignmentName = assignmentName;
        this.contactIndices = new ArrayList<>(contactIndices);
        this.classGroupName = classGroupName;
        this.score = score;
        this.gradingDate = gradingDate;
    }

    /**
     * Creates a GradeAssignmentCommand to grade the specified assignment for the
     * specified contacts with the given grade info.
     */
    public GradeAssignmentCommand(AssignmentName assignmentName, List<Index> contactIndices, float score,
            LocalDateTime gradingDate) {
        Objects.requireNonNull(assignmentName);
        Objects.requireNonNull(contactIndices);
        Objects.requireNonNull(gradingDate);
        this.assignmentName = assignmentName;
        this.contactIndices = new ArrayList<>(contactIndices);
        this.classGroupName = null;
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

        ClassGroup classGroupToGrade = ClassGroupUtil.findClassGroup(model.getAddressBook().getClassGroupList(),
                this.classGroupName);
        if (this.classGroupName != null && classGroupToGrade == null) {
            throw new CommandException(Messages.MESSAGE_CLASS_GROUP_NOT_FOUND);
        }

        if (classGroupToGrade != null && classGroupToGrade.getContactIdSet().isEmpty()) {
            throw new CommandException(Messages.MESSAGE_CLASS_GROUP_NO_CONTACTS);
        }

        this.gradeByContactIndices(model, assignmentToGrade, lastShownContactList);
        if (classGroupToGrade != null) {
            this.gradeByClassGroup(model, assignmentToGrade, classGroupToGrade);
        }

        if (this.alreadyGradedCount == 0) {
            this.alreadyGradedContacts.append("None");
        }

        if (this.notSubmittedCount == 0) {
            this.notSubmittedContacts.append("None");
        }

        if (this.notAllocatedCount == 0) {
            this.notAllocatedContacts.append("None");
        }

        if (this.gradeTimeBeforeSubmissionTimeCount == 0) {
            this.gradeTimeBeforeSubmissionTimeContacts.append("None");
        }

        if (this.gradedCount == 0) {
            throw new CommandException(
                    String.format(GradeAssignmentCommand.MESSAGE_GRADE_FAILED, this.alreadyGradedContacts.toString(),
                            this.notSubmittedContacts.toString(), this.notAllocatedContacts.toString(),
                            this.gradeTimeBeforeSubmissionTimeContacts.toString()));
        }

        return new CommandResult(
                String.format(GradeAssignmentCommand.MESSAGE_SUCCESS, Messages.format(assignmentToGrade),
                        this.gradingDate.format(ParserUtil.DATETIME_FORMATTER), this.gradedCount, this.score,
                        this.gradedContacts.toString(), this.alreadyGradedContacts.toString(),
                        this.notSubmittedContacts.toString(), this.notAllocatedContacts.toString(),
                        this.gradeTimeBeforeSubmissionTimeContacts.toString()));
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
                && Objects.equals(this.classGroupName, o.classGroupName)
                && Float.compare(this.score, o.score) == 0
                && this.gradingDate.equals(o.gradingDate);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("assignmentName", this.assignmentName)
                .add("contactIndices", this.contactIndices)
                .add("classGroupName", this.classGroupName)
                .add("score", this.score)
                .add("gradingDate", this.gradingDate.format(ParserUtil.DATETIME_FORMATTER))
                .toString();
    }

    private void gradeByContactIndices(Model model, Assignment assignmentToUnallocate,
            List<Contact> lastShownContactList) {

        for (Index idx : this.contactIndices) {
            Contact contact = lastShownContactList.get(idx.getZeroBased());

            this.gradeByContact(model, assignmentToUnallocate, contact);
        }
    }

    private void gradeByClassGroup(Model model, Assignment assignmentToGrade, ClassGroup classGroupToGrade) {
        List<Contact> contactList = model.getAddressBook().getContactList();

        for (String contactId : classGroupToGrade.getContactIdSet()) {
            Contact contact = contactList.stream()
                    .filter(c -> c.getId().equals(contactId))
                    .findAny()
                    .orElse(null);

            if (contact != null) {
                this.gradeByContact(model, assignmentToGrade, contact);
            }
        }
    }

    private void gradeByContact(Model model, Assignment assignment, Contact contact) {
        if (this.contactsToGrade.contains(contact)) {
            // Skip contacts that have already been marked as graded through
            // contact indices in the same command
            return;
        }

        try {
            model.grade(assignment, contact, this.score, this.gradingDate);
            this.gradedCount++;
            this.buildSuccessfulGradeString(contact.getName().fullName);

        } catch (ContactAssignmentNotFoundException e) {
            this.notAllocatedCount++;
            this.buildNotAllocatedString(contact.getName().fullName);
            this.logger
                    .info("Contact not marked as graded (not allocated to assignment): " + contact.getName().fullName);
        } catch (ContactAssignmentNotSubmittedException e) {
            this.notSubmittedCount++;
            this.buildNotSubmittedString(contact.getName().fullName);
            this.logger.info("Contact not marked as graded (not submitted): " + contact.getName().fullName);
        } catch (ContactAssignmentAlreadyGradedException e) {
            this.alreadyGradedCount++;
            this.buildAlreadyGradedString(contact.getName().fullName);
            this.logger.info("Contact not marked as graded (already graded): " + contact.getName().fullName);
        } catch (ContactAssignmentGradedBeforeSubmissionException e) {
            this.gradeTimeBeforeSubmissionTimeCount++;
            this.buildGradeTimeBeforeSubmissionTimeString(contact.getName().fullName);
            this.logger.info(
                    "Contact not marked as graded (grade time before submission time): " + contact.getName().fullName);

        }

        this.contactsToGrade.add(contact);
    }

    private void buildSuccessfulGradeString(String contactName) {
        if (this.gradedContacts.length() > 0) {
            this.gradedContacts.append("; ");
        }
        this.gradedContacts.append(contactName);
    }

    private void buildAlreadyGradedString(String contactName) {
        if (this.alreadyGradedContacts.length() > 0) {
            this.alreadyGradedContacts.append("; ");
        }
        this.alreadyGradedContacts.append(contactName);
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

    private void buildGradeTimeBeforeSubmissionTimeString(String contactName) {
        if (this.gradeTimeBeforeSubmissionTimeContacts.length() > 0) {
            this.gradeTimeBeforeSubmissionTimeContacts.append("; ");
        }
        this.gradeTimeBeforeSubmissionTimeContacts.append(contactName);
    }
}
