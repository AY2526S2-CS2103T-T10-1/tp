package cpp.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import cpp.commons.exceptions.IllegalValueException;
import cpp.model.AddressBook;
import cpp.model.ReadOnlyAddressBook;
import cpp.model.assignment.Assignment;
import cpp.model.assignment.ContactAssignment;
import cpp.model.classgroup.ClassGroup;
import cpp.model.contact.Contact;

/**
 * An Immutable AddressBook that is serializable to JSON format.
 */
@JsonRootName(value = "addressbook")
class JsonSerializableAddressBook {

    public static final String MESSAGE_DUPLICATE_CONTACT = "Contacts list contains duplicate contact(s).";
    public static final String MESSAGE_DUPLICATE_ASSIGNMENT = "Assignments list contains duplicate assignment(s).";
    public static final String MESSAGE_DUPLICATE_CONTACT_ASSIGNMENT = """
            Contact assignments list contains duplicate contact assignment(s).
            """;
    public static final String MESSAGE_DUPLICATE_CLASS_GROUP = "Class groups list contains duplicate class group(s).";

    private final List<JsonAdaptedContact> contacts = new ArrayList<>();
    private final List<JsonAdaptedAssignment> assignments = new ArrayList<>();
    private final List<JsonAdaptedContactAssignment> contactAssignments = new ArrayList<>();
    private final List<JsonAdaptedClassGroup> classGroups = new ArrayList<>();

    /**
     * Constructs a {@code JsonSerializableAddressBook} with the given contacts and
     * assignments.
     */
    @JsonCreator
    public JsonSerializableAddressBook(@JsonProperty("contacts") List<JsonAdaptedContact> contacts,
            @JsonProperty("assignments") List<JsonAdaptedAssignment> assignments,
            @JsonProperty("contactAssignments") List<JsonAdaptedContactAssignment> contactAssignments,
            @JsonProperty("classGroups") List<JsonAdaptedClassGroup> classGroups) {
        if (contacts != null) {
            this.contacts.addAll(contacts);
        }
        if (assignments != null) {
            this.assignments.addAll(assignments);
        }
        if (contactAssignments != null) {
            this.contactAssignments.addAll(contactAssignments);
        }

        if (classGroups != null) {
            this.classGroups.addAll(classGroups);
        }
    }

    /**
     * Converts a given {@code ReadOnlyAddressBook} into this class for Jackson use.
     *
     * @param source future changes to this will not affect the created
     *               {@code JsonSerializableAddressBook}.
     */
    public JsonSerializableAddressBook(ReadOnlyAddressBook source) {
        this.contacts.addAll(source.getContactList().stream().map(JsonAdaptedContact::new)
                .collect(Collectors.toList()));
        this.assignments.addAll(source.getAssignmentList().stream().map(JsonAdaptedAssignment::new)
                .collect(Collectors.toList()));
        this.contactAssignments.addAll(source.getContactAssignmentList().stream()
                .map(JsonAdaptedContactAssignment::new).collect(Collectors.toList()));
        this.classGroups.addAll(source.getClassGroupList().stream().map(JsonAdaptedClassGroup::new)
                .collect(Collectors.toList()));
    }

    /**
     * Converts this address book into the model's {@code AddressBook} object.
     *
     * @throws IllegalValueException if there were any data constraints violated.
     */
    public AddressBook toModelType() throws IllegalValueException {
        AddressBook addressBook = new AddressBook();
        for (JsonAdaptedContact jsonAdaptedContact : this.contacts) {
            Contact contact = jsonAdaptedContact.toModelType();
            if (addressBook.hasContact(contact)) {
                throw new IllegalValueException(JsonSerializableAddressBook.MESSAGE_DUPLICATE_CONTACT);
            }
            addressBook.addContact(contact);
        }

        for (JsonAdaptedAssignment jsonAdaptedAssignment : this.assignments) {
            Assignment assignment = jsonAdaptedAssignment.toModelType();
            if (addressBook.hasAssignment(assignment)) {
                throw new IllegalValueException(JsonSerializableAddressBook.MESSAGE_DUPLICATE_ASSIGNMENT);
            }
            addressBook.addAssignment(assignment);
        }

        for (JsonAdaptedContactAssignment jsonAdaptedContactAssignment : this.contactAssignments) {
            ContactAssignment contactAssignment = jsonAdaptedContactAssignment.toModelType();
            if (addressBook.hasContactAssignment(contactAssignment)) {
                throw new IllegalValueException(JsonSerializableAddressBook.MESSAGE_DUPLICATE_CONTACT_ASSIGNMENT);
            }
            addressBook.addContactAssignment(contactAssignment);
        }

        for (JsonAdaptedClassGroup jsonAdaptedClassGroup : this.classGroups) {
            ClassGroup classGroup = jsonAdaptedClassGroup.toModelType();
            if (addressBook.hasClassGroup(classGroup)) {
                throw new IllegalValueException(JsonSerializableAddressBook.MESSAGE_DUPLICATE_CLASS_GROUP);
            }
            addressBook.addClassGroup(classGroup);
        }

        return addressBook;
    }

}
