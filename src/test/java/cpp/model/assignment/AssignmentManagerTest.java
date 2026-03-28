package cpp.model.assignment;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import cpp.model.assignment.exceptions.ContactAssignmentNotFoundException;
import cpp.model.assignment.exceptions.ContactAssignmentNotGradedException;
import cpp.model.assignment.exceptions.ContactAssignmentNotSubmittedException;
import cpp.testutil.Assert;
import cpp.testutil.TypicalAssignments;
import cpp.testutil.TypicalContacts;

public class AssignmentManagerTest {

    private AssignmentManager manager;
    private ContactAssignment ca1;
    private ContactAssignment ca2;

    @BeforeEach
    public void setUp() {
        this.manager = new AssignmentManager();
        this.ca1 = new ContactAssignment("A1", "C1");
        this.ca2 = new ContactAssignment("A2", "C2");
    }

    @Test
    public void getContactAssignmentMappingForAssignment_withOneCa_success() {
        this.manager.registerContactAssignment(this.ca1);
        Map<String, ContactAssignment> byContact = this.manager.getContactAssignmentMappingForAssignment("C1");

        Assertions.assertEquals(1, byContact.size());
        Assertions.assertSame(this.ca1, byContact.get("A1"));

        // Returned maps must be unmodifiable
        Assert.assertThrows(UnsupportedOperationException.class, () -> byContact.put("X", this.ca1));
    }

    @Test
    public void getContactAssignmentMappingForAssignment_missingAssignment_returnsEmpty() {
        Map<String, ContactAssignment> byContact = this.manager.getContactAssignmentMappingForAssignment("missing");

        Assertions.assertTrue(byContact.isEmpty());

        // Returned maps must be unmodifiable
        Assert.assertThrows(UnsupportedOperationException.class, () -> byContact.put("X", this.ca1));
    }

    @Test
    public void getContactAssignmentMappingForAssignment_withMultipleCa_returnsAll() {
        ContactAssignment ca3 = new ContactAssignment("A3", "C1");
        this.manager.registerContactAssignment(this.ca1);
        this.manager.registerContactAssignment(ca3);

        Map<String, ContactAssignment> byContact = this.manager.getContactAssignmentMappingForAssignment("C1");
        Assertions.assertEquals(2, byContact.size());
        Assertions.assertSame(this.ca1, byContact.get("A1"));
        Assertions.assertSame(ca3, byContact.get("A3"));
    }

    @Test
    public void getContactAssignmentMappingForContact_withOneCa_success() {
        this.manager.registerContactAssignment(this.ca1);
        Map<String, ContactAssignment> byAssignment = this.manager.getContactAssignmentMappingForContact("A1");

        Assertions.assertEquals(1, byAssignment.size());
        Assertions.assertSame(this.ca1, byAssignment.get("C1"));

        // Returned maps must be unmodifiable
        Assert.assertThrows(UnsupportedOperationException.class, () -> byAssignment.put("X", this.ca1));
    }

    @Test
    public void getContactAssignmentMappingForContact_missingContact_returnsEmpty() {
        Map<String, ContactAssignment> byAssignment = this.manager.getContactAssignmentMappingForContact("missing");

        Assertions.assertTrue(byAssignment.isEmpty());

        // Returned maps must be unmodifiable
        Assert.assertThrows(UnsupportedOperationException.class, () -> byAssignment.put("X", this.ca1));
    }

    @Test
    public void getContactAssignmentMappingForContact_withMultipleCa_returnsAll() {
        ContactAssignment ca3 = new ContactAssignment("A1", "C3");
        this.manager.registerContactAssignment(this.ca1);
        this.manager.registerContactAssignment(ca3);

        Map<String, ContactAssignment> byAssignment = this.manager.getContactAssignmentMappingForContact("A1");
        Assertions.assertEquals(2, byAssignment.size());
        Assertions.assertSame(this.ca1, byAssignment.get("C1"));
        Assertions.assertSame(ca3, byAssignment.get("C3"));
    }

    @Test
    public void submitAndGrade_success() {
        LocalDateTime submissionDate = LocalDateTime.now();
        this.manager.registerContactAssignment(this.ca1);
        this.manager.submit("A1", "C1", submissionDate);
        Assertions.assertTrue(this.ca1.isSubmitted());

        this.manager.grade("A1", "C1", 90, submissionDate.plusDays(1));
        Assertions.assertTrue(this.ca1.isGraded());
        Assertions.assertEquals(90, this.ca1.getScore());
    }

    @Test
    public void submit_nonExistingContactAssignment_throws() {
        LocalDateTime submissionDate = LocalDateTime.now();
        Assert.assertThrows(ContactAssignmentNotFoundException.class,
                () -> this.manager.submit("no", "no", submissionDate));
        this.manager.registerContactAssignment(this.ca1);
        Assert.assertThrows(ContactAssignmentNotFoundException.class,
                () -> this.manager.submit("A1", "no", submissionDate));
        Assert.assertThrows(ContactAssignmentNotFoundException.class,
                () -> this.manager.submit("no", "C1", submissionDate));
    }

