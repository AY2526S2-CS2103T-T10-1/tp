package cpp.logic.parser;

import java.util.Set;

import cpp.logic.Messages;
import cpp.logic.commands.AddContactCommand;
import cpp.logic.parser.exceptions.ParseException;
import cpp.model.assignment.AssignmentName;
// import cpp.model.classgroup.ClassGroupName;
import cpp.model.contact.Address;
import cpp.model.contact.Contact;
import cpp.model.contact.ContactName;
import cpp.model.contact.Email;
import cpp.model.contact.Phone;
import cpp.model.tag.Tag;

/**
 * Parses input arguments and creates a new AddContactCommand object
 */
public class AddContactCommandParser implements Parser<AddContactCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the
     * AddContactCommand
     * and returns an AddContactCommand object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    @Override
    public AddContactCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, CliSyntax.PREFIX_NAME, CliSyntax.PREFIX_PHONE,
                CliSyntax.PREFIX_EMAIL,
                CliSyntax.PREFIX_ADDRESS, CliSyntax.PREFIX_CLASS, CliSyntax.PREFIX_ASSIGNMENT, CliSyntax.PREFIX_TAG);

        if (!ParserUtil.arePrefixesPresent(argMultimap, CliSyntax.PREFIX_NAME, CliSyntax.PREFIX_ADDRESS,
                CliSyntax.PREFIX_PHONE, CliSyntax.PREFIX_EMAIL)
                || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(
                    String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, AddContactCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(CliSyntax.PREFIX_NAME, CliSyntax.PREFIX_PHONE, CliSyntax.PREFIX_EMAIL,
                CliSyntax.PREFIX_ADDRESS, CliSyntax.PREFIX_CLASS, CliSyntax.PREFIX_ASSIGNMENT);
        ContactName name = ParserUtil.parseName(argMultimap.getValue(CliSyntax.PREFIX_NAME).get());
        Phone phone = ParserUtil.parsePhone(argMultimap.getValue(CliSyntax.PREFIX_PHONE).get());
        Email email = ParserUtil.parseEmail(argMultimap.getValue(CliSyntax.PREFIX_EMAIL).get());
        Address address = ParserUtil.parseAddress(argMultimap.getValue(CliSyntax.PREFIX_ADDRESS).get());

        // TODO: add classGroup implementation

        String assignmentNameValue = argMultimap.getValue(CliSyntax.PREFIX_ASSIGNMENT).orElse("");
        AssignmentName assignmentName = assignmentNameValue != "" ? ParserUtil.parseAssignmentName(assignmentNameValue)
                : null;

        Set<Tag> tagList = ParserUtil.parseTags(argMultimap.getAllValues(CliSyntax.PREFIX_TAG));

        Contact contact = new Contact(name, phone, email, address, tagList);

        return new AddContactCommand(contact, assignmentName);
    }

}
