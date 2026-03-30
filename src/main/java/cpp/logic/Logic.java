package cpp.logic;

import java.nio.file.Path;
import java.util.List;

import cpp.commons.core.GuiSettings;
import cpp.logic.commands.CommandResult;
import cpp.logic.commands.exceptions.CommandException;
import cpp.logic.parser.exceptions.ParseException;
import cpp.model.ReadOnlyAddressBook;
import cpp.model.assignment.Assignment;
import cpp.model.assignment.ContactAssignmentWithAssignment;
import cpp.model.assignment.ContactAssignmentWithContact;
import cpp.model.classgroup.ClassGroup;
import cpp.model.contact.Contact;
import cpp.model.view.ViewState;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;

/**
 * API of the Logic component
 */
public interface Logic {
    /**
     * Executes the command and returns the result.
     *
     * @param commandText The command as entered by the user.
     * @return the result of the command execution.
     * @throws CommandException If an error occurs during command execution.
     * @throws ParseException   If an error occurs during parsing.
     */
    CommandResult execute(String commandText) throws CommandException, ParseException;

    /**
     * Returns the AddressBook.
     *
     * @see cpp.model.Model#getAddressBook()
     */
    ReadOnlyAddressBook getAddressBook();

    /** Returns an unmodifiable view of the filtered list of contacts */
    ObservableList<Contact> getFilteredContactList();

    /** Returns an unmodifiable view of the filtered list of assignments */
    ObservableList<Assignment> getFilteredAssignmentList();

    /** Returns an unmodifiable view of the filtered list of class groups */
    ObservableList<ClassGroup> getFilteredClassGroupList();

    /**
     * Returns the user prefs' address book file path.
     */
    Path getAddressBookFilePath();

    /**
     * Returns the user prefs' GUI settings.
     */
    GuiSettings getGuiSettings();

    /**
     * Set the user prefs' GUI settings.
     */
    void setGuiSettings(GuiSettings guiSettings);

    /**
     * Returns a list of contact-assignment DTOs pairing contact-assignment and
     * contact for the given assignment.
     */
    List<ContactAssignmentWithContact> getContactAssignmentsWithContactsForAssignment(
            Assignment assignment);

    /**
     * Returns a list of contact-assignment DTOs pairing contact-assignment and
     * assignment for the given contact.
     */
    List<ContactAssignmentWithAssignment> getContactAssignmentsWithAssignmentsForContact(
            Contact contact);

    /**
     * Returns class groups containing the given contact.
     */
    List<ClassGroup> getClassGroupsForContact(Contact contact);

    /**
     * Clears the currently viewed assignment, if any.
     */
    void clearViewState();

    /**
     * Returns the central view state property that the UI can listen to for
     * reactive updates.
     */
    ReadOnlyObjectProperty<ViewState> getViewStateProperty();
}
