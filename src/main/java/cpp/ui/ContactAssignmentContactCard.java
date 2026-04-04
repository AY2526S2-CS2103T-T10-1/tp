package cpp.ui;

import java.util.Comparator;

import cpp.logic.parser.ParserUtil;
import cpp.model.assignment.ContactAssignment;
import cpp.model.assignment.ContactAssignmentWithContact;
import cpp.model.contact.Contact;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

/**
 * UI component that displays information of a {@code ContactAssignment}.
 */
public class ContactAssignmentContactCard extends UiPart<Region> {

    private static final String FXML = "ContactAssignmentContactCard.fxml";

    public final ContactAssignment contactAssignment;

    @FXML
    private HBox cardPane;
    @FXML
    private Label id;
    @FXML
    private Label name;
    @FXML
    private Label phone;
    @FXML
    private Label email;
    @FXML
    private Label address;
    @FXML
    private FlowPane tags;
    @FXML
    private Label submitted;
    @FXML
    private Label graded;
    @FXML
    private Label score;

    /**
     * Creates a {@code ContactAssignmentCard} with the given
     * {@code ContactAssignment}
     * and the associated {@code Contact} for display.
     */
    public ContactAssignmentContactCard(ContactAssignmentWithContact caWithContact, int displayedIndex) {
        super(ContactAssignmentContactCard.FXML);
        ContactAssignment ca = caWithContact.getContactAssignment();
        Contact contact = caWithContact.getContact();
        this.contactAssignment = ca;
        this.id.setText(displayedIndex + ". ");
        this.name.setText(contact == null ? "<unknown>" : contact.getName().fullName);
        this.phone.setText(contact == null ? "" : contact.getPhone().value);
        this.email.setText(contact == null ? "" : contact.getEmail().value);
        this.address.setText(contact == null ? "" : contact.getAddress().value);
        if (contact != null) {
            contact.getTags().stream()
                    .sorted(Comparator.comparing(tag -> tag.tagName))
                    .forEach(tag -> this.tags.getChildren().add(new Label(tag.tagName)));
        }

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
