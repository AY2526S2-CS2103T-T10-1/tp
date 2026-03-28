package cpp.model.assignment.exceptions;

/**
 * Signals that an attempt was made to mark a contact assignment as ungraded
 * when it was not marked as graded.
 */
public class ContactAssignmentNotGradedException extends RuntimeException {
    public ContactAssignmentNotGradedException() {
        super("This assignment is not marked as graded for the contact");
    }
}
