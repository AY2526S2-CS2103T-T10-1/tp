package cpp.ui;

import java.util.List;

import cpp.model.classgroup.ClassGroup;
import cpp.model.contact.Contact;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

/**
 * UI component that displays full details for a single class group and its
 * assigned contacts.
 */
public class UniqueClassGroupView extends UiPart<Region> {
    private static final String FXML = "UniqueClassGroupView.fxml";

    @FXML
    private Label classGroupName;

    @FXML
    private Label numContacts;

    @FXML
    private StackPane contactListPlaceholder;

    private ContactListPanel contactListPanel;

    public UniqueClassGroupView() {
        super(UniqueClassGroupView.FXML);
    }

    /**
     * Sets the class group to display in this view, along with the list of contacts
     * assigned to it.
     */
    public void setClassGroup(ClassGroup classGroup, List<Contact> contacts) {
        this.classGroupName.setText("Class: " + classGroup.getName().toString());
        this.numContacts.setText("Number of Contacts: " + contacts.size());

        ObservableList<Contact> observableContacts = FXCollections.observableArrayList(contacts);
        if (this.contactListPanel != null) {
            this.contactListPlaceholder.getChildren().clear();
        }
        this.contactListPanel = new ContactListPanel(observableContacts);
        this.contactListPlaceholder.getChildren().add(this.contactListPanel.getRoot());
    }
}
