package cpp.logic.parser;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cpp.commons.core.LogsCenter;
import cpp.logic.Messages;
import cpp.logic.commands.AddContactCommand;
import cpp.logic.commands.ClearCommand;
import cpp.logic.commands.Command;
import cpp.logic.commands.DeleteCommand;
import cpp.logic.commands.EditContactCommand;
import cpp.logic.commands.ExitCommand;
import cpp.logic.commands.FindAssignmentCommand;
import cpp.logic.commands.FindClassCommand;
import cpp.logic.commands.FindContactCommand;
import cpp.logic.commands.HelpCommand;
import cpp.logic.commands.ListCommand;
import cpp.logic.commands.assignment.AddAssignmentCommand;
import cpp.logic.commands.assignment.AllocateAssignmentCommand;
import cpp.logic.commands.assignment.EditAssignmentCommand;
import cpp.logic.commands.assignment.GradeAssignmentCommand;
import cpp.logic.commands.assignment.SubmitAssignmentCommand;
import cpp.logic.commands.assignment.UnallocateAssignmentCommand;
import cpp.logic.commands.assignment.UngradeAssignmentCommand;
import cpp.logic.commands.assignment.UnsubmitAssignmentCommand;
import cpp.logic.commands.classgroup.AddClassGroupCommand;
import cpp.logic.commands.classgroup.AllocateClassGroupCommand;
import cpp.logic.commands.classgroup.EditClassGroupCommand;
import cpp.logic.commands.classgroup.UnallocateClassGroupCommand;
import cpp.logic.commands.view.ViewCommand;
import cpp.logic.parser.assignment.AddAssignmentCommandParser;
import cpp.logic.parser.assignment.AllocateAssignmentCommandParser;
import cpp.logic.parser.assignment.EditAssignmentCommandParser;
import cpp.logic.parser.assignment.GradeAssignmentCommandParser;
import cpp.logic.parser.assignment.SubmitAssignmentCommandParser;
import cpp.logic.parser.assignment.UnallocateAssignmentCommandParser;
import cpp.logic.parser.assignment.UngradeAssignmentCommandParser;
import cpp.logic.parser.assignment.UnsubmitAssignmentCommandParser;
import cpp.logic.parser.classgroup.AddClassGroupCommandParser;
import cpp.logic.parser.classgroup.AllocateClassGroupCommandParser;
import cpp.logic.parser.classgroup.EditClassGroupCommandParser;
import cpp.logic.parser.classgroup.UnallocateClassGroupCommandParser;
import cpp.logic.parser.exceptions.ParseException;

/**
 * Parses user input.
 */
public class AddressBookParser {

    /**
     * Used for initial separation of command word and args.
     */
    private static final Pattern BASIC_COMMAND_FORMAT = Pattern.compile("(?<commandWord>\\S+)(?<arguments>.*)");
    private static final Logger logger = LogsCenter.getLogger(AddressBookParser.class);

