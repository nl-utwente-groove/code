package groove.gui;

import static groove.gui.Options.SHOW_ANCHORS_OPTION;
import static groove.gui.Options.SHOW_ASPECTS_OPTION;
import static groove.gui.Options.SHOW_NODE_IDS_OPTION;
import static groove.gui.Options.SHOW_REMARKS_OPTION;
import static groove.gui.Options.SHOW_VALUE_NODES_OPTION;
import groove.graph.LabelStore;
import groove.graph.TypeGraph;
import groove.graph.TypeLabel;
import groove.gui.ResourceDisplay.MainTab;
import groove.gui.dialog.ErrorDialog;
import groove.gui.jgraph.AspectJGraph;
import groove.gui.jgraph.AspectJModel;
import groove.trans.ResourceKind;
import groove.trans.SystemProperties;
import groove.view.FormatException;
import groove.view.GrammarModel;
import groove.view.TypeModel;
import groove.view.aspect.AspectGraph;

import java.awt.Component;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;

import org.jgraph.JGraph;

/** Display tab component showing a graph-based resource. */
final public class GraphTab extends JGraphPanel<AspectJGraph> implements
        MainTab {
    /**
     * Constructs the instance of this tab for a given simulator and
     * resource kind.
     */
    public GraphTab(Simulator simulator, ResourceKind resourceKind) {
        super(new AspectJGraph(simulator, resourceKind.getGraphRole()), false);
        this.resourceKind = resourceKind;
        this.simulatorModel = simulator.getModel();
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
        addRefreshListener(SHOW_REMARKS_OPTION);
        addRefreshListener(SHOW_VALUE_NODES_OPTION);
        getJGraph().setToolTipEnabled(true);
        getJGraph().getLabelTree().addLabelStoreObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                assert arg instanceof LabelStore;
                SystemProperties newProperties =
                    getSimulatorModel().getGrammar().getProperties().clone();
                newProperties.setSubtypes(((LabelStore) arg).toDirectSubtypeString());
                try {
                    getSimulatorModel().doSetProperties(newProperties);
                } catch (IOException exc) {
                    new ErrorDialog(getComponent(),
                        "Error while modifying type hierarchy", exc).setVisible(true);
                }
            }
        });
    }

    @Override
    public Icon getIcon() {
        return Icons.getMainTabIcon(this.resourceKind);
    }

    public String getTitle() {
        // the title of a non-editor tab is the same as the resource name
        return getName();
    }

    @Override
    final public boolean isEditor() {
        return false;
    }

    @Override
    public void setResource(String name) {
        AspectJModel jModel = this.jModelMap.get(name);
        if (jModel == null && name != null) {
            this.jModelMap.put(name, jModel = getJGraph().newModel());
            AspectGraph graph =
                this.simulatorModel.getStore().getGraphs(this.resourceKind).get(
                    name);
            jModel.loadGraph(graph);
        }
        setJModel(jModel);
        setName(name);
        getTabLabel().setTitle(name);
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
        if (grammar == null) {
            getJGraph().setType(null, null);
        } else {
            // set either the type or the label store of the associated JGraph
            try {
                TypeGraph type = grammar.getTypeModel().toResource();
                Map<String,Set<TypeLabel>> labelsMap =
                    new HashMap<String,Set<TypeLabel>>();
                for (String typeName : grammar.getTypeNames()) {
                    TypeModel typeModel = grammar.getTypeModel(typeName);
                    // the view may be null if type names
                    // overlap modulo upper/lowercase
                    if (typeModel != null && typeModel.isEnabled()) {
                        labelsMap.put(typeName, typeModel.getLabels());
                    }
                }
                getJGraph().setType(type, labelsMap);
            } catch (FormatException e) {
                getJGraph().setLabelStore(grammar.getLabelStore());
            }
        }
        refreshStatus();
    }

    /** 
     * Returns the component to be used to fill the tab in a 
     * {@link JTabbedPane}, when this panel is displayed.
     */
    public final TabLabel getTabLabel() {
        if (this.tabLabel == null) {
            this.tabLabel = new TabLabel(this, getIcon(), getName());
        }
        return this.tabLabel;
    }

    @Override
    protected final JToolBar createToolBar() {
        return null;
    }

    @Override
    public Component getComponent() {
        return this;
    }

    /** The tab label used for this tab. */
    private TabLabel tabLabel;
    private final SimulatorModel simulatorModel;
    private final ResourceKind resourceKind;
    /** Mapping from resource names to aspect models. */
    private final Map<String,AspectJModel> jModelMap =
        new HashMap<String,AspectJModel>();
}