// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente
 
// Licensed under the Apache License, Version 2.0 (the "License"); 
// you may not use this file except in compliance with the License. 
// You may obtain a copy of the License at 
// http://www.apache.org/licenses/LICENSE-2.0 
 
// Unless required by applicable law or agreed to in writing, 
// software distributed under the License is distributed on an 
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
// either express or implied. See the License for the specific 
// language governing permissions and limitations under the License.
/*
 * $Id: Editor.java,v 1.15 2007-04-30 19:53:29 rensink Exp $
 */
package groove.gui;

import static groove.gui.Options.HELP_MENU_NAME;
import static groove.gui.Options.IS_ATTRIBUTED_OPTION;
import groove.graph.Graph;
import groove.gui.jgraph.AspectJModel;
import groove.gui.jgraph.EditorJGraph;
import groove.gui.jgraph.EditorJModel;
import groove.gui.jgraph.GraphJModel;
import groove.gui.jgraph.JGraph;
import groove.io.ExtensionFilter;
import groove.io.GrooveFileChooser;
import groove.io.LayedOutXml;
import groove.io.Xml;
import groove.trans.DefaultRuleFactory;
import groove.trans.RuleFactory;
import groove.trans.RuleNameLabel;
import groove.trans.SystemProperties;
import groove.util.Converter;
import groove.util.Groove;
import groove.view.AspectualRuleView;
import groove.view.FormatException;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
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
import javax.swing.event.UndoableEditEvent;

import org.jgraph.event.GraphModelEvent;
import org.jgraph.event.GraphModelListener;
import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;
import org.jgraph.graph.GraphUndoManager;

/**
 * Simplified but usable graph editor.
 * @author Gaudenz Alder, modified by Arend Rensink and Carel van Leeuwen
 * @version $Revision: 1.15 $ $Date: 2007-04-30 19:53:29 $
 */
public class Editor extends JFrame implements GraphModelListener, IEditorModes {
    /** The name of the editor application. */
    public static final String EDITOR_NAME = "Groove Editor";
    /** The name displayed in the frame title for a new graph. */
    public static final String NEW_GRAPH_NAME = "New Graph";
    /** The indication displayed in the frame title for a modified graph. */
    public static final String MODIFIED_INDICATOR = "> ";
    /** Size of the preview dialog window. */
    private static final Dimension PREVIEW_SIZE = new Dimension(500, 500);

    /**
     * @param args empty or a singleton containing a filename of the graph to be edited
     */
    public static void main(String[] args) {
        try {
            // Add an Editor Panel
            final Editor editor = new Editor();
            if (args.length == 0) {
                editor.setModel(null, null);
            } else {
                editor.doOpenGraph(new File(args[0]));
            }
            editor.setVisible(true);
        } catch (IOException exc) {
            System.out.println("Error: " + exc.getMessage());
        }
    }

    /**
     * @param owner
     * @param modal
     * @param editor
     * @return editor dialog
     */
    static public JDialog createEditorDialog(JFrame owner, boolean modal, Editor editor) {
        JDialog result = new JDialog(owner, modal);
        result.setJMenuBar(editor.getJMenuBar());
        result.setContentPane(editor.getContentPane());
        result.setTitle(editor.getTitle());
        result.pack();
        return result;
    }
    
    /**
     * Action to set the editing mode (selection, node or edge).
     */
    protected class SetEditingModeAction extends ToolbarAction {
    	/** Constructs an action with a given name, key and icon. */
        SetEditingModeAction(String text, KeyStroke acceleratorKey, ImageIcon smallIcon) {
            super(text, acceleratorKey, smallIcon);
            putValue(SHORT_DESCRIPTION, null);
        }
    
        /** (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(ActionEvent evt) {
            super.actionPerformed(evt);
            updateModeButtons(this);
        }
    }

    /**
     * Action to preview the current jgraph as a transformation rule.
     */
    protected class RulePreviewAction extends ToolbarAction {
    	/** Constructs an instance of the action. */
        protected RulePreviewAction() {
            super(Options.VIEW_ACTION_NAME, null, Groove.RULE_SMALL_ICON);
        }
    
        /** (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(ActionEvent evt) {
            super.actionPerformed(evt);
            handlePreview();
        }
    }

    /**
     * Action to export the current state of the editor to an image file.
     */
    protected class ExportGraphAction extends AbstractAction {
    	/** Constructs an instance of the action. */
        protected ExportGraphAction() {
            super(Options.EXPORT_ACTION_NAME);
            putValue(ACCELERATOR_KEY, Options.EXPORT_KEY);
        }
    
        /** (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent evt) {
            handleExportGraph();
        }
    }

    /**
     * Action to save the current state of the editor into a file.
     */
    protected class SaveGraphAction extends ToolbarAction {
    	/** Constructs an instance of the action. */
        protected SaveGraphAction() {
            super(Options.SAVE_ACTION_NAME, Options.SAVE_KEY, new ImageIcon(Groove.getResource("save.gif")));
        }
    
        @Override
        public void actionPerformed(ActionEvent evt) {
            super.actionPerformed(evt);
            handleSaveGraph();
        }
    }

    /**
     * Action to open a graph file into the editor.
     */
    protected class OpenGraphAction extends ToolbarAction {
    	/** Constructs an instance of the action. */
        protected OpenGraphAction() {
            super(Options.OPEN_ACTION_NAME, Options.OPEN_KEY, new ImageIcon(Groove.getResource("open.gif")));
        }
    
        @Override
        public void actionPerformed(ActionEvent evt) {
            super.actionPerformed(evt);
            handleOpenGraph();
        }
    }

    /**
     * Action to start with a blank graph.
     */
    protected class NewGraphAction extends ToolbarAction {
    	/** Constructs an instance of the action. */
        NewGraphAction() {
            super(Options.NEW_ACTION_NAME, Options.NEW_KEY, new ImageIcon(Groove.getResource("new.gif")));
        }
    
        @Override
        public void actionPerformed(ActionEvent evt) {
            super.actionPerformed(evt);
            if (showAbandonDialog()) {
                currentFile = null;
                setModel(null, null);
                getGraphSaveChooser().setSelectedFile(null);
            }
        }
    }

