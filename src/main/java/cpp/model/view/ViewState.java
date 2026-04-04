package cpp.model.view;

import java.util.Objects;

import cpp.logic.commands.CommandResult;

/**
 * Represents the current unique view state in the UI.
 * Holds a {@link CommandResult.ViewType} and an optional payload object
 * (e.g. Assignment, Contact, ClassGroup).
 */
public class ViewState {
    private final CommandResult.ViewType type;
    private final Object payload;

    private ViewState(CommandResult.ViewType type, Object payload) {
        this.type = type;
        this.payload = payload;
    }

    public static ViewState ofAssignment(Object assignment) {
        return new ViewState(CommandResult.ViewType.ASSIGNMENT, assignment);
    }

    public static ViewState ofContact(Object contact) {
        return new ViewState(CommandResult.ViewType.CONTACT, contact);
    }

    public static ViewState ofClassGroup(Object cg) {
        return new ViewState(CommandResult.ViewType.CLASSGROUP, cg);
    }

    public static ViewState none() {
        return new ViewState(CommandResult.ViewType.NONE, null);
    }

    public CommandResult.ViewType getType() {
        return this.type;
    }

    public Object getPayload() {
        return this.payload;
    }

    public boolean isViewingAssignment(Object assignment) {
        return this.type == CommandResult.ViewType.ASSIGNMENT && Objects.equals(this.payload, assignment);
    }

    public boolean isViewingContact(Object contact) {
        return this.type == CommandResult.ViewType.CONTACT && Objects.equals(this.payload, contact);
    }

    public boolean isViewingClassGroup(Object classGroup) {
        return this.type == CommandResult.ViewType.CLASSGROUP && Objects.equals(this.payload, classGroup);
    }

}
