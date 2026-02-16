package seedu.address.storage;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.AddressBook;
import seedu.address.testutil.Assert;
import seedu.address.testutil.TypicalPersons;

public class JsonSerializableAddressBookTest {

    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonSerializableAddressBookTest");
    private static final Path TYPICAL_PERSONS_FILE = JsonSerializableAddressBookTest.TEST_DATA_FOLDER
            .resolve("typicalPersonsAddressBook.json");
    private static final Path INVALID_PERSON_FILE = JsonSerializableAddressBookTest.TEST_DATA_FOLDER
            .resolve("invalidPersonAddressBook.json");
    private static final Path DUPLICATE_PERSON_FILE = JsonSerializableAddressBookTest.TEST_DATA_FOLDER
            .resolve("duplicatePersonAddressBook.json");

    @Test
    public void toModelType_typicalPersonsFile_success() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil
                .readJsonFile(JsonSerializableAddressBookTest.TYPICAL_PERSONS_FILE,
                        JsonSerializableAddressBook.class)
                .get();
        AddressBook addressBookFromFile = dataFromFile.toModelType();
        AddressBook typicalPersonsAddressBook = TypicalPersons.getTypicalAddressBook();
        Assertions.assertEquals(addressBookFromFile, typicalPersonsAddressBook);
    }

    @Test
    public void toModelType_invalidPersonFile_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil
                .readJsonFile(JsonSerializableAddressBookTest.INVALID_PERSON_FILE,
                        JsonSerializableAddressBook.class)
                .get();
        Assert.assertThrows(IllegalValueException.class, dataFromFile::toModelType);
    }

    @Test
    public void toModelType_duplicatePersons_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil
                .readJsonFile(JsonSerializableAddressBookTest.DUPLICATE_PERSON_FILE,
                        JsonSerializableAddressBook.class)
                .get();
        Assert.assertThrows(IllegalValueException.class, JsonSerializableAddressBook.MESSAGE_DUPLICATE_PERSON,
                dataFromFile::toModelType);
    }

}
