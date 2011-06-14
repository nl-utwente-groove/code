package groove.gui;

import groove.graph.GraphRole;
import groove.trans.ResourceKind;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

import javax.swing.ImageIcon;

/** Type of components in the panel. */
public enum DisplayKind {
    /** State panel. */
    HOST(ResourceKind.HOST, Icons.GRAPH_FRAME_ICON, "Graphs",
            "Current graph state"),
    /** Rule panel. */
    RULE(ResourceKind.RULE, Icons.RULE_FRAME_ICON, "Rules", "Selected rule"),
    /** LTS panel. */
    LTS(null, Icons.LTS_FRAME_ICON, "Simulation", "Simulation panel"),
    /** Type panel. */
    TYPE(ResourceKind.TYPE, Icons.TYPE_FRAME_ICON, "Types", "Type graphs"),
    /** Control panel. */
    CONTROL(ResourceKind.CONTROL, Icons.CONTROL_FRAME_ICON, "Control",
            "Control specifications"),
    /** Prolog panel. */
    PROLOG(ResourceKind.PROLOG, Icons.PROLOG_FRAME_ICON, "Prolog",
            "Prolog programs");

    private DisplayKind(ResourceKind resource, ImageIcon tabIcon, String title,
            String tip) {
        this.resource = resource;
        this.tabIcon = tabIcon;
        this.title = title;
        this.tip = tip;
    }

    /** Returns the icon that should be used on the tab for a display of this kind. */
    public final ImageIcon getTabIcon() {
        return this.tabIcon;
    }

    /** Returns the kind of resource displayed here, if any. */
    public final ResourceKind getResource() {
        return this.resource;
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

    private final ResourceKind resource;
    private final ImageIcon tabIcon;
    private final String title;
    private final String tip;

    /** Returns the display kind for a given resource kind. */
    public static final DisplayKind toDisplay(ResourceKind resource) {
        return resourceMap.get(resource);
    }

    private static final Map<ResourceKind,DisplayKind> resourceMap =
        new EnumMap<ResourceKind,DisplayKind>(ResourceKind.class);

    static {
        for (DisplayKind kind : EnumSet.allOf(DisplayKind.class)) {
            ResourceKind resource = kind.getResource();
            if (resource != null) {
                resourceMap.put(resource, kind);
            }
        }
    }
}