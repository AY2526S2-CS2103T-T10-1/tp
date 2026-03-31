package cpp.logic.parser;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cpp.logic.Messages;
import cpp.logic.commands.AddContactCommand;
import cpp.logic.commands.ClearCommand;
import cpp.logic.commands.CommandTestUtil;
import cpp.logic.commands.DeleteCommand;
import cpp.logic.commands.DeleteContactCommand;
import cpp.logic.commands.EditContactCommand;
import cpp.logic.commands.EditContactCommand.EditContactDescriptor;
import cpp.logic.commands.ExitCommand;
import cpp.logic.commands.FindContactCommand;
import cpp.logic.commands.HelpCommand;
import cpp.logic.commands.ListCommand;
import cpp.logic.commands.assignment.AddAssignmentCommand;
import cpp.logic.commands.assignment.AllocateAssignmentCommand;
import cpp.logic.commands.assignment.EditAssignmentCommand;
import cpp.logic.commands.assignment.EditAssignmentCommand.EditAssignmentDescriptor;
import cpp.logic.commands.assignment.GradeAssignmentCommand;
import cpp.logic.commands.assignment.SubmitAssignmentCommand;
import cpp.logic.commands.assignment.UnallocateAssignmentCommand;
import cpp.logic.commands.assignment.UngradeAssignmentCommand;
import cpp.logic.commands.assignment.UnsubmitAssignmentCommand;
import cpp.logic.commands.classgroup.AddClassGroupCommand;
import cpp.logic.commands.classgroup.AllocateClassGroupCommand;
import cpp.logic.commands.classgroup.EditClassGroupCommand;
import cpp.logic.commands.classgroup.UnallocateClassGroupCommand;
import cpp.logic.parser.exceptions.ParseException;
import cpp.model.assignment.Assignment;
import cpp.model.assignment.AssignmentName;
import cpp.model.classgroup.ClassGroup;
import cpp.model.classgroup.ClassGroupName;
import cpp.model.contact.Contact;
import cpp.model.contact.ContactNameContainsKeywordsPredicate;
import cpp.testutil.Assert;
import cpp.testutil.AssignmentBuilder;
import cpp.testutil.AssignmentUtil;
import cpp.testutil.ClassGroupBuilder;
import cpp.testutil.ClassGroupUtil;
import cpp.testutil.ContactBuilder;
import cpp.testutil.ContactUtil;
import cpp.testutil.EditContactDescriptorBuilder;
import cpp.testutil.TypicalAssignments;
import cpp.testutil.TypicalClassGroups;
import cpp.testutil.TypicalIndexes;

public class AddressBookParserTest {

    private final AddressBookParser parser = new AddressBookParser();

    @Test
    public void parseCommand_add() throws Exception {
        Contact contact = new ContactBuilder().build();
        AddContactCommand command = (AddContactCommand) this.parser
                .parseCommand(ContactUtil.getAddContactCommand(contact));
        Assertions.assertEquals(new AddContactCommand(contact), command);
    }

    @Test
    public void parseCommand_clear() throws Exception {
        Assertions.assertTrue(this.parser.parseCommand(ClearCommand.COMMAND_WORD) instanceof ClearCommand);
        Assertions.assertTrue(this.parser.parseCommand(ClearCommand.COMMAND_WORD + " 3") instanceof ClearCommand);
    }

    @Test
    public void parseCommand_delete() throws Exception {
        DeleteContactCommand command = (DeleteContactCommand) this.parser.parseCommand(
                DeleteCommand.COMMAND_WORD + " ct/" + TypicalIndexes.INDEX_FIRST_CONTACT.getOneBased());
        Assertions.assertEquals(new DeleteContactCommand(List.of(TypicalIndexes.INDEX_FIRST_CONTACT)), command);
    }

    @Test
    public void parseCommand_edit() throws Exception {
        Contact contact = new ContactBuilder().build();
        EditContactDescriptor descriptor = new EditContactDescriptorBuilder(contact).build();
        EditContactCommand command = (EditContactCommand) this.parser.parseCommand(EditContactCommand.COMMAND_WORD + " "
                + TypicalIndexes.INDEX_FIRST_CONTACT.getOneBased() + " "
                + ContactUtil.getEditContactDescriptorDetails(descriptor));
        Assertions.assertEquals(new EditContactCommand(TypicalIndexes.INDEX_FIRST_CONTACT, descriptor), command);
    }

    @Test
    public void parseCommand_exit() throws Exception {
        Assertions.assertTrue(this.parser.parseCommand(ExitCommand.COMMAND_WORD) instanceof ExitCommand);
        Assertions.assertTrue(this.parser.parseCommand(ExitCommand.COMMAND_WORD + " 3") instanceof ExitCommand);
    }

