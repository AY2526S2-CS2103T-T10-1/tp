package cpp.logic.parser.classgroup;

import java.util.List;

import cpp.commons.core.index.Index;
import cpp.logic.Messages;
import cpp.logic.commands.classgroup.AllocateClassGroupCommand;
import cpp.logic.parser.ArgumentMultimap;
import cpp.logic.parser.ArgumentTokenizer;
import cpp.logic.parser.CliSyntax;
import cpp.logic.parser.Parser;
import cpp.logic.parser.ParserUtil;
import cpp.logic.parser.exceptions.ParseException;
import cpp.model.classgroup.ClassGroupName;

/**
 * Parses input arguments and creates a new AllocateClassGroupCommand object.
 * Expected parameters: class group name (c/) and contact indices (ct/).
 */
public class AllocateClassGroupCommandParser implements Parser<AllocateClassGroupCommand> {

    @Override
    public AllocateClassGroupCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args,
                CliSyntax.PREFIX_CLASS, CliSyntax.PREFIX_CONTACT);

        if (!ParserUtil.arePrefixesPresent(argMultimap, CliSyntax.PREFIX_CLASS,
                CliSyntax.PREFIX_CONTACT)
                || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT,
                    AllocateClassGroupCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(CliSyntax.PREFIX_CLASS, CliSyntax.PREFIX_CONTACT);

        ClassGroupName classGroupName = ParserUtil
                .parseClassGroupName(argMultimap.getValue(CliSyntax.PREFIX_CLASS).get(), true);

        String contactString = argMultimap.getValue(CliSyntax.PREFIX_CONTACT).orElse("");
        List<Index> contactIndices = ParserUtil.parseContactIndices(contactString);

        return new AllocateClassGroupCommand(classGroupName, contactIndices);
    }

}
