package cpp.logic.commands.classgroup;

import java.util.List;
import java.util.Objects;

import cpp.commons.core.index.Index;
import cpp.commons.util.ToStringBuilder;
import cpp.logic.Messages;
import cpp.logic.commands.Command;
import cpp.logic.commands.CommandResult;
import cpp.logic.commands.CommandUtil;
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
    public static final String COMMAND_WORD_ALIAS = "addc";

    public static final String MESSAGE_USAGE = AddClassGroupCommand.COMMAND_WORD
            + ": Adds a new class grouping.\n"
            + "Alias: " + AddClassGroupCommand.COMMAND_WORD_ALIAS + "\n"
            + "Parameters: "
            + CliSyntax.PREFIX_CLASS + "CLASS_NAME "
            + "[" + CliSyntax.PREFIX_CONTACT + "CONTACT_INDICES...]\n"
            + "Example: " + AddClassGroupCommand.COMMAND_WORD + " "
            + CliSyntax.PREFIX_CLASS + "CS2103T T10 1 "
            + CliSyntax.PREFIX_CONTACT + "1 2 3";

    public static final String MESSAGE_SUCCESS = "New class group added: %1$s";
    public static final String MESSAGE_DUPLICATE_CLASS_GROUP = "This class group already exists in the address book";
    public static final String MESSAGE_SUCCESS_WITH_ALLOCATION = """
            New class group added: %1$s
            Allocated class group to %2$s contact(s).
            Contacts allocated: %3$s""";

    private final ClassGroup toAdd;
    private final List<Index> contactIndices;

    private int allocatedCount;
    private StringBuilder allocatedContacts;

    /**
     * Creates an AddClassGroupCommand with the specified class group to add.
     */
    public AddClassGroupCommand(ClassGroup classGroup, List<Index> contactIndices) {
        Objects.requireNonNull(classGroup);
        Objects.requireNonNull(contactIndices);
        this.toAdd = classGroup;
        this.contactIndices = contactIndices;
        this.allocatedCount = 0;
        this.allocatedContacts = new StringBuilder();
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        Objects.requireNonNull(model);

        if (model.hasClassGroup(this.toAdd)) {
            throw new CommandException(AddClassGroupCommand.MESSAGE_DUPLICATE_CLASS_GROUP);
        }

        List<Contact> lastShownContactList = model.getFilteredContactList();
        CommandUtil.checkContactIndices(lastShownContactList, this.contactIndices);
        this.allocateContactsToClassGroup(model, lastShownContactList);

        model.addClassGroup(this.toAdd);

        if (this.contactIndices.isEmpty()) {
            return new CommandResult(String.format(AddClassGroupCommand.MESSAGE_SUCCESS, Messages.format(this.toAdd)));
        } else {
            return new CommandResult(String.format(AddClassGroupCommand.MESSAGE_SUCCESS_WITH_ALLOCATION,
                    Messages.format(this.toAdd), this.allocatedCount, this.allocatedContacts.toString()));
        }
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

    private void allocateContactsToClassGroup(Model model, List<Contact> lastShownContactList) {
        for (Index index : this.contactIndices) {
            Contact contactToAllocate = lastShownContactList.get(index.getZeroBased());
            String contactId = contactToAllocate.getId();

            // Assumption: No catching of ContactAlreadyAllocatedClassGroupException is
            // necessary, as the ClassGroup is newly created and thus cannot have any
            // contacts allocated to it yet.
            this.toAdd.allocateContact(contactId);
            this.allocatedCount++;
            if (this.allocatedContacts.length() > 0) {
                this.allocatedContacts.append("; ");
            }
            this.allocatedContacts.append(contactToAllocate.getName().fullName);
        }
    }
}
