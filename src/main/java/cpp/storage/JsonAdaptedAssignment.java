package cpp.storage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import cpp.commons.exceptions.IllegalValueException;
import cpp.model.assignment.Assignment;
import cpp.model.assignment.Name;

/**
 * Jackson-friendly version of {@link Assignment}.
 */
class JsonAdaptedAssignment {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Assignment's %s field is missing!";

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    private final String id;
    private final String name;
    private final String deadline;

    /**
     * Constructs a {@code JsonAdaptedAssignment} with the given assignment details.
     */
    @JsonCreator
    public JsonAdaptedAssignment(@JsonProperty("id") String id, @JsonProperty("name") String name,
            @JsonProperty("deadline") String deadline) {
        this.id = id;
        this.name = name;
        this.deadline = deadline;
    }

    /**
     * Converts a given {@code Assignment} into this class for Jackson use.
     */
    public JsonAdaptedAssignment(Assignment source) {
        this.id = source.getId();
        this.name = source.getName().fullName;
        this.deadline = source.getDeadline().format(JsonAdaptedAssignment.FORMATTER);
    }

    /**
     * Converts this Jackson-friendly adapted assignment object into the model's
     * {@code Assignment} object.
     *
     * @throws IllegalValueException if there were any data constraints violated
     *                               in the adapted assignment.
     */
    public Assignment toModelType() throws IllegalValueException {
        if (this.id == null) {
            throw new IllegalValueException("An assignment's id field is missing.");
        }
        if (this.name == null) {
            throw new IllegalValueException(String.format(JsonAdaptedAssignment.MISSING_FIELD_MESSAGE_FORMAT,
                    Name.class.getSimpleName()));
        }
        if (!Name.isValidName(this.name)) {
            throw new IllegalValueException(Name.MESSAGE_CONSTRAINTS);
        }
        final Name modelName = new Name(this.name);

        if (this.deadline == null) {
            throw new IllegalValueException(String.format(JsonAdaptedAssignment.MISSING_FIELD_MESSAGE_FORMAT,
                    "Deadline"));
        }
        final LocalDateTime modelDeadline;
        try {
            modelDeadline = LocalDateTime.parse(this.deadline, JsonAdaptedAssignment.FORMATTER);
        } catch (Exception e) {
            throw new IllegalValueException("Invalid date and time format. Please use the format: dd-MM-yyyy HH:mm");
        }

        return new Assignment(this.id, modelName, modelDeadline);
    }

}
