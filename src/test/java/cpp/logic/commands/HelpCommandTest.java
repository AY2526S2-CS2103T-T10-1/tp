package cpp.logic.commands;

import org.junit.jupiter.api.Test;

import cpp.model.Model;
import cpp.model.ModelManager;

public class HelpCommandTest {
    private Model model = new ModelManager();
    private Model expectedModel = new ModelManager();

    @Test
    public void execute_help_success() {
        CommandResult expectedCommandResult = new CommandResult(HelpCommand.SHOWING_HELP_MESSAGE,
                CommandResult.ListView.NONE, CommandResult.ViewType.NONE, true, false);
        CommandTestUtil.assertCommandSuccess(new HelpCommand(), this.model, expectedCommandResult, this.expectedModel);
    }
}
