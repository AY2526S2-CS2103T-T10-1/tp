package cpp.model.assignment;

import java.util.List;
import java.util.function.Predicate;

import cpp.commons.util.StringUtil;
import cpp.commons.util.ToStringBuilder;

/**
 * Tests that an {@code Assignment}'s {@code Name} matches any of the keywords
 * given.
 */
public class AssignmentNameContainsKeywordsPredicate implements Predicate<Assignment> {
    private final List<String> keywords;

    public AssignmentNameContainsKeywordsPredicate(List<String> keywords) {
        this.keywords = keywords;
    }

    @Override
    public boolean test(Assignment assignment) {
        return this.keywords.stream()
                .anyMatch(keyword -> StringUtil.containsWordIgnoreCase(assignment.getName().fullName, keyword));
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
        return this.keywords.equals(pred.keywords);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("keywords", this.keywords).toString();
    }
}
