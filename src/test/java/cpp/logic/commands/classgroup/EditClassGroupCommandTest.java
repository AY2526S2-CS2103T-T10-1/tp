package cpp.logic.commands.classgroup;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import cpp.commons.core.index.Index;
import cpp.logic.Messages;
import cpp.logic.commands.CommandTestUtil;
import cpp.model.Model;
import cpp.model.ModelManager;
import cpp.model.UserPrefs;
import cpp.model.classgroup.ClassGroup;
import cpp.model.classgroup.ClassGroupName;
import cpp.testutil.ClassGroupBuilder;
import cpp.testutil.TypicalClassGroups;
import cpp.testutil.TypicalContacts;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code EditClassGroupCommand}.
 */
public class EditClassGroupCommandTest {

    private Model model;

    @BeforeEach
    public void setUp() {
        this.model = new ModelManager(TypicalContacts.getTypicalAddressBook(), new UserPrefs());
        this.model.addClassGroup(TypicalClassGroups.CLASS_GROUP_ONE);
        this.model.addClassGroup(TypicalClassGroups.CLASS_GROUP_TWO);
    }

    @Test
    public void execute_validIndex_success() {
        ClassGroup classGroupToEdit = TypicalClassGroups.CLASS_GROUP_ONE;
        ClassGroupName newName = new ClassGroupName("CS2103TNewName");
        EditClassGroupCommand editCommand = new EditClassGroupCommand(Index.fromOneBased(1), newName);

        ClassGroup editedClassGroup = new ClassGroupBuilder(classGroupToEdit).withName("CS2103TNewName").build();
        String expectedMessage = String.format(EditClassGroupCommand.MESSAGE_EDIT_CLASS_GROUP_SUCCESS,
                Messages.format(editedClassGroup));

        ModelManager expectedModel = new ModelManager(this.model.getAddressBook(), new UserPrefs());
        expectedModel.setClassGroup(classGroupToEdit, editedClassGroup);

        CommandTestUtil.assertCommandSuccess(editCommand, this.model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndex_commandFailure() {
        Index outOfBoundIndex = Index.fromOneBased(this.model.getFilteredClassGroupList().size() + 1);
        EditClassGroupCommand editCommand = new EditClassGroupCommand(outOfBoundIndex, new ClassGroupName("AnyName"));

        CommandTestUtil.assertCommandFailure(editCommand, this.model,
                Messages.MESSAGE_INVALID_CLASS_GROUP_DISPLAYED_INDEX);
    }

    @Test
    public void execute_duplicateName_commandFailure() {
        // Try to rename CLASS_GROUP_ONE to CLASS_GROUP_TWO's name
        ClassGroupName duplicateName = TypicalClassGroups.CLASS_GROUP_TWO.getName();
        EditClassGroupCommand editCommand = new EditClassGroupCommand(Index.fromOneBased(1), duplicateName);

        CommandTestUtil.assertCommandFailure(editCommand, this.model,
                EditClassGroupCommand.MESSAGE_DUPLICATE_CLASS_GROUP);
    }

    @Test
    public void execute_sameNameSameGroup_success() {
        // Renaming to the same name should succeed (not a duplicate conflict with itself)
        ClassGroup classGroupToEdit = TypicalClassGroups.CLASS_GROUP_ONE;
        ClassGroupName sameName = classGroupToEdit.getName();
        EditClassGroupCommand editCommand = new EditClassGroupCommand(Index.fromOneBased(1), sameName);

        ClassGroup editedClassGroup = new ClassGroupBuilder(classGroupToEdit)
                .withName(sameName.toString()).build();
        String expectedMessage = String.format(EditClassGroupCommand.MESSAGE_EDIT_CLASS_GROUP_SUCCESS,
                Messages.format(editedClassGroup));

        ModelManager expectedModel = new ModelManager(this.model.getAddressBook(), new UserPrefs());
        expectedModel.setClassGroup(classGroupToEdit, editedClassGroup);

        CommandTestUtil.assertCommandSuccess(editCommand, this.model, expectedMessage, expectedModel);
    }

    @Test
    public void equals() {
        EditClassGroupCommand editFirstCommand = new EditClassGroupCommand(
                Index.fromOneBased(1), new ClassGroupName("CS2103T10"));
        EditClassGroupCommand editSecondCommand = new EditClassGroupCommand(
                Index.fromOneBased(2), new ClassGroupName("CS2103T10"));
        EditClassGroupCommand editDifferentNameCommand = new EditClassGroupCommand(
                Index.fromOneBased(1), new ClassGroupName("CS2101T10"));

        // same object -> returns true
        Assertions.assertTrue(editFirstCommand.equals(editFirstCommand));

        // same values -> returns true
        EditClassGroupCommand editFirstCommandCopy = new EditClassGroupCommand(
                Index.fromOneBased(1), new ClassGroupName("CS2103T10"));
        Assertions.assertTrue(editFirstCommand.equals(editFirstCommandCopy));

        // different types -> returns false
        Assertions.assertFalse(editFirstCommand.equals(1));

        // null -> returns false
        Assertions.assertFalse(editFirstCommand.equals(null));

        // different index -> returns false
        Assertions.assertFalse(editFirstCommand.equals(editSecondCommand));

        // different name -> returns false
        Assertions.assertFalse(editFirstCommand.equals(editDifferentNameCommand));
    }

    @Test
    public void toStringMethod() {
        Index index = Index.fromOneBased(1);
        ClassGroupName newName = new ClassGroupName("CS2103T10");
        EditClassGroupCommand editCommand = new EditClassGroupCommand(index, newName);
        String expected = EditClassGroupCommand.class.getCanonicalName()
                + "{index=" + index + ", newName=" + newName + "}";
        Assertions.assertEquals(expected, editCommand.toString());
    }
}
