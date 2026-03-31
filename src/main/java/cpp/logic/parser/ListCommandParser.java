package cpp.logic.parser;

import cpp.logic.Messages;
import cpp.logic.commands.ListAssignmentCommand;
import cpp.logic.commands.ListClassCommand;
import cpp.logic.commands.ListCommand;
import cpp.logic.commands.ListContactCommand;
import cpp.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates an appropriate ListCommand subclass object
 */
public class ListCommandParser implements Parser<ListCommand> {

    @Override
    public ListCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim().toLowerCase();
        if (trimmedArgs.equals("contacts") || trimmedArgs.equals("ct")) {
            return new ListContactCommand();
        } else if (trimmedArgs.equals("assignments") || trimmedArgs.equals("ass")) {
            return new ListAssignmentCommand();
        } else if (trimmedArgs.equals("classes") || trimmedArgs.equals("c")) {
            return new ListClassCommand();
        } else if (trimmedArgs.isEmpty()) {
            throw new ParseException(String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT,
                    ListCommand.MESSAGE_TAB_EMPTY + "\n" + ListCommand.MESSAGE_USAGE));
        } else {
            throw new ParseException(String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT,
                    ListCommand.MESSAGE_USAGE));
        }
    }
}