    /** This will change the source of the actionevent to graph. */
    protected class TransferAction extends ToolbarAction {
        /**
         * Constructs an action that redirects to another action, while 
         * seting the source of the event to the editor's j-graph.
         */
        public TransferAction(Action action, KeyStroke acceleratorKey, String name) {
            super(name, acceleratorKey, (ImageIcon) action.getValue(SMALL_ICON));
            putValue(SHORT_DESCRIPTION, name);
            setEnabled(false);
            this.action = action;
        }
    
        /** Redirects the Actionevent. */
        @Override
        public void actionPerformed(ActionEvent evt) {
            super.actionPerformed(evt);
            evt = new ActionEvent(jgraph, evt.getID(), evt.getActionCommand(), evt.getModifiers());
            action.actionPerformed(evt);
            if (this == getCutAction() || this == getCopyAction()) {
                getPasteAction().setEnabled(true);
            }
        }
        
        /** The action that this transfer action wraps. */
        protected Action action;        
    }

    /**
     * General class for actions with toolbar buttons. Takes care of image, name and key
     * accelleration; moreover, the <tt>actionPerformed(ActionEvent)</tt> starts by invoking
     * <tt>stopEditing()</tt>.
     * @author Arend Rensink
     * @version $Revision: 1.15 $
     */
    protected abstract class ToolbarAction extends AbstractAction {
    	/** Constructs an action with a given name, key and icon. */
        ToolbarAction(String name, KeyStroke acceleratorKey, Icon icon) {
            super(name, icon);
            putValue(Action.SHORT_DESCRIPTION, name);
            putValue(ACCELERATOR_KEY, acceleratorKey);
            jGraphPanel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                    .put(acceleratorKey, name);
            jgraph.getInputMap().put(acceleratorKey, name);
            jGraphPanel.getActionMap().put(name, this);
        }
    
        public void actionPerformed(ActionEvent evt) {
            jgraph.stopEditing();
        }
    }

    /**
     * Action for quitting the editor.
     * Calls {@link Editor#handleQuit()} to execute the action.
     */
    protected class QuitAction extends AbstractAction {
    	/** Constructs an instance of the action. */
        public QuitAction() {
            super(Options.QUIT_ACTION_NAME);
            putValue(ACCELERATOR_KEY, Options.QUIT_KEY);
        }
    
        /**
         * Calls {@link Editor#handleQuit()}.
         */
        public void actionPerformed(ActionEvent e) {
            handleQuit();
        }
    }

    /**
     * Action for displaying an about box.
     */
    protected class AboutAction extends AbstractAction {
    	/** Constructs an instance of the action. */
        protected AboutAction() {
            super(Options.ABOUT_ACTION_NAME);
        }

        public void actionPerformed(ActionEvent evt) {
            new AboutBox(Editor.this);
        }
    }

    /**
     * An action to close the editor, used if the editor is invoked in the
     * context of some other frame.
     * Closing is done using {@link Editor#handleClose()}.
     */
    protected class CloseEditorAction extends AbstractAction {
    	/** Constructs an instance of the action. */
        public CloseEditorAction() {
            super(Options.CLOSE_ACTION_NAME);
        }
        
        /** Calls {@link Editor#handleClose()}. */
        public void actionPerformed(ActionEvent e) {
            handleClose();
        }
    }

    private class EditPropertiesAction extends AbstractAction {
    	/** Constructs an instance of the action. */
        public EditPropertiesAction() {
            super(Options.EDIT_ACTION_NAME);
        }
        
        /** 
         * Displays a {@link PropertiesDialog} for the properties
         * of the edited graph.
         */
        public void actionPerformed(ActionEvent e) {
            PropertiesDialog dialog = new PropertiesDialog(Editor.this, getModel().getProperties(), true);
            if (dialog.showDialog()) {
            	getModel().setProperties(dialog.getProperties());
            	currentGraphModified = true;
            }
        }
    }

    /** 
     * Constructs an editor frame with an initially empty graph.
     * It is not configured as an auxiliary component.
     * @see #isAuxiliary()  
     */
    public Editor() {
        this(false);
    }

    /** 
     * Constructs an editor frame with an initially empty graph,
     * possibly for use as an auxiliary component in another frame.
     * @param auxiliary <tt>true</tt> if the editor is an auxiliary component
     * (in which case it should not exit on closing)
     * @ensure <tt>isAuxiliary() == auxiliary</tt>
     */
    public Editor(boolean auxiliary) {
        super(EDITOR_NAME);
        this.auxiliary = auxiliary;
        // set file chooser for load, save & exit
        currentDir = new File(Groove.WORKING_DIR);
        // Construct the main components
        jgraph = new EditorJGraph(this);
        jGraphPanel = new JGraphPanel<EditorJGraph>(jgraph);

        initListeners();
        initGUI();
        pack();
    }

