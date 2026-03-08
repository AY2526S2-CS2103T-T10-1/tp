package cpp.model.contact;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import cpp.commons.util.CollectionUtil;
import cpp.model.contact.exceptions.ContactNotFoundException;
import cpp.model.contact.exceptions.DuplicateContactException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * A list of contacts that enforces uniqueness between its elements and does not
 * allow nulls.
 * A contact is considered unique by comparing using
 * {@code Contact#isSameContact(Contact)}. As such, adding and updating of
 * contacts uses Contact#isSameContact(Contact) for equality so as to ensure
 * that
 * the contact being added or updated is
 * unique in terms of identity in the UniqueContactList. However, the removal of
 * a contact uses Contact#equals(Object) so
 * as to ensure that the contact with exactly the same fields will be removed.
 *
 * Supports a minimal set of list operations.
 *
 * @see Contact#isSameContact(Contact)
 */
public class UniqueContactList implements Iterable<Contact> {

    private final ObservableList<Contact> internalList = FXCollections.observableArrayList();
    private final ObservableList<Contact> internalUnmodifiableList = FXCollections
            .unmodifiableObservableList(this.internalList);

    /**
     * Returns true if the list contains an equivalent contact as the given
     * argument.
     */
    public boolean contains(Contact toCheck) {
        Objects.requireNonNull(toCheck);
        return this.internalList.stream().anyMatch(toCheck::isSameContact);
    }

    /**
     * Adds a contact to the list.
     * The contact must not already exist in the list.
     */
    public void add(Contact toAdd) {
        Objects.requireNonNull(toAdd);
        if (this.contains(toAdd)) {
            throw new DuplicateContactException();
        }
        this.internalList.add(toAdd);
    }

    /**
     * Replaces the contact {@code target} in the list with {@code editedContact}.
     * {@code target} must exist in the list.
     * The contact identity of {@code editedContact} must not be the same as another
     * existing contact in the list.
     */
    public void setContact(Contact target, Contact editedContact) {
        CollectionUtil.requireAllNonNull(target, editedContact);

        int index = this.internalList.indexOf(target);
        if (index == -1) {
            throw new ContactNotFoundException();
        }

        if (!target.isSameContact(editedContact) && this.contains(editedContact)) {
            throw new DuplicateContactException();
        }

        this.internalList.set(index, editedContact);
    }

    /**
     * Removes the equivalent contact from the list.
     * The contact must exist in the list.
     */
    public void remove(Contact toRemove) {
        Objects.requireNonNull(toRemove);
        if (!this.internalList.remove(toRemove)) {
            throw new ContactNotFoundException();
        }
    }

    public void setContacts(UniqueContactList replacement) {
        Objects.requireNonNull(replacement);
        this.internalList.setAll(replacement.internalList);
    }

    /**
     * Replaces the contents of this list with {@code contacts}.
     * {@code contacts} must not contain duplicate contacts.
     */
    public void setContacts(List<Contact> contacts) {
        CollectionUtil.requireAllNonNull(contacts);
        if (!this.contactsAreUnique(contacts)) {
            throw new DuplicateContactException();
        }

        this.internalList.setAll(contacts);
    }

    /**
     * Returns the backing list as an unmodifiable {@code ObservableList}.
     */
    public ObservableList<Contact> asUnmodifiableObservableList() {
        return this.internalUnmodifiableList;
    }

    @Override
    public Iterator<Contact> iterator() {
        return this.internalList.iterator();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof UniqueContactList)) {
            return false;
        }

        UniqueContactList otherUniqueContactList = (UniqueContactList) other;
        return this.internalList.equals(otherUniqueContactList.internalList);
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
     * Returns true if {@code contacts} contains only unique contacts.
     */
    private boolean contactsAreUnique(List<Contact> contacts) {
        for (int i = 0; i < contacts.size() - 1; i++) {
            for (int j = i + 1; j < contacts.size(); j++) {
                if (contacts.get(i).isSameContact(contacts.get(j))) {
                    return false;
                }
            }
        }
        return true;
    }
}
