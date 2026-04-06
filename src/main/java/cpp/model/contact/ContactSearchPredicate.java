package cpp.model.contact;

import java.util.function.Predicate;

/**
 * Interface for contact search predicates.
 * All contact-related search predicates should implement this interface.
 */
public interface ContactSearchPredicate extends Predicate<Contact> {
}
