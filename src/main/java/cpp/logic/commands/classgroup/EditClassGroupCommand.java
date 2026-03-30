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
import cpp.model.classgroup.ClassGroupName;
import cpp.model.util.ClassGroupUtil;

/**
 * Edits the name of an existing class group in the address book.
 */
public class EditClassGroupCommand extends Command {

    public static final String COMMAND_WORD = "editclass";

    public static final String MESSAGE_USAGE = EditClassGroupCommand.COMMAND_WORD
            + ": Edits the name of the class group identified "
            + "by the index number used in the displayed class group list.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + CliSyntax.PREFIX_CLASS + "CLASS_NAME\n"
            + "Example: " + EditClassGroupCommand.COMMAND_WORD + " 1 "
            + CliSyntax.PREFIX_CLASS + "CS2103T11";

    public static final String MESSAGE_EDIT_CLASS_GROUP_SUCCESS = "Edited Class Group: %1$s";
    public static final String MESSAGE_DUPLICATE_CLASS_GROUP = "This class group already exists in the address book.";

    private final Index index;
    private final ClassGroupName newName;

    /**
     * @param index   of the class group in the filtered class group list to edit
     * @param newName new name for the class group
     */
    public EditClassGroupCommand(Index index, ClassGroupName newName) {
        Objects.requireNonNull(index);
        Objects.requireNonNull(newName);
        this.index = index;
        this.newName = newName;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        Objects.requireNonNull(model);
        List<ClassGroup> lastShownList = model.getFilteredClassGroupList();

        if (this.index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_CLASS_GROUP_DISPLAYED_INDEX);
        }

        ClassGroup classGroupToEdit = lastShownList.get(this.index.getZeroBased());

        ClassGroup potentialDuplicate = ClassGroupUtil.findClassGroup(
                model.getAddressBook().getClassGroupList(), this.newName);
        if (potentialDuplicate != null && !potentialDuplicate.getId().equals(classGroupToEdit.getId())) {
            throw new CommandException(EditClassGroupCommand.MESSAGE_DUPLICATE_CLASS_GROUP);
        }

        ClassGroup editedClassGroup = new ClassGroup(classGroupToEdit.getId(), this.newName);
        editedClassGroup.setContactIdSet(classGroupToEdit.getContactIdSet());

        model.setClassGroup(classGroupToEdit, editedClassGroup);
        return new CommandResult(
                String.format(EditClassGroupCommand.MESSAGE_EDIT_CLASS_GROUP_SUCCESS,
                        Messages.format(editedClassGroup)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof EditClassGroupCommand)) {
            return false;
        }
        EditClassGroupCommand otherCmd = (EditClassGroupCommand) other;
        return this.index.equals(otherCmd.index) && this.newName.equals(otherCmd.newName);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", this.index)
                .add("newName", this.newName)
                .toString();
    }
}
