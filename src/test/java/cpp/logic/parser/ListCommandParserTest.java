package cpp.logic.parser;

import org.junit.jupiter.api.Test;

import cpp.logic.Messages;
import cpp.logic.commands.ListAssignmentCommand;
import cpp.logic.commands.ListClassCommand;
import cpp.logic.commands.ListCommand;
import cpp.logic.commands.ListContactCommand;

public class ListCommandParserTest {
    private ListCommandParser parser = new ListCommandParser();

    @Test
    public void parse_emptyArgs_throwsParseException() {
        CommandParserTestUtil.assertParseFailure(this.parser, "     ",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_TAB_EMPTY));
    }

    @Test
    public void parse_emptyString_throwsParseException() {
        CommandParserTestUtil.assertParseFailure(this.parser, "",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_TAB_EMPTY));
    }

    @Test
    public void parse_validArgs_returnsListCommand() {
        // no leading and trailing whitespaces
        ListCommand expectedListCommand = new ListContactCommand();
        CommandParserTestUtil.assertParseSuccess(this.parser, "contacts", expectedListCommand);

        // multiple whitespaces between keywords
        CommandParserTestUtil.assertParseSuccess(this.parser, " \n contacts \n \t ", expectedListCommand);
    }

    @Test
    public void parse_assignmentsArgs_returnsListCommand() {
        ListCommand expectedListCommand = new ListAssignmentCommand();
        CommandParserTestUtil.assertParseSuccess(this.parser, "assignments", expectedListCommand);
    }

    @Test
    public void parse_assignmentsArgsWithWhitespace_returnsListCommand() {
        ListCommand expectedListCommand = new ListAssignmentCommand();
        CommandParserTestUtil.assertParseSuccess(this.parser, "  assignments  ", expectedListCommand);
        CommandParserTestUtil.assertParseSuccess(this.parser, "\t\nassignments\t\n", expectedListCommand);
    }

    @Test
    public void parse_classesArgs_returnsListCommand() {
        ListCommand expectedListCommand = new ListClassCommand();
        CommandParserTestUtil.assertParseSuccess(this.parser, "classes", expectedListCommand);
    }

    @Test
    public void parse_classesArgsWithWhitespace_returnsListCommand() {
        ListCommand expectedListCommand = new ListClassCommand();
        CommandParserTestUtil.assertParseSuccess(this.parser, "  classes  ", expectedListCommand);
        CommandParserTestUtil.assertParseSuccess(this.parser, "\n\tclasses\n\t", expectedListCommand);
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        CommandParserTestUtil.assertParseFailure(this.parser, "invalid",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_partialMatch_throwsParseException() {
        CommandParserTestUtil.assertParseFailure(this.parser, "contact",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
        CommandParserTestUtil.assertParseFailure(this.parser, "assign",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
        CommandParserTestUtil.assertParseFailure(this.parser, "class",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_caseInsensitive_contacts() {
        ListCommand expectedListCommand = new ListContactCommand();
        CommandParserTestUtil.assertParseSuccess(this.parser, "CONTACTS", expectedListCommand);
        CommandParserTestUtil.assertParseSuccess(this.parser, "Contacts", expectedListCommand);
        CommandParserTestUtil.assertParseSuccess(this.parser, "CoNtAcTs", expectedListCommand);
        CommandParserTestUtil.assertParseSuccess(this.parser, "cOnTaCtS", expectedListCommand);
    }

    @Test
    public void parse_caseInsensitive_assignments() {
        ListCommand expectedListCommand = new ListAssignmentCommand();
        CommandParserTestUtil.assertParseSuccess(this.parser, "ASSIGNMENTS", expectedListCommand);
        CommandParserTestUtil.assertParseSuccess(this.parser, "Assignments", expectedListCommand);
        CommandParserTestUtil.assertParseSuccess(this.parser, "AsSiGnMeNtS", expectedListCommand);
    }

    @Test
    public void parse_caseInsensitive_classes() {
        ListCommand expectedListCommand = new ListClassCommand();
        CommandParserTestUtil.assertParseSuccess(this.parser, "CLASSES", expectedListCommand);
        CommandParserTestUtil.assertParseSuccess(this.parser, "Classes", expectedListCommand);
        CommandParserTestUtil.assertParseSuccess(this.parser, "ClAsSeS", expectedListCommand);
    }

    @Test
    public void parse_caseInsensitiveWithWhitespace_contacts() {
        ListCommand expectedListCommand = new ListContactCommand();
        CommandParserTestUtil.assertParseSuccess(this.parser, "  CONTACTS  ", expectedListCommand);
        CommandParserTestUtil.assertParseSuccess(this.parser, "\t Contacts \n", expectedListCommand);
    }

    @Test
    public void parse_caseInsensitiveWithWhitespace_assignments() {
        ListCommand expectedListCommand = new ListAssignmentCommand();
        CommandParserTestUtil.assertParseSuccess(this.parser, "  ASSIGNMENTS  ", expectedListCommand);
        CommandParserTestUtil.assertParseSuccess(this.parser, "\t Assignments \n", expectedListCommand);
    }

    @Test
    public void parse_caseInsensitiveWithWhitespace_classes() {
        ListCommand expectedListCommand = new ListClassCommand();
        CommandParserTestUtil.assertParseSuccess(this.parser, "  CLASSES  ", expectedListCommand);
        CommandParserTestUtil.assertParseSuccess(this.parser, "\t Classes \n", expectedListCommand);
    }

    @Test
    public void parse_singleCharacter_throwsParseException() {
        CommandParserTestUtil.assertParseFailure(this.parser, "c",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
        CommandParserTestUtil.assertParseFailure(this.parser, "a",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_longInvalidInput_throwsParseException() {
        String longInput = "this is a very long invalid input that should not match any list command";
        CommandParserTestUtil.assertParseFailure(this.parser, longInput,
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_specialCharacters_throwsParseException() {
        CommandParserTestUtil.assertParseFailure(this.parser, "contacts!",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
        CommandParserTestUtil.assertParseFailure(this.parser, "@contacts",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
        CommandParserTestUtil.assertParseFailure(this.parser, "con@tacts",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_numbersOnly_throwsParseException() {
        CommandParserTestUtil.assertParseFailure(this.parser, "123",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
        CommandParserTestUtil.assertParseFailure(this.parser, "1",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
    }
}
