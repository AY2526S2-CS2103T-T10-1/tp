package cpp.logic.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import cpp.model.Model;
import cpp.model.ModelManager;
import cpp.model.UserPrefs;
import cpp.testutil.TypicalIndexes;
import cpp.testutil.TypicalPersons;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * ListCommand.
 */
public class ListCommandTest {

    private Model model;
    private Model expectedModel;

    @BeforeEach
    public void setUp() {
        this.model = new ModelManager(TypicalPersons.getTypicalAddressBook(), new UserPrefs());
        this.expectedModel = new ModelManager(this.model.getAddressBook(), new UserPrefs());
    }

    @Test
    public void execute_listIsNotFiltered_showsSameList() {
        CommandTestUtil.assertCommandSuccess(new ListCommand(), this.model, ListCommand.MESSAGE_SUCCESS,
                this.expectedModel);
    }

    @Test
    public void execute_listIsFiltered_showsEverything() {
        CommandTestUtil.showPersonAtIndex(this.model, TypicalIndexes.INDEX_FIRST_PERSON);
        CommandTestUtil.assertCommandSuccess(new ListCommand(), this.model, ListCommand.MESSAGE_SUCCESS,
                this.expectedModel);
    }
}
