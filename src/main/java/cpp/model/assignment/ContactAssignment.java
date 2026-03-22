package cpp.model.assignment;

import java.util.Objects;

import cpp.commons.util.CollectionUtil;
import cpp.model.assignment.exceptions.ContactAssignmentAlreadySubmittedException;

/**
 * Association between an Assignment and a Contact (by id).
 * Holds per-contact state such as submission and grading.
 */
public class ContactAssignment {

    // Identity fields
    private final String assignmentId;
    private final String contactId;

    // Data fields
    private boolean isSubmitted;
    private boolean isGraded;
    private int score;

    /**
     * Creates a contact assignment between the given assignment and contact ids.
     * The contact assignment is initially not submitted and not graded.
     */
    public ContactAssignment(String assignmentId, String contactId) {
        CollectionUtil.requireAllNonNull(assignmentId, contactId);
        this.assignmentId = assignmentId;
        this.contactId = contactId;
        this.isSubmitted = false;
        this.isGraded = false;
        this.score = 0;
    }

    /**
     * Creates a contact assignment with the given details. This constructor is used
     * for
     * loading from storage, where the submission and grading state is already
     * available.
     */
    public ContactAssignment(String assignmentId, String contactId, boolean isSubmitted, boolean isGraded, int score) {
        CollectionUtil.requireAllNonNull(assignmentId, contactId, isSubmitted, isGraded, score);
        this.assignmentId = assignmentId;
        this.contactId = contactId;
        this.isSubmitted = isSubmitted;
        this.isGraded = isGraded;
        this.score = score;
    }

    public String getAssignmentId() {
        return this.assignmentId;
    }

    public String getContactId() {
        return this.contactId;
    }

    public boolean isSubmitted() {
        return this.isSubmitted;
    }

    public boolean isGraded() {
        return this.isGraded;
    }

    public int getScore() {
        return this.score;
    }

    public void markSubmitted() {
        if (this.isSubmitted) {
            throw new ContactAssignmentAlreadySubmittedException();
        }
        this.isSubmitted = true;
    }

    public void markUnsubmitted() {
        this.isSubmitted = false;
    }

    /**
     * Grade this contact assignment with the given score. Marks as graded.
     *
     * @param score the score to assign to this contact assignment
     */
    public void grade(int score) {
        this.isGraded = true;
        this.score = score;
    }

    /**
     * Remove the grade from this contact assignment. Marks as ungraded.
     */
    public void ungrade() {
        this.isGraded = false;
        this.score = 0;
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
                + ", submitted=" + this.isSubmitted
                + ", graded=" + this.isGraded
                + ", score=" + this.score + "]";
    }
}
