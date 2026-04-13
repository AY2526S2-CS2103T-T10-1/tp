package cpp.model.contact;

import java.util.Objects;

import cpp.commons.util.AppUtil;

/**
 * Represents a Contact's name in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidName(String)}
 */
public class ContactName {

    public static final String MESSAGE_CONSTRAINTS = """
            Contact names must start with an alphabetic character, and only contain alphanumeric characters, \
            spaces, "s/o" or "d/o" patterns, hyphens, and parentheses.
            The contact name should not be blank.
            Forward slashes "/" are only allowed in "s/o" or "d/o" patterns (case-insensitive), and must follow the \
            pattern [Name] [s/o or d/o] [Name], \
            requiring at least one word before and after the space-delimited separator.
            Hyphens "-" must be between two alphanumeric characters.
            Parentheses must:
                - Not be at the start of the name
                - Contain only spaces and alphanumeric characters, with at least 1 alphanumeric character
                - Be properly closed (No nesting allowed)
                - Open parenthesis "(" must be immediately followed by an alphanumeric character
                - Closing parenthesis ")" must be immediately preceded by an alphanumeric character.""";

    /*
     * Names must:
     * - Start with an alphabetic character [A-Za-z]
     * - Contain only alphanumeric, spaces, s/o, d/o, hyphens, and parentheses
     * - Slashes only allowed in patterns: s/o, d/o (case-insensitive)
     * - Hyphens must be between two alphanumeric characters
     * - Parentheses must contain at least one alphanumeric character and be closed
     * (not at start)
     */
    public static final String VALIDATION_REGEX = """
            [A-Za-z](\
            [\\p{Alnum} ]\
            |(?<=[\\p{Alnum}])-(?=[\\p{Alnum}])\
            |(?<=[\\p{Alnum}\\)] )[sSdD]/[oO](?= [\\(\\p{Alnum}])\
            |\\([\\p{Alnum}]([\\p{Alnum} ]*[\\p{Alnum}])?\\))*""";

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
        return this.fullName.toLowerCase().replaceAll("\\s+", "")
                .equals(otherName.fullName.toLowerCase().replaceAll("\\s+", ""));
    }

    @Override
    public int hashCode() {
        return this.fullName.toLowerCase().replaceAll("\\s+", "").hashCode();
    }

}
