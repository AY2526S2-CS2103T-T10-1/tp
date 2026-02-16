package seedu.address.commons.util;

import java.io.FileNotFoundException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import seedu.address.testutil.Assert;

public class StringUtilTest {

    // ---------------- Tests for isNonZeroUnsignedInteger
    // --------------------------------------

    @Test
    public void isNonZeroUnsignedInteger() {

        // EP: empty strings
        Assertions.assertFalse(StringUtil.isNonZeroUnsignedInteger("")); // Boundary value
        Assertions.assertFalse(StringUtil.isNonZeroUnsignedInteger("  "));

        // EP: not a number
        Assertions.assertFalse(StringUtil.isNonZeroUnsignedInteger("a"));
        Assertions.assertFalse(StringUtil.isNonZeroUnsignedInteger("aaa"));

        // EP: zero
        Assertions.assertFalse(StringUtil.isNonZeroUnsignedInteger("0"));

        // EP: zero as prefix
        Assertions.assertTrue(StringUtil.isNonZeroUnsignedInteger("01"));

        // EP: signed numbers
        Assertions.assertFalse(StringUtil.isNonZeroUnsignedInteger("-1"));
        Assertions.assertFalse(StringUtil.isNonZeroUnsignedInteger("+1"));

        // EP: numbers with white space
        Assertions.assertFalse(StringUtil.isNonZeroUnsignedInteger(" 10 ")); // Leading/trailing spaces
        Assertions.assertFalse(StringUtil.isNonZeroUnsignedInteger("1 0")); // Spaces in the middle

        // EP: number larger than Integer.MAX_VALUE
        Assertions.assertFalse(StringUtil.isNonZeroUnsignedInteger(Long.toString(Integer.MAX_VALUE + 1)));

        // EP: valid numbers, should return true
        Assertions.assertTrue(StringUtil.isNonZeroUnsignedInteger("1")); // Boundary value
        Assertions.assertTrue(StringUtil.isNonZeroUnsignedInteger("10"));
    }

    // ---------------- Tests for containsWordIgnoreCase
    // --------------------------------------

    /*
     * Invalid equivalence partitions for word: null, empty, multiple words
     * Invalid equivalence partitions for sentence: null
     * The four test cases below test one invalid input at a time.
     */

    @Test
    public void containsWordIgnoreCase_nullWord_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class,
                () -> StringUtil.containsWordIgnoreCase("typical sentence", null));
    }

    @Test
    public void containsWordIgnoreCase_emptyWord_throwsIllegalArgumentException() {
        Assert.assertThrows(IllegalArgumentException.class, "Word parameter cannot be empty",
                () -> StringUtil.containsWordIgnoreCase("typical sentence", "  "));
    }

    @Test
    public void containsWordIgnoreCase_multipleWords_throwsIllegalArgumentException() {
        Assert.assertThrows(IllegalArgumentException.class, "Word parameter should be a single word",
                () -> StringUtil.containsWordIgnoreCase("typical sentence", "aaa BBB"));
    }

    @Test
    public void containsWordIgnoreCase_nullSentence_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class, () -> StringUtil.containsWordIgnoreCase(null, "abc"));
    }

    /*
     * Valid equivalence partitions for word:
     * - any word
     * - word containing symbols/numbers
     * - word with leading/trailing spaces
     *
     * Valid equivalence partitions for sentence:
     * - empty string
     * - one word
     * - multiple words
     * - sentence with extra spaces
     *
     * Possible scenarios returning true:
     * - matches first word in sentence
     * - last word in sentence
     * - middle word in sentence
     * - matches multiple words
     *
     * Possible scenarios returning false:
     * - query word matches part of a sentence word
     * - sentence word matches part of the query word
     *
     * The test method below tries to verify all above with a reasonably low number
     * of test cases.
     */

    @Test
    public void containsWordIgnoreCase_validInputs_correctResult() {

        // Empty sentence
        Assertions.assertFalse(StringUtil.containsWordIgnoreCase("", "abc")); // Boundary case
        Assertions.assertFalse(StringUtil.containsWordIgnoreCase("    ", "123"));

        // Matches a partial word only
        // Sentence word bigger than query word
        Assertions.assertFalse(StringUtil.containsWordIgnoreCase("aaa bbb ccc", "bb"));
        // Query word bigger than sentence word
        Assertions.assertFalse(StringUtil.containsWordIgnoreCase("aaa bbb ccc", "bbbb"));

        // Matches word in the sentence, different upper/lower case letters
        Assertions.assertTrue(StringUtil.containsWordIgnoreCase("aaa bBb ccc", "Bbb")); // First word (boundary case)
        Assertions.assertTrue(StringUtil.containsWordIgnoreCase("aaa bBb ccc@1", "CCc@1")); // Last word (boundary case)
        // Sentence has extra spaces
        Assertions.assertTrue(StringUtil.containsWordIgnoreCase("  AAA   bBb   ccc  ", "aaa"));
        // Only one word in sentence (boundary case)
        Assertions.assertTrue(StringUtil.containsWordIgnoreCase("Aaa", "aaa"));
        Assertions.assertTrue(StringUtil.containsWordIgnoreCase("aaa bbb ccc", "  ccc  ")); // Leading/trailing spaces

        // Matches multiple words in sentence
        Assertions.assertTrue(StringUtil.containsWordIgnoreCase("AAA bBb ccc  bbb", "bbB"));
    }

    // ---------------- Tests for getDetails --------------------------------------

    /*
     * Equivalence Partitions: null, valid throwable object
     */

    @Test
    public void getDetails_exceptionGiven() {
        Assertions.assertTrue(StringUtil.getDetails(new FileNotFoundException("file not found"))
                .contains("java.io.FileNotFoundException: file not found"));
    }

    @Test
    public void getDetails_nullGiven_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class, () -> StringUtil.getDetails(null));
    }

}
