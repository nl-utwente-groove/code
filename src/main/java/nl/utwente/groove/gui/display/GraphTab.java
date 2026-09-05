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

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.grammar.ResourceProperties;
import nl.utwente.groove.grammar.ResourceProperties.Key;
import nl.utwente.groove.gui.Icons;
import nl.utwente.groove.gui.Options;
import nl.utwente.groove.gui.dialog.PropertiesTable;
import nl.utwente.groove.gui.look.Values;
import nl.utwente.groove.gui.view.AspectGraphCanvas;
import nl.utwente.groove.gui.view.AspectGraphViewController;
import nl.utwente.groove.gui.view.AspectGraphViewModel;
import nl.utwente.groove.gui.view.CellChange;
import nl.utwente.groove.gui.view.GraphCanvas;
import nl.utwente.groove.gui.view.GraphCanvasListener;
import nl.utwente.groove.gui.tree.RuleLevelTree;
import nl.utwente.groove.gui.tree.TypeTree;
import nl.utwente.groove.util.QualName;
import nl.utwente.groove.gui.list.ErrorEntry;

/** Display tab component showing a graph-based resource. */
final public class GraphTab extends ResourceTab {
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
        var jModel = getViewModel();
        return jModel == null
            ? null
            : jModel.getGraph();
    }

    @Override
    protected void start() {
        super.start();
        getCanvas().setToolTipEnabled(true);
        getCanvas().getComponent().addMouseListener(new EditMouseListener());
        getCanvas().addCanvasListener(new GraphCanvasListener<@NonNull AspectGraph>() {
            @Override
            public void cellsChanged(GraphCanvas<@NonNull AspectGraph> canvas,
                                     CellChange<@NonNull AspectGraph> change) {
                storeGraph();
            }
        });
    }

    /**
     * Stores the displayed graph back into the grammar after a change to its cells,
     * such as a layout change; ignored while the model is being loaded.
     */
    private void storeGraph() {
        var viewModel = getViewModel();
        if (viewModel == null || viewModel.isLoading()) {
            return;
        }
        try {
            viewModel.syncGraph();
            var graph = viewModel.getGraph();
            assert graph != null; // a model shown on the canvas has a graph
            getSimulatorModel().doAddGraph(getResourceKind(), graph, true);
            loadProperties(viewModel);
        } catch (IOException e1) {
            // do nothing
        }
    }

    @Override
    protected PropertyChangeListener createErrorListener() {
        return arg -> {
            var jModel = getViewModel();
            if (jModel != null) {
                var entry = (ErrorEntry) arg.getNewValue();
                if (entry == null) {
                    getCanvas().clearSelection();
                } else {
                    getCanvas().selectElements(entry.getElements());
                }
            }
        };
    }

    @Override
    public GraphPanel<@NonNull AspectGraph> getEditArea() {
        GraphPanel<@NonNull AspectGraph> result = this.editArea;
        if (result == null) {
            this.editArea = result = new GraphPanel<>(getCanvas());
            result.setFocusable(false);
            result.setEnabled(false);
            result.initialise();
        }
        return result;
    }

    /** Graph panel of this tab. */
    private GraphPanel<@NonNull AspectGraph> editArea;

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
            this.propertiesPanel = result = new PropertiesTable(ResourceProperties.Key.class, false);
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

    /** Loads the properties from a given view model into the properties panel. */
    private void loadProperties(AspectGraphViewModel jModel) {
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
            boolean notableProperties = ResourceProperties.getProperties(graph).isNotable();
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
            result = this.levelTree = new RuleLevelTree(getCanvas());
        }
        return result;
    }

    private RuleLevelTree levelTree;

    /** Lazily creates and returns the (non-{@code null}) label tree. */
    private TypeTree getLabelTree() {
        TypeTree result = this.labelTree;
        if (result == null) {
            result = this.labelTree = new TypeTree(getCanvas(), true);
        }
        return result;
    }

    private TypeTree labelTree;

    @Override
    public boolean setResource(@Nullable QualName name) {
        AspectGraphViewModel jModel = this.viewModelMap.get(name);
        if (jModel == null && name != null) {
            AspectGraph graph = getSimulatorModel().getGrammar().getModelGraph(getResourceKind(), name);
            if (graph != null) {
                this.viewModelMap.put(name, jModel = getCanvas().newViewModel());
                jModel.loadGraph(graph);
            }
        }
        if (jModel == null) {
            name = null;
        }
        getCanvas().setViewModel(jModel);
        if (jModel != null) {
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
                ? Values.ACTIVE_BACKGROUND
                : Values.INACTIVE_BACKGROUND;
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
        this.viewModelMap.remove(name);
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
     * canvas of the type change.
     */
    @Override
    public void updateGrammar(GrammarModel grammar) {
        this.viewModelMap.clear();
        setResource(getQualName());
    }

    /** Returns the canvas of this tab, created by its controller on first request. */
    public final @NonNull AspectGraphCanvas getCanvas() {
        return getController().getCanvas();
    }

    /** Returns the controller of this tab's graph view, creating it on first request. */
    public final @NonNull AspectGraphViewController getController() {
        AspectGraphViewController result = this.controller;
        if (result == null) {
            result = this.controller
                = new AspectGraphViewController(getSimulator(), getDisplay().getKind(), false);
            result.setLabelTree(getLabelTree());
            result.setLevelTree(getLevelTree());
        }
        return result;
    }

    /** The controller of this tab's graph view. */
    private AspectGraphViewController controller;

    /** Returns the view model currently shown on the canvas, if any. */
    public final @Nullable AspectGraphViewModel getViewModel() {
        return getCanvas().getViewModel();
    }

    /** Mapping from resource names to aspect models. */
    private final Map<QualName,AspectGraphViewModel> viewModelMap = new HashMap<>();
}