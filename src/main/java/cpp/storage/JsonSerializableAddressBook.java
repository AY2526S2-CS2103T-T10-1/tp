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
import cpp.model.person.Person;

/**
 * An Immutable AddressBook that is serializable to JSON format.
 */
@JsonRootName(value = "addressbook")
class JsonSerializableAddressBook {

    public static final String MESSAGE_DUPLICATE_PERSON = "Persons list contains duplicate person(s).";
    public static final String MESSAGE_DUPLICATE_ASSIGNMENT = "Assignments list contains duplicate assignment(s).";

    private final List<JsonAdaptedPerson> persons = new ArrayList<>();
    private final List<JsonAdaptedAssignment> assignments = new ArrayList<>();

    /**
     * Constructs a {@code JsonSerializableAddressBook} with the given persons and
     * assignments.
     */
    @JsonCreator
    public JsonSerializableAddressBook(@JsonProperty("persons") List<JsonAdaptedPerson> persons,
            @JsonProperty("assignments") List<JsonAdaptedAssignment> assignments) {
        if (persons != null) {
            this.persons.addAll(persons);
        }
        if (assignments != null) {
            this.assignments.addAll(assignments);
        }
    }

    /**
     * Converts a given {@code ReadOnlyAddressBook} into this class for Jackson use.
     *
     * @param source future changes to this will not affect the created
     *               {@code JsonSerializableAddressBook}.
     */
    public JsonSerializableAddressBook(ReadOnlyAddressBook source) {
        this.persons.addAll(source.getPersonList().stream().map(JsonAdaptedPerson::new).collect(Collectors.toList()));
        this.assignments.addAll(source.getAssignmentList().stream().map(JsonAdaptedAssignment::new)
                .collect(Collectors.toList()));
    }

    /**
     * Converts this address book into the model's {@code AddressBook} object.
     *
     * @throws IllegalValueException if there were any data constraints violated.
     */
    public AddressBook toModelType() throws IllegalValueException {
        AddressBook addressBook = new AddressBook();
        for (JsonAdaptedPerson jsonAdaptedPerson : this.persons) {
            Person person = jsonAdaptedPerson.toModelType();
            if (addressBook.hasPerson(person)) {
                throw new IllegalValueException(JsonSerializableAddressBook.MESSAGE_DUPLICATE_PERSON);
            }
            addressBook.addPerson(person);
        }

        for (JsonAdaptedAssignment jsonAdaptedAssignment : this.assignments) {
            Assignment assignment = jsonAdaptedAssignment.toModelType();
            if (addressBook.hasAssignment(assignment)) {
                throw new IllegalValueException(JsonSerializableAddressBook.MESSAGE_DUPLICATE_ASSIGNMENT);
            }
            addressBook.addAssignment(assignment);
        }

        return addressBook;
    }

}
