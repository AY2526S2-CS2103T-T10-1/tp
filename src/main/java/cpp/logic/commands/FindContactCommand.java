package cpp.logic.commands;

import java.util.Objects;

import cpp.commons.util.ToStringBuilder;
import cpp.logic.Messages;
import cpp.logic.parser.CliSyntax;
import cpp.model.Model;
import cpp.model.contact.ContactSearchPredicate;

/**
 * Finds and lists all contacts in address book using the {@code findcontact}
 * ({@code findct}) command.
 * <p>
 * Supports three search modes:
 * <br>
 * - Name search: finds contacts whose names contain any of the specified
 * keywords (case-insensitive, keyword-based search).<br>
 * - Phone search: finds contacts whose phone number matches the specified value
 * exactly.<br>
 * - Email search: finds contacts whose email address matches the specified
 * value exactly (case-insensitive).
 */
public class FindContactCommand extends Command {

    public static final String COMMAND_WORD = "findcontact";
    public static final String COMMAND_WORD_ALIAS = "findct";

    public static final String MESSAGE_USAGE = FindContactCommand.COMMAND_WORD
            + ": Finds all contacts whose name contains any of the specified keywords or whose phone/email "
            + "match exactly (case-insensitive) and displays them as a list with index numbers.\n"
            + "Alias: " + FindContactCommand.COMMAND_WORD_ALIAS + "\n"
            + "Parameters: ["
            + CliSyntax.PREFIX_NAME + "NAME_KEYWORDS...] ["
            + CliSyntax.PREFIX_PHONE + "PHONE_NUMBER] ["
            + CliSyntax.PREFIX_EMAIL + "EMAIL]\n"
            + "Exactly one of ["
            + CliSyntax.PREFIX_NAME + "NAME_KEYWORDS...], ["
            + CliSyntax.PREFIX_PHONE + "PHONE_NUMBER], or ["
            + CliSyntax.PREFIX_EMAIL + "EMAIL] must be provided.\n"
            + "Example: " + FindContactCommand.COMMAND_WORD + " " + CliSyntax.PREFIX_NAME + "alice bob\n"
            + "Example: " + FindContactCommand.COMMAND_WORD + " " + CliSyntax.PREFIX_PHONE + "91234567\n"
            + "Example: " + FindContactCommand.COMMAND_WORD + " " + CliSyntax.PREFIX_EMAIL + "alice@gmail.com";

    private final ContactSearchPredicate predicate;

    public FindContactCommand(ContactSearchPredicate predicate) {
        this.predicate = predicate;
    }

    @Override
    public CommandResult execute(Model model) {
        Objects.requireNonNull(model);
        model.updateFilteredContactList(this.predicate);
        return new CommandResult(
                String.format(Messages.MESSAGE_CONTACTS_LISTED_OVERVIEW, model.getFilteredContactList().size()),
                CommandResult.ListView.CONTACTS);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof FindContactCommand)) {
            return false;
        }

        FindContactCommand otherFindContactCommand = (FindContactCommand) other;
        return this.predicate.equals(otherFindContactCommand.predicate);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("predicate", this.predicate)
                .toString();
    }
}
