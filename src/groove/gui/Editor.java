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
 * $Id: Editor.java,v 1.32 2007-05-22 11:46:17 rensink Exp $
 */
package groove.gui;

import static groove.gui.Options.HELP_MENU_NAME;
import groove.graph.Graph;
import groove.graph.GraphInfo;
import groove.graph.GraphProperties;
import groove.gui.jgraph.AspectJModel;
import groove.gui.jgraph.EditorJGraph;
import groove.gui.jgraph.EditorJModel;
import groove.gui.jgraph.GraphJModel;
import groove.gui.jgraph.JGraph;
import groove.io.ExtensionFilter;
import groove.io.GrooveFileChooser;
import groove.io.LayedOutXml;
import groove.io.PriorityFileName;
import groove.io.Xml;
import groove.util.Groove;
import groove.view.AspectualView;
import groove.view.FormatException;
import groove.view.aspect.AspectGraph;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;

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
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.filechooser.FileFilter;

import org.jgraph.event.GraphModelEvent;
import org.jgraph.event.GraphModelListener;
import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;
import org.jgraph.graph.GraphUndoManager;

/**
 * Simplified but usable graph editor.
 * @author Gaudenz Alder, modified by Arend Rensink and Carel van Leeuwen
 * @version $Revision: 1.32 $ $Date: 2007-05-22 11:46:17 $
 */
public class Editor implements GraphModelListener, PropertyChangeListener, IEditorModes {
    /** 
     * Constructs an editor frame with an initially empty graph
     * and a given display options setting.
     * @param options the display options object; may be <code>null</code>
     */
    Editor(Options options) {
        // force the LAF to be set
        groove.gui.Options.initLookAndFeel();
        // Construct the main components
        this.options = options;
        this.frame = new JFrame(EDITOR_NAME);
        this.jgraph = new EditorJGraph(this);
        initListeners();
        initGUI();
        this.frame.pack();
    }

    /** 
     * Constructs an editor frame with an initially empty graph.
     * It is not configured as an auxiliary component.
     */
    public Editor() {
        this(null);
    }

