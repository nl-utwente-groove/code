package groove.gui.dialog;

import groove.grammar.aspect.AspectGraph;
import groove.grammar.host.HostGraph;
import groove.grammar.model.ResourceKind;
import groove.graph.Graph;
import groove.graph.GraphRole;
import groove.gui.Simulator;
import groove.gui.display.DisplayKind;
import groove.gui.display.JGraphPanel;
import groove.gui.jgraph.AspectJGraph;
import groove.gui.jgraph.CtrlJGraph;
import groove.gui.jgraph.JGraph;
import groove.gui.jgraph.JModel;
import groove.gui.jgraph.LTSJGraph;
import groove.gui.jgraph.PlainJGraph;

import java.awt.Point;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JDialog;

/**
 * Dialog showing an given graph in the most appropriate
 * GUI component. 
 */
public class GraphPreviewDialog<G extends Graph> extends JDialog {
    /** Constructs a new dialog, for a given graph. */
    public GraphPreviewDialog(Simulator simulator, G graph) {
        super(simulator == null ? null : simulator.getFrame());
        this.simulator = simulator;
        this.graph = graph;
        setTitle(graph.getName());
        if (simulator != null) {
            Point p = simulator.getFrame().getLocation();
            setLocation(new Point(p.x + 50, p.y + 50));
        }
        JGraphPanel<G> autPanel = new JGraphPanel<G>(getJGraph());
        autPanel.initialise();
        autPanel.setEnabled(true);
        add(autPanel);
        setSize(600, 700);
        if (simulator == null) {
            this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        }
    }

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
            jGraph = new CtrlJGraph(this.simulator);
            break;
        case HOST:
        case TYPE:
        case RULE:
            if (this.graph instanceof HostGraph && this.simulator != null) {
                shownGraph =
                    ((HostGraph) this.graph).toAspectMap().getAspectGraph();
            }
            if (shownGraph instanceof AspectGraph) {
                DisplayKind kind =
                    DisplayKind.toDisplay(ResourceKind.toResource(this.graph.getRole()));
                jGraph = new AspectJGraph(this.simulator, kind, false);
            } else {
                jGraph = PlainJGraph.newInstance(this.simulator);
            }
            break;
        case LTS:
            jGraph = new LTSJGraph(this.simulator);
            break;
        default:
            jGraph = PlainJGraph.newInstance(this.simulator);
        }
        JModel<G> model = jGraph.newModel();
        model.loadGraph(this.graph);
        jGraph.setModel(model);
        jGraph.doLayout(true);
        return jGraph;
    }

    private JGraph<G> jGraph;
    /** The graph to be displayed in the dialog. */
    protected final G graph;
    /** The simulator reference, may be null. */
    protected final Simulator simulator;

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
            if (!TIMER || recentPreviews.get(role).add(name)) {
                new GraphPreviewDialog<G>(simulator, graph).setVisible(true);
                if (TIMER) {
                    final Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            synchronized (recentPreviews) {
                                recentPreviews.get(role).remove(name);
                            }
                            timer.cancel();
                        }
                    }, 1000);
                }
            }
        }
    }

    private static Simulator globalSimulator;
    private static Map<GraphRole,Set<String>> recentPreviews =
        new EnumMap<GraphRole,Set<String>>(GraphRole.class);
    static {
        for (GraphRole role : GraphRole.values()) {
            recentPreviews.put(role, new HashSet<String>());
        }
    }
    private static final boolean TIMER = true;
}