    @Test
    public void unsubmit_success() {
        LocalDateTime submissionDate = LocalDateTime.now();
        this.manager.registerContactAssignment(this.ca1);
        this.manager.submit("A1", "C1", submissionDate);
        Assertions.assertTrue(this.ca1.isSubmitted());

        this.manager.unsubmit("A1", "C1");
        Assertions.assertFalse(this.ca1.isSubmitted());
    }

    @Test
    public void unsubmit_nonExistingContactAssignment_throwsContactAssignmentNotFoundException() {
        Assert.assertThrows(ContactAssignmentNotFoundException.class,
                () -> this.manager.unsubmit("no", "no"));
        this.manager.registerContactAssignment(this.ca1);
        Assert.assertThrows(ContactAssignmentNotFoundException.class,
                () -> this.manager.unsubmit("A1", "no"));
        Assert.assertThrows(ContactAssignmentNotFoundException.class,
                () -> this.manager.unsubmit("no", "C1"));
    }

    @Test
    public void grade_withoutSubmit_throws() {
        this.manager.registerContactAssignment(this.ca2);
        Assert.assertThrows(ContactAssignmentNotSubmittedException.class,
                () -> this.manager.grade("A2", "C2", 50, LocalDateTime.now()));
    }

    @Test
    public void ungrade_withoutGrade_throwsContactAssignmentNotGradedException() {
        this.manager.registerContactAssignment(this.ca2);
        this.manager.submit("A2", "C2", LocalDateTime.now());
        Assert.assertThrows(ContactAssignmentNotGradedException.class,
                () -> this.manager.ungrade("A2", "C2"));
    }

    @Test
    public void ungrade_withoutSubmit_throwsContactAssignmentNotSubmittedException() {
        this.manager.registerContactAssignment(this.ca2);
        Assert.assertThrows(ContactAssignmentNotSubmittedException.class,
                () -> this.manager.ungrade("A2", "C2"));
    }

    @Test
    public void ungrade_success() {
        LocalDateTime submissionDate = LocalDateTime.now();
        LocalDateTime gradingDate = submissionDate.plusDays(1);
        this.manager.registerContactAssignment(this.ca2);
        this.manager.submit("A2", "C2", submissionDate);
        this.manager.grade("A2", "C2", 50, gradingDate);

        this.manager.ungrade("A2", "C2");
        Assertions.assertFalse(this.ca2.isGraded());
        Assertions.assertEquals(0, this.ca2.getScore());
    }

    @Test
    public void registerContactAssignment_success() {
        this.manager.registerContactAssignment(this.ca1);
        Assertions.assertTrue(this.manager.getContactAssignmentMappingForAssignment("C1").containsKey("A1"));
    }

    @Test
    public void registerContactAssignment_duplicate_doesNotOverwrite() {
        ContactAssignment duplicate = new ContactAssignment("A1", "C1");
        this.manager.registerContactAssignment(this.ca1);
        this.manager.registerContactAssignment(duplicate);

        Assertions.assertSame(this.ca1, this.manager.getContactAssignmentMappingForAssignment("C1").get("A1"));
    }

    @Test
    public void deregisterContactAssignment_success() {
        this.manager.registerContactAssignment(this.ca1);
        this.manager.deregisterContactAssignment(this.ca1);

        Assertions.assertTrue(this.manager.getContactAssignmentMappingForAssignment("C1").isEmpty());
        Assertions.assertTrue(this.manager.getContactAssignmentMappingForContact("A1").isEmpty());
    }

    @Test
    public void deregisterContactAssignment_missingAssignment_doesNothing() {
        this.manager.registerContactAssignment(this.ca1);
        ContactAssignment missing = new ContactAssignment("missing", "C3");
        this.manager.deregisterContactAssignment(missing);

        Assertions.assertTrue(this.manager.getContactAssignmentMappingForAssignment("C1").containsKey("A1"));
    }

    @Test
    public void deregisterContactAssignment_nonEmpty_doesNotAffectOtherAssignments() {
        ContactAssignment ca3 = new ContactAssignment("A3", "C2");
        this.manager.registerContactAssignment(this.ca1);
        this.manager.registerContactAssignment(this.ca2);
        this.manager.registerContactAssignment(ca3);

        this.manager.deregisterContactAssignment(this.ca1);

        Assertions.assertTrue(this.manager.getContactAssignmentMappingForAssignment("C1").isEmpty());
        Assertions.assertTrue(this.manager.getContactAssignmentMappingForContact("A2").containsKey("C2"));
    }