    /**
     * Indicates if this editor is used as an auxiliary component in
     * some other frame.
     */
    public boolean isAuxiliary() {
        return auxiliary;
//        return false;
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
     * Reads the graph to be edited from a file.
     * If the file does not exist, a new, empty model with the given name is created.
     * @param fromFile the file to read from
     * @throws IOException if <tt>fromFile</tt> did not contain a correctly formatted graph
     */
    public void doOpenGraph(final File fromFile) throws IOException {
        currentFile = fromFile;
        currentDir = currentFile.getAbsoluteFile().getParentFile();
        // first create a graph from the gxl file
        Graph graph = null;
        try {
            graph = layoutGxl.unmarshalGraph(fromFile);
        } catch (FormatException e) {
            throw new IOException("Can't load graph from " + fromFile.getName() + ": format error");
        } catch (FileNotFoundException e) {
            // we create a new model
        }
        // load the model in the event dispatch thread, to avoid concurrency issues
        final GraphJModel model = graph == null ? null : new GraphJModel(graph, getOptions());
        SwingUtilities.invokeLater(new Runnable() {
			public void run() {
		        setModel(fromFile.getName(), model);
			}        	
        });
    }

    /**
     * Saves the currently edited model as an ordinary graph to a file.
     * @param toFile the file to save to
     * @throws IOException if <tt>fromFile</tt> did not contain a correctly formatted graph
     */
    public void doSaveGraph(File toFile) throws FormatException, IOException {
        currentFile = toFile;
        currentDir = toFile.getParentFile();
        Graph saveGraph = getModel().toPlainGraph();
        layoutGxl.marshalGraph(saveGraph, toFile);
        setModelName(currentFile.getName());
        setCurrentGraphModified(false);
        notifyGraphSaved();
    }

    /**
     * Exports the currently edited model, including hidden and emphasis, to an image file.
     * @param filter the filter that determines the format to export to
     * @param toFile the file to save to
     * @throws IOException if <tt>fromFile</tt> did not contain a correctly formatted graph
     */
    public void doExportGraph(ExtensionFilter filter, File toFile) throws IOException {
        if (filter == fsmFilter) {
            PrintWriter writer = new PrintWriter(new FileWriter(toFile));
            Converter.graphToFsm(getModel().toPlainGraph(), writer);
            writer.close();
        } else {
            String formatName = filter.getExtension().substring(1);
            Iterator<ImageWriter> writerIter = ImageIO.getImageWritersBySuffix(formatName);
            if (writerIter.hasNext()) {
                ImageIO.write(jgraph.toImage(), formatName, toFile);
            } else {
                showErrorDialog("No image writer found for " + filter.getDescription(), null);
            }
        }
    }

    /**
     * Changes the graph being edited to a given j-model, with a given name. If the model is
     * <tt>null</tt>, a fresh {@link EditorJModel}is created; otherwise, the given j-model is
     * copied into a new {@link EditorJModel}.
     * @param name the name of the model; if <tt>null</tt>, the graph has no name
     * @param model the j-model to be set; if <tt>null</tt>, a fresh {@link EditorJModel}is
     *        created
     * @see EditorJModel#EditorJModel()
     * @see EditorJModel#EditorJModel(String, GraphJModel)
     */
    public void setModel(String name, GraphJModel model) {
        // unregister listeners with the model
        getModel().removeUndoableEditListener(getUndoManager());
        getModel().removeGraphModelListener(this);
        if (model == null) {
            jgraph.setModel(new EditorJModel(name));
        } else {
            jgraph.setModel(new EditorJModel(name, model));
        }
        setCurrentGraphModified(false);
        getUndoManager().discardAllEdits();
        getModel().addUndoableEditListener(getUndoManager());
        getModel().addGraphModelListener(this);
        updateHistoryButtons();
        updateStatus();
    }
    
    /**
     * @return the j-model currently being edited, or 
     * <tt>null</tt> if no editor model is set.
     */
    public EditorJModel getModel() {
        return jgraph.getModel();
    }

    /** Records the current number of visible graph elements on the status bar. 
     *  (non-Javadoc)
     * @see org.jgraph.event.GraphModelListener#graphChanged(org.jgraph.event.GraphModelEvent)
     */
    public void graphChanged(GraphModelEvent e) {
        updateStatus();
    }

    /**
     * Handler method to execute a {@link OpenGraphAction}.
     * Invokes a file chooser dialog, and calls {@link #doOpenGraph(File)} 
     * if a file is selected. 
     */
    protected void handleOpenGraph() {
        getGraphOpenChooser().setCurrentDirectory(currentDir);
        getGraphOpenChooser().setSelectedFile(null);
        int result = getGraphOpenChooser().showOpenDialog(jGraphPanel);
        if (result == JFileChooser.APPROVE_OPTION && showAbandonDialog()) {
            try {
                doOpenGraph(getGraphOpenChooser().getSelectedFile());
            } catch (IOException exc) {
                // exc.printStackTrace(System.err);
                showErrorDialog("Error while loading graph from " + currentFile, exc);
            }
        }
    }

    /**
     * Handler method to execute a {@link SaveGraphAction}.
     * Invokes a file chooser dialog, and calls {@link #doSaveGraph(File)} 
     * if a file is selected. 
     * The return value is the save file, or <code>null</code> if nothing was saved.
     */
    protected File handleSaveGraph() {
        // set filter to the one that accepts the current file (if any)
        javax.swing.filechooser.FileFilter[] fileFilters = getGraphSaveChooser().getChoosableFileFilters();
        boolean filterFound = false;
        for (int i = 0; !filterFound && i < fileFilters.length; i++) {
            if (currentFile == null || fileFilters[i].accept(currentFile)) {
                filterFound = true;
                getGraphSaveChooser().setFileFilter(fileFilters[i]);
            }
        }
        getGraphSaveChooser().setCurrentDirectory(currentDir);
        getGraphSaveChooser().setSelectedFile(currentFile);
        // get the file to write to
        File toFile = ExtensionFilter.showSaveDialog(getGraphSaveChooser(), jGraphPanel);
        if (toFile != null) {
            try {
                if (getGraphSaveChooser().getFileFilter() == ruleFilter
                        && getConfirmPreviewCheckBox().isSelected()) {
                    if (handlePreview()) {
                        doSaveGraph(toFile);
                    } else {
                        // the graph was after all not saved
                        toFile = null;
                    }
                } else {
                    doSaveGraph(toFile);
                }
            } catch (Exception exc) {
                showErrorDialog("Error while saving graph to " + currentFile, exc);
                toFile = null;
            }
        }
        return toFile;
    }

    /**
     * Handler method to execute a {@link ExportGraphAction}.
     * Invokes a file chooser dialog, and calls {@link #doSaveGraph(File)} 
     * if a file is selected. 
     */
    protected void handleExportGraph() {
        if (getModelName() != null) {
            getExportChooser().setSelectedFile(new File(graphFilter.stripExtension(getModelName())));
        }
        File toFile = ExtensionFilter.showSaveDialog(getExportChooser(), jGraphPanel);
        if (toFile != null) {
            ExtensionFilter filter = (ExtensionFilter) getExportChooser().getFileFilter();
            try {
                doExportGraph(filter, toFile);
            } catch (IOException exc) {
                showErrorDialog("Error while saving to " + toFile, exc);
            }
        }
    }

    /**
     * Shows a preview dialog; if confirmed, changes the model to a new layout.
     * @return <tt>true</tt> if the dialog was confirmed
     */
	protected boolean handlePreview() {
	    try {
	    	RuleNameLabel ruleName = new RuleNameLabel("temp");
            AspectualRuleView ruleGraph = (AspectualRuleView) getRuleFactory().createRuleView(getModel().toPlainGraph(), ruleName, 0, getSystemProperties());
            AspectJModel ruleModel = new AspectJModel(ruleGraph, getOptions());
            JGraph previewGraph = new JGraph(ruleModel);
            JOptionPane previewPane = new JOptionPane(new JScrollPane(previewGraph),
                    JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
            JDialog previewDialog = previewPane.createDialog(jGraphPanel, "Production rule view");
            previewDialog.setSize(PREVIEW_SIZE);
            previewGraph.setToolTipEnabled(true);
            previewDialog.setVisible(true);
            Integer answer = (Integer) previewPane.getValue();
            if (answer != null && answer.intValue() == JOptionPane.OK_OPTION) {
                setSelectInsertedCells(false);
                getModel().replace(new GraphJModel(ruleModel.toPlainGraph(), getOptions()));
                setSelectInsertedCells(true);
                return true;
            }
        } catch (FormatException exc) {
            showErrorDialog("Error in graph format", exc);
        }
        return false;
	}

    /**
     * Closes the editor by making the root component invisible.
     */
    protected void handleClose() {
        getRootComponent().setVisible(false);
        this.dispose();
    }

    /**
     * If the editor has unsaved changes, asks if these should be abandoned;
     * then calls {@link #dispose()}.
     */
    protected void handleQuit() {
        if (showAbandonDialog()) {
            // calling exit is too rigorous
            dispose();
        }
    }
    
    /** Initialises the graph selection listener and attributed graph listener. */
    protected void initListeners() {
        jgraph.setToolTipEnabled(true);
        // Update ToolBar based on Selection Changes
        jgraph.getSelectionModel().addGraphSelectionListener(new GraphSelectionListener() {
            public void valueChanged(GraphSelectionEvent e) {
                // Update Button States based on Current Selection
                boolean selected = !jgraph.isSelectionEmpty();
                getDeleteAction().setEnabled(selected);
                getCopyAction().setEnabled(selected);
                getCutAction().setEnabled(selected);
            }
        });
//
//		final JCheckBoxMenuItem attributedGraphsItem = getOptions().getItem(Options.IS_ATTRIBUTED_OPTION);
//		// listen to the option controlling the parsing of attributed graphs
//		attributedGraphsItem.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				setRuleFactory(DefaultRuleFactory.getInstance());
//			}
//		});
    }
//
//    /**
//     * Initializes the actions, including keyboard shortcuts.
//     */
//    protected void initActions() {
////        // actions if the component is not auxiliary
////        if (isAuxiliary()) {
////            closeAction = getCloseEditorAction();
////        } else {
////            newAction = getNewAction();
////            openAction = getOpenGraphAction();
////            saveAction = getSaveGraphAction();
////            exportAction = getExportGraphAction();
////            quitAction = getQuitAction();
////        }
////        // Set selection mode
////        getSelectModeAction();
////        getNodeModeAction();
////        getEdgeModeAction();
////
////        getUndoAction();
////
////        getRedoAction();
//        getUndoManager();
////
////        getCopyAction();
////        getPasteAction();
////        getCutAction();
////        getDeleteAction();
//    }

    /**
     * Creates and lazily returns the undo manager for this editor.
     */
    private GraphUndoManager getUndoManager() {
        if (undoManager == null) {
            // Create a GraphUndoManager which also Updates the ToolBar
            undoManager = new GraphUndoManager() {
                @Override
                public void undoableEditHappened(UndoableEditEvent e) {
                    super.undoableEditHappened(e);
                    updateHistoryButtons();
                }
            };
        }
        return undoManager;
    }

    /**
     * Lazily creates and returns the action to cut graph elements in the editor.
     */
    private Action getCutAction() {
        if (cutAction == null) {
            Action action = TransferHandler.getCutAction();
            action.putValue(Action.SMALL_ICON, new ImageIcon(Groove.getResource("cut.gif")));
            action.putValue(Action.ACCELERATOR_KEY, Options.CUT_KEY);
            cutAction = new TransferAction(action, Options.CUT_KEY, Options.CUT_ACTION_NAME);
        }
        return cutAction;
    }

    /**
     * Lazily creates and returns the action to copy graph elements in the editor.
     */
    private Action getCopyAction() {
        if (copyAction == null) {
            Action action = TransferHandler.getCopyAction();
            action.putValue(Action.SMALL_ICON, new ImageIcon(Groove.getResource("copy.gif")));
            copyAction = new TransferAction(action, Options.COPY_KEY, Options.COPY_ACTION_NAME);
        }
        return copyAction;
    }

    /**
     * Lazily creates and returns the action to paste graph elements into the editor.
     */
    private Action getPasteAction() {
        if (pasteAction == null) {
            Action action = TransferHandler.getPasteAction();
            action.putValue(Action.SMALL_ICON, new ImageIcon(Groove.getResource("paste.gif")));
            pasteAction = new TransferAction(action, Options.PASTE_KEY, Options.PASTE_ACTION_NAME);
        }
        return pasteAction;
    }

    /**
     * Lazily creates and returns the action to delete graph elements from the editor.
     */
    private Action getDeleteAction() {
        if (deleteAction == null) {
            // Remove
            ImageIcon deleteIcon = new ImageIcon(Groove.getResource("delete.gif"));
            deleteAction = new ToolbarAction(Options.DELETE_ACTION_NAME, Options.DELETE_KEY,
                    deleteIcon) {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (!jgraph.isSelectionEmpty()) {
                        Object[] cells = jgraph.getSelectionCells();
                        cells = jgraph.getDescendants(cells);
                        jgraph.getModel().remove(cells);
                    }
                }
            };
            deleteAction.setEnabled(false);
        }
        return deleteAction;
    }

    /**
     * Lazily creates and returns the action to redo the last editor action.
     */
    private Action getRedoAction() {
        if (redoAction == null) {
            ImageIcon redoIcon = new ImageIcon(Groove.getResource("redo.gif"));
            redoAction = new ToolbarAction(Options.REDO_ACTION_NAME, Options.REDO_KEY, redoIcon) {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    super.actionPerformed(evt);
                    redoLastEdit();
                }
            };
            redoAction.setEnabled(false);
        }
        return redoAction;
    }

