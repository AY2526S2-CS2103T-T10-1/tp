package cpp.model.assignment;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import cpp.commons.util.ToStringBuilder;

public class Assignment {

    // Identity fields
    private final String id;
    private final Name name;
    private final LocalDateTime deadline;

    public Assignment(Name name, LocalDateTime deadline) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.deadline = deadline;
    }

    public Assignment(String id, Name name, LocalDateTime deadline) {
        this.id = id;
        this.name = name;
        this.deadline = deadline;
    }

    public String getId() {
        return this.id;
    }

    public Name getName() {
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
        return Objects.hash(this.name, this.deadline);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", this.name)
                .add("deadline", this.deadline)
                .toString();
    }
}
