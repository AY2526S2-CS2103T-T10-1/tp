package cpp.testutil;

import java.util.Set;

import cpp.logic.commands.AddContactCommand;
import cpp.logic.commands.EditCommand.EditContactDescriptor;
import cpp.logic.parser.CliSyntax;
import cpp.model.contact.Contact;
import cpp.model.tag.Tag;

/**
 * A utility class for Contact.
 */
public class ContactUtil {

    /**
     * Returns an add command string for adding the {@code contact}.
     */
    public static String getAddContactCommand(Contact contact) {
        return AddContactCommand.COMMAND_WORD + " " + ContactUtil.getContactDetails(contact);
    }

    /**
     * Returns the part of command string for the given {@code contact}'s details.
     */
    public static String getContactDetails(Contact contact) {
        StringBuilder sb = new StringBuilder();
        sb.append(CliSyntax.PREFIX_NAME + contact.getName().fullName + " ");
        sb.append(CliSyntax.PREFIX_PHONE + contact.getPhone().value + " ");
        sb.append(CliSyntax.PREFIX_EMAIL + contact.getEmail().value + " ");
        sb.append(CliSyntax.PREFIX_ADDRESS + contact.getAddress().value + " ");
        contact.getTags().stream().forEach(
                s -> sb.append(CliSyntax.PREFIX_TAG + s.tagName + " "));
        return sb.toString();
    }

    /**
     * Returns the part of command string for the given
     * {@code EditContactDescriptor}'s details.
     */
    public static String getEditContactDescriptorDetails(EditContactDescriptor descriptor) {
        StringBuilder sb = new StringBuilder();
        descriptor.getName().ifPresent(name -> sb.append(CliSyntax.PREFIX_NAME).append(name.fullName).append(" "));
        descriptor.getPhone().ifPresent(phone -> sb.append(CliSyntax.PREFIX_PHONE).append(phone.value).append(" "));
        descriptor.getEmail().ifPresent(email -> sb.append(CliSyntax.PREFIX_EMAIL).append(email.value).append(" "));
        descriptor.getAddress()
                .ifPresent(address -> sb.append(CliSyntax.PREFIX_ADDRESS).append(address.value).append(" "));
        if (descriptor.getTags().isPresent()) {
            Set<Tag> tags = descriptor.getTags().get();
            if (tags.isEmpty()) {
                sb.append(CliSyntax.PREFIX_TAG);
            } else {
                tags.forEach(s -> sb.append(CliSyntax.PREFIX_TAG).append(s.tagName).append(" "));
            }
        }
        return sb.toString();
    }
}
