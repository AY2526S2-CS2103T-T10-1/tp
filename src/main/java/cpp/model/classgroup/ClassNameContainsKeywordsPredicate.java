package cpp.model.classgroup;

import java.util.List;
import java.util.function.Predicate;

import cpp.commons.util.StringUtil;
import cpp.commons.util.ToStringBuilder;

/**
 * Tests that a {@code ClassGroup}'s {@code Name} matches any of the keywords
 * given.
 */
public class ClassNameContainsKeywordsPredicate implements Predicate<ClassGroup> {
    private final List<String> keywords;

    public ClassNameContainsKeywordsPredicate(List<String> keywords) {
        this.keywords = keywords;
    }

    @Override
    public boolean test(ClassGroup classGroup) {
        return this.keywords.stream()
                .anyMatch(keyword -> StringUtil.containsWordIgnoreCase(classGroup.getName().fullName, keyword));
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
        return this.keywords.equals(pred.keywords);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("keywords", this.keywords).toString();
    }
}
