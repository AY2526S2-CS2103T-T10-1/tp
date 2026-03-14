package cpp.logic.commands;

import java.util.List;
import java.util.Objects;

import cpp.commons.util.ToStringBuilder;
import cpp.logic.Messages;
import cpp.logic.commands.exceptions.CommandException;
import cpp.logic.parser.CliSyntax;
import cpp.model.Model;
import cpp.model.assignment.Assignment;
import cpp.model.assignment.AssignmentName;
import cpp.model.assignment.ContactAssignment;
// import cpp.model.classgroup.ClassGroup;
// import cpp.model.classgroup.ClassGroupName;
import cpp.model.contact.Contact;
import cpp.model.util.AssignmentUtil;

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
    // private final ClassGroupName classGroupName;
    private final AssignmentName assignmentName;

    public AddContactCommand(Contact contact) {
        this(contact, null);
    }

    /**
     * Creates an AddContactCommand to add the specified {@code Contact}
     */
    public AddContactCommand(Contact contact, AssignmentName assignmentName) {
        Objects.requireNonNull(contact);
        this.toAdd = contact;
        this.assignmentName = assignmentName;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        Objects.requireNonNull(model);

        if (model.hasContact(this.toAdd)) {
            throw new CommandException(AddContactCommand.MESSAGE_DUPLICATE_CONTACT);
        }

        // TODO: add classGroup allocation

        Assignment assignmentToAllocate = null;

        if (this.assignmentName != null) {
            List<Assignment> assignmentList = model.getAddressBook().getAssignmentList();
            assignmentToAllocate = AssignmentUtil.findAssignment(assignmentList, this.assignmentName);

            if (assignmentToAllocate == null) {
                throw new CommandException(AddContactCommand.MESSAGE_INVALID_ASSIGNMENT_NAME);
            }
        }

        // TODO: add classGroup validity check

        // Code under here only if everything is valid

        if (assignmentToAllocate != null) {
            ContactAssignment ca = new ContactAssignment(assignmentToAllocate.getId(), this.toAdd.getId());
            model.addContactAssignment(ca);
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
