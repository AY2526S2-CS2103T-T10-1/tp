package cpp.model;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cpp.commons.core.GuiSettings;
import cpp.model.assignment.ContactAssignment;
import cpp.model.assignment.exceptions.ContactAlreadyAllocatedAssignmentException;
import cpp.model.assignment.exceptions.ContactAssignmentNotFoundException;
import cpp.model.classgroup.ClassGroup;
import cpp.model.classgroup.exceptions.DuplicateClassGroupException;
import cpp.model.contact.ContactNameContainsKeywordsPredicate;
import cpp.testutil.AddressBookBuilder;
import cpp.testutil.Assert;
import cpp.testutil.ClassGroupBuilder;
import cpp.testutil.TypicalAssignments;
import cpp.testutil.TypicalClassGroups;
import cpp.testutil.TypicalContacts;

public class ModelManagerTest {

    private ModelManager modelManager = new ModelManager();

    @Test
    public void constructor() {
        Assertions.assertEquals(new UserPrefs(), this.modelManager.getUserPrefs());
        Assertions.assertEquals(new GuiSettings(), this.modelManager.getGuiSettings());
        Assertions.assertEquals(new AddressBook(), new AddressBook(this.modelManager.getAddressBook()));
    }

    @Test
    public void setUserPrefs_nullUserPrefs_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class,
                () -> this.modelManager.setUserPrefs(null));
    }

    @Test
    public void setUserPrefs_validUserPrefs_copiesUserPrefs() {
        UserPrefs userPrefs = new UserPrefs();
        userPrefs.setAddressBookFilePath(Paths.get("address/book/file/path"));
        userPrefs.setGuiSettings(new GuiSettings(1, 2, 3, 4));
        this.modelManager.setUserPrefs(userPrefs);
        Assertions.assertEquals(userPrefs, this.modelManager.getUserPrefs());

        // Modifying userPrefs should not modify modelManager's userPrefs
        UserPrefs oldUserPrefs = new UserPrefs(userPrefs);
        userPrefs.setAddressBookFilePath(Paths.get("new/address/book/file/path"));
        Assertions.assertEquals(oldUserPrefs, this.modelManager.getUserPrefs());
    }

    @Test
    public void setGuiSettings_nullGuiSettings_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class,
                () -> this.modelManager.setGuiSettings(null));
    }

    @Test
    public void setGuiSettings_validGuiSettings_setsGuiSettings() {
        GuiSettings guiSettings = new GuiSettings(1, 2, 3, 4);
        this.modelManager.setGuiSettings(guiSettings);
        Assertions.assertEquals(guiSettings, this.modelManager.getGuiSettings());
    }

    @Test
    public void setAddressBookFilePath_nullPath_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class,
                () -> this.modelManager.setAddressBookFilePath(null));
    }

    @Test
    public void setAddressBookFilePath_validPath_setsAddressBookFilePath() {
        Path path = Paths.get("address/book/file/path");
        this.modelManager.setAddressBookFilePath(path);
        Assertions.assertEquals(path, this.modelManager.getAddressBookFilePath());
    }

    @Test
    public void hasContact_nullContact_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class, () -> this.modelManager.hasContact(null));
    }

    @Test
    public void hasContact_contactNotInAddressBook_returnsFalse() {
        Assertions.assertFalse(this.modelManager.hasContact(TypicalContacts.ALICE));
    }

    @Test
    public void hasContact_contactInAddressBook_returnsTrue() {
        this.modelManager.addContact(TypicalContacts.ALICE);
        Assertions.assertTrue(this.modelManager.hasContact(TypicalContacts.ALICE));
    }

    @Test
    public void hasAssignment_nullAssignment_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class,
                () -> this.modelManager.hasAssignment(null));
    }

    @Test
    public void hasAssignment_assignmentNotInAddressBook_returnsFalse() {
        Assertions.assertFalse(this.modelManager.hasAssignment(TypicalAssignments.ASSIGNMENT_ONE));
    }

    @Test
    public void hasAssignment_assignmentInAddressBook_returnsTrue() {
        this.modelManager.addAssignment(TypicalAssignments.ASSIGNMENT_ONE);
        Assertions.assertTrue(this.modelManager.hasAssignment(TypicalAssignments.ASSIGNMENT_ONE));
    }

    @Test
    public void addAssignment_nullAssignment_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class,
                () -> this.modelManager.addAssignment(null));
    }

    @Test
    public void addAssignment_validAssignment_addSuccessful() {
        this.modelManager.addAssignment(TypicalAssignments.ASSIGNMENT_ONE);
        Assertions.assertTrue(this.modelManager.hasAssignment(TypicalAssignments.ASSIGNMENT_ONE));
    }

    @Test
    public void deleteContact_contactInAddressBook_removesContact() {
        this.modelManager.addContact(TypicalContacts.ALICE);
        Assertions.assertTrue(this.modelManager.hasContact(TypicalContacts.ALICE));
        this.modelManager.deleteContact(TypicalContacts.ALICE);
        Assertions.assertFalse(this.modelManager.hasContact(TypicalContacts.ALICE));
    }

    @Test
    public void deleteAssignment_assignmentInAddressBook_removesAssignment() {
        this.modelManager.addAssignment(TypicalAssignments.ASSIGNMENT_ONE);
        Assertions.assertTrue(this.modelManager.hasAssignment(TypicalAssignments.ASSIGNMENT_ONE));
        this.modelManager.deleteAssignment(TypicalAssignments.ASSIGNMENT_ONE);
        Assertions.assertFalse(this.modelManager.hasAssignment(TypicalAssignments.ASSIGNMENT_ONE));
    }

    @Test
    public void addContactAssignment_validContactAssignment_addSuccessful() {
        ContactAssignment ca = new ContactAssignment(TypicalAssignments.ASSIGNMENT_ONE.getId(),
                TypicalContacts.ALICE.getId(), false, null, false, null, 0);
        this.modelManager.addContact(TypicalContacts.ALICE);
        this.modelManager.addAssignment(TypicalAssignments.ASSIGNMENT_ONE);
        this.modelManager.addContactAssignment(ca);
        Assertions.assertTrue(this.modelManager.getAddressBook().getContactAssignmentList().contains(ca));
    }

    @Test
    public void addContactAssignment_duplicateContactAssignment_throwsIllegalArgumentException() {
        ContactAssignment ca = new ContactAssignment(TypicalAssignments.ASSIGNMENT_ONE.getId(),
                TypicalContacts.ALICE.getId(), false, null, false, null, 0);
        this.modelManager.addContact(TypicalContacts.ALICE);
        this.modelManager.addAssignment(TypicalAssignments.ASSIGNMENT_ONE);
        this.modelManager.addContactAssignment(ca);
        Assert.assertThrows(
                ContactAlreadyAllocatedAssignmentException.class,
                () -> this.modelManager.addContactAssignment(ca));
    }

    @Test
    public void addContactAssignment_nullContactAssignment_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class,
                () -> this.modelManager.addContactAssignment(null));
    }

    @Test
    public void removeContactAssignment_validContactAssignment_removeSuccessful() {
        ContactAssignment ca = new ContactAssignment(TypicalAssignments.ASSIGNMENT_ONE.getId(),
                TypicalContacts.ALICE.getId(), false, null, false, null, 0);
        this.modelManager.addContact(TypicalContacts.ALICE);
        this.modelManager.addAssignment(TypicalAssignments.ASSIGNMENT_ONE);
        this.modelManager.addContactAssignment(ca);
        Assertions.assertTrue(this.modelManager.getAddressBook().getContactAssignmentList().contains(ca));
        this.modelManager.removeContactAssignment(ca);
        Assertions.assertFalse(this.modelManager.getAddressBook().getContactAssignmentList().contains(ca));
    }

    @Test
    public void removeContactAssignment_nonExistentContactAssignment_throwsIllegalArgumentException() {
        ContactAssignment ca = new ContactAssignment(TypicalAssignments.ASSIGNMENT_ONE.getId(),
                TypicalContacts.ALICE.getId(), false, null, false, null, 0);
        Assert.assertThrows(ContactAssignmentNotFoundException.class,
                () -> this.modelManager.removeContactAssignment(ca));
    }

    @Test
    public void removeContactAssignment_nullContactAssignment_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class,
                () -> this.modelManager.removeContactAssignment(null));
    }

    @Test
    public void markSubmitted_validContactAssignment_markSuccessful() {
        ContactAssignment ca = new ContactAssignment(TypicalAssignments.ASSIGNMENT_ONE.getId(),
                TypicalContacts.ALICE.getId(), false, null, false, null, 0);
        this.modelManager.addContact(TypicalContacts.ALICE);
        this.modelManager.addAssignment(TypicalAssignments.ASSIGNMENT_ONE);
        this.modelManager.addContactAssignment(ca);
        Assertions.assertFalse(ca.isSubmitted());
        this.modelManager.markSubmitted(TypicalAssignments.ASSIGNMENT_ONE, TypicalContacts.ALICE, LocalDateTime.now());
        Assertions.assertTrue(ca.isSubmitted());
    }

    @Test
    public void markUnsubmitted_validContactAssignment_markSuccessful() {
        ContactAssignment ca = new ContactAssignment(TypicalAssignments.ASSIGNMENT_ONE.getId(),
                TypicalContacts.ALICE.getId(), true, LocalDateTime.now(), false, null, 0);
        this.modelManager.addContact(TypicalContacts.ALICE);
        this.modelManager.addAssignment(TypicalAssignments.ASSIGNMENT_ONE);
        this.modelManager.addContactAssignment(ca);
        Assertions.assertTrue(ca.isSubmitted());
        this.modelManager.markUnsubmitted(TypicalAssignments.ASSIGNMENT_ONE, TypicalContacts.ALICE);
        Assertions.assertFalse(ca.isSubmitted());
    }

    @Test
    public void grade() {
        ContactAssignment ca = new ContactAssignment(TypicalAssignments.ASSIGNMENT_ONE.getId(),
                TypicalContacts.ALICE.getId(), true, LocalDateTime.now(), false, null, 0);
        this.modelManager.addContact(TypicalContacts.ALICE);
        this.modelManager.addAssignment(TypicalAssignments.ASSIGNMENT_ONE);
        this.modelManager.addContactAssignment(ca);
        Assertions.assertFalse(ca.isGraded());
        Assertions.assertEquals(0, ca.getScore());
        this.modelManager.grade(TypicalAssignments.ASSIGNMENT_ONE, TypicalContacts.ALICE, 85, LocalDateTime.now());
        Assertions.assertTrue(ca.isGraded());
        Assertions.assertEquals(85, ca.getScore());
    }

    @Test
    public void ungrade() {
        ContactAssignment ca = new ContactAssignment(TypicalAssignments.ASSIGNMENT_ONE.getId(),
                TypicalContacts.ALICE.getId(), true, LocalDateTime.now(), true, LocalDateTime.now(), 85);
        this.modelManager.addContact(TypicalContacts.ALICE);
        this.modelManager.addAssignment(TypicalAssignments.ASSIGNMENT_ONE);
        this.modelManager.addContactAssignment(ca);
        Assertions.assertTrue(ca.isGraded());
        Assertions.assertEquals(85, ca.getScore());
        this.modelManager.ungrade(TypicalAssignments.ASSIGNMENT_ONE, TypicalContacts.ALICE);
        Assertions.assertFalse(ca.isGraded());
        Assertions.assertEquals(0, ca.getScore());
    }

    @Test
    public void hasClassGroup_classGroupNotInAddressBook_returnsFalse() {
        Assertions.assertFalse(this.modelManager.hasClassGroup(TypicalClassGroups.CLASS_GROUP_ONE));
    }

    @Test
    public void hasClassGroup_classGroupInAddressBook_returnsTrue() {
        this.modelManager.addClassGroup(TypicalClassGroups.CLASS_GROUP_ONE);
        Assertions.assertTrue(this.modelManager.hasClassGroup(TypicalClassGroups.CLASS_GROUP_ONE));
    }

    @Test
    public void addClassGroup_nullClassGroup_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class,
                () -> this.modelManager.addClassGroup(null));
    }

    @Test
    public void addClassGroup_validClassGroup_addSuccessful() {
        this.modelManager.addClassGroup(TypicalClassGroups.CLASS_GROUP_ONE);
        Assertions.assertTrue(this.modelManager.hasClassGroup(TypicalClassGroups.CLASS_GROUP_ONE));
    }

    @Test
    public void addClassGroup_duplicateName_throwsDuplicateClassGroupException() {
        this.modelManager.addClassGroup(TypicalClassGroups.CLASS_GROUP_ONE);
        ClassGroup duplicateNameDifferentId = new ClassGroupBuilder(TypicalClassGroups.CLASS_GROUP_ONE)
                .withId("aaaaaaaa-1111-2222-3333-bbbbbbbbbbb9")
                .build();
        Assert.assertThrows(DuplicateClassGroupException.class,
                () -> this.modelManager.addClassGroup(duplicateNameDifferentId));
    }

    @Test
    public void setClassGroup_nullTarget_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class,
                () -> this.modelManager.setClassGroup(null, TypicalClassGroups.CLASS_GROUP_ONE));
    }

    @Test
    public void setClassGroup_nullEditedClassGroup_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class,
                () -> this.modelManager.setClassGroup(TypicalClassGroups.CLASS_GROUP_ONE, null));
    }

    @Test
    public void setClassGroup_validClassGroup_updatesClassGroup() {
        this.modelManager.addClassGroup(TypicalClassGroups.CLASS_GROUP_ONE);

        ClassGroup edited = new ClassGroupBuilder(TypicalClassGroups.CLASS_GROUP_ONE)
                .withId("aaaaaaaa-1111-2222-3333-bbbbbbbbbbb8")
                .build();
        this.modelManager.setClassGroup(TypicalClassGroups.CLASS_GROUP_ONE, edited);

        Assertions.assertFalse(this.modelManager.getAddressBook().getClassGroupList()
                .contains(TypicalClassGroups.CLASS_GROUP_ONE));
        Assertions.assertTrue(this.modelManager.getAddressBook().getClassGroupList().contains(edited));
    }

    @Test
    public void deleteClassGroup_classGroupInAddressBook_removesClassGroup() {
        this.modelManager.addClassGroup(TypicalClassGroups.CLASS_GROUP_ONE);
        Assertions.assertTrue(this.modelManager.getAddressBook().getClassGroupList()
                .contains(TypicalClassGroups.CLASS_GROUP_ONE));

        this.modelManager.deleteClassGroup(TypicalClassGroups.CLASS_GROUP_ONE);
        Assertions.assertFalse(this.modelManager.getAddressBook().getClassGroupList()
                .contains(TypicalClassGroups.CLASS_GROUP_ONE));
        Assertions.assertFalse(this.modelManager.hasClassGroup(TypicalClassGroups.CLASS_GROUP_ONE));
    }

    @Test
    public void getFilteredContactList_modifyList_throwsUnsupportedOperationException() {
        Assert.assertThrows(UnsupportedOperationException.class,
                () -> this.modelManager.getFilteredContactList().remove(0));
    }

    @Test
    public void getFilteredAssignmentList_modifyList_throwsUnsupportedOperationException() {
        Assert.assertThrows(UnsupportedOperationException.class,
                () -> this.modelManager.getFilteredAssignmentList().remove(0));
    }

    @Test
    public void getFilteredClassGroupList_modifyList_throwsUnsupportedOperationException() {
        Assert.assertThrows(UnsupportedOperationException.class,
                () -> this.modelManager.getFilteredClassGroupList().remove(0));
    }

    @Test
    public void updateFilteredAssignmentList_validPredicate_filtersAssignments() {
        this.modelManager.addAssignment(TypicalAssignments.ASSIGNMENT_ONE);
        // This would need an assignment predicate - for now just test it doesn't throw
        Assertions.assertNotNull(this.modelManager.getFilteredAssignmentList());
    }

    @Test
    public void updateFilteredClassGroupList_validPredicate_filtersClasses() {
        // This would need a class group - for now just test it doesn't throw
        Assertions.assertNotNull(this.modelManager.getFilteredClassGroupList());
    }

    @Test
    public void equals() {
        AddressBook addressBook = new AddressBookBuilder().withContact(TypicalContacts.ALICE)
                .withContact(TypicalContacts.BENSON).withAssignment(TypicalAssignments.ASSIGNMENT_ONE).build();
        AddressBook differentAddressBook1 = new AddressBookBuilder().withContact(TypicalContacts.ALICE)
                .withContact(TypicalContacts.BENSON).build();
        AddressBook differentAddressBook2 = new AddressBookBuilder().withAssignment(TypicalAssignments.ASSIGNMENT_ONE)
                .build();
        UserPrefs userPrefs = new UserPrefs();

        // same values -> returns true
        this.modelManager = new ModelManager(addressBook, userPrefs);
        ModelManager modelManagerCopy = new ModelManager(addressBook, userPrefs);
        Assertions.assertTrue(this.modelManager.equals(modelManagerCopy));

        // same object -> returns true
        Assertions.assertTrue(this.modelManager.equals(this.modelManager));

        // null -> returns false
        Assertions.assertFalse(this.modelManager.equals(null));

        // different types -> returns false
        Assertions.assertFalse(this.modelManager.equals(5));

        // different addressBook -> returns false
        Assertions.assertFalse(this.modelManager.equals(new ModelManager(differentAddressBook1, userPrefs)));
        Assertions.assertFalse(this.modelManager.equals(new ModelManager(differentAddressBook2, userPrefs)));

        // different filteredList -> returns false
        String[] keywords = TypicalContacts.ALICE.getName().fullName.split("\\s+");
        this.modelManager.updateFilteredContactList(new ContactNameContainsKeywordsPredicate(Arrays.asList(keywords)));
        Assertions.assertFalse(this.modelManager.equals(new ModelManager(addressBook, userPrefs)));

        // resets modelManager to initial state for upcoming tests
        this.modelManager.updateFilteredContactList(Model.PREDICATE_SHOW_ALL_CONTACTS);

        // different userPrefs -> returns false
        UserPrefs differentUserPrefs = new UserPrefs();
        differentUserPrefs.setAddressBookFilePath(Paths.get("differentFilePath"));
        Assertions.assertFalse(this.modelManager.equals(new ModelManager(addressBook, differentUserPrefs)));
    }
}
