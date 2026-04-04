package cpp.ui;

import cpp.model.assignment.ContactAssignmentWithAssignment;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;

/**
 * Panel containing the list of assignment entries for a contact.
 */
public class ContactAssignmentAssignmentListPanel extends UiPart<Region> {
    private static final String FXML = "ContactAssignmentContactListPanel.fxml";

    @FXML
    private ListView<ContactAssignmentWithAssignment> contactAssignmentListView;

    /**
     * Creates a {@code ContactAssignmentAssignmentListPanel} with the given
     * {@code ObservableList} of {@code ContactAssignmentWithAssignment}.
     */
    public ContactAssignmentAssignmentListPanel(ObservableList<ContactAssignmentWithAssignment> cas) {
        super(ContactAssignmentAssignmentListPanel.FXML);
        this.contactAssignmentListView.setItems(cas);
        Label caAssignmentPlaceholder = new Label("No assignments found.");
        caAssignmentPlaceholder.setStyle("-fx-text-fill: #b4b4b4;");
        this.contactAssignmentListView.setPlaceholder(caAssignmentPlaceholder);
        this.contactAssignmentListView.setCellFactory(listView -> new ContactAssignmentAssignmentListViewCell());
    }

    class ContactAssignmentAssignmentListViewCell extends ListCell<ContactAssignmentWithAssignment> {
        @Override
        protected void updateItem(ContactAssignmentWithAssignment caWithAssignment, boolean empty) {
            super.updateItem(caWithAssignment, empty);
            if (empty || caWithAssignment == null) {
                this.setGraphic(null);
                this.setText(null);
            } else {
                this.setGraphic(new ContactAssignmentAssignmentCard(caWithAssignment, this.getIndex() + 1).getRoot());
            }
        }
    }
}
