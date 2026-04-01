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
import cpp.model.classgroup.ClassGroup;
import cpp.model.classgroup.ClassGroupName;
import cpp.model.contact.Contact;
import cpp.model.util.AssignmentUtil;
import cpp.model.util.ClassGroupUtil;

/**
 * Adds a contact to the address book.
 */
public class AddContactCommand extends Command {

    public static final String COMMAND_WORD = "addcontact";
    public static final String COMMAND_WORD_ALIAS = "addct";

    public static final String MESSAGE_USAGE = AddContactCommand.COMMAND_WORD
            + ": Adds a contact to the address book.\n"
            + "Alias: " + AddContactCommand.COMMAND_WORD_ALIAS + "\n"
            + "Parameters: "
            + CliSyntax.PREFIX_NAME + "NAME "
            + CliSyntax.PREFIX_PHONE + "PHONE_NUMBER "
            + CliSyntax.PREFIX_EMAIL + "EMAIL "
            + CliSyntax.PREFIX_ADDRESS + "ADDRESS "
            + "[" + CliSyntax.PREFIX_CLASS + "CLASS_NAME] "
            + "[" + CliSyntax.PREFIX_ASSIGNMENT + "ASSIGNMENT_NAME] "
            + "[" + CliSyntax.PREFIX_TAG + "TAG]...\n"
            + "Example: " + AddContactCommand.COMMAND_WORD + " "
            + CliSyntax.PREFIX_NAME + "John Doe "
            + CliSyntax.PREFIX_PHONE + "98765432 "
            + CliSyntax.PREFIX_EMAIL + "johnd@example.com "
            + CliSyntax.PREFIX_ADDRESS + "311, Clementi Ave 2, #02-25 "
            + CliSyntax.PREFIX_CLASS + "CS2103T10 "
            + CliSyntax.PREFIX_ASSIGNMENT + "Assignment 1 "
            + CliSyntax.PREFIX_TAG + "friends "
            + CliSyntax.PREFIX_TAG + "owesMoney";

    public static final String MESSAGE_SUCCESS = "New contact added: %1$s%2$s%3$s";
    public static final String MESSAGE_SUCCESS_CLASS_GROUP_ALLOCATION = "\nAllocated class group: %1$s";
    public static final String MESSAGE_SUCCESS_ASSIGNMENT_ALLOCATION = "\nAllocated assignment: %1$s";
    public static final String MESSAGE_DUPLICATE_CONTACT = "This contact already exists in the address book";

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
        // Even though they are Optional parameters, we can't pass in invalid values (to
        // use for future comparison) since they will throw errors in the parser

        ClassGroup classGroupToAllocate = null;

        if (this.classGroupName != null) {
            List<ClassGroup> classGroupList = model.getAddressBook().getClassGroupList();
            classGroupToAllocate = ClassGroupUtil.findClassGroup(classGroupList, this.classGroupName);

            if (classGroupToAllocate == null) {
                throw new CommandException(Messages.MESSAGE_CLASS_GROUP_NOT_FOUND);
            }
        }

        Assignment assignmentToAllocate = null;

        if (this.assignmentName != null) {
            List<Assignment> assignmentList = model.getAddressBook().getAssignmentList();
            assignmentToAllocate = AssignmentUtil.findAssignment(assignmentList, this.assignmentName);

            if (assignmentToAllocate == null) {
                throw new CommandException(Messages.MESSAGE_ASSIGNMENT_NOT_FOUND);
            }
        }

        // Passed validity checks, so can proceed with allocation and addition

        String allocatedAssignmentString = "";
        if (assignmentToAllocate != null) {
            ContactAssignment ca = new ContactAssignment(assignmentToAllocate.getId(), this.toAdd.getId());
            model.addContactAssignment(ca);
            allocatedAssignmentString = String.format(AddContactCommand.MESSAGE_SUCCESS_ASSIGNMENT_ALLOCATION,
                    Messages.format(assignmentToAllocate));
        }

        String allocatedClassGroupString = "";
        if (classGroupToAllocate != null) {
            classGroupToAllocate.allocateContact(this.toAdd.getId());
            allocatedClassGroupString = String.format(AddContactCommand.MESSAGE_SUCCESS_CLASS_GROUP_ALLOCATION,
                    Messages.format(classGroupToAllocate));
        }

        model.addContact(this.toAdd);

        return new CommandResult(String.format(AddContactCommand.MESSAGE_SUCCESS, Messages.format(this.toAdd),
                allocatedClassGroupString, allocatedAssignmentString));
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
        return this.toAdd.equals(otherAddContactCommand.toAdd)
                && Objects.equals(this.classGroupName, otherAddContactCommand.classGroupName)
                && Objects.equals(this.assignmentName, otherAddContactCommand.assignmentName);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("toAdd", this.toAdd)
                .add("classGroupName", this.classGroupName)
                .add("assignmentName", this.assignmentName)
                .toString();
    }
}
