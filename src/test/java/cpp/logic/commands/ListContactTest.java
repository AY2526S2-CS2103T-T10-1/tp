package cpp.logic.commands;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import cpp.logic.commands.exceptions.CommandException;
import cpp.model.Model;
import cpp.model.ModelManager;
import cpp.model.UserPrefs;
import cpp.testutil.TypicalContacts;
import cpp.testutil.TypicalIndexes;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * ListContactCommand.
 */
public class ListContactTest {

    private Model model;
    private Model expectedModel;

    @BeforeEach
    public void setUp() {
        this.model = new ModelManager(TypicalContacts.getTypicalAddressBook(), new UserPrefs());
        this.expectedModel = new ModelManager(this.model.getAddressBook(), new UserPrefs());
    }

    @Test
    public void execute_listIsNotFiltered_showsSameList() {
        CommandResult expectedResult = new CommandResult(ListCommand.MESSAGE_CONTACTS,
                CommandResult.ListView.CONTACTS);
        CommandTestUtil.assertCommandSuccess(new ListContactCommand(), this.model, expectedResult,
                this.expectedModel);
    }

    @Test
    public void execute_listIsFiltered_showsEverything() {
        CommandTestUtil.showContactAtIndex(this.model, TypicalIndexes.INDEX_FIRST_CONTACT);
        CommandResult expectedResult = new CommandResult(ListCommand.MESSAGE_CONTACTS,
                CommandResult.ListView.CONTACTS);
        CommandTestUtil.assertCommandSuccess(new ListContactCommand(), this.model, expectedResult,
                this.expectedModel);
    }

    @Test
    public void execute_returnsCorrectListView() throws CommandException {
        ListContactCommand command = new ListContactCommand();
        CommandResult result = command.execute(this.model);
        Assertions.assertEquals(CommandResult.ListView.CONTACTS, result.getListView());
    }

    @Test
    public void execute_modelNotModified() throws CommandException {
        Model originalModel = new ModelManager(TypicalContacts.getTypicalAddressBook(), new UserPrefs());
        ListContactCommand command = new ListContactCommand();
        command.execute(this.model);
        Assertions.assertEquals(originalModel.getAddressBook(), this.model.getAddressBook());
    }

    @Test
    public void execute_multipleExecutions_sameResult() throws CommandException {
        ListContactCommand command1 = new ListContactCommand();
        ListContactCommand command2 = new ListContactCommand();
        CommandResult result1 = command1.execute(this.model);
        CommandResult result2 = command2.execute(this.model);
        Assertions.assertEquals(result1, result2);
    }

    @Test
    public void execute_noFilters_displaysAllContacts() {
        Assertions.assertEquals(this.model.getFilteredContactList(), this.expectedModel.getFilteredContactList());
        CommandResult expectedResult = new CommandResult(ListCommand.MESSAGE_CONTACTS,
                CommandResult.ListView.CONTACTS);
        CommandTestUtil.assertCommandSuccess(new ListContactCommand(), this.model, expectedResult,
                this.expectedModel);
    }

    @Test
    public void equals_sameCommand_returnsTrue() {
        ListContactCommand command1 = new ListContactCommand();
        ListContactCommand command2 = new ListContactCommand();
        Assertions.assertEquals(command1, command2);
    }

    @Test
    public void equals_sameInstance_returnsTrue() {
        ListContactCommand command = new ListContactCommand();
        Assertions.assertEquals(command, command);
    }

    @Test
    public void equals_differentCommandType_returnsFalse() {
        ListContactCommand command1 = new ListContactCommand();
        ListAssignmentCommand command2 = new ListAssignmentCommand();
        Assertions.assertNotEquals(command1, command2);
    }

    @Test
    public void equals_nullCommand_returnsFalse() {
        ListContactCommand command = new ListContactCommand();
        Assertions.assertNotEquals(command, null);
    }

    @Test
    public void equals_nonCommandObject_returnsFalse() {
        ListContactCommand command = new ListContactCommand();
        Assertions.assertNotEquals(command, new Object());
    }

    @Test
    public void toString_returnsCorrectFormat() {
        ListContactCommand command = new ListContactCommand();
        String result = command.toString();
        Assertions.assertNotNull(result);
    }
}
