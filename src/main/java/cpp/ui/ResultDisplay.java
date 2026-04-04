package cpp.ui;

import java.util.Objects;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;

/**
 * A ui for the status bar that is displayed at the header of the application.
 */
public class ResultDisplay extends UiPart<Region> {

    private static final String FXML = "ResultDisplay.fxml";

    @FXML
    private TextArea resultDisplay;

    /**
     * Creates a {@code ResultDisplay} and wires automatic height adjustment
     * listeners.
     */
    public ResultDisplay() {
        super(ResultDisplay.FXML);
        this.resultDisplay.setWrapText(true);
        this.resultDisplay.setEditable(false);

        // Prevent any typing/paste/cut/backspace/delete while still allowing
        // selection and copy.
        this.resultDisplay.addEventFilter(KeyEvent.KEY_TYPED, event -> event.consume());
        this.resultDisplay.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            KeyCode code = event.getCode();
            // Block navigation keys that modify content
            if (code == KeyCode.BACK_SPACE || code == KeyCode.DELETE) {
                event.consume();
            }
            // Block common editing shortcuts (paste, cut, Ctrl+Backspace)
            if (event.isControlDown()) {
                if (code == KeyCode.V || code == KeyCode.X || code == KeyCode.BACK_SPACE) {
                    event.consume();
                }
            }
        });

    }

    /**
     * Updates the feedback text shown to the user.
     *
     * @param feedbackToUser feedback message to display
     */
    public void setFeedbackToUser(String feedbackToUser) {
        Objects.requireNonNull(feedbackToUser);
        this.resultDisplay.setText(feedbackToUser);
    }

}
