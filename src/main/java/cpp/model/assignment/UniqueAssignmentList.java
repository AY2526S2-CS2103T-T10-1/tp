package cpp.model.assignment;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import cpp.commons.util.CollectionUtil;
import cpp.model.assignment.exceptions.AssignmentNotFoundException;
import cpp.model.assignment.exceptions.DuplicateAssignmentException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * A list of assignments that enforces uniqueness between its elements and does
 * not allow nulls.
 *
 * An assignment is considered unique by comparing using the assignment name.
 * Adding and updating of assignments uses the assignment name for equality so
 * as to ensure that the assignment being added or updated is unique in terms of
 * identity in the UniqueAssignmentList. However, the removal of an assignment
 * uses {@code Assignment#equals(Object)} so as to ensure that the assignment
 * with exactly the same fields will be removed.
 */
public class UniqueAssignmentList implements Iterable<Assignment> {

    private final ObservableList<Assignment> internalList = FXCollections.observableArrayList();
    private final ObservableList<Assignment> internalUnmodifiableList = FXCollections
            .unmodifiableObservableList(this.internalList);

    /**
     * Returns true if the list contains an equivalent assignment as the given
     * argument (by name).
     */
    public boolean contains(Assignment toCheck) {
        Objects.requireNonNull(toCheck);
        return this.internalList.stream().anyMatch(a -> a.getName().equals(toCheck.getName()));
    }

    /**
     * Adds an assignment to the list.
     * The assignment must not already exist in the list.
     */
    public void add(Assignment toAdd) {
        Objects.requireNonNull(toAdd);
        if (this.contains(toAdd)) {
            throw new DuplicateAssignmentException();
        }
        this.internalList.add(toAdd);
    }

    /**
     * Replaces the assignment {@code target} in the list with
     * {@code editedAssignment}.
     * {@code target} must exist in the list.
     * The assignment identity of {@code editedAssignment} must not be the
     * same as another existing assignment in the list (by name).
     */
    public void setAssignment(Assignment target, Assignment editedAssignment) {
        CollectionUtil.requireAllNonNull(target, editedAssignment);

        int index = this.internalList.indexOf(target);
        if (index == -1) {
            throw new AssignmentNotFoundException("Assignment not found");
        }

        if (!target.getName().equals(editedAssignment.getName()) && this.contains(editedAssignment)) {
            throw new DuplicateAssignmentException();
        }

        this.internalList.set(index, editedAssignment);
    }

    /**
     * Removes the equivalent assignment from the list.
     * The assignment must exist in the list.
     */
    public void remove(Assignment toRemove) {
        Objects.requireNonNull(toRemove);
        if (!this.internalList.remove(toRemove)) {
            throw new AssignmentNotFoundException("Assignment not found");
        }
    }

    public void setAssignments(UniqueAssignmentList replacement) {
        Objects.requireNonNull(replacement);
        this.internalList.setAll(replacement.internalList);
    }

    /**
     * Replaces the contents of this list with {@code assignments}.
     * {@code assignments} must not contain duplicate assignments (by name).
     */
    public void setAssignments(List<Assignment> assignments) {
        CollectionUtil.requireAllNonNull(assignments);
        if (!this.assignmentsAreUnique(assignments)) {
            throw new DuplicateAssignmentException();
        }

        this.internalList.setAll(assignments);
    }

    /**
     * Returns the backing list as an unmodifiable {@code ObservableList}.
     */
    public ObservableList<Assignment> asUnmodifiableObservableList() {
        return this.internalUnmodifiableList;
    }

    @Override
    public Iterator<Assignment> iterator() {
        return this.internalList.iterator();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof UniqueAssignmentList)) {
            return false;
        }

        UniqueAssignmentList otherUniqueAssignmentList = (UniqueAssignmentList) other;
        return this.internalList.equals(otherUniqueAssignmentList.internalList);
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
     * Returns true if {@code assignments} contains only unique assignments
     * (by name).
     */
    private boolean assignmentsAreUnique(List<Assignment> assignments) {
        for (int i = 0; i < assignments.size() - 1; i++) {
            for (int j = i + 1; j < assignments.size(); j++) {
                if (assignments.get(i).getName().equals(assignments.get(j).getName())) {
                    return false;
                }
            }
        }
        return true;
    }

}
