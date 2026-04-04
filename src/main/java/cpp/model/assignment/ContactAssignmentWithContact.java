package cpp.model.assignment;

import cpp.model.contact.Contact;

/**
 * Simple data transfer object that pairs a ContactAssignment with its Contact.
 */
public class ContactAssignmentWithContact {
    private final ContactAssignment contactAssignment;
    private final Contact contact;

    /**
     * Constructs a ContactAssignmentWithContact pairing.
     *
     * @param contactAssignment the contact-assignment
     * @param contact           the contact (may be null)
     */
    public ContactAssignmentWithContact(ContactAssignment contactAssignment, Contact contact) {
        this.contactAssignment = contactAssignment;
        this.contact = contact;
    }

    public ContactAssignment getContactAssignment() {
        return this.contactAssignment;
    }

    public Contact getContact() {
        return this.contact;
    }
}
