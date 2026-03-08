package cpp.storage;

import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cpp.commons.exceptions.IllegalValueException;
import cpp.model.assignment.AssignmentName;
import cpp.testutil.Assert;
import cpp.testutil.TypicalAssignments;

public class JsonAdaptedAssignmentTest {
    private static final String INVALID_NAME = "R@chel";
    private static final String INVALID_DEADLINE = "12-13-2020 10:00";

    private static final String VALID_ID = TypicalAssignments.ASSIGNMENT_ONE.getId();
    private static final String VALID_NAME = TypicalAssignments.ASSIGNMENT_ONE.getName().fullName;
    private static final String VALID_DEADLINE = TypicalAssignments.ASSIGNMENT_ONE.getDeadline()
            .format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));

    @Test
    public void toModelType_validAssignmentDetails_returnsAssignment() throws Exception {
        JsonAdaptedAssignment json = new JsonAdaptedAssignment(TypicalAssignments.ASSIGNMENT_ONE);
        Assertions.assertEquals(TypicalAssignments.ASSIGNMENT_ONE, json.toModelType());
    }

    @Test
    public void toModelType_nullId_throwsIllegalValueException() {
        JsonAdaptedAssignment json = new JsonAdaptedAssignment(null, JsonAdaptedAssignmentTest.VALID_NAME,
                JsonAdaptedAssignmentTest.VALID_DEADLINE);
        String expectedMessage = "An assignment's id field is missing.";
        Assert.assertThrows(IllegalValueException.class, expectedMessage, json::toModelType);
    }

    @Test
    public void toModelType_invalidName_throwsIllegalValueException() {
        JsonAdaptedAssignment json = new JsonAdaptedAssignment(JsonAdaptedAssignmentTest.VALID_ID,
                JsonAdaptedAssignmentTest.INVALID_NAME,
                JsonAdaptedAssignmentTest.VALID_DEADLINE);
        String expectedMessage = AssignmentName.MESSAGE_CONSTRAINTS;
        Assert.assertThrows(IllegalValueException.class, expectedMessage, json::toModelType);
    }

    @Test
    public void toModelType_nullName_throwsIllegalValueException() {
        JsonAdaptedAssignment json = new JsonAdaptedAssignment(JsonAdaptedAssignmentTest.VALID_ID, null,
                JsonAdaptedAssignmentTest.VALID_DEADLINE);
        String expectedMessage = String.format(JsonAdaptedAssignment.MISSING_FIELD_MESSAGE_FORMAT,
                AssignmentName.class.getSimpleName());
        Assert.assertThrows(IllegalValueException.class, expectedMessage, json::toModelType);
    }

    @Test
    public void toModelType_invalidDeadline_throwsIllegalValueException() {
        JsonAdaptedAssignment json = new JsonAdaptedAssignment(JsonAdaptedAssignmentTest.VALID_ID,
                JsonAdaptedAssignmentTest.VALID_NAME, JsonAdaptedAssignmentTest.INVALID_DEADLINE);
        String expectedMessage = "Invalid date and time format. Please use the format: dd-MM-yyyy HH:mm";
        Assert.assertThrows(IllegalValueException.class, expectedMessage, json::toModelType);
    }

    @Test
    public void toModelType_nullDeadline_throwsIllegalValueException() {
        JsonAdaptedAssignment json = new JsonAdaptedAssignment(JsonAdaptedAssignmentTest.VALID_ID,
                JsonAdaptedAssignmentTest.VALID_NAME, null);
        String expectedMessage = String.format(JsonAdaptedAssignment.MISSING_FIELD_MESSAGE_FORMAT, "Deadline");
        Assert.assertThrows(IllegalValueException.class, expectedMessage, json::toModelType);
    }

}
