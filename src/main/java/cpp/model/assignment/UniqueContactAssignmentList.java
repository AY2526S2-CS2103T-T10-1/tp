package cpp.model.assignment;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import cpp.commons.util.CollectionUtil;
import cpp.model.assignment.exceptions.ContactAssignmentNotFoundException;
import cpp.model.assignment.exceptions.DuplicateContactAssignmentException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * A list of contact assignments that enforces uniqueness between its elements
 * and does not allow nulls.
 */
public class UniqueContactAssignmentList implements Iterable<ContactAssignment> {

    private final ObservableList<ContactAssignment> internalList = FXCollections.observableArrayList();
    private final ObservableList<ContactAssignment> internalUnmodifiableList = FXCollections
            .unmodifiableObservableList(this.internalList);

    /**
     * Returns true if the list contains an equivalent contact assignment as the
     * given argument.
     */
    public boolean contains(ContactAssignment toCheck) {
        Objects.requireNonNull(toCheck);
        return this.internalList.stream().anyMatch(ca -> ca.equals(toCheck));
    }

    /**
     * Adds a contact assignment to the list.
     * The contact assignment must not already exist in the list.
     */
    public void add(ContactAssignment toAdd) {
        Objects.requireNonNull(toAdd);
        if (this.contains(toAdd)) {
            throw new DuplicateContactAssignmentException();
        }
        this.internalList.add(toAdd);
    }

    /**
     * Removes the equivalent contact assignment from the list.
     * The contact assignment must exist in the list.
     */
    public void remove(ContactAssignment toRemove) {
        Objects.requireNonNull(toRemove);
        if (!this.internalList.remove(toRemove)) {
            throw new ContactAssignmentNotFoundException();
        }
    }

    /**
     * Removes all the contact assignments in the given list from this list. The
     * contact assignments must exist in the list.
     */
    public void removeMultiple(List<ContactAssignment> toRemoveList) {
        CollectionUtil.requireAllNonNull(toRemoveList);
        for (ContactAssignment ca : toRemoveList) {
            try {
                this.remove(ca);
            } catch (ContactAssignmentNotFoundException e) {
                // ignore if any contact assignment is not found since that aligns with our goal
            }
        }
    }

    public void setContactAssignments(UniqueContactAssignmentList replacement) {
        Objects.requireNonNull(replacement);
        this.internalList.setAll(replacement.internalList);
    }

    /**
     * Replaces the contents of this list with {@code contactAssignments}.
     * {@code contactAssignments} must not contain duplicate entries.
     */
    public void setContactAssignments(List<ContactAssignment> contactAssignments) {
        CollectionUtil.requireAllNonNull(contactAssignments);
        if (!this.contactAssignmentsAreUnique(contactAssignments)) {
            throw new DuplicateContactAssignmentException();
        }

        this.internalList.setAll(contactAssignments);
    }

    /**
     * Returns the backing list as an unmodifiable {@code ObservableList}.
     */
    public ObservableList<ContactAssignment> asUnmodifiableObservableList() {
        return this.internalUnmodifiableList;
    }

    @Override
    public Iterator<ContactAssignment> iterator() {
        return this.internalList.iterator();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof UniqueContactAssignmentList)) {
            return false;
        }

        UniqueContactAssignmentList otherList = (UniqueContactAssignmentList) other;
        return this.internalList.equals(otherList.internalList);
    }

    @Override
    public int hashCode() {
        return this.internalList.hashCode();
    }

    @Override
    public String toString() {
        return this.internalList.toString();
    }

    /**
     * Returns true if {@code contactAssignments} contains only unique contact
     * assignments.
     */
    private boolean contactAssignmentsAreUnique(List<ContactAssignment> contactAssignments) {
        for (int i = 0; i < contactAssignments.size() - 1; i++) {
            for (int j = i + 1; j < contactAssignments.size(); j++) {
                if (contactAssignments.get(i).equals(contactAssignments.get(j))) {
                    return false;
                }
            }
        }
        return true;
    }

}
