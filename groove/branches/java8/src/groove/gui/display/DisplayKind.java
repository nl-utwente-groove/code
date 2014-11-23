package groove.gui.display;

import groove.grammar.model.ResourceKind;
import groove.graph.GraphRole;
import groove.gui.Icons;
import groove.gui.Icons.Icon;
import groove.util.Exceptions;

import java.util.EnumMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNull;

/** Type of components in the panel. */
public enum DisplayKind {
    /** Host graph display. */
    HOST(ResourceKind.HOST, Icons.GRAPH_FRAME_ICON, "Graphs", "Current graph state"),
    /** Rule display. */
    RULE(ResourceKind.RULE, Icons.RULE_FRAME_ICON, "Rules", "Selected rule"),
    /** Type display. */
    TYPE(ResourceKind.TYPE, Icons.TYPE_FRAME_ICON, "Types", "Type graphs"),
    /** Control display. */
    CONTROL(ResourceKind.CONTROL, Icons.CONTROL_FRAME_ICON, "Control", "Control specifications"),
    /** Prolog display. */
    PROLOG(ResourceKind.PROLOG, Icons.PROLOG_FRAME_ICON, "Prolog", "Prolog programs"),
    /** Groovy panel. */
    GROOVY(ResourceKind.GROOVY, Icons.GROOVY_FRAME_ICON, "Groovy", "Groovy scripts"),
    /** Format configuration panel. */
    FORMAT(ResourceKind.FORMAT, Icons.FORMAT_FRAME_ICON, "Format", "Format configurations"),
    /** State display. */
    STATE(null, Icons.STATE_FRAME_ICON, "State", "State panel"),
    /** LTS display. */
    LTS(null, Icons.LTS_FRAME_ICON, "Simulation", "Simulation panel"),
    /** Properties display. */
    PROPERTIES(ResourceKind.PROPERTIES, Icons.PROPERTIES_FRAME_ICON, "Properties",
        "System properties");

    private DisplayKind(ResourceKind resource, Icon tabIcon, String title, String tip) {
        this.resource = resource;
        this.tabIcon = tabIcon;
        this.title = title;
        this.tip = tip;
    }

    /** Returns the icon that should be used on the tab for a display of this kind. */
    public final Icon getTabIcon() {
        return this.tabIcon;
    }

    /** Returns the kind of resource displayed here, if any. */
    public final ResourceKind getResource() {
        return this.resource;
    }

    /** Indicates if this is a resource-related display. */
    public final boolean hasResource() {
        return getResource() != null;
    }

    /** Indicates if this display should be put on the displays panel. */
    public final boolean showDisplay() {
        return this != PROPERTIES;
    }

    /** Returns the list panel position for this display. */
    public final @NonNull Position getListPanel() {
        Position result;
        switch (this) {
        case STATE:
        case RULE:
        case PROPERTIES:
        case FORMAT:
            result = Position.UPPER;
            break;
        case CONTROL:
        case HOST:
        case TYPE:
        case PROLOG:
        case GROOVY:
            result = Position.LOWER;
            break;
        case LTS:
            result = Position.NONE;
            break;
        default:
            throw Exceptions.UNREACHABLE;
        }
        return result;
    }

    /** Returns the title of this display. */
    public final String getTitle() {
        return this.title;
    }

    /** Returns the tool tip description for this display. */
    public final String getTip() {
        return this.tip;
    }

    /** Returns the graph role corresponding to this tab kind, if any. */
    public final GraphRole getGraphRole() {
        return GraphRole.valueOf(name());
    }

    /** Returns true if this display kind is used for showing graphs. */
    public final boolean isGraphBased() {
        return this == HOST || this == RULE || this == LTS || this == STATE || this == TYPE;
    }

    private final ResourceKind resource;
    private final Icon tabIcon;
    private final String title;
    private final String tip;

    /** Returns the display kind for a given resource kind. */
    public static final DisplayKind toDisplay(ResourceKind resource) {
        return resourceMap.get(resource);
    }

    private static final Map<ResourceKind,DisplayKind> resourceMap =
        new EnumMap<ResourceKind,DisplayKind>(ResourceKind.class);

    static {
        for (DisplayKind kind : DisplayKind.values()) {
            if (kind.hasResource()) {
                resourceMap.put(kind.getResource(), kind);
            }
        }
    }

    /** Returns the position of the list panel for this display kind. */
    public static enum Position {
        /** There is no list panel. */
        NONE,
        /** Placed in the upper list panel. */
        UPPER,
        /** Placed in the lower list panel. */
        LOWER;
    }
}
