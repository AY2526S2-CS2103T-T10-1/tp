package cpp.model.classgroup;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cpp.model.classgroup.exceptions.ClassGroupNotFoundException;
import cpp.model.classgroup.exceptions.DuplicateClassGroupException;
import cpp.testutil.Assert;
import cpp.testutil.ClassGroupBuilder;
import cpp.testutil.TypicalClassGroups;
import cpp.testutil.TypicalContacts;

public class UniqueClassGroupListTest {

    private final UniqueClassGroupList uniqueClassGroupList = new UniqueClassGroupList();

    @Test
    public void contains_nullClassGroup_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class, () -> this.uniqueClassGroupList.contains(null));
    }

    @Test
    public void contains_classGroupNotInList_returnsFalse() {
        Assertions.assertFalse(this.uniqueClassGroupList.contains(TypicalClassGroups.CLASS_GROUP_ONE));
    }

    @Test
    public void contains_classGroupInList_returnsTrue() {
        this.uniqueClassGroupList.add(TypicalClassGroups.CLASS_GROUP_ONE);
        Assertions.assertTrue(this.uniqueClassGroupList.contains(TypicalClassGroups.CLASS_GROUP_ONE));
    }

    @Test
    public void contains_classGroupWithSameNameInList_returnsTrue() {
        this.uniqueClassGroupList.add(TypicalClassGroups.CLASS_GROUP_ONE);
        ClassGroup sameNameDifferentId = new ClassGroupBuilder(TypicalClassGroups.CLASS_GROUP_ONE)
                .withId("differentId").build();
        Assertions.assertTrue(this.uniqueClassGroupList.contains(sameNameDifferentId));
    }

    @Test
    public void add_nullClassGroup_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class, () -> this.uniqueClassGroupList.add(null));
    }

    @Test
    public void add_duplicateClassGroup_throwsDuplicateClassGroupException() {
        this.uniqueClassGroupList.add(TypicalClassGroups.CLASS_GROUP_ONE);
        Assert.assertThrows(DuplicateClassGroupException.class,
                () -> this.uniqueClassGroupList.add(TypicalClassGroups.CLASS_GROUP_ONE));
    }

    @Test
    public void setClassGroup_nullTarget_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class,
                () -> this.uniqueClassGroupList.setClassGroup(null, TypicalClassGroups.CLASS_GROUP_ONE));
    }

    @Test
    public void setClassGroup_nullEdited_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class,
                () -> this.uniqueClassGroupList.setClassGroup(TypicalClassGroups.CLASS_GROUP_ONE, null));
    }

    @Test
    public void setClassGroup_targetNotInList_throwsClassGroupNotFoundException() {
        Assert.assertThrows(ClassGroupNotFoundException.class,
                () -> this.uniqueClassGroupList.setClassGroup(TypicalClassGroups.CLASS_GROUP_ONE,
                        TypicalClassGroups.CLASS_GROUP_ONE));
    }

    @Test
    public void setClassGroup_editedHasSameIdentity_success() {
        this.uniqueClassGroupList.add(TypicalClassGroups.CLASS_GROUP_ONE);
        this.uniqueClassGroupList.setClassGroup(TypicalClassGroups.CLASS_GROUP_ONE,
                TypicalClassGroups.CLASS_GROUP_ONE);
        UniqueClassGroupList expected = new UniqueClassGroupList();
        expected.add(TypicalClassGroups.CLASS_GROUP_ONE);
        Assertions.assertEquals(expected, this.uniqueClassGroupList);
    }

    @Test
    public void setClassGroup_editedHasDifferentIdentity_success() {
        this.uniqueClassGroupList.add(TypicalClassGroups.CLASS_GROUP_ONE);
        ClassGroup different = new ClassGroupBuilder().withId("otherid").withName("CS3230T3").build();
        this.uniqueClassGroupList.setClassGroup(TypicalClassGroups.CLASS_GROUP_ONE, different);
        UniqueClassGroupList expected = new UniqueClassGroupList();
        expected.add(different);
        Assertions.assertEquals(expected, this.uniqueClassGroupList);
    }

    @Test
    public void setClassGroup_editedNonUnique_throwsDuplicateClassGroupException() {
        this.uniqueClassGroupList.add(TypicalClassGroups.CLASS_GROUP_ONE);
        ClassGroup other = new ClassGroupBuilder().withId("otherid").withName("CS2101T10").build();
        this.uniqueClassGroupList.add(other);
        Assert.assertThrows(DuplicateClassGroupException.class,
                () -> this.uniqueClassGroupList.setClassGroup(TypicalClassGroups.CLASS_GROUP_ONE, other));
    }

    @Test
    public void remove_nullClassGroup_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class, () -> this.uniqueClassGroupList.remove(null));
    }

    @Test
    public void remove_classGroupDoesNotExist_throwsClassGroupNotFoundException() {
        Assert.assertThrows(ClassGroupNotFoundException.class,
                () -> this.uniqueClassGroupList.remove(TypicalClassGroups.CLASS_GROUP_ONE));
    }

    @Test
    public void remove_existingClassGroup_removesClassGroup() {
        this.uniqueClassGroupList.add(TypicalClassGroups.CLASS_GROUP_ONE);
        this.uniqueClassGroupList.remove(TypicalClassGroups.CLASS_GROUP_ONE);
        UniqueClassGroupList expected = new UniqueClassGroupList();
        Assertions.assertEquals(expected, this.uniqueClassGroupList);
    }

    @Test
    public void unallocateContactFromAllClassGroups_validContact_unallocatesContactFromAllClassGroups() {
        ClassGroup classGroupOne = new ClassGroupBuilder().withId("id1").withName("CS2101T10")
                .withContactIds(TypicalContacts.ALICE.getId()).build();
        ClassGroup classGroupTwo = new ClassGroupBuilder().withId("id2").withName("CS3230T3")
                .withContactIds(TypicalContacts.ALICE.getId(), TypicalContacts.BOB.getId()).build();
        ClassGroup classGroupThree = new ClassGroupBuilder().withId("id3").withName("CS2103T10").build();
        this.uniqueClassGroupList.add(classGroupOne);
        this.uniqueClassGroupList.add(classGroupTwo);
        this.uniqueClassGroupList.add(classGroupThree);
        this.uniqueClassGroupList.unallocateContactFromAllClassGroups(TypicalContacts.ALICE);
        UniqueClassGroupList expected = new UniqueClassGroupList();
        expected.add(new ClassGroupBuilder(classGroupOne).withContactIds().build());
        expected.add(new ClassGroupBuilder(classGroupTwo).withContactIds(TypicalContacts.BOB.getId()).build());
        expected.add(new ClassGroupBuilder(classGroupThree).withContactIds().build());

        Assertions.assertEquals(expected, this.uniqueClassGroupList);
    }

    @Test
    public void setClassGroups_nullUniqueClassGroupList_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class,
                () -> this.uniqueClassGroupList.setClassGroups((UniqueClassGroupList) null));
    }

    @Test
    public void setClassGroups_replacesOwnListWithProvidedUniqueClassGroupList() {
        this.uniqueClassGroupList.add(TypicalClassGroups.CLASS_GROUP_ONE);
        UniqueClassGroupList expected = new UniqueClassGroupList();
        ClassGroup other = new ClassGroupBuilder().withId("otherid").withName("CS2101T10").build();
        expected.add(other);
        this.uniqueClassGroupList.setClassGroups(expected);
        Assertions.assertEquals(expected, this.uniqueClassGroupList);
    }

    @Test
    public void setClassGroups_nullList_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class,
                () -> this.uniqueClassGroupList.setClassGroups((List<ClassGroup>) null));
    }

    @Test
    public void setClassGroups_list_replacesOwnListWithProvidedList() {
        this.uniqueClassGroupList.add(TypicalClassGroups.CLASS_GROUP_ONE);
        List<ClassGroup> classGroupList = Collections
                .singletonList(new ClassGroupBuilder().withId("otherid").withName("CS2101T10").build());
        this.uniqueClassGroupList.setClassGroups(classGroupList);
        UniqueClassGroupList expected = new UniqueClassGroupList();
        expected.add(classGroupList.get(0));
        Assertions.assertEquals(expected, this.uniqueClassGroupList);
    }

    @Test
    public void setClassGroups_listWithDuplicateClassGroups_throwsDuplicateClassGroupException() {
        List<ClassGroup> listWithDuplicate = Arrays.asList(TypicalClassGroups.CLASS_GROUP_ONE,
                TypicalClassGroups.CLASS_GROUP_ONE);
        Assert.assertThrows(DuplicateClassGroupException.class,
                () -> this.uniqueClassGroupList.setClassGroups(listWithDuplicate));
    }

    @Test
    public void asUnmodifiableObservableList_modifyList_throwsUnsupportedOperationException() {
        Assert.assertThrows(UnsupportedOperationException.class,
                () -> this.uniqueClassGroupList.asUnmodifiableObservableList().remove(0));
    }

    @Test
    public void equals_sameUniqueClassGroupLists_returnsTrue() {
        UniqueClassGroupList list1 = new UniqueClassGroupList();
        UniqueClassGroupList list2 = new UniqueClassGroupList();
        Assertions.assertEquals(list1, list2);
    }

    @Test
    public void equals_null_returnsFalse() {
        Assertions.assertFalse(this.uniqueClassGroupList.equals(null));
    }

    @Test
    public void equals_differentUniqueClassGroupLists_returnsFalse() {
        UniqueClassGroupList list1 = new UniqueClassGroupList();
        UniqueClassGroupList list2 = new UniqueClassGroupList();
        list1.add(TypicalClassGroups.CLASS_GROUP_ONE);
        Assertions.assertNotEquals(list1, list2);
    }

    @Test
    public void hashCode_sameUniqueClassGroupLists_returnsSameHashCode() {
        UniqueClassGroupList list1 = new UniqueClassGroupList();
        UniqueClassGroupList list2 = new UniqueClassGroupList();
        Assertions.assertEquals(list1.hashCode(), list2.hashCode());
    }

    @Test
    public void toString_sameList_equals() {
        Assertions.assertEquals(this.uniqueClassGroupList.asUnmodifiableObservableList().toString(),
                this.uniqueClassGroupList.toString());
    }

}
