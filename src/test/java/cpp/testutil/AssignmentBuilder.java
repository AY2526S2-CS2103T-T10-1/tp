package cpp.testutil;

import java.time.LocalDateTime;

import cpp.model.assignment.Assignment;
import cpp.model.assignment.AssignmentName;

/**
 * A utility class to help with building Assignment objects.
 */
public class AssignmentBuilder {
    public static final String DEFAULT_ID = "793f92b5-9e96-47bb-94cd-e8ede5523d7a";
    public static final String DEFAULT_NAME = "Assignment 1";
    public static final String DEFAULT_DEADLINE = "13-12-2020 10:01";

    private String id;
    private AssignmentName name;
    private LocalDateTime deadline;

    /**
     * Creates an {@code AssignmentBuilder} with the default details.
     */
    public AssignmentBuilder() {
        this.id = AssignmentBuilder.DEFAULT_ID;
        this.name = new AssignmentName(AssignmentBuilder.DEFAULT_NAME);
        this.deadline = LocalDateTime.parse(AssignmentBuilder.DEFAULT_DEADLINE,
                java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
    }

    /**
     * Initializes the AssignmentBuilder with the data of {@code assignmentToCopy}.
     */
    public AssignmentBuilder(Assignment assignmentToCopy) {
        this.id = assignmentToCopy.getId();
        this.name = assignmentToCopy.getName();
        this.deadline = assignmentToCopy.getDeadline();
    }

    /**
     * Sets the {@code id} of the {@code Assignment} that we are building.
     */
    public AssignmentBuilder withId(String id) {
        this.id = id;
        return this;
    }

    /**
     * Sets the {@code name} of the {@code Assignment} that we are building.
     */
    public AssignmentBuilder withName(String name) {
        this.name = new AssignmentName(name);
        return this;
    }

    /**
     * Sets the {@code deadline} of the {@code Assignment} that we are building.
     */
    public AssignmentBuilder withDeadline(String deadline) {
        this.deadline = LocalDateTime.parse(deadline,
                java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
        return this;
    }

    public Assignment build() {
        return new Assignment(this.id, this.name, this.deadline);
    }
}
