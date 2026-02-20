package seedu.address;

import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javafx.application.Application;

public class AppParametersTest {

    private final ParametersStub parametersStub = new ParametersStub();
    private final AppParameters expected = new AppParameters();

    @Test
    public void parse_validConfigPath_success() {
        this.parametersStub.namedParameters.put("config", "config.json");
        this.expected.setConfigPath(Paths.get("config.json"));
        Assertions.assertEquals(this.expected, AppParameters.parse(this.parametersStub));
    }

    @Test
    public void parse_nullConfigPath_success() {
        this.parametersStub.namedParameters.put("config", null);
        Assertions.assertEquals(this.expected, AppParameters.parse(this.parametersStub));
    }

    @Test
    public void parse_invalidConfigPath_success() {
        this.parametersStub.namedParameters.put("config", "a\0");
        this.expected.setConfigPath(null);
        Assertions.assertEquals(this.expected, AppParameters.parse(this.parametersStub));
    }

    @Test
    public void toStringMethod() {
        AppParameters appParameters = new AppParameters();
        String expected = AppParameters.class.getCanonicalName() + "{configPath=" + appParameters.getConfigPath() + "}";
        Assertions.assertEquals(expected, appParameters.toString());
    }

    @Test
    public void equals() {
        AppParameters appParameters = new AppParameters();

        // same values -> returns true
        Assertions.assertTrue(appParameters.equals(new AppParameters()));

        // same object -> returns true
        Assertions.assertTrue(appParameters.equals(appParameters));

        // null -> returns false
        Assertions.assertFalse(appParameters.equals(null));

        // different types -> returns false
        Assertions.assertFalse(appParameters.equals(5.0f));

        // different config path -> returns false
        AppParameters otherAppParameters = new AppParameters();
        otherAppParameters.setConfigPath(Paths.get("configPath"));
        Assertions.assertFalse(appParameters.equals(otherAppParameters));
    }

    private static class ParametersStub extends Application.Parameters {
        private Map<String, String> namedParameters = new HashMap<>();

        @Override
        public List<String> getRaw() {
            throw new AssertionError("should not be called");
        }

        @Override
        public List<String> getUnnamed() {
            throw new AssertionError("should not be called");
        }

        @Override
        public Map<String, String> getNamed() {
            return Collections.unmodifiableMap(this.namedParameters);
        }
    }
}
