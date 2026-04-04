package cpp.ui;

import cpp.logic.parser.ParserUtil;
import cpp.model.assignment.Assignment;
import cpp.model.assignment.ContactAssignment;
import cpp.model.assignment.ContactAssignmentWithAssignment;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

/**
 * UI component that displays information of a {@code ContactAssignment}
 * within a contact's context.
 */
public class ContactAssignmentAssignmentCard extends UiPart<Region> {

    private static final String FXML = "ContactAssignmentAssignmentCard.fxml";

    public final ContactAssignment contactAssignment;

    @FXML
    private HBox cardPane;
    @FXML
    private Label id;
    @FXML
    private Label assignmentName;
    @FXML
    private Label deadline;
    @FXML
    private Label submitted;
    @FXML
    private Label graded;
    @FXML
    private Label score;

    /**
     * Creates a {@code ContactAssignmentAssignmentCard} with the given
     * {@code ContactAssignmentWithAssignment}.
     */
    public ContactAssignmentAssignmentCard(ContactAssignmentWithAssignment caWithAssignment, int displayedIndex) {
        super(ContactAssignmentAssignmentCard.FXML);
        ContactAssignment ca = caWithAssignment.getContactAssignment();
        Assignment assignment = caWithAssignment.getAssignment();
        this.contactAssignment = ca;

        this.id.setText(displayedIndex + ". ");
        this.assignmentName.setText(assignment == null ? ca.getAssignmentId() : assignment.getName().toString());
        this.deadline.setText(assignment == null ? ""
                : "Deadline: " + assignment.getDeadline().format(ParserUtil.DATETIME_FORMATTER));

        if (ca.isSubmitted()) {
            this.submitted.setText("Submitted on " + ca.getSubmissionDate().format(ParserUtil.DATETIME_FORMATTER));
        } else {
            this.submitted.setText("Not Submitted");
        }

        if (ca.isGraded()) {
            String gradedText = "Graded on " + ca.getGradingDate().format(ParserUtil.DATETIME_FORMATTER);
            this.graded.setText(gradedText);
            this.score.setText(String.format("Score: %.1f", ca.getScore()));
        } else {
            this.graded.setText("Not Graded");
            this.score.setText("");
        }
    }
}