    /**
     * Lazily creates and returns the action to undo the last editor action.
     */
    private Action getUndoAction() {
        if (undoAction == null) {
            ImageIcon undoIcon = new ImageIcon(Groove.getResource("undo.gif"));
            undoAction = new ToolbarAction(Options.UNDO_ACTION_NAME, Options.UNDO_KEY, undoIcon) {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    super.actionPerformed(evt);
                    undoLastEdit();
                }
            };
            undoAction.setEnabled(false);
        }
        return undoAction;
    }

    /**
     * Lazily creates and returns the action to set the editor to selection mode.
     */
    private Action getSelectModeAction() {
        if (selectModeAction == null) {
            ImageIcon selectIcon = new ImageIcon(Groove.getResource("select.gif"));
            selectModeAction = new SetEditingModeAction(Options.SELECT_MODE_NAME,
                    Options.SELECT_MODE_KEY, selectIcon);
        }
        return selectModeAction;
    }

    /**
     * Lazily creates and returns the action to set the editor to node editing mode.
     */
    private Action getNodeModeAction() {
        if (nodeModeAction == null) {
            ImageIcon nodeIcon = new ImageIcon(Groove.getResource("rectangle.gif"));
            nodeModeAction = new SetEditingModeAction(Options.NODE_MODE_NAME,
                    Options.NODE_MODE_KEY, nodeIcon);
        }
        return nodeModeAction;
    }

