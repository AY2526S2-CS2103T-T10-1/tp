package cpp.logic.commands;

import org.junit.jupiter.api.Test;

import cpp.model.Model;
import cpp.model.ModelManager;

public class ExitCommandTest {
    private Model model = new ModelManager();
    private Model expectedModel = new ModelManager();

    @Test
    public void execute_exit_success() {
        CommandResult expectedCommandResult = new CommandResult(ExitCommand.MESSAGE_EXIT_ACKNOWLEDGEMENT,
                CommandResult.ListView.NONE, CommandResult.ViewType.NONE, false, true);
        CommandTestUtil.assertCommandSuccess(new ExitCommand(), this.model, expectedCommandResult, this.expectedModel);
    }
}
