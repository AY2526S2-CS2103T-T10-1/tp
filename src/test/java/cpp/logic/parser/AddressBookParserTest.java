package cpp.logic.parser;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cpp.logic.Messages;
import cpp.logic.commands.AddCommand;
import cpp.logic.commands.ClearCommand;
import cpp.logic.commands.DeleteCommand;
import cpp.logic.commands.EditCommand;
import cpp.logic.commands.EditCommand.EditPersonDescriptor;
import cpp.logic.commands.ExitCommand;
import cpp.logic.commands.FindCommand;
import cpp.logic.commands.HelpCommand;
import cpp.logic.commands.ListCommand;
import cpp.logic.parser.exceptions.ParseException;
import cpp.model.person.NameContainsKeywordsPredicate;
import cpp.model.person.Person;
import cpp.testutil.Assert;
import cpp.testutil.EditPersonDescriptorBuilder;
import cpp.testutil.PersonBuilder;
import cpp.testutil.PersonUtil;
import cpp.testutil.TypicalIndexes;

public class AddressBookParserTest {

    private final AddressBookParser parser = new AddressBookParser();

    @Test
    public void parseCommand_add() throws Exception {
        Person person = new PersonBuilder().build();
        AddCommand command = (AddCommand) this.parser.parseCommand(PersonUtil.getAddCommand(person));
        Assertions.assertEquals(new AddCommand(person), command);
    }

    @Test
    public void parseCommand_clear() throws Exception {
        Assertions.assertTrue(this.parser.parseCommand(ClearCommand.COMMAND_WORD) instanceof ClearCommand);
        Assertions.assertTrue(this.parser.parseCommand(ClearCommand.COMMAND_WORD + " 3") instanceof ClearCommand);
    }

    @Test
    public void parseCommand_delete() throws Exception {
        DeleteCommand command = (DeleteCommand) this.parser.parseCommand(
                DeleteCommand.COMMAND_WORD + " " + TypicalIndexes.INDEX_FIRST_PERSON.getOneBased());
        Assertions.assertEquals(new DeleteCommand(TypicalIndexes.INDEX_FIRST_PERSON), command);
    }

    @Test
    public void parseCommand_edit() throws Exception {
        Person person = new PersonBuilder().build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(person).build();
        EditCommand command = (EditCommand) this.parser.parseCommand(EditCommand.COMMAND_WORD + " "
                + TypicalIndexes.INDEX_FIRST_PERSON.getOneBased() + " "
                + PersonUtil.getEditPersonDescriptorDetails(descriptor));
        Assertions.assertEquals(new EditCommand(TypicalIndexes.INDEX_FIRST_PERSON, descriptor), command);
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
        Assertions.assertEquals(new FindCommand(new NameContainsKeywordsPredicate(keywords)), command);
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
