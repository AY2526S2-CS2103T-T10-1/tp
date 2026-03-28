package cpp.model.assignment;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cpp.logic.parser.ParserUtil;
import cpp.model.assignment.exceptions.ContactAssignmentAlreadyGradedException;
import cpp.model.assignment.exceptions.ContactAssignmentAlreadySubmittedException;
import cpp.model.assignment.exceptions.ContactAssignmentNotSubmittedException;
import cpp.testutil.Assert;

public class ContactAssignmentTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class, () -> new ContactAssignment(null, "c1"));
        Assert.assertThrows(NullPointerException.class, () -> new ContactAssignment("a1", null));
    }

    @Test
    public void constructor_validAssignment_success() {
        ContactAssignment ca = new ContactAssignment("a1", "c1");
        Assertions.assertEquals("a1", ca.getAssignmentId());
        Assertions.assertEquals("c1", ca.getContactId());
        Assertions.assertFalse(ca.isSubmitted());
        Assertions.assertFalse(ca.isGraded());
        Assertions.assertEquals(0, ca.getScore());
    }

    @Test
    public void constructor_validAssignmentWithState_success() {
        LocalDateTime submissionDate = LocalDateTime.now();
        LocalDateTime gradingDate = submissionDate.plusDays(1);
        ContactAssignment ca = new ContactAssignment("a1", "c1", true, submissionDate, true, gradingDate, 85);
        Assertions.assertEquals("a1", ca.getAssignmentId());
        Assertions.assertEquals("c1", ca.getContactId());
        Assertions.assertTrue(ca.isSubmitted());
        Assertions.assertTrue(ca.isGraded());
        Assertions.assertEquals(85, ca.getScore());
    }

    @Test
    public void equals_sameValues_returnsTrue() {
        ContactAssignment ca1 = new ContactAssignment("a1", "c1");
        ContactAssignment ca2 = new ContactAssignment("a1", "c1");
        Assertions.assertEquals(ca1, ca2);
    }

    @Test
    public void equals_differentValues_returnsFalse() {
        ContactAssignment ca1 = new ContactAssignment("a1", "c1");
        ContactAssignment ca2 = new ContactAssignment("a2", "c1");
        ContactAssignment ca3 = new ContactAssignment("a1", "c2");
        Assertions.assertNotEquals(ca1, ca2);
        Assertions.assertNotEquals(ca1, ca3);
    }

    @Test
    public void equals_differentObject_returnsFalse() {
        ContactAssignment ca = new ContactAssignment("a1", "c1");
        Assertions.assertNotEquals(ca, null);
    }

    @Test
    public void toString_validContactAssignment_returnsString() {
        LocalDateTime submissionDate = LocalDateTime.parse(LocalDateTime.now().format(ParserUtil.DATETIME_FORMATTER),
                ParserUtil.DATETIME_FORMATTER);
        LocalDateTime gradingDate = LocalDateTime.parse(
                submissionDate.plusDays(1).format(ParserUtil.DATETIME_FORMATTER), ParserUtil.DATETIME_FORMATTER);
        ContactAssignment ca = new ContactAssignment(
                "a1", "c1", true, submissionDate, true, gradingDate, 90);
        String expected = """
                ContactAssignment[assignmentId=a1, contactId=c1, submission=SubmissionInfo[submitted=true, \
                date=%s], grade=GradeInfo[graded=true, date=%s, score=90.0]]"""
                .formatted(submissionDate.format(ParserUtil.DATETIME_FORMATTER),
                        gradingDate.format(ParserUtil.DATETIME_FORMATTER));
        Assertions.assertEquals(expected, ca.toString());
    }

    @Test
    public void markSubmitted() {
        LocalDateTime submissionDate = LocalDateTime.now();
        ContactAssignment ca = new ContactAssignment("a1", "c1");
        Assertions.assertFalse(ca.isSubmitted());
        ca.markSubmitted(submissionDate);
        Assertions.assertTrue(ca.isSubmitted());
        Assert.assertThrows(ContactAssignmentAlreadySubmittedException.class, () -> ca.markSubmitted(submissionDate));
    }

    @Test
    public void markUnsubmitted() {
        LocalDateTime submissionDate = LocalDateTime.now();
        ContactAssignment ca = new ContactAssignment("a1", "c1", true, submissionDate, false, null, 0);
        Assertions.assertTrue(ca.isSubmitted());
        ca.markUnsubmitted();
        Assertions.assertFalse(ca.isSubmitted());
        Assert.assertThrows(ContactAssignmentNotSubmittedException.class, () -> ca.markUnsubmitted());
    }

    @Test
    public void grade() {
        ContactAssignment ca = new ContactAssignment("a1", "c1");
        Assertions.assertFalse(ca.isGraded());
        Assertions.assertEquals(0, ca.getScore());
        LocalDateTime submissionDate = LocalDateTime.now();
        ca.markSubmitted(submissionDate);
        ca.grade(85, submissionDate.plusDays(1));
        Assertions.assertTrue(ca.isGraded());
        Assertions.assertEquals(85, ca.getScore());
        Assert.assertThrows(ContactAssignmentAlreadyGradedException.class,
                () -> ca.grade(90, submissionDate.plusDays(2)));
    }

    @Test
    public void ungrade() {
        LocalDateTime submissionDate = LocalDateTime.now();
        LocalDateTime gradingDate = submissionDate.plusDays(1);
        ContactAssignment ca = new ContactAssignment("a1", "c1", true, submissionDate, true, gradingDate, 85);
        Assertions.assertTrue(ca.isGraded());
        Assertions.assertEquals(85, ca.getScore());
        ca.ungrade();
        Assertions.assertFalse(ca.isGraded());
        Assertions.assertEquals(0, ca.getScore());
    }

    @Test
    public void getters() {
        LocalDateTime submissionDate = LocalDateTime.now();
        LocalDateTime gradingDate = submissionDate.plusDays(1);
        ContactAssignment ca = new ContactAssignment("a1", "c1", true, submissionDate, true, gradingDate, 75);
        Assertions.assertEquals("a1", ca.getAssignmentId());
        Assertions.assertEquals("c1", ca.getContactId());
        Assertions.assertTrue(ca.isSubmitted());
        Assertions.assertTrue(ca.isGraded());
        Assertions.assertEquals(75, ca.getScore());
    }

}
