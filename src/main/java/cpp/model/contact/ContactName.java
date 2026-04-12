package cpp.model.contact;

import java.util.Objects;

import cpp.commons.util.AppUtil;

/**
 * Represents a Contact's name in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidName(String)}
 */
public class ContactName {

    public static final String MESSAGE_CONSTRAINTS = """
            Names should only contain alphanumeric characters and spaces. Special characters
            such as hyphens (-), forward slashes (/), and parentheses () are allowed to support
            naming conventions like hyphenated names (Anne-Marie), son/daughter indicators
            (s/o, d/o), and ethnic names in brackets (Tan Ah Beng (Alan)). The name should
            not be blank""";

    /*
     * Names must:
     * - Start with an alphanumeric character
     * - Contain only alphanumeric, spaces, hyphens (between alphanumeric),
     * parentheses (with content), and s/o, d/o patterns
     * - Hyphens must be between two alphanumeric characters (not at start/end)
     * - Slashes only allowed in patterns: s/o, S/O, d/o, D/O
     * - Parentheses must contain at least one alphanumeric character
     */
    public static final String VALIDATION_REGEX = "[\\p{Alnum}]([\\p{Alnum} ]"
            + "|(?<=[sSdD])/[oO]|-(?=[\\p{Alnum}])|\\([\\p{Alnum} ]*[\\p{Alnum}][\\p{Alnum} ]*\\))*";

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
