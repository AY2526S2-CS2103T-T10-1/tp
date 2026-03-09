package cpp.logic.commands.assignment;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cpp.commons.core.index.Index;
import cpp.logic.Messages;
import cpp.logic.commands.CommandTestUtil;
import cpp.model.Model;
import cpp.model.ModelManager;
import cpp.model.UserPrefs;
import cpp.model.assignment.Assignment;
import cpp.testutil.TypicalContacts;
import cpp.testutil.TypicalIndexes;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code DeleteAssignmentCommand}.
 */
public class DeleteAssignmentCommandTest {

    private Model model = new ModelManager(TypicalContacts.getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validIndex_success() {
        Assignment assignmentToDelete = this.model.getFilteredAssignmentList()
                .get(TypicalIndexes.INDEX_FIRST_CONTACT.getZeroBased());
        DeleteAssignmentCommand deleteCommand = new DeleteAssignmentCommand(TypicalIndexes.INDEX_FIRST_CONTACT);

        String expectedMessage = String.format(DeleteAssignmentCommand.MESSAGE_DELETE_ASSIGNMENT_SUCCESS,
                Messages.format(assignmentToDelete));

        ModelManager expectedModel = new ModelManager(this.model.getAddressBook(), new UserPrefs());
        expectedModel.deleteAssignment(assignmentToDelete);

        CommandTestUtil.assertCommandSuccess(deleteCommand, this.model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndex_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(this.model.getFilteredAssignmentList().size() + 1);
        DeleteAssignmentCommand deleteCommand = new DeleteAssignmentCommand(outOfBoundIndex);

        CommandTestUtil.assertCommandFailure(deleteCommand, this.model,
                Messages.MESSAGE_INVALID_ASSIGNMENT_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        DeleteAssignmentCommand deleteFirstCommand = new DeleteAssignmentCommand(TypicalIndexes.INDEX_FIRST_CONTACT);
        DeleteAssignmentCommand deleteSecondCommand = new DeleteAssignmentCommand(TypicalIndexes.INDEX_SECOND_CONTACT);

        // same object -> returns true
        Assertions.assertTrue(deleteFirstCommand.equals(deleteFirstCommand));

        // same values -> returns true
        DeleteAssignmentCommand deleteFirstCommandCopy = new DeleteAssignmentCommand(TypicalIndexes.INDEX_FIRST_CONTACT);
        Assertions.assertTrue(deleteFirstCommand.equals(deleteFirstCommandCopy));

        // different types -> returns false
        Assertions.assertFalse(deleteFirstCommand.equals(1));

        // null -> returns false
        Assertions.assertFalse(deleteFirstCommand.equals(null));

        // different index -> returns false
        Assertions.assertFalse(deleteFirstCommand.equals(deleteSecondCommand));
    }

    @Test
    public void toStringMethod() {
        Index targetIndex = Index.fromOneBased(1);
        DeleteAssignmentCommand deleteCommand = new DeleteAssignmentCommand(targetIndex);
        String expected = DeleteAssignmentCommand.class.getCanonicalName() + "{targetIndex=" + targetIndex + "}";
        Assertions.assertEquals(expected, deleteCommand.toString());
    }
}
