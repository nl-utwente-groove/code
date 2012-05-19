package groove.gui.dialog;

import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.GraphRole;
import groove.gui.DisplayKind;
import groove.gui.JGraphPanel;
import groove.gui.Simulator;
import groove.gui.jgraph.AspectJGraph;
import groove.gui.jgraph.CtrlJGraph;
import groove.gui.jgraph.GraphJGraph;
import groove.gui.jgraph.GraphJGraph.AttributeFactory;
import groove.gui.jgraph.GraphJModel;
import groove.gui.jgraph.LTSJGraph;
import groove.trans.ResourceKind;
import groove.verify.BuchiGraph;
import groove.verify.BuchiLocation;

import java.awt.Color;
import java.awt.Point;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JDialog;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.GraphConstants;

/**
 * Dialog showing an given graph in the most appropriate
 * GUI component. 
 */
public class GraphPreviewDialog extends JDialog {
    /** Constructs a new dialog, for a given graph. */
    public GraphPreviewDialog(Simulator simulator, Graph<?,?> graph) {
        super(simulator == null ? null : simulator.getFrame());
        this.simulator = simulator;
        this.graph = graph;
        if (simulator != null) {
            Point p = simulator.getFrame().getLocation();
            setLocation(new Point(p.x + 50, p.y + 50));
        }
        JGraphPanel<GraphJGraph> autPanel =
            new JGraphPanel<GraphJGraph>(getJGraph(), true);
        autPanel.initialise();
        autPanel.setEnabled(true);
        add(autPanel);
        setSize(600, 700);
        if (simulator == null) {
            this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        }
    }

    private GraphJGraph getJGraph() {
        if (this.jGraph == null) {
            this.jGraph = createJGraph();
        }
        return this.jGraph;
    }

    /** Returns the proper jGraph for the graph set in the constructor. */
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected GraphJGraph createJGraph() {
        GraphJGraph jGraph;
        switch (this.graph.getRole()) {
        case BUCHI:
            jGraph =
                GraphJGraph.createJGraph(this.graph, new AttributeFactory() {
                    @Override
                    public AttributeMap getAttributes(Edge edge) {
                        return null;
                    }

                    @Override
                    public AttributeMap getAttributes(groove.graph.Node node) {
                        BuchiLocation location = (BuchiLocation) node;
                        AttributeMap result = new AttributeMap();
                        if (location.isAccepting()) {
                            GraphConstants.setBackground(result, Color.orange);
                        }
                        if (location.equals(((BuchiGraph) GraphPreviewDialog.this.graph).getInitial())) {
                            GraphConstants.setLineWidth(result, 3);
                        }
                        return result;
                    }
                });
            break;
        case CTRL:
            jGraph = new CtrlJGraph(this.simulator);
            break;
        case TYPE:
        case RULE:
        case HOST:
            DisplayKind kind =
                DisplayKind.toDisplay(ResourceKind.toResource(this.graph.getRole()));
            jGraph = new AspectJGraph(this.simulator, kind, false);
            break;
        case LTS:
            jGraph = new LTSJGraph(this.simulator);
            break;
        default:
            jGraph = new GraphJGraph(this.simulator, false);
        }
        GraphJModel<?,?> model = jGraph.newModel();
        model.loadGraph((Graph) this.graph);
        jGraph.setModel(model);
        jGraph.doGraphLayout();
        return jGraph;
    }

    private GraphJGraph jGraph;
    /** The graph to be displayed in the dialog. */
    protected final Graph<?,?> graph;
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
    public static void showGraph(Graph<?,?> graph) {
        showGraph(globalSimulator, graph);
    }

    /**
     * Creates a dialog for the given graph and (possibly {@code null}) 
     * simulator, and sets it to visible.
     */
    public static void showGraph(Simulator simulator, Graph<?,?> graph) {
        final GraphRole role = graph.getRole();
        final String name = graph.getName();
        synchronized (recentPreviews) {
            if (!TIMER || recentPreviews.get(role).add(name)) {
                new GraphPreviewDialog(simulator, graph).setVisible(true);
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
        for (GraphRole role : EnumSet.allOf(GraphRole.class)) {
            recentPreviews.put(role, new HashSet<String>());
        }
    }
    private static final boolean TIMER = true;
}