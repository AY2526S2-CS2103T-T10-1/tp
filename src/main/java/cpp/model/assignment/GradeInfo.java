package cpp.model.assignment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import cpp.logic.parser.ParserUtil;

/**
 * Encapsulates grading state for a contact assignment and validates invariants.
 */
public final class GradeInfo {

    public static final String INVALID_GRADE_STRING = """
            isGraded cannot be true if submissionInfo is not marked as submitted;
            gradingDate must exist only when marked as graded;
            score must be a non-negative number (decimal places allowed) between 0 and 100;
            gradingDate cannot be before submissionDate""";
    public static final String INVALID_SCORE_STRING = """
            Score must be a non-negative number (decimal places allowed) between 0 and 100""";

    private final boolean isGraded;
    private final LocalDateTime gradingDate;
    private final float score;

    /**
     * Creates a GradeInfo with the given details. GradeInfo is valid only if the
     * given details satisfy the following invariants:
     * - isGraded cannot be true if submissionInfo is not marked as submitted
     * - gradingDate must exist only when marked as graded
     * - score must be a non-negative float between 0 and 100
     * - gradingDate cannot be before submissionDate
     */
    public GradeInfo(boolean isGraded, LocalDateTime gradingDate, float score, SubmissionInfo submissionInfo) {
        this.isGraded = isGraded;
        this.gradingDate = gradingDate;
        this.score = score;
    }

    /**
     * Creates a GradeInfo when loading from storage. This strictly validates
     * invariants and throws {@link IllegalArgumentException} on invalid data.
     */
    public static GradeInfo createFromStorage(boolean isGraded, LocalDateTime gradingDate, float score,
            SubmissionInfo submissionInfo) {
        if (!GradeInfo.isValidGradeInfo(isGraded, gradingDate, score, submissionInfo)) {
            throw new IllegalArgumentException(GradeInfo.INVALID_GRADE_STRING);
        }
        return new GradeInfo(isGraded, gradingDate, score, submissionInfo);
    }

    /**
     * Checks whether the given grading details satisfy the invariants for a valid
     * GradeInfo.
     */
    public static boolean isValidGradeInfo(boolean isGraded, LocalDateTime gradingDate, float score,
            SubmissionInfo submissionInfo) {
        if (isGraded && !submissionInfo.isSubmitted()) {
            return false;
        }
        if (isGraded && gradingDate == null) {
            return false;
        }
        if (!isGraded && gradingDate != null) {
            return false;
        }
        if (!GradeInfo.isValidScore(new BigDecimal(score))) {
            return false;
        }
        if (isGraded && submissionInfo.isSubmitted() && gradingDate.isBefore(submissionInfo.getSubmissionDate())) {
            return false;
        }
        return true;
    }

    public static boolean isValidScore(BigDecimal score) {
        return score.compareTo(BigDecimal.ZERO) >= 0 && score.compareTo(new BigDecimal(100)) <= 0;
    }

    public boolean isGraded() {
        return this.isGraded;
    }

    public LocalDateTime getGradingDate() {
        return this.gradingDate;
    }

    public float getScore() {
        return this.score;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof GradeInfo)) {
            return false;
        }
        GradeInfo o = (GradeInfo) other;
        return this.isGraded == o.isGraded
                && Objects.equals(this.gradingDate, o.gradingDate)
                && Float.compare(this.score, o.score) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.isGraded, this.gradingDate, this.score);
    }

    @Override
    public String toString() {
        String dateString = (this.gradingDate == null)
                ? "None"
                : this.gradingDate.format(ParserUtil.DATETIME_FORMATTER);
        return "GradeInfo[graded=" + this.isGraded + ", date=" + dateString
                + ", score=" + this.score + "]";
    }
}
