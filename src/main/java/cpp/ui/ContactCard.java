package cpp.ui;

import java.util.Comparator;

import cpp.model.contact.Contact;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

/**
 * An UI component that displays information of a {@code Contact}.
 *
 * @author ZSYUYIIE
 */
public class ContactCard extends UiPart<Region> {

    private static final String FXML = "ContactListCard.fxml";

    /**
     * Note: Certain keywords such as "location" and "resources" are reserved
     * keywords in JavaFX.
     * As a consequence, UI elements' variable names cannot be set to such keywords
     * or an exception will be thrown by JavaFX during runtime.
     *
     * @see <a href="https://github.com/se-edu/addressbook-level4/issues/336">The
     *      issue on AddressBook level 4</a>
     */

    public final Contact contact;

    @FXML
    private HBox cardPane;
    @FXML
    private Label name;
    @FXML
    private Label id;
    @FXML
    private Label phone;
    @FXML
    private Label address;
    @FXML
    private Label email;
    @FXML
    private FlowPane tags;
    @FXML
    private Tooltip nameTooltip;

    /**
     * Creates a {@code ContactCode} with the given {@code Contact} and index to
     * display.
     */
    public ContactCard(Contact contact, int displayedIndex) {
        super(ContactCard.FXML);
        this.contact = contact;
        this.id.setText(displayedIndex + ". ");
        this.name.setText(contact.getName().fullName);
        this.phone.setText(contact.getPhone().value);
        this.address.setText(contact.getAddress().value);
        this.email.setText(contact.getEmail().value);
        this.nameTooltip.setText(contact.getName().fullName);
        contact.getTags().stream()
                .sorted(Comparator.comparing(tag -> tag.tagName))
                .forEach(tag -> this.tags.getChildren().add(new Label(tag.tagName)));
    }
}
