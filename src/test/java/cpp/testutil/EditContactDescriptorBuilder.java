package cpp.testutil;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import cpp.logic.commands.EditContactCommand.EditContactDescriptor;
import cpp.model.contact.Address;
import cpp.model.contact.Contact;
import cpp.model.contact.ContactName;
import cpp.model.contact.Email;
import cpp.model.contact.Phone;
import cpp.model.tag.Tag;

/**
 * A utility class to help with building EditContactDescriptor objects.
 */
public class EditContactDescriptorBuilder {

    private EditContactDescriptor descriptor;

    public EditContactDescriptorBuilder() {
        this.descriptor = new EditContactDescriptor();
    }

    public EditContactDescriptorBuilder(EditContactDescriptor descriptor) {
        this.descriptor = new EditContactDescriptor(descriptor);
    }

    /**
     * Returns an {@code EditContactDescriptor} with fields containing
     * {@code contact}'s details
     */
    public EditContactDescriptorBuilder(Contact contact) {
        this.descriptor = new EditContactDescriptor();
        this.descriptor.setName(contact.getName());
        this.descriptor.setPhone(contact.getPhone());
        this.descriptor.setEmail(contact.getEmail());
        this.descriptor.setAddress(contact.getAddress());
        this.descriptor.setTags(contact.getTags());
    }

    /**
     * Sets the {@code Name} of the {@code EditContactDescriptor} that we are
     * building.
     */
    public EditContactDescriptorBuilder withName(String name) {
        this.descriptor.setName(new ContactName(name));
        return this;
    }

    /**
     * Sets the {@code Phone} of the {@code EditContactDescriptor} that we are
     * building.
     */
    public EditContactDescriptorBuilder withPhone(String phone) {
        this.descriptor.setPhone(new Phone(phone));
        return this;
    }

    /**
     * Sets the {@code Email} of the {@code EditContactDescriptor} that we are
     * building.
     */
    public EditContactDescriptorBuilder withEmail(String email) {
        this.descriptor.setEmail(new Email(email));
        return this;
    }

    /**
     * Sets the {@code Address} of the {@code EditContactDescriptor} that we are
     * building.
     */
    public EditContactDescriptorBuilder withAddress(String address) {
        this.descriptor.setAddress(new Address(address));
        return this;
    }

    /**
     * Parses the {@code tags} into a {@code Set<Tag>} and set it to the
     * {@code EditContactDescriptor}
     * that we are building.
     */
    public EditContactDescriptorBuilder withTags(String... tags) {
        Set<Tag> tagSet = Stream.of(tags).map(Tag::new).collect(Collectors.toSet());
        this.descriptor.setTags(tagSet);
        return this;
    }

    public EditContactDescriptor build() {
        return this.descriptor;
    }
}
