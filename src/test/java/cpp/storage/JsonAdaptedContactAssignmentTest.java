package cpp.storage;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cpp.commons.exceptions.IllegalValueException;
import cpp.model.AddressBook;
import cpp.model.assignment.Assignment;
import cpp.model.assignment.AssignmentName;
import cpp.model.assignment.ContactAssignment;
import cpp.model.contact.Address;
import cpp.model.contact.Contact;
import cpp.model.contact.ContactName;
import cpp.model.contact.Email;
import cpp.model.contact.Phone;
import cpp.model.tag.Tag;
import cpp.testutil.Assert;

public class JsonAdaptedContactAssignmentTest {
    private static final String INVALID_CONTACT_ID = null;
    private static final String INVALID_ASSIGNMENT_ID = null;
    private static final String VALID_CONTACT_ID = "valid contact id";
    private static final String VALID_ASSIGNMENT_ID = "valid assignment id";

    @Test
    public void toModelType_validContactAssignmentDetails_returnsContactAssignment() throws Exception {
        AddressBook addressBook = new AddressBook();
        Assignment assignment = new Assignment(JsonAdaptedContactAssignmentTest.VALID_ASSIGNMENT_ID,
                new AssignmentName("Assignment 1"), LocalDateTime.now());
        addressBook.addAssignment(assignment);
        Set<Tag> tags = new HashSet<>();
        Contact contact = new Contact(JsonAdaptedContactAssignmentTest.VALID_CONTACT_ID,
                new ContactName("Alice"), new Phone("12345678"), new Email("alice@example.com"),
                new Address("Some address"), tags);
        addressBook.addContact(contact);
        JsonAdaptedContactAssignment json = new JsonAdaptedContactAssignment(
                JsonAdaptedContactAssignmentTest.VALID_ASSIGNMENT_ID, JsonAdaptedContactAssignmentTest.VALID_CONTACT_ID,
                true, true, 100);
        ContactAssignment expected = new ContactAssignment(JsonAdaptedContactAssignmentTest.VALID_ASSIGNMENT_ID,
                JsonAdaptedContactAssignmentTest.VALID_CONTACT_ID, true, true, 100);
        Assertions.assertEquals(expected, json.toModelType(addressBook));
    }

    @Test
    public void toModelType_validContactAssignmentDetailsFromCa_returnsCa() throws Exception {
        AddressBook addressBook = new AddressBook();
        Assignment assignment = new Assignment(JsonAdaptedContactAssignmentTest.VALID_ASSIGNMENT_ID,
                new AssignmentName("Assignment 1"), LocalDateTime.now());
        addressBook.addAssignment(assignment);
        Set<Tag> tags = new HashSet<>();
        Contact contact = new Contact(JsonAdaptedContactAssignmentTest.VALID_CONTACT_ID,
                new ContactName("Alice"), new Phone("12345678"), new Email("alice@example.com"),
                new Address("Some address"), tags);
        addressBook.addContact(contact);
        ContactAssignment contactAssignment = new ContactAssignment(
                JsonAdaptedContactAssignmentTest.VALID_ASSIGNMENT_ID,
                JsonAdaptedContactAssignmentTest.VALID_CONTACT_ID, true, true, 100);
        JsonAdaptedContactAssignment json = new JsonAdaptedContactAssignment(contactAssignment);
        Assertions.assertEquals(contactAssignment, json.toModelType(addressBook));
    }

    @Test
    public void toModelType_nullAssignmentId_throwsIllegalValueException() {
        JsonAdaptedContactAssignment json = new JsonAdaptedContactAssignment(
                JsonAdaptedContactAssignmentTest.INVALID_ASSIGNMENT_ID,
                JsonAdaptedContactAssignmentTest.VALID_CONTACT_ID, true, true, 100);
        String expectedMessage = String.format(JsonAdaptedContactAssignment.MISSING_FIELD_MESSAGE_FORMAT,
                "assignmentId");
        Assert.assertThrows(IllegalValueException.class, expectedMessage,
                () -> json.toModelType(new AddressBook()));
    }

    @Test
    public void toModelType_nullContactId_throwsIllegalValueException() {
        AddressBook addressBook = new AddressBook();
        Assignment assignment = new Assignment(JsonAdaptedContactAssignmentTest.VALID_ASSIGNMENT_ID,
                new AssignmentName("Assignment 1"), LocalDateTime.now());
        addressBook.addAssignment(assignment);
        JsonAdaptedContactAssignment json = new JsonAdaptedContactAssignment(
                JsonAdaptedContactAssignmentTest.VALID_ASSIGNMENT_ID,
                JsonAdaptedContactAssignmentTest.INVALID_CONTACT_ID, true, true, 100);
        String expectedMessage = String.format(JsonAdaptedContactAssignment.MISSING_FIELD_MESSAGE_FORMAT,
                "contactId");
        Assert.assertThrows(IllegalValueException.class, expectedMessage, () -> json.toModelType(addressBook));
    }

    @Test
    public void toModelType_invalidAssignmentId_throwsIllegalValueException() {
        JsonAdaptedContactAssignment json = new JsonAdaptedContactAssignment(
                JsonAdaptedContactAssignmentTest.VALID_ASSIGNMENT_ID,
                JsonAdaptedContactAssignmentTest.VALID_CONTACT_ID, true, true, 100);
        String expectedMessage = String.format(JsonAdaptedContactAssignment.INVALID_ASSIGNMENT_ID_MESSAGE,
                JsonAdaptedContactAssignmentTest.VALID_ASSIGNMENT_ID);
        Assert.assertThrows(IllegalValueException.class, expectedMessage, () -> json.toModelType(new AddressBook()));
    }

