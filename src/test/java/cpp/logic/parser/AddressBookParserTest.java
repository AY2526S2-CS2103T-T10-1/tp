package cpp.logic.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cpp.logic.Messages;
import cpp.logic.commands.AddCommand;
import cpp.logic.commands.ClearCommand;
import cpp.logic.commands.DeleteCommand;
import cpp.logic.commands.DeleteContactCommand;
import cpp.logic.commands.EditCommand;
import cpp.logic.commands.EditCommand.EditContactDescriptor;
import cpp.logic.commands.ExitCommand;
import cpp.logic.commands.FindCommand;
import cpp.logic.commands.HelpCommand;
import cpp.logic.commands.ListCommand;
import cpp.logic.commands.assignment.AddAssignmentCommand;
import cpp.logic.commands.classgroup.AddClassGroupCommand;
import cpp.logic.parser.exceptions.ParseException;
import cpp.model.assignment.Assignment;
import cpp.model.classgroup.ClassGroup;
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
import cpp.testutil.TypicalIndexes;

public class AddressBookParserTest {

    private final AddressBookParser parser = new AddressBookParser();

    @Test
    public void parseCommand_add() throws Exception {
        Contact contact = new ContactBuilder().build();
        AddCommand command = (AddCommand) this.parser.parseCommand(ContactUtil.getAddCommand(contact));
        Assertions.assertEquals(new AddCommand(contact), command);
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
        EditCommand command = (EditCommand) this.parser.parseCommand(EditCommand.COMMAND_WORD + " "
                + TypicalIndexes.INDEX_FIRST_CONTACT.getOneBased() + " "
                + ContactUtil.getEditContactDescriptorDetails(descriptor));
        Assertions.assertEquals(new EditCommand(TypicalIndexes.INDEX_FIRST_CONTACT, descriptor), command);
    }

    @Test
    public void parseCommand_exit() throws Exception {
        Assertions.assertTrue(this.parser.parseCommand(ExitCommand.COMMAND_WORD) instanceof ExitCommand);
        Assertions.assertTrue(this.parser.parseCommand(ExitCommand.COMMAND_WORD + " 3") instanceof ExitCommand);
    }

    @Test
    public void parseCommand_find() throws Exception {
        List<String> keywords = Arrays.asList("foo", "bar", "baz");
        FindCommand command = (FindCommand) this.parser.parseCommand(
                FindCommand.COMMAND_WORD + " " + keywords.stream().collect(Collectors.joining(" ")));
        Assertions.assertEquals(new FindCommand(new ContactNameContainsKeywordsPredicate(keywords)), command);
    }

    @Test
    public void parseCommand_help() throws Exception {
        Assertions.assertTrue(this.parser.parseCommand(HelpCommand.COMMAND_WORD) instanceof HelpCommand);
        Assertions.assertTrue(this.parser.parseCommand(HelpCommand.COMMAND_WORD + " 3") instanceof HelpCommand);
    }

    @Test
    public void parseCommand_list() throws Exception {
        Assertions.assertTrue(this.parser.parseCommand(ListCommand.COMMAND_WORD) instanceof ListCommand);
        Assertions.assertTrue(this.parser.parseCommand(ListCommand.COMMAND_WORD + " 3") instanceof ListCommand);
    }

    @Test
    public void parseCommand_addAssignment() throws Exception {
        Assignment assignment = new AssignmentBuilder(TypicalAssignments.ASSIGNMENT_ONE).build();
        AddAssignmentCommand command = (AddAssignmentCommand) this.parser
                .parseCommand(AssignmentUtil.getAddAssignmentCommand(assignment));
        Assertions.assertEquals(new AddAssignmentCommand(assignment), command);
    }

    @Test
    public void parseCommand_addClassGroup() throws Exception {
        ClassGroup classGroup = new ClassGroupBuilder().build();
        AddClassGroupCommand command = (AddClassGroupCommand) this.parser
                .parseCommand(ClassGroupUtil.getAddClassGroupCommand(classGroup));
        Assertions.assertEquals(new AddClassGroupCommand(classGroup, new ArrayList<>()), command);
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