    @Test
    public void parseCommand_find() throws Exception {
        List<String> keywords = Arrays.asList("foo", "bar", "baz");
        FindContactCommand command = (FindContactCommand) this.parser.parseCommand(
                FindContactCommand.COMMAND_WORD + " " + keywords.stream().collect(Collectors.joining(" ")));
        Assertions.assertEquals(new FindContactCommand(new ContactNameContainsKeywordsPredicate(keywords)), command);
    }

    @Test
    public void parseCommand_help() throws Exception {
        Assertions.assertTrue(this.parser.parseCommand(HelpCommand.COMMAND_WORD) instanceof HelpCommand);
        Assertions.assertTrue(this.parser.parseCommand(HelpCommand.COMMAND_WORD + " 3") instanceof HelpCommand);
    }

    @Test
    public void parseCommand_list() throws Exception {
        Assertions.assertTrue(this.parser.parseCommand(ListCommand.COMMAND_WORD + " contacts") instanceof ListCommand);
        Assertions
                .assertTrue(this.parser.parseCommand(ListCommand.COMMAND_WORD + " assignments") instanceof ListCommand);
        Assertions.assertTrue(this.parser.parseCommand(ListCommand.COMMAND_WORD + " classes") instanceof ListCommand);
    }

    @Test
    public void parseCommand_addAssignment() throws Exception {
        Assignment assignment = new AssignmentBuilder(TypicalAssignments.ASSIGNMENT_ONE).build();
        AddAssignmentCommand command = (AddAssignmentCommand) this.parser
                .parseCommand(AssignmentUtil.getAddAssignmentCommand(assignment));
        Assertions.assertEquals(new AddAssignmentCommand(assignment, List.of()), command);
    }

    @Test
    public void parseCommand_allocateAssignment() throws Exception {
        Assignment sampleAssignment = new AssignmentBuilder(TypicalAssignments.ASSIGNMENT_ONE).build();
        AllocateAssignmentCommand command = (AllocateAssignmentCommand) this.parser
                .parseCommand(AllocateAssignmentCommand.COMMAND_WORD + " " + CliSyntax.PREFIX_ASSIGNMENT
                        + sampleAssignment.getName().fullName + " " + CommandTestUtil.CONTACT_INDICES_MULTIPLE);
        Assertions.assertEquals(new AllocateAssignmentCommand(sampleAssignment.getName(), new ArrayList<>(Arrays.asList(
                TypicalIndexes.INDEX_FIRST_CONTACT, TypicalIndexes.INDEX_SECOND_CONTACT,
                TypicalIndexes.INDEX_THIRD_CONTACT))), command);

        AllocateAssignmentCommand commandWithClassGroup = (AllocateAssignmentCommand) this.parser
                .parseCommand(AllocateAssignmentCommand.COMMAND_WORD + " " + CliSyntax.PREFIX_ASSIGNMENT
                        + sampleAssignment.getName().fullName + " " + CliSyntax.PREFIX_CLASS
                        + TypicalClassGroups.CLASS_GROUP_ONE.getName().fullName);
        Assertions.assertEquals(new AllocateAssignmentCommand(sampleAssignment.getName(), new ArrayList<>(),
                TypicalClassGroups.CLASS_GROUP_ONE.getName()), commandWithClassGroup);
    }

    @Test
    public void parseCommand_unallocateAssignment() throws Exception {
        Assignment sampleAssignment = new AssignmentBuilder(TypicalAssignments.ASSIGNMENT_ONE).build();
        UnallocateAssignmentCommand command = (UnallocateAssignmentCommand) this.parser
                .parseCommand(UnallocateAssignmentCommand.COMMAND_WORD + " " + CliSyntax.PREFIX_ASSIGNMENT
                        + sampleAssignment.getName().fullName + " " + CommandTestUtil.CONTACT_INDICES_MULTIPLE);
        Assertions.assertEquals(new UnallocateAssignmentCommand(sampleAssignment.getName(),
                new ArrayList<>(Arrays.asList(
                        TypicalIndexes.INDEX_FIRST_CONTACT, TypicalIndexes.INDEX_SECOND_CONTACT,
                        TypicalIndexes.INDEX_THIRD_CONTACT))),
                command);
    }

