package cpp.model.classgroup;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cpp.model.classgroup.exceptions.ContactAlreadyAllocatedClassGroupException;
import cpp.model.classgroup.exceptions.ContactNotAllocatedClassGroupException;
import cpp.testutil.ClassGroupBuilder;

public class ClassGroupTest {

    @Test
    public void equals_differentType_returnsFalse() {
        ClassGroup cg = new ClassGroupBuilder().build();
        String s = "Not a class group";
        Assertions.assertNotEquals(cg, s);
    }

    @Test
    public void equals_sameIdAndName_returnsTrue() {
        ClassGroup cg1 = new ClassGroupBuilder().withId("id1").withName("CS2103T10").build();
        ClassGroup cg2 = new ClassGroupBuilder().withId("id1").withName("CS2103T10").build();
        Assertions.assertEquals(cg1, cg2);
    }

    @Test
    public void equals_differentId_returnsFalse() {
        ClassGroup cg1 = new ClassGroupBuilder().withId("id1").build();
        ClassGroup cg2 = new ClassGroupBuilder().withId("id2").build();
        Assertions.assertNotEquals(cg1, cg2);
    }

    @Test
    public void equals_differentName_returnsFalse() {
        ClassGroup cg1 = new ClassGroupBuilder().withId("id1").withName("CS2103T10").build();
        ClassGroup cg2 = new ClassGroupBuilder().withId("id1").withName("CS2101T10").build();
        Assertions.assertNotEquals(cg1, cg2);
    }

    @Test
    public void hashCode_sameIdAndName_equal() {
        ClassGroup cg1 = new ClassGroupBuilder().withId("id1").withName("CS2103T10").build();
        ClassGroup cg2 = new ClassGroupBuilder().withId("id1").withName("CS2103T10").build();
        Assertions.assertEquals(cg1.hashCode(), cg2.hashCode());
    }

    @Test
    public void hashCode_differentName_notEqual() {
        ClassGroup cg1 = new ClassGroupBuilder().withId("id1").withName("CS2103T10").build();
        ClassGroup cg2 = new ClassGroupBuilder().withId("id1").withName("CS2101T10").build();
        Assertions.assertNotEquals(cg1.hashCode(), cg2.hashCode());
    }

    @Test
    public void toString_containsName() {
        ClassGroup cg = new ClassGroupBuilder().withId("id1").withName("CS2103T10").build();
        String s = cg.toString();
        Assertions.assertTrue(s.contains(cg.getName().fullName));
    }

    @Test
    public void allocateContact_newContact_success() {
        ClassGroup cg = new ClassGroupBuilder().build();
        String contactId1 = "contactId1";
        String contactId2 = "contactId2";
        cg.allocateContact(contactId1);
        cg.allocateContact(contactId2);
        Assertions.assertTrue(cg.getContactIdSet().contains(contactId1));
        Assertions.assertTrue(cg.getContactIdSet().contains(contactId2));
    }

    @Test
    public void allocateContact_duplicateAllocation_throwsException() {
        ClassGroup cg = new ClassGroupBuilder().build();
        String contactId = "contactId";
        cg.allocateContact(contactId);
        Assertions.assertThrows(ContactAlreadyAllocatedClassGroupException.class, () -> cg.allocateContact(contactId));
    }

    @Test
    public void unallocateContact_allocatedContact_success() {
        ClassGroup cg = new ClassGroupBuilder().build();
        String contactId = "contactId";
        cg.allocateContact(contactId);
        Assertions.assertTrue(cg.getContactIdSet().contains(contactId));
        cg.unallocateContact(contactId);
        Assertions.assertFalse(cg.getContactIdSet().contains(contactId));
    }

    @Test
    public void unallocateContact_notAllocatedContact_throwsException() {
        ClassGroup cg = new ClassGroupBuilder().build();
        String contactId = "contactId";
        Assertions.assertThrows(ContactNotAllocatedClassGroupException.class, () -> cg.unallocateContact(contactId));
    }
}
