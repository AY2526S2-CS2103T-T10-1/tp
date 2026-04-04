package cpp.ui;

import cpp.model.assignment.Assignment;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;

/**
 * Panel containing the list of assignments.
 */
public class AssignmentListPanel extends UiPart<Region> {
    private static final String FXML = "AssignmentListPanel.fxml";

    @FXML
    private ListView<Assignment> assignmentListView;

    /**
     * Creates an {@code AssignmentListPanel} with the given {@code ObservableList}.
     */
    public AssignmentListPanel(ObservableList<Assignment> assignmentList) {
        super(AssignmentListPanel.FXML);
        this.assignmentListView.setItems(assignmentList);
        Label assignmentPlaceholder = new Label("No assignments found.");
        assignmentPlaceholder.setStyle("-fx-text-fill: #b4b4b4;");
        this.assignmentListView.setPlaceholder(assignmentPlaceholder);
        this.assignmentListView.setCellFactory(listView -> new AssignmentListViewCell());
    }

    /**
     * Custom {@code ListCell} that displays the graphics of an {@code Assignment}
     * using an {@code AssignmentCard}.
     */
    class AssignmentListViewCell extends ListCell<Assignment> {
        @Override
        protected void updateItem(Assignment assignment, boolean empty) {
            super.updateItem(assignment, empty);

            if (empty || assignment == null) {
                this.setGraphic(null);
                this.setText(null);
            } else {
                this.setGraphic(new AssignmentCard(assignment, this.getIndex() + 1).getRoot());
            }
        }
    }

}
