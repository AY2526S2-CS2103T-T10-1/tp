package cpp.testutil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cpp.logic.commands.CommandTestUtil;
import cpp.model.AddressBook;
import cpp.model.assignment.Assignment;
import cpp.model.contact.Contact;

/**
 * A utility class containing a list of {@code Contact} objects to be used in
 * tests.
 */
public class TypicalContacts {

    public static final Contact ALICE = new ContactBuilder().withId("683f92b5-9e96-47bb-94cd-e8ede5523d7a")
            .withName("Alice Pauline")
            .withAddress("123, Jurong West Ave 6, #08-111").withEmail("alice@example.com")
            .withPhone("94351253")
            .withTags("friends").build();
    public static final Contact BENSON = new ContactBuilder().withId("683f92b5-9e96-47bb-94cd-e8ede5523d7b")
            .withName("Benson Meier")
            .withAddress("311, Clementi Ave 2, #02-25")
            .withEmail("johnd@example.com").withPhone("98765432")
            .withTags("owesMoney", "friends").build();
    public static final Contact CARL = new ContactBuilder().withId("683f92b5-9e96-47bb-94cd-e8ede5523d7c")
            .withName("Carl Kurz").withPhone("95352563")
            .withEmail("heinz@example.com").withAddress("wall street").build();
    public static final Contact DANIEL = new ContactBuilder().withId("683f92b5-9e96-47bb-94cd-e8ede5523d7d")
            .withName("Daniel Meier").withPhone("87652533")
            .withEmail("cornelia@example.com").withAddress("10th street").withTags("friends").build();
    public static final Contact ELLE = new ContactBuilder().withId("683f92b5-9e96-47bb-94cd-e8ede5523d7e")
            .withName("Elle Meyer").withPhone("9482224")
            .withEmail("werner@example.com").withAddress("michegan ave").build();
    public static final Contact FIONA = new ContactBuilder().withId("683f92b5-9e96-47bb-94cd-e8ede5523d7f")
            .withName("Fiona Kunz").withPhone("9482427")
            .withEmail("lydia@example.com").withAddress("little tokyo").build();
    public static final Contact GEORGE = new ContactBuilder().withId("683f92b5-9e96-47bb-94cd-e8ede5523d80")
            .withName("George Best").withPhone("9482442")
            .withEmail("anna@example.com").withAddress("4th street").build();

    // Manually added
    public static final Contact HOON = new ContactBuilder().withId("683f92b5-9e96-47bb-94cd-e8ede5523d81")
            .withName("Hoon Meier").withPhone("8482424")
            .withEmail("stefan@example.com").withAddress("little india").build();
    public static final Contact IDA = new ContactBuilder().withId("683f92b5-9e96-47bb-94cd-e8ede5523d82")
            .withName("Ida Mueller").withPhone("8482131")
            .withEmail("hans@example.com").withAddress("chicago ave").build();

    // Manually added - Contact's details found in {@code CommandTestUtil}
    public static final Contact AMY = new ContactBuilder().withId(CommandTestUtil.VALID_ID_AMY)
            .withName(CommandTestUtil.VALID_NAME_AMY)
            .withPhone(CommandTestUtil.VALID_PHONE_AMY)
            .withEmail(CommandTestUtil.VALID_EMAIL_AMY).withAddress(CommandTestUtil.VALID_ADDRESS_AMY)
            .withTags(CommandTestUtil.VALID_TAG_FRIEND).build();
    public static final Contact BOB = new ContactBuilder().withId(CommandTestUtil.VALID_ID_BOB)
            .withName(CommandTestUtil.VALID_NAME_BOB)
            .withPhone(CommandTestUtil.VALID_PHONE_BOB)
            .withEmail(CommandTestUtil.VALID_EMAIL_BOB).withAddress(CommandTestUtil.VALID_ADDRESS_BOB)
            .withTags(CommandTestUtil.VALID_TAG_HUSBAND, CommandTestUtil.VALID_TAG_FRIEND)
            .build();

    public static final String KEYWORD_MATCHING_MEIER = "Meier"; // A keyword that matches MEIER

    private TypicalContacts() {
    } // prevents instantiation

    /**
     * Returns an {@code AddressBook} with all the typical contacts.
     */
    public static AddressBook getTypicalAddressBook() {
        AddressBook ab = new AddressBook();
        for (Contact contact : TypicalContacts.getTypicalContacts()) {
            ab.addContact(contact);
        }
        for (Assignment assignment : TypicalAssignments.getTypicalAssignments()) {
            ab.addAssignment(assignment);
        }
        return ab;
    }

    public static List<Contact> getTypicalContacts() {
        return new ArrayList<>(Arrays.asList(TypicalContacts.ALICE, TypicalContacts.BENSON, TypicalContacts.CARL,
                TypicalContacts.DANIEL, TypicalContacts.ELLE, TypicalContacts.FIONA,
                TypicalContacts.GEORGE));
    }
}
