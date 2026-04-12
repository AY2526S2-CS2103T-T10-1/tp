package cpp.model.classgroup;

import java.util.Objects;

import cpp.commons.util.AppUtil;

/**
 * Represents a Class Group's name in the class group list.
 */
public class ClassGroupName {

    public static final String MESSAGE_CONSTRAINTS = """
            Names should only contain alphanumeric characters and spaces, and it should not be blank""";

    /*
     * The first character of the address must not be a whitespace,
     * otherwise " " (a blank string) becomes a valid input.
     */
    public static final String VALIDATION_REGEX = "[\\p{Alnum}][\\p{Alnum} ]*";

    public final String fullName;

    /**
     * Constructs a {@code Name}.
     *
     * @param name A valid name.
     */
    public ClassGroupName(String name) {
        Objects.requireNonNull(name);
        AppUtil.checkArgument(ClassGroupName.isValidName(name), ClassGroupName.MESSAGE_CONSTRAINTS);
        this.fullName = name;
    }

    /**
     * Returns true if a given string is a valid name.
     */
    public static boolean isValidName(String test) {
        return test.matches(ClassGroupName.VALIDATION_REGEX);
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
        if (!(other instanceof ClassGroupName)) {
            return false;
        }

        ClassGroupName otherName = (ClassGroupName) other;
        return this.fullName.toLowerCase().equals(otherName.fullName.toLowerCase());
    }

    @Override
    public int hashCode() {
        return this.fullName.toLowerCase().hashCode();
    }

}
