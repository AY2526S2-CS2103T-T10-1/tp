package cpp.logic.commands;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import cpp.logic.Messages;
import cpp.model.Model;
import cpp.model.ModelManager;
import cpp.model.UserPrefs;
import cpp.model.assignment.Assignment;
import cpp.model.assignment.AssignmentName;
import cpp.model.assignment.ContactAssignment;
import cpp.model.classgroup.ClassGroup;
import cpp.model.classgroup.ClassGroupName;
import cpp.model.contact.Contact;
import cpp.testutil.ClassGroupBuilder;
import cpp.testutil.ContactBuilder;
import cpp.testutil.TypicalClassGroups;
import cpp.testutil.TypicalContacts;

/**
 * Contains integration tests (interaction with the Model) for
 * {@code AddContactCommand}.
 */
public class AddContactCommandIntegrationTest {

    private Model model;

    @BeforeEach
    public void setUp() {
        this.model = new ModelManager(TypicalContacts.getTypicalAddressBook(), new UserPrefs());
    }

    @Test
    public void execute_newContact_success() {
        Contact validContact = new ContactBuilder().build();

        Model expectedModel = new ModelManager(this.model.getAddressBook(), new UserPrefs());
        expectedModel.addContact(validContact);

        CommandTestUtil.assertCommandSuccess(new AddContactCommand(validContact), this.model,
                String.format(AddContactCommand.MESSAGE_SUCCESS, Messages.format(validContact), "", ""),
                expectedModel);
    }

    @Test
    public void execute_duplicateContact_commandFailure() {
        Contact contactInList = this.model.getAddressBook().getContactList().get(0);
        CommandTestUtil.assertCommandFailure(new AddContactCommand(contactInList), this.model,
                AddContactCommand.MESSAGE_DUPLICATE_CONTACT);
    }

    @Test
    public void execute_nonexistentClassGroup_commandFailure() {
        Contact validContact = new ContactBuilder().build();

        CommandTestUtil.assertCommandFailure(
                new AddContactCommand(validContact, new ClassGroupName("CS2103T10"), null),
                this.model,
                Messages.MESSAGE_CLASS_GROUP_NOT_FOUND);
    }

    @Test
    public void execute_nonexistentAssignment_commandFailure() {
        Contact validContact = new ContactBuilder().build();

        CommandTestUtil.assertCommandFailure(
                new AddContactCommand(validContact, null, new AssignmentName("Assignment 9")),
                this.model,
                Messages.MESSAGE_ASSIGNMENT_NOT_FOUND);
    }

    @Test
    public void execute_newContactWithAssignment_success() {
        Contact validContact = new ContactBuilder().build();
        Assignment assignment = this.model.getAddressBook().getAssignmentList().get(0);

        Model expectedModel = new ModelManager(this.model.getAddressBook(), new UserPrefs());
        expectedModel.addContact(validContact);
        expectedModel.addContactAssignment(new ContactAssignment(assignment.getId(), validContact.getId()));

        CommandTestUtil.assertCommandSuccess(
                new AddContactCommand(validContact, null, assignment.getName()), this.model,
                String.format(AddContactCommand.MESSAGE_SUCCESS, Messages.format(validContact), "",
                        String.format(AddContactCommand.MESSAGE_SUCCESS_ASSIGNMENT_ALLOCATION,
                                Messages.format(assignment))),
                expectedModel);
    }

    @Test
    public void execute_newContactWithClassGroup_success() throws Exception {
        Contact validContact = new ContactBuilder().build();
        ClassGroup classGroup = new ClassGroupBuilder(TypicalClassGroups.CLASS_GROUP_ONE).build();
        ClassGroupName classGroupName = classGroup.getName();
        this.model.addClassGroup(classGroup);

        CommandResult commandResult = new AddContactCommand(validContact, classGroupName, null).execute(this.model);

        Assertions.assertEquals(
                String.format(AddContactCommand.MESSAGE_SUCCESS, Messages.format(validContact),
                        String.format(AddContactCommand.MESSAGE_SUCCESS_CLASS_GROUP_ALLOCATION,
                                Messages.format(classGroup)),
                        ""),
                commandResult.getFeedbackToUser());
        Assertions.assertTrue(this.model.hasContact(validContact));
        ClassGroup actualClassGroup = this.model.getAddressBook().getClassGroupList().stream()
                .filter(cg -> cg.getName().equals(classGroupName))
                .findFirst()
                .orElseThrow();
        Assertions.assertTrue(actualClassGroup.getContactIdSet().contains(validContact.getId()));
    }

    @Test
    public void execute_newContactWithAssignmentAndClassGroup_success() throws Exception {
        Contact validContact = new ContactBuilder().build();
        Assignment assignment = this.model.getAddressBook().getAssignmentList().get(0);
        ClassGroup classGroup = new ClassGroupBuilder(TypicalClassGroups.CLASS_GROUP_ONE).build();
        ClassGroupName classGroupName = classGroup.getName();
        this.model.addClassGroup(classGroup);

        CommandResult commandResult = new AddContactCommand(validContact, classGroupName, assignment.getName())
                .execute(this.model);

        Assertions.assertEquals(
                String.format(AddContactCommand.MESSAGE_SUCCESS, Messages.format(validContact),
                        String.format(AddContactCommand.MESSAGE_SUCCESS_CLASS_GROUP_ALLOCATION,
                                Messages.format(classGroup)),
                        String.format(AddContactCommand.MESSAGE_SUCCESS_ASSIGNMENT_ALLOCATION,
                                Messages.format(assignment))),
                commandResult.getFeedbackToUser());
        Assertions.assertTrue(this.model.hasContact(validContact));
        Assertions.assertTrue(this.model.getAddressBook().getContactAssignmentList()
                .contains(new ContactAssignment(assignment.getId(), validContact.getId())));
        ClassGroup actualClassGroup = this.model.getAddressBook().getClassGroupList().stream()
                .filter(cg -> cg.getName().equals(classGroupName))
                .findFirst()
                .orElseThrow();
        Assertions.assertTrue(actualClassGroup.getContactIdSet().contains(validContact.getId()));
    }

}
