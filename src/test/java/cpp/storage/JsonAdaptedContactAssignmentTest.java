package cpp.storage;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cpp.commons.exceptions.IllegalValueException;
import cpp.logic.parser.ParserUtil;
import cpp.model.AddressBook;
import cpp.model.assignment.Assignment;
import cpp.model.assignment.AssignmentName;
import cpp.model.assignment.ContactAssignment;
import cpp.model.assignment.GradeInfo;
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
    private static final LocalDateTime submissionDate = LocalDateTime.now().minusMonths(1);
    private static final String submissionDateString = JsonAdaptedContactAssignmentTest.submissionDate
            .format(ParserUtil.DATETIME_FORMATTER);
    private static final LocalDateTime gradingDate = JsonAdaptedContactAssignmentTest.submissionDate.plusDays(1);
    private static final String gradingDateString = JsonAdaptedContactAssignmentTest.gradingDate
            .format(ParserUtil.DATETIME_FORMATTER);

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
                "true", JsonAdaptedContactAssignmentTest.submissionDateString, "true",
                JsonAdaptedContactAssignmentTest.gradingDateString, "100");
        ContactAssignment expected = new ContactAssignment(JsonAdaptedContactAssignmentTest.VALID_ASSIGNMENT_ID,
                JsonAdaptedContactAssignmentTest.VALID_CONTACT_ID, true,
                JsonAdaptedContactAssignmentTest.submissionDate, true, JsonAdaptedContactAssignmentTest.gradingDate,
                100);
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
                JsonAdaptedContactAssignmentTest.VALID_CONTACT_ID, true,
                JsonAdaptedContactAssignmentTest.submissionDate, true, JsonAdaptedContactAssignmentTest.gradingDate,
                100);
        JsonAdaptedContactAssignment json = new JsonAdaptedContactAssignment(contactAssignment);
        Assertions.assertEquals(contactAssignment, json.toModelType(addressBook));
    }

    @Test
    public void toModelType_nullAssignmentId_throwsIllegalValueException() {
        JsonAdaptedContactAssignment json = new JsonAdaptedContactAssignment(
                JsonAdaptedContactAssignmentTest.INVALID_ASSIGNMENT_ID,
                JsonAdaptedContactAssignmentTest.VALID_CONTACT_ID, "true",
                JsonAdaptedContactAssignmentTest.submissionDateString, "true",
                JsonAdaptedContactAssignmentTest.gradingDateString,
                "100");
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
                JsonAdaptedContactAssignmentTest.INVALID_CONTACT_ID, "true",
                JsonAdaptedContactAssignmentTest.submissionDateString, "true",
                JsonAdaptedContactAssignmentTest.gradingDateString, "100");
        String expectedMessage = String.format(JsonAdaptedContactAssignment.MISSING_FIELD_MESSAGE_FORMAT,
                "contactId");
        Assert.assertThrows(IllegalValueException.class, expectedMessage, () -> json.toModelType(addressBook));
    }

    @Test
    public void toModelType_invalidAssignmentId_throwsIllegalValueException() {
        JsonAdaptedContactAssignment json = new JsonAdaptedContactAssignment(
                JsonAdaptedContactAssignmentTest.VALID_ASSIGNMENT_ID,
                JsonAdaptedContactAssignmentTest.VALID_CONTACT_ID, "true",
                JsonAdaptedContactAssignmentTest.submissionDateString, "true",
                JsonAdaptedContactAssignmentTest.gradingDateString,
                "100");
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
                JsonAdaptedContactAssignmentTest.VALID_CONTACT_ID, "true",
                JsonAdaptedContactAssignmentTest.submissionDateString, "true",
                JsonAdaptedContactAssignmentTest.gradingDateString, "100");
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
                "true", JsonAdaptedContactAssignmentTest.submissionDateString, "true",
                JsonAdaptedContactAssignmentTest.gradingDateString, "-10");
        String expectedMessage = String.format(GradeInfo.INVALID_SCORE_STRING);
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
                "true", JsonAdaptedContactAssignmentTest.submissionDateString, "true",
                JsonAdaptedContactAssignmentTest.gradingDateString, null);
        String expectedMessage = String.format(GradeInfo.INVALID_SCORE_STRING);
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
                null, null, "true", null, "100");
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
                "true", JsonAdaptedContactAssignmentTest.submissionDateString, null, null, "100");
        String expectedMessage = String.format(JsonAdaptedContactAssignment.MISSING_FIELD_MESSAGE_FORMAT, "isGraded");
        Assert.assertThrows(IllegalValueException.class, expectedMessage, () -> json.toModelType(addressBook));
    }

    @Test
    public void toModelType_missingSubmissionDate_throwsIllegalValueException() {
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
                "true", null, "false", null, "0");
        String expectedMessage = String.format(JsonAdaptedContactAssignment.MISSING_FIELD_MESSAGE_FORMAT,
                "submissionDate or isSubmitted");
        Assert.assertThrows(IllegalValueException.class, expectedMessage, () -> json.toModelType(addressBook));

        expectedMessage = String.format(JsonAdaptedContactAssignment.INVALID_DATE_MESSAGE,
                "123");
        JsonAdaptedContactAssignment json2 = new JsonAdaptedContactAssignment(
                JsonAdaptedContactAssignmentTest.VALID_ASSIGNMENT_ID, JsonAdaptedContactAssignmentTest.VALID_CONTACT_ID,
                "true", "123", "false", null, "0");
        Assert.assertThrows(IllegalValueException.class, expectedMessage, () -> json2.toModelType(addressBook));
    }

    @Test
    public void toModelType_invalidGradingDate() {
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
                "true", JsonAdaptedContactAssignmentTest.submissionDateString, "true", null, "50");
        String expectedMessage = GradeInfo.INVALID_GRADE_STRING;
        Assert.assertThrows(IllegalValueException.class, expectedMessage, () -> json.toModelType(addressBook));

        expectedMessage = String.format(JsonAdaptedContactAssignment.INVALID_DATE_MESSAGE, "123");
        JsonAdaptedContactAssignment json2 = new JsonAdaptedContactAssignment(
                JsonAdaptedContactAssignmentTest.VALID_ASSIGNMENT_ID, JsonAdaptedContactAssignmentTest.VALID_CONTACT_ID,
                "true", JsonAdaptedContactAssignmentTest.submissionDateString, "true", "123", "50");
        Assert.assertThrows(IllegalValueException.class, expectedMessage, () -> json2.toModelType(addressBook));

        AddressBook addressBook2 = new AddressBook();
        Assignment assignment2 = new Assignment(JsonAdaptedContactAssignmentTest.VALID_ASSIGNMENT_ID,
                new AssignmentName("Assignment 1"), LocalDateTime.now());
        addressBook2.addAssignment(assignment2);
        Set<Tag> tags2 = new HashSet<>();
        Contact contact2 = new Contact(JsonAdaptedContactAssignmentTest.VALID_CONTACT_ID,
                new ContactName("Alice"), new Phone("12345678"), new Email("alice@example.com"),
                new Address("Some address"), tags2);
        addressBook2.addContact(contact2);
        LocalDateTime invalidGradingDate = JsonAdaptedContactAssignmentTest.submissionDate.minusDays(1);
        String invalidGradingDateString = invalidGradingDate.format(ParserUtil.DATETIME_FORMATTER);
        JsonAdaptedContactAssignment json3 = new JsonAdaptedContactAssignment(
                JsonAdaptedContactAssignmentTest.VALID_ASSIGNMENT_ID, JsonAdaptedContactAssignmentTest.VALID_CONTACT_ID,
                "true", JsonAdaptedContactAssignmentTest.submissionDateString, "true",
                invalidGradingDateString, "50");
        String expectedMessage2 = String.format(GradeInfo.INVALID_GRADE_STRING);
        Assert.assertThrows(IllegalValueException.class, expectedMessage2, () -> json3.toModelType(addressBook2));
    }

    @Test
    public void toModelType_gradedButNotSubmitted_throwsIllegalValueException() {
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
                "false", null, "true", JsonAdaptedContactAssignmentTest.gradingDateString, "50");
        String expectedMessage = GradeInfo.INVALID_GRADE_STRING;
        Assert.assertThrows(IllegalValueException.class, expectedMessage, () -> json.toModelType(addressBook));
    }

    @Test
    public void toModelType_scoreTooLarge_throwsIllegalValueException() {
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
                "true", JsonAdaptedContactAssignmentTest.submissionDateString, "true",
                JsonAdaptedContactAssignmentTest.gradingDateString, "101");
        String expectedMessage = String.format(GradeInfo.INVALID_SCORE_STRING);
        Assert.assertThrows(IllegalValueException.class, expectedMessage, () -> json.toModelType(addressBook));
    }

    @Test
    public void toModelType_scoreNotInteger_throwsIllegalValueException() {
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
                "true", JsonAdaptedContactAssignmentTest.submissionDateString, "true",
                JsonAdaptedContactAssignmentTest.gradingDateString, "not_a_number");
        String expectedMessage = String.format(GradeInfo.INVALID_SCORE_STRING);
        Assert.assertThrows(IllegalValueException.class, expectedMessage, () -> json.toModelType(addressBook));
    }

}
