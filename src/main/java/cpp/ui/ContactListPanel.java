package cpp.ui;

import cpp.model.contact.Contact;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;

/**
 * Panel containing the list of contacts.
 */
public class ContactListPanel extends UiPart<Region> {
    private static final String FXML = "ContactListPanel.fxml";

    @FXML
    private ListView<Contact> contactListView;

    /**
     * Creates a {@code ContactListPanel} with the given {@code ObservableList}.
     */
    public ContactListPanel(ObservableList<Contact> contactList) {
        super(ContactListPanel.FXML);
        this.contactListView.setItems(contactList);
        Label contactPlaceholder = new Label("No contacts found.");
        contactPlaceholder.setStyle("-fx-text-fill: #b4b4b4;");
        this.contactListView.setPlaceholder(contactPlaceholder);
        this.contactListView.setCellFactory(listView -> new ContactListViewCell());
    }

    /**
     * Custom {@code ListCell} that displays the graphics of a {@code Contact} using
     * a {@code ContactCard}.
     */
    class ContactListViewCell extends ListCell<Contact> {
        @Override
        protected void updateItem(Contact contact, boolean empty) {
            super.updateItem(contact, empty);

            if (empty || contact == null) {
                this.setGraphic(null);
                this.setText(null);
            } else {
                this.setGraphic(new ContactCard(contact, this.getIndex() + 1).getRoot());
            }
        }
    }

}
