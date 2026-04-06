package cpp.ui;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import cpp.commons.core.GuiSettings;
import cpp.commons.core.LogsCenter;
import cpp.logic.Logic;
import cpp.logic.commands.CommandResult;
import cpp.logic.commands.CommandResult.ListView;
import cpp.logic.commands.CommandResult.ViewType;
import cpp.logic.commands.exceptions.CommandException;
import cpp.logic.parser.exceptions.ParseException;
import cpp.model.assignment.Assignment;
import cpp.model.assignment.ContactAssignmentWithAssignment;
import cpp.model.assignment.ContactAssignmentWithContact;
import cpp.model.classgroup.ClassGroup;
import cpp.model.contact.Contact;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * The Main Window. Provides the basic application layout containing
 * a menu bar and space where other JavaFX elements can be placed.
 */
public class MainWindow extends UiPart<Stage> {

    private static final String FXML = "MainWindow.fxml";

    private final Logger logger = LogsCenter.getLogger(this.getClass());

    private Stage primaryStage;
    private Logic logic;

    // Independent Ui parts residing in this Ui container
    private ContactListPanel contactListPanel;
    private AssignmentListPanel assignmentListPanel;
    private ClassGroupListPanel classGroupListPanel;

    private ResultDisplay resultDisplay;
    private HelpWindow helpWindow;
    private UniqueAssignmentView assignmentViewPanel;
    private UniqueContactView contactViewPanel;
    private UniqueClassGroupView classGroupViewPanel;
    private Map<ViewType, UiPart<?>> viewPanels = new EnumMap<>(ViewType.class);
    private ViewType currentViewType = ViewType.NONE;
    private Object currentViewPayload = null;

    @FXML
    private StackPane commandBoxPlaceholder;

    @FXML
    private MenuItem helpMenuItem;

    @FXML
    private StackPane contactListPanelPlaceholder;

    @FXML
    private StackPane assignmentListPanelPlaceholder;

    @FXML
    private StackPane classListPanelPlaceholder;

    @FXML
    private StackPane viewPanelPlaceholder;

    @FXML
    private StackPane resultDisplayPlaceholder;

    @FXML
    private StackPane statusbarPlaceholder;

    @FXML
    private TabPane mainTabPane;

    @FXML
    private Tab contactsTab;

    @FXML
    private Tab classesTab;

    @FXML
    private Tab assignmentsTab;

    @FXML
    private Tab viewTab;

    /**
     * Creates a {@code MainWindow} with the given {@code Stage} and {@code Logic}.
     */
    public MainWindow(Stage primaryStage, Logic logic) {
        super(MainWindow.FXML, primaryStage);

        // Set dependencies
        this.primaryStage = primaryStage;
        this.logic = logic;

        // Configure the UI
        GuiSettings guiSettings = logic.getGuiSettings();
        if (guiSettings.getWindowHeight() < 700 || guiSettings.getWindowWidth() < 450) {
            logic.setGuiSettings(new GuiSettings());
        }
        this.setWindowDefaultSize(logic.getGuiSettings());

        this.setAccelerators();

        this.helpWindow = new HelpWindow();
    }

    public Stage getPrimaryStage() {
        return this.primaryStage;
    }

    private void setAccelerators() {
        this.setAccelerator(this.helpMenuItem, KeyCombination.valueOf("F1"));
    }

