package cpp.model.classgroup;

import java.util.Objects;

import cpp.commons.util.AppUtil;

/**
 * Represents a Class Group's name in the class group list.
 */
public class ClassGroupName {

    public static final String MESSAGE_CONSTRAINTS = """
            Class names must contain only alphanumeric characters, spaces, hyphens, and parentheses.
            Hyphens "-" must be between two alphanumeric characters (not at the start or end).
            Open parenthesis "(" cannot be at the start of the name, and must have a closing parenthesis ")".
            There can only be alphanumeric characters inside the parentheses, \
            and there must be at least 1 alphanumeric character.\nThe name should not be blank.""";

    /*
     * Class names must:
     * - Contain only alphanumeric, spaces, hyphens, and parentheses
     * - Hyphens must be between two alphanumeric characters (not at start/end)
     * - Parentheses must contain at least one alphanumeric character and be closed
     * (not at start)
     */
    public static final String VALIDATION_REGEX = "[\\p{Alnum}]([\\p{Alnum} ]"
            + "|-(?=[\\p{Alnum}])|\\([\\p{Alnum} ]*[\\p{Alnum}][\\p{Alnum} ]*\\))*";

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
        return this.fullName.toLowerCase().replaceAll("\\s+", "")
                .equals(otherName.fullName.toLowerCase().replaceAll("\\s+", ""));
    }

    @Override
    public int hashCode() {
        return this.fullName.toLowerCase().replaceAll("\\s+", "").hashCode();
    }

}
