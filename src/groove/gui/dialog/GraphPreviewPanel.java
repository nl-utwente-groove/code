package groove.gui.dialog;

import groove.control.CtrlAut;
import groove.grammar.aspect.GraphConverter;
import groove.grammar.model.GrammarModel;
import groove.grammar.model.ResourceKind;
import groove.graph.Graph;
import groove.gui.Simulator;
import groove.gui.display.DisplayKind;
import groove.gui.display.JGraphPanel;
import groove.gui.jgraph.AspectJGraph;
import groove.gui.jgraph.CtrlJGraph;
import groove.gui.jgraph.JGraph;
import groove.gui.jgraph.JModel;
import groove.gui.jgraph.LTSJGraph;
import groove.gui.jgraph.PlainJGraph;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Window;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Dialog showing an given graph in the most appropriate
 * GUI component. 
 */
public class GraphPreviewPanel extends JPanel {
    /** Constructs a new dialog, for a given graph. */
    public GraphPreviewPanel(GrammarModel grammar, Graph graph) {
        this(null, grammar, graph);
    }

    /** Constructs a new dialog, for a given graph. */
    public GraphPreviewPanel(Simulator simulator, Graph graph) {
        this(simulator, null, graph);
    }

    /** Constructs a new dialog, for a given graph. */
    GraphPreviewPanel(Simulator simulator, GrammarModel grammar, Graph graph) {
        super(new BorderLayout());
        this.grammar = grammar;
        this.simulator = simulator;
        this.graph = graph;
        JGraphPanel<Graph> autPanel = new JGraphPanel<Graph>(getJGraph());
        autPanel.initialise();
        autPanel.setEnabled(true);
        this.add(autPanel);
        // make any dialog in which this panel is embedded resizable
        // taken from https://blogs.oracle.com/scblog/entry/tip_making_joptionpane_dialog_resizable
        addHierarchyListener(new HierarchyListener() {
            public void hierarchyChanged(HierarchyEvent e) {
                Window window =
                    SwingUtilities.getWindowAncestor(GraphPreviewPanel.this);
                if (window instanceof Dialog) {
                    Dialog dialog = (Dialog) window;
                    if (!dialog.isResizable()) {
                        dialog.setResizable(true);
                    }
                }
            }
        });
    }

    /** Returns the JGraph shown on this dialog. */
    public JGraph<Graph> getJGraph() {
        if (this.jGraph == null) {
            this.jGraph = createJGraph();
        }
        return this.jGraph;
    }

    /** Returns the proper jGraph for the graph set in the constructor. */
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected JGraph<Graph> createJGraph() {
        JGraph jGraph = null;
        Graph shownGraph = this.graph;
        switch (this.graph.getRole()) {
        case CTRL:
            if (shownGraph instanceof CtrlAut) {
                jGraph = new CtrlJGraph(this.simulator);
            }
            break;
        case HOST:
        case TYPE:
        case RULE:
            if (this.simulator != null || this.grammar != null) {
                shownGraph = GraphConverter.toAspect(this.graph);
                DisplayKind kind =
                    DisplayKind.toDisplay(ResourceKind.toResource(this.graph.getRole()));
                AspectJGraph aspectJGraph =
                    new AspectJGraph(this.simulator, kind, false);
                if (this.simulator == null) {
                    aspectJGraph.setGrammar(this.grammar);
                }
                jGraph = aspectJGraph;
            }
            break;
        case LTS:
            jGraph = new LTSJGraph(this.simulator);
            break;
        }
        if (jGraph == null) {
            jGraph = PlainJGraph.newInstance(this.simulator);
        }
        JModel<Graph> model = jGraph.newModel();
        model.loadGraph(shownGraph);
        jGraph.setModel(model);
        jGraph.doLayout(true);
        return jGraph;
    }

    private JGraph<Graph> jGraph;
    /** The graph to be displayed in the dialog. */
    private final Graph graph;
    /** The simulator reference, may be null. */
    private final Simulator simulator;
    /** The grammar model in case the simulator is null. */
    private final GrammarModel grammar;
}