package cpp.model.assignment.exceptions;

public class DuplicateAssignmentException extends RuntimeException {
    public DuplicateAssignmentException() {
        super("Operation would result in duplicate assignments");
    }
}
