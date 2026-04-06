package cpp.logic.parser;

import cpp.commons.core.index.Index;
import cpp.logic.Messages;
import cpp.logic.commands.view.ViewAssignmentCommand;
import cpp.logic.commands.view.ViewClassGroupCommand;
import cpp.logic.commands.view.ViewCommand;
import cpp.logic.commands.view.ViewContactCommand;
import cpp.logic.parser.exceptions.ParseException;
import cpp.model.assignment.AssignmentName;
import cpp.model.classgroup.ClassGroupName;

/**
 * Parses input arguments and creates a new {@code ViewCommand} object
 */
public class ViewCommandParser implements Parser<ViewCommand> {

    /**
     * Parses the given {@code String} of arguments and returns a
     * {@code ViewCommand} object for execution.
     */
    @Override
    public ViewCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args,
                CliSyntax.PREFIX_CONTACT, CliSyntax.PREFIX_ASSIGNMENT, CliSyntax.PREFIX_CLASS);

        argMultimap.verifyNoDuplicatePrefixesFor(
                CliSyntax.PREFIX_CONTACT, CliSyntax.PREFIX_ASSIGNMENT, CliSyntax.PREFIX_CLASS);

        boolean hasContact = argMultimap.getValue(CliSyntax.PREFIX_CONTACT).isPresent();
        boolean hasAssignment = argMultimap.getValue(CliSyntax.PREFIX_ASSIGNMENT).isPresent();
        boolean hasClass = argMultimap.getValue(CliSyntax.PREFIX_CLASS).isPresent();

        if (hasContact && !hasAssignment && !hasClass) {
            return this.parseViewContact(argMultimap);
        } else if (hasAssignment && !hasContact && !hasClass) {
            return this.parseViewAssignment(argMultimap);
        } else if (hasClass && !hasContact && !hasAssignment) {
            return this.parseViewClassGroup(argMultimap);
        } else {
            throw new ParseException(
                    String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, ViewCommand.MESSAGE_USAGE));
        }
    }

    /**
     * Parses view contact arguments and returns a {@code ViewCommand}.
     */
    private ViewContactCommand parseViewContact(ArgumentMultimap argMultimap) throws ParseException {
        Index index = ParserUtil.parseIndex(argMultimap.getValue(CliSyntax.PREFIX_CONTACT).get());
        return new ViewContactCommand(index);
    }

    /**
     * Parses view assignment arguments and returns a {@code ViewCommand}.
     */
    private ViewCommand parseViewAssignment(ArgumentMultimap argMultimap) throws ParseException {
        AssignmentName name = ParserUtil.parseAssignmentName(
                argMultimap.getValue(CliSyntax.PREFIX_ASSIGNMENT).get());
        return new ViewAssignmentCommand(name);
    }

    /**
     * Parses view class group arguments and returns a {@code ViewCommand}.
     */
    private ViewCommand parseViewClassGroup(ArgumentMultimap argMultimap) throws ParseException {
        ClassGroupName name = ParserUtil.parseClassGroupName(
                argMultimap.getValue(CliSyntax.PREFIX_CLASS).get());
        return new ViewClassGroupCommand(name);
    }

}
