package cpp.logic.commands;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cpp.model.Model;
import cpp.model.ModelManager;
import cpp.model.UserPrefs;
import cpp.testutil.AddressBookBuilder;
import cpp.testutil.TypicalAssignments;
import cpp.testutil.TypicalContacts;

/**
 * Contains unit tests for ListCommand base class and its polymorphic behavior,
 * plus comprehensive integration tests for edge cases.
 */
public class ListCommandTest {

    @Test
    public void equals_sameTab_returnsTrue() {
        ListContactCommand command1 = new ListContactCommand();
        ListContactCommand command2 = new ListContactCommand();
        Assertions.assertEquals(command1, command2);
    }

    @Test
    public void equals_differentTab_returnsFalse() {
        ListContactCommand command1 = new ListContactCommand();
        ListAssignmentCommand command2 = new ListAssignmentCommand();
        Assertions.assertNotEquals(command1, command2);
    }

    @Test
    public void equals_sameInstance_returnsTrue() {
        ListContactCommand command = new ListContactCommand();
        Assertions.assertEquals(command, command);
    }

    @Test
    public void equals_notListCommand_returnsFalse() {
        ListContactCommand command = new ListContactCommand();
        Assertions.assertNotEquals(command, new Object());
    }

    @Test
    public void equals_null_returnsFalse() {
        ListContactCommand command = new ListContactCommand();
        Assertions.assertNotEquals(command, null);
    }

    @Test
    public void polymorphism_contactAndAssignment_notEqual() {
        ListContactCommand contact = new ListContactCommand();
        ListAssignmentCommand assignment = new ListAssignmentCommand();
        Assertions.assertNotEquals(contact, assignment);
    }

    @Test
    public void polymorphism_contactAndClass_notEqual() {
        ListContactCommand contact = new ListContactCommand();
        ListClassCommand classCmd = new ListClassCommand();
        Assertions.assertNotEquals(contact, classCmd);
    }

    @Test
    public void polymorphism_assignmentAndClass_notEqual() {
        ListAssignmentCommand assignment = new ListAssignmentCommand();
        ListClassCommand classCmd = new ListClassCommand();
        Assertions.assertNotEquals(assignment, classCmd);
    }

    @Test
    public void polymorphism_allDifferent_returnsFalse() {
        ListContactCommand contact = new ListContactCommand();
        ListAssignmentCommand assignment = new ListAssignmentCommand();
        ListClassCommand classCmd = new ListClassCommand();

        Assertions.assertNotEquals(contact, assignment);
        Assertions.assertNotEquals(assignment, classCmd);
        Assertions.assertNotEquals(contact, classCmd);
    }

    @Test
    public void hashCode_equalObjects_sameHashCode() {
        ListContactCommand command1 = new ListContactCommand();
        ListContactCommand command2 = new ListContactCommand();
        Assertions.assertEquals(command1.hashCode(), command2.hashCode());
    }

    @Test
    public void hashCode_differentTypes_mayBeDifferent() {
        ListContactCommand contact = new ListContactCommand();
        ListAssignmentCommand assignment = new ListAssignmentCommand();
        // Different types should generally have different hash codes (not required but
        // typical)
        // This test documents the behavior
        Assertions.assertNotEquals(contact, assignment);
    }

    // ========== EDGE CASE INTEGRATION TESTS ==========

    @Test
    public void listContactCommand_emptyAddressBook_showsEmptyList() {
        Model model = new ModelManager(new AddressBookBuilder().build(), new UserPrefs());
        ListContactCommand command = new ListContactCommand();
        CommandResult result = command.execute(model);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(ListCommand.MESSAGE_CONTACTS, result.getFeedbackToUser());
        Assertions.assertTrue(model.getFilteredContactList().isEmpty());
    }

    @Test
    public void listAssignmentCommand_emptyAddressBook_showsEmptyList() {
        Model model = new ModelManager(new AddressBookBuilder().build(), new UserPrefs());
        ListAssignmentCommand command = new ListAssignmentCommand();
        CommandResult result = command.execute(model);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(ListCommand.MESSAGE_ASSIGNMENTS, result.getFeedbackToUser());
        Assertions.assertTrue(model.getFilteredAssignmentList().isEmpty());
    }

    @Test
    public void listClassCommand_emptyAddressBook_showsEmptyList() {
        Model model = new ModelManager(new AddressBookBuilder().build(), new UserPrefs());
        ListClassCommand command = new ListClassCommand();
        CommandResult result = command.execute(model);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(ListCommand.MESSAGE_CLASSES, result.getFeedbackToUser());
        Assertions.assertTrue(model.getFilteredClassGroupList().isEmpty());
    }

