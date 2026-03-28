package cpp.model.assignment.exceptions;

/**
 * Signals that an attempt was made to grade a contact assignment that has
 * already been graded.
 */
public class ContactAssignmentAlreadyGradedException extends RuntimeException {
    public ContactAssignmentAlreadyGradedException() {
        super("This assignment is already graded for the contact");
    }
}
