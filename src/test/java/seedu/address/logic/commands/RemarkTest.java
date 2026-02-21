package seedu.address.logic.commands;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;

public class RemarkTest {
    private static final String MESSAGE_NOT_IMPLEMENTED_YET = "This command has not been implemented yet.";
    private Model model = new ModelManager();

    @Test
    public void execute() {
        CommandTestUtil.assertCommandFailure(new RemarkCommand(), this.model, RemarkTest.MESSAGE_NOT_IMPLEMENTED_YET);
    }

}
