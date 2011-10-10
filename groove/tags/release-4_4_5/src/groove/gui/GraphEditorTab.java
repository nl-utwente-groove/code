/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * $Id: EditorDialog.java,v 1.15 2008-01-30 09:33:35 iovka Exp $
 */
package groove.gui;

import static groove.gui.Options.SHOW_ASPECTS_OPTION;
import static groove.gui.Options.SHOW_NODE_IDS_OPTION;
import static groove.gui.Options.SHOW_VALUE_NODES_OPTION;
import static groove.gui.jgraph.JGraphMode.EDIT_MODE;
import static groove.gui.jgraph.JGraphMode.PREVIEW_MODE;
import groove.algebra.Algebras;
import groove.annotation.Help;
import groove.graph.EdgeRole;
import groove.graph.GraphInfo;
import groove.graph.GraphProperties;
import groove.graph.GraphRole;
import groove.gui.action.SnapToGridAction;
import groove.gui.jgraph.AspectJEdge;
import groove.gui.jgraph.AspectJGraph;
import groove.gui.jgraph.AspectJModel;
import groove.gui.jgraph.GraphJCell;
import groove.gui.jgraph.GraphJGraph;
import groove.gui.jgraph.JAttr;
import groove.gui.jgraph.JGraphMode;
import groove.rel.RegExpr;
import groove.util.Pair;
import groove.view.GrammarModel;
import groove.view.GraphBasedModel;
import groove.view.ResourceModel;
import groove.view.aspect.AspectGraph;
import groove.view.aspect.AspectKind;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListSelectionModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.TransferHandler;
import javax.swing.event.UndoableEditEvent;

import org.jgraph.event.GraphModelEvent;
import org.jgraph.event.GraphModelListener;
import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;
import org.jgraph.graph.GraphLayoutCache.GraphLayoutCacheEdit;
import org.jgraph.graph.GraphUndoManager;

/**
 * Dialog wrapping a graph editor, such that no file operations are possible.
 * @author Arend Rensink
 * @version $Revision$
 */
