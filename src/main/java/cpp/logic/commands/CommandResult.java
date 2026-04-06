package cpp.logic.commands;

import java.util.Objects;

import cpp.commons.util.ToStringBuilder;

/**
 * Represents the result of a command execution.
 */
public class CommandResult {

    /**
     * Enum representing the list view to be displayed.
     */
    public enum ListView {
        CONTACTS, ASSIGNMENTS, CLASSGROUPS, NONE
    }

    /**
     * Enum representing a unique view to be shown.
     */
    public enum ViewType {
        CONTACT, ASSIGNMENT, CLASSGROUP, NONE
    }

    private final String feedbackToUser;

    /** The list view to be shown to the user. */
    private final ListView listView;

    /** The unique view to be shown (if any). */
    private final ViewType viewType;

    /** Help information should be shown to the user. */
    private final boolean showHelp;

    /** The application should exit. */
    private final boolean exit;

    /**
     * Constructs a {@code CommandResult} with the specified fields.
     */
    public CommandResult(String feedbackToUser, ListView listView, ViewType viewType, boolean showHelp,
            boolean exit) {
        this.feedbackToUser = Objects.requireNonNull(feedbackToUser);
        this.listView = Objects.requireNonNull(listView);
        this.viewType = Objects.requireNonNull(viewType);
        this.showHelp = showHelp;
        this.exit = exit;
    }

    /**
     * Constructs a {@code CommandResult} with the specified {@code feedbackToUser},
     * and other fields set to their default value. ListView defaults to NONE.
     */
    public CommandResult(String feedbackToUser) {
        this(feedbackToUser, ListView.NONE, ViewType.NONE, false, false);
    }

    /**
     * Constructs a {@code CommandResult} with the specified {@code feedbackToUser}
     * and {@code listView},
     * and other fields set to their default value.
     */
    public CommandResult(String feedbackToUser, ListView listView) {
        this(feedbackToUser, listView, ViewType.NONE, false, false);
    }

    /**
     * Constructs a CommandResult with a specific unique view type.
     */
    public CommandResult(String feedbackToUser, ViewType viewType) {
        this(feedbackToUser, ListView.NONE, viewType, false, false);
    }

    public String getFeedbackToUser() {
        return this.feedbackToUser;
    }

    public ListView getListView() {
        return this.listView;
    }

    public ViewType getViewType() {
        return this.viewType;
    }

    public boolean isShowHelp() {
        return this.showHelp;
    }

    public boolean isExit() {
        return this.exit;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof CommandResult)) {
            return false;
        }

        CommandResult otherCommandResult = (CommandResult) other;
        return this.feedbackToUser.equals(otherCommandResult.feedbackToUser)
                && this.listView == otherCommandResult.listView
                && this.viewType == otherCommandResult.viewType
                && this.showHelp == otherCommandResult.showHelp
                && this.exit == otherCommandResult.exit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.feedbackToUser, this.listView, this.viewType, this.showHelp, this.exit);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("feedbackToUser", this.feedbackToUser)
                .add("listView", this.listView)
                .add("viewType", this.viewType)
                .add("showHelp", this.showHelp)
                .add("exit", this.exit)
                .toString();
    }

}