    /** Returns the frame in which the editor is displayed. */
    public final JFrame getFrame() {
		return frame;
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
     * @param graph the graph to be edited; if <code>null</code>, an empty model is started.
     */
    public void setPlainGraph(Graph graph) {
        if (graph == null) {
        	setModel(new EditorJModel(getOptions()));
        } else {
			setModel(new EditorJModel(GraphJModel.newInstance(graph, getOptions())));
			setRole(GraphInfo.getRole(graph));
		}
    }
    
    /** Returns a plain graph constructed from the editor j-model and role. */
    public Graph getPlainGraph() {
    	Graph result = getModel().toPlainGraph();
    	GraphInfo.setRole(result, getRole(false));
    	return result;
    }

    /**
     * Changes the graph being edited to a given j-model, with a given name. If the model is
     * <tt>null</tt>, a fresh {@link EditorJModel}is created; otherwise, the given j-model is
     * copied into a new {@link EditorJModel}.
     * @param model the j-model to be set
     * @see EditorJModel#EditorJModel(Options)
     * @see EditorJModel#EditorJModel(GraphJModel)
     */
    private void setModel(EditorJModel model) {
        // unregister listeners with the model
        getModel().removeUndoableEditListener(getUndoManager());
        getModel().removeGraphModelListener(this);
        jgraph.setModel(model);
        setCurrentGraphModified(false);
        getUndoManager().discardAllEdits();
        getModel().addUndoableEditListener(getUndoManager());
        getModel().addGraphModelListener(this);
        updateHistoryButtons();
        updateStatus();
        updateTitle();
    }
    
    /**
     * @return the j-model currently being edited, or 
     * <tt>null</tt> if no editor model is set.
     */
    public EditorJModel getModel() {
        return jgraph.getModel();
    }

    /** 
     * Creates and returns an aspectual view, based on the current plain graph.
     * The view is a graph view or a rule view, depending in {@link #hasGraphRole()}.
     */
    public AspectualView<?> toView() {
    	return AspectualView.createView(toAspectGraph());
    }

    /** 
     * Creates and returns an aspect graph, based on the current plain graph.
     * The view is a graph view or a rule view, depending in {@link #hasGraphRole()}.
     */
    public AspectGraph toAspectGraph() {
    	return AspectGraph.getFactory().fromPlainGraph(getPlainGraph());
    }

    /** 
     * Refreshes the statur bar.
     */
    public void graphChanged(GraphModelEvent e) {
        updateStatus();
    }
    
    /**
     * We listn to the {@link #ROLE_PROPERTY}.
     */
    public void propertyChange(PropertyChangeEvent evt) {
		getGraphTypeButton().setSelected(hasGraphRole());
		getRuleTypeButton().setSelected(!hasGraphRole());
    	updateStatus();
    	updateTitle();
	}

    /**
	 * Handler method to execute a {@link OpenGraphAction}. Invokes a file
	 * chooser dialog, and calls {@link #doOpenGraph(File)} if a file is
	 * selected.
	 */
    protected void handleOpenGraph() {
        int result = getGraphChooser().showOpenDialog(getGraphPanel());
        if (result == JFileChooser.APPROVE_OPTION && confirmAbandon()) {
        	File selectedFile = getGraphChooser().getSelectedFile();
            try {
                doOpenGraph(selectedFile);
            } catch (IOException exc) {
                showErrorDialog(String.format("Error while loading %s", selectedFile), exc);
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
    	if (getOptions().isSelected(Options.PREVIEW_ON_SAVE_OPTION) && !handlePreview(null)) {
    		return null;
    	} else if (toAspectGraph().hasErrors()) {
    		JOptionPane.showMessageDialog(getFrame(), "Cannot save graph with syntax errors");
    		return null;
    	} else {
			File toFile = ExtensionFilter.showSaveDialog(getGraphChooser(), getGraphPanel());
			if (toFile != null) {
				try {
					doSaveGraph(toFile);
					// parse the file name to extract any priority info
					PriorityFileName priorityName = new PriorityFileName(toFile);
					String actualName = priorityName.getActualName();
					setModelName(actualName);
					if (priorityName.hasPriority()) {
						getModel().getProperties().setPriority(priorityName.getPriority());
					}
					toFile = new File(toFile.getParentFile(), actualName
							+ ExtensionFilter.getExtension(toFile));
					currentFile = toFile;
				} catch (Exception exc) {
					showErrorDialog(String.format("Error while saving to %s", toFile), exc);
					toFile = null;
				}
			}
			return toFile;
		}
    }

    /**
	 * Shows a preview dialog, and possibly replaces the edited graph by the
	 * previewed model.
	 * 
	 * @return <tt>true</tt> if the dialog was confirmed; if so, the jModel is
	 *         aspect correct (and so can be saved).
	 */
	protected boolean handlePreview(String okOption) {
		AspectJModel previewedModel = showPreviewDialog(toView(), okOption);
		if (previewedModel != null) {
			setSelectInsertedCells(false);
			getModel().replace(GraphJModel.newInstance(previewedModel.toPlainGraph(), getOptions()));
			setSelectInsertedCells(true);
			return true;
		} else {
			return false;
		}
	}
	
    /**
     * If the editor has unsaved changes, asks if these should be abandoned;
     * then calls {@link JFrame#dispose()}.
     */
    protected void handleQuit() {
        if (confirmAbandon()) {
            // calling exit is too rigorous
        	getFrame().dispose();
        }
    }

	/**
     * Reads the graph to be edited from a file.
     * If the file does not exist, a new, empty model with the given name is created.
     * @param fromFile the file to read from
     * @throws IOException if <tt>fromFile</tt> did not contain a correctly formatted graph
     */
    private void doOpenGraph(final File fromFile) throws IOException {
    	currentFile = fromFile;
    	// first create a graph from the gxl file
    	final Graph graph = layoutGxl.unmarshalGraph(fromFile);
    	// load the model in the event dispatch thread, to avoid concurrency
    	// issues
    	SwingUtilities.invokeLater(new Runnable() {
    		public void run() {
    			setPlainGraph(graph);
    		}
    	});
    }

    /**
     * Saves the currently edited model as an ordinary graph to a file.
     * @param toFile the file to save to
     * @throws IOException if <tt>fromFile</tt> did not contain a correctly formatted graph
     */
    private void doSaveGraph(File toFile) throws FormatException, IOException { 
        Graph saveGraph = getPlainGraph();
        layoutGxl.marshalGraph(saveGraph, toFile);
        setGraphSaved();
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
        getChangeSupport().addPropertyChangeListener(ROLE_PROPERTY, this);
    }

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
//
//    /**
// * Lazily creates and returns the action to close the editor (in case it is auxiliary).
// */
//private Action getCloseEditorAction() {
//	if (closeAction == null) {
//		closeAction = new CloseEditorAction();
//	}
//	return closeAction;
//}
//
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
     * Lazily creates and returns the action to edit the graph properties.
     */
    private Action getEditPropertiesAction() {
    	if (editPropertiesAction == null) {
    		editPropertiesAction = new EditPropertiesAction();
    	}
    	return editPropertiesAction;
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
     * Lazily creates and returns the action to start editing a fresh graph.
     */
    private Action getNewAction() {
    	if (newAction == null) {
    		newAction = new NewGraphAction();
    	}
    	return newAction;
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
     * Lazily creates and returns the action to open a new graph.
     */
    private Action getOpenGraphAction() {
    	if (openAction == null) {
    		openAction =  new OpenGraphAction();
    	}
    	return openAction;
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
	 * Lazily creates and returns the action to quit the editor.
	 */
	private Action getQuitAction() {
		if (quitAction == null) {
			quitAction = new QuitAction();
		}
		return quitAction;
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
	 * Lazily creates and returns the action to save the current graph.
	 */
	private Action getSaveGraphAction() {
		if (saveAction == null) {
			saveAction = new SaveGraphAction();
		}
		return saveAction;
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

    /** Returns the rule preview action, lazily creating it first. */
    private Action getSetRuleTypeAction() {
        if (rulePreviewAction == null) {
            rulePreviewAction = new SetRuleRoleAction();
        }
        return rulePreviewAction;
    }

    /** Returns the rule preview action, lazily creating it first. */
    private Action getSetGraphTypeAction() {
        if (graphPreviewAction == null) {
            graphPreviewAction = new SetGraphRoleAction();
        }
        return graphPreviewAction;
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
     * Creates a panel showing a given toolbar, and the graph and status panels of the editor.
     */
    JPanel createContentPanel(JToolBar toolBar) {
        JPanel result = new JPanel();
        // initialize the main editor panel
        // Use Border Layout
        result.setLayout(new BorderLayout());
        // Add the main pane as Center Component
        // initEditorPane(createSplitEditorPane());
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
		if (graphChooser == null) {
			graphChooser = new MyFileChooser();
//			graphOpenChooser.addChoosableFileFilter(graphFilter);
		}
		return graphChooser;
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
		updateTitle();
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
    protected void setGraphSaved() {
    	anyGraphSaved = true;
        setCurrentGraphModified(false);
    }

    /**
     * Indicates if any graph was saved during the lifetime of this editor.
     */
    protected boolean isAnyGraphSaved() {
        return anyGraphSaved;
    }

    /**
     * Indicates if we are editing a rule or a graph.
     * @return <code>true</code> if we are editing a graph.
     */
    private boolean hasGraphRole() {
        return Groove.GRAPH_ROLE.equals(role);
    }

    /**
     * Returns a textual representation of the graph type,
     * with the first letter capitalised on demand.
     * @param upper if <code>true</code>, the first letter is capitalised
     */
    String getRole(boolean upper) {
    	if (role == null) {
    		role = Groove.GRAPH_ROLE;
    	}
    	if (upper) {
			char[] result = role.toCharArray();
			result[0] = Character.toUpperCase(result[0]);
			return String.valueOf(result);
		} else {
			return role;
		}
    }
    
    /**
     * Sets the edit role to {@link Groove#GRAPH_ROLE} or {@link Groove#RULE_ROLE}.
     * @param role if <code>true</code>, the edit type is set to graph
     * @return <code>true</code> if the edit type was actually changed; <code>false</code> if it 
     * was already equal to <code>type</code>
     */
    private boolean setRole(String role) {
    	if (! (Groove.GRAPH_ROLE.equals(role) || Groove.RULE_ROLE.equals(role))) {
    		throw new IllegalArgumentException(String.format("Illegal role %s", role));
    	}
    	String oldRole = this.role;
        boolean result = !role.equals(oldRole);
        // set the value if it has changed
        if (result) {
        	this.role = role;
        	// fire change only if there was a previous value
        	if (oldRole != null) {
        		getChangeSupport().firePropertyChange(ROLE_PROPERTY, oldRole, role);
        	}
        }
        return result;
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
            updateTitle();
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

	/** Lazily creates and returns the property change support object for this editor. */
	private PropertyChangeSupport getChangeSupport() {
		if (propertyChangeSupport == null) {
			propertyChangeSupport = new PropertyChangeSupport(this);
		}
		return propertyChangeSupport;
	}
	
	/**
     * Sets the name of the graph in the title bar. If the indicated name is <tt>null</tt>, a
     * {@link #NEW_GRAPH_TITLE} is used.
     */
    protected void updateTitle() {
        String modelName = getModelName();
        if (modelName == null) {
        	modelName = hasGraphRole() ? NEW_GRAPH_TITLE : NEW_RULE_TITLE;
        }
        String title = (currentGraphModified ? MODIFIED_INDICATOR: "") + (modelName == null ? NEW_GRAPH_TITLE : modelName) + " - " + EDITOR_NAME;
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
        Component component = getGraphPanel();
        while (component != null && !(component instanceof JFrame || component instanceof JDialog)) {
            component = component.getParent();
        }
        return component;
    }
    
    JGraphPanel getGraphPanel() {
        if (jGraphPanel == null) {
            jGraphPanel = new JGraphPanel<EditorJGraph>(jgraph, false, getOptions());
        }
    	return jGraphPanel;
    }

    /**
     * Creates and returns the menu bar. Requires the actions to have been initialized first.
     */
    JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        // file menu, only if the component is not auxiliary
//        if (getFixedEditType() == null) {
        	menuBar.add(createFileMenu());
//        }
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
	JMenu createEditMenu() {
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
	JMenu createOptionsMenu() {
        JMenu optionsMenu = new JMenu(Options.OPTIONS_MENU_NAME);
        optionsMenu.add(getOptions().getItem(Options.PREVIEW_ON_SAVE_OPTION));
        optionsMenu.add(getOptions().getItem(Options.SHOW_VALUE_NODES_OPTION));
        return optionsMenu;
	}

	/**
	 * Creates and returns a properties menu for the menu bar.
	 */
	JMenu createPropertiesMenu() {
        JMenu result = new JMenu(Options.PROPERTIES_MENU_NAME);
	    result.addSeparator();
	    result.add(getEditPropertiesAction());
        return result;
	}

	/**
	 * Creates and returns a display menu for the menu bar.
	 */
	JMenu createDisplayMenu() {
        JMenu displayMenu = new JMenu(Options.DISPLAY_MENU_NAME);
        jgraph.fillOutDisplayMenu(displayMenu.getPopupMenu());
        displayMenu.addSeparator();
        displayMenu.add(getGraphPanel().getViewLabelListItem());
        return displayMenu;
	}

	/**
	 * Creates and returns a help menu for the menu bar.
	 */
	JMenu createHelpMenu() {
		JMenu result = new JMenu(HELP_MENU_NAME);
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
        toolbar.add(getGraphTypeButton());
        toolbar.add(getRuleTypeButton());
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
		if (modeButtonGroup == null) {
			modeButtonGroup = new ButtonGroup();
			modeButtonGroup.add(getSelectModeButton());
			modeButtonGroup.add(getNodeModeButton());
			modeButtonGroup.add(getEdgeModeButton());
		}
		return modeButtonGroup;
	}

	/**
	 * Returns the group of editing mode buttons, lazily creating it first.
	 */
	private ButtonGroup getTypeButtonGroup() {
		if (typeButtonGroup == null) {
			typeButtonGroup = new ButtonGroup();
			typeButtonGroup.add(getGraphTypeButton());
			typeButtonGroup.add(getRuleTypeButton());
		}
		return typeButtonGroup;
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
	 * Returns the button for setting node editing mode, lazily creating it first.
	 */
	JToggleButton getGraphTypeButton() {
		if (graphTypeButton == null) {
			graphTypeButton = new JToggleButton(getSetGraphTypeAction());
			graphTypeButton.setText(null);
            graphTypeButton.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    graphTypeButton.setToolTipText(graphTypeButton.isSelected() ? Options.PREVIEW_ACTION_NAME : Options.SET_GRAPH_ROLE_ACTION_NAME);
                }
            });
            graphTypeButton.doClick();
//			graphEditButton.setToolTipText(Options.GRAPH_MODE_ACTION_NAME);
		}
		return graphTypeButton;
	}

	/**
	 * Returns the button for setting selection mode, lazily creating it first.
	 */
	JToggleButton getRuleTypeButton() {
		if (ruleTypeButton == null) {
			ruleTypeButton = new JToggleButton(getSetRuleTypeAction());
			ruleTypeButton.setText(null);
            ruleTypeButton.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    ruleTypeButton.setToolTipText(ruleTypeButton.isSelected() ? Options.PREVIEW_ACTION_NAME : Options.SET_RULE_ROLE_ACTION_NAME);
                }
            });
//			ruleEditButton.setToolTipText(Options.RULE_MODE_ACTION_NAME);
		}
		return ruleTypeButton;
	}

    /** Creates a panel consisting of the error panel and the status bar. */
    JPanel getStatusPanel() {
        if (statusPanel == null) {
            statusPanel = new JPanel(new BorderLayout());
            statusPanel.add(getErrorPanel());
            statusPanel.add(getStatusBar(), BorderLayout.SOUTH);
        }
        return statusPanel;
    }

    /** Lazily creates and returns the error panel. */
    private ErrorListPanel getErrorPanel() {
        if (errorPanel == null) {
            errorPanel = new ErrorListPanel();
        }
        return errorPanel;
    }
    
    /** Lazily creates and returns the error panel. */
    private JLabel getStatusBar() {
        return statusBar;
    }

	/** 
	 * Callback factory method for a properties dialog for the currently edited model. 
	 */
	private PropertiesDialog createPropertiesDialog(boolean editable) {
		return new PropertiesDialog(getModel().getProperties(), GraphProperties.DEFAULT_KEYS, editable);
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

    /**
     * Activates the appropriate mode button (select, node or edge), based on a given (mode) action.
     * @param forAction the mode action for which the corresponding button is to be activated
     */
    protected void updateTypeButtons(Action forAction) {
        Enumeration<AbstractButton> modeButtonEnum = getTypeButtonGroup().getElements();
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
        getStatusBar().setText(""+elementCount+" visible elements");
    	getErrorPanel().setErrors(toView().getErrors());
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
        new ErrorDialog(getGraphPanel(), message, exc).setVisible(true);
    }

    /** Creates and shows a confirmation dialog for abandoning the currently edited graph. */
    private boolean confirmAbandon() {
        if (isCurrentGraphModified()) {
            int res = JOptionPane.showConfirmDialog(getGraphPanel(),
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

    /** 
     * Creates a preview of an aspect model, with properties.
     * Returns a j-model if the edited model should be replaced, <code>null</code> otherwise.
     */
    private AspectJModel showPreviewDialog(AspectualView<?> view, String okOption) {
    	boolean partial = view.getAspectGraph().hasErrors();
    	AspectJModel previewModel = AspectJModel.newInstance(view, getOptions());
        JGraph jGraph = new JGraph(previewModel);
        jGraph.setToolTipEnabled(true);
        JScrollPane jGraphPane = new JScrollPane(jGraph);
        jGraphPane.setBorder(new BevelBorder(BevelBorder.LOWERED));
        JComponent previewContent = new JPanel();
        previewContent.setLayout(new BorderLayout());
        previewContent.add(jGraphPane);
        if (!previewModel.getProperties().isEmpty()) {
            previewContent.add(createPropertiesDialog(false).createTablePane(), BorderLayout.NORTH);
        }
        if (partial) {
        	JLabel errorLabel = new JLabel(String.format("Incomplete preview due to syntax errors in edited %s", getRole(false)));
        	errorLabel.setForeground(SystemColor.RED);
        	previewContent.add(errorLabel, BorderLayout.SOUTH);
        	if (okOption == null) {
        		okOption = Options.USE_BUTTON;
        	}
        } else if (okOption == null) {
        	okOption = Options.OK_BUTTON;
        }
        JOptionPane previewPane = new JOptionPane(previewContent, JOptionPane.PLAIN_MESSAGE);
        previewPane.setOptions(new String[] { okOption, Options.CANCEL_BUTTON });
        JDialog dialog = previewPane.createDialog(getFrame(), String.format("%s preview", getRole(true)));
        dialog.setSize(PREVIEW_SIZE);
        dialog.setResizable(true);
        dialog.setVisible(true);
        Object response = previewPane.getValue();
        return okOption.equals(response) ? previewModel : null;
    }

    /**
     * Returns the options object associated with the simulator.
     */
    public final Options getOptions() {
    	// lazily creates the options 
    	if (options == null) {
    		options = new Options();
        	options.getItem(Options.SHOW_BACKGROUND_OPTION).setSelected(true);
        	options.getItem(Options.SHOW_REMARKS_OPTION).setSelected(true);
        	options.getItem(Options.PREVIEW_ON_SAVE_OPTION).setSelected(true);
    	}
    	return options;
    }
    
    /**
     * The options object of this simulator.
     */
    private Options options;

    /** The frame of the editor. */
    private final JFrame frame;
    
    /** The jgraph instance used in this editor. */
    private final EditorJGraph jgraph;
//
//    /**
//     * Rule factory used for previewing the graph as a rule.
//     */
//    private RuleFactory ruleFactory;
//
//    /**
//     * Fixed graph type for the editor, or <code>null</code> if the type is not fixed.
//     */
//    private final String fixedType;
//    
//    /** The tool bar of this editor. */
//    private JToolBar editorToolBar;
    
    /** The jgraph panel used in this editor. */
    private JGraphPanel<EditorJGraph> jGraphPanel;
    
    /** Status bar of the editor. */
    private final JLabel statusBar = new JLabel();

    /** Panel containing the error panel and status par. */
    private JPanel statusPanel;
    /** Panel displaying format error messages. */
    private ErrorListPanel errorPanel;
//
//    /** Text area containing error messages. */
//    private JTextArea errorArea;
//    
    /** Indicates whether jgraph has been modified since the last save. */
    private boolean currentGraphModified;
    
    /** Indicates whether jgraph has been modified since the last save. */
    private boolean anyGraphSaved;

    /** Flag indicating if the editor is editing a graph or a rule. */
    private String role;
    
    /** The undo manager of the editor. */
    private transient GraphUndoManager undoManager;

    /** Object providing the core functionality for property changes. */
    private PropertyChangeSupport propertyChangeSupport;
    
    /** Currently edited file. */
    private File currentFile;

    /**
     * The GXL converter used for marshalling and unmarshalling layouted graphs.
     */
    private final Xml<Graph> layoutGxl = new LayedOutXml();

    /**
     * File chooser for graph opening.
     */
    private MyFileChooser graphChooser;

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
    /** Action to switch to graph editing. */
    private Action graphPreviewAction;
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

//    /** Action to close the editor. Only if the editor is auxiliary. */
//    private Action closeAction;
    /** Action to quit the editor. */
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
    /** Button for setting graph editing mode. */
    private transient JToggleButton graphTypeButton;
    /** Button for setting rule editing mode. */
    private transient JToggleButton ruleTypeButton;
    /** Collection of editing mode buttons. */
    private ButtonGroup modeButtonGroup;
    /** Collection of graph editing type buttons. */
    private ButtonGroup typeButtonGroup;
    
	/**
     * @param args empty or a singleton containing a filename of the graph to be edited
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
            editor.getFrame().setVisible(true);
        } catch (IOException exc) {
            System.out.println("Error: " + exc.getMessage());
        }
    }
    
    /** The name of the editor application. */
    public static final String EDITOR_NAME = "Groove Editor";
    /** The name displayed in the frame title for a new graph. */
    public static final String NEW_GRAPH_TITLE = "New Graph";
    /** The name displayed in the frame title for a new rule. */
    public static final String NEW_RULE_TITLE = "New Rule";
    /** The indication displayed in the frame title for a modified graph. */
    public static final String MODIFIED_INDICATOR = "> ";
    /** Size of the preview dialog window. */
    private static final Dimension PREVIEW_SIZE = new Dimension(500, 500);
    /** 
     * Property name of the edit type of the editor.
     * The edit type is the kind of object being edited.
     * Possible values are {@link Groove#GRAPH_ROLE} and {@link Groove#RULE_ROLE}.
     */
    static public final String ROLE_PROPERTY = "type";
//    /** 
//     * Value of the {@link #ROLE_PROPERTY} property, indicating that a graph is being edited.
//     */
//    static public final String GRAPH_TYPE = "graph";
//    /** 
//     * Value of the {@link #ROLE_PROPERTY} property, indicating that a rule is being edited.
//     */
//    static public final String RULE_TYPE = "rule";
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
//
//    /**
//     * An action to close the editor, used if the editor is invoked in the
//     * context of some other frame.
//     */
//    private class CloseEditorAction extends AbstractAction {
//        /** Constructs an instance of the action. */
//        public CloseEditorAction() {
//            super(Options.CLOSE_ACTION_NAME);
//        }
//        
//        /** 
//         * Calls up a preview if the edited graph has syntax errors, then
//         * hides and disposes the frame.
//         */
//        public void actionPerformed(ActionEvent e) {
//        	if (! createAspectGraph().hasErrors() || handlePreview(null)) {
//    			getRootComponent().setVisible(false);
//    			getFrame().dispose();
//        	}
//		}
//    }

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
            PropertiesDialog dialog = createPropertiesDialog(true);
            if (dialog.showDialog(getFrame())) {
                getModel().setProperties(new GraphProperties(dialog.getProperties()));
                currentGraphModified = true;
                updateTitle();
            }
        }
    }

    /**
     * Action to export the current state of the editor to an image file.
     */
    private class ExportGraphAction extends AbstractAction {
        /** Constructs an instance of the action. */
        protected ExportGraphAction() {
            super(Options.EXPORT_ACTION_NAME);
            putValue(ACCELERATOR_KEY, Options.EXPORT_KEY);
        }

        /** (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent evt) {
			if (getModelName() != null) {
                exporter.getFileChooser().setSelectedFile(new File(getModelName()));
			}
			File toFile = ExtensionFilter.showSaveDialog(exporter.getFileChooser(), getFrame());
			if (toFile != null) {
				try {
					exporter.export(jgraph, toFile);
				} catch (IOException exc) {
					showErrorDialog("Error while saving to " + toFile, exc);
				}
			}
        }
//
//        /**
//         * Exports the currently edited model, including hidden and emphasis, to an image file.
//         * @param filter the filter that determines the format to export to
//         * @param toFile the file to save to
//         * @throws IOException if <tt>fromFile</tt> did not contain a correctly formatted graph
//         */
//        private void doExportGraph(ExtensionFilter filter, File toFile) throws IOException {
//            if (filter == fsmFilter) {
//                PrintWriter writer = new PrintWriter(new FileWriter(toFile));
//                Converter.graphToFsm(getPlainGraph(), writer);
//                writer.close();
//            } else {
//                String formatName = filter.getExtension().substring(1);
//                Iterator<ImageWriter> writerIter = ImageIO.getImageWritersBySuffix(formatName);
//                if (writerIter.hasNext()) {
//                    ImageIO.write(jgraph.toImage(), formatName, toFile);
//                } else {
//                    showErrorDialog("No image writer found for " + filter.getDescription(), null);
//                }
//            }
//        }
//        
        private final Exporter exporter = new Exporter();
//
//    	/**
//    	 * Returns a file chooser for exporting graphs, after lazily creating it.
//    	 */
//    	private JFileChooser getExportChooser() {
//    		if (exportChooser == null) {
//    			exportChooser = new GrooveFileChooser();
//    			exportChooser.setAcceptAllFileFilterUsed(false);
//    			exportChooser.addChoosableFileFilter(fsmFilter);
//    			exportChooser.addChoosableFileFilter(jpgFilter);
//    			exportChooser.addChoosableFileFilter(pngFilter);
//    			exportChooser.addChoosableFileFilter(epsFilter);
//    			exportChooser.setFileFilter(pngFilter);
//    			exportChooser.setCurrentDirectory(new File(Groove.WORKING_DIR));
//    		}
//    		return exportChooser;
//    	}
//
//        /**
//         * File chooser for export actions.
//         */
//        private JFileChooser exportChooser;
//
//        /**
//         * Extension filter used for exporting the graph in fsm format.
//         */
//        private final ExtensionFilter fsmFilter = Groove.createFsmFilter();
//
//        /**
//         * Extension filter used for exporting the graph in jpeg format.
//         */
//        private final ExtensionFilter jpgFilter = new ExtensionFilter("JPEG image files",
//                Groove.JPG_EXTENSION);
//
//        /**
//         * Extension filter used for exporting the graph in PNG format.
//         */
//        private final ExtensionFilter pngFilter = new ExtensionFilter("PNG files",
//                Groove.PNG_EXTENSION);
//
//        /**
//         * Extension filter used for exporting the graph in EPS format.
//         */
//        private final ExtensionFilter epsFilter = new ExtensionFilter("EPS files",
//                Groove.EPS_EXTENSION);
    }


    /**
	 * Action to start with a blank graph.
	 */
    private class NewGraphAction extends ToolbarAction {
        /** Constructs an instance of the action. */
        NewGraphAction() {
            super(Options.NEW_ACTION_NAME, Options.NEW_KEY, new ImageIcon(Groove.getResource("new.gif")));
        }
    
        @Override
        public void actionPerformed(ActionEvent evt) {
            super.actionPerformed(evt);
            if (confirmAbandon()) {
                currentFile = null;
                setPlainGraph(null);
            }
        }
    }

    /**
     * Action to open a graph file into the editor.
     */
    private class OpenGraphAction extends ToolbarAction {
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
     * Action for quitting the editor.
     * Calls {@link Editor#handleQuit()} to execute the action.
     */
    private class QuitAction extends AbstractAction {
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
     * Action to save the current state of the editor into a file.
     */
    private class SaveGraphAction extends ToolbarAction {
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
     * Action to set the editing mode (selection, node or edge).
     */
    private class SetEditingModeAction extends ToolbarAction {
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
    private class SetGraphRoleAction extends ToolbarAction {
        /** Constructs an instance of the action. */
        protected SetGraphRoleAction() {
            super(Options.SET_GRAPH_ROLE_ACTION_NAME, null, Groove.GRAPH_MODE_ICON);
        }
    
        /** (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(ActionEvent evt) {
            super.actionPerformed(evt);
            if (!setRole(Groove.GRAPH_ROLE)) {
                // only do a preview if the type was not changed (on the second click)
                handlePreview(null);
            }
        }
    }

    /**
     * Action to preview the current jgraph as a transformation rule.
     */
    private class SetRuleRoleAction extends ToolbarAction {
        /** Constructs an instance of the action. */
        protected SetRuleRoleAction() {
            super(Options.SET_RULE_ROLE_ACTION_NAME, null, Groove.RULE_MODE_ICON);
        }
    
        /** (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(ActionEvent evt) {
            super.actionPerformed(evt);
            if (!setRole(Groove.RULE_ROLE)) {
                // only do a preview if the type was not changed (on the second click)
                handlePreview(null);
            }
        }
    }

    /** This will change the source of the actionevent to graph. */
    private class TransferAction extends ToolbarAction {
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
     * @version $Revision: 1.32 $
     */
    private abstract class ToolbarAction extends AbstractAction {
        /** Constructs an action with a given name, key and icon. */
        ToolbarAction(String name, KeyStroke acceleratorKey, Icon icon) {
            super(name, icon);
            putValue(Action.SHORT_DESCRIPTION, name);
            putValue(ACCELERATOR_KEY, acceleratorKey);
            getGraphPanel().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                    .put(acceleratorKey, name);
            jgraph.getInputMap().put(acceleratorKey, name);
            getGraphPanel().getActionMap().put(name, this);
        }
    
        public void actionPerformed(ActionEvent evt) {
            jgraph.stopEditing();
        }
    }
    
    /** File chooser taking the distinction between graphs and rules into account. */
    private class MyFileChooser extends GrooveFileChooser {        
        @Override
        public int showOpenDialog(Component parent) throws HeadlessException {
            resetChoosableFileFilters();
            setAcceptAllFileFilterUsed(true);
            addChoosableFileFilter(getGraphFilter());
            setAccessory(null);
            setCurrentDirectory(getCurrentDir());
            setSelectedFile(null);
            int result = super.showOpenDialog(parent);
            return result;
        }

        @Override
        public int showSaveDialog(Component parent) throws HeadlessException {
            resetChoosableFileFilters();
            setAcceptAllFileFilterUsed(false);
            setFilters(hasGraphRole());
            setAccessory(getRolePanel());
            setCurrentDirectory(getCurrentDir());
            setSelectedFile(getSaveFile());
            // get the file to write to
            listenToFilterChanges = true;
            int result = super.showSaveDialog(parent);
            lastSaveFilter = getFileFilter();
            listenToFilterChanges = false;
            if (result == JFileChooser.APPROVE_OPTION) {
                if (getPreviewCheckBox().isSelected() || toAspectGraph().hasErrors()) {
                	boolean previewOK = handlePreview("Save");
                	if (! previewOK) {
                        result = JFileChooser.CANCEL_OPTION;
                	} else if (toAspectGraph().hasErrors()) {
                		JOptionPane.showMessageDialog(getFrame(), String.format("%s contains syntax errors; not saved", getRole(true)));
                        result = JFileChooser.CANCEL_OPTION;
                	}
                }
            }
            return result;
        }

		/**
		 * Returns the name of the file to be saved.
		 * This is derived from the model name.
		 */
		private File getSaveFile() {
            String graphName = getModelName();
			return graphName == null ? null : new File(graphName);
		}
 
        @Override
		public void setFileFilter(FileFilter filter) {
			super.setFileFilter(filter);
			setSelectedFile(getSaveFile());
			if (listenToFilterChanges) {
				if (isRuleFilter(filter)) {
					setRole(Groove.RULE_ROLE);
				} else if (isStateFilter(filter)) {
					setRole(Groove.GRAPH_ROLE);
				}
			}
		}

		/**
         * Sets the file filters to either those that accept graphs, or rules.
         */
        private void setFilters(boolean graphRole) {
            resetChoosableFileFilters();
            FileFilter defaultFilter = graphRole == isStateFilter(lastSaveFilter) ? lastSaveFilter : null;
            for (FileFilter filter: new FileFilter[] { getStateFilter(), getRuleFilter(), getGxlFilter()} ) {
                addChoosableFileFilter(filter);
                boolean suitable = graphRole ? !isRuleFilter(filter) : !isStateFilter(filter);
                if (suitable && defaultFilter == null) {
                	defaultFilter = filter;
                }
            }
            setFileFilter(defaultFilter);
        }
        
        /** Determines if a given file filter is dedicated to graph states. */
        private boolean isStateFilter(FileFilter filter) {
            return filter == getStateFilter();
        }
        
        /** Determines if a given file filter is dedicated to rules. */
        private boolean isRuleFilter(FileFilter filter) {
            return filter == getRuleFilter();
        }

        private JPanel getRolePanel() {
            if (rolePanel == null) {
                JPanel innerPanel = new JPanel(new BorderLayout());
                innerPanel.setBorder(new javax.swing.border.EmptyBorder(0, 5, 0, 0));
                innerPanel.add(getPreviewCheckBox(), BorderLayout.SOUTH);
                rolePanel = new JPanel(new BorderLayout());
                rolePanel.add(innerPanel, BorderLayout.SOUTH);
            }
            return rolePanel;
        }

        /**
         * Returns a checkbox forcing a preview dialog before saving a rule.
         */
        private JCheckBox getPreviewCheckBox() {
            if (previewCheckBox == null) {
                previewCheckBox = new JCheckBox();
                previewCheckBox.setText("Preview");
                previewCheckBox.setSelected(true);
            }
            return previewCheckBox;
        }
        
        /** Retrieves the directory file from the #currentFile */
        private File getCurrentDir() {
            return currentFile == null ? new File(Groove.WORKING_DIR) : currentFile.getAbsoluteFile().getParentFile();
        }
        
        /** Lazily creates and returns the state filter. */
        private ExtensionFilter getStateFilter() {
        	if (stateFilter == null) {
        		stateFilter = Groove.createStateFilter();
        	}
        	return stateFilter;
        }
        
        /** Lazily creates and returns the state filter. */
        private ExtensionFilter getRuleFilter() {
        	if (ruleFilter == null) {
        		ruleFilter = Groove.createRuleFilter();
        	}
        	return ruleFilter;
        }

        /** Lazily creates and returns the state filter. */
        private ExtensionFilter getGxlFilter() {
        	if (gxlFilter == null) {
        		gxlFilter = Groove.createGxlFilter();
        	}
        	return gxlFilter;
        }

        /** Lazily creates and returns the graph filter. */
        private ExtensionFilter getGraphFilter() {
        	if (graphFilter == null) {
        		graphFilter = new ExtensionFilter("Graph files", "") {
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
        	}
        	return graphFilter;
        }
        
        /** The auxiliary component used in the save dialog. */
        private JPanel rolePanel;
        /** Last file filter used in a save dialog. */
        private FileFilter lastSaveFilter;
//        /** Combo box to choose between graph and rule edit type. */
//        private JComboBox typeComboBox;
        /** Checkbox to indicate that saving rules should be preceded by a preview. */
        private JCheckBox previewCheckBox;

        /** Flag indicating that filter changes may change the edit type. */
        private boolean listenToFilterChanges;
        /**
         * Extension filter for state files.
         */
        private ExtensionFilter stateFilter;

        /**
         * Extension filter for rule files.
         */
        private ExtensionFilter ruleFilter;

        /**
         * Extension filter used for exporting the LTS in jpeg format.
         */
        private ExtensionFilter gxlFilter;
        
        /**
         * Extension filter for all known kinds of graph files.
         */
        private ExtensionFilter graphFilter;
    }
}