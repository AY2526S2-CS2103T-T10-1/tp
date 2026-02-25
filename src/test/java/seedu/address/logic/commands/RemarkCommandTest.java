package seedu.address.logic.commands;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Remark;
import seedu.address.testutil.TypicalIndexes;
import seedu.address.testutil.TypicalPersons;;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * RemarkCommand.
 */
public class RemarkCommandTest {

        private Model model = new ModelManager(TypicalPersons.getTypicalAddressBook(), new UserPrefs());

        @Test
        public void execute() {
                final Remark remark = new Remark("Some remark");

                CommandTestUtil.assertCommandFailure(new RemarkCommand(TypicalIndexes.INDEX_FIRST_PERSON, remark),
                                this.model,
                                String.format(RemarkCommand.MESSAGE_ARGUMENTS,
                                                TypicalIndexes.INDEX_FIRST_PERSON.getOneBased(),
                                                remark));
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
