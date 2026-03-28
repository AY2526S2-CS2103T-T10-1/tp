package cpp.logic.commands.assignment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cpp.logic.Messages;
import cpp.logic.commands.CommandResult;
import cpp.logic.commands.exceptions.CommandException;
import cpp.logic.parser.ParserUtil;
import cpp.model.AddressBook;
import cpp.model.ReadOnlyAddressBook;
import cpp.model.assignment.Assignment;
import cpp.model.assignment.AssignmentName;
import cpp.model.assignment.exceptions.ContactAssignmentAlreadyGradedException;
import cpp.model.assignment.exceptions.ContactAssignmentGradedBeforeSubmissionException;
import cpp.model.assignment.exceptions.ContactAssignmentNotFoundException;
import cpp.model.assignment.exceptions.ContactAssignmentNotSubmittedException;
import cpp.model.contact.Contact;
import cpp.testutil.Assert;
import cpp.testutil.ClassGroupBuilder;
import cpp.testutil.ModelStub;
import cpp.testutil.TypicalAssignments;
import cpp.testutil.TypicalContacts;
import cpp.testutil.TypicalIndexes;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class GradeAssignmentCommandTest {

    @Test
    public void execute_assignmentAcceptedByModel_gradeSuccessful() throws Exception {
        ModelStubAcceptingGrade modelStub = new ModelStubAcceptingGrade();
        Assignment assignment = TypicalAssignments.ASSIGNMENT_ONE;
        LocalDateTime date = LocalDateTime.parse("21-02-2026 23:50", ParserUtil.DATETIME_FORMATTER);

        GradeAssignmentCommand cmd = new GradeAssignmentCommand(assignment.getName(),
                ParserUtil.parseContactIndices("1 2 3"), 85f, date);

        CommandResult result = cmd.execute(modelStub);

        String expected = String.format(GradeAssignmentCommand.MESSAGE_SUCCESS,
                Messages.format(assignment), date.format(ParserUtil.DATETIME_FORMATTER), 3, 85f,
                TypicalContacts.ALICE.getName().fullName + "; " + TypicalContacts.BENSON.getName().fullName + "; "
                        + TypicalContacts.CARL.getName().fullName,
                "None", "None", "None", "None");

        Assertions.assertEquals(expected, result.getFeedbackToUser());
    }

    @Test
    public void execute_classGroupAcceptedByModel_gradeSuccessful() throws Exception {
        ModelStubAcceptingGrade modelStub = new ModelStubAcceptingGrade();
        Assignment assignment = TypicalAssignments.ASSIGNMENT_ONE;
        LocalDateTime date = LocalDateTime.parse("21-02-2026 23:50", ParserUtil.DATETIME_FORMATTER);

        GradeAssignmentCommand cmd = new GradeAssignmentCommand(assignment.getName(), List.of(),
                ParserUtil.parseClassGroupName("CS2103T10"), 85f, date);

        CommandResult result = cmd.execute(modelStub);
        String feedback = result.getFeedbackToUser();

        Assertions.assertTrue(feedback.contains(Messages.format(assignment)));
        Assertions.assertTrue(feedback.contains(date.format(ParserUtil.DATETIME_FORMATTER)));
        Assertions.assertTrue(feedback.contains("for 3 contact(s)"));
        // ensure all three contacts are present in the graded list (order independent)
        Assertions.assertTrue(feedback.contains(TypicalContacts.ALICE.getName().fullName));
        Assertions.assertTrue(feedback.contains(TypicalContacts.BENSON.getName().fullName));
        Assertions.assertTrue(feedback.contains(TypicalContacts.CARL.getName().fullName));
        // other buckets should be "None"
        Assertions.assertTrue(feedback.contains("Contacts not graded (already graded): None"));
        Assertions.assertTrue(feedback.contains("Contacts not graded (not submitted yet): None"));
        Assertions.assertTrue(feedback.contains("Contacts not graded (not allocated the assignment): None"));
        Assertions.assertTrue(feedback.contains("Contacts not graded (grade time before submission time): None"));
    }

    @Test
    public void execute_invalidContactIndex_throwsCommandException() throws Exception {
        GradeAssignmentCommand cmd = new GradeAssignmentCommand(TypicalAssignments.ASSIGNMENT_ONE.getName(),
                ParserUtil.parseContactIndices("100"), 50f, LocalDateTime.now());

        ModelStubAcceptingGrade modelStub = new ModelStubAcceptingGrade();
        Assert.assertThrows(CommandException.class, Messages.MESSAGE_INVALID_CONTACT_DISPLAYED_INDEX,
                () -> cmd.execute(modelStub));
    }

    @Test
    public void execute_assignmentNotFound_throwsCommandException() throws Exception {
        ModelStubAcceptingGrade modelStub = new ModelStubAcceptingGrade();
        GradeAssignmentCommand cmd = new GradeAssignmentCommand(new AssignmentName("NonExistent"),
                ParserUtil.parseContactIndices("1"), 10f, LocalDateTime.now());

        Assert.assertThrows(CommandException.class, Messages.MESSAGE_ASSIGNMENT_NOT_FOUND,
                () -> cmd.execute(modelStub));
    }

    @Test
    public void execute_noContactsGraded_throwsCommandException() throws Exception {
        ModelStubGradeAllNotAllocated modelStub = new ModelStubGradeAllNotAllocated();
        Assignment assignment = TypicalAssignments.ASSIGNMENT_ONE;
        GradeAssignmentCommand cmd = new GradeAssignmentCommand(assignment.getName(),
                ParserUtil.parseContactIndices("1 2 3"), 10f,
                LocalDateTime.parse("21-02-2026 23:50", ParserUtil.DATETIME_FORMATTER));

        String expected = String.format(GradeAssignmentCommand.MESSAGE_GRADE_FAILED, "None", "None",
                TypicalContacts.ALICE.getName().fullName + "; " + TypicalContacts.BENSON.getName().fullName + "; "
                        + TypicalContacts.CARL.getName().fullName,
                "None");

        Assert.assertThrows(CommandException.class, expected, () -> cmd.execute(modelStub));

        ModelStubGradeAllNotSubmitted modelStub2 = new ModelStubGradeAllNotSubmitted();
        GradeAssignmentCommand cmd2 = new GradeAssignmentCommand(assignment.getName(),
                ParserUtil.parseContactIndices("1 2 3"), 10f,
                LocalDateTime.parse("21-02-2026 23:50", ParserUtil.DATETIME_FORMATTER));
        expected = String.format(GradeAssignmentCommand.MESSAGE_GRADE_FAILED, "None",
                TypicalContacts.ALICE.getName().fullName + "; " + TypicalContacts.BENSON.getName().fullName + "; "
                        + TypicalContacts.CARL.getName().fullName,
                "None", "None");
        Assert.assertThrows(CommandException.class, expected, () -> cmd2.execute(modelStub2));

        ModelStubGradeAllAlreadyGraded modelStub3 = new ModelStubGradeAllAlreadyGraded();
        GradeAssignmentCommand cmd3 = new GradeAssignmentCommand(assignment.getName(),
                ParserUtil.parseContactIndices("1 2 3"), 10f,
                LocalDateTime.parse("21-02-2026 23:50", ParserUtil.DATETIME_FORMATTER));
        expected = String.format(
                GradeAssignmentCommand.MESSAGE_GRADE_FAILED, TypicalContacts.ALICE.getName().fullName + "; "
                        + TypicalContacts.BENSON.getName().fullName + "; " + TypicalContacts.CARL.getName().fullName,
                "None", "None", "None");
        Assert.assertThrows(CommandException.class, expected, () -> cmd3.execute(modelStub3));

        // grade time before submission -> should appear in the last bucket
        ModelStubGradeAllGradeTimeBeforeSubmission modelStub4 = new ModelStubGradeAllGradeTimeBeforeSubmission();
        GradeAssignmentCommand cmd4 = new GradeAssignmentCommand(assignment.getName(),
                ParserUtil.parseContactIndices("1 2 3"), 10f,
                LocalDateTime.parse("21-02-2026 23:50", ParserUtil.DATETIME_FORMATTER));
        expected = String.format(GradeAssignmentCommand.MESSAGE_GRADE_FAILED, "None", "None", "None",
                TypicalContacts.ALICE.getName().fullName + "; " + TypicalContacts.BENSON.getName().fullName + "; "
                        + TypicalContacts.CARL.getName().fullName);
        Assert.assertThrows(CommandException.class, expected, () -> cmd4.execute(modelStub4));
    }

    @Test
    public void equals_sameValues_returnsTrue() throws Exception {
        Assignment assignment = TypicalAssignments.ASSIGNMENT_ONE;
        GradeAssignmentCommand cmd1 = new GradeAssignmentCommand(assignment.getName(),
                ParserUtil.parseContactIndices("1 2"), 10f,
                LocalDateTime.parse("21-02-2026 23:50", ParserUtil.DATETIME_FORMATTER));
        GradeAssignmentCommand cmd2 = new GradeAssignmentCommand(assignment.getName(),
                ParserUtil.parseContactIndices("1 2"), 10f,
                LocalDateTime.parse("21-02-2026 23:50", ParserUtil.DATETIME_FORMATTER));

        Assertions.assertTrue(cmd1.equals(cmd1));
        Assertions.assertTrue(cmd1.equals(cmd2));
    }

    @Test
    public void equals_differentValues_returnsFalse() throws Exception {
        Assignment assignment = TypicalAssignments.ASSIGNMENT_ONE;
        GradeAssignmentCommand cmd = new GradeAssignmentCommand(assignment.getName(),
                ParserUtil.parseContactIndices("1 2"), 10f,
                LocalDateTime.parse("21-02-2026 23:50", ParserUtil.DATETIME_FORMATTER));

        // different type
        Assertions.assertFalse(cmd.equals(1));
        // null
        Assertions.assertFalse(cmd.equals(null));

        // different assignment name
        GradeAssignmentCommand diff = new GradeAssignmentCommand(new AssignmentName("Different"),
                ParserUtil.parseContactIndices("1 2"), 10f,
                LocalDateTime.parse("21-02-2026 23:50", ParserUtil.DATETIME_FORMATTER));
        Assertions.assertFalse(cmd.equals(diff));

        // different contact indices
        diff = new GradeAssignmentCommand(assignment.getName(), ParserUtil.parseContactIndices("2 3"), 10f,
                LocalDateTime.parse("21-02-2026 23:50", ParserUtil.DATETIME_FORMATTER));
        Assertions.assertFalse(cmd.equals(diff));

        // different score
        diff = new GradeAssignmentCommand(assignment.getName(), ParserUtil.parseContactIndices("1 2"), 5f,
                LocalDateTime.parse("21-02-2026 23:50", ParserUtil.DATETIME_FORMATTER));
        Assertions.assertFalse(cmd.equals(diff));
    }

    @Test
    public void toString_typicalValue_correctOutput() {
        GradeAssignmentCommand cmd = new GradeAssignmentCommand(TypicalAssignments.ASSIGNMENT_ONE.getName(),
                List.of(TypicalIndexes.INDEX_FIRST_CONTACT, TypicalIndexes.INDEX_SECOND_CONTACT), 10f,
                LocalDateTime.parse("21-02-2026 23:50", ParserUtil.DATETIME_FORMATTER));
        String expected = GradeAssignmentCommand.class.getCanonicalName() + "{"
                + "assignmentName=" + TypicalAssignments.ASSIGNMENT_ONE.getName()
                + ", contactIndices=[" + TypicalIndexes.INDEX_FIRST_CONTACT + ", " + TypicalIndexes.INDEX_SECOND_CONTACT
                + "]"
                + ", classGroupName=null"
                + ", score=10.0"
                + ", gradingDate=21-02-2026 23:50}";
        Assertions.assertEquals(expected, cmd.toString());
    }

    @Test
    public void toString_classGroupValue_correctOutput() throws Exception {
        GradeAssignmentCommand cmd = new GradeAssignmentCommand(TypicalAssignments.ASSIGNMENT_ONE.getName(),
                List.of(TypicalIndexes.INDEX_FIRST_CONTACT, TypicalIndexes.INDEX_SECOND_CONTACT),
                ParserUtil.parseClassGroupName("CS2103T10"), 10f,
                LocalDateTime.parse("21-02-2026 23:50", ParserUtil.DATETIME_FORMATTER));
        String expected = GradeAssignmentCommand.class.getCanonicalName() + "{"
                + "assignmentName=" + TypicalAssignments.ASSIGNMENT_ONE.getName()
                + ", contactIndices=[" + TypicalIndexes.INDEX_FIRST_CONTACT + ", " + TypicalIndexes.INDEX_SECOND_CONTACT
                + "]"
                + ", classGroupName=CS2103T10"
                + ", score=10.0"
                + ", gradingDate=21-02-2026 23:50}";
        Assertions.assertEquals(expected, cmd.toString());
    }

    /**
     * Model stub that accepts grade calls.
     */
    private class ModelStubAcceptingGrade extends ModelStub {

        @Override
        public ObservableList<Contact> getFilteredContactList() {
            return FXCollections.observableArrayList(TypicalContacts.getTypicalContacts());
        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            AddressBook ab = new AddressBook();
            for (Contact c : TypicalContacts.getTypicalContacts()) {
                ab.addContact(c);
            }
            ab.addAssignment(TypicalAssignments.ASSIGNMENT_ONE);
            // add a class group that contains first three typical contacts
            ab.addClassGroup(new ClassGroupBuilder().withName("CS2103T10").withContactIds(
                    TypicalContacts.ALICE.getId(), TypicalContacts.BENSON.getId(), TypicalContacts.CARL.getId())
                    .build());
            return ab;
        }

        @Override
        public void grade(Assignment assignment, Contact contact, float score, LocalDateTime gradingDate)
                throws ContactAssignmentAlreadyGradedException, ContactAssignmentNotSubmittedException {
            Objects.requireNonNull(assignment);
            Objects.requireNonNull(contact);
            // succeed silently
        }
    }

    private class ModelStubGradeAllNotAllocated extends ModelStubAcceptingGrade {
        @Override
        public void grade(Assignment assignment, Contact contact, float score, LocalDateTime gradingDate)
                throws ContactAssignmentNotFoundException {
            throw new ContactAssignmentNotFoundException();
        }
    }

    private class ModelStubGradeAllNotSubmitted extends ModelStubAcceptingGrade {
        @Override
        public void grade(Assignment assignment, Contact contact, float score, LocalDateTime gradingDate)
                throws ContactAssignmentNotSubmittedException {
            throw new ContactAssignmentNotSubmittedException();
        }
    }

    private class ModelStubGradeAllAlreadyGraded extends ModelStubAcceptingGrade {
        @Override
        public void grade(Assignment assignment, Contact contact, float score, LocalDateTime gradingDate)
                throws ContactAssignmentAlreadyGradedException {
            throw new ContactAssignmentAlreadyGradedException();
        }
    }

    private class ModelStubGradeAllGradeTimeBeforeSubmission extends ModelStubAcceptingGrade {
        @Override
        public void grade(Assignment assignment, Contact contact, float score, LocalDateTime gradingDate)
                throws ContactAssignmentGradedBeforeSubmissionException {
            throw new ContactAssignmentGradedBeforeSubmissionException();
        }
    }

}
