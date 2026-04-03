package cpp.logic.commands.view;

import java.util.List;
import java.util.Objects;

import cpp.commons.util.ToStringBuilder;
import cpp.logic.Messages;
import cpp.logic.commands.CommandResult;
import cpp.logic.commands.exceptions.CommandException;
import cpp.model.Model;
import cpp.model.classgroup.ClassGroup;
import cpp.model.classgroup.ClassGroupName;
import cpp.model.util.ClassGroupUtil;

/**
 * Views a class group identified using its name as displayed in the address
 * book. Displays full detailed information including name, and contacts
 * allocated to the class group.
 */
public class ViewClassGroupCommand extends ViewCommand {

    public static final String MESSAGE_SUCCESS = "Viewing details of Class Group: %1$s";

    private final ClassGroupName classGroupName;

    public ViewClassGroupCommand(ClassGroupName classGroupName) {
        this.classGroupName = classGroupName;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        Objects.requireNonNull(model);

        List<ClassGroup> classGroupList = model.getAddressBook().getClassGroupList();
        ClassGroup classGroupToView = ClassGroupUtil.findClassGroup(classGroupList, this.classGroupName);

        if (classGroupToView == null) {
            throw new CommandException(Messages.MESSAGE_CLASS_GROUP_NOT_FOUND);
        }

        model.viewClassGroup(classGroupToView);

        return new CommandResult(
                String.format(ViewClassGroupCommand.MESSAGE_SUCCESS, Messages.format(classGroupToView)),
                CommandResult.ViewType.CLASSGROUP);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ViewClassGroupCommand)) {
            return false;
        }
        ViewClassGroupCommand o = (ViewClassGroupCommand) other;
        return Objects.equals(this.classGroupName, o.classGroupName);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("classGroupName", this.classGroupName)
                .toString();
    }

}
