package seedu.address.model.person;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import seedu.address.testutil.Assert;

public class EmailTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class, () -> new Email(null));
    }

    @Test
    public void constructor_invalidEmail_throwsIllegalArgumentException() {
        String invalidEmail = "";
        Assert.assertThrows(IllegalArgumentException.class, () -> new Email(invalidEmail));
    }

    @Test
    public void isValidEmail() {
        // null email
        Assert.assertThrows(NullPointerException.class, () -> Email.isValidEmail(null));

        // blank email
        Assertions.assertFalse(Email.isValidEmail("")); // empty string
        Assertions.assertFalse(Email.isValidEmail(" ")); // spaces only

        // missing parts
        Assertions.assertFalse(Email.isValidEmail("@example.com")); // missing local part
        Assertions.assertFalse(Email.isValidEmail("peterjackexample.com")); // missing '@' symbol
        Assertions.assertFalse(Email.isValidEmail("peterjack@")); // missing domain name

        // invalid parts
        Assertions.assertFalse(Email.isValidEmail("peterjack@-")); // invalid domain name
        Assertions.assertFalse(Email.isValidEmail("peterjack@exam_ple.com")); // underscore in domain name
        Assertions.assertFalse(Email.isValidEmail("peter jack@example.com")); // spaces in local part
        Assertions.assertFalse(Email.isValidEmail("peterjack@exam ple.com")); // spaces in domain name
        Assertions.assertFalse(Email.isValidEmail(" peterjack@example.com")); // leading space
        Assertions.assertFalse(Email.isValidEmail("peterjack@example.com ")); // trailing space
        Assertions.assertFalse(Email.isValidEmail("peterjack@@example.com")); // double '@' symbol
        Assertions.assertFalse(Email.isValidEmail("peter@jack@example.com")); // '@' symbol in local part
        Assertions.assertFalse(Email.isValidEmail("-peterjack@example.com")); // local part starts with a hyphen
        Assertions.assertFalse(Email.isValidEmail("peterjack-@example.com")); // local part ends with a hyphen
        Assertions.assertFalse(Email.isValidEmail("peter..jack@example.com")); // local part has two consecutive periods
        Assertions.assertFalse(Email.isValidEmail("peterjack@example@com")); // '@' symbol in domain name
        Assertions.assertFalse(Email.isValidEmail("peterjack@.example.com")); // domain name starts with a period
        Assertions.assertFalse(Email.isValidEmail("peterjack@example.com.")); // domain name ends with a period
        Assertions.assertFalse(Email.isValidEmail("peterjack@-example.com")); // domain name starts with a hyphen
        Assertions.assertFalse(Email.isValidEmail("peterjack@example.com-")); // domain name ends with a hyphen
        Assertions.assertFalse(Email.isValidEmail("peterjack@example.c")); // top level domain has less than two chars

        // valid email
        Assertions.assertTrue(Email.isValidEmail("PeterJack_1190@example.com")); // underscore in local part
        Assertions.assertTrue(Email.isValidEmail("PeterJack.1190@example.com")); // period in local part
        Assertions.assertTrue(Email.isValidEmail("PeterJack+1190@example.com")); // '+' symbol in local part
        Assertions.assertTrue(Email.isValidEmail("PeterJack-1190@example.com")); // hyphen in local part
        Assertions.assertTrue(Email.isValidEmail("a@bc")); // minimal
        Assertions.assertTrue(Email.isValidEmail("test@localhost")); // alphabets only
        Assertions.assertTrue(Email.isValidEmail("123@145")); // numeric local part and domain name
        // mixture of alphanumeric and special characters
        Assertions.assertTrue(Email.isValidEmail("a1+be.d@example1.com"));
        Assertions.assertTrue(Email.isValidEmail("peter_jack@very-very-very-long-example.com")); // long domain name
        Assertions.assertTrue(Email.isValidEmail("if.you.dream.it_you.can.do.it@example.com")); // long local part
        Assertions.assertTrue(Email.isValidEmail("e1234567@u.nus.edu")); // more than one period in domain
    }

    @Test
    public void equals() {
        Email email = new Email("valid@email");

        // same values -> returns true
        Assertions.assertTrue(email.equals(new Email("valid@email")));

        // same object -> returns true
        Assertions.assertTrue(email.equals(email));

        // null -> returns false
        Assertions.assertFalse(email.equals(null));

        // different types -> returns false
        Assertions.assertFalse(email.equals(5.0f));

        // different values -> returns false
        Assertions.assertFalse(email.equals(new Email("other.valid@email")));
    }
}
