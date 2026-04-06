package cpp.model.assignment;

import java.util.function.Predicate;

/**
 * Interface for assignment search predicates.
 * All assignment-related search predicates should implement this interface.
 */
public interface AssignmentSearchPredicate extends Predicate<Assignment> {
}
