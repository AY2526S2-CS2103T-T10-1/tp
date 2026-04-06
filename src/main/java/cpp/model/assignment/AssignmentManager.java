package cpp.model.assignment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import cpp.commons.util.CollectionUtil;
import cpp.model.assignment.exceptions.ContactAssignmentNotFoundException;
import cpp.model.contact.Contact;

/**
 * Manages allocations of assignments to contacts and their per-contact and
 * per-assignment state, for fast lookup of contact assignments by assignment
 * and by contact.
 */
public class AssignmentManager {

    private final Map<String, Map<String, ContactAssignment>> byAssignment;
    private final Map<String, Map<String, ContactAssignment>> byContact;

    /**
     * Creates an empty assignment manager with no assignments or contacts.
     */
    public AssignmentManager() {
        this.byAssignment = new HashMap<>();
        this.byContact = new HashMap<>();
    }

    /**
     * Creates an assignment manager with the given initial contact assignments. The
     * initial contact assignments must not contain duplicates.
     */
    public AssignmentManager(List<ContactAssignment> initialAssignments) {
        this.byAssignment = new HashMap<>();
        this.byContact = new HashMap<>();
        for (ContactAssignment ca : initialAssignments) {
            this.registerContactAssignment(ca);
        }
    }

    /**
     * Registers a contact assignment for fast lookup.
     *
     * @param ca the contact assignment to register
     */
    public void registerContactAssignment(ContactAssignment ca) {
        CollectionUtil.requireAllNonNull(ca);
        Map<String, ContactAssignment> assMap = this.byAssignment.computeIfAbsent(ca.getAssignmentId(),
                k -> new HashMap<>());
        if (!assMap.containsKey(ca.getContactId())) {
            assMap.put(ca.getContactId(), ca);
        }
        Map<String, ContactAssignment> contactMap = this.byContact.computeIfAbsent(ca.getContactId(),
                k -> new HashMap<>());
        if (!contactMap.containsKey(ca.getAssignmentId())) {
            contactMap.put(ca.getAssignmentId(), ca);
        }
    }

    /**
     * Deregisters a contact assignment from the fast lookup maps.
     *
     * @param ca the contact assignment to deregister
     */
    public void deregisterContactAssignment(ContactAssignment ca) {
        CollectionUtil.requireAllNonNull(ca);
        Map<String, ContactAssignment> assMap = this.byAssignment.get(ca.getAssignmentId());
        if (assMap != null) {
            assMap.remove(ca.getContactId());
            if (assMap.isEmpty()) {
                this.byAssignment.remove(ca.getAssignmentId());
            }
        }

        Map<String, ContactAssignment> contactMap = this.byContact.get(ca.getContactId());
        if (contactMap != null) {
            contactMap.remove(ca.getAssignmentId());
            if (contactMap.isEmpty()) {
                this.byContact.remove(ca.getContactId());
            }
        }
    }

    private ContactAssignment find(String assignmentId, String contactId) {
        Map<String, ContactAssignment> assMap = this.byAssignment.get(assignmentId);
        if (assMap == null) {
            throw new ContactAssignmentNotFoundException();
        }
        ContactAssignment ca = assMap.get(contactId);
        if (ca == null) {
            throw new ContactAssignmentNotFoundException();
        }
        return ca;
    }

    /**
     * Marks the contact assignment for the given assignment and contact as
     * submitted. Contact assignment must exist and not already be marked as
     * submitted.
     *
     * @param assignmentId   the id of the assignment to mark as submitted
     * @param contactId      the id of the contact to mark as submitted
     * @param submissionDate the date and time when the assignment was submitted
     */
    public void submit(String assignmentId, String contactId, LocalDateTime submissionDate) {
        ContactAssignment ca = this.find(assignmentId, contactId);
        ca.markSubmitted(submissionDate);
    }

    /**
     * Marks the contact assignment for the given assignment and contact as
     * unsubmitted. Contact assignment must exist and be currently marked as
     * submitted.
     *
     * @param assignmentId the id of the assignment to mark as unsubmitted
     * @param contactId    the id of the contact to mark as unsubmitted
     */
    public void unsubmit(String assignmentId, String contactId) {
        ContactAssignment ca = this.find(assignmentId, contactId);
        ca.markUnsubmitted();
    }

    /**
     * Grades the contact assignment for the given assignment and contact with a
     * score.
     *
     * @param assignmentId the id of the assignment to grade
     * @param contactId    the id of the contact to mark as graded
     * @param score        the score to assign to this contact assignment
     * @param gradingDate  the date and time when this contact assignment was graded
     */
    public void grade(String assignmentId, String contactId, float score, LocalDateTime gradingDate) {
        ContactAssignment ca = this.find(assignmentId, contactId);
        ca.grade(score, gradingDate);
    }

    /**
     * Ungrades the contact assignment for the given assignment and contact. Contact
     * assignment must exist and be currently marked as graded.
     *
     * @param assignmentId the id of the assignment to ungrade
     * @param contactId    the id of the contact to ungrade
     */
    public void ungrade(String assignmentId, String contactId) {
        ContactAssignment ca = this.find(assignmentId, contactId);
        ca.ungrade();
    }

