package cpp.model.assignment.exceptions;

/**
 * Signals that the operation is unable to find the specified assignment.
 */
public class AssignmentNotFoundException extends RuntimeException {
    public AssignmentNotFoundException(String message) {
        super(message);
    }
}
