/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2023
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
package nl.utwente.groove.gui.display;

import static nl.utwente.groove.gui.view.GraphViewMode.EDIT_MODE;
import static nl.utwente.groove.gui.view.GraphViewMode.PREVIEW_MODE;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListSelectionModel;
import javax.swing.Icon;
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

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.algebra.Algebras;
import nl.utwente.groove.algebra.UserSignature;
import nl.utwente.groove.annotation.HelpMap;
import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.aspect.AspectKind;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.grammar.model.NamedResourceModel;
import nl.utwente.groove.grammar.rule.RegExpr;
import nl.utwente.groove.graph.EdgeRole;
import nl.utwente.groove.grammar.ResourceProperties;
import nl.utwente.groove.graph.GraphRole;
import nl.utwente.groove.gui.Icons;
import nl.utwente.groove.gui.Options;
import nl.utwente.groove.gui.action.SnapToGridAction;
import nl.utwente.groove.gui.dialog.PropertiesTable;
import nl.utwente.groove.gui.look.Values;
import nl.utwente.groove.gui.view.AspectGraphViewModel;
import nl.utwente.groove.gui.view.EditHistory;
import nl.utwente.groove.gui.view.GraphCanvas;
import nl.utwente.groove.gui.view.GraphClipboard;
import nl.utwente.groove.gui.view.GraphCanvasListener;
import nl.utwente.groove.gui.view.GraphViewMode;
import nl.utwente.groove.gui.view.ViewCell;
import nl.utwente.groove.gui.view.ViewEdge;
import nl.utwente.groove.io.store.EditType;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.QualName;

/**
 * Dialog wrapping a graph editor, such that no file operations are possible.
 * @author Arend Rensink
 * @version $Revision$
 */
