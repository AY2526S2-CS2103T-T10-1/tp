package cpp.model.assignment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import cpp.commons.exceptions.IllegalValueException;
import cpp.logic.parser.ParserUtil;

/**
 * Encapsulates grading state for a contact assignment and validates invariants.
 */
public final class GradeInfo {

    public static final String INVALID_GRADE_STRING = """
            isGraded cannot be true if submissionInfo is not marked as submitted;
            gradingDate must be null if and only if ungraded;
            score must be 0 when ungraded;
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
     * - gradingDate must be null if and only if ungraded
     * - score must be 0 when ungraded
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
     * invariants and throws {@link IllegalValueException} on invalid data.
     */
    public static GradeInfo createFromStorage(boolean isGraded, LocalDateTime gradingDate, String scoreString,
            SubmissionInfo submissionInfo) throws IllegalValueException {
        if (isGraded && !submissionInfo.isSubmitted()) {
            throw new IllegalValueException("isGraded cannot be true if the assignment is not submitted");
        }
        if (isGraded && gradingDate == null) {
            throw new IllegalValueException("gradingDate cannot be null if the assignment is graded");
        }
        if (!isGraded && gradingDate != null) {
            throw new IllegalValueException("gradingDate must be null if the assignment is not graded");
        }
        BigDecimal score;
        try {
            score = new BigDecimal(scoreString);
        } catch (NumberFormatException e) {
            throw new IllegalValueException(GradeInfo.INVALID_SCORE_STRING);
        }
        if (!GradeInfo.isValidScore(score)) {
            throw new IllegalValueException(GradeInfo.INVALID_SCORE_STRING);
        }
        if (!isGraded && score.compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalValueException("score must be 0 when ungraded");
        }
        if (isGraded && submissionInfo.isSubmitted() && gradingDate.isBefore(submissionInfo.getSubmissionDate())) {
            throw new IllegalValueException("gradingDate cannot be before submissionDate");
        }
        return new GradeInfo(isGraded, gradingDate, ParserUtil.parseScore(scoreString), submissionInfo);
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
