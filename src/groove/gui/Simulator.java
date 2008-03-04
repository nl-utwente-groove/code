/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0 
 *
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on an 
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific 
 * language governing permissions and limitations under the License.
 * 
 * $Id: Simulator.java,v 1.89 2008-03-04 22:03:35 rensink Exp $
 */
package groove.gui;

import static groove.gui.Options.DELETE_RULE_OPTION;
import static groove.gui.Options.HELP_MENU_NAME;
import static groove.gui.Options.OPTIONS_MENU_NAME;
import static groove.gui.Options.REPLACE_RULE_OPTION;
import static groove.gui.Options.REPLACE_START_GRAPH_OPTION;
import static groove.gui.Options.SHOW_ANCHORS_OPTION;
import static groove.gui.Options.SHOW_ASPECTS_OPTION;
import static groove.gui.Options.SHOW_BACKGROUND_OPTION;
import static groove.gui.Options.SHOW_NODE_IDS_OPTION;
import static groove.gui.Options.SHOW_REMARKS_OPTION;
import static groove.gui.Options.SHOW_STATE_IDS_OPTION;
import static groove.gui.Options.SHOW_VALUE_NODES_OPTION;
import static groove.gui.Options.START_SIMULATION_OPTION;
import static groove.gui.Options.STOP_SIMULATION_OPTION;
import groove.abs.AbstrSimulationProperties;
import groove.abs.Abstraction;
import groove.abs.lts.AGTS;
import groove.abs.lts.AbstrStateGenerator;
import groove.control.ControlView;
import groove.explore.ScenarioHandler;
import groove.explore.strategy.ExploreStateStrategy;
import groove.explore.util.ExploreCache;
import groove.graph.Graph;
import groove.graph.GraphAdapter;
import groove.graph.GraphFactory;
import groove.graph.GraphInfo;
import groove.graph.GraphListener;
import groove.graph.GraphProperties;
import groove.graph.GraphShape;
import groove.graph.Node;
import groove.gui.jgraph.GraphJModel;
import groove.gui.jgraph.JCell;
import groove.gui.jgraph.JGraph;
import groove.gui.jgraph.LTSJGraph;
import groove.gui.jgraph.LTSJModel;
import groove.io.AspectGxl;
import groove.io.AspectualViewGps;
import groove.io.ExtensionFilter;
import groove.io.GrammarViewXml;
import groove.io.GrooveFileChooser;
import groove.io.LayedOutGps;
import groove.io.LayedOutXml;
import groove.io.Xml;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.State;
import groove.lts.StateGenerator;
import groove.trans.NameLabel;
import groove.trans.RuleMatch;
import groove.trans.RuleNameLabel;
import groove.trans.SystemProperties;
import groove.util.Groove;
import groove.util.GrooveModules;
import groove.verify.CTLFormula;
import groove.verify.CTLModelChecker;
import groove.verify.TemporalFormula;
import groove.view.AspectualGraphView;
import groove.view.AspectualRuleView;
import groove.view.DefaultGrammarView;
import groove.view.FormatException;
import groove.view.aspect.AspectGraph;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;
import java.util.Set;
import java.util.Stack;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
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
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

/**
 * Program that applies a production system to an initial graph.
 * @author Arend Rensink
 * @version $Revision: 1.89 $
 */
public class Simulator {
    /**
     * Constructs a simulator with an empty graph grammar.
     */
    public Simulator() {
    	loadModules();
        initGrammarLoaders();
        getFrame();
    }

    /**
     * Constructs a simulator using all production rules in a given directory.
     * All known graph grammar format
     * loaders are polled to find one that can load the grammar.
     * @param grammarLocation the location (file or directory) containing the grammar; if <tt>null</tt>, no grammar is loaded.
     */
    public Simulator(String grammarLocation) throws IOException {
        this(grammarLocation, null);
    }

    /**
     * Constructs a simulator using the grammar in a given location and a given graph as
     * start state. All known graph grammar format
     * loaders are polled to find one that can load the grammar.
     * @param grammarLocation the location (file or directory) containing the grammar; if <tt>null</tt>, no grammar is loaded.
     * @param startGraphName the file containing the start state; if <tt>null</tt>, the default start state is chosen.
     */
    public Simulator(final String grammarLocation, final String startGraphName) throws IOException {
        this();
        if (grammarLocation != null) {
            final File location = new File(Groove.createRuleSystemFilter().addExtension(grammarLocation)).getAbsoluteFile();
            AspectualViewGps grammarLoader = null;
            for (Map.Entry<ExtensionFilter,AspectualViewGps> loaderEntry: grammarLoaderMap.entrySet()) {
                ExtensionFilter filter = loaderEntry.getKey();
                if (filter.accept(location)) {
                    grammarLoader = loaderEntry.getValue();
                }
            }
            if (grammarLoader == null) {
                throw new IOException("Cannot load grammar "+location);
            } else {
            	// load the grammar, but on the event dispatch thread so we don't get
            	// concurrency issues
            	final AspectualViewGps loader = grammarLoader;
            	SwingUtilities.invokeLater(new Runnable() {
					public void run() {
		                doLoadGrammar(loader, location, startGraphName);                
					}            		
            	});
            }
        }
    }

    /** Starts the simulator, by calling {@link JFrame#pack()} and {@link JFrame#setVisible(boolean)}. */
    public void start() {
        getFrame().pack();
        getFrame().setVisible(true);
    }

    /**
     * Load the different modules.
     */
    private void loadModules() {
    	loadLTLModule();
    }

    /**
     * LTL verification is only supported on a selection of platforms.
     */
    private void loadLTLModule() {
        if (System.getProperty("os.name").startsWith("Windows") ||
        	System.getProperty("os.name").startsWith("Linux") ||
        	System.getProperty("os.name").startsWith("FreeBSD")) {
        	System.setProperty(GrooveModules.GROOVE_MODULE_LTL_VERIFICATION, GrooveModules.GROOVE_MODULE_ENABLED);
        } else {
        	System.setProperty(GrooveModules.GROOVE_MODULE_LTL_VERIFICATION, GrooveModules.GROOVE_MODULE_DISABLED);
        }
    }

    /**
     * Returns the currently loaded graph grammar, or <tt>null</tt> if none is loaded.
     */
    public DefaultGrammarView getCurrentGrammar() {
        return currentGrammar;
    }

    /** 
     * Sets the {@link #currentGrammar} and {@link #currentRule} fields. 
     * @return <code>true</code> if the new grammar is different from the previous
     */
    public boolean setCurrentGrammar(DefaultGrammarView grammar) {
    	boolean result = currentGrammar != grammar;
		this.currentGrammar = grammar;
		if (currentRule != null && grammar.getRule(currentRule.getNameLabel()) == null) {
			this.currentRule = null;
		}
		return result;
    }

    /**
     * Returns the currently set GTS, or <tt>null</tt> if none is set.
     */
    public GTS getCurrentGTS() {
        return currentGTS;
    }
    
    /**
     * Sets the current GTS to a given GTS, possibly <code>null</code>.
     * If the new GTS is not <code>null</code>, also sets the current state to
     * the GTS' start state.
     * In any case, sets the current transition to <code>null</code>.
     * @return <code>true</code> if the new GTS is different from the previous
     */
    private boolean setCurrentGTS(GTS gts) {
    	boolean result = currentGTS == gts;
    	currentGTS = gts;
        currentTransition = null;
        currentMatch = null;
    	currentState = gts == null ? null : gts.startState();
    	if (gts != null) {
    	    getGenerator().setGTS(gts);
    	}
    	return result;
    }

    /**
     * Returns the currently selected state, or <tt>null</tt> if none is selected. The selected
     * state is the one displayed in the state panel.
     * @see StatePanel#getJModel()
     */
    public GraphState getCurrentState() {
        return currentState;
    }

    /**
     * Sets the current state field to a given state, and the current
     * transition field to <code>null</code>.
     * @return <code>true</code> if the new state is different from the previous
     */
    private boolean setCurrentState(GraphState state) {
    	boolean result = currentState != state;
    	currentState = state;
    	currentTransition = null;
    	currentMatch = null;
    	return result;
    }

    /**
     * Returns the currently selected transition, or <tt>null</tt> if none is selected. The
     * selected state is the one selected in the rule tree and emphasized in the state panel.
     */
    public GraphTransition getCurrentTransition() {
        return currentTransition;
    }

    /** Returns the currently selected match */
    public RuleMatch getCurrentMatch() {
    	return currentMatch;
    }
    
    /** 
     * Sets the currently selected match. 
     */
    protected boolean setCurrentMatch(RuleMatch match) {
    	boolean result = currentMatch != match;
    	currentMatch = match;
    	return result;
    }
    
    
    /**
     * Sets the currently selected transition to a given value (possibly <code>null</code>).
     * If the new transition is not <code>null</code>, also sets the 
     * current state to the new transition's source state.
     * @return <code>true</code> if the new transition is different from the previous
     */
    private boolean setCurrentTransition(GraphTransition transition) {
    	boolean result = currentTransition != transition;
    	currentTransition = transition;
    	if (transition != null) {
    		currentState = transition.source();
    	}
		return result;
    }

    /**
     * Returns the currently selected rule, or <tt>null</tt> if none is selected. The selected
     * rule is the one displayed in the rule panel.
     */
    public AspectualRuleView getCurrentRule() {
        return currentRule;
    }

    /**
     * Sets the currently selected rule to a given value (possibly <code>null</code>).
     * @return <code>true</code> if the new rule is different from the previous
     */
    public boolean setCurrentRule(AspectualRuleView rule) {
    	boolean result = currentRule != rule;
    	currentRule = rule;
    	return result;
    }

    /**
     * Returns the transition application action permanently associated with this simulator.
     */
    public ApplyTransitionAction getApplyTransitionAction() {
    	if (applyTransitionAction == null) {
    		applyTransitionAction = new ApplyTransitionAction();
    	}
        return applyTransitionAction;
    }

    /** Returns the edit action permanently associated with this simulator. */
	public CopyRuleAction getCopyRuleAction() {
		// lazily create the action
		if (copyRuleAction == null) {
			copyRuleAction = new CopyRuleAction();
		}
	    return copyRuleAction;
	}

    /** Returns the rule deletion action permanently associated with this simulator. */
	public DeleteRuleAction getDeleteRuleAction() {
		// lazily create the action
		if (deleteRuleAction == null) {
			deleteRuleAction = new DeleteRuleAction();
		}
	    return deleteRuleAction;
	}

	/** Lazily creates and returns the graph edit action permanently associated with this simulator. */
	public EditGraphAction getEditGraphAction() {
		// lazily create the action
		if (editGraphAction == null) {
			editGraphAction = new EditGraphAction();
		}
	    return editGraphAction;
	}

    /** Lazily creates and returns the rule edit action permanently associated with this simulator. */
	public EditRuleAction getEditRuleAction() {
		// lazily create the action
		if (editRuleAction == null) {
			editRuleAction = new EditRuleAction();
		}
	    return editRuleAction;
	}

    /** Returns the properties edit action permanently associated with this simulator. */
	public EditRulePropertiesAction getEditRulePropertiesAction() {
		// lazily create the action
		if (editRulePropertiesAction == null) {
			editRulePropertiesAction = new EditRulePropertiesAction();
		}
	    return editRulePropertiesAction;
	}

    /** Returns the action to show the system properties of the current grammar. */
	public Action getEditSystemPropertiesAction() {
		// lazily create the action
		if (editSystemPropertiesAction == null) {
			editSystemPropertiesAction = new EditSystemPropertiesAction();
		}
	    return editSystemPropertiesAction;
	}

	/** Returns the rule enabling action permanently associated with this simulator. */
	public EnableRuleAction getEnableRuleAction() {
		// lazily create the action
		if (enableRuleAction == null) {
			enableRuleAction = new EnableRuleAction();
		}
	    return enableRuleAction;
	}

	/** Returns the graph export action permanently associated with this simulator. */
	public ExportGraphAction getExportGraphAction() {
		// lazily create the action
		if (exportGraphAction == null) {
			exportGraphAction = new ExportGraphAction(); 
		}
	    return exportGraphAction;
	}

	/**
     * Returns the go-to start state action permanently associated with this simulator.
     */
    public GotoStartStateAction getGotoStartStateAction() {
    	// lazily create the action
    	if (gotoStartStateAction == null) {
    		gotoStartStateAction = new GotoStartStateAction();
    	}
        return gotoStartStateAction;
    }

    /** Returns the start graph load action permanently associated with this simulator. */
    public LoadStartGraphAction getLoadStartGraphAction() {
    	// lazily create the action
    	if (loadStartGraphAction == null) {
    		loadStartGraphAction = new LoadStartGraphAction();
    	}
        return loadStartGraphAction;
    }

    /** Returns the grammar load action permanently associated with this simulator. */
    public Action getLoadGrammarAction() {
    	// lazily create the action
    	if (loadGrammarAction == null) {
    		loadGrammarAction = new LoadGrammarAction();
    	}
        return loadGrammarAction;
    }

    /** Returns the rule system creation action permanently associated with this simulator. */
    public NewGrammarAction getNewGrammarAction() {
        // lazily create the action
        if (newGrammarAction == null) {
            newGrammarAction = new NewGrammarAction();
        }
        return newGrammarAction;
    }

    /** Returns the graph creation action permanently associated with this simulator. */
	public NewGraphAction getNewGraphAction() {
		// lazily create the action
		if (newGraphAction == null) {
			newGraphAction = new NewGraphAction();
		}
	    return newGraphAction;
	}

