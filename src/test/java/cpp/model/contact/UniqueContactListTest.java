package cpp.model.contact;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cpp.logic.commands.CommandTestUtil;
import cpp.model.contact.exceptions.ContactNotFoundException;
import cpp.model.contact.exceptions.DuplicateContactException;
import cpp.testutil.Assert;
import cpp.testutil.ContactBuilder;
import cpp.testutil.TypicalContacts;

public class UniqueContactListTest {

    private final UniqueContactList uniqueContactList = new UniqueContactList();

    @Test
    public void contains_nullContact_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class, () -> this.uniqueContactList.contains(null));
    }

    @Test
    public void contains_contactNotInList_returnsFalse() {
        Assertions.assertFalse(this.uniqueContactList.contains(TypicalContacts.ALICE));
    }

    @Test
    public void contains_contactInList_returnsTrue() {
        this.uniqueContactList.add(TypicalContacts.ALICE);
        Assertions.assertTrue(this.uniqueContactList.contains(TypicalContacts.ALICE));
    }

    @Test
    public void contains_contactWithSameIdentityFieldsInList_returnsTrue() {
        this.uniqueContactList.add(TypicalContacts.ALICE);
        Contact editedAlice = new ContactBuilder(TypicalContacts.ALICE).withAddress(CommandTestUtil.VALID_ADDRESS_BOB)
                .withTags(CommandTestUtil.VALID_TAG_HUSBAND)
                .build();
        Assertions.assertTrue(this.uniqueContactList.contains(editedAlice));
    }

    @Test
    public void add_nullContact_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class, () -> this.uniqueContactList.add(null));
    }

    @Test
    public void add_duplicateContact_throwsDuplicateContactException() {
        this.uniqueContactList.add(TypicalContacts.ALICE);
        Assert.assertThrows(DuplicateContactException.class, () -> this.uniqueContactList.add(TypicalContacts.ALICE));
    }

    @Test
    public void setContact_nullTargetContact_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class,
                () -> this.uniqueContactList.setContact(null, TypicalContacts.ALICE));
    }

    @Test
    public void setContact_nullEditedContact_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class,
                () -> this.uniqueContactList.setContact(TypicalContacts.ALICE, null));
    }

    @Test
    public void setContact_targetContactNotInList_throwsContactNotFoundException() {
        Assert.assertThrows(ContactNotFoundException.class,
                () -> this.uniqueContactList.setContact(TypicalContacts.ALICE, TypicalContacts.ALICE));
    }

    @Test
    public void setContact_editedContactIsSameContact_success() {
        this.uniqueContactList.add(TypicalContacts.ALICE);
        this.uniqueContactList.setContact(TypicalContacts.ALICE, TypicalContacts.ALICE);
        UniqueContactList expectedUniqueContactList = new UniqueContactList();
        expectedUniqueContactList.add(TypicalContacts.ALICE);
        Assertions.assertEquals(expectedUniqueContactList, this.uniqueContactList);
    }

    @Test
    public void setContact_editedContactHasSameIdentity_success() {
        this.uniqueContactList.add(TypicalContacts.ALICE);
        Contact editedAlice = new ContactBuilder(TypicalContacts.ALICE).withAddress(CommandTestUtil.VALID_ADDRESS_BOB)
                .withTags(CommandTestUtil.VALID_TAG_HUSBAND)
                .build();
        this.uniqueContactList.setContact(TypicalContacts.ALICE, editedAlice);
        UniqueContactList expectedUniqueContactList = new UniqueContactList();
        expectedUniqueContactList.add(editedAlice);
        Assertions.assertEquals(expectedUniqueContactList, this.uniqueContactList);
    }

    @Test
    public void setContact_editedContactHasDifferentIdentity_success() {
        this.uniqueContactList.add(TypicalContacts.ALICE);
        this.uniqueContactList.setContact(TypicalContacts.ALICE, TypicalContacts.BOB);
        UniqueContactList expectedUniqueContactList = new UniqueContactList();
        expectedUniqueContactList.add(TypicalContacts.BOB);
        Assertions.assertEquals(expectedUniqueContactList, this.uniqueContactList);
    }

    @Test
    public void setContact_editedContactHasNonUniqueIdentity_throwsDuplicateContactException() {
        this.uniqueContactList.add(TypicalContacts.ALICE);
        this.uniqueContactList.add(TypicalContacts.BOB);
        Assert.assertThrows(DuplicateContactException.class,
                () -> this.uniqueContactList.setContact(TypicalContacts.ALICE, TypicalContacts.BOB));
    }

    @Test
    public void remove_nullContact_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class, () -> this.uniqueContactList.remove(null));
    }

    @Test
    public void remove_contactDoesNotExist_throwsContactNotFoundException() {
        Assert.assertThrows(ContactNotFoundException.class, () -> this.uniqueContactList.remove(TypicalContacts.ALICE));
    }

    @Test
    public void remove_existingContact_removesContact() {
        this.uniqueContactList.add(TypicalContacts.ALICE);
        this.uniqueContactList.remove(TypicalContacts.ALICE);
        UniqueContactList expectedUniqueContactList = new UniqueContactList();
        Assertions.assertEquals(expectedUniqueContactList, this.uniqueContactList);
    }

    @Test
    public void setContacts_nullUniqueContactList_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class,
                () -> this.uniqueContactList.setContacts((UniqueContactList) null));
    }

    @Test
    public void setContacts_uniqueContactList_replacesOwnListWithProvidedUniqueContactList() {
        this.uniqueContactList.add(TypicalContacts.ALICE);
        UniqueContactList expectedUniqueContactList = new UniqueContactList();
        expectedUniqueContactList.add(TypicalContacts.BOB);
        this.uniqueContactList.setContacts(expectedUniqueContactList);
        Assertions.assertEquals(expectedUniqueContactList, this.uniqueContactList);
    }

    @Test
    public void setContacts_nullList_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class, () -> this.uniqueContactList.setContacts((List<Contact>) null));
    }

    @Test
    public void setContacts_list_replacesOwnListWithProvidedList() {
        this.uniqueContactList.add(TypicalContacts.ALICE);
        List<Contact> contactList = Collections.singletonList(TypicalContacts.BOB);
        this.uniqueContactList.setContacts(contactList);
        UniqueContactList expectedUniqueContactList = new UniqueContactList();
        expectedUniqueContactList.add(TypicalContacts.BOB);
        Assertions.assertEquals(expectedUniqueContactList, this.uniqueContactList);
    }

    @Test
    public void setContacts_listWithDuplicateContacts_throwsDuplicateContactException() {
        List<Contact> listWithDuplicateContacts = Arrays.asList(TypicalContacts.ALICE, TypicalContacts.ALICE);
        Assert.assertThrows(DuplicateContactException.class,
                () -> this.uniqueContactList.setContacts(listWithDuplicateContacts));
    }

    @Test
    public void asUnmodifiableObservableList_modifyList_throwsUnsupportedOperationException() {
        Assert.assertThrows(UnsupportedOperationException.class,
                () -> this.uniqueContactList.asUnmodifiableObservableList().remove(0));
    }

    @Test
    public void toStringMethod() {
        Assertions.assertEquals(this.uniqueContactList.asUnmodifiableObservableList().toString(),
                this.uniqueContactList.toString());
    }
}
