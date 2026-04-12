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
        Assertions.assertTrue(ContactName.isValidName("Mary-Jane")); // with hyphen in middle
        Assertions.assertTrue(ContactName.isValidName("Anne-Marie Tan")); // hyphenated first name
        Assertions.assertTrue(ContactName.isValidName("John (Doc) Smith")); // with brackets
        Assertions.assertTrue(ContactName.isValidName("Tan Ah Beng (Alan)")); // ethnic name in brackets
        Assertions.assertTrue(ContactName.isValidName("Arun s/o Muthu")); // son of pattern
        Assertions.assertTrue(ContactName.isValidName("Priya d/o Kumar")); // daughter of pattern
        Assertions.assertTrue(ContactName.isValidName("Arun S/O Muthu")); // uppercase son of pattern
        Assertions.assertTrue(ContactName.isValidName("Priya D/O Kumar")); // uppercase daughter of pattern
        Assertions.assertTrue(ContactName.isValidName("s/o")); // son of alone (for search keywords)
        Assertions.assertTrue(ContactName.isValidName("d/o")); // daughter of alone (for search keywords)
        Assertions.assertTrue(ContactName.isValidName("/o")); // slash followed by letter
        Assertions.assertTrue(ContactName.isValidName("-James")); // hyphen at start
        Assertions.assertTrue(ContactName.isValidName("-")); // just hyphen
        Assertions.assertTrue(ContactName.isValidName("(Alan)")); // name in brackets
    }
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
