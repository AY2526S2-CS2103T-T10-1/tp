package cpp.logic.parser;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cpp.commons.core.LogsCenter;
import cpp.logic.Messages;
import cpp.logic.commands.AddContactCommand;
import cpp.logic.commands.ClearCommand;
import cpp.logic.commands.Command;
import cpp.logic.commands.DeleteCommand;
import cpp.logic.commands.EditCommand;
import cpp.logic.commands.ExitCommand;
import cpp.logic.commands.FindCommand;
import cpp.logic.commands.HelpCommand;
import cpp.logic.commands.ListCommand;
import cpp.logic.commands.assignment.AddAssignmentCommand;
import cpp.logic.commands.assignment.AllocateAssignmentCommand;
import cpp.logic.commands.classgroup.AddClassGroupCommand;
import cpp.logic.commands.classgroup.AllocateClassGroupCommand;
import cpp.logic.parser.assignment.AddAssignmentCommandParser;
import cpp.logic.parser.assignment.AllocateAssignmentCommandParser;
import cpp.logic.parser.classgroup.AddClassGroupCommandParser;
import cpp.logic.parser.classgroup.AllocateClassGroupCommandParser;
import cpp.logic.parser.exceptions.ParseException;

/**
 * Parses user input.
 */
public class AddressBookParser {

    /**
     * Used for initial separation of command word and args.
     */
    private static final Pattern BASIC_COMMAND_FORMAT = Pattern.compile("(?<commandWord>\\S+)(?<arguments>.*)");
    private static final Logger logger = LogsCenter.getLogger(AddressBookParser.class);

    /**
     * Parses user input into command for execution.
     *
     * @param userInput full user input string
     * @return the command based on the user input
     * @throws ParseException if the user input does not conform the expected format
     */
    public Command parseCommand(String userInput) throws ParseException {
        final Matcher matcher = AddressBookParser.BASIC_COMMAND_FORMAT.matcher(userInput.trim());
        if (!matcher.matches()) {
            throw new ParseException(String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
        }

        final String commandWord = matcher.group("commandWord");
        final String arguments = matcher.group("arguments");

        // Note to developers: Change the log level in config.json to enable lower level
        // (i.e., FINE, FINER and lower)
        // log messages such as the one below.
        // Lower level log messages are used sparingly to minimize noise in the code.
        AddressBookParser.logger.fine("Command word: " + commandWord + "; Arguments: " + arguments);

        switch (commandWord) {

        case AddContactCommand.COMMAND_WORD:
            return new AddContactCommandParser().parse(arguments);

        case EditCommand.COMMAND_WORD:
            return new EditCommandParser().parse(arguments);

        case DeleteCommand.COMMAND_WORD:
            return new DeleteCommandParser().parse(arguments);

        case ClearCommand.COMMAND_WORD:
            return new ClearCommand();

        case FindCommand.COMMAND_WORD:
            return new FindCommandParser().parse(arguments);

        case ListCommand.COMMAND_WORD:
            return new ListCommandParser().parse(arguments);

        case ExitCommand.COMMAND_WORD:
            return new ExitCommand();

        case HelpCommand.COMMAND_WORD:
            return new HelpCommand();

        case AddAssignmentCommand.COMMAND_WORD:
            return new AddAssignmentCommandParser().parse(arguments);

        case AllocateAssignmentCommand.COMMAND_WORD:
            return new AllocateAssignmentCommandParser().parse(arguments);

        case AddClassGroupCommand.COMMAND_WORD:
            return new AddClassGroupCommandParser().parse(arguments);

        case AllocateClassGroupCommand.COMMAND_WORD:
            return new AllocateClassGroupCommandParser().parse(arguments);

        default:
            AddressBookParser.logger.finer("This user input caused a ParseException: " + userInput);
            throw new ParseException(Messages.MESSAGE_UNKNOWN_COMMAND);
        }
    }

}
