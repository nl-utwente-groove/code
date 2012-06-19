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

import static groove.graph.GraphRole.HOST;
import static groove.graph.GraphRole.RULE;
import static groove.graph.GraphRole.TYPE;
import static groove.gui.Options.HELP_MENU_NAME;
import static groove.gui.Options.SHOW_ASPECTS_OPTION;
import static groove.gui.Options.SHOW_NODE_IDS_OPTION;
import static groove.gui.Options.SHOW_VALUE_NODES_OPTION;
import static groove.gui.jgraph.JGraphMode.EDIT_MODE;
import static groove.gui.jgraph.JGraphMode.PREVIEW_MODE;
import static groove.gui.jgraph.JGraphMode.SELECT_MODE;
import groove.graph.GraphProperties;
import groove.graph.GraphRole;
import groove.graph.TypeGraph;
import groove.gui.dialog.AboutBox;
import groove.gui.dialog.ErrorDialog;
import groove.gui.dialog.PropertiesDialog;
import groove.gui.jgraph.AspectJGraph;
import groove.gui.jgraph.AspectJModel;
import groove.gui.jgraph.GraphJCell;
import groove.gui.jgraph.GraphJGraph;
import groove.gui.jgraph.JGraphMode;
import groove.io.AspectGxl;
import groove.io.ExtensionFilter;
import groove.io.GrooveFileChooser;
import groove.io.LayedOutXml;
import groove.io.PriorityFileName;
import groove.trans.SystemProperties;
import groove.util.Groove;
import groove.util.Property;
import groove.view.FormatError;
import groove.view.FormatException;
import groove.view.StoredGrammarView.TypeViewList;
import groove.view.aspect.AspectGraph;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.WindowConstants;
import javax.swing.event.UndoableEditEvent;
import javax.swing.filechooser.FileFilter;

