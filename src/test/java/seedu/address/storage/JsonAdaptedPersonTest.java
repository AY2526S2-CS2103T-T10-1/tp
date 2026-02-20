package seedu.address.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.testutil.Assert;
import seedu.address.testutil.TypicalPersons;

public class JsonAdaptedPersonTest {
    private static final String INVALID_NAME = "R@chel";
    private static final String INVALID_PHONE = "+651234";
    private static final String INVALID_ADDRESS = " ";
    private static final String INVALID_EMAIL = "example.com";
    private static final String INVALID_TAG = "#friend";

    private static final String VALID_NAME = TypicalPersons.BENSON.getName().toString();
    private static final String VALID_PHONE = TypicalPersons.BENSON.getPhone().toString();
    private static final String VALID_EMAIL = TypicalPersons.BENSON.getEmail().toString();
    private static final String VALID_ADDRESS = TypicalPersons.BENSON.getAddress().toString();
    private static final List<JsonAdaptedTag> VALID_TAGS = TypicalPersons.BENSON.getTags().stream()
            .map(JsonAdaptedTag::new)
            .collect(Collectors.toList());

    @Test
    public void toModelType_validPersonDetails_returnsPerson() throws Exception {
        JsonAdaptedPerson person = new JsonAdaptedPerson(TypicalPersons.BENSON);
        Assertions.assertEquals(TypicalPersons.BENSON, person.toModelType());
    }

    @Test
    public void toModelType_invalidName_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(JsonAdaptedPersonTest.INVALID_NAME,
                JsonAdaptedPersonTest.VALID_PHONE, JsonAdaptedPersonTest.VALID_EMAIL,
                JsonAdaptedPersonTest.VALID_ADDRESS,
                JsonAdaptedPersonTest.VALID_TAGS);
        String expectedMessage = Name.MESSAGE_CONSTRAINTS;
        Assert.assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullName_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(null, JsonAdaptedPersonTest.VALID_PHONE,
                JsonAdaptedPersonTest.VALID_EMAIL, JsonAdaptedPersonTest.VALID_ADDRESS,
                JsonAdaptedPersonTest.VALID_TAGS);
        String expectedMessage = String.format(JsonAdaptedPerson.MISSING_FIELD_MESSAGE_FORMAT,
                Name.class.getSimpleName());
        Assert.assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidPhone_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(JsonAdaptedPersonTest.VALID_NAME,
                JsonAdaptedPersonTest.INVALID_PHONE, JsonAdaptedPersonTest.VALID_EMAIL,
                JsonAdaptedPersonTest.VALID_ADDRESS,
                JsonAdaptedPersonTest.VALID_TAGS);
        String expectedMessage = Phone.MESSAGE_CONSTRAINTS;
        Assert.assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullPhone_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(JsonAdaptedPersonTest.VALID_NAME, null,
                JsonAdaptedPersonTest.VALID_EMAIL, JsonAdaptedPersonTest.VALID_ADDRESS,
                JsonAdaptedPersonTest.VALID_TAGS);
        String expectedMessage = String.format(JsonAdaptedPerson.MISSING_FIELD_MESSAGE_FORMAT,
                Phone.class.getSimpleName());
        Assert.assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidEmail_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(JsonAdaptedPersonTest.VALID_NAME,
                JsonAdaptedPersonTest.VALID_PHONE, JsonAdaptedPersonTest.INVALID_EMAIL,
                JsonAdaptedPersonTest.VALID_ADDRESS,
                JsonAdaptedPersonTest.VALID_TAGS);
        String expectedMessage = Email.MESSAGE_CONSTRAINTS;
        Assert.assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullEmail_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(JsonAdaptedPersonTest.VALID_NAME,
                JsonAdaptedPersonTest.VALID_PHONE, null, JsonAdaptedPersonTest.VALID_ADDRESS,
                JsonAdaptedPersonTest.VALID_TAGS);
        String expectedMessage = String.format(JsonAdaptedPerson.MISSING_FIELD_MESSAGE_FORMAT,
                Email.class.getSimpleName());
        Assert.assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidAddress_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(JsonAdaptedPersonTest.VALID_NAME,
                JsonAdaptedPersonTest.VALID_PHONE, JsonAdaptedPersonTest.VALID_EMAIL,
                JsonAdaptedPersonTest.INVALID_ADDRESS,
                JsonAdaptedPersonTest.VALID_TAGS);
        String expectedMessage = Address.MESSAGE_CONSTRAINTS;
        Assert.assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullAddress_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(JsonAdaptedPersonTest.VALID_NAME,
                JsonAdaptedPersonTest.VALID_PHONE, JsonAdaptedPersonTest.VALID_EMAIL, null,
                JsonAdaptedPersonTest.VALID_TAGS);
        String expectedMessage = String.format(JsonAdaptedPerson.MISSING_FIELD_MESSAGE_FORMAT,
                Address.class.getSimpleName());
        Assert.assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidTags_throwsIllegalValueException() {
        List<JsonAdaptedTag> invalidTags = new ArrayList<>(JsonAdaptedPersonTest.VALID_TAGS);
        invalidTags.add(new JsonAdaptedTag(JsonAdaptedPersonTest.INVALID_TAG));
        JsonAdaptedPerson person = new JsonAdaptedPerson(JsonAdaptedPersonTest.VALID_NAME,
                JsonAdaptedPersonTest.VALID_PHONE, JsonAdaptedPersonTest.VALID_EMAIL,
                JsonAdaptedPersonTest.VALID_ADDRESS,
                invalidTags);
        Assert.assertThrows(IllegalValueException.class, person::toModelType);
    }

}