    /**
     * Lazily creates and returns the action to set the editor to edge editing mode.
     */
    private Action getEdgeModeAction() {
        if (edgeModeAction == null) {
            ImageIcon edgeIcon = new ImageIcon(Groove.getResource("edge.gif"));
            edgeModeAction = new SetEditingModeAction(Options.EDGE_MODE_NAME,
                    Options.EDGE_MODE_KEY, edgeIcon);
        }
        return edgeModeAction;
    }

    /**
	 * Lazily creates and returns the action to close the editor (in case it is auxiliary).
	 */
	private Action getCloseEditorAction() {
		if (closeAction == null) {
			closeAction = new CloseEditorAction();
		}
		return closeAction;
	}

	/**
	 * Lazily creates and returns the action to quit the editor.
	 */
	private Action getQuitAction() {
		if (quitAction == null) {
			quitAction = new QuitAction();
		}
		return quitAction;
	}

	/**
	 * Lazily creates and returns the action to export the current graph.
	 */
	private Action getExportGraphAction() {
		if (exportAction == null) {
			exportAction = new ExportGraphAction();
		}
		return exportAction;
	}

	/**
	 * Lazily creates and returns the action to save the current graph.
	 */
	private Action getSaveGraphAction() {
		if (saveAction == null) {
			saveAction = new SaveGraphAction();
		}
		return saveAction;
	}

	/**
	 * Lazily creates and returns the action to open a new graph.
	 */
	private Action getOpenGraphAction() {
		if (openAction == null) {
			openAction =  new OpenGraphAction();
		}
		return openAction;
	}

	/**
	 * Lazily creates and returns the action to start editing a fresh graph.
	 */
	private Action getNewAction() {
		if (newAction == null) {
			newAction = new NewGraphAction();
		}
		return newAction;
	}
	
	/**
	 * Lazily creates and returns the action to edit the graph properties.
	 */
	private Action getEditPropertiesAction() {
		if (editPropertiesAction == null) {
			editPropertiesAction = new EditPropertiesAction();
		}
		return editPropertiesAction;
	}

