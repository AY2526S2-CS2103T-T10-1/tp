package cpp.storage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import cpp.commons.exceptions.IllegalValueException;
import cpp.model.person.Address;
import cpp.model.person.Email;
import cpp.model.person.Name;
import cpp.model.person.Person;
import cpp.model.person.Phone;
import cpp.model.tag.Tag;

/**
 * Jackson-friendly version of {@link Person}.
 */
class JsonAdaptedPerson {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Person's %s field is missing!";

    private final String id;
    private final String name;
    private final String phone;
    private final String email;
    private final String address;
    private final List<JsonAdaptedTag> tags = new ArrayList<>();

    /**
     * Constructs a {@code JsonAdaptedPerson} with the given person details.
     */
    @JsonCreator
    public JsonAdaptedPerson(@JsonProperty("id") String id, @JsonProperty("name") String name,
            @JsonProperty("phone") String phone,
            @JsonProperty("email") String email, @JsonProperty("address") String address,
            @JsonProperty("tags") List<JsonAdaptedTag> tags) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        if (tags != null) {
            this.tags.addAll(tags);
        }
    }

    /**
     * Converts a given {@code Person} into this class for Jackson use.
     */
    public JsonAdaptedPerson(Person source) {
        this.id = source.getId();
        this.name = source.getName().fullName;
        this.phone = source.getPhone().value;
        this.email = source.getEmail().value;
        this.address = source.getAddress().value;
        this.tags.addAll(source.getTags().stream()
                .map(JsonAdaptedTag::new)
                .collect(Collectors.toList()));
    }

    /**
     * Converts this Jackson-friendly adapted person object into the model's
     * {@code Person} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in
     *                               the adapted person.
     */
    public Person toModelType() throws IllegalValueException {
        final List<Tag> personTags = new ArrayList<>();
        for (JsonAdaptedTag tag : this.tags) {
            personTags.add(tag.toModelType());
        }

        if (this.name == null) {
            throw new IllegalValueException(
                    String.format(JsonAdaptedPerson.MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName()));
        }
        if (!Name.isValidName(this.name)) {
            throw new IllegalValueException(Name.MESSAGE_CONSTRAINTS);
        }
        final Name modelName = new Name(this.name);

        if (this.phone == null) {
            throw new IllegalValueException(
                    String.format(JsonAdaptedPerson.MISSING_FIELD_MESSAGE_FORMAT, Phone.class.getSimpleName()));
        }
        if (!Phone.isValidPhone(this.phone)) {
            throw new IllegalValueException(Phone.MESSAGE_CONSTRAINTS);
        }
        final Phone modelPhone = new Phone(this.phone);

        if (this.email == null) {
            throw new IllegalValueException(
                    String.format(JsonAdaptedPerson.MISSING_FIELD_MESSAGE_FORMAT, Email.class.getSimpleName()));
        }
        if (!Email.isValidEmail(this.email)) {
            throw new IllegalValueException(Email.MESSAGE_CONSTRAINTS);
        }
        final Email modelEmail = new Email(this.email);

        if (this.address == null) {
            throw new IllegalValueException(
                    String.format(JsonAdaptedPerson.MISSING_FIELD_MESSAGE_FORMAT, Address.class.getSimpleName()));
        }
        if (!Address.isValidAddress(this.address)) {
            throw new IllegalValueException(Address.MESSAGE_CONSTRAINTS);
        }
        final Address modelAddress = new Address(this.address);

        final Set<Tag> modelTags = new HashSet<>(personTags);
        return new Person(this.id, modelName, modelPhone, modelEmail, modelAddress, modelTags);
    }

}
