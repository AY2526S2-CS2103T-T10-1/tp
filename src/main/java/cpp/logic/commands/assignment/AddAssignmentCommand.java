package cpp.logic.commands.assignment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cpp.commons.core.index.Index;
import cpp.commons.util.ToStringBuilder;
import cpp.logic.Messages;
import cpp.logic.commands.Command;
import cpp.logic.commands.CommandResult;
import cpp.logic.commands.CommandUtil;
import cpp.logic.commands.exceptions.CommandException;
import cpp.logic.parser.CliSyntax;
import cpp.model.Model;
import cpp.model.assignment.Assignment;
import cpp.model.assignment.ContactAssignment;
import cpp.model.assignment.exceptions.ContactAlreadyAllocatedAssignmentException;
import cpp.model.classgroup.ClassGroup;
import cpp.model.classgroup.ClassGroupName;
import cpp.model.contact.Contact;
import cpp.model.util.ClassGroupUtil;

/**
 * Adds an assignment to the assignment list.
 */
public class AddAssignmentCommand extends Command {

    public static final String COMMAND_WORD = "addass";

    public static final String MESSAGE_USAGE = AddAssignmentCommand.COMMAND_WORD
            + ": Adds an assignment to the assignment list.\n"
            + "Parameters: "
            + CliSyntax.PREFIX_ASSIGNMENT + "ASSIGNMENT_NAME "
            + CliSyntax.PREFIX_DATETIME + "DEADLINE "
            + "[" + CliSyntax.PREFIX_CLASS + "CLASS_NAME] "
            + "[" + CliSyntax.PREFIX_CONTACT + "CONTACT_INDICES...]\n"
            + "Example: " + AddAssignmentCommand.COMMAND_WORD + " "
            + CliSyntax.PREFIX_ASSIGNMENT + "Assignment 1 "
            + CliSyntax.PREFIX_DATETIME + "21-02-2026 23:59 "
            + CliSyntax.PREFIX_CLASS + "CS2103T10 "
            + CliSyntax.PREFIX_CONTACT + "1 2 3";

    public static final String MESSAGE_SUCCESS = "New assignment added: %1$s";
    public static final String MESSAGE_SUCCESS_WITH_ALLOCATION = """
            New assignment added: %1$s
            Allocated assignment to %2$s contact(s).
            Contacts allocated: %3$s""";
    public static final String MESSAGE_DUPLICATE_ASSIGNMENT = "This assignment already exists in the assignment list";

    private final Assignment toAdd;
    private final List<Index> contactIndices;
    private final ClassGroupName classGroupName;
    private int allocatedCount;
    private StringBuilder allocatedContacts;

    /**
     * Creates an AddAssignmentCommand to add the specified {@code Assignment} and
     * assign it to the specified contacts.
     */
    public AddAssignmentCommand(Assignment assignment, List<Index> contactIndices) {
        Objects.requireNonNull(assignment);
        Objects.requireNonNull(contactIndices);
        this.toAdd = assignment;
        this.contactIndices = new ArrayList<>(contactIndices);
        this.classGroupName = null;
        this.allocatedCount = 0;
        this.allocatedContacts = new StringBuilder();
    }

    /**
     * Creates an AddAssignmentCommand to add the specified {@code Assignment} and
     * assign it to the specified contacts and class group.
     */
    public AddAssignmentCommand(Assignment assignment, List<Index> contactIndices, ClassGroupName classGroupName) {
        Objects.requireNonNull(assignment);
        Objects.requireNonNull(contactIndices);
        Objects.requireNonNull(classGroupName);
        this.toAdd = assignment;
        this.contactIndices = new ArrayList<>(contactIndices);
        this.classGroupName = classGroupName;
        this.allocatedCount = 0;
        this.allocatedContacts = new StringBuilder();
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        Objects.requireNonNull(model);

        if (model.hasAssignment(this.toAdd)) {
            throw new CommandException(AddAssignmentCommand.MESSAGE_DUPLICATE_ASSIGNMENT);
        }

        List<Contact> lastShownContactList = model.getFilteredContactList();

        CommandUtil.checkContactIndices(lastShownContactList, this.contactIndices);

        ClassGroup classGroupToAllocate = ClassGroupUtil.findClassGroup(model.getAddressBook().getClassGroupList(),
                this.classGroupName);
        if (this.classGroupName != null && classGroupToAllocate == null) {
            throw new CommandException(Messages.MESSAGE_CLASS_GROUP_NOT_FOUND);
        }

        if (classGroupToAllocate != null && classGroupToAllocate.getContactIdSet().isEmpty()) {
            throw new CommandException(Messages.MESSAGE_CLASS_GROUP_NO_CONTACTS);
        }

        this.allocateToContactsByContactIndices(model, this.toAdd, lastShownContactList);
        if (classGroupToAllocate != null) {
            this.allocateToContactsByClassGroup(model, this.toAdd, classGroupToAllocate);
        }

        model.addAssignment(this.toAdd);

        if (this.classGroupName == null && this.contactIndices.isEmpty()) {
            return new CommandResult(String.format(AddAssignmentCommand.MESSAGE_SUCCESS, Messages.format(this.toAdd)));
        } else {
            return new CommandResult(String.format(
                    AddAssignmentCommand.MESSAGE_SUCCESS_WITH_ALLOCATION,
                    Messages.format(this.toAdd), this.allocatedCount, this.allocatedContacts.toString()));
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof AddAssignmentCommand)) {
            return false;
        }
        AddAssignmentCommand o = (AddAssignmentCommand) other;
        return this.toAdd.getName().equals(o.toAdd.getName())
                && this.toAdd.getDeadline().equals(o.toAdd.getDeadline())
                && this.contactIndices.equals(o.contactIndices)
                && Objects.equals(this.classGroupName, o.classGroupName);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("toAddAssignment", this.toAdd)
                .add("contactIndices", this.contactIndices)
                .add("classGroupName", this.classGroupName)
                .toString();
    }

    private void allocateToContactsByContactIndices(Model model, Assignment assignmentToAllocate,
            List<Contact> lastShownContactList) {

        for (Index idx : this.contactIndices) {
            Contact contact = lastShownContactList.get(idx.getZeroBased());

            this.allocateToContact(model, assignmentToAllocate, contact);
        }
    }

    private void allocateToContactsByClassGroup(Model model, Assignment assignmentToAllocate,
            ClassGroup classGroupToAllocate) {
        List<Contact> contactList = model.getAddressBook().getContactList();

        for (String contactId : classGroupToAllocate.getContactIdSet()) {
            Contact contact = contactList.stream()
                    .filter(c -> c.getId().equals(contactId))
                    .findAny()
                    .orElse(null);

            if (contact != null) {
                this.allocateToContact(model, assignmentToAllocate, contact);
            }
        }
    }

    private void allocateToContact(Model model, Assignment assignment, Contact contact) {
        ContactAssignment ca = new ContactAssignment(assignment.getId(), contact.getId());

        try {
            model.addContactAssignment(ca);

            if (this.allocatedContacts.length() > 0) {
                this.allocatedContacts.append("; ");
            }
            this.allocatedContacts.append(contact.getName().fullName);
            this.allocatedCount++;

        } catch (ContactAlreadyAllocatedAssignmentException e) {
            // Skip contacts that already have the assignment allocated.
            // This should only happen when allocating by both class group and contact
            // indices, and the same contact is allocated via both methods. In this case, we
            // can simply skip the duplicate allocation and continue allocating to the rest
            // of the contacts.
        }
    }
}
