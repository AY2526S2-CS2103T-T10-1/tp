package cpp.logic.commands;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cpp.commons.core.GuiSettings;
import cpp.logic.Messages;
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
import cpp.testutil.ContactBuilder;
import cpp.testutil.TypicalContacts;
import javafx.collections.ObservableList;

public class AddContactCommandTest {

    @Test
    public void constructor_nullContact_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class, () -> new AddContactCommand(null));
    }

    @Test
    public void execute_contactAcceptedByModel_addSuccessful() throws Exception {
        ModelStubAcceptingContactAdded modelStub = new ModelStubAcceptingContactAdded();
        Contact validContact = new ContactBuilder().build();

        CommandResult commandResult = new AddContactCommand(validContact).execute(modelStub);

        Assertions.assertEquals(String.format(AddContactCommand.MESSAGE_SUCCESS, Messages.format(validContact)),
                commandResult.getFeedbackToUser());
        Assertions.assertEquals(Arrays.asList(validContact), modelStub.contactsAdded);
    }

    @Test
    public void execute_duplicateContact_throwsCommandException() {
        Contact validContact = new ContactBuilder().build();
        AddContactCommand addContactCommand = new AddContactCommand(validContact);
        ModelStub modelStub = new ModelStubWithContact(validContact);

        Assert.assertThrows(CommandException.class, AddContactCommand.MESSAGE_DUPLICATE_CONTACT,
                () -> addContactCommand.execute(modelStub));
    }

    @Test
    public void equals() {
        Contact alice = new ContactBuilder().withName("Alice").build();
        Contact bob = new ContactBuilder().withName("Bob").build();
        AddContactCommand addAliceCommand = new AddContactCommand(alice);
        AddContactCommand addBobCommand = new AddContactCommand(bob);

        // same object -> returns true
        Assertions.assertTrue(addAliceCommand.equals(addAliceCommand));

        // same values -> returns true
        AddContactCommand addAliceCommandCopy = new AddContactCommand(alice);
        Assertions.assertTrue(addAliceCommand.equals(addAliceCommandCopy));

        // different types -> returns false
        Assertions.assertFalse(addAliceCommand.equals(1));

        // null -> returns false
        Assertions.assertFalse(addAliceCommand.equals(null));

        // different contact -> returns false
        Assertions.assertFalse(addAliceCommand.equals(addBobCommand));
    }

    @Test
    public void toStringMethod() {
        AddContactCommand addContactCommand = new AddContactCommand(TypicalContacts.ALICE);
        String expected = AddContactCommand.class.getCanonicalName() + "{toAdd=" + TypicalContacts.ALICE + "}";
        Assertions.assertEquals(expected, addContactCommand.toString());
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

        @Override
        public ObservableList<Assignment> getFilteredAssignmentList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void updateFilteredAssignmentList(Predicate<Assignment> predicate) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableList<ClassGroup> getFilteredClassGroupList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void updateFilteredClassGroupList(Predicate<ClassGroup> predicate) {
            throw new AssertionError("This method should not be called.");
        }
    }

    /**
     * A Model stub that contains a single contact.
     */
    private class ModelStubWithContact extends ModelStub {
        private final Contact contact;

        ModelStubWithContact(Contact contact) {
            Objects.requireNonNull(contact);
            this.contact = contact;
        }

        @Override
        public boolean hasContact(Contact contact) {
            Objects.requireNonNull(contact);
            return this.contact.isSameContact(contact);
        }
    }

    /**
     * A Model stub that always accept the contact being added.
     */
    private class ModelStubAcceptingContactAdded extends ModelStub {
        final ArrayList<Contact> contactsAdded = new ArrayList<>();

        @Override
        public boolean hasContact(Contact contact) {
            Objects.requireNonNull(contact);
            return this.contactsAdded.stream().anyMatch(contact::isSameContact);
        }

        @Override
        public void addContact(Contact contact) {
            Objects.requireNonNull(contact);
            this.contactsAdded.add(contact);
        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            return new AddressBook();
        }
    }

}
