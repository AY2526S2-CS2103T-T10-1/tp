package cpp.model.assignment;

import java.time.LocalDateTime;
import java.util.Objects;

import cpp.logic.parser.ParserUtil;

/**
 * Encapsulates submission state for a contact assignment.
 */
public final class SubmissionInfo {

    private final boolean isSubmitted;
    private final LocalDateTime submissionDate;

    /**
     * Creates a SubmissionInfo with the given details. SubmissionInfo is valid only
     * if the given details satisfy the following invariant:
     * - submissionDate must exist only when marked as submitted
     */
    public SubmissionInfo(boolean isSubmitted, LocalDateTime submissionDate) {
        if (!SubmissionInfo.isValidSubmissionInfo(isSubmitted, submissionDate)) {
            throw new IllegalArgumentException("submissionDate must exist only when marked as submitted");
        }
        this.isSubmitted = isSubmitted;
        this.submissionDate = submissionDate;
    }

    /**
     * Checks whether the given submission details satisfy the invariant for a valid
     * SubmissionInfo.
     */
    public static boolean isValidSubmissionInfo(boolean isSubmitted, LocalDateTime submissionDate) {
        // Return false if submitted but no submission date, or if not submitted but
        // submission date exists
        if (isSubmitted ^ (submissionDate != null)) {
            return false;
        }
        return true;
    }

    public boolean isSubmitted() {
        return this.isSubmitted;
    }

    public LocalDateTime getSubmissionDate() {
        return this.submissionDate;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof SubmissionInfo)) {
            return false;
        }
        SubmissionInfo o = (SubmissionInfo) other;
        return this.isSubmitted == o.isSubmitted
                && Objects.equals(this.submissionDate, o.submissionDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.isSubmitted, this.submissionDate);
    }

    @Override
    public String toString() {
        String formattedDate = (this.submissionDate == null)
                ? "None"
                : this.submissionDate.format(ParserUtil.DATETIME_FORMATTER);
        return "SubmissionInfo[submitted=" + this.isSubmitted + ", date=" + formattedDate + "]";
    }
}
