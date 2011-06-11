package groove.gui;

import static groove.gui.Options.SHOW_ANCHORS_OPTION;
import static groove.gui.Options.SHOW_ASPECTS_OPTION;
import static groove.gui.Options.SHOW_NODE_IDS_OPTION;
import static groove.gui.Options.SHOW_REMARKS_OPTION;
import static groove.gui.Options.SHOW_VALUE_NODES_OPTION;
import groove.graph.LabelStore;
import groove.gui.ResourceDisplay.MainTab;
import groove.gui.dialog.ErrorDialog;
import groove.gui.jgraph.AspectJGraph;
import groove.gui.jgraph.AspectJModel;
import groove.trans.ResourceKind;
import groove.trans.SystemProperties;
import groove.view.GrammarModel;
import groove.view.aspect.AspectGraph;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.swing.Icon;
import javax.swing.JTabbedPane;

import org.jgraph.JGraph;

/** Display tab component showing a graph-based resource. */
final public class GraphTab extends JGraphPanel<AspectJGraph> implements
        MainTab {
    /**
     * Constructs the instance of this tab for a given simulator and
     * resource kind.
     */
    public GraphTab(ResourceDisplay display) {
        super(new AspectJGraph(display.getSimulator(),
            display.getResourceKind().getGraphRole(), false), false);
        this.display = display;
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
        getJGraph().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    getDisplay().getEditAction().execute();
                }
            }
        });
    }

    @Override
    public Icon getIcon() {
        return Icons.getMainTabIcon(getResourceKind());
    }

    public String getTitle() {
        // the title of a non-editor tab is the same as the resource name
        return getName();
    }

    /** Returns the display on which this tab is placed. */
    public final ResourceDisplay getDisplay() {
        return this.display;
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
                getSimulatorModel().getStore().getGraphs(getResourceKind()).get(
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
        getJGraph().updateGrammar(grammar);
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
    public Component getComponent() {
        return this;
    }

    private ResourceKind getResourceKind() {
        return getDisplay().getResourceKind();
    }

    /** The tab label used for this tab. */
    private TabLabel tabLabel;
    /** The display on which this tab is placed. */
    private final ResourceDisplay display;
    /** Mapping from resource names to aspect models. */
    private final Map<String,AspectJModel> jModelMap =
        new HashMap<String,AspectJModel>();
}