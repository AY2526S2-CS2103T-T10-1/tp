package cpp.ui;

import cpp.model.classgroup.ClassGroup;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

/**
 * An UI component that displays information of a {@code ClassGroup}.
 */
public class ClassGroupCard extends UiPart<Region> {

    private static final String FXML = "ClassGroupListCard.fxml";

    public final ClassGroup classGroup;

    @FXML
    private HBox cardPane;
    @FXML
    private Label name;
    @FXML
    private Label id;

    /**
     * Creates a {@code ClassGroupCard} with the given {@code ClassGroup} and index
     * to display.
     */
    public ClassGroupCard(ClassGroup classGroup, int displayedIndex) {
        super(ClassGroupCard.FXML);
        this.classGroup = classGroup;
        this.id.setText(displayedIndex + ". ");
        this.name.setText(classGroup.getName().fullName);
    }
}
