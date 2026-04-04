package cpp.logic.commands;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CommandResultTest {
    @Test
    public void equals() {
        CommandResult commandResult = new CommandResult("feedback");

        // same values -> returns true
        Assertions.assertTrue(commandResult.equals(new CommandResult("feedback")));
        Assertions.assertTrue(
                commandResult.equals(new CommandResult("feedback", CommandResult.ListView.NONE,
                        CommandResult.ViewType.NONE, false, false)));

        // same object -> returns true
        Assertions.assertTrue(commandResult.equals(commandResult));

        // null -> returns false
        Assertions.assertFalse(commandResult.equals(null));

        // different types -> returns false
        Assertions.assertFalse(commandResult.equals(0.5f));

        // different feedbackToUser value -> returns false
        Assertions.assertFalse(commandResult.equals(new CommandResult("different")));

        // different listView value -> returns false
        Assertions.assertFalse(commandResult.equals(new CommandResult("feedback", CommandResult.ListView.CONTACTS)));

        // different showHelp value -> returns false
        Assertions.assertFalse(
                commandResult.equals(new CommandResult("feedback", CommandResult.ListView.NONE,
                        CommandResult.ViewType.NONE, true, false)));

        // different exit value -> returns false
        Assertions.assertFalse(
                commandResult.equals(new CommandResult("feedback", CommandResult.ListView.NONE,
                        CommandResult.ViewType.NONE, false, true)));
    }

    @Test
    public void hashcode() {
        CommandResult commandResult = new CommandResult("feedback");

        // same values -> returns same hashcode
        Assertions.assertEquals(commandResult.hashCode(), new CommandResult("feedback").hashCode());

        // different feedbackToUser value -> returns different hashcode
        Assertions.assertNotEquals(commandResult.hashCode(), new CommandResult("different").hashCode());

        // different listView value -> returns different hashcode
        Assertions.assertNotEquals(commandResult.hashCode(),
                new CommandResult("feedback", CommandResult.ListView.CONTACTS).hashCode());

        // different showHelp value -> returns different hashcode
        Assertions.assertNotEquals(commandResult.hashCode(),
                new CommandResult("feedback", CommandResult.ListView.NONE, CommandResult.ViewType.NONE, true, false)
                        .hashCode());

        // different exit value -> returns different hashcode
        Assertions.assertNotEquals(commandResult.hashCode(),
                new CommandResult("feedback", CommandResult.ListView.NONE, CommandResult.ViewType.NONE, false, true)
                        .hashCode());
    }

    @Test
    public void toStringMethod() {
        CommandResult commandResult = new CommandResult("feedback");
        String expected = CommandResult.class.getCanonicalName() + "{feedbackToUser="
                + commandResult.getFeedbackToUser() + ", listView=" + commandResult.getListView()
                + ", viewType=" + commandResult.getViewType()
                + ", showHelp=" + commandResult.isShowHelp()
                + ", exit=" + commandResult.isExit() + "}";
        Assertions.assertEquals(expected, commandResult.toString());
    }
}
