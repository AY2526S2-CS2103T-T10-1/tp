package cpp.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import cpp.commons.exceptions.IllegalValueException;
import cpp.model.classgroup.ClassGroup;
import cpp.model.classgroup.ClassGroupName;

/**
 * Jackson-friendly version of {@link ClassGroup}.
 */
class JsonAdaptedClassGroup {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "ClassGroup's %s field is missing!";

    private final String id;
    private final String name;

    /**
     * Constructs a {@code JsonAdaptedClassGroup} with the given class group
     * details.
     */
    @JsonCreator
    public JsonAdaptedClassGroup(@JsonProperty("id") String id, @JsonProperty("name") String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Converts a given {@code ClassGroup} into this class for Jackson use.
     */
    public JsonAdaptedClassGroup(ClassGroup source) {
        this.id = source.getId();
        this.name = source.getName().fullName;
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
        final ClassGroupName modelName = new ClassGroupName(this.name);

        return new ClassGroup(this.id, modelName);
    }

}
