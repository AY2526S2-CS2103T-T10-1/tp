package cpp.model;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import cpp.commons.core.GuiSettings;

/**
 * Represents User's preferences.
 */
public class UserPrefs implements ReadOnlyUserPrefs {

    private GuiSettings guiSettings = new GuiSettings();
    private Path addressBookFilePath = Paths.get("data", "addressbook.json");

    /**
     * Creates a {@code UserPrefs} with default values.
     */
    public UserPrefs() {
    }

    /**
     * Creates a {@code UserPrefs} with the prefs in {@code userPrefs}.
     */
    public UserPrefs(ReadOnlyUserPrefs userPrefs) {
        this();
        this.resetData(userPrefs);
    }

    /**
     * Resets the existing data of this {@code UserPrefs} with {@code newUserPrefs}.
     */
    public void resetData(ReadOnlyUserPrefs newUserPrefs) {
        Objects.requireNonNull(newUserPrefs);
        this.setGuiSettings(newUserPrefs.getGuiSettings());
        this.setAddressBookFilePath(newUserPrefs.getAddressBookFilePath());
    }

    @Override
    public GuiSettings getGuiSettings() {
        return this.guiSettings;
    }

    public void setGuiSettings(GuiSettings guiSettings) {
        Objects.requireNonNull(guiSettings);
        this.guiSettings = guiSettings;
    }

    @Override
    public Path getAddressBookFilePath() {
        return this.addressBookFilePath;
    }

    public void setAddressBookFilePath(Path addressBookFilePath) {
        Objects.requireNonNull(addressBookFilePath);
        this.addressBookFilePath = addressBookFilePath;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof UserPrefs)) {
            return false;
        }

        UserPrefs otherUserPrefs = (UserPrefs) other;
        return this.guiSettings.equals(otherUserPrefs.guiSettings)
                && this.addressBookFilePath.equals(otherUserPrefs.addressBookFilePath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.guiSettings, this.addressBookFilePath);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Gui Settings : " + this.guiSettings);
        sb.append("\nLocal data file location : " + this.addressBookFilePath);
        return sb.toString();
    }

}
