package seedu.address.model.person;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import seedu.address.testutil.Assert;

public class AddressTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class, () -> new Address(null));
    }

    @Test
    public void constructor_invalidAddress_throwsIllegalArgumentException() {
        String invalidAddress = "";
        Assert.assertThrows(IllegalArgumentException.class, () -> new Address(invalidAddress));
    }

    @Test
    public void isValidAddress() {
        // null address
        Assert.assertThrows(NullPointerException.class, () -> Address.isValidAddress(null));

        // invalid addresses
        Assertions.assertFalse(Address.isValidAddress("")); // empty string
        Assertions.assertFalse(Address.isValidAddress(" ")); // spaces only

        // valid addresses
        Assertions.assertTrue(Address.isValidAddress("Blk 456, Den Road, #01-355"));
        Assertions.assertTrue(Address.isValidAddress("-")); // one character
        // long address
        Assertions.assertTrue(Address.isValidAddress("Leng Inc; 1234 Market St; San Francisco CA 2349879; USA"));
    }

    @Test
    public void equals() {
        Address address = new Address("Valid Address");

        // same values -> returns true
        Assertions.assertTrue(address.equals(new Address("Valid Address")));

        // same object -> returns true
        Assertions.assertTrue(address.equals(address));

        // null -> returns false
        Assertions.assertFalse(address.equals(null));

        // different types -> returns false
        Assertions.assertFalse(address.equals(5.0f));

        // different values -> returns false
        Assertions.assertFalse(address.equals(new Address("Other Valid Address")));
    }
}
