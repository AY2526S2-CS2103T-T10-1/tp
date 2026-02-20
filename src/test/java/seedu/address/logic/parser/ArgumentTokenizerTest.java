package seedu.address.logic.parser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ArgumentTokenizerTest {

    private final Prefix unknownPrefix = new Prefix("--u");
    private final Prefix pSlash = new Prefix("p/");
    private final Prefix dashT = new Prefix("-t");
    private final Prefix hatQ = new Prefix("^Q");

    @Test
    public void tokenize_emptyArgsString_noValues() {
        String argsString = "  ";
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(argsString, this.pSlash);

        this.assertPreambleEmpty(argMultimap);
        this.assertArgumentAbsent(argMultimap, this.pSlash);
    }

    private void assertPreamblePresent(ArgumentMultimap argMultimap, String expectedPreamble) {
        Assertions.assertEquals(expectedPreamble, argMultimap.getPreamble());
    }

    private void assertPreambleEmpty(ArgumentMultimap argMultimap) {
        Assertions.assertTrue(argMultimap.getPreamble().isEmpty());
    }

    /**
     * Asserts all the arguments in {@code argMultimap} with {@code prefix} match
     * the {@code expectedValues}
     * and only the last value is returned upon calling
     * {@code ArgumentMultimap#getValue(Prefix)}.
     */
    private void assertArgumentPresent(ArgumentMultimap argMultimap, Prefix prefix, String... expectedValues) {

        // Verify the last value is returned
        Assertions.assertEquals(expectedValues[expectedValues.length - 1], argMultimap.getValue(prefix).get());

        // Verify the number of values returned is as expected
        Assertions.assertEquals(expectedValues.length, argMultimap.getAllValues(prefix).size());

        // Verify all values returned are as expected and in order
        for (int i = 0; i < expectedValues.length; i++) {
            Assertions.assertEquals(expectedValues[i], argMultimap.getAllValues(prefix).get(i));
        }
    }

    private void assertArgumentAbsent(ArgumentMultimap argMultimap, Prefix prefix) {
        Assertions.assertFalse(argMultimap.getValue(prefix).isPresent());
    }

    @Test
    public void tokenize_noPrefixes_allTakenAsPreamble() {
        String argsString = "  some random string /t tag with leading and trailing spaces ";
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(argsString);

        // Same string expected as preamble, but leading/trailing spaces should be
        // trimmed
        this.assertPreamblePresent(argMultimap, argsString.trim());

    }

    @Test
    public void tokenize_oneArgument() {
        // Preamble present
        String argsString = "  Some preamble string p/ Argument value ";
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(argsString, this.pSlash);
        this.assertPreamblePresent(argMultimap, "Some preamble string");
        this.assertArgumentPresent(argMultimap, this.pSlash, "Argument value");

        // No preamble
        argsString = " p/   Argument value ";
        argMultimap = ArgumentTokenizer.tokenize(argsString, this.pSlash);
        this.assertPreambleEmpty(argMultimap);
        this.assertArgumentPresent(argMultimap, this.pSlash, "Argument value");

    }

    @Test
    public void tokenize_multipleArguments() {
        // Only two arguments are present
        String argsString = "SomePreambleString -t dashT-Value p/pSlash value";
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(argsString, this.pSlash, this.dashT, this.hatQ);
        this.assertPreamblePresent(argMultimap, "SomePreambleString");
        this.assertArgumentPresent(argMultimap, this.pSlash, "pSlash value");
        this.assertArgumentPresent(argMultimap, this.dashT, "dashT-Value");
        this.assertArgumentAbsent(argMultimap, this.hatQ);

        // All three arguments are present
        argsString = "Different Preamble String ^Q111 -t dashT-Value p/pSlash value";
        argMultimap = ArgumentTokenizer.tokenize(argsString, this.pSlash, this.dashT, this.hatQ);
        this.assertPreamblePresent(argMultimap, "Different Preamble String");
        this.assertArgumentPresent(argMultimap, this.pSlash, "pSlash value");
        this.assertArgumentPresent(argMultimap, this.dashT, "dashT-Value");
        this.assertArgumentPresent(argMultimap, this.hatQ, "111");

        /* Also covers: Reusing of the tokenizer multiple times */

        // Reuse tokenizer on an empty string to ensure ArgumentMultimap is correctly
        // reset
        // (i.e. no stale values from the previous tokenizing remain)
        argsString = "";
        argMultimap = ArgumentTokenizer.tokenize(argsString, this.pSlash, this.dashT, this.hatQ);
        this.assertPreambleEmpty(argMultimap);
        this.assertArgumentAbsent(argMultimap, this.pSlash);

        /* Also covers: testing for prefixes not specified as a prefix */

        // Prefixes not previously given to the tokenizer should not return any values
        argsString = this.unknownPrefix + "some value";
        argMultimap = ArgumentTokenizer.tokenize(argsString, this.pSlash, this.dashT, this.hatQ);
        this.assertArgumentAbsent(argMultimap, this.unknownPrefix);
        this.assertPreamblePresent(argMultimap, argsString); // Unknown prefix is taken as part of preamble
    }

    @Test
    public void tokenize_multipleArgumentsWithRepeats() {
        // Two arguments repeated, some have empty values
        String argsString = "SomePreambleString -t dashT-Value ^Q ^Q -t another dashT value p/ pSlash value -t";
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(argsString, this.pSlash, this.dashT, this.hatQ);
        this.assertPreamblePresent(argMultimap, "SomePreambleString");
        this.assertArgumentPresent(argMultimap, this.pSlash, "pSlash value");
        this.assertArgumentPresent(argMultimap, this.dashT, "dashT-Value", "another dashT value", "");
        this.assertArgumentPresent(argMultimap, this.hatQ, "", "");
    }

    @Test
    public void tokenize_multipleArgumentsJoined() {
        String argsString = "SomePreambleStringp/ pSlash joined-tjoined -t not joined^Qjoined";
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(argsString, this.pSlash, this.dashT, this.hatQ);
        this.assertPreamblePresent(argMultimap, "SomePreambleStringp/ pSlash joined-tjoined");
        this.assertArgumentAbsent(argMultimap, this.pSlash);
        this.assertArgumentPresent(argMultimap, this.dashT, "not joined^Qjoined");
        this.assertArgumentAbsent(argMultimap, this.hatQ);
    }

    @Test
    public void equalsMethod() {
        Prefix aaa = new Prefix("aaa");

        Assertions.assertEquals(aaa, aaa);
        Assertions.assertEquals(aaa, new Prefix("aaa"));

        Assertions.assertNotEquals(aaa, "aaa");
        Assertions.assertNotEquals(aaa, new Prefix("aab"));
    }

}
