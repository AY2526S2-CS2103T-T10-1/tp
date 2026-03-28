package cpp.logic.parser.assignment;

import java.time.LocalDateTime;
import java.util.List;

import cpp.commons.core.index.Index;
import cpp.logic.Messages;
import cpp.logic.commands.assignment.AddAssignmentCommand;
import cpp.logic.parser.ArgumentMultimap;
import cpp.logic.parser.ArgumentTokenizer;
import cpp.logic.parser.CliSyntax;
import cpp.logic.parser.Parser;
import cpp.logic.parser.ParserUtil;
import cpp.logic.parser.exceptions.ParseException;
import cpp.model.assignment.Assignment;
import cpp.model.assignment.AssignmentName;
import cpp.model.classgroup.ClassGroupName;

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
                CliSyntax.PREFIX_DATETIME,
                CliSyntax.PREFIX_CLASS, CliSyntax.PREFIX_CONTACT);

        if (!ParserUtil.arePrefixesPresent(argMultimap, CliSyntax.PREFIX_ASSIGNMENT,
                CliSyntax.PREFIX_DATETIME)
                || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT,
                    AddAssignmentCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(CliSyntax.PREFIX_ASSIGNMENT, CliSyntax.PREFIX_DATETIME,
                CliSyntax.PREFIX_CLASS, CliSyntax.PREFIX_CONTACT);
        AssignmentName assignmentName = ParserUtil
                .parseAssignmentName(argMultimap.getValue(CliSyntax.PREFIX_ASSIGNMENT).get());
        LocalDateTime deadline = ParserUtil.parseDeadline(argMultimap.getValue(CliSyntax.PREFIX_DATETIME).get());

        Assignment assignment = new Assignment(assignmentName, deadline);

        List<Index> contactIndices = List.of();
        if (ParserUtil.arePrefixesPresent(argMultimap, CliSyntax.PREFIX_CONTACT)) {
            String contactString = argMultimap.getValue(CliSyntax.PREFIX_CONTACT).orElse("");
            contactIndices = ParserUtil.parseContactIndices(contactString);
        }

        if (ParserUtil.arePrefixesPresent(argMultimap, CliSyntax.PREFIX_CLASS)) {
            String classGroupString = argMultimap.getValue(CliSyntax.PREFIX_CLASS).orElse("");
            ClassGroupName classGroupName = ParserUtil.parseClassGroupName(classGroupString);
            return new AddAssignmentCommand(assignment, contactIndices, classGroupName);
        }

        return new AddAssignmentCommand(assignment, contactIndices);
    }

}
