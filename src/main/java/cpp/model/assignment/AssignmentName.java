package cpp.model.assignment;

import java.util.Objects;

import cpp.commons.util.AppUtil;

/**
 * Represents an Assignment's name in the assignment list.
 */
public class AssignmentName {

    public static final String MESSAGE_CONSTRAINTS = """
            Assignment names must contain only alphanumeric characters, spaces, hyphens, and parentheses.
            The assignment name should not be blank.
            Hyphens "-" must be between two alphanumeric characters.
            Open parenthesis "(" cannot be at the start of the name, and must have a closing parenthesis ")".
            Parentheses must:
                - Not be at the start of the name
                - Contain only spaces and alphanumeric characters, with at least 1 alphanumeric character
                - Be properly closed (No nesting allowed)
                - Open parenthesis "(" must be immediately followed by an alphanumeric character
                - Closing parenthesis ")" must be immediately preceded by an alphanumeric character.""";

    /*
     * Assignment names must:
     * - Contain only alphanumeric, spaces, hyphens, and parentheses
     * - Hyphens must be between two alphanumeric characters
     * - Parentheses must contain at least one alphanumeric character and be closed
     * (not at start)
     */
    public static final String VALIDATION_REGEX = """
            [\\p{Alnum}](\
            [\\p{Alnum} ]\
            |(?<=[\\p{Alnum}])-(?=[\\p{Alnum}])\
            |\\([\\p{Alnum}]([\\p{Alnum} ]*[\\p{Alnum}])?\\))*""";

    public final String fullName;

    /**
     * Constructs a {@code Name}.
     *
     * @param name A valid name.
     */
    public AssignmentName(String name) {
        Objects.requireNonNull(name);
        AppUtil.checkArgument(AssignmentName.isValidName(name), AssignmentName.MESSAGE_CONSTRAINTS);
        this.fullName = name;
    }

    /**
     * Returns true if a given string is a valid name.
     */
    public static boolean isValidName(String test) {
        return test.matches(AssignmentName.VALIDATION_REGEX);
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
        if (!(other instanceof AssignmentName)) {
            return false;
        }

        AssignmentName otherName = (AssignmentName) other;
        return this.fullName.toLowerCase().replaceAll("\\s+", "")
                .equals(otherName.fullName.toLowerCase().replaceAll("\\s+", ""));
    }

    @Override
    public int hashCode() {
        return this.fullName.toLowerCase().replaceAll("\\s+", "").hashCode();
    }

}