    @Test
    public void parseCommand_submitAssignment() throws Exception {
        Assignment sampleAssignment = new AssignmentBuilder(TypicalAssignments.ASSIGNMENT_ONE).build();
        LocalDateTime submissionDate = LocalDateTime.now();
        LocalDateTime expectedSubmissionDate = LocalDateTime.parse(submissionDate.format(ParserUtil.DATETIME_FORMATTER),
                ParserUtil.DATETIME_FORMATTER);
        SubmitAssignmentCommand command = (SubmitAssignmentCommand) this.parser
                .parseCommand(SubmitAssignmentCommand.COMMAND_WORD + " " + CliSyntax.PREFIX_ASSIGNMENT
                        + sampleAssignment.getName().fullName + " " + CommandTestUtil.CONTACT_INDICES_MULTIPLE + " "
                        + CliSyntax.PREFIX_DATETIME + submissionDate.format(ParserUtil.DATETIME_FORMATTER));
        Assertions.assertEquals(new SubmitAssignmentCommand(sampleAssignment.getName(),
                new ArrayList<>(Arrays.asList(
                        TypicalIndexes.INDEX_FIRST_CONTACT, TypicalIndexes.INDEX_SECOND_CONTACT,
                        TypicalIndexes.INDEX_THIRD_CONTACT)),
                expectedSubmissionDate),
                command);

        SubmitAssignmentCommand commandWithClassGroup = (SubmitAssignmentCommand) this.parser
                .parseCommand(SubmitAssignmentCommand.COMMAND_WORD + " " + CliSyntax.PREFIX_ASSIGNMENT
                        + sampleAssignment.getName().fullName + " " + CliSyntax.PREFIX_CLASS
                        + TypicalClassGroups.CLASS_GROUP_ONE.getName().fullName + " " + CliSyntax.PREFIX_DATETIME
                        + submissionDate.format(ParserUtil.DATETIME_FORMATTER));
        Assertions.assertEquals(new SubmitAssignmentCommand(sampleAssignment.getName(), new ArrayList<>(),
                TypicalClassGroups.CLASS_GROUP_ONE.getName(), expectedSubmissionDate), commandWithClassGroup);
    }

    @Test
    public void parseCommand_unsubmitAssignment() throws Exception {
        Assignment sampleAssignment = new AssignmentBuilder(TypicalAssignments.ASSIGNMENT_ONE).build();
        UnsubmitAssignmentCommand command = (UnsubmitAssignmentCommand) this.parser
                .parseCommand(UnsubmitAssignmentCommand.COMMAND_WORD + " " + CliSyntax.PREFIX_ASSIGNMENT
                        + sampleAssignment.getName().fullName + " " + CommandTestUtil.CONTACT_INDICES_MULTIPLE);
        Assertions.assertEquals(new UnsubmitAssignmentCommand(sampleAssignment.getName(),
                new ArrayList<>(Arrays.asList(
                        TypicalIndexes.INDEX_FIRST_CONTACT, TypicalIndexes.INDEX_SECOND_CONTACT,
                        TypicalIndexes.INDEX_THIRD_CONTACT))),
                command);

        UnsubmitAssignmentCommand commandWithClassGroup = (UnsubmitAssignmentCommand) this.parser
                .parseCommand(UnsubmitAssignmentCommand.COMMAND_WORD + " " + CliSyntax.PREFIX_ASSIGNMENT
                        + sampleAssignment.getName().fullName + " " + CliSyntax.PREFIX_CLASS
                        + TypicalClassGroups.CLASS_GROUP_ONE.getName().fullName);
        Assertions.assertEquals(new UnsubmitAssignmentCommand(sampleAssignment.getName(), new ArrayList<>(),
                TypicalClassGroups.CLASS_GROUP_ONE.getName()), commandWithClassGroup);
    }

    @Test
    public void parseCommand_gradeAssignment() throws Exception {
        Assignment sampleAssignment = new AssignmentBuilder(TypicalAssignments.ASSIGNMENT_ONE).build();
        LocalDateTime gradingDate = LocalDateTime.now();
        LocalDateTime expectedGradingDate = LocalDateTime.parse(gradingDate.format(ParserUtil.DATETIME_FORMATTER),
                ParserUtil.DATETIME_FORMATTER);
        GradeAssignmentCommand command = (GradeAssignmentCommand) this.parser
                .parseCommand(GradeAssignmentCommand.COMMAND_WORD + " " + CliSyntax.PREFIX_ASSIGNMENT
                        + sampleAssignment.getName().fullName + " " + CommandTestUtil.CONTACT_INDICES_MULTIPLE + " "
                        + CliSyntax.PREFIX_SCORE + "85" + " " + CliSyntax.PREFIX_DATETIME
                        + gradingDate.format(ParserUtil.DATETIME_FORMATTER));
        Assertions.assertEquals(new GradeAssignmentCommand(sampleAssignment.getName(),
                new ArrayList<>(Arrays.asList(
                        TypicalIndexes.INDEX_FIRST_CONTACT, TypicalIndexes.INDEX_SECOND_CONTACT,
                        TypicalIndexes.INDEX_THIRD_CONTACT)),
                85f, expectedGradingDate),
                command);
    }