    /** Returns the rule creation action permanently associated with this simulator. */
	public NewRuleAction getNewRuleAction() {
		// lazily create the action
		if (newRuleAction == null) {
			newRuleAction = new NewRuleAction();
		}
	    return newRuleAction;
	}

	/** Returns the quit action permanently associated with this simulator. */
	public Action getQuitAction() {
		// lazily create the action
		if (quitAction == null) {
			quitAction = new QuitAction();
		}
	    return quitAction;
	}

	/**
	 * Returns the ctl formula providing action permanently associated with this simulator.
	 */
	public Action getProvideTemporalFormulaAction() {
		if (provideTemporalFormulaAction == null) {
			provideTemporalFormulaAction = new ProvideTemporalFormulaAction();
		}
		return provideTemporalFormulaAction;
	}

	/** Returns the redo action permanently associated with this simulator. */
	public Action getRedoAction() {
	    if (redoAction == null) {
	        redoAction = getUndoHistory().getRedoAction();
	        addAccelerator(redoAction);
	    }
	    return redoAction;
	}

	/** Returns the grammar refresh action permanently associated with this simulator. */
    public RefreshGrammarAction getRefreshGrammarAction() {
    	// lazily create the action
    	if (refreshGrammarAction == null) {
    		refreshGrammarAction = new RefreshGrammarAction();
    	}
        return refreshGrammarAction;
    }

    /** Returns the rule renaming action permanently associated with this simulator. */
	public RenameRuleAction getRenameRuleAction() {
		// lazily create the action
		if (renameRuleAction == null) {
			renameRuleAction = new RenameRuleAction();
		}
	    return renameRuleAction;
	}

	/** Lazily creates and returns an instance of {@link Simulator.StartSimulationAction}. */
	public Action getStartSimulationAction() {
		// lazily create the action
		if (startSimulationAction == null) {
			startSimulationAction = new StartSimulationAction();
		}
	    return startSimulationAction;    	
	}

	/** A variant of {@link #getStartSimulationAction()} for abstract simulation. */
	public Action getStartAbstrSimulationAction() {
		// lazily create the action
		if (startAbstrSimulationAction == null) {
			startAbstrSimulationAction = new StartAbstrSimulationAction();
		}
	    return startAbstrSimulationAction;    	
	}
	
	/** Returns the graph save action permanently associated with this simulator. */
    public SaveGraphAction getSaveGraphAction() {
    	// lazily create the action
    	if (saveGraphAction == null) {
    		saveGraphAction = new SaveGraphAction();
    	}
        return saveGraphAction;
    }

	/** Returns the graph save action permanently associated with this simulator. */
    public SaveGrammarAction getSaveGrammarAction() {
    	// lazily create the action
    	if (saveGrammarAction == null) {
    		saveGrammarAction = new SaveGrammarAction();
    	}
        return saveGrammarAction;
    }

    /** Returns the undo action permanently associated with this simulator. */
    public Action getUndoAction() {
        if (undoAction == null) {
            undoAction = getUndoHistory().getUndoAction();
            addAccelerator(undoAction);
        }
        return undoAction;
    }

	/**
     * Returns the go-to start state action permanently associated with this simulator.
     * HARMEN: temporary method for showing model checking results
     */
    public ShowResultAction showResultAction() {
    	// lazily create the action
    	if (showResultAction == null) {
    		showResultAction = new ShowResultAction();
    	}
        return showResultAction;
    }
   
    /** Returns (after lazily creating) the undo history for this simulator. */
    protected UndoHistory getUndoHistory() {
    	if (undoHistory == null) {
    		undoHistory = new UndoHistory(this);
    	}
    	return undoHistory;
    }

    /**
     * Handles the execution of a {@link SaveGraphAction}. 
     * Calls {@link #doSaveGraph(Graph, File)} for the actual saving.
     * @param state <tt>true</tt> if it is a state that has to be saved (otherwise it is an LTS)
     * @param graph the j-model from which the graph is to be obtained
     * @param proposedName the proposed name for the graph, to be filled into the dialog
     * @return the file to which the graph has been saved; <tt>null</tt> if the graph has not been
     *         saved
     */
    File handleSaveGraph(boolean state, Graph graph, String proposedName) {
        getStateFileChooser().setFileFilter(state ? stateFilter : gxlFilter);
        getStateFileChooser().setSelectedFile(new File(proposedName));
        File selectedFile = ExtensionFilter.showSaveDialog(getStateFileChooser(), getFrame(), null);
        // now save, if so required
        if (selectedFile != null) {
            doSaveGraph(graph, selectedFile);
        }
        return selectedFile;
    }
    
    File handleSaveControl(String controlProgram) {
    	// check if we had a control program
    	File selectedFile = getCurrentGrammar().getControl().getFile();
    	if( selectedFile == null ) {
    		// need to implement a filechooser here.
    	}

    	if( selectedFile != null ) {
    		doSaveControl(controlProgram, selectedFile);
    	}
    	
    	return selectedFile;
    }
//    
//    /**
//     * Creates and displays a modal dialog wrapping an editor.
//     * @param graph the input graph for the editor
//     * @return the dialog object, which can be queried as to the result of editing
//     */
//    EditorDialog showEditorDialog(Graph graph) {
//        final EditorDialog result = new EditorDialog(getFrame(), getOptions(), graph);
//        result.setVisible(true);
////        new Thread() {
////            @Override
////            public void run() {
////                result.setVisible(true);
////            }
////        }.start();
////        synchronized(getFrame()) {
////            try {
////                getFrame().wait();
////            } catch (InterruptedException e) {
////                // empty
////            }
////        }
//        return result;
//    }

    /** Inverts the enabledness of the current rule, and stores the result. */
    void doEnableRule() {
    	AspectGraph ruleGraph = getCurrentRule().getAspectGraph();
    	GraphProperties properties = GraphInfo.getProperties(ruleGraph, true);
    	properties.setEnabled(!properties.isEnabled());
    	doAddRule(getCurrentRule().getNameLabel(), ruleGraph);
	}
    

	/**
     * Loads in a grammar from a given grammar and start state file and using a given loader. File
     * and loader may not be <tt>null</tt>; if the start state file is <tt>null</tt>, the
     * default start state name is used. Sets the current grammar and start state files and loader
     * to the given parameters.
     * @param grammarLoader the loader to be used
     * @param grammarFile the grammar file to be used
     * @param startStateName the name of the start state; if <tt>null</tt>, the default
     *        start state name is used
     * @see GrammarViewXml#DEFAULT_START_GRAPH_NAME
     */
    void doLoadGrammar(final AspectualViewGps grammarLoader, final File grammarFile, final String startStateName) {
        final ProgressBarDialog dialog = new ProgressBarDialog(getFrame(), "Load Progress");
        final Observer loadListener = new Observer() {
            public void update(Observable o, Object arg) {
                if (arg instanceof String) {
                    dialog.setMessage((String) arg);
                } else if (arg instanceof Integer) {
                    if (size == 0) {
                        size = ((Integer) arg) + 1;
                        dialog.setRange(0, size);
                    }
                } else {
                    if (size > 0) {
                        dialog.incProgress();
                    }
                }
            }
            
            private int size;
        };
        dialog.activate(1000);
        grammarLoader.addObserver(loadListener);
        new Thread() {
            @Override
            public void run() {
                try {
                    final DefaultGrammarView grammar = grammarLoader.unmarshal(grammarFile, startStateName);
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            setGrammar(grammar);
                        }
                    });
                    // now we know loading succeeded, we can set the current names & files
                    currentGrammarFile = grammarFile;
                    currentGrammarLoader = grammarLoader;
                    if (grammar.getStartGraph() != null) {
                        File startFile = new File(grammarFile, grammar.getStartGraph().getName());
                        getStateFileChooser().setSelectedFile(startFile);
                    }
                    getGrammarFileChooser().setSelectedFile(grammarFile);
                } catch (final IOException exc) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            showErrorDialog(exc.getMessage(), exc.getCause());
                        }
                    });
                }
                dialog.deactivate();
                grammarLoader.deleteObserver(loadListener);
            }
        }.start();
    }

	/**
     * Saves the current grammar to a given file.
     * @param grammarLoader the loader to be used
     * @param grammarFile the grammar file to be used
     */
    void doSaveGrammar(AspectualViewGps grammarLoader, File grammarFile) {
        try {
        	grammarLoader.marshal(getCurrentGrammar(), grammarFile);
        	String grammarName = currentGrammarLoader.getExtensionFilter().stripExtension(grammarFile.getName());
        	getCurrentGrammar().setName(grammarName);
        	setTitle();
            // now we know saving succeeded, we can set the current names & files
            currentGrammarFile = grammarFile;
            currentGrammarLoader = grammarLoader;
        	getStateFileChooser().setCurrentDirectory(currentGrammarFile);
        	AspectualGraphView startGraph = getCurrentGrammar().getStartGraph();
            if (startGraph != null) {
            	getStateFileChooser().setSelectedFile(new File(startGraph.getName()));
            } else {
            	getStateFileChooser().setSelectedFile(new File(""));
            }
            getGrammarFileChooser().setSelectedFile(grammarFile);
        } catch (IOException exc) {
            showErrorDialog("Error while saving grammar to " + grammarFile, exc);
        } 
    }

	/**
     * Sets the contents of a given file as start state. This results in a reset of the LTS.
     */
    void doLoadStartGraph(File file) {
    	try {
            AspectGraph aspectStartGraph = graphLoader.unmarshalGraph(file);
            AspectualGraphView startGraph = new AspectualGraphView(aspectStartGraph);
            getCurrentGrammar().setStartGraph(startGraph);
            setGrammar(getCurrentGrammar());
        } catch (IOException exc) {
            showErrorDialog("Could not load start graph from " + file.getName(), exc);
        }
    }
    
    /**
	 * Creates an empty grammar and an empty directory, and sets it in the
	 * simulator.
	 * 
	 * @param grammarLoader
	 *            the loader to be used
	 * @param grammarFile
	 *            the grammar file to be used
	 */
	void doNewGrammar(AspectualViewGps grammarLoader, File grammarFile) {
		grammarFile.mkdir();
		String grammarName = grammarLoader.getExtensionFilter().stripExtension(grammarFile.getName());
		DefaultGrammarView grammar = new DefaultGrammarView(grammarName);
		setGrammar(grammar);
		// now we know loading succeeded, we can set the current names & files
		currentGrammarFile = grammarFile;
		currentGrammarLoader = grammarLoader;
		getStateFileChooser().setCurrentDirectory(grammarFile);
		getStateFileChooser().setSelectedFile(new File(""));
		getGrammarFileChooser().setSelectedFile(grammarFile);
	}

	RuleNameLabel generateNewRuleName(String basis) {
    	RuleNameLabel result = new RuleNameLabel(basis);
    	Set<RuleNameLabel> existingNames = getCurrentGrammar().getRuleMap().keySet();
    	for (int i = 1; existingNames.contains(result); i++) {
    		result = new RuleNameLabel(basis+i);
    	}
    	return result;
    }
    
    /**
     * Ends the program.
     */
    void doQuit() {
        if (confirmAbandon(false)) {
            if (REPORT) {
                try {
                    BufferedReader systemIn = new BufferedReader(new InputStreamReader(System.in));
                    System.out.print("Log file? ");
                    String filename = systemIn.readLine();
                    if (filename.length() != 0) {
                        groove.util.Reporter.report(new PrintWriter(new FileWriter(filename
                                + ".log", true), true));
                    }
                } catch (IOException exc) {
                    System.out.println(exc.getMessage());
                }
                groove.util.Reporter.report(new PrintWriter(System.out));
            }
            getFrame().dispose();
        }
    }

    /**
	 * Saves an aspect graph as a rule under a given name, and puts the rule into the
	 * current grammar view. 
     * @param ruleName the name of the new rule
     * @param ruleAsGraph the new rule, given as an aspect graph
	 */
	void doAddRule(RuleNameLabel ruleName, AspectGraph ruleAsGraph) {
		try {
		    GraphInfo.setName(ruleAsGraph, ruleName.name());
			AspectualRuleView ruleView = new AspectualRuleView(
					ruleAsGraph, ruleName, getCurrentGrammar()
							.getProperties());
			getCurrentGrammar().addRule(ruleView);
			currentGrammarLoader.marshalRule(ruleView, currentGrammarFile);
			setGrammar(getCurrentGrammar());
			setRule(ruleName);
		} catch (IOException exc) {
			showErrorDialog("Error while saving edited rule", exc);
		}
	}

    /**
	 * Deletes a rule from the grammar and the file system, and resets the grammar view. 
	 */
	void doDeleteRule(RuleNameLabel name) {
		AspectualRuleView rule = getCurrentGrammar().removeRule(name);
		if (rule != null) {
			currentGrammarLoader.deleteRule(rule, currentGrammarFile);
			setGrammar(getCurrentGrammar());
		}
	}

	/**
     * Refreshes the currently loaded grammar, if any. Does not ask for confirmation. Has no effect
     * if no grammar is currently loaded.
     */
    void doRefreshGrammar() {
        if (currentGrammarFile != null) {
        	AspectualGraphView startGraph = getCurrentGrammar().getStartGraph();
            try {
            	File currentStateFile = getStateFileChooser().getSelectedFile();
            	if (startGraph != null && currentStateFile == null) {
            		setGrammar(currentGrammarLoader.unmarshal(currentGrammarFile, startGraph.getName()));
            	} else {
                    setGrammar(currentGrammarLoader.unmarshal(currentGrammarFile));
                    if (currentStateFile != null) {
                    	doLoadStartGraph(currentStateFile);
                    }
            	}
            } catch (IOException exc) {
                showErrorDialog("Error while loading grammar from " + currentGrammarFile, exc);
            }
        }
    }

    /**
     * Saves the contents of a given j-model to a given file.
     */
    void doSaveGraph(Graph graph, File file) {
        try {
        	AspectGraph saveGraph = AspectGraph.getFactory().fromPlainGraph(graph);
        	if (saveGraph.hasErrors()) {
                showErrorDialog("Errors in graph", new FormatException(saveGraph.getErrors()));
        	} else { 
        		graphLoader.marshalGraph(saveGraph, file);
        	}
        } catch (IOException exc) {
            showErrorDialog("Error while saving to " + file, exc);
        }
    }

    void doSaveControl(String controlProgram, File file) {
    	try {
    		
    		ControlView.saveFile(controlProgram, file);
    		
    	} catch( IOException exc) {
    		showErrorDialog("Error while saving to " + file, exc);
    	}
    }
    
    /**
	 * Sets a new graph transition system. Invokes
	 * {@link #fireSetGrammar(DefaultGrammarView)} to notify all observers of the change.
	 * 
	 * @param grammar
	 *            the new graph transition system
	 * @see #fireSetGrammar(DefaultGrammarView)
	 */
    public synchronized void setGrammar(DefaultGrammarView grammar) {
		setCurrentGrammar(grammar);
		setCurrentGTS(null);
		fireSetGrammar(grammar);
		refresh();
		List<String> grammarErrors = grammar.getErrors();
		boolean grammarCorrect = grammarErrors.isEmpty();
		getErrorPanel().setErrors(grammarErrors);
		if (grammarCorrect && confirmBehaviourOption(START_SIMULATION_OPTION)) {
			if (this.isAbstractSimulation()) {
				startAbstrSimulation(grammar);
			} else {
				startSimulation(grammar);
			}
		}
    }

    /**
	 * Sets a new graph transition system. Invokes
	 * {@link #fireStartSimulation(GTS)} to notify all observers of the
	 * change.
	 * 
	 * @param grammar
	 *            the new graph transition system
	 * @see #fireSetGrammar(DefaultGrammarView)
	 */
    public synchronized void startSimulation(DefaultGrammarView grammar) {
    	try {
    		setCurrentGrammar(grammar);
    		setCurrentGTS(new GTS(getCurrentGrammar().toGrammar()));
			//getGenerator().explore(getCurrentState());
			fireStartSimulation(getCurrentGTS());
			refresh();
		} catch (FormatException exc) {
			showErrorDialog("Error while starting simulation", exc);
		}
    }

    /**
     *  HARMEN: possibly merge this method with the above one.
     */
    public synchronized void startSimulation(DefaultGrammarView grammar, boolean findMatchings) {
    	try {
    		setCurrentGrammar(grammar);
    		setCurrentGTS(new GTS(getCurrentGrammar().toGrammar()));
//    		if (findMatchings) {
//        		getGenerator().explore(getCurrentState());
//    		}
			fireStartSimulation(getCurrentGTS());
			refresh();
		} catch (FormatException exc) {
			showErrorDialog("Error while starting simulation", exc);
		}
    }

    /** A variant of the {@link #startSimulation(DefaultGrammarView)} method for 
     * starting an abstract simulation.
     */
    public synchronized void startAbstrSimulation(DefaultGrammarView grammar) {
    	try {
    		AbstrSimulationProperties properties = new AbstrSimulationProperties();
    		PropertiesDialog dialog = new PropertiesDialog(properties, AbstrSimulationProperties.DEFAULT_KEYS, true);
    		boolean changed = dialog.showDialog(getFrame());
    		properties.update(dialog.getEditedProperties());
			boolean symred = properties.getSymmetryReduction();
			Abstraction.LinkPrecision linkPrecision = properties.getLinksPrecision();
			Abstraction.Parameters options = new Abstraction.Parameters(symred, linkPrecision, properties.getRadius(), properties.getPrecision(), 10);
			AGTS agts = new AGTS(getCurrentGrammar().toGrammar(), options);		
			setCurrentGTS(agts);
			// IOVKA to be uncommented when abstrExploreMenu items are enabled
			//this.abstrExploreMenu.refreshOptions();
			
			// getGenerator().explore(getCurrentState());
			fireStartSimulation(getCurrentGTS());
			refresh();
		} catch (FormatException exc) {
			showErrorDialog("Error while starting simulation", exc);
		}
	}
    
    
    /**
	 * Sets the current state graph to a given state. Adds the previous state or
	 * active derivation to the history. Invokes <tt>notifySetState(state)</tt>
	 * to notify all observers of the change.
	 * 
	 * @param state
	 *            the new state
	 * @see #fireSetState(GraphState)
	 */
    public synchronized void setState(GraphState state) {
//        if (setCurrentState(state)) {
//            getGenerator().explore(state);
//        }
    	setCurrentState(state);
        fireSetState(state);
        refreshActions();
    }
    
    
    public synchronized void exploreState (GraphState state) {
    	getExploreState().setGTS(this.getCurrentGTS());
    	getExploreState().setState(state);
    	getExploreState().next();
    	setState(state);
    }

   
    /**
     * Sets the current production rule. Invokes <tt>notifySetRule(name)</tt> to notify all
     * observers of the change. The current derivation (if any) is thereby deactivated.
     * @param name the name of the new rule
     * @require name != null
     * @see #fireSetRule(NameLabel)
     */
    public synchronized void setRule(RuleNameLabel name) {
        setCurrentRule(getCurrentGrammar().getRule(name));
        setCurrentTransition(null);
        setCurrentMatch(null);
        fireSetRule(name);
        refreshActions();
    }

    /**
     * Sets the current match and notify all observers of the RULE change
     */
    public synchronized void setMatch(RuleMatch match) {
    	setCurrentTransition(null);
    	setCurrentRule(getCurrentGrammar().getRule(match.getRule().getName()));
    	setCurrentMatch(match);
    	fireSetRule(match.getRule().getName());
    	refreshActions();
    }
    
    /**
     * Activates a given derivation. Adds the previous state or derivation to the history. Invokes
     * <tt>notifySetTransition(edge)</tt> to notify all observers of the change.
     * @param transition the derivation to be activated.
     * @see #fireSetTransition(GraphTransition)
     */
    public synchronized void setTransition(GraphTransition transition) {
        if (setCurrentTransition(transition)) {
        	RuleNameLabel ruleName = transition.getEvent().getRule().getName();
            setCurrentRule(getCurrentGrammar().getRule(ruleName));
            setCurrentMatch(transition.getMatch());
        }
        fireSetTransition(getCurrentTransition());
        refreshActions();
    }

