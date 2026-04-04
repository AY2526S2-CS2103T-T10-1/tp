package cpp.ui;

import java.time.format.DateTimeFormatter;

import cpp.model.assignment.Assignment;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

/**
 * An UI component that displays information of an {@code Assignment}.
 *
 * @author ZSYUYIIE
 */
public class AssignmentCard extends UiPart<Region> {

    private static final String FXML = "AssignmentListCard.fxml";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

    public final Assignment assignment;

    @FXML
    private HBox cardPane;
    @FXML
    private Label name;
    @FXML
    private Label id;
    @FXML
    private Label deadline;
    @FXML
    private Tooltip nameTooltip;

    /**
     * Creates an {@code AssignmentCard} with the given {@code Assignment} and index
     * to display.
     */
    public AssignmentCard(Assignment assignment, int displayedIndex) {
        super(AssignmentCard.FXML);
        this.assignment = assignment;
        this.id.setText(displayedIndex + ". ");
        this.name.setText(assignment.getName().fullName);
        this.deadline.setText(assignment.getDeadline().format(AssignmentCard.FORMATTER));
        this.nameTooltip.setText(assignment.getName().fullName);
    }
}
