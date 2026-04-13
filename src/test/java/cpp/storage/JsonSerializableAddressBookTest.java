package cpp.storage;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cpp.commons.exceptions.IllegalValueException;
import cpp.commons.util.JsonUtil;
import cpp.model.AddressBook;
import cpp.model.contact.Contact;
import cpp.testutil.Assert;
import cpp.testutil.ContactBuilder;
import cpp.testutil.TypicalAssignments;
import cpp.testutil.TypicalClassGroups;
import cpp.testutil.TypicalContacts;

public class JsonSerializableAddressBookTest {

    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data",
            "JsonSerializableAddressBookTest");
    private static final Path TYPICAL_CONTACTS_FILE = JsonSerializableAddressBookTest.TEST_DATA_FOLDER
            .resolve("typicalContactsAddressBook.json");
    private static final Path INVALID_CONTACT_FILE_NAME = JsonSerializableAddressBookTest.TEST_DATA_FOLDER
            .resolve("invalidContactAddressBookName.json");
    private static final Path INVALID_CONTACT_FILE_PHONE = JsonSerializableAddressBookTest.TEST_DATA_FOLDER
            .resolve("invalidContactAddressBookPhone.json");
    private static final Path INVALID_CONTACT_FILE_EMAIL = JsonSerializableAddressBookTest.TEST_DATA_FOLDER
            .resolve("invalidContactAddressBookEmail.json");
    private static final Path INVALID_CONTACT_FILE_ADDRESS = JsonSerializableAddressBookTest.TEST_DATA_FOLDER
            .resolve("invalidContactAddressBookAddress.json");
    private static final Path DUPLICATE_CONTACT_FILE = JsonSerializableAddressBookTest.TEST_DATA_FOLDER
            .resolve("duplicateContactAddressBook.json");
    private static final Path TYPICAL_ASSIGNMENTS_FILE = JsonSerializableAddressBookTest.TEST_DATA_FOLDER
            .resolve("typicalAssignmentsAddressBook.json");
    private static final Path INVALID_ASSIGNMENT_FILE_ID = JsonSerializableAddressBookTest.TEST_DATA_FOLDER
            .resolve("invalidAssignmentAddressBookId.json");
    private static final Path INVALID_ASSIGNMENT_FILE_DEADLINE = JsonSerializableAddressBookTest.TEST_DATA_FOLDER
            .resolve("invalidAssignmentAddressBookDeadline.json");
    private static final Path INVALID_ASSIGNMENT_FILE_NAME = JsonSerializableAddressBookTest.TEST_DATA_FOLDER
            .resolve("invalidAssignmentAddressBookName.json");
    private static final Path DUPLICATE_ASSIGNMENT_FILE = JsonSerializableAddressBookTest.TEST_DATA_FOLDER
            .resolve("duplicateAssignmentAddressBook.json");
    private static final Path DUPLICATE_CONTACT_ASSIGNMENT_FILE = JsonSerializableAddressBookTest.TEST_DATA_FOLDER
            .resolve("duplicateContactAssignmentAddressBook.json");
    private static final Path TYPICAL_CLASS_GROUPS_FILE = JsonSerializableAddressBookTest.TEST_DATA_FOLDER
            .resolve("typicalClassGroupsAddressBook.json");
    private static final Path INVALID_CLASS_GROUP_NAME_FILE = JsonSerializableAddressBookTest.TEST_DATA_FOLDER
            .resolve("invalidClassGroupNameAddressBook.json");
    private static final Path DUPLICATE_CLASS_GROUP_FILE = JsonSerializableAddressBookTest.TEST_DATA_FOLDER
            .resolve("duplicateClassGroupAddressBook.json");

    @Test
    public void toModelType_typicalContactsFile_success() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil
                .readJsonFile(JsonSerializableAddressBookTest.TYPICAL_CONTACTS_FILE,
                        JsonSerializableAddressBook.class)
                .get();
        AddressBook addressBookFromFile = dataFromFile.toModelType();
        AddressBook typicalContactsAddressBook = TypicalContacts.getTypicalAddressBook();
        Assertions.assertEquals(addressBookFromFile, typicalContactsAddressBook);
    }

    @Test
    public void toModelType_invalidContactFile_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil
                .readJsonFile(JsonSerializableAddressBookTest.INVALID_CONTACT_FILE_NAME,
                        JsonSerializableAddressBook.class)
                .get();
        Assert.assertThrows(IllegalValueException.class, dataFromFile::toModelType);

        dataFromFile = JsonUtil
                .readJsonFile(JsonSerializableAddressBookTest.INVALID_CONTACT_FILE_PHONE,
                        JsonSerializableAddressBook.class)
                .get();
        Assert.assertThrows(IllegalValueException.class, dataFromFile::toModelType);

        dataFromFile = JsonUtil
                .readJsonFile(JsonSerializableAddressBookTest.INVALID_CONTACT_FILE_EMAIL,
                        JsonSerializableAddressBook.class)
                .get();
        Assert.assertThrows(IllegalValueException.class, dataFromFile::toModelType);

        dataFromFile = JsonUtil
                .readJsonFile(JsonSerializableAddressBookTest.INVALID_CONTACT_FILE_ADDRESS,
                        JsonSerializableAddressBook.class)
                .get();
        Assert.assertThrows(IllegalValueException.class, dataFromFile::toModelType);
    }

    @Test
    public void toModelType_typicalAssignmentsFile_success() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil
                .readJsonFile(JsonSerializableAddressBookTest.TYPICAL_ASSIGNMENTS_FILE,
                        JsonSerializableAddressBook.class)
                .get();
        AddressBook addressBookFromFile = dataFromFile.toModelType();
        AddressBook expected = new AddressBook();
        expected.addAssignment(TypicalAssignments.ASSIGNMENT_ONE);
        Assertions.assertEquals(addressBookFromFile, expected);
    }

    @Test
    public void toModelType_invalidAssignmentFile_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil
                .readJsonFile(JsonSerializableAddressBookTest.INVALID_ASSIGNMENT_FILE_ID,
                        JsonSerializableAddressBook.class)
                .get();
        Assert.assertThrows(IllegalValueException.class, dataFromFile::toModelType);

        dataFromFile = JsonUtil
                .readJsonFile(JsonSerializableAddressBookTest.INVALID_ASSIGNMENT_FILE_DEADLINE,
                        JsonSerializableAddressBook.class)
                .get();
        Assert.assertThrows(IllegalValueException.class, dataFromFile::toModelType);

        dataFromFile = JsonUtil
                .readJsonFile(JsonSerializableAddressBookTest.INVALID_ASSIGNMENT_FILE_NAME,
                        JsonSerializableAddressBook.class)
                .get();
        Assert.assertThrows(IllegalValueException.class, dataFromFile::toModelType);
    }

    @Test
    public void toModelType_duplicateAssignments_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil
                .readJsonFile(JsonSerializableAddressBookTest.DUPLICATE_ASSIGNMENT_FILE,
                        JsonSerializableAddressBook.class)
                .get();
        Assert.assertThrows(IllegalValueException.class,
                JsonSerializableAddressBook.MESSAGE_DUPLICATE_ASSIGNMENT,
                dataFromFile::toModelType);
    }

    @Test
    public void toModelType_typicalClassGroupsFile_success() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil
                .readJsonFile(JsonSerializableAddressBookTest.TYPICAL_CLASS_GROUPS_FILE,
                        JsonSerializableAddressBook.class)
                .get();
        AddressBook addressBookFromFile = dataFromFile.toModelType();
        AddressBook expected = new AddressBook();
        expected.addClassGroup(TypicalClassGroups.CLASS_GROUP_ONE);
        expected.addClassGroup(TypicalClassGroups.CLASS_GROUP_TWO);
        Contact expectedContact = new ContactBuilder()
                .withId("aaaaaaaa-1111-2222-3333-cccccccccccc")
                .withEmail(TypicalContacts.ALICE.getEmail().toString())
                .withAddress(TypicalContacts.ALICE.getAddress().toString())
                .withName(TypicalContacts.ALICE.getName().toString())
                .withPhone(TypicalContacts.ALICE.getPhone().toString())
                .withTags("friends")
                .build();
        expected.addContact(expectedContact);
        Assertions.assertEquals(addressBookFromFile, expected);
    }

    @Test
    public void toModelType_invalidClassGroupFile_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil
                .readJsonFile(JsonSerializableAddressBookTest.INVALID_CLASS_GROUP_NAME_FILE,
                        JsonSerializableAddressBook.class)
                .get();
        Assert.assertThrows(IllegalValueException.class, dataFromFile::toModelType);
    }

    @Test
    public void toModelType_duplicateClassGroups_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil
                .readJsonFile(JsonSerializableAddressBookTest.DUPLICATE_CLASS_GROUP_FILE,
                        JsonSerializableAddressBook.class)
                .get();
        Assert.assertThrows(IllegalValueException.class,
                JsonSerializableAddressBook.MESSAGE_DUPLICATE_CLASS_GROUP,
                dataFromFile::toModelType);
    }

    @Test
    public void toModelType_duplicateContacts_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil
                .readJsonFile(JsonSerializableAddressBookTest.DUPLICATE_CONTACT_FILE,
                        JsonSerializableAddressBook.class)
                .get();
        Assert.assertThrows(IllegalValueException.class,
                JsonSerializableAddressBook.MESSAGE_DUPLICATE_CONTACT,
                dataFromFile::toModelType);
    }

    @Test
    public void toModelType_duplicateContactAssignments_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil
                .readJsonFile(JsonSerializableAddressBookTest.DUPLICATE_CONTACT_ASSIGNMENT_FILE,
                        JsonSerializableAddressBook.class)
                .get();
        Assert.assertThrows(IllegalValueException.class,
                JsonSerializableAddressBook.MESSAGE_DUPLICATE_CONTACT_ASSIGNMENT,
                dataFromFile::toModelType);
    }
}