    @Test
    public void deregisterContactAssignment_oneCaForContact_deregistersAllForContact() {
        this.manager.registerContactAssignment(this.ca1);
        this.manager.registerContactAssignment(this.ca2);

        this.manager.deregisterContactAssignment(this.ca1);

        Assertions.assertTrue(this.manager.getContactAssignmentMappingForContact("A1").isEmpty());
        Assertions.assertTrue(this.manager.getContactAssignmentMappingForAssignment("C1").isEmpty());
        Assertions.assertTrue(this.manager.getContactAssignmentMappingForAssignment("C2").containsKey("A2"));
    }

    @Test
    public void constructor_withInitialList_registersAll() {
        List<ContactAssignment> initial = Arrays.asList(this.ca1, this.ca2);
        AssignmentManager m2 = new AssignmentManager(initial);
        Assertions.assertSame(this.ca1, m2.getContactAssignmentMappingForAssignment("C1").get("A1"));
        Assertions.assertSame(this.ca2, m2.getContactAssignmentMappingForContact("A2").get("C2"));
    }

    @Test
    public void operations_onMissingContactAssignment_throwNotFound() {
        LocalDateTime submissionDate = LocalDateTime.now();
        LocalDateTime gradingDate = submissionDate.plusDays(1);
        Assert.assertThrows(ContactAssignmentNotFoundException.class,
                () -> this.manager.submit("no", "no", submissionDate));
        Assert.assertThrows(ContactAssignmentNotFoundException.class,
                () -> this.manager.grade("no", "no", 100, gradingDate));
    }

    @Test
    public void deregisterContactAssignmentsForAssignment_success() {
        ContactAssignment ca3 = new ContactAssignment(TypicalAssignments.ASSIGNMENT_ONE.getId(), "C1");
        ContactAssignment ca4 = new ContactAssignment(TypicalAssignments.ASSIGNMENT_TWO.getId(), "C1");
        this.manager.registerContactAssignment(ca3);
        this.manager.registerContactAssignment(ca4);

        Assertions.assertTrue(this.manager.getContactAssignmentMappingForAssignment("C1").containsKey(
                TypicalAssignments.ASSIGNMENT_ONE.getId()));
        Assertions.assertTrue(this.manager.getContactAssignmentMappingForAssignment("C1").containsKey(
                TypicalAssignments.ASSIGNMENT_TWO.getId()));

        this.manager.deregisterContactAssignmentsForAssignment(
                TypicalAssignments.ASSIGNMENT_ONE);

        Assertions.assertFalse(this.manager.getContactAssignmentMappingForAssignment("C1").isEmpty());
        Assertions.assertTrue(this.manager
                .getContactAssignmentMappingForContact(TypicalAssignments.ASSIGNMENT_ONE.getId()).isEmpty());

        this.manager.deregisterContactAssignmentsForAssignment(
                TypicalAssignments.ASSIGNMENT_TWO);
        Assertions.assertTrue(this.manager.getContactAssignmentMappingForAssignment("C1").isEmpty());
        Assertions.assertTrue(this.manager
                .getContactAssignmentMappingForContact(TypicalAssignments.ASSIGNMENT_TWO.getId()).isEmpty());

    }

    @Test
    public void deregisterContactAssignmentsForAssignment_missingAssignment_doesNothing() {
        ContactAssignment ca3 = new ContactAssignment(TypicalAssignments.ASSIGNMENT_ONE.getId(), "C1");
        this.manager.registerContactAssignment(ca3);
        this.manager.deregisterContactAssignmentsForAssignment(TypicalAssignments.ASSIGNMENT_TWO);
        Assertions.assertTrue(this.manager.getContactAssignmentMappingForAssignment("C1").containsKey(
                TypicalAssignments.ASSIGNMENT_ONE.getId()));
        Assertions.assertFalse(this.manager.getContactAssignmentMappingForAssignment("C1").containsKey(
                TypicalAssignments.ASSIGNMENT_TWO.getId()));
    }

    @Test
    public void deregisterContactAssignmentsForContact_success() {
        ContactAssignment ca3 = new ContactAssignment("a1", TypicalContacts.ALICE.getId());
        ContactAssignment ca4 = new ContactAssignment("a2", TypicalContacts.BOB.getId());
        ContactAssignment ca5 = new ContactAssignment("a1", TypicalContacts.BOB.getId());
        this.manager.registerContactAssignment(ca3);
        this.manager.registerContactAssignment(ca4);
        this.manager.registerContactAssignment(ca5);

        this.manager.deregisterContactAssignmentsForContact(TypicalContacts.BOB);
        Assertions
                .assertTrue(
                        this.manager.getContactAssignmentMappingForAssignment(TypicalContacts.BOB.getId()).isEmpty());

        Assertions.assertFalse(
                this.manager.getContactAssignmentMappingForAssignment(TypicalContacts.ALICE.getId()).isEmpty());

        this.manager.deregisterContactAssignmentsForContact(TypicalContacts.ALICE);

        Assertions.assertTrue(
                this.manager.getContactAssignmentMappingForAssignment(TypicalContacts.ALICE.getId()).isEmpty());

    }
}
