package cpp.logic.parser;

import java.util.List;

import cpp.commons.core.index.Index;
import cpp.logic.Messages;
import cpp.logic.commands.Command;
import cpp.logic.commands.DeleteCommand;
import cpp.logic.commands.DeleteContactCommand;
import cpp.logic.commands.assignment.DeleteAssignmentCommand;
import cpp.logic.commands.classgroup.DeleteClassGroupCommand;
import cpp.logic.parser.exceptions.ParseException;
import cpp.model.assignment.AssignmentName;
import cpp.model.classgroup.ClassGroupName;

/**
 * Parses input arguments and creates a new {@code DeleteCommand} object.
 */
public class DeleteCommandParser implements Parser<Command> {

    @Override
    public Command parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args,
                CliSyntax.PREFIX_CONTACT, CliSyntax.PREFIX_ASSIGNMENT, CliSyntax.PREFIX_CLASS);

        argMultimap.verifyNoDuplicatePrefixesFor(
                CliSyntax.PREFIX_CONTACT, CliSyntax.PREFIX_ASSIGNMENT, CliSyntax.PREFIX_CLASS);

        boolean hasContact = argMultimap.getValue(CliSyntax.PREFIX_CONTACT).isPresent();
        boolean hasAssignment = argMultimap.getValue(CliSyntax.PREFIX_ASSIGNMENT).isPresent();
        boolean hasClass = argMultimap.getValue(CliSyntax.PREFIX_CLASS).isPresent();

        if (hasContact && !hasAssignment && !hasClass) {
            return this.parseDeleteContact(argMultimap);
        } else if (hasAssignment && !hasContact && !hasClass) {
            return this.parseDeleteAssignment(argMultimap);
        } else if (hasClass && !hasContact && !hasAssignment) {
            return this.parseDeleteClassGroup(argMultimap);
        } else {
            throw new ParseException(
                    String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
        }
    }

    /**
     * Parses delete contact arguments and returns a {@code DeleteContactCommand}.
     */
    private DeleteContactCommand parseDeleteContact(ArgumentMultimap argMultimap) throws ParseException {
        List<Index> indices = ParserUtil.parseContactIndices(
                argMultimap.getValue(CliSyntax.PREFIX_CONTACT).get());
        return new DeleteContactCommand(indices);
    }

    /**
     * Parses delete assignment arguments and returns a
     * {@code DeleteAssignmentCommand}.
     */
    private DeleteAssignmentCommand parseDeleteAssignment(ArgumentMultimap argMultimap) throws ParseException {
        AssignmentName name = ParserUtil.parseAssignmentName(
                argMultimap.getValue(CliSyntax.PREFIX_ASSIGNMENT).get());
        return new DeleteAssignmentCommand(name);
    }

    /**
     * Parses delete class group arguments and returns a
     * {@code DeleteClassGroupCommand}.
     */
    private DeleteClassGroupCommand parseDeleteClassGroup(ArgumentMultimap argMultimap) throws ParseException {
        ClassGroupName name = ParserUtil.parseClassGroupName(
                argMultimap.getValue(CliSyntax.PREFIX_CLASS).get());
        return new DeleteClassGroupCommand(name);
    }

}
