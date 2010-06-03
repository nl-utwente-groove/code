/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License. $Id: Editor.java,v
 * 1.56 2008-03-05 06:07:23 rensink Exp $
 */
package groove.gui;

import static groove.gui.Options.HELP_MENU_NAME;
import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.Graph;
import groove.graph.GraphInfo;
import groove.graph.GraphProperties;
import groove.graph.Node;
import groove.graph.NodeEdgeHashMap;
import groove.graph.NodeEdgeMap;
import groove.graph.TypeGraph;
import groove.gui.dialog.AboutBox;
import groove.gui.dialog.ErrorDialog;
import groove.gui.dialog.PropertiesDialog;
import groove.gui.dialog.SingleListDialog;
import groove.gui.jgraph.AspectJModel;
import groove.gui.jgraph.EditorJGraph;
import groove.gui.jgraph.EditorJModel;
import groove.gui.jgraph.GraphJModel;
import groove.gui.jgraph.JCell;
import groove.gui.jgraph.JGraph;
import groove.gui.jgraph.JModel;
import groove.io.AspectGxl;
import groove.io.ExtensionFilter;
import groove.io.GrooveFileChooser;
import groove.io.LayedOutXml;
import groove.io.PriorityFileName;
import groove.util.Groove;
import groove.util.Property;
import groove.util.Version;
import groove.view.FormatError;
import groove.view.FormatException;
import groove.view.GraphView;
import groove.view.RuleView;
import groove.view.View;
import groove.view.StoredGrammarView.TypeViewList;
import groove.view.aspect.AspectGraph;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.TableCellEditor;

import org.jgraph.event.GraphModelEvent;
import org.jgraph.event.GraphModelListener;
import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;
import org.jgraph.graph.GraphUndoManager;

/**
 * Simplified but usable graph editor.
 * @author Gaudenz Alder, modified by Arend Rensink and Carel van Leeuwen
 * @version $Revision$ $Date: 2008-03-05 06:07:23 $
 */
public class Editor implements GraphModelListener, PropertyChangeListener {
    /**
     * Constructs an editor frame with an initially empty graph and a given
     * display options setting.
     * @param options the display options object; may be <code>null</code>
     */
    Editor(JFrame frame, Options options) {
        // force the LAF to be set
        groove.gui.Options.initLookAndFeel();
        // Construct the main components
        this.options = options;
        if (frame == null) {
            this.frame = new JFrame(EDITOR_NAME);
            // this.frame.getRootPane().setDoubleBuffered(false);
        } else {
            this.frame = frame;
            this.frame.setTitle(EDITOR_NAME);
        }
        this.jgraph = new EditorJGraph(this);
        this.jgraph.setExporter(getExporter());
        initListeners();
        initGUI();
    }

    /**
     * Constructs an editor frame with an initially empty graph. It is not
     * configured as an auxiliary component.
     */
    public Editor() {
        this(null, null);
    }

    /** Creates the frame and makes it visible. */
    public void start() {
        getFrame().pack();
        getFrame().setVisible(true);
    }

    /** Returns the frame in which the editor is displayed. */
    public final JFrame getFrame() {
        return this.frame;
    }

    /**
     * Indicates whether the editor is in node editing mode.
     * @return <tt>true</tt> if the editor is in node editing mode.
     */
    public boolean isNodeMode() {
        return getNodeModeButton().isSelected();
    }

    /**
     * Indicates whether the editor is in edge editing mode.
     * @return <tt>true</tt> if the editor is in edge editing mode.
     */
    public boolean isEdgeMode() {
        return getEdgeModeButton().isSelected();
    }

    /**
     * Sets the graph to be edited
     * @param graph the graph to be edited; if <code>null</code>, an empty model
     *        is started.
     */
    public void setPlainGraph(Graph graph) {
        setErrors(null);
        if (graph == null) {
            setModel(new EditorJModel(this));
        } else {
            // don't set the errors, now as they will be computed again anyway
            // setErrors(GraphInfo.getErrors(graph));
            getModel().replace(
                GraphJModel.newInstance(graph, getOptions(), true));
            // set the model afresh to make sure everything gets updated properly
            setRole(roleIndexMap.get(GraphInfo.getRole(graph)));
            setModel(getModel());
        }
    }

    /** Sets the type graph for this editor. */
    public void setTypeView(TypeViewList typeView) {
        this.type = null;
        this.typeViewList = null;
        if (typeView != null) {
            try {
                this.type = typeView.toModel();
                this.typeViewList = typeView;
            } catch (FormatException e) {
                // do nothing
            }
        }
    }

    /**
     * Generates the aspect graph stored in this editor
     * from the editor contents.
     */
    private void setAspectGraph() {
        Map<Element,JCell> plainToModelMap = new HashMap<Element,JCell>();
        Graph result = getModel().toPlainGraph(plainToModelMap);
        GraphInfo.setRole(result, getRole(false));
        GraphInfo.setVersion(result, Version.GXL_VERSION);
        NodeEdgeMap plainToAspectMap = new NodeEdgeHashMap();
        this.graph =
            AspectGraph.getFactory().fromPlainGraph(result, plainToAspectMap);
        this.graphToModelMap = new HashMap<Element,JCell>();
        for (Map.Entry<Element,JCell> plainToModelEntry : plainToModelMap.entrySet()) {
            Element plainKey = plainToModelEntry.getKey();
            Element aspectKey =
                plainKey instanceof Node
                        ? plainToAspectMap.getNode((Node) plainKey)
                        : plainToAspectMap.getEdge((Edge) plainKey);
            this.graphToModelMap.put(aspectKey, plainToModelEntry.getValue());
        }
    }

    /** Returns the aspect graph generated from the current editor contents. */
    public AspectGraph getAspectGraph() {
        if (this.graph == null) {
            setAspectGraph();
        }
        return this.graph;
    }

    /**
     * Changes the graph being edited to a given j-model, with a given name. If
     * the model is <tt>null</tt>, a fresh {@link EditorJModel}is created;
     * otherwise, the given j-model is copied into a new {@link EditorJModel}.
     * @param model the j-model to be set
     * @see EditorJModel#EditorJModel(Editor)
     */
    private void setModel(EditorJModel model) {
        // unregister listeners with the model
        getModel().removeUndoableEditListener(getUndoManager());
        getModel().removeGraphModelListener(this);
        this.jgraph.setModel(model);
        setCurrentGraphModified(false);
        getUndoManager().discardAllEdits();
        getModel().addUndoableEditListener(getUndoManager());
        getModel().addGraphModelListener(this);
        updateHistoryButtons();
        updateStatus();
        updateTitle();
    }

    /**
     * @return the j-model currently being edited, or <tt>null</tt> if no editor
     *         model is set.
     */
    public EditorJModel getModel() {
        return this.jgraph == null ? null : this.jgraph.getModel();
    }

    /** Returns the type graph set in this editor, if any. */
    public TypeViewList getTypeViewList() {
        return this.typeViewList;
    }

    /** Returns the type graph set in this editor, if any. */
    public TypeGraph getType() {
        return this.type;
    }

