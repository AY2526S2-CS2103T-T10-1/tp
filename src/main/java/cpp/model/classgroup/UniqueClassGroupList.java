package cpp.model.classgroup;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import cpp.commons.util.CollectionUtil;
import cpp.model.classgroup.exceptions.ClassGroupNotFoundException;
import cpp.model.classgroup.exceptions.ContactNotAllocatedClassGroupException;
import cpp.model.classgroup.exceptions.DuplicateClassGroupException;
import cpp.model.contact.Contact;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * A list of class groups that enforces uniqueness between its elements and does
 * not allow nulls.
 *
 * A class group is considered unique by comparing using the class group name.
 * Adding and updating of class groups uses the class group name for equality
 * so as to ensure that the class group being added or updated is unique in
 * terms of identity in the UniqueClassGroupList. However, the removal of a
 * class group uses {@code ClassGroup#equals(Object)} so as to ensure that the
 * class group with exactly the same fields will be removed.
 */
public class UniqueClassGroupList implements Iterable<ClassGroup> {

    private final ObservableList<ClassGroup> internalList = FXCollections.observableArrayList();
    private final ObservableList<ClassGroup> internalUnmodifiableList = FXCollections
            .unmodifiableObservableList(this.internalList);

    /**
     * Returns true if the list contains a class group with the same id as
     * {@code id}.
     */
    public boolean containsId(String id) {
        Objects.requireNonNull(id);
        return this.internalList.stream().anyMatch(cg -> cg.getId().equals(id));
    }

    /**
     * Returns true if the list contains an equivalent class group as the given
     * argument (by name).
     */
    public boolean contains(ClassGroup toCheck) {
        Objects.requireNonNull(toCheck);
        return this.internalList.stream().anyMatch(cg -> cg.getName().equals(toCheck.getName()));
    }

    /**
     * Adds a class group to the list.
     * The class group must not already exist in the list.
     */
    public void add(ClassGroup toAdd) {
        Objects.requireNonNull(toAdd);
        if (this.contains(toAdd)) {
            throw new DuplicateClassGroupException();
        }
        this.internalList.add(toAdd);
    }

    /**
     * Replaces the class group {@code target} in the list with
     * {@code editedClassGroup}.
     * {@code target} must exist in the list.
     * The class group identity of {@code editedClassGroup} must not be the same as
     * another existing class group in the list (by name).
     */
    public void setClassGroup(ClassGroup target, ClassGroup editedClassGroup) {
        CollectionUtil.requireAllNonNull(target, editedClassGroup);

        int index = this.internalList.indexOf(target);
        if (index == -1) {
            throw new ClassGroupNotFoundException();
        }

        if (!target.getName().equals(editedClassGroup.getName()) && this.contains(editedClassGroup)) {
            throw new DuplicateClassGroupException();
        }

        this.internalList.set(index, editedClassGroup);
    }

    /**
     * Replaces the contents of this list with {@code classGroups}.
     * {@code classGroups} must not contain duplicate class groups (by name).
     */
    public void setClassGroups(List<ClassGroup> classGroups) {
        CollectionUtil.requireAllNonNull(classGroups);
        if (!this.classGroupsAreUnique(classGroups)) {
            throw new DuplicateClassGroupException();
        }
        this.internalList.setAll(classGroups);
    }

    public void setClassGroups(UniqueClassGroupList replacement) {
        Objects.requireNonNull(replacement);
        this.internalList.setAll(replacement.internalList);
    }

    /**
     * Removes the equivalent class group from the list.
     * This class group must exist in the list.
     */
    public void remove(ClassGroup toRemove) {
        Objects.requireNonNull(toRemove);
        if (!this.internalList.remove(toRemove)) {
            throw new ClassGroupNotFoundException();
        }
    }

    /**
     * Unallocates a contact from all class groups in this list. If the contact is
     * not allocated to a class group, nothing happens.
     */
    public void unallocateContactFromAllClassGroups(Contact contact) {
        String contactId = contact.getId();
        for (ClassGroup cg : this.internalList) {
            try {
                cg.unallocateContact(contactId);
            } catch (ContactNotAllocatedClassGroupException e) {
                // do nothing
            }
        }
    }

    /**
     * Returns the backing list as an unmodifiable {@code ObservableList}.
     */
    public ObservableList<ClassGroup> asUnmodifiableObservableList() {
        return this.internalUnmodifiableList;
    }

    @Override
    public Iterator<ClassGroup> iterator() {
        return this.internalList.iterator();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof UniqueClassGroupList)) {
            return false;
        }

        UniqueClassGroupList otherUniqueClassGroupList = (UniqueClassGroupList) other;
        return this.internalList.equals(otherUniqueClassGroupList.internalList);
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
     * Returns true if {@code classGroups} contains only unique class groups
     * (by name).
     */
    private boolean classGroupsAreUnique(List<ClassGroup> classGroups) {
        for (int i = 0; i < classGroups.size() - 1; i++) {
            for (int j = i + 1; j < classGroups.size(); j++) {
                if (classGroups.get(i).getName().equals(classGroups.get(j).getName())) {
                    return false;
                }
            }
        }
        return true;
    }
}
