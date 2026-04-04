package cpp.logic;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;

import cpp.commons.core.GuiSettings;
import cpp.commons.core.LogsCenter;
import cpp.logic.commands.Command;
import cpp.logic.commands.CommandResult;
import cpp.logic.commands.exceptions.CommandException;
import cpp.logic.parser.AddressBookParser;
import cpp.logic.parser.exceptions.ParseException;
import cpp.model.Model;
import cpp.model.ReadOnlyAddressBook;
import cpp.model.assignment.Assignment;
import cpp.model.assignment.ContactAssignmentWithAssignment;
import cpp.model.assignment.ContactAssignmentWithContact;
import cpp.model.classgroup.ClassGroup;
import cpp.model.contact.Contact;
import cpp.model.view.ViewState;
import cpp.storage.Storage;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;

/**
 * The main LogicManager of the app.
 */
public class LogicManager implements Logic {
    public static final String FILE_OPS_ERROR_FORMAT = "Could not save data due to the following error: %s";

    public static final String FILE_OPS_PERMISSION_ERROR_FORMAT = """
            Could not save data to file %s due to insufficient permissions to write to the file or the folder.""";

    private final Logger logger = LogsCenter.getLogger(LogicManager.class);

    private final Model model;
    private final Storage storage;
    private final AddressBookParser addressBookParser;

    /**
     * Constructs a {@code LogicManager} with the given {@code Model} and
     * {@code Storage}.
     */
    public LogicManager(Model model, Storage storage) {
        this.model = model;
        this.storage = storage;
        this.addressBookParser = new AddressBookParser();
    }

    @Override
    public CommandResult execute(String commandText) throws CommandException, ParseException {
        this.logger.info("----------------[USER COMMAND][" + commandText + "]");

        CommandResult commandResult;
        Command command = this.addressBookParser.parseCommand(commandText);
        commandResult = command.execute(this.model);

        try {
            this.storage.saveAddressBook(this.model.getAddressBook());
        } catch (AccessDeniedException e) {
            throw new CommandException(String.format(LogicManager.FILE_OPS_PERMISSION_ERROR_FORMAT, e.getMessage()), e);
        } catch (IOException ioe) {
            throw new CommandException(String.format(LogicManager.FILE_OPS_ERROR_FORMAT, ioe.getMessage()), ioe);
        }

        return commandResult;
    }

    @Override
    public ReadOnlyAddressBook getAddressBook() {
        return this.model.getAddressBook();
    }

    @Override
    public ObservableList<Contact> getFilteredContactList() {
        return this.model.getFilteredContactList();
    }

    @Override
    public ObservableList<Assignment> getFilteredAssignmentList() {
        return this.model.getFilteredAssignmentList();
    }

    @Override
    public ObservableList<ClassGroup> getFilteredClassGroupList() {
        return this.model.getFilteredClassGroupList();
    }

    @Override
    public Path getAddressBookFilePath() {
        return this.model.getAddressBookFilePath();
    }

    @Override
    public GuiSettings getGuiSettings() {
        return this.model.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        this.model.setGuiSettings(guiSettings);
    }

    @Override
    public List<ContactAssignmentWithContact> getContactAssignmentsWithContactsForAssignment(
            Assignment assignment) {
        return this.model.getContactAssignmentsWithContactsForAssignment(assignment);
    }

    @Override
    public List<ContactAssignmentWithAssignment> getContactAssignmentsWithAssignmentsForContact(
            Contact contact) {
        return this.model.getContactAssignmentsWithAssignmentsForContact(contact);
    }

    @Override
    public List<ClassGroup> getClassGroupsForContact(Contact contact) {
        return this.model.getClassGroupsForContact(contact);
    }

    @Override
    public List<Contact> getContactsInClassGroup(ClassGroup classGroup) {
        return this.model.getContactsInClassGroup(classGroup);
    }

    @Override
    public void clearViewState() {
        this.model.clearViewState();
    }

    @Override
    public ReadOnlyObjectProperty<ViewState> getViewStateProperty() {
        return this.model.getViewStateProperty();
    }
}
