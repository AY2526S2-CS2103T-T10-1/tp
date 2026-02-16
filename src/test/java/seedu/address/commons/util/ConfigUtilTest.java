package seedu.address.commons.util;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.logging.Level;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.core.Config;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.testutil.Assert;

public class ConfigUtilTest {

    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "ConfigUtilTest");

    @TempDir
    public Path tempDir;

    @Test
    public void read_null_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class, () -> this.read(null));
    }

    @Test
    public void read_missingFile_emptyResult() throws DataLoadingException {
        Assertions.assertFalse(this.read("NonExistentFile.json").isPresent());
    }

    @Test
    public void read_notJsonFormat_exceptionThrown() {
        Assert.assertThrows(DataLoadingException.class, () -> this.read("NotJsonFormatConfig.json"));
    }

    @Test
    public void read_fileInOrder_successfullyRead() throws DataLoadingException {

        Config expected = this.getTypicalConfig();

        Config actual = this.read("TypicalConfig.json").get();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void read_valuesMissingFromFile_defaultValuesUsed() throws DataLoadingException {
        Config actual = this.read("EmptyConfig.json").get();
        Assertions.assertEquals(new Config(), actual);
    }

    @Test
    public void read_extraValuesInFile_extraValuesIgnored() throws DataLoadingException {
        Config expected = this.getTypicalConfig();
        Config actual = this.read("ExtraValuesConfig.json").get();

        Assertions.assertEquals(expected, actual);
    }

    private Config getTypicalConfig() {
        Config config = new Config();
        config.setLogLevel(Level.INFO);
        config.setUserPrefsFilePath(Paths.get("preferences.json"));
        return config;
    }

    private Optional<Config> read(String configFileInTestDataFolder) throws DataLoadingException {
        Path configFilePath = this.addToTestDataPathIfNotNull(configFileInTestDataFolder);
        return ConfigUtil.readConfig(configFilePath);
    }

    @Test
    public void save_nullConfig_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class, () -> this.save(null, "SomeFile.json"));
    }

    @Test
    public void save_nullFile_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class, () -> this.save(new Config(), null));
    }

    @Test
    public void saveConfig_allInOrder_success() throws DataLoadingException, IOException {
        Config original = this.getTypicalConfig();

        Path configFilePath = this.tempDir.resolve("TempConfig.json");

        // Try writing when the file doesn't exist
        ConfigUtil.saveConfig(original, configFilePath);
        Config readBack = ConfigUtil.readConfig(configFilePath).get();
        Assertions.assertEquals(original, readBack);

        // Try saving when the file exists
        original.setLogLevel(Level.FINE);
        ConfigUtil.saveConfig(original, configFilePath);
        readBack = ConfigUtil.readConfig(configFilePath).get();
        Assertions.assertEquals(original, readBack);
    }

    private void save(Config config, String configFileInTestDataFolder) throws IOException {
        Path configFilePath = this.addToTestDataPathIfNotNull(configFileInTestDataFolder);
        ConfigUtil.saveConfig(config, configFilePath);
    }

    private Path addToTestDataPathIfNotNull(String configFileInTestDataFolder) {
        return configFileInTestDataFolder != null
                ? ConfigUtilTest.TEST_DATA_FOLDER.resolve(configFileInTestDataFolder)
                : null;
    }

}
