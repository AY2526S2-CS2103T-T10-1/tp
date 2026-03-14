package cpp.logic.commands.assignment;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Predicate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cpp.commons.core.GuiSettings;
import cpp.logic.Messages;
import cpp.logic.commands.CommandResult;
import cpp.logic.commands.exceptions.CommandException;
import cpp.model.AddressBook;
import cpp.model.Model;
import cpp.model.ReadOnlyAddressBook;
import cpp.model.ReadOnlyUserPrefs;
import cpp.model.assignment.Assignment;
import cpp.model.assignment.ContactAssignment;
import cpp.model.classgroup.ClassGroup;
import cpp.model.contact.Contact;
import cpp.testutil.Assert;
import cpp.testutil.AssignmentBuilder;
import cpp.testutil.TypicalAssignments;
import javafx.collections.ObservableList;

public class AddAssignmentCommandTest {

    @Test
    public void constructor_nullAssignment_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class, () -> new AddAssignmentCommand(null));
    }

    @Test
    public void execute_assignmentAcceptedByModel_addSuccessful() throws Exception {
        ModelStubAcceptingAssignmentAdded modelStub = new ModelStubAcceptingAssignmentAdded();
        Assignment validAssignment = TypicalAssignments.ASSIGNMENT_ONE;

        CommandResult commandResult = new AddAssignmentCommand(validAssignment).execute(modelStub);

        Assertions.assertEquals(String.format(AddAssignmentCommand.MESSAGE_SUCCESS, Messages.format(validAssignment)),
                commandResult.getFeedbackToUser());
        Assertions.assertEquals(1, modelStub.assignmentsAdded.size());
        Assertions.assertEquals(validAssignment, modelStub.assignmentsAdded.get(0));
    }

    @Test
    public void execute_duplicateAssignment_throwsCommandException() {
        Assignment validAssignment = TypicalAssignments.ASSIGNMENT_ONE;
        AddAssignmentCommand addCommand = new AddAssignmentCommand(validAssignment);
        ModelStub modelStub = new ModelStubWithAssignment(validAssignment);

        Assert.assertThrows(CommandException.class, AddAssignmentCommand.MESSAGE_DUPLICATE_ASSIGNMENT,
                () -> addCommand.execute(modelStub));
    }

    @Test
    public void equals_sameValues_returnsTrue() {
        Assignment assignment = TypicalAssignments.ASSIGNMENT_ONE;
        AddAssignmentCommand addCommand = new AddAssignmentCommand(assignment);
        AddAssignmentCommand addCommandCopy = new AddAssignmentCommand(assignment);

        // same object -> true
        Assertions.assertTrue(addCommand.equals(addCommand));

        // same values -> true
        Assertions.assertTrue(addCommand.equals(addCommandCopy));
    }

    @Test
    public void equals_differentValues_returnsFalse() {
        Assignment assignment = TypicalAssignments.ASSIGNMENT_ONE;
        AddAssignmentCommand addCommand = new AddAssignmentCommand(assignment);

        // different types -> false
        Assertions.assertFalse(addCommand.equals(1));

        // null -> false
        Assertions.assertFalse(addCommand.equals(null));

        // different assignment -> false
        Assignment different = new AssignmentBuilder(TypicalAssignments.ASSIGNMENT_ONE)
                .withName("Different").build();
        AddAssignmentCommand differentCommand = new AddAssignmentCommand(different);
        Assertions.assertFalse(addCommand.equals(differentCommand));
    }

    @Test
    public void toString_typicalValue_correctOutput() {
        AddAssignmentCommand addCommand = new AddAssignmentCommand(TypicalAssignments.ASSIGNMENT_ONE);
        String expected = AddAssignmentCommand.class.getCanonicalName() + "{toAddAssignment="
                + TypicalAssignments.ASSIGNMENT_ONE + "}";
        Assertions.assertEquals(expected, addCommand.toString());
    }

    /**
     * A default model stub that have all of the methods failing.
     */
    private class ModelStub implements Model {
        @Override
        public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ReadOnlyUserPrefs getUserPrefs() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public GuiSettings getGuiSettings() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setGuiSettings(GuiSettings guiSettings) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Path getAddressBookFilePath() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setAddressBookFilePath(Path addressBookFilePath) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void addContact(Contact contact) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setAddressBook(ReadOnlyAddressBook newData) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean hasContact(Contact contact) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void deleteContact(Contact target) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setContact(Contact target, Contact editedContact) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableList<Contact> getFilteredContactList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void updateFilteredContactList(Predicate<Contact> predicate) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean hasAssignment(Assignment assignment) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void addAssignment(Assignment assignment) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void deleteAssignment(Assignment target) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void addContactAssignment(ContactAssignment ca) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void removeContactAssignment(ContactAssignment ca) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean hasClassGroup(ClassGroup classGroup) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void addClassGroup(ClassGroup classGroup) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setClassGroup(ClassGroup target, ClassGroup editedClassGroup) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void deleteClassGroup(ClassGroup target) {
            throw new AssertionError("This method should not be called.");
        }
    }

    /**
     * A Model stub that contains a single assignment.
     */
    private class ModelStubWithAssignment extends ModelStub {
        private final Assignment assignment;

        ModelStubWithAssignment(Assignment assignment) {
            Objects.requireNonNull(assignment);
            this.assignment = assignment;
        }

        @Override
        public boolean hasAssignment(Assignment assignment) {
            Objects.requireNonNull(assignment);
            return this.assignment.getName().equals(assignment.getName());
        }
    }

    /**
     * A Model stub that always accept the assignment being added.
     */
    private class ModelStubAcceptingAssignmentAdded extends ModelStub {
        final ArrayList<Assignment> assignmentsAdded = new ArrayList<>();

        @Override
        public boolean hasAssignment(Assignment assignment) {
            Objects.requireNonNull(assignment);
            return this.assignmentsAdded.stream().anyMatch(a -> a.getName().equals(assignment.getName()));
        }

        @Override
        public void addAssignment(Assignment assignment) {
            Objects.requireNonNull(assignment);
            this.assignmentsAdded.add(assignment);
        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            return new AddressBook();
        }
    }

}
