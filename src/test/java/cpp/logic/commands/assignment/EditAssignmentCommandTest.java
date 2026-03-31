package cpp.logic.commands.assignment;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cpp.commons.core.index.Index;
import cpp.logic.Messages;
import cpp.logic.commands.CommandResult;
import cpp.logic.commands.CommandTestUtil;
import cpp.logic.commands.assignment.EditAssignmentCommand.EditAssignmentDescriptor;
import cpp.model.Model;
import cpp.model.ModelManager;
import cpp.model.UserPrefs;
import cpp.model.assignment.Assignment;
import cpp.model.assignment.AssignmentName;
import cpp.testutil.AssignmentBuilder;
import cpp.testutil.TypicalAssignments;
import cpp.testutil.TypicalContacts;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code EditAssignmentCommand}.
 */
public class EditAssignmentCommandTest {

    private Model model = new ModelManager(TypicalContacts.getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_allFieldsSpecified_success() {
        Assignment editedAssignment = new AssignmentBuilder()
                .withId(TypicalAssignments.ASSIGNMENT_ONE.getId())
                .withName("Updated Assignment")
                .withDeadline("25-12-2026 23:59")
                .build();

        EditAssignmentDescriptor descriptor = new EditAssignmentDescriptor();
        descriptor.setName(new AssignmentName("Updated Assignment"));
        descriptor.setDeadline(editedAssignment.getDeadline());

        EditAssignmentCommand editCommand = new EditAssignmentCommand(Index.fromOneBased(1), descriptor);

        CommandResult expectedResult = new CommandResult(
                String.format(EditAssignmentCommand.MESSAGE_EDIT_ASSIGNMENT_SUCCESS,
                        Messages.format(editedAssignment)));

        ModelManager expectedModel = new ModelManager(this.model.getAddressBook(), new UserPrefs());
        expectedModel.setAssignment(TypicalAssignments.ASSIGNMENT_ONE, editedAssignment);

        CommandTestUtil.assertCommandSuccess(editCommand, this.model, expectedResult, expectedModel);
    }

    @Test
    public void execute_invalidIndex_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(this.model.getFilteredAssignmentList().size() + 1);
        EditAssignmentDescriptor descriptor = new EditAssignmentDescriptor();
        descriptor.setName(new AssignmentName("Some Name"));
        EditAssignmentCommand editCommand = new EditAssignmentCommand(outOfBoundIndex, descriptor);

        CommandTestUtil.assertCommandFailure(editCommand, this.model,
                Messages.MESSAGE_INVALID_ASSIGNMENT_DISPLAYED_INDEX);
    }

    @Test
    public void editAssignmentDescriptor_noFieldEdited_returnsFalse() {
        EditAssignmentDescriptor descriptor = new EditAssignmentDescriptor();
        Assertions.assertFalse(descriptor.isAnyFieldEdited());
    }

    @Test
    public void editAssignmentDescriptor_oneFieldEdited_returnsTrue() {
        EditAssignmentDescriptor descriptor = new EditAssignmentDescriptor();
        descriptor.setName(new AssignmentName("Test"));
        Assertions.assertTrue(descriptor.isAnyFieldEdited());
    }

    @Test
    public void execute_onlyDeadlineEdited_success() {
        // ASSIGNMENT_ONE is already in the typical address book at index 1
        Assignment editedAssignment = new AssignmentBuilder()
                .withId(TypicalAssignments.ASSIGNMENT_ONE.getId())
                .withName(TypicalAssignments.ASSIGNMENT_ONE.getName().fullName)
                .withDeadline("25-12-2026 23:59")
                .build();

        EditAssignmentDescriptor descriptor = new EditAssignmentDescriptor();
        descriptor.setDeadline(editedAssignment.getDeadline());

        EditAssignmentCommand editCommand = new EditAssignmentCommand(Index.fromOneBased(1), descriptor);

        CommandResult expectedResult = new CommandResult(
                String.format(EditAssignmentCommand.MESSAGE_EDIT_ASSIGNMENT_SUCCESS,
                        Messages.format(editedAssignment)));

        ModelManager expectedModel = new ModelManager(this.model.getAddressBook(), new UserPrefs());
        expectedModel.setAssignment(TypicalAssignments.ASSIGNMENT_ONE, editedAssignment);

        CommandTestUtil.assertCommandSuccess(editCommand, this.model, expectedResult, expectedModel);
    }

