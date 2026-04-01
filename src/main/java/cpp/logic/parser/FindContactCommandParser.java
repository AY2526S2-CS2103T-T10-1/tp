package cpp.logic.parser;

import java.util.Arrays;

import cpp.logic.Messages;
import cpp.logic.commands.FindContactCommand;
import cpp.logic.parser.exceptions.ParseException;
import cpp.model.contact.ContactEmailContainsKeywordsPredicate;
import cpp.model.contact.ContactNameContainsKeywordsPredicate;
import cpp.model.contact.ContactPhoneContainsKeywordsPredicate;
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
     * Supports finding by name (default), phone (p/KEYWORD), or email (e/KEYWORD)
     * Examples: findcontact alice
     * findcontact p/91234567
     * findcontact e/gmail
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    @Override
    public FindContactCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, CliSyntax.PREFIX_NAME,
                CliSyntax.PREFIX_PHONE, CliSyntax.PREFIX_EMAIL);

        argMultimap.verifyNoDuplicatePrefixesFor(CliSyntax.PREFIX_NAME, CliSyntax.PREFIX_PHONE, CliSyntax.PREFIX_EMAIL);

        ContactSearchPredicate predicate;

        boolean hasPhonePrefix = argMultimap.getValue(CliSyntax.PREFIX_PHONE).isPresent();
        boolean hasEmailPrefix = argMultimap.getValue(CliSyntax.PREFIX_EMAIL).isPresent();
        boolean hasNamePrefix = argMultimap.getValue(CliSyntax.PREFIX_NAME).isPresent();
        String preamble = argMultimap.getPreamble().trim();

        // Check for conflicting prefixes
        int prefixCount = (hasPhonePrefix ? 1 : 0) + (hasEmailPrefix ? 1 : 0) + (hasNamePrefix ? 1 : 0);
        if (prefixCount > 1) {
            throw new ParseException(
                    String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, FindContactCommand.MESSAGE_USAGE));
        }

        if (hasPhonePrefix) {
            if (!preamble.isEmpty()) {
                throw new ParseException(
                        String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, FindContactCommand.MESSAGE_USAGE));
            }
            String phoneValue = argMultimap.getValue(CliSyntax.PREFIX_PHONE).get().trim();
            if (phoneValue.isEmpty()) {
                throw new ParseException(
                        String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, FindContactCommand.MESSAGE_USAGE));
            }
            predicate = new ContactPhoneContainsKeywordsPredicate(Arrays.asList(phoneValue));
        } else if (hasEmailPrefix) {
            if (!preamble.isEmpty()) {
                throw new ParseException(
                        String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, FindContactCommand.MESSAGE_USAGE));
            }
            String emailValue = argMultimap.getValue(CliSyntax.PREFIX_EMAIL).get().trim();
            if (emailValue.isEmpty()) {
                throw new ParseException(
                        String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, FindContactCommand.MESSAGE_USAGE));
            }
            predicate = new ContactEmailContainsKeywordsPredicate(Arrays.asList(emailValue));
        } else if (hasNamePrefix) {
            if (!preamble.isEmpty()) {
                throw new ParseException(
                        String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, FindContactCommand.MESSAGE_USAGE));
            }
            String nameValue = argMultimap.getValue(CliSyntax.PREFIX_NAME).get().trim();
            if (nameValue.isEmpty()) {
                throw new ParseException(
                        String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, FindContactCommand.MESSAGE_USAGE));
            }
            String[] nameKeywords = nameValue.split("\\s+");
            predicate = new ContactNameContainsKeywordsPredicate(Arrays.asList(nameKeywords));
        } else {
            // Default to name search using preamble
            if (preamble.isEmpty()) {
                throw new ParseException(
                        String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, FindContactCommand.MESSAGE_USAGE));
            }
            // Reject unrecognized prefixes (e.g., c/, ass/, etc.)
            if (preamble.contains("/")) {
                throw new ParseException(
                        String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, FindContactCommand.MESSAGE_USAGE));
            }
            String[] nameKeywords = preamble.split("\\s+");
            predicate = new ContactNameContainsKeywordsPredicate(Arrays.asList(nameKeywords));
        }

        return new FindContactCommand(predicate);
    }

}
