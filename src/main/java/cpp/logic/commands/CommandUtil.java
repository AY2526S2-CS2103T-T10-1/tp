package cpp.logic.commands;

import java.util.List;

import cpp.commons.core.index.Index;
import cpp.logic.Messages;
import cpp.logic.commands.exceptions.CommandException;
import cpp.model.contact.Contact;

/**
 * Utility class for Command related operations.
 */
public class CommandUtil {

    /**
     * Checks if the provided contact indices are valid against the last shown
     * contact list.
     */
    public static void checkContactIndices(List<Contact> lastShownContactList, List<Index> contactIndices)
            throws CommandException {

        if (lastShownContactList.isEmpty()) {
            throw new CommandException(Messages.MESSAGE_INVALID_CONTACT_DISPLAYED_INDEX
                    + '\n'
                    + Messages.EMPTY_CONTACT_LIST);
        }

        for (Index idx : contactIndices) {
            if (idx.getZeroBased() >= lastShownContactList.size()) {
                throw new CommandException(Messages.MESSAGE_INVALID_CONTACT_DISPLAYED_INDEX
                        + '\n'
                        + String.format(Messages.MESSAGE_VALID_INDEX_BOUNDS, lastShownContactList.size()));
            }
        }
    }
}
