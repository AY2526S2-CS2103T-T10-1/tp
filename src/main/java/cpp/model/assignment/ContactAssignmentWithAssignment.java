package cpp.model.assignment;

/**
 * Simple data transfer object that pairs a ContactAssignment with its Assignment.
 */
public class ContactAssignmentWithAssignment {
    private final ContactAssignment contactAssignment;
    private final Assignment assignment;

    /**
     * Constructs a ContactAssignmentWithAssignment pairing.
     *
     * @param contactAssignment the contact-assignment
     * @param assignment        the assignment (may be null)
     */
    public ContactAssignmentWithAssignment(ContactAssignment contactAssignment, Assignment assignment) {
        this.contactAssignment = contactAssignment;
        this.assignment = assignment;
    }

    public ContactAssignment getContactAssignment() {
        return this.contactAssignment;
    }

    public Assignment getAssignment() {
        return this.assignment;
    }
}
