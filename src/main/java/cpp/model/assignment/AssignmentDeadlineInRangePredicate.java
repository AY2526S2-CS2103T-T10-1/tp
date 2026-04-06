package cpp.model.assignment;

import java.time.LocalDateTime;

import cpp.commons.util.ToStringBuilder;

/**
 * Tests that an {@code Assignment}'s {@code Deadline} falls within the
 * specified range, inclusive of the start and end datetimes.
 */
public class AssignmentDeadlineInRangePredicate implements AssignmentSearchPredicate {

    private final LocalDateTime datetimeStart;
    private final LocalDateTime datetimeEnd;

    /**
     * Creates an AssignmentDeadlineInRangePredicate with the given
     * datetime range. The predicate tests if an assignment's deadline falls within
     * the specified range, inclusive of the start and end datetimes.
     *
     * @param datetimeStart the start datetime for the range
     * @param datetimeEnd   the end datetime for the range
     */
    public AssignmentDeadlineInRangePredicate(LocalDateTime datetimeStart, LocalDateTime datetimeEnd) {
        this.datetimeStart = datetimeStart;
        this.datetimeEnd = datetimeEnd;
    }

    @Override
    public boolean test(Assignment assignment) {
        LocalDateTime deadline = assignment.getDeadline();

        return !deadline.isBefore(this.datetimeStart) && !deadline.isAfter(this.datetimeEnd);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof AssignmentDeadlineInRangePredicate)) {
            return false;
        }

        AssignmentDeadlineInRangePredicate otherPredicate = (AssignmentDeadlineInRangePredicate) other;
        return this.datetimeStart.equals(otherPredicate.datetimeStart)
                && this.datetimeEnd.equals(otherPredicate.datetimeEnd);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("datetimeStart", this.datetimeStart)
                .add("datetimeEnd", this.datetimeEnd)
                .toString();
    }
}
