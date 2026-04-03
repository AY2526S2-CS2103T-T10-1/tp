package cpp.logic.commands.classgroup;

import java.util.Objects;

import cpp.commons.util.ToStringBuilder;
import cpp.logic.Messages;
import cpp.logic.commands.CommandResult;
import cpp.logic.commands.DeleteCommand;
import cpp.logic.commands.exceptions.CommandException;
import cpp.model.Model;
import cpp.model.classgroup.ClassGroup;
import cpp.model.classgroup.ClassGroupName;
import cpp.model.util.ClassGroupUtil;

/**
 * Deletes a class group identified using its name from the address book.
 */
public class DeleteClassGroupCommand extends DeleteCommand {

    public static final String COMMAND_WORD_ALIAS = "deletec";
    public static final String MESSAGE_DELETE_CLASS_GROUP_SUCCESS = "Deleted Class Group: %1$s";

    private final ClassGroupName targetName;

    public DeleteClassGroupCommand(ClassGroupName targetName) {
        this.targetName = targetName;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        Objects.requireNonNull(model);

        ClassGroup classGroupToDelete = ClassGroupUtil.findClassGroup(
                model.getAddressBook().getClassGroupList(), this.targetName);
        if (classGroupToDelete == null) {
            throw new CommandException(Messages.MESSAGE_CLASS_GROUP_NOT_FOUND);
        }

        model.deleteClassGroup(classGroupToDelete);
        return new CommandResult(
                String.format(DeleteClassGroupCommand.MESSAGE_DELETE_CLASS_GROUP_SUCCESS,
                        Messages.format(classGroupToDelete)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof DeleteClassGroupCommand)) {
            return false;
        }

        DeleteClassGroupCommand otherDeleteClassGroupCommand = (DeleteClassGroupCommand) other;
        return this.targetName.equals(otherDeleteClassGroupCommand.targetName);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetName", this.targetName)
                .toString();
    }
}
