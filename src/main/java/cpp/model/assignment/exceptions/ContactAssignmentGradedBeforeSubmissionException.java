package cpp.model.assignment.exceptions;

/**
 * Signals that a contact assignment is graded before the time of submission.
 */
public class ContactAssignmentGradedBeforeSubmissionException extends RuntimeException {
    public ContactAssignmentGradedBeforeSubmissionException() {
        super("A contact's assignment cannot be graded before the time of submission");
    }

}
