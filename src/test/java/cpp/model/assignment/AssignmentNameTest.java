package cpp.model.assignment;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cpp.testutil.Assert;

public class AssignmentNameTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class, () -> new AssignmentName(null));
    }

    @Test
    public void constructor_invalidName_throwsIllegalArgumentException() {
        String invalidName = "";
        Assert.assertThrows(IllegalArgumentException.class, () -> new AssignmentName(invalidName));
    }

    @Test
    public void isValidName_validName_returnsTrue() {
        Assertions.assertTrue(AssignmentName.isValidName("peter jack")); // alphabets only
        Assertions.assertTrue(AssignmentName.isValidName("12345")); // numbers only
        Assertions.assertTrue(AssignmentName.isValidName("peter the 2nd")); // alphanumeric characters
        Assertions.assertTrue(AssignmentName.isValidName("Capital Tan")); // with capital letters
        Assertions.assertTrue(AssignmentName.isValidName("David Roger Jackson Ray Jr 2nd")); // long names
    }

    @Test
    public void isValidName_invalidName_returnsFalse() {
        Assertions.assertFalse(AssignmentName.isValidName("")); // empty string
        Assertions.assertFalse(AssignmentName.isValidName(" ")); // spaces only
        Assertions.assertFalse(AssignmentName.isValidName("^")); // only non-alphanumeric characters
        Assertions.assertFalse(AssignmentName.isValidName("peter*")); // contains invalid character
        Assertions.assertFalse(AssignmentName.isValidName("-Assignment")); // hyphen at start
        Assertions.assertFalse(AssignmentName.isValidName("/Assignment")); // slash at start
    }

    @Test
    public void isValidName_null_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class, () -> AssignmentName.isValidName(null));
    }

    @Test
    public void equals_sameValues_returnsTrue() {
        AssignmentName name = new AssignmentName("Valid Name");

        // same values -> returns true
        Assertions.assertTrue(name.equals(new AssignmentName("Valid Name")));

        // same object -> returns true
        Assertions.assertTrue(name.equals(name));
    }

    @Test
    public void equals_differentValues_returnsFalse() {
        AssignmentName name = new AssignmentName("Valid Name");

        // different types -> returns false
        Assertions.assertFalse(name.equals(5));

        // null -> returns false
        Assertions.assertFalse(name.equals(null));

        // different name -> returns false
        Assertions.assertFalse(name.equals(new AssignmentName("Different Name")));
    }

}
