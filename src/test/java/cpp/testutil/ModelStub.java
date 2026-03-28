package cpp.testutil;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.function.Predicate;

import cpp.commons.core.GuiSettings;
import cpp.model.Model;
import cpp.model.ReadOnlyAddressBook;
import cpp.model.ReadOnlyUserPrefs;
import cpp.model.assignment.Assignment;
import cpp.model.assignment.ContactAssignment;
import cpp.model.classgroup.ClassGroup;
import cpp.model.contact.Contact;
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
    public void setAssignment(Assignment target, Assignment editedAssignment) {
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
}
