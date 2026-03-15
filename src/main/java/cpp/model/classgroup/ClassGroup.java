package cpp.model.classgroup;

import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

import cpp.commons.util.CollectionUtil;
import cpp.commons.util.ToStringBuilder;
import cpp.model.classgroup.exceptions.ContactAlreadyAllocatedClassGroupException;
import cpp.model.classgroup.exceptions.ContactNotAllocatedClassGroupException;

/**
 * Represents a Class Grouping in the address book.
 * Guarantees: details are present and not null, field values are validated,
 * immutable.
 */
public class ClassGroup {

    private final String id;
    private final ClassGroupName name;
    private final HashSet<String> contactIdSet;

    /**
     * Creates a class grouping with the given name.
     * Every field must be present and not null.
     */
    public ClassGroup(ClassGroupName name) {
        CollectionUtil.requireAllNonNull(name);
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.contactIdSet = new HashSet<>();
    }

    /**
     * Creates a class grouping with the given id and name. Every field must be
     * present and not null.
     *
     * This constructor is used for loading from storage, where the id is already
     * available.
     */
    public ClassGroup(String id, ClassGroupName name) {
        CollectionUtil.requireAllNonNull(id, name);
        this.id = id;
        this.name = name;
        this.contactIdSet = new HashSet<>();
    }

    public String getId() {
        return this.id;
    }

    public ClassGroupName getName() {
        return this.name;
    }

    public HashSet<String> getContactIdSet() {
        return this.contactIdSet;
    }

    /**
     * Allocates a contact to this class group. If the contact is already allocated
     * to this class group, a ContactAlreadyAllocatedClassGroupException is thrown.
     */
    public void allocateContact(String contactId) throws ContactAlreadyAllocatedClassGroupException {
        if (this.contactIdSet.contains(contactId)) {
            throw new ContactAlreadyAllocatedClassGroupException();
        }
        this.contactIdSet.add(contactId);
    }

    /**
     * Unallocates a contact from this class group. If the contact is not allocated
     * to this class group, a ContactNotAllocatedClassGroupException is thrown.
     */
    public void unallocateContact(String contactId) throws ContactNotAllocatedClassGroupException {
        if (!this.contactIdSet.contains(contactId)) {
            throw new ContactNotAllocatedClassGroupException();
        }
        this.contactIdSet.remove(contactId);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof ClassGroup)) {
            return false;
        }

        ClassGroup otherClassGroup = (ClassGroup) other;
        return otherClassGroup.getId().equals(this.getId())
                && otherClassGroup.getName().equals(this.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.name);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", this.name)
                .add("contactIdSet", this.contactIdSet)
                .toString();
    }
}
