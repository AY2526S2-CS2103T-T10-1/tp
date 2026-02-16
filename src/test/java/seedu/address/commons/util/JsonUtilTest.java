package seedu.address.commons.util;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import seedu.address.testutil.SerializableTestClass;
import seedu.address.testutil.TestUtil;

/**
 * Tests JSON Read and Write
 */
public class JsonUtilTest {

    private static final Path SERIALIZATION_FILE = TestUtil.getFilePathInSandboxFolder("serialize.json");

    @Test
    public void serializeObjectToJsonFile_noExceptionThrown() throws IOException {
        SerializableTestClass serializableTestClass = new SerializableTestClass();
        serializableTestClass.setTestValues();

        JsonUtil.serializeObjectToJsonFile(JsonUtilTest.SERIALIZATION_FILE, serializableTestClass);

        Assertions.assertEquals(FileUtil.readFromFile(JsonUtilTest.SERIALIZATION_FILE),
                SerializableTestClass.JSON_STRING_REPRESENTATION);
    }

    @Test
    public void deserializeObjectFromJsonFile_noExceptionThrown() throws IOException {
        FileUtil.writeToFile(JsonUtilTest.SERIALIZATION_FILE, SerializableTestClass.JSON_STRING_REPRESENTATION);

        SerializableTestClass serializableTestClass = JsonUtil
                .deserializeObjectFromJsonFile(JsonUtilTest.SERIALIZATION_FILE, SerializableTestClass.class);

        Assertions.assertEquals(serializableTestClass.getName(), SerializableTestClass.getNameTestValue());
        Assertions.assertEquals(serializableTestClass.getListOfLocalDateTimes(),
                SerializableTestClass.getListTestValues());
        Assertions.assertEquals(serializableTestClass.getMapOfIntegerToString(),
                SerializableTestClass.getHashMapTestValues());
    }

    // TODO: @Test jsonUtil_readJsonStringToObjectInstance_correctObject()

    // TODO: @Test jsonUtil_writeThenReadObjectToJson_correctObject()
}
