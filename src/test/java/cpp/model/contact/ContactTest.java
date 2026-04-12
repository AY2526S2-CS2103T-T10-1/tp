package cpp.model.contact;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cpp.logic.commands.CommandTestUtil;
import cpp.testutil.Assert;
import cpp.testutil.ContactBuilder;
import cpp.testutil.TypicalContacts;

public class ContactTest {

    @Test
    public void constructor_nullId_success() {
        Contact contact = new ContactBuilder().withId(null).build();
        Assertions.assertNotNull(contact.getId());
    }

    @Test
    public void constructor_validId_success() {
        Contact contact = new ContactBuilder().withId("1").build();
        Assertions.assertEquals("1", contact.getId());
    }

    @Test
    public void asObservableList_modifyList_throwsUnsupportedOperationException() {
        Contact contact = new ContactBuilder().build();
        Assert.assertThrows(UnsupportedOperationException.class, () -> contact.getTags().remove(0));
    }

    @Test
    public void isSameContact() {
        // same object -> returns true
        Assertions.assertTrue(TypicalContacts.ALICE.isSameContact(TypicalContacts.ALICE));

        // null -> returns false
        Assertions.assertFalse(TypicalContacts.ALICE.isSameContact(null));

        // same name, all other attributes different -> returns true
        Contact editedAlice = new ContactBuilder(TypicalContacts.ALICE).withPhone(CommandTestUtil.VALID_PHONE_BOB)
                .withEmail(CommandTestUtil.VALID_EMAIL_BOB)
                .withAddress(CommandTestUtil.VALID_ADDRESS_BOB).withTags(CommandTestUtil.VALID_TAG_HUSBAND).build();
        Assertions.assertTrue(TypicalContacts.ALICE.isSameContact(editedAlice));

        // different name, all other attributes same -> returns false
        editedAlice = new ContactBuilder(TypicalContacts.ALICE).withName(CommandTestUtil.VALID_NAME_BOB).build();
        Assertions.assertFalse(TypicalContacts.ALICE.isSameContact(editedAlice));

        // name differs in case, all other attributes same -> returns true
        Contact editedBob = new ContactBuilder(TypicalContacts.BOB)
                .withName(CommandTestUtil.VALID_NAME_BOB.toLowerCase())
                .build();
        Assertions.assertTrue(TypicalContacts.BOB.isSameContact(editedBob));
    }

    @Test
    public void equals() {
        // same values -> returns true
        Contact aliceCopy = new ContactBuilder(TypicalContacts.ALICE).build();
        Assertions.assertTrue(TypicalContacts.ALICE.equals(aliceCopy));

        // same object -> returns true
        Assertions.assertTrue(TypicalContacts.ALICE.equals(TypicalContacts.ALICE));

        // null -> returns false
        Assertions.assertFalse(TypicalContacts.ALICE.equals(null));

        // different type -> returns false
        Assertions.assertFalse(TypicalContacts.ALICE.equals(5));

        // different contact -> returns false
        Assertions.assertFalse(TypicalContacts.ALICE.equals(TypicalContacts.BOB));

        // different name -> returns false
        Contact editedAlice = new ContactBuilder(TypicalContacts.ALICE).withName(CommandTestUtil.VALID_NAME_BOB)
                .build();
        Assertions.assertFalse(TypicalContacts.ALICE.equals(editedAlice));

        // different phone -> returns false
        editedAlice = new ContactBuilder(TypicalContacts.ALICE).withPhone(CommandTestUtil.VALID_PHONE_BOB).build();
        Assertions.assertFalse(TypicalContacts.ALICE.equals(editedAlice));

        // different email -> returns false
        editedAlice = new ContactBuilder(TypicalContacts.ALICE).withEmail(CommandTestUtil.VALID_EMAIL_BOB).build();
        Assertions.assertFalse(TypicalContacts.ALICE.equals(editedAlice));

        // different address -> returns false
        editedAlice = new ContactBuilder(TypicalContacts.ALICE).withAddress(CommandTestUtil.VALID_ADDRESS_BOB).build();
        Assertions.assertFalse(TypicalContacts.ALICE.equals(editedAlice));

        // different tags -> returns false
        editedAlice = new ContactBuilder(TypicalContacts.ALICE).withTags(CommandTestUtil.VALID_TAG_HUSBAND).build();
        Assertions.assertFalse(TypicalContacts.ALICE.equals(editedAlice));
    }

    @Test
    public void toStringMethod() {
        String expected = Contact.class.getCanonicalName() + "{name=" + TypicalContacts.ALICE.getName() + ", phone="
                + TypicalContacts.ALICE.getPhone()
                + ", email=" + TypicalContacts.ALICE.getEmail() + ", address=" + TypicalContacts.ALICE.getAddress()
                + ", tags=" + TypicalContacts.ALICE.getTags() + "}";
        Assertions.assertEquals(expected, TypicalContacts.ALICE.toString());
    }
}
