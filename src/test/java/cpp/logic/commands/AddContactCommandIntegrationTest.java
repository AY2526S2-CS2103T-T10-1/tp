package cpp.logic.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import cpp.logic.Messages;
import cpp.model.Model;
import cpp.model.ModelManager;
import cpp.model.UserPrefs;
import cpp.model.contact.Contact;
import cpp.testutil.ContactBuilder;
import cpp.testutil.TypicalContacts;

/**
 * Contains integration tests (interaction with the Model) for
 * {@code AddContactCommand}.
 */
public class AddContactCommandIntegrationTest {

    private Model model;

    @BeforeEach
    public void setUp() {
        this.model = new ModelManager(TypicalContacts.getTypicalAddressBook(), new UserPrefs());
    }

    @Test
    public void execute_newContact_success() {
        Contact validContact = new ContactBuilder().build();

        Model expectedModel = new ModelManager(this.model.getAddressBook(), new UserPrefs());
        expectedModel.addContact(validContact);

        CommandTestUtil.assertCommandSuccess(new AddContactCommand(validContact), this.model,
                String.format(AddContactCommand.MESSAGE_SUCCESS, Messages.format(validContact)),
                expectedModel);
    }

    @Test
    public void execute_duplicateContact_throwsCommandException() {
        Contact contactInList = this.model.getAddressBook().getContactList().get(0);
        CommandTestUtil.assertCommandFailure(new AddContactCommand(contactInList), this.model,
                AddContactCommand.MESSAGE_DUPLICATE_CONTACT);
    }

}
