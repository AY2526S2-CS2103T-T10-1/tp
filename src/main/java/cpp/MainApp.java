package cpp;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import cpp.commons.core.Config;
import cpp.commons.core.LogsCenter;
import cpp.commons.core.Version;
import cpp.commons.exceptions.DataLoadingException;
import cpp.commons.util.ConfigUtil;
import cpp.commons.util.StringUtil;
import cpp.logic.Logic;
import cpp.logic.LogicManager;
import cpp.model.AddressBook;
import cpp.model.Model;
import cpp.model.ModelManager;
import cpp.model.ReadOnlyAddressBook;
import cpp.model.ReadOnlyUserPrefs;
import cpp.model.UserPrefs;
import cpp.model.util.SampleDataUtil;
import cpp.storage.AddressBookStorage;
import cpp.storage.JsonAddressBookStorage;
import cpp.storage.JsonUserPrefsStorage;
import cpp.storage.Storage;
import cpp.storage.StorageManager;
import cpp.storage.UserPrefsStorage;
import cpp.ui.Ui;
import cpp.ui.UiManager;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Runs the application.
 */
public class MainApp extends Application {

    public static final Version VERSION = new Version(0, 2, 2, true);

    private static final Logger logger = LogsCenter.getLogger(MainApp.class);

    protected Ui ui;
    protected Logic logic;
    protected Storage storage;
    protected Model model;
    protected Config config;

    @Override
    public void init() throws Exception {
        MainApp.logger.info("=============================[ Initializing AddressBook ]===========================");
        super.init();

        AppParameters appParameters = AppParameters.parse(this.getParameters());
        this.config = this.initConfig(appParameters.getConfigPath());
        this.initLogging(this.config);

        UserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(this.config.getUserPrefsFilePath());
        UserPrefs userPrefs = this.initPrefs(userPrefsStorage);
        AddressBookStorage addressBookStorage = new JsonAddressBookStorage(userPrefs.getAddressBookFilePath());
        this.storage = new StorageManager(addressBookStorage, userPrefsStorage);

        this.model = this.initModelManager(this.storage, userPrefs);

        this.logic = new LogicManager(this.model, this.storage);

        this.ui = new UiManager(this.logic);
    }

    /**
     * Returns a {@code ModelManager} with the data from {@code storage}'s address
     * book and {@code userPrefs}. <br>
     * The data from the sample address book will be used instead if
     * {@code storage}'s address book is not found,
     * or an empty address book will be used instead if errors occur when reading
     * {@code storage}'s address book.
     */
    private Model initModelManager(Storage storage, ReadOnlyUserPrefs userPrefs) {
        MainApp.logger.info("Using data file : " + storage.getAddressBookFilePath());

        Optional<ReadOnlyAddressBook> addressBookOptional;
        ReadOnlyAddressBook initialData;
        try {
            addressBookOptional = storage.readAddressBook();
            if (!addressBookOptional.isPresent()) {
                MainApp.logger.info("Creating a new data file " + storage.getAddressBookFilePath()
                        + " populated with a sample AddressBook.");
            }
            initialData = addressBookOptional.orElseGet(SampleDataUtil::getSampleAddressBook);
        } catch (DataLoadingException e) {
            MainApp.logger.warning("Data file at " + storage.getAddressBookFilePath() + " could not be loaded."
                    + " Will be starting with an empty AddressBook.");
            initialData = new AddressBook();
        }

        return new ModelManager(initialData, userPrefs);
    }

    private void initLogging(Config config) {
        LogsCenter.init(config);
    }

    /**
     * Returns a {@code Config} using the file at {@code configFilePath}. <br>
     * The default file path {@code Config#DEFAULT_CONFIG_FILE} will be used instead
     * if {@code configFilePath} is null.
     */
    protected Config initConfig(Path configFilePath) {
        Config initializedConfig;
        Path configFilePathUsed;

        configFilePathUsed = Config.DEFAULT_CONFIG_FILE;

        if (configFilePath != null) {
            MainApp.logger.info("Custom Config file specified " + configFilePath);
            configFilePathUsed = configFilePath;
        }

        MainApp.logger.info("Using config file : " + configFilePathUsed);

        try {
            Optional<Config> configOptional = ConfigUtil.readConfig(configFilePathUsed);
            if (!configOptional.isPresent()) {
                MainApp.logger.info("Creating new config file " + configFilePathUsed);
            }
            initializedConfig = configOptional.orElse(new Config());
        } catch (DataLoadingException e) {
            MainApp.logger.warning("Config file at " + configFilePathUsed + " could not be loaded."
                    + " Using default config properties.");
            initializedConfig = new Config();
        }

        // Update config file in case it was missing to begin with or there are
        // new/unused fields
        try {
            ConfigUtil.saveConfig(initializedConfig, configFilePathUsed);
        } catch (IOException e) {
            MainApp.logger.warning("Failed to save config file : " + StringUtil.getDetails(e));
        }
        return initializedConfig;
    }

    /**
     * Returns a {@code UserPrefs} using the file at {@code storage}'s user prefs
     * file path,
     * or a new {@code UserPrefs} with default configuration if errors occur when
     * reading from the file.
     */
    protected UserPrefs initPrefs(UserPrefsStorage storage) {
        Path prefsFilePath = storage.getUserPrefsFilePath();
        MainApp.logger.info("Using preference file : " + prefsFilePath);

        UserPrefs initializedPrefs;
        try {
            Optional<UserPrefs> prefsOptional = storage.readUserPrefs();
            if (!prefsOptional.isPresent()) {
                MainApp.logger.info("Creating new preference file " + prefsFilePath);
            }
            initializedPrefs = prefsOptional.orElse(new UserPrefs());
        } catch (DataLoadingException e) {
            MainApp.logger.warning("Preference file at " + prefsFilePath + " could not be loaded."
                    + " Using default preferences.");
            initializedPrefs = new UserPrefs();
        }

        // Update prefs file in case it was missing to begin with or there are
        // new/unused fields
        try {
            storage.saveUserPrefs(initializedPrefs);
        } catch (IOException e) {
            MainApp.logger.warning("Failed to save config file : " + StringUtil.getDetails(e));
        }

        return initializedPrefs;
    }

    @Override
    public void start(Stage primaryStage) {
        MainApp.logger.info("Starting AddressBook " + MainApp.VERSION);
        this.ui.start(primaryStage);
    }

    @Override
    public void stop() {
        MainApp.logger.info("============================ [ Stopping AddressBook ] =============================");
        try {
            this.storage.saveUserPrefs(this.model.getUserPrefs());
        } catch (IOException e) {
            MainApp.logger.severe("Failed to save preferences " + StringUtil.getDetails(e));
        }
    }
}