    @Test
    public void listContactCommand_singleContact_showsList() {
        Model model = new ModelManager(
                new AddressBookBuilder().withContact(TypicalContacts.ALICE).build(),
                new UserPrefs());
        ListContactCommand command = new ListContactCommand();
        CommandResult result = command.execute(model);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(ListCommand.MESSAGE_CONTACTS, result.getFeedbackToUser());
        Assertions.assertEquals(1, model.getFilteredContactList().size());
    }

    @Test
    public void listContactCommand_multipleContacts_showsList() {
        Model model = new ModelManager(
                new AddressBookBuilder()
                        .withContact(TypicalContacts.ALICE)
                        .withContact(TypicalContacts.BENSON)
                        .withContact(TypicalContacts.CARL)
                        .build(),
                new UserPrefs());
        ListContactCommand command = new ListContactCommand();
        CommandResult result = command.execute(model);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(ListCommand.MESSAGE_CONTACTS, result.getFeedbackToUser());
        Assertions.assertEquals(3, model.getFilteredContactList().size());
    }

    @Test
    public void listAssignmentCommand_singleAssignment_showsList() {
        Model model = new ModelManager(
                new AddressBookBuilder().withAssignment(TypicalAssignments.ASSIGNMENT_ONE).build(),
                new UserPrefs());
        ListAssignmentCommand command = new ListAssignmentCommand();
        CommandResult result = command.execute(model);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(ListCommand.MESSAGE_ASSIGNMENTS, result.getFeedbackToUser());
        Assertions.assertEquals(1, model.getFilteredAssignmentList().size());
    }

    @Test
    public void listAssignmentCommand_multipleAssignments_showsList() {
        Model model = new ModelManager(
                new AddressBookBuilder()
                        .withAssignment(TypicalAssignments.ASSIGNMENT_ONE)
                        .withAssignment(TypicalAssignments.ASSIGNMENT_TWO)
                        .build(),
                new UserPrefs());
        ListAssignmentCommand command = new ListAssignmentCommand();
        CommandResult result = command.execute(model);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(ListCommand.MESSAGE_ASSIGNMENTS, result.getFeedbackToUser());
        Assertions.assertEquals(2, model.getFilteredAssignmentList().size());
    }

    @Test
    public void listContactCommand_resetsFilters_showsAllData() {
        Model model = new ModelManager(
                new AddressBookBuilder()
                        .withContact(TypicalContacts.ALICE)
                        .withContact(TypicalContacts.BENSON)
                        .build(),
                new UserPrefs());

        // Apply a filter that shows only some contacts
        model.updateFilteredContactList(contact -> contact.getName().fullName.startsWith("A"));
        Assertions.assertEquals(1, model.getFilteredContactList().size());

        // Execute list command which should reset filters
        ListContactCommand command = new ListContactCommand();
        command.execute(model);

        // Now all contacts should be visible
        Assertions.assertEquals(2, model.getFilteredContactList().size());
    }

    @Test
    public void listAssignmentCommand_resetsFilters_showsAllData() {
        Model model = new ModelManager(
                new AddressBookBuilder()
                        .withAssignment(TypicalAssignments.ASSIGNMENT_ONE)
                        .withAssignment(TypicalAssignments.ASSIGNMENT_TWO)
                        .build(),
                new UserPrefs());

        // Apply a predicate filter
        model.updateFilteredAssignmentList(assignment -> false); // Show none
        Assertions.assertEquals(0, model.getFilteredAssignmentList().size());

        // Execute list command
        ListAssignmentCommand command = new ListAssignmentCommand();
        command.execute(model);

        // Now all should be visible
        Assertions.assertEquals(2, model.getFilteredAssignmentList().size());
    }

    @Test
    public void listContactCommand_multipleExecutions_showsAllData() {
        Model model = new ModelManager(
                new AddressBookBuilder()
                        .withContact(TypicalContacts.ALICE)
                        .withContact(TypicalContacts.BENSON)
                        // .withAssignment(TypicalAssignments.ASSIGNMENT_ONE)
                        .build(),
                new UserPrefs());

        ListContactCommand contactCommand = new ListContactCommand();

        // Execute multiple times - should consistently show all contacts
        contactCommand.execute(model);
        int firstCount = model.getFilteredContactList().size();

        contactCommand.execute(model);
        int secondCount = model.getFilteredContactList().size();

        contactCommand.execute(model);
        int thirdCount = model.getFilteredContactList().size();

        Assertions.assertEquals(2, firstCount);
        Assertions.assertEquals(2, secondCount);
        Assertions.assertEquals(2, thirdCount);
    }

