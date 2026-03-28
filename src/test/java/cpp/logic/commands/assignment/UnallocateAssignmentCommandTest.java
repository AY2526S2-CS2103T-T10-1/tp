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
import cpp.model.assignment.exceptions.ContactAssignmentNotFoundException;
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

public class UnallocateAssignmentCommandTest {

    @Test
    public void constructor_nullInputs_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class,
                () -> new UnallocateAssignmentCommand(null, new ArrayList<>()));
        Assert.assertThrows(NullPointerException.class,
                () -> new UnallocateAssignmentCommand(TypicalAssignments.ASSIGNMENT_ONE.getName(), null));
    }

    @Test
    public void constructorOverloaded_nullClassGroupName_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class,
                () -> new UnallocateAssignmentCommand(null, new ArrayList<>(), null));
        Assert.assertThrows(NullPointerException.class,
                () -> new UnallocateAssignmentCommand(TypicalAssignments.ASSIGNMENT_ONE.getName(), new ArrayList<>(),
                        null));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        UnallocateAssignmentCommand cmd = new UnallocateAssignmentCommand(
                TypicalAssignments.ASSIGNMENT_ONE.getName(), new ArrayList<>());

        Assert.assertThrows(NullPointerException.class, () -> cmd.execute(null));
    }

    @Test
    public void execute_validAssignmentNameAndContactIndices_success() throws Exception {
        Assignment validAssignment = TypicalAssignments.ASSIGNMENT_ONE;
        Contact validContact1 = TypicalContacts.getTypicalContacts().get(0);
        Contact validContact2 = TypicalContacts.getTypicalContacts().get(1);
        ModelStubWithAssignedContactAssignments modelStub = new ModelStubWithAssignedContactAssignments(validContact1,
                validContact2,
                validAssignment);

        ArrayList<Index> validContactIndices = new ArrayList<>(
                Arrays.asList(TypicalIndexes.INDEX_FIRST_CONTACT, TypicalIndexes.INDEX_SECOND_CONTACT));

        UnallocateAssignmentCommand cmd = new UnallocateAssignmentCommand(validAssignment.getName(),
                validContactIndices);

        CommandResult result = cmd.execute(modelStub);

        String expectedUnallocatedContacts = validContact1.getName().fullName + "; " + validContact2.getName().fullName;
        Assertions.assertEquals(String.format(UnallocateAssignmentCommand.MESSAGE_SUCCESS,
                Messages.format(validAssignment), 2, expectedUnallocatedContacts, "None"), result.getFeedbackToUser());
        Assertions.assertEquals(0, modelStub.contactAssignments.size());
    }

    @Test
    public void execute_validClassGroupName_unallocatesFromClassGroup() throws Exception {
        Assignment validAssignment = TypicalAssignments.ASSIGNMENT_ONE;
        ModelStubWithAssignedClassGroup modelStub = new ModelStubWithAssignedClassGroup(validAssignment);

        UnallocateAssignmentCommand cmd = new UnallocateAssignmentCommand(validAssignment.getName(),
                new ArrayList<>(), new ClassGroupName("ValidClassGroup"));

        CommandResult result = cmd.execute(modelStub);

        String expectedUnallocatedContacts = TypicalContacts.BENSON.getName().fullName;
        Assertions.assertEquals(String.format(UnallocateAssignmentCommand.MESSAGE_SUCCESS,
                Messages.format(validAssignment), 1, expectedUnallocatedContacts, "None"), result.getFeedbackToUser());
        Assertions.assertEquals(1, modelStub.contactAssignments.size()); // AMY should still be allocated
    }

    @Test
    public void execute_validClassGroupNameAndContactIndices_unallocatesFromClassGroupAndSpecifiedContacts()
            throws Exception {
        Assignment validAssignment = TypicalAssignments.ASSIGNMENT_ONE;
        Contact validContact1 = TypicalContacts.getTypicalContacts().get(0); // ALICE
        Contact validContact2 = TypicalContacts.getTypicalContacts().get(1); // BENSON
        ModelStubWithAssignedClassGroup modelStub = new ModelStubWithAssignedClassGroup(validAssignment);

        ArrayList<Index> validContactIndices = new ArrayList<>(
                Arrays.asList(TypicalIndexes.INDEX_FIRST_CONTACT, TypicalIndexes.INDEX_SECOND_CONTACT,
                        TypicalIndexes.INDEX_THIRD_CONTACT));

        UnallocateAssignmentCommand cmd = new UnallocateAssignmentCommand(validAssignment.getName(),
                validContactIndices, new ClassGroupName("ValidClassGroup"));

        CommandResult result = cmd.execute(modelStub);

        Assertions.assertEquals(String.format(UnallocateAssignmentCommand.MESSAGE_SUCCESS,
                Messages.format(validAssignment), 1, validContact2.getName().fullName,
                validContact1.getName().fullName + "; "
                        + TypicalContacts.getTypicalContacts().get(2).getName().fullName),
                result.getFeedbackToUser());
        // AMY should still be allocated, BENSON should be unallocated, ALICE was not
        // allocated to begin with so should not affect the count
        Assertions.assertEquals(1, modelStub.contactAssignments.size());
    }

    @Test
    public void execute_invalidClassGroupName_throwsCommandException() {
        Assignment validAssignment = TypicalAssignments.ASSIGNMENT_ONE;
        ModelStubWithAssignedClassGroup modelStub = new ModelStubWithAssignedClassGroup(validAssignment);

        UnallocateAssignmentCommand cmd = new UnallocateAssignmentCommand(validAssignment.getName(),
                new ArrayList<>(), new ClassGroupName("InvalidClassGroup"));

        Assert.assertThrows(CommandException.class, Messages.MESSAGE_CLASS_GROUP_NOT_FOUND,
                () -> cmd.execute(modelStub));
    }

    @Test
    public void equals() {
        ArrayList<Index> indices = new ArrayList<>(
                Arrays.asList(TypicalIndexes.INDEX_FIRST_CONTACT, TypicalIndexes.INDEX_SECOND_CONTACT));
        ClassGroupName cgName = new ClassGroupName("SomeGroup");

        UnallocateAssignmentCommand cmd = new UnallocateAssignmentCommand(TypicalAssignments.ASSIGNMENT_ONE.getName(),
                indices, cgName);
        UnallocateAssignmentCommand cmdCopy = new UnallocateAssignmentCommand(
                TypicalAssignments.ASSIGNMENT_ONE.getName(),
                new ArrayList<>(indices), new ClassGroupName("SomeGroup"));

        // same values
        Assertions.assertTrue(cmd.equals(cmdCopy));
        Assertions.assertTrue(cmd.equals(cmd));

        // different types
        Assertions.assertFalse(cmd.equals(1));

        // different assignment
        UnallocateAssignmentCommand differentAssignment = new UnallocateAssignmentCommand(
                new AssignmentName("Different"), indices, cgName);
        Assertions.assertFalse(cmd.equals(differentAssignment));

        // different indices
        UnallocateAssignmentCommand differentIndices = new UnallocateAssignmentCommand(
                TypicalAssignments.ASSIGNMENT_ONE.getName(),
                new ArrayList<>(Arrays.asList(TypicalIndexes.INDEX_FIRST_CONTACT)), cgName);
        Assertions.assertFalse(cmd.equals(differentIndices));

        // different class group
        UnallocateAssignmentCommand differentCg = new UnallocateAssignmentCommand(
                TypicalAssignments.ASSIGNMENT_ONE.getName(), indices, new ClassGroupName("OtherGroup"));
        Assertions.assertFalse(cmd.equals(differentCg));

        // null
        Assertions.assertFalse(cmd.equals(null));
    }

    @Test
    public void toStringMethod() {
        ArrayList<Index> indices = new ArrayList<>(
                Arrays.asList(TypicalIndexes.INDEX_FIRST_CONTACT, TypicalIndexes.INDEX_SECOND_CONTACT));
        ClassGroupName cgName = new ClassGroupName("SomeGroup");

        UnallocateAssignmentCommand cmd = new UnallocateAssignmentCommand(TypicalAssignments.ASSIGNMENT_ONE.getName(),
                indices, cgName);

        String expectedString = UnallocateAssignmentCommand.class.getCanonicalName() + "{"
                + "assignmentName=" + TypicalAssignments.ASSIGNMENT_ONE.getName()
                + ", contactIndices=" + indices
                + ", classGroupName=" + cgName
                + "}";
        Assertions.assertEquals(expectedString, cmd.toString());
    }

    @Test
    public void execute_invalidAssignmentName_throwsCommandException() {
        Contact validContact1 = TypicalContacts.getTypicalContacts().get(0);
        Contact validContact2 = TypicalContacts.getTypicalContacts().get(1);
        ModelStubWithContactsOnly modelStub = new ModelStubWithContactsOnly(validContact1, validContact2);

        ArrayList<Index> validContactIndices = new ArrayList<>(
                Arrays.asList(TypicalIndexes.INDEX_FIRST_CONTACT));

        UnallocateAssignmentCommand cmd = new UnallocateAssignmentCommand(
                new AssignmentName("Unknown"),
                validContactIndices);

        Assert.assertThrows(CommandException.class, Messages.MESSAGE_ASSIGNMENT_NOT_FOUND,
                () -> cmd.execute(modelStub));
    }

    @Test
    public void execute_emptyClassGroup_throwsCommandException() {
        Assignment validAssignment = TypicalAssignments.ASSIGNMENT_ONE;
        ModelStubWithEmptyClassGroup modelStub = new ModelStubWithEmptyClassGroup(validAssignment);

        ClassGroupName emptyClassGroupName = new ClassGroupName("EmptyGroup");
        UnallocateAssignmentCommand cmd = new UnallocateAssignmentCommand(validAssignment.getName(),
                new ArrayList<>(), emptyClassGroupName);

        Assert.assertThrows(CommandException.class, Messages.MESSAGE_CLASS_GROUP_NO_CONTACTS,
                () -> cmd.execute(modelStub));
    }

    @Test
    public void execute_unallocationFailed_throwsCommandException() {
        Assignment validAssignment = TypicalAssignments.ASSIGNMENT_ONE;
        Contact validContact1 = TypicalContacts.getTypicalContacts().get(0);
        Contact validContact2 = TypicalContacts.getTypicalContacts().get(1);
        ModelStubWithAssignmentNoContactAssignments modelStub = new ModelStubWithAssignmentNoContactAssignments(
                validContact1, validContact2, validAssignment);

        ArrayList<Index> validContactIndices = new ArrayList<>(
                Arrays.asList(TypicalIndexes.INDEX_FIRST_CONTACT));

        UnallocateAssignmentCommand cmd = new UnallocateAssignmentCommand(validAssignment.getName(),
                validContactIndices);

        Assert.assertThrows(CommandException.class,
                String.format(UnallocateAssignmentCommand.MESSAGE_UNALLOCATION_FAILED,
                        TypicalContacts.ALICE.getName().fullName),
                () -> cmd.execute(modelStub));
    }

    /**
     * A Model stub that contains two contacts and one assignment, with the
     * assignment allocated to both contacts.
     */
    private class ModelStubWithAssignedContactAssignments extends ModelStub {
        private final Contact contact1;
        private final Contact contact2;
        private final Assignment assignment;
        private final ArrayList<ContactAssignment> contactAssignments = new ArrayList<>();

        ModelStubWithAssignedContactAssignments(Contact contact1, Contact contact2, Assignment assignment) {
            Objects.requireNonNull(contact1);
            Objects.requireNonNull(contact2);
            Objects.requireNonNull(assignment);
            this.contact1 = contact1;
            this.contact2 = contact2;
            this.assignment = assignment;
            this.contactAssignments.add(new ContactAssignment(assignment.getId(), contact1.getId()));
            this.contactAssignments.add(new ContactAssignment(assignment.getId(), contact2.getId()));
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
        public void removeContactAssignment(ContactAssignment ca) {
            Objects.requireNonNull(ca);
            boolean removed = this.contactAssignments.removeIf(existing -> existing.equals(ca));
            if (!removed) {
                throw new ContactAssignmentNotFoundException();
            }
        }
    }

    private class ModelStubWithAssignmentNoContactAssignments extends ModelStub {
        private final Contact contact1;
        private final Contact contact2;
        private final Assignment assignment;

        ModelStubWithAssignmentNoContactAssignments(Contact contact1, Contact contact2, Assignment assignment) {
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
        public void removeContactAssignment(ContactAssignment ca) {
            Objects.requireNonNull(ca);
            throw new ContactAssignmentNotFoundException();
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
     * A Model stub that contains typical contacts, one assignment and a class
     * group which contains BENSON and has the assignment allocated to BENSON.
     */
    private class ModelStubWithAssignedClassGroup extends ModelStub {
        private final Assignment assignment;
        private final ArrayList<ContactAssignment> contactAssignments = new ArrayList<>();

        ModelStubWithAssignedClassGroup(Assignment assignment) {
            Objects.requireNonNull(assignment);
            this.assignment = assignment;
            // pre-allocate to BENSON and AMY
            this.contactAssignments.add(new ContactAssignment(assignment.getId(), TypicalContacts.BENSON.getId()));
            this.contactAssignments.add(
                    new ContactAssignment(assignment.getId(), TypicalContacts.AMY.getId()));
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
        public void removeContactAssignment(ContactAssignment ca) {
            Objects.requireNonNull(ca);
            boolean removed = this.contactAssignments.removeIf(existing -> existing.equals(ca));
            if (!removed) {
                throw new ContactAssignmentNotFoundException();
            }
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
