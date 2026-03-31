package cpp.logic.commands;

import java.util.Objects;

import cpp.commons.util.ToStringBuilder;
import cpp.logic.Messages;
import cpp.model.Model;
import cpp.model.contact.ContactSearchPredicate;

/**
 * Finds and lists all contacts in address book whose name/phone/email contains
 * any of the
 * argument keywords.
 * Keyword matching is case insensitive.
 */
public class FindContactCommand extends Command {

    public static final String COMMAND_WORD = "findcontact";
    public static final String COMMAND_WORD_ABBREV = "findct";

    public static final String MESSAGE_USAGE = FindContactCommand.COMMAND_WORD + " ("
            + FindContactCommand.COMMAND_WORD_ABBREV + ")"
            + ": Finds all contacts whose name/phone/email contain any of "
            + "the specified keywords (case-insensitive) and displays them as a list with index numbers.\n"
            + "Parameters: KEYWORD [MORE_KEYWORDS]... | p/KEYWORD [MORE_KEYWORDS]... | e/KEYWORD [MORE_KEYWORDS]...\n"
            + "Default search: name\n"
            + "Example: " + FindContactCommand.COMMAND_WORD + " alice bob\n"
            + "Example: " + FindContactCommand.COMMAND_WORD + " p/91234567 98765432\n"
            + "Example: " + FindContactCommand.COMMAND_WORD + " e/gmail yahoo";

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
