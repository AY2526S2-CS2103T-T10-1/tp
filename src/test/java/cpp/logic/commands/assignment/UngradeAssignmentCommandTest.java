package cpp.logic.commands.assignment;

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
import cpp.model.assignment.exceptions.ContactAssignmentNotFoundException;
import cpp.model.assignment.exceptions.ContactAssignmentNotGradedException;
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

public class UngradeAssignmentCommandTest {

    @Test
    public void execute_assignmentAcceptedByModel_ungradeSuccessful() throws Exception {
        ModelStubAcceptingUngrade modelStub = new ModelStubAcceptingUngrade();
        Assignment assignment = TypicalAssignments.ASSIGNMENT_ONE;

        UngradeAssignmentCommand cmd = new UngradeAssignmentCommand(assignment.getName(),
                ParserUtil.parseContactIndices("1 2 3"));

        CommandResult result = cmd.execute(modelStub);

        String expected = String.format(UngradeAssignmentCommand.MESSAGE_SUCCESS,
                Messages.format(assignment), 3,
                TypicalContacts.ALICE.getName().fullName + "; " + TypicalContacts.BENSON.getName().fullName + "; "
                        + TypicalContacts.CARL.getName().fullName,
                "None", "None", "None");

        Assertions.assertEquals(expected, result.getFeedbackToUser());
    }

    @Test
    public void execute_classGroupAcceptedByModel_ungradeSuccessful() throws Exception {
        ModelStubAcceptingUngrade modelStub = new ModelStubAcceptingUngrade();
        Assignment assignment = TypicalAssignments.ASSIGNMENT_ONE;

        UngradeAssignmentCommand cmd = new UngradeAssignmentCommand(assignment.getName(), List.of(),
                ParserUtil.parseClassGroupName("CS2103T10"));

        CommandResult result = cmd.execute(modelStub);
        String feedback = result.getFeedbackToUser();

        Assertions.assertTrue(feedback.contains(Messages.format(assignment)));
        Assertions.assertTrue(feedback.contains("3"));
        // ensure all three contacts are present (order independent)
        Assertions.assertTrue(feedback.contains(TypicalContacts.ALICE.getName().fullName));
        Assertions.assertTrue(feedback.contains(TypicalContacts.BENSON.getName().fullName));
        Assertions.assertTrue(feedback.contains(TypicalContacts.CARL.getName().fullName));
        // other buckets should be "None"
        Assertions.assertTrue(feedback.contains("None"));
    }

    @Test
    public void execute_invalidContactIndex_throwsCommandException() throws Exception {
        UngradeAssignmentCommand cmd = new UngradeAssignmentCommand(TypicalAssignments.ASSIGNMENT_ONE.getName(),
                ParserUtil.parseContactIndices("100"));

        ModelStubAcceptingUngrade modelStub = new ModelStubAcceptingUngrade();
        Assert.assertThrows(CommandException.class, Messages.MESSAGE_INVALID_CONTACT_DISPLAYED_INDEX,
                () -> cmd.execute(modelStub));
    }

    @Test
    public void execute_assignmentNotFound_throwsCommandException() throws Exception {
        ModelStubAcceptingUngrade modelStub = new ModelStubAcceptingUngrade();
        UngradeAssignmentCommand cmd = new UngradeAssignmentCommand(new AssignmentName("NonExistent"),
                ParserUtil.parseContactIndices("1 2 3"));

        Assert.assertThrows(CommandException.class, Messages.MESSAGE_ASSIGNMENT_NOT_FOUND,
                () -> cmd.execute(modelStub));
    }

    @Test
    public void execute_noContactsUngraded_throwsCommandException() throws Exception {
        ModelStubUngradeAllNotGraded modelStub = new ModelStubUngradeAllNotGraded();
        Assignment assignment = TypicalAssignments.ASSIGNMENT_ONE;
        UngradeAssignmentCommand cmd = new UngradeAssignmentCommand(assignment.getName(),
                ParserUtil.parseContactIndices("1 2 3"));

        String expected = String.format(UngradeAssignmentCommand.MESSAGE_GRADE_FAILED,
                TypicalContacts.ALICE.getName().fullName + "; " + TypicalContacts.BENSON.getName().fullName + "; "
                        + TypicalContacts.CARL.getName().fullName,
                "None", "None");

        Assert.assertThrows(CommandException.class, expected, () -> cmd.execute(modelStub));

        ModelStubUngradeAllNotSubmitted modelStubNotSubmitted = new ModelStubUngradeAllNotSubmitted();
        UngradeAssignmentCommand cmdNotSubmitted = new UngradeAssignmentCommand(assignment.getName(),
                ParserUtil.parseContactIndices("1 2 3"));
        expected = String.format(UngradeAssignmentCommand.MESSAGE_GRADE_FAILED, "None",
                TypicalContacts.ALICE.getName().fullName + "; " + TypicalContacts.BENSON.getName().fullName + "; "
                        + TypicalContacts.CARL.getName().fullName,
                "None");
        Assert.assertThrows(CommandException.class, expected, () -> cmdNotSubmitted.execute(modelStubNotSubmitted));

        ModelStubUngradeAllNotAllocated modelStubNotAllocated = new ModelStubUngradeAllNotAllocated();
        UngradeAssignmentCommand cmdNotAllocated = new UngradeAssignmentCommand(assignment.getName(),
                ParserUtil.parseContactIndices("1 2 3"));
        expected = String.format(UngradeAssignmentCommand.MESSAGE_GRADE_FAILED, "None", "None",
                TypicalContacts.ALICE.getName().fullName + "; " + TypicalContacts.BENSON.getName().fullName + "; "
                        + TypicalContacts.CARL.getName().fullName);
        Assert.assertThrows(CommandException.class, expected, () -> cmdNotAllocated.execute(modelStubNotAllocated));
    }

