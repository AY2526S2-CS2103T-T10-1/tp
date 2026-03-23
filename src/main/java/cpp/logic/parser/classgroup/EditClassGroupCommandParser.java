package cpp.logic.parser.classgroup;

import java.util.Objects;

import cpp.commons.core.index.Index;
import cpp.logic.Messages;
import cpp.logic.commands.classgroup.EditClassGroupCommand;
import cpp.logic.parser.ArgumentMultimap;
import cpp.logic.parser.ArgumentTokenizer;
import cpp.logic.parser.CliSyntax;
import cpp.logic.parser.Parser;
import cpp.logic.parser.ParserUtil;
import cpp.logic.parser.exceptions.ParseException;
import cpp.model.classgroup.ClassGroupName;

/**
 * Parses input arguments and creates a new EditClassGroupCommand object.
 */
public class EditClassGroupCommandParser implements Parser<EditClassGroupCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the
     * EditClassGroupCommand and returns an EditClassGroupCommand object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    @Override
    public EditClassGroupCommand parse(String args) throws ParseException {
        Objects.requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, CliSyntax.PREFIX_CLASS);

        Index index;
        try {
            index = ParserUtil.parseIndex(argMultimap.getPreamble());
        } catch (ParseException pe) {
            throw new ParseException(
                    String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT,
                            EditClassGroupCommand.MESSAGE_USAGE), pe);
        }

        if (!ParserUtil.arePrefixesPresent(argMultimap, CliSyntax.PREFIX_CLASS)) {
            throw new ParseException(
                    String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT,
                            EditClassGroupCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(CliSyntax.PREFIX_CLASS);

        ClassGroupName newName = ParserUtil.parseClassGroupName(
                argMultimap.getValue(CliSyntax.PREFIX_CLASS).get());

        return new EditClassGroupCommand(index, newName);
    }
}
