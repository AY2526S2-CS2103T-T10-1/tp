package cpp.logic.parser;

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
        String trimmedArgs = args.trim();
        if (trimmedArgs.equals("contacts")) {
            return new ListContactCommand();
        } else if (trimmedArgs.equals("assignments")) {
            return new ListAssignmentCommand();
        } else if (trimmedArgs.equals("classes")) {
            return new ListClassCommand();
        } else if (trimmedArgs.isEmpty()) {
            throw new ParseException(ListCommand.MESSAGE_TAB_EMPTY);
        } else {
            throw new ParseException(ListCommand.MESSAGE_TAB_INVALID);
        }
    }
}
