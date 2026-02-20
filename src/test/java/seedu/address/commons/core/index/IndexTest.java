package seedu.address.commons.core.index;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import seedu.address.testutil.Assert;

public class IndexTest {

    @Test
    public void createOneBasedIndex() {
        // invalid index
        Assert.assertThrows(IndexOutOfBoundsException.class, () -> Index.fromOneBased(0));

        // check equality using the same base
        Assertions.assertEquals(1, Index.fromOneBased(1).getOneBased());
        Assertions.assertEquals(5, Index.fromOneBased(5).getOneBased());

        // convert from one-based index to zero-based index
        Assertions.assertEquals(0, Index.fromOneBased(1).getZeroBased());
        Assertions.assertEquals(4, Index.fromOneBased(5).getZeroBased());
    }

    @Test
    public void createZeroBasedIndex() {
        // invalid index
        Assert.assertThrows(IndexOutOfBoundsException.class, () -> Index.fromZeroBased(-1));

        // check equality using the same base
        Assertions.assertEquals(0, Index.fromZeroBased(0).getZeroBased());
        Assertions.assertEquals(5, Index.fromZeroBased(5).getZeroBased());

        // convert from zero-based index to one-based index
        Assertions.assertEquals(1, Index.fromZeroBased(0).getOneBased());
        Assertions.assertEquals(6, Index.fromZeroBased(5).getOneBased());
    }

    @Test
    public void equals() {
        final Index fifthPersonIndex = Index.fromOneBased(5);

        // same values -> returns true
        Assertions.assertTrue(fifthPersonIndex.equals(Index.fromOneBased(5)));
        Assertions.assertTrue(fifthPersonIndex.equals(Index.fromZeroBased(4)));

        // same object -> returns true
        Assertions.assertTrue(fifthPersonIndex.equals(fifthPersonIndex));

        // null -> returns false
        Assertions.assertFalse(fifthPersonIndex.equals(null));

        // different types -> returns false
        Assertions.assertFalse(fifthPersonIndex.equals(5.0f));

        // different index -> returns false
        Assertions.assertFalse(fifthPersonIndex.equals(Index.fromOneBased(1)));
    }

    @Test
    public void toStringMethod() {
        Index index = Index.fromZeroBased(0);
        String expected = Index.class.getCanonicalName() + "{zeroBasedIndex=" + index.getZeroBased() + "}";
        Assertions.assertEquals(expected, index.toString());
    }
}