import org.jgraph.event.GraphModelEvent;
import org.jgraph.event.GraphModelListener;
import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;
import org.jgraph.graph.GraphLayoutCache.GraphLayoutCacheEdit;
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
    Editor(JFrame frame, Options options, SystemProperties properties) {
        // force the LAF to be set
        groove.gui.Options.initLookAndFeel();
        // Construct the main components
        this.options = options;
        this.properties = properties;
        if (frame == null) {
            this.frame = new JFrame(EDITOR_NAME);
            // this.frame.getRootPane().setDoubleBuffered(false);
        } else {
            this.frame = frame;
            this.frame.setTitle(EDITOR_NAME);
        }
        this.jgraph = new AspectJGraph(this);
        AspectJModel newModel = this.jgraph.newModel();
        newModel.loadGraph(AspectGraph.emptyGraph(HOST));
        this.jgraph.setModel(newModel);
        this.jgraph.setExporter(getExporter());
        initListeners();
        initGUI();
    }

    /**
     * Constructs an editor frame with an initially empty graph. It is not
     * configured as an auxiliary component.
     */
    public Editor() {
        this(null, null, null);
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
     * Sets the graph to be edited
     * @param graph the graph to be edited; if <code>null</code>, an empty model
     *        is started.
     * @param refreshModel if {@code true}, the model is refreshed
     * (which causes the edit history to be reset, etc.)
     */
    public void setGraph(AspectGraph graph, boolean refreshModel) {
        // set the model afresh to make sure everything gets updated properly
        setRole(graph.getRole());
        if (refreshModel) {
            AspectJModel newModel = getJGraph().newModel();
            newModel.setType(this.type);
            newModel.loadGraph(graph);
            setModel(newModel);
        } else {
            getModel().loadGraph(graph);
            updateStatus();
            updateTitle();
        }
    }

    /** Returns the aspect graph generated from the current editor contents. */
    public AspectGraph getGraph() {
        return getModel().getGraph();
    }

    /**
     * Changes the graph being edited to a given j-model, with a given name. If
     * the model is <tt>null</tt>, a fresh {@link AspectJModel}is created;
     * otherwise, the given j-model is copied into a new {@link AspectJModel}.
     * @param model the j-model to be set
     */
    private void setModel(AspectJModel model) {
        // unregister listeners with the model
        getModel().removeUndoableEditListener(getUndoManager());
        getModel().removeGraphModelListener(this);
        this.jgraph.setModel(model);
        setDirty(false);
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
    public AspectJModel getModel() {
        return this.jgraph == null ? null : this.jgraph.getModel();
    }

    /** Sets the type graph for this editor. */
    public void setTypeView(TypeViewList typeView) {
        this.type = null;
        if (typeView != null) {
            try {
                this.type = typeView.toModel();
            } catch (FormatException e) {
                // do nothing
            }
        }
        if (getModel().setType(this.type)) {
            updateStatus();
        }
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
     * We listen to the {@link #ROLE_PROPERTY} and the 
     * {@link GraphJGraph#JGRAPH_MODE_PROPERTY}.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        boolean refresh = false;
        if (evt.getPropertyName().equals(ROLE_PROPERTY)) {
            getGraphRoleButton().setSelected(getRole() == HOST);
            getRuleRoleButton().setSelected(getRole() == RULE);
            getTypeRoleButton().setSelected(getRole() == TYPE);
            // we need to refresh because the errors may have changed
            refresh = true;
        } else {
            assert evt.getPropertyName().equals(
                GraphJGraph.JGRAPH_MODE_PROPERTY);
            JGraphMode mode = getJGraph().getMode();
            if (mode == PREVIEW_MODE || evt.getOldValue() == PREVIEW_MODE) {
                getJGraph().setEditable(mode != PREVIEW_MODE);
                refresh = true;
            }
        }
        if (refresh) {
            getModel().syncGraph();
            updateStatus();
            Editor.this.refreshing = true;
            getGraphPanel().refresh();
            Editor.this.refreshing = false;
            updateTitle();
        }
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
                showErrorDialog(
                    String.format("Error while loading %s", selectedFile), exc);
            }
        }
    }

    /**
     * Handler method to execute an Editor.SaveGraphAction or
     * SaveGraphAsAction. If the action was save-as, or there is no
     * model name, invokes a file chooser dialog. Calls
     * {@link #doSaveGraph(File)} if a file is selected. The return value is the
     * save file, or <code>null</code> if nothing was saved.
     * @param as if <code>true</code>, the action was save-as and a save dialog
     *        should always be shown
     */
    protected File handleSaveGraph(boolean as) {
        File toFile = getCurrentFile();
        if (as || toFile == null) {
            toFile =
                ExtensionFilter.showSaveDialog(getGraphChooser(),
                    getGraphPanel(), toFile);
        }
        if (toFile != null) {
            try {
                // parse the file name to extract any priority info
                PriorityFileName priorityName = new PriorityFileName(toFile);
                String actualName = priorityName.getActualName();
                setModelName(actualName);
                toFile =
                    new File(toFile.getParentFile(), actualName
                        + ExtensionFilter.getExtension(toFile));
                doSaveGraph(toFile);
                setCurrentFile(toFile);
            } catch (Exception exc) {
                showErrorDialog(
                    String.format("Error while saving to %s", toFile), exc);
                toFile = null;
            }
        }
        return toFile;
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
                setGraph(graph, true);
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
        AspectGraph saveGraph = getGraph();
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
        getJGraph().addJGraphModeListener(this);
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
                    boolean relevant = true;
                    if (Editor.this.refreshing
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
                        updateHistoryButtons();
                    }
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
        getFrame().setContentPane(getMainPanel());
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
                            GraphRole newRole = result.getFilterRole(filter);
                            if (newRole != null) {
                                setRole(newRole);
                            }
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
     * @param dirty the new modified status
     * @see #isDirty()
     */
    protected void setDirty(boolean dirty) {
        this.dirty = dirty;
        updateTitle();
    }

    /**
     * Returns the current modified status of the underlying jgraph.
     * @see #setDirty(boolean)
     */
    protected boolean isDirty() {
        return this.dirty;
    }

    /**
     * Registers that a graph has been saved.
     * @see #isAnyGraphSaved()
     */
    protected void setGraphSaved() {
        this.anyGraphSaved = true;
        setDirty(false);
    }

    /**
     * Indicates if any graph was saved during the lifetime of this editor.
     */
    protected boolean isAnyGraphSaved() {
        return this.anyGraphSaved;
    }

    /**
     * Indicates if we are editing a rule or a graph.
     * @return <code>true</code> if we are editing a graph.
     */
    public GraphRole getRole() {
        return this.graphRole;
    }

    /**
     * Sets the edit role to a given graph role.
     * @param role the edit role to be set.
     * @return <code>true</code> if the edit type was actually changed;
     *         <code>false</code> if it was already equal to <code>role</code>
     */
    boolean setRole(GraphRole role) {
        assert role.inGrammar();
        GraphRole oldRole = this.graphRole;
        boolean result = role != oldRole;
        // set the value if it has changed
        if (result) {
            this.graphRole = role;
            // fire change only if there was a previous value
            getChangeSupport().firePropertyChange(ROLE_PROPERTY, oldRole, role);
        }
        return result;
    }

    /**
     * Sets the name of the editor model. The name may be <tt>null</tt> if the
     * model is to be anonymous.
     * @param name new name for the editor model
     * @see AspectJModel#setName(String)
     */
    protected void setModelName(String name) {
        if (getModel() != null) {
            getModel().setName(name);
            updateTitle();
        }
    }

    /**
     * Returns the current name of the editor model.
     * @see AspectJModel#getName()
     */
    protected String getModelName() {
        if (getModel() != null) {
            return getModel().getName();
        } else {
            return "";
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
            JGraphPanel<?> result =
                this.jGraphPanel =
                    new JGraphPanel<AspectJGraph>(this.jgraph, false,
                        getOptions()) {
                        @Override
                        protected JToolBar createToolBar() {
                            return Editor.this.createToolBar();
                        }
                    };
            result.initialise();
            result.addRefreshListener(SHOW_NODE_IDS_OPTION);
            result.addRefreshListener(SHOW_VALUE_NODES_OPTION);
            result.addRefreshListener(SHOW_ASPECTS_OPTION);
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
        result.add(getJGraph().getModeAction(SELECT_MODE));
        result.add(getJGraph().getModeAction(EDIT_MODE));
        result.add(getJGraph().getModeAction(PREVIEW_MODE));
        getJGraph().addSubmenu(result, getJGraph().createPopupMenu(null));
        return result;
    }

    /**
     * Creates and returns an options menu for the menu bar.
     */
    JMenu createOptionsMenu() {
        JMenu result = new JMenu(Options.OPTIONS_MENU_NAME);
        result.setMnemonic(Options.OPTIONS_MENU_MNEMONIC);
        result.add(getOptions().getItem(Options.SHOW_NODE_IDS_OPTION));
        result.add(getOptions().getItem(Options.SHOW_ASPECTS_OPTION));
        result.add(getOptions().getItem(Options.SHOW_VALUE_NODES_OPTION));
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
        result.add(getSnapToGridAction());
        this.jgraph.addSubmenu(result, this.jgraph.createDisplayMenu());
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
        addGridButtons(toolbar);
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
        toolbar.add(getJGraph().getModeButton(SELECT_MODE));
        toolbar.add(getJGraph().getModeButton(EDIT_MODE));
        toolbar.add(getJGraph().getModeButton(PREVIEW_MODE));
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
     * Adds a separator and a snap to grid button to a given toolbar.
     * @param toolbar the toolbar to be extended
     */
    void addGridButtons(JToolBar toolbar) {
        // Grid Block
        toolbar.addSeparator();
        toolbar.add(getSnapToGridButton());
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

    /** Creates a panel consisting of the error panel and the status bar. */
    JSplitPane getMainPanel() {
        if (this.mainPanel == null) {
            this.mainPanel =
                new JSplitPane(JSplitPane.VERTICAL_SPLIT, getGraphPanel(),
                    getStatusPanel());
            this.mainPanel.setDividerSize(1);
            this.mainPanel.setContinuousLayout(true);
            this.mainPanel.setResizeWeight(0.9);
            this.mainPanel.resetToPreferredSizes();
        }
        return this.mainPanel;
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
                        GraphJCell errorCell =
                            getModel().getErrorMap().get(arg);
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
        if (getRole() != RULE) {
            keys.remove(GraphProperties.CONFLUENT_KEY);
            keys.remove(GraphProperties.PRIORITY_KEY);
            keys.remove(GraphProperties.ENABLED_KEY);
            keys.remove(GraphProperties.TRANSITION_LABEL_KEY);
        }
        return new PropertiesDialog(getModel().getProperties(), keys, editable);
    }

    /**
     * Updates the Undo/Redo Button State based on Undo Manager. Also sets
     * {@link #isDirty()} if no more undos are available.
     */
    protected void updateHistoryButtons() {
        // The View Argument Defines the Context
        getUndoAction().setEnabled(getUndoManager().canUndo());
        getRedoAction().setEnabled(getUndoManager().canRedo());
        setDirty(getUndoManager().canUndo());
    }

    /**
     * Activates the appropriate role button (host, rule or type), based on a
     * given (type) action.
     * @param forAction the mode action for which the corresponding button is to
     *        be activated
     */
    protected void updateTypeButtons(Action forAction) {
        Enumeration<AbstractButton> typeButtonEnum =
            getTypeButtonGroup().getElements();
        while (typeButtonEnum.hasMoreElements()) {
            JToggleButton button = (JToggleButton) typeButtonEnum.nextElement();
            if (button.getAction() == forAction) {
                button.setSelected(true);
            }
        }
    }

    /**
     * Sets the name of the graph in the title bar. If the indicated name is
     * <tt>null</tt>, the appropriate element of {@link #TITLE} is used.
     */
    protected void updateTitle() {
        String modelName = getModelName();
        if (modelName == null || modelName.length() == 0) {
            modelName = TITLE.get(getRole());
        }
        String title =
            (this.dirty ? MODIFIED_INDICATOR : "") + modelName + " - "
                + EDITOR_NAME;
        Component window = getRootComponent();
        if (window instanceof JFrame) {
            ((JFrame) window).setTitle(title);
        } else if (window instanceof JDialog) {
            ((JDialog) window).setTitle(title);
        }
    }

    /** Sets the enabling of the transfer buttons. */
    protected void updateCopyPasteButtons() {
        boolean previewing = getJGraph().getMode() == PREVIEW_MODE;
        boolean hasSelection = !getJGraph().isSelectionEmpty();
        getCopyAction().setEnabled(!previewing && hasSelection);
        getCutAction().setEnabled(!previewing && hasSelection);
        getDeleteAction().setEnabled(!previewing && hasSelection);
        getPasteAction().setEnabled(!previewing && clipboardFilled);
    }

    /**
     * Updates the status bar and the error panel 
     * with information about the currently edited graph.
     */
    protected void updateStatus() {
        if (!getJGraph().isInserting()) {
            updateCopyPasteButtons();
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    int nodeCount = getGraph().nodeCount();
                    int edgeCount = getGraph().edgeCount();
                    getStatusBar().setText(
                        String.format("%s nodes, %s edges", nodeCount,
                            edgeCount));
                    setErrors(getModel().getErrorMap().keySet());
                }
            });
        }
    }

    /**
     * Displays a list of errors, or hides the error panel if the list is empty.
     */
    private void setErrors(Collection<FormatError> errors) {
        getErrorPanel().setErrors(errors);
        if (!getJGraph().isInserting()) {
            if (getErrorPanel().isVisible()) {
                getMainPanel().setBottomComponent(getErrorPanel());
                getMainPanel().setDividerSize(1);
                getMainPanel().resetToPreferredSizes();
            } else {
                getMainPanel().remove(getErrorPanel());
                getMainPanel().setDividerSize(0);
            }
        }
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
        setSelectInsertedCells(false);
        getUndoManager().undo();
        setSelectInsertedCells(true);
        updateHistoryButtons();
    }

    /** Redoes the latest undone change to the Model or the View. */
    protected void redoLastEdit() {
        setSelectInsertedCells(false);
        getUndoManager().redo();
        setSelectInsertedCells(true);
        updateHistoryButtons();
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
        if (isDirty()) {
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

    /**
     * Returns the options object associated with the simulator.
     */
    public final Options getOptions() {
        // lazily creates the options
        if (this.options == null) {
            this.options = new Options();
        }
        return this.options;
    }

    /** 
     * The grammar properties of the grammar to which the edited
     * graph belongs. May be {@code null} in case no grammar is set.
     */
    public final SystemProperties getProperties() {
        return this.properties;
    }

    /** Returns the jgraph component of this editor. */
    public AspectJGraph getJGraph() {
        return this.jgraph;
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
     * The properties of the grammar being edited.
     */
    private final SystemProperties properties;
    /**
     * The options object of this editor.
     */
    private Options options;
    /** The (optional) type graph against which he edited model is checked. */
    private TypeGraph type;

    /** The frame of the editor. */
    private final JFrame frame;

    /** The jgraph instance used in this editor. */
    private final AspectJGraph jgraph;

    /** The jgraph panel used in this editor. */
    private JGraphPanel<AspectJGraph> jGraphPanel;

    /** Status bar of the editor. */
    private final JLabel statusBar = new JLabel();

    /** Panel containing the graph panel and status panel. */
    private JSplitPane mainPanel;
    /** Panel containing the error panel and status par. */
    private JPanel statusPanel;
    /** Panel displaying format error messages. */
    private ErrorListPanel errorPanel;

    /** Indicates whether jgraph has been modified since the last save. */
    private boolean dirty;

    /** Indicates whether jgraph has been modified since the last save. */
    private boolean anyGraphSaved;

    /** Index of the currently set editor role */
    private GraphRole graphRole = HOST;
    /** The undo manager of the editor. */
    private transient GraphUndoManager undoManager;

    /** 
     * Flag that is set to true while the preview mode switch
     * is being executed.
     */
    private transient boolean refreshing;
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
            result.setSelected(true);
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
            result.setFocusable(false);
            result.setText(null);
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
            result.setFocusable(false);
            result.setText(null);
        }
        return this.typeRoleButton;
    }

    /** Button for setting type editing mode. */
    private transient JToggleButton typeRoleButton;
    /** Collection of graph editing type buttons. */
    private ButtonGroup typeButtonGroup;

    /**
     * Returns the button for setting selection mode, lazily creating it first.
     */
    JToggleButton getSnapToGridButton() {
        if (this.snapToGridButton == null) {
            this.snapToGridButton = new JToggleButton(getSnapToGridAction());
            this.snapToGridButton.setFocusable(false);
            this.snapToGridButton.setText(null);
        }
        return this.snapToGridButton;
    }

    JToggleButton getSnapToGridButton(GraphJGraph jgraph) {
        JToggleButton button = new JToggleButton(new SnapToGridAction(jgraph));
        button.setFocusable(false);
        button.setText(null);
        return button;
    }

    /** Button for snap to grid. */
    transient JToggleButton snapToGridButton;

    /** Returns the exporter of this editor. */
    public final Exporter getExporter() {
        return this.exporter;
    }

    /** Exporter used for all {@link GraphJGraph}s in the editor. */
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
                setDirty(true);
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
                setGraph(AspectGraph.emptyGraph(getRole()), true);
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
            setRole(HOST);
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
            setRole(RULE);
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
            super.actionPerformed(evt);
            setRole(TYPE);
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

    /** Returns the snap to grid action, lazily creating it first. */
    Action getSnapToGridAction() {
        if (this.snapToGridAction == null) {
            this.snapToGridAction = new SnapToGridAction(this.jgraph);
        }
        return this.snapToGridAction;
    }

    /** Action to toggle the snap to grid. */
    private Action snapToGridAction;

    /**
     * Action to preview the current type graph.
     */
    private class SnapToGridAction extends ToolbarAction {

        private GraphJGraph jgraph;

        /** Constructs an instance of the action. */
        protected SnapToGridAction(GraphJGraph jgraph) {
            super(Options.SNAP_TO_GRID_NAME, null, Groove.GRID_ICON);
            this.jgraph = jgraph;
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            super.actionPerformed(evt);
            JToggleButton button = Editor.this.snapToGridButton;
            boolean toggle;
            if (evt.getSource().equals(button)) {
                toggle = button.isSelected();
            } else {
                toggle = !button.isSelected();
            }
            this.jgraph.setGridEnabled(toggle);
            this.jgraph.setGridVisible(toggle);
            if (this.jgraph != Editor.this.jgraph) {
                // We got a click in the preview window, update the editor
                // jGraph as well.
                Editor.this.jgraph.setGridEnabled(toggle);
                Editor.this.jgraph.setGridVisible(toggle);
            }
            button.setSelected(toggle);
        }
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
            setFilters(getRole());
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
        private void setFilters(GraphRole roleIndex) {
            resetChoosableFileFilters();
            for (FileFilter filter : this.filters.values()) {
                addChoosableFileFilter(filter);
            }
            FileFilter defaultFilter = null;
            if (getRole() == HOST && this.lastSaveFilter != null
                && getFilterRole(this.lastSaveFilter) == HOST) {
                defaultFilter = this.lastSaveFilter;
            } else {
                defaultFilter = this.filters.get(roleIndex);
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
                            for (ExtensionFilter filter : MyFileChooser.this.filters.values()) {
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
                        for (ExtensionFilter filter : MyFileChooser.this.filters.values()) {
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

        /** Map of graph roles to extension filters. */
        private final Map<GraphRole,ExtensionFilter> filters =
            new HashMap<GraphRole,ExtensionFilter>();
        {
            this.filters.put(HOST, getStateFilter());
            this.filters.put(RULE, getRuleFilter());
            this.filters.put(TYPE, getTypeFilter());
            // add the GXL filter without association to a graph role
            this.filters.put(null, getGxlFilter());
        }

        /**
         * Returns the role for which a given extension filter acts.
         * @param filter the filter for which the role should be returned
         * @return the role for {@code filter}, or {@code null} if 
         * {@code filter} has no associated filter.
         */
        private GraphRole getFilterRole(FileFilter filter) {
            GraphRole result = this.filterRoleMap.get(filter);
            return result;
        }

        /**
         * Mapping from file filters to the corresponding role of the saved
         * graph.
         */
        private final Map<FileFilter,GraphRole> filterRoleMap =
            new LinkedHashMap<FileFilter,GraphRole>();
        {
            this.filterRoleMap.put(getStateFilter(), HOST);
            this.filterRoleMap.put(getRuleFilter(), RULE);
            this.filterRoleMap.put(getTypeFilter(), TYPE);
            this.filterRoleMap.put(getGxlFilter(), HOST);
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
                editor.setGraph(AspectGraph.emptyGraph(editor.getRole()), true);
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

    private static final Map<GraphRole,String> TITLE =
        new EnumMap<GraphRole,String>(GraphRole.class);

    static {
        TITLE.put(HOST, "New Graph");
        TITLE.put(RULE, "New Rule");
        TITLE.put(TYPE, "New Type");
    }

    /** The indication displayed in the frame title for a modified graph. */
    public static final String MODIFIED_INDICATOR = "> ";

    /**
     * Flag shared between all Editor instances indicating that
     * the clipboard was filled by a cut or copy action.
     */
    private static boolean clipboardFilled;
    /**
     * Property name of the edit type of the editor. The edit type is the kind
     * of object being edited. Values are of type {@link GraphRole}.
     */
    static public final String ROLE_PROPERTY = "type";
}