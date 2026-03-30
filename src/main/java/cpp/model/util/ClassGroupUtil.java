package cpp.model.util;

import java.util.List;

import cpp.model.classgroup.ClassGroup;
import cpp.model.classgroup.ClassGroupName;
import cpp.model.contact.Contact;
import javafx.collections.ObservableList;

/**
 * Utility class for ClassGroup related operations.
 */
public class ClassGroupUtil {

    /**
     * Finds and returns the class group with the given name from the list of class
     * groups.
     * Returns null if no such class group is found.
     */
    public static ClassGroup findClassGroup(List<ClassGroup> classGroups, ClassGroupName name) {
        for (ClassGroup classGroup : classGroups) {
            if (classGroup.getName().equals(name)) {
                return classGroup;
            }
        }
        return null;
    }

    /**
     * Returns a list of {@code Contact} objects that are allocated to the given
     * class group.
     */
    public static List<Contact> getContactsInClassGroup(List<Contact> contacts, ClassGroup classGroup) {
        List<Contact> allContacts = classGroup.getContactIdSet().stream()
                .map(id -> ClassGroupUtil.getSingleContact(contacts, id))
                .toList();
        return allContacts;
    }

    /**
     * Returns all class groups that the given contact belongs to.
     */
    public static List<ClassGroup> getClassGroupsForContact(Contact contact, ObservableList<ClassGroup> classGroups) {
        String contactId = contact.getId();
        return classGroups.stream()
                .filter(classGroup -> classGroup.getContactIdSet().contains(contactId))
                .toList();
    }

    private static Contact getSingleContact(List<Contact> contacts, String id) {
        for (Contact contact : contacts) {
            if (contact.getId().equals(id)) {
                return contact;
            }
        }
        return null;
    }
}
