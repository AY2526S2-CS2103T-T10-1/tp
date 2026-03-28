package cpp.logic.commands.classgroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import cpp.commons.core.LogsCenter;
import cpp.commons.core.index.Index;
import cpp.commons.util.ToStringBuilder;
import cpp.logic.LogicManager;
import cpp.logic.Messages;
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
 * Allocates contact(s) to a class group by their displayed indices.
 */
public class AllocateClassGroupCommand extends Command {

    public static final String COMMAND_WORD = "allocclass";

    public static final String MESSAGE_USAGE = AllocateClassGroupCommand.COMMAND_WORD
            + ": Allocates contact(s) to a class group.\n"
            + "Parameters: "
            + CliSyntax.PREFIX_CLASS + "CLASS_NAME "
            + CliSyntax.PREFIX_CONTACT + "CONTACT_INDICES...\n"
            + "Example: " + AllocateClassGroupCommand.COMMAND_WORD + " "
            + CliSyntax.PREFIX_CLASS + "CS2103T10 "
            + CliSyntax.PREFIX_CONTACT + "1 2 3";

    public static final String MESSAGE_SUCCESS = """
            Allocated class group: %1$s to %2$s contact(s).
            Contacts allocated: %3$s
            Contacts not allocated (already allocated to class group): %4$s""";
    public static final String MESSAGE_ALLOCATION_FAILED = """
            No new contacts were allocated the class group.
            Contacts not allocated (already allocated to class group): %1$s""";

    private final ClassGroupName classGroupName;
    private final List<Index> contactIndices;

    private int successfulAllocationCount = 0;
    private int unsuccessfulAllocationCount = 0;
    private StringBuilder successfulContactAllocations = new StringBuilder();
    private StringBuilder unsuccessfulContactAllocations = new StringBuilder();

    // Logger for logging unsuccessful allocations
    private final Logger logger = LogsCenter.getLogger(LogicManager.class);

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
            throw new CommandException(Messages.MESSAGE_CLASS_GROUP_NOT_FOUND);
        }

        List<Contact> lastShownContactList = model.getFilteredContactList();
        CommandUtil.checkContactIndices(lastShownContactList, this.contactIndices);
        this.allocateContactsToClassGroup(classGroupToAllocate, lastShownContactList);

        if (this.unsuccessfulAllocationCount == 0) {
            this.unsuccessfulContactAllocations.append("None");
        }

        if (this.successfulAllocationCount == 0) {
            throw new CommandException(String.format(AllocateClassGroupCommand.MESSAGE_ALLOCATION_FAILED,
                    this.unsuccessfulContactAllocations.toString()));
        }

        return new CommandResult(
                String.format(AllocateClassGroupCommand.MESSAGE_SUCCESS, this.classGroupName.fullName,
                        this.successfulAllocationCount, this.successfulContactAllocations.toString(),
                        this.unsuccessfulContactAllocations.toString()));
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

    private void allocateContactsToClassGroup(ClassGroup classGroupToAllocate, List<Contact> lastShownContactList) {
        for (Index index : this.contactIndices) {
            Contact contact = lastShownContactList.get(index.getZeroBased());
            String contactId = contact.getId();
            try {
                classGroupToAllocate.allocateContact(contactId);
                this.successfulAllocationCount++;
                this.buildSuccessfulAllocationString(contact.getName().fullName);
            } catch (ContactAlreadyAllocatedClassGroupException e) {
                this.logger.info("Contact already allocated to class group: " + contact.getName().fullName);
                this.unsuccessfulAllocationCount++;
                this.buildUnsuccessfulAllocationString(contact.getName().fullName);
            }
        }
    }

    private void buildSuccessfulAllocationString(String contactName) {
        if (this.successfulContactAllocations.length() > 0) {
            this.successfulContactAllocations.append("; ");
        }
        this.successfulContactAllocations.append(contactName);
    }

    private void buildUnsuccessfulAllocationString(String contactName) {
        if (this.unsuccessfulContactAllocations.length() > 0) {
            this.unsuccessfulContactAllocations.append("; ");
        }
        this.unsuccessfulContactAllocations.append(contactName);
    }
}
