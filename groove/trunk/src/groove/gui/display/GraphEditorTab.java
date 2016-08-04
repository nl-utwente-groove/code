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
 * $Id$
 */
package groove.gui.display;

import static groove.gui.jgraph.JGraphMode.EDIT_MODE;
import static groove.gui.jgraph.JGraphMode.PREVIEW_MODE;

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
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.TransferHandler;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.UndoableEdit;

import org.jgraph.event.GraphLayoutCacheEvent.GraphLayoutCacheChange;
import org.jgraph.event.GraphModelEvent;
import org.jgraph.event.GraphModelEvent.GraphModelChange;
import org.jgraph.event.GraphModelListener;
import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.ConnectionSet;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphUndoManager;

import groove.algebra.Algebras;
import groove.annotation.Help;
import groove.automaton.RegExpr;
import groove.grammar.QualName;
import groove.grammar.aspect.AspectGraph;
import groove.grammar.aspect.AspectKind;
import groove.grammar.model.GrammarModel;
import groove.grammar.model.GraphBasedModel;
import groove.grammar.model.ResourceModel;
import groove.graph.EdgeRole;
import groove.graph.GraphInfo;
import groove.graph.GraphProperties;
import groove.graph.GraphRole;
import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.action.SnapToGridAction;
import groove.gui.dialog.PropertiesTable;
import groove.gui.jgraph.AspectJEdge;
import groove.gui.jgraph.AspectJGraph;
import groove.gui.jgraph.AspectJModel;
import groove.gui.jgraph.JAttr;
import groove.gui.jgraph.JCell;
import groove.gui.jgraph.JGraph;
import groove.gui.jgraph.JGraphMode;
import groove.gui.tree.TypeTree;
import groove.util.Pair;

/**
 * Dialog wrapping a graph editor, such that no file operations are possible.
 * @author Arend Rensink
 * @version $Revision$
 */
