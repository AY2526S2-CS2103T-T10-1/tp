package cpp.storage;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import cpp.commons.exceptions.IllegalValueException;
import cpp.logic.parser.ParserUtil;
import cpp.logic.parser.exceptions.ParseException;
import cpp.model.AddressBook;
import cpp.model.assignment.ContactAssignment;
import cpp.model.assignment.GradeInfo;
import cpp.model.assignment.SubmissionInfo;

/**
 * Jackson-friendly version of {@link ContactAssignment}.
 */
class JsonAdaptedContactAssignment {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "ContactAssignment's %s field is missing!";
    public static final String INVALID_ASSIGNMENT_ID_MESSAGE = """
            Assignment with id %s does not exist in the address book""";
    public static final String INVALID_CONTACT_ID_MESSAGE = "Contact with id %s does not exist in the address book";

    private final String assignmentId;
    private final String contactId;
    private final String isSubmitted;
    private final String submissionDate;
    private final String isGraded;
    private final String gradingDate;
    private final String score;

    /**
     * Constructs a {@code JsonAdaptedContactAssignment} with the given details.
     */
    @JsonCreator
    public JsonAdaptedContactAssignment(@JsonProperty("assignmentId") String assignmentId,
            @JsonProperty("contactId") String contactId, @JsonProperty("isSubmitted") String isSubmitted,
            @JsonProperty("submissionDate") String submissionDate, @JsonProperty("isGraded") String isGraded,
            @JsonProperty("gradingDate") String gradingDate, @JsonProperty("score") String score) {
        this.assignmentId = assignmentId;
        this.contactId = contactId;
        this.isSubmitted = isSubmitted;
        this.submissionDate = submissionDate;
        this.isGraded = isGraded;
        this.gradingDate = gradingDate;
        this.score = score;
    }

    /**
     * Converts a given {@code ContactAssignment} into this class for Jackson use.
     */
    public JsonAdaptedContactAssignment(ContactAssignment source) {
        this.assignmentId = source.getAssignmentId();
        this.contactId = source.getContactId();
        this.isSubmitted = String.valueOf(source.isSubmitted());
        this.submissionDate = source.getSubmissionDate() != null
                ? source.getSubmissionDate().format(ParserUtil.DATETIME_FORMATTER)
                : null;
        this.isGraded = String.valueOf(source.isGraded());
        this.gradingDate = source.getGradingDate() != null
                ? source.getGradingDate().format(ParserUtil.DATETIME_FORMATTER)
                : null;
        this.score = String.valueOf(source.getScore());
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

        final SubmissionInfo modelSubmissionInfo;
        if (this.isSubmitted == null) {
            throw new IllegalValueException(String.format(JsonAdaptedContactAssignment.MISSING_FIELD_MESSAGE_FORMAT,
                    "isSubmitted"));
        }
        final LocalDateTime modelSubmissionDate;
        try {
            if (this.submissionDate != null) {
                modelSubmissionDate = ParserUtil.parseDateTime(this.submissionDate);
            } else {
                modelSubmissionDate = null;
            }
        } catch (ParseException e) {
            throw new IllegalValueException(String.format(JsonAdaptedContactAssignment.MISSING_FIELD_MESSAGE_FORMAT,
                    "submissionDate"));
        }
        if (this.isSubmitted != null && !this.isSubmitted.toLowerCase().equals("true")
                && !this.isSubmitted.toLowerCase().equals("false")) {
            throw new IllegalValueException(String.format(JsonAdaptedContactAssignment.MISSING_FIELD_MESSAGE_FORMAT,
                    "isSubmitted"));
        }
        if (!SubmissionInfo.isValidSubmissionInfo(Boolean.parseBoolean(
                this.isSubmitted), modelSubmissionDate)) {
            throw new IllegalValueException(String.format(JsonAdaptedContactAssignment.MISSING_FIELD_MESSAGE_FORMAT,
                    "submissionDate"));
        }
        modelSubmissionInfo = new SubmissionInfo(Boolean.parseBoolean(this.isSubmitted), modelSubmissionDate);

        final GradeInfo modelGradeInfo;
        if (this.isGraded == null) {
            throw new IllegalValueException(String.format(JsonAdaptedContactAssignment.MISSING_FIELD_MESSAGE_FORMAT,
                    "isGraded"));
        }
        if (this.score == null) {
            throw new IllegalValueException(GradeInfo.INVALID_SCORE_STRING);
        }
        final LocalDateTime modelGradingDate;
        try {
            if (this.gradingDate != null) {
                modelGradingDate = ParserUtil.parseDateTime(this.gradingDate);
            } else {
                modelGradingDate = null;
            }
        } catch (ParseException e) {
            throw new IllegalValueException(String.format(JsonAdaptedContactAssignment.MISSING_FIELD_MESSAGE_FORMAT,
                    "gradingDate"));
        }
        try {
            final float parsedScore = ParserUtil.parseScore(this.score);
            if (!GradeInfo.isValidGradeInfo(Boolean.parseBoolean(this.isGraded), modelGradingDate,
                    parsedScore,
                    modelSubmissionInfo)) {
                throw new IllegalValueException(GradeInfo.INVALID_GRADE_STRING);
            }

            modelGradeInfo = GradeInfo.createFromStorage(Boolean.parseBoolean(this.isGraded), modelGradingDate,
                    parsedScore, modelSubmissionInfo);
        } catch (ParseException e) {
            throw new IllegalValueException(e.getMessage());
        }

        return new ContactAssignment(this.assignmentId, this.contactId, modelSubmissionInfo.isSubmitted(),
                modelSubmissionDate, modelGradeInfo.isGraded(), modelGradingDate, modelGradeInfo.getScore());
    }

}
