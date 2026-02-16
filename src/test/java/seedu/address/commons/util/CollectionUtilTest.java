package seedu.address.commons.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import seedu.address.testutil.Assert;

public class CollectionUtilTest {
    @Test
    public void requireAllNonNullVarargs() {
        // no arguments
        this.assertNullPointerExceptionNotThrown();

        // any non-empty argument list
        this.assertNullPointerExceptionNotThrown(new Object(), new Object());
        this.assertNullPointerExceptionNotThrown("test");
        this.assertNullPointerExceptionNotThrown("");

        // argument lists with just one null at the beginning
        this.assertNullPointerExceptionThrown((Object) null);
        this.assertNullPointerExceptionThrown(null, "", new Object());
        this.assertNullPointerExceptionThrown(null, new Object(), new Object());

        // argument lists with nulls in the middle
        this.assertNullPointerExceptionThrown(new Object(), null, null, "test");
        this.assertNullPointerExceptionThrown("", null, new Object());

        // argument lists with one null as the last argument
        this.assertNullPointerExceptionThrown("", new Object(), null);
        this.assertNullPointerExceptionThrown(new Object(), new Object(), null);

        // null reference
        this.assertNullPointerExceptionThrown((Object[]) null);

        // confirms nulls inside lists in the argument list are not considered
        List<Object> containingNull = Arrays.asList((Object) null);
        this.assertNullPointerExceptionNotThrown(containingNull, new Object());
    }

    @Test
    public void requireAllNonNullCollection() {
        // lists containing nulls in the front
        this.assertNullPointerExceptionThrown(Arrays.asList((Object) null));
        this.assertNullPointerExceptionThrown(Arrays.asList(null, new Object(), ""));

        // lists containing nulls in the middle
        this.assertNullPointerExceptionThrown(Arrays.asList("spam", null, new Object()));
        this.assertNullPointerExceptionThrown(Arrays.asList("spam", null, "eggs", null, new Object()));

        // lists containing nulls at the end
        this.assertNullPointerExceptionThrown(Arrays.asList("spam", new Object(), null));
        this.assertNullPointerExceptionThrown(Arrays.asList(new Object(), null));

        // null reference
        this.assertNullPointerExceptionThrown((Collection<Object>) null);

        // empty list
        this.assertNullPointerExceptionNotThrown(Collections.emptyList());

        // list with all non-null elements
        this.assertNullPointerExceptionNotThrown(Arrays.asList(new Object(), "ham", Integer.valueOf(1)));
        this.assertNullPointerExceptionNotThrown(Arrays.asList(new Object()));

        // confirms nulls inside nested lists are not considered
        List<Object> containingNull = Arrays.asList((Object) null);
        this.assertNullPointerExceptionNotThrown(Arrays.asList(containingNull, new Object()));
    }

    @Test
    public void isAnyNonNull() {
        Assertions.assertFalse(CollectionUtil.isAnyNonNull());
        Assertions.assertFalse(CollectionUtil.isAnyNonNull((Object) null));
        Assertions.assertFalse(CollectionUtil.isAnyNonNull((Object[]) null));
        Assertions.assertTrue(CollectionUtil.isAnyNonNull(new Object()));
        Assertions.assertTrue(CollectionUtil.isAnyNonNull(new Object(), null));
    }

    /**
     * Asserts that {@code CollectionUtil#requireAllNonNull(Object...)} throw
     * {@code NullPointerException}
     * if {@code objects} or any element of {@code objects} is null.
     */
    private void assertNullPointerExceptionThrown(Object... objects) {
        Assert.assertThrows(NullPointerException.class, () -> CollectionUtil.requireAllNonNull(objects));
    }

    /**
     * Asserts that {@code CollectionUtil#requireAllNonNull(Collection<?>)} throw
     * {@code NullPointerException}
     * if {@code collection} or any element of {@code collection} is null.
     */
    private void assertNullPointerExceptionThrown(Collection<?> collection) {
        Assert.assertThrows(NullPointerException.class, () -> CollectionUtil.requireAllNonNull(collection));
    }

    private void assertNullPointerExceptionNotThrown(Object... objects) {
        CollectionUtil.requireAllNonNull(objects);
    }

    private void assertNullPointerExceptionNotThrown(Collection<?> collection) {
        CollectionUtil.requireAllNonNull(collection);
    }
}
