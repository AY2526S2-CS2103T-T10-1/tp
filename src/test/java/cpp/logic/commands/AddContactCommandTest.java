package cpp.logic.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cpp.logic.Messages;
import cpp.logic.commands.exceptions.CommandException;
import cpp.model.AddressBook;
import cpp.model.ReadOnlyAddressBook;
import cpp.model.assignment.AssignmentName;
import cpp.model.classgroup.ClassGroupName;
import cpp.model.contact.Contact;
import cpp.testutil.Assert;
import cpp.testutil.ContactBuilder;
import cpp.testutil.ModelStub;
import cpp.testutil.TypicalContacts;

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
    public void execute_invalidClassName_throwsCommandException() {
        Contact validContact = new ContactBuilder().build();
        AddContactCommand addContactCommand = new AddContactCommand(validContact, new ClassGroupName("CS2103T10"),
                null);
        ModelStub modelStub = new ModelStubAcceptingContactAdded();

        Assert.assertThrows(CommandException.class, Messages.MESSAGE_CLASS_GROUP_NOT_FOUND,
                () -> addContactCommand.execute(modelStub));
    }

    @Test
    public void execute_invalidAssignmentName_throwsCommandException() {
        Contact validContact = new ContactBuilder().build();
        AddContactCommand addContactCommand = new AddContactCommand(validContact, null,
                new AssignmentName("Assignment 9"));
        ModelStub modelStub = new ModelStubAcceptingContactAdded();

        Assert.assertThrows(CommandException.class, Messages.MESSAGE_ASSIGNMENT_NOT_FOUND,
                () -> addContactCommand.execute(modelStub));
    }

    @Test
    public void execute_bothInvalidOptionalNames_classGroupValidationHappensFirst() {
        Contact validContact = new ContactBuilder().build();
        AddContactCommand addContactCommand = new AddContactCommand(validContact,
                new ClassGroupName("CS2103T10"), new AssignmentName("Assignment 9"));
        ModelStub modelStub = new ModelStubAcceptingContactAdded();

        Assert.assertThrows(CommandException.class, Messages.MESSAGE_CLASS_GROUP_NOT_FOUND,
                () -> addContactCommand.execute(modelStub));
    }

    @Test
    public void equals() {
        Contact alice = new ContactBuilder().withName("Alice").build();
        Contact bob = new ContactBuilder().withName("Bob").build();
        ClassGroupName classGroupName = new ClassGroupName("CS2103T10");
        ClassGroupName anotherClassGroupName = new ClassGroupName("CS2101T10");
        AssignmentName assignmentName = new AssignmentName("Assignment 1");
        AssignmentName anotherAssignmentName = new AssignmentName("Assignment 2");

        AddContactCommand addAliceCommand = new AddContactCommand(alice);
        AddContactCommand addBobCommand = new AddContactCommand(bob);
        AddContactCommand addAliceWithOptionalFieldsCommand = new AddContactCommand(alice, classGroupName,
                assignmentName);
        AddContactCommand addAliceWithDifferentClassCommand = new AddContactCommand(alice, anotherClassGroupName,
                assignmentName);
        AddContactCommand addAliceWithDifferentAssignmentCommand = new AddContactCommand(alice, classGroupName,
                anotherAssignmentName);
        AddContactCommand addAliceWithoutOptionalFieldsCommand = new AddContactCommand(alice);

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

        // same contact and same optional fields -> returns true
        Assertions.assertTrue(addAliceWithOptionalFieldsCommand
                .equals(new AddContactCommand(alice, classGroupName, assignmentName)));

        // different class group name -> returns false
        Assertions.assertFalse(addAliceWithOptionalFieldsCommand.equals(addAliceWithDifferentClassCommand));

        // different assignment name -> returns false
        Assertions.assertFalse(addAliceWithOptionalFieldsCommand.equals(addAliceWithDifferentAssignmentCommand));

        // different class group name and assignment name -> returns false
        Assertions.assertFalse(addAliceWithDifferentClassCommand.equals(addAliceWithDifferentAssignmentCommand));

        // one command has optional fields while the other does not -> returns false
        Assertions.assertFalse(addAliceWithOptionalFieldsCommand.equals(addAliceWithoutOptionalFieldsCommand));
    }

    @Test
    public void toStringMethod() {
        AddContactCommand addContactCommand = new AddContactCommand(TypicalContacts.ALICE);
        String expected = AddContactCommand.class.getCanonicalName() + "{toAdd=" + TypicalContacts.ALICE + "}";
        Assertions.assertEquals(expected, addContactCommand.toString());
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
