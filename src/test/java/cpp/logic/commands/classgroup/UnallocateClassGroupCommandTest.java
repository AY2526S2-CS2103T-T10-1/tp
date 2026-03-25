package cpp.logic.commands.classgroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cpp.commons.core.index.Index;
import cpp.logic.commands.CommandResult;
import cpp.logic.commands.exceptions.CommandException;
import cpp.model.AddressBook;
import cpp.model.classgroup.ClassGroup;
import cpp.model.classgroup.ClassGroupName;
import cpp.model.contact.Contact;
import cpp.testutil.Assert;
import cpp.testutil.ClassGroupBuilder;
import cpp.testutil.ModelStub;
import cpp.testutil.TypicalClassGroups;
import cpp.testutil.TypicalContacts;
import cpp.testutil.TypicalIndexes;
import javafx.collections.ObservableList;

public class UnallocateClassGroupCommandTest {

    @Test
    public void constructor_nullInputs_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class,
                () -> new UnallocateClassGroupCommand(null, new ArrayList<>()));
        Assert.assertThrows(NullPointerException.class,
                () -> new UnallocateClassGroupCommand(new ClassGroupName(ClassGroupBuilder.DEFAULT_NAME), null));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        UnallocateClassGroupCommand unallocateClassGroupCommand = new UnallocateClassGroupCommand(
                new ClassGroupName(ClassGroupBuilder.DEFAULT_NAME), new ArrayList<>());

        Assert.assertThrows(NullPointerException.class, () -> unallocateClassGroupCommand.execute(null));
    }

    @Test
    public void execute_validClassGroupNameAndContactIndices_success() throws Exception {
        ClassGroupName validClassGroupName = new ClassGroupName(ClassGroupBuilder.DEFAULT_NAME);
        Contact validContact1 = TypicalContacts.getTypicalContacts().get(0);
        Contact validContact2 = TypicalContacts.getTypicalContacts().get(1);
        Contact validContact3 = TypicalContacts.getTypicalContacts().get(2);
        ClassGroup validClassGroup = new ClassGroup(validClassGroupName);
        validClassGroup.allocateContact(validContact1.getId());
        validClassGroup.allocateContact(validContact2.getId());
        validClassGroup.allocateContact(validContact3.getId());
        ModelStubWithContactAndClassGroup modelStub = new ModelStubWithContactAndClassGroup(validContact1,
                validContact2, validContact3,
                validClassGroup);

        Assertions.assertEquals(3, validClassGroup.getContactIdSet().size());
        Assertions.assertEquals(3, modelStub.classGroup.getContactIdSet().size());
        Assertions.assertTrue(validClassGroup.getContactIdSet().contains(validContact1.getId()));
        Assertions.assertTrue(validClassGroup.getContactIdSet().contains(validContact2.getId()));
        Assertions.assertTrue(validClassGroup.getContactIdSet().contains(validContact3.getId()));

        ArrayList<Index> validContactIndices = new ArrayList<>(
                Arrays.asList(TypicalIndexes.INDEX_FIRST_CONTACT, TypicalIndexes.INDEX_SECOND_CONTACT));

        UnallocateClassGroupCommand unallocateClassGroupCommand = new UnallocateClassGroupCommand(validClassGroupName,
                validContactIndices);

        CommandResult commandResult = unallocateClassGroupCommand.execute(modelStub);

        Assertions.assertEquals(
                String.format(UnallocateClassGroupCommand.MESSAGE_SUCCESS, validClassGroupName.fullName, 2,
                        validContact1.getName() + "; " + validContact2.getName(), "None"),
                commandResult.getFeedbackToUser());
        Assertions.assertEquals(1, validClassGroup.getContactIdSet().size());
        Assertions.assertEquals(1, modelStub.classGroup.getContactIdSet().size());
        Assertions.assertFalse(validClassGroup.getContactIdSet().contains(validContact1.getId()));
        Assertions.assertFalse(validClassGroup.getContactIdSet().contains(validContact2.getId()));
        Assertions.assertTrue(validClassGroup.getContactIdSet().contains(validContact3.getId()));

        // New command with Indices 1 to 3, where indices 1 and 2 are already
        // unallocated from the class group, and index 3 is not unallocated yet
        ArrayList<Index> allContactIndices = new ArrayList<>(
                Arrays.asList(TypicalIndexes.INDEX_FIRST_CONTACT, TypicalIndexes.INDEX_SECOND_CONTACT,
                        TypicalIndexes.INDEX_THIRD_CONTACT));
        UnallocateClassGroupCommand unallocateClassGroupCommand2 = new UnallocateClassGroupCommand(validClassGroupName,
                allContactIndices);
        CommandResult newCommandResult = unallocateClassGroupCommand2.execute(modelStub);

        Assertions.assertEquals(
                String.format(UnallocateClassGroupCommand.MESSAGE_SUCCESS, validClassGroupName.fullName, 1,
                        validContact3.getName(), validContact1.getName() + "; " + validContact2.getName()),
                newCommandResult.getFeedbackToUser());
        Assertions.assertEquals(0, validClassGroup.getContactIdSet().size());
        Assertions.assertEquals(0, modelStub.classGroup.getContactIdSet().size());
        Assertions.assertFalse(validClassGroup.getContactIdSet().contains(validContact1.getId()));
        Assertions.assertFalse(validClassGroup.getContactIdSet().contains(validContact2.getId()));
        Assertions.assertFalse(validClassGroup.getContactIdSet().contains(validContact3.getId()));
    }

    @Test
    public void execute_invalidClassGroupName_throwsCommandException() {
        ClassGroupName existingClassGroupName = new ClassGroupName(ClassGroupBuilder.DEFAULT_NAME);
        ClassGroupName invalidClassGroupName = new ClassGroupName("UnknownClass");
        Contact validContact1 = TypicalContacts.getTypicalContacts().get(0);
        Contact validContact2 = TypicalContacts.getTypicalContacts().get(1);
        Contact validContact3 = TypicalContacts.getTypicalContacts().get(2);
        ClassGroup existingClassGroup = new ClassGroup(existingClassGroupName);
        existingClassGroup.allocateContact(validContact1.getId());
        ModelStubWithContactAndClassGroup modelStub = new ModelStubWithContactAndClassGroup(validContact1,
                validContact2, validContact3,
                existingClassGroup);

        ArrayList<Index> validContactIndices = new ArrayList<>(
                Arrays.asList(TypicalIndexes.INDEX_FIRST_CONTACT));

        UnallocateClassGroupCommand unallocateClassGroupCommand = new UnallocateClassGroupCommand(invalidClassGroupName,
                validContactIndices);

        Assert.assertThrows(CommandException.class, UnallocateClassGroupCommand.MESSAGE_INVALID_CLASS_GROUP_NAME,
                () -> unallocateClassGroupCommand.execute(modelStub));
    }

    @Test
    public void execute_classGroupNotAllocated_throwsCommandException() {
        ClassGroupName validClassGroupName = new ClassGroupName(ClassGroupBuilder.DEFAULT_NAME);
        Contact validContact1 = TypicalContacts.getTypicalContacts().get(0);
        Contact validContact2 = TypicalContacts.getTypicalContacts().get(1);
        Contact validContact3 = TypicalContacts.getTypicalContacts().get(2);
        ClassGroup validClassGroup = new ClassGroup(validClassGroupName);
        ModelStubWithContactAndClassGroup modelStub = new ModelStubWithContactAndClassGroup(validContact1,
                validContact2, validContact3,
                validClassGroup);

        ArrayList<Index> validContactIndices = new ArrayList<>(
                Arrays.asList(TypicalIndexes.INDEX_FIRST_CONTACT));

        UnallocateClassGroupCommand unallocateClassGroupCommand = new UnallocateClassGroupCommand(validClassGroupName,
                validContactIndices);

        Assert.assertThrows(CommandException.class, UnallocateClassGroupCommand.MESSAGE_UNALLOCATION_FAILED,
                () -> unallocateClassGroupCommand.execute(modelStub));
    }

    @Test
    public void equals_sameValues_returnsTrue() {
        UnallocateClassGroupCommand unallocateClassGroupCommand = new UnallocateClassGroupCommand(
                new ClassGroupName(ClassGroupBuilder.DEFAULT_NAME),
                new ArrayList<>(
                        Arrays.asList(TypicalIndexes.INDEX_FIRST_CONTACT, TypicalIndexes.INDEX_SECOND_CONTACT)));
        UnallocateClassGroupCommand unallocateClassGroupCommandCopy = new UnallocateClassGroupCommand(
                new ClassGroupName(ClassGroupBuilder.DEFAULT_NAME),
                new ArrayList<>(
                        Arrays.asList(TypicalIndexes.INDEX_FIRST_CONTACT, TypicalIndexes.INDEX_SECOND_CONTACT)));

        // Different object instances, but same values -> returns true
        Assertions.assertTrue(unallocateClassGroupCommand.equals(unallocateClassGroupCommandCopy));
        // Same object instance -> returns true
        Assertions.assertTrue(unallocateClassGroupCommand.equals(unallocateClassGroupCommand));
    }

    @Test
    public void equals_differentTypes_returnsFalse() {
        UnallocateClassGroupCommand unallocateClassGroupCommand = new UnallocateClassGroupCommand(
                new ClassGroupName(ClassGroupBuilder.DEFAULT_NAME), new ArrayList<>());
        AddClassGroupCommand addClassGroupCommand = new AddClassGroupCommand(
                TypicalClassGroups.CLASS_GROUP_ONE, new ArrayList<>());

        Assertions.assertFalse(unallocateClassGroupCommand.equals(addClassGroupCommand));
    }

    @Test
    public void equals_differentClassGroupName_returnsFalse() {
        UnallocateClassGroupCommand unallocateClassGroupCommand = new UnallocateClassGroupCommand(
                new ClassGroupName(ClassGroupBuilder.DEFAULT_NAME), new ArrayList<>());
        UnallocateClassGroupCommand differentClassGroupCommand = new UnallocateClassGroupCommand(
                new ClassGroupName("Different Name"), new ArrayList<>());

        Assertions.assertFalse(unallocateClassGroupCommand.equals(differentClassGroupCommand));
    }

    @Test
    public void equals_differentContactIndices_returnsFalse() {
        UnallocateClassGroupCommand unallocateClassGroupCommand = new UnallocateClassGroupCommand(
                new ClassGroupName(ClassGroupBuilder.DEFAULT_NAME),
                new ArrayList<>(Arrays.asList(TypicalIndexes.INDEX_FIRST_CONTACT)));
        UnallocateClassGroupCommand differentContactIndicesCommand = new UnallocateClassGroupCommand(
                new ClassGroupName(ClassGroupBuilder.DEFAULT_NAME),
                new ArrayList<>(Arrays.asList(TypicalIndexes.INDEX_SECOND_CONTACT)));

        Assertions.assertFalse(unallocateClassGroupCommand.equals(differentContactIndicesCommand));
    }

    @Test
    public void equals_null_returnsFalse() {
        UnallocateClassGroupCommand unallocateClassGroupCommand = new UnallocateClassGroupCommand(
                new ClassGroupName(ClassGroupBuilder.DEFAULT_NAME), new ArrayList<>());

        Assertions.assertFalse(unallocateClassGroupCommand.equals(null));
    }

    @Test
    public void toString_typicalValue_correctOutput() {
        UnallocateClassGroupCommand unallocateClassGroupCommand = new UnallocateClassGroupCommand(
                new ClassGroupName(ClassGroupBuilder.DEFAULT_NAME),
                new ArrayList<>(Arrays.asList(TypicalIndexes.INDEX_FIRST_CONTACT,
                        TypicalIndexes.INDEX_SECOND_CONTACT)));

        String expectedString = UnallocateClassGroupCommand.class.getCanonicalName() + "{"
                + "classGroupName=" + new ClassGroupName(ClassGroupBuilder.DEFAULT_NAME)
                + ", contactIndices="
                + new ArrayList<>(
                        Arrays.asList(TypicalIndexes.INDEX_FIRST_CONTACT, TypicalIndexes.INDEX_SECOND_CONTACT))
                + "}";

        Assertions.assertEquals(expectedString, unallocateClassGroupCommand.toString());
    }

    /**
     * A Model stub that contains a single class group and two contacts.
     */
    private class ModelStubWithContactAndClassGroup extends ModelStub {
        private final Contact contact1;
        private final Contact contact2;
        private final Contact contact3;
        private final ClassGroup classGroup;

        ModelStubWithContactAndClassGroup(Contact contact1, Contact contact2, Contact contact3, ClassGroup classGroup) {
            Objects.requireNonNull(contact1);
            Objects.requireNonNull(contact2);
            Objects.requireNonNull(contact3);
            Objects.requireNonNull(classGroup);
            this.contact1 = contact1;
            this.contact2 = contact2;
            this.contact3 = contact3;
            this.classGroup = classGroup;
        }

        @Override
        public boolean hasClassGroup(ClassGroup classGroup) {
            Objects.requireNonNull(classGroup);
            return this.classGroup.getName().equals(classGroup.getName());
        }

        @Override
        public AddressBook getAddressBook() {
            AddressBook addressBook = new AddressBook();
            addressBook.addContact(this.contact1);
            addressBook.addContact(this.contact2);
            addressBook.addContact(this.contact3);
            addressBook.addClassGroup(this.classGroup);
            return addressBook;
        }

        @Override
        public ObservableList<Contact> getFilteredContactList() {
            AddressBook addressBook = new AddressBook();
            addressBook.addContact(this.contact1);
            addressBook.addContact(this.contact2);
            addressBook.addContact(this.contact3);
            return addressBook.getContactList();
        }
    }
}