final public class GraphEditorTab extends ResourceTab implements
        GraphModelListener, PropertyChangeListener {
    /**
     * Constructs an instance of the dialog, for a given graph or rule.
     * @param parent the component on which this panel is placed
     * @param role the input graph for the editor
     */
    public GraphEditorTab(ResourceDisplay parent, final GraphRole role) {
        super(parent);
        this.role = role;
        this.jgraph = new AspectJGraph(getSimulator(), parent.getKind(), true);
        this.jgraph.updateGrammar(getSimulatorModel().getGrammar());
        setFocusCycleRoot(true);
        // start is called from the constructor;
        // this may go wrong in case of subclassing
        setSnapToGrid();
        initListeners();
        start();
    }

    /** Sets a given graph as the model to be edited. */
    public void setGraph(AspectGraph graph) {
        AspectJModel oldModel = getModel();
        if (oldModel != null) {
            oldModel.addUndoableEditListener(getUndoManager());
            oldModel.addGraphModelListener(this);
        }
        setName(graph.getName());
        AspectJModel newModel = getJGraph().newModel();
        newModel.loadGraph(graph);
        getJGraph().setModel(newModel);
        newModel.addUndoableEditListener(getUndoManager());
        newModel.addGraphModelListener(this);
        setDirty(false);
        getUndoManager().discardAllEdits();
        updateHistoryButtons();
        updateGrammar(getSimulatorModel().getGrammar());
    }

    /** Returns the graph being edited. */
    public AspectGraph getGraph() {
        return getModel().getGraph();
    }

    @Override
    protected Observer createErrorListener() {
        return new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                if (arg != null) {
                    GraphJCell errorCell = getModel().getErrorMap().get(arg);
                    if (errorCell != null) {
                        getJGraph().setSelectionCell(errorCell);
                    }
                }
            }
        };
    }

    @Override
    protected JToolBar createToolBar() {
        JToolBar result = super.createToolBar();
        result.addSeparator();
        result.add(getJGraph().getModeButton(EDIT_MODE));
        result.add(getJGraph().getModeButton(PREVIEW_MODE));
        result.addSeparator();
        result.add(getUndoAction());
        result.add(getRedoAction());
        result.addSeparator();
        result.add(getCopyAction());
        result.add(getPasteAction());
        result.add(getCutAction());
        result.add(getDeleteAction());
        result.addSeparator();
        result.add(getSnapToGridButton());
        processToolBar(result);
        return result;
    }

    /** Post-processes an already constructed toolbar.
     */
    private void processToolBar(JToolBar toolBar) {
        for (int i = 0; i < toolBar.getComponentCount(); i++) {
            Component element = toolBar.getComponent(i);
            if (element instanceof JButton) {
                JButton button = (JButton) element;
                Action action = button.getAction();
                if (action != null) {
                    getJGraph().addAccelerator(action);
                }
            }
        }
        // ensure the JGraph gets focus as soon as the graph panel
        // is clicked anywhere
        // for reasons not clear to me, mouse listeners do not work on
        // the level of the JGraphPanel
        toolBar.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                getJGraph().requestFocus();
            }
        });
    }

    /** Returns the role of the graph being edited. */
    public GraphRole getRole() {
        return this.role;
    }

    @Override
    public void updateGrammar(GrammarModel grammar) {
        // test if the graph being edited is still in the grammar;
        // if not, silently dispose it - it's too late to do anything else!
        GraphBasedModel<?> graphModel =
            (GraphBasedModel<?>) grammar.getResource(getResourceKind(),
                getName());
        if (graphModel == null) {
            dispose();
        } else if (isDirty() || getGraph() == graphModel.getSource()) {
            getJGraph().updateGrammar(grammar);
            // check if the properties have changed
            GraphProperties properties =
                GraphInfo.getProperties(graphModel.getSource(), false);
            if (properties != null
                && !properties.equals(GraphInfo.getProperties(getGraph(), false))) {
                AspectGraph newGraph = getGraph().clone();
                GraphInfo.setProperties(newGraph, properties);
                newGraph.setFixed();
                change(newGraph);
            }
            updateStatus();
        } else {
            setGraph(graphModel.getSource());
        }
    }

    @Override
    public void setClean() {
        setDirty(false);
    }

    /**
     * Sets the modified status of the currently edited graph. Also updates the
     * frame title to reflect the new modified status.
     * @param dirty the new modified status
     * @see #isDirty()
     */
    private void setDirty(boolean dirty) {
        if (dirty) {
            // if the dirt count was negative, this cannot be
            // undone any more, so change to positive
            this.dirtCount = Math.abs(this.dirtCount) + 1;
        } else {
            this.dirtCount = 0;
        }
        updateDirty();
    }

    /**
     * Returns the current modified status of the underlying jgraph.
     * @see #setDirty(boolean)
     */
    @Override
    public boolean isDirty() {
        return this.dirtCount != 0;
    }

    /** Changes the edited graph. */
    public void change(AspectGraph newGraph) {
        assert newGraph.getName().equals(getGraph().getName())
            && newGraph.getRole() == getGraph().getRole();
        getModel().loadGraph(newGraph);
        updateStatus();
    }

    /** Renames the edited graph. */
    public void rename(String newName) {
        AspectGraph newGraph = getGraph().clone();
        newGraph.setName(newName);
        newGraph.setFixed();
        getModel().loadGraph(newGraph);
        updateStatus();
        setName(newName);
    }

    @Override
    protected ResourceModel<?> getResource() {
        return getModel().getResourceModel();
    }

    @Override
    protected void saveResource() {
        getSaveAction().doSaveGraph(getGraph());
        setDirty(false);
    }

    /** Returns the jgraph component of this editor. */
    public AspectJGraph getJGraph() {
        return this.jgraph;
    }

    /**
     * @return the j-model currently being edited, or <tt>null</tt> if no editor
     *         model is set.
     */
    private AspectJModel getModel() {
        return getJGraph().getModel();
    }

    /**
     * Refreshes the status bar and the errors, if the text on any of the cells
     * has changed.
     */
    public void graphChanged(GraphModelEvent e) {
        boolean changed =
            e.getChange().getInserted() != null
                || e.getChange().getRemoved() != null
                || e.getChange().getAttributes() != null;
        if (changed) {
            updateStatus();
        }
    }

    /**
     * We listen to the 
     * {@link GraphJGraph#JGRAPH_MODE_PROPERTY}.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        assert evt.getPropertyName().equals(GraphJGraph.JGRAPH_MODE_PROPERTY);
        JGraphMode mode = getJGraph().getMode();
        if (mode == PREVIEW_MODE || evt.getOldValue() == PREVIEW_MODE) {
            this.refreshing = true;
            getModel().syncGraph();
            getJGraph().setEditable(mode != PREVIEW_MODE);
            getJGraph().refreshAllCells();
            getJGraph().refresh();
            this.refreshing = false;
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        getSnapToGridAction().removeSnapListener(this);
    }

    /** Initialises the graph selection listener and attributed graph listener. */
    private void initListeners() {
        getJGraph().setToolTipEnabled(true);
        // Update ToolBar based on Selection Changes
        getJGraph().getSelectionModel().addGraphSelectionListener(
            new GraphSelectionListener() {
                public void valueChanged(GraphSelectionEvent e) {
                    // Update Button States based on Current Selection
                    boolean selected = !getJGraph().isSelectionEmpty();
                    getDeleteAction().setEnabled(selected);
                    getCopyAction().setEnabled(selected);
                    getCutAction().setEnabled(selected);
                }
            });
        getJGraph().addJGraphModeListener(this);
        getSnapToGridAction().addSnapListener(this);
    }

    /**
     * Creates and lazily returns the undo manager for this editor.
     */
    private GraphUndoManager getUndoManager() {
        if (this.undoManager == null) {
            // Create a GraphUndoManager which also Updates the ToolBar
            this.undoManager = new GraphUndoManager() {
                @Override
                public void undoableEditHappened(UndoableEditEvent e) {
                    boolean relevant = true;
                    if (GraphEditorTab.this.refreshing
                        || getJGraph().isModelRefreshing()) {
                        relevant = false;
                    } else if (e.getEdit() instanceof GraphLayoutCacheEdit) {
                        // only process edits that really changed anything
                        GraphLayoutCacheEdit edit =
                            (GraphLayoutCacheEdit) e.getEdit();
                        relevant =
                            edit.getInserted().length > 0
                                || edit.getRemoved().length > 0
                                || edit.getChanged().length > 0;
                    }
                    if (relevant) {
                        super.undoableEditHappened(e);
                        setDirty(true);
                        updateHistoryButtons();
                    }
                }
            };
        }
        return this.undoManager;
    }

    @Override
    protected JGraphPanel<?> getEditArea() {
        if (this.jGraphPanel == null) {
            JGraphPanel<?> result = this.jGraphPanel = new MyGraphPanel(this);
            result.initialise();
            result.addRefreshListener(SHOW_NODE_IDS_OPTION);
            result.addRefreshListener(SHOW_VALUE_NODES_OPTION);
            result.addRefreshListener(SHOW_ASPECTS_OPTION);
            result.setEnabled(true);
        }
        return this.jGraphPanel;
    }

    /**
     * Updates the Undo/Redo Button State based on Undo Manager. Also sets
     * {@link #isDirty()} if no more undos are available.
     */
    private void updateHistoryButtons() {
        // The View Argument Defines the Context
        getUndoAction().setEnabled(getUndoManager().canUndo());
        getRedoAction().setEnabled(getUndoManager().canRedo());
        updateDirty();
    }

    /** Sets the enabling of the transfer buttons. */
    private void updateCopyPasteButtons() {
        boolean previewing = getJGraph().getMode() == PREVIEW_MODE;
        boolean hasSelection = !getJGraph().isSelectionEmpty();
        getCopyAction().setEnabled(!previewing && hasSelection);
        getCutAction().setEnabled(!previewing && hasSelection);
        getDeleteAction().setEnabled(!previewing && hasSelection);
        getPasteAction().setEnabled(!previewing && clipboardFilled);
    }

    /**
     * Returns the button for setting selection mode, lazily creating it first.
     */
    private JToggleButton getSnapToGridButton() {
        if (this.snapToGridButton == null) {
            this.snapToGridButton = new JToggleButton(getSnapToGridAction());
            this.snapToGridButton.setFocusable(false);
            this.snapToGridButton.setText(null);
        }
        return this.snapToGridButton;
    }

    /** Refreshes the snap-to-grid status of this editor tab. */
    public void setSnapToGrid() {
        boolean snap = getSnapToGridAction().getSnap();
        getSnapToGridButton().setSelected(snap);
        getJGraph().setGridEnabled(snap);
        getJGraph().setGridVisible(snap);
    }

    /**
     * Updates the observers
     * with information about the currently edited graph.
     */
    private void updateStatus() {
        if (!getJGraph().isInserting()) {
            updateCopyPasteButtons();
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    if (!getJGraph().isInserting()) {
                        updateErrors();
                    }
                }
            });
        }
        updateDirty();
        getTabLabel().setError(hasErrors());
    }

    /** Undoes the last registered change to the Model or the View. */
    private void undoLastEdit() {
        setSelectInsertedCells(false);
        getUndoManager().undo();
        this.dirtCount--;
        setSelectInsertedCells(true);
        updateHistoryButtons();
    }

    /** Redoes the latest undone change to the Model or the View. */
    private void redoLastEdit() {
        setSelectInsertedCells(false);
        getUndoManager().redo();
        this.dirtCount++;
        setSelectInsertedCells(true);
        updateHistoryButtons();
    }

    /** Sets the property whether all inserted cells are automatically selected. */
    private void setSelectInsertedCells(boolean select) {
        this.jgraph.getGraphLayoutCache().setSelectsAllInsertedCells(select);
    }

    /** Creates and returns a panel for the syntax descriptions. */
    private Component createSyntaxHelp() {
        initSyntax();
        JPanel result = new JPanel();
        result.setLayout(new BorderLayout());
        result.add(new JLabel("Allowed labels:"), BorderLayout.NORTH);
        final JTabbedPane tabbedPane = new JTabbedPane();
        final int nodeTabIndex = tabbedPane.getTabCount();
        tabbedPane.addTab("Nodes", null, createSyntaxList(this.nodeKeys),
            "Label prefixes that are allowed on nodes");
        final int edgeTabIndex = tabbedPane.getTabCount();
        tabbedPane.addTab("Edges", null, createSyntaxList(this.edgeKeys),
            "Label prefixes that are allowed on edges");
        if (this.role == GraphRole.RULE) {
            tabbedPane.addTab("RegExpr", null,
                createSyntaxList(RegExpr.getDocMap().keySet()),
                "Syntax for regular expressions over labels");
            tabbedPane.addTab("Expr", null,
                createSyntaxList(Algebras.getDocMap().keySet()),
                "Available attribute operators");
        }
        result.add(tabbedPane, BorderLayout.CENTER);
        // add a listener that switches the syntax help between nodes and edges
        // when a cell edit is started in the JGraph
        getJGraph().addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName() == GraphJGraph.CELL_EDIT_PROPERTY) {
                    int index =
                        evt.getNewValue() instanceof AspectJEdge ? edgeTabIndex
                                : nodeTabIndex;
                    tabbedPane.setSelectedIndex(index);
                }
            }
        });
        return result;
    }

    /**
     * Creates and returns a list of aspect descriptions.
     * @param data the data for the {@link JList}
     */
    private JComponent createSyntaxList(Collection<String> data) {
        final JList list = new JList();
        list.setCellRenderer(new SyntaxCellRenderer());
        list.setBackground(JAttr.EDITOR_BACKGROUND);
        list.setListData(data.toArray());
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (e.getSource() == list) {
                    this.manager.setDismissDelay(Integer.MAX_VALUE);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (e.getSource() == list) {
                    this.manager.setDismissDelay(this.standardDelay);
                }
            }

            private final ToolTipManager manager =
                ToolTipManager.sharedInstance();
            private final int standardDelay = this.manager.getDismissDelay();
        });
        list.setSelectionModel(new DefaultListSelectionModel() {
            @Override
            public void setSelectionInterval(int index0, int index1) {
                // do nothing
            }

            @Override
            public void setLeadSelectionIndex(int leadIndex) {
                // do nothing
            }
        });
        return new JScrollPane(list);
    }

    /**
     * Initialises the syntax descriptions of all aspect kinds of this 
     * editor's graph mode.
     */
    private void initSyntax() {
        if (this.nodeKeys != null) {
            return;
        }
        this.nodeKeys =
            new TreeSet<String>(AspectKind.getNodeDocMap(this.role).keySet());
        this.edgeKeys =
            new TreeSet<String>(AspectKind.getEdgeDocMap(this.role).keySet());
        // the edge role description for binary edges in rule graphs is inappropriate
        Help extra = null;
        for (Map.Entry<EdgeRole,Pair<String,String>> entry : EdgeRole.getRoleToDocMap().entrySet()) {
            String item = entry.getValue().one();
            switch (entry.getKey()) {
            case BINARY:
                if (this.role == GraphRole.RULE) {
                    extra = EdgeRole.createHelp();
                    extra.setSyntax("regexpr");
                    extra.setHeader("Regular expression path");
                    extra.setBody(
                        "An unadorned edge label in a rule by default denotes a regular expression.",
                        "This means that labels with non-standard characters need to be quoted, or preceded with 'COLON'.");
                    this.edgeKeys.add(extra.getItem());
                } else {
                    this.edgeKeys.add(item);
                }
                break;
            case FLAG:
            case NODE_TYPE:
                this.nodeKeys.add(item);
                break;
            default:
                assert false;
            }
        }
        this.docMap = new HashMap<String,String>();
        this.docMap.putAll(AspectKind.getNodeDocMap(this.role));
        this.docMap.putAll(AspectKind.getEdgeDocMap(this.role));
        this.docMap.putAll(EdgeRole.getDocMap());
        this.docMap.putAll(RegExpr.getDocMap());
        this.docMap.putAll(Algebras.getDocMap());
        if (extra != null) {
            this.docMap.put(extra.getItem(), extra.getTip());
        }
    }

    /** Mapping from syntax documentation items to corresponding tool tips. */
    private Map<String,String> docMap;
    private Set<String> nodeKeys;
    private Set<String> edgeKeys;

    /** Button for snap to grid. */
    transient JToggleButton snapToGridButton;

    /** The jgraph instance used in this editor. */
    private final AspectJGraph jgraph;

    /** The jgraph panel used in this editor. */
    private JGraphPanel<AspectJGraph> jGraphPanel;

    /** 
     * The number of edit steps the editor state is removed
     * from a saved graph.
     * This can be negative, if undos happened since the last save. 
     */
    private int dirtCount;

    /** The undo manager of the editor. */
    private transient GraphUndoManager undoManager;

    /** 
     * Flag that is set to true while the preview mode switch
     * is being executed.
     */
    private transient boolean refreshing;

    /** The role of the graph being edited. */
    private final GraphRole role;
    /**
     * Flag shared between all Editor instances indicating that
     * the clipboard was filled by a cut or copy action.
     */
    private static boolean clipboardFilled;

    /**
     * Lazily creates and returns the action to cut graph elements in the
     * editor.
     */
    private Action getCutAction() {
        if (this.cutAction == null) {
            Action action = TransferHandler.getCutAction();
            action.putValue(Action.ACCELERATOR_KEY, Options.CUT_KEY);
            this.cutAction =
                new TransferAction(action, Options.CUT_KEY,
                    Options.CUT_ACTION_NAME);
            this.cutAction.putValue(Action.SMALL_ICON, Icons.CUT_ICON);
        }
        return this.cutAction;
    }

    /** Action to cut the selected elements. */
    private Action cutAction;

    /**
     * Lazily creates and returns the action to copy graph elements in the
     * editor.
     */
    private Action getCopyAction() {
        if (this.copyAction == null) {
            Action action = TransferHandler.getCopyAction();
            this.copyAction =
                new TransferAction(action, Options.COPY_KEY,
                    Options.COPY_ACTION_NAME);
            this.copyAction.putValue(Action.SMALL_ICON, Icons.COPY_ICON);
        }
        return this.copyAction;
    }

    /** Action to copy the selected elements. */
    private Action copyAction;

    /**
     * Lazily creates and returns the action to paste graph elements into the
     * editor.
     */
    private Action getPasteAction() {
        if (this.pasteAction == null) {
            Action action = TransferHandler.getPasteAction();
            this.pasteAction =
                new TransferAction(action, Options.PASTE_KEY,
                    Options.PASTE_ACTION_NAME);
            this.pasteAction.putValue(Action.SMALL_ICON, Icons.PASTE_ICON);
            this.pasteAction.setEnabled(true);
        }
        return this.pasteAction;
    }

    /** Action to paste the previously cut or copied elements. */
    private Action pasteAction;

    /**
     * Lazily creates and returns the action to redo the last editor action.
     */
    private Action getRedoAction() {
        if (this.redoAction == null) {
            this.redoAction =
                new ToolbarAction(Options.REDO_ACTION_NAME, Options.REDO_KEY,
                    Icons.REDO_ICON) {
                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        if (isEnabled()) {
                            super.actionPerformed(evt);
                            redoLastEdit();
                        }
                    }
                };
            this.redoAction.setEnabled(false);
        }
        return this.redoAction;
    }

    /** Action to redo the last (undone) edit. */
    private Action redoAction;

    /**
     * Lazily creates and returns the action to undo the last editor action.
     */
    private Action getUndoAction() {
        if (this.undoAction == null) {
            this.undoAction =
                new ToolbarAction(Options.UNDO_ACTION_NAME, Options.UNDO_KEY,
                    Icons.UNDO_ICON) {
                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        if (isEnabled()) {
                            super.actionPerformed(evt);
                            undoLastEdit();
                        }
                    }
                };
            this.undoAction.setEnabled(false);
        }
        return this.undoAction;
    }

    /** Action to undo the last edit. */
    private Action undoAction;

    /**
     * Lazily creates and returns the action to delete graph elements from the
     * editor.
     */
    private Action getDeleteAction() {
        if (this.deleteAction == null) {
            this.deleteAction = new DeleteAction();
        }
        return this.deleteAction;
    }

    /** Action to delete the selected elements. */
    private Action deleteAction;

    /**
     * Action to delete the selected elements.
     */
    private class DeleteAction extends ToolbarAction {
        /** Constructs an instance of the action. */
        protected DeleteAction() {
            super(Options.DELETE_ACTION_NAME, Options.DELETE_KEY,
                Icons.DELETE_ICON);
            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            if (!getJGraph().isSelectionEmpty()) {
                Object[] cells = getJGraph().getSelectionCells();
                cells = getJGraph().getDescendants(cells);
                getJGraph().getModel().remove(cells);
            }
        }
    }

    /** Returns the snap to grid action, lazily creating it first. */
    private SnapToGridAction getSnapToGridAction() {
        return getSimulator().getActions().getSnapToGridAction();
    }

    /**
     * General class for actions with toolbar buttons. Takes care of image, name
     * and key acceleration; moreover, the
     * <tt>actionPerformed(ActionEvent)</tt> starts by invoking
     * <tt>stopEditing()</tt>.
     * @author Arend Rensink
     * @version $Revision: 3512 $
     */
    private abstract class ToolbarAction extends AbstractAction {
        /** Constructs an action with a given name, key and icon. */
        ToolbarAction(String name, KeyStroke acceleratorKey, Icon icon) {
            super(name, icon);
            putValue(Action.SHORT_DESCRIPTION, name);
            putValue(ACCELERATOR_KEY, acceleratorKey);
        }

        public void actionPerformed(ActionEvent evt) {
            getJGraph().stopEditing();
        }
    }

    /** This will change the source of the action event to graph. */
    private class TransferAction extends ToolbarAction {
        /**
         * Constructs an action that redirects to another action, while setting
         * the source of the event to the editor's j-graph.
         */
        public TransferAction(Action action, KeyStroke acceleratorKey,
                String name) {
            super(name, acceleratorKey, (ImageIcon) action.getValue(SMALL_ICON));
            putValue(SHORT_DESCRIPTION, name);
            setEnabled(false);
            this.action = action;
        }

        /** Redirects the Action event. */
        @Override
        public void actionPerformed(ActionEvent evt) {
            super.actionPerformed(evt);
            evt =
                new ActionEvent(getJGraph(), evt.getID(),
                    evt.getActionCommand(), evt.getModifiers());
            this.action.actionPerformed(evt);
            if (this == getCutAction() || this == getCopyAction()) {
                clipboardFilled = true;
                getPasteAction().setEnabled(true);
            }
        }

        /** The action that this transfer action wraps. */
        protected Action action;
    }

    /** Private cell renderer class that inserts the correct tool tips. */
    private class SyntaxCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            Component result =
                super.getListCellRendererComponent(list, value, index,
                    isSelected, cellHasFocus);
            if (result == this) {
                setToolTipText(GraphEditorTab.this.docMap.get(value));
            }
            return result;
        }
    }

    private static class MyGraphPanel extends JGraphPanel<AspectJGraph> {
        public MyGraphPanel(GraphEditorTab editorTab) {
            super(editorTab.getJGraph(), false);
            this.editorTab = editorTab;
            setEnabledBackground(JAttr.EDITOR_BACKGROUND);
        }

        @Override
        protected JComponent createLabelPane() {
            JComponent labelPane = super.createLabelPane();
            JSplitPane result =
                new JSplitPane(JSplitPane.VERTICAL_SPLIT, labelPane,
                    this.editorTab.createSyntaxHelp());
            return result;
        }

        private final GraphEditorTab editorTab;
    }
}