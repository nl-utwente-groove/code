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
 * $Id: Simulator.java,v 1.92 2008/03/18 15:34:40 iovka Exp $
 */
package groove.gui;

import static groove.graph.GraphRole.HOST;
import static groove.graph.GraphRole.RULE;
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
import static groove.gui.Options.SHOW_UNFILTERED_EDGES_OPTION;
import static groove.gui.Options.SHOW_VALUE_NODES_OPTION;
import static groove.gui.Options.START_SIMULATION_OPTION;
import static groove.gui.Options.STOP_SIMULATION_OPTION;
import static groove.gui.Options.VERIFY_ALL_STATES_OPTION;
import static groove.io.FileType.GRAMMAR_FILTER;
import static groove.io.FileType.GXL_FILTER;
import static groove.io.FileType.JAR_FILTER;
import static groove.io.FileType.RULE_FILTER;
import static groove.io.FileType.STATE_FILTER;
import static groove.io.FileType.ZIP_FILTER;
import gov.nasa.ltl.trans.ParseErrorException;
import groove.abstraction.Multiplicity;
import groove.abstraction.lts.AGTS;
import groove.explore.AcceptorEnumerator;
import groove.explore.Exploration;
import groove.explore.ModelCheckingScenario;
import groove.explore.Scenario;
import groove.explore.StrategyEnumerator;
import groove.explore.strategy.Boundary;
import groove.explore.strategy.BoundedModelCheckingStrategy;
import groove.explore.strategy.ExploreStateStrategy;
import groove.explore.util.ExplorationStatistics;
import groove.explore.util.MatchApplier;
import groove.explore.util.RuleEventApplier;
import groove.graph.DefaultGraph;
import groove.graph.Element;
import groove.graph.GraphInfo;
import groove.graph.GraphProperties;
import groove.graph.Label;
import groove.graph.TypeLabel;
import groove.gui.dialog.BoundedModelCheckingDialog;
import groove.gui.dialog.ErrorDialog;
import groove.gui.dialog.ExplorationDialog;
import groove.gui.dialog.ExplorationStatsDialog;
import groove.gui.dialog.SaveLTSAsDialog;
import groove.gui.dialog.FreshNameDialog;
import groove.gui.dialog.ProgressBarDialog;
import groove.gui.dialog.PropertiesDialog;
import groove.gui.dialog.RelabelDialog;
import groove.gui.dialog.SaveDialog;
import groove.gui.dialog.StringDialog;
import groove.gui.dialog.VersionDialog;
import groove.gui.jgraph.AspectJGraph;
import groove.gui.jgraph.AspectJModel;
import groove.gui.jgraph.GraphJCell;
import groove.gui.jgraph.GraphJGraph;
import groove.gui.jgraph.GraphJModel;
import groove.gui.jgraph.LTSJGraph;
import groove.gui.jgraph.LTSJModel;
import groove.io.ExtensionFilter;
import groove.io.GrooveFileChooser;
import groove.io.external.Exporter;
import groove.io.store.DefaultFileSystemStore;
import groove.io.store.SystemStore;
import groove.io.store.SystemStoreFactory;
import groove.io.xml.AspectGxl;
import groove.io.xml.DefaultGxl;
import groove.io.xml.Xml;
import groove.lts.GTS;
import groove.lts.GTSAdapter;
import groove.lts.GTSListener;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.trans.RuleEvent;
import groove.trans.RuleMatch;
import groove.trans.RuleName;
import groove.trans.SystemProperties;
import groove.util.Duo;
import groove.util.Groove;
import groove.util.Version;
import groove.verify.CTLFormula;
import groove.verify.CTLModelChecker;
import groove.verify.LTLParser;
import groove.verify.TemporalFormula;
import groove.view.CtrlView;
import groove.view.FormatError;
import groove.view.FormatException;
import groove.view.GraphView;
import groove.view.RuleView;
import groove.view.StoredGrammarView;
import groove.view.StoredGrammarView.TypeViewList;
import groove.view.aspect.Aspect;
import groove.view.aspect.AspectGraph;
import groove.view.aspect.AspectKind;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;
import java.util.Set;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JColorChooser;
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
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.WindowConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.TreePath;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;

import apple.dts.samplecode.osxadapter.OSXAdapter;

/**
 * Program that applies a production system to an initial graph.
 * @author Arend Rensink
 * @version $Revision$
 */
public class Simulator {
    /**
     * Constructs a simulator with an empty graph grammar.
     */
    public Simulator() {
        initGrammarLoaders();
        getFrame();
        setDefaultExploration(new Exploration());
    }

    /**
     * Constructs a simulator using all production rules in a given directory.
     * All known graph grammar format loaders are polled to find one that can
     * load the grammar.
     * @param grammarLocation the location (file or directory) containing the
     *        grammar; if <tt>null</tt>, no grammar is loaded.
     */
    public Simulator(String grammarLocation) throws IOException {
        this(grammarLocation, null);
    }

