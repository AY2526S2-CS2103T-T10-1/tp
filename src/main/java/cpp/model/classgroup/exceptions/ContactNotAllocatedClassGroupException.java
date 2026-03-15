package cpp.model.classgroup.exceptions;

/**
 * Signals that the contact is not allocated to the class group.
 */
public class ContactNotAllocatedClassGroupException extends RuntimeException {
    public ContactNotAllocatedClassGroupException() {
        super("The contact is not allocated to the class group.");
    }
}
