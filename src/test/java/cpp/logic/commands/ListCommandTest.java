package cpp.logic.commands;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import cpp.model.Model;
import cpp.model.ModelManager;
import cpp.model.UserPrefs;
import cpp.testutil.TypicalContacts;
import cpp.testutil.TypicalIndexes;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * ListCommand and its subclasses.
 */
public class ListCommandTest {

    private Model model;
    private Model expectedModel;

    @BeforeEach
    public void setUp() {
        this.model = new ModelManager(TypicalContacts.getTypicalAddressBook(), new UserPrefs());
        this.expectedModel = new ModelManager(this.model.getAddressBook(), new UserPrefs());
    }

    @Test
    public void execute_listIsNotFiltered_showsSameList() {
        CommandTestUtil.assertCommandSuccess(new ListContactCommand(), this.model, ListCommand.MESSAGE_SUCCESS,
                this.expectedModel);
    }

    @Test
    public void execute_listIsFiltered_showsEverything() {
        CommandTestUtil.showContactAtIndex(this.model, TypicalIndexes.INDEX_FIRST_CONTACT);
        CommandTestUtil.assertCommandSuccess(new ListContactCommand(), this.model, ListCommand.MESSAGE_SUCCESS,
                this.expectedModel);
    }

    @Test
    public void execute_listAssignments_returnsAssignmentMessage() {
        CommandTestUtil.assertCommandSuccess(new ListAssignmentCommand(), this.model,
                ListCommand.MESSAGE_ASSIGNMENTS,
                this.expectedModel);
    }

    @Test
    public void execute_listClasses_returnsClassesMessage() {
        CommandTestUtil.assertCommandSuccess(new ListClassCommand(), this.model, ListCommand.MESSAGE_CLASSES,
                this.expectedModel);
    }

    @Test
    public void equals_sameTab_returnsTrue() {
        ListContactCommand command1 = new ListContactCommand();
        ListContactCommand command2 = new ListContactCommand();
        Assertions.assertEquals(command1, command2);
    }

    @Test
    public void equals_differentTab_returnsFalse() {
        ListContactCommand command1 = new ListContactCommand();
        ListAssignmentCommand command2 = new ListAssignmentCommand();
        Assertions.assertNotEquals(command1, command2);
    }

    @Test
    public void equals_sameInstance_returnsTrue() {
        ListContactCommand command = new ListContactCommand();
        Assertions.assertEquals(command, command);
    }

    @Test
    public void equals_notListCommand_returnsFalse() {
        ListContactCommand command = new ListContactCommand();
        Assertions.assertNotEquals(command, new Object());
    }

    @Test
    public void equals_null_returnsFalse() {
        ListContactCommand command = new ListContactCommand();
        Assertions.assertNotEquals(command, null);
    }
}
