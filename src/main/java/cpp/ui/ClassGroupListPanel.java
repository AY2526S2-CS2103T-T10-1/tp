package cpp.ui;

import cpp.model.classgroup.ClassGroup;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;

/**
 * Panel containing the list of class groups.
 */
public class ClassGroupListPanel extends UiPart<Region> {
    private static final String FXML = "ClassGroupListPanel.fxml";

    @FXML
    private ListView<ClassGroup> classGroupListView;

    /**
     * Creates a {@code ClassGroupListPanel} with the given {@code ObservableList}.
     */
    public ClassGroupListPanel(ObservableList<ClassGroup> classGroupList) {
        super(ClassGroupListPanel.FXML);
        this.classGroupListView.setItems(classGroupList);
        Label classGroupPlaceholder = new Label("No classes found.");
        classGroupPlaceholder.setStyle("-fx-text-fill: #b4b4b4;");
        this.classGroupListView.setPlaceholder(classGroupPlaceholder);
        this.classGroupListView.setCellFactory(listView -> new ClassGroupListViewCell());
    }

    /**
     * Custom {@code ListCell} that displays the graphics of a {@code ClassGroup}
     * using a {@code ClassGroupCard}.
     */
    class ClassGroupListViewCell extends ListCell<ClassGroup> {
        @Override
        protected void updateItem(ClassGroup classGroup, boolean empty) {
            super.updateItem(classGroup, empty);

            if (empty || classGroup == null) {
                this.setGraphic(null);
                this.setText(null);
            } else {
                this.setGraphic(new ClassGroupCard(classGroup, this.getIndex() + 1).getRoot());
            }
        }
    }

}
