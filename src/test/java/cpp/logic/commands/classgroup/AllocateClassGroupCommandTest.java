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

public class AllocateClassGroupCommandTest {

    @Test
    public void constructor_nullInputs_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class, () -> new AllocateClassGroupCommand(null, new ArrayList<>()));
        Assert.assertThrows(NullPointerException.class,
                () -> new AllocateClassGroupCommand(new ClassGroupName(ClassGroupBuilder.DEFAULT_NAME), null));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        AllocateClassGroupCommand allocateClassGroupCommand = new AllocateClassGroupCommand(
                new ClassGroupName(ClassGroupBuilder.DEFAULT_NAME), new ArrayList<>());

        Assert.assertThrows(NullPointerException.class, () -> allocateClassGroupCommand.execute(null));
    }

    @Test
    public void execute_validClassGroupNameAndContactIndices_success() throws Exception {
        ClassGroupName validClassGroupName = new ClassGroupName(ClassGroupBuilder.DEFAULT_NAME);
        Contact validContact1 = TypicalContacts.getTypicalContacts().get(0);
        Contact validContact2 = TypicalContacts.getTypicalContacts().get(1);
        ClassGroup validClassGroup = new ClassGroup(validClassGroupName);
        ModelStubWithContactAndClassGroup modelStub = new ModelStubWithContactAndClassGroup(validContact1,
                validContact2,
                validClassGroup);

        ArrayList<Index> validContactIndices = new ArrayList<>(
                Arrays.asList(TypicalIndexes.INDEX_FIRST_CONTACT, TypicalIndexes.INDEX_SECOND_CONTACT));

        AllocateClassGroupCommand allocateClassGroupCommand = new AllocateClassGroupCommand(validClassGroupName,
                validContactIndices);

        CommandResult commandResult = allocateClassGroupCommand.execute(modelStub);

        Assertions.assertEquals(
                String.format(AllocateClassGroupCommand.MESSAGE_SUCCESS, validClassGroupName, 2,
                        validContact1.getName() + "; " + validContact2.getName()),
                commandResult.getFeedbackToUser());
        Assertions.assertEquals(2, validClassGroup.getContactIdSet().size());
        Assertions.assertEquals(2, modelStub.classGroup.getContactIdSet().size());
        Assertions.assertTrue(validClassGroup.getContactIdSet().contains(validContact1.getId()));
        Assertions.assertTrue(validClassGroup.getContactIdSet().contains(validContact2.getId()));
    }

    @Test
    public void execute_classGroupAlreadyAllocated_throwsCommandException() {
        ClassGroupName validClassGroupName = new ClassGroupName(ClassGroupBuilder.DEFAULT_NAME);
        Contact validContact1 = TypicalContacts.getTypicalContacts().get(0);
        Contact validContact2 = TypicalContacts.getTypicalContacts().get(1);
        ClassGroup validClassGroup = new ClassGroup(validClassGroupName);
        validClassGroup.allocateContact(validContact1.getId());
        ModelStubWithContactAndClassGroup modelStub = new ModelStubWithContactAndClassGroup(validContact1,
                validContact2,
                validClassGroup);

        ArrayList<Index> validContactIndices = new ArrayList<>(
                Arrays.asList(TypicalIndexes.INDEX_FIRST_CONTACT));

        AllocateClassGroupCommand allocateClassGroupCommand = new AllocateClassGroupCommand(validClassGroupName,
                validContactIndices);

        Assert.assertThrows(CommandException.class, AllocateClassGroupCommand.MESSAGE_ALLOCATION_FAILED,
                () -> allocateClassGroupCommand.execute(modelStub));
    }

    @Test
    public void equals_sameValues_returnsTrue() {
        AllocateClassGroupCommand allocateClassGroupCommand = new AllocateClassGroupCommand(
                new ClassGroupName(ClassGroupBuilder.DEFAULT_NAME),
                new ArrayList<>(
                        Arrays.asList(TypicalIndexes.INDEX_FIRST_CONTACT, TypicalIndexes.INDEX_SECOND_CONTACT)));
        AllocateClassGroupCommand allocateClassGroupCommandCopy = new AllocateClassGroupCommand(
                new ClassGroupName(ClassGroupBuilder.DEFAULT_NAME),
                new ArrayList<>(
                        Arrays.asList(TypicalIndexes.INDEX_FIRST_CONTACT, TypicalIndexes.INDEX_SECOND_CONTACT)));

        // Different object instances, but same values -> returns true
        Assertions.assertTrue(allocateClassGroupCommand.equals(allocateClassGroupCommandCopy));
        // Same object instance -> returns true
        Assertions.assertTrue(allocateClassGroupCommand.equals(allocateClassGroupCommand));
    }

    @Test
    public void equals_differentTypes_returnsFalse() {
        AllocateClassGroupCommand allocateClassGroupCommand = new AllocateClassGroupCommand(
                new ClassGroupName(ClassGroupBuilder.DEFAULT_NAME), new ArrayList<>());
        AddClassGroupCommand addClassGroupCommand = new AddClassGroupCommand(
                TypicalClassGroups.CLASS_GROUP_ONE, new ArrayList<>());

        Assertions.assertFalse(allocateClassGroupCommand.equals(addClassGroupCommand));
    }

    @Test
    public void equals_differentClassGroupName_returnsFalse() {
        AllocateClassGroupCommand allocateClassGroupCommand = new AllocateClassGroupCommand(
                new ClassGroupName(ClassGroupBuilder.DEFAULT_NAME), new ArrayList<>());
        AllocateClassGroupCommand differentClassGroupCommand = new AllocateClassGroupCommand(
                new ClassGroupName("Different Name"), new ArrayList<>());

        Assertions.assertFalse(allocateClassGroupCommand.equals(differentClassGroupCommand));
    }

    @Test
    public void equals_differentContactIndices_returnsFalse() {
        AllocateClassGroupCommand allocateClassGroupCommand = new AllocateClassGroupCommand(
                new ClassGroupName(ClassGroupBuilder.DEFAULT_NAME),
                new ArrayList<>(Arrays.asList(TypicalIndexes.INDEX_FIRST_CONTACT)));
        AllocateClassGroupCommand differentContactIndicesCommand = new AllocateClassGroupCommand(
                new ClassGroupName(ClassGroupBuilder.DEFAULT_NAME),
                new ArrayList<>(Arrays.asList(TypicalIndexes.INDEX_SECOND_CONTACT)));

        Assertions.assertFalse(allocateClassGroupCommand.equals(differentContactIndicesCommand));
    }

    @Test
    public void equals_null_returnsFalse() {
        AllocateClassGroupCommand allocateClassGroupCommand = new AllocateClassGroupCommand(
                new ClassGroupName(ClassGroupBuilder.DEFAULT_NAME), new ArrayList<>());

        Assertions.assertFalse(allocateClassGroupCommand.equals(null));
    }

    @Test
    public void toString_typicalValue_correctOutput() {
        AllocateClassGroupCommand allocateClassGroupCommand = new AllocateClassGroupCommand(
                new ClassGroupName(ClassGroupBuilder.DEFAULT_NAME),
                new ArrayList<>(Arrays.asList(TypicalIndexes.INDEX_FIRST_CONTACT,
                        TypicalIndexes.INDEX_SECOND_CONTACT)));

        String expectedString = AllocateClassGroupCommand.class.getCanonicalName() + "{"
                + "classGroupName=" + new ClassGroupName(ClassGroupBuilder.DEFAULT_NAME)
                + ", contactIndices="
                + new ArrayList<>(
                        Arrays.asList(TypicalIndexes.INDEX_FIRST_CONTACT, TypicalIndexes.INDEX_SECOND_CONTACT))
                + "}";

        Assertions.assertEquals(expectedString, allocateClassGroupCommand.toString());
    }

    /**
     * A Model stub that contains a single class group and two contacts.
     */
    private class ModelStubWithContactAndClassGroup extends ModelStub {
        private final Contact contact1;
        private final Contact contact2;
        private final ClassGroup classGroup;

        ModelStubWithContactAndClassGroup(Contact contact1, Contact contact2, ClassGroup classGroup) {
            Objects.requireNonNull(contact1);
            Objects.requireNonNull(contact2);
            Objects.requireNonNull(classGroup);
            this.contact1 = contact1;
            this.contact2 = contact2;
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
            addressBook.addClassGroup(this.classGroup);
            return addressBook;
        }

        @Override
        public ObservableList<Contact> getFilteredContactList() {
            AddressBook addressBook = new AddressBook();
            addressBook.addContact(this.contact1);
            addressBook.addContact(this.contact2);
            return addressBook.getContactList();
        }
    }
}