    @Test
    public void listContactCommand_correctViewType_showList() {
        Model model = new ModelManager(new AddressBookBuilder().build(), new UserPrefs());
        ListContactCommand command = new ListContactCommand();
        CommandResult result = command.execute(model);

        Assertions.assertEquals(CommandResult.ListView.CONTACTS, result.getListView());
    }

    @Test
    public void listAssignmentCommand_correctViewType_showList() {
        Model model = new ModelManager(new AddressBookBuilder().build(), new UserPrefs());
        ListAssignmentCommand command = new ListAssignmentCommand();
        CommandResult result = command.execute(model);

        Assertions.assertEquals(CommandResult.ListView.ASSIGNMENTS, result.getListView());
    }

    @Test
    public void listClassCommand_correctViewType_showList() {
        Model model = new ModelManager(new AddressBookBuilder().build(), new UserPrefs());
        ListClassCommand command = new ListClassCommand();
        CommandResult result = command.execute(model);

        Assertions.assertEquals(CommandResult.ListView.CLASSGROUPS, result.getListView());
    }

    @Test
    public void listContactCommand_nullModel_throwsNullPointerException() {
        ListContactCommand command = new ListContactCommand();
        Assertions.assertThrows(NullPointerException.class, () -> command.execute(null));
    }

    @Test
    public void listAssignmentCommand_nullModel_throwsNullPointerException() {
        ListAssignmentCommand command = new ListAssignmentCommand();
        Assertions.assertThrows(NullPointerException.class, () -> command.execute(null));
    }

    @Test
    public void listClassCommand_nullModel_throwsNullPointerException() {
        ListClassCommand command = new ListClassCommand();
        Assertions.assertThrows(NullPointerException.class, () -> command.execute(null));
    }

    @Test
    public void toString_listContactCommand() {
        ListContactCommand command = new ListContactCommand();
        String result = command.toString();
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.contains("ListContactCommand"));
    }

    @Test
    public void toString_listAssignmentCommand() {
        ListAssignmentCommand command = new ListAssignmentCommand();
        String result = command.toString();
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.contains("ListAssignmentCommand"));
    }

    @Test
    public void toString_listClassCommand() {
        ListClassCommand command = new ListClassCommand();
        String result = command.toString();
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.contains("ListClassCommand"));
    }

    @Test
    public void listContactCommand_afterModification_showsUpdatedData() {
        Model model = new ModelManager(
                new AddressBookBuilder().withContact(TypicalContacts.ALICE).build(),
                new UserPrefs());

        ListContactCommand command = new ListContactCommand();
        CommandResult result1 = command.execute(model);
        Assertions.assertNotNull(result1);
        Assertions.assertEquals(1, model.getFilteredContactList().size());

        // Add another contact
        model.addContact(TypicalContacts.BENSON);

        // Execute list again - should show updated count
        // Note: List command doesn't return the count in this implementation,
        // but the filtered list should reflect it
        command.execute(model);
        Assertions.assertEquals(2, model.getFilteredContactList().size());
    }

    @Test
    public void listAssignmentCommand_afterModification_showsUpdatedData() {
        Model model = new ModelManager(
                new AddressBookBuilder().withAssignment(TypicalAssignments.ASSIGNMENT_ONE).build(),
                new UserPrefs());

        ListAssignmentCommand command = new ListAssignmentCommand();
        CommandResult result1 = command.execute(model);
        Assertions.assertNotNull(result1);
        Assertions.assertEquals(1, model.getFilteredAssignmentList().size());

        // Add another assignment
        model.addAssignment(TypicalAssignments.ASSIGNMENT_TWO);

        // Execute list again - should show updated count
        command.execute(model);
        Assertions.assertEquals(2, model.getFilteredAssignmentList().size());
    }

    @Test
    public void listClassCommand_afterModification_showsUpdatedData() {
        Model model = new ModelManager(new AddressBookBuilder().build(), new UserPrefs());

        ListClassCommand command = new ListClassCommand();
        CommandResult result1 = command.execute(model);
        Assertions.assertNotNull(result1);
        Assertions.assertEquals(0, model.getFilteredClassGroupList().size());

        // Execute list again - should still show 0 items since nothing was added
        command.execute(model);
        Assertions.assertEquals(0, model.getFilteredClassGroupList().size());
    }
}