    @Test
    public void equals_sameValues_returnsTrue() throws Exception {
        Assignment assignment = TypicalAssignments.ASSIGNMENT_ONE;
        UngradeAssignmentCommand cmd1 = new UngradeAssignmentCommand(assignment.getName(),
                ParserUtil.parseContactIndices("1 2"));
        UngradeAssignmentCommand cmd2 = new UngradeAssignmentCommand(assignment.getName(),
                ParserUtil.parseContactIndices("1 2"));

        Assertions.assertTrue(cmd1.equals(cmd1));
        Assertions.assertTrue(cmd1.equals(cmd2));
    }

    @Test
    public void equals_differentValues_returnsFalse() throws Exception {
        Assignment assignment = TypicalAssignments.ASSIGNMENT_ONE;
        UngradeAssignmentCommand cmd = new UngradeAssignmentCommand(assignment.getName(),
                ParserUtil.parseContactIndices("1 2"));

        // different type
        Assertions.assertFalse(cmd.equals(1));
        // null
        Assertions.assertFalse(cmd.equals(null));

        // different assignment name
        UngradeAssignmentCommand diff = new UngradeAssignmentCommand(new AssignmentName("Different"),
                ParserUtil.parseContactIndices("1 2"));
        Assertions.assertFalse(cmd.equals(diff));

        // different contact indices
        diff = new UngradeAssignmentCommand(assignment.getName(), ParserUtil.parseContactIndices("2 3"));
        Assertions.assertFalse(cmd.equals(diff));
    }

    @Test
    public void toString_typicalValue_correctOutput() {
        UngradeAssignmentCommand cmd = new UngradeAssignmentCommand(TypicalAssignments.ASSIGNMENT_ONE.getName(),
                List.of(TypicalIndexes.INDEX_FIRST_CONTACT, TypicalIndexes.INDEX_SECOND_CONTACT));
        String expected = UngradeAssignmentCommand.class.getCanonicalName() + "{"
                + "assignmentName=" + TypicalAssignments.ASSIGNMENT_ONE.getName()
                + ", contactIndices=[" + TypicalIndexes.INDEX_FIRST_CONTACT + ", " + TypicalIndexes.INDEX_SECOND_CONTACT
                + "], "
                + "classGroupName=null"
                + "}";
        Assertions.assertEquals(expected, cmd.toString());
    }

    @Test
    public void toString_classGroupValue_correctOutput() throws Exception {
        UngradeAssignmentCommand cmd = new UngradeAssignmentCommand(TypicalAssignments.ASSIGNMENT_ONE.getName(),
                List.of(TypicalIndexes.INDEX_FIRST_CONTACT, TypicalIndexes.INDEX_SECOND_CONTACT),
                ParserUtil.parseClassGroupName("CS2103T10"));
        String expected = UngradeAssignmentCommand.class.getCanonicalName() + "{"
                + "assignmentName=" + TypicalAssignments.ASSIGNMENT_ONE.getName()
                + ", contactIndices=[" + TypicalIndexes.INDEX_FIRST_CONTACT + ", " + TypicalIndexes.INDEX_SECOND_CONTACT
                + "], "
                + "classGroupName=CS2103T10"
                + "}";
        Assertions.assertEquals(expected, cmd.toString());
    }

    /**
     * Model stub that accepts ungrade calls.
     */
    private class ModelStubAcceptingUngrade extends ModelStub {

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
        public void ungrade(Assignment assignment, Contact contact) {
            Objects.requireNonNull(assignment);
            Objects.requireNonNull(contact);
            // succeed silently
        }
    }

    private class ModelStubUngradeAllNotGraded extends ModelStubAcceptingUngrade {
        @Override
        public void ungrade(Assignment assignment, Contact contact) throws ContactAssignmentNotGradedException {
            throw new ContactAssignmentNotGradedException();
        }
    }

    private class ModelStubUngradeAllNotSubmitted extends ModelStubAcceptingUngrade {
        @Override
        public void ungrade(Assignment assignment, Contact contact) throws ContactAssignmentNotSubmittedException {
            throw new ContactAssignmentNotSubmittedException();
        }
    }

    private class ModelStubUngradeAllNotAllocated extends ModelStubAcceptingUngrade {
        @Override
        public void ungrade(Assignment assignment, Contact contact) throws ContactAssignmentNotFoundException {
            throw new ContactAssignmentNotFoundException();
        }
    }

}
