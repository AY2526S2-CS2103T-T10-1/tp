package cpp.logic.parser;

import cpp.logic.Messages;
import cpp.logic.commands.ListCommand;
import cpp.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new ListCommand object
 */
public class ListCommandParser implements Parser<ListCommand> {

    @Override
    public ListCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        try {
            if (trimmedArgs.isEmpty() || trimmedArgs.equals("contacts")) {
                return new ListCommand("contacts");
            } else if (trimmedArgs.equals("assignments")) {
                return new ListCommand("assignments");
            } else {
                throw new ParseException(
                        String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
            }
        } catch (Exception e) {
            throw new ParseException(String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE),
                    e);
        }
    }
}
