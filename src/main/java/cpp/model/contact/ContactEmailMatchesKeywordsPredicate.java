package cpp.model.contact;

import cpp.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Contact}'s {@code Email} matches exactly with the given
 * keyword (case-insensitive).
 */
public class ContactEmailMatchesKeywordsPredicate implements ContactSearchPredicate {
    private final String searchString;

    public ContactEmailMatchesKeywordsPredicate(String searchString) {
        this.searchString = searchString;
    }

    @Override
    public boolean test(Contact contact) {
        return contact.getEmail().value.toLowerCase().contains(this.searchString.toLowerCase());
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
        return this.searchString.equals(otherPredicate.searchString);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("searchString", this.searchString)
                .toString();
    }
}
