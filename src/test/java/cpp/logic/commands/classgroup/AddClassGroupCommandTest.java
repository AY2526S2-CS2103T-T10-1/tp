package cpp.logic.commands.classgroup;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Predicate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cpp.commons.core.GuiSettings;
import cpp.logic.Messages;
import cpp.logic.commands.CommandResult;
import cpp.logic.commands.exceptions.CommandException;
import cpp.model.AddressBook;
import cpp.model.Model;
import cpp.model.ReadOnlyAddressBook;
import cpp.model.ReadOnlyUserPrefs;
import cpp.model.assignment.Assignment;
import cpp.model.assignment.ContactAssignment;
import cpp.model.classgroup.ClassGroup;
import cpp.model.contact.Contact;
import cpp.testutil.Assert;
import cpp.testutil.ClassGroupBuilder;
import cpp.testutil.TypicalClassGroups;
import javafx.collections.ObservableList;

public class AddClassGroupCommandTest {

    @Test
    public void constructor_nullClassGroup_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class, () -> new AddClassGroupCommand(null, null));
    }

    @Test
    public void execute_classGroupAcceptedByModel_addSuccessful() throws Exception {
        ModelStubAcceptingClassGroupAdded modelStub = new ModelStubAcceptingClassGroupAdded();
        ClassGroup validClassGroup = TypicalClassGroups.CLASS_GROUP_ONE;

        CommandResult commandResult = new AddClassGroupCommand(validClassGroup, new ArrayList<>()).execute(modelStub);

        Assertions.assertEquals(
                String.format(AddClassGroupCommand.MESSAGE_SUCCESS, Messages.format(validClassGroup)),
                commandResult.getFeedbackToUser());
        Assertions.assertEquals(1, modelStub.classGroupsAdded.size());
        Assertions.assertEquals(validClassGroup, modelStub.classGroupsAdded.get(0));
    }

    @Test
    public void execute_duplicateClassGroup_throwsCommandException() {
        ClassGroup validClassGroup = TypicalClassGroups.CLASS_GROUP_ONE;
        AddClassGroupCommand addCommand = new AddClassGroupCommand(validClassGroup, new ArrayList<>());
        ModelStub modelStub = new ModelStubWithClassGroup(validClassGroup);

        Assert.assertThrows(CommandException.class, AddClassGroupCommand.MESSAGE_DUPLICATE_CLASS_GROUP,
                () -> addCommand.execute(modelStub));
    }

    @Test
    public void equals_sameValues_returnsTrue() {
        ClassGroup classGroup = TypicalClassGroups.CLASS_GROUP_ONE;
        AddClassGroupCommand addCommand = new AddClassGroupCommand(classGroup, new ArrayList<>());
        AddClassGroupCommand addCommandCopy = new AddClassGroupCommand(classGroup, new ArrayList<>());

        // same object -> true
        Assertions.assertTrue(addCommand.equals(addCommand));

        // same values -> true
        Assertions.assertTrue(addCommand.equals(addCommandCopy));
    }

    @Test
    public void equals_differentValues_returnsFalse() {
        ClassGroup classGroup = TypicalClassGroups.CLASS_GROUP_ONE;
        AddClassGroupCommand addCommand = new AddClassGroupCommand(classGroup, new ArrayList<>());

        // different types -> false
        Assertions.assertFalse(addCommand.equals(1));

        // null -> false
        Assertions.assertFalse(addCommand.equals(null));

        // different class group -> false
        ClassGroup different = new ClassGroupBuilder(classGroup).withName("CS2101T10").build();
        AddClassGroupCommand differentCommand = new AddClassGroupCommand(different, new ArrayList<>());
        Assertions.assertFalse(addCommand.equals(differentCommand));
    }

    @Test
    public void toString_typicalValue_correctOutput() {
        AddClassGroupCommand addCommand = new AddClassGroupCommand(TypicalClassGroups.CLASS_GROUP_ONE,
                new ArrayList<>());
        String expected = AddClassGroupCommand.class.getCanonicalName() + "{toAdd="
                + TypicalClassGroups.CLASS_GROUP_ONE + ", contactIndices=" + new ArrayList<>() + "}";
        Assertions.assertEquals(expected, addCommand.toString());
    }

    /**
     * A default model stub that have all of the methods failing.
     */
    private class ModelStub implements Model {
        @Override
        public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ReadOnlyUserPrefs getUserPrefs() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public GuiSettings getGuiSettings() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setGuiSettings(GuiSettings guiSettings) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Path getAddressBookFilePath() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setAddressBookFilePath(Path addressBookFilePath) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void addContact(Contact contact) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setAddressBook(ReadOnlyAddressBook newData) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean hasContact(Contact contact) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void deleteContact(Contact target) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setContact(Contact target, Contact editedContact) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableList<Contact> getFilteredContactList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void updateFilteredContactList(Predicate<Contact> predicate) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean hasAssignment(Assignment assignment) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void addAssignment(Assignment assignment) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void deleteAssignment(Assignment target) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void addContactAssignment(ContactAssignment ca) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void removeContactAssignment(ContactAssignment ca) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean hasClassGroup(ClassGroup classGroup) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void addClassGroup(ClassGroup classGroup) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setClassGroup(ClassGroup target, ClassGroup editedClassGroup) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void deleteClassGroup(ClassGroup target) {
            throw new AssertionError("This method should not be called.");
        }
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
            return new AddressBook();
        }

        @Override
        public ObservableList<Contact> getFilteredContactList() {
            return new AddressBook().getContactList();
        }
    }
}
