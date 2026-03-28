package cpp.model.assignment.exceptions;

/**
 * Signals that an attempt was made to mark a contact assignment as unsubmitted
 * when it was not marked as submitted.
 */
public class ContactAssignmentNotSubmittedException extends RuntimeException {
    public ContactAssignmentNotSubmittedException() {
        super("This assignment is not marked as submitted for the contact");
    }
}
