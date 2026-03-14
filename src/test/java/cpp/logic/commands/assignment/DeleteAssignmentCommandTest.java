package cpp.logic.commands.assignment;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cpp.logic.Messages;
import cpp.logic.commands.CommandTestUtil;
import cpp.model.Model;
import cpp.model.ModelManager;
import cpp.model.UserPrefs;
import cpp.model.assignment.Assignment;
import cpp.model.assignment.AssignmentName;
import cpp.testutil.TypicalAssignments;
import cpp.testutil.TypicalContacts;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code DeleteAssignmentCommand}.
 */
public class DeleteAssignmentCommandTest {

    private Model model = new ModelManager(TypicalContacts.getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validName_success() {
        Assignment assignmentToDelete = TypicalAssignments.ASSIGNMENT_ONE;
        DeleteAssignmentCommand deleteCommand = new DeleteAssignmentCommand(assignmentToDelete.getName());

        String expectedMessage = String.format(DeleteAssignmentCommand.MESSAGE_DELETE_ASSIGNMENT_SUCCESS,
                Messages.format(assignmentToDelete));

        ModelManager expectedModel = new ModelManager(this.model.getAddressBook(), new UserPrefs());
        expectedModel.deleteAssignment(assignmentToDelete);

        CommandTestUtil.assertCommandSuccess(deleteCommand, this.model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_notFound_commandFailure() {
        DeleteAssignmentCommand deleteCommand = new DeleteAssignmentCommand(
                new AssignmentName("Nonexistent Assignment"));

        CommandTestUtil.assertCommandFailure(deleteCommand, this.model,
                Messages.MESSAGE_ASSIGNMENT_NOT_FOUND);
    }

    @Test
    public void equals() {
        DeleteAssignmentCommand deleteFirstCommand = new DeleteAssignmentCommand(
                TypicalAssignments.ASSIGNMENT_ONE.getName());
        DeleteAssignmentCommand deleteSecondCommand = new DeleteAssignmentCommand(
                TypicalAssignments.ASSIGNMENT_TWO.getName());

        // same object -> returns true
        Assertions.assertTrue(deleteFirstCommand.equals(deleteFirstCommand));

        // same values -> returns true
        DeleteAssignmentCommand deleteFirstCommandCopy = new DeleteAssignmentCommand(
                TypicalAssignments.ASSIGNMENT_ONE.getName());
        Assertions.assertTrue(deleteFirstCommand.equals(deleteFirstCommandCopy));

        // different types -> returns false
        Assertions.assertFalse(deleteFirstCommand.equals(1));

        // null -> returns false
        Assertions.assertFalse(deleteFirstCommand.equals(null));

        // different assignment -> returns false
        Assertions.assertFalse(deleteFirstCommand.equals(deleteSecondCommand));
    }

    @Test
    public void toStringMethod() {
        AssignmentName targetName = new AssignmentName("Assignment 1");
        DeleteAssignmentCommand deleteCommand = new DeleteAssignmentCommand(targetName);
        String expected = DeleteAssignmentCommand.class.getCanonicalName() + "{targetName=" + targetName + "}";
        Assertions.assertEquals(expected, deleteCommand.toString());
    }
}
