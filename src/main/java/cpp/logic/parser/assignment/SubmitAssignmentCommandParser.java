package cpp.logic.parser.assignment;

import java.time.LocalDateTime;
import java.util.List;

import cpp.commons.core.index.Index;
import cpp.logic.Messages;
import cpp.logic.commands.assignment.SubmitAssignmentCommand;
import cpp.logic.parser.ArgumentMultimap;
import cpp.logic.parser.ArgumentTokenizer;
import cpp.logic.parser.CliSyntax;
import cpp.logic.parser.Parser;
import cpp.logic.parser.ParserUtil;
import cpp.logic.parser.exceptions.ParseException;
import cpp.model.assignment.AssignmentName;
import cpp.model.classgroup.ClassGroupName;

/**
 * Parses input arguments and creates a new {@code SubmitAssignmentCommand}
 * object
 */
public class SubmitAssignmentCommandParser implements Parser<SubmitAssignmentCommand> {
    /**
     * Parses the given {@code String} of arguments and returns a
     * {@code SubmitAssignmentCommand} object for execution.
     */
    @Override
    public SubmitAssignmentCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args,
                CliSyntax.PREFIX_ASSIGNMENT, CliSyntax.PREFIX_CLASS, CliSyntax.PREFIX_CONTACT,
                CliSyntax.PREFIX_DATETIME);

        boolean hasAssignment = argMultimap.getValue(CliSyntax.PREFIX_ASSIGNMENT).isPresent();
        boolean hasContact = argMultimap.getValue(CliSyntax.PREFIX_CONTACT).isPresent();
        boolean hasClass = argMultimap.getValue(CliSyntax.PREFIX_CLASS).isPresent();

        if (!hasAssignment || !(hasContact || hasClass) || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT,
                    SubmitAssignmentCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(CliSyntax.PREFIX_ASSIGNMENT, CliSyntax.PREFIX_CLASS,
                CliSyntax.PREFIX_CONTACT, CliSyntax.PREFIX_DATETIME);

        AssignmentName assignmentName = ParserUtil
                .parseAssignmentName(argMultimap.getValue(CliSyntax.PREFIX_ASSIGNMENT).get());

        LocalDateTime submissionDate = ParserUtil.parseDateTime(argMultimap.getValue(CliSyntax.PREFIX_DATETIME)
                .orElseGet(() -> LocalDateTime.now().format(ParserUtil.DATETIME_FORMATTER)));

        List<Index> contactIndices = List.of();
        if (hasContact) {
            String contactString = argMultimap.getValue(CliSyntax.PREFIX_CONTACT).orElse("");
            contactIndices = ParserUtil.parseContactIndices(contactString);
        }

        if (hasClass) {
            String classGroupString = argMultimap.getValue(CliSyntax.PREFIX_CLASS).orElse("");
            ClassGroupName classGroupName = ParserUtil.parseClassGroupName(classGroupString);
            return new SubmitAssignmentCommand(assignmentName, contactIndices, classGroupName, submissionDate);
        }

        return new SubmitAssignmentCommand(assignmentName, contactIndices, submissionDate);
    }
}
