package cpp.commons.core;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.logging.Level;

import cpp.commons.util.ToStringBuilder;

/**
 * Config values used by the app
 */
public class Config {

    public static final Path DEFAULT_CONFIG_FILE = Paths.get("config.json");

    // Config values customizable through config file
    private Level logLevel = Level.INFO;
    private Path userPrefsFilePath = Paths.get("preferences.json");

    public Level getLogLevel() {
        return this.logLevel;
    }

    public void setLogLevel(Level logLevel) {
        this.logLevel = logLevel;
    }

    public Path getUserPrefsFilePath() {
        return this.userPrefsFilePath;
    }

    public void setUserPrefsFilePath(Path userPrefsFilePath) {
        this.userPrefsFilePath = userPrefsFilePath;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Config)) {
            return false;
        }

        Config otherConfig = (Config) other;
        return Objects.equals(this.logLevel, otherConfig.logLevel)
                && Objects.equals(this.userPrefsFilePath, otherConfig.userPrefsFilePath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.logLevel, this.userPrefsFilePath);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("logLevel", this.logLevel)
                .add("userPrefsFilePath", this.userPrefsFilePath)
                .toString();
    }

}
