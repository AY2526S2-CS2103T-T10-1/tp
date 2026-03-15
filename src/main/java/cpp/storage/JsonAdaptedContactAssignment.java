package cpp.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import cpp.commons.exceptions.IllegalValueException;
import cpp.model.AddressBook;
import cpp.model.assignment.ContactAssignment;

/**
 * Jackson-friendly version of {@link ContactAssignment}.
 */
class JsonAdaptedContactAssignment {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "ContactAssignment's %s field is missing!";
    public static final String INVALID_ASSIGNMENT_ID_MESSAGE = "Assignment with id %s does not exist in the address book";
    public static final String INVALID_CONTACT_ID_MESSAGE = "Contact with id %s does not exist in the address book";

    private final String assignmentId;
    private final String contactId;
    private final Boolean isSubmitted;
    private final Boolean isGraded;
    private final Integer score;

    /**
     * Constructs a {@code JsonAdaptedContactAssignment} with the given details.
     */
    @JsonCreator
    public JsonAdaptedContactAssignment(@JsonProperty("assignmentId") String assignmentId,
            @JsonProperty("contactId") String contactId, @JsonProperty("isSubmitted") Boolean isSubmitted,
            @JsonProperty("isGraded") Boolean isGraded, @JsonProperty("score") Integer score) {
        this.assignmentId = assignmentId;
        this.contactId = contactId;
        this.isSubmitted = isSubmitted;
        this.isGraded = isGraded;
        this.score = score;
    }

    /**
     * Converts a given {@code ContactAssignment} into this class for Jackson use.
     */
    public JsonAdaptedContactAssignment(ContactAssignment source) {
        this.assignmentId = source.getAssignmentId();
        this.contactId = source.getContactId();
        this.isSubmitted = source.isSubmitted();
        this.isGraded = source.isGraded();
        this.score = source.getScore();
    }

    /**
     * Converts this Jackson-friendly adapted contact assignment object into the
     * model's {@code ContactAssignment} object.
     *
     * @throws IllegalValueException if there were any data constraints violated
     *                               in the adapted contact assignment.
     */
    public ContactAssignment toModelType(AddressBook addressBook) throws IllegalValueException {
        if (this.assignmentId == null) {
            throw new IllegalValueException(String.format(JsonAdaptedContactAssignment.MISSING_FIELD_MESSAGE_FORMAT,
                    "assignmentId"));
        }
        if (!addressBook.hasAssignmentId(this.assignmentId)) {
            throw new IllegalValueException(
                    String.format(JsonAdaptedContactAssignment.INVALID_ASSIGNMENT_ID_MESSAGE, this.assignmentId));
        }
        if (this.contactId == null) {
            throw new IllegalValueException(String.format(JsonAdaptedContactAssignment.MISSING_FIELD_MESSAGE_FORMAT,
                    "contactId"));
        }
        if (!addressBook.hasContactId(this.contactId)) {
            throw new IllegalValueException(
                    String.format(JsonAdaptedContactAssignment.INVALID_CONTACT_ID_MESSAGE, this.contactId));
        }
        if (this.isSubmitted == null) {
            throw new IllegalValueException(String.format(JsonAdaptedContactAssignment.MISSING_FIELD_MESSAGE_FORMAT,
                    "isSubmitted"));
        }
        if (this.isGraded == null) {
            throw new IllegalValueException(String.format(JsonAdaptedContactAssignment.MISSING_FIELD_MESSAGE_FORMAT,
                    "isGraded"));
        }
        if (this.score == null) {
            throw new IllegalValueException(String.format(JsonAdaptedContactAssignment.MISSING_FIELD_MESSAGE_FORMAT,
                    "score"));
        }
        if (this.score < 0) {
            throw new IllegalValueException("Score cannot be negative");
        }

        return new ContactAssignment(this.assignmentId, this.contactId, this.isSubmitted, this.isGraded,
                this.score);
    }

}
