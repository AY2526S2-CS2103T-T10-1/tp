package cpp.model.util;

import java.util.List;

import cpp.model.classgroup.ClassGroup;
import cpp.model.classgroup.ClassGroupName;

/**
 * Utility class for ClassGroup related operations.
 */
public class ClassGroupUtil {

    /**
     * Finds and returns the class group with the given name from the list of class
     * groups.
     * Returns null if no such class group is found.
     */
    public static ClassGroup findClassGroup(List<ClassGroup> classGroups, ClassGroupName name) {
        for (ClassGroup classGroup : classGroups) {
            if (classGroup.getName().equals(name)) {
                return classGroup;
            }
        }
        return null;
    }
}