final public class GraphEditorTab extends ResourceTab
    implements GraphModelListener, PropertyChangeListener {
    /**
     * Constructs a new tab instance.
     * @param parent the component on which this panel is placed
     * @param role the input graph for the editor
     */
    public GraphEditorTab(ResourceDisplay parent, final GraphRole role) {
        super(parent);
        this.role = role;
        setFocusCycleRoot(true);
        // start is called from the constructor;
        // this may go wrong in case of subclassing
        setSnapToGrid();
        initListeners();
        start();
    }

    /** Sets a given graph as the model to be edited. */
    public void setGraph(AspectGraph graph) {
        AspectJModel oldModel = getJModel();
        if (oldModel != null) {
            oldModel.removeUndoableEditListener(getUndoManager());
            oldModel.removeGraphModelListener(this);
        }
        setQualName(graph.getQualName());
        AspectJModel newModel = getJGraph().newModel();
        newModel.setBeingEdited(true);
        AspectGraph graphClone = graph.clone();
        graphClone.setFixed();
        newModel.loadGraph(graphClone);
        getJGraph().setModel(newModel);
        loadProperties(graphClone, true);
        newModel.addUndoableEditListener(getUndoManager());
        newModel.addGraphModelListener(this);
        setClean();
        getUndoManager().discardAllEdits();
        updateHistoryButtons();
        updateStatus();
    }

    /** Returns the graph being edited. */
    public AspectGraph getGraph() {
        return getJModel().getGraph();
    }

    @Override
    protected Observer createErrorListener() {
        return new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                if (arg != null) {
                    JCell<?> errorCell = getJModel().getErrorMap()
                        .get(arg);
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
        GraphBasedModel<?> graphModel =
            (GraphBasedModel<?>) grammar.getResource(getResourceKind(), getQualName());
        AspectGraph source = graphModel == null ? null : graphModel.getSource();
        // test if the graph being edited is still in the grammar;
        // if not, silently dispose it - it's too late to do anything else!
        if (source == null) {
            dispose();
        } else if (isDirty() || source == getGraph()) {
            // to keep the edit history, don't change the underlying graph
            // check if the properties have changed
            GraphProperties properties = GraphInfo.getProperties(source);
            if (!properties.equals(GraphInfo.getProperties(getGraph()))) {
                changeProperties(properties, true);
            } else {
                getJModel().setGraphModified();
                getJGraph().refresh();
            }
            updateStatus();
        } else {
            setGraph(source);
        }
    }

    @Override
    public void setClean() {
        this.dirtCount = 0;
        this.dirtMinor = true;
        updateDirty();
    }

    /**
     * Sets the modified status of the currently edited graph. Also updates the
     * frame title to reflect the new modified status.
     * @param minor {@code true} if this was a minor edit, not necessitating
     * a refresh of all resources
     * @see #isDirty()
     */
    public void setDirty(boolean minor) {
        // if the dirt count was negative, this cannot be
        // undone any more, so change to positive
        this.dirtCount = Math.abs(this.dirtCount) + 1;
        this.dirtMinor &= minor;
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

    /** Indicates if there is only minor (i.e., layout) dirt in the editor. */
    public boolean isDirtMinor() {
        return this.dirtMinor;
    }

    /** Renames the edited graph. */
    public void rename(QualName newName) {
        AspectGraph newGraph = getGraph().rename(newName);
        getJModel().loadGraph(newGraph);
        loadProperties(newGraph, true);
        setQualName(newName);
        updateStatus();
    }

    /**
     * Changes the properties of the graph in the JModel.
     * @param propertiesMap the new properties
     * @param updatePropertiesPanel if {@code true}, the change did not originate
     * from the properties table, so the table has to be refreshed as well
     */
    private void changeProperties(Map<?,?> propertiesMap, boolean updatePropertiesPanel) {
        AspectGraph newGraph = getGraph().clone();
        GraphProperties newProperties = new GraphProperties(propertiesMap);
        GraphInfo.setProperties(newGraph, newProperties);
        newGraph.setFixed();
        getJModel().loadGraph(newGraph);
        loadProperties(newGraph, updatePropertiesPanel);
        updateStatus();
    }

    /**
     * Changes the graph to be displayed, as well as the graph properties
     * and the status.
     * @param newGraph the new graph to be displayed
     * @param updatePropertiesPanel if {@code true}, the change did not originate
     * from the properties table, so the table has to be refreshed as well
     */
    private void loadProperties(AspectGraph newGraph, boolean updatePropertiesPanel) {
        if (updatePropertiesPanel) {
            // get the table first as creating it sets listenToPropertiesPanel to true
            PropertiesTable panel = getPropertiesPanel();
            this.listenToPropertiesPanel = false;
            panel.setProperties(GraphInfo.getProperties(newGraph));
            this.listenToPropertiesPanel = true;
        }
    }

    @Override
    public boolean setResource(QualName name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeResource(QualName name) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected ResourceModel<?> getResource() {
        return getJModel().getResourceModel();
    }

    @Override
    protected void saveResource() {
        getSaveAction().doSaveGraph(getGraph(), isDirtMinor());
        setClean();
    }

    /** Returns the jgraph component of this editor. */
    public AspectJGraph getJGraph() {
        AspectJGraph result = this.jgraph;
        if (result == null) {
            result = this.jgraph = new AspectJGraph(getSimulator(), getDisplay().getKind(), true);
            result.setLabelTree(getLabelTree());
        }
        return result;
    }

    /** The jgraph instance used in this editor. */
    private AspectJGraph jgraph;

    /**
     * @return the j-model currently being edited, or <tt>null</tt> if no editor
     *         model is set.
     */
    public AspectJModel getJModel() {
        return getJGraph().getModel();
    }

    /**
     * Refreshes the status bar and the errors, if the text on any of the cells
     * has changed.
     */
    @Override
    public void graphChanged(GraphModelEvent e) {
        boolean changed = e.getChange()
            .getInserted() != null
            || e.getChange()
                .getRemoved() != null
            || e.getChange()
                .getAttributes() != null;
        if (changed) {
            updateStatus();
        }
    }

    /**
     * We listen to the
     * {@link JGraph#JGRAPH_MODE_PROPERTY}.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        assert evt.getPropertyName()
            .equals(JGraph.JGRAPH_MODE_PROPERTY);
        JGraphMode mode = getJGraph().getMode();
        if (mode == PREVIEW_MODE || evt.getOldValue() == PREVIEW_MODE) {
            this.refreshing = true;
            getJModel().syncGraph();
            getJGraph().setEditable(mode != PREVIEW_MODE);
            getJGraph().refreshAllCells();
            getJGraph().refresh();
            this.refreshing = false;
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        // unregister listeners
        getSnapToGridAction().removeSnapListener(this);
        getJGraph().removeListeners();
    }

    /** Initialises the graph selection listener and attributed graph listener. */
    private void initListeners() {
        getJGraph().setToolTipEnabled(true);
        // Update ToolBar based on Selection Changes
        getJGraph().getSelectionModel()
            .addGraphSelectionListener(new GraphSelectionListener() {
                @Override
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
            this.undoManager = new GraphTabUndoManager();
        }
        return this.undoManager;
    }

    @Override
    protected JGraphPanel<?> getEditArea() {
        JGraphPanel<?> result = this.editArea;
        if (result == null) {
            result = this.editArea = new JGraphPanel<>(getJGraph());
            result.setEnabledBackground(JAttr.EDITOR_BACKGROUND);
            result.initialise();
            result.setEnabled(true);
        }
        return result;
    }

    /** The jgraph panel used in this editor. */
    private JGraphPanel<AspectGraph> editArea;

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
                scrollPanel.getViewport()
                    .setBackground(propertiesPanel.getBackground());
                result.add(scrollPanel);
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
            result.setEnabledBackground(JAttr.EDITOR_BACKGROUND);
        }
        return result;
    }

    /** Label panel of this tab. */
    private TitledPanel labelPanel;

    /** Lazily creates and returns the (non-{@code null}) label tree. */
    private TypeTree getLabelTree() {
        TypeTree result = this.labelTree;
        if (result == null) {
            result = this.labelTree = new TypeTree(getJGraph(), false);
        }
        return result;
    }

    private TypeTree labelTree;

    private PropertiesTable getPropertiesPanel() {
        PropertiesTable result = this.propertiesPanel;
        if (result == null) {
            this.propertiesPanel = result = new PropertiesTable(GraphProperties.Key.class, true);
            result.setName("Properties");
            result.setBackground(JAttr.EDITOR_BACKGROUND);
            result.setProperties(GraphInfo.getProperties(getGraph()));
            // add the listener after initialising the properties, to avoid needless refreshes
            result.getModel()
                .addTableModelListener(new TableModelListener() {
                    @Override
                    public void tableChanged(TableModelEvent e) {
                        if (GraphEditorTab.this.listenToPropertiesPanel) {
                            changeProperties(GraphEditorTab.this.propertiesPanel.getProperties(),
                                false);
                            setDirty(false);
                        }
                    }
                });
            this.listenToPropertiesPanel = true;
        }
        return result;
    }

    /** Properties panel of this tab. */
    private PropertiesTable propertiesPanel;
    /** Flag indicating if table changes should be propagated to the graph properties. */
    private boolean listenToPropertiesPanel;

    @Override
    protected JComponent getLowerInfoPanel() {
        JComponent result = this.syntaxHelp;
        if (result == null) {
            this.syntaxHelp = result = createSyntaxHelp();
        }
        return result;
    }

    /** Syntax help panel. */
    private JComponent syntaxHelp;

    /** Creates and returns a panel for the syntax descriptions. */
    private JComponent createSyntaxHelp() {
        initSyntax();
        final JTabbedPane tabbedPane = new JTabbedPane();
        final int nodeTabIndex = tabbedPane.getTabCount();
        tabbedPane.addTab("Nodes",
            null,
            createSyntaxList(this.nodeKeys),
            "Label prefixes that are allowed on nodes");
        final int edgeTabIndex = tabbedPane.getTabCount();
        tabbedPane.addTab("Edges",
            null,
            createSyntaxList(this.edgeKeys),
            "Label prefixes that are allowed on edges");
        if (this.role == GraphRole.RULE) {
            tabbedPane.addTab("RegExpr", null, createSyntaxList(RegExpr.getDocMap()
                .keySet()), "Syntax for regular expressions over labels");
            tabbedPane.addTab("Expr", null, createSyntaxList(Algebras.getDocMap()
                .keySet()), "Available attribute operators");
        }
        JPanel result = new TitledPanel("Label syntax help", tabbedPane, null, false);
        // add a listener that switches the syntax help between nodes and edges
        // when a cell edit is started in the JGraph
        getJGraph().addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName() == JGraph.CELL_EDIT_PROPERTY) {
                    int index =
                        evt.getNewValue() instanceof AspectJEdge ? edgeTabIndex : nodeTabIndex;
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
        final JList<String> list = new JList<>();
        list.setCellRenderer(new SyntaxCellRenderer());
        list.setBackground(JAttr.EDITOR_BACKGROUND);
        list.setListData(data.toArray(new String[data.size()]));
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

            private final ToolTipManager manager = ToolTipManager.sharedInstance();
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
        this.nodeKeys = new TreeSet<>(AspectKind.getNodeDocMap(this.role)
            .keySet());
        this.edgeKeys = new TreeSet<>(AspectKind.getEdgeDocMap(this.role)
            .keySet());
        // the edge role description for binary edges in rule graphs is inappropriate
        Help extra = null;
        for (Map.Entry<EdgeRole,Pair<String,String>> entry : EdgeRole.getRoleToDocMap()
            .entrySet()) {
            String item = entry.getValue()
                .one();
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
        this.docMap = new HashMap<>();
        this.docMap.putAll(AspectKind.getNodeDocMap(this.role));
        this.docMap.putAll(AspectKind.getEdgeDocMap(this.role));
        this.docMap.putAll(EdgeRole.getDocMap());
        this.docMap.putAll(RegExpr.getDocMap());
        this.docMap.putAll(Algebras.getDocMap());
        if (extra != null) {
            this.docMap.put(extra.getItem(), extra.getTip());
        }
    }

    /**
     * Updates the Undo/Redo Button State based on Undo Manager. Also sets
     * {@link #isDirty()} if no more undos are available.
     */
    private void updateHistoryButtons() {
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
        updateCopyPasteButtons();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                updateErrors();
            }
        });
        updateDirty();
        getTabLabel().setError(hasErrors());
    }

    /** Undoes the last registered change to the Model or the View. */
    private void undoLastEdit() {
        setSelectInsertedCells(false);
        getUndoManager().undo();
        setSelectInsertedCells(true);
        updateHistoryButtons();
    }

    /** Redoes the latest undone change to the Model or the View. */
    private void redoLastEdit() {
        setSelectInsertedCells(false);
        getUndoManager().redo();
        setSelectInsertedCells(true);
        updateHistoryButtons();
    }

    /** Sets the property whether all inserted cells are automatically selected. */
    private void setSelectInsertedCells(boolean select) {
        this.jgraph.getGraphLayoutCache()
            .setSelectsAllInsertedCells(select);
    }

    /** Mapping from syntax documentation items to corresponding tool tips. */
    private Map<String,String> docMap;
    private Set<String> nodeKeys;
    private Set<String> edgeKeys;

    /** Button for snap to grid. */
    transient JToggleButton snapToGridButton;

    /**
     * The number of edit steps the editor state is removed
     * from a saved graph.
     * This can be negative, if undos happened since the last save.
     */
    private int dirtCount;

    /** Flag indicating that there is only minor (layout) dirt in the editor. */
    private boolean dirtMinor;
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
            this.cutAction = new TransferAction(action, Options.CUT_KEY, Options.CUT_ACTION_NAME);
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
                new TransferAction(action, Options.COPY_KEY, Options.COPY_ACTION_NAME);
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
                new TransferAction(action, Options.PASTE_KEY, Options.PASTE_ACTION_NAME);
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
                new ToolbarAction(Options.REDO_ACTION_NAME, Options.REDO_KEY, Icons.REDO_ICON) {
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
                new ToolbarAction(Options.UNDO_ACTION_NAME, Options.UNDO_KEY, Icons.UNDO_ICON) {
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
     * @author rensink
     * @version $Revision $
     */
    private final class GraphTabUndoManager extends GraphUndoManager {
        @Override
        public void undoableEditHappened(UndoableEditEvent e) {
            boolean relevant = true;
            // only process edits that really changed anything
            if (GraphEditorTab.this.refreshing || getJGraph().isModelRefreshing()) {
                relevant = false;
            } else if (e.getEdit() instanceof GraphLayoutCacheChange) {
                GraphModelChange edit = (GraphModelChange) e.getEdit();
                Object[] inserted = edit.getInserted();
                Object[] removed = edit.getRemoved();
                Object[] changed = edit.getChanged();
                relevant =
                    inserted != null && inserted.length > 0 || removed != null && removed.length > 0
                        || changed != null && changed.length > 0;
            }
            if (relevant) {
                super.undoableEditHappened(e);
                setDirty(isMinor(e.getEdit()));
                updateHistoryButtons();
            }
        }

        @Override
        public void undo() {
            GraphEditorTab.this.dirtMinor &= isMinor(editToBeUndone());
            GraphEditorTab.this.dirtCount--;
            super.undo();
            updateHistoryButtons();
        }

        @Override
        public void redo() {
            GraphEditorTab.this.dirtMinor &= isMinor(editToBeRedone());
            GraphEditorTab.this.dirtCount++;
            super.redo();
            updateHistoryButtons();
        }

        /** Checks if a given edit event is minor, such as a layout action.
         * A minor edit causes fewer refresh actions to to be done as a consequence.
         */
        private boolean isMinor(UndoableEdit e) {
            boolean minor = true;
            // only process edits that really changed anything
            if (e instanceof GraphLayoutCacheChange) {
                GraphModelChange edit = (GraphModelChange) e;
                Object[] inserted = edit.getInserted();
                Object[] removed = edit.getRemoved();
                Object[] changed = edit.getChanged();
                ConnectionSet connections = edit.getConnectionSet();
                minor = (connections == null || connections.isEmpty())
                    && (inserted == null || inserted.length == 0)
                    && (removed == null || removed.length == 0);
                if (minor && changed != null) {
                    for (Object in : changed) {
                        AttributeMap attrs = (AttributeMap) edit.getAttributes()
                            .get(in);
                        if (GraphConstants.getValue(attrs) != null) {
                            minor = false;
                            break;
                        }
                    }
                }
            } else {
                minor = false;
            }
            return minor;
        }
    }

    /**
     * Action to delete the selected elements.
     */
    private class DeleteAction extends ToolbarAction {
        /** Constructs an instance of the action. */
        protected DeleteAction() {
            super(Options.DELETE_ACTION_NAME, Options.DELETE_KEY, Icons.DELETE_ICON);
            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            if (!getJGraph().isSelectionEmpty()) {
                Object[] cells = getJGraph().getSelectionCells();
                cells = getJGraph().getDescendants(cells);
                getJGraph().getModel()
                    .remove(cells);
            }
        }
    }

    /** Returns the snap to grid action, lazily creating it first. */
    private SnapToGridAction getSnapToGridAction() {
        return getSimulator().getActions()
            .getSnapToGridAction();
    }

    /**
     * General class for actions with toolbar buttons. Takes care of image, name
     * and key acceleration; moreover, the
     * <tt>actionPerformed(ActionEvent)</tt> starts by invoking
     * <tt>stopEditing()</tt>.
     * @author Arend Rensink
     * @version $Revision$
     */
    private abstract class ToolbarAction extends AbstractAction {
        /** Constructs an action with a given name, key and icon. */
        ToolbarAction(String name, KeyStroke acceleratorKey, Icon icon) {
            super(name, icon);
            putValue(Action.SHORT_DESCRIPTION, name);
            putValue(ACCELERATOR_KEY, acceleratorKey);
        }

        @Override
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
        public TransferAction(Action action, KeyStroke acceleratorKey, String name) {
            super(name, acceleratorKey, (ImageIcon) action.getValue(SMALL_ICON));
            putValue(SHORT_DESCRIPTION, name);
            setEnabled(false);
            this.action = action;
        }

        /** Redirects the Action event. */
        @Override
        public void actionPerformed(ActionEvent evt) {
            super.actionPerformed(evt);
            evt = new ActionEvent(getJGraph(), evt.getID(), evt.getActionCommand(),
                evt.getModifiers());
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
        @SuppressWarnings("rawtypes")
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index,
            boolean isSelected, boolean cellHasFocus) {
            Component result =
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (result == this) {
                setToolTipText(GraphEditorTab.this.docMap.get(value));
            }
            return result;
        }
    }
}