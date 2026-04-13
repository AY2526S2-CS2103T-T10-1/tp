package cpp.model.classgroup;

import java.util.function.Predicate;

import cpp.commons.util.ToStringBuilder;

/**
 * Tests that a {@code ClassGroup}'s {@code Name} matches any of the keywords
 * given.
 */
public class ClassNameContainsKeywordsPredicate implements Predicate<ClassGroup> {
    private final String searchString;

    public ClassNameContainsKeywordsPredicate(String searchString) {
        this.searchString = searchString;
    }

    @Override
    public boolean test(ClassGroup classGroup) {
        return classGroup.getName().fullName.toLowerCase().contains(this.searchString.toLowerCase());
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ClassNameContainsKeywordsPredicate)) {
            return false;
        }

        ClassNameContainsKeywordsPredicate pred = (ClassNameContainsKeywordsPredicate) other;
        return this.searchString.equals(pred.searchString);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("searchString", this.searchString).toString();
    }
}
