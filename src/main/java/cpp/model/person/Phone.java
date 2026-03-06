package cpp.model.person;

import java.util.Objects;

import cpp.commons.util.AppUtil;

/**
 * Represents a Person's phone number in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidPhone(String)}
 */
public class Phone {

    public static final String MESSAGE_CONSTRAINTS = """
            Phone numbers should only contain numbers, and it should be at least 3 digits long""";
    public static final String VALIDATION_REGEX = "\\d{3,}";
    public final String value;

    /**
     * Constructs a {@code Phone}.
     *
     * @param phone A valid phone number.
     */
    public Phone(String phone) {
        Objects.requireNonNull(phone);
        AppUtil.checkArgument(Phone.isValidPhone(phone), Phone.MESSAGE_CONSTRAINTS);
        this.value = phone;
    }

    /**
     * Returns true if a given string is a valid phone number.
     */
    public static boolean isValidPhone(String test) {
        return test.matches(Phone.VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return this.value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Phone)) {
            return false;
        }

        Phone otherPhone = (Phone) other;
        return this.value.equals(otherPhone.value);
    }

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }

}
