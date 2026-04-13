package cpp.model.contact;

import cpp.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Contact}'s {@code Name} matches any of the keywords
 * given.
 */
public class ContactNameContainsKeywordsPredicate implements ContactSearchPredicate {
    private final String searchString;

    public ContactNameContainsKeywordsPredicate(String searchString) {
        this.searchString = searchString;
    }

    @Override
    public boolean test(Contact contact) {
        return contact.getName().fullName.toLowerCase().contains(this.searchString.toLowerCase());
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
        return this.searchString.equals(pred.searchString);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("searchString", this.searchString).toString();
    }
}
