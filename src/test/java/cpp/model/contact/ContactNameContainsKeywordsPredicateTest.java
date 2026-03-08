package cpp.model.contact;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cpp.testutil.ContactBuilder;

public class ContactNameContainsKeywordsPredicateTest {

    @Test
    public void equals() {
        List<String> firstPredicateKeywordList = Collections.singletonList("first");
        List<String> secondPredicateKeywordList = Arrays.asList("first", "second");

        ContactNameContainsKeywordsPredicate firstPredicate = new ContactNameContainsKeywordsPredicate(
                firstPredicateKeywordList);
        ContactNameContainsKeywordsPredicate secondPredicate = new ContactNameContainsKeywordsPredicate(
                secondPredicateKeywordList);

        // same object -> returns true
        Assertions.assertTrue(firstPredicate.equals(firstPredicate));

        // same values -> returns true
        ContactNameContainsKeywordsPredicate firstPredicateCopy = new ContactNameContainsKeywordsPredicate(
                firstPredicateKeywordList);
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
        ContactNameContainsKeywordsPredicate predicate = new ContactNameContainsKeywordsPredicate(
                Collections.singletonList("Alice"));
        Assertions.assertTrue(predicate.test(new ContactBuilder().withName("Alice Bob").build()));

        // Multiple keywords
        predicate = new ContactNameContainsKeywordsPredicate(Arrays.asList("Alice", "Bob"));
        Assertions.assertTrue(predicate.test(new ContactBuilder().withName("Alice Bob").build()));

        // Only one matching keyword
        predicate = new ContactNameContainsKeywordsPredicate(Arrays.asList("Bob", "Carol"));
        Assertions.assertTrue(predicate.test(new ContactBuilder().withName("Alice Carol").build()));

        // Mixed-case keywords
        predicate = new ContactNameContainsKeywordsPredicate(Arrays.asList("aLIce", "bOB"));
        Assertions.assertTrue(predicate.test(new ContactBuilder().withName("Alice Bob").build()));
    }

    @Test
    public void test_nameDoesNotContainKeywords_returnsFalse() {
        // Zero keywords
        ContactNameContainsKeywordsPredicate predicate = new ContactNameContainsKeywordsPredicate(
                Collections.emptyList());
        Assertions.assertFalse(predicate.test(new ContactBuilder().withName("Alice").build()));

        // Non-matching keyword
        predicate = new ContactNameContainsKeywordsPredicate(Arrays.asList("Carol"));
        Assertions.assertFalse(predicate.test(new ContactBuilder().withName("Alice Bob").build()));

        // Keywords match phone, email and address, but does not match name
        predicate = new ContactNameContainsKeywordsPredicate(
                Arrays.asList("12345", "alice@email.com", "Main", "Street"));
        Assertions.assertFalse(predicate.test(new ContactBuilder().withName("Alice").withPhone("12345")
                .withEmail("alice@email.com").withAddress("Main Street").build()));
    }

    @Test
    public void toStringMethod() {
        List<String> keywords = List.of("keyword1", "keyword2");
        ContactNameContainsKeywordsPredicate predicate = new ContactNameContainsKeywordsPredicate(keywords);

        String expected = ContactNameContainsKeywordsPredicate.class.getCanonicalName() + "{keywords=" + keywords + "}";
        Assertions.assertEquals(expected, predicate.toString());
    }
}
