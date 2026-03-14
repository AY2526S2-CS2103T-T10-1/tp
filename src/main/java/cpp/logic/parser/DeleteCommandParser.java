package cpp.logic.parser;

import java.util.List;

import cpp.commons.core.index.Index;
import cpp.logic.Messages;
import cpp.logic.commands.Command;
import cpp.logic.commands.DeleteCommand;
import cpp.logic.commands.DeleteContactCommand;
import cpp.logic.commands.assignment.DeleteAssignmentCommand;
import cpp.logic.parser.exceptions.ParseException;
import cpp.model.assignment.AssignmentName;

/**
 * Parses input arguments and creates a new DeleteContactCommand or DeleteAssignmentCommand object.
 */
public class DeleteCommandParser implements Parser<Command> {

    @Override
    public Command parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args,
                CliSyntax.PREFIX_CONTACT, CliSyntax.PREFIX_ASSIGNMENT);

        boolean hasContact = argMultimap.getValue(CliSyntax.PREFIX_CONTACT).isPresent();
        boolean hasAssignment = argMultimap.getValue(CliSyntax.PREFIX_ASSIGNMENT).isPresent();

        if (hasContact && !hasAssignment) {
            return parseDeleteContact(argMultimap);
        } else if (hasAssignment && !hasContact) {
            return parseDeleteAssignment(argMultimap);
        } else {
            throw new ParseException(
                    String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
        }
    }

    // parse delete contact by one or more space-separated indices
    private DeleteContactCommand parseDeleteContact(ArgumentMultimap argMultimap) throws ParseException {
        List<Index> indices = ParserUtil.parseContactIndices(
                argMultimap.getValue(CliSyntax.PREFIX_CONTACT).get());
        if (indices.isEmpty()) {
            throw new ParseException(ParserUtil.MESSAGE_EMPTY_INDICES);
        }
        return new DeleteContactCommand(indices);
    }

    // parse delete assignment by name
    private DeleteAssignmentCommand parseDeleteAssignment(ArgumentMultimap argMultimap) throws ParseException {
        AssignmentName name = ParserUtil.parseAssignmentName(
                argMultimap.getValue(CliSyntax.PREFIX_ASSIGNMENT).get());
        return new DeleteAssignmentCommand(name);
    }

}
