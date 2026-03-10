package cpp.logic.commands.classgroup;

import java.util.List;
import java.util.Objects;

import cpp.commons.core.index.Index;
import cpp.commons.util.ToStringBuilder;
import cpp.logic.Messages;
import cpp.logic.commands.Command;
import cpp.logic.commands.CommandResult;
import cpp.logic.commands.exceptions.CommandException;
import cpp.logic.parser.CliSyntax;
import cpp.model.Model;
import cpp.model.classgroup.ClassGroup;
import cpp.model.contact.Contact;

/**
 * Adds a new class group to the class grouping list.
 */
public class AddClassGroupCommand extends Command {

    public static final String COMMAND_WORD = "addclass";

    public static final String MESSAGE_USAGE = AddClassGroupCommand.COMMAND_WORD + ": Defines a new class grouping. "
            + "Parameters: "
            + CliSyntax.PREFIX_CLASS + "NAME "
            + "[" + CliSyntax.PREFIX_CONTACT + "CONTACT INDICES...]\n"
            + "Example: " + AddClassGroupCommand.COMMAND_WORD + " "
            + CliSyntax.PREFIX_CLASS + "CS2103T10 "
            + CliSyntax.PREFIX_CONTACT + "1 2 3";

    public static final String MESSAGE_SUCCESS = "New class grouping added: %1$s";
    public static final String MESSAGE_DUPLICATE_CLASS_GROUP = "This class grouping already exists in the address book";

    private final ClassGroup toAdd;
    private final List<Index> contactIndices;

    /**
     * Creates an AddClassGroupCommand with the specified class group to add.
     */
    public AddClassGroupCommand(ClassGroup classGroup, List<Index> contactIndices) {
        Objects.requireNonNull(classGroup);
        Objects.requireNonNull(contactIndices);
        this.toAdd = classGroup;
        this.contactIndices = contactIndices;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        Objects.requireNonNull(model);

        if (model.hasClassGroup(this.toAdd)) {
            throw new CommandException(AddClassGroupCommand.MESSAGE_DUPLICATE_CLASS_GROUP);
        }

        List<Contact> lastShownContactList = model.getFilteredContactList();
        this.checkContactIndicesInRange(lastShownContactList, this.contactIndices);
        this.allocateContactsToClassGroup(model, lastShownContactList);

        model.addClassGroup(this.toAdd);
        return new CommandResult(String.format(AddClassGroupCommand.MESSAGE_SUCCESS, Messages.format(this.toAdd)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof AddClassGroupCommand)) {
            return false;
        }

        AddClassGroupCommand o = (AddClassGroupCommand) other;
        return this.toAdd.getName().equals(o.toAdd.getName()) && this.contactIndices.equals(o.contactIndices);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("toAdd", this.toAdd)
                .add("contactIndices", this.contactIndices)
                .toString();
    }

    private void checkContactIndicesInRange(List<Contact> lastShownContactList, List<Index> contactIndices)
            throws CommandException {
        for (Index index : contactIndices) {
            if (index.getZeroBased() >= lastShownContactList.size()) {
                throw new CommandException(Messages.MESSAGE_INVALID_CONTACT_DISPLAYED_INDEX);
            }
        }
    }

    private void allocateContactsToClassGroup(Model model, List<Contact> lastShownContactList) {
        for (Index index : this.contactIndices) {
            String contactId = lastShownContactList.get(index.getZeroBased()).getId();

            // Assumption: No catching of ContactAlreadyAllocatedClassGroupException is
            // necessary, as the ClassGroup is newly created and thus cannot have any
            // contacts allocated to it yet.
            this.toAdd.allocateContact(contactId);
        }
    }
}
