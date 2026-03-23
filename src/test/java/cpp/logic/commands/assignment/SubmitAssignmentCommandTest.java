package cpp.logic.commands.assignment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cpp.logic.Messages;
import cpp.logic.commands.CommandResult;
import cpp.logic.commands.exceptions.CommandException;
import cpp.logic.parser.ParserUtil;
import cpp.model.AddressBook;
import cpp.model.ReadOnlyAddressBook;
import cpp.model.assignment.Assignment;
import cpp.model.assignment.AssignmentName;
import cpp.model.assignment.exceptions.ContactAssignmentAlreadySubmittedException;
import cpp.model.assignment.exceptions.ContactAssignmentNotFoundException;
import cpp.model.classgroup.ClassGroup;
import cpp.model.classgroup.ClassGroupName;
import cpp.model.contact.Contact;
import cpp.testutil.Assert;
import cpp.testutil.ModelStub;
import cpp.testutil.TypicalAssignments;
import cpp.testutil.TypicalContacts;
import cpp.testutil.TypicalIndexes;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class SubmitAssignmentCommandTest {

    @Test
    public void constructor_nullAssignmentName_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class,
                () -> new SubmitAssignmentCommand(null, List.of(), LocalDateTime.now()));
    }

    @Test
    public void execute_assignmentAcceptedByModel_markSuccessful() throws Exception {
        ModelStubAcceptingMarkSubmitted modelStub = new ModelStubAcceptingMarkSubmitted();
        Assignment assignment = TypicalAssignments.ASSIGNMENT_ONE;
        LocalDateTime date = LocalDateTime.parse("21-02-2026 23:50", ParserUtil.DATETIME_FORMATTER);

        SubmitAssignmentCommand cmd = new SubmitAssignmentCommand(assignment.getName(),
                ParserUtil.parseContactIndices("1 2 3"),
                date);

        CommandResult result = cmd.execute(modelStub);

        String expected = String.format(SubmitAssignmentCommand.MESSAGE_SUCCESS,
                Messages.format(assignment), date.format(ParserUtil.DATETIME_FORMATTER), 3,
                TypicalContacts.ALICE.getName().fullName + "; " + TypicalContacts.BENSON.getName().fullName + "; "
                        + TypicalContacts.CARL.getName().fullName,
                "None", "None");

        Assertions.assertEquals(expected, result.getFeedbackToUser());
    }

    @Test
    public void execute_validClassGroup_markSuccessful() {
        SubmitAssignmentCommand cmd = new SubmitAssignmentCommand(TypicalAssignments.ASSIGNMENT_ONE.getName(),
                List.of(), new ClassGroupName("ValidClassGroup"),
                LocalDateTime.parse("21-02-2026 23:50", ParserUtil.DATETIME_FORMATTER));

        ModelStubWithClassGroup modelStub = new ModelStubWithClassGroup();
        try {
            CommandResult result = cmd.execute(modelStub);
            // Benson is in class group, so 1 contact marked
            String expected = String.format(SubmitAssignmentCommand.MESSAGE_SUCCESS,
                    Messages.format(TypicalAssignments.ASSIGNMENT_ONE),
                    LocalDateTime.parse("21-02-2026 23:50", ParserUtil.DATETIME_FORMATTER)
                            .format(ParserUtil.DATETIME_FORMATTER),
                    1, TypicalContacts.BENSON.getName().fullName, "None", "None");
            Assertions.assertEquals(expected, result.getFeedbackToUser());
        } catch (CommandException e) {
            Assertions.fail("Execution should not throw CommandException.");
        }
    }

    @Test
    public void execute_duplicateBetweenContactIndicesAndClassGroup_skipsDuplicate() throws Exception {
        SubmitAssignmentCommand cmd = new SubmitAssignmentCommand(TypicalAssignments.ASSIGNMENT_ONE.getName(),
                ParserUtil.parseContactIndices("1"), new ClassGroupName("OverlapGroup"),
                LocalDateTime.parse("21-02-2026 23:50", ParserUtil.DATETIME_FORMATTER));

        ModelStubWithClassGroupOverlap modelStub = new ModelStubWithClassGroupOverlap();
        try {
            CommandResult result = cmd.execute(modelStub);
            // Alice is both in index 1 and overlap group; should be counted once
            String expectedAllocatedContacts = TypicalContacts.ALICE.getName().fullName;
            Assertions.assertTrue(result.getFeedbackToUser().contains(expectedAllocatedContacts));
        } catch (CommandException e) {
            Assertions.fail("Execution should not throw CommandException.");
        }
    }

    @Test
    public void execute_invalidContactIndex_throwsCommandException() throws Exception {
        SubmitAssignmentCommand cmd = new SubmitAssignmentCommand(TypicalAssignments.ASSIGNMENT_ONE.getName(),
                ParserUtil.parseContactIndices("100"),
                LocalDateTime.parse("21-02-2026 23:50", ParserUtil.DATETIME_FORMATTER));

        ModelStubAcceptingMarkSubmitted modelStub = new ModelStubAcceptingMarkSubmitted();
        Assert.assertThrows(CommandException.class, Messages.MESSAGE_INVALID_CONTACT_DISPLAYED_INDEX,
                () -> cmd.execute(modelStub));
    }

    @Test
    public void execute_classGroupNotFound_throwsCommandException() {
        SubmitAssignmentCommand cmd = new SubmitAssignmentCommand(TypicalAssignments.ASSIGNMENT_ONE.getName(),
                List.of(), new ClassGroupName("NonExistent"),
                LocalDateTime.parse("21-02-2026 23:50", ParserUtil.DATETIME_FORMATTER));

        ModelStubAcceptingMarkSubmitted modelStub = new ModelStubAcceptingMarkSubmitted();
        Assert.assertThrows(CommandException.class, Messages.MESSAGE_CLASS_GROUP_NOT_FOUND,
                () -> cmd.execute(modelStub));
    }

    @Test
    public void execute_emptyClassGroup_throwsCommandException() {
        SubmitAssignmentCommand cmd = new SubmitAssignmentCommand(TypicalAssignments.ASSIGNMENT_ONE.getName(),
                List.of(), new ClassGroupName("EmptyGroup"),
                LocalDateTime.parse("21-02-2026 23:50", ParserUtil.DATETIME_FORMATTER));

        ModelStubWithEmptyClassGroup modelStub = new ModelStubWithEmptyClassGroup(TypicalAssignments.ASSIGNMENT_ONE);
        Assert.assertThrows(CommandException.class, Messages.MESSAGE_CLASS_GROUP_NO_CONTACTS,
                () -> cmd.execute(modelStub));
    }

    @Test
    public void execute_assignmentNotFound_throwsCommandException() throws Exception {
        ModelStubAcceptingMarkSubmitted modelStub = new ModelStubAcceptingMarkSubmitted();
        SubmitAssignmentCommand cmd = new SubmitAssignmentCommand(new AssignmentName("NonExistent"),
                ParserUtil.parseContactIndices("1"), LocalDateTime.now());

        Assert.assertThrows(CommandException.class, Messages.MESSAGE_ASSIGNMENT_NOT_FOUND,
                () -> cmd.execute(modelStub));
    }

    @Test
    public void execute_noContactsMarked_throwsCommandException() throws Exception {
        ModelStubMarkAllNotAllocated modelStub = new ModelStubMarkAllNotAllocated();
        Assignment assignment = TypicalAssignments.ASSIGNMENT_ONE;
        SubmitAssignmentCommand cmd = new SubmitAssignmentCommand(assignment.getName(),
                ParserUtil.parseContactIndices("1"),
                LocalDateTime.now());

        Assert.assertThrows(CommandException.class, String.format(SubmitAssignmentCommand.MESSAGE_SUBMISSION_FAILED,
                "None", TypicalContacts.ALICE.getName().fullName),
                () -> cmd.execute(modelStub));
    }

    @Test
    public void execute_mixedMarkedAndAlreadyMarked_marksOne() throws Exception {
        // indices: 3 (Carl) will be marked, 1 (Alice) and 2 (Benson) already submitted
        SubmitAssignmentCommand cmd = new SubmitAssignmentCommand(TypicalAssignments.ASSIGNMENT_ONE.getName(),
                ParserUtil.parseContactIndices("3 1 2"),
                LocalDateTime.parse("21-02-2026 23:50", ParserUtil.DATETIME_FORMATTER));

        ModelStubTwoAlreadyMarked modelStub = new ModelStubTwoAlreadyMarked();
        try {
            CommandResult result = cmd.execute(modelStub);
            // markedCount = 1, alreadyMarkedCount = 2
            Assertions.assertTrue(result.getFeedbackToUser().contains("1"));
            Assertions.assertTrue(result.getFeedbackToUser().contains("2"));
            Assertions.assertTrue(result.getFeedbackToUser().contains(TypicalContacts.CARL.getName().fullName));
        } catch (CommandException e) {
            Assertions.fail("Execution should not throw CommandException.");
        }
    }

    @Test
    public void execute_mixedMarkedAndNotAllocated_hasTwoNotAllocated() throws Exception {
        // indices: 3 (Carl) will be marked, 1 (Alice) and 2 (Benson) not allocated
        SubmitAssignmentCommand cmd = new SubmitAssignmentCommand(TypicalAssignments.ASSIGNMENT_ONE.getName(),
                ParserUtil.parseContactIndices("3 1 2"),
                LocalDateTime.parse("21-02-2026 23:50", ParserUtil.DATETIME_FORMATTER));

        ModelStubTwoNotAllocated modelStub = new ModelStubTwoNotAllocated();
        try {
            CommandResult result = cmd.execute(modelStub);
            // markedCount = 1, notAllocatedCount = 2
            Assertions.assertTrue(result.getFeedbackToUser().contains("1"));
            Assertions.assertTrue(result.getFeedbackToUser().contains("2"));
            Assertions.assertTrue(result.getFeedbackToUser().contains(TypicalContacts.CARL.getName().fullName));
        } catch (CommandException e) {
            Assertions.fail("Execution should not throw CommandException.");
        }
    }

    @Test
    public void equals_sameValues_returnsTrue() throws Exception {
        Assignment assignment = TypicalAssignments.ASSIGNMENT_ONE;
        SubmitAssignmentCommand cmd1 = new SubmitAssignmentCommand(assignment.getName(),
                ParserUtil.parseContactIndices("1 2"),
                LocalDateTime.parse("21-02-2026 23:50", ParserUtil.DATETIME_FORMATTER));
        SubmitAssignmentCommand cmd2 = new SubmitAssignmentCommand(assignment.getName(),
                ParserUtil.parseContactIndices("1 2"),
                LocalDateTime.parse("21-02-2026 23:50", ParserUtil.DATETIME_FORMATTER));

        Assertions.assertTrue(cmd1.equals(cmd1));
        Assertions.assertTrue(cmd1.equals(cmd2));
    }

    @Test
    public void equals_differentValues_returnsFalse() throws Exception {
        Assignment assignment = TypicalAssignments.ASSIGNMENT_ONE;
        SubmitAssignmentCommand cmd = new SubmitAssignmentCommand(assignment.getName(),
                ParserUtil.parseContactIndices("1 2"),
                LocalDateTime.parse("21-02-2026 23:50", ParserUtil.DATETIME_FORMATTER));

        // different type
        Assertions.assertFalse(cmd.equals(1));
        // null
        Assertions.assertFalse(cmd.equals(null));

        // different assignment name
        SubmitAssignmentCommand diff = new SubmitAssignmentCommand(new AssignmentName("Different"),
                ParserUtil.parseContactIndices("1 2"),
                LocalDateTime.parse("21-02-2026 23:50", ParserUtil.DATETIME_FORMATTER));
        Assertions.assertFalse(cmd.equals(diff));

        // different contact indices
        diff = new SubmitAssignmentCommand(assignment.getName(),
                ParserUtil.parseContactIndices("2 3"),
                LocalDateTime.parse("21-02-2026 23:50", ParserUtil.DATETIME_FORMATTER));
        Assertions.assertFalse(cmd.equals(diff));

        // different class group
        diff = new SubmitAssignmentCommand(assignment.getName(),
                ParserUtil.parseContactIndices("1 2"),
                new ClassGroupName("DifferentGroup"),
                LocalDateTime.parse("21-02-2026 23:50", ParserUtil.DATETIME_FORMATTER));
        Assertions.assertFalse(cmd.equals(diff));

        // different submission date
        diff = new SubmitAssignmentCommand(assignment.getName(),
                ParserUtil.parseContactIndices("1 2"),
                LocalDateTime.parse("22-02-2026 23:50", ParserUtil.DATETIME_FORMATTER));
        Assertions.assertFalse(cmd.equals(diff));
    }

    @Test
    public void toString_typicalValue_correctOutput() {
        SubmitAssignmentCommand cmd = new SubmitAssignmentCommand(TypicalAssignments.ASSIGNMENT_ONE.getName(),
                List.of(TypicalIndexes.INDEX_FIRST_CONTACT, TypicalIndexes.INDEX_SECOND_CONTACT),
                LocalDateTime.parse("21-02-2026 23:50", ParserUtil.DATETIME_FORMATTER));
        String expected = SubmitAssignmentCommand.class.getCanonicalName() + "{"
                + "assignmentName=" + TypicalAssignments.ASSIGNMENT_ONE.getName()
                + ", contactIndices=[" + TypicalIndexes.INDEX_FIRST_CONTACT + ", " + TypicalIndexes.INDEX_SECOND_CONTACT
                + "]"
                + ", classGroupName=null"
                + ", submissionDate="
                + LocalDateTime.parse("21-02-2026 23:50", ParserUtil.DATETIME_FORMATTER)
                        .format(ParserUtil.DATETIME_FORMATTER)
                + "}";
        Assertions.assertEquals(expected, cmd.toString());
    }

    /**
     * A Model stub that accepts markSubmitted calls.
     */
    private class ModelStubAcceptingMarkSubmitted extends ModelStub {

        @Override
        public ObservableList<Contact> getFilteredContactList() {
            return FXCollections.observableArrayList(TypicalContacts.getTypicalContacts());
        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            AddressBook ab = new AddressBook();
            for (Contact c : TypicalContacts.getTypicalContacts()) {
                ab.addContact(c);
            }
            ab.addAssignment(TypicalAssignments.ASSIGNMENT_ONE);
            return ab;
        }

        @Override
        public void markSubmitted(Assignment assignment, Contact contact, LocalDateTime submissionDate) {
            Objects.requireNonNull(assignment);
            Objects.requireNonNull(contact);
            // succeed silently
        }
    }

    private class ModelStubTwoAlreadyMarked extends ModelStubAcceptingMarkSubmitted {
        @Override
        public void markSubmitted(Assignment assignment, Contact contact, LocalDateTime submissionDate)
                throws ContactAssignmentAlreadySubmittedException {
            if (contact.getId().equals(TypicalContacts.ALICE.getId())
                    || contact.getId().equals(TypicalContacts.BENSON.getId())) {
                throw new ContactAssignmentAlreadySubmittedException();
            }
        }
    }

    private class ModelStubTwoNotAllocated extends ModelStubAcceptingMarkSubmitted {
        @Override
        public void markSubmitted(Assignment assignment, Contact contact, LocalDateTime submissionDate)
                throws ContactAssignmentNotFoundException {
            if (contact.getId().equals(TypicalContacts.ALICE.getId())
                    || contact.getId().equals(TypicalContacts.BENSON.getId())) {
                throw new ContactAssignmentNotFoundException();
            }
        }
    }

    private class ModelStubMarkAllNotAllocated extends ModelStubAcceptingMarkSubmitted {
        @Override
        public void markSubmitted(Assignment assignment, Contact contact, LocalDateTime submissionDate)
                throws ContactAssignmentNotFoundException {
            throw new ContactAssignmentNotFoundException();
        }
    }

    private class ModelStubWithClassGroup extends ModelStubAcceptingMarkSubmitted {
        @Override
        public boolean hasClassGroup(ClassGroup classGroup) {
            return classGroup.getName().equals(new ClassGroupName("ValidClassGroup"));
        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            AddressBook ab = new AddressBook();
            for (Contact c : TypicalContacts.getTypicalContacts()) {
                ab.addContact(c);
            }
            ClassGroup cg = new ClassGroup(new ClassGroupName("ValidClassGroup"));
            try {
                cg.allocateContact(TypicalContacts.BENSON.getId());
                cg.allocateContact(TypicalContacts.BOB.getId());
            } catch (Exception e) {
                // ignore
            }
            ab.addClassGroup(cg);
            ab.addAssignment(TypicalAssignments.ASSIGNMENT_ONE);
            return ab;
        }
    }

    private class ModelStubWithClassGroupOverlap extends ModelStubAcceptingMarkSubmitted {
        @Override
        public boolean hasClassGroup(ClassGroup classGroup) {
            return classGroup.getName().equals(new ClassGroupName("OverlapGroup"));
        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            AddressBook ab = new AddressBook();
            for (Contact c : TypicalContacts.getTypicalContacts()) {
                ab.addContact(c);
            }
            ClassGroup cg = new ClassGroup(new ClassGroupName("OverlapGroup"));
            try {
                cg.allocateContact(TypicalContacts.ALICE.getId());
            } catch (Exception e) {
                // ignore
            }
            ab.addClassGroup(cg);
            ab.addAssignment(TypicalAssignments.ASSIGNMENT_ONE);
            return ab;
        }
    }

    public class ModelStubWithEmptyClassGroup extends ModelStub {
        private final Assignment assignment;

        ModelStubWithEmptyClassGroup(Assignment assignment) {
            Objects.requireNonNull(assignment);
            this.assignment = assignment;
        }

        @Override
        public ObservableList<Contact> getFilteredContactList() {
            return FXCollections.observableArrayList(TypicalContacts.getTypicalContacts());
        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            AddressBook ab = new AddressBook();
            for (Contact c : TypicalContacts.getTypicalContacts()) {
                ab.addContact(c);
            }
            ab.addAssignment(this.assignment);
            ClassGroup cg = new ClassGroup(new ClassGroupName("EmptyGroup"));
            ab.addClassGroup(cg);
            return ab;
        }
    }

}
