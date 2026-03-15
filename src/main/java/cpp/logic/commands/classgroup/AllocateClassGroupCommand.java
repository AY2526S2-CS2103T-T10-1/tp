package cpp.logic.commands.classgroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cpp.commons.core.index.Index;
import cpp.commons.util.ToStringBuilder;
import cpp.logic.commands.Command;
import cpp.logic.commands.CommandResult;
import cpp.logic.commands.CommandUtil;
import cpp.logic.commands.exceptions.CommandException;
import cpp.logic.parser.CliSyntax;
import cpp.model.Model;
import cpp.model.classgroup.ClassGroup;
import cpp.model.classgroup.ClassGroupName;
import cpp.model.classgroup.exceptions.ContactAlreadyAllocatedClassGroupException;
import cpp.model.contact.Contact;
import cpp.model.util.ClassGroupUtil;

/**
 * Allocates a contact to a class group by their displayed indices.
 */
public class AllocateClassGroupCommand extends Command {

    public static final String COMMAND_WORD = "allocclass";

    public static final String MESSAGE_USAGE = AllocateClassGroupCommand.COMMAND_WORD
            + ": Allocates an contact to a class group. "
            + "Parameters: "
            + CliSyntax.PREFIX_CLASS + "CLASS NAME "
            + CliSyntax.PREFIX_CONTACT + "CONTACT INDICES...\n"
            + "Example: " + AllocateClassGroupCommand.COMMAND_WORD + " "
            + CliSyntax.PREFIX_CLASS + "CS2103T10 "
            + CliSyntax.PREFIX_CONTACT + "1 2 3";

    public static final String MESSAGE_SUCCESS = """
            Allocated class group: %1$s to %2$s contacts.\nContacts allocated: %3$s""";
    public static final String MESSAGE_INVALID_CLASS_GROUP_NAME = "The class group name provided is invalid";
    public static final String MESSAGE_ALLOCATION_FAILED = "No new contacts were allocated the class group";

    private final ClassGroupName classGroupName;
    private final List<Index> contactIndices;

    private int successfulAllocations = 0;
    private StringBuilder successfullyAllocatedNames = new StringBuilder();

    /**
     * Creates an AllocateClassGroupCommand with the specified class group name and
     * contact indices.
     */
    public AllocateClassGroupCommand(ClassGroupName classGroupName, List<Index> contactIndices) {
        Objects.requireNonNull(classGroupName);
        Objects.requireNonNull(contactIndices);
        this.classGroupName = classGroupName;
        this.contactIndices = new ArrayList<>(contactIndices);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        Objects.requireNonNull(model);

        List<ClassGroup> classGroupList = model.getAddressBook().getClassGroupList();
        ClassGroup classGroupToAllocate = ClassGroupUtil.findClassGroup(classGroupList, this.classGroupName);

        if (classGroupToAllocate == null) {
            throw new CommandException(AllocateClassGroupCommand.MESSAGE_INVALID_CLASS_GROUP_NAME);
        }

        List<Contact> lastShownContactList = model.getFilteredContactList();
        CommandUtil.checkContactIndices(lastShownContactList, this.contactIndices);
        this.allocateContactsToClassGroup(classGroupToAllocate, lastShownContactList);

        return new CommandResult(
                String.format(AllocateClassGroupCommand.MESSAGE_SUCCESS, this.classGroupName.fullName,
                        this.successfulAllocations, this.successfullyAllocatedNames.toString()));
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof AllocateClassGroupCommand)) {
            return false;
        }

        AllocateClassGroupCommand o = (AllocateClassGroupCommand) other;
        return o.classGroupName.equals(this.classGroupName)
                && o.contactIndices.equals(this.contactIndices);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("classGroupName", this.classGroupName)
                .add("contactIndices", this.contactIndices)
                .toString();
    }

    private void allocateContactsToClassGroup(ClassGroup classGroupToAllocate, List<Contact> lastShownContactList)
            throws CommandException {
        boolean anySuccessfulAllocation = false;
        for (Index index : this.contactIndices) {
            String contactId = lastShownContactList.get(index.getZeroBased()).getId();
            try {
                classGroupToAllocate.allocateContact(contactId);
                anySuccessfulAllocation = true;
                this.successfulAllocations++;
                this.buildSuccessfulAllocationString(lastShownContactList.get(index.getZeroBased()).getName().fullName);
            } catch (ContactAlreadyAllocatedClassGroupException e) {
                // Contact is already allocated to this class group, skip and continue
                // allocating the rest of the contacts
            }
        }

        if (!anySuccessfulAllocation) {
            throw new CommandException(AllocateClassGroupCommand.MESSAGE_ALLOCATION_FAILED);
        }
    }

    private void buildSuccessfulAllocationString(String contactName) {
        if (this.successfullyAllocatedNames.length() > 0) {
            this.successfullyAllocatedNames.append("; ");
        }
        this.successfullyAllocatedNames.append(contactName);
    }
}
