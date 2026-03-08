package cpp.logic.commands.classgroup;

import java.util.Objects;

import cpp.commons.util.ToStringBuilder;
import cpp.logic.commands.AddCommand;
import cpp.logic.commands.Command;
import cpp.logic.commands.CommandResult;
import cpp.logic.parser.CliSyntax;
import cpp.model.Model;
import cpp.model.classgroup.ClassGroup;

/**
 * Adds a new class group to the class grouping list.
 */
public class AddClassGroupCommand extends Command {

    public static final String COMMAND_WORD = "addclass";

    public static final String MESSAGE_USAGE = AddCommand.COMMAND_WORD + ": Defines a new class grouping. "
            + "Parameters: "
            + CliSyntax.PREFIX_NAME + "NAME "
            + "[" + CliSyntax.PREFIX_TAG + "TAG]...\n"
            + "Example: " + AddClassGroupCommand.COMMAND_WORD + " "
            + CliSyntax.PREFIX_NAME + "CS2103T10 ";

    public static final String MESSAGE_SUCCESS = "New class grouping added: %1$s";
    public static final String MESSAGE_DUPLICATE_CLASS_GROUP = "This class grouping already exists in the address book";

    private final ClassGroup toAdd;

    /**
     * Creates an AddClassGroupCommand with the specified class group to add.
     */
    public AddClassGroupCommand(ClassGroup classGroup) {
        Objects.requireNonNull(classGroup);
        this.toAdd = classGroup;
    }

    @Override
    public CommandResult execute(Model model) {
        Objects.requireNonNull(model);

        // TODO: Integrate with Model addClassGroup method
        return new CommandResult("Implementation in progress!");
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof AddClassGroupCommand)) {
            return false;
        }

        AddClassGroupCommand otherCommand = (AddClassGroupCommand) other;
        return otherCommand.toAdd.equals(this.toAdd);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("toAdd", this.toAdd)
                .toString();
    }
}
