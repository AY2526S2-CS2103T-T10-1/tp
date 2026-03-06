package cpp.commons.core;

import java.awt.Point;
import java.io.Serializable;
import java.util.Objects;

import cpp.commons.util.ToStringBuilder;

/**
 * A Serializable class that contains the GUI settings.
 * Guarantees: immutable.
 */
public class GuiSettings implements Serializable {

    private static final double DEFAULT_HEIGHT = 600;
    private static final double DEFAULT_WIDTH = 740;

    private final double windowWidth;
    private final double windowHeight;
    private final Point windowCoordinates;

    /**
     * Constructs a {@code GuiSettings} with the default height, width and position.
     */
    public GuiSettings() {
        this.windowWidth = GuiSettings.DEFAULT_WIDTH;
        this.windowHeight = GuiSettings.DEFAULT_HEIGHT;
        this.windowCoordinates = null; // null represent no coordinates
    }

    /**
     * Constructs a {@code GuiSettings} with the specified height, width and
     * position.
     */
    public GuiSettings(double windowWidth, double windowHeight, int xPosition, int yPosition) {
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        this.windowCoordinates = new Point(xPosition, yPosition);
    }

    public double getWindowWidth() {
        return this.windowWidth;
    }

    public double getWindowHeight() {
        return this.windowHeight;
    }

    public Point getWindowCoordinates() {
        return this.windowCoordinates != null ? new Point(this.windowCoordinates) : null;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof GuiSettings)) {
            return false;
        }

        GuiSettings otherGuiSettings = (GuiSettings) other;
        return this.windowWidth == otherGuiSettings.windowWidth
                && this.windowHeight == otherGuiSettings.windowHeight
                && Objects.equals(this.windowCoordinates, otherGuiSettings.windowCoordinates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.windowWidth, this.windowHeight, this.windowCoordinates);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("windowWidth", this.windowWidth)
                .add("windowHeight", this.windowHeight)
                .add("windowCoordinates", this.windowCoordinates)
                .toString();
    }
}
