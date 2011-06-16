package groove.gui.jgraph;

import groove.gui.Icons;
import groove.gui.Options;

import java.awt.Cursor;

import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

/** Manipulation and viewing mode of a JGraph. */
public enum JGraphMode {
    /** Selection and possibly label edit mode. */
    SELECT_MODE(Options.SELECT_MODE_NAME, Options.SELECT_MODE_KEY,
            Icons.SELECT_ICON),
    /** Panning and zooming. */
    PAN_MODE(Options.PAN_MODE_NAME, Options.PAN_MODE_KEY, Icons.HAND_OPEN_ICON,
            Icons.HAND_OPEN_CURSOR, Icons.HAND_CLOSED_CURSOR),
    /** Edge edit mode. */
    EDIT_MODE(Options.EDIT_MODE_NAME, Options.EDIT_MODE_KEY, Icons.EDGE_ICON),
    /** JGraph preview mode. */
    PREVIEW_MODE(Options.PREVIEW_MODE_NAME, Options.PREVIEW_MODE_KEY,
            Icons.PREVIEW_ICON);

    private JGraphMode(String text, KeyStroke acceleratorKey, ImageIcon icon,
            Cursor moveCursor, Cursor dragCursor) {
        this.text = text;
        this.acceleratorKey = acceleratorKey;
        this.icon = icon;
        this.cursor = moveCursor;
        this.dragCursor = dragCursor;
    }

    private JGraphMode(String text, KeyStroke acceleratorKey, ImageIcon icon) {
        this(text, acceleratorKey, icon, Cursor.getDefaultCursor(),
            Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
    }

    /** Returns a descriptive (tool tip) text for this mode. */
    public final String getName() {
        return this.text;
    }

    /** Returns a (possibly {@code null}) accelerator key for this mode. */
    public final KeyStroke getAcceleratorKey() {
        return this.acceleratorKey;
    }

    /** Returns an icon for this mode. */
    public final ImageIcon getIcon() {
        return this.icon;
    }

    /** Returns the preferred (normal) cursor for this mode. */
    public final Cursor getCursor() {
        return this.cursor;
    }

    /** Returns the preferred drag cursor for this mode. */
    public final Cursor getDragCursor() {
        return this.dragCursor;
    }

    private final String text;
    private final KeyStroke acceleratorKey;
    private final ImageIcon icon;
    private final Cursor cursor;
    private final Cursor dragCursor;
}