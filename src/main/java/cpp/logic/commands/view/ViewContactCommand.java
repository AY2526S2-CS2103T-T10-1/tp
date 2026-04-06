package cpp.logic.commands.view;

import java.util.List;
import java.util.Objects;

import cpp.commons.core.index.Index;
import cpp.commons.util.ToStringBuilder;
import cpp.logic.Messages;
import cpp.logic.commands.CommandResult;
import cpp.logic.commands.exceptions.CommandException;
import cpp.model.Model;
import cpp.model.contact.Contact;

/**
 * Views a contact identified using its displayed index from the address book.
 * Displays full detailed information including name, phone, email, classes, and
 * assignments.
 */
public class ViewContactCommand extends ViewCommand {

    public static final String MESSAGE_SUCCESS = "Viewing details of Contact:\n%1$s";

    private final Index targetIndex;

    public ViewContactCommand(Index targetIndex) {
        this.targetIndex = targetIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        Objects.requireNonNull(model);
        List<Contact> lastShownList = model.getFilteredContactList();

        if (this.targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_CONTACT_DISPLAYED_INDEX);
        }

        Contact contactToView = lastShownList.get(this.targetIndex.getZeroBased());

        model.viewContact(contactToView);

        return new CommandResult(
                String.format(ViewContactCommand.MESSAGE_SUCCESS, Messages.format(contactToView)),
                CommandResult.ViewType.CONTACT);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof ViewContactCommand)) {
            return false;
        }

        ViewContactCommand otherViewContactCommand = (ViewContactCommand) other;
        return this.targetIndex.equals(otherViewContactCommand.targetIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", this.targetIndex)
                .toString();
    }
}