final public class AspectEditorTab extends AspectTab
    implements GraphCanvasListener<@NonNull AspectGraph> {
    /**
     * Constructs a new tab instance.
     * @param parent the component on which this panel is placed
     * @param role the input graph for the editor
     */
    public AspectEditorTab(ResourceDisplay parent, final GraphRole role) {
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
        var oldHistory = getHistory();
        if (oldHistory != null) {
            oldHistory.removeListener(this.historyListener);
        }
        this.editModel = null;
        setQualName(graph.getQualName());
        AspectGraphViewModel newModel = getCanvas().newViewModel();
        newModel.setBeingEdited(true);
        AspectGraph graphClone = graph.clone();
        graphClone.setFixed();
        newModel.loadGraph(graphClone);
        getCanvas().setViewModel(newModel);
        loadProperties(graphClone, true);
        getNonNullHistory().addListener(this.historyListener);
        setClean();
        updateHistoryButtons();
        updateStatus();
        if (getCanvas().getMode() == PREVIEW_MODE) {
            enterPreview();
        }
    }

    /** Returns the graph being edited. */
    @Override
    public @NonNull AspectGraph getGraph() {
        var result = getNonNullEditModel().getGraph();
        assert result != null; // the edit model always holds a graph
        return result;
    }

    @Override
    protected JToolBar createToolBar() {
        JToolBar result = super.createToolBar();
        result.addSeparator();
        result.add(getController().getModeButton(EDIT_MODE));
        result.add(getController().getModeButton(PREVIEW_MODE));
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
            if (element instanceof JButton button) {
                Action action = button.getAction();
                if (action != null) {
                    getCanvas().addAccelerator(action);
                }
            }
        }
        // ensure the JGraph gets focus as soon as the graph panel
        // is clicked anywhere
        // for reasons not clear to me, mouse listeners do not work on
        // the level of the GraphPanel
        toolBar.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                getCanvas().getComponent().requestFocus();
            }
        });
    }

    /** Returns the role of the graph being edited. */
    public GraphRole getRole() {
        return this.role;
    }

    @Override
    public void updateGrammar(GrammarModel grammar) {
        AspectGraph source = grammar.getModelGraph(getResourceKind(), getQualName());
        // test if the graph being edited is still in the grammar;
        // if not, silently dispose it - it's too late to do anything else!
        if (source == null) {
            dispose();
        } else if (isDirty() || source == getGraph()) {
            // to keep the edit history, don't change the underlying graph
            // check if the properties have changed
            ResourceProperties properties = ResourceProperties.getProperties(source);
            if (!properties.equals(ResourceProperties.getProperties(getGraph()))) {
                changeProperties(properties.entryStream(), true);
            } else {
                getNonNullEditModel().setGraphModified();
                getCanvas().refreshAll(false);
            }
            updateStatus();
        } else {
            setGraph(source);
        }
    }

    @Override
    public void setClean() {
        getNonNullHistory().setClean();
        updateDirty();
    }

    /**
     * Adds dirt that is not an edit of the history (a change of the properties).
     * @param minor if {@code true}, the change is minor (layout only)
     */
    public void setDirty(boolean minor) {
        getNonNullHistory().markDirty(minor);
        updateDirty();
    }

    @Override
    public boolean isDirty() {
        var history = getHistory();
        return history != null && history.isDirty();
    }

    /** Indicates if all dirt is minor (layout only), so that saving needs no grammar reload. */
    public boolean isDirtMinor() {
        var history = getHistory();
        return history == null || history.isDirtMinor();
    }

    /** Renames the edited graph. */
    public void rename(QualName newName) {
        AspectGraph newGraph = getGraph().rename(newName);
        getNonNullEditModel().loadGraph(newGraph);
        loadProperties(newGraph, true);
        setQualName(newName);
        updateStatus();
    }

    /**
     * Changes the properties of the graph being edited.
     * @param propertiesStream the new properties
     * @param updatePropertiesPanel if {@code true}, the change did not originate
     * from the properties table, so the table has to be refreshed as well
     */
    private void changeProperties(Stream<Map.Entry<String,String>> propertiesStream,
                                  boolean updatePropertiesPanel) {
        AspectGraph newGraph = getGraph().clone();
        ResourceProperties newProperties = new ResourceProperties();
        propertiesStream.forEach(e -> newProperties.setProperty(e.getKey(), e.getValue()));
        ResourceProperties.setProperties(newGraph, newProperties);
        newGraph.setFixed();
        getNonNullEditModel().loadGraph(newGraph);
        loadProperties(newGraph, updatePropertiesPanel);
        updateStatus();
    }

    /**
     * Loads the properties of a new graph into the properties table.
     * @param newGraph the new graph to be displayed
     * @param updatePropertiesPanel if {@code true}, the change did not originate
     * from the properties table, so the table has to be refreshed as well
     */
    private void loadProperties(AspectGraph newGraph, boolean updatePropertiesPanel) {
        if (updatePropertiesPanel) {
            loadProperties(ResourceProperties.getProperties(newGraph), newGraph);
        }
    }

    /* Passes the edited properties on to the graph. */
    @Override
    protected void propertiesEdited(PropertiesTable panel) {
        changeProperties(panel.getProperties().entrySet().stream(), false);
        setDirty(false);
    }

    @Override
    protected NamedResourceModel<?> getResource() {
        return getNonNullEditModel().getResourceModel();
    }

    @Override
    protected void saveResource() {
        getSaveAction().doSaveGraph(getGraph(), isDirtMinor());
        setClean();
    }

    /**
     * Returns the model being edited: the stashed edit model while a preview
     * is on display, otherwise the canvas' model.
     */
    public @Nullable AspectGraphViewModel getEditModel() {
        AspectGraphViewModel result = this.editModel;
        if (result == null) {
            result = getViewModel();
        }
        return result;
    }

    private @NonNull AspectGraphViewModel getNonNullEditModel() {
        var result = getEditModel();
        assert result != null;
        return result;
    }

    /** Returns the edit history of the model being edited; {@code null} if there is no model yet. */
    private @Nullable EditHistory<@NonNull AspectGraph> getHistory() {
        var model = getEditModel();
        return model == null
            ? null
            : model.getEditHistory();
    }

    private EditHistory<@NonNull AspectGraph> getNonNullHistory() {
        var result = getHistory();
        assert result != null; // the edit model records its edits
        return result;
    }

    /** Listener on the edit history: refreshes the buttons and the status after every change. */
    private final Runnable historyListener = () -> {
        updateHistoryButtons();
        updateStatus();
    };

    /** Enters or exits the preview when the canvas mode changes to or from preview mode. */
    @Override
    public void modeChanged(GraphCanvas<@NonNull AspectGraph> canvas, GraphViewMode oldMode,
                            GraphViewMode mode) {
        if (mode == PREVIEW_MODE || oldMode == PREVIEW_MODE) {
            if (mode == PREVIEW_MODE) {
                enterPreview();
            } else {
                exitPreview();
            }
            getCanvas().setEditable(mode != PREVIEW_MODE);
            getCanvas().refreshAll(true);
            getCanvas().repaint();
            updateHistoryButtons();
        }
    }

    @AIGenerated("Claude Fable 5, 2026-08")
    private void enterPreview() {
        AspectGraphViewModel model = getNonNullEditModel();
        model.syncGraph();
        var graph = model.getGraph();
        assert graph != null;
        AspectGraphViewModel previewModel = getCanvas().newViewModel();
        previewModel.loadGraph(graph);
        this.editModel = model;
        getCanvas().setViewModel(previewModel);
    }

    @AIGenerated("Claude Fable 5, 2026-08")
    private void exitPreview() {
        AspectGraphViewModel model = this.editModel;
        if (model != null) {
            this.editModel = null;
            getCanvas().setViewModel(model);
        }
    }

    /**
     * The model being edited, while the canvas displays a preview clone;
     * {@code null} when not in preview mode. While this is set,
     * {@link #getEditModel()} returns it rather than the displayed clone, so that
     * all edits and queries keep addressing the edit model.
     */
    private @Nullable AspectGraphViewModel editModel;

    @Override
    public void dispose() {
        super.dispose();
        // unregister listeners
        getCanvas().removeCanvasListener(this);
        getSnapToGridAction().removeSnapListener(this);
        getCanvas().removeListeners();
    }

    /** Initialises the graph selection listener and attributed graph listener. */
    private void initListeners() {
        getCanvas().setToolTipEnabled(true);
        // Update ToolBar based on Selection Changes
        getCanvas().addCanvasListener(new GraphCanvasListener<@NonNull AspectGraph>() {
            @Override
            public void selectionChanged(GraphCanvas<@NonNull AspectGraph> canvas) {
                // Update Button States based on Current Selection
                boolean selected = !canvas.isSelectionEmpty();
                getDeleteAction().setEnabled(selected);
                getCopyAction().setEnabled(selected);
                getCutAction().setEnabled(selected);
            }
        });
        getCanvas().addCanvasListener(this);
        getCanvas().addCanvasListener(getSimulator().getActions().getSelectColorAction());
        getSnapToGridAction().addSnapListener(this);
    }

    @Override
    protected JComponent getLowerInfoPanel() {
        JComponent result = this.syntaxHelp;
        if (result == null) {
            this.syntaxHelp = result = createSyntaxHelp();
            UserSignature.addUser(() -> this.syntaxHelp = null);
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
        tabbedPane
            .addTab("Nodes", null, createSyntaxList(this.nodeKeys),
                    "Label prefixes that are allowed on nodes");
        final int edgeTabIndex = tabbedPane.getTabCount();
        tabbedPane
            .addTab("Edges", null, createSyntaxList(this.edgeKeys),
                    "Label prefixes that are allowed on edges");
        if (this.role == GraphRole.RULE) {
            tabbedPane
                .addTab("RegExpr", null, createSyntaxList(RegExpr.getDocMap().keySet()),
                        "Syntax for regular expressions over labels");
            tabbedPane
                .addTab("Expr", null, createSyntaxList(Algebras.getExprDocMap().keySet()),
                        "Syntax for attribute expressions");
            tabbedPane
                .addTab("Ops", null, createSyntaxList(Algebras.getOpDocMap().keySet()),
                        "Available attribute operators");
        }
        JPanel result = new TitledPanel("Label syntax help", tabbedPane, null, false);
        // add a listener that switches the syntax help between nodes and edges
        // when a cell edit is started in the JGraph
        getCanvas().addCanvasListener(new GraphCanvasListener<@NonNull AspectGraph>() {
            @Override
            public void editingStarted(GraphCanvas<@NonNull AspectGraph> canvas,
                                       ViewCell<@NonNull AspectGraph> cell) {
                int index = cell instanceof ViewEdge
                    ? edgeTabIndex
                    : nodeTabIndex;
                tabbedPane.setSelectedIndex(index);
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
        list.setBackground(Values.EDITOR_BACKGROUND);
        list.setListData(data.toArray(new String[data.size()]));
        list.addMouseListener(new DismissDelayer(list));
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
        this.nodeKeys = new TreeSet<>(AspectKind.getNodeDocMap(this.role).keySet());
        this.edgeKeys = new TreeSet<>(AspectKind.getEdgeDocMap(this.role).keySet());
        // the edge role description for binary edges in rule graphs is inappropriate
        for (var entry : EdgeRole.getRoleToDocMap().entrySet()) {
            String item = entry.getValue().getItem();
            switch (entry.getKey()) {
            case BINARY:
                // for rules, this is already covered by the ATOM aspect type
                if (this.role != GraphRole.RULE) {
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
        this.docMap = new HelpMap();
        this.docMap.putAll(AspectKind.getNodeDocMap(this.role));
        this.docMap.putAll(AspectKind.getEdgeDocMap(this.role));
        this.docMap.putAll(EdgeRole.getDocMap());
        this.docMap.putAll(RegExpr.getDocMap());
        this.docMap.putAll(Algebras.getOpDocMap());
        this.docMap.putAll(Algebras.getExprDocMap());
    }

    private void updateHistoryButtons() {
        // undo/redo would change the stashed edit model while the preview
        // clone is on display, so they are disabled during preview
        boolean previewing = getCanvas().getMode() == PREVIEW_MODE;
        var history = getHistory();
        getUndoAction().setEnabled(!previewing && history != null && history.canUndo());
        getRedoAction().setEnabled(!previewing && history != null && history.canRedo());
        updateDirty();
    }

    /** Sets the enabling of the transfer buttons. */
    private void updateCopyPasteButtons() {
        boolean previewing = getCanvas().getMode() == PREVIEW_MODE;
        boolean hasSelection = !getCanvas().isSelectionEmpty();
        getCopyAction().setEnabled(!previewing && hasSelection);
        getCutAction().setEnabled(!previewing && hasSelection);
        getDeleteAction().setEnabled(!previewing && hasSelection);
        getPasteAction().setEnabled(!previewing && GraphClipboard.hasFragment());
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
        getCanvas().setGridEnabled(snap);
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
        getTabLabel().setSeverity(getSeverity());
    }

    /** Undoes the last registered change to the Model or the View. */
    private void undoLastEdit() {
        getNonNullHistory().undo();
    }

    /** Redoes the latest undone change to the Model or the View. */
    private void redoLastEdit() {
        getNonNullHistory().redo();
    }

    /** Mapping from syntax documentation items to corresponding tool tips. */
    private HelpMap docMap;
    private Set<String> nodeKeys;
    private Set<String> edgeKeys;

    /** Button for snap to grid. */
    transient JToggleButton snapToGridButton;

    /** The role of the graph being edited. */
    private final GraphRole role;

    /**
     * Lazily creates and returns the action to cut graph elements in the
     * editor.
     */
    private Action getCutAction() {
        if (this.cutAction == null) {
            this.cutAction
                = new ToolbarAction(Options.CUT_ACTION_NAME, Options.CUT_KEY, Icons.CUT_ICON) {
                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        super.actionPerformed(evt);
                        if (GraphClipboard.cut(getCanvas())) {
                            getPasteAction().setEnabled(true);
                        }
                    }
                };
            this.cutAction.setEnabled(false);
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
            this.copyAction
                = new ToolbarAction(Options.COPY_ACTION_NAME, Options.COPY_KEY, Icons.COPY_ICON) {
                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        super.actionPerformed(evt);
                        if (GraphClipboard.copy(getCanvas())) {
                            getPasteAction().setEnabled(true);
                        }
                    }
                };
            this.copyAction.setEnabled(false);
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
            this.pasteAction = new ToolbarAction(Options.PASTE_ACTION_NAME, Options.PASTE_KEY,
                Icons.PASTE_ICON) {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    super.actionPerformed(evt);
                    GraphClipboard.paste(getCanvas());
                }
            };
            this.pasteAction.setEnabled(GraphClipboard.hasFragment());
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
            this.redoAction
                = new ToolbarAction(EditType.REDO_ACTION_NAME, Options.REDO_KEY, Icons.REDO_ICON) {
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
            this.undoAction
                = new ToolbarAction(EditType.UNDO_ACTION_NAME, Options.UNDO_KEY, Icons.UNDO_ICON) {
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
            super(Options.DELETE_ACTION_NAME, Options.DELETE_KEY, Icons.DELETE_ICON);
            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            if (!getCanvas().isSelectionEmpty()) {
                getNonNullEditModel().remove(getCanvas().getSelection());
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
            getCanvas().finishEditing();
        }
    }

    /** Private cell renderer class that inserts the correct tool tips. */
    private class SyntaxCellRenderer extends DefaultListCellRenderer {
        @SuppressWarnings("rawtypes")
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            Component result
                = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (result == this) {
                setToolTipText(AspectEditorTab.this.docMap.get(value));
            }
            return result;
        }
    }
}