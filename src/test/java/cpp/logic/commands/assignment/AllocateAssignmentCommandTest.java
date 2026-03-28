package cpp.logic.commands.assignment;

import java.util.ArrayList;
import java.util.Arrays;
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
import cpp.model.assignment.ContactAssignment;
import cpp.model.assignment.exceptions.ContactAlreadyAllocatedAssignmentException;
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

public class AllocateAssignmentCommandTest {

    @Test
    public void constructor_nullInputs_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class,
                () -> new AllocateAssignmentCommand(null, new ArrayList<>()));
        Assert.assertThrows(NullPointerException.class,
                () -> new AllocateAssignmentCommand(TypicalAssignments.ASSIGNMENT_ONE.getName(), null));
    }

    @Test
    public void constructorOverloaded_nullClassGroupName_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class,
                () -> new AllocateAssignmentCommand(null, new ArrayList<>(), null));
        Assert.assertThrows(NullPointerException.class,
                () -> new AllocateAssignmentCommand(TypicalAssignments.ASSIGNMENT_ONE.getName(), new ArrayList<>(),
                        null));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        AllocateAssignmentCommand cmd = new AllocateAssignmentCommand(
                TypicalAssignments.ASSIGNMENT_ONE.getName(), new ArrayList<>());

        Assert.assertThrows(NullPointerException.class, () -> cmd.execute(null));
    }

    @Test
    public void execute_validClassGroupName_allocatesToClassGroupContacts() throws Exception {
        Assignment validAssignment = TypicalAssignments.ASSIGNMENT_ONE;
        // use typical contacts; class group will contain BENSON
        ModelStubWithClassGroup modelStub = new ModelStubWithClassGroup(validAssignment);

        AllocateAssignmentCommand cmd = new AllocateAssignmentCommand(validAssignment.getName(),
                new ArrayList<>(), new ClassGroupName("ValidClassGroup"));

        CommandResult result = cmd.execute(modelStub);

        String expectedAllocatedContacts = TypicalContacts.BENSON.getName().fullName;
        Assertions.assertEquals(String.format(AllocateAssignmentCommand.MESSAGE_SUCCESS,
                Messages.format(validAssignment), 1, expectedAllocatedContacts, "None"), result.getFeedbackToUser());
        Assertions.assertEquals(1, modelStub.contactAssignmentsAdded.size());
        Assertions.assertTrue(modelStub.contactAssignmentsAdded.stream()
                .anyMatch(ca -> ca.getContactId().equals(TypicalContacts.BENSON.getId())));
    }

    @Test
    public void execute_invalidClassGroupName_throwsCommandException() {
        Assignment validAssignment = TypicalAssignments.ASSIGNMENT_ONE;
        ModelStubWithClassGroup modelStub = new ModelStubWithClassGroup(validAssignment);

        AllocateAssignmentCommand cmd = new AllocateAssignmentCommand(validAssignment.getName(),
                new ArrayList<>(), new ClassGroupName("NonExistentGroup"));

        Assert.assertThrows(CommandException.class, Messages.MESSAGE_CLASS_GROUP_NOT_FOUND,
                () -> cmd.execute(modelStub));
    }

    @Test
    public void equals() throws Exception {
        ArrayList<Index> indices = new ArrayList<>(
                Arrays.asList(TypicalIndexes.INDEX_FIRST_CONTACT, TypicalIndexes.INDEX_SECOND_CONTACT));
        ClassGroupName cgName = new ClassGroupName("SomeGroup");

        AllocateAssignmentCommand cmd = new AllocateAssignmentCommand(TypicalAssignments.ASSIGNMENT_ONE.getName(),
                indices, cgName);
        AllocateAssignmentCommand cmdCopy = new AllocateAssignmentCommand(TypicalAssignments.ASSIGNMENT_ONE.getName(),
                new ArrayList<>(indices), new ClassGroupName("SomeGroup"));

        // same values
        Assertions.assertTrue(cmd.equals(cmdCopy));
        Assertions.assertTrue(cmd.equals(cmd));

        // different types
        Assertions.assertFalse(cmd.equals(1));

        // different assignment
        AllocateAssignmentCommand differentAssignment = new AllocateAssignmentCommand(
                new AssignmentName("Different"), indices, cgName);
        Assertions.assertFalse(cmd.equals(differentAssignment));

        // different indices
        AllocateAssignmentCommand differentIndices = new AllocateAssignmentCommand(
                TypicalAssignments.ASSIGNMENT_ONE.getName(),
                new ArrayList<>(Arrays.asList(TypicalIndexes.INDEX_FIRST_CONTACT)), cgName);
        Assertions.assertFalse(cmd.equals(differentIndices));

        // different class group
        AllocateAssignmentCommand differentCg = new AllocateAssignmentCommand(
                TypicalAssignments.ASSIGNMENT_ONE.getName(), indices, new ClassGroupName("OtherGroup"));
        Assertions.assertFalse(cmd.equals(differentCg));

        // null
        Assertions.assertFalse(cmd.equals(null));
    }

    @Test
    public void toStringMethod() throws Exception {
        ArrayList<Index> indices = new ArrayList<>(
                Arrays.asList(TypicalIndexes.INDEX_FIRST_CONTACT, TypicalIndexes.INDEX_SECOND_CONTACT));
        ClassGroupName cgName = new ClassGroupName("SomeGroup");

        AllocateAssignmentCommand cmd = new AllocateAssignmentCommand(TypicalAssignments.ASSIGNMENT_ONE.getName(),
                indices, cgName);

        String expectedString = AllocateAssignmentCommand.class.getCanonicalName() + "{"
                + "assignmentName=" + TypicalAssignments.ASSIGNMENT_ONE.getName()
                + ", contactIndices=" + indices
                + ", classGroupName=" + cgName
                + "}";
        Assertions.assertEquals(expectedString, cmd.toString());
    }

    @Test
    public void execute_validAssignmentNameAndContactIndices_success() throws Exception {
        Assignment validAssignment = TypicalAssignments.ASSIGNMENT_ONE;
        Contact validContact1 = TypicalContacts.getTypicalContacts().get(0);
        Contact validContact2 = TypicalContacts.getTypicalContacts().get(1);
        ModelStubWithContactAndAssignment modelStub = new ModelStubWithContactAndAssignment(validContact1,
                validContact2,
                validAssignment);

        ArrayList<Index> validContactIndices = new ArrayList<>(
                Arrays.asList(TypicalIndexes.INDEX_FIRST_CONTACT, TypicalIndexes.INDEX_SECOND_CONTACT));

        AllocateAssignmentCommand cmd = new AllocateAssignmentCommand(validAssignment.getName(), validContactIndices);

        CommandResult result = cmd.execute(modelStub);

        String expectedAllocatedContacts = validContact1.getName().fullName + "; " + validContact2.getName().fullName;
        Assertions.assertEquals(String.format(AllocateAssignmentCommand.MESSAGE_SUCCESS,
                Messages.format(validAssignment), 2, expectedAllocatedContacts, "None"), result.getFeedbackToUser());
        Assertions.assertEquals(2, modelStub.contactAssignmentsAdded.size());
        Assertions.assertTrue(modelStub.contactAssignmentsAdded.stream()
                .anyMatch(ca -> ca.getContactId().equals(validContact1.getId())));
        Assertions.assertTrue(modelStub.contactAssignmentsAdded.stream()
                .anyMatch(ca -> ca.getContactId().equals(validContact2.getId())));
    }

    @Test
    public void execute_validAssignmentNameAndContactIndicesAndClassGroupName_success() throws Exception {
        Assignment validAssignment = TypicalAssignments.ASSIGNMENT_ONE;
        Contact validContact1 = TypicalContacts.getTypicalContacts().get(0);
        Contact validContact2 = TypicalContacts.getTypicalContacts().get(1);
        ModelStubWithClassGroup modelStub = new ModelStubWithClassGroup(validAssignment);

        ArrayList<Index> validContactIndices = new ArrayList<>(
                Arrays.asList(TypicalIndexes.INDEX_FIRST_CONTACT, TypicalIndexes.INDEX_SECOND_CONTACT));
        ClassGroupName validClassGroupName = new ClassGroupName("ValidClassGroup");

        // prepopulate with allocation so duplicate exception occurs for BENSON in class
        // group allocation
        modelStub.contactAssignmentsAdded.add(new ContactAssignment(validAssignment.getId(), validContact1.getId()));

        AllocateAssignmentCommand cmd = new AllocateAssignmentCommand(validAssignment.getName(), validContactIndices,
                validClassGroupName);

        CommandResult result = cmd.execute(modelStub);

        String expectedAllocatedContacts = validContact2.getName().fullName;
        Assertions.assertEquals(String.format(AllocateAssignmentCommand.MESSAGE_SUCCESS,
                Messages.format(validAssignment), 1, expectedAllocatedContacts, validContact1.getName().fullName),
                result.getFeedbackToUser());
    }

    @Test
    public void execute_assignmentNotFound_throwsCommandException() {
        Contact validContact1 = TypicalContacts.getTypicalContacts().get(0);
        Contact validContact2 = TypicalContacts.getTypicalContacts().get(1);
        ModelStubWithContactsOnly modelStub = new ModelStubWithContactsOnly(validContact1, validContact2);

        ArrayList<Index> validContactIndices = new ArrayList<>(
                Arrays.asList(TypicalIndexes.INDEX_FIRST_CONTACT));

        AllocateAssignmentCommand cmd = new AllocateAssignmentCommand(new AssignmentName("NonExistent"),
                validContactIndices);

        Assert.assertThrows(CommandException.class, Messages.MESSAGE_ASSIGNMENT_NOT_FOUND,
                () -> cmd.execute(modelStub));
    }

    @Test
    public void execute_emptyClassGroup_throwsCommandException() {
        Assignment validAssignment = TypicalAssignments.ASSIGNMENT_ONE;
        ModelStubWithEmptyClassGroup modelStub = new ModelStubWithEmptyClassGroup(validAssignment);

        ClassGroupName emptyClassGroupName = new ClassGroupName("EmptyGroup");

        AllocateAssignmentCommand cmd = new AllocateAssignmentCommand(validAssignment.getName(),
                new ArrayList<>(), emptyClassGroupName);

        Assert.assertThrows(CommandException.class, Messages.MESSAGE_CLASS_GROUP_NO_CONTACTS,
                () -> cmd.execute(modelStub));
    }

    @Test
    public void execute_allocationAlreadyAllocated_throwsCommandException() {
        Assignment validAssignment = TypicalAssignments.ASSIGNMENT_ONE;
        Contact validContact1 = TypicalContacts.getTypicalContacts().get(0);
        Contact validContact2 = TypicalContacts.getTypicalContacts().get(1);
        ModelStubWithContactAndAssignment modelStub = new ModelStubWithContactAndAssignment(validContact1,
                validContact2,
                validAssignment);

        // prepopulate with allocations so duplicate exception occurs for both contacts
        modelStub.contactAssignmentsAdded.add(new ContactAssignment(validAssignment.getId(), validContact1.getId()));
        modelStub.contactAssignmentsAdded.add(new ContactAssignment(validAssignment.getId(), validContact2.getId()));

        ArrayList<Index> validContactIndices = new ArrayList<>(
                Arrays.asList(TypicalIndexes.INDEX_FIRST_CONTACT, TypicalIndexes.INDEX_SECOND_CONTACT));

        AllocateAssignmentCommand cmd = new AllocateAssignmentCommand(validAssignment.getName(), validContactIndices);

        Assert.assertThrows(CommandException.class,
                String.format(AllocateAssignmentCommand.MESSAGE_ALLOCATION_FAILED,
                        validContact1.getName().fullName + "; " + validContact2.getName().fullName),
                () -> cmd.execute(modelStub));
    }

    /**
     * A Model stub that contains two contacts and an existing assignment and
     * accepts allocations.
     */
    private class ModelStubWithContactAndAssignment extends ModelStub {
        private final Contact contact1;
        private final Contact contact2;
        private final Assignment assignment;
        private final ArrayList<ContactAssignment> contactAssignmentsAdded = new ArrayList<>();

        ModelStubWithContactAndAssignment(Contact contact1, Contact contact2, Assignment assignment) {
            Objects.requireNonNull(contact1);
            Objects.requireNonNull(contact2);
            Objects.requireNonNull(assignment);
            this.contact1 = contact1;
            this.contact2 = contact2;
            this.assignment = assignment;
        }

        @Override
        public ObservableList<Contact> getFilteredContactList() {
            return FXCollections.observableArrayList(this.contact1, this.contact2);
        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            AddressBook ab = new AddressBook();
            ab.addContact(this.contact1);
            ab.addContact(this.contact2);
            ab.addAssignment(this.assignment);
            return ab;
        }

        @Override
        public void addContactAssignment(ContactAssignment ca) {
            Objects.requireNonNull(ca);
            if (this.contactAssignmentsAdded.stream().anyMatch(existing -> existing.equals(ca))) {
                throw new ContactAlreadyAllocatedAssignmentException();
            }
            this.contactAssignmentsAdded.add(ca);
        }
    }

    /**
     * Model stub with only contacts and no assignments.
     */
    private class ModelStubWithContactsOnly extends ModelStub {
        private final Contact contact1;
        private final Contact contact2;

        ModelStubWithContactsOnly(Contact contact1, Contact contact2) {
            Objects.requireNonNull(contact1);
            Objects.requireNonNull(contact2);
            this.contact1 = contact1;
            this.contact2 = contact2;
        }

        @Override
        public ObservableList<Contact> getFilteredContactList() {
            return FXCollections.observableArrayList(this.contact1, this.contact2);
        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            AddressBook ab = new AddressBook();
            ab.addContact(this.contact1);
            ab.addContact(this.contact2);
            return ab;
        }
    }

    /**
     * A Model stub that contains a typical set of contacts, one assignment, and
     * a class group containing BENSON.
     */
    private class ModelStubWithClassGroup extends ModelStub {
        private final Assignment assignment;
        private final ArrayList<ContactAssignment> contactAssignmentsAdded = new ArrayList<>();

        ModelStubWithClassGroup(Assignment assignment) {
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
            ClassGroup cg = new ClassGroup(new ClassGroupName("ValidClassGroup"));
            try {
                cg.allocateContact(TypicalContacts.BENSON.getId());
            } catch (Exception e) {
                // ignore
            }
            ab.addClassGroup(cg);
            return ab;
        }

        @Override
        public void addContactAssignment(ContactAssignment ca) {
            Objects.requireNonNull(ca);
            if (this.contactAssignmentsAdded.stream().anyMatch(existing -> existing.equals(ca))) {
                throw new ContactAlreadyAllocatedAssignmentException();
            }
            this.contactAssignmentsAdded.add(ca);
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
