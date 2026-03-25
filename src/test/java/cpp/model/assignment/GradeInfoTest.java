package cpp.model.assignment;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GradeInfoTest {

    @Test
    public void constructor_validGraded_createsObject() {
        LocalDateTime submission = LocalDateTime.now();
        LocalDateTime grading = submission.plusHours(1);
        SubmissionInfo si = new SubmissionInfo(true, submission);
        GradeInfo gi = new GradeInfo(true, grading, 95f, si);
        Assertions.assertEquals(true, gi.isGraded());
        Assertions.assertEquals(grading, gi.getGradingDate());
        Assertions.assertEquals(95f, gi.getScore());
    }

    @Test
    public void constructor_validUngraded_createsObject() {
        SubmissionInfo si = new SubmissionInfo(false, null);
        GradeInfo gi = new GradeInfo(false, null, 0f, si);
        Assertions.assertEquals(false, gi.isGraded());
        Assertions.assertEquals(null, gi.getGradingDate());
        Assertions.assertEquals(0f, gi.getScore());
    }

    @Test
    public void createFromStorage_gradedButNotSubmitted_throwsIllegalArgumentException() {
        SubmissionInfo si = new SubmissionInfo(false, null);
        LocalDateTime grading = LocalDateTime.now();
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> GradeInfo.createFromStorage(true, grading, 50f, si));
    }

    @Test
    public void createFromStorage_gradedWithNullDate_throwsIllegalArgumentException() {
        SubmissionInfo si = new SubmissionInfo(true, LocalDateTime.now());
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> GradeInfo.createFromStorage(true, null, 50f, si));
    }

    @Test
    public void createFromStorage_ungradedWithNonNullDate_throwsIllegalArgumentException() {
        SubmissionInfo si = new SubmissionInfo(true, LocalDateTime.now());
        LocalDateTime grading = LocalDateTime.now();
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> GradeInfo.createFromStorage(false, grading, 0f, si));
    }

    @Test
    public void createFromStorage_negativeScore_throwsIllegalArgumentException() {
        SubmissionInfo si = new SubmissionInfo(true, LocalDateTime.now());
        LocalDateTime grading = LocalDateTime.now();
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> GradeInfo.createFromStorage(true, grading, -1f, si));
    }

    @Test
    public void createFromStorage_scoreTooLarge_throwsIllegalArgumentException() {
        SubmissionInfo si = new SubmissionInfo(true, LocalDateTime.now());
        LocalDateTime grading = LocalDateTime.now();
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> GradeInfo.createFromStorage(true, grading, 101f, si));
    }

    @Test
    public void createFromStorage_gradingBeforeSubmission_throwsIllegalArgumentException() {
        LocalDateTime submission = LocalDateTime.now();
        LocalDateTime grading = submission.minusDays(1);
        SubmissionInfo si = new SubmissionInfo(true, submission);
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> GradeInfo.createFromStorage(true, grading, 50f, si));
    }

    @Test
    public void isValidGradeInfo_gradedButNotSubmitted_returnsFalse() {
        LocalDateTime submission = LocalDateTime.now();
        LocalDateTime grading = submission.plusHours(1);
        SubmissionInfo si = new SubmissionInfo(false, null);
        Assertions.assertEquals(false, GradeInfo.isValidGradeInfo(true, grading, 50f, si));
    }

    @Test
    public void equals() {
        LocalDateTime submission = LocalDateTime.now();
        LocalDateTime grading = submission.plusHours(1);
        LocalDateTime grading2 = submission.plusHours(2);
        SubmissionInfo si = new SubmissionInfo(true, submission);
        GradeInfo a = new GradeInfo(true, grading, 75f, si);
        GradeInfo b = new GradeInfo(true, grading, 75f, si);
        GradeInfo c = new GradeInfo(false, null, 0f, new SubmissionInfo(false, null));
        GradeInfo d = new GradeInfo(true, grading2, 75f, si);
        GradeInfo e = new GradeInfo(true, grading, 80f, si);

        Assertions.assertEquals(a, b);
        Assertions.assertEquals(a, a);
        Assertions.assertEquals(false, a.equals(c));
        Assertions.assertNotEquals(a, d);
        Assertions.assertNotEquals(a, e);
        Assertions.assertNotEquals(a, null);
    }

    @Test
    public void hashCodeMethod() {
        LocalDateTime submission = LocalDateTime.now();
        LocalDateTime grading = submission.plusHours(1);
        SubmissionInfo si = new SubmissionInfo(true, submission);
        GradeInfo a = new GradeInfo(true, grading, 75f, si);
        GradeInfo b = new GradeInfo(true, grading, 75f, si);

        Assertions.assertEquals(a.hashCode(), b.hashCode());
    }
}
