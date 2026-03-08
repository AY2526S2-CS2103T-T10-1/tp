package cpp.model.classgroup;

import java.util.Objects;
import java.util.UUID;

import cpp.commons.util.CollectionUtil;
import cpp.commons.util.ToStringBuilder;

/**
 * Represents a Class Grouping in the address book.
 * Guarantees: details are present and not null, field values are validated,
 * immutable.
 */
public class ClassGroup {

    private final String id;
    private final String name;

    /**
     * Creates a class grouping with the given name.
     * Every field must be present and not null.
     */
    public ClassGroup(String name) {
        CollectionUtil.requireAllNonNull(name);
        this.id = UUID.randomUUID().toString();
        this.name = name;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof ClassGroup)) {
            return false;
        }

        ClassGroup otherClassGroup = (ClassGroup) other;
        return otherClassGroup.getId().equals(this.getId())
                && otherClassGroup.getName().equals(this.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.name);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", this.name)
                .toString();
    }
}
