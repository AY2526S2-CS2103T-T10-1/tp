package cpp.testutil;

import java.time.format.DateTimeFormatter;

import cpp.logic.commands.assignment.AddAssignmentCommand;
import cpp.logic.parser.CliSyntax;
import cpp.model.assignment.Assignment;

/**
 * A utility class containing methods to help with testing of Assignment.
 */
public class AssignmentUtil {
    /**
     * Returns an add command string for adding the {@code assignment}.
     */
    public static String getAddAssignmentCommand(Assignment assignment) {
        return AddAssignmentCommand.COMMAND_WORD + " " + AssignmentUtil.getAssignmentDetails(assignment);
    }

    /**
     * Returns the part of command string for the given {@code assignment}'s
     * details.
     */
    public static String getAssignmentDetails(Assignment assignment) {
        StringBuilder sb = new StringBuilder();
        sb.append(" ").append(CliSyntax.PREFIX_ASSIGNMENT).append(assignment.getName().fullName).append(" ");
        sb.append(" ").append(CliSyntax.PREFIX_DEADLINE)
                .append(assignment.getDeadline().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))
                        .toString())
                .append(" ");
        return sb.toString();
    }
}
