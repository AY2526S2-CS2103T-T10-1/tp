package cpp.testutil;

import cpp.model.AddressBook;
import cpp.model.assignment.Assignment;
import cpp.model.contact.Contact;

/**
 * A utility class to help with building Addressbook objects.
 * Example usage: <br>
 * {@code AddressBook ab = new AddressBookBuilder().withContact("John", "Doe").build();}
 */
public class AddressBookBuilder {

    private AddressBook addressBook;

    public AddressBookBuilder() {
        this.addressBook = new AddressBook();
    }

    public AddressBookBuilder(AddressBook addressBook) {
        this.addressBook = addressBook;
    }

    /**
     * Adds a new {@code Contact} to the {@code AddressBook} that we are building.
     */
    public AddressBookBuilder withContact(Contact contact) {
        this.addressBook.addContact(contact);
        return this;
    }

    /**
     * Adds a new {@code Assignment} to the {@code AddressBook} that we are
     * building.
     */
    public AddressBookBuilder withAssignment(Assignment assignment) {
        this.addressBook.addAssignment(assignment);
        return this;
    }

    public AddressBook build() {
        return this.addressBook;
    }
}