//    /**
//     * Applies the active derivation. The current state is set to the derivation's cod, and the
//     * current derivation to null. Invokes <tt>notifyApplyTransition()</tt> to notify all
//     * observers of the change.
//     * @see #fireApplyTransition(GraphTransition)
//     */
//    public synchronized void applyTransition() {
//        GraphTransition appliedTransition = getCurrentTransition();
//        setCurrentState(appliedTransition.target());
//        fireApplyTransition(appliedTransition);
//        refreshActions();
//    }
    
    /** Applies a match to the current state.
     * The current state is set to the derivation's cod, and the
     * current derivation to null.
     * Invokes <tt>notifyApplyTransition()</tt> to notify all
     * observers of the change.
     * @see #fireApplyTransition(GraphTransition)
     */
    public synchronized void applyMatch () {
    	if (getCurrentMatch() != null) {
    		ExploreCache cache = getCurrentGTS().getRecord().createCache(getCurrentState(), false, false);
    		GraphTransition trans = new StateGenerator(getCurrentGTS()).applyMatch(getCurrentState(), getCurrentMatch(), cache).iterator().next();
    		setCurrentState(trans.target());
    		fireApplyTransition(trans);
    		refreshActions();
    	}
    }

    /**
     * Directs the actual verification process.
     * @param property the property to be checked
     */
    public synchronized void verifyProperty(String property) {
    	try{
    		TemporalFormula formula = CTLFormula.parseFormula(property);
    		String invalidAtom = TemporalFormula.validAtoms(formula, getCurrentGrammar().getRuleMap().keySet());
    		if (invalidAtom == null) {
        		CTLModelChecker modelChecker = new CTLModelChecker(getCurrentGTS(), formula);
        		modelChecker.verify();
        		Set<State> counterExamples = formula.getCounterExamples();
        		fireVerifyProperty(counterExamples);
    		} else {
    			showErrorDialog("Invalid atomic proposition", new Exception("'" + invalidAtom + "' is not a valid atomic proposition."));
    		}
    	} catch (FormatException efe) {
    		showErrorDialog("Format error in temporal formula", efe);
    	}
    }

    // ---------------------- NOTIFICATIONS --------------------------------

    /**
     * Adds a listener to the registered simulation listeners. From this moment on, the listener
     * will be notified.
     * @param listener the listener to be added
     */
    public synchronized void addSimulationListener(SimulationListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes a listener from the registered simulation listeners. From this moment on, the
     * listener will no longer be notified.
     * @param listener the listener to be removed
     */
    public synchronized void removeSimulationListener(SimulationListener listener) {
        listeners.remove(listener);
    }

    /**
     * Lazily creates and returns the frame of this simulator.
     */
    public JFrame getFrame() {
        if (frame == null) {
        	// force the LAF to be set
        	groove.gui.Options.initLookAndFeel();
        	
            // set up the frame
            frame = new JFrame(APPLICATION_NAME);
            // small icon doesn't look nice due to shadow
            frame.setIconImage(Groove.GROOVE_ICON_16x16.getImage());
            // frame.setSize(500,300);
            frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    doQuit();
                }
            });