    /** Initialises the GUI. */
    protected void initGUI() {
        JPanel editorPanel = createEditor();
        setIconImage(Groove.GROOVE_ICON_16x16.getImage());
        // Set Close Operation to Exit
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evt) {
                handleQuit();
            }
        });
        setJMenuBar(createMenuBar());
        setContentPane(editorPanel);
    }

    /**
     * @return the main editor panel
     */
    public JPanel createEditor() {
        JPanel result = new JPanel();
        // initialize the main editor panel
        // Use Border Layout
        result.setLayout(new BorderLayout());
        // Add the main pane as Center Component
        // initEditorPane(createSplitEditorPane());
        // Add a ToolBar
        editorToolBar = createToolBar();
        result.add(editorToolBar, BorderLayout.NORTH);
        result.add(jGraphPanel, BorderLayout.CENTER);
        result.add(statusBar, BorderLayout.SOUTH);
        return result;
    }

	/**
	 * Returns a file chooser for exporting graphs, after lazily creating it.
	 */
	protected JFileChooser getExportChooser() {
		if (exportChooser == null) {
			exportChooser = new GrooveFileChooser();
			exportChooser.setAcceptAllFileFilterUsed(false);
			exportChooser.addChoosableFileFilter(fsmFilter);
			exportChooser.addChoosableFileFilter(jpgFilter);
			exportChooser.addChoosableFileFilter(pngFilter);
			exportChooser.addChoosableFileFilter(epsFilter);
			exportChooser.setFileFilter(pngFilter);
			exportChooser.setCurrentDirectory(new File(Groove.WORKING_DIR));
		}
		return exportChooser;
	}

	/**
	 * Returns a file chooser for loading graphs, after lazily creating it.
	 */
	protected JFileChooser getGraphOpenChooser() {
		if (graphOpenChooser == null) {
			graphOpenChooser = new GrooveFileChooser();
			graphOpenChooser.addChoosableFileFilter(graphFilter);
		}
		return graphOpenChooser;
	}

	/**
	 * Returns a file chooser for saving graphs, after lazily creating it.
	 */
	protected JFileChooser getGraphSaveChooser() {
		if (graphSaveChooser == null) {
			graphSaveChooser = new GrooveFileChooser();
			graphSaveChooser.setAcceptAllFileFilterUsed(false);
			graphSaveChooser.addChoosableFileFilter(stateFilter);
			graphSaveChooser.addChoosableFileFilter(ruleFilter);
			graphSaveChooser.addChoosableFileFilter(gxlFilter);
			graphSaveChooser.setFileFilter(stateFilter);
			JPanel checkBoxPanel = new JPanel(new BorderLayout());
			checkBoxPanel.setBorder(new javax.swing.border.EmptyBorder(0, 5, 0,
					0));
			checkBoxPanel.add(getConfirmPreviewCheckBox(), BorderLayout.SOUTH);
			graphSaveChooser.setAccessory(checkBoxPanel);
		}
		return graphSaveChooser;
	}

	/**
	 * Returns a checkbox forcing a preview dialog before saving a rule.
	 */
	protected JCheckBox getConfirmPreviewCheckBox() {
		if (confirmPreviewCheckBox == null) {
			confirmPreviewCheckBox = new JCheckBox();
			confirmPreviewCheckBox.setText("Rule preview");
			confirmPreviewCheckBox.setSelected(true);
		}
		return confirmPreviewCheckBox;
	}
    
    /**
	 * Sets the modified status of the currentle edited graph. Also updates the frame
	 * title to reflect the new modified status.
	 * 
	 * @param modified
	 *            the new modified status
	 * @see #isCurrentGraphModified()
	 */
    protected void setCurrentGraphModified(boolean modified) {
    	currentGraphModified = modified;
		refreshTitle();
    }

    /**
     * Returns the current modified status of the underlying jgraph.
     * @see #setCurrentGraphModified(boolean)
     */
    protected boolean isCurrentGraphModified() {
        return currentGraphModified;
    }

    /**
	 * Registers that a graph has been saved.
	 * @see #isAnyGraphSaved()
	 */
    protected void notifyGraphSaved() {
    	anyGraphSaved = true;
    }

    /**
     * Indicates if any graph was saved during the lifetime of this editor.
     */
    protected boolean isAnyGraphSaved() {
        return anyGraphSaved;
    }

    /**
     * Sets the name of the editor model.
     * The name may be <tt>null</tt> if the model is to be anonymous.
     * @param name new name for the editor model
     * @see EditorJModel#setName(String)
     */
    protected void setModelName(String name) {
        if (getModel() != null) {
            getModel().setName(name);
            refreshTitle();
        }
    }

    /**
     * Returns the current name of the editor model.
     * The name may be <tt>null</tt> if the model is anonymous.
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
     * Sets the name of the graph in the title bar. If the indicated name is <tt>null</tt>, a
     * {@link #NEW_GRAPH_NAME} is used.
     */
    protected void refreshTitle() {
        String modelName = getModelName();
        String title = (currentGraphModified ? MODIFIED_INDICATOR: "") + (modelName == null ? NEW_GRAPH_NAME : modelName) + " - " + EDITOR_NAME;
        Component window = getRootComponent();
        if (window instanceof JFrame) {
            ((JFrame) window).setTitle(title);        
        } else if (window instanceof JDialog) {
            ((JDialog) window).setTitle(title);                    
        }
    }

    /**
     * Returns the top level component of the graph panel in the containmeint hierarchy.
     */
    protected Component getRootComponent() {
        Component component = jGraphPanel;
        while (component != null && !(component instanceof JFrame || component instanceof JDialog)) {
            component = component.getParent();
        }
        return component;
    }
    
    /**
     * Creates and returns the menu bar. Requires the actions to have been initialized first.
     */
    protected JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        // file menu, only if the component is not auxiliary
        if (! isAuxiliary()) {
        	menuBar.add(createFileMenu());
        }
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
	private JMenu createFileMenu() {
		JMenu result = new JMenu(Options.FILE_MENU_NAME);
	    result.add(getNewAction());
	    result.add(getOpenGraphAction());
	    result.addSeparator();
	    result.add(getSaveGraphAction());
	    result.add(getExportGraphAction());
	    result.addSeparator();
	    result.add(getQuitAction());
	    return result;
	}

	/**
	 * Creates and returns an edit menu for the menu bar.
	 */
	private JMenu createEditMenu() {
	    JMenu result = new JMenu(Options.EDIT_MENU_NAME);
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
	    jgraph.fillOutEditMenu(result.getPopupMenu());
	    return result;
	}

	/**
	 * Creates and returns an options menu for the menu bar.
	 */
	private JMenu createOptionsMenu() {
        JMenu optionsMenu = new JMenu(Options.OPTIONS_MENU_NAME);
        optionsMenu.add(getOptions().getItem(IS_ATTRIBUTED_OPTION));
        return optionsMenu;
	}

	/**
	 * Creates and returns a properties menu for the menu bar.
	 */
	private JMenu createPropertiesMenu() {
        JMenu result = new JMenu(Options.PROPERTIES_MENU_NAME);
	    result.addSeparator();
	    result.add(getEditPropertiesAction());
        return result;
	}

	/**
	 * Creates and returns a display menu for the menu bar.
	 */
	private JMenu createDisplayMenu() {
        JMenu displayMenu = new JMenu(Options.DISPLAY_MENU_NAME);
        jgraph.fillOutDisplayMenu(displayMenu.getPopupMenu());
        displayMenu.addSeparator();
        displayMenu.add(jGraphPanel.getViewLabelListItem());
        return displayMenu;
	}

	/**
	 * Creates and returns a help menu for the menu bar.
	 */
	private JMenu createHelpMenu() {
		JMenu result = new JMenu(HELP_MENU_NAME);
    	result.add(new JMenuItem(new AboutAction()));
    	return result;
	}

    /**
     * Creates and returns the tool bar. Requires the actions to have been initialized.
     */
    protected JToolBar createToolBar() {
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        if (isAuxiliary()) {
            toolbar.add(getCloseEditorAction());
        } else {
            toolbar.add(getNewAction());
            toolbar.add(getOpenGraphAction());
            toolbar.add(getSaveGraphAction());
        }
        toolbar.add(getRulePreviewAction());

        // Mode block
        toolbar.addSeparator();

        toolbar.add(getSelectModeButton());        
        toolbar.add(getNodeModeButton());
        toolbar.add(getEdgeModeButton());


        // Undo Block
        toolbar.addSeparator();
        toolbar.add(getUndoAction());
        toolbar.add(getRedoAction());

        // Edit Block
        toolbar.addSeparator();
        toolbar.add(getCopyAction());
        toolbar.add(getPasteAction());
        toolbar.add(getCutAction());
        toolbar.add(getDeleteAction());
        return toolbar;
    }

    /** Returns a rule properties object based on the current options setting. */
    protected SystemProperties getSystemProperties() {
    	return SystemProperties.getInstance(getOptions().getValue(IS_ATTRIBUTED_OPTION));
    }
    
	/**
	 * Returns the group of editing mode buttons, lazily creating it first.
	 */
	private ButtonGroup getModeButtonGroup() {
		if (modeButtonGroup == null) {
			modeButtonGroup = new ButtonGroup();
			modeButtonGroup.add(getSelectModeButton());
			modeButtonGroup.add(getNodeModeButton());
			modeButtonGroup.add(getEdgeModeButton());
		}
		return modeButtonGroup;
	}

	/**
	 * Returns the button for setting edge editing mode, lazily creating it first.
	 */
	private JToggleButton getEdgeModeButton() {
		if (edgeModeButton == null) {
			edgeModeButton = new JToggleButton(getEdgeModeAction());
			edgeModeButton.setText(null);
			edgeModeButton.setToolTipText(Options.EDGE_MODE_NAME);
		}
		return edgeModeButton;
	}

	/**
	 * Returns the button for setting node editing mode, lazily creating it first.
	 */
	private JToggleButton getNodeModeButton() {
		if (nodeModeButton == null) {
			nodeModeButton = new JToggleButton(getNodeModeAction());
			nodeModeButton.setText(null);
			nodeModeButton.setToolTipText(Options.NODE_MODE_NAME);
		}
		return nodeModeButton;
	}

	/**
	 * Returns the button for setting selection mode, lazily creating it first.
	 */
	private JToggleButton getSelectModeButton() {
		if (selectModeButton == null) {
			selectModeButton = new JToggleButton(getSelectModeAction());
			selectModeButton.setText(null);
			selectModeButton.setToolTipText(Options.SELECT_MODE_NAME);
			selectModeButton.doClick();
		}
		return selectModeButton;
	}

    /** 
     * Updates the Undo/Redo Button State based on Undo Manager.
     * Also sets {@link #isCurrentGraphModified()} if no more undos are available.
     */
    protected void updateHistoryButtons() {
        // The View Argument Defines the Context
        getUndoAction().setEnabled(getUndoManager().canUndo());
        getRedoAction().setEnabled(getUndoManager().canRedo());
        setCurrentGraphModified(getUndoManager().canUndo());
    }

    /**
     * Activates the appropriate mode button (select, node or edge), based on a given (mode) action.
     * @param forAction the mode action for which the corresponding button is to be activated
     */
    protected void updateModeButtons(Action forAction) {
        Enumeration<AbstractButton> modeButtonEnum = getModeButtonGroup().getElements();
        while (modeButtonEnum.hasMoreElements()) {
            JToggleButton button = (JToggleButton) modeButtonEnum.nextElement();
            if (button.getAction() == forAction) {
                button.setSelected(true);
            }
        }
    }
    
    /** Updates the status bar with information about the currently edited graph. */
    protected void updateStatus() {
        int elementCount = getModel().getRootCount() - getModel().getGrayedOutCount();
        statusBar.setText(""+elementCount+" visible elements");
    }
    
    /** Sets the property whether all inserted cells are automatically selected. */
    protected void setSelectInsertedCells(boolean select) {
        jgraph.getGraphLayoutCache().setSelectsAllInsertedCells(select);  
    }
    
    /** Returns the current property whether all inserted cells are automatically selected. */
    protected boolean isSelectInsertedCells() {
        return jgraph.getGraphLayoutCache().isSelectsAllInsertedCells();          
    }

    /** Undoes the last registered change to the Model or the View. */
    protected void undoLastEdit() {
        try {
            setSelectInsertedCells(false);
            getUndoManager().undo(jgraph.getGraphLayoutCache());
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
            getUndoManager().redo(jgraph.getGraphLayoutCache());
            setSelectInsertedCells(true);
        } catch (Exception ex) {
            System.err.println(ex);
        } finally {
            updateHistoryButtons();
        }
    }

    /** Creates and displays an {@link ErrorDialog} with a given message and exception. */
    private void showErrorDialog(String message, Exception exc) {
        new ErrorDialog(jGraphPanel, message, exc).setVisible(true);
    }

    /** Creates and shows a confirmation dialog for abandoning the currently edited graph. */
    private boolean showAbandonDialog() {
        if (isCurrentGraphModified()) {
            int res = JOptionPane.showConfirmDialog(jGraphPanel,
                "Save changes in current graph?",
                null,
                JOptionPane.YES_NO_CANCEL_OPTION);
            if (res == JOptionPane.YES_OPTION) {
                File toFile = handleSaveGraph();
                return toFile != null;
            } else {
                return res == JOptionPane.NO_OPTION;
            }
        } else {
            return true;
        }
    }
