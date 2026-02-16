package seedu.address.model.person;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.CommandTestUtil;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.model.person.exceptions.PersonNotFoundException;
import seedu.address.testutil.Assert;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.TypicalPersons;

public class UniquePersonListTest {

    private final UniquePersonList uniquePersonList = new UniquePersonList();

    @Test
    public void contains_nullPerson_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class, () -> this.uniquePersonList.contains(null));
    }

    @Test
    public void contains_personNotInList_returnsFalse() {
        Assertions.assertFalse(this.uniquePersonList.contains(TypicalPersons.ALICE));
    }

    @Test
    public void contains_personInList_returnsTrue() {
        this.uniquePersonList.add(TypicalPersons.ALICE);
        Assertions.assertTrue(this.uniquePersonList.contains(TypicalPersons.ALICE));
    }

    @Test
    public void contains_personWithSameIdentityFieldsInList_returnsTrue() {
        this.uniquePersonList.add(TypicalPersons.ALICE);
        Person editedAlice = new PersonBuilder(TypicalPersons.ALICE).withAddress(CommandTestUtil.VALID_ADDRESS_BOB)
                .withTags(CommandTestUtil.VALID_TAG_HUSBAND)
                .build();
        Assertions.assertTrue(this.uniquePersonList.contains(editedAlice));
    }

    @Test
    public void add_nullPerson_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class, () -> this.uniquePersonList.add(null));
    }

    @Test
    public void add_duplicatePerson_throwsDuplicatePersonException() {
        this.uniquePersonList.add(TypicalPersons.ALICE);
        Assert.assertThrows(DuplicatePersonException.class, () -> this.uniquePersonList.add(TypicalPersons.ALICE));
    }

    @Test
    public void setPerson_nullTargetPerson_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class,
                () -> this.uniquePersonList.setPerson(null, TypicalPersons.ALICE));
    }

    @Test
    public void setPerson_nullEditedPerson_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class,
                () -> this.uniquePersonList.setPerson(TypicalPersons.ALICE, null));
    }

    @Test
    public void setPerson_targetPersonNotInList_throwsPersonNotFoundException() {
        Assert.assertThrows(PersonNotFoundException.class,
                () -> this.uniquePersonList.setPerson(TypicalPersons.ALICE, TypicalPersons.ALICE));
    }

    @Test
    public void setPerson_editedPersonIsSamePerson_success() {
        this.uniquePersonList.add(TypicalPersons.ALICE);
        this.uniquePersonList.setPerson(TypicalPersons.ALICE, TypicalPersons.ALICE);
        UniquePersonList expectedUniquePersonList = new UniquePersonList();
        expectedUniquePersonList.add(TypicalPersons.ALICE);
        Assertions.assertEquals(expectedUniquePersonList, this.uniquePersonList);
    }

    @Test
    public void setPerson_editedPersonHasSameIdentity_success() {
        this.uniquePersonList.add(TypicalPersons.ALICE);
        Person editedAlice = new PersonBuilder(TypicalPersons.ALICE).withAddress(CommandTestUtil.VALID_ADDRESS_BOB)
                .withTags(CommandTestUtil.VALID_TAG_HUSBAND)
                .build();
        this.uniquePersonList.setPerson(TypicalPersons.ALICE, editedAlice);
        UniquePersonList expectedUniquePersonList = new UniquePersonList();
        expectedUniquePersonList.add(editedAlice);
        Assertions.assertEquals(expectedUniquePersonList, this.uniquePersonList);
    }

    @Test
    public void setPerson_editedPersonHasDifferentIdentity_success() {
        this.uniquePersonList.add(TypicalPersons.ALICE);
        this.uniquePersonList.setPerson(TypicalPersons.ALICE, TypicalPersons.BOB);
        UniquePersonList expectedUniquePersonList = new UniquePersonList();
        expectedUniquePersonList.add(TypicalPersons.BOB);
        Assertions.assertEquals(expectedUniquePersonList, this.uniquePersonList);
    }

    @Test
    public void setPerson_editedPersonHasNonUniqueIdentity_throwsDuplicatePersonException() {
        this.uniquePersonList.add(TypicalPersons.ALICE);
        this.uniquePersonList.add(TypicalPersons.BOB);
        Assert.assertThrows(DuplicatePersonException.class,
                () -> this.uniquePersonList.setPerson(TypicalPersons.ALICE, TypicalPersons.BOB));
    }

    @Test
    public void remove_nullPerson_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class, () -> this.uniquePersonList.remove(null));
    }

    @Test
    public void remove_personDoesNotExist_throwsPersonNotFoundException() {
        Assert.assertThrows(PersonNotFoundException.class, () -> this.uniquePersonList.remove(TypicalPersons.ALICE));
    }

    @Test
    public void remove_existingPerson_removesPerson() {
        this.uniquePersonList.add(TypicalPersons.ALICE);
        this.uniquePersonList.remove(TypicalPersons.ALICE);
        UniquePersonList expectedUniquePersonList = new UniquePersonList();
        Assertions.assertEquals(expectedUniquePersonList, this.uniquePersonList);
    }

    @Test
    public void setPersons_nullUniquePersonList_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class,
                () -> this.uniquePersonList.setPersons((UniquePersonList) null));
    }

    @Test
    public void setPersons_uniquePersonList_replacesOwnListWithProvidedUniquePersonList() {
        this.uniquePersonList.add(TypicalPersons.ALICE);
        UniquePersonList expectedUniquePersonList = new UniquePersonList();
        expectedUniquePersonList.add(TypicalPersons.BOB);
        this.uniquePersonList.setPersons(expectedUniquePersonList);
        Assertions.assertEquals(expectedUniquePersonList, this.uniquePersonList);
    }

    @Test
    public void setPersons_nullList_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class, () -> this.uniquePersonList.setPersons((List<Person>) null));
    }

    @Test
    public void setPersons_list_replacesOwnListWithProvidedList() {
        this.uniquePersonList.add(TypicalPersons.ALICE);
        List<Person> personList = Collections.singletonList(TypicalPersons.BOB);
        this.uniquePersonList.setPersons(personList);
        UniquePersonList expectedUniquePersonList = new UniquePersonList();
        expectedUniquePersonList.add(TypicalPersons.BOB);
        Assertions.assertEquals(expectedUniquePersonList, this.uniquePersonList);
    }

    @Test
    public void setPersons_listWithDuplicatePersons_throwsDuplicatePersonException() {
        List<Person> listWithDuplicatePersons = Arrays.asList(TypicalPersons.ALICE, TypicalPersons.ALICE);
        Assert.assertThrows(DuplicatePersonException.class,
                () -> this.uniquePersonList.setPersons(listWithDuplicatePersons));
    }

    @Test
    public void asUnmodifiableObservableList_modifyList_throwsUnsupportedOperationException() {
        Assert.assertThrows(UnsupportedOperationException.class,
                () -> this.uniquePersonList.asUnmodifiableObservableList().remove(0));
    }

    @Test
    public void toStringMethod() {
        Assertions.assertEquals(this.uniquePersonList.asUnmodifiableObservableList().toString(),
                this.uniquePersonList.toString());
    }
}