    /**
     * Parses user input into command for execution.
     *
     * @param userInput full user input string
     * @return the command based on the user input
     * @throws ParseException if the user input does not conform the expected format
     */
    public Command parseCommand(String userInput) throws ParseException {
        final Matcher matcher = AddressBookParser.BASIC_COMMAND_FORMAT.matcher(userInput.trim());
        final Matcher untrimmedMatcher = AddressBookParser.BASIC_COMMAND_FORMAT.matcher(userInput.stripLeading());
        if (!matcher.matches()) {
            throw new ParseException(String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
        }
        if (!untrimmedMatcher.matches()) {
            throw new ParseException(String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
        }

        final String commandWord = matcher.group("commandWord").toLowerCase();
        final String arguments = matcher.group("arguments");
        final String untrimmedArguments = untrimmedMatcher.group("arguments");

        // Note to developers: Change the log level in config.json to enable lower level
        // (i.e., FINE, FINER and lower)
        // log messages such as the one below.
        // Lower level log messages are used sparingly to minimize noise in the code.
        AddressBookParser.logger.fine("Command word: " + commandWord + "; Arguments: " + arguments);

        switch (commandWord) {

        case AddContactCommand.COMMAND_WORD:
        case AddContactCommand.COMMAND_WORD_ALIAS:
            return new AddContactCommandParser().parse(arguments);

        case EditContactCommand.COMMAND_WORD:
        case EditContactCommand.COMMAND_WORD_ALIAS:
            return new EditContactCommandParser().parse(arguments);

        case EditClassGroupCommand.COMMAND_WORD:
        case EditClassGroupCommand.COMMAND_WORD_ALIAS:
            return new EditClassGroupCommandParser().parse(arguments);

        case EditAssignmentCommand.COMMAND_WORD:
            return new EditAssignmentCommandParser().parse(arguments);

        case DeleteCommand.COMMAND_WORD:
            return new DeleteCommandParser().parse(arguments);

        case ClearCommand.COMMAND_WORD:
            return new ClearCommand();

        case FindContactCommand.COMMAND_WORD:
        case FindContactCommand.COMMAND_WORD_ALIAS:
            return new FindContactCommandParser().parse(untrimmedArguments);

        case FindClassCommand.COMMAND_WORD:
        case FindClassCommand.COMMAND_WORD_ALIAS:
            return new FindClassCommandParser().parse(untrimmedArguments);

        case FindAssignmentCommand.COMMAND_WORD:
            return new FindAssignmentCommandParser().parse(untrimmedArguments);

        case ListCommand.COMMAND_WORD:
            return new ListCommandParser().parse(arguments);

        case ExitCommand.COMMAND_WORD:
            return new ExitCommand();

        case HelpCommand.COMMAND_WORD:
            return new HelpCommand();

        case AddAssignmentCommand.COMMAND_WORD:
            return new AddAssignmentCommandParser().parse(arguments);

        case AllocateAssignmentCommand.COMMAND_WORD:
            return new AllocateAssignmentCommandParser().parse(arguments);

        case UnallocateAssignmentCommand.COMMAND_WORD:
            return new UnallocateAssignmentCommandParser().parse(arguments);

        case SubmitAssignmentCommand.COMMAND_WORD:
            return new SubmitAssignmentCommandParser().parse(arguments);

        case UnsubmitAssignmentCommand.COMMAND_WORD:
            return new UnsubmitAssignmentCommandParser().parse(arguments);

        case GradeAssignmentCommand.COMMAND_WORD:
            return new GradeAssignmentCommandParser().parse(arguments);

        case UngradeAssignmentCommand.COMMAND_WORD:
            return new UngradeAssignmentCommandParser().parse(arguments);

        case AddClassGroupCommand.COMMAND_WORD:
        case AddClassGroupCommand.COMMAND_WORD_ALIAS:
            return new AddClassGroupCommandParser().parse(arguments);

        case AllocateClassGroupCommand.COMMAND_WORD:
        case AllocateClassGroupCommand.COMMAND_WORD_ALIAS:
            return new AllocateClassGroupCommandParser().parse(arguments);

        case UnallocateClassGroupCommand.COMMAND_WORD:
        case UnallocateClassGroupCommand.COMMAND_WORD_ALIAS:
            return new UnallocateClassGroupCommandParser().parse(arguments);

        case ViewCommand.COMMAND_WORD:
            return new ViewCommandParser().parse(arguments);

        default:
            AddressBookParser.logger.finer("This user input caused a ParseException: " + userInput);
            String commandSuggestion = AddressBookParser.getCommandSuggestion(commandWord);
            if (commandSuggestion != null) {
                throw new ParseException(String.format(Messages.MESSAGE_UNKNOWN_COMMAND,
                        String.format(Messages.MESSAGE_COMMAND_SUGGESTION, commandSuggestion)));
            } else {
                throw new ParseException(String.format(Messages.MESSAGE_UNKNOWN_COMMAND,
                        Messages.MESSAGE_COMMAND_SUGGESTION_NO_SIMILARITY));
            }
        }
    }

    private static String getCommandSuggestion(String userInput) {
        // Note to developers:
        // Currently, this function only considers non-alias command words for
        // suggestions to avoid confusion. If alias suggestions are desired, they can be
        // added here.
        String[] validCommands = { AddContactCommand.COMMAND_WORD,
                EditContactCommand.COMMAND_WORD,
                EditClassGroupCommand.COMMAND_WORD,
                EditAssignmentCommand.COMMAND_WORD,
                DeleteCommand.COMMAND_WORD,
                ClearCommand.COMMAND_WORD,
                FindContactCommand.COMMAND_WORD,
                FindClassCommand.COMMAND_WORD,
                FindAssignmentCommand.COMMAND_WORD,
                ListCommand.COMMAND_WORD,
                ExitCommand.COMMAND_WORD,
                HelpCommand.COMMAND_WORD,
                AddAssignmentCommand.COMMAND_WORD,
                AllocateAssignmentCommand.COMMAND_WORD,
                UnallocateAssignmentCommand.COMMAND_WORD,
                SubmitAssignmentCommand.COMMAND_WORD,
                UnsubmitAssignmentCommand.COMMAND_WORD,
                GradeAssignmentCommand.COMMAND_WORD,
                UngradeAssignmentCommand.COMMAND_WORD,
                AddClassGroupCommand.COMMAND_WORD,
                AllocateClassGroupCommand.COMMAND_WORD,
                UnallocateClassGroupCommand.COMMAND_WORD,
                ViewCommand.COMMAND_WORD
        };
        String closestMatch = null;
        int minDistance = Integer.MAX_VALUE;
        for (String command : validCommands) {
            int distance = AddressBookParser.levenshteinDistance(userInput.toLowerCase(), command);
            if (distance < minDistance) {
                minDistance = distance;
                closestMatch = command;
            }
        }
        // Note to developers: Arbitrary threshold of 3 chosen. Can be tuned based on
        // user testing.
        return (minDistance <= 3) ? closestMatch : null;
    }

    /**
     * Computes the Levenshtein distance between two strings.
     *
     * Code reuse declaration:
     * This implementation is reused from user {@code tohyzhong} (who is part of the
     * developer team) on GitHub:
     * https://github.com/tohyzhong/ip/blob/master/src/main/java/patrick/task/TaskList.java
     */
    private static int levenshteinDistance(String s1, String s2) {
        int len1 = s1.length();
        int len2 = s2.length();
        int[][] dp = new int[len1 + 1][len2 + 1];
        for (int i = 0; i <= len1; i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= len2; j++) {
            dp[0][j] = j;
        }
        for (int i = 1; i <= len1; i++) {
            for (int j = 1; j <= len2; j++) {
                int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1), dp[i - 1][j - 1] + cost);
            }
        }
        return dp[len1][len2];
    }
}
