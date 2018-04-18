package groove.gui.action;

import groove.grammar.model.NamedResourceModel;
import groove.graph.Graph;
import groove.graph.GraphRole;
import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.gui.display.Display;
import groove.gui.display.DisplayKind;
import groove.gui.display.GraphEditorTab;
import groove.gui.display.GraphTab;
import groove.gui.display.ResourceDisplay;
import groove.gui.display.ResourceTab;
import groove.gui.jgraph.AspectJGraph;
import groove.gui.jgraph.JGraph;
import groove.io.external.Exportable;
import groove.io.external.Exporters;

/**
 * Action to save the content of a {@link JGraph},
 * as a graph or in some export format.
 * There is a discrepancy between exporter action for JGraphs and for displays: JGraph exports have no access to the original resource (if any)
 * and so an export initiated from a JGraph directly (as opposed for example form the menu) will never show an export option that requires a resource
 * @see Exporters#doExport
 */
public class ExportAction extends SimulatorAction {
    /** Constructs an instance of the action for a given display. */
    public ExportAction(Simulator simulator, DisplayKind displayKind) {
        // fill in a generic name, as the JGraph may not yet hold a graph.
        super(simulator, Options.EXPORT_ACTION_NAME, Icons.EXPORT_ICON);
        putValue(ACCELERATOR_KEY, Options.EXPORT_KEY);
        this.displayKind = displayKind;
        this.display = simulator.getDisplaysPanel()
            .getDisplay(displayKind);
        this.jGraph = null;
        this.isGraph = this.displayKind.isGraphBased();
    }

    /** Constructs an instance of the action. */
    public ExportAction(JGraph<?> jGraph) {
        // fill in a generic name, as the JGraph may not yet hold a graph.
        super(jGraph.getActions()
            .getSimulator(), Options.EXPORT_ACTION_NAME, Icons.EXPORT_ICON);
        putValue(ACCELERATOR_KEY, Options.EXPORT_KEY);
        this.display = null;
        this.displayKind = null;
        this.jGraph = jGraph;
        this.isGraph = true;
    }

    @Override
    public void execute() {
        Exportable exportable;
        if (this.isGraph) {
            // Export graph
            if (getResource() != null) {
                exportable = new Exportable(getJGraph(), getResource());
            } else {
                exportable = new Exportable(getJGraph());
            }
        } else {
            // Export resource
            exportable = new Exportable(getResource());
        }
        Exporters.doExport(exportable, getSimulator());
    }

    /** Refreshes the name of this action. */
    @Override
    public void refresh() {
        boolean setenabled = getSimulatorModel().getGrammar() != null;
        if (this.isGraph && setenabled) {
            JGraph<?> jGraph = getJGraph();
            setenabled = jGraph != null && jGraph.isEnabled();
        } else if (setenabled) {
            setenabled = getResource() != null;
        }
        setEnabled(setenabled);
        if (setenabled) {
            // there is certainly a graph, so now we can set the real action name
            putValue(NAME, getActionName());
            putValue(SHORT_DESCRIPTION, getActionName());
        } else {
            // When disabled, use generic description
            putValue(NAME, "Export...");
            putValue(SHORT_DESCRIPTION, "Export...");
        }
    }

    /** Returns the export action name for a given JGraph being saved. */
    private String getActionName() {
        String type = null;
        if (this.isGraph) {
            JGraph<?> jGraph = getJGraph();
            Graph graph = jGraph.getModel()
                .getGraph();
            GraphRole role = graph.getRole();
            boolean isState =
                jGraph instanceof AspectJGraph && ((AspectJGraph) jGraph).isForState();
            type = isState ? "State" : role.getDescription();
        } else {
            type = this.displayKind.getResource()
                .getDescription();
        }
        return "Export " + type + " ...";
    }

    /** Get active resource if any */
    private final NamedResourceModel<?> getResource() {
        if (!(this.display instanceof ResourceDisplay)) {
            return null;
        }

        ResourceTab tab = ((ResourceDisplay) this.display).getSelectedTab();
        if (tab == null) {
            return null;
        }
        return getGrammarModel().getResource(this.displayKind.getResource(), tab.getQualName());
    }

    // Get active graph if any
    private final JGraph<?> getJGraph() {
        assert(this.isGraph);
        if (this.jGraph == null) {
            switch (this.displayKind) {
            case HOST:
            case RULE:
            case TYPE:
                ResourceTab selectedTab = ((ResourceDisplay) this.display).getSelectedTab();
                return selectedTab == null ? null
                    : selectedTab instanceof GraphTab ? ((GraphTab) selectedTab).getJGraph()
                        : ((GraphEditorTab) selectedTab).getJGraph();
            case STATE:
                return getStateDisplay().getJGraph();
            case LTS:
                return getLtsDisplay().getJGraph();
            default:
                assert false;
                return null;
            }
        } else {
            return this.jGraph;
        }
    }

    /** The fixed JGraph with which this action is associated,
     * if it is not associated with a {@link Display}.
     */
    private final JGraph<?> jGraph;
    /**
     * The display with which this action is associated,
     * if it is not associated with a fixed {@link JGraph}.
     */
    private final Display display;
    /** The display kind, if the display is set. */
    private final DisplayKind displayKind;
    /** True if exporter for jgraphs, false otherwise. */
    private boolean isGraph;
}
