package cpp.model.assignment;

import java.time.LocalDateTime;
import java.util.Objects;

import cpp.commons.util.CollectionUtil;
import cpp.model.assignment.exceptions.ContactAssignmentAlreadyGradedException;
import cpp.model.assignment.exceptions.ContactAssignmentAlreadySubmittedException;
import cpp.model.assignment.exceptions.ContactAssignmentGradedBeforeSubmissionException;
import cpp.model.assignment.exceptions.ContactAssignmentNotGradedException;
import cpp.model.assignment.exceptions.ContactAssignmentNotSubmittedException;

/**
 * Association between an Assignment and a Contact (by id).
 * Holds per-contact state such as submission and grading.
 */
public class ContactAssignment {

    // Identity fields
    private final String assignmentId;
    private final String contactId;

    // Data fields
    private SubmissionInfo submissionInfo;
    private GradeInfo gradeInfo;

    /**
     * Creates a contact assignment between the given assignment and contact ids.
     * The contact assignment is initially not submitted and not graded.
     */
    public ContactAssignment(String assignmentId, String contactId) {
        CollectionUtil.requireAllNonNull(assignmentId, contactId);
        this.assignmentId = assignmentId;
        this.contactId = contactId;
        this.submissionInfo = new SubmissionInfo(false, null);
        this.gradeInfo = new GradeInfo(false, null, 0, this.submissionInfo);
    }

    /**
     * Creates a contact assignment with the given details. This constructor is used
     * for loading from storage, where the submission and grading state is already
     * available.
     */
    public ContactAssignment(String assignmentId, String contactId, boolean isSubmitted, LocalDateTime submissionDate,
            boolean isGraded, LocalDateTime gradingDate, float score) {
        // Note: we allow submissionDate and gradingDate to be null, as they may not be
        // set if the assignment is not yet submitted or graded.
        CollectionUtil.requireAllNonNull(assignmentId, contactId, isSubmitted, isGraded, score);
        this.assignmentId = assignmentId;
        this.contactId = contactId;
        this.submissionInfo = new SubmissionInfo(isSubmitted, submissionDate);
        this.gradeInfo = new GradeInfo(isGraded, gradingDate, score, this.submissionInfo);
    }

    public String getAssignmentId() {
        return this.assignmentId;
    }

    public String getContactId() {
        return this.contactId;
    }

    public boolean isSubmitted() {
        return this.submissionInfo.isSubmitted();
    }

    public LocalDateTime getSubmissionDate() {
        return this.submissionInfo.getSubmissionDate();
    }

    public boolean isGraded() {
        return this.gradeInfo.isGraded();
    }

    public LocalDateTime getGradingDate() {
        return this.gradeInfo.getGradingDate();
    }

    public float getScore() {
        return this.gradeInfo.getScore();
    }

    /**
     * Marks this contact assignment as submitted. Throws an exception if it is
     * already marked as submitted.
     */
    public void markSubmitted(LocalDateTime submissionDate) {
        if (this.submissionInfo.isSubmitted()) {
            throw new ContactAssignmentAlreadySubmittedException();
        }
        this.submissionInfo = new SubmissionInfo(true, submissionDate);
        this.gradeInfo = new GradeInfo(this.gradeInfo.isGraded(), this.gradeInfo.getGradingDate(),
                this.gradeInfo.getScore(), this.submissionInfo);
    }

    /**
     * Marks this contact assignment as unsubmitted. Throws an exception if it is
     * not currently marked as submitted. Unsubmitting also resets the grading state
     * to ungraded and score to 0.
     */
    public void markUnsubmitted() {
        if (!this.submissionInfo.isSubmitted()) {
            throw new ContactAssignmentNotSubmittedException();
        }
        this.submissionInfo = new SubmissionInfo(false, null);
        this.gradeInfo = new GradeInfo(false, null, 0, this.submissionInfo);
    }

    /**
     * Grade this contact assignment with the given score. Marks as graded.
     * Throws an exception if it is already marked as graded.
     * Throws an exception if it is not currently marked as submitted, as a contact
     * assignment cannot be graded if it has not been submitted.
     *
     * @param score       the score to assign to this contact assignment
     * @param gradingDate the date and time when this contact assignment was graded
     */
    public void grade(float score, LocalDateTime gradingDate) {
        if (!this.isSubmitted()) {
            throw new ContactAssignmentNotSubmittedException();
        }
        if (this.isGraded()) {
            throw new ContactAssignmentAlreadyGradedException();
        }
        if (gradingDate.isBefore(this.submissionInfo.getSubmissionDate())) {
            throw new ContactAssignmentGradedBeforeSubmissionException();
        }
        this.gradeInfo = new GradeInfo(true, gradingDate, score, this.submissionInfo);
    }

    /**
     * Remove the grade from this contact assignment. Marks as ungraded.
     * Throws an exception if it is not currently marked as graded.
     * Throws an exception if it is not currently marked as submitted, as a contact
     * assignment cannot be ungraded if it has not been submitted.
     */
    public void ungrade() {
        if (!this.isSubmitted()) {
            throw new ContactAssignmentNotSubmittedException();
        }
        if (!this.isGraded()) {
            throw new ContactAssignmentNotGradedException();
        }
        this.gradeInfo = new GradeInfo(false, null, 0, this.submissionInfo);
    }

    /**
     * Returns true if both contact assignments have the same assignment and contact
     * ids.
     *
     * @param other the other contact assignment to compare with
     * @return true if both contact assignments have the same assignment and contact
     *         ids
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ContactAssignment)) {
            return false;
        }
        ContactAssignment o = (ContactAssignment) other;
        return this.assignmentId.equals(o.assignmentId)
                && this.contactId.equals(o.contactId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.assignmentId, this.contactId);
    }

    @Override
    public String toString() {
        return "ContactAssignment[assignmentId=" + this.assignmentId
                + ", contactId=" + this.contactId
                + ", submission=" + this.submissionInfo
                + ", grade=" + this.gradeInfo + "]";
    }
}
