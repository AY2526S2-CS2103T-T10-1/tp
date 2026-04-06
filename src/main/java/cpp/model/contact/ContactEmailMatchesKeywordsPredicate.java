package cpp.model.contact;

import java.util.List;

import cpp.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Contact}'s {@code Email} matches exactly with the given
 * keyword (case-insensitive).
 */
public class ContactEmailMatchesKeywordsPredicate implements ContactSearchPredicate {
    private final List<String> keywords;

    public ContactEmailMatchesKeywordsPredicate(List<String> keywords) {
        this.keywords = keywords;
    }

    @Override
    public boolean test(Contact contact) {
        return this.keywords.stream()
                .anyMatch(keyword -> contact.getEmail().value.equalsIgnoreCase(keyword));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof ContactEmailMatchesKeywordsPredicate)) {
            return false;
        }

        ContactEmailMatchesKeywordsPredicate otherPredicate = (ContactEmailMatchesKeywordsPredicate) other;
        return this.keywords.equals(otherPredicate.keywords);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("keywords", this.keywords)
                .toString();
    }
}
