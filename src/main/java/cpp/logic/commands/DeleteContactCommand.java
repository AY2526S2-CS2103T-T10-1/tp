package cpp.logic.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cpp.commons.core.index.Index;
import cpp.commons.util.ToStringBuilder;
import cpp.logic.Messages;
import cpp.logic.commands.exceptions.CommandException;
import cpp.model.Model;
import cpp.model.contact.Contact;

/**
 * Deletes one or more contacts identified using their displayed indices from the address book.
 */
public class DeleteContactCommand extends DeleteCommand {

    public static final String MESSAGE_DELETE_CONTACT_SUCCESS = "Deleted Contact: %1$s";

    private final List<Index> targetIndices;

    public DeleteContactCommand(List<Index> targetIndices) {
        this.targetIndices = targetIndices;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        Objects.requireNonNull(model);
        List<Contact> lastShownList = model.getFilteredContactList();

        for (Index index : this.targetIndices) {
            if (index.getZeroBased() >= lastShownList.size()) {
                throw new CommandException(Messages.MESSAGE_INVALID_CONTACT_DISPLAYED_INDEX);
            }
        }

        List<Contact> contactsToDelete = new ArrayList<>();
        for (Index index : this.targetIndices) {
            contactsToDelete.add(lastShownList.get(index.getZeroBased()));
        }

        StringBuilder result = new StringBuilder();
        for (Contact contact : contactsToDelete) {
            model.deleteContact(contact);
            result.append(String.format(DeleteContactCommand.MESSAGE_DELETE_CONTACT_SUCCESS,
                    Messages.format(contact))).append("\n");
        }

        return new CommandResult(result.toString().trim());
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof DeleteContactCommand)) {
            return false;
        }

        DeleteContactCommand otherDeleteContactCommand = (DeleteContactCommand) other;
        return this.targetIndices.equals(otherDeleteContactCommand.targetIndices);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndices", this.targetIndices)
                .toString();
    }
}
