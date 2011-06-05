package groove.gui;

import groove.graph.GraphRole;
import groove.trans.ResourceKind;

import javax.swing.ImageIcon;

/** Type of components in the panel. */
public enum DisplayKind {
    /** State panel. */
    HOST(ResourceKind.HOST, Icons.GRAPH_FRAME_ICON, Icons.EDIT_GRAPH_ICON,
            Icons.GRAPH_LIST_ICON, "Graphs", "Current graph state"),
    /** Rule panel. */
    RULE(ResourceKind.RULE, Icons.RULE_FRAME_ICON, Icons.EDIT_RULE_ICON,
            Icons.RULE_LIST_ICON, "Rules", "Selected rule"),
    /** LTS panel. */
    LTS(null, Icons.LTS_FRAME_ICON, null, null, "State space",
            "Labelled transition system"),
    /** Type panel. */
    TYPE(ResourceKind.TYPE, Icons.TYPE_FRAME_ICON, Icons.EDIT_TYPE_ICON,
            Icons.TYPE_LIST_ICON, "Types", "Type graphs"),
    /** Control panel. */
    CONTROL(ResourceKind.CONTROL, Icons.CONTROL_FRAME_ICON,
            Icons.EDIT_CONTROL_ICON, Icons.CONTROL_LIST_ICON, "Control",
            "Control specifications"),
    /** Prolog panel. */
    PROLOG(ResourceKind.PROLOG, Icons.PROLOG_FRAME_ICON,
            Icons.EDIT_PROLOG_ICON, Icons.PROLOG_LIST_ICON, "Prolog",
            "Prolog programs");

    private DisplayKind(ResourceKind resource, ImageIcon tabIcon,
            ImageIcon editIcon, ImageIcon listIcon, String title, String tip) {
        this.resource = resource;
        this.tabIcon = tabIcon;
        this.editIcon = editIcon;
        this.listIcon = listIcon;
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

    /** Returns the icon that should be used for the label list. */
    public final ImageIcon getListIcon() {
        return this.listIcon;
    }

    /** Returns the icon that should be used for editors in this display. */
    public final ImageIcon getEditIcon() {
        return this.editIcon;
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
    private final ImageIcon editIcon;
    private final ImageIcon listIcon;
    private final String title;
    private final String tip;
}