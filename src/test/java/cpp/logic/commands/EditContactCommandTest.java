package cpp.logic.commands;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cpp.commons.core.index.Index;
import cpp.logic.Messages;
import cpp.logic.commands.EditContactCommand.EditContactDescriptor;
import cpp.model.AddressBook;
import cpp.model.Model;
import cpp.model.ModelManager;
import cpp.model.UserPrefs;
import cpp.model.contact.Contact;
import cpp.testutil.ContactBuilder;
import cpp.testutil.EditContactDescriptorBuilder;
import cpp.testutil.TypicalContacts;
import cpp.testutil.TypicalIndexes;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * EditContactCommand.
 */
public class EditContactCommandTest {

    private Model model = new ModelManager(TypicalContacts.getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_allFieldsSpecifiedUnfilteredList_success() {
        Contact editedContact = new ContactBuilder().build();
        EditContactDescriptor descriptor = new EditContactDescriptorBuilder(editedContact).build();
        EditContactCommand editCommand = new EditContactCommand(TypicalIndexes.INDEX_FIRST_CONTACT, descriptor);

        String expectedMessage = String.format(EditContactCommand.MESSAGE_EDIT_CONTACT_SUCCESS,
                Messages.format(editedContact));

        Model expectedModel = new ModelManager(new AddressBook(this.model.getAddressBook()), new UserPrefs());
        expectedModel.setContact(this.model.getFilteredContactList().get(0), editedContact);

        CommandTestUtil.assertCommandSuccess(editCommand, this.model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_someFieldsSpecifiedUnfilteredList_success() {
        Index indexLastContact = Index.fromOneBased(this.model.getFilteredContactList().size());
        Contact lastContact = this.model.getFilteredContactList().get(indexLastContact.getZeroBased());

        ContactBuilder contactInList = new ContactBuilder(lastContact);
        Contact editedContact = contactInList.withName(CommandTestUtil.VALID_NAME_BOB)
                .withPhone(CommandTestUtil.VALID_PHONE_BOB)
                .withTags(CommandTestUtil.VALID_TAG_HUSBAND).build();

        EditContactDescriptor descriptor = new EditContactDescriptorBuilder()
                .withName(CommandTestUtil.VALID_NAME_BOB)
                .withPhone(CommandTestUtil.VALID_PHONE_BOB).withTags(CommandTestUtil.VALID_TAG_HUSBAND)
                .build();
        EditContactCommand editCommand = new EditContactCommand(indexLastContact, descriptor);

        String expectedMessage = String.format(EditContactCommand.MESSAGE_EDIT_CONTACT_SUCCESS,
                Messages.format(editedContact));

        Model expectedModel = new ModelManager(new AddressBook(this.model.getAddressBook()), new UserPrefs());
        expectedModel.setContact(lastContact, editedContact);

        CommandTestUtil.assertCommandSuccess(editCommand, this.model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_noFieldSpecifiedUnfilteredList_success() {
        EditContactCommand editCommand = new EditContactCommand(TypicalIndexes.INDEX_FIRST_CONTACT,
                new EditContactDescriptor());
        Contact editedContact = this.model.getFilteredContactList()
                .get(TypicalIndexes.INDEX_FIRST_CONTACT.getZeroBased());

        String expectedMessage = String.format(EditContactCommand.MESSAGE_EDIT_CONTACT_SUCCESS,
                Messages.format(editedContact));

        Model expectedModel = new ModelManager(new AddressBook(this.model.getAddressBook()), new UserPrefs());

        CommandTestUtil.assertCommandSuccess(editCommand, this.model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_filteredList_success() {
        CommandTestUtil.showContactAtIndex(this.model, TypicalIndexes.INDEX_FIRST_CONTACT);

        Contact contactInFilteredList = this.model.getFilteredContactList()
                .get(TypicalIndexes.INDEX_FIRST_CONTACT.getZeroBased());
        Contact editedContact = new ContactBuilder(contactInFilteredList).withName(CommandTestUtil.VALID_NAME_BOB)
                .build();
        EditContactCommand editCommand = new EditContactCommand(TypicalIndexes.INDEX_FIRST_CONTACT,
                new EditContactDescriptorBuilder().withName(CommandTestUtil.VALID_NAME_BOB).build());

        String expectedMessage = String.format(EditContactCommand.MESSAGE_EDIT_CONTACT_SUCCESS,
                Messages.format(editedContact));

        Model expectedModel = new ModelManager(new AddressBook(this.model.getAddressBook()), new UserPrefs());
        expectedModel.setContact(this.model.getFilteredContactList().get(0), editedContact);

        CommandTestUtil.assertCommandSuccess(editCommand, this.model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_duplicateContactUnfilteredList_failure() {
        Contact firstContact = this.model.getFilteredContactList()
                .get(TypicalIndexes.INDEX_FIRST_CONTACT.getZeroBased());
        EditContactDescriptor descriptor = new EditContactDescriptorBuilder(firstContact).build();
        EditContactCommand editCommand = new EditContactCommand(TypicalIndexes.INDEX_SECOND_CONTACT, descriptor);

        CommandTestUtil.assertCommandFailure(editCommand, this.model, EditContactCommand.MESSAGE_DUPLICATE_CONTACT);
    }

    @Test
    public void execute_duplicateContactFilteredList_failure() {
        CommandTestUtil.showContactAtIndex(this.model, TypicalIndexes.INDEX_FIRST_CONTACT);

        // edit contact in filtered list into a duplicate in address book
        Contact contactInList = this.model.getAddressBook().getContactList()
                .get(TypicalIndexes.INDEX_SECOND_CONTACT.getZeroBased());
        EditContactCommand editCommand = new EditContactCommand(TypicalIndexes.INDEX_FIRST_CONTACT,
                new EditContactDescriptorBuilder(contactInList).build());

        CommandTestUtil.assertCommandFailure(editCommand, this.model, EditContactCommand.MESSAGE_DUPLICATE_CONTACT);
    }

    @Test
    public void execute_invalidContactIndexUnfilteredList_failure() {
        Index outOfBoundIndex = Index.fromOneBased(this.model.getFilteredContactList().size() + 1);
        EditContactDescriptor descriptor = new EditContactDescriptorBuilder()
                .withName(CommandTestUtil.VALID_NAME_BOB)
                .build();
        EditContactCommand editCommand = new EditContactCommand(outOfBoundIndex, descriptor);

        CommandTestUtil.assertCommandFailure(editCommand, this.model,
                Messages.MESSAGE_INVALID_CONTACT_DISPLAYED_INDEX);
    }

    /**
     * Edit filtered list where index is larger than size of filtered list,
     * but smaller than size of address book
     */
    @Test
    public void execute_invalidContactIndexFilteredList_failure() {
        CommandTestUtil.showContactAtIndex(this.model, TypicalIndexes.INDEX_FIRST_CONTACT);
        Index outOfBoundIndex = TypicalIndexes.INDEX_SECOND_CONTACT;
        // ensures that outOfBoundIndex is still in bounds of address book list
        Assertions.assertTrue(
                outOfBoundIndex.getZeroBased() < this.model.getAddressBook().getContactList().size());

        EditContactCommand editCommand = new EditContactCommand(outOfBoundIndex,
                new EditContactDescriptorBuilder().withName(CommandTestUtil.VALID_NAME_BOB).build());

        CommandTestUtil.assertCommandFailure(editCommand, this.model,
                Messages.MESSAGE_INVALID_CONTACT_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        final EditContactCommand standardCommand = new EditContactCommand(TypicalIndexes.INDEX_FIRST_CONTACT,
                CommandTestUtil.DESC_AMY);

        // same values -> returns true
        EditContactDescriptor copyDescriptor = new EditContactDescriptor(CommandTestUtil.DESC_AMY);
        EditContactCommand commandWithSameValues = new EditContactCommand(
                TypicalIndexes.INDEX_FIRST_CONTACT, copyDescriptor);
        Assertions.assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        Assertions.assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        Assertions.assertFalse(standardCommand.equals(null));

        // different types -> returns false
        Assertions.assertFalse(standardCommand.equals(new ClearCommand()));

        // different index -> returns false
        Assertions.assertFalse(
                standardCommand.equals(new EditContactCommand(TypicalIndexes.INDEX_SECOND_CONTACT,
                        CommandTestUtil.DESC_AMY)));

        // different descriptor -> returns false
        Assertions.assertFalse(
                standardCommand.equals(new EditContactCommand(TypicalIndexes.INDEX_FIRST_CONTACT,
                        CommandTestUtil.DESC_BOB)));
    }

    @Test
    public void toStringMethod() {
        Index index = Index.fromOneBased(1);
        EditContactDescriptor editContactDescriptor = new EditContactDescriptor();
        EditContactCommand editCommand = new EditContactCommand(index, editContactDescriptor);
        String expected = EditContactCommand.class.getCanonicalName() + "{index=" + index + ", editContactDescriptor="
                + editContactDescriptor + "}";
        Assertions.assertEquals(expected, editCommand.toString());
    }

}
