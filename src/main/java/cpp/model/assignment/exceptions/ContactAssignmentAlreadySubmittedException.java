package cpp.model.assignment.exceptions;

/**
 * Signals that an attempt was made to mark a contact assignment as submitted
 * when it was already marked as submitted.
 */
public class ContactAssignmentAlreadySubmittedException extends RuntimeException {
    public ContactAssignmentAlreadySubmittedException() {
        super("This assignment is already marked as submitted for the contact");
    }
}