    /**
     * Returns an unmodifiable map from assignment IDs to contact assignments for
     * the given contact.
     *
     * @param contactId the id of the contact to get the contact assignment mapping
     *                  for
     */
    public Map<String, ContactAssignment> getContactAssignmentMappingForAssignment(String contactId) {
        Objects.requireNonNull(contactId);
        return Collections.unmodifiableMap(this.byContact.getOrDefault(contactId, Collections.emptyMap()));
    }

    /**
     * Returns an unmodifiable map from contact IDs to contact assignments for the
     * given assignment.
     *
     * @param assignmentId the id of the assignment to get the contact assignment
     *                     mapping for
     */
    public Map<String, ContactAssignment> getContactAssignmentMappingForContact(String assignmentId) {
        Objects.requireNonNull(assignmentId);
        return Collections.unmodifiableMap(this.byAssignment.getOrDefault(assignmentId, Collections.emptyMap()));
    }

    /**
     * Returns a list of contact assignments for the given assignment.
     */
    public List<ContactAssignment> getContactAssignmentsForAssignment(Assignment assignment) {
        Objects.requireNonNull(assignment);
        return List.copyOf(this.byAssignment.getOrDefault(assignment.getId(), Collections.emptyMap()).values());
    }

    /**
     * Returns a list of ContactAssignmentWithContact for the given assignment by
     * pairing each contact assignment with its corresponding Contact from the
     * provided contacts list. If a contact is not found, the contact value will
     * be null in the DTO.
     */
    public List<ContactAssignmentWithContact> getContactAssignmentsWithContactsForAssignment(
            Assignment assignment, List<Contact> contacts) {
        Objects.requireNonNull(assignment);
        Objects.requireNonNull(contacts);
        List<ContactAssignment> cas = this.getContactAssignmentsForAssignment(assignment);
        ArrayList<ContactAssignmentWithContact> result = new ArrayList<>();
        Map<String, Contact> contactMap = new HashMap<>();
        for (Contact contact : contacts) {
            contactMap.put(contact.getId(), contact);
        }
        for (ContactAssignment ca : cas) {
            Contact contact = contactMap.get(ca.getContactId());
            result.add(new ContactAssignmentWithContact(ca, contact));
        }
        return result;
    }

    public List<ContactAssignmentWithAssignment> getContactAssignmentsWithAssignmentsForContact(
            Contact contact, List<Assignment> assignments) {
        Objects.requireNonNull(contact);
        Objects.requireNonNull(assignments);
        List<ContactAssignment> cas = this.getContactAssignmentsForContact(contact);
        ArrayList<ContactAssignmentWithAssignment> result = new ArrayList<>();
        Map<String, Assignment> assignmentMap = new HashMap<>();
        for (Assignment assignment : assignments) {
            assignmentMap.put(assignment.getId(), assignment);
        }
        for (ContactAssignment ca : cas) {
            Assignment assignment = assignmentMap.get(ca.getAssignmentId());
            result.add(new ContactAssignmentWithAssignment(ca, assignment));
        }
        return result;
    }

    /**
     * Returns a list of contact assignments for the given contact.
     */
    public List<ContactAssignment> getContactAssignmentsForContact(Contact contact) {
        Objects.requireNonNull(contact);
        return List.copyOf(this.byContact.getOrDefault(contact.getId(), Collections.emptyMap()).values());
    }

    /**
     * Deregisters all contact assignments for the given assignment ID. Should be
     * called when an assignment is deleted.
     */
    public void deregisterContactAssignmentsForAssignment(Assignment assignment) {
        Objects.requireNonNull(assignment);
        String assignmentId = assignment.getId();
        Map<String, ContactAssignment> assMap = this.byAssignment.remove(assignmentId);
        if (assMap == null) {
            return;
        }

        for (ContactAssignment ca : assMap.values()) {
            Map<String, ContactAssignment> contactMap = this.byContact.get(ca.getContactId());
            if (contactMap == null) {
                continue;
            }

            contactMap.remove(assignmentId);
            if (contactMap.isEmpty()) {
                this.byContact.remove(ca.getContactId());
            }
        }
    }

    /**
     * Deregisters all contact assignments for the given contact ID. Should be
     * called when a contact is deleted.
     */
    public void deregisterContactAssignmentsForContact(Contact contact) {
        Objects.requireNonNull(contact);
        String contactId = contact.getId();
        Map<String, ContactAssignment> contactMap = this.byContact.remove(contactId);
        if (contactMap == null) {
            return;
        }

        for (ContactAssignment ca : contactMap.values()) {
            Map<String, ContactAssignment> assMap = this.byAssignment.get(ca.getAssignmentId());
            if (assMap == null) {
                continue;
            }

            assMap.remove(contactId);
            if (assMap.isEmpty()) {
                this.byAssignment.remove(ca.getAssignmentId());
            }
        }
    }
}
