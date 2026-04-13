package cpp.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import cpp.commons.exceptions.IllegalValueException;
import cpp.logic.parser.ParserUtil;
import cpp.model.classgroup.ClassGroup;
import cpp.model.classgroup.ClassGroupName;
import cpp.model.classgroup.exceptions.ContactAlreadyAllocatedClassGroupException;

/**
 * Jackson-friendly version of {@link ClassGroup}.
 */
class JsonAdaptedClassGroup {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "ClassGroup's %s field is missing!";

    private final String id;
    private final String name;
    private final String[] contactIds;

    /**
     * Constructs a {@code JsonAdaptedClassGroup} with the given class group
     * details.
     */
    @JsonCreator
    public JsonAdaptedClassGroup(@JsonProperty("id") String id, @JsonProperty("name") String name,
            @JsonProperty("contactIds") String[] contactIds) {
        this.id = id;
        this.name = name;
        this.contactIds = contactIds;
    }

    /**
     * Converts a given {@code ClassGroup} into this class for Jackson use.
     */
    public JsonAdaptedClassGroup(ClassGroup source) {
        this.id = source.getId();
        this.name = source.getName().fullName;
        this.contactIds = source.getContactIdSet().toArray(new String[0]);
    }

    /**
     * Converts this Jackson-friendly adapted class group object into the model's
     * {@code ClassGroup} object.
     *
     * @throws IllegalValueException if there were any data constraints violated
     *                               in the adapted class group.
     */
    public ClassGroup toModelType() throws IllegalValueException {
        if (this.id == null) {
            throw new IllegalValueException("A class group's id field is missing.");
        }
        if (this.name == null) {
            throw new IllegalValueException(String.format(JsonAdaptedClassGroup.MISSING_FIELD_MESSAGE_FORMAT,
                    ClassGroupName.class.getSimpleName()));
        }
        if (!ClassGroupName.isValidName(this.name)) {
            throw new IllegalValueException(ClassGroupName.MESSAGE_CONSTRAINTS);
        }

        if (this.contactIds == null) {
            throw new IllegalValueException("A class group's contactIds field is missing.");
        }

        final ClassGroupName modelName = ParserUtil.parseClassGroupName(this.name);

        ClassGroup classGroup = new ClassGroup(this.id, modelName);
        for (String contactId : this.contactIds) {
            try {
                classGroup.allocateContact(contactId);
            } catch (ContactAlreadyAllocatedClassGroupException e) {
                throw new IllegalValueException("Duplicate contactId found during allocation.");
            }
        }
        return classGroup;
    }

}
