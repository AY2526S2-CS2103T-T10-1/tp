package cpp.storage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import cpp.commons.core.LogsCenter;
import cpp.commons.exceptions.DataLoadingException;
import cpp.commons.util.JsonUtil;
import cpp.model.ReadOnlyUserPrefs;
import cpp.model.UserPrefs;

/**
 * A class to access UserPrefs stored in the hard disk as a json file
 */
public class JsonUserPrefsStorage implements UserPrefsStorage {

    private static final Logger logger = LogsCenter.getLogger(JsonUserPrefsStorage.class);
    private Path filePath;

    public JsonUserPrefsStorage(Path filePath) {
        this.filePath = filePath;
    }

    @Override
    public Path getUserPrefsFilePath() {
        return this.filePath;
    }

    @Override
    public Optional<UserPrefs> readUserPrefs() throws DataLoadingException {
        return this.readUserPrefs(this.filePath);
    }

    /**
     * Similar to {@link #readUserPrefs()}
     *
     * @param prefsFilePath location of the data. Cannot be null.
     * @throws DataLoadingException if the file format is not as expected.
     */
    public Optional<UserPrefs> readUserPrefs(Path prefsFilePath) throws DataLoadingException {
        Optional<UserPrefs> prefs = JsonUtil.readJsonFile(prefsFilePath, UserPrefs.class);
        if (prefs.isPresent()) {
            int tz = prefs.get().getTimeZoneOffset();
            if (tz < -18 || tz > 18) {
                JsonUserPrefsStorage.logger.info("Invalid time zone offset in file: " + tz);
                throw new DataLoadingException(new IllegalArgumentException(
                        "Invalid time zone offset in file: " + tz));
            }
        }
        return prefs;
    }

    @Override
    public void saveUserPrefs(ReadOnlyUserPrefs userPrefs) throws IOException {
        JsonUtil.saveJsonFile(userPrefs, this.filePath);
    }

}
