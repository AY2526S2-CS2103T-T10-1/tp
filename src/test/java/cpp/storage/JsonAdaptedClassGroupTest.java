package cpp.storage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cpp.commons.exceptions.IllegalValueException;
import cpp.model.AddressBook;
import cpp.model.classgroup.ClassGroupName;
import cpp.model.contact.Contact;
import cpp.testutil.Assert;
import cpp.testutil.ContactBuilder;
import cpp.testutil.TypicalClassGroups;

public class JsonAdaptedClassGroupTest {

    private static final String INVALID_NAME = "Cl@ss 1";

    private static final String VALID_ID = TypicalClassGroups.CLASS_GROUP_ONE.getId();
    private static final String VALID_NAME = TypicalClassGroups.CLASS_GROUP_ONE.getName().fullName;
    private static final String[] VALID_CONTACT_IDS = TypicalClassGroups.CLASS_GROUP_ONE.getContactIdSet()
            .toArray(new String[0]);

    @Test
    public void toModelType_validClassGroupDetails_returnsClassGroup() throws Exception {
        AddressBook addressBook = new AddressBook();
        Contact sampleContact = new ContactBuilder().withId("aaaaaaaa-1111-2222-3333-cccccccccccc").build();
        addressBook.addContact(sampleContact);
        JsonAdaptedClassGroup json = new JsonAdaptedClassGroup(TypicalClassGroups.CLASS_GROUP_ONE);
        Assertions.assertEquals(TypicalClassGroups.CLASS_GROUP_ONE, json.toModelType(addressBook));
    }

    @Test
    public void toModelType_nullId_throwsIllegalValueException() {
        AddressBook addressBook = new AddressBook();
        JsonAdaptedClassGroup json = new JsonAdaptedClassGroup(null, JsonAdaptedClassGroupTest.VALID_NAME,
                JsonAdaptedClassGroupTest.VALID_CONTACT_IDS);
        String expectedMessage = "A class group's id field is missing.";
        Assert.assertThrows(IllegalValueException.class, expectedMessage, () -> json.toModelType(addressBook));
    }

    @Test
    public void toModelType_invalidName_throwsIllegalValueException() {
        AddressBook addressBook = new AddressBook();
        JsonAdaptedClassGroup json = new JsonAdaptedClassGroup(JsonAdaptedClassGroupTest.VALID_ID,
                JsonAdaptedClassGroupTest.INVALID_NAME, JsonAdaptedClassGroupTest.VALID_CONTACT_IDS);
        String expectedMessage = ClassGroupName.MESSAGE_CONSTRAINTS;
        Assert.assertThrows(IllegalValueException.class, expectedMessage, () -> json.toModelType(addressBook));
    }

    @Test
    public void toModelType_nullName_throwsIllegalValueException() {
        AddressBook addressBook = new AddressBook();
        JsonAdaptedClassGroup json = new JsonAdaptedClassGroup(JsonAdaptedClassGroupTest.VALID_ID, null,
                JsonAdaptedClassGroupTest.VALID_CONTACT_IDS);
        String expectedMessage = String.format(JsonAdaptedClassGroup.MISSING_FIELD_MESSAGE_FORMAT,
                ClassGroupName.class.getSimpleName());
        Assert.assertThrows(IllegalValueException.class, expectedMessage, () -> json.toModelType(addressBook));
    }

    @Test
    public void toModelType_nullContactIds_throwsIllegalValueException() {
        AddressBook addressBook = new AddressBook();
        JsonAdaptedClassGroup json = new JsonAdaptedClassGroup(JsonAdaptedClassGroupTest.VALID_ID,
                JsonAdaptedClassGroupTest.VALID_NAME, null);
        String expectedMessage = "A class group's contactIds field is missing.";
        Assert.assertThrows(IllegalValueException.class, expectedMessage, () -> json.toModelType(addressBook));
    }

    @Test
    public void toModelType_duplicateContactIds_throwsIllegalValueException() {
        AddressBook addressBook = new AddressBook();
        Contact sampleContact = new ContactBuilder().withId("Contact1").build();
        addressBook.addContact(sampleContact);
        String[] duplicateContactIds = new String[] { "Contact1", "Contact1" };
        JsonAdaptedClassGroup json = new JsonAdaptedClassGroup(JsonAdaptedClassGroupTest.VALID_ID,
                JsonAdaptedClassGroupTest.VALID_NAME, duplicateContactIds);
        String expectedMessage = "Duplicate contactId found during allocation.";
        Assert.assertThrows(IllegalValueException.class, expectedMessage, () -> json.toModelType(addressBook));
    }
}
