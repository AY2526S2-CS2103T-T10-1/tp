package cpp.ui;

import cpp.model.assignment.ContactAssignmentWithContact;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;

/**
 * Panel containing the list of contact-assignment entries for an assignment.
 */
public class ContactAssignmentContactListPanel extends UiPart<Region> {
    private static final String FXML = "ContactAssignmentContactListPanel.fxml";

    @FXML
    private ListView<ContactAssignmentWithContact> contactAssignmentListView;

    /**
     * Creates a {@code ContactAssignmentListPanel} with the given
     * {@code ObservableList} of {@code ContactAssignmentWithContact}.
     */
    public ContactAssignmentContactListPanel(ObservableList<ContactAssignmentWithContact> cas) {
        super(ContactAssignmentContactListPanel.FXML);
        this.contactAssignmentListView.setItems(cas);
        Label caContactPlaceholder = new Label("No contacts allocated to this assignment.");
        caContactPlaceholder.setStyle("-fx-text-fill: #b4b4b4;");
        this.contactAssignmentListView.setPlaceholder(caContactPlaceholder);
        this.contactAssignmentListView.setCellFactory(listView -> new ContactAssignmentContactListViewCell());
    }

    class ContactAssignmentContactListViewCell extends ListCell<ContactAssignmentWithContact> {
        @Override
        protected void updateItem(ContactAssignmentWithContact caWithContact, boolean empty) {
            super.updateItem(caWithContact, empty);
            if (empty || caWithContact == null) {
                this.setGraphic(null);
                this.setText(null);
            } else {
                this.setGraphic(new ContactAssignmentContactCard(caWithContact, this.getIndex() + 1).getRoot());
            }
        }
    }
}