//            frame.setContentPane(splitPane);
            frame.setJMenuBar(createMenuBar());
            
        	// set up the content pane of the frame as a splt pane,
            // with the rule directory to the left and a desktop pane to the right
            JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
            splitPane.setLeftComponent(getRuleJTreePanel());
            splitPane.setRightComponent(getGraphViewsPanel());
            
            Container contentPane = frame.getContentPane();
            contentPane.setLayout(new BorderLayout());
            contentPane.add(splitPane);
            contentPane.add(getErrorPanel(), BorderLayout.SOUTH);
        }
        return frame;
    }

    /**
     * Lazily creates and returns the panel with the state, rule and LTS views.
     */
    JTabbedPane getGraphViewsPanel() {
        if (graphViewsPanel == null) {
            graphViewsPanel = new JTabbedPane();
            graphViewsPanel.addTab(null, Groove.GRAPH_FRAME_ICON, getStatePanel(), "Current graph state");
            graphViewsPanel.addTab(null, Groove.RULE_FRAME_ICON, getRulePanel(), "Selected rule");
            graphViewsPanel.addTab(null, Groove.LTS_FRAME_ICON, getLtsPanel(), "Labelled transition system");
            graphViewsPanel.addTab(null, Groove.CTRL_FRAME_ICON , getControlPanel(), "Control specification" );
            // add this simulator as a listener so that the actions are updated regularly
            graphViewsPanel.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent evt) {
                    refreshActions();
                }
            });
            graphViewsPanel.setVisible(true);
        }
        return graphViewsPanel;
    }

    /**
     * Lazily creates and returns the panel with the rule tree.
     */
    JScrollPane getRuleJTreePanel() {
        if (ruleJTreePanel == null) {
            // make sure the preferred width is not smaller than the minimum width
            ruleJTreePanel = new JScrollPane(getRuleTree()) {
                @Override
                public Dimension getPreferredSize() {
                    Dimension superSize = super.getPreferredSize();
                    return new Dimension((int) Math.max(superSize.getWidth(),
                        RULE_TREE_MINIMUM_WIDTH), (int) superSize.getHeight());
                }
            };
            ruleJTreePanel.setMinimumSize(new Dimension(RULE_TREE_MINIMUM_WIDTH, 0));
        }
        return ruleJTreePanel;
    }

    /**
     * Returns the simulator panel on which the current state is displayed. Note that this panel may
     * currently not be visible.
     * @see #setGraphPanel(JGraphPanel)
     */
    StatePanel getStatePanel() {
        if (statePanel == null) {
            // panel for state display
            statePanel = new StatePanel(this);
            statePanel.setPreferredSize(GRAPH_VIEW_PREFERRED_SIZE);
        }
        return statePanel;
    }

    
    CAPanel getControlPanel() {
    	if( controlPanel == null ) {
    		controlPanel = new CAPanel(this);
    		controlPanel.setPreferredSize(GRAPH_VIEW_PREFERRED_SIZE);
    	}
    	return controlPanel;
    }
    
    /**
     * Returns the simulator panel on which the currently selected production rule is displayed.
     * Note that this panel may currently not be visible.
     * @see #setGraphPanel(JGraphPanel)
     */
    RulePanel getRulePanel() {
        if (rulePanel == null) {
            // panel for production display
            rulePanel = new RulePanel(this);
            // res.setSize(preferredFrameDimension);
            rulePanel.setPreferredSize(GRAPH_VIEW_PREFERRED_SIZE);
        }
        return rulePanel;
    }

    /**
     * Returns the simulator panel on which the LTS. Note that this panel may currently not be
     * visible.
     * @see #setGraphPanel(JGraphPanel)
     */
    LTSPanel getLtsPanel() {
        if (ltsPanel == null) {
            ltsPanel = new LTSPanel(this);
            ltsPanel.setPreferredSize(GRAPH_VIEW_PREFERRED_SIZE);
        }
        return ltsPanel;
    }

    /**
     * Returns the tree of rules and matches displayed in the simulator.
     */
    JTree getRuleTree() {
        if (ruleJTree == null) {
            ruleJTree = new RuleJTree(this);
        }
        return ruleJTree;
    }

    /** Returns the exporter of the simulator. */
    Exporter getExporter() {
    	return exporter;
    }
    
    /**
	 * Returns the currently selected graph view component. This is be the state, rule or LTS view.
	 * @see #getStatePanel()
	 * @see #getRulePanel()
	 * @see #getLtsPanel()
	 * @see #setGraphPanel(JGraphPanel)
	 */
    JGraphPanel<?> getGraphPanel() {
	    // hack om splitpane te supporten
    	Component c = getGraphViewsPanel().getSelectedComponent();
	    if( c instanceof CAPanel )
	    	c = ((CAPanel)c).getJGraphPanel();
    	return (JGraphPanel<?>) c; 
	}

	/**
	 * Brings one of the graph view components to the foreground. This should be the state, rule or
	 * LTS view.
	 * @param component the graph view component to bring to the foreground
	 * @see #getStatePanel()
	 * @see #getRulePanel()
	 * @see #getLtsPanel()
	 * @see #getGraphPanel()
	 */
    void setGraphPanel(JGraphPanel<?> component) {
	    getGraphViewsPanel().setSelectedComponent(component);
	}

    /** 
     * Changes the enabledness of one of the graph panels
     * @param component the panel to change
     * @param enabled the new enabledness status
     */
    void setGraphPanelEnabled(JGraphPanel<?> component, boolean enabled) {
		int index = getGraphViewsPanel().indexOfComponent(component);
    	getGraphViewsPanel().setEnabledAt(index, enabled);
		if (component == getLtsPanel()) {
			String text;
			if (enabled) {
				text = "Labelled transition system";
			} else if (getCurrentGrammar() == null) {
				text = "Currently distabled; load grammar";
			} else if (getCurrentGrammar().getErrors().isEmpty()) {
				text = String.format("Currently disabled; press %s to start simulation", KeyEvent.getKeyText(((KeyStroke) getStartSimulationAction().getValue(Action.ACCELERATOR_KEY)).getKeyCode()));
			} else {
				text = "Disabled due to grammar errors";
			}
			getGraphViewsPanel().setToolTipTextAt(index, text);
		}
    }
    
    /** 
     * Adds an element to the set of refreshables.
     * Also calls {@link Refreshable#refresh()} on the element. 
     */
    void addRefreshable(Refreshable element) {
    	if (refreshables.add(element)) {
    		element.refresh();
    	}
    }
    
    /**
     * Is called after a change to current state, rule or derivation or to the currently selected
     * view panel to allow registered refreshable elements to refresh themselves.
     */
    void refreshActions() {
    	for (Refreshable action: refreshables) {
    		action.refresh();
    	}
    }
    
    /**
     * Adds the accelerator key for a given action to the action and input maps of the simulator
     * frame's content pane.
     * @param action the action to be added
     * @require <tt>frame.getContentPane()</tt> should be initialised
     */
    void addAccelerator(Action action) {
        JComponent contentPane = (JComponent) getFrame().getContentPane();
        ActionMap am = contentPane.getActionMap();
        am.put(action.getValue(Action.NAME), action);
        InputMap im = contentPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        im.put((KeyStroke) action.getValue(Action.ACCELERATOR_KEY), action.getValue(Action.NAME));
    }

    /**
     * Creates, initializes and returns a menu bar for the simulator. The actions have to be
     * initialized before invoking this.
     */
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(createFileMenu());
        menuBar.add(createEditMenu());
        menuBar.add(createDisplayMenu());
        menuBar.add(createExploreMenu());
        menuBar.add(createVerifyMenu());
        menuBar.add(createHelpMenu());
        return menuBar;
    }

	/**
	 * Creates and returns a file menu for the menu bar.
	 */
	private JMenu createFileMenu() {
	    JMenu result = new JMenu(Options.FILE_MENU_NAME);
	    result.add(createNewMenu());
	    result.add(new JMenuItem(getLoadGrammarAction()));
        result.add(new JMenuItem(getLoadStartGraphAction()));
        result.add(new JMenuItem(getRefreshGrammarAction()));
	    result.addSeparator();
	    result.add(new JMenuItem(getSaveGrammarAction()));
	    result.add(new JMenuItem(getSaveGraphAction()));
	    result.add(new JMenuItem(getExportGraphAction()));
	    result.addSeparator();
	    result.add(getEditItem());
	    result.add(new JMenuItem(getEditSystemPropertiesAction()));
	    result.addSeparator();
	    result.add(new JMenuItem(getQuitAction()));
	    return result;
	}

	private JMenu createNewMenu() {
		JMenu result = new JMenu(Options.NEW_ACTION_NAME);
//        result.setAccelerator(Options.NEW_KEY);
		String menuName = result.getText();
		result.add(createItem(getNewGrammarAction(), menuName));
		result.add(createItem(getNewGraphAction(), menuName));
		result.add(createItem(getNewRuleAction(), menuName));
		return result;
	}

	/**
	 * Creates and returns a file menu for the menu bar.
	 */
	private JMenu createEditMenu() {
	    JMenu result = new JMenu(Options.EDIT_MENU_NAME);
	    result.add(getNewRuleAction());
		result.addSeparator();
		result.add(getEnableRuleAction());
		result.addSeparator();
		result.add(getCopyRuleAction());
		result.add(getDeleteRuleAction());
		result.add(getRenameRuleAction());
		result.addSeparator();
		result.add(getEditRuleAction());
		result.add(getEditGraphAction());
		result.addSeparator();
		result.add(getEditRulePropertiesAction());
		result.add(getEditSystemPropertiesAction());
	    return result;
	}

	/**
	 * Returns the menu item in the file menu that specifies
	 * saving the currently displayed graph (in the currently selected graph panel).
	 */
	JMenuItem getEditItem() {
		if (editGraphItem == null) {
			editGraphItem = new JMenuItem();
			// load the rule edit action, even though it is not user here
			getEditRuleAction();
			getEditGraphAction();
			editGraphItem.setAccelerator(Options.EDIT_KEY);
		}
		return editGraphItem;
	}

	/**
	 * Creates and returns a display menu for the menu bar.
	 * The menu is filled out each time it gets selected
	 * so as to be sure it applies to the current jgraph
	 */
	private JMenu createDisplayMenu() {
	    JMenu result = new JMenu(Options.DISPLAY_MENU_NAME) {
			@Override
	        public void menuSelectionChanged(boolean selected) {
	            removeAll();
	            JGraph jgraph = getGraphPanel().getJGraph();
	            JPopupMenu popupMenu = getPopupMenu();
	            jgraph.fillOutEditMenu(popupMenu, true);
	            jgraph.fillOutDisplayMenu(popupMenu);
	            popupMenu.addSeparator();
	            popupMenu.add(createOptionsMenu());
	            super.menuSelectionChanged(selected);
	        }
	    };
	    return result;
	}

	/**
	 * Creates and returns an options menu for the menu bar.
	 */
	JMenu createOptionsMenu() {
        JMenu result = new JMenu(OPTIONS_MENU_NAME);
        result.add(getOptions().getItem(SHOW_NODE_IDS_OPTION));
        result.add(getOptions().getItem(SHOW_ANCHORS_OPTION));
    	result.add(getOptions().getItem(SHOW_ASPECTS_OPTION));
    	result.add(getOptions().getItem(SHOW_REMARKS_OPTION));
    	result.add(getOptions().getItem(SHOW_BACKGROUND_OPTION));
    	result.add(getOptions().getItem(SHOW_VALUE_NODES_OPTION));
    	result.add(getOptions().getItem(SHOW_STATE_IDS_OPTION));
    	result.addSeparator();
    	result.add(getOptions().getItem(START_SIMULATION_OPTION));
    	result.add(getOptions().getItem(STOP_SIMULATION_OPTION));
    	result.add(getOptions().getItem(DELETE_RULE_OPTION));
    	result.add(getOptions().getItem(REPLACE_RULE_OPTION));
    	result.add(getOptions().getItem(REPLACE_START_GRAPH_OPTION));
    	return result;
	}

	/**
	 * Creates and returns an exploration menu for the menu bar.
	 */
	private JMenu createExploreMenu() {
		JMenu result = new JMenu();
		//JMenu exploreMenu = new ExploreStrategyMenu(this, false);
		// ADD: switch from explore to scenario menu
		JMenu exploreMenu = new ScenarioMenu(this, false);
		result.setText(exploreMenu.getText());
        result.add(new JMenuItem(getUndoAction()));
        result.add(new JMenuItem(getRedoAction()));
        result.addSeparator();
        result.add(new JMenuItem(getStartSimulationAction()));
        //result.add(new JMenuItem(getStartAbstrSimulationAction()));
        result.add(new JMenuItem(getApplyTransitionAction()));
        result.add(new JMenuItem(getGotoStartStateAction()));
        result.addSeparator();
        // copy the exploration meny
        for (Component menuComponent: exploreMenu.getMenuComponents()) {
        	result.add(menuComponent);
        }
        result.addSeparator();
        result.add(new JMenuItem(showResultAction()));
        return result;
	}

	/**
	 * Creates and returns a verification menu for the menu bar.
	 */
	private JMenu createVerifyMenu() {
	    JMenu result = new VerifyMenu(this);
	    return result;
	}

	/**
	 * Creates and returns a help menu for the menu bar.
	 */
	private JMenu createHelpMenu() {
		JMenu result = new JMenu(HELP_MENU_NAME);
		result.add(new JMenuItem(new AboutAction()));
		return result;
	}

	/** Creates a menu item from an action, while omitting some of the label text. */ 
	private JMenuItem createItem(Action action, String omit) {
		JMenuItem result = new JMenuItem(action);
		String text = (String) action.getValue(Action.NAME);
		if (text != null) {
			int omitIndex = text.indexOf(omit);
			if (omitIndex >= 0) {
				String pre = text.substring(0, omitIndex);
				String post = text.substring(omitIndex + omit.length()).trim();
				result.setText((pre + post).trim());
			}
		}
		return result;
	}
	
	/**
	 * Returns the file chooser for grammar (GPR) files, lazily creating it first.
	 */
	JFileChooser getGrammarFileChooser() {
		if (grammarFileChooser == null) {
			grammarFileChooser = new GrooveFileChooser();
			grammarFileChooser.setAcceptAllFileFilterUsed(false);
			for (FileFilter filter : grammarLoaderMap.keySet()) {
				grammarFileChooser.addChoosableFileFilter(filter);
			}
			grammarFileChooser.setFileFilter(gpsLoader.getExtensionFilter());
			grammarFileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		}
		return grammarFileChooser;
	}

	/** Returns the grammar loader map of this simulator. */
	Map<ExtensionFilter,AspectualViewGps> getGrammarLoaderMap() {
		return grammarLoaderMap;
	}
	
	/** Returns the current grmmar file of this simulator. */
	File getCurrentGrammarFile() {
		return currentGrammarFile;
	}
	
	/**
	 * Returns the file chooser for state (GST or GXL) files, lazily creating it first.
	 */
	JFileChooser getStateFileChooser() {
		if (stateFileChooser == null) {
			stateFileChooser = new GrooveFileChooser();
			stateFileChooser.addChoosableFileFilter(stateFilter);
			stateFileChooser.addChoosableFileFilter(gxlFilter);
			stateFileChooser.setFileFilter(stateFilter);
		}
        return stateFileChooser;
	}

	public FormulaDialog getFormulaDialog() {
		if (formulaDialog == null)
			formulaDialog = new FormulaDialog();
		return formulaDialog;
	}

	private ErrorListPanel getErrorPanel() {
		if (errorPanel == null) {
			errorPanel = new ErrorListPanel();
		}
		return errorPanel;
	}

	/**
     * Adds all implemented grammar loaders to the menu.
     */
    protected void initGrammarLoaders() {
        grammarLoaderMap.clear();
        gpsLoader = new LayedOutGps();
//        gpsLoader = new GpsGrammar(graphLoader, DefaultRuleFactory.getInstance());
        grammarLoaderMap.put(gpsLoader.getExtensionFilter(), gpsLoader);
//        ggxLoader = new GgxGrammar();
//        grammarLoaderMap.put(ggxLoader.getExtensionFilter(), ggxLoader);
    }

    /**
     * Notifies all listeners of a new graph grammar. As a result,
     * {@link SimulationListener#setGrammarUpdate(DefaultGrammarView)}is invoked on all currently
     * registered listeners. This method should not be called directly: use
     * {@link #setGrammar(DefaultGrammarView)}instead.
     * @see SimulationListener#setGrammarUpdate(DefaultGrammarView)
     */
    protected synchronized void fireSetGrammar(DefaultGrammarView grammar) {
    	if (!updating) {
			updating = true;
			for (SimulationListener listener : listeners) {
				listener.setGrammarUpdate(grammar);
			}
			updating = false;
    	}
    }

    /**
     * Notifies all listeners of the start of a new active simulation. As a result,
     * {@link SimulationListener#startSimulationUpdate(GTS)} is invoked on all currently
     * registered listeners. This method should not be called directly: use
     * {@link #startSimulation(DefaultGrammarView)}instead.
     * @see SimulationListener#startSimulationUpdate(GTS)
     */
    protected synchronized void fireStartSimulation(GTS gts) {
    	if (!updating) {
			updating = true;
			for (SimulationListener listener : listeners) {
				listener.startSimulationUpdate(gts);
			}
			updating = false;
		}
    }

    
    /**
     * Notifies all listeners of a new state. As a result,
     * {@link SimulationListener#setStateUpdate(GraphState)}is invoked on all currently registered
     * listeners. This method should not be called directly: use {@link #setState(GraphState)}instead.
     * @see SimulationListener#setStateUpdate(GraphState)
     * @see #setState(GraphState)
     */
    protected synchronized void fireSetState(GraphState state) {
    	if (!updating) {
			updating = true;
			for (SimulationListener listener : listeners) {
				listener.setStateUpdate(state);
			}
			updating = false;
		}
    }

    /**
	 * Notifies all listeners of a new rule. As a result,
	 * {@link SimulationListener#setRuleUpdate(NameLabel)}is invoked on all
	 * currently registered listeners. This method should not be called
	 * directly: use {@link #setRule(RuleNameLabel)}instead.
	 * 
	 * @see SimulationListener#setRuleUpdate(NameLabel)
	 * @see #setRule(RuleNameLabel)
	 */
    protected synchronized void fireSetRule(NameLabel name) {
    	if (!updating) {
			updating = true;
			for (SimulationListener listener : listeners) {
				listener.setRuleUpdate(name);
			}
			updating = false;
		}
    }

    /**
	 * Notifies all listeners of a new detivation. As a result,
	 * {@link SimulationListener#setTransitionUpdate(GraphTransition)}is
	 * invoked on all currently registered listeners. This method should not be
	 * called directly: use {@link #setTransition(GraphTransition)}instead.
	 * 
	 * @see SimulationListener#setTransitionUpdate(GraphTransition)
	 * @see #setTransition(GraphTransition)
	 */
    protected synchronized void fireSetTransition(GraphTransition transition) {
    	if (!updating) {
			updating = true;
			for (SimulationListener listener : listeners) {
				listener.setTransitionUpdate(transition);
			}
			updating = false;
    	}
    }

    /**
     * Notifies all listeners of the application of the current derivation. As a result,
     * {@link SimulationListener#applyTransitionUpdate(GraphTransition)}is invoked on all currently
     * registered listeners. This method should not be called directly: use
     * {@link #applyMatch()}instead.
     * @param transition the transition that has been applied
     * @see SimulationListener#applyTransitionUpdate(GraphTransition)
     * @see #applyMatch()
     */
    protected synchronized void fireApplyTransition(GraphTransition transition) {
    	if (!updating) {
			updating = true;
			for (SimulationListener listener : listeners) {
				listener.applyTransitionUpdate(transition);
			}
			updating = false;
		}
    }

    /**
	 * Notifies all listeners of the verification of the current generated
	 * transition system. This method should not be called directly: use
	 * {@link #verifyProperty(String)} instead.
	 * 
	 * @param counterExamples
	 *            the collection of states that do not satisfy the property
	 *            verified
	 */
    protected synchronized void fireVerifyProperty(Set<State> counterExamples) {
    	if (counterExamples.isEmpty()) {
    		JOptionPane.showMessageDialog(getFrame(), "There were no counter-examples.", "Verification results", JOptionPane.INFORMATION_MESSAGE, Groove.GROOVE_ICON_32x32);
    	} else {
    		if (counterExamples.size() == 1) {
        		JOptionPane.showMessageDialog(getFrame(), "There was 1 counter-example.", "Verification results", JOptionPane.INFORMATION_MESSAGE, Groove.GROOVE_BLUE_ICON_32x32);
    		} else {
        		JOptionPane.showMessageDialog(getFrame(), "There were " + counterExamples.size() + " counter-examples.", "Verification results", JOptionPane.INFORMATION_MESSAGE, Groove.GROOVE_BLUE_ICON_32x32);
    		}
    		// reset lts display visibility
    		setGraphPanel(getLtsPanel());
    		LTSJModel jModel = getLtsPanel().getJModel();
    		Set<JCell> jCells = new HashSet<JCell>();
    		for(State counterExample: counterExamples) {
    			jCells.add(jModel.getJCell(counterExample));
    		}
    		jModel.setEmphasized(jCells);
    	}
    }

    /**
     * Emphasizes the transitions in the GTS that constitute the counter-example, if found.
     * If no counter-example had been found, a message will say so.
     * @param systemOK boolean indicating whether the a counter-example had been found
     * @param counterExample the actual path representing the counter-example
     */
    public synchronized void notifyCounterExample(boolean systemOK, Stack<GraphTransition> counterExample) {
    	if (!systemOK) {
            // reset lts display visibility
            setGraphPanel(getLtsPanel());
            LTSJModel jModel = getLtsPanel().getJModel();
            jModel.clearEmphasized();
            Set<JCell> jCells = new HashSet<JCell>();
            while (!counterExample.isEmpty()) {
            	jCells.add(jModel.getJCell(counterExample.pop()));
            }
            jModel.setEmphasized(jCells);
    	} else {
    		String message = "The system is OK.";
    		JOptionPane.showConfirmDialog(getFrame(), message, "Verdict", JOptionPane.PLAIN_MESSAGE);
    	}
	}
    
    public synchronized void notifyCounterExample(Collection<GraphState> counterExample) {
    	// reset lts display visibility
    	setGraphPanel(getLtsPanel());
    	LTSJModel jModel = getLtsPanel().getJModel();
    	jModel.clearEmphasized();
    	Set<JCell> jCells = new HashSet<JCell>();
    	for (GraphState state: counterExample) {
    		jCells.add(jModel.getJCell(state));
    	}
//    	while (!counterExample.isEmpty()) {
//    		jCells.add(jModel.getJCell(counterExample.pop()));
//    	}
    	jModel.setEmphasized(jCells);
    	JOptionPane.showConfirmDialog(getFrame(), "There is a counter-example.", "Verdict", JOptionPane.PLAIN_MESSAGE);
    }

    /**
	 * Refreshes the title bar, layout and actions.
	 */
	private void refresh() {
		setTitle();
		refreshActions();
	}

    /**
     * Sets the title of the frame to a given title.
     */
    private void setTitle() {
    	StringBuffer title = new StringBuffer();
    	if (getCurrentGrammar() != null && getCurrentGrammar().getName() != null) {
    		title.append(getCurrentGrammar().getName());
    		AspectualGraphView startGraph = getCurrentGrammar().getStartGraph();
    		if (startGraph != null) {
    			title.append(TITLE_NAME_SEPARATOR);
    			title.append(startGraph.getName());
    		}
    		title.append(" - ");
    	}
    	title.append(APPLICATION_NAME);
        getFrame().setTitle(title.toString());
    }

    /**
     * Returns the state generator for the current GTS, if any.
     */
    private StateGenerator getGenerator() {
    	if (stateGenerator == null || stateGenerator.getGTS() != getCurrentGTS()) {
    		if (getCurrentGTS() != null) {
    			stateGenerator = createStateGenerator(getCurrentGTS());
    		}
    	}
        return stateGenerator;
    }

    /** Callback factory method for the state generator. */
    protected StateGenerator createStateGenerator(GTS gts) {
    	StateGenerator result;
    	if (gts instanceof AGTS) {
    		result =  new AbstrStateGenerator(((AGTS) gts).getParameters());
    	} else {
    		result = new StateGenerator();
    	}
    	result.setGTS(gts);
    	return result;
    }
    
    /**
     * If a simulation is active, asks through a dialog whether it may be abandoned.
     * @param setGrammar flag indicating that {@link #setGrammar(DefaultGrammarView)}
     * is to be called with the current grammar, in case the simulation is abandoned
     * @return <tt>true</tt> if the current grammar may be abandoned
     */
    boolean confirmAbandon(boolean setGrammar) {
    	boolean result;
        if (getCurrentGTS() != null) {
            result = confirmBehaviourOption(STOP_SIMULATION_OPTION);
            if (result && setGrammar) {
            	setGrammar(getCurrentGrammar());
            }
        } else {
            result = true;
        }
        return result;
    }

    /**
     * Asks whether the current start graph should be replaced by the edited version.
     */
    boolean confirmLoadStartState(String stateName) {
    	if (getCurrentGrammar().getStartGraph() == null) {
    		return true;
    	} else {
			String question = String.format("Replace start graph with %s?",
					stateName);
			return confirmBehaviour(REPLACE_START_GRAPH_OPTION, question);
		}
	}

    /**
     * Asks whether a given existing file should be overwritten by a new grammar.
     */
    boolean confirmOverwriteGrammar(File grammarFile) {
    	if (grammarFile.exists()) {
    		int response = JOptionPane.showConfirmDialog(getFrame(), "Overwrite existing grammar?", null, JOptionPane.OK_CANCEL_OPTION);
    		return response == JOptionPane.OK_OPTION;

    	} else {
    		return true;
		}
	}

    /**
	 * Creates and shows an {@link ErrorDialog} for a given message and
	 * exception.
	 */
    void showErrorDialog(String message, Throwable exc) {
        new ErrorDialog(getFrame(), message, exc).setVisible(true);
    }

    /** 
     * Enters a dialog that results in a name label that does not yet
     * occur in the current grammar, or <code>null</code> if the dialog
     * was cancelled.
     * @param title dialog title; if <code>null</code>, a default title is used
     * @param name an initially proposed name
     * @param mustBeFresh if <code>true</code>, the returned name is guaranteed to be
     * distinct from the existing rule names
     * @return a rule name not occurring in the current grammar, or <code>null</code>
     */
    RuleNameLabel askNewRuleName(String title, String name, boolean mustBeFresh) {
    	RuleNameLabel suggestion = mustBeFresh ? generateNewRuleName(name) : new RuleNameLabel(name);
    	RuleNameDialog ruleNameDialog = new RuleNameDialog(getCurrentGrammar().getRuleMap().keySet(), suggestion);
    	ruleNameDialog.showDialog(getFrame(), title);
    	return ruleNameDialog.getName();
    }

    /** 
     * Checks if a given option is confirmed.
     * The question can be set explicitly.
     */
    boolean confirmBehaviour(String option, String question) {
    	BehaviourOption menu = (BehaviourOption) getOptions().getItem(option);
    	return menu.confirm(getFrame(), question);
    }

    /** 
     * Checks if a given option is confirmed.
     */
    boolean confirmBehaviourOption(String option) {
    	return confirmBehaviour(option, null);
    }

    /**
     * Returns the options object associated with the simulator.
     */
    public Options getOptions() {
    	// lazily creates the options 
    	if (options == null) {
    		options = new Options();
    		options.getItem(SHOW_REMARKS_OPTION).setSelected(true);
            options.getItem(SHOW_STATE_IDS_OPTION).setSelected(true);
            options.getItem(SHOW_BACKGROUND_OPTION).setSelected(true);
            options.getItem(Options.PREVIEW_ON_CLOSE_OPTION).setSelected(true);
    	}
    	return options;
    }
    
    /** Returns true if the current simulation is abstract. */
	public boolean isAbstractSimulation() {
		return this.getCurrentGTS() != null && this.getCurrentGTS() instanceof AGTS;
	}
 
    /**
     * The options object of this simulator.
     */
    private Options options;

    /**
     * The underlying graph grammar of this simulator. If <tt>null</tt>, no grammar has been
     * loaded.
     */
    private DefaultGrammarView currentGrammar;

    /**
     * The current graph transition system.
     */
    private GTS currentGTS;

    /**
     * The currently selected state graph.
     */
    private GraphState currentState;

    /**
     * The currently selected production rule.
     */
    private AspectualRuleView currentRule;

    /**
     * The currently activated derivation.
     * @invariant currentTransition == null || currentTransition.source().equals(currentState) &&
     *            currentTransition.rule().equals(currentRule)
     */
    private GraphTransition currentTransition;
