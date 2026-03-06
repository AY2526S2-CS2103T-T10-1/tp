package cpp.logic.commands;

import cpp.model.Model;

/**
 * Format full help instructions for every command for display.
 */
public class HelpCommand extends Command {

    public static final String COMMAND_WORD = "help";

    public static final String MESSAGE_USAGE = HelpCommand.COMMAND_WORD + ": Shows program usage instructions.\n"
            + "Example: " + HelpCommand.COMMAND_WORD;

    public static final String SHOWING_HELP_MESSAGE = "Opened help window.";

    @Override
    public CommandResult execute(Model model) {
        return new CommandResult(HelpCommand.SHOWING_HELP_MESSAGE, true, false);
    }
}
