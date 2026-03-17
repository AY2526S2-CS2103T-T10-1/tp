package cpp.model.assignment;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cpp.model.assignment.exceptions.AssignmentNotFoundException;
import cpp.model.assignment.exceptions.DuplicateAssignmentException;
import cpp.testutil.Assert;
import cpp.testutil.AssignmentBuilder;
import cpp.testutil.TypicalAssignments;

public class UniqueAssignmentListTest {

    private final UniqueAssignmentList uniqueAssignmentList = new UniqueAssignmentList();

    @Test
    public void contains_nullAssignment_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class, () -> this.uniqueAssignmentList.contains(null));
    }

    @Test
    public void containsId_nullId_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class, () -> this.uniqueAssignmentList.containsId(null));
    }

    @Test
    public void containsId_assignmentNotInList_returnsFalse() {
        Assertions.assertFalse(this.uniqueAssignmentList.containsId(TypicalAssignments.ASSIGNMENT_ONE.getId()));
    }

    @Test
    public void containsId_assignmentInList_returnsTrue() {
        this.uniqueAssignmentList.add(TypicalAssignments.ASSIGNMENT_ONE);
        Assertions.assertTrue(this.uniqueAssignmentList.containsId(TypicalAssignments.ASSIGNMENT_ONE.getId()));
    }

    @Test
    public void contains_assignmentNotInList_returnsFalse() {
        Assertions.assertFalse(this.uniqueAssignmentList.contains(TypicalAssignments.ASSIGNMENT_ONE));
    }

    @Test
    public void contains_assignmentInList_returnsTrue() {
        this.uniqueAssignmentList.add(TypicalAssignments.ASSIGNMENT_ONE);
        Assertions.assertTrue(this.uniqueAssignmentList.contains(TypicalAssignments.ASSIGNMENT_ONE));
    }

    @Test
    public void contains_assignmentWithSameIdentityFieldsInList_returnsTrue() {
        this.uniqueAssignmentList.add(TypicalAssignments.ASSIGNMENT_ONE);
        Assignment edited = new AssignmentBuilder(TypicalAssignments.ASSIGNMENT_ONE).withDeadline("14-12-2020 11:00")
                .build();
        Assertions.assertTrue(this.uniqueAssignmentList.contains(edited));
    }

    @Test
    public void add_nullAssignment_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class, () -> this.uniqueAssignmentList.add(null));
    }

    @Test
    public void add_duplicateAssignment_throwsDuplicateAssignmentException() {
        this.uniqueAssignmentList.add(TypicalAssignments.ASSIGNMENT_ONE);
        Assert.assertThrows(DuplicateAssignmentException.class,
                () -> this.uniqueAssignmentList.add(TypicalAssignments.ASSIGNMENT_ONE));
    }

    @Test
    public void setAssignment_nullTarget_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class,
                () -> this.uniqueAssignmentList.setAssignment(null, TypicalAssignments.ASSIGNMENT_ONE));
    }

    @Test
    public void setAssignment_nullEdited_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class,
                () -> this.uniqueAssignmentList.setAssignment(TypicalAssignments.ASSIGNMENT_ONE, null));
    }

    @Test
    public void setAssignment_targetNotInList_throwsAssignmentNotFoundException() {
        Assert.assertThrows(AssignmentNotFoundException.class,
                () -> this.uniqueAssignmentList.setAssignment(TypicalAssignments.ASSIGNMENT_ONE,
                        TypicalAssignments.ASSIGNMENT_ONE));
    }

    @Test
    public void setAssignment_editedAssignmentHasSameIdentity_success() {
        this.uniqueAssignmentList.add(TypicalAssignments.ASSIGNMENT_ONE);
        this.uniqueAssignmentList.setAssignment(TypicalAssignments.ASSIGNMENT_ONE, TypicalAssignments.ASSIGNMENT_ONE);
        UniqueAssignmentList expected = new UniqueAssignmentList();
        expected.add(TypicalAssignments.ASSIGNMENT_ONE);
        Assertions.assertEquals(expected, this.uniqueAssignmentList);
    }

    @Test
    public void setAssignment_editedAssignmentHasDifferentIdentity_success() {
        this.uniqueAssignmentList.add(TypicalAssignments.ASSIGNMENT_ONE);
        Assignment different = new AssignmentBuilder().withId("otherid").withName("Assignment 2")
                .withDeadline("14-12-2020 11:00").build();
        this.uniqueAssignmentList.setAssignment(TypicalAssignments.ASSIGNMENT_ONE, different);
        UniqueAssignmentList expected = new UniqueAssignmentList();
        expected.add(different);
        Assertions.assertEquals(expected, this.uniqueAssignmentList);
    }

    @Test
    public void setAssignment_editedAssignmentNonUnique_throwsDuplicateAssignmentException() {
        this.uniqueAssignmentList.add(TypicalAssignments.ASSIGNMENT_ONE);
        Assignment other = new AssignmentBuilder().withId("otherid").withName("Assignment 2").build();
        this.uniqueAssignmentList.add(other);
        Assert.assertThrows(DuplicateAssignmentException.class,
                () -> this.uniqueAssignmentList.setAssignment(TypicalAssignments.ASSIGNMENT_ONE, other));
    }

    @Test
    public void remove_nullAssignment_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class, () -> this.uniqueAssignmentList.remove(null));
    }

    @Test
    public void remove_assignmentDoesNotExist_throwsAssignmentNotFoundException() {
        Assert.assertThrows(AssignmentNotFoundException.class,
                () -> this.uniqueAssignmentList.remove(TypicalAssignments.ASSIGNMENT_ONE));
    }

    @Test
    public void remove_existingAssignment_removesAssignment() {
        this.uniqueAssignmentList.add(TypicalAssignments.ASSIGNMENT_ONE);
        this.uniqueAssignmentList.remove(TypicalAssignments.ASSIGNMENT_ONE);
        UniqueAssignmentList expected = new UniqueAssignmentList();
        Assertions.assertEquals(expected, this.uniqueAssignmentList);
    }

    @Test
    public void setAssignments_nullUniqueAssignmentList_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class,
                () -> this.uniqueAssignmentList.setAssignments((UniqueAssignmentList) null));
    }

    @Test
    public void setAssignments_replacesOwnListWithProvidedUniqueAssignmentList() {
        this.uniqueAssignmentList.add(TypicalAssignments.ASSIGNMENT_ONE);
        UniqueAssignmentList expected = new UniqueAssignmentList();
        Assignment other = new AssignmentBuilder().withId("otherid").withName("Assignment 2").build();
        expected.add(other);
        this.uniqueAssignmentList.setAssignments(expected);
        Assertions.assertEquals(expected, this.uniqueAssignmentList);
    }

    @Test
    public void setAssignments_nullList_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class,
                () -> this.uniqueAssignmentList.setAssignments((List<Assignment>) null));
    }

    @Test
    public void setAssignments_list_replacesOwnListWithProvidedList() {
        this.uniqueAssignmentList.add(TypicalAssignments.ASSIGNMENT_ONE);
        List<Assignment> assignmentList = Collections
                .singletonList(new AssignmentBuilder().withId("otherid").withName("Assignment 2").build());
        this.uniqueAssignmentList.setAssignments(assignmentList);
        UniqueAssignmentList expected = new UniqueAssignmentList();
        expected.add(assignmentList.get(0));
        Assertions.assertEquals(expected, this.uniqueAssignmentList);
    }

    @Test
    public void setAssignments_listWithDuplicateAssignments_throwsDuplicateAssignmentException() {
        List<Assignment> listWithDuplicate = Arrays.asList(TypicalAssignments.ASSIGNMENT_ONE,
                TypicalAssignments.ASSIGNMENT_ONE);
        Assert.assertThrows(DuplicateAssignmentException.class,
                () -> this.uniqueAssignmentList.setAssignments(listWithDuplicate));
    }

    @Test
    public void asUnmodifiableObservableList_modifyList_throwsUnsupportedOperationException() {
        Assert.assertThrows(UnsupportedOperationException.class,
                () -> this.uniqueAssignmentList.asUnmodifiableObservableList().remove(0));
    }

    @Test
    public void iterator_normalList_success() {
        this.uniqueAssignmentList.add(TypicalAssignments.ASSIGNMENT_ONE);
        this.uniqueAssignmentList.add(TypicalAssignments.ASSIGNMENT_TWO);
        List<Assignment> expectedList = Arrays.asList(TypicalAssignments.ASSIGNMENT_ONE,
                TypicalAssignments.ASSIGNMENT_TWO);
        int i = 0;
        for (Assignment assignment : this.uniqueAssignmentList) {
            Assertions.assertEquals(expectedList.get(i), assignment);
            i++;
        }
    }

    @Test
    public void equals_sameObject_returnsTrue() {
        Assertions.assertTrue(this.uniqueAssignmentList.equals(this.uniqueAssignmentList));
    }

    @Test
    public void equals_sameUniqueAssignmentLists_returnsTrue() {
        UniqueAssignmentList uniqueAssignmentList1 = new UniqueAssignmentList();
        UniqueAssignmentList uniqueAssignmentList2 = new UniqueAssignmentList();
        Assertions.assertEquals(uniqueAssignmentList1, uniqueAssignmentList2);
    }

    @Test
    public void equals_null_returnsFalse() {
        Assertions.assertFalse(this.uniqueAssignmentList.equals(null));
    }

    @Test
    public void equals_differentUniqueAssignmentLists_returnsFalse() {
        UniqueAssignmentList uniqueAssignmentList1 = new UniqueAssignmentList();
        UniqueAssignmentList uniqueAssignmentList2 = new UniqueAssignmentList();
        uniqueAssignmentList1.add(TypicalAssignments.ASSIGNMENT_ONE);
        Assertions.assertNotEquals(uniqueAssignmentList1, uniqueAssignmentList2);
    }

    @Test
    public void hashCode_sameUniqueAssignmentLists_returnsSameHashCode() {
        UniqueAssignmentList uniqueAssignmentList1 = new UniqueAssignmentList();
        UniqueAssignmentList uniqueAssignmentList2 = new UniqueAssignmentList();
        Assertions.assertEquals(uniqueAssignmentList1.hashCode(), uniqueAssignmentList2.hashCode());
    }

    @Test
    public void toString_sameList_equals() {
        Assertions.assertEquals(this.uniqueAssignmentList.asUnmodifiableObservableList().toString(),
                this.uniqueAssignmentList.toString());
    }

}
