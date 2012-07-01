/*
 * GROOVE: GRaphs for Object Oriented VErification
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
 * $Id: Simulator.java,v 1.1.1.2 2007-03-20 10:42:45 kastenberg Exp $
 */
package groove.gui;

import groove.graph.Graph;
import groove.graph.GraphAdapter;
import groove.graph.GraphFormatException;
import groove.graph.GraphListener;
import groove.graph.GraphShape;
import groove.graph.Node;
import groove.gui.jgraph.GraphJModel;
import groove.gui.jgraph.JCell;
import groove.gui.jgraph.JGraph;
import groove.gui.jgraph.JModel;
import groove.gui.jgraph.LTSJGraph;
import groove.gui.jgraph.LTSJModel;
import groove.gui.jgraph.RuleJModel;
import groove.io.ExtensionFilter;
import groove.io.GpsGrammar;
import groove.io.GrooveFileChooser;
import groove.io.LayedOutXml;
import groove.io.XmlException;
import groove.io.XmlGrammar;
import groove.lts.DerivedGraphRuleFactory;
import groove.lts.ExploreStrategy;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.State;
import groove.lts.StateGenerator;
import groove.lts.Transition;
import groove.trans.NameLabel;
import groove.trans.Rule;
import groove.trans.RuleFactory;
import groove.trans.match.MatchingMatcher;
import groove.trans.view.RuleGraph;
import groove.trans.view.RuleViewGrammar;
import groove.util.Converter;
import groove.util.ExprFormatException;
import groove.util.Groove;
import groove.verify.CTLFormula;
import groove.verify.CTLModelChecker;
import groove.verify.TemporalFormula;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import net.sf.epsgraphics.ColorMode;
import net.sf.epsgraphics.EpsGraphics;

/**
 * Program that applies a production system to an initial graph.
 * @author Arend Rensink
 * @version $Revision: 1.1.1.2 $
 */
