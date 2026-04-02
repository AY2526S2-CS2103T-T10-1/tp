package cpp.model.assignment;

import java.time.LocalDateTime;

/**
 * Tests that an {@code Assignment}'s {@code Deadline} matches the given
 * {@code LocalDateTime} exactly.
 */
public class AssignmentDeadlineContainsKeywordPredicate implements AssignmentSearchPredicate {

    private final LocalDateTime datetime;
    private final boolean isDateOnly;

    /**
     * Creates an AssignmentDeadlineContainsKeywordPredicate with the given
     * datetime. The datetime will be treated as date-only if {@code isDateOnly} is
     * true, meaning that it will match any assignment whose deadline falls on the
     * same date, regardless of the time.
     *
     * @param datetime   the datetime to match against
     * @param isDateOnly whether the provided datetime should be treated as
     *                   date-only
     */
    public AssignmentDeadlineContainsKeywordPredicate(LocalDateTime datetime, boolean isDateOnly) {
        this.datetime = datetime;
        this.isDateOnly = isDateOnly;
    }

    @Override
    public boolean test(Assignment assignment) {
        LocalDateTime deadline = assignment.getDeadline();

        if (this.isDateOnly) {
            return deadline.toLocalDate().equals(this.datetime.toLocalDate());
        }

        return deadline.equals(this.datetime);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof AssignmentDeadlineContainsKeywordPredicate)) {
            return false;
        }

        AssignmentDeadlineContainsKeywordPredicate otherPredicate = (AssignmentDeadlineContainsKeywordPredicate) other;
        return this.isDateOnly == otherPredicate.isDateOnly
                && this.datetime.equals(otherPredicate.datetime);
    }

    @Override
    public String toString() {
        return new cpp.commons.util.ToStringBuilder(this)
                .add("datetime", this.datetime)
                .add("dateOnly", this.isDateOnly)
                .toString();
    }
}
