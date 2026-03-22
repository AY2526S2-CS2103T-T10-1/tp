package cpp.model.assignment;

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
            score must be a non-negative integer below 100;
            gradingDate cannot be before submissionDate""";

    private final boolean isGraded;
    private final LocalDateTime gradingDate;
    private final float score;

    /**
     * Creates a GradeInfo with the given details. GradeInfo is valid only if the
     * given details satisfy the following invariants:
     * - isGraded cannot be true if submissionInfo is not marked as submitted
     * - gradingDate must exist only when marked as graded
     * - score must be a non-negative integer below 100
     * - gradingDate cannot be before submissionDate
     */
    public GradeInfo(boolean isGraded, LocalDateTime gradingDate, float score, SubmissionInfo submissionInfo) {
        if (!GradeInfo.isValidGradeInfo(isGraded, gradingDate, score, submissionInfo)) {
            throw new IllegalArgumentException(GradeInfo.INVALID_GRADE_STRING);
        }
        this.isGraded = isGraded;
        this.gradingDate = gradingDate;
        this.score = score;
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
        if (score < 0 || score > 100) {
            return false;
        }
        if (isGraded && submissionInfo.isSubmitted() && gradingDate.isBefore(submissionInfo.getSubmissionDate())) {
            return false;
        }
        return true;
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
        return "GradeInfo[graded=" + this.isGraded + ", date=" + this.gradingDate.format(ParserUtil.DATETIME_FORMATTER)
                + ", score=" + this.score + "]";
    }
}
