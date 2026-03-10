package cpp.model.classgroup.exceptions;

/**
 * Signals that an operation attempted to allocate a contact to a class group,
 * but the contact is already allocated to a class group.
 */
public class ContactAlreadyAllocatedClassGroupException extends RuntimeException {
    public ContactAlreadyAllocatedClassGroupException() {
        super("Contact is already allocated to a class group");
    }
}
