package cpp.logic.commands.classgroup;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cpp.logic.Messages;
import cpp.logic.commands.CommandTestUtil;
import cpp.model.Model;
import cpp.model.ModelManager;
import cpp.model.UserPrefs;
import cpp.model.classgroup.ClassGroup;
import cpp.model.classgroup.ClassGroupName;
import cpp.testutil.TypicalClassGroups;
import cpp.testutil.TypicalContacts;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code DeleteClassGroupCommand}.
 */
public class DeleteClassGroupCommandTest {

    private Model model = new ModelManager(TypicalContacts.getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validName_success() {
        this.model.addClassGroup(TypicalClassGroups.CLASS_GROUP_ONE);

        ClassGroup classGroupToDelete = TypicalClassGroups.CLASS_GROUP_ONE;
        DeleteClassGroupCommand deleteCommand = new DeleteClassGroupCommand(classGroupToDelete.getName());

        String expectedMessage = String.format(DeleteClassGroupCommand.MESSAGE_DELETE_CLASS_GROUP_SUCCESS,
                Messages.format(classGroupToDelete));

        ModelManager expectedModel = new ModelManager(this.model.getAddressBook(), new UserPrefs());
        expectedModel.deleteClassGroup(classGroupToDelete);

        CommandTestUtil.assertCommandSuccess(deleteCommand, this.model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_classGroupNotFound_commandFailure() {
        DeleteClassGroupCommand deleteCommand = new DeleteClassGroupCommand(
                new ClassGroupName("NonexistentClass"));

        CommandTestUtil.assertCommandFailure(deleteCommand, this.model,
                Messages.MESSAGE_CLASS_GROUP_NOT_FOUND);
    }

    @Test
    public void equals() {
        DeleteClassGroupCommand deleteFirstCommand = new DeleteClassGroupCommand(
                TypicalClassGroups.CLASS_GROUP_ONE.getName());
        DeleteClassGroupCommand deleteSecondCommand = new DeleteClassGroupCommand(
                TypicalClassGroups.CLASS_GROUP_TWO.getName());

        // same object -> returns true
        Assertions.assertTrue(deleteFirstCommand.equals(deleteFirstCommand));

        // same values -> returns true
        DeleteClassGroupCommand deleteFirstCommandCopy = new DeleteClassGroupCommand(
                TypicalClassGroups.CLASS_GROUP_ONE.getName());
        Assertions.assertTrue(deleteFirstCommand.equals(deleteFirstCommandCopy));

        // different types -> returns false
        Assertions.assertFalse(deleteFirstCommand.equals(1));

        // null -> returns false
        Assertions.assertFalse(deleteFirstCommand.equals(null));

        // different class group -> returns false
        Assertions.assertFalse(deleteFirstCommand.equals(deleteSecondCommand));
    }

    @Test
    public void toStringMethod() {
        ClassGroupName targetName = new ClassGroupName("CS2103T10");
        DeleteClassGroupCommand deleteCommand = new DeleteClassGroupCommand(targetName);
        String expected = DeleteClassGroupCommand.class.getCanonicalName() + "{targetName=" + targetName + "}";
        Assertions.assertEquals(expected, deleteCommand.toString());
    }
}
