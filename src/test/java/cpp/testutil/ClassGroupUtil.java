package cpp.testutil;

import java.util.List;

import cpp.commons.core.index.Index;
import cpp.logic.commands.classgroup.AddClassGroupCommand;
import cpp.logic.commands.classgroup.AllocateClassGroupCommand;
import cpp.logic.commands.classgroup.UnallocateClassGroupCommand;
import cpp.logic.parser.CliSyntax;
import cpp.model.classgroup.ClassGroup;

/**
 * A utility class containing methods to help with testing of ClassGroup.
 */
public class ClassGroupUtil {

    /**
     * Returns an add command string for adding the {@code classGroup}.
     */
    public static String getAddClassGroupCommand(ClassGroup classGroup) {
        return AddClassGroupCommand.COMMAND_WORD + " " + ClassGroupUtil.getClassGroupDetails(classGroup);
    }

    /**
     * Returns an allocate command string for allocating the {@code classGroup} to
     * the specified {@code contactIndices}.
     */
    public static String getAllocateClassGroupCommand(ClassGroup classGroup, List<Index> contactIndices) {
        return AllocateClassGroupCommand.COMMAND_WORD + " " + ClassGroupUtil.getClassGroupDetails(classGroup) + " "
                + CliSyntax.PREFIX_CONTACT + ClassGroupUtil.getContactIndicesString(contactIndices);
    }

    /**
     * Returns an unallocate command string for unallocating the {@code classGroup}
     * from the specified {@code contactIndices}.
     */
    public static String getUnallocateClassGroupCommand(ClassGroup classGroup, List<Index> contactIndices) {
        return UnallocateClassGroupCommand.COMMAND_WORD + " " + ClassGroupUtil.getClassGroupDetails(classGroup) + " "
                + CliSyntax.PREFIX_CONTACT + ClassGroupUtil.getContactIndicesString(contactIndices);
    }

    /**
     * Returns the part of command string for the given {@code classGroup}'s
     * details.
     */
    public static String getClassGroupDetails(ClassGroup classGroup) {
        return " " + CliSyntax.PREFIX_CLASS + classGroup.getName().fullName + " ";
    }

    /**
     * Returns a string of contact indices separated by spaces for the given list of
     * indices.
     */
    public static String getContactIndicesString(List<Index> contactIndices) {
        return contactIndices.stream()
                .map(Index::getOneBased)
                .map(String::valueOf)
                .reduce((a, b) -> a + " " + b)
                .orElse("");
    }
}