    @Test
    public void toModelType_invalidContactId_throwsIllegalValueException() {
        AddressBook addressBook = new AddressBook();
        Assignment assignment = new Assignment(JsonAdaptedContactAssignmentTest.VALID_ASSIGNMENT_ID,
                new AssignmentName("Assignment 1"), LocalDateTime.now());
        addressBook.addAssignment(assignment);
        JsonAdaptedContactAssignment json = new JsonAdaptedContactAssignment(
                JsonAdaptedContactAssignmentTest.VALID_ASSIGNMENT_ID,
                JsonAdaptedContactAssignmentTest.VALID_CONTACT_ID, true, true, 100);
        String expectedMessage = String.format(JsonAdaptedContactAssignment.INVALID_CONTACT_ID_MESSAGE,
                JsonAdaptedContactAssignmentTest.VALID_CONTACT_ID);
        Assert.assertThrows(IllegalValueException.class, expectedMessage, () -> json.toModelType(addressBook));
    }

    @Test
    public void toModelType_negativeScore_throwsIllegalValueException() {
        AddressBook addressBook = new AddressBook();
        Assignment assignment = new Assignment(JsonAdaptedContactAssignmentTest.VALID_ASSIGNMENT_ID,
                new AssignmentName("Assignment 1"), LocalDateTime.now());
        addressBook.addAssignment(assignment);
        Set<Tag> tags = new HashSet<>();
        Contact contact = new Contact(JsonAdaptedContactAssignmentTest.VALID_CONTACT_ID,
                new ContactName("Alice"), new Phone("12345678"), new Email("alice@example.com"),
                new Address("Some address"), tags);
        addressBook.addContact(contact);
        JsonAdaptedContactAssignment json = new JsonAdaptedContactAssignment(
                JsonAdaptedContactAssignmentTest.VALID_ASSIGNMENT_ID, JsonAdaptedContactAssignmentTest.VALID_CONTACT_ID,
                true, true, -10);
        String expectedMessage = String.format(JsonAdaptedContactAssignment.INVALID_SCORE_MESSAGE);
        Assert.assertThrows(IllegalValueException.class, expectedMessage, () -> json.toModelType(addressBook));
    }

    @Test
    public void toModelType_nullScore_throwsIllegalValueException() {
        AddressBook addressBook = new AddressBook();
        Assignment assignment = new Assignment(JsonAdaptedContactAssignmentTest.VALID_ASSIGNMENT_ID,
                new AssignmentName("Assignment 1"), LocalDateTime.now());
        addressBook.addAssignment(assignment);
        Set<Tag> tags = new HashSet<>();
        Contact contact = new Contact(JsonAdaptedContactAssignmentTest.VALID_CONTACT_ID,
                new ContactName("Alice"), new Phone("12345678"), new Email("alice@example.com"),
                new Address("Some address"), tags);
        addressBook.addContact(contact);
        JsonAdaptedContactAssignment json = new JsonAdaptedContactAssignment(
                JsonAdaptedContactAssignmentTest.VALID_ASSIGNMENT_ID, JsonAdaptedContactAssignmentTest.VALID_CONTACT_ID,
                true, true, null);
        String expectedMessage = String.format(JsonAdaptedContactAssignment.MISSING_FIELD_MESSAGE_FORMAT, "score");
        Assert.assertThrows(IllegalValueException.class, expectedMessage, () -> json.toModelType(addressBook));
    }

    @Test
    public void toModelType_nullIsSubmitted_throwsIllegalValueException() {
        AddressBook addressBook = new AddressBook();
        Assignment assignment = new Assignment(JsonAdaptedContactAssignmentTest.VALID_ASSIGNMENT_ID,
                new AssignmentName("Assignment 1"), LocalDateTime.now());
        addressBook.addAssignment(assignment);
        Set<Tag> tags = new HashSet<>();
        Contact contact = new Contact(JsonAdaptedContactAssignmentTest.VALID_CONTACT_ID,
                new ContactName("Alice"), new Phone("12345678"), new Email("alice@example.com"),
                new Address("Some address"), tags);
        addressBook.addContact(contact);
        JsonAdaptedContactAssignment json = new JsonAdaptedContactAssignment(
                JsonAdaptedContactAssignmentTest.VALID_ASSIGNMENT_ID, JsonAdaptedContactAssignmentTest.VALID_CONTACT_ID,
                null, true, 100);
        String expectedMessage = String.format(JsonAdaptedContactAssignment.MISSING_FIELD_MESSAGE_FORMAT,
                "isSubmitted");
        Assert.assertThrows(IllegalValueException.class, expectedMessage, () -> json.toModelType(addressBook));
    }

    @Test
    public void toModelType_nullIsGraded_throwsIllegalValueException() {
        AddressBook addressBook = new AddressBook();
        Assignment assignment = new Assignment(JsonAdaptedContactAssignmentTest.VALID_ASSIGNMENT_ID,
                new AssignmentName("Assignment 1"), LocalDateTime.now());
        addressBook.addAssignment(assignment);
        Set<Tag> tags = new HashSet<>();
        Contact contact = new Contact(JsonAdaptedContactAssignmentTest.VALID_CONTACT_ID,
                new ContactName("Alice"), new Phone("12345678"), new Email("alice@example.com"),
                new Address("Some address"), tags);
        addressBook.addContact(contact);
        JsonAdaptedContactAssignment json = new JsonAdaptedContactAssignment(
                JsonAdaptedContactAssignmentTest.VALID_ASSIGNMENT_ID, JsonAdaptedContactAssignmentTest.VALID_CONTACT_ID,
                true, null, 100);
        String expectedMessage = String.format(JsonAdaptedContactAssignment.MISSING_FIELD_MESSAGE_FORMAT, "isGraded");
        Assert.assertThrows(IllegalValueException.class, expectedMessage, () -> json.toModelType(addressBook));
    }

}
