package cpp.logic;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import cpp.logic.parser.ParserUtil;
import cpp.logic.parser.Prefix;
import cpp.model.assignment.Assignment;
import cpp.model.classgroup.ClassGroup;
import cpp.model.contact.Contact;

/**
 * Container for user visible messages.
 */
public class Messages {

    public static final String MESSAGE_UNKNOWN_COMMAND = "Unknown command";
    public static final String MESSAGE_INVALID_COMMAND_FORMAT = "Invalid command format! \n%1$s";
    public static final String MESSAGE_INVALID_CONTACT_DISPLAYED_INDEX = "The contact index provided is invalid";
    public static final String MESSAGE_INVALID_CLASS_GROUP_DISPLAYED_INDEX =
            "The class group index provided is invalid";
    public static final String MESSAGE_ASSIGNMENT_NOT_FOUND = "The assignment provided is not found";
    public static final String MESSAGE_CLASS_GROUP_NOT_FOUND = "The class group provided is not found";
    public static final String MESSAGE_CONTACTS_LISTED_OVERVIEW = "%1$d contacts listed!";
    public static final String MESSAGE_DUPLICATE_FIELDS = """
            Multiple values specified for the following single-valued field(s): """;

    /**
     * Returns an error message indicating the duplicate prefixes.
     */
    public static String getErrorMessageForDuplicatePrefixes(Prefix... duplicatePrefixes) {
        assert duplicatePrefixes.length > 0;

        Set<String> duplicateFields = Stream.of(duplicatePrefixes).map(Prefix::toString).collect(Collectors.toSet());

        return Messages.MESSAGE_DUPLICATE_FIELDS + String.join(" ", duplicateFields);
    }

    /**
     * Formats the {@code contact} for display to the user.
     */
    public static String format(Contact contact) {
        final StringBuilder builder = new StringBuilder();
        builder.append(contact.getName())
                .append("; Phone: ")
                .append(contact.getPhone())
                .append("; Email: ")
                .append(contact.getEmail())
                .append("; Address: ")
                .append(contact.getAddress())
                .append("; Tags: ");
        contact.getTags().forEach(builder::append);
        return builder.toString();
    }

    /**
     * Formats the {@code assignment} for display to the user.
     */
    public static String format(Assignment assignment) {
        final StringBuilder builder = new StringBuilder();
        builder.append(assignment.getName())
                .append("; Deadline: ")
                .append(assignment.getDeadline().format(ParserUtil.DEADLINE_FORMATTER));
        return builder.toString();
    }

    /**
     * Formats the {@code classGroup} for display to the user.
     */
    public static String format(ClassGroup classGroup) {
        final StringBuilder builder = new StringBuilder();
        builder.append(classGroup.getName());
        return builder.toString();
    }

}
