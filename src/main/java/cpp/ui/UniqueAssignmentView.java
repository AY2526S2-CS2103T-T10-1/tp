package cpp.ui;

import java.util.List;

import cpp.logic.parser.ParserUtil;
import cpp.model.assignment.Assignment;
import cpp.model.assignment.ContactAssignmentWithContact;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

/**
 * UI component that displays full details for a single assignment and its
 * per-contact data.
 */
public class UniqueAssignmentView extends UiPart<Region> {
    private static final String FXML = "UniqueAssignmentView.fxml";

    @FXML
    private Label assignmentName;

    @FXML
    private Label assignmentDeadline;

    @FXML
    private StackPane contactAssignmentsPlaceholder;

    private ContactAssignmentContactListPanel contactAssignmentListPanel;

    public UniqueAssignmentView() {
        super(UniqueAssignmentView.FXML);
    }

    /**
     * Sets the assignment to display in this view, along with the per-contact data
     * for the assignment. The per-contact data is provided as a list of
     * ContactAssignmentWithContact DTOs, which contain the contact assignment data
     * along with the associated contact data for display.
     */
    public void setAssignment(Assignment assignment,
            List<ContactAssignmentWithContact> cas) {
        this.assignmentName.setText("Assignment: " + assignment.getName().toString());
        this.assignmentDeadline
                .setText("Deadline: " + assignment.getDeadline().format(ParserUtil.DATETIME_FORMATTER));

        ObservableList<ContactAssignmentWithContact> observableCas = FXCollections
                .observableArrayList(cas);
        if (this.contactAssignmentListPanel != null) {
            this.contactAssignmentsPlaceholder.getChildren().clear();
        }
        this.contactAssignmentListPanel = new ContactAssignmentContactListPanel(observableCas);
        this.contactAssignmentsPlaceholder.getChildren().add(this.contactAssignmentListPanel.getRoot());
    }
}
