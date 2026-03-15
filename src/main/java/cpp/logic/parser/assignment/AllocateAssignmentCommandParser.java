package cpp.logic.parser.assignment;

import java.util.List;

import cpp.commons.core.index.Index;
import cpp.logic.Messages;
import cpp.logic.commands.assignment.AllocateAssignmentCommand;
import cpp.logic.parser.ArgumentMultimap;
import cpp.logic.parser.ArgumentTokenizer;
import cpp.logic.parser.CliSyntax;
import cpp.logic.parser.Parser;
import cpp.logic.parser.ParserUtil;
import cpp.logic.parser.exceptions.ParseException;
import cpp.model.assignment.AssignmentName;
import cpp.model.classgroup.ClassGroupName;

/**
 * Parses input arguments and creates a new AllocateAssignmentCommand object.
 * Expected parameters: name and contact indices (ct/).
 */
public class AllocateAssignmentCommandParser implements Parser<AllocateAssignmentCommand> {

    @Override
    public AllocateAssignmentCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args,
                CliSyntax.PREFIX_ASSIGNMENT, CliSyntax.PREFIX_CLASS, CliSyntax.PREFIX_CONTACT);

        if ((!ParserUtil.arePrefixesPresent(argMultimap, CliSyntax.PREFIX_ASSIGNMENT,
                CliSyntax.PREFIX_CONTACT)
                && !ParserUtil.arePrefixesPresent(argMultimap, CliSyntax.PREFIX_ASSIGNMENT,
                        CliSyntax.PREFIX_CLASS))
                || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT,
                    AllocateAssignmentCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(CliSyntax.PREFIX_ASSIGNMENT, CliSyntax.PREFIX_CLASS,
                CliSyntax.PREFIX_CONTACT);

        AssignmentName assignmentName = ParserUtil
                .parseAssignmentName(argMultimap.getValue(CliSyntax.PREFIX_ASSIGNMENT).get());

        List<Index> contactIndices = List.of();
        if (ParserUtil.arePrefixesPresent(argMultimap, CliSyntax.PREFIX_CONTACT)) {
            String contactString = argMultimap.getValue(CliSyntax.PREFIX_CONTACT).orElse("");
            contactIndices = ParserUtil.parseContactIndices(contactString);
        }

        if (ParserUtil.arePrefixesPresent(argMultimap, CliSyntax.PREFIX_CLASS)) {
            String classGroupString = argMultimap.getValue(CliSyntax.PREFIX_CLASS).orElse("");
            ClassGroupName classGroupName = ParserUtil.parseClassGroupName(classGroupString);
            return new AllocateAssignmentCommand(assignmentName, contactIndices, classGroupName);
        }

        return new AllocateAssignmentCommand(assignmentName, contactIndices);
    }

}
