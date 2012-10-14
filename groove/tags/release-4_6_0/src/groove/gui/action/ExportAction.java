package groove.gui.action;

import groove.graph.Graph;
import groove.graph.GraphRole;
import groove.gui.Display;
import groove.gui.Display.Tab;
import groove.gui.DisplayKind;
import groove.gui.GraphEditorTab;
import groove.gui.GraphTab;
import groove.gui.Icons;
import groove.gui.LTSDisplay;
import groove.gui.Options;
import groove.gui.ResourceDisplay;
import groove.gui.Simulator;
import groove.gui.dialog.ErrorDialog;
import groove.gui.dialog.SaveDialog;
import groove.gui.jgraph.AspectJGraph;
import groove.gui.jgraph.GraphJGraph;
import groove.io.external.Exporter;

import java.io.File;
import java.io.IOException;

/**
 * Action to save the content of a {@link GraphJGraph},
 * as a graph or in some export format.
 * @see Exporter#export(GraphJGraph, File)
 */
public class ExportAction extends SimulatorAction {
    /** Constructs an instance of the action for a given display. */
    public ExportAction(Simulator simulator, DisplayKind displayKind) {
        // fill in a generic name, as the JGraph may not yet hold a graph.
        super(simulator, Options.EXPORT_ACTION_NAME, Icons.EXPORT_ICON);
        putValue(ACCELERATOR_KEY, Options.EXPORT_KEY);
        this.displayKind = displayKind;
        this.display = simulator.getDisplaysPanel().getDisplayFor(displayKind);
        this.jGraph = null;
        assert this.displayKind.isGraphBased();
    }

    /** Constructs an instance of the action. */
    public ExportAction(GraphJGraph jGraph) {
        // fill in a generic name, as the JGraph may not yet hold a graph.
        super(jGraph.getActions().getSimulator(), Options.EXPORT_ACTION_NAME,
            Icons.EXPORT_ICON);
        putValue(ACCELERATOR_KEY, Options.EXPORT_KEY);
        this.display = null;
        this.displayKind = null;
        this.jGraph = jGraph;
    }

    @Override
    public void execute() {
        GraphJGraph jGraph = getJGraph();
        String fileName = jGraph.getModel().getName();
        if (fileName != null) {
            this.exporter.getFileChooser().setSelectedFile(new File(fileName));
        }
        File selectedFile =
            SaveDialog.show(this.exporter.getFileChooser(), jGraph, null);
        // now save, if so required
        if (selectedFile != null) {
            try {
                this.exporter.export(jGraph, selectedFile);
            } catch (IOException exc) {
                new ErrorDialog(jGraph, "Error while exporting to "
                    + selectedFile, exc).setVisible(true);
            }

        }
    }

    /** Refreshes the name of this action. */
    @Override
    public void refresh() {
        GraphJGraph jGraph = getJGraph();
        boolean enabled = jGraph != null && jGraph.isEnabled();
        setEnabled(enabled);
        if (enabled) {
            // there is certainly a graph, so now we can set the real action name
            Graph<?,?> graph = jGraph.getModel().getGraph();
            GraphRole role = graph.getRole();
            putValue(NAME, getActionName(role));
            putValue(SHORT_DESCRIPTION, getActionName(role));
        }
    }

    /** Returns the export action name for a given JGraph being saved. */
    private String getActionName(GraphRole role) {
        GraphJGraph jGraph = getJGraph();
        boolean isState =
            jGraph instanceof AspectJGraph
                && ((AspectJGraph) jGraph).isForState();
        String type = isState ? "State" : role.getDescription();
        return "Export " + type + " ...";
    }

    private final GraphJGraph getJGraph() {
        if (this.jGraph == null) {
            switch (this.displayKind) {
            case HOST:
            case RULE:
            case TYPE:
                Tab selectedTab =
                    ((ResourceDisplay) this.display).getSelectedTab();
                return selectedTab == null ? null
                        : selectedTab instanceof GraphTab
                                ? ((GraphTab) selectedTab).getJGraph()
                                : ((GraphEditorTab) selectedTab).getJGraph();
            case LTS:
                LTSDisplay ltsDisplay = (LTSDisplay) this.display;
                return ltsDisplay.isStateTabSelected()
                        ? ltsDisplay.getStateTab().getJGraph()
                        : ltsDisplay.getLtsJGraph();
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
    private final GraphJGraph jGraph;
    /** 
     * The display with which this action is associated,
     * if it is not associated with a fixed {@link GraphJGraph}.
     */
    private final Display display;
    /** The display kind, if the display is set. */
    private final DisplayKind displayKind;
    private final Exporter exporter = Exporter.getInstance();
}