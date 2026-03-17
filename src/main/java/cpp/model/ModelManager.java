package cpp.model;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.logging.Logger;

import cpp.commons.core.GuiSettings;
import cpp.commons.core.LogsCenter;
import cpp.commons.util.CollectionUtil;
import cpp.model.assignment.Assignment;
import cpp.model.assignment.AssignmentManager;
import cpp.model.assignment.ContactAssignment;
import cpp.model.assignment.exceptions.ContactAlreadyAllocatedAssignmentException;
import cpp.model.assignment.exceptions.ContactAssignmentNotFoundException;
import cpp.model.classgroup.ClassGroup;
import cpp.model.contact.Contact;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

/**
 * Represents the in-memory model of the address book data.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final AddressBook addressBook;
    private final AssignmentManager assignmentManager;
    private final UserPrefs userPrefs;
    private final FilteredList<Contact> filteredContacts;
    private final FilteredList<Assignment> filteredAssignments;
    private final FilteredList<ClassGroup> filteredClassGroups;

    /**
     * Initializes a ModelManager with the given addressBook and userPrefs.
     */
    public ModelManager(ReadOnlyAddressBook addressBook, ReadOnlyUserPrefs userPrefs) {
        CollectionUtil.requireAllNonNull(addressBook, userPrefs);

        ModelManager.logger.fine("Initializing with address book: " + addressBook + " and user prefs " + userPrefs);

        this.addressBook = new AddressBook(addressBook);
        this.assignmentManager = new AssignmentManager(addressBook.getContactAssignmentList());
        this.userPrefs = new UserPrefs(userPrefs);
        this.filteredContacts = new FilteredList<>(this.addressBook.getContactList());
        this.filteredAssignments = new FilteredList<>(this.addressBook.getAssignmentList());
        this.filteredClassGroups = new FilteredList<>(this.addressBook.getClassGroupList());

        // Initialize with predicates that show all items
        this.filteredContacts.setPredicate(Model.PREDICATE_SHOW_ALL_CONTACTS);
        this.filteredAssignments.setPredicate(Model.PREDICATE_SHOW_ALL_ASSIGNMENTS);
        this.filteredClassGroups.setPredicate(Model.PREDICATE_SHOW_ALL_CLASSGROUPS);
    }

    public ModelManager() {
        this(new AddressBook(), new UserPrefs());
    }

    // =========== UserPrefs
    // ==================================================================================

    @Override
    public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {
        Objects.requireNonNull(userPrefs);
        this.userPrefs.resetData(userPrefs);
    }

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        return this.userPrefs;
    }

    @Override
    public GuiSettings getGuiSettings() {
        return this.userPrefs.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        Objects.requireNonNull(guiSettings);
        this.userPrefs.setGuiSettings(guiSettings);
    }

    @Override
    public Path getAddressBookFilePath() {
        return this.userPrefs.getAddressBookFilePath();
    }

    @Override
    public void setAddressBookFilePath(Path addressBookFilePath) {
        Objects.requireNonNull(addressBookFilePath);
        this.userPrefs.setAddressBookFilePath(addressBookFilePath);
    }

    // =========== AddressBook
    // ================================================================================

    @Override
    public void setAddressBook(ReadOnlyAddressBook addressBook) {
        this.addressBook.resetData(addressBook);
    }

    @Override
    public ReadOnlyAddressBook getAddressBook() {
        return this.addressBook;
    }

    // =========== Contact-level operations
    // ===============================================================================
    @Override
    public boolean hasContact(Contact contact) {
        Objects.requireNonNull(contact);
        return this.addressBook.hasContact(contact);
    }

    @Override
    public void deleteContact(Contact target) {
        Objects.requireNonNull(target);
        List<ContactAssignment> caList = this.assignmentManager.getContactAssignmentsForContact(target);
        this.assignmentManager.deregisterContactAssignmentsForContact(target);
        this.addressBook.removeContact(target, caList);
    }

    @Override
    public void addContact(Contact contact) {
        Objects.requireNonNull(contact);
        this.addressBook.addContact(contact);
        this.updateFilteredContactList(Model.PREDICATE_SHOW_ALL_CONTACTS);
    }

    @Override
    public void setContact(Contact target, Contact editedContact) {
        CollectionUtil.requireAllNonNull(target, editedContact);
        this.addressBook.setContact(target, editedContact);
    }

    // =========== Assignment-level operations
    // ===============================================================================
    @Override
    public boolean hasAssignment(Assignment assignment) {
        Objects.requireNonNull(assignment);
        return this.addressBook.hasAssignment(assignment);
    }

    @Override
    public void addAssignment(Assignment assignment) {
        Objects.requireNonNull(assignment);
        this.addressBook.addAssignment(assignment);
    }

    @Override
    public void deleteAssignment(Assignment target) {
        Objects.requireNonNull(target);
        List<ContactAssignment> caList = this.assignmentManager.getContactAssignmentsForAssignment(target);
        this.assignmentManager.deregisterContactAssignmentsForAssignment(target);
        this.addressBook.removeAssignment(target, caList);
    }

    @Override
    public void addContactAssignment(ContactAssignment ca) {
        Objects.requireNonNull(ca);
        if (this.addressBook.hasContactAssignment(ca)) {
            throw new ContactAlreadyAllocatedAssignmentException();
        }

        this.assignmentManager.registerContactAssignment(ca);
        this.addressBook.addContactAssignment(ca);
    }

    @Override
    public void removeContactAssignment(ContactAssignment ca) {
        Objects.requireNonNull(ca);
        if (!this.addressBook.hasContactAssignment(ca)) {
            throw new ContactAssignmentNotFoundException();
        }

        this.assignmentManager.deregisterContactAssignment(ca);
        this.addressBook.removeContactAssignment(ca);
    }

    @Override
    public boolean hasClassGroup(ClassGroup classGroup) {
        Objects.requireNonNull(classGroup);
        return this.addressBook.hasClassGroup(classGroup);
    }

    @Override
    public void addClassGroup(ClassGroup classGroup) {
        Objects.requireNonNull(classGroup);
        this.addressBook.addClassGroup(classGroup);
    }

    @Override
    public void setClassGroup(ClassGroup target, ClassGroup editedClassGroup) {
        CollectionUtil.requireAllNonNull(target, editedClassGroup);
        this.addressBook.setClassGroup(target, editedClassGroup);
    }

    @Override
    public void deleteClassGroup(ClassGroup target) {
        this.addressBook.removeClassGroup(target);
    }

    // =========== Filtered Contact List Accessors
    // =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Contact} backed by the
     * internal list of
     * {@code versionedAddressBook}
     */
    @Override
    public ObservableList<Contact> getFilteredContactList() {
        return this.filteredContacts;
    }

    @Override
    public void updateFilteredContactList(Predicate<Contact> predicate) {
        Objects.requireNonNull(predicate);
        this.filteredContacts.setPredicate(predicate);
    }

    @Override
    public ObservableList<Assignment> getFilteredAssignmentList() {
        return this.filteredAssignments;
    }

    @Override
    public void updateFilteredAssignmentList(Predicate<Assignment> predicate) {
        Objects.requireNonNull(predicate);
        this.filteredAssignments.setPredicate(predicate);
    }

    @Override
    public ObservableList<ClassGroup> getFilteredClassGroupList() {
        return this.filteredClassGroups;
    }

    @Override
    public void updateFilteredClassGroupList(Predicate<ClassGroup> predicate) {
        Objects.requireNonNull(predicate);
        this.filteredClassGroups.setPredicate(predicate);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ModelManager)) {
            return false;
        }

        ModelManager otherModelManager = (ModelManager) other;
        return this.addressBook.equals(otherModelManager.addressBook)
                && this.userPrefs.equals(otherModelManager.userPrefs)
                && this.filteredContacts.equals(otherModelManager.filteredContacts)
                && this.filteredAssignments.equals(otherModelManager.filteredAssignments)
                && this.filteredClassGroups.equals(otherModelManager.filteredClassGroups);
    }

}
