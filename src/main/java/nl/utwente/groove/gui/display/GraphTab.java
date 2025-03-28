package nl.utwente.groove.gui.display;

import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.jgraph.JGraph;
import org.jgraph.graph.DefaultGraphModel.GraphModelEdit;

import nl.utwente.groove.grammar.QualName;
import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.graph.GraphProperties;
import nl.utwente.groove.graph.GraphProperties.Key;
import nl.utwente.groove.gui.Icons;
import nl.utwente.groove.gui.Options;
import nl.utwente.groove.gui.dialog.PropertiesTable;
import nl.utwente.groove.gui.jgraph.AspectJGraph;
import nl.utwente.groove.gui.jgraph.AspectJModel;
import nl.utwente.groove.gui.jgraph.JAttr;
import nl.utwente.groove.gui.jgraph.JModel;
import nl.utwente.groove.gui.look.Values;
import nl.utwente.groove.gui.tree.RuleLevelTree;
import nl.utwente.groove.gui.tree.TypeTree;
import nl.utwente.groove.util.parse.FormatError;

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

    /** Returns the graph being displayed. */
    public @Nullable AspectGraph getGraph() {
        var jModel = getJModel();
        return jModel == null
            ? null
            : jModel.getGraph();
    }

    @Override
    protected void start() {
        super.start();
        getJGraph().setToolTipEnabled(true);
        getJGraph().addMouseListener(new EditMouseListener());
    }

    @Override
    protected PropertyChangeListener createErrorListener() {
        return arg -> {
            var jModel = getJModel();
            if (jModel != null) {
                var error = (FormatError) arg.getNewValue();
                if (error == null) {
                    getJGraph().clearSelection();
                } else {
                    getJGraph().setSelectionCells(error.getElements());
                }
            }
        };
    }

    @Override
    public JGraphPanel<AspectGraph> getEditArea() {
        JGraphPanel<AspectGraph> result = this.editArea;
        if (result == null) {
            this.editArea = result = new JGraphPanel<>(getJGraph());
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
    protected JTabbedPane getUpperInfoPanel() {
        JTabbedPane result = this.upperInfoPanel;
        if (result == null) {
            this.upperInfoPanel = result = new JTabbedPane();
            result.add(getLabelPanel());
            if (getResourceKind().hasProperties()) {
                var propertiesPanel = getPropertiesScrollPanel();
                result.add(propertiesPanel);
                int index = result.indexOfComponent(propertiesPanel);
                this.propertiesHeader.setText(propertiesPanel.getName());
                result.setTitleAt(index, null);
                result.setTabComponentAt(index, this.propertiesHeader);
                updatePropertiesNotable();
                result.addChangeListener(createInfoListener(true));
            }
        }
        if (getResourceKind().hasProperties()) {
            result.setSelectedIndex(getDisplay().getInfoTabIndex(true));
        }
        return result;
    }

    /** Label panel of this tab. */
    private JTabbedPane upperInfoPanel;

    private TitledPanel getLabelPanel() {
        TitledPanel result = this.labelPanel;
        if (result == null) {
            TypeTree labelTree = getLabelTree();
            this.labelPanel = result = new TitledPanel(Options.LABEL_PANE_TITLE, labelTree,
                labelTree.createToolBar(), true);
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

    private @NonNull JScrollPane getPropertiesScrollPanel() {
        var result = this.propertiesScrollPanel;
        if (result == null) {
            var propertiesPanel = getPropertiesPanel();
            this.propertiesScrollPanel = result = new JScrollPane(propertiesPanel);
            result.setName(propertiesPanel.getName());
            result.getViewport().setBackground(propertiesPanel.getBackground());
        }
        return result;
    }

    private JScrollPane propertiesScrollPanel;

    /** Tab component of the properties tab in the upper info panel. */
    private final JLabel propertiesHeader = new JLabel();

    /** Loads the properties from a given {@link JModel} into the properties panel. */
    private void loadProperties(AspectJModel jModel) {
        var properties = jModel.getProperties();
        var graph = jModel.getGraph();
        getPropertiesPanel().setProperties(properties);
        getPropertiesPanel().setCheckerMap(properties.getCheckers(graph));
    }

    /**
     * Adapt the properties header according to the notability of the properties
     */
    private void updatePropertiesNotable() {
        var graph = getGraph();
        if (graph != null) {
            boolean notableProperties = graph.getProperties().isNotable();
            this.propertiesHeader
                .setForeground(notableProperties
                    ? Values.INFO_NORMAL_FOREGROUND
                    : Values.NORMAL_FOREGROUND);
        }
    }

    @Override
    protected JComponent getLowerInfoPanel() {
        JPanel result = this.lowerInfoPanel;
        RuleLevelTree levelTree = getLevelTree();
        if (result == null && levelTree != null) {
            this.lowerInfoPanel = result = new TitledPanel("Nesting levels", levelTree, null, true);
        }
        return levelTree != null && levelTree.isEnabled()
            ? result
            : null;
    }

    /** Level tree panel of this tab, if any. */
    private JPanel lowerInfoPanel;

    /** Lazily creates and returns the (possibly {@code null}) rule level tree. */
    private @Nullable RuleLevelTree getLevelTree() {
        RuleLevelTree result = this.levelTree;
        if (result == null && getResourceKind() == ResourceKind.RULE) {
            result = this.levelTree = new RuleLevelTree(getJGraph());
        }
        return result;
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
    public boolean setResource(@Nullable QualName name) {
        AspectJModel jModel = this.jModelMap.get(name);
        if (jModel == null && name != null) {
            AspectGraph graph = getSimulatorModel().getGrammar().getModelGraph(getResourceKind(), name);
            if (graph != null) {
                this.jModelMap.put(name, jModel = getJGraph().newModel());
                jModel.loadGraph(graph);
            }
        }
        if (jModel == null) {
            name = null;
        }
        JModel<?> oldJModel = getJModel();
        if (oldJModel != null) {
            oldJModel.removeUndoableEditListener(this);
        }
        getJGraph().setModel(jModel);
        if (jModel != null) {
            jModel.addUndoableEditListener(this);
            loadProperties(jModel);
        }
        setQualName(name);
        String nameString = name == null
            ? null
            : name.toString();
        getTabLabel().setTitle(nameString);
        var resource = getResource();
        if (resource != null) {
            Color background = getResource().isActive()
                ? JAttr.ACTIVE_BACKGROUND
                : JAttr.INACTIVE_BACKGROUND;
            getEditArea().setEnabledBackground(background);
            getLabelTree().setBackground(background);
            var levelTree = getLevelTree();
            if (levelTree != null) {
                levelTree.setBackground(background);
            }
            getPropertiesPanel().setBackground(background);
        }
        updateErrors();
        updatePropertiesNotable();
        return jModel != null;
    }

    @Override
    public boolean removeResource(QualName name) {
        boolean result = name.equals(getQualName());
        this.jModelMap.remove(name);
        if (result) {
            setResource(null);
        }
        return result;
    }

    @Override
    public void setPropertyKey(Key propertyKey) {
        var upperInfoPanel = getUpperInfoPanel();
        if (upperInfoPanel != null && propertyKey != null) {
            upperInfoPanel.setSelectedComponent(getPropertiesScrollPanel());
            getPropertiesPanel().setSelected(propertyKey);
        }
    }

    /**
     * Notifies the tab that the grammar has changed.
     * This resets the internal data structures, and informs the
     * underlying {@link JGraph} of the type change.
     */
    @Override
    public void updateGrammar(GrammarModel grammar) {
        this.jModelMap.clear();
        setResource(getQualName());
    }

    @Override
    public void undoableEditHappened(UndoableEditEvent e) {
        if (e.getEdit() instanceof GraphModelEdit) {
            try {
                var jModel = getJModel();
                assert jModel != null;
                jModel.syncGraph();
                // we need to clone the graph to properly freeze the next layout change
                var graph = jModel.getGraph();
                assert graph != null;
                //AspectGraph graphClone = graph.clone();
                //graphClone.setFixed();
                getSimulatorModel().doAddGraph(getResourceKind(), graph, true);
                loadProperties(jModel);
            } catch (IOException e1) {
                // do nothing
            }
        }
    }

    /** Returns the underlying JGraph of this tab. */
    public final @NonNull AspectJGraph getJGraph() {
        AspectJGraph result = this.jGraph;
        if (result == null) {
            result = this.jGraph = new AspectJGraph(getSimulator(), getDisplay().getKind(), false);
            result.setLabelTree(getLabelTree());
            result.setLevelTree(getLevelTree());
        }
        return result;
    }

    /** The jgraph instance used in this tab. */
    private AspectJGraph jGraph;

    /** Returns the underlying JGraph of this tab. */
    public final @Nullable AspectJModel getJModel() {
        return getJGraph().getModel();
    }

    /** Mapping from resource names to aspect models. */
    private final Map<QualName,AspectJModel> jModelMap = new HashMap<>();
}