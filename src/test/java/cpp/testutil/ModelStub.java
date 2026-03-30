package cpp.testutil;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Predicate;

import cpp.commons.core.GuiSettings;
import cpp.model.Model;
import cpp.model.ReadOnlyAddressBook;
import cpp.model.ReadOnlyUserPrefs;
import cpp.model.assignment.Assignment;
import cpp.model.assignment.ContactAssignment;
import cpp.model.assignment.ContactAssignmentWithAssignment;
import cpp.model.assignment.ContactAssignmentWithContact;
import cpp.model.classgroup.ClassGroup;
import cpp.model.contact.Contact;
import cpp.model.view.ViewState;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;

/**
 * A default model stub that has all of the methods failing.
 */
public class ModelStub implements Model {
    @Override
    public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public GuiSettings getGuiSettings() {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public Path getAddressBookFilePath() {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public void setAddressBookFilePath(Path addressBookFilePath) {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public void addContact(Contact contact) {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public void setAddressBook(ReadOnlyAddressBook newData) {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public ReadOnlyAddressBook getAddressBook() {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public boolean hasContact(Contact contact) {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public void deleteContact(Contact target) {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public void setContact(Contact target, Contact editedContact) {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public ObservableList<Contact> getFilteredContactList() {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public void updateFilteredContactList(Predicate<Contact> predicate) {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public boolean hasAssignment(Assignment assignment) {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public void addAssignment(Assignment assignment) {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public void deleteAssignment(Assignment target) {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public void addContactAssignment(ContactAssignment ca) {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public void removeContactAssignment(ContactAssignment ca) {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public void markSubmitted(Assignment assignment, Contact contact, LocalDateTime submissionDate) {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public void markUnsubmitted(Assignment assignment, Contact contact) {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public void grade(Assignment assignment, Contact contact, float score, LocalDateTime gradingDate) {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public void ungrade(Assignment assignment, Contact contact) {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public List<ContactAssignment> getContactAssignmentsForContact(Contact contact) {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public boolean hasClassGroup(ClassGroup classGroup) {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public void addClassGroup(ClassGroup classGroup) {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public void setClassGroup(ClassGroup target, ClassGroup editedClassGroup) {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public void deleteClassGroup(ClassGroup target) {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public ObservableList<Assignment> getFilteredAssignmentList() {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public void updateFilteredAssignmentList(Predicate<Assignment> predicate) {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public ObservableList<ClassGroup> getFilteredClassGroupList() {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public void updateFilteredClassGroupList(Predicate<ClassGroup> predicate) {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public void viewAssignment(Assignment assignment) {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public void viewContact(Contact contact) {
        throw new AssertionError("This method should not be called.");
    }

    /** Clears any currently viewed assignment. */
    @Override
    public void clearViewState() {
        throw new AssertionError("This method should not be called.");
    }

    /** Returns the contact assignments for the given assignment. */
    @Override
    public List<ContactAssignmentWithContact> getContactAssignmentsWithContactsForAssignment(
            Assignment assignment) {
        throw new AssertionError("This method should not be called.");
    }

    /** Returns the contact assignments for the given contact. */
    @Override
    public List<ContactAssignmentWithAssignment> getContactAssignmentsWithAssignmentsForContact(
            Contact contact) {
        throw new AssertionError("This method should not be called.");
    }

    /** Returns the class groups containing the given contact. */
    @Override
    public List<ClassGroup> getClassGroupsForContact(Contact contact) {
        throw new AssertionError("This method should not be called.");
    }

    /** Observable property for the current view state. */
    @Override
    public ReadOnlyObjectProperty<ViewState> getViewStateProperty() {
        throw new AssertionError("This method should not be called.");
    }
}
