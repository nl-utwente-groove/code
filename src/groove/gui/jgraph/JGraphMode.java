package groove.gui.jgraph;

import groove.gui.Options;
import groove.util.Groove;

import java.awt.Cursor;

import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

/** Manipulation and viewing mode of a JGraph. */
public enum JGraphMode {
    /** Selection and possibly label edit mode. */
    SELECT_MODE(Options.SELECT_MODE_NAME, Options.SELECT_MODE_KEY,
            Groove.createIcon("select.gif")),
    /** Panning and zooming. */
    PAN_MODE(Options.PAN_MODE_NAME, null, Groove.OPEN_HAND_ICON,
            Groove.OPEN_HAND_CURSOR),
    /** Node edit mode. */
    NODE_MODE(Options.NODE_MODE_NAME, Options.NODE_MODE_KEY,
            Groove.createIcon("rectangle.gif")),
    /** Edge edit mode. */
    EDGE_MODE(Options.EDGE_MODE_NAME, Options.EDGE_MODE_KEY,
            Groove.createIcon("edge.gif")),
    /** JGraph preview mode. */
    PREVIEW_MODE(Options.PREVIEW_MODE_NAME, Options.PREVIEW_MODE_KEY,
            Groove.createIcon("preview.gif"));

    private JGraphMode(String text, KeyStroke acceleratorKey, ImageIcon icon,
            Cursor cursor) {
        this.text = text;
        this.acceleratorKey = acceleratorKey;
        this.icon = icon;
        this.cursor = cursor;
    }

    private JGraphMode(String text, KeyStroke acceleratorKey, ImageIcon icon) {
        this(text, acceleratorKey, icon, Cursor.getDefaultCursor());
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

    /** Returns the preferred cursor for this mode. */
    public final Cursor getCursor() {
        return this.cursor;
    }

    private final String text;
    private final KeyStroke acceleratorKey;
    private final ImageIcon icon;
    private final Cursor cursor;
}