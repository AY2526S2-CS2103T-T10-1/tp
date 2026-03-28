package cpp.model.assignment;

import java.util.function.Predicate;

import cpp.commons.util.ToStringBuilder;

/**
 * Tests that an {@code Assignment}'s {@code Name} contains the given search
 * string.
 */
public class AssignmentNameContainsKeywordsPredicate implements Predicate<Assignment> {
    private final String searchString;

    public AssignmentNameContainsKeywordsPredicate(String searchString) {
        this.searchString = searchString;
    }

    @Override
    public boolean test(Assignment assignment) {
        return assignment.getName().fullName.toLowerCase().contains(this.searchString.toLowerCase());
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AssignmentNameContainsKeywordsPredicate)) {
            return false;
        }

        AssignmentNameContainsKeywordsPredicate pred = (AssignmentNameContainsKeywordsPredicate) other;
        return this.searchString.equals(pred.searchString);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("searchString", this.searchString).toString();
    }
}
