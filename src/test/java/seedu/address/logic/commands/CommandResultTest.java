package seedu.address.logic.commands;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CommandResultTest {
    @Test
    public void equals() {
        CommandResult commandResult = new CommandResult("feedback");

        // same values -> returns true
        Assertions.assertTrue(commandResult.equals(new CommandResult("feedback")));
        Assertions.assertTrue(commandResult.equals(new CommandResult("feedback", false, false)));

        // same object -> returns true
        Assertions.assertTrue(commandResult.equals(commandResult));

        // null -> returns false
        Assertions.assertFalse(commandResult.equals(null));

        // different types -> returns false
        Assertions.assertFalse(commandResult.equals(0.5f));

        // different feedbackToUser value -> returns false
        Assertions.assertFalse(commandResult.equals(new CommandResult("different")));

        // different showHelp value -> returns false
        Assertions.assertFalse(commandResult.equals(new CommandResult("feedback", true, false)));

        // different exit value -> returns false
        Assertions.assertFalse(commandResult.equals(new CommandResult("feedback", false, true)));
    }

    @Test
    public void hashcode() {
        CommandResult commandResult = new CommandResult("feedback");

        // same values -> returns same hashcode
        Assertions.assertEquals(commandResult.hashCode(), new CommandResult("feedback").hashCode());

        // different feedbackToUser value -> returns different hashcode
        Assertions.assertNotEquals(commandResult.hashCode(), new CommandResult("different").hashCode());

        // different showHelp value -> returns different hashcode
        Assertions.assertNotEquals(commandResult.hashCode(), new CommandResult("feedback", true, false).hashCode());

        // different exit value -> returns different hashcode
        Assertions.assertNotEquals(commandResult.hashCode(), new CommandResult("feedback", false, true).hashCode());
    }

    @Test
    public void toStringMethod() {
        CommandResult commandResult = new CommandResult("feedback");
        String expected = CommandResult.class.getCanonicalName() + "{feedbackToUser="
                + commandResult.getFeedbackToUser() + ", showHelp=" + commandResult.isShowHelp()
                + ", exit=" + commandResult.isExit() + "}";
        Assertions.assertEquals(expected, commandResult.toString());
    }
}
