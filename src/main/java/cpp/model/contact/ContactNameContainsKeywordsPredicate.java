package cpp.model.contact;

import java.util.List;

import cpp.commons.util.StringUtil;
import cpp.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Contact}'s {@code Name} matches any of the keywords
 * given.
 */
public class ContactNameContainsKeywordsPredicate implements ContactSearchPredicate {
    private final List<String> keywords;

    public ContactNameContainsKeywordsPredicate(List<String> keywords) {
        this.keywords = keywords;
    }

    @Override
    public boolean test(Contact contact) {
        return this.keywords.stream()
                .anyMatch(keyword -> StringUtil.containsWordIgnoreCase(contact.getName().fullName, keyword));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ContactNameContainsKeywordsPredicate)) {
            return false;
        }

        ContactNameContainsKeywordsPredicate pred = (ContactNameContainsKeywordsPredicate) other;
        return this.keywords.equals(pred.keywords);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("keywords", this.keywords).toString();
    }
}
