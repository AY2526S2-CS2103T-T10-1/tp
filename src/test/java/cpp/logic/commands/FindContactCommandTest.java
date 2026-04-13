package cpp.logic.commands;

import java.util.Arrays;

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
 * {@code FindContactCommand}.
 */
public class FindContactCommandTest {
    private Model model = new ModelManager(TypicalContacts.getTypicalAddressBook(), new UserPrefs());
    private Model expectedModel = new ModelManager(TypicalContacts.getTypicalAddressBook(), new UserPrefs());

    @Test
    public void equals() {
        ContactNameContainsKeywordsPredicate firstPredicate = new ContactNameContainsKeywordsPredicate("first");
        ContactNameContainsKeywordsPredicate secondPredicate = new ContactNameContainsKeywordsPredicate("second");

        FindContactCommand findFirstCommand = new FindContactCommand(firstPredicate);
        FindContactCommand findSecondCommand = new FindContactCommand(secondPredicate);

        // same object -> returns true
        Assertions.assertTrue(findFirstCommand.equals(findFirstCommand));

        // same values -> returns true
        FindContactCommand findFirstCommandCopy = new FindContactCommand(firstPredicate);
        Assertions.assertTrue(findFirstCommand.equals(findFirstCommandCopy));

        // different types -> returns false
        Assertions.assertFalse(findFirstCommand.equals(1));

        // null -> returns false
        Assertions.assertFalse(findFirstCommand.equals(null));

        // different contact -> returns false
        Assertions.assertFalse(findFirstCommand.equals(findSecondCommand));
    }

    @Test
    public void execute_singleSpace_allContactsFound() {
        String expectedMessage = String.format(Messages.MESSAGE_CONTACTS_LISTED_OVERVIEW,
                this.expectedModel.getFilteredContactList().size());
        ContactNameContainsKeywordsPredicate predicate = this.preparePredicate(" ");
        FindContactCommand command = new FindContactCommand(predicate);
        this.expectedModel.updateFilteredContactList(predicate);
        CommandTestUtil.assertCommandSuccess(command, this.model,
                new CommandResult(expectedMessage, CommandResult.ListView.CONTACTS), this.expectedModel);
        Assertions.assertEquals(TypicalContacts.getTypicalContacts(), this.model.getFilteredContactList());
    }

    @Test
    public void execute_multipleKeywords_singleContactFound() {
        String expectedMessage = String.format(Messages.MESSAGE_CONTACTS_LISTED_OVERVIEW, 1);
        ContactNameContainsKeywordsPredicate predicate = this.preparePredicate("Elle M");
        FindContactCommand command = new FindContactCommand(predicate);
        this.expectedModel.updateFilteredContactList(predicate);
        CommandTestUtil.assertCommandSuccess(command, this.model,
                new CommandResult(expectedMessage, CommandResult.ListView.CONTACTS), this.expectedModel);
        Assertions.assertEquals(Arrays.asList(TypicalContacts.ELLE),
                this.model.getFilteredContactList());
    }

    @Test
    public void toStringMethod() {
        ContactNameContainsKeywordsPredicate predicate = new ContactNameContainsKeywordsPredicate("keyword");
        FindContactCommand findCommand = new FindContactCommand(predicate);
        String expected = FindContactCommand.class.getCanonicalName() + "{predicate=" + predicate + "}";
        Assertions.assertEquals(expected, findCommand.toString());
    }

    /**
     * Parses {@code userInput} into a {@code NameContainsKeywordsPredicate}.
     */
    private ContactNameContainsKeywordsPredicate preparePredicate(String userInput) {
        return new ContactNameContainsKeywordsPredicate(userInput);
    }
}
