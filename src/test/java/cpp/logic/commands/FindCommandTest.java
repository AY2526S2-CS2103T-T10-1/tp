package cpp.logic.commands;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cpp.logic.Messages;
import cpp.model.Model;
import cpp.model.ModelManager;
import cpp.model.UserPrefs;
import cpp.model.contact.ContactNameContainsKeywordsPredicate;
import cpp.testutil.TypicalContacts;

/**
 * Contains integration tests (interaction with the Model) for
 * {@code FindCommand}.
 */
public class FindCommandTest {
    private Model model = new ModelManager(TypicalContacts.getTypicalAddressBook(), new UserPrefs());
    private Model expectedModel = new ModelManager(TypicalContacts.getTypicalAddressBook(), new UserPrefs());

    @Test
    public void equals() {
        ContactNameContainsKeywordsPredicate firstPredicate = new ContactNameContainsKeywordsPredicate(
                Collections.singletonList("first"));
        ContactNameContainsKeywordsPredicate secondPredicate = new ContactNameContainsKeywordsPredicate(
                Collections.singletonList("second"));

        FindCommand findFirstCommand = new FindCommand(firstPredicate);
        FindCommand findSecondCommand = new FindCommand(secondPredicate);

        // same object -> returns true
        Assertions.assertTrue(findFirstCommand.equals(findFirstCommand));

        // same values -> returns true
        FindCommand findFirstCommandCopy = new FindCommand(firstPredicate);
        Assertions.assertTrue(findFirstCommand.equals(findFirstCommandCopy));

        // different types -> returns false
        Assertions.assertFalse(findFirstCommand.equals(1));

        // null -> returns false
        Assertions.assertFalse(findFirstCommand.equals(null));

        // different contact -> returns false
        Assertions.assertFalse(findFirstCommand.equals(findSecondCommand));
    }

    @Test
    public void execute_zeroKeywords_noContactFound() {
        String expectedMessage = String.format(Messages.MESSAGE_CONTACTS_LISTED_OVERVIEW, 0);
        ContactNameContainsKeywordsPredicate predicate = this.preparePredicate(" ");
        FindCommand command = new FindCommand(predicate);
        this.expectedModel.updateFilteredContactList(predicate);
        CommandTestUtil.assertCommandSuccess(command, this.model, expectedMessage, this.expectedModel);
        Assertions.assertEquals(Collections.emptyList(), this.model.getFilteredContactList());
    }

    @Test
    public void execute_multipleKeywords_multipleContactsFound() {
        String expectedMessage = String.format(Messages.MESSAGE_CONTACTS_LISTED_OVERVIEW, 3);
        ContactNameContainsKeywordsPredicate predicate = this.preparePredicate("Kurz Elle Kunz");
        FindCommand command = new FindCommand(predicate);
        this.expectedModel.updateFilteredContactList(predicate);
        CommandTestUtil.assertCommandSuccess(command, this.model, expectedMessage, this.expectedModel);
        Assertions.assertEquals(Arrays.asList(TypicalContacts.CARL, TypicalContacts.ELLE, TypicalContacts.FIONA),
                this.model.getFilteredContactList());
    }

    @Test
    public void toStringMethod() {
        ContactNameContainsKeywordsPredicate predicate = new ContactNameContainsKeywordsPredicate(
                Arrays.asList("keyword"));
        FindCommand findCommand = new FindCommand(predicate);
        String expected = FindCommand.class.getCanonicalName() + "{predicate=" + predicate + "}";
        Assertions.assertEquals(expected, findCommand.toString());
    }

    /**
     * Parses {@code userInput} into a {@code NameContainsKeywordsPredicate}.
     */
    private ContactNameContainsKeywordsPredicate preparePredicate(String userInput) {
        return new ContactNameContainsKeywordsPredicate(Arrays.asList(userInput.split("\\s+")));
    }
}