    /**
     * Creates and returns a view, based on the current aspect graph.
     */
    private View<?> toView() {
        View<?> result = getAspectGraph().toView();
        if (getType() != null) {
            if (result instanceof GraphView) {
                ((GraphView) result).setType(getType());
            }
            if (result instanceof RuleView) {
                ((RuleView) result).setType(getType());
            }
        }
        return result;
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
            setErrors(null);
            updateStatus();
        }
    }

    /**
     * We listen to the {@link #ROLE_PROPERTY}.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        getGraphRoleButton().setSelected(getRoleIndex() == GRAPH_INDEX);
        getRuleRoleButton().setSelected(getRoleIndex() == RULE_INDEX);
        getTypeRoleButton().setSelected(getRoleIndex() == TYPE_INDEX);
        // we need to refresh because the errors may have changed
        updateStatus();
        updateTitle();
    }

    /** Tests if a given cell occurs in the set of erroneous cells. */
    public boolean hasError(JCell cell) {
        return this.errorCells.contains(cell);
    }

    /**
     * Handler method to execute a {@link Editor.OpenGraphAction}. Invokes a
     * file chooser dialog, and calls {@link #doOpenGraph(File)} if a file is
     * selected.
     */
    protected void handleOpenGraph() {
        int result = getGraphChooser().showOpenDialog(getGraphPanel());
        if (result == JFileChooser.APPROVE_OPTION && confirmAbandon()) {
            File selectedFile = getGraphChooser().getSelectedFile();
            try {
                doOpenGraph(selectedFile);
            } catch (IOException exc) {
                showErrorDialog(String.format("Error while loading %s",
                    selectedFile), exc);
            }
        }
    }

    /**
     * Handler method to execute a {@link Editor.SaveGraphAction} or
     * {@link SaveGraphAsAction}. If the action was save-as, or there is no
     * model name, invokes a file chooser dialog. Calls
     * {@link #doSaveGraph(File)} if a file is selected. The return value is the
     * save file, or <code>null</code> if nothing was saved.
     * @param as if <code>true</code>, the action was save-as and a save dialog
     *        should always be shown
     */
    protected File handleSaveGraph(boolean as) {
        if (getOptions().isSelected(Options.PREVIEW_ON_SAVE_OPTION)
            && !handlePreview(null)) {
            return null;
        } else if (getAspectGraph().hasErrors()) {
            JOptionPane.showMessageDialog(getFrame(),
                "Cannot save graph with syntax errors", null,
                JOptionPane.WARNING_MESSAGE);
            return null;
        } else {
            File toFile = getCurrentFile();
            if (as || toFile == null) {
                toFile =
                    ExtensionFilter.showSaveDialog(getGraphChooser(),
                        getGraphPanel(), toFile);
            }
            if (toFile != null) {
                try {
                    doSaveGraph(toFile);
                    // parse the file name to extract any priority info
                    PriorityFileName priorityName =
                        new PriorityFileName(toFile);
                    String actualName = priorityName.getActualName();
                    setModelName(actualName);
                    if (priorityName.hasPriority()) {
                        getModel().getProperties().setPriority(
                            priorityName.getPriority());
                    }
                    toFile =
                        new File(toFile.getParentFile(), actualName
                            + ExtensionFilter.getExtension(toFile));
                    setCurrentFile(toFile);
                } catch (Exception exc) {
                    showErrorDialog(String.format("Error while saving to %s",
                        toFile), exc);
                    toFile = null;
                }
            }
            return toFile;
        }
    }

    /**
     * Shows a preview dialog, and possibly replaces the edited graph by the
     * previewed model.
     * @return <tt>true</tt> if the dialog was confirmed; if so, the jModel is
     *         aspect correct (and so can be saved).
     */
    protected boolean handlePreview(String okOption) {
        AspectJModel previewedModel = showPreviewDialog(toView(), okOption);
        if (previewedModel != null) {
            // setSelectInsertedCells(false);
            Graph plainGraph = previewedModel.toPlainGraph();
            setErrors(GraphInfo.getErrors(plainGraph));
            getModel().replace(
                GraphJModel.newInstance(plainGraph, getOptions(), true));
            // copy the edited properties into the model
            getModel().setProperties(previewedModel.getProperties());
            // setSelectInsertedCells(true);
            return true;
        } else {
            return false;
        }
    }

    /**
     * If the editor has unsaved changes, asks if these should be abandoned;
     * then calls {@link #doQuit()}.
     */
    protected void handleQuit() {
        if (confirmAbandon()) {
            doQuit();
        }
    }

    /**
     * Makes sure all resources are abandoned.
     */
    protected void doQuit() {
        getFrame().dispose();
        getGraphPanel().dispose();
        // try to persist the user preferences
        try {
            Preferences.userRoot().flush();
        } catch (BackingStoreException e) {
            // do nothing
        }
    }

    /**
     * Reads the graph to be edited from a file. If the file does not exist, a
     * new, empty model with the given name is created.
     * @param fromFile the file to read from
     * @throws IOException if <tt>fromFile</tt> did not contain a correctly
     *         formatted graph
     */
    private void doOpenGraph(final File fromFile) throws IOException {
        setCurrentFile(fromFile);
        // first create a graph from the gxl file
        final AspectGraph graph = this.layoutGxl.unmarshalGraph(fromFile);
        // load the model in the event dispatch thread, to avoid concurrency
        // issues
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                setPlainGraph(graph.toPlainGraph());
            }
        });
    }

    /**
     * Saves the currently edited model as an ordinary graph to a file.
     * @param toFile the file to save to
     * @throws IOException if <tt>fromFile</tt> did not contain a correctly
     *         formatted graph
     */
    private void doSaveGraph(File toFile) throws IOException {
        AspectGraph saveGraph = getAspectGraph();
        this.layoutGxl.marshalGraph(saveGraph, toFile);
        setGraphSaved();
    }

    /** Initialises the graph selection listener and attributed graph listener. */
    protected void initListeners() {
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
        getChangeSupport().addPropertyChangeListener(ROLE_PROPERTY, this);
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
                    super.undoableEditHappened(e);
                    updateHistoryButtons();
                }
            };
        }
        return this.undoManager;
    }

    /** Initialises the GUI. */
    protected void initGUI() {
        getFrame().setIconImage(Groove.GROOVE_ICON_16x16.getImage());
        // Set Close Operation to Exit
        getFrame().setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        getFrame().addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evt) {
                handleQuit();
            }
        });
        getFrame().setJMenuBar(createMenuBar());
        getFrame().setContentPane(createContentPanel(createToolBar()));
    }

    /**
     * Creates a panel showing a given toolbar, and the graph and status panels
     * of the editor.
     */
    JPanel createContentPanel(JToolBar toolBar) {
        JPanel result = new JPanel(new BorderLayout(), false);
        // initialize the main editor panel
        // Add a ToolBar
        result.add(toolBar, BorderLayout.NORTH);
        result.add(getGraphPanel(), BorderLayout.CENTER);
        result.add(getStatusPanel(), BorderLayout.SOUTH);
        return result;
    }

    /**
     * Returns a file chooser for loading graphs, after lazily creating it.
     */
    protected MyFileChooser getGraphChooser() {
        if (this.graphChooser == null) {
            final MyFileChooser result =
                this.graphChooser = new MyFileChooser();
            // listen to file filter changes; possibly we have to update the
            // editor role
            result.addPropertyChangeListener(
                JFileChooser.FILE_FILTER_CHANGED_PROPERTY,
                new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent evt) {
                        // change the extension of the current file name
                        FileFilter filter = (FileFilter) evt.getNewValue();
                        File currentFile = getCurrentFile();
                        if (filter instanceof ExtensionFilter) {
                            if (currentFile != null) {
                                String name =
                                    ExtensionFilter.getPureName(currentFile);
                                setCurrentFile(new File(
                                    currentFile.getPath(),
                                    ((ExtensionFilter) filter).addExtension(name)));
                            }
                            // change the role of the currently edited graph
                            setRole(result.getFilterRole(filter));
                        }
                    }
                });

            // graphOpenChooser.addChoosableFileFilter(graphFilter);
        }
        return this.graphChooser;
    }

    /**
     * Sets the modified status of the currently edited graph. Also updates the
     * frame title to reflect the new modified status.
     * @param modified the new modified status
     * @see #isCurrentGraphModified()
     */
    protected void setCurrentGraphModified(boolean modified) {
        this.currentGraphModified = modified;
        updateTitle();
    }

    /**
     * Returns the current modified status of the underlying jgraph.
     * @see #setCurrentGraphModified(boolean)
     */
    protected boolean isCurrentGraphModified() {
        return this.currentGraphModified;
    }

    /**
     * Registers that a graph has been saved.
     * @see #isAnyGraphSaved()
     */
    protected void setGraphSaved() {
        this.anyGraphSaved = true;
        setCurrentGraphModified(false);
    }

    /**
     * Indicates if any graph was saved during the lifetime of this editor.
     */
    protected boolean isAnyGraphSaved() {
        return this.anyGraphSaved;
    }

    /** Indicates if the current graph has any load errors. */
    private boolean hasErrors() {
        return this.errors != null;
    }

    /** Returns the collection of load errors in the current graph. */
    private Collection<FormatError> getErrors() {
        return this.errors;
    }

    /** Sets the load errors in the current graph to a given collection. */
    private void setErrors(Collection<FormatError> errors) {
        this.errors = errors;
    }

    /**
     * Indicates if we are editing a rule or a graph.
     * @return <code>true</code> if we are editing a graph.
     */
    private int getRoleIndex() {
        return this.roleIndex;
    }

    /**
     * Returns a textual representation of the graph role, with the first letter
     * capitalised on demand.
     * @param upper if <code>true</code>, the first letter is capitalised
     */
    String getRole(boolean upper) {
        String role = roles[getRoleIndex()];
        if (upper) {
            char[] result = role.toCharArray();
            result[0] = Character.toUpperCase(result[0]);
            return String.valueOf(result);
        } else {
            return role;
        }
    }

    /**
     * Sets the edit role to {@link Groove#GRAPH_ROLE} or
     * {@link Groove#RULE_ROLE}.
     * @param roleIndex the edit role to be set; if <code>null</code>, it is set
     *        to {@link #GRAPH_INDEX}.
     * @return <code>true</code> if the edit type was actually changed;
     *         <code>false</code> if it was already equal to <code>role</code>
     */
    boolean setRole(int roleIndex) {
        int oldRoleIndex = this.roleIndex;
        boolean result = roleIndex != oldRoleIndex;
        // set the value if it has changed
        if (result) {
            this.roleIndex = roleIndex;
            // fire change only if there was a previous value
            getChangeSupport().firePropertyChange(ROLE_PROPERTY, oldRoleIndex,
                roleIndex);
        }
        return result;
    }

    /**
     * Sets the name of the editor model. The name may be <tt>null</tt> if the
     * model is to be anonymous.
     * @param name new name for the editor model
     * @see EditorJModel#setName(String)
     */
    protected void setModelName(String name) {
        if (getModel() != null) {
            getModel().setName(name);
            updateTitle();
        }
    }

    /**
     * Returns the current name of the editor model. The name may be
     * <tt>null</tt> if the model is anonymous.
     * @see EditorJModel#getName()
     */
    protected String getModelName() {
        if (getModel() != null) {
            return getModel().getName();
        } else {
            return null;
        }
    }

    /**
     * Lazily creates and returns the property change support object for this
     * editor.
     */
    private PropertyChangeSupport getChangeSupport() {
        if (this.propertyChangeSupport == null) {
            this.propertyChangeSupport = new PropertyChangeSupport(this);
        }
        return this.propertyChangeSupport;
    }

    /**
     * Sets the name of the graph in the title bar. If the indicated name is
     * <tt>null</tt>, the appropriate element of {@link #TITLE} is used.
     */
    protected void updateTitle() {
        String modelName = getModelName();
        if (modelName == null) {
            modelName = TITLE[getRoleIndex()];
        }
        String title =
            (this.currentGraphModified ? MODIFIED_INDICATOR : "") + modelName
                + " - " + EDITOR_NAME;
        Component window = getRootComponent();
        if (window instanceof JFrame) {
            ((JFrame) window).setTitle(title);
        } else if (window instanceof JDialog) {
            ((JDialog) window).setTitle(title);
        }
    }

    /**
     * Returns the top level component of the graph panel in the containmeint
     * hierarchy.
     */
    protected Component getRootComponent() {
        Component component = getGraphPanel();
        while (component != null
            && !(component instanceof JFrame || component instanceof JDialog)) {
            component = component.getParent();
        }
        return component;
    }

    JGraphPanel<?> getGraphPanel() {
        if (this.jGraphPanel == null) {
            this.jGraphPanel =
                new JGraphPanel<EditorJGraph>(this.jgraph, false, false,
                    getOptions());
        }
        return this.jGraphPanel;
    }

    /**
     * Creates and returns the menu bar. Requires the actions to have been
     * initialised first.
     */
    JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        // file menu, only if the component is not auxiliary
        // if (getFixedEditType() == null) {
        menuBar.add(createFileMenu());
        // }
        menuBar.add(createEditMenu());
        menuBar.add(createPropertiesMenu());
        menuBar.add(createDisplayMenu());
        menuBar.add(createOptionsMenu());
        menuBar.add(createHelpMenu());
        return menuBar;
    }

    /**
     * Creates and returns a file menu for the menu bar.
     */
    JMenu createFileMenu() {
        JMenu result = new JMenu(Options.FILE_MENU_NAME);
        result.setMnemonic(Options.FILE_MENU_MNEMONIC);
        result.add(getNewAction());
        result.add(getOpenGraphAction());
        result.addSeparator();
        result.add(getSaveGraphAction());
        // Save as not yet enabled (for backward compatibility reasons)
        // result.add(getSaveGraphAsAction());
        result.add(getJGraph().getExportAction());
        result.addSeparator();
        result.add(getQuitAction());
        return result;
    }

    /**
     * Creates and returns an edit menu for the menu bar.
     */
    JMenu createEditMenu() {
        JMenu result = new JMenu(Options.EDIT_MENU_NAME);
        result.setMnemonic(Options.EDIT_MENU_MNEMONIC);
        result.add(getUndoAction());
        result.add(getRedoAction());
        result.addSeparator();
        result.add(getCutAction());
        result.add(getCopyAction());
        result.add(getPasteAction());
        result.add(getDeleteAction());
        result.addSeparator();
        result.add(getSelectModeAction());
        result.add(getNodeModeAction());
        result.add(getEdgeModeAction());
        this.jgraph.fillOutEditMenu(result.getPopupMenu(), true);
        return result;
    }

    /**
     * Creates and returns an options menu for the menu bar.
     */
    JMenu createOptionsMenu() {
        JMenu result = new JMenu(Options.OPTIONS_MENU_NAME);
        result.setMnemonic(Options.OPTIONS_MENU_MNEMONIC);
        result.add(getOptions().getItem(Options.PREVIEW_ON_SAVE_OPTION));
        result.add(getOptions().getItem(Options.SHOW_VALUE_NODES_OPTION));
        result.add(getOptions().getItem(Options.SHOW_NODE_IDS_OPTION));
        return result;
    }

    /**
     * Creates and returns a properties menu for the menu bar.
     */
    JMenu createPropertiesMenu() {
        JMenu result = new JMenu(Options.PROPERTIES_MENU_NAME);
        result.setMnemonic(Options.PROPERTIES_MENU_MNEMONIC);
        result.addSeparator();
        result.add(getEditPropertiesAction());
        return result;
    }

    /**
     * Creates and returns a display menu for the menu bar.
     */
    JMenu createDisplayMenu() {
        JMenu result = new JMenu(Options.DISPLAY_MENU_NAME);
        result.setMnemonic(Options.DISPLAY_MENU_MNEMONIC);
        this.jgraph.fillOutDisplayMenu(result.getPopupMenu());
        result.addSeparator();
        result.add(getGraphPanel().getViewLabelListItem());
        return result;
    }

    /**
     * Creates and returns a help menu for the menu bar.
     */
    JMenu createHelpMenu() {
        JMenu result = new JMenu(HELP_MENU_NAME);
        result.setMnemonic(Options.HELP_MENU_MNEMONIC);
        result.add(new JMenuItem(new AboutAction()));
        return result;
    }

    /**
     * Creates and returns the tool bar.
     */
    JToolBar createToolBar() {
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        addFileButtons(toolbar);
        addTypeButtons(toolbar);
        addModeButtons(toolbar);
        addUndoButtons(toolbar);
        addCopyPasteButtons(toolbar);
        return toolbar;
    }

    /**
     * Adds file buttons to a given toolbar.
     * @param toolbar the toolbar to be extended
     */
    void addFileButtons(JToolBar toolbar) {
        toolbar.add(getNewAction());
        toolbar.add(getOpenGraphAction());
        toolbar.add(getSaveGraphAction());
    }

    /**
     * Adds a separator and graph type buttons to a given toolbar.
     * @param toolbar the toolbar to be extended
     */
    void addTypeButtons(JToolBar toolbar) {
        // Type mode block
        toolbar.addSeparator();
        toolbar.add(getGraphRoleButton());
        toolbar.add(getRuleRoleButton());
        toolbar.add(getTypeRoleButton());
        getTypeButtonGroup();

    }

    /**
     * Adds a separator and editing mode buttons to a given toolbar.
     * @param toolbar the toolbar to be extended
     */
    void addModeButtons(JToolBar toolbar) {
        // Mode block
        toolbar.addSeparator();
        toolbar.add(getSelectModeButton());
        toolbar.add(getNodeModeButton());
        toolbar.add(getEdgeModeButton());
    }

    /**
     * Adds a separator and undo/redo-buttons to a given toolbar.
     * @param toolbar the toolbar to be extended
     */
    void addUndoButtons(JToolBar toolbar) {
        // Undo Block
        toolbar.addSeparator();
        toolbar.add(getUndoAction());
        toolbar.add(getRedoAction());
    }

    /**
     * Adds a separator and copy/paste-buttons to a given toolbar.
     * @param toolbar the toolbar to be extended
     */
    void addCopyPasteButtons(JToolBar toolbar) {
        // Edit Block
        toolbar.addSeparator();
        toolbar.add(getCopyAction());
        toolbar.add(getPasteAction());
        toolbar.add(getCutAction());
        toolbar.add(getDeleteAction());
    }

    /**
     * Returns the group of editing mode buttons, lazily creating it first.
     */
    private ButtonGroup getModeButtonGroup() {
        if (this.modeButtonGroup == null) {
            this.modeButtonGroup = new ButtonGroup();
            this.modeButtonGroup.add(getSelectModeButton());
            this.modeButtonGroup.add(getNodeModeButton());
            this.modeButtonGroup.add(getEdgeModeButton());
        }
        return this.modeButtonGroup;
    }

    /**
     * Returns the group of editing mode buttons, lazily creating it first.
     */
    private ButtonGroup getTypeButtonGroup() {
        if (this.typeButtonGroup == null) {
            this.typeButtonGroup = new ButtonGroup();
            this.typeButtonGroup.add(getGraphRoleButton());
            this.typeButtonGroup.add(getRuleRoleButton());
            this.typeButtonGroup.add(getTypeRoleButton());
        }
        return this.typeButtonGroup;
    }

    /**
     * Returns the button for setting edge editing mode, lazily creating it
     * first.
     */
    private JToggleButton getEdgeModeButton() {
        if (this.edgeModeButton == null) {
            this.edgeModeButton = new JToggleButton(getEdgeModeAction());
            this.edgeModeButton.setText(null);
            this.edgeModeButton.setToolTipText(Options.EDGE_MODE_NAME);
        }
        return this.edgeModeButton;
    }

    /**
     * Returns the button for setting node editing mode, lazily creating it
     * first.
     */
    private JToggleButton getNodeModeButton() {
        if (this.nodeModeButton == null) {
            this.nodeModeButton = new JToggleButton(getNodeModeAction());
            this.nodeModeButton.setText(null);
            this.nodeModeButton.setToolTipText(Options.NODE_MODE_NAME);
        }
        return this.nodeModeButton;
    }

    /**
     * Returns the button for setting selection mode, lazily creating it first.
     */
    private JToggleButton getSelectModeButton() {
        if (this.selectModeButton == null) {
            this.selectModeButton = new JToggleButton(getSelectModeAction());
            this.selectModeButton.setText(null);
            this.selectModeButton.setToolTipText(Options.SELECT_MODE_NAME);
            this.selectModeButton.doClick();
        }
        return this.selectModeButton;
    }

    /** Creates a panel consisting of the error panel and the status bar. */
    JPanel getStatusPanel() {
        if (this.statusPanel == null) {
            this.statusPanel = new JPanel(new BorderLayout());
            this.statusPanel.add(getErrorPanel());
            this.statusPanel.add(getStatusBar(), BorderLayout.SOUTH);
        }
        return this.statusPanel;
    }

    /** Lazily creates and returns the error panel. */
    private ErrorListPanel getErrorPanel() {
        if (this.errorPanel == null) {
            final ErrorListPanel result =
                this.errorPanel = new ErrorListPanel("Format errors in graph");
            result.addSelectionListener(new Observer() {
                @Override
                public void update(Observable o, Object arg) {
                    if (arg != null) {
                        JCell errorCell = Editor.this.errorCellMap.get(arg);
                        if (errorCell != null) {
                            getJGraph().setSelectionCell(errorCell);
                        }
                    }
                }
            });
        }
        return this.errorPanel;
    }

    /** Lazily creates and returns the error panel. */
    private JLabel getStatusBar() {
        return this.statusBar;
    }

    /**
     * Callback factory method for a properties dialog for the currently edited
     * model.
     */
    PropertiesDialog createPropertiesDialog(boolean editable) {
        Map<String,Property<String>> keys =
            new LinkedHashMap<String,Property<String>>(
                GraphProperties.DEFAULT_USER_KEYS);
        if (getRoleIndex() != RULE_INDEX) {
            keys.remove(GraphProperties.CONFLUENT_KEY);
            keys.remove(GraphProperties.PRIORITY_KEY);
            keys.remove(GraphProperties.ENABLED_KEY);
            keys.remove(GraphProperties.TRANSITION_LABEL_KEY);
        }
        return new PropertiesDialog(getModel().getProperties(), keys, editable);
    }

    /**
     * Updates the Undo/Redo Button State based on Undo Manager. Also sets
     * {@link #isCurrentGraphModified()} if no more undos are available.
     */
    protected void updateHistoryButtons() {
        // The View Argument Defines the Context
        getUndoAction().setEnabled(getUndoManager().canUndo());
        getRedoAction().setEnabled(getUndoManager().canRedo());
        setCurrentGraphModified(getUndoManager().canUndo());
    }

    /**
     * Activates the appropriate mode button (select, node or edge), based on a
     * given (mode) action.
     * @param forAction the mode action for which the corresponding button is to
     *        be activated
     */
    protected void updateModeButtons(Action forAction) {
        Enumeration<AbstractButton> modeButtonEnum =
            getModeButtonGroup().getElements();
        while (modeButtonEnum.hasMoreElements()) {
            JToggleButton button = (JToggleButton) modeButtonEnum.nextElement();
            if (button.getAction() == forAction) {
                button.setSelected(true);
            }
        }
    }

    /**
     * Activates the appropriate mode button (select, node or edge), based on a
     * given (mode) action.
     * @param forAction the mode action for which the corresponding button is to
     *        be activated
     */
    protected void updateTypeButtons(Action forAction) {
        Enumeration<AbstractButton> modeButtonEnum =
            getTypeButtonGroup().getElements();
        while (modeButtonEnum.hasMoreElements()) {
            JToggleButton button = (JToggleButton) modeButtonEnum.nextElement();
            if (button.getAction() == forAction) {
                button.setSelected(true);
            }
        }
    }

    /**
     * Updates the status bar and the error panel 
     * with information about the currently edited graph.
     */
    protected void updateStatus() {
        setAspectGraph();
        int elementCount =
            getModel().getRootCount() - getModel().getGrayedOut().size();
        getStatusBar().setText("" + elementCount + " visible elements");
        List<FormatError> errors = new ArrayList<FormatError>();
        if (hasErrors()) {
            errors.addAll(getErrors());
        }
        errors.addAll(toView().getErrors());
        this.errorCells.clear();
        this.errorCellMap.clear();
        for (FormatError error : errors) {
            if (error.getObject() != null) {
                JCell errorCell = this.graphToModelMap.get(error.getObject());
                if (errorCell != null) {
                    this.errorCells.add(errorCell);
                    this.errorCellMap.put(error, errorCell);
                }
            }
        }
        getErrorPanel().setErrors(errors);
        this.jgraph.refresh();
    }

    /** Sets the property whether all inserted cells are automatically selected. */
    protected void setSelectInsertedCells(boolean select) {
        this.jgraph.getGraphLayoutCache().setSelectsAllInsertedCells(select);
    }

    /**
     * Returns the current property whether all inserted cells are automatically
     * selected.
     */
    protected boolean isSelectInsertedCells() {
        return this.jgraph.getGraphLayoutCache().isSelectsAllInsertedCells();
    }

    /** Undoes the last registered change to the Model or the View. */
    protected void undoLastEdit() {
        try {
            setSelectInsertedCells(false);
            getUndoManager().undo(this.jgraph.getGraphLayoutCache());
            setSelectInsertedCells(true);
        } catch (Exception ex) {
            System.err.println(ex);
        } finally {
            updateHistoryButtons();
        }
    }

    /** Redoes the latest undone change to the Model or the View. */
    protected void redoLastEdit() {
        try {
            setSelectInsertedCells(false);
            getUndoManager().redo(this.jgraph.getGraphLayoutCache());
            setSelectInsertedCells(true);
        } catch (Exception ex) {
            System.err.println(ex);
        } finally {
            updateHistoryButtons();
        }
    }

    /**
     * Creates and displays an {@link ErrorDialog} with a given message and
     * exception.
     */
    void showErrorDialog(String message, Exception exc) {
        new ErrorDialog(getGraphPanel(), message, exc).setVisible(true);
    }

    /**
     * Creates and shows a confirmation dialog for abandoning the currently
     * edited graph.
     */
    boolean confirmAbandon() {
        if (isCurrentGraphModified()) {
            int res =
                JOptionPane.showConfirmDialog(getGraphPanel(),
                    "Save changes in current graph?", null,
                    JOptionPane.YES_NO_CANCEL_OPTION);
            if (res == JOptionPane.YES_OPTION) {
                // save-as property set to true, for backward compatibility
                // reasons
                File toFile = handleSaveGraph(true);
                return toFile != null;
            } else {
                return res == JOptionPane.NO_OPTION;
            }
        } else {
            return true;
        }
    }

    private String showTypeGraphSelectionDialog() {
        List<String> typeNames =
            new ArrayList<String>(
                this.getTypeViewList().getTypeViewMap().keySet());
        if (typeNames.size() == 1) {
            return typeNames.iterator().next();
        } else {
            SingleListDialog dialog =
                new SingleListDialog(this.getFrame(), "Type graph selection",
                    "Select the type graph to display:", typeNames, false);
            return dialog.getSelectedItem();
        }
    }

    /**
     * Creates a preview of an aspect model, with properties. Returns a j-model
     * if the edited model should be replaced, <code>null</code> otherwise.
     */
    private AspectJModel showPreviewDialog(View<?> view, String okOption) {
        if (this.previewSize == null) {
            this.previewSize = DEFAULT_PREVIEW_SIZE;
        }
        boolean partial = view.getView().hasErrors();
        AspectJModel previewModel =
            AspectJModel.newInstance(view, getOptions());
        JGraph jGraph = createJGraph(previewModel);
        jGraph.setToolTipEnabled(true);
        JScrollPane jGraphPane = new JScrollPane(jGraph);
        jGraphPane.setBorder(new BevelBorder(BevelBorder.LOWERED));
        JComponent previewContent = new JPanel(false);
        previewContent.setLayout(new BorderLayout());
        previewContent.add(jGraphPane);
        // if (!previewModel.getProperties().isEmpty()) {
        // getModel().setProperties(new
        // GraphProperties(dialog.getEditedProperties()));
        PropertiesDialog propertiesDialog = createPropertiesDialog(true);
        previewContent.add(propertiesDialog.createTablePane(),
            BorderLayout.NORTH);
        // }
        if (partial) {
            JLabel errorLabel =
                new JLabel(String.format(
                    "Incomplete preview due to syntax errors in edited %s",
                    getRole(false)));
            errorLabel.setForeground(Color.RED);
            previewContent.add(errorLabel, BorderLayout.SOUTH);
            if (okOption == null) {
                okOption = Options.USE_BUTTON;
            }
        } else if (okOption == null) {
            okOption = Options.OK_BUTTON;
        }
        JOptionPane previewPane =
            new JOptionPane(previewContent, JOptionPane.PLAIN_MESSAGE);
        previewPane.setOptions(new Object[] {
            createOkButtonOnPreviewDialog(okOption, previewPane,
                propertiesDialog),
            createCancelButtonOnPreviewDialog(Options.CANCEL_BUTTON,
                previewPane, propertiesDialog)});
        JDialog dialog =
            previewPane.createDialog(getFrame(), String.format("%s preview",
                getRole(true)));
        dialog.setSize(this.previewSize);
        dialog.setResizable(true);
        dialog.setVisible(true);
        // put the edited properties into the model
        previewModel.setProperties(new GraphProperties(
            propertiesDialog.getEditedProperties()));
        Object response = previewPane.getValue();
        this.previewSize = dialog.getSize();
        if (response instanceof JButton
            && okOption.equals(((JButton) response).getText())) {
            return previewModel;
        } else {
            return null;
        }
    }

    /*
     * Specialized listeners for the buttons on the showPreviewDialog. Same
     * functionality as in PropertiesDialog.
     */
    private class CloseListener implements ActionListener {
        JOptionPane previewPane;
        PropertiesDialog propertiesDialog;

        public CloseListener(JOptionPane previewPane,
                PropertiesDialog propertiesDialog) {
            this.previewPane = previewPane;
            this.propertiesDialog = propertiesDialog;
        }

        public void actionPerformed(ActionEvent e) {
            this.previewPane.setValue(e.getSource());
            this.previewPane.setVisible(false);
        }
    }

    /**
     * Specialised OK button for the showPreviewDialog. Same functionality as in
     * PropertiesDialog. Signals the editors to stop editing, which ensures that
     * partially edited results are not lost.
     */
    private JButton createOkButtonOnPreviewDialog(String message,
            JOptionPane previewPane, PropertiesDialog propertiesDialog) {
        JButton theButton = new JButton(message);
        theButton.addActionListener(new CloseListener(previewPane,
            propertiesDialog) {
            @Override
            public void actionPerformed(ActionEvent e) {
                TableCellEditor editor =
                    this.propertiesDialog.getInnerTable().getCellEditor();
                if (editor == null || editor.stopCellEditing()) {
                    super.actionPerformed(e);
                }
            }
        });
        return theButton;
    }

    /*
     * Specialized cancel button for the showPreviewDialog. Same functionality
     * as in PropertiesDialog.
     */
    private JButton createCancelButtonOnPreviewDialog(String message,
            JOptionPane previewPane, PropertiesDialog propertiesDialog) {
        JButton theButton = new JButton(message);
        theButton.addActionListener(new CloseListener(previewPane,
            propertiesDialog));
        return theButton;
    }

    /**
     * Returns the options object associated with the simulator.
     */
    public final Options getOptions() {
        // lazily creates the options
        if (this.options == null) {
            this.options = new Options();
            // options.getItem(Options.SHOW_BACKGROUND_OPTION).setSelected(true);
            // options.getItem(Options.SHOW_REMARKS_OPTION).setSelected(true);
            // options.getItem(Options.PREVIEW_ON_SAVE_OPTION).setSelected(true);
        }
        return this.options;
    }

    /** Returns the jgraph component of this editor. */
    EditorJGraph getJGraph() {
        return this.jgraph;
    }

    JGraph getAspectJGraph() {
        AspectJModel model = AspectJModel.newInstance(toView(), getOptions());
        JGraph jGraph = createJGraph(model);
        jGraph.setModel(model);
        // Ugly hack to prevent clipping of the image. We set the jGraph size
        // to twice its normal size. This does not affect the final size of
        // the exported figure, hence it can be considered harmless... ;P
        Dimension oldPrefSize = jGraph.getPreferredSize();
        Dimension newPrefSize =
            new Dimension(oldPrefSize.width * 2, oldPrefSize.height * 2);
        jGraph.setSize(newPrefSize);
        return jGraph;
    }

    /**
     * Factory method to create a {@link JGraph} displaying a given
     * {@link JModel}.
     */
    private JGraph createJGraph(AspectJModel jmodel) {
        JGraph result = new JGraph(jmodel, false, null);
        result.setExporter(getExporter());
        return result;
    }

    /**
     * Sets the current file to a given value.
     */
    final void setCurrentFile(File file) {
        this.currentFile = file;
    }

    /**
     * @return Returns the currentFile.
     */
    final File getCurrentFile() {
        return this.currentFile;
    }

    /**
     * The options object of this simulator.
     */
    private Options options;

    /** The frame of the editor. */
    private final JFrame frame;

    /** The jgraph instance used in this editor. */
    private final EditorJGraph jgraph;

    /** The jgraph panel used in this editor. */
    private JGraphPanel<EditorJGraph> jGraphPanel;

    /** Status bar of the editor. */
    private final JLabel statusBar = new JLabel();

    /** Panel containing the error panel and status par. */
    private JPanel statusPanel;
    /** Panel displaying format error messages. */
    private ErrorListPanel errorPanel;
    /** The size of the (previous) preview dialog. */
    private Dimension previewSize;

    /** Indicates whether jgraph has been modified since the last save. */
    private boolean currentGraphModified;

    /** Indicates whether jgraph has been modified since the last save. */
    private boolean anyGraphSaved;

    /** The graph generated from the current editor content. */
    private AspectGraph graph;
    /** Mapping from elements in {@link #getAspectGraph()} to elements in {@link #getModel()}. */
    private Map<Element,JCell> graphToModelMap;
    /** Set of erroneous cells in the current editor model. */
    private Set<JCell> errorCells = new HashSet<JCell>();
    /** Mapping from error messages to the corresponding cells. */
    private Map<FormatError,JCell> errorCellMap =
        new HashMap<FormatError,JCell>();
    /** Index of the currently set editor role */
    private int roleIndex = -1;
    /** Type view against which the edited graph is checked. */
    private TypeViewList typeViewList;
    /** Type against which the edited graph is checked. */
    private TypeGraph type;
    /**
     * Collection of errors in the currently loaded graph; <code>null</code> if
     * there are none.
     */
    private Collection<FormatError> errors;

    /** The undo manager of the editor. */
    private transient GraphUndoManager undoManager;

    /** Object providing the core functionality for property changes. */
    private PropertyChangeSupport propertyChangeSupport;

    /** Currently edited file. */
    private File currentFile;

    /**
     * The GXL converter used for marshalling and unmarshalling layouted graphs.
     */
    private final AspectGxl layoutGxl = new AspectGxl(new LayedOutXml());

    /**
     * File chooser for graph opening.
     */
    private MyFileChooser graphChooser;

    /** Button for setting edge editing mode. */
    private transient JToggleButton edgeModeButton;
    /** Button for setting node editing mode. */
    private transient JToggleButton nodeModeButton;
    /** Button for setting selection mode. */
    private transient JToggleButton selectModeButton;

    /**
     * Returns the button for setting node editing mode, lazily creating it
     * first.
     */
    JToggleButton getGraphRoleButton() {
        if (this.graphRoleButton == null) {
            final JToggleButton result =
                this.graphRoleButton =
                    new JToggleButton(getSetGraphRoleAction());
            result.setText(null);
            result.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    result.setToolTipText(result.isSelected()
                            ? Options.PREVIEW_GRAPH_ACTION_NAME
                            : Options.SET_GRAPH_ROLE_ACTION_NAME);
                }
            });
            result.doClick();
        }
        return this.graphRoleButton;
    }

    /** Button for setting graph editing mode. */
    private transient JToggleButton graphRoleButton;

    /**
     * Returns the button for setting selection mode, lazily creating it first.
     */
    JToggleButton getRuleRoleButton() {
        if (this.ruleRoleButton == null) {
            final JToggleButton result =
                this.ruleRoleButton = new JToggleButton(getSetRuleRoleAction());
            result.setText(null);
            result.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    result.setToolTipText(result.isSelected()
                            ? Options.PREVIEW_RULE_ACTION_NAME
                            : Options.SET_RULE_ROLE_ACTION_NAME);
                }
            });
        }
        return this.ruleRoleButton;
    }

    /** Button for setting rule editing mode. */
    private transient JToggleButton ruleRoleButton;

    /**
     * Returns the button for setting selection mode, lazily creating it first.
     */
    JToggleButton getTypeRoleButton() {
        if (this.typeRoleButton == null) {
            final JToggleButton result =
                this.typeRoleButton = new JToggleButton(getSetTypeRoleAction());
            result.setText(null);
            result.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    result.setToolTipText(result.isSelected()
                            ? Options.PREVIEW_TYPE_ACTION_NAME
                            : Options.SET_TYPE_ROLE_ACTION_NAME);
                }
            });
        }
        return this.typeRoleButton;
    }

    /** Button for setting type editing mode. */
    private transient JToggleButton typeRoleButton;
    /** Collection of editing mode buttons. */
    private ButtonGroup modeButtonGroup;
    /** Collection of graph editing type buttons. */
    private ButtonGroup typeButtonGroup;

    /** Returns the exporter of this editor. */
    public final Exporter getExporter() {
        return this.exporter;
    }

    /** Exporter used for all {@link JGraph}s in the editor. */
    private final Exporter exporter = new Exporter();

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
            this.cutAction.putValue(Action.SMALL_ICON, Groove.CUT_ICON);
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
            this.copyAction.putValue(Action.SMALL_ICON, Groove.COPY_ICON);
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
            this.pasteAction.putValue(Action.SMALL_ICON, Groove.PASTE_ICON);
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
            ImageIcon redoIcon = new ImageIcon(Groove.getResource("redo.gif"));
            this.redoAction =
                new ToolbarAction(Options.REDO_ACTION_NAME, Options.REDO_KEY,
                    redoIcon) {
                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        super.actionPerformed(evt);
                        redoLastEdit();
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
            ImageIcon undoIcon = new ImageIcon(Groove.getResource("undo.gif"));
            this.undoAction =
                new ToolbarAction(Options.UNDO_ACTION_NAME, Options.UNDO_KEY,
                    undoIcon) {
                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        super.actionPerformed(evt);
                        undoLastEdit();
                    }
                };
            this.undoAction.setEnabled(false);
        }
        return this.undoAction;
    }

    /** Action to undo the last edit. */
    private Action undoAction;

    /**
     * General class for actions with toolbar buttons. Takes care of image, name
     * and key accelleration; moreover, the
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
            getGraphPanel().getInputMap(
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
                acceleratorKey, name);
            getJGraph().getInputMap().put(acceleratorKey, name);
            getGraphPanel().getActionMap().put(name, this);
        }

        public void actionPerformed(ActionEvent evt) {
            getJGraph().stopEditing();
        }
    }

    /**
     * Action for displaying an about box.
     */
    private class AboutAction extends AbstractAction {
        /** Constructs an instance of the action. */
        protected AboutAction() {
            super(Options.ABOUT_ACTION_NAME);
        }

        public void actionPerformed(ActionEvent evt) {
            new AboutBox(getFrame());
        }
    }

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
                Groove.DELETE_ICON);
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

    /**
     * Lazily creates and returns the action to edit the graph properties.
     */
    private Action getEditPropertiesAction() {
        if (this.editPropertiesAction == null) {
            this.editPropertiesAction = new EditPropertiesAction();
        }
        return this.editPropertiesAction;
    }

    /** Action to edit the graph properties. */
    private Action editPropertiesAction;

    private class EditPropertiesAction extends AbstractAction {
        /** Constructs an instance of the action. */
        public EditPropertiesAction() {
            super(Options.EDIT_ACTION_NAME);
        }

        /**
         * Displays a {@link PropertiesDialog} for the properties of the edited
         * graph.
         */
        public void actionPerformed(ActionEvent e) {
            PropertiesDialog dialog = createPropertiesDialog(true);
            if (dialog.showDialog(getFrame())) {
                getModel().setProperties(
                    new GraphProperties(dialog.getEditedProperties()));
                setCurrentGraphModified(true);
                updateTitle();
            }
        }
    }

    /**
     * Lazily creates and returns the action to start editing a fresh graph.
     */
    private Action getNewAction() {
        if (this.newAction == null) {
            this.newAction = new NewGraphAction();
        }
        return this.newAction;
    }

    /** Action to start an empty graph for editing. */
    private Action newAction;

    /**
     * Action to start with a blank graph.
     */
    private class NewGraphAction extends ToolbarAction {
        /** Constructs an instance of the action. */
        NewGraphAction() {
            super(Options.NEW_ACTION_NAME, Options.NEW_KEY, Groove.NEW_ICON);
            putValue(MNEMONIC_KEY, Options.NEW_MNEMONIC);
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            super.actionPerformed(evt);
            if (confirmAbandon()) {
                setCurrentFile(null);
                setPlainGraph(null);
            }
        }
    }

    /**
     * Lazily creates and returns the action to open a new graph.
     */
    private Action getOpenGraphAction() {
        if (this.openAction == null) {
            this.openAction = new OpenGraphAction();
        }
        return this.openAction;
    }

    /** Action to open a new graph for editing. */
    private Action openAction;

    /**
     * Action to open a graph file into the editor.
     */
    private class OpenGraphAction extends ToolbarAction {
        /** Constructs an instance of the action. */
        protected OpenGraphAction() {
            super(Options.OPEN_ACTION_NAME, Options.OPEN_KEY, new ImageIcon(
                Groove.getResource("open.gif")));
            putValue(MNEMONIC_KEY, Options.OPEN_MNEMONIC);
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            super.actionPerformed(evt);
            handleOpenGraph();
        }
    }

    /**
     * Lazily creates and returns the action to quit the editor.
     */
    private Action getQuitAction() {
        if (this.quitAction == null) {
            this.quitAction = new QuitAction();
        }
        return this.quitAction;
    }

    // /** Action to close the editor. Only if the editor is auxiliary. */
    // private Action closeAction;
    /** Action to quit the editor. */
    private Action quitAction;

    /**
     * Action for quitting the editor. Calls {@link Editor#handleQuit()} to
     * execute the action.
     */
    private class QuitAction extends AbstractAction {
        /** Constructs an instance of the action. */
        public QuitAction() {
            super(Options.QUIT_ACTION_NAME);
            putValue(ACCELERATOR_KEY, Options.QUIT_KEY);
            putValue(MNEMONIC_KEY, Options.QUIT_MNEMONIC);
        }

        /**
         * Calls {@link Editor#handleQuit()}.
         */
        public void actionPerformed(ActionEvent e) {
            handleQuit();
        }
    }

    /** Action to save the current graph. */
    private Action saveAction;

    /**
     * Lazily creates and returns the action to save the current graph.
     */
    private Action getSaveGraphAction() {
        if (this.saveAction == null) {
            this.saveAction = new SaveGraphAction();
        }
        return this.saveAction;
    }

    /**
     * Action to save the current state of the editor into a file.
     */
    private class SaveGraphAction extends ToolbarAction {
        /** Constructs an instance of the action. */
        protected SaveGraphAction() {
            super(Options.SAVE_ACTION_NAME, Options.SAVE_KEY, Groove.SAVE_ICON);
            putValue(MNEMONIC_KEY, Options.SAVE_MNEMONIC);
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            super.actionPerformed(evt);
            // save-as property set to true, for backward compatibility reasons
            handleSaveGraph(true);
        }
    }

    /**
     * Lazily creates and returns the action to save the current graph under a
     * different name.
     */
    @SuppressWarnings("all")
    private Action getSaveGraphAsAction() {
        if (this.saveAsAction == null) {
            this.saveAsAction = new SaveGraphAsAction();
        }
        return this.saveAsAction;
    }

    /** Action to save the current graph in a new file. */
    private Action saveAsAction;

    /**
     * Action to save the current state of the editor into a new file.
     */
    private class SaveGraphAsAction extends AbstractAction {
        /** Constructs an instance of the action. */
        protected SaveGraphAsAction() {
            super(Options.SAVE_AS_ACTION_NAME);
        }

        public void actionPerformed(ActionEvent evt) {
            handleSaveGraph(true);
        }
    }

    /**
     * Lazily creates and returns the action to set the editor to edge editing
     * mode.
     */
    private Action getEdgeModeAction() {
        if (this.edgeModeAction == null) {
            ImageIcon edgeIcon = new ImageIcon(Groove.getResource("edge.gif"));
            this.edgeModeAction =
                new SetEditingModeAction(Options.EDGE_MODE_NAME,
                    Options.EDGE_MODE_KEY, edgeIcon);
        }
        return this.edgeModeAction;
    }

    /** Action to set the editor to edge editing mode. */
    private Action edgeModeAction;

    /**
     * Lazily creates and returns the action to set the editor to node editing
     * mode.
     */
    private Action getNodeModeAction() {
        if (this.nodeModeAction == null) {
            ImageIcon nodeIcon =
                new ImageIcon(Groove.getResource("rectangle.gif"));
            this.nodeModeAction =
                new SetEditingModeAction(Options.NODE_MODE_NAME,
                    Options.NODE_MODE_KEY, nodeIcon);
        }
        return this.nodeModeAction;
    }

    /** Action to set the editor to node editing mode. */
    private Action nodeModeAction;

    /**
     * Lazily creates and returns the action to set the editor to selection
     * mode.
     */
    private Action getSelectModeAction() {
        if (this.selectModeAction == null) {
            ImageIcon selectIcon =
                new ImageIcon(Groove.getResource("select.gif"));
            this.selectModeAction =
                new SetEditingModeAction(Options.SELECT_MODE_NAME,
                    Options.SELECT_MODE_KEY, selectIcon);
        }
        return this.selectModeAction;
    }

    /** Action to set the editor to selection mode. */
    private Action selectModeAction;

    /**
     * Action to set the editing mode (selection, node or edge).
     */
    private class SetEditingModeAction extends ToolbarAction {
        /** Constructs an action with a given name, key and icon. */
        SetEditingModeAction(String text, KeyStroke acceleratorKey,
                ImageIcon smallIcon) {
            super(text, acceleratorKey, smallIcon);
            putValue(SHORT_DESCRIPTION, null);
        }

        /**
         * (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(ActionEvent evt) {
            super.actionPerformed(evt);
            updateModeButtons(this);
        }
    }

    /** Returns the rule preview action, lazily creating it first. */
    private Action getSetGraphRoleAction() {
        if (this.setGraphRoleAction == null) {
            this.setGraphRoleAction = new SetGraphRoleAction();
        }
        return this.setGraphRoleAction;
    }

    /** Action to switch to graph editing. */
    private Action setGraphRoleAction;

    /**
     * Action to preview the current jgraph as a transformation rule.
     */
    private class SetGraphRoleAction extends ToolbarAction {
        /** Constructs an instance of the action. */
        protected SetGraphRoleAction() {
            super(Options.SET_GRAPH_ROLE_ACTION_NAME, null,
                Groove.GRAPH_MODE_ICON);
        }

        /**
         * (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(ActionEvent evt) {
            super.actionPerformed(evt);
            if (!setRole(GRAPH_INDEX)) {
                // only do a preview if the type was not changed (on the second
                // click)
                handlePreview(null);
            }
        }
    }

    /** Returns the rule preview action, lazily creating it first. */
    private Action getSetRuleRoleAction() {
        if (this.setRuleRoleAction == null) {
            this.setRuleRoleAction = new SetRuleRoleAction();
        }
        return this.setRuleRoleAction;
    }

    /** Action to create a rule preview dialog. */
    private Action setRuleRoleAction;

    /**
     * Action to preview the current jgraph as a transformation rule.
     */
    private class SetRuleRoleAction extends ToolbarAction {
        /** Constructs an instance of the action. */
        protected SetRuleRoleAction() {
            super(Options.SET_RULE_ROLE_ACTION_NAME, null,
                Groove.RULE_MODE_ICON);
        }

        /**
         * (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(ActionEvent evt) {
            super.actionPerformed(evt);
            if (!setRole(RULE_INDEX)) {
                // only do a preview if the type was not changed (on the second
                // click)
                handlePreview(null);
            }
        }
    }

    /** Returns the type role action, lazily creating it first. */
    private Action getSetTypeRoleAction() {
        if (this.setTypeRoleAction == null) {
            this.setTypeRoleAction = new SetTypeRoleAction();
        }
        return this.setTypeRoleAction;
    }

    /** Action to set the edited graph to the type role. */
    private Action setTypeRoleAction;

    /**
     * Action to preview the current jgraph as a graph type.
     */
    private class SetTypeRoleAction extends ToolbarAction {
        /** Constructs an instance of the action. */
        protected SetTypeRoleAction() {
            super(Options.SET_TYPE_ROLE_ACTION_NAME, null,
                Groove.TYPE_MODE_ICON);
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            if (getType() != null) {
                String typeName = showTypeGraphSelectionDialog();
                showPreviewDialog(
                    getTypeViewList().getTypeViewByName(typeName),
                    Options.OK_BUTTON);
            } else {
                super.actionPerformed(evt);
                if (!setRole(TYPE_INDEX)) {
                    // only do a preview if the type was not changed (on the
                    // second click)
                    handlePreview(null);
                }
            }
        }
    }

    /** Returns the type preview action, lazily creating it first. */
    Action getPreviewTypeAction() {
        if (this.setPreviewTypeAction == null) {
            this.setPreviewTypeAction = new PreviewTypeAction();
        }
        return this.setPreviewTypeAction;
    }

    /** Action to create a type preview dialog. */
    private Action setPreviewTypeAction;

    /**
     * Action to preview the current type graph.
     */
    private class PreviewTypeAction extends ToolbarAction {
        /** Constructs an instance of the action. */
        protected PreviewTypeAction() {
            super(Options.PREVIEW_TYPE_ACTION_NAME, null, Groove.TYPE_MODE_ICON);
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            String typeName = showTypeGraphSelectionDialog();
            showPreviewDialog(getTypeViewList().getTypeViewByName(typeName),
                Options.OK_BUTTON);
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

        /** Redirects the Actionevent. */
        @Override
        public void actionPerformed(ActionEvent evt) {
            super.actionPerformed(evt);
            evt =
                new ActionEvent(getJGraph(), evt.getID(),
                    evt.getActionCommand(), evt.getModifiers());
            this.action.actionPerformed(evt);
            if (this == getCutAction() || this == getCopyAction()) {
                getPasteAction().setEnabled(true);
            }
        }

        /** The action that this transfer action wraps. */
        protected Action action;
    }

    /**
     * File chooser taking the distinction between graphs and rules into
     * account.
     */
    private class MyFileChooser extends GrooveFileChooser {
        /** Empty constructor with the right visibility. */
        MyFileChooser() {
            // empty
        }

        @Override
        public int showOpenDialog(Component parent) throws HeadlessException {
            resetChoosableFileFilters();
            setAcceptAllFileFilterUsed(true);
            addChoosableFileFilter(getGraphFilter());
            if (getCurrentFile() != null) {
                setCurrentDirectory(getCurrentFile().getParentFile());
                rescanCurrentDirectory();
            }
            setSelectedFile(new File(""));
            int result = super.showOpenDialog(parent);
            return result;
        }

        @Override
        public int showSaveDialog(Component parent) throws HeadlessException {
            int result;
            resetChoosableFileFilters();
            setAcceptAllFileFilterUsed(false);
            setFilters(getRoleIndex());
            if (getCurrentFile() != null) {
                setCurrentDirectory(getCurrentFile().getParentFile());
                rescanCurrentDirectory();
                setSelectedFile(getCurrentFile());
            }
            // get the file to write to
            result = super.showSaveDialog(parent);
            this.lastSaveFilter = getFileFilter();
            return result;
        }

        /**
         * Sets the file filter to one that accepts the given role.
         */
        private void setFilters(int roleIndex) {
            resetChoosableFileFilters();
            for (FileFilter filter : this.filters) {
                addChoosableFileFilter(filter);
            }
            FileFilter defaultFilter = null;
            if (getRoleIndex() == GRAPH_INDEX && this.lastSaveFilter != null
                && getFilterRole(this.lastSaveFilter) == GRAPH_INDEX) {
                defaultFilter = this.lastSaveFilter;
            } else {
                defaultFilter = this.filters[roleIndex];
            }
            setFileFilter(defaultFilter);
        }

        /** Last file filter used in a save dialog. */
        private FileFilter lastSaveFilter;

        /**
         * Returns the private int getFilterRole(ExtensionFilter filter) { int
         * result = 0; for (ExtensionFilter search: filters) { if (search ==
         * filter) { break; } result++; } return result; }
         * 
         * /** Lazily creates and returns the GXL filter.
         */
        private ExtensionFilter getGxlFilter() {
            if (this.gxlFilter == null) {
                this.gxlFilter = Groove.createGxlFilter();
            }
            return this.gxlFilter;
        }

        /**
         * Extension filter used for the gxl format.
         */
        private ExtensionFilter gxlFilter;

        /** Lazily creates and returns the graph filter. */
        private ExtensionFilter getGraphFilter() {
            if (this.graphFilter == null) {
                this.graphFilter = new ExtensionFilter("Graph files", "") {
                    @Override
                    public boolean accept(File file) {
                        boolean result =
                            isAcceptDirectories() && file.isDirectory();
                        if (!result) {
                            for (ExtensionFilter filter : MyFileChooser.this.filters) {
                                if (filter.acceptExtension(file)) {
                                    result = true;
                                    break;
                                }
                            }
                        }
                        return result;
                    }

                    @Override
                    public String getDescription() {
                        return String.format(
                            "Graph files (*%s, *%s, *%s, *%s)",
                            Groove.STATE_EXTENSION, Groove.RULE_EXTENSION,
                            Groove.TYPE_EXTENSION, Groove.GXL_EXTENSION);
                    }

                    @Override
                    public boolean acceptExtension(File file) {
                        return false;
                    }

                    @Override
                    public String stripExtension(String fileName) {
                        File file = new File(fileName);
                        for (ExtensionFilter filter : MyFileChooser.this.filters) {
                            if (filter.acceptExtension(file)) {
                                return filter.stripExtension(fileName);
                            }
                        }
                        return fileName;
                    }
                };
            }
            return this.graphFilter;
        }

        /**
         * Extension filter for all known kinds of graph files.
         */
        private ExtensionFilter graphFilter;

        /** Lazily creates and returns the rule filter. */
        private ExtensionFilter getRuleFilter() {
            if (this.ruleFilter == null) {
                this.ruleFilter = Groove.createRuleFilter();
            }
            return this.ruleFilter;
        }

        /**
         * Extension filter for rule files.
         */
        private ExtensionFilter ruleFilter;

        /** Lazily creates and returns the state filter. */
        private ExtensionFilter getStateFilter() {
            if (this.stateFilter == null) {
                this.stateFilter = Groove.createStateFilter();
            }
            return this.stateFilter;
        }

        /**
         * Extension filter for state files.
         */
        private ExtensionFilter stateFilter;

        /** Lazily creates and returns the type filter. */
        private ExtensionFilter getTypeFilter() {
            if (this.typeFilter == null) {
                this.typeFilter = Groove.createTypeFilter();
            }
            return this.typeFilter;
        }

        /**
         * Extension filter for type files.
         */
        private ExtensionFilter typeFilter;

        /** Array of extension filters, ordered by role index. */
        private final ExtensionFilter[] filters =
            {getStateFilter(), getRuleFilter(), getTypeFilter(), getGxlFilter()};

        /**
         * Returns the role for which a given extension filter acts.
         * @param filter the filter for which the role should be returned
         * @return the role for {@code filter}; {@link #GRAPH_INDEX} if {@code
         *         filter} is {@code null}
         */
        private int getFilterRole(FileFilter filter) {
            Integer result = this.filterRoleMap.get(filter);
            if (result == null) {
                result = GRAPH_INDEX;
            }
            return result;
        }

        /**
         * Mapping from file filters to the corresponding role of the saved
         * graph.
         */
        private final Map<FileFilter,Integer> filterRoleMap =
            new LinkedHashMap<FileFilter,Integer>();
        {
            this.filterRoleMap.put(getStateFilter(), GRAPH_INDEX);
            this.filterRoleMap.put(getRuleFilter(), RULE_INDEX);
            this.filterRoleMap.put(getTypeFilter(), TYPE_INDEX);
            this.filterRoleMap.put(getGxlFilter(), GRAPH_INDEX);
        }
    }

    /**
     * @param args empty or a singleton containing a filename of the graph to be
     *        edited
     */
    public static void main(String[] args) {
        try {
            // Add an Editor Panel
            final Editor editor = new Editor();
            if (args.length == 0) {
                editor.setPlainGraph(null);
            } else {
                editor.doOpenGraph(new File(args[0]));
            }
            editor.start();
        } catch (IOException exc) {
            System.out.println("Error: " + exc.getMessage());
        }
    }

    /** The name of the editor application. */
    public static final String EDITOR_NAME = "Groove Editor";

    private static final String[] TITLE = {"New Graph", "New Rule", "New Type"};

    /** The indication displayed in the frame title for a modified graph. */
    public static final String MODIFIED_INDICATOR = "> ";

    /** Size of the preview dialog window. */
    private static final Dimension DEFAULT_PREVIEW_SIZE =
        new Dimension(500, 500);

    /** Index of the graph role. */
    static final int GRAPH_INDEX = 0;
    /** Index of the rule role. */
    static final int RULE_INDEX = 1;
    /** Index of the type role. */
    static final int TYPE_INDEX = 2;
    /** Mapping from roles to role indices. */
    static final Map<String,Integer> roleIndexMap =
        new HashMap<String,Integer>();
    {
        roleIndexMap.put(Groove.GRAPH_ROLE, GRAPH_INDEX);
        roleIndexMap.put(Groove.RULE_ROLE, RULE_INDEX);
        roleIndexMap.put(Groove.TYPE_ROLE, TYPE_INDEX);
    }
    private static final String[] roles =
        {Groove.GRAPH_ROLE, Groove.RULE_ROLE, Groove.TYPE_ROLE};
    /**
     * Property name of the edit type of the editor. The edit type is the kind
     * of object being edited. Possible values are {@link Groove#GRAPH_ROLE},
     * {@link Groove#RULE_ROLE} and {@link Groove#RULE_ROLE}.
     */
    static public final String ROLE_PROPERTY = "type";
}