package cpp.ui;

import java.util.Objects;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;

/**
 * A ui for the status bar that is displayed at the header of the application.
 */
public class ResultDisplay extends UiPart<Region> {

    private static final String FXML = "ResultDisplay.fxml";
    private static final double MIN_HEIGHT = 100;
    private static final double MAX_HEIGHT = 200;
    private static final double TEXTAREA_VERTICAL_PADDING = 28;
    private static final double TEXTAREA_HORIZONTAL_PADDING = 24;

    @FXML
    private TextArea resultDisplay;

    /**
     * Creates a {@code ResultDisplay} and wires automatic height adjustment
     * listeners.
     */
    public ResultDisplay() {
        super(ResultDisplay.FXML);
        this.resultDisplay.textProperty().addListener((observable, oldValue, newValue) -> this.adjustHeight());
        this.resultDisplay.widthProperty().addListener((observable, oldValue, newValue) -> this.adjustHeight());
        this.resultDisplay.fontProperty().addListener((observable, oldValue, newValue) -> this.adjustHeight());

        // Defer initial sizing until layout pass provides a valid width.
        Platform.runLater(this::adjustHeight);
    }

    /**
     * Updates the feedback text shown to the user.
     *
     * @param feedbackToUser feedback message to display
     */
    public void setFeedbackToUser(String feedbackToUser) {
        Objects.requireNonNull(feedbackToUser);
        this.resultDisplay.setText(feedbackToUser);
        this.adjustHeight();
    }

    private void adjustHeight() {
        Platform.runLater(() -> {
            double textAreaWidth = this.resultDisplay.getWidth();
            if (textAreaWidth <= 0) {
                return;
            }

            String content = this.resultDisplay.getText();
            Text helper = new Text((content == null || content.isEmpty()) ? " " : content);
            helper.setFont(this.resultDisplay.getFont());
            helper.setWrappingWidth(Math.max(0, textAreaWidth - ResultDisplay.TEXTAREA_HORIZONTAL_PADDING));

            double contentHeight = helper.getLayoutBounds().getHeight();
            double targetHeight = ResultDisplay.clamp(contentHeight + ResultDisplay.TEXTAREA_VERTICAL_PADDING,
                    ResultDisplay.MIN_HEIGHT, ResultDisplay.MAX_HEIGHT);

            this.resultDisplay.setMinHeight(targetHeight);
            this.resultDisplay.setPrefHeight(targetHeight);
            this.resultDisplay.setMaxHeight(targetHeight);

            Region root = this.getRoot();
            root.setMinHeight(targetHeight);
            root.setPrefHeight(targetHeight);
            root.setMaxHeight(targetHeight);
        });
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

}
