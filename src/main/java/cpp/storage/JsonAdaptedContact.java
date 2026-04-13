package cpp.storage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import cpp.commons.exceptions.IllegalValueException;
import cpp.logic.parser.ParserUtil;
import cpp.model.contact.Address;
import cpp.model.contact.Contact;
import cpp.model.contact.ContactName;
import cpp.model.contact.Email;
import cpp.model.contact.Phone;
import cpp.model.tag.Tag;

/**
 * Jackson-friendly version of {@link Contact}.
 */
class JsonAdaptedContact {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Contact's %s field is missing!";

    private final String id;
    private final String name;
    private final String phone;
    private final String email;
    private final String address;
    private final List<JsonAdaptedTag> tags = new ArrayList<>();

    /**
     * Constructs a {@code JsonAdaptedContact} with the given contact details.
     */
    @JsonCreator
    public JsonAdaptedContact(@JsonProperty("id") String id, @JsonProperty("name") String name,
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
     * Converts a given {@code Contact} into this class for Jackson use.
     */
    public JsonAdaptedContact(Contact source) {
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
     * Converts this Jackson-friendly adapted contact object into the model's
     * {@code Contact} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in
     *                               the adapted contact.
     */
    public Contact toModelType() throws IllegalValueException {
        final List<Tag> contactTags = new ArrayList<>();
        for (JsonAdaptedTag tag : this.tags) {
            contactTags.add(tag.toModelType());
        }

        if (this.name == null) {
            throw new IllegalValueException(
                    String.format(JsonAdaptedContact.MISSING_FIELD_MESSAGE_FORMAT, ContactName.class.getSimpleName()));
        }
        if (!ContactName.isValidName(this.name)) {
            throw new IllegalValueException(ContactName.MESSAGE_CONSTRAINTS);
        }
        final ContactName modelName = ParserUtil.parseName(this.name);

        if (this.phone == null) {
            throw new IllegalValueException(
                    String.format(JsonAdaptedContact.MISSING_FIELD_MESSAGE_FORMAT, Phone.class.getSimpleName()));
        }
        if (!Phone.isValidPhone(this.phone)) {
            throw new IllegalValueException(Phone.MESSAGE_CONSTRAINTS);
        }
        final Phone modelPhone = ParserUtil.parsePhone(this.phone);

        if (this.email == null) {
            throw new IllegalValueException(
                    String.format(JsonAdaptedContact.MISSING_FIELD_MESSAGE_FORMAT, Email.class.getSimpleName()));
        }
        if (!Email.isValidEmail(this.email)) {
            throw new IllegalValueException(Email.MESSAGE_CONSTRAINTS);
        }
        final Email modelEmail = ParserUtil.parseEmail(this.email);

        if (this.address == null) {
            throw new IllegalValueException(
                    String.format(JsonAdaptedContact.MISSING_FIELD_MESSAGE_FORMAT, Address.class.getSimpleName()));
        }
        if (!Address.isValidAddress(this.address)) {
            throw new IllegalValueException(Address.MESSAGE_CONSTRAINTS);
        }
        final Address modelAddress = ParserUtil.parseAddress(this.address);

        final Set<Tag> modelTags = new HashSet<>(contactTags);
        return new Contact(this.id, modelName, modelPhone, modelEmail, modelAddress, modelTags);
    }

}
