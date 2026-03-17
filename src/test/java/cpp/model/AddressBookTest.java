package cpp.model;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cpp.logic.commands.CommandTestUtil;
import cpp.model.assignment.Assignment;
import cpp.model.assignment.AssignmentName;
import cpp.model.assignment.ContactAssignment;
import cpp.model.classgroup.ClassGroup;
import cpp.model.contact.Contact;
import cpp.model.contact.exceptions.DuplicateContactException;
import cpp.testutil.Assert;
import cpp.testutil.ContactBuilder;
import cpp.testutil.TypicalContacts;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class AddressBookTest {

    private final AddressBook addressBook = new AddressBook();

    @Test
    public void constructor() {
        Assertions.assertEquals(Collections.emptyList(), this.addressBook.getContactList());
    }

    @Test
    public void resetData_null_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class, () -> this.addressBook.resetData(null));
    }

    @Test
    public void resetData_withValidReadOnlyAddressBook_replacesData() {
        AddressBook newData = TypicalContacts.getTypicalAddressBook();
        this.addressBook.resetData(newData);
        Assertions.assertEquals(newData, this.addressBook);
    }

    @Test
    public void resetData_withDuplicateContacts_throwsDuplicateContactException() {
        // Two contacts with the same identity fields
        Contact editedAlice = new ContactBuilder(TypicalContacts.ALICE).withAddress(CommandTestUtil.VALID_ADDRESS_BOB)
                .withTags(CommandTestUtil.VALID_TAG_HUSBAND)
                .build();
        List<Contact> newContacts = Arrays.asList(TypicalContacts.ALICE, editedAlice);
        AddressBookStub newData = new AddressBookStub(newContacts);

        Assert.assertThrows(DuplicateContactException.class, () -> this.addressBook.resetData(newData));
    }

    @Test
    public void hasContact_nullContact_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class, () -> this.addressBook.hasContact(null));
    }

    @Test
    public void hasContact_contactNotInAddressBook_returnsFalse() {
        Assertions.assertFalse(this.addressBook.hasContact(TypicalContacts.ALICE));
    }

    @Test
    public void hasContact_contactInAddressBook_returnsTrue() {
        this.addressBook.addContact(TypicalContacts.ALICE);
        Assertions.assertTrue(this.addressBook.hasContact(TypicalContacts.ALICE));
    }

    @Test
    public void hasContact_contactWithSameIdentityFieldsInAddressBook_returnsTrue() {
        this.addressBook.addContact(TypicalContacts.ALICE);
        Contact editedAlice = new ContactBuilder(TypicalContacts.ALICE).withAddress(CommandTestUtil.VALID_ADDRESS_BOB)
                .withTags(CommandTestUtil.VALID_TAG_HUSBAND)
                .build();
        Assertions.assertTrue(this.addressBook.hasContact(editedAlice));
    }

    @Test
    public void getContactList_modifyList_throwsUnsupportedOperationException() {
        Assert.assertThrows(UnsupportedOperationException.class,
                () -> this.addressBook.getContactList().remove(0));
    }

    @Test
    public void toStringMethod() {
        String expected = AddressBook.class.getCanonicalName() + "{contacts=" + this.addressBook.getContactList()
                + ", assignments=" + this.addressBook.getAssignmentList()
                + ", classGroups=" + this.addressBook.getClassGroupList() + "}";
        Assertions.assertEquals(expected, this.addressBook.toString());
    }

    @Test
    public void hasAssignment_nullAssignment_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class,
                () -> this.addressBook.hasAssignment(null));
    }

    @Test
    public void hasAssignment_assignmentNotInAddressBook_returnsFalse() {
        Assignment assignment = new Assignment(new AssignmentName("Assignment 1"),
                LocalDateTime.of(2020, 1, 1, 10, 0));
        Assertions.assertFalse(this.addressBook.hasAssignment(assignment));
    }

    @Test
    public void hasAssignment_assignmentInAddressBook_returnsTrue() {
        Assignment assignment = new Assignment(new AssignmentName("Assignment 1"),
                LocalDateTime.of(2020, 1, 1, 10, 0));
        this.addressBook.addAssignment(assignment);
        Assertions.assertTrue(this.addressBook.hasAssignment(assignment));
    }

    @Test
    public void hasAssignment_assignmentWithSameIdentityFieldsInAddressBook_returnsTrue() {
        Assignment assignment = new Assignment(new AssignmentName("Assignment 1"),
                LocalDateTime.of(2020, 1, 1, 10, 0));
        this.addressBook.addAssignment(assignment);
        // Same name (identity), different deadline
        Assignment editedAssignment = new Assignment(new AssignmentName("Assignment 1"),
                LocalDateTime.of(2021, 1, 1, 10, 0));
        Assertions.assertTrue(this.addressBook.hasAssignment(editedAssignment));
    }

    @Test
    public void addAssignment_nullAssignment_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class,
                () -> this.addressBook.addAssignment(null));
    }

    @Test
    public void addAssignment_validAssignment_addSuccessful() {
        Assignment assignment = new Assignment(new AssignmentName("Assignment 1"),
                LocalDateTime.of(2020, 1, 1, 10, 0));
        this.addressBook.addAssignment(assignment);
        Assertions.assertTrue(this.addressBook.hasAssignment(assignment));
    }

    @Test
    public void removeContact_contactInAddressBook_removesContact() {
        this.addressBook.addContact(TypicalContacts.ALICE);
        Assertions.assertTrue(this.addressBook.hasContact(TypicalContacts.ALICE));
        this.addressBook.removeContact(TypicalContacts.ALICE, List.of());
        Assertions.assertFalse(this.addressBook.hasContact(TypicalContacts.ALICE));
    }

    @Test
    public void removeAssignment_assignmentInAddressBook_removesAssignment() {
        Assignment assignment = new Assignment(new AssignmentName("Assignment 1"),
                LocalDateTime.of(2020, 1, 1, 10, 0));
        this.addressBook.addAssignment(assignment);
        Assertions.assertTrue(this.addressBook.hasAssignment(assignment));
        this.addressBook.removeAssignment(assignment, List.of());
        Assertions.assertFalse(this.addressBook.hasAssignment(assignment));
    }

    @Test
    public void equals_sameAddressBooks_returnsTrue() {
        AddressBook addressBook1 = new AddressBook();
        Assertions.assertEquals(addressBook1, addressBook1);
        addressBook1.addContact(TypicalContacts.ALICE);
        Assertions.assertEquals(addressBook1, addressBook1);
    }

    @Test
    public void hashCode_sameAddressBooks_equal() {
        AddressBook addressBook1 = new AddressBook();
        AddressBook addressBook2 = new AddressBook();
        Assertions.assertEquals(addressBook1.hashCode(), addressBook2.hashCode());
        addressBook1.addContact(TypicalContacts.ALICE);
        addressBook2.addContact(TypicalContacts.ALICE);
        Assertions.assertEquals(addressBook1.hashCode(), addressBook2.hashCode());
    }

    @Test
    public void hashCode_differentAddressBooks_notEqual() {
        AddressBook addressBook1 = new AddressBook();
        AddressBook addressBook2 = new AddressBook();

        addressBook1.addContact(TypicalContacts.ALICE);
        Assertions.assertNotEquals(addressBook1.hashCode(), addressBook2.hashCode());

        addressBook2.addContact(TypicalContacts.BOB);
        Assertions.assertNotEquals(addressBook1.hashCode(), addressBook2.hashCode());
    }

    /**
     * A stub ReadOnlyAddressBook whose contacts list can violate interface
     * constraints.
     */
    private static class AddressBookStub implements ReadOnlyAddressBook {
        private final ObservableList<Contact> contacts = FXCollections.observableArrayList();

        AddressBookStub(Collection<Contact> contacts) {
            this.contacts.setAll(contacts);
        }

        @Override
        public ObservableList<Contact> getContactList() {
            return this.contacts;
        }

        @Override
        public ObservableList<Assignment> getAssignmentList() {
            throw new UnsupportedOperationException("Method 'getAssignmentList' should not be called.");
        }

        @Override
        public ObservableList<ContactAssignment> getContactAssignmentList() {
            throw new UnsupportedOperationException("Method 'getContactAssignmentList' should not be called.");
        }

        @Override
        public ObservableList<ClassGroup> getClassGroupList() {
            throw new UnsupportedOperationException("Method 'getClassGroupList' should not be called.");
        }
    }

}
