package cpp.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cpp.commons.exceptions.IllegalValueException;
import cpp.model.contact.Address;
import cpp.model.contact.ContactName;
import cpp.model.contact.Email;
import cpp.model.contact.Phone;
import cpp.testutil.Assert;
import cpp.testutil.TypicalContacts;

public class JsonAdaptedContactTest {
    private static final String INVALID_ID = null;
    private static final String INVALID_NAME = "R@chel";
    private static final String INVALID_PHONE = "+651234";
    private static final String INVALID_ADDRESS = " ";
    private static final String INVALID_EMAIL = "example.com";
    private static final String INVALID_TAG = "#friend";

    private static final String VALID_ID = "683f92b5-9e96-47bb-94cd-e8ede5523d7a";
    private static final String VALID_NAME = TypicalContacts.BENSON.getName().toString();
    private static final String VALID_PHONE = TypicalContacts.BENSON.getPhone().toString();
    private static final String VALID_EMAIL = TypicalContacts.BENSON.getEmail().toString();
    private static final String VALID_ADDRESS = TypicalContacts.BENSON.getAddress().toString();
    private static final List<JsonAdaptedTag> VALID_TAGS = TypicalContacts.BENSON.getTags().stream()
            .map(JsonAdaptedTag::new)
            .collect(Collectors.toList());

    @Test
    public void toModelType_validContactDetails_returnsContact() throws Exception {
        JsonAdaptedContact contact = new JsonAdaptedContact(TypicalContacts.BENSON);
        Assertions.assertEquals(TypicalContacts.BENSON, contact.toModelType());
    }

    @Test
    public void toModelType_invalidName_throwsIllegalValueException() {
        JsonAdaptedContact contact = new JsonAdaptedContact(JsonAdaptedContactTest.VALID_ID,
                JsonAdaptedContactTest.INVALID_NAME,
                JsonAdaptedContactTest.VALID_PHONE, JsonAdaptedContactTest.VALID_EMAIL,
                JsonAdaptedContactTest.VALID_ADDRESS,
                JsonAdaptedContactTest.VALID_TAGS);
        String expectedMessage = ContactName.MESSAGE_CONSTRAINTS;
        Assert.assertThrows(IllegalValueException.class, expectedMessage, contact::toModelType);
    }

    @Test
    public void toModelType_nullName_throwsIllegalValueException() {
        JsonAdaptedContact contact = new JsonAdaptedContact(JsonAdaptedContactTest.VALID_ID, null,
                JsonAdaptedContactTest.VALID_PHONE,
                JsonAdaptedContactTest.VALID_EMAIL, JsonAdaptedContactTest.VALID_ADDRESS,
                JsonAdaptedContactTest.VALID_TAGS);
        String expectedMessage = String.format(JsonAdaptedContact.MISSING_FIELD_MESSAGE_FORMAT,
                ContactName.class.getSimpleName());
        Assert.assertThrows(IllegalValueException.class, expectedMessage, contact::toModelType);
    }

    @Test
    public void toModelType_invalidPhone_throwsIllegalValueException() {
        JsonAdaptedContact contact = new JsonAdaptedContact(JsonAdaptedContactTest.VALID_ID,
                JsonAdaptedContactTest.VALID_NAME,
                JsonAdaptedContactTest.INVALID_PHONE, JsonAdaptedContactTest.VALID_EMAIL,
                JsonAdaptedContactTest.VALID_ADDRESS,
                JsonAdaptedContactTest.VALID_TAGS);
        String expectedMessage = Phone.MESSAGE_CONSTRAINTS;
        Assert.assertThrows(IllegalValueException.class, expectedMessage, contact::toModelType);
    }