//
//	/**
//     * Sets the {@link #ruleFactory} with the given object.
//     * @param ruleFactory the new rule factory
//     */
//    protected void setRuleFactory(RuleFactory ruleFactory) {
//    	this.ruleFactory = ruleFactory;
//    }

    /**
     * Returns the rule factory.
     * @return the {@link #ruleFactory}-value
     */
    protected RuleFactory getRuleFactory() {
    	if (ruleFactory == null) {
    		ruleFactory = DefaultRuleFactory.getInstance();
    	}
    	return ruleFactory;
    }
    
    /** Returns the rule preview action, lazily creating it first. */
    Action getRulePreviewAction() {
    	if (rulePreviewAction == null) {
    		rulePreviewAction = new RulePreviewAction();
    	}
    	return rulePreviewAction;
    }

    /**
     * Returns the options object associated with the simulator.
     */
    Options getOptions() {
    	// lazily creates the options 
    	if (options == null) {
    		options = new Options();
        	options.getItem(Options.SHOW_REMARKS_OPTION).setSelected(true);
    	}
    	return options;
    }
    
    /**
     * The options object of this simulator.
     */
    private Options options;

    /** The jgraph instance used in this editor. */
    private final EditorJGraph jgraph;

    /**
     * Rule factory used for previewing the graph as a rule.
     */
    private RuleFactory ruleFactory;

    /**
     * Indicates if the editor is an auxiliary component.
     */
    private final boolean auxiliary;
    
    /** The tool bar of this editor. */
    private JToolBar editorToolBar;
    
    /** The jgraph panel used in this editor. */
    private final JGraphPanel<EditorJGraph> jGraphPanel;
    
    /** Status bar of the editor. */
    private final JLabel statusBar = new JLabel();

    /** Checkbox to indicate that saving rules should be preceded by a preview. */
    private JCheckBox confirmPreviewCheckBox;

    /** Indicates whether jgraph has been modified since the last save. */
    private boolean currentGraphModified;

    /** Indicates whether jgraph has been modified since the last save. */
    private boolean anyGraphSaved;

    /** The undo manager of the editor. */
    private transient GraphUndoManager undoManager;

    /** Currently edited file. */
    private File currentFile;

    /** Current directory of file choosers. */
    private File currentDir = new File(Groove.WORKING_DIR);
    /**
     * The GXL converter used for marshalling and unmarshalling layouted graphs.
     */
    private final Xml<Graph> layoutGxl = new LayedOutXml();

    /**
     * File chooser for graph opening.
     */
    private JFileChooser graphOpenChooser;

    /**
     * File chooser for graph saving.
     */
    private JFileChooser graphSaveChooser;

    /**
     * File chooser for export actions.
     */
    private JFileChooser exportChooser;

    /**
     * Extension filter used for exporting the graph in fsm format.
     */
    private final ExtensionFilter fsmFilter = Groove.createFsmFilter();

    /**
     * Extension filter used for exporting the graph in jpeg format.
     */
    private final ExtensionFilter jpgFilter = new ExtensionFilter("JPEG image files",
            Groove.JPG_EXTENSION);

    /**
     * Extension filter used for exporting the graph in PNG format.
     */
    private final ExtensionFilter pngFilter = new ExtensionFilter("PNG files",
            Groove.PNG_EXTENSION);

    /**
     * Extension filter used for exporting the graph in EPS format.
     */
    private final ExtensionFilter epsFilter = new ExtensionFilter("EPS files",
            Groove.EPS_EXTENSION);

    /**
     * Extension filter for state files.
     */
    private final ExtensionFilter stateFilter = Groove.createStateFilter();

    /**
     * Extension filter for rule files.
     */
    private final ExtensionFilter ruleFilter = Groove.createRuleFilter();

    /**
     * Extension filter used for exporting the LTS in jpeg format.
     */
    private final ExtensionFilter gxlFilter = Groove.createGxlFilter();
    
    /**
     * Extension filter for all known kinds of graph files.
     */
    private final ExtensionFilter graphFilter = new ExtensionFilter("Graph files", "") {
        @Override
        public boolean accept(File file) {
            return isAcceptDirectories() && file.isDirectory()  
            		|| file.getName().endsWith(Groove.GXL_EXTENSION)
                    || file.getName().endsWith(Groove.RULE_EXTENSION)
                    || file.getName().endsWith(Groove.STATE_EXTENSION);
        }

        @Override
        public String getDescription() {
            return "Graph files (*" + Groove.GXL_EXTENSION + ", *" + Groove.RULE_EXTENSION
                    + ", *" + Groove.STATE_EXTENSION + ")";
        }
        
        @Override
        public boolean acceptExtension(File file) {
            return false;
        }
        
        @Override
        public String stripExtension(String fileName) {
            File file = new File(fileName);
            if (gxlFilter.acceptExtension(file)) {
                return gxlFilter.stripExtension(fileName);
            } else if (stateFilter.acceptExtension(file)) {
                return stateFilter.stripExtension(fileName);
            } else if (ruleFilter.acceptExtension(file)) {
                return ruleFilter.stripExtension(fileName);
            } else {
                return fileName;
            }
        }
    };

    /** Action to undo the last edit. */
    private Action undoAction;
    /** Action to redo the last (undone) edit. */
    private Action redoAction;
    /** Action to delete the selected elements. */
    private Action deleteAction;
    /** Action to cut the selected elements. */
    private Action cutAction;
    /** Action to copy the selected elements. */
    private Action copyAction;
    /** Action to paste the previously cut or copied elements. */
    private Action pasteAction;
    /** Action to create a rule preview dialog. */
    private Action rulePreviewAction;
    /** Action to save the current graph. */
    private Action saveAction;
    /** Action to export the current graph in an image format. */
    private Action exportAction;
    /** Action to edit the graph properties. */
    private Action editPropertiesAction;
    /** Action to open a new graph for editing. */
    private Action openAction;
    /** Action to start an empty graph for editing. */
    private Action newAction;

    /** Action to close the editor. Only if the editor is auxiliary. */
    private Action closeAction;
    /** Action to quit the editor. Only if the editor is not auxiliary. */
    private Action quitAction;
    /** Action to set the editor to selection mode. */
    private Action selectModeAction;
    /** Action to set the editor to node editing mode. */
    private Action nodeModeAction;
    /** Action to set the editor to edge editing mode. */
    private Action edgeModeAction;

    /** Button for setting edge editing mode. */
    private transient JToggleButton edgeModeButton;
    /** Button for setting node editing mode. */
    private transient JToggleButton nodeModeButton;
    /** Button for setting selection mode. */
    private transient JToggleButton selectModeButton;
    /** Collection of editing mode buttons. */
    private ButtonGroup modeButtonGroup;
}