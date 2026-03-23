package cpp.model.assignment;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cpp.model.assignment.exceptions.ContactAssignmentNotFoundException;
import cpp.model.assignment.exceptions.DuplicateContactAssignmentException;

public class UniqueContactAssignmentListTest {
    private final UniqueContactAssignmentList uniqueContactAssignmentList = new UniqueContactAssignmentList();

    @Test
    public void contains_nullContactAssignment_throwsNullPointerException() {
        Assertions.assertThrows(NullPointerException.class, () -> this.uniqueContactAssignmentList.contains(null));
    }

    @Test
    public void contains_contactAssignmentNotInList_returnsFalse() {
        ContactAssignment ca = new ContactAssignment("contactId", "assignmentId");
        Assertions.assertTrue(!this.uniqueContactAssignmentList.contains(ca));
    }

    @Test
    public void contains_contactAssignmentInList_returnsTrue() {
        ContactAssignment ca = new ContactAssignment("contactId", "assignmentId");
        this.uniqueContactAssignmentList.add(ca);
        Assertions.assertTrue(this.uniqueContactAssignmentList.contains(ca));
    }

    @Test
    public void add_nullContactAssignment_throwsNullPointerException() {
        Assertions.assertThrows(NullPointerException.class, () -> this.uniqueContactAssignmentList.add(null));
    }

    @Test
    public void add_duplicateContactAssignment_throwsDuplicateContactAssignmentException() {
        ContactAssignment ca = new ContactAssignment("contactId", "assignmentId");
        this.uniqueContactAssignmentList.add(ca);
        Assertions.assertThrows(DuplicateContactAssignmentException.class,
                () -> this.uniqueContactAssignmentList.add(ca));
    }

    @Test
    public void add_validContactAssignment_addsContactAssignment() {
        ContactAssignment ca = new ContactAssignment("contactId", "assignmentId");
        this.uniqueContactAssignmentList.add(ca);
        Assertions.assertTrue(this.uniqueContactAssignmentList.contains(ca));
    }

    @Test
    public void remove_nullContactAssignment_throwsNullPointerException() {
        Assertions.assertThrows(NullPointerException.class, () -> this.uniqueContactAssignmentList.remove(null));
    }

    @Test
    public void remove_contactAssignmentDoesNotExist_throwsContactAssignmentNotFoundException() {
        ContactAssignment ca = new ContactAssignment("contactId", "assignmentId");
        Assertions.assertThrows(ContactAssignmentNotFoundException.class,
                () -> this.uniqueContactAssignmentList.remove(ca));
    }

    @Test
    public void remove_existingContactAssignment_removesContactAssignment() {
        ContactAssignment ca = new ContactAssignment("contactId", "assignmentId");
        this.uniqueContactAssignmentList.add(ca);
        this.uniqueContactAssignmentList.remove(ca);
        Assertions.assertTrue(!this.uniqueContactAssignmentList.contains(ca));
    }

    @Test
    public void removeMultiple_nullList_throwsNullPointerException() {
        Assertions.assertThrows(NullPointerException.class,
                () -> this.uniqueContactAssignmentList.removeMultiple(null));
    }

    @Test
    public void removeMultiple_normalList_removesAllContactAssignmentsInList() {
        ContactAssignment ca1 = new ContactAssignment("contactId1", "assignmentId1");
        ContactAssignment ca2 = new ContactAssignment("contactId2", "assignmentId2");
        this.uniqueContactAssignmentList.add(ca1);
        this.uniqueContactAssignmentList.add(ca2);

        java.util.List<ContactAssignment> toRemoveList = new java.util.ArrayList<>();
        toRemoveList.add(ca1);
        toRemoveList.add(ca2);

        this.uniqueContactAssignmentList.removeMultiple(toRemoveList);
        Assertions.assertTrue(!this.uniqueContactAssignmentList.contains(ca1));
        Assertions.assertTrue(!this.uniqueContactAssignmentList.contains(ca2));
    }

    @Test
    public void setContactAssignments_nullUniqueContactAssignmentList_throwsNullPointerException() {
        Assertions.assertThrows(NullPointerException.class,
                () -> this.uniqueContactAssignmentList.setContactAssignments((UniqueContactAssignmentList) null));
    }

