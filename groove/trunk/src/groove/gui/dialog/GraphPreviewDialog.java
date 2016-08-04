package groove.gui.dialog;

import java.awt.Dialog;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import groove.control.graph.ControlGraph;
import groove.grammar.aspect.GraphConverter;
import groove.grammar.model.GrammarModel;
import groove.grammar.model.ResourceKind;
import groove.graph.Graph;
import groove.graph.GraphRole;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.gui.display.DisplayKind;
import groove.gui.display.JGraphPanel;
import groove.gui.jgraph.AspectJGraph;
import groove.gui.jgraph.CtrlJGraph;
import groove.gui.jgraph.JGraph;
import groove.gui.jgraph.JModel;
import groove.gui.jgraph.LTSJGraph;
import groove.gui.jgraph.PlainJGraph;

/**
 * Dialog showing an given graph in the most appropriate
 * GUI component.
 */
public class GraphPreviewDialog<G extends Graph> extends JDialog {
    /** Constructs a new dialog, for a given graph. */
    public GraphPreviewDialog(GrammarModel grammar, G graph) {
        this(null, grammar, graph);
    }

    /** Constructs a new dialog, for a given graph. */
    public GraphPreviewDialog(Simulator simulator, G graph) {
        this(simulator, null, graph);
    }

    /** Constructs a new dialog, for a given graph. */
    private GraphPreviewDialog(Simulator simulator, GrammarModel grammar, G graph) {
        super(simulator == null ? null : simulator.getFrame());
        this.simulator = simulator;
        this.grammar = grammar;
        this.graph = graph;
        setTitle(graph.getName());
        if (simulator != null) {
            Point p = simulator.getFrame()
                .getLocation();
            setLocation(new Point(p.x + 50, p.y + 50));
        }
        add(getContent());
        setSize(600, 700);
        if (simulator == null) {
            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        }
        pack();
    }

    /** Returns the main panel shown on this dialog. */
    public GraphPreviewPanel getContent() {
        if (this.contentPanel == null) {
            this.contentPanel = new GraphPreviewPanel(getJGraph());
            this.contentPanel.initialise();
            this.contentPanel.setEnabled(true);
            // make any dialog in which this panel is embedded resizable
            // taken from https://blogs.oracle.com/scblog/entry/tip_making_joptionpane_dialog_resizable
            this.contentPanel.addHierarchyListener(new HierarchyListener() {
                @Override
                public void hierarchyChanged(HierarchyEvent e) {
                    Window window =
                        SwingUtilities.getWindowAncestor(GraphPreviewDialog.this.contentPanel);
                    if (window instanceof Dialog) {
                        Dialog dialog = (Dialog) window;
                        if (!dialog.isResizable()) {
                            dialog.setResizable(true);
                        }
                    }
                }
            });
        }
        return this.contentPanel;
    }

    private GraphPreviewPanel contentPanel;

    /** Returns the JGraph shown on this dialog. */
    private JGraph<G> getJGraph() {
        if (this.jGraph == null) {
            this.jGraph = createJGraph();
        }
        return this.jGraph;
    }

    /** Returns the proper jGraph for the graph set in the constructor. */
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected JGraph<G> createJGraph() {
        JGraph jGraph;
        Graph shownGraph = this.graph;
        switch (this.graph.getRole()) {
        case CTRL:
            if (shownGraph instanceof ControlGraph) {
                jGraph = new CtrlJGraph(this.simulator);
            } else {
                jGraph = null;
            }
            break;
        case HOST:
        case TYPE:
        case RULE:
            if (this.simulator != null || this.grammar != null) {
                shownGraph = GraphConverter.toAspect(this.graph);
                DisplayKind kind =
                    DisplayKind.toDisplay(ResourceKind.toResource(this.graph.getRole()));
                AspectJGraph aspectJGraph = new AspectJGraph(this.simulator, kind, false);
                if (this.simulator == null) {
                    aspectJGraph.setGrammar(this.grammar);
                }
                jGraph = aspectJGraph;
            } else {
                jGraph = null;
            }
            break;
        case LTS:
            jGraph = new LTSJGraph(this.simulator);
            break;
        default:
            jGraph = null;
        }
        if (jGraph == null) {
            jGraph = PlainJGraph.newInstance(this.simulator);
        }
        JModel<G> model = jGraph.newModel();
        model.loadGraph((G) shownGraph);
        jGraph.setModel(model);
        jGraph.doLayout(false);
        return jGraph;
    }

    private JGraph<G> jGraph;
    /** The graph to be displayed in the dialog. */
    protected final G graph;
    /** The simulator reference, may be null. */
    protected final Simulator simulator;
    /** The grammar model in case the simulator is null. */
    protected final GrammarModel grammar;

    /** Sets the static simulator in a global variable,
     * to be used by calls to {@link #showGraph(Graph)}.
     * @param simulator the simulator to be used by {@link #showGraph(Graph)}
     */
    public static void setSimulator(Simulator simulator) {
        GraphPreviewDialog.globalSimulator = simulator;
    }

    /**
     * Creates a dialog for the given graph, and sets it to visible.
     */
    public static void showGraph(Graph graph) {
        showGraph(globalSimulator, graph);
    }

    /**
     * Creates a dialog for the given graph and (possibly {@code null})
     * simulator, and sets it to visible.
     */
    public static <G extends Graph> void showGraph(Simulator simulator, G graph) {
        final GraphRole role = graph.getRole();
        final String name = graph.getName();
        synchronized (recentPreviews) {
            if (!TIMER || recentPreviews.get(role)
                .add(name)) {
                new GraphPreviewDialog<>(simulator, graph).setVisible(true);
                if (TIMER) {
                    final Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            synchronized (recentPreviews) {
                                recentPreviews.get(role)
                                    .remove(name);
                            }
                            timer.cancel();
                        }
                    }, 1000);
                }
            }
        }
    }

    /** Creates a panel showing a preview of a given graph. */
    static public GraphPreviewPanel createPanel(GrammarModel grammar, Graph graph) {
        return new GraphPreviewDialog<>(grammar, graph).getContent();
    }

    private static Simulator globalSimulator;
    private static Map<GraphRole,Set<String>> recentPreviews =
        new EnumMap<>(GraphRole.class);

    static {
        for (GraphRole role : GraphRole.values()) {
            recentPreviews.put(role, new HashSet<String>());
        }
    }

    private static final boolean TIMER = true;

    /** A panel showing a JGraph, with functionality te retrieve the rendering options. */
    public static class GraphPreviewPanel extends JGraphPanel<Graph> {
        /** Creates a panel for a given JGraph. */
        public GraphPreviewPanel(JGraph<? extends Graph> jGraph) {
            super(jGraph);
        }

        /** Returns the options object used in rendering the JGraph. */
        public Options getOptions() {
            return getJGraph().getOptions();
        }
    }
}