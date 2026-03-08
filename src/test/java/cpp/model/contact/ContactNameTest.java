package cpp.model.contact;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cpp.testutil.Assert;

public class ContactNameTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class, () -> new ContactName(null));
    }

    @Test
    public void constructor_invalidName_throwsIllegalArgumentException() {
        String invalidName = "";
        Assert.assertThrows(IllegalArgumentException.class, () -> new ContactName(invalidName));
    }

    @Test
    public void isValidName() {
        // null name
        Assert.assertThrows(NullPointerException.class, () -> ContactName.isValidName(null));

        // invalid name
        Assertions.assertFalse(ContactName.isValidName("")); // empty string
        Assertions.assertFalse(ContactName.isValidName(" ")); // spaces only
        Assertions.assertFalse(ContactName.isValidName("^")); // only non-alphanumeric characters
        Assertions.assertFalse(ContactName.isValidName("peter*")); // contains non-alphanumeric characters

        // valid name
        Assertions.assertTrue(ContactName.isValidName("peter jack")); // alphabets only
        Assertions.assertTrue(ContactName.isValidName("12345")); // numbers only
        Assertions.assertTrue(ContactName.isValidName("peter the 2nd")); // alphanumeric characters
        Assertions.assertTrue(ContactName.isValidName("Capital Tan")); // with capital letters
        Assertions.assertTrue(ContactName.isValidName("David Roger Jackson Ray Jr 2nd")); // long names
    }

    @Test
    public void equals() {
        ContactName name = new ContactName("Valid Name");

        // same values -> returns true
        Assertions.assertTrue(name.equals(new ContactName("Valid Name")));

        // same object -> returns true
        Assertions.assertTrue(name.equals(name));

        // null -> returns false
        Assertions.assertFalse(name.equals(null));

        // different types -> returns false
        Assertions.assertFalse(name.equals(5.0f));

        // different values -> returns false
        Assertions.assertFalse(name.equals(new ContactName("Other Valid Name")));
    }
}