    @Test
    public void toModelType_nullPhone_throwsIllegalValueException() {
        JsonAdaptedContact contact = new JsonAdaptedContact(JsonAdaptedContactTest.VALID_ID,
                JsonAdaptedContactTest.VALID_NAME, null,
                JsonAdaptedContactTest.VALID_EMAIL, JsonAdaptedContactTest.VALID_ADDRESS,
                JsonAdaptedContactTest.VALID_TAGS);
        String expectedMessage = String.format(JsonAdaptedContact.MISSING_FIELD_MESSAGE_FORMAT,
                Phone.class.getSimpleName());
        Assert.assertThrows(IllegalValueException.class, expectedMessage, contact::toModelType);
    }

    @Test
    public void toModelType_invalidEmail_throwsIllegalValueException() {
        JsonAdaptedContact contact = new JsonAdaptedContact(JsonAdaptedContactTest.VALID_ID,
                JsonAdaptedContactTest.VALID_NAME,
                JsonAdaptedContactTest.VALID_PHONE, JsonAdaptedContactTest.INVALID_EMAIL,
                JsonAdaptedContactTest.VALID_ADDRESS,
                JsonAdaptedContactTest.VALID_TAGS);
        String expectedMessage = Email.MESSAGE_CONSTRAINTS;
        Assert.assertThrows(IllegalValueException.class, expectedMessage, contact::toModelType);
    }

    @Test
    public void toModelType_nullEmail_throwsIllegalValueException() {
        JsonAdaptedContact contact = new JsonAdaptedContact(JsonAdaptedContactTest.VALID_ID,
                JsonAdaptedContactTest.VALID_NAME,
                JsonAdaptedContactTest.VALID_PHONE, null, JsonAdaptedContactTest.VALID_ADDRESS,
                JsonAdaptedContactTest.VALID_TAGS);
        String expectedMessage = String.format(JsonAdaptedContact.MISSING_FIELD_MESSAGE_FORMAT,
                Email.class.getSimpleName());
        Assert.assertThrows(IllegalValueException.class, expectedMessage, contact::toModelType);
    }

    @Test
    public void toModelType_invalidAddress_throwsIllegalValueException() {
        JsonAdaptedContact contact = new JsonAdaptedContact(JsonAdaptedContactTest.VALID_ID,
                JsonAdaptedContactTest.VALID_NAME,
                JsonAdaptedContactTest.VALID_PHONE, JsonAdaptedContactTest.VALID_EMAIL,
                JsonAdaptedContactTest.INVALID_ADDRESS,
                JsonAdaptedContactTest.VALID_TAGS);
        String expectedMessage = Address.MESSAGE_CONSTRAINTS;
        Assert.assertThrows(IllegalValueException.class, expectedMessage, contact::toModelType);
    }

    @Test
    public void toModelType_nullAddress_throwsIllegalValueException() {
        JsonAdaptedContact contact = new JsonAdaptedContact(JsonAdaptedContactTest.VALID_ID,
                JsonAdaptedContactTest.VALID_NAME,
                JsonAdaptedContactTest.VALID_PHONE, JsonAdaptedContactTest.VALID_EMAIL, null,
                JsonAdaptedContactTest.VALID_TAGS);
        String expectedMessage = String.format(JsonAdaptedContact.MISSING_FIELD_MESSAGE_FORMAT,
                Address.class.getSimpleName());
        Assert.assertThrows(IllegalValueException.class, expectedMessage, contact::toModelType);
    }

    @Test
    public void toModelType_invalidTags_throwsIllegalValueException() {
        List<JsonAdaptedTag> invalidTags = new ArrayList<>(JsonAdaptedContactTest.VALID_TAGS);
        invalidTags.add(new JsonAdaptedTag(JsonAdaptedContactTest.INVALID_TAG));
        JsonAdaptedContact contact = new JsonAdaptedContact(JsonAdaptedContactTest.VALID_ID,
                JsonAdaptedContactTest.VALID_NAME,
                JsonAdaptedContactTest.VALID_PHONE, JsonAdaptedContactTest.VALID_EMAIL,
                JsonAdaptedContactTest.VALID_ADDRESS,
                invalidTags);
        Assert.assertThrows(IllegalValueException.class, contact::toModelType);
    }

}
