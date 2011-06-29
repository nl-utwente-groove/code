package groove.gui;

import static groove.gui.Options.SHOW_ANCHORS_OPTION;
import static groove.gui.Options.SHOW_ASPECTS_OPTION;
import static groove.gui.Options.SHOW_NODE_IDS_OPTION;
import static groove.gui.Options.SHOW_REMARKS_OPTION;
import static groove.gui.Options.SHOW_VALUE_NODES_OPTION;
import groove.graph.LabelStore;
import groove.gui.ResourceDisplay.MainTab;
import groove.gui.dialog.ErrorDialog;
import groove.gui.dialog.GraphPreviewDialog;
import groove.gui.jgraph.AspectJGraph;
import groove.gui.jgraph.AspectJModel;
import groove.gui.jgraph.GraphJCell;
import groove.trans.SystemProperties;
import groove.view.FormatError;
import groove.view.GrammarModel;
import groove.view.aspect.AspectGraph;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.swing.Icon;

import org.jgraph.JGraph;

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
                    new ErrorDialog(getDisplay().createDisplayPanel(),
                        "Error while modifying type hierarchy", exc).setVisible(true);
                }
            }
        });
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
    protected Collection<FormatError> getErrors() {
        AspectJModel jModel = getJModel();
        if (jModel == null) {
            return Collections.emptySet();
        } else {
            return jModel.getErrorMap().keySet();
        }
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
                    GraphPreviewDialog.showGraph(graph.normalise());
                }
                this.jModelMap.put(name, jModel =
                    getEditArea().getJGraph().newModel());
                jModel.loadGraph(graph);
            }
        }
        if (jModel != null) {
            getEditArea().setJModel(jModel);
            setName(name);
            getTabLabel().setTitle(name);
            updateErrors();
        }
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
        getJGraph().updateGrammar(grammar);
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
            addRefreshListener(SHOW_REMARKS_OPTION);
            addRefreshListener(SHOW_VALUE_NODES_OPTION);
        }
    }
}