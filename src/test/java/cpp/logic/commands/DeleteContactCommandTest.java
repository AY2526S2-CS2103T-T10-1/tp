package cpp.logic.commands;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cpp.commons.core.index.Index;
import cpp.logic.Messages;
import cpp.model.Model;
import cpp.model.ModelManager;
import cpp.model.UserPrefs;
import cpp.model.contact.Contact;
import cpp.testutil.TypicalContacts;
import cpp.testutil.TypicalIndexes;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code DeleteContactCommand}.
 */
public class DeleteContactCommandTest {

    private Model model = new ModelManager(TypicalContacts.getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validIndexUnfilteredList_success() {
        Contact contactToDelete = this.model.getFilteredContactList()
                .get(TypicalIndexes.INDEX_FIRST_CONTACT.getZeroBased());
        DeleteContactCommand deleteCommand = new DeleteContactCommand(
                List.of(TypicalIndexes.INDEX_FIRST_CONTACT));

        String expectedMessage = String.format(DeleteContactCommand.MESSAGE_DELETE_CONTACT_SUCCESS,
                Messages.format(contactToDelete));

        ModelManager expectedModel = new ModelManager(this.model.getAddressBook(), new UserPrefs());
        expectedModel.deleteContact(contactToDelete);

        CommandTestUtil.assertCommandSuccess(deleteCommand, this.model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(this.model.getFilteredContactList().size() + 1);
        DeleteContactCommand deleteCommand = new DeleteContactCommand(List.of(outOfBoundIndex));

        CommandTestUtil.assertCommandFailure(deleteCommand, this.model,
                Messages.MESSAGE_INVALID_CONTACT_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validIndexFilteredList_success() {
        CommandTestUtil.showContactAtIndex(this.model, TypicalIndexes.INDEX_FIRST_CONTACT);

        Contact contactToDelete = this.model.getFilteredContactList()
                .get(TypicalIndexes.INDEX_FIRST_CONTACT.getZeroBased());
        DeleteContactCommand deleteCommand = new DeleteContactCommand(
                List.of(TypicalIndexes.INDEX_FIRST_CONTACT));

        String expectedMessage = String.format(DeleteContactCommand.MESSAGE_DELETE_CONTACT_SUCCESS,
                Messages.format(contactToDelete));

        Model expectedModel = new ModelManager(this.model.getAddressBook(), new UserPrefs());
        expectedModel.deleteContact(contactToDelete);
        this.showNoContact(expectedModel);

        CommandTestUtil.assertCommandSuccess(deleteCommand, this.model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        CommandTestUtil.showContactAtIndex(this.model, TypicalIndexes.INDEX_FIRST_CONTACT);

        Index outOfBoundIndex = TypicalIndexes.INDEX_SECOND_CONTACT;
        // ensures that outOfBoundIndex is still in bounds of address book list
        Assertions.assertTrue(outOfBoundIndex.getZeroBased() < this.model.getAddressBook().getContactList().size());

        DeleteContactCommand deleteCommand = new DeleteContactCommand(List.of(outOfBoundIndex));

        CommandTestUtil.assertCommandFailure(deleteCommand, this.model,
                Messages.MESSAGE_INVALID_CONTACT_DISPLAYED_INDEX);
    }

    @Test
    public void execute_multipleValidIndices_success() {
        Contact firstContact = this.model.getFilteredContactList()
                .get(TypicalIndexes.INDEX_FIRST_CONTACT.getZeroBased());
        Contact secondContact = this.model.getFilteredContactList()
                .get(TypicalIndexes.INDEX_SECOND_CONTACT.getZeroBased());
        DeleteContactCommand deleteCommand = new DeleteContactCommand(
                List.of(TypicalIndexes.INDEX_FIRST_CONTACT, TypicalIndexes.INDEX_SECOND_CONTACT));

        String expectedMessage = String.format(DeleteContactCommand.MESSAGE_DELETE_CONTACT_SUCCESS,
                Messages.format(firstContact))
                + "\n"
                + String.format(DeleteContactCommand.MESSAGE_DELETE_CONTACT_SUCCESS,
                Messages.format(secondContact));

        ModelManager expectedModel = new ModelManager(this.model.getAddressBook(), new UserPrefs());
        expectedModel.deleteContact(firstContact);
        expectedModel.deleteContact(secondContact);

        CommandTestUtil.assertCommandSuccess(deleteCommand, this.model, expectedMessage, expectedModel);
    }

    @Test
    public void equals() {
        DeleteContactCommand deleteFirstCommand = new DeleteContactCommand(
                List.of(TypicalIndexes.INDEX_FIRST_CONTACT));
        DeleteContactCommand deleteSecondCommand = new DeleteContactCommand(
                List.of(TypicalIndexes.INDEX_SECOND_CONTACT));

        // same object -> returns true
        Assertions.assertTrue(deleteFirstCommand.equals(deleteFirstCommand));

        // same values -> returns true
        DeleteContactCommand deleteFirstCommandCopy = new DeleteContactCommand(
                List.of(TypicalIndexes.INDEX_FIRST_CONTACT));
        Assertions.assertTrue(deleteFirstCommand.equals(deleteFirstCommandCopy));

        // different types -> returns false
        Assertions.assertFalse(deleteFirstCommand.equals(1));

        // null -> returns false
        Assertions.assertFalse(deleteFirstCommand.equals(null));

        // different contact -> returns false
        Assertions.assertFalse(deleteFirstCommand.equals(deleteSecondCommand));
    }

    @Test
    public void toStringMethod() {
        List<Index> targetIndices = List.of(Index.fromOneBased(1));
        DeleteContactCommand deleteCommand = new DeleteContactCommand(targetIndices);
        String expected = DeleteContactCommand.class.getCanonicalName() + "{targetIndices=" + targetIndices + "}";
        Assertions.assertEquals(expected, deleteCommand.toString());
    }

    /**
     * Updates {@code model}'s filtered list to show no one.
     */
    private void showNoContact(Model model) {
        model.updateFilteredContactList(p -> false);

        Assertions.assertTrue(model.getFilteredContactList().isEmpty());
    }
}
