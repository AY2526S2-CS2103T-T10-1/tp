package cpp.model.assignment;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import cpp.commons.util.CollectionUtil;
import cpp.commons.util.ToStringBuilder;

/**
 * Represents an Assignment in the address book.
 * Guarantees: details are present and not null, field values are validated,
 * immutable.
 */
public class Assignment {

    // Identity fields
    private final String id;
    private final AssignmentName name;
    private final LocalDateTime deadline;

    /**
     * Creates an assignment with the given name and deadline.
     * Generates a random id for the assignment.
     * Every field must be present and not null.
     */
    public Assignment(AssignmentName name, LocalDateTime deadline) {
        CollectionUtil.requireAllNonNull(name, deadline);
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.deadline = deadline;
    }

    /**
     * Creates an assignment with the given id, name and deadline. Every field must
     * be present and not null.
     * This constructor is used for loading from storage, where the id is already
     * available.
     */
    public Assignment(String id, AssignmentName name, LocalDateTime deadline) {
        CollectionUtil.requireAllNonNull(id, name, deadline);
        this.id = id;
        this.name = name;
        this.deadline = deadline;
    }

    public String getId() {
        return this.id;
    }

    public AssignmentName getName() {
        return this.name;
    }

    public LocalDateTime getDeadline() {
        return this.deadline;
    }

    /**
     * Returns true if both assignments have the same id, name and deadline.
     *
     * @param other the other assignment to compare with
     * @return true if both assignments have the same id, name and deadline
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Assignment)) {
            return false;
        }

        Assignment otherAssignment = (Assignment) other;
        return this.id.equals(otherAssignment.id)
                && this.name.equals(otherAssignment.name)
                && this.deadline.equals(otherAssignment.deadline);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.name, this.deadline);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", this.name)
                .add("deadline", this.deadline)
                .toString();
    }
}
