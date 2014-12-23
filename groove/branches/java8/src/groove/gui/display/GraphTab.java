package groove.gui.display;

import groove.grammar.aspect.AspectGraph;
import groove.grammar.model.GrammarModel;
import groove.grammar.model.Resource;
import groove.grammar.model.ResourceKind;
import groove.graph.GraphProperties;
import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.dialog.GraphPreviewDialog;
import groove.gui.dialog.PropertiesTable;
import groove.gui.jgraph.AspectJGraph;
import groove.gui.jgraph.AspectJModel;
import groove.gui.jgraph.JCell;
import groove.gui.tree.RuleLevelTree;
import groove.gui.tree.TypeTree;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;

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
                    JCell<?> errorCell = getJModel().get().getErrorMap().get(arg);
                    if (errorCell != null) {
                        getJGraph().setSelectionCell(errorCell);
                    }
                }
            }
        };
    }

    @Override
    public JGraphPanel<AspectGraph> getEditArea() {
        JGraphPanel<AspectGraph> result = this.editArea;
        if (result == null) {
            this.editArea = result = new JGraphPanel<AspectGraph>(getJGraph());
            result.setFocusable(false);
            result.setEnabled(false);
            result.initialise();
        }
        return result;
    }

    /** Graph panel of this tab. */
    private JGraphPanel<AspectGraph> editArea;

    @Override
    public boolean isDirty() {
        return false;
    }

    @Override
    public boolean isDirtMinor() {
        return true;
    }

    @Override
    public void setClean() {
        // do nothing
    }

    @Override
    public Icon getIcon() {
        return Icons.getMainTabIcon(getResourceKind()).getIcon();
    }

    @Override
    final public boolean isEditor() {
        return false;
    }

    @Override
    protected Optional<JComponent> getUpperInfoPanel() {
        JTabbedPane result = this.upperInfoPanel;
        if (result == null) {
            this.upperInfoPanel = result = new JTabbedPane();
            result.add(getLabelPanel());
            if (getResourceKind().hasProperties()) {
                JComponent propertiesPanel = getPropertiesPanel();
                JScrollPane scrollPanel = new JScrollPane(propertiesPanel);
                scrollPanel.setName(propertiesPanel.getName());
                scrollPanel.getViewport().setBackground(propertiesPanel.getBackground());
                result.add(scrollPanel);
                result.addChangeListener(createInfoListener(true));
            }
        }
        if (getResourceKind().hasProperties()) {
            result.setSelectedIndex(getDisplay().getInfoTabIndex(true));
        }
        return Optional.of(result);
    }

    /** Label panel of this tab. */
    private JTabbedPane upperInfoPanel;

    private TitledPanel getLabelPanel() {
        TitledPanel result = this.labelPanel;
        if (result == null) {
            TypeTree labelTree = getLabelTree();
            this.labelPanel =
                result =
                    new TitledPanel(Options.LABEL_PANE_TITLE, labelTree, labelTree.createToolBar(),
                        true);
            result.setTitled(false);
        }
        return result;
    }

    /** Label panel of this tab. */
    private TitledPanel labelPanel;

    private PropertiesTable getPropertiesPanel() {
        PropertiesTable result = this.propertiesPanel;
        if (result == null) {
            this.propertiesPanel = result = new PropertiesTable(GraphProperties.Key.class, false);
            result.setName("Properties");
            result.addMouseListener(new EditMouseListener());
        }
        return result;
    }

    /** Properties panel of this tab. */
    private PropertiesTable propertiesPanel;

    @Override
    protected Optional<JComponent> getLowerInfoPanel() {
        Optional<JComponent> result;
        Optional<RuleLevelTree> levelTree = getLevelTree();
        if (getLevelTree().map(t -> t.isEnabled()).orElse(false)) {
            if (this.lowerInfoPanel == null) {
                this.lowerInfoPanel =
                    new TitledPanel("Nesting levels", levelTree.get(), null, true);
            }
            result = Optional.of(this.lowerInfoPanel);
        } else {
            result = Optional.empty();
        }
        return result;
    }

    /** Level tree panel of this tab, if any. */
    private JPanel lowerInfoPanel;

    /** Lazily creates and returns the (possibly {@code null}) rule level tree. */
    private Optional<RuleLevelTree> getLevelTree() {
        if (this.levelTree == null && getResourceKind() == ResourceKind.RULE) {
            this.levelTree = new RuleLevelTree(getJGraph());
        }
        return Optional.ofNullable(this.levelTree);
    }

    private RuleLevelTree levelTree;

    /** Lazily creates and returns the (non-{@code null}) label tree. */
    private TypeTree getLabelTree() {
        TypeTree result = this.labelTree;
        if (result == null) {
            result = this.labelTree = new TypeTree(getJGraph(), true);
        }
        return result;
    }

    private TypeTree labelTree;

    @Override
    public Optional<Resource> getResource() {
        return getJModel().map(m -> m.getGraph());
    }

    @Override
    protected void setResource(Resource res) {
        getJModel().ifPresent(m -> m.removeUndoableEditListener(this));
        String name = res.getName();
        AspectJModel jModel = this.jModelMap.get(name);
        if (jModel == null) {
            if (DEBUG) {
                GraphPreviewDialog.showGraph(((AspectGraph) res).normalise(null));
            }
            jModel = getJGraph().newJModel();
            this.jModelMap.put(name, jModel);
            loadGraphIntoJModel(jModel, (AspectGraph) res);
        }
        getJGraph().setModel(jModel);
        jModel.addUndoableEditListener(this);
        getPropertiesPanel().setProperties(jModel.getProperties());
        updateErrors();
        super.setResource(res);
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
            getJModel().ifPresent(m -> m.removeUndoableEditListener(this));
            getJGraph().setModel(null);
            updateErrors();
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
                AspectJModel jModel = getJModel().get();
                jModel.syncGraph();
                // we need to clone the graph to properly freeze the next layout change
                AspectGraph graphClone = jModel.getGraph().clone();
                graphClone.setFixed();
                getSimulatorModel().doAdd(graphClone, true);
                getPropertiesPanel().setProperties(jModel.getProperties());
            } catch (IOException e1) {
                // do nothing
            }
        }
    }

    /** Returns the underlying JGraph of this tab. */
    public final AspectJGraph getJGraph() {
        if (this.jGraph == null) {
            this.jGraph = new AspectJGraph(getSimulator(), getDisplay().getKind(), false);
            this.jGraph.setLabelTree(getLabelTree());
            getLevelTree().ifPresent(t -> this.jGraph.setLevelTree(t));
        }
        return this.jGraph;
    }

    /** The jgraph instance used in this tab. */
    private AspectJGraph jGraph;

    /** Returns the underlying JGraph of this tab. */
    public final Optional<AspectJModel> getJModel() {
        return getJGraph().getJModel();
    }

    /** Mapping from resource names to aspect models. */
    private final Map<String,AspectJModel> jModelMap = new HashMap<>();

    private final static boolean DEBUG = false;
}