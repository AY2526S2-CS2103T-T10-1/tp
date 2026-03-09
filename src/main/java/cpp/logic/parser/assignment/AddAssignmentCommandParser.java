package cpp.logic.parser.assignment;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import cpp.logic.Messages;
import cpp.logic.commands.assignment.AddAssignmentCommand;
import cpp.logic.parser.ArgumentMultimap;
import cpp.logic.parser.ArgumentTokenizer;
import cpp.logic.parser.CliSyntax;
import cpp.logic.parser.Parser;
import cpp.logic.parser.ParserUtil;
import cpp.logic.parser.Prefix;
import cpp.logic.parser.exceptions.ParseException;
import cpp.model.assignment.Assignment;
import cpp.model.assignment.AssignmentName;

/**
 * Parses input arguments and creates a new AddAssignmentCommand object
 */
public class AddAssignmentCommandParser implements Parser<AddAssignmentCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the
     * AddAssignmentCommand
     * and returns an AddAssignmentCommand object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    @Override
    public AddAssignmentCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, CliSyntax.PREFIX_ASSIGNMENT,
                CliSyntax.PREFIX_DEADLINE,
                CliSyntax.PREFIX_CLASS, CliSyntax.PREFIX_CONTACT);

        if (!AddAssignmentCommandParser.arePrefixesPresent(argMultimap, CliSyntax.PREFIX_ASSIGNMENT,
                CliSyntax.PREFIX_DEADLINE)
                || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT,
                    AddAssignmentCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(CliSyntax.PREFIX_ASSIGNMENT, CliSyntax.PREFIX_DEADLINE,
                CliSyntax.PREFIX_CLASS, CliSyntax.PREFIX_CONTACT);
        AssignmentName name = ParserUtil.parseAssignmentName(argMultimap.getValue(CliSyntax.PREFIX_ASSIGNMENT).get());
        LocalDateTime deadline = ParserUtil.parseDeadline(argMultimap.getValue(CliSyntax.PREFIX_DEADLINE).get());
        // TODO: handle optional class name and contact indices
        // Name className =
        // ParserUtil.parseClassName(argMultimap.getValue(CliSyntax.PREFIX_CLASS).orElse(null));
        // String contactId = ParserUtil
        // .parseContactIndices(argMultimap.getValue(CliSyntax.PREFIX_CONTACT).orElse(null));

        Assignment assignment = new Assignment(name, deadline);

        return new AddAssignmentCommand(assignment);
    }

    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }

}