    @Test
    public void setContactAssignments_validUniqueContactAssignmentList_replacesList() {
        UniqueContactAssignmentList replacement = new UniqueContactAssignmentList();
        ContactAssignment ca = new ContactAssignment("contactId", "assignmentId");
        replacement.add(ca);
        this.uniqueContactAssignmentList.setContactAssignments(replacement);
        Assertions.assertTrue(this.uniqueContactAssignmentList.contains(ca));
    }

    @Test
    public void setContactAssignments_nullList_throwsNullPointerException() {
        Assertions.assertThrows(NullPointerException.class,
                () -> this.uniqueContactAssignmentList.setContactAssignments((java.util.List<ContactAssignment>) null));
    }

    @Test
    public void setContactAssignments_validList_replacesOwnListWithProvidedList() {
        java.util.List<ContactAssignment> replacement = new java.util.ArrayList<>();
        ContactAssignment ca = new ContactAssignment("contactId", "assignmentId");
        replacement.add(ca);
        this.uniqueContactAssignmentList.setContactAssignments(replacement);
        Assertions.assertTrue(this.uniqueContactAssignmentList.contains(ca));
    }

    @Test
    public void setContactAssignments_listWithDuplicateContactAssignments_throwsDuplicateContactAssignmentException() {
        java.util.List<ContactAssignment> replacement = new java.util.ArrayList<>();
        ContactAssignment ca = new ContactAssignment("contactId", "assignmentId");
        ContactAssignment ca2 = new ContactAssignment("contactId2", "assignmentId2");
        ContactAssignment ca3 = new ContactAssignment("contactId3", "assignmentId3");
        replacement.add(ca);
        replacement.add(ca2);
        replacement.add(ca3);
        replacement.add(ca3);
        Assertions.assertThrows(DuplicateContactAssignmentException.class,
                () -> this.uniqueContactAssignmentList.setContactAssignments(replacement));
    }

    @Test
    public void setContactAssignments_listWithNullContactAssignment_throwsNullPointerException() {
        java.util.List<ContactAssignment> replacement = new java.util.ArrayList<>();
        replacement.add(null);
        Assertions.assertThrows(NullPointerException.class,
                () -> this.uniqueContactAssignmentList.setContactAssignments(replacement));
    }

    @Test
    public void iterator_normalBehaviour_returnsContactAssignmentsInList() {
        ContactAssignment ca1 = new ContactAssignment("contactId1", "assignmentId1");
        ContactAssignment ca2 = new ContactAssignment("contactId2", "assignmentId2");
        this.uniqueContactAssignmentList.add(ca1);
        this.uniqueContactAssignmentList.add(ca2);

        java.util.Iterator<ContactAssignment> iterator = this.uniqueContactAssignmentList.iterator();
        Assertions.assertTrue(iterator.hasNext());
        Assertions.assertTrue(iterator.next().equals(ca1));
        Assertions.assertTrue(iterator.hasNext());
        Assertions.assertTrue(iterator.next().equals(ca2));
        Assertions.assertFalse(iterator.hasNext());
    }

    @Test
    public void asUnmodifiableObservableList_modifyList_throwsUnsupportedOperationException() {
        Assertions.assertThrows(UnsupportedOperationException.class,
                () -> this.uniqueContactAssignmentList.asUnmodifiableObservableList().remove(0));
    }

    @Test
    public void equals() {
        ContactAssignment ca1 = new ContactAssignment("contactId1", "assignmentId1");
        ContactAssignment ca2 = new ContactAssignment("contactId2", "assignmentId2");

        UniqueContactAssignmentList list1 = new UniqueContactAssignmentList();
        list1.add(ca1);
        list1.add(ca2);

        UniqueContactAssignmentList list2 = new UniqueContactAssignmentList();
        list2.add(ca1);
        list2.add(ca2);

        Assertions.assertTrue(list1.equals(list2));
        Assertions.assertTrue(list1.equals(list1));
        Assertions.assertFalse(list1.equals(null));
    }

    @Test
    public void hashCode_sameContactAssignments_sameHashCode() {
        ContactAssignment ca1 = new ContactAssignment("contactId1", "assignmentId1");
        ContactAssignment ca2 = new ContactAssignment("contactId2", "assignmentId2");

        UniqueContactAssignmentList list1 = new UniqueContactAssignmentList();
        list1.add(ca1);
        list1.add(ca2);

        UniqueContactAssignmentList list2 = new UniqueContactAssignmentList();
        list2.add(ca1);
        list2.add(ca2);

        Assertions.assertTrue(list1.hashCode() == list2.hashCode());
    }
}
