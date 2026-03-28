package cpp.model.assignment;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SubmissionInfoTest {

    @Test
    public void constructor_validSubmitted_createsObject() {
        LocalDateTime now = LocalDateTime.now();
        SubmissionInfo si = new SubmissionInfo(true, now);
        Assertions.assertEquals(true, si.isSubmitted());
        Assertions.assertEquals(now, si.getSubmissionDate());
    }

    @Test
    public void constructor_validUnsubmitted_createsObject() {
        SubmissionInfo si = new SubmissionInfo(false, null);
        Assertions.assertEquals(false, si.isSubmitted());
        Assertions.assertEquals(null, si.getSubmissionDate());
    }

    @Test
    public void constructor_submittedWithNullDate_throwsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new SubmissionInfo(true, null));
    }

    @Test
    public void constructor_unsubmittedWithNonNullDate_throwsIllegalArgumentException() {
        LocalDateTime now = LocalDateTime.now();
        Assertions.assertThrows(IllegalArgumentException.class, () -> new SubmissionInfo(false, now));
    }

    @Test
    public void equals() {
        LocalDateTime now = LocalDateTime.now();
        SubmissionInfo a = new SubmissionInfo(true, now);
        SubmissionInfo b = new SubmissionInfo(true, now);
        SubmissionInfo c = new SubmissionInfo(false, null);
        SubmissionInfo d = new SubmissionInfo(true, now.plusHours(1));

        Assertions.assertEquals(a, b);
        // reflexive
        Assertions.assertEquals(a, a);
        // different
        Assertions.assertEquals(false, a.equals(c));
        Assertions.assertNotEquals(a, null);
        Assertions.assertNotEquals(a, d);
    }

    @Test
    public void hashCodeMethod() {
        LocalDateTime now = LocalDateTime.now();
        SubmissionInfo a = new SubmissionInfo(true, now);
        SubmissionInfo b = new SubmissionInfo(true, now);

        Assertions.assertEquals(a.hashCode(), b.hashCode());
    }
}
