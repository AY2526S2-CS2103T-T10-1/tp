package cpp.logic.commands.assignment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cpp.commons.core.index.Index;
import cpp.logic.Messages;
import cpp.logic.commands.CommandResult;
import cpp.logic.commands.exceptions.CommandException;
import cpp.logic.parser.ParserUtil;
import cpp.model.AddressBook;
import cpp.model.ReadOnlyAddressBook;
import cpp.model.assignment.Assignment;
import cpp.model.assignment.ContactAssignment;
import cpp.model.assignment.exceptions.ContactAlreadyAllocatedAssignmentException;
import cpp.model.classgroup.ClassGroup;
import cpp.model.classgroup.ClassGroupName;
import cpp.model.classgroup.exceptions.ContactAlreadyAllocatedClassGroupException;
import cpp.model.contact.Contact;
import cpp.testutil.Assert;
import cpp.testutil.AssignmentBuilder;
import cpp.testutil.ModelStub;
import cpp.testutil.TypicalAssignments;
import cpp.testutil.TypicalContacts;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class AddAssignmentCommandTest {

    @Test
    public void constructor_nullAssignment_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class, () -> new AddAssignmentCommand(null, List.of()));
    }

    @Test
    public void execute_assignmentAcceptedByModel_addSuccessful() throws Exception {
        ModelStubAcceptingAssignmentAdded modelStub = new ModelStubAcceptingAssignmentAdded();
        Assignment validAssignment = TypicalAssignments.ASSIGNMENT_ONE;

        CommandResult commandResult = new AddAssignmentCommand(validAssignment, List.of()).execute(modelStub);

        Assertions.assertEquals(String.format(AddAssignmentCommand.MESSAGE_SUCCESS, Messages.format(validAssignment)),
                commandResult.getFeedbackToUser());
        Assertions.assertEquals(1, modelStub.assignmentsAdded.size());
        Assertions.assertEquals(validAssignment, modelStub.assignmentsAdded.get(0));
    }

    @Test
    public void execute_duplicateAssignment_throwsCommandException() {
        Assignment validAssignment = TypicalAssignments.ASSIGNMENT_ONE;
        AddAssignmentCommand addAssignmentCommand = new AddAssignmentCommand(validAssignment, List.of());
        ModelStub modelStub = new ModelStubWithAssignment(validAssignment);

        Assert.assertThrows(CommandException.class, AddAssignmentCommand.MESSAGE_DUPLICATE_ASSIGNMENT,
                () -> addAssignmentCommand.execute(modelStub));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        AddAssignmentCommand addAssignmentCommand = new AddAssignmentCommand(TypicalAssignments.ASSIGNMENT_ONE,
                List.of());

        Assert.assertThrows(NullPointerException.class, () -> addAssignmentCommand.execute(null));
    }

    @Test
    public void execute_classGroupNotFound_throwsCommandException() {
        AddAssignmentCommand addAssignmentCommand = new AddAssignmentCommand(TypicalAssignments.ASSIGNMENT_ONE,
                List.of(),
                new ClassGroupName("NonExistentClassGroup"));

        Assert.assertThrows(CommandException.class, Messages.MESSAGE_CLASS_GROUP_NOT_FOUND,
                () -> addAssignmentCommand.execute(new ModelStubAcceptingAssignmentAdded()));
    }

    @Test
    public void execute_classGroupWithNoContacts_throwsCommandException() {
        AddAssignmentCommand addContactCommand = new AddAssignmentCommand(TypicalAssignments.ASSIGNMENT_ONE, List.of(),
                new ClassGroupName("EmptyGroup"));

        ModelStub modelStub = new ModelStubAcceptingAssignmentAdded() {
            @Override
            public boolean hasClassGroup(ClassGroup classGroup) {
                return classGroup.getName().equals(new ClassGroupName("EmptyGroup"));
            }

            @Override
            public ReadOnlyAddressBook getAddressBook() {
                AddressBook ab = new AddressBook();
                ClassGroup cg = new ClassGroup(new ClassGroupName("EmptyGroup"));
                ab.addClassGroup(cg);
                return ab;
            }
        };

        Assert.assertThrows(CommandException.class, Messages.MESSAGE_CLASS_GROUP_NO_CONTACTS,
                () -> addContactCommand.execute(modelStub));
    }

    @Test
    public void execute_validClassGroup_createsAssignmentAndAllocatesToContactsInClassGroup() {
        AddAssignmentCommand addAssignmentCommand = new AddAssignmentCommand(TypicalAssignments.ASSIGNMENT_ONE,
                List.of(Index.fromOneBased(1)), new ClassGroupName("ValidClassGroup"));
        ModelStubWithClassGroup modelStub = new ModelStubWithClassGroup();
        try {
            CommandResult result = addAssignmentCommand.execute(modelStub);

            // assignment added
            Assertions.assertEquals(1, modelStub.assignmentsAdded.size());
            Assertions.assertEquals(TypicalAssignments.ASSIGNMENT_ONE, modelStub.assignmentsAdded.get(0));

            // two allocations: one by index (Alice) and one by class group (Benson)
            Assertions.assertEquals(2, modelStub.contactAssignmentsAdded.size());

            String expectedAllocatedContacts = TypicalContacts.ALICE.getName().fullName + "; "
                    + TypicalContacts.BENSON.getName().fullName;

            Assertions.assertEquals(String.format(AddAssignmentCommand.MESSAGE_SUCCESS_WITH_ALLOCATION,
                    Messages.format(TypicalAssignments.ASSIGNMENT_ONE), 2, expectedAllocatedContacts),
                    result.getFeedbackToUser());

        } catch (CommandException e) {
            Assertions.fail("Execution of valid AddAssignmentCommand should not throw CommandException.");
        }
    }

    @Test
    public void execute_duplicateBetweenContactIndicesAndClassGroup_skipsDuplicate() {
        AddAssignmentCommand addAssignmentCommand = new AddAssignmentCommand(TypicalAssignments.ASSIGNMENT_ONE,
                List.of(Index.fromOneBased(1)), new ClassGroupName("OverlapGroup"));

        ModelStubWithClassGroupOverlap modelStub = new ModelStubWithClassGroupOverlap();
        try {
            CommandResult result = addAssignmentCommand.execute(modelStub);

            // assignment added
            Assertions.assertEquals(1, modelStub.assignmentsAdded.size());

            // allocation should only add Alice once (index + group overlap)
            Assertions.assertEquals(1, modelStub.contactAssignmentsAdded.size());
            String expectedAllocatedContacts = TypicalContacts.ALICE.getName().fullName;
            Assertions.assertEquals(String.format(AddAssignmentCommand.MESSAGE_SUCCESS_WITH_ALLOCATION,
                    Messages.format(TypicalAssignments.ASSIGNMENT_ONE), 1, expectedAllocatedContacts),
                    result.getFeedbackToUser());

        } catch (CommandException e) {
            Assertions.fail("Execution should not throw CommandException.");
        }
    }

    @Test
    public void execute_invalidContactIndex_throwsCommandException() {
        AddAssignmentCommand addAssignmentCommand = new AddAssignmentCommand(TypicalAssignments.ASSIGNMENT_ONE,
                List.of(Index.fromOneBased(100)));

        ModelStubAcceptingAssignmentAdded modelStub = new ModelStubAcceptingAssignmentAdded();
        Assert.assertThrows(CommandException.class, Messages.MESSAGE_INVALID_CONTACT_DISPLAYED_INDEX,
                () -> addAssignmentCommand.execute(modelStub));
    }

    @Test
    public void equals_sameValues_returnsTrue() throws Exception {
        Assignment assignment = TypicalAssignments.ASSIGNMENT_ONE;
        AddAssignmentCommand addAssignmentCommand = new AddAssignmentCommand(assignment,
                ParserUtil.parseContactIndices("1 2"), new ClassGroupName("ValidClassGroup"));
        AddAssignmentCommand addAssignmentCommandCopy = new AddAssignmentCommand(assignment,
                ParserUtil.parseContactIndices("1 2"), new ClassGroupName("ValidClassGroup"));

        // same object -> true
        Assertions.assertTrue(addAssignmentCommand.equals(addAssignmentCommand));

        // same values -> true
        Assertions.assertTrue(addAssignmentCommand.equals(addAssignmentCommandCopy));
    }

    @Test
    public void equals_differentValues_returnsFalse() throws Exception {
        Assignment assignment = TypicalAssignments.ASSIGNMENT_ONE;
        AddAssignmentCommand addAssignmentCommand = new AddAssignmentCommand(assignment,
                ParserUtil.parseContactIndices("1 2"), new ClassGroupName("ValidClassGroup"));

        // different types -> false
        Assertions.assertFalse(addAssignmentCommand.equals(1));

        // null -> false
        Assertions.assertFalse(addAssignmentCommand.equals(null));

        // different assignment -> false
        Assignment different = new AssignmentBuilder(TypicalAssignments.ASSIGNMENT_ONE)
                .withName("Different").build();
        AddAssignmentCommand differentCommand = new AddAssignmentCommand(different,
                ParserUtil.parseContactIndices("1 2"), new ClassGroupName("ValidClassGroup"));
        Assertions.assertFalse(addAssignmentCommand.equals(differentCommand));

        // different contact indices -> false
        AddAssignmentCommand differentCommand2 = new AddAssignmentCommand(assignment,
                ParserUtil.parseContactIndices("2 3"), new ClassGroupName("ValidClassGroup"));
        Assertions.assertFalse(addAssignmentCommand.equals(differentCommand2));

        // different class group -> false
        AddAssignmentCommand differentCommand3 = new AddAssignmentCommand(assignment,
                ParserUtil.parseContactIndices("1 2"), new ClassGroupName("DifferentClassGroup"));
        Assertions.assertFalse(addAssignmentCommand.equals(differentCommand3));
    }

    @Test
    public void toString_typicalValue_correctOutput() {
        AddAssignmentCommand addAssignmentCommand = new AddAssignmentCommand(TypicalAssignments.ASSIGNMENT_ONE,
                List.of());
        String expected = AddAssignmentCommand.class.getCanonicalName() + "{toAddAssignment="
                + TypicalAssignments.ASSIGNMENT_ONE
                + ", contactIndices=[]"
                + ", classGroupName=null"
                + "}";
        Assertions.assertEquals(expected, addAssignmentCommand.toString());
    }

    /**
     * A Model stub that contains a single assignment.
     */
    private class ModelStubWithAssignment extends ModelStub {
        private final Assignment assignment;

        ModelStubWithAssignment(Assignment assignment) {
            Objects.requireNonNull(assignment);
            this.assignment = assignment;
        }

        @Override
        public boolean hasAssignment(Assignment assignment) {
            Objects.requireNonNull(assignment);
            return this.assignment.getName().equals(assignment.getName());
        }
    }

    /**
     * A Model stub that always accept the assignment being added.
     */
    private class ModelStubAcceptingAssignmentAdded extends ModelStub {
        final ArrayList<Assignment> assignmentsAdded = new ArrayList<>();
        final ArrayList<ContactAssignment> contactAssignmentsAdded = new ArrayList<>();

        @Override
        public boolean hasAssignment(Assignment assignment) {
            Objects.requireNonNull(assignment);
            return this.assignmentsAdded.stream().anyMatch(a -> a.getName().equals(assignment.getName()));
        }

        @Override
        public void addAssignment(Assignment assignment) {
            Objects.requireNonNull(assignment);
            this.assignmentsAdded.add(assignment);
        }

        @Override
        public void addContactAssignment(ContactAssignment ca) {
            Objects.requireNonNull(ca);
            if (this.contactAssignmentsAdded.stream().anyMatch(existing -> existing.equals(ca))) {
                throw new ContactAlreadyAllocatedAssignmentException();
            }
            this.contactAssignmentsAdded.add(ca);
        }

        @Override
        public ObservableList<Contact> getFilteredContactList() {
            return FXCollections.observableArrayList();
        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            return new AddressBook();
        }
    }

    private class ModelStubWithClassGroup extends ModelStubAcceptingAssignmentAdded {
        @Override
        public boolean hasClassGroup(ClassGroup classGroup) {
            return classGroup.getName().equals(new ClassGroupName("ValidClassGroup"));
        }

        @Override
        public void addContactAssignment(ContactAssignment ca) {
            super.addContactAssignment(ca);
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
            ClassGroup cg = new ClassGroup(new ClassGroupName("ValidClassGroup"));
            try {
                cg.allocateContact(TypicalContacts.BENSON.getId());
            } catch (ContactAlreadyAllocatedClassGroupException e) {
                // ignore
            }
            ab.addClassGroup(cg);
            return ab;
        }

    }

    private class ModelStubWithClassGroupOverlap extends ModelStubAcceptingAssignmentAdded {
        @Override
        public boolean hasClassGroup(ClassGroup classGroup) {
            return classGroup.getName().equals(new ClassGroupName("OverlapGroup"));
        }

        @Override
        public void addContactAssignment(ContactAssignment ca) {
            super.addContactAssignment(ca);
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
            ClassGroup cg = new ClassGroup(new ClassGroupName("OverlapGroup"));
            try {
                cg.allocateContact(TypicalContacts.ALICE.getId());
            } catch (ContactAlreadyAllocatedClassGroupException e) {
                // ignore
            }
            ab.addClassGroup(cg);
            return ab;
        }
    }

}