public class Simulator {
    /**
     * Name of the LTS file, when it is isaved or exported.
     */
    static private final String LTS_FILE_NAME = "lts";

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
	        simulator.start();
	    } catch (XmlException exc) {
	        exc.printStackTrace();
	        System.out.println(exc.getMessage());
	        // System.exit(0);
	    } catch (IOException exc) {
	        exc.printStackTrace();
	        System.out.println(exc.getMessage());
	        // System.exit(0);
	    }
	}

	/**
	 * Class that spawns a thread to perform a long-lasting action,
	 * while displaying a dialog that can interrupt the thread.
	 */
	abstract private class CancellableAction extends Thread {
	    /**
	     * Constructs an action that can be cancelled through a dialog.
	     * @param parentComponent the parent for the cancel dialog
	     * @param cancelDialogTitle the title of the cancel dialog
	     */
		public CancellableAction(Component parentComponent, String cancelDialogTitle) {
	        this.cancelDialog = createCancelDialog(parentComponent, cancelDialogTitle);
	    }
	
		/**
		 * Calls {@link #doAction()}, then disposes the cancel dialog.
		 */
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
		}
		
		/** 
		 * Method that should contain the code to be executed in parallel.
		 * It is invoked as a callback method from {@link #run()}.
		 */ 
		abstract protected void doAction();
		
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
	        cancelDialog.dispose();
	    }
	
	    /**
	     * Hook to give subclasses the opportunity to put something on the
	     * cancel dialog.
	     * Note that this callback mathod is invoked at construction time,
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
		            CancellableAction.this.interrupt();
		        }
		    };
		}

		/** Dialog for cancelling the thread. */
		private final JDialog cancelDialog;
	}

	/**
	 * Thread class to wrap the exploration of the simulator's current GTS.
	 */
	private class GenerateThread extends CancellableAction {
	    /**
	     * Constructs a generate thread for a given exploration stragegy.
	     * @param strategy the exploration strategy of this thread
	     */
	    GenerateThread(ExploreStrategy strategy) {
	    	super(ltsPanel, "Exploring state space");
	        this.strategy = strategy;
	        this.progressListener = createProgressListener();
	    }

		public void doAction() {
			GTS gts = currentGrammar.gts();
			displayProgress(gts);
//			gts.addGraphListener(progressListener);
			try {
				strategy.setLTS(gts);
//				gts.setExploreStrategy(strategy);
				strategy.setAtState(currentState);
				strategy.explore();
			} catch (InterruptedException exc) {
				// proceed
			}
			gts.removeGraphListener(progressListener);
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
				public void addUpdate(GraphShape graph, Node node) {
				    displayProgress(graph);
				}
				
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
		private void displayProgress(GraphShape graph) {
		    getStateCountLabel().setText("States: " + graph.nodeCount());
		    getTransitionCountLabel().setText("Transitions: " + graph.edgeCount());
		}

		/** LTS generation strategy of this thread. */
		private final ExploreStrategy strategy;
		/** Progress listener for the generate thread. */
		private final GraphListener progressListener;
		/** Label displaying the number of states generated so far. */
		private JLabel transitionCountLabel;
		/** Label displaying the number of transitions generated so far. */
		private JLabel stateCountLabel;
	}

	/**
     * Action to generate (and view) part of the LTS.
     */
    protected class GenerateLTSAction extends AbstractAction {
        /**
         * Constructs a generate action with a given explore strategy.
         * @param strategy the strategy to be used during exploration
         */
        protected GenerateLTSAction(ExploreStrategy strategy) {
            super(strategy.toString());
            this.strategy = strategy;
        }

        /**
         * Returns the explore strategy to which this generate action is initialized.
         */
        public ExploreStrategy getExploreStrategy() {
            return strategy;
        }

        /**
         * Extends superclass method with a change of action name.
         */
        public void setEnabled(boolean enabled) {
            super.setEnabled(enabled);
            putValue(Action.NAME, strategy.toString());
        }

        public void actionPerformed(ActionEvent evt) {
        	doGenerate(strategy);
        }

        /** The exploration strategy for the action. */
		private final ExploreStrategy strategy;
    }

    /**
     * Action for setting the initial state of the LTS as current state.
     * @see GTS#startState()
     * @see Simulator#setState(GraphState)
     */
    protected class GotoStartStateAction extends AbstractAction {
    	/** Constructs an instance of the action. */
        protected GotoStartStateAction() {
            super(Options.GOTO_START_STATE_ACTION_NAME);
            putValue(ACCELERATOR_KEY, Options.GOTO_START_STATE_KEY);
            setEnabled(false);
        }

        public void actionPerformed(ActionEvent evt) {
            setState(currentGrammar.gts().startState());
        }
    }

    /**
     * Action for applying the current derivation to the current state.
     * @see Simulator#applyTransition()
     */
    protected class ApplyTransitionAction extends AbstractAction {
    	/** Constructs an instance of the action. */
        protected ApplyTransitionAction() {
            super(Options.APPLY_TRANSITION_ACTION_NAME);
            putValue(Action.ACCELERATOR_KEY, Options.APPLY_KEY);
            setEnabled(false);
        }

        public void actionPerformed(ActionEvent evt) {
            applyTransition();
        }
    }

    /**
     * Action for inputting a CTL formula.
     */
    protected class ProvideCTLFormulaAction extends AbstractAction {
    	/** Constructs an instance of the action. */
    	protected ProvideCTLFormulaAction() {
    		super(Options.PROVIDE_CTL_FORMULA_ACTION_NAME);
    		setEnabled(true);
    	}

    	public void actionPerformed(ActionEvent evt) {
    		String property = JOptionPane.showInputDialog(null, "Enter the temporal formula to be verified by GROOVE.");
    		if (property != null) {
    			verifyProperty(property);
    		} else {
    			// do nothing
    		}
    	}
    }
    /**
     * Action for loading and setting a new initial state.
     * @see Simulator#doLoadStartState(File)
     */
    protected class LoadStartStateAction extends AbstractAction {
    	/** Constructs an instance of the action. */
        protected LoadStartStateAction() {
            super(Options.LOAD_START_STATE_ACTION_NAME);
        }

        public void actionPerformed(ActionEvent evt) {
//            stateFileChooser.setSelectedFile(currentStartStateFile);
            int result = getStateFileChooser().showOpenDialog(frame);
            // now load, if so required
            if (result == JFileChooser.APPROVE_OPTION && confirmAbandon()) {
                doLoadStartState(getStateFileChooser().getSelectedFile());
            }
        }
    }

    /**
     * Action for loading a new rule system.
     * @see Simulator#doLoadGrammar(XmlGrammar, File, String)
     */
    protected class LoadGrammarAction extends AbstractAction {
    	/** Constructs an instance of the action. */
        protected LoadGrammarAction() {
            super(Options.LOAD_GRAMMAR_ACTION_NAME);
            putValue(ACCELERATOR_KEY, Options.OPEN_KEY);
        }

        public void actionPerformed(ActionEvent evt) {
            int result = getGrammarFileChooser().showOpenDialog(frame);
            // now load, if so required
            if (result == JFileChooser.APPROVE_OPTION && confirmAbandon()) {
                File selectedFile = getGrammarFileChooser().getSelectedFile();
                FileFilter filterUsed = getGrammarFileChooser().getFileFilter();
                doLoadGrammar(grammarLoaderMap.get(filterUsed), selectedFile, null);
            }
        }
    }

    /**
     * Action for refreshing the rule system. Reloads the current rule system and start graph.
     * @see Simulator#doRefreshGrammar()
     */
    protected class RefreshGrammarAction extends AbstractAction {
    	/** Constructs an instance of the action. */
        protected RefreshGrammarAction() {
            super(Options.REFRESH_GRAMMAR_ACTION_NAME);
            putValue(ACCELERATOR_KEY, Options.REFRESH_KEY);
        }

        public void actionPerformed(ActionEvent evt) {
            if (confirmAbandon()) {
                doRefreshGrammar();
            }
        }
    }

    /**
     * Action for saving a rule system. Currently not enabled.
     */
    protected class SaveGrammarAction extends AbstractAction {
    	/** Constructs an instance of the action. */
        protected SaveGrammarAction() {
            super(Options.SAVE_GRAMMAR_ACTION_NAME);
        }

        public void actionPerformed(ActionEvent evt) {
            // initialize current directory if necessary
            getGrammarFileChooser().rescanCurrentDirectory();

            int result = getGrammarFileChooser().showOpenDialog(frame);
            // now load, if so required
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = getGrammarFileChooser().getSelectedFile();
                // currentDirectory = selectedFile.getAbsoluteFile().getParentFile();
                try {
                    javax.swing.filechooser.FileFilter filterUsed = getGrammarFileChooser()
                            .getFileFilter();
                    XmlGrammar saver = grammarLoaderMap.get(filterUsed);
                    saver.marshal(currentGrammar, selectedFile);
                    currentGrammarFile = selectedFile;
                } catch (IOException exc) {
                    showErrorDialog("Error while exporting to " + selectedFile, exc);
                }
            }
        }
    }

    /**
     * Action for quiting the simulator.
     * @see Simulator#doQuit()
     */
    protected class QuitAction extends AbstractAction {
    	/** Constructs an instance of the action. */
        protected QuitAction() {
            super(Options.QUIT_ACTION_NAME);
            putValue(ACCELERATOR_KEY, Options.QUIT_KEY);
        }

        public void actionPerformed(ActionEvent evt) {
            doQuit();
        }
    }

    /**
     * Action for editing the current state or rule.
     * @see Simulator#handleEditState()
     * @see Simulator#handleEditRule()
     */
    protected class EditGraphAction extends AbstractAction {
    	/** Constructs an instance of the action. */
        protected EditGraphAction() {
            super(Options.EDIT_ACTION_NAME);
            putValue(ACCELERATOR_KEY, Options.EDIT_KEY);
        }

        /**
         * Checks if the enabling condition is satisfied, and if so, calls
         * {@link #setEnabled(boolean)}.
         */
        public void checkEnabled() {
            if (getGraphPanel() == statePanel) {
                setEnabled(getStatePanel().getJGraph().isEnabled());
                putValue(NAME, Options.EDIT_STATE_ACTION_NAME);
            } else if (getGraphPanel() == getRulePanel()) {
                setEnabled(getRulePanel().getJGraph().isEnabled());
                putValue(NAME, Options.EDIT_RULE_ACTION_NAME);
            } else {
                setEnabled(false);
                putValue(NAME, Options.EDIT_ACTION_NAME);
            }
        }

        public void actionPerformed(ActionEvent e) {
            if (getGraphPanel() == getStatePanel()) {
                handleEditState();
            } else {
                handleEditRule();
            }
        }
    }

    /**
     * Action to save the state or LTS as a graph.
     * @see Simulator#handleSaveGraph(boolean, JModel, String)
     * @see Simulator#doSaveGraph(JModel, File)
     */
    protected class SaveGraphAction extends AbstractAction {
    	/** Constructs an instance of the action. */
        protected SaveGraphAction() {
            super(Options.SAVE_ACTION_NAME);
            putValue(ACCELERATOR_KEY, Options.SAVE_KEY);
        }

        public void actionPerformed(ActionEvent e) {
            if (getGraphPanel() == getLtsPanel()) {
                handleSaveGraph(false, ltsPanel.getJModel(), LTS_FILE_NAME);
            } else {
                handleSaveGraph(true, statePanel.getJModel(), currentState.toString());
            }
        }

        /**
         * Tests if the action should be enabled according to the current state of the simulator,
         * and also modifies the action name.
         * 
         */
        protected void checkEnabled() {
            if (getGraphPanel() == getLtsPanel()) {
                setEnabled(true);
                putValue(NAME, Options.SAVE_LTS_ACTION_NAME);
            } else if (getGraphPanel() == getStatePanel()) {
                setEnabled(true);
                putValue(NAME, Options.SAVE_STATE_ACTION_NAME);
            } else {
                setEnabled(false);
                putValue(NAME, Options.SAVE_ACTION_NAME);
            }
        }
    }

    /**
     * Action to save the state, as a graph or in some export format.
     * @see Simulator#doExportGraph(JGraph, File)
     */
    protected class ExportGraphAction extends AbstractAction {
    	/** Constructs an instance of the action. */
        protected ExportGraphAction() {
            super(Options.EXPORT_ACTION_NAME);
            putValue(ACCELERATOR_KEY, Options.EXPORT_KEY);
        }

        public void actionPerformed(ActionEvent e) {
            String fileName;
            JGraph jGraph;
            if (getGraphPanel() == getLtsPanel()) {
                fileName = "lts";
                jGraph = ltsPanel.getJGraph();
            } else if (getGraphPanel() == getStatePanel()) {
                fileName = currentState.toString();
                jGraph = statePanel.getJGraph();
            } else {
                fileName = currentRule.getName().toString();
                jGraph = rulePanel.getJGraph();
            }
            getExportChooser().setSelectedFile(new File(fileName));
            File selectedFile = ExtensionFilter.showSaveDialog(getExportChooser(), frame);
            // now save, if so required
            if (selectedFile != null) {
                doExportGraph(jGraph, selectedFile);
            }
        }

        /**
         * Tests if the action should be enabled according to the current state of the simulator,
         * and also modifies the action name.
         */
        protected void checkEnabled() {
            if (getGraphPanel() == ltsPanel) {
                setEnabled(true);
                putValue(NAME, Options.EXPORT_LTS_ACTION_NAME);
            } else if (getGraphPanel() == statePanel) {
                setEnabled(true);
                putValue(NAME, Options.EXPORT_STATE_ACTION_NAME);
            } else {
                setEnabled(currentRule != null);
                putValue(NAME, Options.EXPORT_RULE_ACTION_NAME);
            }
        }
    }
    
    // --------------------- INSTANCE DEFINITIONS -----------------------------

    /**
     * Constructs a simulator with an empty graph grammar.
     */
    public Simulator() {
        initGrammarLoaders();
        initContentPane();
        initActions();
        // set the menu bar
        frame.setJMenuBar(createMenuBar());
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
            final File location = new File(Groove.createRuleSystemFilter().addExtension(grammarLocation));
            XmlGrammar grammarLoader = null;
            for (Map.Entry<ExtensionFilter,XmlGrammar> loaderEntry: grammarLoaderMap.entrySet()) {
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
            	final XmlGrammar loader = grammarLoader;
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
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Returns the simulator panel on which the current state is displayed. Note that this panel may
     * currently not be visible.
     * @see #setGraphPanel(JGraphPanel)
     */
    public StatePanel getStatePanel() {
        return statePanel;
    }

    /**
     * Returns the simulator panel on which the currently selected production rule is displayed.
     * Note that this panel may currently not be visible.
     * @see #setGraphPanel(JGraphPanel)
     */
    public RulePanel getRulePanel() {
        return rulePanel;
    }

    /**
     * Returns the simulator panel on which the LTS. Note that this panel may currently not be
     * visible.
     * @see #setGraphPanel(JGraphPanel)
     */
    public LTSPanel getLtsPanel() {
        return ltsPanel;
    }

    /**
     * Returns the tree of rules and matches displayed in the simulator.
     */
    public JTree getRuleTree() {
        return ruleJTree;
    }

    /**
     * Returns the currently loaded graph grammar, or <tt>null</tt> if none is loaded.
     */
    public RuleViewGrammar getCurrentGrammar() {
        return currentGrammar;
    }

    /**
     * Returns the currently selected state, or <tt>null</tt> if none is selected. The selected
     * state is the one displayed in the state panel.
     * @see StatePanel#getJModel()
     */
    public State getCurrentState() {
        return currentState;
    }

    /**
     * Returns the currently selected transition, or <tt>null</tt> if none is selected. The
     * selected state is the one selected in the rule tree and emphasized in the state panel.
     */
    public Transition getCurrentTransition() {
        return currentTransition;
    }

    /**
     * Returns the currently selected rule, or <tt>null</tt> if none is selected. The selected
     * rule is the one displayed in the rule panel.
     */
    public Rule getCurrentRule() {
        return currentRule;
    }

    /**
     * Returns the transition application action permanently associated with this simulator.
     */
    public Action getApplyTransitionAction() {
    	if (applyTransitionAction == null) {
    		applyTransitionAction = new ApplyTransitionAction();
    	}
        return applyTransitionAction;
    }

    /**
     * Returns the ctl formula providing action permanently associated with this simulator.
     */
    public Action getProvideCTLFormulaAction() {
    	if (provideCTLFormulaAction == null) {
    		provideCTLFormulaAction = new ProvideCTLFormulaAction();
    	}
    	return provideCTLFormulaAction;
    }

    /**
     * Returns the go-to start state action permanently associated with this simulator.
     */
    public Action getGotoStartStateAction() {
    	// lazily create the action
    	if (gotoStartStateAction == null) {
    		gotoStartStateAction = new GotoStartStateAction();
    	}
        return gotoStartStateAction;
    }

    /** Returns the start state load action permanently associated with this simulator. */
    public Action getLoadStartStateAction() {
    	// lazily create the action
    	if (loadStartStateAction == null) {
    		loadStartStateAction = new LoadStartStateAction();
    	}
        return loadStartStateAction;
    }

    /** Returns the grammar load action permanently associated with this simulator. */
    public Action getLoadGrammarAction() {
    	// lazily create the action
    	if (loadGrammarAction == null) {
    		loadGrammarAction = new LoadGrammarAction();
    	}
        return loadGrammarAction;
    }

    /** Returns the grammar refresh action permanently associated with this simulator. */
    public Action getRefreshGrammarAction() {
    	// lazily create the action
    	if (refreshGrammarAction == null) {
    		refreshGrammarAction = new RefreshGrammarAction();
    	}
        return refreshGrammarAction;
    }

    /** Returns the graph save action permanently associated with this simulator. */
    public SaveGraphAction getSaveGraphAction() {
    	// lazily create the action
    	if (saveGraphAction == null) {
    		saveGraphAction = new SaveGraphAction();
    	}
        return saveGraphAction;
    }

    /** Returns the graph export action permanently associated with this simulator. */
    public ExportGraphAction getExportGraphAction() {
    	// lazily create the action
    	if (exportGraphAction == null) {
    		exportGraphAction = new ExportGraphAction();
    	}
        return exportGraphAction;
    }

    /** Returns the edit action permanently associated with this simulator. */
    public EditGraphAction getEditGraphAction() {
    	// lazily create the action
    	if (editGraphAction == null) {
    		editGraphAction = new EditGraphAction();
    	}
        return editGraphAction;
    }

    /** Returns the quit action permanently associated with this simulator. */
    public Action getQuitAction() {
    	// lazily create the action
    	if (quitAction == null) {
    		quitAction = new QuitAction();
    	}
        return quitAction;
    }

    /** Returns the redo action permanently associated with this simulator. */
    public Action getRedoAction() {
    	return getUndoHistory().getRedoAction();
    }

    /** Returns the undo action permanently associated with this simulator. */
    public Action getUndoAction() {
    	return getUndoHistory().getUndoAction();
    }

    /**
     * Returns the currently selected graph view component. This is be the state, rule or LTS view.
     * @see #getStatePanel()
     * @see #getRulePanel()
     * @see #getLtsPanel()
     * @see #setGraphPanel(JGraphPanel)
     */
    public JGraphPanel<?> getGraphPanel() {
        return (JGraphPanel) graphViewsPanel.getSelectedComponent();
    }
    
    /** Returns (after lazily creating) the undo history for this simulator. */
    protected UndoHistory getUndoHistory() {
    	if (undoHistory == null) {
    		undoHistory = new UndoHistory(this);
    	}
    	return undoHistory;
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
    public void setGraphPanel(JGraphPanel<?> component) {
        graphViewsPanel.setSelectedComponent(component);
    }

    /**
     * Handles the execution of a <code>SaveGraphAction</code>. Calls {@link #doSaveGraph(JModel, File)}
     * for the actual saving.
     * @param state <tt>true</tt> if it is a state that has to be saved (otherwise it is an LTS)
     * @param jModel the j-model from which the graph is to be obtained
     * @param proposedName the proposed name for the graph, to be filled into the dialog
     * @return the file to which the graph has been saved; <tt>null</tt> if the graph has not been
     *         saved
     */
    public File handleSaveGraph(boolean state, JModel jModel, String proposedName) {
        getStateFileChooser().setFileFilter(state ? stateFilter : gxlFilter);
        getStateFileChooser().setSelectedFile(new File(proposedName));
        File selectedFile = ExtensionFilter.showSaveDialog(getStateFileChooser(), frame);
        // now save, if so required
        if (selectedFile != null) {
            doSaveGraph(jModel, selectedFile);
        }
        return selectedFile;
    }

    /**
     * Invokes the editor on the current state.
     * Handles the execution of an <code>EditGraphAction</code>, if the
     * current panel is the state panel.
     */
    public void handleEditState() {
        Editor editor = new Editor(true);
        String stateName = currentState.toString();
        editor.setModel(stateName, statePanel.getJModel());
        editorDialog = Editor.createEditorDialog(frame, true, editor);
        editor.getRulePreviewAction().setEnabled(false);
        editorDialog.setVisible(true);
        // now the editor is done; see if we have do make any updates
        if (editor.isJGraphModified()) {
			File saveFile = handleSaveGraph(true,
					editor.jgraph.getModel(),
					stateName);
			if (saveFile != null && confirmLoadStartState(saveFile.getName())) {
				doLoadStartState(saveFile);
			}
		}
    }

    /**
	 * Invokes the editor on the current rule. Handles the execution of an
	 * <code>EditGraphAction</code>, if the current panel is the rule panel.
	 * 
	 * @require <tt>getCurrentRule != null</tt>.
	 */
    public void handleEditRule() {
        Editor editor = new Editor(true);
        String ruleName = currentRule.getName().toString();
        RuleJModel ruleJModel = getRulePanel().getJGraph().getModel();
        editor.setModel(ruleName, new GraphJModel(ruleJModel.toLayedOutGraph()));
        editorDialog = Editor.createEditorDialog(frame, true, editor);
        editorDialog.setVisible(true);
        // now the editor is done; see if we have do make any updates
        if (editor.isJGraphModified()) {
            Graph editedGraph = editor.jgraph.getModel().toLayedOutGraph();
            if (confirmReplaceRule(ruleName)) {
                replaceCurrentRule(editedGraph);
            }
        }
    }

    /**
     * Exports the current state to a given format. The format is deduced from the file name, using
     * known file filters.
     */
    public void doExportGraph(JGraph jGraph, File file) {
        try {
            if (fsmFilter.accept(file)) {
                PrintWriter writer = new PrintWriter(new FileWriter(file));
                Converter.graphToFsm(jGraph.getModel().toLayedOutGraph(), writer);
                writer.close();
                getStateFileChooser().setSelectedFile(new File(""));
            } else if (jpgFilter.accept(file)) {
                // JGraph jGraph = new JGraph();
                // jGraph.setModel(jModel);
                // jGraph.setSize(jGraph.getPreferredSize());
                ImageIO.write(jGraph.toImage(), jpgFilter.getExtension().substring(1), file);
                getStateFileChooser().setSelectedFile(new File(""));
            } else if (pngFilter.accept(file)) {
                // JGraph jGraph = new JGraph();
                // jGraph.setModel(jModel);
                // jGraph.setSize(jGraph.getPreferredSize());
                ImageIO.write(jGraph.toImage(), pngFilter.getExtension().substring(1), file);
                getStateFileChooser().setSelectedFile(new File(""));
            } else if (epsFilter.accept(file)) {
                // Create a graphics contents on the buffered image
            	BufferedImage image = jGraph.toImage();

                // Create an output stream
                OutputStream out = new FileOutputStream(file);

                // minX,minY,maxX,maxY
                EpsGraphics g2d = new EpsGraphics("Title", out, 0, 0, image.getWidth(), image.getHeight(), ColorMode.COLOR_RGB);

                g2d.drawImage(jGraph.toImage(), new AffineTransform(), null);
                g2d.close();
            }
        } catch (IOException exc) {
            new ErrorDialog(frame, "Error while saving to " + file, exc);
        }
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
     * @see XmlGrammar#DEFAULT_START_STATE_NAME
     */
    public void doLoadGrammar(XmlGrammar grammarLoader, File grammarFile, String startStateName) {
        try {
            setGrammar((RuleViewGrammar) grammarLoader.unmarshal(grammarFile, startStateName));
            // now we know loading succeeded, we can set the current names & files
            currentGrammarFile = grammarFile;
            currentGrammarLoader = grammarLoader;
            File startStateFile = startStateName == null ? null : new File(startStateName);
            if (startStateFile != null && startStateFile.exists()) {
                getStateFileChooser().setSelectedFile(startStateFile);
            } else if (currentGrammarFile.isDirectory()) {
                getStateFileChooser().setCurrentDirectory(currentGrammarFile);
            } else {
                getStateFileChooser().setCurrentDirectory(currentGrammarFile.getParentFile());
            }
            getGrammarFileChooser().setSelectedFile(currentGrammarFile);
        } catch (IOException exc) {
            showErrorDialog("Error while loading grammar from " + grammarFile, exc);
        }
    }

    /**
     * Sets the contents of a given file as start state. This results in a reset of the LTS.
     */
    public void doLoadStartState(File file) {
        currentStartStateName = file.getName();
        try {
            Graph startGraph = graphLoader.unmarshal(file);
            startGraph.setFixed();
            currentGrammar.setStartGraph(startGraph);
            setGrammar(currentGrammar);
        } catch (IOException exc) {
            showErrorDialog("Could not load start graph from " + currentStartStateName,
                exc);
        }
    }
    
    /**
     * Applies a given exploration strategy to the current GTS.
     * The application is done concurrently, and can be cancelled from the GUI.
     */
    public void doGenerate(ExploreStrategy strategy) {
        GTS gts = currentGrammar.gts();
        GraphJModel ltsJModel = getLtsPanel().getJModel();
        synchronized (ltsJModel) {
            // unhook the lts' jmodel from the lts, for efficiency's sake
        	gts.removeGraphListener(ltsJModel);
            // disable rule application for the time being
            boolean applyEnabled = getApplyTransitionAction().isEnabled();
            getApplyTransitionAction().setEnabled(false);
            // create a thread to do the work in the background
            Thread generateThread = new GenerateThread(strategy);
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
     * Ends the program.
     */
    public void doQuit() {
        if (confirmAbandon()) {
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
            frame.dispose();
        }
    }

    /**
     * Refreshes the currently loaded grammar, if any. Does not ask for confirmation. Has no effect
     * if no grammar is currently loaded.
     */
    public void doRefreshGrammar() {
        if (currentGrammarFile != null) {
            try {
                setGrammar((RuleViewGrammar) currentGrammarLoader
                        .unmarshal(currentGrammarFile, currentStartStateName));
            } catch (IOException exc) {
                showErrorDialog("Error while loading grammar from " + currentGrammarFile, exc);
            }
        }
    }

    /**
     * Saves the contents of a given j-model to a given file.
     */
    public void doSaveGraph(JModel jModel, File file) {
        try {
            graphLoader.marshal(jModel.toLayedOutGraph(), file);
        } catch (IOException exc) {
            new ErrorDialog(frame, "Error while saving to " + file, exc);
        }
    }

    /**
     * Saves a new rule under the name of the currently selected rule.
     * @param ruleAsGraph the new rule, given in editor input format
     */
    public void replaceCurrentRule(Graph ruleAsGraph) {
        if (currentGrammarLoader instanceof GpsGrammar) {
        	RuleFactory ruleFactory = currentGrammarLoader.getRuleFactory();
            Rule currentRule = getCurrentRule();
            NameLabel currentRuleName = currentRule.getName();
            int currentRulePriority = currentRule.getPriority();
            try {
                RuleGraph newRuleGraph = (RuleGraph) ruleFactory.createRuleView(ruleAsGraph, currentRuleName, currentRulePriority);
                ((GpsGrammar) currentGrammarLoader).marshalRule(newRuleGraph, currentGrammarFile);
                currentGrammar.add(newRuleGraph);
                NameLabel oldRuleName = currentRuleName;
                setGrammar(currentGrammar);
                setRule(oldRuleName);
            } catch (IOException exc) {
                showErrorDialog("Error while saving edited rule", exc);
            } catch (GraphFormatException exc) {
                showErrorDialog("Internal error", exc);
            }
        }
    }

    /**
     * Loads in a given graph grammar. Invokes <tt>notifySetGrammar(grammar)</tt> to notify all
     * observers of the change. 
     * @param grammar the new graph grammar
     * @see #notifySetState(GraphState)
     */
    public synchronized void setGrammar(RuleViewGrammar grammar) {
        clearGrammar();
        this.currentGrammar = grammar;
        this.stateGenerator = createStateGenerator(grammar.gts());
        if (grammar.getName() == null) {
            setTitle(APPLICATION_NAME);
        } else {
            setTitle(currentGrammar.getName() + " - " + APPLICATION_NAME);
        }
        // reset the node numbering
        // DefaultNode.resetNodeNr();
        GTS lts = grammar.gts();
        if (lts == null) {
            currentState = null;
        } else {
            currentState = lts.startState();
            stateGenerator.computeSuccessors(currentState);
        }
        currentRule = null;
        currentTransition = null;
        notifySetGrammar(grammar);
        setActionsEnabled();
        if (frame.getContentPane() instanceof JSplitPane) {
            ((JSplitPane) frame.getContentPane()).resetToPreferredSizes();
        }
    }

	/**
	 * Clears temporary storage space occupied by the computations
	 * for the current graph grammar. 
	 */
	private void clearGrammar() {
		MatchingMatcher.clear();
	}

    /**
     * Sets the current state graph to a given state. Adds the previous state or active derivation
     * to the history. Invokes <tt>notifySetState(state)</tt> to notify all observers of the
     * change.
     * @param state the new state
     * @see #notifySetState(GraphState)
     */
    public synchronized void setState(GraphState state) {
        if (currentState != state) {
            currentState = state;
            stateGenerator.computeSuccessors(currentState);
        }
        currentTransition = null;
        notifySetState(currentState);
        setActionsEnabled();
    }

    /**
     * Sets the current production rule. Invokes <tt>notifySetRule(name)</tt> to notify all
     * observers of the change. The current derivation (if any) is thereby deactivated.
     * @param name the name of the new rule
     * @require name != null
     * @see #notifySetRule(NameLabel)
     */
    public synchronized void setRule(NameLabel name) {
        currentRule = currentGrammar.getRule(name);
        currentTransition = null;
        notifySetRule(name);
        setActionsEnabled();
    }

    /**
     * Activates a given derivation. Adds the previous state or derivation to the history. Invokes
     * <tt>notifySetTransition(edge)</tt> to notify all observers of the change.
     * @param edge the derivation to be activated.
     * @see #notifySetTransition(GraphTransition)
     */
    public synchronized void setTransition(GraphTransition edge) {
        if (currentTransition != edge) {
            if (edge.source() != currentState) {
                currentState = edge.source();
            }
            // also set the new current state to the source of the derivation
            currentTransition = edge;
            currentRule = edge.getRule();
        }
        notifySetTransition(currentTransition);
        setActionsEnabled();
    }

    /**
     * Applies the active derivation. The current state is set to the derivation's cod, and the
     * current derivation to null. Invokes <tt>notifyApplyTransition()</tt> to notify all
     * observers of the change.
     * @see #notifyApplyTransition(GraphTransition)
     */
    public synchronized void applyTransition() {
        currentState = currentTransition.target();
        GraphTransition appliedTransition = currentTransition;
        currentTransition = null;
        stateGenerator.computeSuccessors(currentState);
        notifyApplyTransition(appliedTransition);
        setActionsEnabled();
    }

    /**
     * Directs the actual verification process.
     * @param property the property to be checked
     */
    public synchronized void verifyProperty(String property) {
    	try{
    		TemporalFormula formula = CTLFormula.parseFormula(property);
    		String invalidAtom = TemporalFormula.validAtoms(formula, currentGrammar.getRuleNames());
    		if (invalidAtom == null) {
    			GTS gts = currentGrammar.gts();

        		CTLModelChecker modelChecker = new CTLModelChecker(gts, formula);
        		modelChecker.verify();
        		Set<State> counterExamples = formula.getCounterExamples();
        		notifyVerifyProperty(counterExamples);
    		} else {
    			showErrorDialog("Invalid atomic proposition", new Exception("'" + invalidAtom + "' is not a valid atomic proposition."));
    		}
    	} catch (ExprFormatException efe) {
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
	 * Returns the file chooser for grammar (GPR) files, lazily creating it first.
	 */
	protected JFileChooser getGrammarFileChooser() {
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

	/**
	 * Returns the file chooser for state (GST or GXL) files, lazily creating it first.
	 */
	protected JFileChooser getStateFileChooser() {
		if (stateFileChooser == null) {
			stateFileChooser = new GrooveFileChooser();
			stateFileChooser.addChoosableFileFilter(stateFilter);
			stateFileChooser.addChoosableFileFilter(gxlFilter);
			stateFileChooser.setFileFilter(stateFilter);
		}
        return stateFileChooser;
	}

	/**
	 * Returns the file chooser for exporting, lazily creating it first.
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
		} 
		return exportChooser;
	}

    /**
     * Sets up the content pane of the GUI.
     */
    protected void initContentPane() {
        // production rule directory
        ruleJTree = new RuleJTree(this);
        // make sure the preferred width is not smaller than the minimum width
        ruleJTreePanel = new JScrollPane(ruleJTree) {
            public Dimension getPreferredSize() {
                Dimension superSize = super.getPreferredSize();
                return new Dimension((int) Math.max(superSize.getWidth(), RULE_TREE_MINIMUM_WIDTH),
                        (int) superSize.getHeight());
            }
        };
        ruleJTreePanel.setMinimumSize(new Dimension(RULE_TREE_MINIMUM_WIDTH, 0));

        // desktop for displaying internal frames
        graphViewsPanel.setVisible(true);
        // panel for state display
        statePanel = new StatePanel(this);
        statePanel.setPreferredSize(GRAPH_VIEW_PREFERRED_SIZE);
        graphViewsPanel.addTab(null, Groove.GRAPH_FRAME_ICON, statePanel, "");
        // panel for production display
        rulePanel = new RulePanel(this);
        // res.setSize(preferredFrameDimension);
        rulePanel.setPreferredSize(GRAPH_VIEW_PREFERRED_SIZE);
        graphViewsPanel.addTab(null, Groove.RULE_FRAME_ICON, rulePanel, "");
        // panel for lts display
        ltsPanel = new LTSPanel(this);
        ltsPanel.setPreferredSize(GRAPH_VIEW_PREFERRED_SIZE);
        graphViewsPanel.addTab(null, Groove.LTS_FRAME_ICON, ltsPanel, "");
        // add this simulator as a listener so that the actions are updated regularly
        graphViewsPanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent evt) {
                setActionsEnabled();
            }
        });

        // set up the content pane of the frame as a splt pane,
        // with the rule directory to the left and a desktop pane to the right
        JSplitPane contentPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        contentPane.setLeftComponent(ruleJTreePanel);
        contentPane.setRightComponent(graphViewsPanel);

        // set up the frame
        frame = new JFrame(APPLICATION_NAME);
        // small icon doesn't look nice due to shadow
        frame.setIconImage(Groove.GROOVE_ICON_16x16.getImage());
        // frame.setSize(500,300);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setContentPane(contentPane);
    }

    /**
     * Initializes the simulator actions.
     * @require {@link #initGrammarLoaders()}should be invoked first
     */
    protected void initActions() {
        addAccelerator(getLoadGrammarAction());
        addAccelerator(getRefreshGrammarAction());

        getLoadStartStateAction().setEnabled(false);
        getSaveGraphAction().setEnabled(false);
        getExportGraphAction().setEnabled(false);


        // We initialize the UndoHistory now, whereas this should
        // actually be part of initComponents. However, this causes
        // dependency problems, since the undo and redo actions are
        // created in the UndoHistory; for the other components they
        // are created in the Simulator instead.

        // undo action
        addAccelerator(getUndoAction());
        // redo action
        addAccelerator(getRedoAction());
        // derivation actions
        addAccelerator(getApplyTransitionAction());
    }

    /**
     * Is called after a change to current state, rule or derivation or to the currently selected
     * view panel to allow actions to be enabled and disabled.
     */
    protected void setActionsEnabled() {
        getApplyTransitionAction().setEnabled(currentTransition != null);
        getGotoStartStateAction().setEnabled(currentState != null && currentState != currentGrammar.gts().startState());
        getLoadStartStateAction().setEnabled(true);
        getSaveGraphAction().checkEnabled();
        getExportGraphAction().checkEnabled();
        getEditGraphAction().checkEnabled();
    }

    /**
     * Adds the accelerator key for a given action to the action and input maps of the simulator
     * frame's content pane.
     * @param action the action to be added
     * @require <tt>frame.getContentPane()</tt> should be initialized
     */
    protected void addAccelerator(Action action) {
        JComponent contentPane = (JComponent) frame.getContentPane();
        ActionMap am = contentPane.getActionMap();
        am.put(action.getValue(Action.NAME), action);
        InputMap im = contentPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        im.put((KeyStroke) action.getValue(Action.ACCELERATOR_KEY), action.getValue(Action.NAME));
    }

    /**
     * Creates, initializes and returns a menu bar for the simulator. The actions have to be
     * initialized before invoking this.
     */
    protected JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // file menu
        JMenu fileMenu = new JMenu(Options.FILE_MENU_NAME);
        menuBar.add(fileMenu);
        fileMenu.add(new JMenuItem(getLoadGrammarAction()));
        fileMenu.add(new JMenuItem(getLoadStartStateAction()));
        fileMenu.addSeparator();
        fileMenu.add(new JMenuItem(getSaveGraphAction()));
        fileMenu.add(new JMenuItem(getExportGraphAction()));
        fileMenu.addSeparator();
        fileMenu.add(new JMenuItem(getEditGraphAction()));
        fileMenu.addSeparator();
        fileMenu.add(new JMenuItem(getQuitAction()));
        //
        // display menu
        // we fill it out each time it gets selected
        // so as to be sure it applies to the current jgraph
        menuBar.add(new JMenu(Options.DISPLAY_MENU_NAME) {
            public void menuSelectionChanged(boolean selected) {
                removeAll();
                JGraph jgraph = getGraphPanel().getJGraph();
                jgraph.fillOutEditMenu(getPopupMenu());
                jgraph.fillOutDisplayMenu(getPopupMenu());
                super.menuSelectionChanged(selected);
            }
        });

        // generate menu
        JMenu exploreMenu = new ExploreStrategyMenu(this, false);
        menuBar.add(exploreMenu);
        exploreMenu.add(new JMenuItem(getUndoAction()), 0);
        exploreMenu.add(new JMenuItem(getRedoAction()), 1);
        exploreMenu.insertSeparator(2);
        exploreMenu.add(new JMenuItem(getApplyTransitionAction()), 3);
        exploreMenu.add(new JMenuItem(getGotoStartStateAction()), 4);
        exploreMenu.insertSeparator(5);

        // verify menu
        JMenu verifyMenu = new VerifyMenu(this);
        menuBar.add(verifyMenu);

        // options menu
        JMenu optionsMenu = new JMenu(Options.OPTIONS_MENU_NAME);
        menuBar.add(optionsMenu);
        for (JCheckBoxMenuItem optionItem: getOptions().getItemSet()) {
        	optionsMenu.add(optionItem);
        }
        return menuBar;
    }

    /**
     * Adds all implemented grammar loaders to the menu.
     */
    protected void initGrammarLoaders() {
        grammarLoaderMap.clear();
        gpsLoader = new GpsGrammar(graphLoader, DerivedGraphRuleFactory.getInstance());
//        gpsLoader = new GpsGrammar(graphLoader, DefaultRuleFactory.getInstance());
        grammarLoaderMap.put(gpsLoader.getExtensionFilter(), gpsLoader);
//        ggxLoader = new GgxGrammar();
//        grammarLoaderMap.put(ggxLoader.getExtensionFilter(), ggxLoader);
    }

    /**
     * Notifies all listeners of a new graph grammar. As a result,
     * {@link SimulationListener#setGrammarUpdate(RuleViewGrammar)}is invoked on all currently
     * registered listeners. This method should not be called directly: use
     * {@link #setGrammar(RuleViewGrammar)}instead.
     * @see SimulationListener#setGrammarUpdate(RuleViewGrammar)
     * @see #setGrammar(RuleViewGrammar)
     */
    protected synchronized void notifySetGrammar(RuleViewGrammar grammar) {
    	for (SimulationListener listener: listeners) {
    		listener.setGrammarUpdate(grammar);
        }
    }

    /**
     * Notifies all listeners of a new state. As a result,
     * {@link SimulationListener#setStateUpdate(GraphState)}is invoked on all currently registered
     * listeners. This method should not be called directly: use {@link #setState(GraphState)}instead.
     * @see SimulationListener#setStateUpdate(GraphState)
     * @see #setState(GraphState)
     */
    protected synchronized void notifySetState(GraphState state) {
    	for (SimulationListener listener: listeners) {
        	listener.setStateUpdate(state);
        }
    }

    /**
     * Notifies all listeners of a new rule. As a result,
     * {@link SimulationListener#setRuleUpdate(NameLabel)}is invoked on all currently registered
     * listeners. This method should not be called directly: use {@link #setRule(NameLabel)}instead.
     * @see SimulationListener#setRuleUpdate(NameLabel)
     * @see #setRule(NameLabel)
     */
    protected synchronized void notifySetRule(NameLabel name) {
    	for (SimulationListener listener: listeners) {
        	listener.setRuleUpdate(name);
        }
    }

    /**
     * Notifies all listeners of a new detivation. As a result,
     * {@link SimulationListener#setTransitionUpdate(GraphTransition)}is invoked on all currently
     * registered listeners. This method should not be called directly: use
     * {@link #setTransition(GraphTransition)}instead.
     * @see SimulationListener#setTransitionUpdate(GraphTransition)
     * @see #setTransition(GraphTransition)
     */
    protected synchronized void notifySetTransition(GraphTransition transition) {
    	for (SimulationListener listener: listeners) {
        	listener.setTransitionUpdate(transition);
        }
    }

    /**
     * Notifies all listeners of the application of the current derivation. As a result,
     * {@link SimulationListener#applyTransitionUpdate(GraphTransition)}is invoked on all currently
     * registered listeners. This method should not be called directly: use
     * {@link #applyTransition()}instead.
     * @param transition the transition that has been applied
     * @see SimulationListener#applyTransitionUpdate(GraphTransition)
     * @see #applyTransition()
     */
    protected synchronized void notifyApplyTransition(GraphTransition transition) {
    	for (SimulationListener listener: listeners) {
            listener.applyTransitionUpdate(transition);
        }
    }

    /**
     * Notifies all listeners of the verification of the current generated transistion
     * system. This method should not be called directly: use {@link #verifyProperty(String)}
     * instead.
     * @param counterExamples the collection of states that do not satisfy the
     * property verfied
     */
    protected synchronized void notifyVerifyProperty(Set<State> counterExamples) {
        // reset lts display visibility
        setGraphPanel(getLtsPanel());
        LTSJModel jModel = getLtsPanel().getJModel();
        Set<JCell> jCells = new HashSet<JCell>();
        for(State counterExample: counterExamples) {
        	jCells.add(jModel.getJCell(counterExample));
        }
        jModel.setEmphasized(jCells);
//    	System.out.println("Emphasize the states violating the propery.\n" + counterExamples);
    }

    /**
     * Sets the title of the frame to a given title.
     */
    protected void setTitle(String title) {
        frame.setTitle(title);
    }

    /** Callback factory method for the state generator. */
    protected StateGenerator createStateGenerator(GTS gts) {
    	return new StateGenerator(gts);
    }
    
    /**
     * If the current grammar is set, asks through a dialog whether it may be abandoned.
     * @return <tt>true</tt> if the current grammar may be abandoned
     */
    private boolean confirmAbandon() {
        if (currentGrammar != null) {
            int res = JOptionPane.showConfirmDialog(frame,
                "Abandon current LTS?",
                null,
                JOptionPane.OK_CANCEL_OPTION);
            return res == JOptionPane.OK_OPTION;
        } else {
            return true;
        }
    }

    /**
     * Asks whether the current rule should be replaced by the edited version.
     */
    private boolean confirmReplaceRule(String ruleName) {
        int answer = JOptionPane.showConfirmDialog(frame, "Replace rule " + ruleName
                + " with edited version?", null, JOptionPane.OK_CANCEL_OPTION);
        return answer == JOptionPane.OK_OPTION;
    }

    /**
     * Asks whether the current rule should be replaced by the edited version.
     */
    private boolean confirmLoadStartState(String stateName) {
        int answer = JOptionPane.showConfirmDialog(frame, "Replace start state with " + stateName
                + "?", null, JOptionPane.OK_CANCEL_OPTION);
        return answer == JOptionPane.OK_OPTION;
    }

    /** Creates and shows an {@link ErrorDialog} for a given message and exception. */
    private void showErrorDialog(String message, Exception exc) {
        new ErrorDialog(frame, message, exc).setVisible(true);
    }
    
    /**
     * Returns the options object associated with the simulator.
     */
    Options getOptions() {
    	// lazily creates the options 
    	if (options == null) {
    		options = new Options();
        	options.add(Options.SHOW_ANCHORS_OPTION);
        	options.add(Options.SHOW_NODE_IDS_OPTION);
    	}
    	return options;
    }
    
    /**
     * The options object of this simulator.
     */
    private Options options;
    
    /**
     * The underlying graph grammar of this simulator. If <tt>null</tt>, no grammar has been
     * loaded.
     */
    protected RuleViewGrammar currentGrammar;

    /**
     * The currently selected state graph.
     */
    protected GraphState currentState;

    /**
     * The currently selected production rule.
     */
    protected Rule currentRule;

    /**
     * The currently activated derivation.
     * @invariant currentTransition == null || currentTransition.source().equals(currentState) &&
     *            currentTransition.rule().equals(currentRule)
     */
    protected GraphTransition currentTransition;

    /**
     * The name of the current start state.
     * May be a file name or the name of a graph within th current grammar or <code>null</code>.
     */
    protected String currentStartStateName;

    /**
     * The file or directory containing the last loaded or saved grammar, or <tt>null</tt> if no
     * grammar was yet loaded.
     */
    protected File currentGrammarFile;

    /**
     * The loader used to load the current grammar, if <tt>currentGrammarFile</tt> is not
     * <tt>null</tt>.
     */
    protected XmlGrammar currentGrammarLoader;
    
    /** The state generator strategy for the current GTS. */
    private StateGenerator stateGenerator;

    /**
     * The loader used for unmarshalling gps-formatted graph grammars.
     */
    protected GpsGrammar gpsLoader;

    /**
     * The loader used for unmarshalling ggx grammars.
     */
    protected XmlGrammar ggxLoader;

    /**
     * A mapping from extension filters (recognizing the file formats from the names) to the
     * corresponding grammar loaders.
     */
    protected final Map<ExtensionFilter,XmlGrammar> grammarLoaderMap = new LinkedHashMap<ExtensionFilter,XmlGrammar>();

    /**
     * The graph loader used for saving graphs (states and LTS).
     */
    protected final LayedOutXml graphLoader = new LayedOutXml();

    /**
     * File chooser for grammar files.
     */
    protected JFileChooser grammarFileChooser;

    /**
     * File chooser for state files and LTS.
     */
    protected JFileChooser stateFileChooser;

    /**
     * File chooser for state and LTS export actions.
     */
    protected JFileChooser exportChooser;

    /**
     * File chooser for state and LTS export actions.
     */
    protected final JFileChooser formulaProvider = new GrooveFileChooser();

    /**
     * Extension filter used for exporting graphs in fsm format.
     */
    protected final ExtensionFilter fsmFilter = Groove.createFsmFilter();

    /**
     * Extension filter used for exporting graphs in jpeg format.
     */
    protected final ExtensionFilter jpgFilter = new ExtensionFilter("JPEG image files",
            Groove.JPG_EXTENSION);

    /**
     * Extension filter used for exporting graphs in png format.
     */
    protected final ExtensionFilter pngFilter = new ExtensionFilter("PNG image files",
            Groove.PNG_EXTENSION);

    /**
     * Extension filter used for exporting graphs in png format.
     */
    protected final ExtensionFilter epsFilter = new ExtensionFilter("EPS image files",
            Groove.EPS_EXTENSION);

    /**
     * Extension filter for state files.
     */
    protected final ExtensionFilter stateFilter = Groove.createStateFilter();

    /**
     * Extension filter used for exporting the LTS in jpeg format.
     */
    protected final ExtensionFilter gxlFilter = Groove.createGxlFilter();

    /**
     * Set of registered simulation listeners.
     * @invariant <tt>listeners \subseteq SimulationListener</tt>
     */
    protected final Set<SimulationListener> listeners = new HashSet<SimulationListener>();

    /**
     * This application's main frame.
     */
    protected JFrame frame;

    /** Production rule directory. */
    protected JTree ruleJTree;

    /** Production rule display panel. */
    protected RulePanel rulePanel;

    /** State display panel. */
    protected StatePanel statePanel;

    /** LTS display panel. */
    protected LTSPanel ltsPanel;

    /** The dialog for the editor component used in this simulator. */
    protected JDialog editorDialog;

    /** Undo history. */
    protected UndoHistory undoHistory;

    /** background for displays. */
    protected final JTabbedPane graphViewsPanel = new JTabbedPane();

    /** panel for the rule directory. */
    protected JScrollPane ruleJTreePanel;

    // --------------------------- Action bjects ---------------------------------
    /** The state save action permanently associated with this simulator. */
    private SaveGraphAction saveGraphAction;

    /** The state export action permanently associated with this simulator. */
    private ExportGraphAction exportGraphAction;

    /** The start state load action permanently associated with this simulator. */
    private LoadStartStateAction loadStartStateAction;

    /** The grammar load action permanently associated with this simulator. */
    private LoadGrammarAction loadGrammarAction;

    /** The grammar refresh action permanently associated with this simulator. */
    private RefreshGrammarAction refreshGrammarAction;

    /** The state and rule edit action permanently associated with this simulator. */
    private EditGraphAction editGraphAction;

    /** The quit action permanently associated with this simulator. */
    private QuitAction quitAction;

    /** The go-to start state action permanently associated with this simulator. */
    private GotoStartStateAction gotoStartStateAction;

    /** The transition application action permanently associated with this simulator. */
    private ApplyTransitionAction applyTransitionAction;

    /** The ctl formula providing action permanently associated with this simulator. */
    private ProvideCTLFormulaAction provideCTLFormulaAction;

    // ----------------------- DEBUG DEFINITIONS -----------------------

    /** Flag controlling if a report should be printed after quitting. */
    private static final boolean REPORT = false;
}