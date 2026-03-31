package cpp.logic.parser.assignment;

import java.util.Objects;

import cpp.commons.core.index.Index;
import cpp.logic.Messages;
import cpp.logic.commands.assignment.EditAssignmentCommand;
import cpp.logic.commands.assignment.EditAssignmentCommand.EditAssignmentDescriptor;
import cpp.logic.parser.ArgumentMultimap;
import cpp.logic.parser.ArgumentTokenizer;
import cpp.logic.parser.CliSyntax;
import cpp.logic.parser.Parser;
import cpp.logic.parser.ParserUtil;
import cpp.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new EditAssignmentCommand object.
 */
public class EditAssignmentCommandParser implements Parser<EditAssignmentCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the
     *
     * EditAssignmentCommand and returns an EditAssignmentCommand object for
     * execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    @Override
    public EditAssignmentCommand parse(String args) throws ParseException {
        Objects.requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(
                args, CliSyntax.PREFIX_ASSIGNMENT, CliSyntax.PREFIX_DATETIME);

        if (argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT,
                    EditAssignmentCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(
                CliSyntax.PREFIX_ASSIGNMENT, CliSyntax.PREFIX_DATETIME);

        EditAssignmentDescriptor descriptor = new EditAssignmentDescriptor();

        if (argMultimap.getValue(CliSyntax.PREFIX_ASSIGNMENT).isPresent()) {
            descriptor.setName(ParserUtil.parseAssignmentName(
                    argMultimap.getValue(CliSyntax.PREFIX_ASSIGNMENT).get()));
        }
        if (argMultimap.getValue(CliSyntax.PREFIX_DATETIME).isPresent()) {
            descriptor.setDeadline(ParserUtil.parseDeadline(
                    argMultimap.getValue(CliSyntax.PREFIX_DATETIME).get()));
        }

        if (!descriptor.isAnyFieldEdited()) {
            throw new ParseException(EditAssignmentCommand.MESSAGE_NOT_EDITED);
        }

        Index index = ParserUtil.parseIndex(argMultimap.getPreamble());

        return new EditAssignmentCommand(index, descriptor);
    }
}
