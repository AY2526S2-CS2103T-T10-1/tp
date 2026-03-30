package cpp.ui;

import java.util.List;

import cpp.model.assignment.ContactAssignmentWithAssignment;
import cpp.model.contact.Contact;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

/**
 * UI component that displays full details for a single contact and related
 * assignment data.
 */
public class UniqueContactView extends UiPart<Region> {
    private static final String FXML = "UniqueContactView.fxml";

    @FXML
    private Label contactName;

    @FXML
    private Label contactPhone;

    @FXML
    private Label contactEmail;

    @FXML
    private Label contactAddress;

    @FXML
    private StackPane contactAssignmentsPlaceholder;

    private ContactAssignmentAssignmentListPanel assignmentListPanel;

    public UniqueContactView() {
        super(UniqueContactView.FXML);
    }

    /**
     * Sets the contact details shown in this view.
     */
    public void setContact(Contact contact) {
        this.contactName.setText(contact.getName().fullName);
        this.contactPhone.setText(contact.getPhone().value);
        this.contactEmail.setText(contact.getEmail().value);
        this.contactAddress.setText(contact.getAddress().value);
    }

    /**
     * Sets the contact details and related assignment data.
     */
    public void setContact(Contact contact, List<ContactAssignmentWithAssignment> cas) {
        this.contactName.setText(contact.getName().fullName);
        this.contactPhone.setText(contact.getPhone().value);
        this.contactEmail.setText(contact.getEmail().value);
        this.contactAddress.setText(contact.getAddress().value);

        ObservableList<ContactAssignmentWithAssignment> observableCas = FXCollections
                .observableArrayList(cas);
        if (this.assignmentListPanel != null) {
            this.contactAssignmentsPlaceholder.getChildren().clear();
        }
        this.assignmentListPanel = new ContactAssignmentAssignmentListPanel(observableCas);
        this.contactAssignmentsPlaceholder.getChildren().add(this.assignmentListPanel.getRoot());
    }
}
