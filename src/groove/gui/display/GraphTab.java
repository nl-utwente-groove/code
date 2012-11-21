package groove.gui.display;

import static groove.gui.Options.SHOW_ANCHORS_OPTION;
import static groove.gui.Options.SHOW_ASPECTS_OPTION;
import static groove.gui.Options.SHOW_BIDIRECTIONAL_EDGES_OPTION;
import static groove.gui.Options.SHOW_NODE_IDS_OPTION;
import static groove.gui.Options.SHOW_UNFILTERED_EDGES_OPTION;
import static groove.gui.Options.SHOW_VALUE_NODES_OPTION;
import groove.graph.GraphProperties;
import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.dialog.GraphPreviewDialog;
import groove.gui.dialog.PropertiesTable;
import groove.gui.jgraph.AspectJGraph;
import groove.gui.jgraph.AspectJModel;
import groove.gui.jgraph.GraphJCell;
import groove.gui.jgraph.GraphJModel;
import groove.gui.tree.LabelTree;
import groove.gui.tree.RuleLevelTree;
import groove.view.GrammarModel;
import groove.view.aspect.AspectGraph;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;

import org.jgraph.JGraph;
import org.jgraph.graph.DefaultGraphModel.GraphModelEdit;

/** Display tab component showing a graph-based resource. */
final public class GraphTab extends ResourceTab implements UndoableEditListener {
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
        JGraphPanel<AspectJGraph> result = this.graphPanel;
        if (result == null) {
            this.graphPanel =
                result = new JGraphPanel<AspectJGraph>(getJGraph(), false);
            result.setFocusable(false);
            result.setEnabled(false);
            result.initialise();
            result.addRefreshListener(SHOW_ANCHORS_OPTION);
            result.addRefreshListener(SHOW_ASPECTS_OPTION);
            result.addRefreshListener(SHOW_NODE_IDS_OPTION);
            result.addRefreshListener(SHOW_VALUE_NODES_OPTION);
            result.addRefreshListener(SHOW_UNFILTERED_EDGES_OPTION);
            result.addRefreshListener(SHOW_BIDIRECTIONAL_EDGES_OPTION);
        }
        return result;
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
    protected JComponent getUpperInfoPanel() {
        JTabbedPane result = this.upperInfoPanel;
        if (result == null) {
            this.upperInfoPanel = result = new JTabbedPane();
            result.add(getLabelPanel());
            if (getResourceKind().hasProperties()) {
                JComponent propertiesPanel = getPropertiesPanel();
                JScrollPane scrollPanel = new JScrollPane(propertiesPanel);
                scrollPanel.setName(propertiesPanel.getName());
                scrollPanel.getViewport().setBackground(
                    propertiesPanel.getBackground());
                result.add(scrollPanel);
                result.addChangeListener(createInfoListener(true));
            }
        }
        if (getResourceKind().hasProperties()) {
            result.setSelectedIndex(getDisplay().getInfoTabIndex(true));
        }
        return result;
    }

    private TitledPanel getLabelPanel() {
        TitledPanel result = this.labelPanel;
        if (result == null) {
            LabelTree labelTree = getJGraph().getLabelTree();
            this.labelPanel =
                result =
                    new TitledPanel(Options.LABEL_PANE_TITLE, labelTree,
                        labelTree.createToolBar(), true);
            result.setTitled(false);
        }
        return result;
    }

    private PropertiesTable getPropertiesPanel() {
        PropertiesTable result = this.propertiesPanel;
        if (result == null) {
            this.propertiesPanel =
                result = new PropertiesTable(GraphProperties.KEYS, false);
            result.setName("Properties");
            result.addMouseListener(new EditMouseListener());
        }
        return result;
    }

    @Override
    protected JComponent getLowerInfoPanel() {
        JPanel result = this.levelTreePanel;
        final RuleLevelTree levelTree = getJGraph().getLevelTree();
        if (result == null && levelTree != null) {
            this.levelTreePanel =
                result =
                    new TitledPanel("Nesting levels", levelTree, null, true);
        }
        return levelTree != null && levelTree.isEnabled() ? result : null;
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
                loadGraphIntoJModel(jModel, graph);
            }
        }
        if (jModel == null) {
            name = null;
        }
        GraphJModel<?,?> oldJModel = getJModel();
        if (oldJModel != null) {
            oldJModel.removeUndoableEditListener(this);
        }
        getEditArea().setJModel(jModel);
        if (jModel != null) {
            jModel.addUndoableEditListener(this);
            getPropertiesPanel().setProperties(jModel.getProperties());
        }
        setName(name);
        getTabLabel().setTitle(name);
        updateErrors();
        return jModel != null;
    }

    /** Clones the graph with the given name, if any, and loads the clone into the model. */
    private void loadGraphIntoJModel(AspectJModel jModel, AspectGraph graph) {
        AspectGraph graphClone = graph.clone();
        graphClone.setFixed();
        jModel.loadGraph(graphClone);
    }

    @Override
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
    @Override
    public void updateGrammar(GrammarModel grammar) {
        this.jModelMap.clear();
        setResource(getName());
    }

    @Override
    public void undoableEditHappened(UndoableEditEvent e) {
        if (e.getEdit() instanceof GraphModelEdit) {
            try {
                getJModel().syncGraph();
                AspectGraph graph = getJModel().getGraph();
                // we need to clone the graph to properly freeze the next layout change
                getSimulatorModel().doAddGraph(getResourceKind(),
                    graph.clone(), true);
                getPropertiesPanel().setProperties(getJModel().getProperties());
            } catch (IOException e1) {
                // do nothing
            }
        }
    }

    /** Returns the underlying JGraph of this tab. */
    public final AspectJGraph getJGraph() {
        return this.jGraph;
    }

    /** Returns the underlying JGraph of this tab. */
    public final AspectJModel getJModel() {
        return getJGraph().getModel();
    }

    /** Returns the label tree associated with this tab. */
    public final LabelTree getLabelTree() {
        return getJGraph().getLabelTree();
    }

    /** Graph panel of this tab. */
    private JGraphPanel<AspectJGraph> graphPanel;
    /** Label panel of this tab. */
    private JTabbedPane upperInfoPanel;
    /** Properties panel of this tab. */
    private PropertiesTable propertiesPanel;
    /** Label panel of this tab. */
    private TitledPanel labelPanel;
    /** Level tree panel of this tab, if any. */
    private JPanel levelTreePanel;
    /** The jgraph instance used in this tab. */
    private final AspectJGraph jGraph;
    /** Mapping from resource names to aspect models. */
    private final Map<String,AspectJModel> jModelMap =
        new HashMap<String,AspectJModel>();

    private final static boolean DEBUG = false;
}