package cpp.model.contact;

import java.util.Objects;

import cpp.commons.util.AppUtil;

/**
 * Represents a Contact's name in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidName(String)}
 */
public class ContactName {

    public static final String MESSAGE_CONSTRAINTS = """
            Names should only contain alphanumeric\
             characters and spaces.
            Names must start with an alphabetic character.
            Forward slashes are only allowed in s/o or d/o patterns (case-insensitive).
            The name should not be blank.""";

    /*
     * Names must:
     * - Start with an alphabetic character [A-Za-z]
     * - Contain only alphanumeric, spaces, and s/o, d/o patterns
     * - Slashes only allowed in patterns: s/o, S/O, d/o, D/O (case-insensitive)
     * - Hyphens and parentheses are not allowed
     */
    public static final String VALIDATION_REGEX = "[A-Za-z]([\\p{Alnum} ]|(?<=[sSdD])/[oO])*";

    public final String fullName;

    /**
     * Constructs a {@code Name}.
     *
     * @param name A valid name.
     */
    public ContactName(String name) {
        Objects.requireNonNull(name);
        AppUtil.checkArgument(ContactName.isValidName(name), ContactName.MESSAGE_CONSTRAINTS);
        this.fullName = name;
    }

    /**
     * Returns true if a given string is a valid name.
     */
    public static boolean isValidName(String test) {
        return test.matches(ContactName.VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return this.fullName;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ContactName)) {
            return false;
        }

        ContactName otherName = (ContactName) other;
        return this.fullName.toLowerCase().equals(otherName.fullName.toLowerCase());
    }

    @Override
    public int hashCode() {
        return this.fullName.toLowerCase().hashCode();
    }

}
