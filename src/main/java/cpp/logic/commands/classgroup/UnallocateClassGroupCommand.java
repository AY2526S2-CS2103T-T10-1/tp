package cpp.logic.commands.classgroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import cpp.commons.core.LogsCenter;
import cpp.commons.core.index.Index;
import cpp.commons.util.ToStringBuilder;
import cpp.logic.LogicManager;
import cpp.logic.commands.Command;
import cpp.logic.commands.CommandResult;
import cpp.logic.commands.CommandUtil;
import cpp.logic.commands.exceptions.CommandException;
import cpp.logic.parser.CliSyntax;
import cpp.model.Model;
import cpp.model.classgroup.ClassGroup;
import cpp.model.classgroup.ClassGroupName;
import cpp.model.classgroup.exceptions.ContactNotAllocatedClassGroupException;
import cpp.model.contact.Contact;
import cpp.model.util.ClassGroupUtil;

/**
 * Unallocates contact(s) from a class group by their displayed indices.
 */
public class UnallocateClassGroupCommand extends Command {

    public static final String COMMAND_WORD = "unallocclass";

    public static final String MESSAGE_USAGE = UnallocateClassGroupCommand.COMMAND_WORD
            + ": Unallocates contact(s) from a class group.\n"
            + "Parameters: "
            + CliSyntax.PREFIX_CLASS + "CLASS_NAME "
            + CliSyntax.PREFIX_CONTACT + "CONTACT_INDICES...\n"
            + "Example: " + UnallocateClassGroupCommand.COMMAND_WORD + " "
            + CliSyntax.PREFIX_CLASS + "CS2103T10 "
            + CliSyntax.PREFIX_CONTACT + "1 2 3";

    public static final String MESSAGE_SUCCESS = """
            Unallocated class group: %1$s from %2$s contact(s).
            Contacts unallocated: %3$s
            Contacts not unallocated (not allocated to class group initially): %4$s""";
    public static final String MESSAGE_INVALID_CLASS_GROUP_NAME = "The class group name provided is invalid";
    public static final String MESSAGE_UNALLOCATION_FAILED = """
            No contacts were unallocated from the class group.
            Contacts not unallocated (not allocated to class group initially): %1$s""";

    private final ClassGroupName classGroupName;
    private final List<Index> contactIndices;

    private int successfulUnallocationCount;
    private int unsuccessfulUnallocationCount;
    private StringBuilder successfulContactUnallocations = new StringBuilder();
    private StringBuilder unsuccessfulContactUnallocations = new StringBuilder();

    // Logger for logging unsuccessful unallocations
    private final Logger logger = LogsCenter.getLogger(LogicManager.class);

    /**
     * Creates an UnallocateClassGroupCommand with the specified class group name
     * and contact indices.
     */
    public UnallocateClassGroupCommand(ClassGroupName classGroupName, List<Index> contactIndices) {
        Objects.requireNonNull(classGroupName);
        Objects.requireNonNull(contactIndices);
        this.classGroupName = classGroupName;
        this.contactIndices = new ArrayList<>(contactIndices);
        this.successfulUnallocationCount = 0;
        this.unsuccessfulUnallocationCount = 0;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        Objects.requireNonNull(model);

        List<ClassGroup> classGroupList = model.getAddressBook().getClassGroupList();
        ClassGroup classGroupToUnallocate = ClassGroupUtil.findClassGroup(classGroupList, this.classGroupName);

        if (classGroupToUnallocate == null) {
            throw new CommandException(UnallocateClassGroupCommand.MESSAGE_INVALID_CLASS_GROUP_NAME);
        }

        List<Contact> lastShownContactList = model.getFilteredContactList();
        CommandUtil.checkContactIndices(lastShownContactList, this.contactIndices);
        this.unallocateContactsFromClassGroup(classGroupToUnallocate, lastShownContactList);

        if (this.unsuccessfulUnallocationCount == 0) {
            this.unsuccessfulContactUnallocations.append("None");
        }

        if (this.successfulUnallocationCount == 0) {
            throw new CommandException(String.format(UnallocateClassGroupCommand.MESSAGE_UNALLOCATION_FAILED,
                    this.unsuccessfulContactUnallocations.toString()));
        }

        return new CommandResult(
                String.format(UnallocateClassGroupCommand.MESSAGE_SUCCESS, this.classGroupName.fullName,
                        this.successfulUnallocationCount, this.successfulContactUnallocations.toString(),
                        this.unsuccessfulContactUnallocations.toString()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof UnallocateClassGroupCommand)) {
            return false;
        }
        UnallocateClassGroupCommand o = (UnallocateClassGroupCommand) other;
        return this.classGroupName.equals(o.classGroupName)
                && this.contactIndices.equals(o.contactIndices);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("classGroupName", this.classGroupName)
                .add("contactIndices", this.contactIndices)
                .toString();
    }

    private void unallocateContactsFromClassGroup(ClassGroup classGroupToUnallocate,
            List<Contact> lastShownContactList) throws CommandException {
        for (Index contactIndex : this.contactIndices) {
            Contact contactToUnallocate = lastShownContactList.get(contactIndex.getZeroBased());
            try {
                classGroupToUnallocate.unallocateContact(contactToUnallocate.getId());
                this.successfulUnallocationCount++;
                this.buildSuccessfulUnallocationString(contactToUnallocate.getName().fullName);
            } catch (ContactNotAllocatedClassGroupException e) {
                this.logger.info(
                        "Contact already unallocated from class group: " + contactToUnallocate.getName().fullName);
                this.unsuccessfulUnallocationCount++;
                this.buildUnsuccessfulUnallocationString(contactToUnallocate.getName().fullName);
            }
        }
    }

    private void buildSuccessfulUnallocationString(String contactName) {
        if (this.successfulContactUnallocations.length() > 0) {
            this.successfulContactUnallocations.append("; ");
        }
        this.successfulContactUnallocations.append(contactName);
    }

    private void buildUnsuccessfulUnallocationString(String contactName) {
        if (this.unsuccessfulContactUnallocations.length() > 0) {
            this.unsuccessfulContactUnallocations.append("; ");
        }
        this.unsuccessfulContactUnallocations.append(contactName);
    }
}
