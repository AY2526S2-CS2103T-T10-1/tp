package cpp.model.contact;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cpp.testutil.ContactBuilder;

public class ContactNameContainsKeywordsPredicateTest {

    @Test
    public void equals() {

        ContactNameContainsKeywordsPredicate firstPredicate = new ContactNameContainsKeywordsPredicate("first");
        ContactNameContainsKeywordsPredicate secondPredicate = new ContactNameContainsKeywordsPredicate("first second");

        // same object -> returns true
        Assertions.assertTrue(firstPredicate.equals(firstPredicate));

        // same values -> returns true
        ContactNameContainsKeywordsPredicate firstPredicateCopy = new ContactNameContainsKeywordsPredicate("first");
        Assertions.assertTrue(firstPredicate.equals(firstPredicateCopy));

        // different types -> returns false
        Assertions.assertFalse(firstPredicate.equals(1));

        // null -> returns false
        Assertions.assertFalse(firstPredicate.equals(null));

        // different contact -> returns false
        Assertions.assertFalse(firstPredicate.equals(secondPredicate));
    }

    @Test
    public void test_nameContainsKeywords_returnsTrue() {
        // One keyword
        ContactNameContainsKeywordsPredicate predicate = new ContactNameContainsKeywordsPredicate("Alice");
        Assertions.assertTrue(predicate.test(new ContactBuilder().withName("Alice Bob").build()));

        // Multiple keywords
        predicate = new ContactNameContainsKeywordsPredicate("Alice Bob");
        Assertions.assertTrue(predicate.test(new ContactBuilder().withName("Alice Bob").build()));

        // Only one matching keyword
        predicate = new ContactNameContainsKeywordsPredicate(" Carol");
        Assertions.assertTrue(predicate.test(new ContactBuilder().withName("Alice Carol").build()));

        // Mixed-case keywords
        predicate = new ContactNameContainsKeywordsPredicate("aLIce bOB");
        Assertions.assertTrue(predicate.test(new ContactBuilder().withName("Alice Bob").build()));
    }

    @Test
    public void test_nameDoesNotContainKeywords_returnsFalse() {
        // Zero keywords
        ContactNameContainsKeywordsPredicate predicate = new ContactNameContainsKeywordsPredicate(" ");
        Assertions.assertFalse(predicate.test(new ContactBuilder().withName("Alice").build()));

        // Non-matching keyword
        predicate = new ContactNameContainsKeywordsPredicate("Carol");
        Assertions.assertFalse(predicate.test(new ContactBuilder().withName("Alice Bob").build()));

        // Keywords match phone, email and address, but does not match name
        predicate = new ContactNameContainsKeywordsPredicate("12345 alice@email.com Main Street");
        Assertions.assertFalse(predicate.test(new ContactBuilder().withName("Alice").withPhone("12345")
                .withEmail("alice@email.com").withAddress("Main Street").build()));
    }

    @Test
    public void toStringMethod() {
        ContactNameContainsKeywordsPredicate predicate = new ContactNameContainsKeywordsPredicate("keyword1 keyword2");

        String expected = ContactNameContainsKeywordsPredicate.class.getCanonicalName()
                + "{searchString=keyword1 keyword2}";
        Assertions.assertEquals(expected, predicate.toString());
    }
}
