package cpp.logic.commands.classgroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cpp.logic.Messages;
import cpp.logic.commands.CommandResult;
import cpp.logic.commands.exceptions.CommandException;
import cpp.model.AddressBook;
import cpp.model.ReadOnlyAddressBook;
import cpp.model.classgroup.ClassGroup;
import cpp.model.contact.Contact;
import cpp.testutil.Assert;
import cpp.testutil.ClassGroupBuilder;
import cpp.testutil.ModelStub;
import cpp.testutil.TypicalClassGroups;
import cpp.testutil.TypicalContacts;
import cpp.testutil.TypicalIndexes;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class AddClassGroupCommandTest {

    @Test
    public void constructor_nullInputs_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class,
                () -> new AddClassGroupCommand(TypicalClassGroups.CLASS_GROUP_ONE, null));
        Assert.assertThrows(NullPointerException.class,
                () -> new AddClassGroupCommand(null, new ArrayList<>()));
    }

    @Test
    public void execute_classGroupAcceptedByModelWithoutAllocation_addSuccessful() throws Exception {
        ModelStubAcceptingClassGroupAdded modelStub = new ModelStubAcceptingClassGroupAdded();
        ClassGroup validClassGroup = TypicalClassGroups.CLASS_GROUP_ONE;

        CommandResult commandResult = new AddClassGroupCommand(validClassGroup, new ArrayList<>()).execute(modelStub);

        Assertions.assertEquals(String.format(AddClassGroupCommand.MESSAGE_SUCCESS, Messages.format(validClassGroup)),
                commandResult.getFeedbackToUser());
        Assertions.assertEquals(1, modelStub.classGroupsAdded.size());
        Assertions.assertEquals(validClassGroup, modelStub.classGroupsAdded.get(0));
    }

    @Test
    public void execute_classGroupAcceptedByModelWithAllocation_addSuccessful() throws Exception {
        ModelStubAcceptingClassGroupAdded modelStub = new ModelStubAcceptingClassGroupAdded();
        ClassGroup validClassGroup = TypicalClassGroups.CLASS_GROUP_ONE;

        CommandResult commandResult = new AddClassGroupCommand(validClassGroup,
                Arrays.asList(TypicalIndexes.INDEX_FIRST_CONTACT, TypicalIndexes.INDEX_SECOND_CONTACT))
                .execute(modelStub);

        Assertions.assertEquals(
                String.format(AddClassGroupCommand.MESSAGE_SUCCESS_WITH_ALLOCATION, Messages.format(validClassGroup), 2,
                        "Alice Pauline; Benson Meier"),
                commandResult.getFeedbackToUser());
        Assertions.assertEquals(1, modelStub.classGroupsAdded.size());
        Assertions.assertEquals(validClassGroup, modelStub.classGroupsAdded.get(0));
        Assertions.assertTrue(
                modelStub.classGroupsAdded.get(0).getContactIdSet()
                        .contains(TypicalContacts.getTypicalContacts().get(0).getId()));
        Assertions.assertTrue(modelStub.classGroupsAdded.get(0).getContactIdSet()
                .contains(TypicalContacts.getTypicalContacts().get(1).getId()));
    }

    @Test
    public void execute_duplicateClassGroup_throwsCommandException() {
        ClassGroup validClassGroup = TypicalClassGroups.CLASS_GROUP_ONE;
        AddClassGroupCommand addContactCommand = new AddClassGroupCommand(validClassGroup, new ArrayList<>());
        ModelStub modelStub = new ModelStubWithClassGroup(validClassGroup);

        Assert.assertThrows(CommandException.class, AddClassGroupCommand.MESSAGE_DUPLICATE_CLASS_GROUP,
                () -> addContactCommand.execute(modelStub));
    }

    @Test
    public void equals_sameValues_returnsTrue() {
        ClassGroup classGroup = TypicalClassGroups.CLASS_GROUP_ONE;
        AddClassGroupCommand addContactCommand = new AddClassGroupCommand(classGroup, new ArrayList<>());
        AddClassGroupCommand addContactCommandCopy = new AddClassGroupCommand(classGroup, new ArrayList<>());

        // same object -> true
        Assertions.assertTrue(addContactCommand.equals(addContactCommand));

        // same values -> true
        Assertions.assertTrue(addContactCommand.equals(addContactCommandCopy));
    }

    @Test
    public void equals_differentValues_returnsFalse() {
        ClassGroup classGroup = TypicalClassGroups.CLASS_GROUP_ONE;
        AddClassGroupCommand addContactCommand = new AddClassGroupCommand(classGroup, new ArrayList<>());

        // different types -> false
        Assertions.assertFalse(addContactCommand.equals(1));

        // null -> false
        Assertions.assertFalse(addContactCommand.equals(null));

        // different class group -> false
        ClassGroup different = new ClassGroupBuilder(classGroup).withName("CS2101T10").build();
        AddClassGroupCommand differentCommand = new AddClassGroupCommand(different, new ArrayList<>());
        Assertions.assertFalse(addContactCommand.equals(differentCommand));
    }

    @Test
    public void toString_typicalValue_correctOutput() {
        AddClassGroupCommand addContactCommand = new AddClassGroupCommand(TypicalClassGroups.CLASS_GROUP_ONE,
                new ArrayList<>());
        String expected = AddClassGroupCommand.class.getCanonicalName() + "{toAdd="
                + TypicalClassGroups.CLASS_GROUP_ONE + ", contactIndices=" + new ArrayList<>() + "}";
        Assertions.assertEquals(expected, addContactCommand.toString());
    }

    /**
     * A Model stub that contains a single class group.
     */
    private class ModelStubWithClassGroup extends ModelStub {
        private final ClassGroup classGroup;

        ModelStubWithClassGroup(ClassGroup classGroup) {
            Objects.requireNonNull(classGroup);
            this.classGroup = classGroup;
        }

        @Override
        public boolean hasClassGroup(ClassGroup classGroup) {
            Objects.requireNonNull(classGroup);
            return this.classGroup.getName().equals(classGroup.getName());
        }
    }

    /**
     * A Model stub that always accepts the class group being added.
     */
    private class ModelStubAcceptingClassGroupAdded extends ModelStub {
        final ArrayList<ClassGroup> classGroupsAdded = new ArrayList<>();

        @Override
        public boolean hasClassGroup(ClassGroup classGroup) {
            Objects.requireNonNull(classGroup);
            return this.classGroupsAdded.stream().anyMatch(cg -> cg.getName().equals(classGroup.getName()));
        }

        @Override
        public void addClassGroup(ClassGroup classGroup) {
            Objects.requireNonNull(classGroup);
            this.classGroupsAdded.add(classGroup);
        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            AddressBook ab = new AddressBook();
            for (Contact c : TypicalContacts.getTypicalContacts()) {
                ab.addContact(c);
            }
            return ab;
        }

        @Override
        public ObservableList<Contact> getFilteredContactList() {
            return FXCollections.observableArrayList(TypicalContacts.getTypicalContacts());
        }
    }
}
