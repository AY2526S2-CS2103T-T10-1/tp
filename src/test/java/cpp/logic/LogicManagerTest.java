package cpp.logic;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Path;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import cpp.logic.commands.AddContactCommand;
import cpp.logic.commands.CommandResult;
import cpp.logic.commands.CommandTestUtil;
import cpp.logic.commands.ListCommand;
import cpp.logic.commands.exceptions.CommandException;
import cpp.logic.parser.exceptions.ParseException;
import cpp.model.Model;
import cpp.model.ModelManager;
import cpp.model.ReadOnlyAddressBook;
import cpp.model.UserPrefs;
import cpp.model.contact.Contact;
import cpp.storage.JsonAddressBookStorage;
import cpp.storage.JsonUserPrefsStorage;
import cpp.storage.StorageManager;
import cpp.testutil.Assert;
import cpp.testutil.ContactBuilder;
import cpp.testutil.TypicalContacts;

public class LogicManagerTest {
    private static final IOException DUMMY_IO_EXCEPTION = new IOException("dummy IO exception");
    private static final IOException DUMMY_AD_EXCEPTION = new AccessDeniedException("dummy access denied exception");

    @TempDir
    public Path temporaryFolder;

    private Model model = new ModelManager();
    private Logic logic;

    @BeforeEach
    public void setUp() {
        JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(
                this.temporaryFolder.resolve("addressBook.json"));
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(
                this.temporaryFolder.resolve("userPrefs.json"));
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage);
        this.logic = new LogicManager(this.model, storage);
    }

    @Test
    public void execute_invalidCommandFormat_throwsParseException() {
        String invalidCommand = "uicfhmowqewca";
        this.assertParseException(invalidCommand, Messages.MESSAGE_UNKNOWN_COMMAND);
    }

    @Test
    public void execute_commandExecutionError_throwsCommandException() {
        String deleteCommand = "delete ct/9";
        this.assertCommandException(deleteCommand, Messages.MESSAGE_INVALID_CONTACT_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validCommand_success() throws Exception {
        String listCommand = ListCommand.COMMAND_WORD + " contacts";
        this.assertCommandSuccess(listCommand, ListCommand.MESSAGE_CONTACTS, this.model);
    }

    @Test
    public void execute_listAssignmentsCommand_success() throws Exception {
        String listCommand = ListCommand.COMMAND_WORD + " assignments";
        this.assertCommandSuccess(listCommand, ListCommand.MESSAGE_ASSIGNMENTS, this.model);
    }

    @Test
    public void execute_listClassesCommand_success() throws Exception {
        String listCommand = ListCommand.COMMAND_WORD + " classes";
        this.assertCommandSuccess(listCommand, ListCommand.MESSAGE_CLASSES, this.model);
    }

    @Test
    public void execute_listContactsExplicit_success() throws Exception {
        String listCommand = ListCommand.COMMAND_WORD + " contacts";
        CommandResult result = this.logic.execute(listCommand);
        Assertions.assertEquals(ListCommand.MESSAGE_CONTACTS, result.getFeedbackToUser());
        Assertions.assertEquals(CommandResult.ListView.CONTACTS, result.getListView());
    }

    @Test
    public void execute_listAssignmentsExplicit_success() throws Exception {
        String listCommand = ListCommand.COMMAND_WORD + " assignments";
        CommandResult result = this.logic.execute(listCommand);
        Assertions.assertEquals(ListCommand.MESSAGE_ASSIGNMENTS, result.getFeedbackToUser());
        Assertions.assertEquals(CommandResult.ListView.ASSIGNMENTS, result.getListView());
    }

    @Test
    public void execute_listClassesExplicit_success() throws Exception {
        String listCommand = ListCommand.COMMAND_WORD + " classes";
        CommandResult result = this.logic.execute(listCommand);
        Assertions.assertEquals(ListCommand.MESSAGE_CLASSES, result.getFeedbackToUser());
        Assertions.assertEquals(CommandResult.ListView.CLASSGROUPS, result.getListView());
    }

    @Test
    public void execute_listCommandCaseInsensitive_success() throws Exception {
        String listCommand = ListCommand.COMMAND_WORD + " CONTACTS";
        this.assertCommandSuccess(listCommand, ListCommand.MESSAGE_CONTACTS, this.model);
    }

    @Test
    public void execute_listAssignmentsCaseInsensitive_success() throws Exception {
        String listCommand = ListCommand.COMMAND_WORD + " ASSIGNMENTS";
        this.assertCommandSuccess(listCommand, ListCommand.MESSAGE_ASSIGNMENTS, this.model);
    }

    @Test
    public void execute_listClassesCaseInsensitive_success() throws Exception {
        String listCommand = ListCommand.COMMAND_WORD + " CLASSES";
        this.assertCommandSuccess(listCommand, ListCommand.MESSAGE_CLASSES, this.model);
    }

    @Test
    public void execute_storageThrowsIoException_throwsCommandException() {
        this.assertCommandFailureForExceptionFromStorage(LogicManagerTest.DUMMY_IO_EXCEPTION, String.format(
                LogicManager.FILE_OPS_ERROR_FORMAT, LogicManagerTest.DUMMY_IO_EXCEPTION.getMessage()));
    }

    @Test
    public void execute_storageThrowsAdException_throwsCommandException() {
        this.assertCommandFailureForExceptionFromStorage(LogicManagerTest.DUMMY_AD_EXCEPTION, String.format(
                LogicManager.FILE_OPS_PERMISSION_ERROR_FORMAT, LogicManagerTest.DUMMY_AD_EXCEPTION.getMessage()));
    }

    @Test
    public void getFilteredContactList_modifyList_throwsUnsupportedOperationException() {
        Assert.assertThrows(UnsupportedOperationException.class, () -> this.logic.getFilteredContactList().remove(0));
    }

    @Test
    public void getFilteredAssignmentList_modifyList_throwsUnsupportedOperationException() {
        Assert.assertThrows(UnsupportedOperationException.class,
                () -> this.logic.getFilteredAssignmentList().remove(0));
    }

    @Test
    public void getFilteredClassGroupList_modifyList_throwsUnsupportedOperationException() {
        Assert.assertThrows(UnsupportedOperationException.class,
                () -> this.logic.getFilteredClassGroupList().remove(0));
    }

    /**
     * Executes the command and confirms that
     * - no exceptions are thrown <br>
     * - the feedback message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in
     * {@code expectedModel} <br>
     *
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandSuccess(String inputCommand, String expectedMessage,
            Model expectedModel) throws CommandException, ParseException {
        CommandResult result = this.logic.execute(inputCommand);
        Assertions.assertEquals(expectedMessage, result.getFeedbackToUser());
        Assertions.assertEquals(expectedModel, this.model);
    }

    /**
     * Executes the command, confirms that a ParseException is thrown and that the
     * result message is correct.
     *
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertParseException(String inputCommand, String expectedMessage) {
        this.assertCommandFailure(inputCommand, ParseException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that a CommandException is thrown and that the
     * result message is correct.
     *
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandException(String inputCommand, String expectedMessage) {
        this.assertCommandFailure(inputCommand, CommandException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that the exception is thrown and that the
     * result message is correct.
     *
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage) {
        Model expectedModel = new ModelManager(this.model.getAddressBook(), new UserPrefs());
        this.assertCommandFailure(inputCommand, expectedException, expectedMessage, expectedModel);
    }

    /**
     * Executes the command and confirms that
     * - the {@code expectedException} is thrown <br>
     * - the resulting error message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in
     * {@code expectedModel} <br>
     *
     * @see #assertCommandSuccess(String, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage, Model expectedModel) {
        Assert.assertThrows(expectedException, expectedMessage, () -> this.logic.execute(inputCommand));
        Assertions.assertEquals(expectedModel, this.model);
    }

    /**
     * Tests the Logic component's handling of an {@code IOException} thrown by the
     * Storage component.
     *
     * @param e               the exception to be thrown by the Storage component
     * @param expectedMessage the message expected inside exception thrown by the
     *                        Logic component
     */
    private void assertCommandFailureForExceptionFromStorage(IOException e, String expectedMessage) {
        Path prefPath = this.temporaryFolder.resolve("ExceptionUserPrefs.json");

        // Inject LogicManager with an AddressBookStorage that throws the IOException e
        // when saving
        JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(prefPath) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook addressBook, Path filePath)
                    throws IOException {
                throw e;
            }
        };

        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(
                this.temporaryFolder.resolve("ExceptionUserPrefs.json"));
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage);

        this.logic = new LogicManager(this.model, storage);

        // Triggers the saveAddressBook method by executing an add command
        String addContactCommand = AddContactCommand.COMMAND_WORD + CommandTestUtil.NAME_DESC_AMY
                + CommandTestUtil.PHONE_DESC_AMY
                + CommandTestUtil.EMAIL_DESC_AMY + CommandTestUtil.ADDRESS_DESC_AMY;
        Contact expectedContact = new ContactBuilder(TypicalContacts.AMY).withTags().build();
        ModelManager expectedModel = new ModelManager();
        expectedModel.addContact(expectedContact);
        this.assertCommandFailure(addContactCommand, CommandException.class, expectedMessage, expectedModel);
    }
}
