package cpp.logic.parser;

import java.util.Arrays;

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
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, CliSyntax.PREFIX_NAME, CliSyntax.PREFIX_PHONE,
                CliSyntax.PREFIX_EMAIL);

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
            ParserUtil.parsePhone(phoneValue);
            predicate = new ContactPhoneMatchesKeywordsPredicate(Arrays.asList(phoneValue));
        } else if (hasEmailPrefix) {
            String emailValue = argMultimap.getValue(CliSyntax.PREFIX_EMAIL).get().trim();
            ParserUtil.parseEmail(emailValue);
            predicate = new ContactEmailMatchesKeywordsPredicate(Arrays.asList(emailValue));
        } else if (hasNamePrefix) {
            String nameValue = argMultimap.getValue(CliSyntax.PREFIX_NAME).get().trim();
            ParserUtil.parseName(nameValue);
            String[] nameKeywords = nameValue.split("\\s+");
            predicate = new ContactNameContainsKeywordsPredicate(Arrays.asList(nameKeywords));
        } else {
            throw new ParseException(
                    String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, FindContactCommand.MESSAGE_USAGE));
        }

        return new FindContactCommand(predicate);
    }

}
