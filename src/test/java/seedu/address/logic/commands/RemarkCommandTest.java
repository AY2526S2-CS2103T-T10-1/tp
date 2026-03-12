package seedu.address.logic.commands;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.Remark;
import seedu.address.testutil.TypicalIndexes;
import seedu.address.testutil.TypicalPersons;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * RemarkCommand.
 */
public class RemarkCommandTest {

        private Model model = new ModelManager(TypicalPersons.getTypicalAddressBook(), new UserPrefs());

        @Test
        public void execute_addRemarkUnfilteredList_success() {
                Person personToEdit = this.model.getFilteredPersonList()
                                .get(TypicalIndexes.INDEX_FIRST_PERSON.getZeroBased());
                Remark newRemark = new Remark(CommandTestUtil.VALID_REMARK_BOB);

                RemarkCommand remarkCommand = new RemarkCommand(TypicalIndexes.INDEX_FIRST_PERSON, newRemark);

                String expectedMessage = String.format(RemarkCommand.MESSAGE_ADD_REMARK_SUCCESS,
                                CommandTestUtil.VALID_NAME_AMY);

                Person editedPerson = new Person(personToEdit.getName(), personToEdit.getPhone(),
                                personToEdit.getEmail(), personToEdit.getAddress(), newRemark, personToEdit.getTags());

                Model expectedModel = new ModelManager(this.model.getAddressBook(), new UserPrefs());
                expectedModel.setPerson(personToEdit, editedPerson);

                CommandTestUtil.assertCommandSuccess(remarkCommand, this.model, expectedMessage, expectedModel);
        }

        @Test
        public void execute_deleteRemarkUnfilteredList_success() {
                Person personToEdit = this.model.getFilteredPersonList()
                                .get(TypicalIndexes.INDEX_FIRST_PERSON.getZeroBased());
                Remark newRemark = new Remark("");

                RemarkCommand remarkCommand = new RemarkCommand(TypicalIndexes.INDEX_FIRST_PERSON, newRemark);

                String expectedMessage = String.format(RemarkCommand.MESSAGE_DELETE_REMARK_SUCCESS,
                                CommandTestUtil.VALID_NAME_AMY);

                Person editedPerson = new Person(personToEdit.getName(), personToEdit.getPhone(),
                                personToEdit.getEmail(), personToEdit.getAddress(), newRemark, personToEdit.getTags());

                Model expectedModel = new ModelManager(this.model.getAddressBook(), new UserPrefs());
                expectedModel.setPerson(personToEdit, editedPerson);

                CommandTestUtil.assertCommandSuccess(remarkCommand, this.model, expectedMessage, expectedModel);
        }

        @Test
        public void execute_invalidIndexUnfilteredList_failure() {
                Remark newRemark = new Remark(CommandTestUtil.VALID_REMARK_BOB);
                Index outOfBoundIndex = Index.fromOneBased(this.model.getFilteredPersonList().size() + 1);
                RemarkCommand remarkCommand = new RemarkCommand(outOfBoundIndex, newRemark);

                CommandTestUtil.assertCommandFailure(remarkCommand, this.model,
                                Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        @Test
        public void equals() {
                final RemarkCommand standardCommand = new RemarkCommand(TypicalIndexes.INDEX_FIRST_PERSON,
                                new Remark(CommandTestUtil.VALID_REMARK_AMY));

                // same values -> returns true
                RemarkCommand commandWithSameValues = new RemarkCommand(TypicalIndexes.INDEX_FIRST_PERSON,
                                new Remark(CommandTestUtil.VALID_REMARK_AMY));
                Assertions.assertTrue(standardCommand.equals(commandWithSameValues));

                // same object -> returns true
                Assertions.assertTrue(standardCommand.equals(standardCommand));

                // null -> returns false
                Assertions.assertFalse(standardCommand.equals(null));

                // different types -> returns false
                Assertions.assertFalse(standardCommand.equals(new ClearCommand()));

                // different index -> returns false
                Assertions.assertFalse(standardCommand
                                .equals(new RemarkCommand(TypicalIndexes.INDEX_SECOND_PERSON,
                                                new Remark(CommandTestUtil.VALID_REMARK_AMY))));

                // different remark -> returns false
                Assertions.assertFalse(standardCommand
                                .equals(new RemarkCommand(TypicalIndexes.INDEX_FIRST_PERSON,
                                                new Remark(CommandTestUtil.VALID_REMARK_BOB))));
        }
}
