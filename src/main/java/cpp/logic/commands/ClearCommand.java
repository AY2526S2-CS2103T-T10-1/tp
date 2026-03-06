package cpp.logic.commands;

import java.util.Objects;

import cpp.model.AddressBook;
import cpp.model.Model;

/**
 * Clears the address book.
 */
public class ClearCommand extends Command {

    public static final String COMMAND_WORD = "clear";
    public static final String MESSAGE_SUCCESS = "Address book has been cleared!";

    @Override
    public CommandResult execute(Model model) {
        Objects.requireNonNull(model);
        model.setAddressBook(new AddressBook());
        return new CommandResult(ClearCommand.MESSAGE_SUCCESS);
    }
}