    /**
     * Constructs a simulator using the grammar in a given location and a given
     * graph as start state. All known graph grammar format loaders are polled
     * to find one that can load the grammar.
     * @param grammarLocation the location (file or directory) containing the
     *        grammar; if <tt>null</tt>, no grammar is loaded.
     * @param startGraphName the file containing the start state; if
     *        <tt>null</tt>, the default start state is chosen.
     */
    public Simulator(final String grammarLocation, final String startGraphName)
        throws IOException {
        this();
        if (grammarLocation != null) {
            final File location =
                new File(GRAMMAR_FILTER.addExtension(grammarLocation)).getAbsoluteFile();

            URL locationURL = Groove.toURL(location);
            final URL grammarURL;
            if (startGraphName != null) {
                grammarURL =
                    new URL(locationURL.toExternalForm() + "?" + startGraphName);
            } else {
                grammarURL = locationURL;
            }
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    doLoadGrammar(grammarURL);
                }
            });
        }
    }

    /**
     * Starts the simulator, by calling {@link JFrame#pack()} and
     * {@link JFrame#setVisible(boolean)}.
     */
    public void start() {
        getFrame().pack();
        groove.gui.UserSettings.applyUserSettings(this.frame);
        getFrame().setVisible(true);
    }

    /**
     * Returns the currently loaded graph grammar, or <tt>null</tt> if none is
     * loaded.
     */
    public StoredGrammarView getGrammarView() {
        return this.grammarView;
    }

    /**
     * Returns the store backing up the currently loaded graph grammar, or
     * <tt>null</tt> if no grammar view is loaded.
     */
    public SystemStore getGrammarStore() {
        return getGrammarView() == null ? null : getGrammarView().getStore();
    }

    /** Returns the type graph associated with the grammar, if any. */
    TypeViewList getTypeView() {
        return getGrammarView() == null ? null
                : getGrammarView().getTypeViewList();
    }

    /**
     * Sets the {@link #grammarView} and {@link #currentRuleName} fields.
     */
    private void setGrammarView(StoredGrammarView grammar) {
        if (this.grammarView != grammar) {
            getUndoManager().discardAllEdits();
            if (this.grammarView != null) {
                this.grammarView.getStore().removeUndoableEditListener(
                    getUndoManager());
            }
            this.grammarView = grammar;
            if (this.grammarView != null) {
                this.grammarView.getStore().addUndoableEditListener(
                    getUndoManager());
            }
        }
    }

    /**
     * Returns the currently set GTS, or <tt>null</tt> if none is set.
     */
    public GTS getGTS() {
        return this.currentGTS;
    }

    /**
     * Sets the current GTS to a given GTS, possibly <code>null</code>. If the
     * new GTS is not <code>null</code>, also sets the current state to the GTS'
     * start state. In any case, sets the current transition to
     * <code>null</code>.
     * @return <code>true</code> if the new GTS is different from the previous
     */
    private boolean setGTS(GTS gts) {
        boolean result = this.currentGTS == gts;
        this.currentGTS = gts;
        this.currentTransition = null;
        this.currentEvent = null;
        this.currentState = gts == null ? null : gts.startState();
        this.explorationStats = null;
        return result;
    }

    /**
     * Returns the currently selected state, or <tt>null</tt> if none is
     * selected. The selected state is the one displayed in the state panel.
     * @see StatePanel#getJModel()
     */
    public GraphState getCurrentState() {
        return this.currentState;
    }

    /**
     * Sets the current state field to a given state, and the current transition
     * field to <code>null</code>.
     * @return <code>true</code> if the new state is different from the previous
     */
    private boolean setCurrentState(GraphState state) {
        boolean result = this.currentState != state;
        this.currentState = state;
        this.currentTransition = null;
        this.currentEvent = null;
        return result;
    }

    /**
     * Returns the currently selected transition, or <tt>null</tt> if none is
     * selected. The selected state is the one selected in the rule tree and
     * emphasized in the state panel.
     */
    public GraphTransition getCurrentTransition() {
        return this.currentTransition;
    }

    /**
     * Sets the currently selected transition to a given value (possibly
     * <code>null</code>). If the new transition is not <code>null</code>, also
     * sets the current state to the new transition's source state.
     * @return <code>true</code> if the new transition is different from the
     *         previous
     */
    private boolean setCurrentTransition(GraphTransition transition) {
        boolean result = this.currentTransition != transition;
        this.currentTransition = transition;
        if (transition != null) {
            this.currentState = transition.source();
            this.currentEvent = transition.getEvent();
        }
        return result;
    }

    /** Returns the currently selected match */
    public RuleEvent getCurrentEvent() {
        return this.currentEvent;
    }

    /**
     * Sets the currently selected match.
     */
    private boolean setCurrentEvent(RuleEvent event) {
        boolean result = this.currentEvent != event;
        this.currentEvent = event;
        return result;
    }

    /**
     * Returns the internally stored default exploration.
     */
    public Exploration getDefaultExploration() {
        return this.defaultExploration;
    }

    /**
     * Sets the internally stored default exploration.
     * @param exploration may not be null
     */
    public void setDefaultExploration(Exploration exploration) {
        this.defaultExploration = exploration;
        this.defaultExplorationMenuItem.setToolTipText("<HTML>"
            + Options.DEFAULT_EXPLORATION_ACTION_NAME + " by means of <B>"
            + exploration.getIdentifier() + "</B></HTML>");
    }

    /**
     * Returns the currently selected rule, or <tt>null</tt> if none is
     * selected. The selected rule is the one displayed in the rule panel.
     */
    public RuleView getCurrentRule() {
        return getGrammarView() == null ? null : getGrammarView().getRuleView(
            this.currentRuleName);
    }

    /**
     * Returns the currently selected rule set, or <tt>null</tt> if none is
     * selected.
     */
    public List<RuleView> getCurrentRuleSet() {
        return this.ruleJTree == null ? Collections.<RuleView>emptyList()
                : this.ruleJTree.getSelectedRules();
    }

    /**
     * Sets the currently selected rule to a given value (possibly
     * <code>null</code>).
     * @return <code>true</code> if the new rule is different from the previous
     */
    private boolean setCurrentRule(RuleView rule) {
        boolean result = this.getCurrentRule() != rule;
        this.currentRuleName = rule == null ? null : rule.getRuleName();
        return result;
    }

    /** Returns (after lazily creating) the undo history for this simulator. */
    private UndoHistory getSimulationHistory() {
        if (this.undoHistory == null) {
            this.undoHistory = new UndoHistory(this);
        }
        return this.undoHistory;
    }

    /** 
     * Adds an editor panel for the given graph, or selects the 
     * one that already exists.
     * @param fresh if {@code true}, this is a new graph (not already in the grammar)
     */
    void handleEditGraph(final AspectGraph graph, boolean fresh) {
        EditorPanel result = null;
        // look if an editor already exists for the graph
        JTabbedPane viewsPane = getSimulatorPanel();
        for (int i = 0; i < viewsPane.getTabCount(); i++) {
            Component view = viewsPane.getComponentAt(i);
            if (view instanceof EditorPanel) {
                AspectGraph editedGraph = ((EditorPanel) view).getGraph();
                if (editedGraph.getName().equals(graph.getName())
                    && editedGraph.getRole() == graph.getRole()) {
                    result = (EditorPanel) view;
                    break;
                }
            }
        }
        if (result == null) {
            result = addEditorPanel(graph, fresh);
        }
        getSimulatorPanel().setSelectedComponent(result);
    }

    /** Creates and adds an editor panel for the given graph. */
    private EditorPanel addEditorPanel(AspectGraph graph, boolean fresh) {
        final EditorPanel result = new EditorPanel(this, graph, fresh);
        getSimulatorPanel().add(result);
        result.start();
        return result;
    }

    /** Changes the type graph in the open editor panels. */
    void changeEditorTypes() {
        for (EditorPanel editor : getSimulatorPanel().getEditors()) {
            editor.setType();
        }
    }

    /** 
     * Attempts to save the dirty editors, asking the user what should happen.
     * Optionally disposes the editors.
     * @param dispose if {@code true}, all editors are disposed (unless 
     * the operation was cancelled)
     * @return {@code true} if the operation was not cancelled
     */
    boolean saveEditors(boolean dispose) {
        boolean result = true;
        for (EditorPanel editor : getSimulatorPanel().getEditors()) {
            if (editor.askAndSave()) {
                if (dispose) {
                    editor.dispose();
                }
            } else {
                result = false;
                break;
            }
        }
        return result;
    }

    /** Tests if there is a dirty editor. */
    boolean isEditorDirty() {
        boolean result = false;
        for (EditorPanel editor : getSimulatorPanel().getEditors()) {
            if (editor.isDirty() && !editor.isSaving()) {
                result = true;
                break;
            }
        }
        return result;
    }

    /** Tests if a given file refers to a graph within the current system store. */
    private boolean isFileInStore(File file, SystemStore store) {
        boolean result = false;
        if (store instanceof DefaultFileSystemStore) {
            Object storeLocation = store.getLocation();
            if (storeLocation instanceof File) {
                String storePath = ((File) storeLocation).getAbsolutePath();
                String filePath = file.getParentFile().getAbsolutePath();
                result = storePath.equals(filePath);
            }
        }
        return result;
    }

    /** Returns true if the Simulator is in abstraction mode. */
    private boolean isAbstractionMode() {
        return this.isAbstractionMode;
    }

    private void setAbstractionMode(boolean value) {
        this.isAbstractionMode = value;
    }

    /**
     * Adds a control program to this grammar.
     * @return <code>true</code> if saving the control program has succeeded
     */
    boolean doAddControl(String name, String program) {
        boolean result = false;
        try {
            getGrammarStore().putControl(name, program);
            boolean isCurrentControl =
                name.equals(getGrammarView().getControlName());
            // we only need to refresh the grammar if the added
            // control program is the currently active one
            if (isCurrentControl) {
                updateGrammar();
            } else {
                // otherwise, we only need to update the control panel
                getControlPanel().refreshAll();
            }
            result = true;
        } catch (IOException exc) {
            showErrorDialog("Error storing control program " + name, exc);
        }
        return result;
    }

    /**
     * Adds a given graph to the graphs in this grammar
     * @return <code>true</code> if saving the graph has succeeded
     */
    boolean doAddGraph(AspectGraph graph) {
        boolean result = false;
        try {
            getGrammarStore().putGraph(graph);
            result = true;
            if (graph.getName().equals(getGrammarView().getStartGraphName())) {
                updateGrammar();
            } else {
                refresh();
            }
            result = true;
        } catch (IOException exc) {
            showErrorDialog(
                String.format("Error while saving graph '%s'", graph.getName()),
                exc);
        }
        return result;
    }

    /**
     * Saves an aspect graph as a rule under a given name, and puts the rule
     * into the current grammar view.
     * @param ruleAsGraph the new rule, given as an aspect graph
     * @return <code>true</code> if saving the rule has succeeded
     */
    boolean doAddRule(AspectGraph ruleAsGraph) {
        boolean result = false;
        try {
            getGrammarStore().putRule(ruleAsGraph);
            ruleAsGraph.invalidateView();
            updateGrammar();
            result = true;
        } catch (IOException exc) {
            showErrorDialog(
                String.format("Error while saving rule '%s'",
                    ruleAsGraph.getName()), exc);
        } catch (UnsupportedOperationException u) {
            showErrorDialog("Current grammar is read-only", u);
        }
        return result;
    }

    /**
     * Saves an aspect graph as a type graph under a given name, and puts the
     * type graph into the current grammar view.
     * @param typeGraph the new type, given as an aspect graph
     * @return <code>true</code> if saving the type graph has succeeded
     */
    boolean doAddType(AspectGraph typeGraph) {
        boolean result = false;
        try {
            getGrammarStore().putType(typeGraph);
            if (getGrammarView().getActiveTypeNames().contains(
                typeGraph.getName())) {
                updateGrammar();
            } else {
                // otherwise, we only need to update the type panel
                getTypePanel().displayType();
            }
            result = true;
        } catch (IOException exc) {
            showErrorDialog(
                String.format("Error while saving type graph '%s'",
                    typeGraph.getName()), exc);
        }
        return result;
    }

    /** Removes a control program from this grammar. */
    void doDeleteControl(String name) {
        boolean isCurrentControl =
            name.equals(getGrammarView().getControlName());
        getGrammarStore().deleteControl(name);
        // we only need to refresh the grammar if the deleted
        // control program was the currently active one
        if (isCurrentControl) {
            updateGrammar();
        } else {
            // otherwise, we only need to update the control panel
            getControlPanel().refreshAll();
        }
    }

    /**
     * Deletes a graph from the start graph view.
     */
    void doDeleteGraph(String name) {
        // test now if this is the start state, before it is deleted from the
        // grammar
        boolean isStartGraph =
            name.equals(getGrammarView().getStartGraphName());
        getGrammarStore().deleteGraph(name);
        if (isStartGraph) {
            // reset the start graph to null
            getGrammarView().removeStartGraph();
            updateGrammar();
        } else {
            refresh();
        }
    }

    /**
     * Deletes a rule from the grammar and the file system, and resets the
     * grammar view.
     */
    void doDeleteRule(RuleName name) {
        AspectGraph rule = getGrammarStore().deleteRule(name);
        if (rule != null) {
            updateGrammar();
        }
    }

    /** Removes a type graph from this grammar. */
    void doDeleteType(String name) {
        boolean isUsed = getGrammarView().getActiveTypeNames().contains(name);
        getGrammarStore().deleteType(name);
        // we only need to refresh the grammar if the deleted
        // type graph was the currently active one
        if (isUsed) {
            updateGrammar();
        } else {
            // otherwise, we only need to update the type panel
            getTypePanel().displayType();
        }
    }

    /** Inverts the enabledness of the current rule, and stores the result. */
    void doEnableRule() {
        // Multiple selection
        // Copy the selected rules to avoid concurrent modifications
        List<RuleView> rules = new ArrayList<RuleView>(getCurrentRuleSet());
        for (RuleView rule : rules) {
            AspectGraph ruleGraph = rule.getAspectGraph();
            GraphProperties properties =
                GraphInfo.getProperties(ruleGraph, true).clone();
            properties.setEnabled(!properties.isEnabled());
            AspectGraph newRuleGraph = ruleGraph.clone();
            GraphInfo.setProperties(newRuleGraph, properties);
            newRuleGraph.setFixed();
            doAddRule(newRuleGraph);
        }
    }

    /**
     * Is only called from the Generator. Will be replaced by doRunExploration.
     */
    public void doGenerate(Scenario scenario) {

        /*
         * When a (LTL) ModelCheckingScenario is started, initialize by asking
         * the user to enter a property (via a getFormulaDialog).
         */
        if (scenario instanceof ModelCheckingScenario) {
            String property =
                getFormulaDialog().showDialog(getFrame(),
                    new StringDialog.StringParser() {
                        @Override
                        public String parse(String text) {
                            String result = null;
                            try {
                                LTLParser.parse(text);
                            } catch (ParseErrorException e) {
                                result = e.getMessage();
                            }
                            return result;
                        }
                    });
            if (property == null) {
                return;
            }
            ((ModelCheckingScenario) scenario).setProperty(property);
        }

        /*
         * When a (LTL) BoundedModelCheckingScenario is started, also prompt the
         * user to enter a boundary (via a BoundedModelCheckingDialog).
         */
        if (scenario.getStrategy() instanceof BoundedModelCheckingStrategy) {
            BoundedModelCheckingDialog dialog =
                new BoundedModelCheckingDialog();
            dialog.setGrammar(getGTS().getGrammar());
            dialog.showDialog(getFrame());
            Boundary boundary = dialog.getBoundary();
            if (boundary == null) {
                return;
            }
            ((BoundedModelCheckingStrategy) scenario.getStrategy()).setBoundary(boundary);
        }

        scenario.prepare(getGTS(), getCurrentState());
        LTSJModel ltsJModel = getLtsPanel().getJModel();
        synchronized (ltsJModel) {
            // unhook the lts' jmodel from the lts, for efficiency's sake
            getGTS().removeLTSListener(ltsJModel);
            // disable rule application for the time being
            boolean applyEnabled = getApplyTransitionAction().isEnabled();
            getApplyTransitionAction().setEnabled(false);
            // create a thread to do the work in the background
            Thread generateThread = new LaunchThread(scenario);
            // go!
            generateThread.start();
            // get the lts' jmodel back on line and re-synchronize its state
            ltsJModel.loadGraph(ltsJModel.getGraph());
            // re-enable rule application
            getApplyTransitionAction().setEnabled(applyEnabled);
            // reset lts display visibility
            switchTabs(getLtsPanel());
        }
        LTSJGraph ltsJGraph = getLtsPanel().getJGraph();
        if (ltsJGraph.getLayouter() != null) {
            ltsJGraph.getLayouter().start(false);
        }
    }

    /**
     * Loads in a grammar from a given file.
     */
    void doLoadGrammar(File grammarFile, String startGraphName) {
        try {
            // Load the grammar.
            final SystemStore store =
                SystemStoreFactory.newStore(grammarFile, false);
            doLoadGrammar(store, startGraphName);
            // now we know loading succeeded, we can set the current
            // names & files
            getGrammarFileChooser().setSelectedFile(grammarFile);
            getRuleFileChooser().setCurrentDirectory(grammarFile);
            if (startGraphName != null) {
                File startFile = new File(grammarFile, startGraphName);
                getStateFileChooser().setSelectedFile(startFile);
            } else {
                // make sure the selected file from an old grammar is
                // unselected
                getStateFileChooser().setSelectedFile(null);
                // make sure the dialog for open state opens at the
                // grammar location
                getStateFileChooser().setCurrentDirectory(grammarFile);
            }
            // updating history
            // this.history.updateLoadGrammar(grammarFile.toString());
        } catch (IOException exc) {
            showErrorDialog(exc.getMessage(), exc);
        }
    }

    /**
     * Loads in a grammar from a given URL.
     */
    void doLoadGrammar(final URL grammarURL) {
        try {
            final SystemStore store = SystemStoreFactory.newStore(grammarURL);
            String startGraphName = grammarURL.getQuery();
            doLoadGrammar(store, startGraphName);
            // updating history
            // this.history.updateLoadGrammar(grammarURL.toString());
        } catch (IOException exc) {
            showErrorDialog(exc.getMessage(), exc);
        }
    }

    /** 
     * Helper method for doLoadGrammar. Asks the user to select a new name for
     * saving the grammar after it has been loaded (and converted).
     */
    private File selectSaveAs(File oldGrammarFile) {
        if (oldGrammarFile != null) {
            getGrammarFileChooser().getSelectedFile();
            getGrammarFileChooser().setSelectedFile(oldGrammarFile);
        }
        int result = getGrammarFileChooser().showSaveDialog(getFrame());
        if (result != JFileChooser.APPROVE_OPTION) {
            return null;
        }
        File selected = getGrammarFileChooser().getSelectedFile();
        if (selected.exists()) {
            if (confirmOverwriteGrammar(selected)) {
                return selected;
            } else {
                return selectSaveAs(oldGrammarFile);
            }
        } else {
            return selected;
        }
    }

    /**
     * Loads in a given system store.
     */
    void doLoadGrammar(final SystemStore store, final String startGraphName)
        throws IOException {
        if (!saveEditors(true)) {
            return;
        }

        // First we check if the versions are compatible.
        store.reload();
        SystemProperties props = store.getProperties();
        String fileGrammarVersion = props.getGrammarVersion();
        int compare = Version.compareGrammarVersion(fileGrammarVersion);
        final boolean saveAfterLoading = (compare != 0);
        final File newGrammarFile;
        if (compare < 0) {
            // Trying to load a newer grammar.
            if (!VersionDialog.showNew(this.getFrame(), props)) {
                return;
            }
            newGrammarFile = null;
        } else if (compare > 0 && store.getLocation() instanceof File) {
            // Trying to load an older grammar from a file.
            File grammarFile = (File) store.getLocation();
            switch (VersionDialog.showOldFile(this.getFrame(), props)) {
            case 0: // save and overwrite
                newGrammarFile = grammarFile;
                break;
            case 1: // save under different name
                newGrammarFile = selectSaveAs(grammarFile);
                if (newGrammarFile == null) {
                    return;
                }
                break;
            default: // cancel
                return;
            }
        } else if (compare > 0) {
            // Trying to load an older grammar from a URL.
            if (!VersionDialog.showOldURL(this.getFrame(), props)) {
                return;
            }
            newGrammarFile = selectSaveAs(null);
            if (newGrammarFile == null) {
                return;
            }
        } else {
            // Loading an up-to-date grammar.
            newGrammarFile = null;
        }

        final ProgressBarDialog dialog =
            new ProgressBarDialog(getFrame(), "Load Progress");
        final Observer loadListener = new Observer() {
            public void update(Observable o, Object arg) {
                if (arg instanceof String) {
                    dialog.setMessage((String) arg);
                } else if (arg instanceof Integer) {
                    if (this.size == 0) {
                        this.size = ((Integer) arg) + 1;
                        dialog.setRange(0, this.size);
                    }
                } else {
                    if (this.size > 0) {
                        dialog.incProgress();
                    }
                }
            }

            private int size;
        };

        SwingUtilities.invokeLater(new Thread() {

            @Override
            public void run() {
                dialog.activate(1000);
                try {
                    if (store instanceof Observable) {
                        ((Observable) store).addObserver(loadListener);
                    }
                    // store.reload(); - MdM - moved to version check code
                    final StoredGrammarView grammar = store.toGrammarView();
                    if (startGraphName != null) {
                        grammar.setStartGraph(startGraphName);
                    }
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            setGrammarView(grammar);
                            updateGrammar();
                            grammar.getProperties().setCurrentVersionProperties();
                            if (saveAfterLoading && newGrammarFile != null) {
                                doSaveGrammar(newGrammarFile,
                                    !newGrammarFile.equals(store.getLocation()));
                            }
                        }
                    });
                    if (store instanceof Observable) {
                        ((Observable) store).deleteObserver(loadListener);
                    }
                } catch (final IllegalArgumentException exc) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            showErrorDialog(exc.getMessage(), exc);
                        }
                    });
                }
                dialog.deactivate();
            }
        });
    }

    /**
     * Sets the contents of a given file as start state. This results in a reset
     * of the LTS.
     */
    void doLoadStartGraph(File file) {
        try {
            AspectGraph startGraph = unmarshalGraph(file);
            getGrammarView().setStartGraph(startGraph);
            updateGrammar();
        } catch (IOException exc) {
            showErrorDialog(
                "Could not load start graph from " + file.getName(), exc);
        }
    }

    /**
     * Sets a graph with given name as start state. This results in a reset of
     * the LTS.
     */
    void doLoadStartGraph(String name) {
        getGrammarView().setStartGraph(name);
        updateGrammar();
    }

    /**
     * Creates an empty grammar and an empty directory, and sets it in the
     * simulator.
     * @param grammarFile the grammar file to be used
     */
    void doNewGrammar(File grammarFile) {
        try {
            if (saveEditors(true)) {
                StoredGrammarView grammar =
                    StoredGrammarView.newInstance(grammarFile, true);
                // now we know loading succeeded, we can set the current names &
                // files
                getStateFileChooser().setCurrentDirectory(grammarFile);
                getStateFileChooser().setSelectedFile(new File(""));
                getGrammarFileChooser().setSelectedFile(grammarFile);
                setGrammarView(grammar);
                updateGrammar();
            }
        } catch (IllegalArgumentException exc) {
            showErrorDialog(
                String.format("Can't create grammar at '%s'", grammarFile), exc);
        } catch (IOException exc) {
            showErrorDialog(String.format(
                "Error while creating grammar at '%s'", grammarFile), exc);
        }
    }

    /**
     * Ends the program. Return value is used on MacOS to signal that quitting
     * itself is successful. 
     */
    public boolean doQuit() {
        boolean result = saveEditors(true);
        if (result) {
            groove.gui.UserSettings.synchSettings(this.frame);
            // Saves the current user settings.
            if (confirmAbandon(false)) {
                getFrame().dispose();
                // try to persist the user preferences
                try {
                    Preferences.userRoot().flush();
                } catch (BackingStoreException e) {
                    // do nothing if the backing store is inaccessible
                }
            }
        }
        return result;
    }

    /**
     * Refreshes the currently loaded grammar, if any. Does not ask for
     * confirmation. Has no effect if no grammar is currently loaded.
     */
    void doRefreshGrammar() {
        if (getGrammarStore() != null) {
            try {
                getGrammarStore().reload();
                getUndoManager().discardAllEdits();
                updateGrammar();
            } catch (IOException exc) {
                showErrorDialog("Error while refreshing grammar from "
                    + getGrammarStore().getLocation(), exc);
            }
        }
    }

    /** Replaces all occurrences of a given label into another label. */
    void doRelabel(TypeLabel oldLabel, TypeLabel newLabel) {
        try {
            getGrammarStore().relabel(oldLabel, newLabel);
            updateGrammar();
        } catch (IOException exc) {
            showErrorDialog(String.format(
                "Error while renaming '%s' into '%s':", oldLabel, newLabel),
                exc);
        }
    }

    /** Renumbers the nodes in all graphs from {@code 0} upwards. */
    void doRenumber() {
        if (getGrammarStore() instanceof DefaultFileSystemStore) {
            try {
                ((DefaultFileSystemStore) getGrammarStore()).renumber();
                updateGrammar();
            } catch (IOException exc) {
                showErrorDialog("Error while renumbering", exc);
            }
        }
    }

    /**
     * Renames one of the graphs in the graph list. If the graph was the start
     * graph, uses the renamed graph again as start graph.
     */
    void doRenameGraph(AspectGraph graph, String newName) {
        String oldName = graph.getName();
        // test now if this is the start state, before it is deleted from the
        // grammar
        String startGraphName = getGrammarView().getStartGraphName();
        boolean isStartGraph =
            oldName.equals(startGraphName) || newName.equals(startGraphName);
        try {
            getGrammarStore().renameGraph(oldName, newName);
            if (isStartGraph) {
                // reset the start graph to the renamed graph
                getGrammarView().setStartGraph(newName);
                updateGrammar();
            } else {
                refresh();
            }
        } catch (IOException exc) {
            showErrorDialog(
                String.format("Error while renaming graph '%s'",
                    graph.getName()), exc);
        }
    }

    /**
     * Renames one of the rule in the grammar.
     */
    void doRenameRule(AspectGraph graph, String newName) {
        String oldName = graph.getName();
        try {
            getGrammarStore().renameRule(oldName, newName);
            updateGrammar();
        } catch (IOException exc) {
            showErrorDialog(
                String.format("Error while renaming rule '%s'", oldName), exc);
        }
    }

    /**
     * Renames a given type graph.
     */
    boolean doRenameType(AspectGraph graph, String newName) {
        boolean result = false;
        String oldName = graph.getName();
        // test now if this is the type graph, before it is deleted from the
        // grammar
        boolean isTypeGraph =
            getGrammarView().getActiveTypeNames().contains(oldName);
        try {
            getGrammarStore().renameType(oldName, newName);
            if (isTypeGraph) {
                updateGrammar();
            } else {
                // otherwise, we only need to update the type panel
                getTypePanel().displayType();
            }
            result = true;
        } catch (IOException exc) {
            showErrorDialog(
                String.format("Error while renaming type graph '%s'", oldName),
                exc);
        }
        return result;
    }

    /**
     * Run a given exploration. Can be called from outside the Simulator.
     * @param exploration - the exploration strategy to be used
     * @param emphasise if {@code true}, the result of the exploration will be emphasised
     */
    public void doRunExploration(Exploration exploration, boolean emphasise) {
        setDefaultExploration(exploration);
        LTSJModel ltsJModel = getLtsPanel().getJModel();
        if (ltsJModel == null) {
            if (startSimulation()) {
                ltsJModel = getLtsPanel().getJModel();
            } else {
                return;
            }
        }
        synchronized (ltsJModel) {
            // unhook the lts' jmodel from the lts, for efficiency's sake
            getGTS().removeLTSListener(ltsJModel);
            // disable rule application for the time being
            boolean applyEnabled = getApplyTransitionAction().isEnabled();
            getApplyTransitionAction().setEnabled(false);
            // create a thread to do the work in the background
            Thread generateThread = new LaunchThread(exploration, emphasise);
            // go!
            this.getExplorationStats().start();
            generateThread.start();
            this.getExplorationStats().stop();
            // collect the result states
            getGTS().setResult(exploration.getLastResult());
            // get the lts' jmodel back on line and re-synchronize its state
            ltsJModel.loadGraph(ltsJModel.getGraph());
            // re-enable rule application
            getApplyTransitionAction().setEnabled(applyEnabled);
            // reset lts display visibility
            switchTabs(getLtsPanel());
        }
        LTSJGraph ltsJGraph = getLtsPanel().getJGraph();
        if (ltsJGraph.getLayouter() != null) {
            ltsJGraph.getLayouter().start(false);
        }
    }

    /**
     * Attempts to save a control program to a file. Failure to do so will be
     * reported in an error dialog. The return value indicates if the attempt
     * was successful.
     * @param controlProgram string containing a (parsable) control program
     *        (non-null)
     * @param file target file; will be overwritten if already existing
     *        (non-null)
     * @return <code>true</code> if the program was successfully saved
     */
    boolean doSaveControl(String controlProgram, File file) {
        try {
            CtrlView.store(controlProgram, new FileOutputStream(file));
            return true;
        } catch (IOException exc) {
            showErrorDialog("Error while saving to " + file, exc);
            return false;
        }
    }

    /**
     * Saves the current grammar to a given file.
     * @param grammarFile the grammar file to be used
     */
    void doSaveGrammar(File grammarFile, boolean clearDir) {
        try {
            if (saveEditors(true)) {
                SystemStore newStore =
                    getGrammarStore().save(grammarFile, clearDir);
                StoredGrammarView newView = newStore.toGrammarView();
                String startGraphName = getGrammarView().getStartGraphName();
                GraphView startGraphView = getGrammarView().getStartGraphView();
                if (startGraphName != null) {
                    newView.setStartGraph(startGraphName);
                } else if (startGraphView != null) {
                    newView.setStartGraph(startGraphView.getAspectGraph());
                }
                setGrammarView(newView);
                setTitle();
                getGrammarFileChooser().setSelectedFile(grammarFile);
                updateGrammar();
            }
        } catch (IOException exc) {
            showErrorDialog("Error while saving grammar to " + grammarFile, exc);
        }
    }

    /** Saves a given system properties object. */
    void doSaveProperties(SystemProperties newProperties) {
        try {
            getGrammarStore().putProperties(newProperties);
            updateGrammar();
        } catch (IOException exc) {
            showErrorDialog("Error while saving edited properties", exc);
        }
    }

    private AspectGraph unmarshalGraph(File file) throws IOException {
        return getGraphLoader(file).unmarshalGraph(file);
    }

    private Xml<AspectGraph> getGraphLoader(File file) {
        return this.aspectLoader;
    }

    /**
     * Sets a new graph transition system. Invokes
     * {@link #fireSetGrammar(StoredGrammarView)} to notify all observers of the
     * change.
     * 
     * @see #fireSetGrammar(StoredGrammarView)
     */
    public synchronized void updateGrammar() {
        setGTS(null);
        fireSetGrammar(getGrammarView());
        changeEditorTypes();
        refresh();
        List<FormatError> grammarErrors = getGrammarView().getErrors();
        setErrors(grammarErrors);
        if (grammarErrors.isEmpty() && !isEditorDirty()
            && confirmBehaviourOption(START_SIMULATION_OPTION)) {
            startSimulation();
        }
        this.history.updateLoadGrammar();
    }

    /**
     * Displays a list of errors, or hides the error panel if the list is empty.
     */
    private void setErrors(List<FormatError> grammarErrors) {
        getErrorPanel().setErrors(grammarErrors);
        JSplitPane contentPane = (JSplitPane) this.frame.getContentPane();
        if (getErrorPanel().isVisible()) {
            contentPane.setBottomComponent(getErrorPanel());
            contentPane.setDividerSize(1);
            contentPane.resetToPreferredSizes();
        } else {
            contentPane.remove(getErrorPanel());
            contentPane.setDividerSize(0);
        }
    }

    /**
     * Sets a new graph transition system. Invokes
     * {@link #fireStartSimulation(GTS)} to notify all observers of the change.
     * 
     * @see #fireSetGrammar(StoredGrammarView)
     */
    public synchronized boolean startSimulation() {
        boolean result = false;
        if (saveEditors(false)) {
            try {
                GTS gts;
                if (this.isAbstractionMode()) {
                    gts = new AGTS(getGrammarView().toGrammar());
                } else {
                    gts = new GTS(getGrammarView().toGrammar());
                }
                gts.getRecord().setRandomAccess(true);
                setGTS(gts);
                fireStartSimulation(getGTS());
                refresh();
                result = true;
            } catch (FormatException exc) {
                showErrorDialog("Error while starting simulation", exc);
            }
        }
        return result;
    }

    /**
     * Sets the current state graph to a given state. Adds the previous state or
     * active derivation to the history. Invokes <tt>notifySetState(state)</tt>
     * to notify all observers of the change.
     * 
     * @param state the new state
     * @see #fireSetState(GraphState)
     */
    public synchronized void setState(GraphState state) {
        setCurrentState(state);
        fireSetState(state);
        refreshActions();
    }

    /** Fully explores a given state of the GTS. */
    public synchronized void exploreState(GraphState state) {
        getExploreStateStrategy().prepare(getGTS(), state);
        getExploreStateStrategy().next();
        setState(state);
    }

    /**
     * Sets the current production rule. Invokes <tt>notifySetRule(name)</tt> to
     * notify all observers of the change. The current derivation (if any) is
     * thereby deactivated.
     * @param name the name of the new rule
     * @require name != null
     * @see #fireSetRule(RuleName)
     */
    public synchronized void setRule(RuleName name) {
        setCurrentRule(getGrammarView().getRuleView(name));
        setCurrentTransition(null);
        setCurrentEvent(null);
        fireSetRule(name);
        refreshActions();
    }

    /**
     * Activates a given derivation, given directly or via its corresponding
     * match. Adds the previous state or derivation to the history. Invokes
     * <tt>notifySetTransition(edge)</tt> to notify all observers of the change.
     * @param transition the derivation to be activated. May be null if
     *        <code>match</code> does not correspond to any transition in the
     *        LTS
     * @see #fireSetTransition(GraphTransition)
     */
    public synchronized void setTransition(GraphTransition transition) {
        if (transition != null) {
            if (setCurrentTransition(transition)) {
                RuleName ruleName = transition.getEvent().getRule().getName();
                setCurrentRule(getGrammarView().getRuleView(ruleName));
                setCurrentEvent(transition.getEvent());
            }
            fireSetTransition(getCurrentTransition());
        }
        refreshActions();
    }

    /**
     * Activates a given match. Invokes {@link #fireSetMatch(RuleMatch)} to
     * notify all observers of the change.
     * @param event the match to be activated.
     * @see #fireSetTransition(GraphTransition)
     */
    public synchronized void setEvent(RuleEvent event) {
        assert event != null : "The match and the transition cannot be both null.";
        RuleName ruleName = event.getRule().getName();
        setCurrentRule(getGrammarView().getRuleView(ruleName));
        setCurrentTransition(null);
        setCurrentEvent(event);
        fireSetMatch(event.getMatch(getCurrentState().getGraph()));
        refreshActions();
    }

    /**
     * Applies a match to the current state. The current state is set to the
     * derivation's target, and the current derivation to null. Invokes
     * <tt>notifyApplyTransition()</tt> to notify all observers of the change.
     * @see #fireApplyTransition(GraphTransition)
     */
    public synchronized void applyMatch() {
        if (getCurrentEvent() != null) {
            GraphTransition result =
                getEventApplier().apply(getCurrentState(), getCurrentEvent());
            if (result != null) {
                setCurrentState(result.target());
                fireApplyTransition(result);
                refreshActions();
            }
        }
    }

    /**
     * Adds a listener to the registered simulation listeners. From this moment
     * on, the listener will be notified.
     * @param listener the listener to be added
     */
    public synchronized void addSimulationListener(SimulationListener listener) {
        this.listeners.add(listener);
    }

    /**
     * Removes a listener from the registered simulation listeners. From this
     * moment on, the listener will no longer be notified.
     * @param listener the listener to be removed
     */
    public synchronized void removeSimulationListener(
            SimulationListener listener) {
        this.listeners.remove(listener);
    }

    /**
     * Lazily creates and returns the frame of this simulator.
     */
    public JFrame getFrame() {
        if (this.frame == null) {
            // force the LAF to be set
            groove.gui.Options.initLookAndFeel();

            // set up the frame
            this.frame = new JFrame(APPLICATION_NAME);
            // small icon doesn't look nice due to shadow
            this.frame.setIconImage(Icons.GROOVE_ICON_16x16.getImage());
            this.frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

            // register doQuit() for the Command-Q shortcut on MacOS 
            if (MAC_OS_X) {
                try {
                    OSXAdapter.setQuitHandler(this,
                        this.getClass().getDeclaredMethod("doQuit"));
                } catch (NoSuchMethodException e1) {
                    // should not happen (thrown when 'doQuit' does not exist)
                    e1.printStackTrace();
                }
            }
            // register doQuit() as the closing method of the window
            this.frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    doQuit();
                }
            });
            this.frame.setJMenuBar(createMenuBar());

            JSplitPane leftPanel =
                new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                    createRuleTreePanel(), createStatesListPanel());
            // make sure tool tips get displayed
            ToolTipManager.sharedInstance().registerComponent(leftPanel);

            JComponent rightPanel = getSimulatorPanel();

            // Set up the content pane of the frame as a split pane,
            // with the rule directory to the left and a desktop pane to the
            // right
            JSplitPane splitPane =
                new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel,
                    rightPanel);

            JSplitPane contentPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT) {

                @Override
                public ActionListener getActionForKeyStroke(KeyStroke aKeyStroke) {
                    ActionListener result =
                        super.getActionForKeyStroke(aKeyStroke);
                    return result;
                }

            };
            contentPane.setTopComponent(splitPane);
            contentPane.setResizeWeight(0.8);
            contentPane.setDividerSize(0);
            contentPane.setContinuousLayout(true);
            this.frame.setContentPane(contentPane);
        }
        return this.frame;
    }

    /**
     * Lazily creates and returns the panel with the state, rule and LTS views.
     */
    SimulatorPanel getSimulatorPanel() {
        if (this.simulatorPanel == null) {
            this.simulatorPanel = new SimulatorPanel(this);
        }
        return this.simulatorPanel;
    }

    /**
     * Lazily creates and returns the panel with the rule tree.
     */
    private JPanel createRuleTreePanel() {
        // set title and toolbar
        JLabel labelPaneTitle =
            new JLabel(" " + Options.RULES_PANE_TITLE + " ");
        labelPaneTitle.setAlignmentX(JLabel.LEFT_ALIGNMENT);
        JToolBar labelTreeToolbar = createRuleTreeToolBar();
        labelTreeToolbar.setAlignmentX(JLabel.LEFT_ALIGNMENT);

        Box labelPaneTop = Box.createVerticalBox();
        labelPaneTop.add(labelPaneTitle);
        labelPaneTop.add(labelTreeToolbar);

        // make sure the preferred width is not smaller than the minimum
        // width
        JScrollPane ruleJTreePanel = new JScrollPane(getRuleTree()) {
            @Override
            public Dimension getPreferredSize() {
                Dimension superSize = super.getPreferredSize();
                return new Dimension((int) Math.max(superSize.getWidth(),
                    RULE_TREE_MINIMUM_WIDTH), (int) superSize.getHeight());
            }
        };
        ruleJTreePanel.setMinimumSize(new Dimension(RULE_TREE_MINIMUM_WIDTH,
            RULE_TREE_MINIMUM_HEIGHT));

        this.ruleTreePanel = ruleJTreePanel;

        JPanel result = new JPanel(new BorderLayout(), false);
        result.add(labelPaneTop, BorderLayout.NORTH);
        result.add(ruleJTreePanel, BorderLayout.CENTER);
        // make sure tool tips get displayed
        ToolTipManager.sharedInstance().registerComponent(result);
        return result;
    }

    /** Creates a tool bar for the rule tree. */
    private JToolBar createRuleTreeToolBar() {
        JToolBar result = createToolBar();
        result.setFloatable(false);
        result.add(getNewRuleAction());
        result.add(getEditRuleAction());
        result.addSeparator();
        result.add(getCopyRuleAction());
        result.add(getDeleteRuleAction());
        result.add(getRenameRuleAction());
        return result;
    }

    /**
     * Creates and returns the panel with the start states list.
     */
    private JPanel createStatesListPanel() {
        // set title and toolbar
        JLabel labelPaneTitle =
            new JLabel(" " + Options.STATES_PANE_TITLE + " ");
        labelPaneTitle.setAlignmentX(JLabel.LEFT_ALIGNMENT);
        JToolBar labelTreeToolbar = createStatesListToolBar();
        labelTreeToolbar.setAlignmentX(JLabel.LEFT_ALIGNMENT);

        Box labelPaneTop = Box.createVerticalBox();
        labelPaneTop.add(labelPaneTitle);
        labelPaneTop.add(labelTreeToolbar);

        JScrollPane startGraphsPane = new JScrollPane(this.getStateList()) {
            @Override
            public Dimension getPreferredSize() {
                Dimension superSize = super.getPreferredSize();
                return new Dimension((int) superSize.getWidth(),
                    START_LIST_MINIMUM_HEIGHT);
            }
        };

        JPanel result = new JPanel(new BorderLayout(), false);
        result.add(labelPaneTop, BorderLayout.NORTH);
        result.add(startGraphsPane, BorderLayout.CENTER);
        // make sure tool tips get displayed
        ToolTipManager.sharedInstance().registerComponent(result);
        return result;
    }

    /** Creates a tool bar for the rule tree. */
    private JToolBar createStatesListToolBar() {
        JToolBar result = createToolBar();
        result.setFloatable(false);
        result.add(getNewGraphAction());
        result.add(getEditGraphAction());
        result.addSeparator();
        result.add(getCopyGraphAction());
        result.add(getDeleteGraphAction());
        result.add(getRenameGraphAction());
        result.addSeparator();
        result.add(getSetStartGraphAction());
        // make sure tool tips get displayed
        ToolTipManager.sharedInstance().registerComponent(result);
        return result;
    }

    private JToolBar createToolBar() {
        JToolBar result = new JToolBar() {
            @Override
            protected JButton createActionComponent(Action a) {
                JButton result = super.createActionComponent(a);
                result.setFocusable(false);
                return result;
            }
        };
        return result;
    }

    private ErrorListPanel getErrorPanel() {
        if (this.errorPanel == null) {
            final ErrorListPanel result =
                this.errorPanel =
                    new ErrorListPanel("Format errors in grammar");
            result.addSelectionListener(new Observer() {
                @Override
                public void update(Observable observable, Object arg) {
                    if (arg != null) {
                        FormatError error = (FormatError) arg;
                        AspectGraph errorGraph = error.getGraph();
                        if (errorGraph != null) {
                            JGraphPanel<?> panel = null;
                            String name = errorGraph.getName();
                            switch (errorGraph.getRole()) {
                            case RULE:
                                panel = getRulePanel();
                                setRule(new RuleName(name));
                                break;
                            case HOST:
                                panel = getStatePanel();
                                getStateList().setSelectedValue(name, true);
                                break;
                            case TYPE:
                                panel = getTypePanel();
                                getTypePanel().setSelectedType(name);
                                break;
                            default:
                                assert false;
                            }
                            // select the error cell and switch to the panel
                            if (panel != null) {
                                for (Element errorObject : error.getElements()) {
                                    if (panel.selectJCell(errorObject)) {
                                        break;
                                    }
                                }
                                switchTabs(panel);
                            }
                        } else if (error.getControl() != null) {
                            getControlPanel().setSelectedControl(
                                error.getControl().getName());
                            String LINE_PATTERN = "line ";
                            String message = error.toString();
                            int index = message.indexOf(LINE_PATTERN);
                            if (index >= 0) {
                                index += LINE_PATTERN.length();
                                int end = message.indexOf(':', index);
                                if (end < 0) {
                                    end = message.length();
                                }
                                String line =
                                    error.toString().substring(index, end);
                                int lineNr;
                                try {
                                    lineNr = Integer.parseInt(line);
                                    getControlPanel().selectLine(lineNr);
                                } catch (NumberFormatException e1) {
                                    // do nothing
                                }
                            }
                            getSimulatorPanel().setSelectedComponent(
                                getControlPanel());
                        }
                    }
                }
            });
        }
        return this.errorPanel;
    }

    /** Error display. */
    private ErrorListPanel errorPanel;

    /**
     * Returns the simulator panel on which the current state is displayed. Note
     * that this panel may currently not be visible.
     * @see #switchTabs(JGraphPanel)
     */
    public StatePanel getStatePanel() {
        if (this.statePanel == null) {
            // panel for state display
            this.statePanel = new StatePanel(this);
            this.statePanel.setPreferredSize(GRAPH_VIEW_PREFERRED_SIZE);
        }
        return this.statePanel;
    }

    ControlPanel getControlPanel() {
        if (this.controlPanel == null) {
            this.controlPanel = new ControlPanel(this);
            this.controlPanel.setPreferredSize(GRAPH_VIEW_PREFERRED_SIZE);
        }
        return this.controlPanel;
    }

    /**
     * Returns the simulator panel on which the currently selected production
     * rule is displayed. Note that this panel may currently not be visible.
     * @see #switchTabs(JGraphPanel)
     */
    RulePanel getRulePanel() {
        if (this.rulePanel == null) {
            // panel for production display
            this.rulePanel = new RulePanel(this);
            // res.setSize(preferredFrameDimension);
            this.rulePanel.setPreferredSize(GRAPH_VIEW_PREFERRED_SIZE);
        }
        return this.rulePanel;
    }

    /**
     * Returns the simulator panel on which the LTS. Note that: - this panel may
     * currently not be visible. - this panel is always contained in the
     * ConditionalLTSPanel.
     * @see #switchTabs(JGraphPanel)
     */
    LTSPanel getLtsPanel() {
        if (this.ltsPanel == null) {
            this.ltsPanel = new LTSPanel(this);
            this.ltsPanel.setPreferredSize(GRAPH_VIEW_PREFERRED_SIZE);
        }
        return this.ltsPanel;
    }

    /**
     * Returns the exploration statistics object associated with the current
     * GTS.
     */
    public ExplorationStatistics getExplorationStats() {
        if (this.explorationStats == null) {
            this.explorationStats = new ExplorationStatistics(this.getGTS());
            this.explorationStats.configureForSimulator();
        }
        return this.explorationStats;
    }

    /**
     * Returns the simulator panel on which the current state is displayed. Note
     * that this panel may currently not be visible.
     * @see #switchTabs(JGraphPanel)
     */
    TypePanel getTypePanel() {
        if (this.typePanel == null) {
            // panel for state display
            this.typePanel = new TypePanel(this);
            this.typePanel.setPreferredSize(GRAPH_VIEW_PREFERRED_SIZE);
        }
        return this.typePanel;
    }

    /**
     * Returns the prolog panel.
     * @see #switchTabs(JGraphPanel)
     */
    public PrologPanel getPrologPanel() {
        if (this.prologPanel == null) {
            this.prologPanel = new PrologPanel(this);
            this.prologPanel.setPreferredSize(GRAPH_VIEW_PREFERRED_SIZE);
        }
        return this.prologPanel;
    }

    StateJList getStateList() {
        if (this.stateJList == null) {
            this.stateJList = new StateJList(this);
        }
        return this.stateJList;
    }

    /**
     * Returns the tree of rules and matches displayed in the simulator.
     */
    RuleJTree getRuleTree() {
        if (this.ruleJTree == null) {
            this.ruleJTree = new RuleJTree(this);
        }
        return this.ruleJTree;
    }

    /** Returns the exporter of the simulator. */
    public Exporter getExporter() {
        return this.exporter;
    }

    /** Returns the currently selected simulator panel. */
    Component getPanel() {
        return getSimulatorPanel().getSelectedComponent();
    }

    /**
     * Returns the currently selected graph view component. This can be the
     * state, rule or LTS view. In case the LTS is active, the inner LTSPanel is
     * returned instead of the outer ConditionalLTSPanel.
     * @see #getStatePanel()
     * @see #getRulePanel()
     * @see #getLtsPanel()
     * @see #switchTabs(JGraphPanel)
     */
    JGraphPanel<?> getGraphPanel() {
        Component selectedComponent =
            getSimulatorPanel().getSelectedComponent();
        if (selectedComponent instanceof EditorPanel) {
            return ((EditorPanel) selectedComponent).getEditor().getGraphPanel();
        }
        if (!(selectedComponent instanceof JGraphPanel<?>)) {
            return null;
        } else {
            return (JGraphPanel<?>) selectedComponent;
        }
    }

    /**
     * Brings one of the graph view components to the foreground. This should be
     * the state, rule or LTS view.
     * @param component the graph view component to bring to the foreground (in
     *        case the LTS panel should be made active, this is expected to be
     *        the inner LTSPanel, instead of the outer ConditionalLTSPanel)
     * @see #getStatePanel()
     * @see #getRulePanel()
     * @see #getLtsPanel()
     * @see #getGraphPanel()
     */
    public void switchTabs(JGraphPanel<?> component) {
        if (getSimulatorPanel().getSelectedComponent() != component
            && getSimulatorPanel().indexOfComponent(component) >= 0) {
            this.switchingTabs = true;
            getSimulatorPanel().setSelectedComponent(component);
            this.switchingTabs = false;
        }
    }

    /**
     * Indicates that the simulator is processing a 
     * {@link #switchTabs(JGraphPanel)}.
     * This may affect the newly selected component's behaviour. 
     */
    public boolean isSwitchingTabs() {
        return this.switchingTabs;
    }

    /**
     * Adds an element to the set of refreshables. Also calls
     * {@link Refreshable#refresh()} on the element.
     */
    void addRefreshable(Refreshable element) {
        this.refreshables.add(element);
    }

    /**
     * Is called after a change to current state, rule or derivation or to the
     * currently selected view panel to allow registered refreshable elements to
     * refresh themselves.
     */
    public void refreshActions() {
        refreshExportMenuItem();
        for (Refreshable action : this.refreshables) {
            action.refresh();
        }
        getControlPanel().refreshAll();
        getTypePanel().refreshActions();
    }

    /**
     * Refreshes the menu item for the export action to the most appropriate
     * action, given the currently selected view panel.
     */
    private void refreshExportMenuItem() {
        if (getGraphPanel() == null) {
            getExportGraphMenuItem().setEnabled(false);
        } else {
            Action exportAction = getGraphPanel().getJGraph().getExportAction();
            getExportGraphMenuItem().setAction(exportAction);
            getExportGraphMenuItem().setEnabled(getGraphPanel().isEnabled());
        }
    }

    /**
     * Adds the accelerator key for a given action to the action and input maps
     * of the simulator frame's content pane.
     * @param action the action to be added
     * @require <tt>frame.getContentPane()</tt> should be initialised
     */
    void addAccelerator(Action action) {
        JComponent contentPane = (JComponent) getFrame().getContentPane();
        ActionMap am = contentPane.getActionMap();
        am.put(action.getValue(Action.NAME), action);
        InputMap im =
            contentPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        im.put((KeyStroke) action.getValue(Action.ACCELERATOR_KEY),
            action.getValue(Action.NAME));
    }

    /**
     * Creates, initializes and returns a menu bar for the simulator. The
     * actions have to be initialized before invoking this.
     */
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(createFileMenu());
        menuBar.add(createEditMenu());
        menuBar.add(createDisplayMenu());
        menuBar.add(createExploreMenu());
        menuBar.add(createVerifyMenu());
        menuBar.add(getExternalActionsMenu());
        menuBar.add(createHelpMenu());
        return menuBar;
    }

    /**
     * Creates and returns a file menu for the menu bar.
     */
    private JMenu createFileMenu() {
        JMenu result = new JMenu(Options.FILE_MENU_NAME);

        result.setMnemonic(Options.FILE_MENU_MNEMONIC);
        result.add(new JMenuItem(getNewGrammarAction()));
        result.add(new JMenuItem(getLoadGrammarAction()));
        result.add(new JMenuItem(new LoadURLAction()));
        result.add(createOpenRecentMenu());

        result.addSeparator();

        result.add(new JMenuItem(getLoadStartGraphAction()));
        result.add(new JMenuItem(getImportRuleAction()));

        result.addSeparator();

        result.add(new JMenuItem(getSaveGrammarAction()));
        result.add(new JMenuItem(getSaveGraphAction()));
        result.add(getExportGraphMenuItem());

        result.addSeparator();

        result.add(new JMenuItem(getRefreshGrammarAction()));

        result.addSeparator();

        result.add(new JMenuItem(getQuitAction()));

        return result;
    }

    private JMenu createOpenRecentMenu() {
        if (this.history == null) {
            this.history = new History();
        }
        return this.history.getOpenRecentMenu();
    }

    /**
     * Creates and returns an edit menu for the menu bar.
     */
    private JMenu createEditMenu() {
        JMenu result = new JMenu(Options.EDIT_MENU_NAME);

        result.setMnemonic(Options.EDIT_MENU_MNEMONIC);
        result.add(getUndoAction());
        result.add(getRedoAction());

        result.addSeparator();

        result.add(getNewGraphAction());
        result.add(getNewRuleAction());
        result.add(getNewTypeAction());

        result.addSeparator();

        result.add(getEditMenuItem());
        result.add(getCopyMenuItem());
        result.add(getDeleteMenuItem());
        result.add(getRenameMenuItem());

        result.addSeparator();

        result.add(getRelabelAction());
        result.add(getRenumberAction());

        result.addSeparator();

        result.add(getEnableRuleAction());
        result.add(getEditRulePropertiesAction());
        result.add(getEditSystemPropertiesAction());

        return result;
    }

    /**
     * Returns the menu item in the edit menu that specifies editing the
     * currently displayed graph or rule.
     */
    JMenuItem getEditMenuItem() {
        if (this.editGraphItem == null) {
            this.editGraphItem = new JMenuItem();
            // load the graph edit action as default
            this.editGraphItem.setAction(getEditGraphAction());
            // give the rule edit action a chance to replace the graph edit
            // action
            getEditRuleAction();
            this.editGraphItem.setAccelerator(Options.EDIT_KEY);
        }
        return this.editGraphItem;
    }

    /**
     * Returns the menu item in the edit menu that specifies copy the currently
     * displayed graph or rule.
     */
    JMenuItem getCopyMenuItem() {
        if (this.copyGraphItem == null) {
            this.copyGraphItem = new JMenuItem();
            // load the graph copy action as default
            this.copyGraphItem.setAction(getCopyGraphAction());
        }
        return this.copyGraphItem;
    }

    /**
     * Returns the menu item in the edit menu that specifies delete the
     * currently displayed graph or rule.
     */
    JMenuItem getDeleteMenuItem() {
        if (this.deleteGraphItem == null) {
            this.deleteGraphItem = new JMenuItem();
            // load the graph delete action as default
            this.deleteGraphItem.setAction(getDeleteGraphAction());
        }
        return this.deleteGraphItem;
    }

    /**
     * Returns the menu item in the edit menu that specifies delete the
     * currently displayed graph or rule.
     */
    JMenuItem getRenameMenuItem() {
        if (this.renameGraphItem == null) {
            this.renameGraphItem = new JMenuItem();
            // load the graph rename action as default
            this.renameGraphItem.setAction(getRenameGraphAction());
            // give the rule rename action a chance to replace the graph rename
            // action
            getRenameRuleAction();
            this.renameGraphItem.setAccelerator(Options.RENAME_KEY);
        }
        return this.renameGraphItem;
    }

    /**
     * Returns the menu item that will contain the current export action.
     */
    private JMenuItem getExportGraphMenuItem() {
        // lazily create the menu item
        if (this.exportGraphMenuItem == null) {
            this.exportGraphMenuItem =
                new JMenuItem(getStatePanel().getJGraph().getExportAction());
        }
        return this.exportGraphMenuItem;
    }

    /**
     * Creates and returns a display menu for the menu bar. The menu is filled
     * out each time it gets selected so as to be sure it applies to the current
     * jgraph
     */
    private JMenu createDisplayMenu() {
        // fills the menu depending on the currently displayed graph panel
        JMenu result = new JMenu(Options.DISPLAY_MENU_NAME) {
            @Override
            public void menuSelectionChanged(boolean selected) {
                removeAll();
                GraphJGraph jGraph;
                if (getGraphPanel() != null) {
                    jGraph = getGraphPanel().getJGraph();
                    if (jGraph instanceof AspectJGraph) {
                        jGraph.addSubmenu(this,
                            ((AspectJGraph) jGraph).createEditMenu(null));
                    }
                    jGraph.addSubmenu(this, jGraph.createDisplayMenu());
                } else {
                    // create a dummy JGraph to add the rest of the menu
                    jGraph = new GraphJGraph(null, false);
                }
                jGraph.addSubmenu(this, createOptionsMenu());
                super.menuSelectionChanged(selected);
            }
        };
        result.setMnemonic(Options.DISPLAY_MENU_MNEMONIC);
        return result;
    }

    /**
     * Creates and returns an options menu for the menu bar.
     */
    JMenu createOptionsMenu() {
        JMenu result = new JMenu(OPTIONS_MENU_NAME);
        result.setMnemonic(Options.OPTIONS_MENU_MNEMONIC);
        result.add(getOptions().getItem(SHOW_NODE_IDS_OPTION));
        result.add(getOptions().getItem(SHOW_ANCHORS_OPTION));
        result.add(getOptions().getItem(SHOW_ASPECTS_OPTION));
        result.add(getOptions().getItem(SHOW_REMARKS_OPTION));
        result.add(getOptions().getItem(SHOW_BACKGROUND_OPTION));
        result.add(getOptions().getItem(SHOW_VALUE_NODES_OPTION));
        result.add(getOptions().getItem(SHOW_STATE_IDS_OPTION));
        result.add(getOptions().getItem(SHOW_UNFILTERED_EDGES_OPTION));
        result.addSeparator();
        result.add(getOptions().getItem(Options.CANCEL_CONTROL_EDIT_OPTION));
        result.add(getOptions().getItem(Options.DELETE_CONTROL_OPTION));
        result.add(getOptions().getItem(Options.DELETE_GRAPH_OPTION));
        result.add(getOptions().getItem(DELETE_RULE_OPTION));
        result.add(getOptions().getItem(START_SIMULATION_OPTION));
        result.add(getOptions().getItem(STOP_SIMULATION_OPTION));
        result.add(getOptions().getItem(REPLACE_RULE_OPTION));
        result.add(getOptions().getItem(REPLACE_START_GRAPH_OPTION));
        result.add(getOptions().getItem(VERIFY_ALL_STATES_OPTION));
        return result;
    }

    /**
     * Creates and returns an exploration menu for the menu bar.
     */
    private JMenu createExploreMenu() {
        JMenu result = new JMenu();

        result.setMnemonic(Options.EXPLORE_MENU_MNEMONIC);
        result.setText(Options.EXPLORE_MENU_NAME);
        result.add(new JMenuItem(getBackAction()));
        result.add(new JMenuItem(getForwardAction()));

        result.addSeparator();

        result.add(new JMenuItem(this.getStartSimulationAction()));
        // EDUARDO: Uncomment to enable abstraction.
        // result.add(new JMenuItem(this.getToggleExplorationStateAction()));
        result.add(new JMenuItem(this.getApplyTransitionAction()));
        result.add(new JMenuItem(this.getGotoStartStateAction()));

        result.addSeparator();

        this.defaultExplorationMenuItem =
            result.add(this.getDefaultExplorationAction());
        result.add(this.getExplorationDialogAction());

        result.addSeparator();

        result.add(this.getExplorationStatsDialogAction());
        result.add(new JMenuItem(getSaveLTSAsAction()));

        return result;
    }

    /**
     * Creates and returns a verification menu for the menu bar.
     */
    private JMenu createVerifyMenu() {
        JMenu result = new JMenu(Options.VERIFY_MENU_NAME);
        result.setMnemonic(Options.VERIFY_MENU_MNEMONIC);
        result.add(getCheckCTLAction(true));
        result.add(getCheckCTLAction(false));
        result.addSeparator();
        JMenu mcScenarioMenu = new ModelCheckingMenu(this, false);
        for (Component menuComponent : mcScenarioMenu.getMenuComponents()) {
            result.add(menuComponent);
        }
        return result;
    }

    /**
     * Lazily creates and returns a menu for externally provided actions in the
     * menu bar.
     */
    private JMenu getExternalActionsMenu() {
        if (this.externalMenu == null) {
            this.externalMenu = createExternalActionsMenu();
            this.dummyExternalAction = new AbstractAction("(empty)") {
                public void actionPerformed(ActionEvent e) {
                    // does nothing
                }
            };
            this.dummyExternalAction.setEnabled(false);
            this.externalMenu.add(this.dummyExternalAction);
        }
        return this.externalMenu;
    }

    /**
     * Creates and returns a menu for externally provided actions in the menu
     * bar.
     */
    private JMenu createExternalActionsMenu() {
        return new JMenu(Options.EXTERNAL_MENU_NAME);
    }

    /**
     * Adds an action to the external actions menu of the simulator. This
     * provides a primitive plugin mechanism.
     */
    public void addExternalAction(Action action) {
        JMenu externalMenu = getExternalActionsMenu();
        // remove the dummy action if it is still there
        if (externalMenu.getItem(0).getAction() == this.dummyExternalAction) {
            externalMenu.remove(0);
        }
        getExternalActionsMenu().add(action);
    }

    /**
     * Creates and returns a help menu for the menu bar.
     */
    private JMenu createHelpMenu() {
        JMenu result = new JMenu(HELP_MENU_NAME);
        result.setMnemonic(Options.HELP_MENU_MNEMONIC);
        result.add(new JMenuItem(new AboutAction()));
        return result;
    }

    /**
     * Returns the file chooser for grammar (GPR) files, lazily creating it
     * first.
     */
    JFileChooser getGrammarFileChooser() {
        if (this.grammarFileChooser == null) {
            this.grammarFileChooser = new GrooveFileChooser();
            this.grammarFileChooser.setAcceptAllFileFilterUsed(false);
            FileFilter firstFilter = null;
            for (FileFilter filter : this.grammarExtensions) {
                this.grammarFileChooser.addChoosableFileFilter(filter);
                if (firstFilter == null) {
                    firstFilter = filter;
                }
            }
            this.grammarFileChooser.setFileFilter(firstFilter);
            this.grammarFileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        }
        return this.grammarFileChooser;
    }

    /**
     * Returns the last file from which a grammar was loaded.
     */
    File getLastGrammarFile() {
        File result = null;
        SystemStore store =
            getGrammarView() == null ? null : getGrammarView().getStore();
        Object location = store == null ? null : store.getLocation();
        if (location instanceof File) {
            result = (File) location;
        } else if (location instanceof URL) {
            result = Groove.toFile((URL) location);
        }
        return result;
    }

    /**
     * Returns the file chooser for state (GST or GXL) files, lazily creating it
     * first.
     */
    JFileChooser getStateFileChooser() {
        if (this.stateFileChooser == null) {
            this.stateFileChooser = new GrooveFileChooser();
            this.stateFileChooser.addChoosableFileFilter(STATE_FILTER);
            this.stateFileChooser.addChoosableFileFilter(GXL_FILTER);
            this.stateFileChooser.setFileFilter(STATE_FILTER);
        }
        return this.stateFileChooser;
    }

    /**
     * Returns the file chooser for rule (GPR) files, lazily creating it first.
     */
    JFileChooser getRuleFileChooser() {
        if (this.ruleFileChooser == null) {
            this.ruleFileChooser = new GrooveFileChooser();
            this.ruleFileChooser.addChoosableFileFilter(RULE_FILTER);
            this.ruleFileChooser.setFileFilter(RULE_FILTER);
        }
        return this.ruleFileChooser;
    }

    /** Returns a dialog that will ask for a formula to be entered. */
    public StringDialog getFormulaDialog() {
        if (this.formulaDialog == null) {
            this.formulaDialog = new StringDialog("Enter the Temporal Formula");
        }
        return this.formulaDialog;
    }

    /**
     * @return the explore-strategy for exploring a single state
     */
    private ExploreStateStrategy getExploreStateStrategy() {
        if (this.exploreStateStrategy == null) {
            this.exploreStateStrategy = new ExploreStateStrategy();
        }
        return this.exploreStateStrategy;
    }

    /**
     * Adds all implemented grammar loaders to the menu.
     */
    private void initGrammarLoaders() {
        this.grammarExtensions.clear();
        // loader for directories representing grammars
        this.grammarExtensions.add(GRAMMAR_FILTER);
        // loader for archives (jar/zip) containing directories representing
        // grammars.
        this.grammarExtensions.add(JAR_FILTER);
        this.grammarExtensions.add(ZIP_FILTER);
    }

    /**
     * Notifies all listeners of a new graph grammar. As a result,
     * {@link SimulationListener#setGrammarUpdate(StoredGrammarView)}is invoked
     * on all currently registered listeners. This method should not be called
     * directly: use {@link #updateGrammar()}instead.
     * @see SimulationListener#setGrammarUpdate(StoredGrammarView)
     */
    private synchronized void fireSetGrammar(StoredGrammarView grammar) {
        if (!this.updating) {
            this.updating = true;
            for (SimulationListener listener : this.listeners) {
                listener.setGrammarUpdate(grammar);
            }
            this.updating = false;
        }
    }

    /**
     * Notifies all listeners of the start of a new active simulation. As a
     * result, {@link SimulationListener#startSimulationUpdate(GTS)} is invoked
     * on all currently registered listeners. This method should not be called
     * directly: use {@link #startSimulation()}instead.
     * @see SimulationListener#startSimulationUpdate(GTS)
     */
    private synchronized void fireStartSimulation(GTS gts) {
        if (!this.updating) {
            this.updating = true;
            for (SimulationListener listener : this.listeners) {
                listener.startSimulationUpdate(gts);
            }
            this.updating = false;
        }
    }

    /**
     * Notifies all listeners of a new state. As a result,
     * {@link SimulationListener#setStateUpdate(GraphState)}is invoked on all
     * currently registered listeners. This method should not be called
     * directly: use {@link #setState(GraphState)}instead.
     * @see SimulationListener#setStateUpdate(GraphState)
     * @see #setState(GraphState)
     */
    private synchronized void fireSetState(GraphState state) {
        if (!this.updating) {
            this.updating = true;
            for (SimulationListener listener : this.listeners) {
                listener.setStateUpdate(state);
            }
            this.updating = false;
        }
    }

    /**
     * Notifies all listeners of a new rule. As a result,
     * {@link SimulationListener#setRuleUpdate(RuleName)}is invoked on all
     * currently registered listeners. This method should not be called
     * directly: use {@link #setRule(RuleName)}instead.
     * 
     * @see SimulationListener#setRuleUpdate(RuleName)
     * @see #setRule(RuleName)
     */
    private synchronized void fireSetRule(RuleName name) {
        if (!this.updating) {
            this.updating = true;
            for (SimulationListener listener : this.listeners) {
                listener.setRuleUpdate(name);
            }
            this.updating = false;
        }
    }

    /**
     * Notifies all listeners of a newly selected match. As a result,
     * {@link SimulationListener#setMatchUpdate(RuleMatch)}is invoked on all
     * currently registered listeners. This method should not be called
     * directly: use {@link #setEvent(RuleEvent)} instead.
     * 
     * @see SimulationListener#setMatchUpdate(RuleMatch)
     */
    private synchronized void fireSetMatch(RuleMatch match) {
        assert match != null;
        if (!this.updating) {
            this.updating = true;
            for (SimulationListener listener : this.listeners) {
                listener.setMatchUpdate(match);
            }
            this.updating = false;
        }
    }

    /**
     * Notifies all listeners of a new derivation. As a result,
     * {@link SimulationListener#setTransitionUpdate(GraphTransition)}is invoked
     * on all currently registered listeners. This method should not be called
     * directly: use {@link #setTransition(GraphTransition)} instead.
     * 
     * @see SimulationListener#setTransitionUpdate(GraphTransition)
     * @see #setTransition(GraphTransition)
     */
    private synchronized void fireSetTransition(GraphTransition transition) {
        if (!this.updating) {
            this.updating = true;
            for (SimulationListener listener : this.listeners) {
                listener.setTransitionUpdate(transition);
            }
            this.updating = false;
        }
    }

    /**
     * Notifies all listeners of the application of the current derivation. As a
     * result, {@link SimulationListener#applyTransitionUpdate(GraphTransition)}
     * is invoked on all currently registered listeners. This method should not
     * be called directly: use {@link #applyMatch()}instead.
     * @param transition the transition that has been applied
     * @see SimulationListener#applyTransitionUpdate(GraphTransition)
     * @see #applyMatch()
     */
    private synchronized void fireApplyTransition(GraphTransition transition) {
        if (!this.updating) {
            this.updating = true;
            for (SimulationListener listener : this.listeners) {
                listener.applyTransitionUpdate(transition);
            }
            this.updating = false;
        }
    }

    /**
     * Refreshes the title bar, layout and actions.
     */
    public void refresh() {
        setTitle();
        getStateList().refreshList(true);
        refreshActions();
    }

    /**
     * Sets the title of the frame to a given title.
     */
    private void setTitle() {
        StringBuffer title = new StringBuffer();
        if (getGrammarView() != null && getGrammarView().getName() != null) {
            title.append(getGrammarView().getName());
            GraphView startGraph = getGrammarView().getStartGraphView();
            if (startGraph != null) {
                title.append(TITLE_NAME_SEPARATOR);
                title.append(startGraph.getName());
            }
            if (getGrammarView().isUseControl()) {
                title.append(" | ");
                title.append(getGrammarView().getControlName());

            }
            if (!getGrammarStore().isModifiable()) {
                title.append(" (read-only)");
            }
            title.append(" - ");

        }
        title.append(APPLICATION_NAME);
        getFrame().setTitle(title.toString());
    }

    /**
     * Returns the state generator for the current GTS, if any.
     */
    private RuleEventApplier getEventApplier() {
        if (this.eventApplier == null || this.eventApplier.getGTS() != getGTS()) {
            if (getGTS() != null) {
                this.eventApplier = new MatchApplier(getGTS());
            }
        }
        return this.eventApplier;
    }

    /**
     * If a simulation is active, asks through a dialog whether it may be
     * abandoned.
     * @param setGrammar flag indicating that {@link #updateGrammar()} is to be
     *        called with the current grammar, in case the simulation is
     *        abandoned
     * @return <tt>true</tt> if the current grammar may be abandoned
     */
    boolean confirmAbandon(boolean setGrammar) {
        boolean result;
        if (getGTS() != null) {
            result = confirmBehaviourOption(STOP_SIMULATION_OPTION);
            if (result && setGrammar) {
                updateGrammar();
            }
        } else {
            result = true;
        }
        return result;
    }

    /**
     * Checks if a given option is confirmed. The question can be set
     * explicitly.
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
     * Asks whether the current start graph should be replaced by the edited
     * version. Always returns <code>true</code> (without asking) if there is no
     * current start graph.
     */
    boolean confirmLoadStartState(String stateName) {
        if (getGrammarView().getStartGraphView() == null) {
            return true;
        } else if (stateName.equals(getGrammarView().getStartGraphName())) {
            return true;
        } else {
            String question =
                String.format("Replace start graph with '%s'?", stateName);
            return confirmBehaviour(REPLACE_START_GRAPH_OPTION, question);
        }
    }

    /**
     * Asks whether a given existing rule should be replaced by a newly loaded
     * one.
     */
    boolean confirmOverwriteRule(RuleName ruleName) {
        int response =
            JOptionPane.showConfirmDialog(getFrame(),
                String.format("Replace existing rule '%s'?", ruleName), null,
                JOptionPane.OK_CANCEL_OPTION);
        return response == JOptionPane.OK_OPTION;
    }

    /**
     * Asks whether a given existing file should be overwritten by a new
     * grammar.
     */
    boolean confirmOverwriteGrammar(File grammarFile) {
        if (grammarFile.exists()) {
            int response =
                JOptionPane.showConfirmDialog(getFrame(),
                    "Overwrite existing grammar?", null,
                    JOptionPane.OK_CANCEL_OPTION);
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
     * Enters a dialog that results in a name label that does not yet occur in
     * the current grammar, or <code>null</code> if the dialog was cancelled.
     * @param title dialog title; if <code>null</code>, a default title is used
     * @param name an initially proposed name
     * @param mustBeFresh if <code>true</code>, the returned name is guaranteed
     *        to be distinct from the existing rule names
     * @return a rule name not occurring in the current grammar, or
     *         <code>null</code>
     */
    RuleName askNewRuleName(String title, String name, boolean mustBeFresh) {
        FreshNameDialog<RuleName> ruleNameDialog =
            new FreshNameDialog<RuleName>(getGrammarView().getRuleNames(),
                name, mustBeFresh) {
                @Override
                protected RuleName createName(String name) {
                    return new RuleName(name);
                }
            };
        ruleNameDialog.showDialog(getFrame(), title);
        return ruleNameDialog.getName();
    }

    /**
     * Enters a dialog that results in a graph name that does not yet occur in
     * the current grammar, or <code>null</code> if the dialog was cancelled.
     * @param title dialog title; if <code>null</code>, a default title is used
     * @param name an initially proposed name
     * @param mustBeFresh if <code>true</code>, the returned name is guaranteed
     *        to be distinct from the existing graph names
     * @return a graph name not occurring in the current grammar, or
     *         <code>null</code>
     */
    String askNewGraphName(String title, String name, boolean mustBeFresh) {
        Set<String> existingNames = getGrammarView().getGraphNames();
        FreshNameDialog<String> nameDialog =
            new FreshNameDialog<String>(existingNames, name, mustBeFresh) {
                @Override
                protected String createName(String name) {
                    return name;
                }
            };
        nameDialog.showDialog(getFrame(), title);
        return nameDialog.getName();
    }

    /**
     * Enters a dialog that results in a control name that does not yet occur in
     * the current grammar, or <code>null</code> if the dialog was cancelled.
     * @param title dialog title; if <code>null</code>, a default title is used
     * @param name an initially proposed name
     * @param mustBeFresh if <code>true</code>, the returned name is guaranteed
     *        to be distinct from the existing names
     * @return a control name not occurring in the current grammar, or
     *         <code>null</code>
     */
    String askNewControlName(String title, String name, boolean mustBeFresh) {
        Set<String> existingNames = getGrammarView().getControlNames();
        FreshNameDialog<String> nameDialog =
            new FreshNameDialog<String>(existingNames, name, mustBeFresh) {
                @Override
                protected String createName(String name) {
                    return name;
                }
            };
        nameDialog.showDialog(getFrame(), title);
        return nameDialog.getName();
    }

    /**
     * Enters a dialog that results in a type graph that does not yet occur in
     * the current grammar, or <code>null</code> if the dialog was cancelled.
     * @param title dialog title; if <code>null</code>, a default title is used
     * @param name an initially proposed name
     * @param mustBeFresh if <code>true</code>, the returned name is guaranteed
     *        to be distinct from the existing names
     * @return a type graph not occurring in the current grammar, or
     *         <code>null</code>
     */
    String askNewTypeName(String title, String name, boolean mustBeFresh) {
        Set<String> existingNames = getGrammarView().getTypeNames();
        FreshNameDialog<String> nameDialog =
            new FreshNameDialog<String>(existingNames, name, mustBeFresh) {
                @Override
                protected String createName(String name) {
                    return name;
                }
            };
        nameDialog.showDialog(getFrame(), title);
        return nameDialog.getName();
    }

    /**
     * Enters a dialog that asks for a label to be renamed, and its the
     * replacement.
     * @return A pair consisting of the label to be replaced and its
     *         replacement, neither of which can be <code>null</code>; or
     *         <code>null</code> if the dialog was cancelled.
     */
    private Duo<TypeLabel> askRelabelling(TypeLabel oldLabel) {
        RelabelDialog dialog =
            new RelabelDialog(getGrammarView().getLabelStore(), oldLabel);
        if (dialog.showDialog(getFrame(), null)) {
            return new Duo<TypeLabel>(dialog.getOldLabel(),
                dialog.getNewLabel());
        } else {
            return null;
        }
    }

    /**
     * Returns the options object associated with the simulator.
     */
    public Options getOptions() {
        // lazily creates the options
        if (this.options == null) {
            this.options = new Options();
            this.options.getItem(SHOW_REMARKS_OPTION).setSelected(true);
            this.options.getItem(SHOW_STATE_IDS_OPTION).setSelected(true);
            this.options.getItem(SHOW_BACKGROUND_OPTION).setSelected(true);
        }
        return this.options;
    }

    /**
     * The options object of this simulator.
     */
    private Options options;

    /**
     * The underlying graph grammar of this simulator. If <tt>null</tt>, no
     * grammar has been loaded.
     */
    private StoredGrammarView grammarView;

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
    private RuleName currentRuleName;

    /**
     * The currently activated derivation.
     * @invariant currentTransition == null ||
     *            currentTransition.source().equals(currentState) &&
     *            currentTransition.rule().equals(currentRule)
     */
    private GraphTransition currentTransition;

    /**
     * The currently selected match.
     */
    private RuleEvent currentEvent;

    /**
     * The default exploration to be performed. This value is either the
     * previous exploration, or the default constructor of the Exploration class
     * (=breadth first). This value may never be null (and must be initialized
     * explicitly).
     */
    private Exploration defaultExploration;

    /**
     * The menu item associated with the 'default exploration' action. This
     * variable is used for setting the tool-tip.
     */
    private JMenuItem defaultExplorationMenuItem;

    /** The rule event applier for the current GTS. */
    private RuleEventApplier eventApplier;

    private ExploreStateStrategy exploreStateStrategy;

    /** Flag to indicate that one of the simulation events is underway. */
    private boolean updating;

    /** Flag to indicate that a {@link #switchTabs(JGraphPanel)} request is underway. */
    private boolean switchingTabs;

    /** Flag to indicate that the Simulator is in abstraction mode. */
    private boolean isAbstractionMode = false;

    /**
     * A mapping from extension filters (recognizing the file formats from the
     * names) to the corresponding grammar loaders.
     */
    private final Set<ExtensionFilter> grammarExtensions =
        new LinkedHashSet<ExtensionFilter>();

    /**
     * The graph loader used for saving aspect graphs.
     */
    private final Xml<AspectGraph> aspectLoader = AspectGxl.getInstance();

    /**
     * The graph loader used for saving arbitrary graphs.
     */
    private final DefaultGxl graphLoader = DefaultGxl.getInstance();

    /**
     * File chooser for grammar files.
     */
    private JFileChooser grammarFileChooser;

    /**
     * File chooser for state files and LTS.
     */
    private JFileChooser stateFileChooser;

    /**
     * File chooser for control files.
     */
    private JFileChooser ruleFileChooser;

    /**
     * Dialog for entering temporal formulae.
     */
    private StringDialog formulaDialog;

    /**
     * Graph exporter.
     */
    private final Exporter exporter = new Exporter();

    /**
     * Set of registered simulation listeners.
     * @invariant <tt>listeners \subseteq SimulationListener</tt>
     */
    private final Set<SimulationListener> listeners =
        new HashSet<SimulationListener>();

    /** Current set of refreshables of this simulator. */
    private final Set<Refreshable> refreshables = new HashSet<Refreshable>();
    /**
     * This application's main frame.
     */
    private JFrame frame;

    /** Production rule directory. */
    private RuleJTree ruleJTree;

    /** Panel with the ruleJTree plus toolbar. */
    private JScrollPane ruleTreePanel;

    /** Production system graph list */
    private StateJList stateJList;

    /** Production rule display panel. */
    private RulePanel rulePanel;

    /** State display panel. */
    private StatePanel statePanel;

    /** Control display panel. */
    private ControlPanel controlPanel;

    /** LTS display panel. (which is contained in the ConditionalLTSPanel) */
    private LTSPanel ltsPanel;

    /** Type graph display panel. */
    private TypePanel typePanel;

    /** Prolog display panel. */
    private PrologPanel prologPanel;

    /** Undo history. */
    private UndoHistory undoHistory;

    /** background for displays. */
    private SimulatorPanel simulatorPanel;

    /** History of recently opened grammars. */
    private History history;

    /** Statistics for the last exploration performed. */
    private ExplorationStatistics explorationStats;

    /** Menu for externally provided actions. */
    private JMenu externalMenu;

    /** The menu item containing the (current) export action. */
    private JMenuItem exportGraphMenuItem;

    /** Dummy action for the {@link #externalMenu}. */
    private Action dummyExternalAction;

    /**
     * Menu items in the edit menu for one of the graph or rule edit actions.
     */
    private JMenuItem editGraphItem;
    private JMenuItem copyGraphItem;
    private JMenuItem deleteGraphItem;
    private JMenuItem renameGraphItem;

    /** Returns the undo manager of this simulator. */
    private final UndoManager getUndoManager() {
        return this.undoManager;
    }

    /** The undo manager of this simulator. */
    private final UndoManager undoManager = new UndoManager() {
        @Override
        public void undoableEditHappened(UndoableEditEvent e) {
            super.undoableEditHappened(e);
            refreshActions();
        }

        @Override
        public synchronized void discardAllEdits() {
            super.discardAllEdits();
            refreshActions();
        }

        @Override
        public synchronized void redo() throws CannotRedoException {
            super.redo();
            refreshActions();
        }

        @Override
        public synchronized void undo() throws CannotUndoException {
            super.undo();
            refreshActions();
        }

        private void refreshActions() {
            getUndoAction().refresh();
            getRedoAction().refresh();
        }
    };

    /**
     * Class that spawns a thread to perform a long-lasting action, while
     * displaying a dialog that can interrupt the thread.
     */
    abstract private class CancellableThread extends Thread {
        /**
         * Constructs an action that can be canceled through a dialog.
         * @param parentComponent the parent for the cancel dialog
         * @param cancelDialogTitle the title of the cancel dialog
         */
        public CancellableThread(Component parentComponent,
                String cancelDialogTitle) {
            this.cancelDialog =
                createCancelDialog(parentComponent, cancelDialogTitle);
        }

        /**
         * Calls {@link #doAction()}, then disposes the cancel dialog.
         */
        @Override
        final public void run() {
            doAction();
            synchronized (this.cancelDialog) {
                // wait for the cancel dialog to become visible
                // (this is necessary if the doAction was actually very fast)
                while (!this.cancelDialog.isVisible()) {
                    try {
                        this.cancelDialog.wait(10);
                    } catch (InterruptedException e) {
                        // do nothing
                    }
                }
                this.cancelDialog.setVisible(false);
            }
        }

        /**
         * Method that should contain the code to be executed in parallel. It is
         * invoked as a callback method from {@link #run()}.
         */
        abstract protected void doAction();

        @Override
        public void start() {
            super.start();
            // make dialog visible
            this.cancelDialog.setVisible(true);
            // wait for the thread to return
            try {
                this.join();
            } catch (InterruptedException exc) {
                // thread is done
            }
            synchronized (this.cancelDialog) {
                this.cancelDialog.dispose();
            }
            finish();
        }

        /**
         * Every thread might perform some tasks after the action finished.
         */
        public abstract void finish();

        /**
         * Hook to give subclasses the opportunity to put something on the
         * cancel dialog. Note that this callback method is invoked at
         * construction time, so should not make reference to instance
         * variables.
         */
        protected Object createCancelDialogContent() {
            return new JLabel();
        }

        /**
         * Creates a modal dialog that will interrupt this thread, when the
         * cancel button is pressed.
         * @param parentComponent the parent for the dialog
         * @param title the title of the dialog
         */
        private JDialog createCancelDialog(Component parentComponent,
                String title) {
            JDialog result;
            // create message dialog
            JOptionPane message =
                new JOptionPane(createCancelDialogContent(),
                    JOptionPane.PLAIN_MESSAGE);
            JButton cancelButton = new JButton("Cancel");
            // add a button to interrupt the generation process and
            // wait for the thread to finish and rejoin this one
            cancelButton.addActionListener(createCancelListener());
            message.setOptions(new Object[] {cancelButton});
            result = message.createDialog(parentComponent, title);
            result.pack();
            return result;
        }

        /**
         * Returns a listener to this {@link CancellableThread} that interrupts
         * the thread and waits for it to rejoin this thread.
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
         * Callback method to refresh attributes of the action such as its name
         * and enabledness status.
         */
        void refresh();
    }

    /**
     * Creates an action that sets the name also as tool tip text, and registers
     * itself as a refreshable.
     */
    private abstract class RefreshableAction extends AbstractAction implements
            Refreshable {
        protected RefreshableAction(String name, Icon icon) {
            super(name, icon);
            putValue(SHORT_DESCRIPTION, name);
            setEnabled(false);
            addRefreshable(this);
        }
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
            JOptionPane.showMessageDialog(null, Version.getAboutHTML(),
                "About", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Returns the transition application action permanently associated with
     * this simulator.
     */
    public ApplyTransitionAction getApplyTransitionAction() {
        if (this.applyTransitionAction == null) {
            this.applyTransitionAction = new ApplyTransitionAction();
        }
        return this.applyTransitionAction;
    }

    /**
     * Action for applying the current derivation to the current state.
     * @see Simulator#applyMatch()
     */
    private class ApplyTransitionAction extends RefreshableAction {
        /** Constructs an instance of the action. */
        ApplyTransitionAction() {
            super(Options.APPLY_TRANSITION_ACTION_NAME, null);
            putValue(Action.ACCELERATOR_KEY, Options.APPLY_KEY);
            addAccelerator(this);
        }

        public void actionPerformed(ActionEvent evt) {
            applyMatch();
        }

        public void refresh() {
            setEnabled(getCurrentEvent() != null);
        }
    }

    /**
     * The transition application action permanently associated with this
     * simulator.
     */
    private ApplyTransitionAction applyTransitionAction;

    /**
     * Returns the back simulation action permanently associated with this
     * simulator.
     */
    public Action getBackAction() {
        if (this.backAction == null) {
            this.backAction = getSimulationHistory().getBackAction();
            addAccelerator(this.backAction);
        }
        return this.backAction;
    }

    /** The back simulation action permanently associated with this simulator. */
    private Action backAction;

    /**
     * Returns the graph copying action permanently associated with this
     * simulator.
     */
    public CopyGraphAction getCopyGraphAction() {
        // lazily create the action
        if (this.copyGraphAction == null) {
            this.copyGraphAction = new CopyGraphAction();
        }
        return this.copyGraphAction;
    }

    /**
     * The graph copying action permanently associated with this simulator.
     */
    private CopyGraphAction copyGraphAction;

    private class CopyGraphAction extends RefreshableAction {
        CopyGraphAction() {
            super(Options.COPY_GRAPH_ACTION_NAME, Icons.COPY_ICON);
            putValue(ACCELERATOR_KEY, Options.COPY_KEY);
        }

        public void refresh() {
            setEnabled(getGrammarStore() != null
                && getGrammarStore().isModifiable()
                && !getStateList().getSelectedGraphs().isEmpty());

            if (getGraphPanel() == getStatePanel()) {
                getCopyMenuItem().setAction(this);
            }
        }

        public void actionPerformed(ActionEvent e) {
            // Multiple selection
            // copy selected graph names
            List<String> selectedGraphs = new ArrayList<String>();
            for (Object name : getStateList().getSelectedValues()) {
                selectedGraphs.add((String) name);
            }
            for (String oldGraphName : selectedGraphs) {
                if (oldGraphName != null) {
                    GraphView oldGraphView =
                        getGrammarView().getGraphView(oldGraphName);
                    String newGraphName =
                        askNewGraphName("Select new graph name", oldGraphName,
                            true);
                    if (newGraphName != null) {
                        doAddGraph(oldGraphView.getAspectGraph().rename(
                            newGraphName));
                    }
                }
            }
        }
    }

    /**
     * Returns the rule copying action permanently associated with this
     * simulator.
     */
    public CopyRuleAction getCopyRuleAction() {
        // lazily create the action
        if (this.copyRuleAction == null) {
            this.copyRuleAction = new CopyRuleAction();
        }
        return this.copyRuleAction;
    }

    /**
     * The rule copying action permanently associated with this simulator.
     */
    private CopyRuleAction copyRuleAction;

    private class CopyRuleAction extends RefreshableAction {
        CopyRuleAction() {
            super(Options.COPY_RULE_ACTION_NAME, Icons.COPY_ICON);
            putValue(ACCELERATOR_KEY, Options.COPY_KEY);
        }

        public void refresh() {
            setEnabled(getCurrentRule() != null
                && getGrammarStore().isModifiable());

            if (getGraphPanel() == getRulePanel()) {
                getCopyMenuItem().setAction(this);
            }
        }

        public void actionPerformed(ActionEvent e) {
            // Multiple selection
            if (confirmAbandon(false)) {
                RuleName newRuleName = null;
                // copy the selected rules to avoid concurrent modifications
                List<RuleView> rules =
                    new ArrayList<RuleView>(getCurrentRuleSet());
                RuleName savedRule = null;
                for (RuleView rule : rules) {
                    AspectGraph oldRuleGraph = rule.getAspectGraph();
                    newRuleName =
                        askNewRuleName("Select new rule name", rule.getName(),
                            true);
                    if (newRuleName != null) {
                        AspectGraph newRuleGraph =
                            oldRuleGraph.rename(newRuleName.toString());
                        if (doAddRule(newRuleGraph)) {
                            savedRule = newRuleName;
                        }
                    }
                }
                // select last copied rule
                if (savedRule != null) {
                    setRule(savedRule);
                }
            }
        }
    }

    /**
     * Returns the graph deletion action permanently associated with this
     * simulator.
     */
    public DeleteGraphAction getDeleteGraphAction() {
        // lazily create the action
        if (this.deleteGraphAction == null) {
            this.deleteGraphAction = new DeleteGraphAction();
        }
        return this.deleteGraphAction;
    }

    /**
     * The graph deletion action permanently associated with this simulator.
     */
    private DeleteGraphAction deleteGraphAction;

    private class DeleteGraphAction extends RefreshableAction {
        DeleteGraphAction() {
            super(Options.DELETE_GRAPH_ACTION_NAME, Icons.DELETE_ICON);
            putValue(ACCELERATOR_KEY, Options.DELETE_KEY);
            addAccelerator(this);
        }

        public void refresh() {
            setEnabled(getGrammarStore() != null
                && getGrammarStore().isModifiable()
                && !getStateList().getSelectedGraphs().isEmpty());

            if (getGraphPanel() == getStatePanel()) {
                getDeleteMenuItem().setAction(this);
            }
        }

        public void actionPerformed(ActionEvent e) {
            // Multiple selection
            // copy selected graph names
            List<String> selectedGraphs = getStateList().getSelectedGraphs();
            String question = "Delete graph(s) '%s'";
            for (int i = 0; i < selectedGraphs.size(); i++) {
                String graphName = selectedGraphs.get(i);
                if (graphName != null) {
                    question = String.format(question, graphName);
                    boolean isStartGraph =
                        graphName.equals(getGrammarView().getStartGraphName());
                    if (isStartGraph) {
                        question = question + " (start graph)";
                    }
                    if (i < selectedGraphs.size() - 1) {
                        question = question + ", '%s'";
                    } else {
                        question = question + "?";
                    }
                }
            }
            if (confirmBehaviour(Options.DELETE_GRAPH_OPTION, question)) {
                for (String graphName : selectedGraphs) {
                    doDeleteGraph(graphName);
                }
            }
        }
    }

    /**
     * Returns the rule deletion action permanently associated with this
     * simulator.
     */
    public DeleteRuleAction getDeleteRuleAction() {
        // lazily create the action
        if (this.deleteRuleAction == null) {
            this.deleteRuleAction = new DeleteRuleAction();
        }
        return this.deleteRuleAction;
    }

    /**
     * The rule deletion action permanently associated with this simulator.
     */
    private DeleteRuleAction deleteRuleAction;

    private class DeleteRuleAction extends RefreshableAction {
        DeleteRuleAction() {
            super(Options.DELETE_RULE_ACTION_NAME, Icons.DELETE_ICON);
            putValue(ACCELERATOR_KEY, Options.DELETE_KEY);
            addAccelerator(this);
        }

        public void refresh() {
            setEnabled(getCurrentRule() != null
                && getGrammarStore().isModifiable());

            if (getGraphPanel() == getRulePanel()) {
                getDeleteMenuItem().setAction(this);
            }
        }

        public void actionPerformed(ActionEvent e) {
            // Multiple selection
            String question = "Delete rule(s) '%s'";
            // copy the selected rules to avoid concurrent modifications
            List<RuleView> rules = new ArrayList<RuleView>(getCurrentRuleSet());
            for (int i = 0; i < rules.size(); i++) {
                RuleName ruleName = rules.get(i).getRuleName();
                question = String.format(question, ruleName);
                if (i < rules.size() - 1) {
                    question = question + ", '%s'";
                } else {
                    question = question + "?";
                }
            }
            if (confirmBehaviour(Options.DELETE_RULE_OPTION, question)) {
                for (RuleView rule : rules) {
                    doDeleteRule(rule.getRuleName());
                }
            }
        }
    }

    /**
     * Lazily creates and returns the state edit action permanently associated
     * with this simulator.
     */
    public EditGraphAction getEditGraphAction() {
        // lazily create the action
        if (this.editGraphAction == null) {
            this.editGraphAction = new EditGraphAction();
        }
        return this.editGraphAction;
    }

    /**
     * The state edit action permanently associated with this simulator.
     */
    private EditGraphAction editGraphAction;

    /**
     * Action for editing the current state.
     */
    private class EditGraphAction extends RefreshableAction {
        /** Constructs an instance of the action. */
        EditGraphAction() {
            super(Options.EDIT_STATE_ACTION_NAME, Icons.EDIT_ICON);
            putValue(ACCELERATOR_KEY, Options.EDIT_KEY);
        }

        /**
         * Checks if the enabling condition is satisfied, and if so, calls
         * {@link #setEnabled(boolean)}.
         */
        public void refresh() {
            boolean enabled = getStatePanel().getJModel() != null;
            if (enabled != isEnabled()) {
                setEnabled(enabled);
            }
            if (getGraphPanel() == getStatePanel()) {
                getEditMenuItem().setAction(this);
            }
        }

        /**
         * Invokes the editor on the current state. Handles the execution of an
         * <code>EditGraphAction</code>, if the current panel is the state
         * panel.
         */
        public void actionPerformed(ActionEvent e) {
            AspectGraph graph = getStatePanel().getJModel().getGraph();
            if (getStatePanel().isShowingState()) {
                String newGraphName =
                    askNewGraphName("Select graph name", graph.getName(), true);
                if (newGraphName != null) {
                    handleEditGraph(graph, true);
                }
            } else {
                handleEditGraph(graph, false);
            }
        }
    }

    /**
     * Returns the properties edit action permanently associated with this
     * simulator.
     */
    public EditRulePropertiesAction getEditRulePropertiesAction() {
        // lazily create the action
        if (this.editRulePropertiesAction == null) {
            this.editRulePropertiesAction = new EditRulePropertiesAction();
        }
        return this.editRulePropertiesAction;
    }

    /**
     * The rule properties edit action permanently associated with this
     * simulator.
     */
    private EditRulePropertiesAction editRulePropertiesAction;

    private class EditRulePropertiesAction extends RefreshableAction {
        EditRulePropertiesAction() {
            super(Options.RULE_PROPERTIES_ACTION_NAME, null);
        }

        public void refresh() {
            setEnabled(getCurrentRule() != null
                && getGrammarStore().isModifiable()
                && getCurrentRuleSet().size() == 1);
        }

        public void actionPerformed(ActionEvent e) {
            // Get selected rules.
            List<RuleView> selectedRules = getCurrentRuleSet();
            RuleView[] ruleViews =
                selectedRules.toArray(new RuleView[selectedRules.size()]);

            // Associated rule graphs.
            AspectGraph[] ruleGraphs = new AspectGraph[ruleViews.length];
            // Associated rule properties.
            GraphProperties[] ruleProperties =
                new GraphProperties[ruleViews.length];

            // INVARIANT: related elements in the arrays are stored at
            // the same position.
            for (int i = 0; i < ruleViews.length; i++) {
                ruleGraphs[i] = ruleViews[i].getAspectGraph();
                ruleProperties[i] =
                    GraphInfo.getProperties(ruleGraphs[i], true).clone();
            }

            // Use the first properties of the first rule as the starting point. 
            GraphProperties dialogProperties =
                new GraphProperties(ruleProperties[0]);

            // Now we go through the rest of the properties and check if there
            // are conflicts. If yes the property will be empty in the dialog.
            for (int i = 1; i < ruleViews.length; i++) {
                for (String key : GraphProperties.DEFAULT_USER_KEYS.keySet()) {
                    String entryValue = (String) ruleProperties[i].get(key);
                    String dialogValue = (String) dialogProperties.get(key);
                    if (dialogValue != null && !dialogValue.equals(entryValue)) {
                        // We have a conflict. Remove the key from the dialog.
                        dialogProperties.remove(key);
                    }
                }
            }

            PropertiesDialog dialog =
                new PropertiesDialog(dialogProperties,
                    GraphProperties.DEFAULT_USER_KEYS, true);

            if (dialog.showDialog(getFrame()) && confirmAbandon(false)) {

                // We go through the results of the dialog.
                GraphProperties editedProperties =
                    new GraphProperties(dialog.getEditedProperties());
                for (int i = 0; i < ruleViews.length; i++) {
                    for (String key : GraphProperties.DEFAULT_USER_KEYS.keySet()) {
                        String entryValue = (String) ruleProperties[i].get(key);
                        String editedValue = (String) editedProperties.get(key);
                        String defaultValue =
                            GraphProperties.getDefaultValue(key);
                        if (editedValue != null
                            && !editedValue.equals(entryValue)) {
                            // The value was changed in the dialog, set it in
                            // the rule properties.
                            ruleProperties[i].setProperty(key, editedValue);
                        } else if (editedValue == null && entryValue != null
                            && !defaultValue.equals(entryValue)) {
                            // The value was cleared in the dialog, set the
                            // default value in the rule properties.
                            ruleProperties[i].setProperty(key, defaultValue);
                        }
                    }
                }

                // Now all the elements of the ruleProperties[] are correct.
                // Let's recreate the rules.
                for (int i = 0; i < ruleViews.length; i++) {
                    // Avoiding call to doDeleteRule() and doAddRule() because
                    // of grammar updates.
                    try {
                        AspectGraph newGraph = ruleGraphs[i].clone();
                        GraphInfo.setProperties(newGraph, ruleProperties[i]);
                        newGraph.setFixed();
                        getGrammarStore().putRule(newGraph);
                        ruleGraphs[i].invalidateView();
                    } catch (IOException exc) {
                        showErrorDialog(String.format(
                            "Error while storing rule '%s'",
                            ruleGraphs[i].getName()), exc);
                    } catch (UnsupportedOperationException u) {
                        showErrorDialog("Current grammar is read-only", u);
                    }
                }
                // We are done with the rule changes.
                // Update the grammar, but just once.. :P
                updateGrammar();
            }
        }
    }

    /**
     * Lazily creates and returns the rule edit action permanently associated
     * with this simulator.
     */
    public EditRuleAction getEditRuleAction() {
        // lazily create the action
        if (this.editRuleAction == null) {
            this.editRuleAction = new EditRuleAction();
        }
        return this.editRuleAction;
    }

    /**
     * The rule edit action permanently associated with this simulator.
     */
    private EditRuleAction editRuleAction;

    /**
     * Action for editing the current state or rule.
     */
    private class EditRuleAction extends RefreshableAction {
        /** Constructs an instance of the action. */
        EditRuleAction() {
            super(Options.EDIT_RULE_ACTION_NAME, Icons.EDIT_ICON);
            putValue(ACCELERATOR_KEY, Options.EDIT_KEY);
        }

        /**
         * Checks if the enabling condition is satisfied, and if so, calls
         * {@link #setEnabled(boolean)}.
         */
        public void refresh() {
            boolean enabled =
                getCurrentRule() != null && getGrammarStore().isModifiable()
                    && getCurrentRuleSet().size() == 1;
            if (enabled != isEnabled()) {
                setEnabled(enabled);
            }

            if (getGraphPanel() == getRulePanel()) {
                getEditMenuItem().setAction(this);
            }
        }

        /**
         * Invokes the editor on the current rule. Handles the execution of an
         * <code>EditGraphAction</code>, if the current panel is the rule panel.
         * 
         * @require <tt>getCurrentRule != null</tt>.
         */
        public void actionPerformed(ActionEvent e) {
            handleEditGraph(getCurrentRule().getAspectGraph(), false);
        }
    }

    /** Returns the action to show the system properties of the current grammar. */
    public Action getEditSystemPropertiesAction() {
        // lazily create the action
        if (this.editSystemPropertiesAction == null) {
            this.editSystemPropertiesAction = new EditSystemPropertiesAction();
        }
        return this.editSystemPropertiesAction;
    }

    /**
     * The action to show the system properties of the currently selected
     * grammar.
     */
    private EditSystemPropertiesAction editSystemPropertiesAction;

    /** Action to show the system properties. */
    private class EditSystemPropertiesAction extends RefreshableAction {
        /** Constructs an instance of the action. */
        EditSystemPropertiesAction() {
            super(Options.SYSTEM_PROPERTIES_ACTION_NAME, null);
        }

        /**
         * Displays a {@link PropertiesDialog} for the properties of the edited
         * graph.
         */
        public void actionPerformed(ActionEvent e) {
            StoredGrammarView grammar = getGrammarView();
            Properties systemProperties = grammar.getProperties();
            PropertiesDialog dialog =
                new PropertiesDialog(systemProperties,
                    SystemProperties.DEFAULT_KEYS, true);
            if (dialog.showDialog(getFrame()) && confirmAbandon(false)) {
                SystemProperties newProperties = new SystemProperties();
                newProperties.putAll(dialog.getEditedProperties());
                doSaveProperties(newProperties);
            }
        }

        /**
         * Tests if the currently selected grammar has non-<code>null</code>
         * system properties.
         */
        public void refresh() {
            setEnabled(getGrammarView() != null
                && getGrammarStore().isModifiable());
        }
    }

    /**
     * Lazily creates and returns the type edit action permanently associated
     * with this simulator.
     */
    public Action getEditTypeAction() {
        return getTypePanel().getEditAction();
    }

    /**
     * Returns the rule enabling action permanently associated with this
     * simulator.
     */
    public EnableRuleAction getEnableRuleAction() {
        // lazily create the action
        if (this.enableRuleAction == null) {
            this.enableRuleAction = new EnableRuleAction();
        }
        return this.enableRuleAction;
    }

    /**
     * The rule enabling action permanently associated with this simulator.
     */
    private EnableRuleAction enableRuleAction;

    /**
     * Action that changes the enabledness status of the currently selected
     * rule.
     * @see #doEnableRule()
     */
    private class EnableRuleAction extends RefreshableAction {
        EnableRuleAction() {
            super(Options.DISABLE_RULE_ACTION_NAME, null);
        }

        public void refresh() {
            boolean ruleSelected = getCurrentRule() != null;
            setEnabled(ruleSelected && getGrammarStore().isModifiable());
            if (ruleSelected && getCurrentRule().isEnabled()) {
                putValue(NAME, Options.DISABLE_RULE_ACTION_NAME);
            } else {
                putValue(NAME, Options.ENABLE_RULE_ACTION_NAME);
            }
        }

        public void actionPerformed(ActionEvent e) {
            if (confirmAbandon(false)) {
                doEnableRule();
            }
        }
    }

    /**
     * Returns the 'default exploration' action that is associated with the
     * simulator.
     */
    public DefaultExplorationAction getDefaultExplorationAction() {
        // lazily create the action
        if (this.defaultExplorationAction == null) {
            this.defaultExplorationAction = new DefaultExplorationAction();
        }

        return this.defaultExplorationAction;
    }

    /**
     * The 'default exploration' action (variable).
     */
    private DefaultExplorationAction defaultExplorationAction;

    /**
     * The 'default exploration' action (class).
     */
    private class DefaultExplorationAction extends RefreshableAction {
        DefaultExplorationAction() {
            super(Options.DEFAULT_EXPLORATION_ACTION_NAME, Icons.FORWARD_ICON);
            putValue(ACCELERATOR_KEY, Options.DEFAULT_EXPLORATION_KEY);
        }

        public void actionPerformed(ActionEvent evt) {
            doRunExploration(getDefaultExploration(), true);
        }

        public void refresh() {
            setEnabled(getGrammarView() != null
                && getGrammarView().getStartGraphView() != null
                && getGrammarView().getErrors().isEmpty());
        }
    }

    /**
     * Returns the exploration dialog action permanently associated with this
     * simulator.
     */
    public ExplorationDialogAction getExplorationDialogAction() {
        // lazily create the action
        if (this.explorationDialogAction == null) {
            this.explorationDialogAction = new ExplorationDialogAction();
        }
        return this.explorationDialogAction;
    }

    /**
     * The exploration dialog action permanently associated with this simulator.
     */
    private ExplorationDialogAction explorationDialogAction;

    /** Action to open the Exploration Dialog. */
    private class ExplorationDialogAction extends RefreshableAction {
        /** Constructs an instance of the action. */
        ExplorationDialogAction() {
            super(Options.EXPLORATION_DIALOG_ACTION_NAME, null);
        }

        public void actionPerformed(ActionEvent evt) {
            new ExplorationDialog(StrategyEnumerator.MASK_CONCRETE,
                AcceptorEnumerator.MASK_CONCRETE, Simulator.this, getFrame());
        }

        public void refresh() {
            setEnabled(getGrammarView() != null
                && getGrammarView().getStartGraphView() != null
                && getGrammarView().getErrors().isEmpty());
        }
    }

    /**
     * Returns the exploration statistics dialog action permanently associated
     * with this simulator.
     */
    public ExplorationStatsDialogAction getExplorationStatsDialogAction() {
        // lazily create the action
        if (this.explorationStatsDialogAction == null) {
            this.explorationStatsDialogAction =
                new ExplorationStatsDialogAction();
        }
        return this.explorationStatsDialogAction;
    }

    /**
     * The exploration statistics dialog action permanently associated with
     * this simulator.
     */
    private ExplorationStatsDialogAction explorationStatsDialogAction;

    /** Action to open the Exploration Statistics Dialog. */
    private class ExplorationStatsDialogAction extends RefreshableAction {
        /** Constructs an instance of the action. */
        ExplorationStatsDialogAction() {
            super(Options.EXPLORATION_STATS_DIALOG_ACTION_NAME, null);
        }

        public void actionPerformed(ActionEvent evt) {
            new ExplorationStatsDialog(Simulator.this, getFrame());
        }

        public void refresh() {
            setEnabled(getGrammarView() != null
                && getGrammarView().getStartGraphView() != null
                && getGrammarView().getErrors().isEmpty());
        }
    }

    /**
     * Returns the Save LTS As action permanently associated with this simulator.
     */
    public SaveLTSAsAction getSaveLTSAsAction() {
        // lazily create the action
        if (this.saveLtsAsAction == null) {
            this.saveLtsAsAction = new SaveLTSAsAction();
        }
        return this.saveLtsAsAction;
    }

    /** The LTS Save As action permanently associated with this simulator. */
    private SaveLTSAsAction saveLtsAsAction;

    private class SaveLTSAsAction extends RefreshableAction {
        SaveLTSAsAction() {
            super("Save LTS As...", null);
        }

        public void actionPerformed(ActionEvent arg0) {
            SaveLTSAsDialog dialog = new SaveLTSAsDialog(Simulator.this);
            if (getLastGrammarFile() != null) {
                dialog.setCurrentDirectory(getLastGrammarFile().getAbsolutePath());
            }

            if (dialog.showDialog(Simulator.this)) {

                File file = new File(dialog.getDirectory());
                int exportStates = dialog.getExportStates();
                boolean showFinal = dialog.showFinal();
                boolean showNames = dialog.showNames();
                boolean showStart = dialog.showStart();
                boolean showOpen = dialog.showOpen();

                GTS gts = Simulator.this.getGTS();

                DefaultGraph lts =
                    gts.toPlainGraph(showFinal, showStart, showOpen, showNames);

                Collection<GraphState> export = new HashSet<GraphState>(0);

                if (exportStates == SaveLTSAsDialog.STATES_ALL) {
                    export = gts.getStateSet();
                } else if (exportStates == SaveLTSAsDialog.STATES_FINAL) {
                    export = gts.getFinalStates();
                }

                try {
                    Groove.saveGraph(lts,
                        new File(file, "lts.gxl").getAbsolutePath());
                    for (GraphState state : export) {
                        String name = state.toString();
                        Groove.saveGraph(
                            state.getGraph().toAspectMap().getAspectGraph().toPlainGraph(),
                            new File(file, name + ".gst").getAbsolutePath());
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

        public void refresh() {
            setEnabled(getGTS() != null);
        }
    }

    /**
     * Returns the forward (= repeat) simulation action permanently associated
     * with this simulator.
     */
    public Action getForwardAction() {
        if (this.forwardAction == null) {
            this.forwardAction = getSimulationHistory().getForwardAction();
            addAccelerator(this.forwardAction);
        }
        return this.forwardAction;
    }

    /**
     * The forward simulation action permanently associated with this simulator.
     */
    private Action forwardAction;

    /**
     * Returns the go-to start state action permanently associated with this
     * simulator.
     */
    public GotoStartStateAction getGotoStartStateAction() {
        // lazily create the action
        if (this.gotoStartStateAction == null) {
            this.gotoStartStateAction = new GotoStartStateAction();
        }
        return this.gotoStartStateAction;
    }

    /**
     * The go-to start state action permanently associated with this simulator.
     */
    private GotoStartStateAction gotoStartStateAction;

    /**
     * Action for setting the initial state of the LTS as current state.
     * @see GTS#startState()
     * @see Simulator#setState(GraphState)
     */
    private class GotoStartStateAction extends RefreshableAction {
        /** Constructs an instance of the action. */
        GotoStartStateAction() {
            super(Options.GOTO_START_STATE_ACTION_NAME, null);
            putValue(ACCELERATOR_KEY, Options.GOTO_START_STATE_KEY);
        }

        public void actionPerformed(ActionEvent evt) {
            setState(getGTS().startState());
        }

        public void refresh() {
            setEnabled(getGTS() != null);
        }
    }

    /**
     * Returns the rule load action permanently associated with this simulator.
     */
    public ImportRuleAction getImportRuleAction() {
        // lazily create the action
        if (this.importRuleAction == null) {
            this.importRuleAction = new ImportRuleAction();
        }
        return this.importRuleAction;
    }

    /**
     * The rule load action permanently associated with this simulator.
     */
    private ImportRuleAction importRuleAction;

    /**
     * Action for loading and setting a different control program.
     */
    private class ImportRuleAction extends RefreshableAction {
        /** Constructs an instance of the action. */
        ImportRuleAction() {
            super(Options.IMPORT_RULE_ACTION_NAME, null);
        }

        public void actionPerformed(ActionEvent evt) {
            int result = getRuleFileChooser().showOpenDialog(getFrame());
            // now load, if so required
            if (result == JFileChooser.APPROVE_OPTION && confirmAbandon(false)) {
                try {
                    File ruleFile = getRuleFileChooser().getSelectedFile();
                    AspectGraph ruleGraph = unmarshalGraph(ruleFile);
                    RuleName ruleName = new RuleName(ruleGraph.getName());
                    if (getGrammarView().getRuleView(ruleName) == null
                        || confirmOverwriteRule(ruleName)) {
                        if (doAddRule(ruleGraph)) {
                            setRule(ruleName);
                        }
                    }
                } catch (IOException e) {
                    showErrorDialog("Error loading rule", e);
                }
            }
        }

        /**
         * Sets the enabling status of this action, depending on whether a
         * grammar is currently loaded.
         */
        public void refresh() {
            setEnabled(getGrammarView() != null);
        }
    }

    /** Creates an action associated to a scenario handler. */
    public LaunchScenarioAction getLaunchScenarioAction(Scenario scenario) {
        // no reuse: the action depends on the scenario
        return new LaunchScenarioAction(scenario);
    }

    /** An action used for launching a scenario. */
    public class LaunchScenarioAction extends AbstractAction {
        LaunchScenarioAction(Scenario scenario) {
            super(scenario.getName());
            this.scenario = scenario;
        }

        @Override
        public void setEnabled(boolean enabled) {
            super.setEnabled(enabled);
            putValue(Action.NAME, this.scenario.getName());
        }

        public void actionPerformed(ActionEvent evt) {
            doGenerate(this.scenario);
        }

        private final Scenario scenario;
    }

    /**
     * Thread class to wrap the exploration of the simulator's current GTS.
     * Either operates on a scenario (only used from the Generator, will be
     * removed!) or an exploration.
     */
    private class LaunchThread extends CancellableThread {
        /**
         * Constructs a generate thread for a given (model checking) scenario.
         * @param scenario the scenario handler of this thread
         */
        LaunchThread(Scenario scenario) {
            super(getLtsPanel(), "Exploring state space");
            this.scenario = scenario;
            this.exploration = null;
            this.progressListener = createProgressListener();
            this.emphasise = true;
        }

        /**
         * Constructs a generate thread for a given exploration strategy.
         * @param exploration the exploration handler of this thread
         * @param emphasise if {@code true}, the result of the exploration should
         * be emphasised
         */
        LaunchThread(Exploration exploration, boolean emphasise) {
            super(getLtsPanel(), "Exploring state space");
            this.scenario = null;
            this.exploration = exploration;
            this.emphasise = emphasise;
            this.progressListener = createProgressListener();
        }

        @Override
        public void doAction() {
            GTS gts = getGTS();
            displayProgress(gts);
            gts.addLTSListener(this.progressListener);

            if (this.exploration == null) {
                this.scenario.play();
            } else {
                try {
                    this.exploration.play(getGTS(), getCurrentState());
                } catch (FormatException exc) {
                    String[] options = {"Yes", "No"};
                    String message =
                        "The last exploration is no longer valid in the "
                            + "current grammar. \n" + "Cannot apply '"
                            + this.exploration.getIdentifier() + "'.\n\n"
                            + "Use Breadth-First Exploration instead?\n";
                    int response =
                        JOptionPane.showOptionDialog(Simulator.this.getFrame(),
                            message, "Invalid Exploration",
                            JOptionPane.YES_NO_CANCEL_OPTION,
                            JOptionPane.QUESTION_MESSAGE, null, options,
                            options[0]);
                    if (response == 0) {
                        Exploration newExplore = new Exploration();
                        setDefaultExploration(newExplore);
                        try {
                            newExplore.play(getGTS(), getCurrentState());
                        } catch (FormatException e) {
                            showErrorDialog("Error: cannot parse exploration.",
                                e);
                        }
                    }
                }
            }
            gts.removeLTSListener(this.progressListener);
        }

        @Override
        public void finish() {
            Collection<GraphState> result;

            if (this.exploration == null) {
                result = this.scenario.getResult().getValue();
            } else {
                result = this.exploration.getLastResult().getValue();
            }

            final List<GraphState> states = new ArrayList<GraphState>(result);
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    if (LaunchThread.this.emphasise) {
                        getLtsPanel().emphasiseStates(states, true);
                    }
                    if (LaunchThread.this.scenario instanceof ModelCheckingScenario) {
                        String property =
                            ((ModelCheckingScenario) LaunchThread.this.scenario).getProperty();
                        if (states.isEmpty()) {
                            JOptionPane.showMessageDialog(getFrame(),
                                String.format(
                                    "The property %s holds for this LTS",
                                    property));
                        } else {
                            JOptionPane.showMessageDialog(getFrame(),
                                String.format(
                                    "A counter-example to %s is highlighted",
                                    property));
                        }
                    }
                }
            });
        }

        /** This implementation returns the state and transition count labels. */
        @Override
        protected Object createCancelDialogContent() {
            return new Object[] {getStateCountLabel(),
                getTransitionCountLabel()};
        }

        /**
         * Creates a graph listener that displays the progress of the generate
         * thread on the cancel dialog.
         */
        private GTSListener createProgressListener() {
            return new GTSAdapter() {
                @Override
                public void addUpdate(GTS gts, GraphState state) {
                    displayProgress(gts);
                }

                @Override
                public void addUpdate(GTS gts, GraphTransition transition) {
                    displayProgress(gts);
                }
            };
        }

        /**
         * Returns the {@link JLabel} used to display the state count in the
         * cencel dialog; first creates the label if that is not yet done.
         */
        private JLabel getStateCountLabel() {
            // lazily create the label
            if (this.stateCountLabel == null) {
                this.stateCountLabel = new JLabel();
            }
            return this.stateCountLabel;
        }

        /**
         * Returns the {@link JLabel} used to display the state count in the
         * cencel dialog; first creates the label if that is not yet done.
         */
        private JLabel getTransitionCountLabel() {
            // lazily create the label
            if (this.transitionCountLabel == null) {
                this.transitionCountLabel = new JLabel();
            }
            return this.transitionCountLabel;
        }

        /**
         * Displays the number of lts states and transitions in the message
         * dialog.
         */
        void displayProgress(GTS gts) {
            getStateCountLabel().setText("States: " + gts.nodeCount());
            getTransitionCountLabel().setText("Transitions: " + gts.edgeCount());
        }

        /** LTS generation strategy of this thread. (old version) */
        private final Scenario scenario;
        /** LTS generation strategy of this thread. (new version) */
        private final Exploration exploration;
        /** Flag indicating if the result states should be emphasised after exploration */
        private final boolean emphasise;
        /** Progress listener for the generate thread. */
        private final GTSListener progressListener;
        /** Label displaying the number of states generated so far. */
        private JLabel transitionCountLabel;
        /** Label displaying the number of transitions generated so far. */
        private JLabel stateCountLabel;
    }

    /**
     * Returns the start graph load action permanently associated with this
     * simulator.
     */
    public LoadStartGraphAction getLoadStartGraphAction() {
        // lazily create the action
        if (this.loadStartGraphAction == null) {
            this.loadStartGraphAction = new LoadStartGraphAction();
        }
        return this.loadStartGraphAction;
    }

    /** The start state load action permanently associated with this simulator. */
    private LoadStartGraphAction loadStartGraphAction;

    /**
     * Action for loading and setting a new initial state.
     * @see Simulator#doLoadStartGraph(File)
     */
    private class LoadStartGraphAction extends RefreshableAction {
        /** Constructs an instance of the action. */
        LoadStartGraphAction() {
            super(Options.LOAD_START_STATE_ACTION_NAME, null);
        }

        public void actionPerformed(ActionEvent evt) {
            // stateFileChooser.setSelectedFile(currentStartStateFile);
            int result = getStateFileChooser().showOpenDialog(getFrame());
            // now load, if so required
            if (result == JFileChooser.APPROVE_OPTION && confirmAbandon(false)) {
                doLoadStartGraph(getStateFileChooser().getSelectedFile());
            }
        }

        /**
         * Sets the enabling status of this action, depending on whether a
         * grammar is currently loaded.
         */
        public void refresh() {
            setEnabled(getGrammarView() != null);
        }
    }

    /**
     * Returns the grammar load action permanently associated with this
     * simulator.
     */
    public Action getLoadGrammarAction() {
        // lazily create the action
        if (this.loadGrammarAction == null) {
            this.loadGrammarAction = new LoadGrammarAction();
        }
        return this.loadGrammarAction;
    }

    /** The grammar load action permanently associated with this simulator. */
    private LoadGrammarAction loadGrammarAction;

    /**
     * Action for loading a new rule system.
     * @see Simulator#doLoadGrammar(URL)
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
                doLoadGrammar(selectedFile, null);
            }
        }
    }

    /**
     * Action for loading a new rule system.
     * @see Simulator#doLoadGrammar(URL)
     */
    private class LoadURLAction extends AbstractAction {
        /** Constructs an instance of the action. */
        LoadURLAction() {
            super(Options.LOAD_URL_GRAMMAR_ACTION_NAME);
            putValue(ACCELERATOR_KEY, Options.OPEN_URL_KEY);
            addAccelerator(this);
        }

        public void actionPerformed(ActionEvent evt) {
            String input = JOptionPane.showInputDialog("Input Grammar URL:");
            if (input != null) {
                try {
                    URL url = new URL(input);
                    doLoadGrammar(url);
                } catch (MalformedURLException e) {
                    showErrorDialog(
                        String.format("Invalid URL '%s'", e.getMessage()), e);
                }
            }
        }
    }

    /**
     * Returns the rule system creation action permanently associated with this
     * simulator.
     */
    public NewGrammarAction getNewGrammarAction() {
        // lazily create the action
        if (this.newGrammarAction == null) {
            this.newGrammarAction = new NewGrammarAction();
        }
        return this.newGrammarAction;
    }

    /**
     * The rule system creation action permanently associated with this
     * simulator.
     */
    private NewGrammarAction newGrammarAction;

    /** Action to create and load a new, initially empty graph grammar. */
    private class NewGrammarAction extends AbstractAction {
        NewGrammarAction() {
            super(Options.NEW_GRAMMAR_ACTION_NAME);
        }

        public void actionPerformed(ActionEvent e) {
            if (confirmAbandon(true)) {
                File grammarFile = getLastGrammarFile();
                File newGrammar;
                if (grammarFile == null) {
                    newGrammar = new File(NEW_GRAMMAR_NAME);
                } else {
                    newGrammar =
                        new File(grammarFile.getParentFile(), NEW_GRAMMAR_NAME);
                }
                getGrammarFileChooser().setSelectedFile(newGrammar);
                getGrammarFileChooser().setFileFilter(GRAMMAR_FILTER);
                boolean ok = false;
                while (!ok) {
                    if (getGrammarFileChooser().showOpenDialog(getFrame()) == JFileChooser.APPROVE_OPTION) {
                        File selectedFile =
                            getGrammarFileChooser().getSelectedFile();

                        FileFilter filter =
                            getGrammarFileChooser().getFileFilter();

                        if (filter instanceof ExtensionFilter) {
                            String extendedName =
                                ((ExtensionFilter) filter).addExtension(selectedFile.getPath());
                            selectedFile = new File(extendedName);
                        }
                        if (selectedFile.exists()) {
                            int response =
                                JOptionPane.showConfirmDialog(getFrame(),
                                    String.format("Load existing grammar %s?",
                                        selectedFile.getName()));
                            if (response == JOptionPane.OK_OPTION) {
                                doLoadGrammar(selectedFile, null);
                            }
                            ok = response != JOptionPane.NO_OPTION;
                        } else {
                            doNewGrammar(selectedFile);
                            ok = true;
                        }
                    } else {
                        ok = true;
                    }
                }
            }
        }
    }

    /**
     * Returns the graph creation action permanently associated with this
     * simulator.
     */
    public NewGraphAction getNewGraphAction() {
        // lazily create the action
        if (this.newGraphAction == null) {
            this.newGraphAction = new NewGraphAction();
        }
        return this.newGraphAction;
    }

    /**
     * The graph creation action permanently associated with this simulator.
     */
    private NewGraphAction newGraphAction;

    private class NewGraphAction extends RefreshableAction {
        NewGraphAction() {
            super(Options.NEW_GRAPH_ACTION_NAME, Icons.NEW_GRAPH_ICON);
        }

        public void actionPerformed(ActionEvent e) {
            String newGraphName =
                askNewGraphName("Select graph name", NEW_GRAPH_NAME, true);
            if (newGraphName != null) {
                AspectGraph newGraph =
                    AspectGraph.emptyGraph(newGraphName, HOST);
                handleEditGraph(newGraph, true);
            }
        }

        /** Enabled if there is a grammar loaded. */
        public void refresh() {
            setEnabled(getGrammarView() != null
                && getGrammarStore().isModifiable());
        }
    }

    /**
     * Returns the rule creation action permanently associated with this
     * simulator.
     */
    public NewRuleAction getNewRuleAction() {
        // lazily create the action
        if (this.newRuleAction == null) {
            this.newRuleAction = new NewRuleAction();
        }
        return this.newRuleAction;
    }

    /**
     * The rule creation action permanently associated with this simulator.
     */
    private NewRuleAction newRuleAction;

    private class NewRuleAction extends RefreshableAction {
        NewRuleAction() {
            super(Options.NEW_RULE_ACTION_NAME, Icons.NEW_RULE_ICON);
        }

        public void actionPerformed(ActionEvent e) {
            final RuleName ruleName = askNewRuleName(null, NEW_RULE_NAME, true);
            if (ruleName != null) {
                AspectGraph newRule =
                    AspectGraph.emptyGraph(ruleName.toString(), RULE);
                handleEditGraph(newRule, true);
            }
        }

        /** Enabled if there is a grammar loaded. */
        public void refresh() {
            setEnabled(getGrammarView() != null
                && getGrammarStore().isModifiable());
        }
    }

    /**
     * Returns the rule creation action permanently associated with this
     * simulator.
     */
    public Action getNewTypeAction() {
        return getTypePanel().getNewAction();
    }

    /** Returns the quit action permanently associated with this simulator. */
    public Action getQuitAction() {
        // lazily create the action
        if (this.quitAction == null) {
            this.quitAction = new QuitAction();
        }
        return this.quitAction;
    }

    /**
     * The quit action permanently associated with this simulator.
     */
    private QuitAction quitAction;

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
     * Returns the redo action permanently associated with this simulator.
     */
    public RedoAction getRedoAction() {
        if (this.redoAction == null) {
            this.redoAction = new RedoAction();
        }
        return this.redoAction;
    }

    /**
     * The redo permanently associated with this simulator.
     */
    private RedoAction redoAction;

    /**
     * Action for redoing the last edit to the grammar.
     */
    private class RedoAction extends AbstractAction {
        /** Constructs an instance of the action. */
        RedoAction() {
            super(Options.REDO_ACTION_NAME, Icons.REDO_ICON);
            putValue(SHORT_DESCRIPTION, Options.REDO_ACTION_NAME);
            putValue(ACCELERATOR_KEY, Options.REDO_KEY);
            setEnabled(false);
        }

        public void actionPerformed(ActionEvent evt) {
            getUndoManager().redo();
            updateGrammar();
        }

        public void refresh() {
            if (getUndoManager().canRedo()) {
                setEnabled(true);
                putValue(Action.NAME,
                    getUndoManager().getRedoPresentationName());
            } else {
                setEnabled(false);
                putValue(Action.NAME, Options.REDO_ACTION_NAME);
            }
        }
    }

    /**
     * Returns the grammar refresh action permanently associated with this
     * simulator.
     */
    public RefreshGrammarAction getRefreshGrammarAction() {
        // lazily create the action
        if (this.refreshGrammarAction == null) {
            this.refreshGrammarAction = new RefreshGrammarAction();
        }
        return this.refreshGrammarAction;
    }

    /** The grammar refresh action permanently associated with this simulator. */
    private RefreshGrammarAction refreshGrammarAction;

    /**
     * Action for refreshing the rule system. Reloads the current rule system
     * and start graph.
     * @see Simulator#doRefreshGrammar()
     */
    private class RefreshGrammarAction extends RefreshableAction {
        /** Constructs an instance of the action. */
        RefreshGrammarAction() {
            super(Options.REFRESH_GRAMMAR_ACTION_NAME, null);
            putValue(ACCELERATOR_KEY, Options.REFRESH_KEY);
            addAccelerator(this);
        }

        public void actionPerformed(ActionEvent evt) {
            if (confirmAbandon(false)) {
                doRefreshGrammar();
            }
        }

        public void refresh() {
            setEnabled(getGrammarView() != null);
        }
    }

    /**
     * Returns the renumbering action permanently associated with this
     * simulator.
     */
    public RenumberAction getRenumberAction() {
        // lazily create the action
        if (this.renumberAction == null) {
            this.renumberAction = new RenumberAction();
        }
        return this.renumberAction;
    }

    /**
     * The renumbering action permanently associated with this simulator.
     */
    private RenumberAction renumberAction;

    private class RenumberAction extends RefreshableAction {
        RenumberAction() {
            super(Options.RENUMBER_ACTION_NAME, null);
        }

        public void refresh() {
            setEnabled(getGrammarView() != null
                && getGrammarStore().isModifiable());
        }

        public void actionPerformed(ActionEvent e) {
            doRenumber();
        }
    }

    /**
     * Returns the rule renaming action permanently associated with this
     * simulator.
     */
    public RelabelAction getRelabelAction() {
        // lazily create the action
        if (this.relabelAction == null) {
            this.relabelAction = new RelabelAction();
        }
        return this.relabelAction;
    }

    /**
     * The graph renaming action permanently associated with this simulator.
     */
    private RelabelAction relabelAction;

    private class RelabelAction extends RefreshableAction implements
            GraphSelectionListener, TreeSelectionListener {
        RelabelAction() {
            super(Options.RELABEL_ACTION_NAME, Icons.RENAME_ICON);
            getStatePanel().getJGraph().addGraphSelectionListener(this);
            getStatePanel().getLabelTree().addTreeSelectionListener(this);
            getRulePanel().getJGraph().addGraphSelectionListener(this);
            getRulePanel().getLabelTree().addTreeSelectionListener(this);
            getTypePanel().getJGraph().addGraphSelectionListener(this);
            getTypePanel().getLabelTree().addTreeSelectionListener(this);
        }

        public void refresh() {
            setEnabled(getGrammarView() != null
                && getGrammarStore().isModifiable()
                && !getGrammarView().getLabelStore().getLabels().isEmpty());
        }

        public void actionPerformed(ActionEvent e) {
            Duo<TypeLabel> relabelling = askRelabelling(this.oldLabel);
            if (relabelling != null) {
                doRelabel(relabelling.one(), relabelling.two());
            }
        }

        /** Sets {@link #oldLabel} based on the {@link GraphJGraph} selection. */
        @Override
        public void valueChanged(GraphSelectionEvent e) {
            this.oldLabel = null;
            Object[] selection =
                ((GraphJGraph) e.getSource()).getSelectionCells();
            if (selection != null && selection.length > 0) {
                Collection<? extends Label> selectedLabels =
                    ((GraphJCell) selection[0]).getListLabels();
                if (selectedLabels.size() > 0) {
                    Label selectedLabel = selectedLabels.iterator().next();
                    if (selectedLabel instanceof TypeLabel) {
                        this.oldLabel = (TypeLabel) selectedLabel;
                    }
                }
            }
        }

        @Override
        public void valueChanged(TreeSelectionEvent e) {
            this.oldLabel = null;
            TreePath[] selection =
                ((LabelTree) e.getSource()).getSelectionPaths();
            if (selection != null && selection.length > 0) {
                Label selectedLabel =
                    ((LabelTree.LabelTreeNode) selection[0].getLastPathComponent()).getLabel();
                if (selectedLabel instanceof TypeLabel) {
                    this.oldLabel = (TypeLabel) selectedLabel;
                }
            }
        }

        /** The label to be replaced; may be {@code null}. */
        private TypeLabel oldLabel;
    }

    /**
     * Returns the rule renaming action permanently associated with this
     * simulator.
     */
    public RenameGraphAction getRenameGraphAction() {
        // lazily create the action
        if (this.renameGraphAction == null) {
            this.renameGraphAction = new RenameGraphAction();
        }
        return this.renameGraphAction;
    }

    /**
     * The graph renaming action permanently associated with this simulator.
     */
    private RenameGraphAction renameGraphAction;

    private class RenameGraphAction extends RefreshableAction {
        RenameGraphAction() {
            super(Options.RENAME_GRAPH_ACTION_NAME, Icons.RENAME_ICON);
            putValue(ACCELERATOR_KEY, Options.RENAME_KEY);
        }

        public void refresh() {
            setEnabled(getGrammarView() != null
                && getGrammarStore().isModifiable()
                && !getStateList().getSelectedGraphs().isEmpty());

            if (getGraphPanel() == getStatePanel()) {
                getRenameMenuItem().setAction(this);
            }
        }

        public void actionPerformed(ActionEvent e) {
            // Multiple selection
            // copy selected graph names
            List<String> selectedGraphs = getStateList().getSelectedGraphs();
            for (String oldGraphName : selectedGraphs) {
                if (oldGraphName != null) {
                    GraphView graph =
                        getGrammarView().getGraphView(oldGraphName);
                    assert graph != null : String.format(
                        "Graph '%s' in graph list but not in grammar",
                        oldGraphName);
                    String newGraphName =
                        askNewGraphName("Select new graph name", oldGraphName,
                            false);
                    if (newGraphName != null
                        && !oldGraphName.equals(newGraphName)) {
                        doRenameGraph(graph.getAspectGraph(), newGraphName);
                    }
                }
            }
        }
    }

    /**
     * Returns the rule renaming action permanently associated with this
     * simulator.
     */
    public RenameRuleAction getRenameRuleAction() {
        // lazily create the action
        if (this.renameRuleAction == null) {
            this.renameRuleAction = new RenameRuleAction();
        }
        return this.renameRuleAction;
    }

    /**
     * The rule renaming action permanently associated with this simulator.
     */
    private RenameRuleAction renameRuleAction;

    private class RenameRuleAction extends RefreshableAction {
        RenameRuleAction() {
            super(Options.RENAME_RULE_ACTION_NAME, Icons.RENAME_ICON);
            putValue(ACCELERATOR_KEY, Options.RENAME_KEY);
        }

        /** This action is disabled if there is more than one selected rule. */
        @Override
        public boolean isEnabled() {
            if (getCurrentRuleSet().size() != 1) {
                return false;
            } else {
                return super.isEnabled();
            }
        }

        public void refresh() {
            setEnabled(getCurrentRule() != null);
            if (getGraphPanel() == getRulePanel()) {
                getRenameMenuItem().setAction(this);
            }
        }

        public void actionPerformed(ActionEvent e) {
            if (confirmAbandon(true)) {
                // Multiple selection
                RuleName newRuleName = null;
                // copy the selected rules to avoid concurrent modifications
                List<RuleView> rules =
                    new ArrayList<RuleView>(getCurrentRuleSet());
                for (RuleView rule : rules) {
                    RuleName oldRuleName = rule.getRuleName();
                    AspectGraph ruleGraph = rule.getAspectGraph();
                    newRuleName =
                        askNewRuleName("Select new rule name",
                            oldRuleName.toString(), true);
                    if (newRuleName != null) {
                        doRenameRule(ruleGraph, newRuleName.toString());
                    }
                }
                if (newRuleName != null) {
                    setRule(newRuleName);
                }
            }
        }
    }

    /**
     * Returns the graph save action permanently associated with this simulator.
     */
    public SaveGrammarAction getSaveGrammarAction() {
        // lazily create the action
        if (this.saveGrammarAction == null) {
            this.saveGrammarAction = new SaveGrammarAction();
        }
        return this.saveGrammarAction;
    }

    /**
     * The grammar save action permanently associated with this simulator.
     */
    private SaveGrammarAction saveGrammarAction;

    /**
     * Action for saving a rule system.
     */
    private class SaveGrammarAction extends RefreshableAction {
        /** Constructs an instance of the action. */
        SaveGrammarAction() {
            super(Options.SAVE_GRAMMAR_ACTION_NAME, null);
            putValue(ACCELERATOR_KEY, Options.SAVE_GRAMMAR_AS_KEY);
        }

        public void actionPerformed(ActionEvent evt) {
            int result = getGrammarFileChooser().showSaveDialog(getFrame());
            // now save, if so required
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = getGrammarFileChooser().getSelectedFile();
                if (confirmOverwriteGrammar(selectedFile)) {
                    doSaveGrammar(selectedFile, true);
                }
            }
        }

        public void refresh() {
            setEnabled(getGrammarView() != null);
        }
    }

    /**
     * Returns the graph save action permanently associated with this simulator.
     */
    public SaveGraphAction getSaveGraphAction() {
        // lazily create the action
        if (this.saveGraphAction == null) {
            this.saveGraphAction = new SaveGraphAction();
        }
        return this.saveGraphAction;
    }

    /**
     * The state save action permanently associated with this simulator.
     */
    private SaveGraphAction saveGraphAction;

    /**
     * Action to save the state or LTS as a graph.
     */
    private class SaveGraphAction extends RefreshableAction {
        /** Constructs an instance of the action. */
        SaveGraphAction() {
            super(Options.SAVE_ACTION_NAME, Icons.SAVE_ICON);
            putValue(ACCELERATOR_KEY, Options.SAVE_KEY);
        }

        public void actionPerformed(ActionEvent e) {
            GraphJModel<?,?> jModel = getGraphPanel().getJModel();
            if (getGraphPanel() == getLtsPanel()) {
                actionForLTS(((LTSJModel) jModel).getGraph());
            } else {
                assert getGraphPanel() == getStatePanel();
                actionForState(((AspectJModel) jModel).getGraph());
            }
        }

        private void actionForState(AspectGraph graph) {
            ExtensionFilter filter = STATE_FILTER;
            String name = graph.getName();
            getStateFileChooser().setFileFilter(filter);
            getStateFileChooser().setSelectedFile(new File(name));
            File selectedFile =
                SaveDialog.show(getStateFileChooser(), getFrame(), null);
            // now save, if so required
            if (selectedFile != null) {
                name = filter.stripExtension(selectedFile.getName());
                graph = graph.rename(name);
                if (isFileInStore(selectedFile, getGrammarStore())) {
                    doAddGraph(graph);
                } else {
                    doSaveState(graph, selectedFile);
                }
            }
        }

        /** Saves a given graph to a given file. */
        private void doSaveState(AspectGraph graph, File selectedFile) {
            try {
                Simulator.this.aspectLoader.marshalGraph(graph, selectedFile);
            } catch (IOException exc) {
                showErrorDialog(String.format(
                    "Error while saving graph to '%s'", selectedFile), exc);
            }
        }

        private void actionForLTS(GTS gts) {
            ExtensionFilter filter = GXL_FILTER;
            getStateFileChooser().setFileFilter(filter);
            getStateFileChooser().setSelectedFile(new File(LTS_FILE_NAME));
            File selectedFile =
                SaveDialog.show(getStateFileChooser(), getFrame(), null);
            // now save, if so required
            if (selectedFile != null) {
                String name = filter.stripExtension(selectedFile.getName());
                gts.setName(name);
                doSaveGTS(gts, selectedFile);
            }
        }

        /** Saves a given graph to a given file. */
        private void doSaveGTS(GTS gts, File selectedFile) {
            try {
                Simulator.this.graphLoader.marshalAnyGraph(gts, selectedFile);
            } catch (IOException exc) {
                showErrorDialog(String.format("Error while saving LTS to '%s'",
                    selectedFile), exc);
            }
        }

        /**
         * Tests if the action should be enabled according to the current state
         * of the simulator, and also modifies the action name.
         * 
         */
        public void refresh() {
            if (getGraphPanel() == getLtsPanel()) {
                setEnabled(getGTS() != null);
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

    /**
     * Returns the undo action permanently associated with this simulator.
     */
    public Action getSelectColorAction() {
        if (this.selectColorAction == null) {
            this.selectColorAction = new SelectColorAction();
        }
        return this.selectColorAction;
    }

    /**
     * The undo action permanently associated with this simulator.
     */
    private SelectColorAction selectColorAction;

    /**
     * Action for undoing an edit to the grammar.
     */
    private class SelectColorAction extends AbstractAction implements
            GraphSelectionListener, TreeSelectionListener {
        /** Constructs an instance of the action. */
        SelectColorAction() {
            super(Options.SELECT_COLOR_ACTION_NAME);
            putValue(SHORT_DESCRIPTION, Options.SELECT_COLOR_ACTION_NAME);
            addAsListener(getStatePanel());
            addAsListener(getRulePanel());
            addAsListener(getTypePanel());
            refresh();
            this.chooser = new JColorChooser();
        }

        /** Adds this action as a listener to the JGraph and label tree of a 
         * given JGraphPanel.
         */
        private void addAsListener(JGraphPanel<?> jPanel) {
            jPanel.getJGraph().addGraphSelectionListener(this);
            if (this.label == null) {
                checkJGraph(jPanel.getJGraph());
            }
            jPanel.getLabelTree().addTreeSelectionListener(this);
            if (this.label == null) {
                checkLabelTree(jPanel.getLabelTree());
            }
        }

        public void actionPerformed(ActionEvent evt) {
            Color initColour =
                getGrammarView().getLabelStore().getColor(this.label);
            if (initColour != null) {
                this.chooser.setColor(initColour);
            }
            JDialog dialog =
                JColorChooser.createDialog(getSimulatorPanel(),
                    "Choose colour for type", false, this.chooser,
                    new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            setColour(SelectColorAction.this.chooser.getColor());
                        }
                    }, null);
            dialog.setVisible(true);
        }

        private void setColour(Color newColour) {
            Aspect colourAspect = null;
            if (!newColour.equals(Color.black)) {
                String colourString =
                    String.format("%s,%s,%s", newColour.getRed(),
                        newColour.getGreen(), newColour.getBlue());
                try {
                    colourAspect =
                        AspectKind.COLOR.getAspect().newInstance(colourString);
                } catch (FormatException e) {
                    // this can't happen, as the colour string is constructed correctly
                    assert false;
                }
            }
            for (String typeViewName : getGrammarView().getActiveTypeNames()) {
                AspectGraph typeView =
                    getGrammarView().getTypeView(typeViewName).getAspectGraph();
                AspectGraph newTypeView =
                    typeView.colour(this.label, colourAspect);
                if (newTypeView != typeView) {
                    doAddType(newTypeView);
                }
            }
        }

        /** Sets {@link #label} based on the {@link GraphJGraph} selection. */
        @Override
        public void valueChanged(GraphSelectionEvent e) {
            checkJGraph((GraphJGraph) e.getSource());
        }

        /** Checks if in a given JGraph a type label is selected. */
        private void checkJGraph(GraphJGraph jGraph) {
            this.label = null;
            Object[] selection = jGraph.getSelectionCells();
            if (selection != null) {
                choose: for (Object cell : selection) {
                    for (Label label : ((GraphJCell) cell).getListLabels()) {
                        if (label instanceof TypeLabel && label.isNodeType()) {
                            this.label = (TypeLabel) label;
                            break choose;
                        }
                    }
                }
            }
            refresh();
        }

        @Override
        public void valueChanged(TreeSelectionEvent e) {
            checkLabelTree((LabelTree) e.getSource());
        }

        /** Checks if in a given {@link LabelTree} a type label is selected. */
        private void checkLabelTree(LabelTree tree) {
            this.label = null;
            TreePath[] selection = tree.getSelectionPaths();
            if (selection != null) {
                for (TreePath path : selection) {
                    Label label =
                        ((LabelTree.LabelTreeNode) path.getLastPathComponent()).getLabel();
                    if (label instanceof TypeLabel && label.isNodeType()) {
                        this.label = (TypeLabel) label;
                        break;
                    }
                }
            }
            refresh();
        }

        private void refresh() {
            super.setEnabled(this.label != null
                && !getGrammarView().getActiveTypeNames().isEmpty());
        }

        /** The label for which a colour is chosen; may be {@code null}. */
        private TypeLabel label;

        private final JColorChooser chooser;
    }

    /**
     * Lazily creates and returns an instance of SetStartGraphAction.
     */
    public Action getSetStartGraphAction() {
        // lazily create the action
        if (this.setStartGraphAction == null) {
            this.setStartGraphAction = new SetStartGraphAction();
        }
        return this.setStartGraphAction;
    }

    /** Singleton instance of {@link SetStartGraphAction}. */
    private SetStartGraphAction setStartGraphAction;

    /** Action to set a new start graph. */
    private class SetStartGraphAction extends RefreshableAction {
        /** Constructs an instance of the action. */
        SetStartGraphAction() {
            super(Options.START_GRAPH_ACTION_NAME, Icons.START_ICON);
        }

        public void actionPerformed(ActionEvent e) {
            String selection = (String) getStateList().getSelectedValue();
            doLoadStartGraph(selection);
        }

        public void refresh() {
            setEnabled(getStateList().getSelectedGraphs().size() == 1);
        }
    }

    /**
     * Lazily creates and returns an instance of
     * {@link Simulator.StartSimulationAction}.
     */
    public Action getStartSimulationAction() {
        // lazily create the action
        if (this.startSimulationAction == null) {
            this.startSimulationAction = new StartSimulationAction();
        }
        return this.startSimulationAction;
    }

    /** The action to start a new simulation. */
    private StartSimulationAction startSimulationAction;

    private class StartSimulationAction extends RefreshableAction {
        /** Constructs an instance of the action. */
        StartSimulationAction() {
            super(Options.START_SIMULATION_ACTION_NAME, Icons.NEW_LTS_ICON);
            putValue(Action.ACCELERATOR_KEY, Options.START_SIMULATION_KEY);
        }

        public void actionPerformed(ActionEvent e) {
            if (confirmAbandon(false)) {
                startSimulation();
            }
        }

        public void refresh() {
            boolean enabled =
                getGrammarView() != null
                    && getGrammarView().getErrors().isEmpty();
            setEnabled(enabled);
        }
    }

    /**
     * Lazily creates and returns an instance of
     * {@link Simulator.ToggleExplorationStateAction}.
     */
    public Action getToggleExplorationStateAction() {
        if (this.toggleExplorationStateAction == null) {
            this.toggleExplorationStateAction =
                new ToggleExplorationStateAction();
        }
        return this.toggleExplorationStateAction;
    }

    /** The action to toggle between concrete and abstract exploration. */
    private ToggleExplorationStateAction toggleExplorationStateAction;

    private class ToggleExplorationStateAction extends RefreshableAction {
        /** Constructs an instance of the action. */
        ToggleExplorationStateAction() {
            super(Options.TOGGLE_TO_ABS_ACTION_NAME, null);
            putValue(Action.ACCELERATOR_KEY, Options.TOGGLE_EXP_MODE_KEY);
        }

        public void actionPerformed(ActionEvent e) {
            Simulator simulator = Simulator.this;
            if (simulator.isAbstractionMode()) {
                simulator.setAbstractionMode(false);
                this.putValue(Action.NAME, Options.TOGGLE_TO_ABS_ACTION_NAME);
                simulator.startSimulation();
            } else {
                simulator.setAbstractionMode(true);
                this.putValue(Action.NAME, Options.TOGGLE_TO_CONC_ACTION_NAME);
                Multiplicity.initMultStore();
                simulator.removeSimulationListener(simulator.getRuleTree());
                simulator.removeSimulationListener(simulator.getStatePanel());
                simulator.ruleJTree = null;
                simulator.ruleTreePanel.setViewportView(simulator.getRuleTree());
                simulator.statePanel = null;
                simulator.getSimulatorPanel().setComponentAt(0,
                    simulator.getStatePanel());
                simulator.startSimulation();
            }
        }

        public void refresh() {
            boolean enabled =
                getGrammarView() != null
                    && getGrammarView().getErrors().isEmpty();
            setEnabled(enabled);
        }
    }

    /**
     * Returns the undo action permanently associated with this simulator.
     */
    public UndoAction getUndoAction() {
        if (this.undoAction == null) {
            this.undoAction = new UndoAction();
        }
        return this.undoAction;
    }

    /**
     * The undo action permanently associated with this simulator.
     */
    private UndoAction undoAction;

    /**
     * Action for undoing an edit to the grammar.
     */
    private class UndoAction extends AbstractAction {
        /** Constructs an instance of the action. */
        UndoAction() {
            super(Options.UNDO_ACTION_NAME, Icons.UNDO_ICON);
            putValue(SHORT_DESCRIPTION, Options.UNDO_ACTION_NAME);
            putValue(ACCELERATOR_KEY, Options.UNDO_KEY);
            setEnabled(false);
        }

        public void actionPerformed(ActionEvent evt) {
            getUndoManager().undo();
            updateGrammar();
        }

        public void refresh() {
            if (getUndoManager().canUndo()) {
                setEnabled(true);
                putValue(Action.NAME,
                    getUndoManager().getUndoPresentationName());
            } else {
                setEnabled(false);
                putValue(Action.NAME, Options.UNDO_ACTION_NAME);
            }
        }
    }

    /**
     * Returns the CTL formula providing action permanently associated with this
     * simulator.
     * @param full if {@code true}, the action first generates the full state
     * space.
     */
    public Action getCheckCTLAction(boolean full) {
        CheckCTLAction result =
            full ? this.checkCTLFreshAction : this.checkCTLAsIsAction;
        if (result == null) {
            result = new CheckCTLAction(full);
            if (full) {
                this.checkCTLFreshAction = result;
            } else {
                this.checkCTLAsIsAction = result;
            }
        }
        return result;
    }

    /**
     * Action to check a CTL property on a fully explored state space.
     */
    private CheckCTLAction checkCTLFreshAction;

    /**
     * Action to check a CTL property on the current state space.
     */
    private CheckCTLAction checkCTLAsIsAction;

    /**
     * Action for verifying a CTL formula.
     */
    private class CheckCTLAction extends RefreshableAction {
        /** Constructs an instance of the action. */
        CheckCTLAction(boolean full) {
            super(full ? Options.CHECK_CTL_FULL_ACTION_NAME
                    : Options.CHECK_CTL_AS_IS_ACTION_NAME, null);
            this.full = full;
        }

        public void actionPerformed(ActionEvent evt) {
            String property =
                getFormulaDialog().showDialog(getFrame(),
                    new StringDialog.StringParser() {
                        @Override
                        public String parse(String text) {
                            String result = null;
                            try {
                                TemporalFormula formula =
                                    CTLFormula.parseFormula(text);
                                String invalidAtom =
                                    TemporalFormula.validAtoms(formula,
                                        getGrammarView().getRuleNames());
                                if (invalidAtom != null) {
                                    result =
                                        String.format("Invalid proposition %s",
                                            invalidAtom);
                                }
                            } catch (FormatException efe) {
                                result = efe.getMessage();
                            }
                            return result;
                        }
                    });
            if (property != null) {
                boolean doCheck = true;
                if (getGTS().hasOpenStates() && this.full) {
                    startSimulation();
                    doRunExploration(getDefaultExploration(), false);
                    doCheck = !getGTS().hasOpenStates();
                }
                if (doCheck) {
                    doCheckProperty(property);
                }
            }
        }

        private void doCheckProperty(String property) {
            TemporalFormula formula;
            try {
                formula = CTLFormula.parseFormula(property);
            } catch (FormatException e) {
                // since the property passed the parser, we can't land here
                assert false;
                formula = null;
            }
            CTLModelChecker modelChecker =
                new CTLModelChecker(getGTS(), formula);
            modelChecker.verify();
            List<GraphState> counterExamples =
                new ArrayList<GraphState>(formula.getCounterExamples());
            boolean reportForAllStates =
                confirmBehaviour(
                    VERIFY_ALL_STATES_OPTION,
                    "Verify all states? Choosing 'No' will verify formula only on start state of LTS.");
            if (!reportForAllStates) {
                GraphState initial = getGTS().startState();
                boolean initialIsCounterexample =
                    counterExamples.contains(initial);
                counterExamples = new ArrayList<GraphState>(1);
                if (initialIsCounterexample) {
                    counterExamples.add(initial);
                }
            }
            getLtsPanel().emphasiseStates(counterExamples, false);
            String message;
            if (counterExamples.isEmpty()) {
                message =
                    String.format("The property '%s' holds for %s of this LTS",
                        property, reportForAllStates ? "all states"
                                : "the initial state");
            } else if (reportForAllStates) {
                message =
                    String.format(
                        "The property '%s' fails to hold in the %d highlighted states",
                        property, counterExamples.size());
            } else {
                message =
                    String.format(
                        "The property '%s' fails to hold in the initial state",
                        property);
            }
            JOptionPane.showMessageDialog(getFrame(), message);
        }

        public void refresh() {
            setEnabled(getGTS() != null);
        }

        private final boolean full;
    }

    /** Class wrapping a menu of recently opened files. */
    private class History {

        /** Constructs a fresh history instance. */
        public History() {
            String[] savedLocations =
                Options.userPrefs.get(SystemProperties.HISTORY_KEY, "").split(
                    ",");
            for (String location : savedLocations) {
                try {
                    this.history.add(new LoadAction(location, null));
                } catch (IOException exc) {
                    // if we can't load from a location, just
                    // omit it from the history
                }
            }

            this.menu.setText(Options.OPEN_RECENT_MENU_NAME);
            this.menu.setMnemonic(Options.OPEN_RECENT_MENU_MNEMONIC);

            synchMenu();
        }

        /**
         * Returns a JMenu that will reflect the current history. The menu is
         * updated when a grammar is loaded.
         */
        public JMenu getOpenRecentMenu() {
            return this.menu;
        }

        /**
         * This method is called when a grammar is loaded, to update the history
         * of loaded grammars. This class will deal with any updates that have
         * to be made accordingly
         */
        public void updateLoadGrammar() {
            try {
                Object location = getGrammarStore().getLocation();
                String startGraphName = getGrammarView().getStartGraphName();
                LoadAction newAction =
                    new LoadAction(location.toString(), startGraphName);
                this.history.remove(newAction);
                this.history.add(0, newAction);
                // trimming list to 10 elements
                while (this.history.size() > 10) {
                    this.history.remove(10);
                }
                synch();
            } catch (IOException exc) {
                // if we can't load from a location, just
                // omit it from the history
            }
        }

        private void synch() {
            synchPrefs();
            synchMenu();
        }

        private void synchPrefs() {
            String newStr = makeHistoryString();
            Options.userPrefs.put(SystemProperties.HISTORY_KEY, newStr);
        }

        private void synchMenu() {
            this.menu.removeAll();
            for (LoadAction action : this.history) {
                this.menu.add(action);
            }
        }

        private String makeHistoryString() {
            StringBuilder result = new StringBuilder();
            for (LoadAction action : this.history) {
                if (result.length() > 0) {
                    result.append(",");
                }
                result.append(action.getLocation());
            }
            return result.toString();
        }

        /** Menu of history items. */
        private final JMenu menu = new JMenu();
        /** List of load actions corresponding to the history items. */
        private final ArrayList<LoadAction> history =
            new ArrayList<LoadAction>();

        private class LoadAction extends AbstractAction {
            /**
             * Constructs an action that will load a grammar from a predefined
             * location.
             * @param location the location to load from; non-null
             * @param startGraphName name of the start graph to be loaded; if
             *        <code>null</code>, the default will be used.
             * 
             */
            LoadAction(String location, String startGraphName)
                throws IOException {
                this.location = location;
                this.startGraphName = startGraphName;
                this.store = SystemStoreFactory.newStore(location);
                putValue(NAME, this.store.toString());
            }

            public void actionPerformed(ActionEvent evt) {
                try {
                    doLoadGrammar(this.store, this.startGraphName);
                } catch (Exception e) {
                    showErrorDialog("Can't load grammar: ", e);
                }
            }

            /** Returns the location that this action was initialised with. */
            public String getLocation() {
                return this.location;
            }

            /**
             * Two actions are considered equal if they load from the same
             * location.
             * @see #getLocation()
             */
            @Override
            public boolean equals(Object obj) {
                return (obj instanceof LoadAction)
                    && ((LoadAction) obj).getLocation().equals(getLocation());
            }

            /**
             * Returns the hash code of this action's location.
             * @see #getLocation()
             */
            @Override
            public int hashCode() {
                return getLocation().hashCode();
            }

            @Override
            public String toString() {
                return this.store.toString();
            }

            /** Location that this action loads from (non-null). */
            private final String location;
            /** Start graph name that should be loaded with the grammar. */
            private final String startGraphName;
            /** System store associated with this action (non-null). */
            private final SystemStore store;
        }
    }

    /**
     * Starts a simulator, optionally setting the graph production system and
     * start state.
     */
    public static void main(String[] args) {
        Simulator simulator;
        try {
            if (args.length == 0) {
                simulator = new Simulator();
            } else if (args.length == 1) {
                simulator = new Simulator(args[0]);
            } else if (args.length == 2) {
                simulator = new Simulator(args[0], args[1]);
            } else {
                throw new IOException(
                    "Usage: Simulator [<production-system> [<start-state>]]");
            }
            // simulator.loadModules();
            simulator.start();
        } catch (IOException exc) {
            exc.printStackTrace();
            System.out.println(exc.getMessage());
            // System.exit(0);
        }
    }

    // --------------------- INSTANCE DEFINITIONS -----------------------------

    /**
     * Name of the LTS file, when it is saved or exported.
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
     * Minimum height of the rule tree component.
     */
    static private final int RULE_TREE_MINIMUM_HEIGHT = 200;

    /**
     * Minimum height of the rule tree component.
     */
    static private final int START_LIST_MINIMUM_HEIGHT = 130;

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
    static private final Dimension GRAPH_VIEW_PREFERRED_SIZE = new Dimension(
        GRAPH_VIEW_PREFERRED_WIDTH, GRAPH_VIEW_PREFERRED_HEIGHT);

    /** Detect if we are on MacOS (used for hooking doQuit() properly.  */
    private static boolean MAC_OS_X =
        System.getProperty("os.name").toLowerCase().startsWith("mac os x");

}