//
//    /**
//     * The name of the current start state.
//     * May be a file name or the name of a graph within the current grammar or <code>null</code>.
//     */
//    private String currentStartStateName;

    /**
     * The currently selected match.
     */
    private RuleMatch currentMatch;
    
    /**
     * The file or directory containing the last loaded or saved grammar, or <tt>null</tt> if no
     * grammar was yet loaded.
     */
    private File currentGrammarFile;

    /**
     * The loader used to load the current grammar, if <tt>currentGrammarFile</tt> is not
     * <tt>null</tt>.
     */
    private AspectualViewGps currentGrammarLoader;
    
    /** The state generator strategy for the current GTS. */
    private StateGenerator stateGenerator;

    /** Flag to indicate that one of the simulation events is underway. */
    private boolean updating;
    /**
     * The loader used for unmarshalling gps-formatted graph grammars.
     */
    private LayedOutGps gpsLoader;

    /**
     * A mapping from extension filters (recognizing the file formats from the names) to the
     * corresponding grammar loaders.
     */
    private final Map<ExtensionFilter,AspectualViewGps> grammarLoaderMap = new LinkedHashMap<ExtensionFilter,AspectualViewGps>();

    /**
     * The graph loader used for saving graphs (states and LTS).
     */
    private final Xml<AspectGraph> graphLoader = new AspectGxl(new LayedOutXml());

    /**
     * File chooser for grammar files.
     */
    private JFileChooser grammarFileChooser;

    /**
     * File chooser for state files and LTS.
     */
    private JFileChooser stateFileChooser;

    /**
     * Dialog for entering temporal formulae.
     */
    private FormulaDialog formulaDialog;

    /**
     * Graph exporter.
     */
    private final Exporter exporter = new Exporter();

