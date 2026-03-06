package cpp.logic.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import cpp.logic.Messages;
import cpp.model.Model;
import cpp.model.ModelManager;
import cpp.model.UserPrefs;
import cpp.model.person.Person;
import cpp.testutil.PersonBuilder;
import cpp.testutil.TypicalPersons;

/**
 * Contains integration tests (interaction with the Model) for
 * {@code AddCommand}.
 */
public class AddCommandIntegrationTest {

    private Model model;

    @BeforeEach
    public void setUp() {
        this.model = new ModelManager(TypicalPersons.getTypicalAddressBook(), new UserPrefs());
    }

    @Test
    public void execute_newPerson_success() {
        Person validPerson = new PersonBuilder().build();

        Model expectedModel = new ModelManager(this.model.getAddressBook(), new UserPrefs());
        expectedModel.addPerson(validPerson);

        CommandTestUtil.assertCommandSuccess(new AddCommand(validPerson), this.model,
                String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(validPerson)),
                expectedModel);
    }

    @Test
    public void execute_duplicatePerson_throwsCommandException() {
        Person personInList = this.model.getAddressBook().getPersonList().get(0);
        CommandTestUtil.assertCommandFailure(new AddCommand(personInList), this.model,
                AddCommand.MESSAGE_DUPLICATE_PERSON);
    }

}
