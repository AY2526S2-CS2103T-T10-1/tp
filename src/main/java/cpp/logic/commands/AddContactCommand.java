package cpp.logic.commands;

import java.util.List;
import java.util.Objects;

import cpp.commons.util.ToStringBuilder;
import cpp.logic.Messages;
import cpp.logic.commands.assignment.AllocateAssignmentCommand;
import cpp.logic.commands.classgroup.AllocateClassGroupCommand;
import cpp.logic.commands.exceptions.CommandException;
import cpp.logic.parser.CliSyntax;
import cpp.model.Model;
import cpp.model.assignment.Assignment;
import cpp.model.assignment.AssignmentName;
import cpp.model.assignment.ContactAssignment;
import cpp.model.classgroup.ClassGroup;
import cpp.model.classgroup.ClassGroupName;
// import cpp.model.classgroup.ClassGroup;
// import cpp.model.classgroup.ClassGroupName;
import cpp.model.contact.Contact;
import cpp.model.util.AssignmentUtil;
import cpp.model.util.ClassGroupUtil;

/**
 * Adds a contact to the address book.
 */
public class AddContactCommand extends Command {

    public static final String COMMAND_WORD = "addcontact";

    public static final String MESSAGE_USAGE = AddContactCommand.COMMAND_WORD + ": Adds a contact to the address book. "
            + "Parameters: "
            + CliSyntax.PREFIX_NAME + "NAME "
            + CliSyntax.PREFIX_PHONE + "PHONE "
            + CliSyntax.PREFIX_EMAIL + "EMAIL "
            + CliSyntax.PREFIX_ADDRESS + "ADDRESS "
            + "[" + CliSyntax.PREFIX_CLASS + "CLASS_NAME] "
            + "[" + CliSyntax.PREFIX_ASSIGNMENT + "ASSIGNMENT_NAME]\n"
            + "Example: " + AddContactCommand.COMMAND_WORD + " "
            + CliSyntax.PREFIX_NAME + "John Doe "
            + CliSyntax.PREFIX_PHONE + "98765432 "
            + CliSyntax.PREFIX_EMAIL + "johnd@example.com "
            + CliSyntax.PREFIX_ADDRESS + "311, Clementi Ave 2, #02-25 "
            + CliSyntax.PREFIX_TAG + "friends "
            + CliSyntax.PREFIX_TAG + "owesMoney";

    public static final String MESSAGE_SUCCESS = "New contact added: %1$s";
    public static final String MESSAGE_DUPLICATE_CONTACT = "This contact already exists in the address book";
    public static final String MESSAGE_INVALID_ASSIGNMENT_NAME = "The assignment name provided is invalid";

    private final Contact toAdd;
    private final ClassGroupName classGroupName;
    private final AssignmentName assignmentName;

    public AddContactCommand(Contact contact) {
        this(contact, null, null);
    }

    /**
     * Creates an AddContactCommand to add the specified {@code Contact}
     */
    public AddContactCommand(Contact contact, ClassGroupName classGroupName, AssignmentName assignmentName) {
        Objects.requireNonNull(contact);
        this.toAdd = contact;
        this.classGroupName = classGroupName;
        this.assignmentName = assignmentName;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        Objects.requireNonNull(model);

        if (model.hasContact(this.toAdd)) {
            throw new CommandException(AddContactCommand.MESSAGE_DUPLICATE_CONTACT);
        }

        // Have to allow assignmentName and classGroupName to be null
        // Even though they are ptional parameters, we can't pass in invalid values (to
        // use for future comparison) since they will throw errors in the parser

        ClassGroup classGroupToAllocate = null;

        if (this.classGroupName != null) {
            List<ClassGroup> classGroupList = model.getAddressBook().getClassGroupList();
            classGroupToAllocate = ClassGroupUtil.findClassGroup(classGroupList, this.classGroupName);

            if (classGroupToAllocate == null) {
                throw new CommandException(AllocateClassGroupCommand.MESSAGE_INVALID_CLASS_GROUP_NAME);
            }
        }

        Assignment assignmentToAllocate = null;

        if (this.assignmentName != null) {
            List<Assignment> assignmentList = model.getAddressBook().getAssignmentList();
            assignmentToAllocate = AssignmentUtil.findAssignment(assignmentList, this.assignmentName);

            if (assignmentToAllocate == null) {
                throw new CommandException(AllocateAssignmentCommand.MESSAGE_INVALID_ASSIGNMENT_NAME);
            }
        }

        // Passed validity checks, so can proceed with allocation and addition

        if (assignmentToAllocate != null) {
            ContactAssignment ca = new ContactAssignment(assignmentToAllocate.getId(), this.toAdd.getId());
            model.addContactAssignment(ca);
        }

        if (classGroupToAllocate != null) {
            classGroupToAllocate.allocateContact(this.toAdd.getId());
        }

        model.addContact(this.toAdd);

        return new CommandResult(String.format(AddContactCommand.MESSAGE_SUCCESS, Messages.format(this.toAdd)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddContactCommand)) {
            return false;
        }

        AddContactCommand otherAddContactCommand = (AddContactCommand) other;
        return this.toAdd.equals(otherAddContactCommand.toAdd);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("toAdd", this.toAdd)
                .toString();
    }
}
