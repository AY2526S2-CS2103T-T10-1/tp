package seedu.address.model.person;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import seedu.address.testutil.Assert;

public class PhoneTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class, () -> new Phone(null));
    }

    @Test
    public void constructor_invalidPhone_throwsIllegalArgumentException() {
        String invalidPhone = "";
        Assert.assertThrows(IllegalArgumentException.class, () -> new Phone(invalidPhone));
    }

    @Test
    public void isValidPhone() {
        // null phone number
        Assert.assertThrows(NullPointerException.class, () -> Phone.isValidPhone(null));

        // invalid phone numbers
        Assertions.assertFalse(Phone.isValidPhone("")); // empty string
        Assertions.assertFalse(Phone.isValidPhone(" ")); // spaces only
        Assertions.assertFalse(Phone.isValidPhone("91")); // less than 3 numbers
        Assertions.assertFalse(Phone.isValidPhone("phone")); // non-numeric
        Assertions.assertFalse(Phone.isValidPhone("9011p041")); // alphabets within digits
        Assertions.assertFalse(Phone.isValidPhone("9312 1534")); // spaces within digits

        // valid phone numbers
        Assertions.assertTrue(Phone.isValidPhone("911")); // exactly 3 numbers
        Assertions.assertTrue(Phone.isValidPhone("93121534"));
        Assertions.assertTrue(Phone.isValidPhone("124293842033123")); // long phone numbers
    }

    @Test
    public void equals() {
        Phone phone = new Phone("999");

        // same values -> returns true
        Assertions.assertTrue(phone.equals(new Phone("999")));

        // same object -> returns true
        Assertions.assertTrue(phone.equals(phone));

        // null -> returns false
        Assertions.assertFalse(phone.equals(null));

        // different types -> returns false
        Assertions.assertFalse(phone.equals(5.0f));

        // different values -> returns false
        Assertions.assertFalse(phone.equals(new Phone("995")));
    }
}
