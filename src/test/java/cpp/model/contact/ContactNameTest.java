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
        Assertions.assertFalse(ContactName.isValidName("peter*")); // contains invalid character
        Assertions.assertFalse(ContactName.isValidName("-James")); // starts with hyphen
        Assertions.assertFalse(ContactName.isValidName("(Alan)")); // starts with parenthesis
        Assertions.assertFalse(ContactName.isValidName("/o")); // slash not in s/o or d/o pattern
        Assertions.assertFalse(ContactName.isValidName("a/b")); // slash not in allowed pattern
        Assertions.assertFalse(ContactName.isValidName("12345")); // starts with number
        Assertions.assertFalse(ContactName.isValidName("(Doc) Smith")); // parentheses at start
        Assertions.assertFalse(ContactName.isValidName("(s/o) Smith")); // non-alphanumeric characters in parentheses
        Assertions.assertFalse(ContactName.isValidName("John (Doc Smith")); // no close parenthesis

        // valid name
        Assertions.assertTrue(ContactName.isValidName("peter jack")); // alphabets only
        Assertions.assertTrue(ContactName.isValidName("peter the 2nd")); // alphanumeric characters
        Assertions.assertTrue(ContactName.isValidName("Capital Tan")); // with capital letters
        Assertions.assertTrue(ContactName.isValidName("David Roger Jackson Ray Jr 2nd")); // long names
        Assertions.assertTrue(ContactName.isValidName("Arun s/o Muthu")); // son of pattern
        Assertions.assertTrue(ContactName.isValidName("Priya d/o Kumar")); // daughter of pattern
        Assertions.assertTrue(ContactName.isValidName("Arun S/O Muthu")); // uppercase son of pattern
        Assertions.assertTrue(ContactName.isValidName("Priya D/O Kumar")); // uppercase daughter of pattern
        Assertions.assertTrue(ContactName.isValidName("Mary-Jane")); // hyphen between alphanumeric characters
        Assertions.assertTrue(ContactName.isValidName("John Smith-1")); // hyphen between alphanumeric characters
        Assertions.assertTrue(ContactName.isValidName("John (Doc)")); // parentheses at the end of name
        // parentheses containing alphanumeric characters
        Assertions.assertTrue(ContactName.isValidName("John (D12) Smith"));
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
