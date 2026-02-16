package seedu.address.model.person;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import seedu.address.testutil.Assert;

public class NameTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class, () -> new Name(null));
    }

    @Test
    public void constructor_invalidName_throwsIllegalArgumentException() {
        String invalidName = "";
        Assert.assertThrows(IllegalArgumentException.class, () -> new Name(invalidName));
    }

    @Test
    public void isValidName() {
        // null name
        Assert.assertThrows(NullPointerException.class, () -> Name.isValidName(null));

        // invalid name
        Assertions.assertFalse(Name.isValidName("")); // empty string
        Assertions.assertFalse(Name.isValidName(" ")); // spaces only
        Assertions.assertFalse(Name.isValidName("^")); // only non-alphanumeric characters
        Assertions.assertFalse(Name.isValidName("peter*")); // contains non-alphanumeric characters

        // valid name
        Assertions.assertTrue(Name.isValidName("peter jack")); // alphabets only
        Assertions.assertTrue(Name.isValidName("12345")); // numbers only
        Assertions.assertTrue(Name.isValidName("peter the 2nd")); // alphanumeric characters
        Assertions.assertTrue(Name.isValidName("Capital Tan")); // with capital letters
        Assertions.assertTrue(Name.isValidName("David Roger Jackson Ray Jr 2nd")); // long names
    }

    @Test
    public void equals() {
        Name name = new Name("Valid Name");

        // same values -> returns true
        Assertions.assertTrue(name.equals(new Name("Valid Name")));

        // same object -> returns true
        Assertions.assertTrue(name.equals(name));

        // null -> returns false
        Assertions.assertFalse(name.equals(null));

        // different types -> returns false
        Assertions.assertFalse(name.equals(5.0f));

        // different values -> returns false
        Assertions.assertFalse(name.equals(new Name("Other Valid Name")));
    }
}
