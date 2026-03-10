package cpp.logic.commands;

import java.util.Objects;

import cpp.commons.util.ToStringBuilder;

/**
 * Represents the result of a command execution.
 */
public class CommandResult {

    private final String feedbackToUser;

    /** The tab to be shown to the user: contacts, classes, assignments */
    private final String tab;

    /** Help information should be shown to the user. */
    private final boolean showHelp;

    /** The application should exit. */
    private final boolean exit;

    /**
     * Constructs a {@code CommandResult} with the specified fields.
     */
    public CommandResult(String feedbackToUser, boolean showHelp, boolean exit, String tab) {
        this.feedbackToUser = Objects.requireNonNull(feedbackToUser);
        this.showHelp = showHelp;
        this.exit = exit;
        this.tab = tab;
    }

    /**
     * Constructs a {@code CommandResult} with the specified {@code feedbackToUser},
     * and other fields set to their default value.
     */
    public CommandResult(String feedbackToUser) {
        this(feedbackToUser, false, false, null);
    }

    public CommandResult(String feedbackToUser, String tab) {
        this(feedbackToUser, false, false, tab);
    }

    public String getFeedbackToUser() {
        return this.feedbackToUser;
    }

    public String getTab() {
        return this.tab;
    }

    public boolean isTabNull() {
        return this.tab == null;
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
                && this.showHelp == otherCommandResult.showHelp
                && this.exit == otherCommandResult.exit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.feedbackToUser, this.showHelp, this.exit);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("feedbackToUser", this.feedbackToUser)
                .add("showHelp", this.showHelp)
                .add("exit", this.exit)
                .toString();
    }

}
