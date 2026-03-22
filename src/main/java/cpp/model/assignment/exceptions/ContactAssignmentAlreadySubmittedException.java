package cpp.model.assignment.exceptions;

public class ContactAssignmentAlreadySubmittedException extends RuntimeException {
    public ContactAssignmentAlreadySubmittedException() {
        super("This assignment is already marked as submitted for the contact");
    }
}
