package cpp.model.contact;

import cpp.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Contact}'s {@code Phone} matches exactly with the given
 * keyword (case-insensitive).
 */
public class ContactPhoneMatchesKeywordsPredicate implements ContactSearchPredicate {
    private final String searchString;

    public ContactPhoneMatchesKeywordsPredicate(String searchString) {
        this.searchString = searchString;
    }

    @Override
    public boolean test(Contact contact) {
        return contact.getPhone().value.toString().contains(this.searchString);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof ContactPhoneMatchesKeywordsPredicate)) {
            return false;
        }

        ContactPhoneMatchesKeywordsPredicate otherPredicate = (ContactPhoneMatchesKeywordsPredicate) other;
        return this.searchString.equals(otherPredicate.searchString);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("searchString", this.searchString)
                .toString();
    }
}
