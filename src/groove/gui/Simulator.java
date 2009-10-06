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
import static groove.gui.Options.VERIFY_ALL_STATES_OPTION;
import groove.abs.AbstrSimulationProperties;
import groove.abs.Abstraction;
import groove.abs.lts.AGTS;
import groove.abs.lts.AbstrStateGenerator;
import groove.control.ControlView;
import groove.explore.Scenario;
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
import groove.gui.chscenar.ScenarioSelectionDialog;
import groove.gui.dialog.ErrorDialog;
import groove.gui.dialog.ExplorationDialog;
import groove.gui.dialog.ExportDialog;
import groove.gui.dialog.FormulaDialog;
import groove.gui.dialog.FreshNameDialog;
import groove.gui.dialog.ProgressBarDialog;
import groove.gui.dialog.PropertiesDialog;
import groove.gui.dialog.ReplaceLabelDialog;
import groove.gui.jgraph.GraphJModel;
import groove.gui.jgraph.JGraph;
import groove.gui.jgraph.LTSJGraph;
import groove.io.AspectGxl;
import groove.io.Aut;
import groove.io.ExtensionFilter;
import groove.io.GrooveFileChooser;
import groove.io.LayedOutXml;
import groove.io.SystemStore;
import groove.io.SystemStoreFactory;
import groove.io.Xml;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.LTSGraph;
import groove.lts.State;
import groove.lts.StateGenerator;
import groove.trans.RuleEvent;
import groove.trans.RuleMatch;
import groove.trans.RuleName;
import groove.trans.SystemProperties;
import groove.util.Groove;
import groove.util.GrooveModules;
import groove.util.Pair;
import groove.verify.CTLFormula;
import groove.verify.CTLModelChecker;
import groove.verify.TemporalFormula;
import groove.view.AspectualGraphView;
import groove.view.AspectualRuleView;
import groove.view.FormatException;
import groove.view.StoredGrammarView;
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
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

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
import javax.swing.JPanel;
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
 * @version $Revision$
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
                new File(Groove.createRuleSystemFilter().addExtension(
                    grammarLocation)).getAbsoluteFile();

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
        groove.gui.UserSettings.applyUserSettings(this.frame); // Applies
        // previous user settings (mzimakova)
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
        if (System.getProperty("os.name").startsWith("Windows")
            || System.getProperty("os.name").startsWith("Linux")
            || System.getProperty("os.name").startsWith("FreeBSD")) {
            // TODO change here to enable ltl model checking
            System.setProperty(GrooveModules.GROOVE_MODULE_LTL_VERIFICATION,
                GrooveModules.GROOVE_MODULE_ENABLED);
        } else {
            System.setProperty(GrooveModules.GROOVE_MODULE_LTL_VERIFICATION,
                GrooveModules.GROOVE_MODULE_DISABLED);
        }
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

    /**
     * Sets the {@link #grammarView} and {@link #currentRule} fields.
     * @return <code>true</code> if the new grammar is different from the
     *         previous
     */
    private boolean setGrammarView(StoredGrammarView grammar) {
        boolean result = this.grammarView != grammar;
        this.grammarView = grammar;
        if (this.currentRule != null
            && grammar.getRuleView(this.currentRule.getRuleName()) == null) {
            this.currentRule = null;
        }
        return result;
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
        if (gts != null) {
            getGenerator().setGTS(gts);
        }
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
     * Returns the currently selected rule, or <tt>null</tt> if none is
     * selected. The selected rule is the one displayed in the rule panel.
     */
    public AspectualRuleView getCurrentRule() {
        return this.currentRule;
    }

    /**
     * mzimakova Returns the currently selected rule set, or <tt>null</tt> if
     * none is selected.
     */
    public ArrayList<AspectualRuleView> getCurrentRuleSet() {
        return this.currentRuleSet;
    }

    /**
     * Sets the currently selected rule to a given value (possibly
     * <code>null</code>).
     * @return <code>true</code> if the new rule is different from the previous
     */
    private boolean setCurrentRule(AspectualRuleView rule) {
        boolean result = this.currentRule != rule;
        this.currentRule = rule;
        this.currentRuleSet.clear();
        this.currentRuleSet.add(rule);
        return result;
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

    private ChooseCustomScenarioAction getChooseCustomScenarioAction() {
        if (this.chooseScenarioAction == null) {
            this.chooseScenarioAction = new ChooseCustomScenarioAction();
        }
        return this.chooseScenarioAction;
    }

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
     * Lazily creates and returns the graph edit action permanently associated
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
     * Lazily creates and returns the state edit action permanently associated
     * with this simulator.
     */
    public EditStateAction getEditStateAction() {
        // lazily create the action
        if (this.editStateAction == null) {
            this.editStateAction = new EditStateAction();
        }
        return this.editStateAction;
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

    /** Returns the action to show the system properties of the current grammar. */
    public Action getEditSystemPropertiesAction() {
        // lazily create the action
        if (this.editSystemPropertiesAction == null) {
            this.editSystemPropertiesAction = new EditSystemPropertiesAction();
        }
        return this.editSystemPropertiesAction;
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
     * Returns the graph export action permanently associated with this
     * simulator.
     */
    public ExportGraphAction getExportGraphAction() {
        // lazily create the action
        if (this.exportGraphAction == null) {
            this.exportGraphAction = new ExportGraphAction();
        }
        return this.exportGraphAction;
    }

    /**
     * Returns the LTS export action permanently associated with this simulator.
     */
    public ExportAction getExportAction() {
        // lazily create the action
        if (this.exportAction == null) {
            this.exportAction = new ExportAction();
        }
        return this.exportAction;
    }

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

    /** Creates an action associated to a scenario handler. */
    public LaunchScenarioAction getLaunchScenarioAction(Scenario scenario) {
        // no reuse: the action depends on the scenario
        return new LaunchScenarioAction(scenario);
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

    //
    // /**
    // * Returns the start graph load action permanently associated with this
    // * simulator.
    // */
    // public LoadControlFileAction getLoadControlFileAction() {
    // // lazily create the action
    // if (this.loadControlFileAction == null) {
    // this.loadControlFileAction = new LoadControlFileAction();
    // }
    // return this.loadControlFileAction;
    // }

    /**
     * mzimakova Returns the rule load action permanently associated with this
     * simulator.
     */
    public LoadRuleAction getLoadRuleAction() {
        // lazily create the action
        if (this.loadRuleAction == null) {
            this.loadRuleAction = new LoadRuleAction();
        }
        return this.loadRuleAction;
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

    //
    // /**
    // * Returns the rule creation action permanently associated with this
    // * simulator.
    // */
    // public NewControlAction getNewControlAction() {
    // // lazily create the action
    // if (this.newControlAction == null) {
    // this.newControlAction = new NewControlAction();
    // }
    // return this.newControlAction;
    // }

    /** Returns the quit action permanently associated with this simulator. */
    public Action getQuitAction() {
        // lazily create the action
        if (this.quitAction == null) {
            this.quitAction = new QuitAction();
        }
        return this.quitAction;
    }

    /**
     * Returns the ctl formula providing action permanently associated with this
     * simulator.
     */
    public Action getProvideTemporalFormulaAction() {
        if (this.verifyAction == null) {
            this.verifyAction = new VerifyCTLAction();
        }
        return this.verifyAction;
    }

    /** Returns the redo action permanently associated with this simulator. */
    public Action getRedoAction() {
        if (this.redoAction == null) {
            this.redoAction = getUndoHistory().getRedoAction();
            addAccelerator(this.redoAction);
        }
        return this.redoAction;
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

    /**
     * Returns the rule renaming action permanently associated with this
     * simulator.
     */
    public ReplaceLabelAction getRenameLabelAction() {
        // lazily create the action
        if (this.renameLabelAction == null) {
            this.renameLabelAction = new ReplaceLabelAction();
        }
        return this.renameLabelAction;
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

    /**
     * A variant of {@link #getStartSimulationAction()} for abstract simulation.
     */
    public Action getStartAbstrSimulationAction() {
        // lazily create the action
        if (this.startAbstrSimulationAction == null) {
            this.startAbstrSimulationAction = new StartAbstrSimulationAction();
        }
        return this.startAbstrSimulationAction;
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
     * Returns the graph save action permanently associated with this simulator.
     */
    public SaveGrammarAction getSaveGrammarAction() {
        // lazily create the action
        if (this.saveGrammarAction == null) {
            this.saveGrammarAction = new SaveGrammarAction();
        }
        return this.saveGrammarAction;
    }

    /** Returns the undo action permanently associated with this simulator. */
    public Action getUndoAction() {
        if (this.undoAction == null) {
            this.undoAction = getUndoHistory().getUndoAction();
            addAccelerator(this.undoAction);
        }
        return this.undoAction;
    }

    /** Returns (after lazily creating) the undo history for this simulator. */
    private UndoHistory getUndoHistory() {
        if (this.undoHistory == null) {
            this.undoHistory = new UndoHistory(this);
        }
        return this.undoHistory;
    }

    /**
     * Handles the execution of a {@link SaveGraphAction}. Calls
     * {@link #doAddGraph(Graph)} for the actual saving.
     * @param state <tt>true</tt> if it is a state that has to be saved
     *        (otherwise it is an LTS)
     * @param graph the graph to be saved
     * @return the file to which the graph has been saved; <tt>null</tt> if the
     *         graph has not been saved
     */
    File handleSaveGraph(boolean state, AspectGraph graph) {
        getStateFileChooser().setFileFilter(
            state ? this.stateFilter : this.gxlFilter);
        String name = state ? GraphInfo.getName(graph) : LTS_FILE_NAME;
        getStateFileChooser().setSelectedFile(new File(name));
        File selectedFile =
            ExtensionFilter.showSaveDialog(getStateFileChooser(), getFrame(),
                null);
        // now save, if so required
        if (selectedFile != null) {
            name = this.stateFilter.stripExtension(selectedFile.getName());
            GraphInfo.setName(graph, name);
            doAddGraph(graph, true);
        }
        return selectedFile;
    }

    /**
     * Does the actual saving of a control program in the current grammar. The
     * target file will be derived from the current grammar location and control
     * name.
     * @param program the (parsable) control program to be saved (non-null)
     * @return the target file used; <code>null</code> if saving failed due to
     *         some error
     */
    boolean handleSaveControl(String program) {
        String controlName = getGrammarView().getProperties().getControlName();
        if (controlName == null) {
            controlName = Groove.DEFAULT_CONTROL_NAME;
        }
        try {
            getGrammarStore().putControl(controlName, program);
            return true;
        } catch (UnsupportedOperationException exc) {
            assert false : "Grammar store cannot save control program";
            return false;
        } catch (IOException exc) {
            showErrorDialog(String.format(
                "Error while saving control program '%s'", controlName), exc);
            return false;
        }
    }

    /**
     * Saves a given graph to a given file.
     */
    void doAddGraph(Graph graph, boolean graphLast) {
        try {
            AspectGraph saveGraph = AspectGraph.newInstance(graph);
            if (saveGraph.hasErrors()) {
                showErrorDialog("Errors in graph", new FormatException(
                    saveGraph.getErrors()));
            } else {
                getGrammarStore().putGraph(saveGraph);
                if (graphLast) {
                   getStateList().refreshList(true);
                }
            }
        } catch (IOException exc) {
            showErrorDialog(String.format("Error while saving graph '%s'",
                GraphInfo.getName(graph)), exc);
        }
    }

    /**
     * Saves an aspect graph as a rule under a given name, and puts the rule
     * into the current grammar view.
     * @param ruleName the name of the new rule
     * @param ruleAsGraph the new rule, given as an aspect graph
     */
    void doAddRule(RuleName ruleName, AspectGraph ruleAsGraph, boolean ruleLast) {
        try {
            GraphInfo.setName(ruleAsGraph, ruleName.text());
            getGrammarStore().putRule(ruleAsGraph);
            setGrammar(getGrammarView());
            if (ruleLast) {
                setRule(ruleName);
            }
        } catch (IOException exc) {
            showErrorDialog("Error while saving rule", exc);
        } catch (UnsupportedOperationException u) {
            showErrorDialog("Current grammar is read-only", u);
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
            setGrammar(getGrammarView());
        } else {
            this.stateJList.refreshList(true);
        }
    }

    /**
     * Deletes a rule from the grammar and the file system, and resets the
     * grammar view.
     */
    void doDeleteRule(RuleName name) {
        AspectGraph rule = getGrammarStore().deleteRule(name);
        if (rule != null) {
            setGrammar(getGrammarView());
        }
    }

    /** Inverts the enabledness of the current rule, and stores the result. */
    void doEnableRule() {
        // Multiple selection - mzimakova
        for (int i = 0; i < getCurrentRuleSet().size(); i++) {
            // AspectGraph ruleGraph = getCurrentRule().getAspectGraph();
            AspectGraph ruleGraph = getCurrentRuleSet().get(i).getAspectGraph();
            GraphProperties properties =
                GraphInfo.getProperties(ruleGraph, true);
            properties.setEnabled(!properties.isEnabled());
            boolean last = (i == getCurrentRuleSet().size() - 1);
            doAddRule(getCurrentRuleSet().get(i).getRuleName(), ruleGraph, last);
        }
    }

    /**
     * Can be called from the ExplorationDialog.
     * @param scenario
     */
    public void doGenerate(Scenario scenario) {
        scenario.prepare(getGTS(), getCurrentState());
        GraphJModel ltsJModel = getLtsPanel().getJModel();
        synchronized (ltsJModel) {
            // unhook the lts' jmodel from the lts, for efficiency's sake
            getGTS().removeGraphListener(ltsJModel);
            // disable rule application for the time being
            boolean applyEnabled = getApplyTransitionAction().isEnabled();
            getApplyTransitionAction().setEnabled(false);
            // create a thread to do the work in the background
            Thread generateThread = new LaunchThread(scenario);
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
     * Loads in a grammar from a given file.
     */
    void doLoadGrammar(File grammarFile, String startGraphName) {
        try {
            final SystemStore store =
                SystemStoreFactory.newStore(grammarFile, false);
            doLoadGrammar(store, startGraphName);
            // now we know loading succeeded, we can set the current
            // names & files
            setLastGrammarFile(grammarFile);
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
     * Loads in a given system store.
     */
    void doLoadGrammar(final SystemStore store, final String startGraphName) {
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
                    store.reload();
                    final StoredGrammarView grammar = store.toGrammarView();
                    if (startGraphName != null) {
                        grammar.setStartGraph(startGraphName);
                    }
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            setGrammar(grammar);
                        }
                    });
                    if (store instanceof Observable) {
                        ((Observable) store).deleteObserver(loadListener);
                    }
                } catch (final IOException exc) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            showErrorDialog(exc.getMessage(), exc);
                        }
                    });
                } catch (final IllegalArgumentException exc) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            showErrorDialog(exc.getMessage(), exc);
                        }
                    });
                }
                dialog.deactivate();
                // updating history
                Simulator.this.history.updateLoadGrammar(store.getLocation());
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
            setGrammar(getGrammarView());
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
        setGrammar(getGrammarView());
    }

    /**
     * Creates an empty grammar and an empty directory, and sets it in the
     * simulator.
     * @param grammarFile the grammar file to be used
     */
    void doNewGrammar(File grammarFile) {
        try {
            StoredGrammarView grammar =
                StoredGrammarView.newInstance(grammarFile, true);
            // now we know loading succeeded, we can set the current names &
            // files
            setLastGrammarFile(grammarFile);
            getStateFileChooser().setCurrentDirectory(grammarFile);
            getStateFileChooser().setSelectedFile(new File(""));
            getGrammarFileChooser().setSelectedFile(grammarFile);
            setGrammar(grammar);
            this.history.updateLoadGrammar(grammarFile.toString());
        } catch (IllegalArgumentException exc) {
            showErrorDialog(String.format("Can't create grammar at '%s'",
                grammarFile), exc);
        } catch (IOException exc) {
            showErrorDialog(String.format(
                "Error while creating grammar at '%s'", grammarFile), exc);
        }
    }

    /**
     * Ends the program.
     */
    void doQuit() {
        groove.gui.UserSettings.synchSettings(this.frame); // Saves a current
        // user settings
        // (mzimakova)
        if (confirmAbandon(false)) {
            if (REPORT) {
                try {
                    BufferedReader systemIn =
                        new BufferedReader(new InputStreamReader(System.in));
                    System.out.print("Log file? ");
                    String filename = systemIn.readLine();
                    if (filename.length() != 0) {
                        groove.util.Reporter.report(new PrintWriter(
                            new FileWriter(filename + ".log", true), true));
                    }
                } catch (IOException exc) {
                    System.out.println(exc.getMessage());
                }
                groove.util.Reporter.report(new PrintWriter(System.out));
            }
            getFrame().dispose();
            // try to persist the user preferences
            try {
                Preferences.userRoot().flush();
            } catch (BackingStoreException e) {
                // do nothing if the backing store is inaccessible
            }
        }
    }

    /**
     * Refreshes the currently loaded grammar, if any. Does not ask for
     * confirmation. Has no effect if no grammar is currently loaded.
     */
    void doRefreshGrammar() {
        if (getGrammarStore() != null) {
            try {
                getGrammarStore().reload();
                setGrammar(getGrammarView());
            } catch (IOException exc) {
                showErrorDialog("Error while refreshing grammar from "
                    + getGrammarStore().getLocation(), exc);
            }
        }
    }

    /**
     * Renames one of the graphs in the graph list. If the graph was the start
     * graph, uses the renamed graph again as start graph.
     */
    void doRenameGraph(AspectGraph graph, String newName, boolean graphLast) {
        String oldName = GraphInfo.getName(graph);
        // test now if this is the start state, before it is deleted from the
        // grammar
        boolean isStartGraph =
            oldName.equals(getGrammarView().getStartGraphName());
        getGrammarStore().deleteGraph(oldName);
        GraphInfo.setName(graph, newName);
        doAddGraph(graph, graphLast);
        if (isStartGraph) {
            // reset the start graph to the renamed graph
            getGrammarView().setStartGraph(newName);
        } else {
            if (graphLast) {
               this.stateJList.refreshList(true);
            }
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
            ControlView.store(controlProgram, new FileOutputStream(file));
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
    void doSaveGrammar(File grammarFile) {
        try {
            SystemStore newStore = getGrammarStore().save(grammarFile);
            setGrammarView(newStore.toGrammarView());
            // now we know saving succeeded, we can set the current names &
            // files
            setLastGrammarFile(grammarFile);

            setTitle();
            getGrammarFileChooser().setSelectedFile(grammarFile);
            setGrammar(getGrammarView());
            this.history.updateLoadGrammar(grammarFile.toString());
        } catch (IOException exc) {
            showErrorDialog("Error while saving grammar to " + grammarFile, exc);
        }
    }

    void doSaveProperties(SystemProperties newProperties) {
        // check if we need to load a new control program
        String newControlName = newProperties.getControlName();
        StoredGrammarView grammar = this.getGrammarView();
        String oldControlName = grammar.getProperties().getControlName();
        boolean refresh =
            newControlName == null ? oldControlName != null
                    : !newControlName.equals(oldControlName);
        try {
            getGrammarStore().putProperties(newProperties);
            if (refresh) {
                doRefreshGrammar();
            } else {
                setGrammar(grammar);
            }
        } catch (IOException exc) {
            showErrorDialog("Error while saving edited properties", exc);
        }
    }

    /** Renames all instances of a given label by another. */
    void doRenameLabel(String original, String replacement) {
        // does nothing for now
    }

    /**
     * Directs the actual verification process.
     * @param property the property to be checked
     */
    void doVerifyProperty(String property) {
        try {
            TemporalFormula formula = CTLFormula.parseFormula(property);
            String invalidAtom =
                TemporalFormula.validAtoms(formula,
                    getGrammarView().getRuleNames());
            if (invalidAtom == null) {
                CTLModelChecker modelChecker =
                    new CTLModelChecker(getGTS(), formula);
                modelChecker.verify();
                Set<State> counterExamples = formula.getCounterExamples();
                boolean reportForAllStates =
                    confirmBehaviour(
                        VERIFY_ALL_STATES_OPTION,
                        "Verify all states? Choosing 'No' will verify formula only on start state of LTS.");
                String message =
                    getLtsPanel().emphasiseStates(counterExamples,
                        reportForAllStates);
                JOptionPane.showMessageDialog(getFrame(), message,
                    "Verification results", JOptionPane.INFORMATION_MESSAGE,
                    Groove.GROOVE_BLUE_ICON_32x32);
            } else {
                showErrorDialog("Invalid atomic proposition", new Exception("'"
                    + invalidAtom + "' is not a valid atomic proposition."));
            }
        } catch (FormatException efe) {
            showErrorDialog("Format error in temporal formula", efe);
            efe.printStackTrace();
        }
    }

    private AspectGraph unmarshalGraph(File file) throws IOException {
        return getGraphLoader(file).unmarshalGraph(file);
    }

    private Xml<AspectGraph> getGraphLoader(File file) {
        if (this.autFilter.accept(file)) {
            return this.autLoader;
        } else {
            return this.graphLoader;
        }
    }

    /**
     * Sets a new graph transition system. Invokes
     * {@link #fireSetGrammar(StoredGrammarView)} to notify all observers of the
     * change.
     * 
     * @param grammar the new graph transition system
     * @see #fireSetGrammar(StoredGrammarView)
     */
    public synchronized void setGrammar(StoredGrammarView grammar) {
        setGrammarView(grammar);
        setGTS(null);
        fireSetGrammar(grammar);
        refresh();
        List<String> grammarErrors = grammar.getErrors();
        boolean grammarCorrect = grammarErrors.isEmpty();
        getErrorPanel().setErrors(grammarErrors);
        if (grammarCorrect && confirmBehaviourOption(START_SIMULATION_OPTION)) {
            if (isAbstractSimulation()) {
                startAbstrSimulation();
            } else {
                startSimulation();
            }
        }
    }

    /**
     * Sets a new graph transition system. Invokes
     * {@link #fireStartSimulation(GTS)} to notify all observers of the change.
     * 
     * @see #fireSetGrammar(StoredGrammarView)
     */
    public synchronized void startSimulation() {
        try {
            setGTS(new GTS(getGrammarView().toGrammar()));
            // getGenerator().explore(getCurrentState());
            fireStartSimulation(getGTS());
            refresh();
        } catch (FormatException exc) {
            showErrorDialog("Error while starting simulation", exc);
        }
    }

    /**
     * A variant of the {@link #startSimulation()} method for starting an
     * abstract simulation.
     */
    public synchronized void startAbstrSimulation() {
        try {
            if (!groove.abs.Util.isAbstractionPossible(getGrammarView().toGrammar())) {
                JOptionPane.showMessageDialog(
                    getFrame(),
                    "Abstract simulation is not possible for grammars with composite rules.",
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            AbstrSimulationProperties properties =
                new AbstrSimulationProperties();
            PropertiesDialog dialog =
                new PropertiesDialog(properties,
                    AbstrSimulationProperties.DEFAULT_KEYS, true);
            dialog.showDialog(getFrame());
            properties.update(dialog.getEditedProperties());
            boolean symred = properties.getSymmetryReduction();
            Abstraction.LinkPrecision linkPrecision =
                properties.getLinksPrecision();
            Abstraction.Parameters options =
                new Abstraction.Parameters(symred, linkPrecision,
                    properties.getRadius(), properties.getPrecision(),
                    properties.getMaxIncidence());
            AGTS agts = new AGTS(getGrammarView().toGrammar(), options);
            setGTS(agts);
            fireStartSimulation(getGTS());
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
     * @param state the new state
     * @see #fireSetState(GraphState)
     */
    public synchronized void setState(GraphState state) {
        // if (setCurrentState(state)) {
        // getGenerator().explore(state);
        // }
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
     * mzimakova Sets the current production rule set.
     */
    public synchronized void setMultipleRule(RuleName name) {
        // setCurrentRule(getGrammarView().getRuleView(name));
        if (this.currentRuleSet.indexOf(getGrammarView().getRuleView(name)) < 0) {
            this.currentRuleSet.add(getGrammarView().getRuleView(name));
        }
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
            // } else {
            // assert match != null : "The match and the transition cannot be
            // both null.";
            // RuleNameLabel ruleName = match.getRule().getName();
            // setCurrentRule(getCurrentGrammar().getRule(ruleName));
            // setCurrentMatch(match);
        }
        // fireSetTransition(getCurrentTransition());
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
        // fireSetTransition(getCurrentTransition());
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
            ExploreCache cache =
                getGTS().getRecord().createCache(getCurrentState(), false,
                    false);
            Set<? extends GraphTransition> resultTransitions =
                getGenerator().applyMatch(getCurrentState(), getCurrentEvent(),
                    cache);
            if (!resultTransitions.isEmpty()) {
                // may be empty in the case of abstract transformation
                GraphTransition trans = resultTransitions.iterator().next();
                setCurrentState(trans.target());
                fireApplyTransition(trans);
            }
            refreshActions();
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
            this.frame.setIconImage(Groove.GROOVE_ICON_16x16.getImage());
            // frame.setSize(500,300);
            this.frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            this.frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    doQuit();
                }
            });
            // frame.setContentPane(splitPane);
            this.frame.setJMenuBar(createMenuBar());

            JSplitPane leftPanel =
                new JSplitPane(JSplitPane.VERTICAL_SPLIT, getRuleJTreePanel(),
                    getStartStatesListPanel());

            // set up the content pane of the frame as a split pane,
            // with the rule directory to the left and a desktop pane to the
            // right
            JSplitPane splitPane =
                new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel,
                    getGraphViewsPanel());

            Container contentPane = this.frame.getContentPane();
            contentPane.setLayout(new BorderLayout());
            contentPane.add(splitPane);
            contentPane.add(getErrorPanel(), BorderLayout.SOUTH);
        }
        return this.frame;
    }

    /**
     * Lazily creates and returns the panel with the state, rule and LTS views.
     */
    JTabbedPane getGraphViewsPanel() {
        if (this.graphViewsPanel == null) {
            this.graphViewsPanel = new JTabbedPane();
            this.graphViewsPanel.addTab(null, Groove.GRAPH_FRAME_ICON,
                getStatePanel(), "Current graph state");
            this.graphViewsPanel.addTab(null, Groove.RULE_FRAME_ICON,
                getRulePanel(), "Selected rule");
            this.graphViewsPanel.addTab(null, Groove.LTS_FRAME_ICON,
                getConditionalLTSPanel(), "Labelled transition system");
            this.graphViewsPanel.addTab(null, Groove.CTRL_FRAME_ICON,
                getControlPanel(), "Control specification");
            // graphViewsPanel.addTab(null, Groove.TYPE_FRAME_ICON,
            // getTypePanel(), "Type graph");
            // add this simulator as a listener so that the actions are updated
            // regularly
            this.graphViewsPanel.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent evt) {
                    JTabbedPane source = (JTabbedPane) evt.getSource();
                    // only refresh actions if the selected panel is not a
                    // control panel,
                    // since that is not a graphpane!
                    if (!(source.getSelectedComponent() instanceof CAPanel)) {
                        refreshActions();
                    }
                }
            });
            this.graphViewsPanel.setVisible(true);
        }
        return this.graphViewsPanel;
    }

    /**
     * Lazily creates and returns the panel with the rule tree.
     */
    JScrollPane getRuleJTreePanel() {
        if (this.ruleJTreePanel == null) {
            // make sure the preferred width is not smaller than the minimum
            // width
            this.ruleJTreePanel = new JScrollPane(getRuleTree()) {
                @Override
                public Dimension getPreferredSize() {
                    Dimension superSize = super.getPreferredSize();
                    return new Dimension((int) Math.max(superSize.getWidth(),
                        RULE_TREE_MINIMUM_WIDTH), (int) superSize.getHeight());
                }
            };
            this.ruleJTreePanel.setMinimumSize(new Dimension(
                RULE_TREE_MINIMUM_WIDTH, RULE_TREE_MINIMUM_HEIGHT));
        }
        return this.ruleJTreePanel;
    }

    /**
     * Creates and returns the panel with the start states list.
     */
    JPanel getStartStatesListPanel() {
        JPanel labelPane = new JPanel(new BorderLayout(), false);
        labelPane.add(new JLabel(" " + Options.STATES_PANE_TITLE + " "),
            BorderLayout.NORTH);
        JScrollPane startGraphsPane = new JScrollPane(this.getStateList()) {
            @Override
            public Dimension getPreferredSize() {
                Dimension superSize = super.getPreferredSize();
                return new Dimension((int) superSize.getWidth(),
                    START_LIST_MINIMUM_HEIGHT);
            }
        };
        labelPane.add(startGraphsPane, BorderLayout.CENTER);
        return labelPane;
    }

    /**
     * Returns the simulator panel on which the current state is displayed. Note
     * that this panel may currently not be visible.
     * @see #setGraphPanel(JGraphPanel)
     */
    StatePanel getStatePanel() {
        if (this.statePanel == null) {
            // panel for state display
            this.statePanel = new StatePanel(this);
            this.statePanel.setPreferredSize(GRAPH_VIEW_PREFERRED_SIZE);
        }
        return this.statePanel;
    }

    CAPanel getControlPanel() {
        if (this.controlPanel == null) {
            this.controlPanel = new CAPanel(this);
            this.controlPanel.setPreferredSize(GRAPH_VIEW_PREFERRED_SIZE);
        }
        return this.controlPanel;
    }

    /**
     * Returns the simulator panel on which the currently selected production
     * rule is displayed. Note that this panel may currently not be visible.
     * @see #setGraphPanel(JGraphPanel)
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
     * @see #setGraphPanel(JGraphPanel)
     */
    LTSPanel getLtsPanel() {
        if (this.ltsPanel == null) {
            this.ltsPanel = new LTSPanel(this);
            this.ltsPanel.setPreferredSize(GRAPH_VIEW_PREFERRED_SIZE);
        }
        return this.ltsPanel;
    }

    /**
     * Returns the LTSOptions panel on the simulator. Note that this panel may
     * currently not be visible.
     */
    ConditionalLTSPanel getConditionalLTSPanel() {
        if (this.conditionalLTSPanel == null) {
            this.conditionalLTSPanel =
                new ConditionalLTSPanel(this.getLtsPanel());
        }
        return this.conditionalLTSPanel;
    }

    /**
     * Returns the simulator panel on which the current state is displayed. Note
     * that this panel may currently not be visible.
     * @see #setGraphPanel(JGraphPanel)
     */
    TypePanel getTypePanel() {
        if (this.typePanel == null) {
            // panel for state display
            this.typePanel = new TypePanel(this);
            this.typePanel.setPreferredSize(GRAPH_VIEW_PREFERRED_SIZE);
        }
        return this.typePanel;
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
    JTree getRuleTree() {
        if (this.ruleJTree == null) {
            this.ruleJTree = new RuleJTree(this);
        }
        return this.ruleJTree;
    }

    /** Returns the exporter of the simulator. */
    Exporter getExporter() {
        return this.exporter;
    }

    /**
     * Returns the currently selected graph view component. This can be the
     * state, rule or LTS view. In case the LTS is active, the inner LTSPanel is
     * returned instead of the outer ConditionalLTSPanel.
     * @see #getStatePanel()
     * @see #getRulePanel()
     * @see #getLtsPanel()
     * @see #getConditionalLTSPanel()
     * @see #setGraphPanel(JGraphPanel)
     */
    JGraphPanel<?> getGraphPanel() {
        Component selectedComponent =
            getGraphViewsPanel().getSelectedComponent();

        if (selectedComponent == getConditionalLTSPanel()) {
            return getLtsPanel();
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
     * @see #getConditionalLTSPanel()
     * @see #getGraphPanel()
     */
    void setGraphPanel(JGraphPanel<?> component) {
        if (component == getLtsPanel()) {
            getGraphViewsPanel().setSelectedComponent(getConditionalLTSPanel());
        } else {
            getGraphViewsPanel().setSelectedComponent(component);
        }
    }

    /**
     * Changes the enabledness of one of the graph panels
     * @param component the panel to change (again, the inner LTSPanel is
     *        expected instead of the outer ConditionalLTSPanel)
     * @param enabled the new enabledness status
     */
    void setGraphPanelEnabled(JGraphPanel<?> component, boolean enabled) {
        int index;

        if (component == getLtsPanel()) {
            index =
                getGraphViewsPanel().indexOfComponent(getConditionalLTSPanel());
        } else {
            index = getGraphViewsPanel().indexOfComponent(component);
        }

        getGraphViewsPanel().setEnabledAt(index, enabled);
        if (component == getLtsPanel()) {
            String text;
            if (enabled) {
                text = "Labelled transition system";
            } else if (getGrammarView() == null) {
                text = "Currently disabled; load grammar";
            } else if (getGrammarView().getErrors().isEmpty()) {
                text =
                    String.format(
                        "Currently disabled; press %s to start simulation",
                        KeyEvent.getKeyText(((KeyStroke) getStartSimulationAction().getValue(
                            Action.ACCELERATOR_KEY)).getKeyCode()));
            } else {
                text = "Disabled due to grammar errors";
            }
            getGraphViewsPanel().setToolTipTextAt(index, text);
        }
    }

    /**
     * Adds an element to the set of refreshables. Also calls
     * {@link Refreshable#refresh()} on the element.
     */
    void addRefreshable(Refreshable element) {
        if (this.refreshables.add(element)) {
            element.refresh();
        }
    }

    /**
     * Is called after a change to current state, rule or derivation or to the
     * currently selected view panel to allow registered refreshable elements to
     * refresh themselves.
     */
    void refreshActions() {
        for (Refreshable action : this.refreshables) {
            action.refresh();
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
        result.add(createNewMenu());
        result.add(new JMenuItem(getLoadGrammarAction()));
        result.add(new JMenuItem(new LoadURLAction()));
        result.add(new JMenuItem(getLoadStartGraphAction()));
        // result.add(new JMenuItem(getLoadControlFileAction()));
        result.add(new JMenuItem(getLoadRuleAction()));
        /** mzimakova* */
        result.add(new JMenuItem(getRefreshGrammarAction()));
        result.add(createOpenRecentMenu());
        result.addSeparator();
        result.add(new JMenuItem(getSaveGrammarAction()));
        result.add(new JMenuItem(getSaveGraphAction()));
        result.add(new JMenuItem(getExportGraphAction()));
        result.add(new JMenuItem(getExportAction()));
        result.addSeparator();
        result.add(getEditItem());
        result.add(new JMenuItem(getEditSystemPropertiesAction()));
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

    private JMenu createNewMenu() {
        JMenu result = new JMenu(Options.CREATE_MENU_NAME);
        result.setMnemonic(Options.CREATE_MENU_MNEMONIC);
        // result.setAccelerator(Options.NEW_KEY);
        String menuName = result.getText();
        result.add(createItem(getNewGrammarAction(), menuName));
        result.add(createItem(getNewGraphAction(), menuName));
        result.add(createItem(getNewRuleAction(), menuName));
        // result.add(createItem(getNewControlAction(), menuName));
        return result;
    }

    /**
     * Creates and returns an edit menu for the menu bar.
     */
    private JMenu createEditMenu() {
        JMenu result = new JMenu(Options.EDIT_MENU_NAME);
        result.setMnemonic(Options.EDIT_MENU_MNEMONIC);
        result.add(getNewRuleAction());
        result.addSeparator();
        result.add(getEnableRuleAction());
        result.addSeparator();
        result.add(getCopyRuleAction());
        result.add(getDeleteRuleAction());
        result.add(getRenameRuleAction());
        result.addSeparator();
        result.add(getRenameLabelAction());
        result.addSeparator();
        result.add(getEditRuleAction());
        result.add(getEditStateAction());
        result.addSeparator();
        result.add(getEditRulePropertiesAction());
        result.add(getEditSystemPropertiesAction());
        return result;
    }

    /**
     * Returns the menu item in the file menu that specifies saving the
     * currently displayed graph (in the currently selected graph panel).
     */
    JMenuItem getEditItem() {
        if (this.editGraphItem == null) {
            this.editGraphItem = new JMenuItem();
            // load the graph edit action as default
            this.editGraphItem.setAction(getEditStateAction());
            // give the rule edit action a chance to replace the graph edit
            // action
            getEditRuleAction();
            this.editGraphItem.setAccelerator(Options.EDIT_KEY);
        }
        return this.editGraphItem;
    }

    /**
     * Creates and returns a display menu for the menu bar. The menu is filled
     * out each time it gets selected so as to be sure it applies to the current
     * jgraph
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
        result.addSeparator();
        result.add(getOptions().getItem(START_SIMULATION_OPTION));
        result.add(getOptions().getItem(STOP_SIMULATION_OPTION));
        result.add(getOptions().getItem(DELETE_RULE_OPTION));
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
        JMenu exploreMenu = new ScenarioMenu(this, false);
        result.setText(exploreMenu.getText());
        result.add(new JMenuItem(getUndoAction()));
        result.add(new JMenuItem(getRedoAction()));
        result.addSeparator();
        result.add(new JMenuItem(getStartSimulationAction()));
        // IOVKA change to activate abstract simulation
        result.add(new JMenuItem(getStartAbstrSimulationAction()));

        result.add(new JMenuItem(getApplyTransitionAction()));
        result.add(new JMenuItem(getGotoStartStateAction()));
        result.addSeparator();
        result.add(new JMenuItem(getExplorationDialogAction()));
        // BEGIN_IOVKA
        result.add(new JMenuItem(getChooseCustomScenarioAction()));
        // END_IOVKA
        result.addSeparator();
        // copy the exploration menu
        for (Component menuComponent : exploreMenu.getMenuComponents()) {
            result.add(menuComponent);
        }
        // TODO uncomment the two lines to enable LTL model checking
        // result.addSeparator();
        // result.add(new JMenuItem(showResultAction()));
        return result;
    }

    /**
     * Creates and returns a verification menu for the menu bar.
     */
    private JMenu createVerifyMenu() {
        JMenu result = new VerifyMenu(this);
        result.addSeparator();
        JMenu mcScenarioMenu = new MCScenarioMenu(this, false);
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
     * Creates a menu item from an action, while omitting some of the label
     * text.
     */
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
        return this.lastGrammarFile;
    }

    /**
     * Sets the last file from which a grammar was last loaded or to which a
     * grammar was saved to a given value.
     * @param grammarFile the file from which a grammar was most recently
     *        loaded, or to which a grammar was most recently saved; non-null.
     */
    void setLastGrammarFile(File grammarFile) {
        this.lastGrammarFile = grammarFile;
    }

    /**
     * Returns the file chooser for state (GST or GXL) files, lazily creating it
     * first.
     */
    JFileChooser getStateFileChooser() {
        if (this.stateFileChooser == null) {
            this.stateFileChooser = new GrooveFileChooser();
            this.stateFileChooser.addChoosableFileFilter(this.stateFilter);
            this.stateFileChooser.addChoosableFileFilter(this.gxlFilter);
            this.stateFileChooser.addChoosableFileFilter(this.autFilter);
            this.stateFileChooser.setFileFilter(this.stateFilter);
        }
        return this.stateFileChooser;
    }

    //
    // /**
    // * Returns the file chooser for control (GCP) files, lazily creating it
    // * first.
    // */
    // JFileChooser getControlFileChooser() {
    // if (this.controlFileChooser == null) {
    // this.controlFileChooser = new GrooveFileChooser();
    // this.controlFileChooser.addChoosableFileFilter(this.controlFilter);
    // // controlFileChooser.addChoosableFileFilter(gxlFilter);
    // this.controlFileChooser.setFileFilter(this.controlFilter);
    // }
    // return this.controlFileChooser;
    // }

    /**
     * mzimakova Returns the file chooser for rule (GPR) files, lazily creating
     * it first.
     */
    JFileChooser getRuleFileChooser() {
        if (this.ruleFileChooser == null) {
            this.ruleFileChooser = new GrooveFileChooser();
            this.ruleFileChooser.addChoosableFileFilter(this.ruleFilter);
            this.ruleFileChooser.setFileFilter(this.ruleFilter);
        }
        return this.ruleFileChooser;
    }

    /** Returns a dialog that will ask for a formula to be entered. */
    public FormulaDialog getFormulaDialog() {
        if (this.formulaDialog == null) {
            this.formulaDialog = new FormulaDialog();
        }
        return this.formulaDialog;
    }

    /**
     * Returns the explore-strategy for exploring a single state.
     * @return the explore-strategy for exploring a single state
     */
    private ExploreStateStrategy getExploreStateStrategy() {
        if (this.exploreStateStrategy == null) {
            this.exploreStateStrategy = new ExploreStateStrategy();
        }
        return this.exploreStateStrategy;
    }

    private ErrorListPanel getErrorPanel() {
        if (this.errorPanel == null) {
            this.errorPanel = new ErrorListPanel();
        }
        return this.errorPanel;
    }

    /**
     * Adds all implemented grammar loaders to the menu.
     */
    private void initGrammarLoaders() {
        this.grammarExtensions.clear();
        // loader for directories representing grammars
        this.grammarExtensions.add(GPS_FILTER);
        // loader for archives (jar/zip) containing directories representing
        // grammmars
        this.grammarExtensions.add(JAR_FILTER);
        this.grammarExtensions.add(ZIP_FILTER);
    }

    /**
     * Notifies all listeners of a new graph grammar. As a result,
     * {@link SimulationListener#setGrammarUpdate(StoredGrammarView)}is invoked
     * on all currently registered listeners. This method should not be called
     * directly: use {@link #setGrammar(StoredGrammarView)}instead.
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
    private void refresh() {
        setTitle();
        refreshActions();
    }

    /**
     * Sets the title of the frame to a given title.
     */
    private void setTitle() {
        StringBuffer title = new StringBuffer();
        if (getGrammarView() != null && getGrammarView().getName() != null) {
            title.append(getGrammarView().getName());
            AspectualGraphView startGraph =
                getGrammarView().getStartGraphView();
            if (startGraph != null) {
                title.append(TITLE_NAME_SEPARATOR);
                title.append(startGraph.getName());
            }
            SystemProperties properties = getGrammarView().getProperties();
            String controlName = properties.getControlName();
            if (properties.isUseControl() && controlName != null) {
                title.append(" | ");
                title.append(controlName);

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
    private StateGenerator getGenerator() {
        if (this.stateGenerator == null
            || this.stateGenerator.getGTS() != getGTS()) {
            if (getGTS() != null) {
                this.stateGenerator = createStateGenerator(getGTS());
            }
        }
        return this.stateGenerator;
    }

    /** Callback factory method for the state generator. */
    private StateGenerator createStateGenerator(GTS gts) {
        StateGenerator result;
        if (gts instanceof AGTS) {
            result =
                new AbstrStateGenerator((AGTS) gts,
                    ((AGTS) gts).getParameters());
        } else {
            result = new StateGenerator(gts);
        }
        // result.setGTS(gts);
        return result;
    }

    /**
     * If a simulation is active, asks through a dialog whether it may be
     * abandoned.
     * @param setGrammar flag indicating that
     *        {@link #setGrammar(StoredGrammarView)} is to be called with the
     *        current grammar, in case the simulation is abandoned
     * @return <tt>true</tt> if the current grammar may be abandoned
     */
    boolean confirmAbandon(boolean setGrammar) {
        boolean result;
        if (getGTS() != null) {
            result = confirmBehaviourOption(STOP_SIMULATION_OPTION);
            if (result && setGrammar) {
                setGrammar(getGrammarView());
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
     * version.
     */
    boolean confirmLoadStartState(String stateName) {
        if (getGrammarView().getStartGraphView() == null) {
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
            JOptionPane.showConfirmDialog(getFrame(), String.format(
                "Replace existing rule '%s'?", ruleName), null,
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
     * Enters a dialog that asks for a label to be renamed, and its the
     * replacement.
     * @return A pair consisting of the label to be replaced and its
     *         replacement, neither of which can be <code>null</code>; or
     *         <code>null</code> if the dialog was cancelled.
     */
    Pair<String,String> askReplacement() {
        ReplaceLabelDialog dialog =
            new ReplaceLabelDialog(Collections.<String>emptySet(), null);
        if (dialog.showDialog(getFrame(), null)) {
            return new Pair<String,String>(dialog.getOriginal(),
                dialog.getReplacement());
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
            this.options.getItem(Options.PREVIEW_ON_CLOSE_OPTION).setSelected(
                true);
        }
        return this.options;
    }

    /** Returns true if the current simulation is abstract. */
    public boolean isAbstractSimulation() {
        return getGTS() != null && getGTS() instanceof AGTS;
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
    private AspectualRuleView currentRule;

    /**
     * mzimakova The currently selected production rule set.
     */
    private final ArrayList<AspectualRuleView> currentRuleSet =
        new ArrayList<AspectualRuleView>();

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
     * The file or directory containing the last loaded or saved grammar, or
     * <tt>null</tt> if no grammar was loaded from file.
     */
    private File lastGrammarFile;

    /** The state generator strategy for the current GTS. */
    private StateGenerator stateGenerator;

    private ExploreStateStrategy exploreStateStrategy;

    /** Flag to indicate that one of the simulation events is underway. */
    private boolean updating;

    /**
     * A mapping from extension filters (recognising the file formats from the
     * names) to the corresponding grammar loaders.
     */
    private final Set<ExtensionFilter> grammarExtensions =
        new LinkedHashSet<ExtensionFilter>();

    /**
     * The graph loader used for saving graphs (states and LTS).
     */
    private final Xml<AspectGraph> graphLoader =
        new AspectGxl(new LayedOutXml());

    /**
     * The graph loader used for graphs in .aut format
     */
    private final Xml<AspectGraph> autLoader = new AspectGxl(new Aut());

    /**
     * File chooser for grammar files.
     */
    private JFileChooser grammarFileChooser;

    /**
     * File chooser for state files and LTS.
     */
    private JFileChooser stateFileChooser;
    //
    // /**
    // * File chooser for control files.
    // */
    // private JFileChooser controlFileChooser;

    /**
     * mzimakova File chooser for control files.
     */
    private JFileChooser ruleFileChooser;

    /**
     * Dialog for entering temporal formulae.
     */
    private FormulaDialog formulaDialog;

    /**
     * Graph exporter.
     */
    private final Exporter exporter = new Exporter();

    /**
     * Extension filter for state files.
     */
    private final ExtensionFilter stateFilter = Groove.createStateFilter();

    /**
     * Extension filter for CADP <code>.aut</code> files.
     */
    private final ExtensionFilter autFilter =
        new ExtensionFilter("CADP .aut files", Groove.AUT_EXTENSION);
    //
    // /**
    // * Extension filter for control files.
    // */
    // private final ExtensionFilter controlFilter =
    // Groove.createControlFilter();

    /**
     * mzimakova Extension filter for rule files.
     */
    private final ExtensionFilter ruleFilter = Groove.createRuleFilter();

    /**
     * Extension filter used for exporting the LTS in jpeg format.
     */
    private final ExtensionFilter gxlFilter = Groove.createGxlFilter();

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
    private JTree ruleJTree;

    /** Production system graph list */
    private StateJList stateJList;

    /** Production rule display panel. */
    private RulePanel rulePanel;

    /** State display panel. */
    private StatePanel statePanel;

    /** Control display panel. */
    private CAPanel controlPanel;

    /** LTS display panel. (which is contained in the ConditionalLTSPanel) */
    private LTSPanel ltsPanel;

    /** Conditional LTS display panel. */
    private ConditionalLTSPanel conditionalLTSPanel;

    /** Type graph display panel. */
    private TypePanel typePanel;

    /** Error display. */
    private ErrorListPanel errorPanel;

    /** Undo history. */
    private UndoHistory undoHistory;

    /** background for displays. */
    private JTabbedPane graphViewsPanel;

    /** History of recently opened grammars. */
    private History history;

    /** panel for the rule directory. */
    private JScrollPane ruleJTreePanel;

    /** Menu for externally provided actions. */
    private JMenu externalMenu;

    /** Dummy action for the {@link #externalMenu}. */
    private Action dummyExternalAction;

    /**
     * Menu item in the file menu for one of the graph or rule edit actions.
     */
    private JMenuItem editGraphItem;
    /**
     * The transition application action permanently associated with this
     * simulator.
     */
    private ApplyTransitionAction applyTransitionAction;
    /**
     * The custom scenario choose action permanently associated with this
     * simulator.
     */
    private ChooseCustomScenarioAction chooseScenarioAction;

    /**
     * The graph copying action permanently associated with this simulator.
     */
    private CopyGraphAction copyGraphAction;
    /**
     * The rule copying action permanently associated with this simulator.
     */
    private CopyRuleAction copyRuleAction;

    /**
     * The rule deletion action permanently associated with this simulator.
     */
    private DeleteRuleAction deleteRuleAction;
    /**
     * The graph deletion action permanently associated with this simulator.
     */
    private DeleteGraphAction deleteGraphAction;
    /**
     * The graph edit action permanently associated with this simulator.
     */
    private EditGraphAction editGraphAction;
    /**
     * The state edit action permanently associated with this simulator.
     */
    private EditStateAction editStateAction;

    /**
     * The rule edit action permanently associated with this simulator.
     */
    private EditRuleAction editRuleAction;

    /**
     * The rule properties edit action permanently associated with this
     * simulator.
     */
    private EditRulePropertiesAction editRulePropertiesAction;
    /**
     * The action to show the system properties of the currently selected
     * grammar.
     */
    private EditSystemPropertiesAction editSystemPropertiesAction;
    /**
     * The rule enabling action permanently associated with this simulator.
     */
    private EnableRuleAction enableRuleAction;

    /**
     * The exploration dialog action permanently associated with this simulator.
     */
    private ExplorationDialogAction explorationDialogAction;

    /** The state export action permanently associated with this simulator. */
    private ExportGraphAction exportGraphAction;

    /** The LTS export action permanently associated with this simulator. */
    private ExportAction exportAction;

    /**
     * The go-to start state action permanently associated with this simulator.
     */
    private GotoStartStateAction gotoStartStateAction;

    /**
     * The rule load action permanently associated with this simulator.
     * --mzimakova
     */
    private LoadRuleAction loadRuleAction;

    /** The start state load action permanently associated with this simulator. */
    private LoadStartGraphAction loadStartGraphAction;

    /** The grammar load action permanently associated with this simulator. */
    private LoadGrammarAction loadGrammarAction;

    /**
     * The graph creation action permanently associated with this simulator.
     */
    private NewGraphAction newGraphAction;
    /**
     * The rule system creation action permanently associated with this
     * simulator.
     */
    private NewGrammarAction newGrammarAction;
    /**
     * The rule creation action permanently associated with this simulator.
     */
    private NewRuleAction newRuleAction;
    // /**
    // * The control creation action permanently associated with this simulator.
    // */
    // private NewControlAction newControlAction;
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
     * The graph renaming action permanently associated with this simulator.
     */
    private RenameGraphAction renameGraphAction;
    /**
     * The rule renaming action permanently associated with this simulator.
     */
    private RenameRuleAction renameRuleAction;
    /**
     * The label renaming action permanently associated with this simulator.
     */
    private ReplaceLabelAction renameLabelAction;
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

    /**
     * The CTL formula providing action permanently associated with this
     * simulator.
     */
    private VerifyCTLAction verifyAction;

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
                        wait(10);
                    } catch (InterruptedException e) {
                        // do nothing
                    }
                }
                this.cancelDialog.setVisible(false);
            }
            finish();
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
     * @see Simulator#applyMatch()
     */
    private class ApplyTransitionAction extends AbstractAction implements
            Refreshable {
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
            // setEnabled(getCurrentTransition() != null);
            setEnabled(getCurrentEvent() != null);
        }
    }

    // BEGIN_IOVKA
    /** Allows to select a custom scenario and run it. */
    private class ChooseCustomScenarioAction extends AbstractAction implements
            Refreshable {
        ChooseCustomScenarioAction() {
            super("Run a custom scenario (work in progress)");
            addRefreshable(this);
        }

        public void actionPerformed(ActionEvent e) {
            ScenarioSelectionDialog sd =
                new ScenarioSelectionDialog(getFrame(), getGTS().getGrammar());
            Scenario customScenario = sd.showDialog();
            if (customScenario != null) {
                LaunchScenarioAction action =
                    getLaunchScenarioAction(customScenario);
                action.actionPerformed(e);
            }
        }

        public void refresh() {
            // Temporarily disable the dialog.
            // setEnabled(getCurrentGrammar() != null &&
            // getCurrentGrammar().getStartGraph() != null);
            setEnabled(false);
        }
    }

    private class CopyGraphAction extends AbstractAction implements Refreshable {
        CopyGraphAction() {
            super(Options.COPY_GRAPH_ACTION_NAME);
            addRefreshable(this);
        }

        public void refresh() {
            setEnabled(getGrammarStore().isModifiable());
        }

        public void actionPerformed(ActionEvent e) {
            //Multiple selection - mzimakova
            for (int i = 0; i < Simulator.this.stateJList.getSelectedIndices().length; i++) {
               String oldGraphName =
                   //(String) Simulator.this.stateJList.getSelectedValue();
                   (String) Simulator.this.stateJList.getSelectedValues()[i];
               if (oldGraphName != null) {
                   AspectualGraphView oldGraphView =
                       getGrammarView().getGraphView(oldGraphName);
                   String newGraphName =
                       askNewGraphName("Select new graph name", oldGraphName, true);
                   AspectGraph newGraph = oldGraphView.getAspectGraph().clone();
                   GraphInfo.setName(newGraph, newGraphName);
                   boolean last = (i == Simulator.this.stateJList.getSelectedIndices().length - 1);
                   doAddGraph(newGraph, last);
               }
            }
        }
    }

    private class CopyRuleAction extends AbstractAction implements Refreshable {
        CopyRuleAction() {
            super(Options.COPY_RULE_ACTION_NAME);
            addRefreshable(this);
        }

        public void refresh() {
            setEnabled(getCurrentRule() != null
                && getGrammarStore().isModifiable());
        }

        public void actionPerformed(ActionEvent e) {
            // Multiple selection - mzimakova
            if (confirmAbandon(false)) {
                for (int i = 0; i < getCurrentRuleSet().size(); i++) {
                    // AspectGraph oldRuleGraph =
                    // getCurrentRule().getAspectGraph();
                    AspectGraph oldRuleGraph =
                        getCurrentRuleSet().get(i).getAspectGraph();
                    RuleName newRuleName =
                        askNewRuleName("Select new rule name",
                        // getCurrentRule().getName(), true);
                            getCurrentRuleSet().get(i).getName(), true);
                    if (newRuleName != null) {
                        boolean last = (i == getCurrentRuleSet().size() - 1);
                        doAddRule(newRuleName, oldRuleGraph.clone(), last);
                    }
                }
            }
        }
    }

    private class DeleteGraphAction extends AbstractAction implements
            Refreshable {
        DeleteGraphAction() {
            super(Options.DELETE_GRAPH_ACTION_NAME);
            putValue(ACCELERATOR_KEY, Options.DELETE_KEY);
            addAccelerator(this);
            addRefreshable(this);
        }

        public void refresh() {
            setEnabled(getGrammarStore().isModifiable());
        }

        public void actionPerformed(ActionEvent e) {
            //Multiple selection - mzimakova
            String question = "Delete graph(s) '%s'";
            for (int i = 0; i < Simulator.this.stateJList.getSelectedIndices().length; i++) {
              String graphName =
                  //(String) Simulator.this.stateJList.getSelectedValue();
                  (String) Simulator.this.stateJList.getSelectedValues()[i];
              if (graphName != null) {
                  question = String.format(question, graphName);
                  boolean isStartGraph =
                      graphName.equals(getGrammarView().getStartGraphName());
                  if (isStartGraph) {
                      question = question + " (start graph)";
                  }
                  if (i < Simulator.this.stateJList.getSelectedIndices().length - 1) {
                      question = question + ", '%s'";
                  } else {
                      question = question + "?";
                  }
              }
            }
            if (confirmBehaviour(Options.DELETE_GRAPH_OPTION, question)) {
              for (int i = 0; i < Simulator.this.stateJList.getSelectedIndices().length; i++) {            
                String graphName =
                      (String) Simulator.this.stateJList.getSelectedValues()[i];
                if (graphName != null) {
                        doDeleteGraph(graphName);
                }
              }
            }
        }
    }

    private class DeleteRuleAction extends AbstractAction implements
            Refreshable {
        DeleteRuleAction() {
            super(Options.DELETE_RULE_ACTION_NAME);
            putValue(ACCELERATOR_KEY, Options.DELETE_KEY);
            addAccelerator(this);
            addRefreshable(this);
        }

        public void refresh() {
            setEnabled(getCurrentRule() != null
                && getGrammarStore().isModifiable());
        }

        public void actionPerformed(ActionEvent e) {
            // Multiple selection - mzimakova
            // RuleName ruleName = getCurrentRule().getRuleName();
            String question = "Delete rule(s) '%s'";
            for (int i = 0; i < getCurrentRuleSet().size(); i++) {
                RuleName ruleName = getCurrentRuleSet().get(i).getRuleName();
                question = String.format(question, ruleName);
                if (i < getCurrentRuleSet().size() - 1) {
                    question = question + ", '%s'";
                } else {
                    question = question + "?";
                }
            }
            if (confirmBehaviour(Options.DELETE_RULE_OPTION, question)) {
                for (int i = 0; i < getCurrentRuleSet().size(); i++) {
                    RuleName ruleName =
                        getCurrentRuleSet().get(i).getRuleName();
                    doDeleteRule(ruleName);
                }
            }

        }
    }

    /**
     * Action for editing the current state.
     */
    private class EditStateAction extends AbstractAction implements Refreshable {
        /** Constructs an instance of the action. */
        EditStateAction() {
            super(Options.EDIT_STATE_ACTION_NAME);
            addRefreshable(this);
        }

        /**
         * Checks if the enabling condition is satisfied, and if so, calls
         * {@link #setEnabled(boolean)}.
         */
        public void refresh() {
            boolean enabled =
                getGraphPanel() == getStatePanel() && getGrammarView() != null
                    && getGrammarView().getStartGraphView() != null
                    && getGrammarStore().isModifiable();
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
            EditorDialog dialog =
                new EditorDialog(getFrame(), getOptions(),
                    stateModel.toPlainGraph()) {
                    @Override
                    public void finish() {
                        File saveFile = handleSaveGraph(true, toAspectGraph());
                        if (saveFile != null
                            && confirmLoadStartState(saveFile.getName())) {
                            doLoadStartGraph(saveFile);
                        }
                    }
                };
            dialog.start();
        }
    }

    /**
     * Action for editing the currently selected graph in the graph list.
     */
    private class EditGraphAction extends AbstractAction implements Refreshable {
        /** Constructs an instance of the action. */
        EditGraphAction() {
            super(Options.EDIT_GRAPH_ACTION_NAME);
            addRefreshable(this);
        }

        public void refresh() {
            getGrammarStore().isModifiable();
        }

        /**
         * Invokes the editor on the current state. Handles the execution of an
         * <code>EditGraphAction</code>, if the current panel is the state
         * panel.
         */
        public void actionPerformed(ActionEvent e) {
            String oldGraphName =
                (String) Simulator.this.stateJList.getSelectedValue();
            if (oldGraphName != null) {
                AspectualGraphView oldGraphView =
                    getGrammarView().getGraphView(oldGraphName);
                EditorDialog dialog =
                    new EditorDialog(getFrame(), getOptions(),
                        oldGraphView.getAspectGraph().toPlainGraph()) {
                        @Override
                        public void finish() {
                            File saveFile =
                                handleSaveGraph(true, toAspectGraph());
                            if (saveFile != null
                                && confirmLoadStartState(saveFile.getName())) {
                                doLoadStartGraph(saveFile);
                            }
                        }
                    };
                dialog.start();
            }
        }
    }

    private class EditRulePropertiesAction extends AbstractAction implements
            Refreshable {
        EditRulePropertiesAction() {
            super(Options.RULE_PROPERTIES_ACTION_NAME);
            addRefreshable(this);
        }

        public void refresh() {
            setEnabled(getCurrentRule() != null
                && getGrammarStore().isModifiable());
        }

        public void actionPerformed(ActionEvent e) {
            //Multiple selection - mzimakova
               AspectualRuleView rule =  getCurrentRule(); 
               AspectGraph ruleGraph = rule.getAspectGraph();
               GraphProperties ruleProperties =
                   GraphInfo.getProperties(ruleGraph, true);
               String currentPriority = null;
               String currentEnabled = null;
               String currentConfluent = null;
               String currentRemark = null;
               //save current rule properties
               if (getCurrentRuleSet().size() > 1) {
                  currentPriority = Integer.toString(ruleProperties.getPriority());
                  currentEnabled = Boolean.toString(ruleProperties.isEnabled());
                  currentConfluent = Boolean.toString(ruleProperties.isConfluent());
                  currentRemark = ruleProperties.getRemark();                 
                  ruleProperties.clear();
               } 
               PropertiesDialog dialog =
                new PropertiesDialog(ruleProperties,
                    GraphProperties.DEFAULT_USER_KEYS, true);
            
            if (dialog.showDialog(getFrame()) && confirmAbandon(false)) {
              
              //Get properties from the dialog frame 
              Map<String,String> editedProperties = dialog.getEditedProperties();
              String editedPriority = editedProperties.get(GraphProperties.PRIORITY_KEY);
              String editedEnabled = editedProperties.get(GraphProperties.ENABLED_KEY);
              String editedConfluent = editedProperties.get(GraphProperties.CONFLUENT_KEY);
              String editedRemark = editedProperties.get(GraphProperties.REMARK_KEY);
              for (int i = 0; i < getCurrentRuleSet().size(); i++)
              { 
                rule = getCurrentRuleSet().get(i); 
                ruleGraph = rule.getAspectGraph();
                ruleProperties =
                    GraphInfo.getProperties(ruleGraph, true);
                
               if (getCurrentRuleSet().size() > 1) {
                   
                   //restore current rule properties
                   if (i==0) {
                    ruleProperties.put(GraphProperties.PRIORITY_KEY, currentPriority); 
                    ruleProperties.put(GraphProperties.ENABLED_KEY, currentEnabled); 
                    ruleProperties.put(GraphProperties.CONFLUENT_KEY, currentConfluent); 
                    ruleProperties.put(GraphProperties.REMARK_KEY, currentRemark); 
                   }
                
                //Check that properties in the dialog frame were changed
                if (editedPriority == null) {
                    editedProperties.put(GraphProperties.PRIORITY_KEY, Integer.toString(ruleProperties.getPriority()));
                } else {
                    editedProperties.put(GraphProperties.PRIORITY_KEY, editedPriority); 
                }
                if (editedEnabled == null) {
                    editedProperties.put(GraphProperties.ENABLED_KEY, Boolean.toString(ruleProperties.isEnabled()));
                } else {
                    editedProperties.put(GraphProperties.ENABLED_KEY, editedEnabled); 
                }
                if (editedConfluent == null) {
                    editedProperties.put(GraphProperties.CONFLUENT_KEY, Boolean.toString(ruleProperties.isConfluent()));
                } else {
                    editedProperties.put(GraphProperties.CONFLUENT_KEY, editedConfluent); 
                }
                if (editedRemark == null) {
                    editedProperties.put(GraphProperties.REMARK_KEY, ruleProperties.getRemark());
                } else {
                    editedProperties.put(GraphProperties.REMARK_KEY, editedRemark);
                }
                
               }
                
                //Set new properties
                ruleProperties.clear();
                ruleProperties.putAll(editedProperties);
                doDeleteRule(rule.getRuleName());
                boolean last = (i==getCurrentRuleSet().size()-1);
                doAddRule(rule.getRuleName(), ruleGraph, last);
               }
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
            boolean enabled =
                getCurrentRule() != null && getGrammarStore().isModifiable();
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
         * <code>EditGraphAction</code>, if the current panel is the rule panel.
         * 
         * @require <tt>getCurrentRule != null</tt>.
         */
        public void actionPerformed(ActionEvent e) {
            final String ruleName = getCurrentRule().getName();
            EditorDialog dialog =
                new EditorDialog(getFrame(), getOptions(),
                    getRulePanel().getJModel().toPlainGraph()) {
                    @Override
                    public void finish() {
                        if (confirmAbandon(false)) {
                            AspectGraph ruleAsAspectGraph = toAspectGraph();
                            RuleName newRuleName =
                                askNewRuleName("Name for edited rule",
                                    ruleName, false);
                            if (newRuleName != null) {
                                doAddRule(newRuleName, ruleAsAspectGraph, true);
                            }
                        }
                    }
                };
            dialog.start();
        }
    }

    /** Action to show the system properties. */
    private class EditSystemPropertiesAction extends AbstractAction implements
            Refreshable {
        /** Constructs an instance of the action. */
        EditSystemPropertiesAction() {
            super(Options.SYSTEM_PROPERTIES_ACTION_NAME);
            addRefreshable(this);
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

    /** Action to open the Exploration Dialog. */
    private class ExplorationDialogAction extends AbstractAction implements
            Refreshable {
        /** Constructs an instance of the action. */
        ExplorationDialogAction() {
            super(Options.EXPLORATION_DIALOG_ACTION_NAME);
            addRefreshable(this);
        }

        public void actionPerformed(ActionEvent evt) {
            new ExplorationDialog(Simulator.this, getFrame());
        }

        public void refresh() {
            // Temporarily disable the dialog.
            // setEnabled(getCurrentGrammar() != null &&
            // getCurrentGrammar().getStartGraph() != null);
            setEnabled(false);
        }
    }

    private class ExportAction extends AbstractAction implements Refreshable {

        ExportAction() {
            super("Export Simulation ...");
            addRefreshable(this);
        }

        public void actionPerformed(ActionEvent arg0) {
            ExportDialog dialog = new ExportDialog(Simulator.this);
            dialog.setCurrentDirectory(getLastGrammarFile().getAbsolutePath());

            if (dialog.showDialog(Simulator.this)) {

                File file = new File(dialog.getDirectory());
                int exportStates = dialog.getExportStates();
                boolean showFinal = dialog.showFinal();
                boolean showNames = dialog.showNames();
                boolean showStart = dialog.showStart();
                boolean showOpen = dialog.showOpen();

                GTS gts = Simulator.this.getGTS();

                LTSGraph lts =
                    new LTSGraph(gts, showFinal, showStart, showOpen, showNames);

                Collection<GraphState> export = new HashSet<GraphState>(0);

                if (exportStates == ExportDialog.STATES_ALL) {
                    export = gts.getStateSet();
                } else if (exportStates == ExportDialog.STATES_FINAL) {
                    export = gts.getFinalStates();
                }

                try {
                    Groove.saveGraph(lts,
                        new File(file, "lts.gxl").getAbsolutePath());
                    for (GraphState state : export) {
                        String name = state.toString();
                        Groove.saveGraph(state.getGraph(), new File(file, name
                            + ".gst").getAbsolutePath());
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
     * Action to save the state, as a graph or in some export format.
     * @see Exporter#export(JGraph, File)
     */
    private class ExportGraphAction extends AbstractAction implements
            Refreshable {
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
                fileName = getCurrentRule().getName();
                jGraph = getRulePanel().getJGraph();
            }
            getExporter().getFileChooser().setSelectedFile(new File(fileName));
            File selectedFile =
                ExtensionFilter.showSaveDialog(getExporter().getFileChooser(),
                    getFrame(), null);
            // now save, if so required
            if (selectedFile != null) {
                try {
                    getExporter().export(jGraph, selectedFile);
                } catch (IOException exc) {
                    new ErrorDialog(getFrame(), "Error while exporting to "
                        + selectedFile, exc);
                }

            }
        }

        /**
         * Tests if the action should be enabled according to the current state
         * of the simulator, and also modifies the action name.
         */
        public void refresh() {
            if (getGraphPanel() == getLtsPanel()) {
                setEnabled(getGTS() != null);
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
     * Action that changes the enabledness status of the currently selected
     * rule.
     * @see #doEnableRule()
     */
    private class EnableRuleAction extends AbstractAction implements
            Refreshable {
        EnableRuleAction() {
            super(Options.DISABLE_ACTION_NAME);
            addRefreshable(this);
        }

        public void refresh() {
            boolean ruleSelected = getCurrentRule() != null;
            setEnabled(ruleSelected && getGrammarStore().isModifiable());
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

    /**
     * Action for setting the initial state of the LTS as current state.
     * @see GTS#startState()
     * @see Simulator#setState(GraphState)
     */
    private class GotoStartStateAction extends AbstractAction implements
            Refreshable {
        /** Constructs an instance of the action. */
        GotoStartStateAction() {
            super(Options.GOTO_START_STATE_ACTION_NAME);
            putValue(ACCELERATOR_KEY, Options.GOTO_START_STATE_KEY);
            addRefreshable(this);
        }

        public void actionPerformed(ActionEvent evt) {
            setState(getGTS().startState());
        }

        public void refresh() {
            setEnabled(getGTS() != null);
        }
    }

    /** An action used for launching a scenario. */
    private class LaunchScenarioAction extends AbstractAction {
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
     */
    private class LaunchThread extends CancellableThread {
        /**
         * Constructs a generate thread for a given exploration stragegy.
         * @param scenario the scenario handler of this thread
         */
        LaunchThread(Scenario scenario) {
            super(getLtsPanel(), "Exploring state space");
            this.scenario = scenario;
            this.progressListener = createProgressListener();
        }

        @Override
        public void doAction() {
            GTS gts = getGTS();
            displayProgress(gts);
            gts.addGraphListener(this.progressListener);
            this.scenario.play();
            gts.removeGraphListener(this.progressListener);
        }

        @Override
        public void finish() {
            // setResult();
            showResult();
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
        void displayProgress(GraphShape graph) {
            getStateCountLabel().setText("States: " + graph.nodeCount());
            getTransitionCountLabel().setText(
                "Transitions: " + graph.edgeCount());
        }

        private void showResult() {
            Collection<? extends Object> result =
                this.scenario.getResult().getValue();
            Collection<GraphState> states = new HashSet<GraphState>();
            for (Object object : result) {
                if (object instanceof GraphState) {
                    states.add((GraphState) object);
                }
            }
            visualize = states;
        }

        /** LTS generation strategy of this thread. */
        private final Scenario scenario;
        /** Progress listener for the generate thread. */
        private final GraphListener progressListener;
        /** Label displaying the number of states generated so far. */
        private JLabel transitionCountLabel;
        /** Label displaying the number of transitions generated so far. */
        private JLabel stateCountLabel;
    }

    /**
     * Action for loading and setting a new initial state.
     * @see Simulator#doLoadStartGraph(File)
     */
    private class LoadStartGraphAction extends AbstractAction implements
            Refreshable {
        /** Constructs an instance of the action. */
        LoadStartGraphAction() {
            super(Options.LOAD_START_STATE_ACTION_NAME);
            addRefreshable(this);
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

    //
    // /**
    // * Action for loading and setting a different control program.
    // * @see Simulator#doLoadControlFile(File)
    // */
    // private class LoadControlFileAction extends AbstractAction implements
    // Refreshable {
    // /** Constructs an instance of the action. */
    // LoadControlFileAction() {
    // super(Options.LOAD_CONTROL_FILE_ACTION_NAME);
    // addRefreshable(this);
    // }
    //
    // public void actionPerformed(ActionEvent evt) {
    // int result = getControlFileChooser().showOpenDialog(getFrame());
    // // now load, if so required
    // if (result == JFileChooser.APPROVE_OPTION && confirmAbandon(false)) {
    // doLoadControlFile(getControlFileChooser().getSelectedFile());
    // }
    // }
    //
    // /**
    // * Sets the enabling status of this action, depending on whether a
    // * grammar is currently loaded.
    // */
    // public void refresh() {
    // setEnabled(getCurrentGrammar() != null);
    // }
    // }

    /**
     * mzimakova Action for loading and setting a different control program.
     */
    private class LoadRuleAction extends AbstractAction implements Refreshable {
        /** Constructs an instance of the action. */
        LoadRuleAction() {
            super(Options.LOAD_RULE_ACTION_NAME);
            addRefreshable(this);
        }

        public void actionPerformed(ActionEvent evt) {
            int result = getRuleFileChooser().showOpenDialog(getFrame());
            // now load, if so required
            if (result == JFileChooser.APPROVE_OPTION && confirmAbandon(false)) {
                try {
                    File ruleFile = getRuleFileChooser().getSelectedFile();
                    AspectGraph ruleGraph = unmarshalGraph(ruleFile);
                    RuleName ruleName =
                        new RuleName(GraphInfo.getName(ruleGraph));
                    if (getGrammarView().getRuleView(ruleName) == null
                        || confirmOverwriteRule(ruleName)) {
                        doAddRule(ruleName, ruleGraph, true);
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
                    showErrorDialog(String.format("Invalid URL '%s'",
                        e.getMessage()), e);
                }
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
                File grammarFile = getLastGrammarFile();
                File newGrammar;
                if (grammarFile == null) {
                    newGrammar = new File(NEW_GRAMMAR_NAME);
                } else {
                    newGrammar =
                        new File(grammarFile.getParentFile(), NEW_GRAMMAR_NAME);
                }
                getGrammarFileChooser().setSelectedFile(newGrammar);
                getGrammarFileChooser().setFileFilter(GPS_FILTER);
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

    private class NewGraphAction extends AbstractAction implements Refreshable {
        NewGraphAction() {
            super(Options.NEW_GRAPH_ACTION_NAME);
            addRefreshable(this);
        }

        public void actionPerformed(ActionEvent e) {
            Graph newGraph = GraphFactory.getInstance().newGraph();
            GraphInfo.setName(newGraph, NEW_GRAPH_NAME);
            GraphInfo.setGraphRole(newGraph);
            EditorDialog dialog =
                new EditorDialog(getFrame(), getOptions(), newGraph) {
                    @Override
                    public void finish() {
                        AspectGraph newGraph = toAspectGraph();
                        File saveFile = handleSaveGraph(true, newGraph);
                        if (saveFile != null) {
                            if (confirmLoadStartState(newGraph.getInfo().getName())) {
                                doLoadStartGraph(saveFile);
                            }
                        }
                    }
                };
            dialog.start();
        }

        /** Enabled if there is a grammar loaded. */
        public void refresh() {
            setEnabled(getGrammarView() != null
                && getGrammarStore().isModifiable());
        }
    }

    private class NewRuleAction extends AbstractAction implements Refreshable {
        NewRuleAction() {
            super(Options.NEW_RULE_ACTION_NAME);
            addRefreshable(this);
        }

        public void actionPerformed(ActionEvent e) {
            if (confirmAbandon(false)) {
                final RuleName ruleName =
                    askNewRuleName(null, NEW_RULE_NAME, true);
                if (ruleName != null) {
                    Graph newRule = GraphFactory.getInstance().newGraph();
                    GraphInfo.setName(newRule, ruleName.text());
                    GraphInfo.setRuleRole(newRule);
                    EditorDialog dialog =
                        new EditorDialog(getFrame(), getOptions(), newRule) {
                            @Override
                            public void finish() {
                                doAddRule(ruleName, toAspectGraph(), true);
                            }
                        };
                    dialog.start();
                }
            }
        }

        /** Enabled if there is a grammar loaded. */
        public void refresh() {
            setEnabled(getGrammarView() != null
                && getGrammarStore().isModifiable());
        }
    }

    // private class NewControlAction extends AbstractAction implements
    // Refreshable {
    // NewControlAction() {
    // super(Options.NEW_CONTROL_ACTION_NAME);
    // addRefreshable(this);
    // }
    //
    // public void actionPerformed(ActionEvent e) {
    // if (confirmAbandon(false)) {
    // File location =
    // FileGps.toFile(Simulator.this.getCurrentGrammarURL());
    // int offset = 0;
    // File controlFile =
    // new File(
    // location,
    // Simulator.this.controlFilter.addExtension(Groove.DEFAULT_CONTROL_NAME));
    // while (controlFile.exists()) {
    // offset++;
    // controlFile =
    // new File(
    // location,
    // Simulator.this.controlFilter.addExtension(Groove.DEFAULT_CONTROL_NAME
    // + offset));
    // }
    //
    // JFileChooser chooser = Simulator.this.getControlFileChooser();
    // chooser.setSelectedFile(controlFile);
    //
    // int result = chooser.showOpenDialog(getFrame());
    // // now load, if so required
    // if (result == JFileChooser.APPROVE_OPTION
    // && confirmAbandon(false)) {
    //
    // if (!chooser.getSelectedFile().exists()) {
    //
    // // ControlView cv = grammar.getControl();
    // try {
    // ControlView.store("", new FileOutputStream(
    // chooser.getSelectedFile()));
    // doLoadControlFile(chooser.getSelectedFile());
    // Simulator.this.getGraphViewsPanel().setSelectedComponent(
    // Simulator.this.getControlPanel());
    // } catch (IOException ioe) {
    // showErrorDialog(ioe.getMessage(), ioe);
    // }
    // }
    // }
    //
    // }
    // }
    //
    // /** Enabled if there is a grammar loaded. */
    // public void refresh() {
    // setEnabled(getCurrentGrammar() != null
    // && Simulator.this.currentGrammarLoader.canWrite());
    // }
    // }

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
     * Action for refreshing the rule system. Reloads the current rule system
     * and start graph.
     * @see Simulator#doRefreshGrammar()
     */
    private class RefreshGrammarAction extends AbstractAction implements
            Refreshable {
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
            setEnabled(getGrammarView() != null);
        }
    }

    private class RenameGraphAction extends AbstractAction implements
            Refreshable {
        RenameGraphAction() {
            super(Options.RENAME_GRAPH_ACTION_NAME);
            addRefreshable(this);
            /*
             * The F2-accelerator is not working, but I do not know why
             * putValue(ACCELERATOR_KEY, Options.RELABEL_KEY);
             * addAccelerator(this);
             */
        }

        public void refresh() {
            setEnabled(getGrammarStore().isModifiable());
        }

        public void actionPerformed(ActionEvent e) {
          //Multiple selection - mzimakova
          for (int i = 0; i < Simulator.this.stateJList.getSelectedIndices().length; i++) {
            String oldGraphName =
                //(String) Simulator.this.stateJList.getSelectedValue();
                (String) Simulator.this.stateJList.getSelectedValues()[i];
            if (oldGraphName != null) {
                AspectualGraphView graph =
                    getGrammarView().getGraphView(oldGraphName);
                assert graph != null : String.format(
                    "Graph '%s' in graph list but not in grammar", oldGraphName);
                String newGraphName =
                    askNewGraphName("Select new graph name", oldGraphName,
                        false);
                if (!oldGraphName.equals(newGraphName)) {
                    boolean last = (i == Simulator.this.stateJList.getSelectedIndices().length - 1);
                    doRenameGraph(graph.getAspectGraph(), newGraphName, last);
                }
            }
          }
        }
    }

    private class RenameRuleAction extends AbstractAction implements
            Refreshable {
        RenameRuleAction() {
            super(Options.RENAME_RULE_ACTION_NAME);
            addRefreshable(this);
            /*
             * The F2-accelerator is not working, but I do not know why
             * putValue(ACCELERATOR_KEY, Options.RELABEL_KEY);
             * addAccelerator(this);
             */
        }

        public void refresh() {
            setEnabled(getCurrentRule() != null);
        }

        public void actionPerformed(ActionEvent e) {
            if (confirmAbandon(true)) {
                //Multiple selection - mzimakova
                //RuleName oldRuleName = getCurrentRule().getRuleName();
               for (int i = 0; i < getCurrentRuleSet().size(); i++)
               {
                RuleName oldRuleName = getCurrentRuleSet().get(i).getRuleName();
                AspectGraph ruleGraph = getCurrentRuleSet().get(i).getAspectGraph();
                RuleName newRuleName =
                    askNewRuleName("Select new rule name", oldRuleName.text(),
                        true);
                if (newRuleName != null) {
                    doDeleteRule(oldRuleName);
                    boolean last = (i==getCurrentRuleSet().size()-1);
                    doAddRule(newRuleName, ruleGraph, last);
                }
               }
            }
        }
    }

    /** Action that renames all instances of a given label into another. */
    private class ReplaceLabelAction extends AbstractAction implements
            Refreshable {
        ReplaceLabelAction() {
            super(Options.REPLACE_ACTION_NAME);
            addRefreshable(this);
        }

        public void refresh() {
            setEnabled(getGrammarView() != null);
        }

        public void actionPerformed(ActionEvent e) {
            if (confirmAbandon(true)) {
                Pair<String,String> renaming = askReplacement();
                if (renaming != null) {
                    doRenameLabel(renaming.first(), renaming.second());
                }
            }
        }
    }

    /**
     * Action for saving a rule system. Currently not enabled.
     */
    private class SaveGrammarAction extends AbstractAction implements
            Refreshable {
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
                    doSaveGrammar(selectedFile);
                }
            }
        }

        public void refresh() {
            setEnabled(getGrammarView() != null);
        }
    }

    /**
     * Action to save the state or LTS as a graph.
     * @see Simulator#handleSaveGraph(boolean, AspectGraph)
     * @see Simulator#doAddGraph(Graph)
     */
    private class SaveGraphAction extends AbstractAction implements Refreshable {
        /** Constructs an instance of the action. */
        SaveGraphAction() {
            super(Options.SAVE_ACTION_NAME);
            putValue(ACCELERATOR_KEY, Options.SAVE_GRAPH_KEY);
            addRefreshable(this);
        }

        public void actionPerformed(ActionEvent e) {
            AspectGraph graph =
                AspectGraph.newInstance(getGraphPanel().getJModel().toPlainGraph());
            if (getGraphPanel() == getLtsPanel()) {
                handleSaveGraph(false, graph);
            } else {
                handleSaveGraph(true, graph);
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

    private class StartSimulationAction extends AbstractAction implements
            Refreshable {
        /** Constructs an instance of the action. */
        StartSimulationAction() {
            super(Options.START_SIMULATION_ACTION_NAME);
            putValue(Action.ACCELERATOR_KEY, Options.START_SIMULATION_KEY);
            addRefreshable(this);
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
     * A variant of {@link Simulator.StartSimulationAction} for abstract
     * simulation.
     */
    private class StartAbstrSimulationAction extends AbstractAction implements
            Refreshable {
        /** Constructs an instance of the action. */
        StartAbstrSimulationAction() {
            super(Options.START_ABSTR_SIMULATION_ACTION_NAME);
            addRefreshable(this);
        }

        public void actionPerformed(ActionEvent e) {
            if (confirmAbandon(false)) {
                startAbstrSimulation();
            }
        }

        public void refresh() {
            boolean enabled =
                getGrammarView() != null
                    && getGrammarView().getErrors().isEmpty();
            setEnabled(enabled);
        }
    }

    // private class NewControlAction extends AbstractAction implements
    // Refreshable {
    // NewControlAction() {
    // super(Options.NEW_CONTROL_ACTION_NAME);
    // addRefreshable(this);
    // }
    //
    // public void actionPerformed(ActionEvent e) {
    // if (confirmAbandon(false)) {
    // File location =
    // FileGps.toFile(Simulator.this.getCurrentGrammarURL());
    // int offset = 0;
    // File controlFile =
    // new File(
    // location,
    // Simulator.this.controlFilter.addExtension(Groove.DEFAULT_CONTROL_NAME));
    // while (controlFile.exists()) {
    // offset++;
    // controlFile =
    // new File(
    // location,
    // Simulator.this.controlFilter.addExtension(Groove.DEFAULT_CONTROL_NAME
    // + offset));
    // }
    //
    // JFileChooser chooser = Simulator.this.getControlFileChooser();
    // chooser.setSelectedFile(controlFile);
    //
    // int result = chooser.showOpenDialog(getFrame());
    // // now load, if so required
    // if (result == JFileChooser.APPROVE_OPTION
    // && confirmAbandon(false)) {
    //
    // if (!chooser.getSelectedFile().exists()) {
    //
    // // ControlView cv = grammar.getControl();
    // try {
    // ControlView.store("", new FileOutputStream(
    // chooser.getSelectedFile()));
    // doLoadControlFile(chooser.getSelectedFile());
    // Simulator.this.getGraphViewsPanel().setSelectedComponent(
    // Simulator.this.getControlPanel());
    // } catch (IOException ioe) {
    // showErrorDialog(ioe.getMessage(), ioe);
    // }
    // }
    // }
    //
    // }
    // }
    //
    // /** Enabled if there is a grammar loaded. */
    // public void refresh() {
    // setEnabled(getCurrentGrammar() != null
    // && Simulator.this.currentGrammarLoader.canWrite());
    // }
    // }

    /**
     * Action for verifying a CTL formula.
     */
    private class VerifyCTLAction extends AbstractAction implements Refreshable {
        /** Constructs an instance of the action. */
        VerifyCTLAction() {
            super(Options.PROVIDE_CTL_FORMULA_ACTION_NAME);
            setEnabled(true);
            addRefreshable(this);
        }

        public void actionPerformed(ActionEvent evt) {
            int goOn = 0;
            // if there are still open states the result might be different as
            // expected
            // ask the user whether really to continue
            if (getGTS().hasOpenStates()) {
                String message =
                    "The transition system still contains open states. Do you want to contiue verifying it?";
                goOn =
                    JOptionPane.showConfirmDialog(getFrame(), message,
                        "Open states", JOptionPane.YES_NO_OPTION);
            }
            if (goOn == JOptionPane.YES_OPTION) {
                FormulaDialog dialog = getFormulaDialog();
                dialog.showDialog(getFrame());
                String property = dialog.getProperty();
                if (property != null) {
                    doVerifyProperty(property);
                }
            }
        }

        public void refresh() {
            setEnabled(getGrammarView() != null);
        }
    }

    /** Class wrapping a menu of recently opened files. */
    private class History {

        private final JMenu menu;
        private final ArrayList<LoadAction> history =
            new ArrayList<LoadAction>();

        /** Constructs a fresh history instance. */
        public History() {
            String[] savedLocations =
                Options.userPrefs.get(SystemProperties.HISTORY_KEY, "").split(
                    ",");
            for (String location : savedLocations) {
                try {
                    this.history.add(new LoadAction(location));
                } catch (IOException exc) {
                    // url should never be malformed, since it appears only in
                    // the history when we could previously load a grammar from
                    // it...
                }
            }

            this.menu = new JMenu(Options.OPEN_RECENT_MENU_NAME);
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
        public void updateLoadGrammar(String location) {
            try {
                LoadAction newAction = new LoadAction(location);
                this.history.remove(newAction);
                this.history.add(0, newAction);
                // trimming list to 10 elements
                while (this.history.size() > 10) {
                    this.history.remove(10);
                }
                synch();
            } catch (IOException exc) {
                // url should never be malformed, since it appears only in
                // the history when we could previously load a grammar from
                // it...
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

        private class LoadAction extends AbstractAction {
            /**
             * Constructs an action that will load a grammar from a predefined
             * location.
             * @param location the location to load from; non-null
             */
            LoadAction(String location) throws IOException {
                this.location = location;
                this.store = SystemStoreFactory.newStore(location);
                putValue(NAME, this.store.toString());
            }

            public void actionPerformed(ActionEvent evt) {
                try {
                    doLoadGrammar(this.store, null);
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

    /** Filter for rule system files. */
    static private final ExtensionFilter GPS_FILTER =
        Groove.createRuleSystemFilter();
    /** File filter for jar files. */
    static private final ExtensionFilter JAR_FILTER =
        new ExtensionFilter("Jar-file containing Groove production system",
            ".gps.jar", false) {
            @Override
            public boolean accept(File file) {
                return super.accept(file) || file.isDirectory()
                    && !GPS_FILTER.hasExtension(file.getName());
            }
        };
    /** File filter for jar files. */
    static private final ExtensionFilter ZIP_FILTER =
        new ExtensionFilter("Zip-file containing Groove production system",
            ".gps.zip", false) {
            @Override
            public boolean accept(File file) {
                return super.accept(file) || file.isDirectory()
                    && !GPS_FILTER.hasExtension(file.getName());
            }

        };

    /**
     * Minimum width of the rule tree component.
     */
    static private final int RULE_TREE_MINIMUM_WIDTH = 100;

    /**
     * Minimum height of the rule tree component.
     */
    static private final int RULE_TREE_MINIMUM_HEIGHT = 500;

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
    static private final Dimension GRAPH_VIEW_PREFERRED_SIZE =
        new Dimension(GRAPH_VIEW_PREFERRED_WIDTH, GRAPH_VIEW_PREFERRED_HEIGHT);

    /** Flag controlling if a report should be printed after quitting. */
    private static final boolean REPORT = false;

    private static Collection<GraphState> visualize; // = new
    // HashSet<GraphState>();
}
