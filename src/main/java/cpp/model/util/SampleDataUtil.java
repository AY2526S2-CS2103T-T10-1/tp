package cpp.model.util;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import cpp.model.AddressBook;
import cpp.model.ReadOnlyAddressBook;
import cpp.model.contact.Address;
import cpp.model.contact.Contact;
import cpp.model.contact.ContactName;
import cpp.model.contact.Email;
import cpp.model.contact.Phone;
import cpp.model.tag.Tag;

/**
 * Contains utility methods for populating {@code AddressBook} with sample data.
 */
public class SampleDataUtil {
    // CHECKSTYLE.OFF: IndentationCheck - Auto-formatting is not working as expected
    public static Contact[] getSampleContacts() {
        return new Contact[] {
                new Contact(
                        new ContactName("Alex Yeoh"),
                        new Phone("87438807"),
                        new Email("alexyeoh@example.com"),
                        new Address("Blk 30 Geylang Street 29, #06-40"),
                        SampleDataUtil.getTagSet("friends")),
                new Contact(
                        new ContactName("Bernice Yu"),
                        new Phone("99272758"),
                        new Email("berniceyu@example.com"),
                        new Address("Blk 30 Lorong 3 Serangoon Gardens, #07-18"),
                        SampleDataUtil.getTagSet("colleagues", "friends")),
                new Contact(
                        new ContactName("Charlotte Oliveiro"),
                        new Phone("93210283"),
                        new Email("charlotte@example.com"),
                        new Address("Blk 11 Ang Mo Kio Street 74, #11-04"),
                        SampleDataUtil.getTagSet("neighbours")),
                new Contact(
                        new ContactName("David Li"),
                        new Phone("91031282"),
                        new Email("lidavid@example.com"),
                        new Address("Blk 436 Serangoon Gardens Street 26, #16-43"),
                        SampleDataUtil.getTagSet("family")),
                new Contact(
                        new ContactName("Irfan Ibrahim"),
                        new Phone("92492021"),
                        new Email("irfan@example.com"),
                        new Address("Blk 47 Tampines Street 20, #17-35"),
                        SampleDataUtil.getTagSet("classmates")),
                new Contact(
                        new ContactName("Roy Balakrishnan"),
                        new Phone("92624417"),
                        new Email("royb@example.com"),
                        new Address("Blk 45 Aljunied Street 85, #11-31"),
                        SampleDataUtil.getTagSet("colleagues"))
        };
    }

    public static ReadOnlyAddressBook getSampleAddressBook() {
        AddressBook sampleAb = new AddressBook();
        for (Contact sampleContact : SampleDataUtil.getSampleContacts()) {
            sampleAb.addContact(sampleContact);
        }
        return sampleAb;
    }

    /**
     * Returns a tag set containing the list of strings given.
     */
    public static Set<Tag> getTagSet(String... strings) {
        return Arrays.stream(strings)
                .map(Tag::new)
                .collect(Collectors.toSet());
    }

}
