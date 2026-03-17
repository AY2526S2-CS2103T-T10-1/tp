package cpp.model.assignment;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import cpp.model.assignment.exceptions.AssignmentNotSubmittedException;
import cpp.model.assignment.exceptions.ContactAssignmentNotFoundException;
import cpp.testutil.Assert;

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
        this.manager.registerContactAssignment(this.ca1);
        this.manager.submit("A1", "C1");
        Assertions.assertTrue(this.ca1.isSubmitted());

        this.manager.grade("A1", "C1", 90);
        Assertions.assertTrue(this.ca1.isGraded());
        Assertions.assertEquals(90, this.ca1.getScore());
    }

    @Test
    public void submit_nonExistingContactAssignment_throws() {
        Assert.assertThrows(ContactAssignmentNotFoundException.class, () -> this.manager.submit("no", "no"));
        this.manager.registerContactAssignment(this.ca1);
        Assert.assertThrows(ContactAssignmentNotFoundException.class, () -> this.manager.submit("A1", "no"));
        Assert.assertThrows(ContactAssignmentNotFoundException.class, () -> this.manager.submit("no", "C1"));
    }

    @Test
    public void grade_withoutSubmit_throws() {
        this.manager.registerContactAssignment(this.ca2);
        Assert.assertThrows(AssignmentNotSubmittedException.class, () -> this.manager.grade("A2", "C2", 50));
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
        Assert.assertThrows(ContactAssignmentNotFoundException.class, () -> this.manager.submit("no", "no"));
        Assert.assertThrows(ContactAssignmentNotFoundException.class, () -> this.manager.grade("no", "no", 10));
    }

}