    /**
     * Sets the accelerator of a MenuItem.
     *
     * @param keyCombination the KeyCombination value of the accelerator
     */
    private void setAccelerator(MenuItem menuItem, KeyCombination keyCombination) {
        menuItem.setAccelerator(keyCombination);

        /*
         * TODO: the code below can be removed once the bug reported here
         * https://bugs.openjdk.java.net/browse/JDK-8131666
         * is fixed in later version of SDK.
         *
         * According to the bug report, TextInputControl (TextField, TextArea) will
         * consume function-key events. Because CommandBox contains a TextField, and
         * ResultDisplay contains a TextArea, thus some accelerators (e.g F1) will
         * not work when the focus is in them because the key event is consumed by
         * the TextInputControl(s).
         *
         * For now, we add following event filter to capture such key events and open
         * help window purposely so to support accelerators even when focus is
         * in CommandBox or ResultDisplay.
         */
        this.getRoot().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getTarget() instanceof TextInputControl && keyCombination.match(event)) {
                menuItem.getOnAction().handle(new ActionEvent());
                event.consume();
            }
        });
    }

    /**
     * Fills up all the placeholders of this window.
     */
    void fillInnerParts() {
        this.contactListPanel = new ContactListPanel(this.logic.getFilteredContactList());
        this.assignmentListPanel = new AssignmentListPanel(this.logic.getFilteredAssignmentList());
        this.classGroupListPanel = new ClassGroupListPanel(this.logic.getFilteredClassGroupList());

        this.contactListPanelPlaceholder.getChildren().add(this.contactListPanel.getRoot());
        this.assignmentListPanelPlaceholder.getChildren().add(this.assignmentListPanel.getRoot());
        this.classListPanelPlaceholder.getChildren().add(this.classGroupListPanel.getRoot());

        // set up assignment view panel inside the view tab placeholder; hide tab
        // initially
        this.assignmentViewPanel = new UniqueAssignmentView();
        this.viewPanelPlaceholder.getChildren().add(this.assignmentViewPanel.getRoot());

        // set up contact view panel inside the view tab placeholder; hide tab initially
        this.contactViewPanel = new UniqueContactView();

        // set up class group view panel inside the view tab placeholder
        this.classGroupViewPanel = new UniqueClassGroupView();

        // register known view panels
        this.viewPanels.put(ViewType.CONTACT, this.contactViewPanel);
        this.viewPanels.put(ViewType.ASSIGNMENT, this.assignmentViewPanel);
        this.viewPanels.put(ViewType.CLASSGROUP, this.classGroupViewPanel);

        // remove the view tab so it is hidden until a view command adds it back
        this.mainTabPane.getTabs().remove(this.viewTab);

        // When user manually clicks any other tab, clear viewed object and hide view
        // tab.
        this.mainTabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab != null && newTab != this.viewTab) {
                this.logic.clearViewState();
                this.hideViewTab();
            }
        });

        // Listen to the central view state so unique views update reactively.
        this.logic.getViewStateProperty().addListener((obs, oldState, newState) -> {
            if (newState == null || newState.getType() == ViewType.NONE) {
                this.hideViewTab();
                this.currentViewType = ViewType.NONE;
                this.currentViewPayload = null;
                return;
            }

            this.currentViewType = newState.getType();
            this.currentViewPayload = newState.getPayload();
            this.showViewForCurrentState();
        });

        this.resultDisplay = new ResultDisplay();
        this.resultDisplayPlaceholder.getChildren().add(this.resultDisplay.getRoot());

        StatusBarFooter statusBarFooter = new StatusBarFooter(this.logic.getAddressBookFilePath());
        this.statusbarPlaceholder.getChildren().add(statusBarFooter.getRoot());

        CommandBox commandBox = new CommandBox(this::executeCommand);
        this.commandBoxPlaceholder.getChildren().add(commandBox.getRoot());
    }

    /**
     * Sets the default size based on {@code guiSettings}.
     */
    private void setWindowDefaultSize(GuiSettings guiSettings) {
        this.primaryStage.setHeight(guiSettings.getWindowHeight());
        this.primaryStage.setWidth(guiSettings.getWindowWidth());
        if (guiSettings.getWindowCoordinates() != null) {
            this.primaryStage.setX(guiSettings.getWindowCoordinates().getX());
            this.primaryStage.setY(guiSettings.getWindowCoordinates().getY());
        }
    }

    /**
     * Opens the help window or focuses on it if it's already opened.
     */
    @FXML
    public void handleHelp() {
        if (!this.helpWindow.isShowing()) {
            this.helpWindow.show();
        } else {
            this.helpWindow.focus();
        }
    }

    void show() {
        this.primaryStage.show();
    }

    /**
     * Closes the application.
     */
    @FXML
    private void handleExit() {
        GuiSettings guiSettings = new GuiSettings(this.primaryStage.getWidth(), this.primaryStage.getHeight(),
                (int) this.primaryStage.getX(), (int) this.primaryStage.getY());
        this.logic.setGuiSettings(guiSettings);
        this.helpWindow.hide();
        this.primaryStage.hide();
    }

    private void handleListCommand(CommandResult commandResult) {
        ListView listView = commandResult.getListView();

        switch (listView) {
        case CONTACTS:
            this.mainTabPane.getSelectionModel().select(this.contactsTab);
            this.hideViewTab();
            break;
        case ASSIGNMENTS:
            this.mainTabPane.getSelectionModel().select(this.assignmentsTab);
            this.hideViewTab();
            break;
        case CLASSGROUPS:
            this.mainTabPane.getSelectionModel().select(this.classesTab);
            this.hideViewTab();
            break;
        case NONE:
        default:
            // Do nothing when list view is NONE
            break;
        }
    }

    public ContactListPanel getContactListPanel() {
        return this.contactListPanel;
    }

    public AssignmentListPanel getAssignmentListPanel() {
        return this.assignmentListPanel;
    }

    public ClassGroupListPanel getClassGroupListPanel() {
        return this.classGroupListPanel;
    }

    /**
     * Show the currently-selected view in the view tab.
     */
    private void showViewForCurrentState() {
        if (this.currentViewType == null || this.currentViewType == ViewType.NONE) {
            this.hideViewTab();
            return;
        }

        switch (this.currentViewType) {
        case ASSIGNMENT:
            Assignment ass = (Assignment) this.currentViewPayload;
            if (ass != null) {
                List<ContactAssignmentWithContact> cas = this.logic
                        .getContactAssignmentsWithContactsForAssignment(ass);
                this.assignmentViewPanel.setAssignment(ass, cas);
                this.viewPanelPlaceholder.getChildren().setAll(this.assignmentViewPanel.getRoot());
                if (!this.mainTabPane.getTabs().contains(this.viewTab)) {
                    this.mainTabPane.getTabs().add(this.viewTab);
                }
                this.mainTabPane.getSelectionModel().select(this.viewTab);
            }
            break;
        case CONTACT:
            Contact ct = (Contact) this.currentViewPayload;
            if (ct != null) {
                List<ContactAssignmentWithAssignment> cas = this.logic
                        .getContactAssignmentsWithAssignmentsForContact(ct);
                List<ClassGroup> classGroups = this.logic.getClassGroupsForContact(ct);
                this.contactViewPanel.setContact(ct, cas, classGroups);
                this.viewPanelPlaceholder.getChildren().setAll(this.contactViewPanel.getRoot());
                if (!this.mainTabPane.getTabs().contains(this.viewTab)) {
                    this.mainTabPane.getTabs().add(this.viewTab);
                }
                this.mainTabPane.getSelectionModel().select(this.viewTab);
            }
            break;
        case CLASSGROUP:
            ClassGroup cg = (ClassGroup) this.currentViewPayload;
            if (cg != null) {
                List<Contact> cts = this.logic.getContactsInClassGroup(cg);
                this.classGroupViewPanel.setClassGroup(cg, cts);
                this.viewPanelPlaceholder.getChildren().setAll(this.classGroupViewPanel.getRoot());
                if (!this.mainTabPane.getTabs().contains(this.viewTab)) {
                    this.mainTabPane.getTabs().add(this.viewTab);
                }
                this.mainTabPane.getSelectionModel().select(this.viewTab);
            }
            break;
        default:
            this.hideViewTab();
            break;
        }
    }

    private void hideViewTab() {
        if (this.mainTabPane.getTabs().contains(this.viewTab)) {
            this.mainTabPane.getTabs().remove(this.viewTab);
        }
    }

    private void refreshCurrentViewIfVisible() {
        if (!this.mainTabPane.getTabs().contains(this.viewTab)) {
            return;
        }

        switch (this.currentViewType) {
        case ASSIGNMENT:
            if (this.currentViewPayload instanceof Assignment) {
                Assignment ass = (Assignment) this.currentViewPayload;
                List<ContactAssignmentWithContact> cas = this.logic
                        .getContactAssignmentsWithContactsForAssignment(ass);
                this.assignmentViewPanel.setAssignment(ass, cas);
            }
            break;
        case CONTACT:
            if (this.currentViewPayload instanceof Contact) {
                Contact ct = (Contact) this.currentViewPayload;
                List<ContactAssignmentWithAssignment> cas = this.logic
                        .getContactAssignmentsWithAssignmentsForContact(ct);
                List<ClassGroup> classGroups = this.logic.getClassGroupsForContact(ct);
                this.contactViewPanel.setContact(ct, cas, classGroups);
            }
            break;
        case CLASSGROUP:
            if (this.currentViewPayload instanceof ClassGroup) {
                ClassGroup cg = (ClassGroup) this.currentViewPayload;
                List<Contact> cts = this.logic.getContactsInClassGroup(cg);
                this.classGroupViewPanel.setClassGroup(cg, cts);
            }
            break;
        default:
            break;
        }
    }

    /**
     * Executes the command and returns the result.
     *
     * @see cpp.logic.Logic#execute(String)
     */
    private CommandResult executeCommand(String commandText) throws CommandException, ParseException {
        try {
            CommandResult commandResult = this.logic.execute(commandText);
            this.logger.info("Result: " + commandResult.getFeedbackToUser());
            this.resultDisplay.setFeedbackToUser(commandResult.getFeedbackToUser());

            if (commandResult.isShowHelp()) {
                this.handleHelp();
            }

            if (commandResult.isExit()) {
                this.handleExit();
            }

            this.handleListCommand(commandResult);

            // Refresh the currently-visible unique view after any command,
            // so changes like submit/unsubmit or grade/ungrade are shown.
            this.refreshCurrentViewIfVisible();

            return commandResult;
        } catch (CommandException | ParseException e) {
            this.logger.info("An error occurred while executing command: " + commandText);
            this.resultDisplay.setFeedbackToUser(e.getMessage());
            throw e;
        }
    }
}