    @Test
    public void parseCommand_ungradeAssignment() throws Exception {
        Assignment sampleAssignment = new AssignmentBuilder(TypicalAssignments.ASSIGNMENT_ONE).build();
        UngradeAssignmentCommand command = (UngradeAssignmentCommand) this.parser
                .parseCommand(UngradeAssignmentCommand.COMMAND_WORD + " " + CliSyntax.PREFIX_ASSIGNMENT
                        + sampleAssignment.getName().fullName + " " + CommandTestUtil.CONTACT_INDICES_MULTIPLE);
        Assertions.assertEquals(new UngradeAssignmentCommand(sampleAssignment.getName(),
                new ArrayList<>(Arrays.asList(
                        TypicalIndexes.INDEX_FIRST_CONTACT, TypicalIndexes.INDEX_SECOND_CONTACT,
                        TypicalIndexes.INDEX_THIRD_CONTACT))),
                command);
    }

    @Test
    public void parseCommand_addClassGroup() throws Exception {
        ClassGroup classGroup = new ClassGroupBuilder().build();
        AddClassGroupCommand command = (AddClassGroupCommand) this.parser
                .parseCommand(ClassGroupUtil.getAddClassGroupCommand(classGroup));
        Assertions.assertEquals(new AddClassGroupCommand(classGroup, new ArrayList<>()), command);
    }

    @Test
    public void parseCommand_allocateClassGroup() throws Exception {
        ClassGroup classGroup = new ClassGroupBuilder().build();
        AllocateClassGroupCommand command = (AllocateClassGroupCommand) this.parser
                .parseCommand(
                        ClassGroupUtil.getAllocateClassGroupCommand(classGroup,
                                new ArrayList<>(Arrays.asList(TypicalIndexes.INDEX_FIRST_CONTACT))));
        Assertions.assertEquals(new AllocateClassGroupCommand(classGroup.getName(),
                List.of(TypicalIndexes.INDEX_FIRST_CONTACT)), command);
    }

    @Test
    public void parseCommand_unallocateClassGroup() throws Exception {
        ClassGroup classGroup = new ClassGroupBuilder().build();
        UnallocateClassGroupCommand command = (UnallocateClassGroupCommand) this.parser
                .parseCommand(
                        ClassGroupUtil.getUnallocateClassGroupCommand(classGroup,
                                new ArrayList<>(Arrays.asList(TypicalIndexes.INDEX_FIRST_CONTACT))));
        Assertions.assertEquals(new UnallocateClassGroupCommand(classGroup.getName(),
                List.of(TypicalIndexes.INDEX_FIRST_CONTACT)), command);
    }

    @Test
    public void parseCommand_editAssignment() throws Exception {
        String newName = "New Assignment";
        EditAssignmentDescriptor descriptor = new EditAssignmentDescriptor();
        descriptor.setName(new AssignmentName(newName));
        EditAssignmentCommand command = (EditAssignmentCommand) this.parser.parseCommand(
                EditAssignmentCommand.COMMAND_WORD + " "
                        + TypicalIndexes.INDEX_FIRST_CONTACT.getOneBased() + " "
                        + CliSyntax.PREFIX_ASSIGNMENT + newName);
        Assertions.assertEquals(
                new EditAssignmentCommand(TypicalIndexes.INDEX_FIRST_CONTACT, descriptor), command);
    }

    @Test
    public void parseCommand_editClassGroup() throws Exception {
        EditClassGroupCommand command = (EditClassGroupCommand) this.parser.parseCommand(
                EditClassGroupCommand.COMMAND_WORD + " "
                        + TypicalIndexes.INDEX_FIRST_CONTACT.getOneBased() + " "
                        + CliSyntax.PREFIX_CLASS + "CS2103T");
        Assertions.assertEquals(
                new EditClassGroupCommand(TypicalIndexes.INDEX_FIRST_CONTACT, new ClassGroupName("CS2103T")),
                command);
    }

    @Test
    public void parseCommand_unrecognisedInput_throwsParseException() {
        Assert.assertThrows(ParseException.class,
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE),
                () -> this.parser.parseCommand(""));
    }

    @Test
    public void parseCommand_unknownCommand_throwsParseException() {
        Assert.assertThrows(ParseException.class, Messages.MESSAGE_UNKNOWN_COMMAND,
                () -> this.parser.parseCommand("unknownCommand"));
    }
}
