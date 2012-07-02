package groove.gui;

import static groove.gui.Options.SHOW_ANCHORS_OPTION;
import static groove.gui.Options.SHOW_ASPECTS_OPTION;
import static groove.gui.Options.SHOW_BIDIRECTIONAL_EDGES_OPTION;
import static groove.gui.Options.SHOW_NODE_IDS_OPTION;
import static groove.gui.Options.SHOW_UNFILTERED_EDGES_OPTION;
import static groove.gui.Options.SHOW_VALUE_NODES_OPTION;
import groove.gui.ResourceDisplay.MainTab;
import groove.gui.dialog.GraphPreviewDialog;
import groove.gui.jgraph.AspectJGraph;
import groove.gui.jgraph.AspectJModel;
import groove.gui.jgraph.GraphJCell;
import groove.view.GrammarModel;
import groove.view.aspect.AspectGraph;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.jgraph.JGraph;
import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;

/** Display tab component showing a graph-based resource. */
final public class GraphTab extends ResourceTab implements MainTab {
    /**
     * Constructs the instance of this tab for a given simulator and
     * resource kind.
     */
    public GraphTab(ResourceDisplay display) {
        super(display);
        this.jGraph =
            new AspectJGraph(getSimulator(), display.getKind(), false);
        setFocusable(false);
        setEnabled(false);
        start();
    }

    @Override
    protected void start() {
        super.start();
        getJGraph().setToolTipEnabled(true);
        getJGraph().addMouseListener(new EditMouseListener());
    }

    @Override
    protected Observer createErrorListener() {
        return new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                if (arg != null) {
                    GraphJCell errorCell = getJModel().getErrorMap().get(arg);
                    if (errorCell != null) {
                        getJGraph().setSelectionCell(errorCell);
                    }
                }
            }
        };
    }

    @Override
    public JGraphPanel<AspectJGraph> getEditArea() {
        if (this.graphPanel == null) {
            this.graphPanel = new GraphPanel(getJGraph());
        }
        return this.graphPanel;
    }

    @Override
    public boolean isDirty() {
        return false;
    }

    @Override
    public void setClean() {
        // do nothing
    }

    @Override
    protected void saveResource() {
        // do nothing
    }

    @Override
    public Icon getIcon() {
        return Icons.getMainTabIcon(getResourceKind());
    }

    @Override
    final public boolean isEditor() {
        return false;
    }

    @Override
    public boolean setResource(String name) {
        AspectJModel jModel = this.jModelMap.get(name);
        if (jModel == null && name != null) {
            AspectGraph graph =
                getSimulatorModel().getStore().getGraphs(getResourceKind()).get(
                    name);
            if (graph != null) {
                if (DEBUG) {
                    GraphPreviewDialog.showGraph(graph.normalise(null));
                }
                this.jModelMap.put(name, jModel = getJGraph().newModel());
                jModel.loadGraph(graph);
            }
        }
        if (jModel == null) {
            name = null;
        }
        getEditArea().setJModel(jModel);
        setName(name);
        getTabLabel().setTitle(name);
        updateErrors();
        return jModel != null;
    }

    public boolean removeResource(String name) {
        boolean result = name.equals(getName());
        this.jModelMap.remove(name);
        if (result) {
            setResource(null);
        }
        return result;
    }

    /** 
     * Notifies the tab that the grammar has changed.
     * This resets the internal data structures, and informs the
     * underlying {@link JGraph} of the type change.
     */
    public void updateGrammar(GrammarModel grammar) {
        this.jModelMap.clear();
        setResource(getName());
    }

    /** Returns the underlying JGraph of this tab. */
    public final AspectJGraph getJGraph() {
        return this.jGraph;
    }

    /** Returns the underlying JGraph of this tab. */
    public final AspectJModel getJModel() {
        return getJGraph().getModel();
    }

    /** Graph panel of this tab. */
    private GraphPanel graphPanel;
    /** The jgraph instance used in this tab. */
    private final AspectJGraph jGraph;
    /** Mapping from resource names to aspect models. */
    private final Map<String,AspectJModel> jModelMap =
        new HashMap<String,AspectJModel>();

    private final static boolean DEBUG = false;

    private class GraphPanel extends JGraphPanel<AspectJGraph> {
        /**
         * Constructs the instance of this tab for a given simulator and
         * resource kind.
         */
        public GraphPanel(AspectJGraph jGraph) {
            super(jGraph, false);
            setFocusable(false);
            setEnabled(false);
            initialise();
        }

        @Override
        protected void installListeners() {
            super.installListeners();
            addRefreshListener(SHOW_ANCHORS_OPTION);
            addRefreshListener(SHOW_ASPECTS_OPTION);
            addRefreshListener(SHOW_NODE_IDS_OPTION);
            addRefreshListener(SHOW_VALUE_NODES_OPTION);
            addRefreshListener(SHOW_UNFILTERED_EDGES_OPTION);
            addRefreshListener(SHOW_BIDIRECTIONAL_EDGES_OPTION);
        }

        @Override
        protected JComponent createLabelPane() {
            JComponent result;
            JComponent labelPane = super.createLabelPane();
            final RuleLevelTree levelTree = getJGraph().getLevelTree();
            if (levelTree == null) {
                result = labelPane;
            } else {
                final JPanel levelTreePanel = createLevelTreePanel(levelTree);
                final JSplitPane splitPane =
                    new JSplitPane(JSplitPane.VERTICAL_SPLIT, labelPane,
                        levelTreePanel);
                splitPane.setResizeWeight(0.75);
                // deselect the level tree whenever the graph
                // selection changes
                getJGraph().addGraphSelectionListener(
                    new GraphSelectionListener() {
                        @Override
                        public void valueChanged(GraphSelectionEvent e) {
                            levelTree.clearSelection();
                        }
                    });
                levelTree.addPropertyChangeListener("enabled",
                    new PropertyChangeListener() {
                        @Override
                        public void propertyChange(PropertyChangeEvent evt) {
                            boolean enabled = (Boolean) evt.getNewValue();
                            levelTreePanel.setVisible(enabled);
                            splitPane.resetToPreferredSizes();
                        }
                    });
                result = splitPane;
            }
            return result;
        }

        /** Creates a panel for the rule level tree. */
        private JPanel createLevelTreePanel(final RuleLevelTree levelTree) {
            final JPanel result = new JPanel(new BorderLayout(), false);
            Box labelPaneTop = Box.createVerticalBox();
            JLabel labelPaneTitle =
                new JLabel(" " + Options.RULE_TREE_PANE_TITLE + " ");
            labelPaneTitle.setAlignmentX(LEFT_ALIGNMENT);
            labelPaneTop.add(labelPaneTitle);
            result.add(labelPaneTop, BorderLayout.NORTH);
            result.add(createLabelScrollPane(levelTree), BorderLayout.CENTER);
            result.setPreferredSize(new Dimension(0, 70));
            levelTree.addPropertyChangeListener("enabled",
                new PropertyChangeListener() {
                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        boolean enabled = (Boolean) evt.getNewValue();
                        result.setVisible(enabled);
                        if (enabled) {
                            result.setSize(result.getPreferredSize());
                        }
                    }
                });
            return result;
        }
    }
}