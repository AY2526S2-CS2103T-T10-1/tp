package cpp.logic.commands.assignment;

import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cpp.commons.core.index.Index;
import cpp.logic.Messages;
import cpp.logic.commands.CommandResult;
import cpp.logic.commands.exceptions.CommandException;
import cpp.model.AddressBook;
import cpp.model.ReadOnlyAddressBook;
import cpp.model.assignment.Assignment;
import cpp.model.assignment.AssignmentName;
import cpp.model.assignment.exceptions.ContactAssignmentNotFoundException;
import cpp.model.assignment.exceptions.ContactAssignmentNotSubmittedException;
import cpp.model.classgroup.ClassGroup;
import cpp.model.classgroup.ClassGroupName;
import cpp.model.contact.Contact;
import cpp.testutil.Assert;
import cpp.testutil.ModelStub;
import cpp.testutil.TypicalAssignments;
import cpp.testutil.TypicalContacts;
import cpp.testutil.TypicalIndexes;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class UnsubmitAssignmentCommandTest {

    @Test
    public void constructor_nullAssignmentName_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class,
                () -> new UnsubmitAssignmentCommand(null, List.of()));
    }

    @Test
    public void execute_assignmentAcceptedByModel_unsubmitSuccessful() throws Exception {
        ModelStubAcceptingUnsubmit modelStub = new ModelStubAcceptingUnsubmit();
        Assignment assignment = TypicalAssignments.ASSIGNMENT_ONE;

        UnsubmitAssignmentCommand cmd = new UnsubmitAssignmentCommand(assignment.getName(),
                List.of(Index.fromOneBased(1)));

        CommandResult result = null;
        try {
            result = cmd.execute(modelStub);
        } catch (CommandException e) {
            Assertions.fail("Execution of valid UnsubmitAssignmentCommand should not throw CommandException.");
        }

        String expected = String.format(UnsubmitAssignmentCommand.MESSAGE_SUCCESS,
                Messages.format(assignment), 1, TypicalContacts.ALICE.getName().fullName, "None", "None");

        Assertions.assertEquals(expected, result.getFeedbackToUser());
    }

    @Test
    public void execute_assignmentNotFound_throwsCommandException() {
        ModelStubAcceptingUnsubmit modelStub = new ModelStubAcceptingUnsubmit();
        UnsubmitAssignmentCommand cmd = new UnsubmitAssignmentCommand(new AssignmentName("NonExistent"), List.of());

        Assert.assertThrows(CommandException.class, Messages.MESSAGE_ASSIGNMENT_NOT_FOUND,
                () -> cmd.execute(modelStub));
    }

    @Test
    public void execute_validClassGroup_unsubmitSuccessful() {
        UnsubmitAssignmentCommand cmd = new UnsubmitAssignmentCommand(TypicalAssignments.ASSIGNMENT_ONE.getName(),
                List.of(), new ClassGroupName("ValidClassGroup"));

        ModelStubWithClassGroup modelStub = new ModelStubWithClassGroup();
        try {
            CommandResult result = cmd.execute(modelStub);
            String expected = String.format(UnsubmitAssignmentCommand.MESSAGE_SUCCESS,
                    Messages.format(TypicalAssignments.ASSIGNMENT_ONE), 1, TypicalContacts.BENSON.getName().fullName,
                    "None", "None");
            Assertions.assertEquals(expected, result.getFeedbackToUser());
        } catch (CommandException e) {
            Assertions.fail("Execution should not throw CommandException.");
        }
    }

    @Test
    public void execute_duplicateBetweenContactIndicesAndClassGroup_skipsDuplicate() {
        UnsubmitAssignmentCommand cmd = new UnsubmitAssignmentCommand(TypicalAssignments.ASSIGNMENT_ONE.getName(),
                List.of(Index.fromOneBased(1)), new ClassGroupName("OverlapGroup"));

        ModelStubWithClassGroupOverlap modelStub = new ModelStubWithClassGroupOverlap();
        try {
            CommandResult result = cmd.execute(modelStub);
            // Alice is both in index 1 and overlap group; should be counted once
            Assertions.assertTrue(result.getFeedbackToUser().contains(TypicalContacts.ALICE.getName().fullName));
        } catch (CommandException e) {
            Assertions.fail("Execution should not throw CommandException.");
        }
    }

    @Test
    public void execute_invalidContactIndex_throwsCommandException() {
        UnsubmitAssignmentCommand cmd = new UnsubmitAssignmentCommand(TypicalAssignments.ASSIGNMENT_ONE.getName(),
                List.of(Index.fromOneBased(100)));

        ModelStubAcceptingUnsubmit modelStub = new ModelStubAcceptingUnsubmit();
        Assert.assertThrows(CommandException.class, Messages.MESSAGE_INVALID_CONTACT_DISPLAYED_INDEX,
                () -> cmd.execute(modelStub));
    }

    @Test
    public void execute_classGroupNotFound_throwsCommandException() {
        UnsubmitAssignmentCommand cmd = new UnsubmitAssignmentCommand(TypicalAssignments.ASSIGNMENT_ONE.getName(),
                List.of(), new ClassGroupName("NonExistent"));

        ModelStubAcceptingUnsubmit modelStub = new ModelStubAcceptingUnsubmit();
        Assert.assertThrows(CommandException.class, Messages.MESSAGE_CLASS_GROUP_NOT_FOUND,
                () -> cmd.execute(modelStub));
    }

    @Test
    public void execute_emptyClassGroup_throwsCommandException() {
        UnsubmitAssignmentCommand cmd = new UnsubmitAssignmentCommand(TypicalAssignments.ASSIGNMENT_ONE.getName(),
                List.of(), new ClassGroupName("EmptyGroup"));

        ModelStubWithEmptyClassGroup modelStub = new ModelStubWithEmptyClassGroup(TypicalAssignments.ASSIGNMENT_ONE);
        Assert.assertThrows(CommandException.class, Messages.MESSAGE_CLASS_GROUP_NO_CONTACTS,
                () -> cmd.execute(modelStub));
    }

    @Test
    public void execute_noContactsMarked_throwsCommandException() {
        ModelStubAllNotSubmitted modelStub = new ModelStubAllNotSubmitted();
        Assignment assignment = TypicalAssignments.ASSIGNMENT_ONE;
        UnsubmitAssignmentCommand cmd = new UnsubmitAssignmentCommand(assignment.getName(),
                List.of(TypicalIndexes.INDEX_FIRST_CONTACT, TypicalIndexes.INDEX_SECOND_CONTACT));

        Assert.assertThrows(CommandException.class,
                String.format(UnsubmitAssignmentCommand.MESSAGE_SUBMISSION_FAILED,
                        TypicalContacts.ALICE.getName().fullName + "; " + TypicalContacts.BENSON.getName().fullName,
                        "None"),
                () -> cmd.execute(modelStub));
    }

    @Test
    public void execute_twoAlreadyUnsubmitted_contactsCountTwoAlreadyUnsubmitted() {
        UnsubmitAssignmentCommand cmd = new UnsubmitAssignmentCommand(TypicalAssignments.ASSIGNMENT_ONE.getName(),
                List.of(Index.fromOneBased(1), Index.fromOneBased(2)));

        ModelStubTwoAlreadyUnsubmitted modelStub = new ModelStubTwoAlreadyUnsubmitted();
        try {
            cmd.execute(modelStub);
            Assertions.fail("Expected CommandException due to no contacts unmarked");
        } catch (CommandException e) {
            Assertions.assertEquals(String.format(UnsubmitAssignmentCommand.MESSAGE_SUBMISSION_FAILED,
                    TypicalContacts.ALICE.getName().fullName + "; " + TypicalContacts.BENSON.getName().fullName,
                    "None"), e.getMessage());
        }
    }

    @Test
    public void execute_twoNotAllocated_contactsCountTwoNotAllocated() {
        UnsubmitAssignmentCommand cmd = new UnsubmitAssignmentCommand(TypicalAssignments.ASSIGNMENT_ONE.getName(),
                List.of(Index.fromOneBased(1), Index.fromOneBased(2)));

        ModelStubTwoNotAllocated modelStub = new ModelStubTwoNotAllocated();
        try {
            cmd.execute(modelStub);
            Assertions.fail("Expected CommandException due to no contacts unmarked");
        } catch (CommandException e) {
            Assertions.assertEquals(String.format(UnsubmitAssignmentCommand.MESSAGE_SUBMISSION_FAILED,
                    "None",
                    TypicalContacts.ALICE.getName().fullName + "; " + TypicalContacts.BENSON.getName().fullName),
                    e.getMessage());
        }
    }

    @Test
    public void execute_overlapIndexAndClassGroup_countsUniqueTwoUnsubmitted() {
        UnsubmitAssignmentCommand cmd = new UnsubmitAssignmentCommand(TypicalAssignments.ASSIGNMENT_ONE.getName(),
                List.of(Index.fromOneBased(1)), new ClassGroupName("TwoGroup"));

        ModelStubWithClassGroupTwo modelStub = new ModelStubWithClassGroupTwo();
        try {
            CommandResult result = cmd.execute(modelStub);
            Assertions.assertTrue(result.getFeedbackToUser().contains("2"));
            Assertions.assertTrue(result.getFeedbackToUser().contains(TypicalContacts.ALICE.getName().fullName));
            Assertions.assertTrue(result.getFeedbackToUser().contains(TypicalContacts.BENSON.getName().fullName));
        } catch (CommandException e) {
            Assertions.fail("Execution should not throw CommandException.");
        }
    }

    @Test
    public void execute_twoContactsUnsubmitted_successfulUnsubmitTwo() {
        UnsubmitAssignmentCommand cmd = new UnsubmitAssignmentCommand(TypicalAssignments.ASSIGNMENT_ONE.getName(),
                List.of(Index.fromOneBased(1), Index.fromOneBased(2)));

        ModelStubAcceptingUnsubmit modelStub = new ModelStubAcceptingUnsubmit();
        try {
            CommandResult result = cmd.execute(modelStub);
            Assertions.assertTrue(result.getFeedbackToUser().contains("2"));
            Assertions.assertTrue(result.getFeedbackToUser().contains(TypicalContacts.ALICE.getName().fullName));
            Assertions.assertTrue(result.getFeedbackToUser().contains(TypicalContacts.BENSON.getName().fullName));
        } catch (CommandException e) {
            Assertions.fail("Execution should not throw CommandException.");
        }
    }

    @Test
    public void execute_mixedUnsubmittedAndAlreadyUnsubmitted_hasTwoAlreadyUnsubmitted() {
        // indices: 3 (Carl) will be unsubmitted, 1 (Alice) and 2 (Benson) already
        // unsubmitted
        UnsubmitAssignmentCommand cmd = new UnsubmitAssignmentCommand(TypicalAssignments.ASSIGNMENT_ONE.getName(),
                List.of(Index.fromOneBased(3), Index.fromOneBased(1), Index.fromOneBased(2)));

        ModelStubTwoAlreadyUnsubmitted modelStub = new ModelStubTwoAlreadyUnsubmitted();
        try {
            CommandResult result = cmd.execute(modelStub);
            Assertions.assertTrue(result.getFeedbackToUser().contains("1"));
            Assertions.assertTrue(result.getFeedbackToUser().contains("2"));
            Assertions.assertTrue(result.getFeedbackToUser().contains(TypicalContacts.CARL.getName().fullName));
        } catch (CommandException e) {
            Assertions.fail("Execution should not throw CommandException.");
        }
    }

    @Test
    public void execute_mixedUnsubmittedAndNotAllocated_hasTwoNotAllocated() {
        // indices: 3 (Carl) will be unsubmitted, 1 (Alice) and 2 (Benson) not allocated
        UnsubmitAssignmentCommand cmd = new UnsubmitAssignmentCommand(TypicalAssignments.ASSIGNMENT_ONE.getName(),
                List.of(Index.fromOneBased(3), Index.fromOneBased(1), Index.fromOneBased(2)));

        ModelStubTwoNotAllocated modelStub = new ModelStubTwoNotAllocated();
        try {
            CommandResult result = cmd.execute(modelStub);
            Assertions.assertTrue(result.getFeedbackToUser().contains("1"));
            Assertions.assertTrue(result.getFeedbackToUser().contains("2"));
            Assertions.assertTrue(result.getFeedbackToUser().contains(TypicalContacts.CARL.getName().fullName));
        } catch (CommandException e) {
            Assertions.fail("Execution should not throw CommandException.");
        }
    }

    @Test
    public void equals_sameValues_returnsTrue() {
        UnsubmitAssignmentCommand cmd1 = new UnsubmitAssignmentCommand(TypicalAssignments.ASSIGNMENT_ONE.getName(),
                List.of());
        UnsubmitAssignmentCommand cmd2 = new UnsubmitAssignmentCommand(TypicalAssignments.ASSIGNMENT_ONE.getName(),
                List.of());

        Assertions.assertTrue(cmd1.equals(cmd1));
        Assertions.assertTrue(cmd1.equals(cmd2));
    }

    @Test
    public void equals_differentValues_returnsFalse() {
        UnsubmitAssignmentCommand cmd = new UnsubmitAssignmentCommand(TypicalAssignments.ASSIGNMENT_ONE.getName(),
                List.of());

        // different type
        Assertions.assertFalse(cmd.equals(1));
        // null
        Assertions.assertFalse(cmd.equals(null));

        // different assignment
        UnsubmitAssignmentCommand diff = new UnsubmitAssignmentCommand(new AssignmentName("Different"), List.of());
        Assertions.assertFalse(cmd.equals(diff));

        // different contacts
        UnsubmitAssignmentCommand diffContacts = new UnsubmitAssignmentCommand(
                TypicalAssignments.ASSIGNMENT_ONE.getName(),
                List.of(Index.fromOneBased(1)));
        Assertions.assertFalse(cmd.equals(diffContacts));

        // different class group
        UnsubmitAssignmentCommand diffClassGroup = new UnsubmitAssignmentCommand(
                TypicalAssignments.ASSIGNMENT_ONE.getName(),
                List.of(), new ClassGroupName("DifferentGroup"));
        Assertions.assertFalse(cmd.equals(diffClassGroup));
    }

    @Test
    public void toString_typicalValue_correctOutput() {
        UnsubmitAssignmentCommand cmd = new UnsubmitAssignmentCommand(TypicalAssignments.ASSIGNMENT_ONE.getName(),
                List.of(TypicalIndexes.INDEX_FIRST_CONTACT, TypicalIndexes.INDEX_SECOND_CONTACT),
                new ClassGroupName("ValidClassGroup"));

        String expected = UnsubmitAssignmentCommand.class.getCanonicalName() + "{"
                + "assignmentName=" + TypicalAssignments.ASSIGNMENT_ONE.getName()
                + ", contactIndices=[" + TypicalIndexes.INDEX_FIRST_CONTACT + ", " + TypicalIndexes.INDEX_SECOND_CONTACT
                + "]"
                + ", classGroupName=ValidClassGroup"
                + "}";
        Assertions.assertEquals(expected, cmd.toString());
    }

    /**
     * Model stub that accepts unsubmit calls.
     */
    private class ModelStubAcceptingUnsubmit extends ModelStub {

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
            return ab;
        }

        @Override
        public void markUnsubmitted(Assignment assignment, Contact contact) {
            Objects.requireNonNull(assignment);
            Objects.requireNonNull(contact);
            // succeed silently
        }
    }

    private class ModelStubAllNotSubmitted extends ModelStubAcceptingUnsubmit {
        @Override
        public void markUnsubmitted(Assignment assignment, Contact contact)
                throws ContactAssignmentNotFoundException, ContactAssignmentNotSubmittedException {
            throw new ContactAssignmentNotSubmittedException();
        }
    }

    private class ModelStubTwoAlreadyUnsubmitted extends ModelStubAcceptingUnsubmit {
        @Override
        public void markUnsubmitted(Assignment assignment, Contact contact)
                throws ContactAssignmentNotFoundException, ContactAssignmentNotSubmittedException {
            if (contact.getId().equals(TypicalContacts.ALICE.getId())
                    || contact.getId().equals(TypicalContacts.BENSON.getId())) {
                throw new ContactAssignmentNotSubmittedException();
            }
        }
    }

    private class ModelStubTwoNotAllocated extends ModelStubAcceptingUnsubmit {
        @Override
        public void markUnsubmitted(Assignment assignment, Contact contact)
                throws ContactAssignmentNotFoundException, ContactAssignmentNotSubmittedException {
            if (contact.getId().equals(TypicalContacts.ALICE.getId())
                    || contact.getId().equals(TypicalContacts.BENSON.getId())) {
                throw new ContactAssignmentNotFoundException();
            }
        }
    }

    private class ModelStubWithClassGroupTwo extends ModelStubAcceptingUnsubmit {
        @Override
        public boolean hasClassGroup(ClassGroup classGroup) {
            return classGroup.getName().equals(new ClassGroupName("TwoGroup"));
        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            AddressBook ab = new AddressBook();
            for (Contact c : TypicalContacts.getTypicalContacts()) {
                ab.addContact(c);
            }
            ClassGroup cg = new ClassGroup(new ClassGroupName("TwoGroup"));
            try {
                cg.allocateContact(TypicalContacts.ALICE.getId());
                cg.allocateContact(TypicalContacts.BENSON.getId());
            } catch (Exception e) {
                // ignore
            }
            ab.addClassGroup(cg);
            ab.addAssignment(TypicalAssignments.ASSIGNMENT_ONE);
            return ab;
        }
    }

    private class ModelStubWithClassGroup extends ModelStubAcceptingUnsubmit {
        @Override
        public boolean hasClassGroup(ClassGroup classGroup) {
            return classGroup.getName().equals(new ClassGroupName("ValidClassGroup"));
        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            AddressBook ab = new AddressBook();
            for (Contact c : TypicalContacts.getTypicalContacts()) {
                ab.addContact(c);
            }
            ClassGroup cg = new ClassGroup(new ClassGroupName("ValidClassGroup"));
            try {
                cg.allocateContact(TypicalContacts.BENSON.getId());
            } catch (Exception e) {
                // ignore
            }
            ab.addClassGroup(cg);
            ab.addAssignment(TypicalAssignments.ASSIGNMENT_ONE);
            return ab;
        }
    }

    private class ModelStubWithClassGroupOverlap extends ModelStubAcceptingUnsubmit {
        @Override
        public boolean hasClassGroup(ClassGroup classGroup) {
            return classGroup.getName().equals(new ClassGroupName("OverlapGroup"));
        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            AddressBook ab = new AddressBook();
            for (Contact c : TypicalContacts.getTypicalContacts()) {
                ab.addContact(c);
            }
            ClassGroup cg = new ClassGroup(new ClassGroupName("OverlapGroup"));
            try {
                cg.allocateContact(TypicalContacts.ALICE.getId());
            } catch (Exception e) {
                // ignore
            }
            ab.addClassGroup(cg);
            ab.addAssignment(TypicalAssignments.ASSIGNMENT_ONE);
            return ab;
        }
    }

    public class ModelStubWithEmptyClassGroup extends ModelStub {
        private final Assignment assignment;

        ModelStubWithEmptyClassGroup(Assignment assignment) {
            Objects.requireNonNull(assignment);
            this.assignment = assignment;
        }

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
            ab.addAssignment(this.assignment);
            ClassGroup cg = new ClassGroup(new ClassGroupName("EmptyGroup"));
            ab.addClassGroup(cg);
            return ab;
        }
    }

}
