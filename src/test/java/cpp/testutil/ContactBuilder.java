package cpp.testutil;

import java.util.HashSet;
import java.util.Set;

import cpp.model.contact.Address;
import cpp.model.contact.Contact;
import cpp.model.contact.ContactName;
import cpp.model.contact.Email;
import cpp.model.contact.Phone;
import cpp.model.tag.Tag;
import cpp.model.util.SampleDataUtil;

/**
 * A utility class to help with building Contact objects.
 */
public class ContactBuilder {

    public static final String DEFAULT_ID = "683f92b5-9e96-47bb-94cd-e8ede5523d95";
    public static final String DEFAULT_NAME = "Amy Bee";
    public static final String DEFAULT_PHONE = "85355255";
    public static final String DEFAULT_EMAIL = "amy@gmail.com";
    public static final String DEFAULT_ADDRESS = "123, Jurong West Ave 6, #08-111";

    private String id;
    private ContactName name;
    private Phone phone;
    private Email email;
    private Address address;
    private Set<Tag> tags;

    /**
     * Creates a {@code ContactBuilder} with the default details.
     */
    public ContactBuilder() {
        this.id = ContactBuilder.DEFAULT_ID;
        this.name = new ContactName(ContactBuilder.DEFAULT_NAME);
        this.phone = new Phone(ContactBuilder.DEFAULT_PHONE);
        this.email = new Email(ContactBuilder.DEFAULT_EMAIL);
        this.address = new Address(ContactBuilder.DEFAULT_ADDRESS);
        this.tags = new HashSet<>();
    }

    /**
     * Initializes the ContactBuilder with the data of {@code contactToCopy}.
     */
    public ContactBuilder(Contact contactToCopy) {
        this.id = contactToCopy.getId();
        this.name = contactToCopy.getName();
        this.phone = contactToCopy.getPhone();
        this.email = contactToCopy.getEmail();
        this.address = contactToCopy.getAddress();
        this.tags = new HashSet<>(contactToCopy.getTags());
    }

    /**
     * Sets the {@code id} of the {@code Contact} that we are building.
     */
    public ContactBuilder withId(String id) {
        this.id = id;
        return this;
    }

    /**
     * Sets the {@code Name} of the {@code Contact} that we are building.
     */
    public ContactBuilder withName(String name) {
        this.name = new ContactName(name);
        return this;
    }

    /**
     * Parses the {@code tags} into a {@code Set<Tag>} and set it to the
     * {@code Contact} that we are building.
     */
    public ContactBuilder withTags(String... tags) {
        this.tags = SampleDataUtil.getTagSet(tags);
        return this;
    }

    /**
     * Sets the {@code Address} of the {@code Contact} that we are building.
     */
    public ContactBuilder withAddress(String address) {
        this.address = new Address(address);
        return this;
    }

    /**
     * Sets the {@code Phone} of the {@code Contact} that we are building.
     */
    public ContactBuilder withPhone(String phone) {
        this.phone = new Phone(phone);
        return this;
    }

    /**
     * Sets the {@code Email} of the {@code Contact} that we are building.
     */
    public ContactBuilder withEmail(String email) {
        this.email = new Email(email);
        return this;
    }

    public Contact build() {
        return new Contact(this.id, this.name, this.phone, this.email, this.address, this.tags);
    }

}
