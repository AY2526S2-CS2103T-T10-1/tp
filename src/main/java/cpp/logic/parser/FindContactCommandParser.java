package cpp.logic.parser;

import cpp.logic.Messages;
import cpp.logic.commands.FindContactCommand;
import cpp.logic.parser.exceptions.ParseException;
import cpp.model.contact.ContactEmailMatchesKeywordsPredicate;
import cpp.model.contact.ContactNameContainsKeywordsPredicate;
import cpp.model.contact.ContactPhoneMatchesKeywordsPredicate;
import cpp.model.contact.ContactSearchPredicate;

/**
 * Parses input arguments and creates a new FindContactCommand object
 */
public class FindContactCommandParser implements Parser<FindContactCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the
     * FindContactCommand
     * and returns a FindContactCommand object for execution.
     *
     * Supports finding by name (n/KEYWORD), phone (p/KEYWORD), or email (e/KEYWORD)
     * Examples: findcontact n/Alice Bob
     * findcontact p/91234567
     * findcontact e/alice@gmail.com
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    @Override
    public FindContactCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer
                .untrimmedTokenize(args, CliSyntax.PREFIX_NAME, CliSyntax.PREFIX_PHONE, CliSyntax.PREFIX_EMAIL);

        argMultimap.verifyNoDuplicatePrefixesFor(CliSyntax.PREFIX_NAME, CliSyntax.PREFIX_PHONE, CliSyntax.PREFIX_EMAIL);

        ContactSearchPredicate predicate;

        boolean hasPhonePrefix = argMultimap.getValue(CliSyntax.PREFIX_PHONE).isPresent();
        boolean hasEmailPrefix = argMultimap.getValue(CliSyntax.PREFIX_EMAIL).isPresent();
        boolean hasNamePrefix = argMultimap.getValue(CliSyntax.PREFIX_NAME).isPresent();

        // Check for conflicting prefixes
        int prefixCount = (hasPhonePrefix ? 1 : 0) + (hasEmailPrefix ? 1 : 0) + (hasNamePrefix ? 1 : 0);
        if (prefixCount > 1) {
            throw new ParseException(
                    String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, FindContactCommand.MESSAGE_USAGE));
        }

        if (!argMultimap.getPreamble().trim().isEmpty()) {
            throw new ParseException(
                    String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, FindContactCommand.MESSAGE_USAGE));
        }

        if (hasPhonePrefix) {
            String phoneValue = argMultimap.getValue(CliSyntax.PREFIX_PHONE).get().trim();
            if (!phoneValue.matches("\\d+")) {
                throw new ParseException("Phone number search string must contain 1 or more digits.");
            }
            predicate = new ContactPhoneMatchesKeywordsPredicate(phoneValue);
        } else if (hasEmailPrefix) {
            String emailValue = argMultimap.getValue(CliSyntax.PREFIX_EMAIL).get().trim();
            if (!emailValue.matches("[A-Za-z0-9+_.@-]+")) {
                throw new ParseException(
                        "Email search string must contain 1 or more letters, digits, +, _, ., @, -");
            }
            predicate = new ContactEmailMatchesKeywordsPredicate(emailValue);
        } else if (hasNamePrefix) {
            String nameValue = argMultimap.getValue(CliSyntax.PREFIX_NAME).get().replaceAll("\\s+", " ");
            if (!nameValue.matches("(?i)([A-Za-z0-9()\\- ]|s/o|d/o)+")) {
                throw new ParseException("""
                        Contact name search string must contain 1 or more letters, digits, -, (, ), \
                        s/o (case-insensitive), d/o (case-insensitive), and spaces""");
            }
            predicate = new ContactNameContainsKeywordsPredicate(nameValue);
        } else {
            throw new ParseException(
                    String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, FindContactCommand.MESSAGE_USAGE));
        }

        return new FindContactCommand(predicate);
    }

}