//    /**
//     * File chooser for state and LTS export actions.
//     */
//    private final JFileChooser formulaProvider = new GrooveFileChooser();

    /**
     * Extension filter for state files.
     */
    private final ExtensionFilter stateFilter = Groove.createStateFilter();

    /**
     * Extension filter used for exporting the LTS in jpeg format.
     */
    private final ExtensionFilter gxlFilter = Groove.createGxlFilter();

    /**
     * Set of registered simulation listeners.
     * @invariant <tt>listeners \subseteq SimulationListener</tt>
     */
    private final Set<SimulationListener> listeners = new HashSet<SimulationListener>();

    /** Current set of refreshables of this simulator. */
    private final Set<Refreshable> refreshables = new HashSet<Refreshable>();
    /**
     * This application's main frame.
     */
    private JFrame frame;

    /** Production rule directory. */
    private JTree ruleJTree;

    /** Production rule display panel. */
    private RulePanel rulePanel;

    /** State display panel. */
    private StatePanel statePanel;

    /** Control display panel. */
    private CAPanel controlPanel;
    
    /** LTS display panel. */
    private LTSPanel ltsPanel;

    /** Error display. */
    private ErrorListPanel errorPanel;
    
    /** Undo history. */
    private UndoHistory undoHistory;

    /** background for displays. */
    private JTabbedPane graphViewsPanel;

    /** panel for the rule directory. */
    private JScrollPane ruleJTreePanel;

    /** 
     * Menu item in the file menu for
     * one of the graph or rule edit actions.
     */
    private JMenuItem editGraphItem;
    /**
	 * The transition application action permanently associated with this simulator. 
	 */
	private ApplyTransitionAction applyTransitionAction;

	/**
	 * The rule copying action permanently associated with this simulator. 
	 */
	private CopyRuleAction copyRuleAction;

	/**
	 * The rule deletion action permanently associated with this simulator. 
	 */
	private DeleteRuleAction deleteRuleAction;
	/**
	 * The state and rule edit action permanently associated with this simulator. 
	 */
	private EditGraphAction editGraphAction;

	/**
	 * The rule edit action permanently associated with this simulator. 
	 */
	private EditRuleAction editRuleAction;

	/**
	 * The rule properties edit action permanently associated with this simulator. 
	 */
	private EditRulePropertiesAction editRulePropertiesAction;
	/**
	 * The action to show the system properties of the currently selected grammar. 
	 */
	private EditSystemPropertiesAction editSystemPropertiesAction;
	/**
	 * The rule enabling action permanently associated with this simulator. 
	 */
	private EnableRuleAction enableRuleAction;

	/** The state export action permanently associated with this simulator. */
    private ExportGraphAction exportGraphAction;

    /**
	 * The go-to start state action permanently associated with this simulator. 
	 */
	private GotoStartStateAction gotoStartStateAction;

	/** The start state load action permanently associated with this simulator. */
    private LoadStartGraphAction loadStartGraphAction;

    /** The grammar load action permanently associated with this simulator. */
    private LoadGrammarAction loadGrammarAction;

    /**
	 * The graph creation action permanently associated with this simulator. 
	 */
	private NewGraphAction newGraphAction;
	/**
	 * The rule system creation action permanently associated with this simulator. 
	 */
	private NewGrammarAction newGrammarAction;
	/**
	 * The rule creation action permanently associated with this simulator. 
	 */
	private NewRuleAction newRuleAction;
	/**
	 * The quit action permanently associated with this simulator. 
	 */
	private QuitAction quitAction;

	/**
	 * The redo action permanently associated with this simulator. 
	 */
	private Action redoAction;

	/** The grammar refresh action permanently associated with this simulator. */
    private RefreshGrammarAction refreshGrammarAction;

    /**
	 * The rule renaming action permanently associated with this simulator. 
	 */
	private RenameRuleAction renameRuleAction;
	/**
	 * The state save action permanently associated with this simulator. 
	 */
	private SaveGraphAction saveGraphAction;
	/**
	 * The grammar save action permanently associated with this simulator. 
	 */
	private SaveGrammarAction saveGrammarAction;

	/** The action to start a new simulation. */
    private StartSimulationAction startSimulationAction;
    /** The action to start a new abstract simulation. */
    private StartAbstrSimulationAction startAbstrSimulationAction;
    /** The undo action permanently associated with this simulator. */
    private Action undoAction;

    /** The undo action permanently associated with this simulator. */
    private ShowResultAction showResultAction;

    /** The ctl formula providing action permanently associated with this simulator. */
    private ProvideTemporalFormulaAction provideTemporalFormulaAction;
        
	/** Starts a simulator, optionally setting the graph production system and start state. */
	public static void main(String[] args) {
	    Simulator simulator;
	    try {
	        if (args.length == 0)
	            simulator = new Simulator();
	        else if (args.length == 1)
	            simulator = new Simulator(args[0]);
	        else if (args.length == 2)
	            simulator = new Simulator(args[0], args[1]);
	        else
	            throw new IOException("Usage: Simulator [<production-system> [<start-state>]]");
//	        simulator.loadModules();
	        simulator.start();
	    } catch (IOException exc) {
	        exc.printStackTrace();
	        System.out.println(exc.getMessage());
	        // System.exit(0);
	    }
	}

    // --------------------- INSTANCE DEFINITIONS -----------------------------


    /**
     * Name of the LTS file, when it is isaved or exported.
     */
    static private final String LTS_FILE_NAME = "lts";
    /**
     * Default name of an empty rule.
     */
    static private final String NEW_GRAMMAR_NAME = "newGrammar";
    /**
     * Default name of an empty rule.
     */
    static private final String NEW_GRAPH_NAME = "newGraph";
    /**
     * Default name of an empty rule.
     */
    static private final String NEW_RULE_NAME = "newRule";

    /**
     * Separator between grammar name and start graph name in the frame title.
     */
    private static final String TITLE_NAME_SEPARATOR = "@";
    
    /** Name of this application. */
    private static final String APPLICATION_NAME = "Production Simulator";

    /**
     * Minimum width of the rule tree component.
     */
    static private final int RULE_TREE_MINIMUM_WIDTH = 100;

    /**
     * Preferred width of the graph view.
     */
    static private final int GRAPH_VIEW_PREFERRED_WIDTH = 500;

    /**
	 * Preferred height of the graph view.
	 */
	static private final int GRAPH_VIEW_PREFERRED_HEIGHT = 400;

	/**
	 * Preferred dimension of the graph view.
	 */
	static private final Dimension GRAPH_VIEW_PREFERRED_SIZE = new Dimension(GRAPH_VIEW_PREFERRED_WIDTH, GRAPH_VIEW_PREFERRED_HEIGHT);


    /** Flag controlling if a report should be printed after quitting. */
    private static final boolean REPORT = false;
    

	/**
	 * Class that spawns a thread to perform a long-lasting action,
	 * while displaying a dialog that can interrupt the thread.
	 */
	abstract private class CancellableThread extends Thread {
	    /**
	     * Constructs an action that can be cancelled through a dialog.
	     * @param parentComponent the parent for the cancel dialog
	     * @param cancelDialogTitle the title of the cancel dialog
	     */
		public CancellableThread(Component parentComponent, String cancelDialogTitle) {
	        this.cancelDialog = createCancelDialog(parentComponent, cancelDialogTitle);
	    }
	
		/**
		 * Calls {@link #doAction()}, then disposes the cancel dialog.
		 */
		@Override
		final public void run() {
			doAction();
			synchronized (cancelDialog) {
				// wait for the cancel dialog to become visible
				// (this is necessary if the doAction was actually very fast)
				while (!cancelDialog.isVisible()) {
					try {
						wait(10);
					} catch (InterruptedException e) {
						// do nothing
					}
				}
				cancelDialog.setVisible(false);
			}
			finish();
		}
		
		/** 
		 * Method that should contain the code to be executed in parallel.
		 * It is invoked as a callback method from {@link #run()}.
		 */ 
		abstract protected void doAction();
		
		@Override
	    public void start() {
	        super.start();
	        // make dialog visible
	        cancelDialog.setVisible(true);
	        // wait for the thread to return
	        try {
	        	this.join();
	        } catch (InterruptedException exc) {
	        	// thread is done
	        }
	        synchronized (cancelDialog) {
	        	cancelDialog.dispose();
	        }
	    }

		/**
		 * Every thread might perform some tasks after the action finished.
		 */
		public abstract void finish();

		/**
	     * Hook to give subclasses the opportunity to put something on the
	     * cancel dialog.
	     * Note that this callback method is invoked at construction time,
	     * so should not make reference to instance variables.
	     */
	    protected Object createCancelDialogContent() {
	    	return new JLabel();
	    }
	    
		/**
		 * Creates a modal dialog that will interrupt this thread,
		 * when the cancel button is pressed.
		 * @param parentComponent the parent for the dialog
		 * @param title the title of the dialog
		 */
		private JDialog createCancelDialog(Component parentComponent, String title) {
			JDialog result;
			// create message dialog
		    JOptionPane message = new JOptionPane(createCancelDialogContent(), JOptionPane.PLAIN_MESSAGE);
		    JButton cancelButton = new JButton("Cancel");
		    // add a button to interrupt the generation process and
		    // wait for the thread to finish and rejoin this one
		    cancelButton.addActionListener(createCancelListener());
		    message.setOptions(new Object[] { cancelButton });
		    result = message.createDialog(parentComponent, title);
		    result.pack();
		    return result;
		}

		/** 
		 * Returns a listener to this {@link GenerateThread} that 
		 * interrupts the thread and waits for it to rejoin this thread.
		 */
		private ActionListener createCancelListener() {
		    return new ActionListener() {
		        public void actionPerformed(ActionEvent evt) {
		            CancellableThread.this.interrupt();
		        }
		    };
		}

		/** Dialog for cancelling the thread. */
		private final JDialog cancelDialog;
	}

	
	/** Interface for actions that should be refreshed upon changes. */
	private interface Refreshable { 
		/** 
		 * Callback method to refresh attributes of the action
		 * such as its name and enabledness status.
		 */
		void refresh();
	}
	
    /**
     * Action for displaying an about box.
     */
    private class AboutAction extends AbstractAction {
    	/** Constructs an instance of the action. */
        AboutAction() {
            super(Options.ABOUT_ACTION_NAME);
        }

        public void actionPerformed(ActionEvent evt) {
            new AboutBox(getFrame());
        }
    }

    /**
     * Action for applying the current derivation to the current state.
     * @see Simulator#applyTransition()
     */
    private class ApplyTransitionAction extends AbstractAction implements Refreshable {
    	/** Constructs an instance of the action. */
        ApplyTransitionAction() {
            super(Options.APPLY_TRANSITION_ACTION_NAME);
            putValue(Action.ACCELERATOR_KEY, Options.APPLY_KEY);
    		addRefreshable(this);
            addAccelerator(this);
        }

        public void actionPerformed(ActionEvent evt) {
            // applyTransition();
        	applyMatch();
        }

		public void refresh() {
			//setEnabled(getCurrentTransition() != null);
			setEnabled(getCurrentMatch() != null);
		}
    }

    private class CopyRuleAction extends AbstractAction implements Refreshable {
    	CopyRuleAction() {
    		super(Options.COPY_RULE_ACTION_NAME);
    		addRefreshable(this);
    	}
    	
		public void refresh() {
			setEnabled(getCurrentRule() != null);
		}
		
		public void actionPerformed(ActionEvent e) {
			if (confirmAbandon(false)) {
				AspectGraph oldRuleGraph = getCurrentRule().getAspectGraph();
				RuleNameLabel newRuleName = askNewRuleName(null, getCurrentRule().getNameLabel().name(), true);
				if (newRuleName != null) {
					doAddRule(newRuleName, oldRuleGraph.clone());
				}
			}
		}
    }

    private class DeleteRuleAction extends AbstractAction implements Refreshable {
    	DeleteRuleAction() {
    		super(Options.DELETE_RULE_ACTION_NAME);
            putValue(ACCELERATOR_KEY, Options.DELETE_KEY);
            addAccelerator(this);
            addRefreshable(this);
    	}
    	
		public void refresh() {
			setEnabled(getCurrentRule() != null);
		}
		
		public void actionPerformed(ActionEvent e) {
			RuleNameLabel ruleName = getCurrentRule().getNameLabel();
	    	String question = String.format("Delete rule %s?", ruleName);
			if (confirmBehaviour(Options.DELETE_RULE_OPTION, question)) {
				doDeleteRule(ruleName);
			}
		}
    }

    /**
	 * Action for editing the current state or rule.
	 */
    private class EditGraphAction extends AbstractAction implements Refreshable {
    	/** Constructs an instance of the action. */
        EditGraphAction() {
            super(Options.EDIT_GRAPH_ACTION_NAME);
            addRefreshable(this);
        }

        /**
         * Checks if the enabling condition is satisfied, and if so, calls
         * {@link #setEnabled(boolean)}.
         */
        public void refresh() {
        	boolean enabled = getGraphPanel() == getStatePanel() && getCurrentGrammar() != null && getCurrentGrammar().getStartGraph() != null;
        	if (enabled != isEnabled()) {
				setEnabled(enabled);
        	}
        	if (enabled) {
        		getEditItem().setAction(this);
        		getEditItem().setAccelerator(Options.EDIT_KEY);
			}
        }

        /**
		 * Invokes the editor on the current state. Handles the execution of an
		 * <code>EditGraphAction</code>, if the current panel is the state
		 * panel.
		 */
        public void actionPerformed(ActionEvent e) {
        	GraphJModel stateModel = getStatePanel().getJModel();
        	final String stateName = stateModel.getName();
            EditorDialog dialog = new EditorDialog(getFrame(), getOptions(), stateModel.toPlainGraph()) {
                @Override
                public void finish() {
                    File saveFile = handleSaveGraph(true, toPlainGraph(), stateName);
                    if (saveFile != null && confirmLoadStartState(saveFile.getName())) {
                        doLoadStartGraph(saveFile);
                    }
                }
            };
            dialog.start();
        }
    }

    private class EditRulePropertiesAction extends AbstractAction implements Refreshable {
    	EditRulePropertiesAction() {
    		super(Options.RULE_PROPERTIES_ACTION_NAME);
    		addRefreshable(this);
    	}
    	
		public void refresh() {
			setEnabled(getCurrentRule() != null);
		}
		
		public void actionPerformed(ActionEvent e) {
			AspectualRuleView rule = getCurrentRule();
			AspectGraph ruleGraph = rule.getAspectGraph();
			GraphProperties ruleProperties = GraphInfo.getProperties(ruleGraph,
					true);
			PropertiesDialog dialog = new PropertiesDialog(ruleProperties,
					GraphProperties.DEFAULT_USER_KEYS, true);
			if (dialog.showDialog(getFrame()) && confirmAbandon(false)) {
				ruleProperties.clear();
				ruleProperties.putAll(dialog.getEditedProperties());
				doDeleteRule(rule.getNameLabel());
				doAddRule(rule.getNameLabel(), ruleGraph);
			}
		}
	}

    /**
     * Action for editing the current state or rule.
     */
    private class EditRuleAction extends AbstractAction implements Refreshable {
    	/** Constructs an instance of the action. */
        EditRuleAction() {
            super(Options.EDIT_RULE_ACTION_NAME);
            addRefreshable(this);
        }

        /**
         * Checks if the enabling condition is satisfied, and if so, calls
         * {@link #setEnabled(boolean)}.
         */
        public void refresh() {
        	boolean enabled = getCurrentRule() != null;
			if (enabled != isEnabled()) {
				setEnabled(enabled);
			}
			if (getGraphPanel() == getRulePanel()) {
				getEditItem().setAction(this);
				getEditItem().setAccelerator(Options.EDIT_KEY);
			}
        }

        /**
		 * Invokes the editor on the current rule. Handles the execution of an
		 * <code>EditGraphAction</code>, if the current panel is the rule
		 * panel.
		 * 
		 * @require <tt>getCurrentRule != null</tt>.
		 */
        public void actionPerformed(ActionEvent e) {
        	final RuleNameLabel ruleName = getCurrentRule().getNameLabel();
            EditorDialog dialog = new EditorDialog(getFrame(), getOptions(), getRulePanel().getJModel().toPlainGraph()) {
                @Override
                public void finish() {
                    if (confirmAbandon(false)) {
                        AspectGraph ruleAsAspectGraph = toAspectGraph();
                        RuleNameLabel newRuleName = askNewRuleName("Name for edited rule", ruleName.name(), false);
                        if (newRuleName != null) {
                            doAddRule(newRuleName, ruleAsAspectGraph);
                        }
                    }
                }
            };
            dialog.start();
        }
    }

    /** Action to show the system properties. */
    private class EditSystemPropertiesAction extends AbstractAction implements Refreshable {
    	/** Constructs an instance of the action. */
        EditSystemPropertiesAction() {
            super(Options.SYSTEM_PROPERTIES_ACTION_NAME);
            addRefreshable(this);
        }
        
        /** 
         * Displays a {@link PropertiesDialog} for the properties
         * of the edited graph.
         */
        public void actionPerformed(ActionEvent e) {
        	DefaultGrammarView grammar = getCurrentGrammar();
        	Properties systemProperties = grammar.getProperties();
        	PropertiesDialog dialog = new PropertiesDialog(systemProperties, SystemProperties.DEFAULT_KEYS, true);
        	if (dialog.showDialog(getFrame()) && confirmAbandon(false)) {
        		SystemProperties newProperties = new SystemProperties();
        		newProperties.putAll(dialog.getEditedProperties());
        		try {
            		String outputFileName = Groove.createPropertyFilter().addExtension(grammar.getName());
            		File outputFile = new File(getCurrentGrammarFile(), outputFileName);
            		outputFile.createNewFile();
            		OutputStream writer = new FileOutputStream(outputFile);
					newProperties.store(writer, String.format(SystemProperties.DESCRIPTION, grammar.getName()));
					grammar.setProperties(newProperties);
					setGrammar(grammar);
				} catch (IOException exc) {
					showErrorDialog("Error while saving edited properties", exc);
				}
        	}
        }
        
        /**
         * Tests if the currently selected grammar has non-<code>null</code>
         * system properties.
         */
        public void refresh() {
        	setEnabled(getCurrentGrammar() != null);
        }
    }
    
    /**
     * Action to save the state, as a graph or in some export format.
     * @see Exporter#export(JGraph, File)
     */
    private class ExportGraphAction extends AbstractAction implements Refreshable {
    	/** Constructs an instance of the action. */
        ExportGraphAction() {
            super(Options.EXPORT_ACTION_NAME);
            putValue(ACCELERATOR_KEY, Options.EXPORT_KEY);
            addRefreshable(this);
        }

        public void actionPerformed(ActionEvent e) {
            String fileName;
            JGraph jGraph;
            if (getGraphPanel() == getLtsPanel()) {
                fileName = "lts";
                jGraph = getLtsPanel().getJGraph();
            } else if (getGraphPanel() == getStatePanel()) {
                fileName = getCurrentState().toString();
                jGraph = getStatePanel().getJGraph();
            } else {
                fileName = getCurrentRule().getNameLabel().toString();
                jGraph = getRulePanel().getJGraph();
            }
            getExporter().getFileChooser().setSelectedFile(new File(fileName));
            File selectedFile = ExtensionFilter.showSaveDialog(getExporter().getFileChooser(), getFrame(), null);
            // now save, if so required
            if (selectedFile != null) {
                try {
                	getExporter().export(jGraph, selectedFile); 
                } catch (IOException exc) {
                    new ErrorDialog(getFrame(), "Error while exporting to " + selectedFile, exc);
                }

            }
        }

        /**
         * Tests if the action should be enabled according to the current state of the simulator,
         * and also modifies the action name.
         */
        public void refresh() {
            if (getGraphPanel() == getLtsPanel()) {
                setEnabled(getCurrentGTS() != null);
                putValue(NAME, Options.EXPORT_LTS_ACTION_NAME);
            } else if (getGraphPanel() == getStatePanel()) {
                setEnabled(getCurrentState() != null);
                putValue(NAME, Options.EXPORT_STATE_ACTION_NAME);
            } else {
                setEnabled(getCurrentRule() != null);
                putValue(NAME, Options.EXPORT_RULE_ACTION_NAME);
            }
        }
    }

    /** 
     * Action that changes the enabledness status of the currently selected rule.
     * @see #doEnableRule()
     */
    private class EnableRuleAction extends AbstractAction implements Refreshable {
    	EnableRuleAction() {
    		super(Options.DISABLE_ACTION_NAME);
    		addRefreshable(this);
    	}
    	
		public void refresh() {
			boolean ruleSelected = getCurrentRule() != null;
			setEnabled(ruleSelected);
			if (ruleSelected && getCurrentRule().isEnabled()) {
				putValue(NAME, Options.DISABLE_ACTION_NAME);
			} else {
				putValue(NAME, Options.ENABLE_ACTION_NAME);
			}
		}
		
		public void actionPerformed(ActionEvent e) {
			if (confirmAbandon(false)) {
				doEnableRule();
			}
		}
    }

    
    public LaunchScenarioAction createLaunchScenarioAction(ScenarioHandler handler) {
    	return new LaunchScenarioAction(handler);
    }
    
    
    private class LaunchScenarioAction extends AbstractAction {
       	LaunchScenarioAction(ScenarioHandler handler) {
            super(handler.getName());
            this.handler = handler;
        }
       	
       	public ScenarioHandler getExploreStrategy() {
            return handler;
        }
       	
		@Override
        public void setEnabled(boolean enabled) {
            super.setEnabled(enabled);
            putValue(Action.NAME, handler.getName());
        }

        public void actionPerformed(ActionEvent evt) {
        	doGenerate(handler);
        }
     	
		private final ScenarioHandler handler;
    }
    
	void doGenerate(ScenarioHandler handler) {
	    GraphJModel ltsJModel = getLtsPanel().getJModel();
	    synchronized (ltsJModel) {
	        // unhook the lts' jmodel from the lts, for efficiency's sake
	    	getCurrentGTS().removeGraphListener(ltsJModel);
	        // disable rule application for the time being
	        boolean applyEnabled = getApplyTransitionAction().isEnabled();
	        getApplyTransitionAction().setEnabled(false);
	        // create a thread to do the work in the background
	        Thread generateThread = new LaunchThread(handler);
	        // go!
	        generateThread.start();
	        // get the lts' jmodel back on line and re-synchronize its state
	        ltsJModel.reload();
	        // re-enable rule application
	        getApplyTransitionAction().setEnabled(applyEnabled);
	        // reset lts display visibility
	        setGraphPanel(getLtsPanel());
	    }
	    LTSJGraph ltsJGraph = getLtsPanel().getJGraph();
	    if (ltsJGraph.getLayouter() != null) {
	        ltsJGraph.getLayouter().start(false);
	    }
	}
	
	
	/**
	 * Thread class to wrap the exploration of the simulator's current GTS.
	 */
	private class LaunchThread extends CancellableThread {
	    /**
	     * Constructs a generate thread for a given exploration stragegy.
	     * @param strategy the exploration strategy of this thread
	     */
	    LaunchThread(ScenarioHandler handler) {
	    	super(getLtsPanel(), "Exploring state space");
	        this.handler = handler;
	        this.progressListener = createProgressListener();
	    }

		@Override
		public void doAction() {
			GTS gts = getCurrentGTS();
			displayProgress(gts);
			gts.addGraphListener(progressListener);
			try {
				handler.playScenario();
			} catch (InterruptedException exc) {
				// proceed
			}

			
			gts.removeGraphListener(progressListener);
		}

		@Override
		public void finish() {
//			setResult();
			showResult();
		}

		/** This implementation returns the state and transition count labels. */
		@Override
		protected Object createCancelDialogContent() {
			return new Object[] { getStateCountLabel(), getTransitionCountLabel() };
		}

		/** 
		 * Creates a graph listener that displays the progress of the generate
		 * thread on the cancel dialog.
		 */
		private GraphListener createProgressListener() {
			return new GraphAdapter() {
				@Override
				public void addUpdate(GraphShape graph, Node node) {
				    displayProgress(graph);
				}
				
				@Override
				public void addUpdate(GraphShape graph, groove.graph.Edge edge) {
				    displayProgress(graph);
				}
			};
		}
		
		/**
		 * Returns the {@link JLabel} used to display the state count in the
		 * cencel dialog; first creates the label if that is not yet done.
		 */
		private JLabel getStateCountLabel() {
			// lazily create the label
			if (stateCountLabel == null) {
				stateCountLabel = new JLabel();
			}
			return stateCountLabel;
		}
		
		/**
		 * Returns the {@link JLabel} used to display the state count in the
		 * cencel dialog; first creates the label if that is not yet done.
		 */
		private JLabel getTransitionCountLabel() {
			// lazily create the label
			if (transitionCountLabel == null) {
				transitionCountLabel = new JLabel();
			}
			return transitionCountLabel;
		}

		/**
		 * Displays the number of lts states and transitions in the message dialog.
		 */
		void displayProgress(GraphShape graph) {
		    getStateCountLabel().setText("States: " + graph.nodeCount());
		    getTransitionCountLabel().setText("Transitions: " + graph.edgeCount());
		}

		private void showResult() {
			Collection<? extends Object> result = handler.getResult();
			Collection<GraphState> states = new HashSet<GraphState>();
			for (Object object: result) {
				if (object instanceof GraphState) {
					states.add((GraphState) object);
				}
			}
			visualize = states;
		}
		/** LTS generation strategy of this thread. */
		private final ScenarioHandler handler;
		/** Progress listener for the generate thread. */
		private final GraphListener progressListener;
		/** Label displaying the number of states generated so far. */
		private JLabel transitionCountLabel;
		/** Label displaying the number of transitions generated so far. */
		private JLabel stateCountLabel;
	}

    /**
     * Action for setting the initial state of the LTS as current state.
     * @see GTS#startState()
     * @see Simulator#setState(GraphState)
     */
    private class GotoStartStateAction extends AbstractAction implements Refreshable {
    	/** Constructs an instance of the action. */
        GotoStartStateAction() {
            super(Options.GOTO_START_STATE_ACTION_NAME);
            putValue(ACCELERATOR_KEY, Options.GOTO_START_STATE_KEY);
            addRefreshable(this);
        }

        public void actionPerformed(ActionEvent evt) {
            setState(getCurrentGTS().startState());
        }

		public void refresh() {
			setEnabled(getCurrentGTS() != null);
		}
    }
    
    /**
     * HARMEN: Temporary class for showing results of model checking. 
     * @author Harmen Kastenberg
     *
     */
    private class ShowResultAction extends AbstractAction implements Refreshable {
    	/** Constructs an instance of the action. */
        public ShowResultAction() {
			// TODO Auto-generated constructor stub
            super("Show Result");
//            putValue(ACCELERATOR_KEY, Options.GOTO_START_STATE_KEY);
//            addRefreshable(this);
        }

        public void actionPerformed(ActionEvent evt) {
        	if (visualize != null) {
        		notifyCounterExample(visualize);
        	}
        }

		public void refresh() {
			setEnabled(getCurrentGTS() != null);
		}
    }

    /**
     * Action for loading and setting a new initial state.
     * @see Simulator#doLoadStartGraph(File)
     */
    private class LoadStartGraphAction extends AbstractAction implements Refreshable {
    	/** Constructs an instance of the action. */
        LoadStartGraphAction() {
            super(Options.LOAD_START_STATE_ACTION_NAME);
            addRefreshable(this);
        }

        public void actionPerformed(ActionEvent evt) {
//            stateFileChooser.setSelectedFile(currentStartStateFile);
            int result = getStateFileChooser().showOpenDialog(getFrame());
            // now load, if so required
            if (result == JFileChooser.APPROVE_OPTION && confirmAbandon(false)) {
                doLoadStartGraph(getStateFileChooser().getSelectedFile());
            }
        }
        
        /** Sets the enabling status of this action, depending on whether a grammar is currently loaded. */ 
        public void refresh() {
            setEnabled(getCurrentGrammar() != null);
        }
    }

    /**
     * Action for loading a new rule system.
     * @see Simulator#doLoadGrammar(AspectualViewGps, File, String)
     */
    private class LoadGrammarAction extends AbstractAction {
    	/** Constructs an instance of the action. */
        LoadGrammarAction() {
            super(Options.LOAD_GRAMMAR_ACTION_NAME);
            putValue(ACCELERATOR_KEY, Options.OPEN_KEY);
            addAccelerator(this);
        }

        public void actionPerformed(ActionEvent evt) {
            int result = getGrammarFileChooser().showOpenDialog(getFrame());
            // now load, if so required
            if (result == JFileChooser.APPROVE_OPTION && confirmAbandon(false)) {
                File selectedFile = getGrammarFileChooser().getSelectedFile();
                FileFilter filterUsed = getGrammarFileChooser().getFileFilter();
                doLoadGrammar(getGrammarLoaderMap().get(filterUsed), selectedFile, null);
            }
        }
    }

    /** Action to create and load a new, initially empty graph grammar. */
    private class NewGrammarAction extends AbstractAction {
        NewGrammarAction() {
            super(Options.NEW_GRAMMAR_ACTION_NAME);
        }
        
        public void actionPerformed(ActionEvent e) {
            if (confirmAbandon(true)) {
                File grammarFile = getCurrentGrammarFile();
                File newGrammar;
                if (grammarFile == null) {
                    newGrammar = new File(NEW_GRAMMAR_NAME);
                } else {
                    newGrammar = new File(grammarFile.getParentFile(), NEW_GRAMMAR_NAME);
                }
            	getGrammarFileChooser().setSelectedFile(newGrammar);
            	boolean ok = false;
            	while (!ok) {
                if (getGrammarFileChooser().showOpenDialog(getFrame()) == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = getGrammarFileChooser().getSelectedFile();
                    FileFilter filter = getGrammarFileChooser().getFileFilter();
                    AspectualViewGps grammarLoader = getGrammarLoaderMap().get(filter);
                    if (filter instanceof ExtensionFilter) {
                        String extendedName = ((ExtensionFilter) filter).addExtension(selectedFile.getPath());
                        selectedFile = new File(extendedName);
                    }
                    if (selectedFile.exists()) {
                    	int response = JOptionPane.showConfirmDialog(getFrame(), String.format("Load existing grammar %s?", selectedFile.getName()));
                    	if (response == JOptionPane.OK_OPTION) {
                    		doLoadGrammar(grammarLoader, selectedFile, null);
                    	}
                    	ok = response != JOptionPane.NO_OPTION;
                    } else {
                        doNewGrammar(grammarLoader, selectedFile);
                        ok = true;
                    }
                } else {
                	ok = true;
                }
            	}
            }
        }
    }

    private class NewGraphAction extends AbstractAction implements Refreshable {
    	NewGraphAction() {
    		super(Options.NEW_GRAPH_ACTION_NAME);
    		addRefreshable(this);
    	}
    	
		public void actionPerformed(ActionEvent e) {
            Graph newGraph = GraphFactory.getInstance().newGraph();
            GraphInfo.setName(newGraph, NEW_GRAPH_NAME);
            GraphInfo.setGraphRole(newGraph);
            EditorDialog dialog = new EditorDialog(getFrame(), getOptions(), newGraph) {
                @Override
                public void finish() {
                    Graph newGraph = toPlainGraph();
                    File saveFile = handleSaveGraph(true, newGraph, NEW_GRAPH_NAME);
                    if (saveFile != null && confirmLoadStartState(saveFile.getName())) {
                        doLoadStartGraph(saveFile);
                    }
                }
            };
            dialog.start();
        }
        
        /** Enabled if there is a grammar loaded. */
        public void refresh() {
            setEnabled(getCurrentGrammar() != null);
        }
    }

    private class NewRuleAction extends AbstractAction implements Refreshable {
    	NewRuleAction() {
    		super(Options.NEW_RULE_ACTION_NAME);
    		addRefreshable(this);
    	}
    	
		public void actionPerformed(ActionEvent e) {
			if (confirmAbandon(false)) {
				final RuleNameLabel ruleName = askNewRuleName(null, NEW_RULE_NAME, true);
				if (ruleName != null) {
                    Graph newRule = GraphFactory.getInstance().newGraph();
                    GraphInfo.setName(newRule, ruleName.name());
                    GraphInfo.setRuleRole(newRule);
                    EditorDialog dialog = new EditorDialog(getFrame(), getOptions(), newRule) {
                        @Override
                        public void finish() {
                            doAddRule(ruleName, toAspectGraph());
                        }
                    };
                    dialog.start();
				}
			}
		}

        /** Enabled if there is a grammar loaded. */
        public void refresh() {
            setEnabled(getCurrentGrammar() != null);
        }
    }

    /**
     * Action for inputting a CTL formula.
     */
    private class ProvideTemporalFormulaAction extends AbstractAction implements Refreshable {
    	/** Constructs an instance of the action. */
    	ProvideTemporalFormulaAction() {
    		super(Options.PROVIDE_CTL_FORMULA_ACTION_NAME);
    		setEnabled(true);
    		addRefreshable(this);
    	}

    	public void actionPerformed(ActionEvent evt) {
    		int goOn = 0;
    		// if there are still open states the result might be different as expected
    		// ask the user whether really to continue
        	if (getCurrentGTS().hasOpenStates()) {
        		String message = "The transition system still contains open states. Do you want to contiue verifying it?";
        		goOn = JOptionPane.showConfirmDialog(getFrame(), message, "Open states", JOptionPane.YES_NO_OPTION);
        	}
        	if (goOn == JOptionPane.YES_OPTION) {
        		FormulaDialog dialog = getFormulaDialog();
        		dialog.showDialog(getFrame());
        		String property = dialog.getProperty();
        		if (property != null) {
        			verifyProperty(property);
        		}
        	}
    	}

    	public void refresh() {
    		setEnabled(getCurrentGrammar() != null);
    	}
    }

    /**
     * Action for quitting the simulator.
     * @see Simulator#doQuit()
     */
    private class QuitAction extends AbstractAction {
    	/** Constructs an instance of the action. */
        QuitAction() {
            super(Options.QUIT_ACTION_NAME);
            putValue(ACCELERATOR_KEY, Options.QUIT_KEY);
        }

        public void actionPerformed(ActionEvent evt) {
            doQuit();
        }
    }
    
    /**
     * Action for refreshing the rule system. Reloads the current rule system and start graph.
     * @see Simulator#doRefreshGrammar()
     */
    private class RefreshGrammarAction extends AbstractAction implements Refreshable {
    	/** Constructs an instance of the action. */
        RefreshGrammarAction() {
            super(Options.REFRESH_GRAMMAR_ACTION_NAME);
            putValue(ACCELERATOR_KEY, Options.REFRESH_KEY);
            addAccelerator(this);
            addRefreshable(this);
        }

        public void actionPerformed(ActionEvent evt) {
            if (confirmAbandon(false)) {
                doRefreshGrammar();
            }
        }

		public void refresh() {
            setEnabled(getCurrentGrammar() != null);
		}
    }

    private class RenameRuleAction extends AbstractAction implements Refreshable {
    	RenameRuleAction() {
    		super(Options.RENAME_RULE_ACTION_NAME);
    		addRefreshable(this);
    		/* The F2-accelerator is not working, but I do not know why 
            putValue(ACCELERATOR_KEY, Options.RELABEL_KEY);
            addAccelerator(this);
            */
    	}
    	
		public void refresh() {
			setEnabled(getCurrentRule() != null);
		}
		
		public void actionPerformed(ActionEvent e) {
			if (confirmAbandon(true)) {
				RuleNameLabel oldRuleName = getCurrentRule().getNameLabel();
				AspectGraph ruleGraph = getCurrentRule().getAspectGraph();
				RuleNameLabel newRuleName = askNewRuleName(null, oldRuleName.name(), true);
				if (newRuleName != null) {
					doDeleteRule(oldRuleName);
					doAddRule(newRuleName, ruleGraph);
				}
			}
		}
    }

    /**
     * Action for saving a rule system. Currently not enabled.
     */
    private class SaveGrammarAction extends AbstractAction implements Refreshable {
    	/** Constructs an instance of the action. */
        SaveGrammarAction() {
            super(Options.SAVE_GRAMMAR_ACTION_NAME);
            putValue(ACCELERATOR_KEY, Options.SAVE_KEY);
            addRefreshable(this);
        }

        public void actionPerformed(ActionEvent evt) {
            int result = getGrammarFileChooser().showSaveDialog(getFrame());
            // now save, if so required
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = getGrammarFileChooser().getSelectedFile();
                if (confirmOverwriteGrammar(selectedFile)) {
                	FileFilter filterUsed = getGrammarFileChooser().getFileFilter();
                	doSaveGrammar(getGrammarLoaderMap().get(filterUsed), selectedFile);
                }
            }
        }

		public void refresh() {
            setEnabled(getCurrentGrammar() != null);
		}
    }

    /**
     * Action to save the state or LTS as a graph.
     * @see Simulator#handleSaveGraph(boolean, Graph, String)
     * @see Simulator#doSaveGraph(Graph, File)
     */
    private class SaveGraphAction extends AbstractAction implements Refreshable {
    	/** Constructs an instance of the action. */
        SaveGraphAction() {
            super(Options.SAVE_ACTION_NAME);
            putValue(ACCELERATOR_KEY, Options.SAVE_GRAPH_KEY);
            addRefreshable(this);
        }

        public void actionPerformed(ActionEvent e) {
        	Graph graph = getGraphPanel().getJModel().toPlainGraph();
            if (getGraphPanel() == getLtsPanel()) {
                handleSaveGraph(false, graph, LTS_FILE_NAME);
            } else {
                handleSaveGraph(true, graph, getCurrentState().toString());
            }
        }

        /**
         * Tests if the action should be enabled according to the current state of the simulator,
         * and also modifies the action name.
         * 
         */
        public void refresh() {
        	if (getGraphPanel() == getLtsPanel()) {
                setEnabled(getCurrentGTS() != null);
                putValue(NAME, Options.SAVE_LTS_ACTION_NAME);
            } else if (getGraphPanel() == getStatePanel()) {
                setEnabled(getCurrentState() != null);
                putValue(NAME, Options.SAVE_STATE_ACTION_NAME);
            } else {
                setEnabled(false);
                putValue(NAME, Options.SAVE_ACTION_NAME);
            }
        }
    }

    private class StartSimulationAction extends AbstractAction implements Refreshable {
        /** Constructs an instance of the action. */
        StartSimulationAction() {
            super(Options.START_SIMULATION_ACTION_NAME);
            putValue(Action.ACCELERATOR_KEY, Options.START_SIMULATION_KEY);
            addRefreshable(this);
        }

        public void actionPerformed(ActionEvent e) {
            if (confirmAbandon(false)) {
                startSimulation(getCurrentGrammar());
            }
        }

        public void refresh() {
            boolean enabled = getCurrentGrammar() != null && getCurrentGrammar().getErrors().isEmpty();
            setEnabled(enabled);
        }
        
        String getKeyText() {
            return KeyEvent.getKeyText(Options.START_SIMULATION_KEY.getKeyCode());
        }
    }
    
    /** A variant of {@link Simulator.StartSimulationAction} for abstract simulation. */
    private class StartAbstrSimulationAction extends AbstractAction implements Refreshable {
        /** Constructs an instance of the action. */
        StartAbstrSimulationAction() {
            super(Options.START_ABSTR_SIMULATION_ACTION_NAME);
            addRefreshable(this);
        }

        public void actionPerformed(ActionEvent e) {
            if (confirmAbandon(false)) {
                startAbstrSimulation(getCurrentGrammar());
            }
        }

        public void refresh() {
        	//IOVKA to be changed in order to activate the menu item for abstract simulation
//        	boolean enabled = false;
            boolean enabled = getCurrentGrammar() != null && getCurrentGrammar().getErrors().isEmpty();
        	setEnabled(enabled);
        }
        
        String getKeyText() { return null; }
    }
    
    
    ExploreStateStrategy exploreState;
    /**
     * Returns the explore-strategy for exploring a single state.
     * @return the explore-strategy for exploring a single state
     */
    public ExploreStateStrategy getExploreState() {
    	if (this.exploreState == null) {
    		this.exploreState = new ExploreStateStrategy();
    	}
    	return this.exploreState;
    }

    public Collection<GraphState> visualize;
}