    @Test
    public void execute_duplicateAssignment_throwsCommandException() {
        // Add ASSIGNMENT_TWO so we can attempt a name+deadline collision
        this.model.addAssignment(TypicalAssignments.ASSIGNMENT_TWO);

        // Try to edit ASSIGNMENT_ONE to match ASSIGNMENT_TWO's name and deadline
        // exactly
        EditAssignmentDescriptor descriptor = new EditAssignmentDescriptor();
        descriptor.setName(TypicalAssignments.ASSIGNMENT_TWO.getName());
        descriptor.setDeadline(TypicalAssignments.ASSIGNMENT_TWO.getDeadline());

        EditAssignmentCommand editCommand = new EditAssignmentCommand(Index.fromOneBased(1), descriptor);

        CommandTestUtil.assertCommandFailure(editCommand, this.model,
                EditAssignmentCommand.MESSAGE_DUPLICATE_ASSIGNMENT);
    }

    @Test
    public void toStringMethod() {
        Index index = Index.fromOneBased(1);
        EditAssignmentDescriptor descriptor = new EditAssignmentDescriptor();
        descriptor.setName(new AssignmentName("Assignment 1"));
        EditAssignmentCommand cmd = new EditAssignmentCommand(index, descriptor);
        String expected = EditAssignmentCommand.class.getCanonicalName()
                + "{index=" + index + ", editAssignmentDescriptor=" + descriptor + "}";
        Assertions.assertEquals(expected, cmd.toString());
    }

    @Test
    public void equals_sameValues_returnsTrue() {
        EditAssignmentDescriptor descriptor = new EditAssignmentDescriptor();
        descriptor.setName(new AssignmentName("Assignment 1"));
        EditAssignmentCommand cmd1 = new EditAssignmentCommand(Index.fromOneBased(1), descriptor);
        EditAssignmentCommand cmd2 = new EditAssignmentCommand(Index.fromOneBased(1), descriptor);

        Assertions.assertTrue(cmd1.equals(cmd2));
    }

    @Test
    public void equals_sameObject_returnsTrue() {
        EditAssignmentDescriptor descriptor = new EditAssignmentDescriptor();
        descriptor.setName(new AssignmentName("Assignment 1"));
        EditAssignmentCommand cmd = new EditAssignmentCommand(Index.fromOneBased(1), descriptor);

        Assertions.assertTrue(cmd.equals(cmd));
    }

    @Test
    public void equals_differentIndex_returnsFalse() {
        EditAssignmentDescriptor descriptor = new EditAssignmentDescriptor();
        descriptor.setName(new AssignmentName("Assignment 1"));
        EditAssignmentCommand cmd1 = new EditAssignmentCommand(Index.fromOneBased(1), descriptor);
        EditAssignmentCommand cmd2 = new EditAssignmentCommand(Index.fromOneBased(2), descriptor);

        Assertions.assertFalse(cmd1.equals(cmd2));
    }

    @Test
    public void equals_differentDescriptor_returnsFalse() {
        EditAssignmentDescriptor descriptor1 = new EditAssignmentDescriptor();
        descriptor1.setName(new AssignmentName("Assignment 1"));
        EditAssignmentDescriptor descriptor2 = new EditAssignmentDescriptor();
        descriptor2.setName(new AssignmentName("Assignment 2"));

        EditAssignmentCommand cmd1 = new EditAssignmentCommand(Index.fromOneBased(1), descriptor1);
        EditAssignmentCommand cmd2 = new EditAssignmentCommand(Index.fromOneBased(1), descriptor2);

        Assertions.assertFalse(cmd1.equals(cmd2));
    }

    @Test
    public void equals_null_returnsFalse() {
        EditAssignmentDescriptor descriptor = new EditAssignmentDescriptor();
        descriptor.setName(new AssignmentName("Assignment 1"));
        EditAssignmentCommand cmd = new EditAssignmentCommand(Index.fromOneBased(1), descriptor);

        Assertions.assertFalse(cmd.equals(null));
    }

    @Test
    public void equals_differentType_returnsFalse() {
        EditAssignmentDescriptor descriptor = new EditAssignmentDescriptor();
        descriptor.setName(new AssignmentName("Assignment 1"));
        EditAssignmentCommand cmd = new EditAssignmentCommand(Index.fromOneBased(1), descriptor);

        Assertions.assertFalse(cmd.equals(42));
    }
}
