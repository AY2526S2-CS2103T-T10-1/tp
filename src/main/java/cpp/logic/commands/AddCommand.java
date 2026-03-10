package cpp.logic.commands;

import java.util.Objects;

import cpp.commons.util.ToStringBuilder;
import cpp.logic.Messages;
import cpp.logic.commands.exceptions.CommandException;
import cpp.logic.parser.CliSyntax;
import cpp.model.Model;
import cpp.model.contact.Contact;

/**
 * Adds a contact to the address book.
 */
public class AddCommand extends Command {

    public static final String COMMAND_WORD = "addcontact";

    public static final String MESSAGE_USAGE = AddCommand.COMMAND_WORD + ": Adds a contact to the address book. "
            + "Parameters: "
            + CliSyntax.PREFIX_NAME + "NAME "
            + CliSyntax.PREFIX_PHONE + "PHONE "
            + CliSyntax.PREFIX_EMAIL + "EMAIL "
            + CliSyntax.PREFIX_ADDRESS + "ADDRESS "
            + "[" + CliSyntax.PREFIX_CLASS + "CLASS_NAME] "
            + "[" + CliSyntax.PREFIX_ASSIGNMENT + "ASSIGNMENT_NAME]\n"
            + "Example: " + AddCommand.COMMAND_WORD + " "
            + CliSyntax.PREFIX_NAME + "John Doe "
            + CliSyntax.PREFIX_PHONE + "98765432 "
            + CliSyntax.PREFIX_EMAIL + "johnd@example.com "
            + CliSyntax.PREFIX_ADDRESS + "311, Clementi Ave 2, #02-25 "
            + CliSyntax.PREFIX_TAG + "friends "
            + CliSyntax.PREFIX_TAG + "owesMoney";

    public static final String MESSAGE_SUCCESS = "New contact added: %1$s";
    public static final String MESSAGE_DUPLICATE_CONTACT = "This contact already exists in the address book";

    private final Contact toAdd;

    /**
     * Creates an AddCommand to add the specified {@code Contact}
     */
    public AddCommand(Contact contact) {
        Objects.requireNonNull(contact);
        this.toAdd = contact;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        Objects.requireNonNull(model);

        if (model.hasContact(this.toAdd)) {
            throw new CommandException(AddCommand.MESSAGE_DUPLICATE_CONTACT);
        }

        model.addContact(this.toAdd);
        return new CommandResult(String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(this.toAdd)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddCommand)) {
            return false;
        }

        AddCommand otherAddCommand = (AddCommand) other;
        return this.toAdd.equals(otherAddCommand.toAdd);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("toAdd", this.toAdd)
                .toString();
    }
}
