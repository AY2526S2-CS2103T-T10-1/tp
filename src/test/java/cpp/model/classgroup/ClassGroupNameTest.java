package cpp.model.classgroup;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cpp.testutil.Assert;

public class ClassGroupNameTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class, () -> new ClassGroupName(null));
    }

    @Test
    public void constructor_invalidName_throwsIllegalArgumentException() {
        String invalidName = "";
        Assert.assertThrows(IllegalArgumentException.class, () -> new ClassGroupName(invalidName));
    }

    @Test
    public void isValidName_validName_returnsTrue() {
        Assertions.assertTrue(ClassGroupName.isValidName("CS2103T10")); // alphanumeric
        Assertions.assertTrue(ClassGroupName.isValidName("Tutorial Group 1")); // with spaces
        Assertions.assertTrue(ClassGroupName.isValidName("12345")); // numbers only
        Assertions.assertTrue(ClassGroupName.isValidName("Group A")); // with capital letters
        Assertions.assertTrue(ClassGroupName.isValidName("Advanced Software Engineering Group 3")); // long name
    }

    @Test
    public void isValidName_invalidName_returnsFalse() {
        Assertions.assertFalse(ClassGroupName.isValidName("")); // empty string
        Assertions.assertFalse(ClassGroupName.isValidName(" ")); // spaces only
        Assertions.assertFalse(ClassGroupName.isValidName("^")); // only non-alphanumeric characters
        Assertions.assertFalse(ClassGroupName.isValidName("Group@1")); // contains non-alphanumeric characters
        Assertions.assertFalse(ClassGroupName.isValidName("-Group")); // hyphen at start
        Assertions.assertFalse(ClassGroupName.isValidName("/Group")); // slash at start
    }

    @Test
    public void isValidName_null_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class, () -> ClassGroupName.isValidName(null));
    }

    @Test
    public void equals_sameValues_returnsTrue() {
        ClassGroupName name = new ClassGroupName("CS2103T10");

        // same values -> returns true
        Assertions.assertTrue(name.equals(new ClassGroupName("CS2103T10")));

        // same object -> returns true
        Assertions.assertTrue(name.equals(name));
    }

    @Test
    public void equals_caseInsensitive_returnsTrue() {
        ClassGroupName name = new ClassGroupName("CS2103T10");

        // different case -> returns true (case-insensitive equality)
        Assertions.assertTrue(name.equals(new ClassGroupName("cs2103t10")));
        Assertions.assertTrue(name.equals(new ClassGroupName("Cs2103T10")));
    }

    @Test
    public void equals_differentValues_returnsFalse() {
        ClassGroupName name = new ClassGroupName("CS2103T10");

        // different types -> returns false
        Assertions.assertFalse(name.equals(5));

        // null -> returns false
        Assertions.assertFalse(name.equals(null));

        // different name -> returns false
        Assertions.assertFalse(name.equals(new ClassGroupName("CS2101T10")));
    }

}
