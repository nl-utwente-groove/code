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
import static groove.gui.Options.SHOW_UNFILTERED_EDGES_OPTION;
import static groove.gui.Options.SHOW_VALUE_NODES_OPTION;
import static groove.gui.Options.START_SIMULATION_OPTION;
import static groove.gui.Options.STOP_SIMULATION_OPTION;
import static groove.gui.Options.VERIFY_ALL_STATES_OPTION;
import static groove.io.FileType.GRAMMAR_FILTER;
import groove.graph.Element;
import groove.graph.GraphRole;
import groove.gui.SimulatorModel.Change;
import groove.gui.action.AboutAction;
import groove.gui.action.ApplyTransitionAction;
import groove.gui.action.CheckCTLAction;
import groove.gui.action.CopyHostAction;
import groove.gui.action.CopyRuleAction;
import groove.gui.action.DeleteHostAction;
import groove.gui.action.DeleteRuleAction;
import groove.gui.action.EditHostOrStateAction;
import groove.gui.action.EditRuleAction;
import groove.gui.action.EditRulePropertiesAction;
import groove.gui.action.EditSystemPropertiesAction;
import groove.gui.action.EnableRuleAction;
import groove.gui.action.ExplorationDialogAction;
import groove.gui.action.ExplorationStatsDialogAction;
import groove.gui.action.ExploreAction;
import groove.gui.action.GotoStartStateAction;
import groove.gui.action.ImportAction;
import groove.gui.action.LoadGrammarAction;
import groove.gui.action.LoadGrammarFromHistoryAction;
import groove.gui.action.LoadGrammarFromURLAction;
import groove.gui.action.LoadStartGraphAction;
import groove.gui.action.NewGrammarAction;
import groove.gui.action.NewHostAction;
import groove.gui.action.NewRuleAction;
import groove.gui.action.QuitAction;
import groove.gui.action.RedoSimulatorAction;
import groove.gui.action.RefreshGrammarAction;
import groove.gui.action.RelabelGrammarAction;
import groove.gui.action.RenameHostAction;
import groove.gui.action.RenameRuleAction;
import groove.gui.action.RenumberGrammarAction;
import groove.gui.action.SaveGrammarAction;
import groove.gui.action.SaveHostAction;
import groove.gui.action.SaveLTSAsAction;
import groove.gui.action.SaveSimulatorAction;
import groove.gui.action.SelectColorAction;
import groove.gui.action.SetStartGraphAction;
import groove.gui.action.SimulatorAction;
import groove.gui.action.StartSimulationAction;
import groove.gui.action.ToggleExplorationStateAction;
import groove.gui.action.UndoSimulatorAction;
import groove.gui.dialog.ErrorDialog;
import groove.gui.jgraph.AspectJGraph;
import groove.gui.jgraph.GraphJGraph;
import groove.trans.SystemProperties;
import groove.util.Groove;
import groove.util.Pair;
import groove.view.FormatError;
import groove.view.GraphView;
import groove.view.StoredGrammarView;
import groove.view.aspect.AspectGraph;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Box;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
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
import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import apple.dts.samplecode.osxadapter.OSXAdapter;

/**
 * Program that applies a production system to an initial graph.
 * @author Arend Rensink
 * @version $Revision$
 */
public class Simulator implements SimulatorListener {
    /**
     * Constructs a simulator with an empty graph grammar.
     */
    public Simulator() {
        this.model = new SimulatorModel();
        this.model.setUndoManager(getUndoManager());
        this.model.addListener(this);
        getFrame();
        refreshActions();
    }

    /**
     * Constructs a simulator using all production rules in a given directory.
     * All known graph grammar format loaders are polled to find one that can
     * load the grammar.
     * @param grammarLocation the location (file or directory) containing the
     *        grammar; if <tt>null</tt>, no grammar is loaded.
     */
    public Simulator(String grammarLocation) {
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
    public Simulator(final String grammarLocation, final String startGraphName) {
        this();
        if (grammarLocation != null) {
            final File location =
                new File(GRAMMAR_FILTER.addExtension(grammarLocation)).getAbsoluteFile();
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    try {
                        getLoadGrammarAction().load(location, startGraphName);
                    } catch (IOException exc) {
                        showErrorDialog(exc.getMessage(), exc);
                    }
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
    public void handleEditGraph(final AspectGraph graph, boolean fresh) {
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
        result.start(graph);
        return result;
    }

    /** Changes the type graph in the open editor panels. */
    void changeEditorTypes() {
        for (EditorPanel editor : getSimulatorPanel().getEditors()) {
            editor.setType();
        }
    }

    /** 
     * Attempts to disposes the editor for certain aspect graphs, if any.
     * This is done in response to a change in the graph outside the editor.
     * @param graphs the graphs that are about to be changed and whose editor 
     * therefore needs to be disposed
     * @return {@code true} if the operation was not cancelled
     */
    public boolean disposeEditors(AspectGraph... graphs) {
        Set<Pair<GraphRole,String>> graphSet =
            new HashSet<Pair<GraphRole,String>>();
        for (AspectGraph graph : graphs) {
            graphSet.add(Pair.newPair(graph.getRole(), graph.getName()));
        }
        boolean result = true;
        for (EditorPanel editor : getSimulatorPanel().getEditors()) {
            AspectGraph graph = editor.getGraph();
            if (graphSet.contains(Pair.newPair(graph.getRole(), graph.getName()))) {
                if (editor.askAndSave()) {
                    editor.dispose();
                } else {
                    result = false;
                    break;
                }
            }
        }
        return result;
    }

    /** 
     * Attempts to save the dirty editors, asking the user what should happen.
     * Optionally disposes the editors.
     * @param dispose if {@code true}, all editors are disposed (unless 
     * the operation was cancelled)
     * @return {@code true} if the operation was not cancelled
     */
    public boolean saveEditors(boolean dispose) {
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

    @Override
    public void update(SimulatorModel source, SimulatorModel oldModel,
            Set<Change> changes) {
        if (changes.contains(Change.GRAMMAR)) {
            changeEditorTypes();
            setTitle();
            List<FormatError> grammarErrors =
                getModel().getGrammar().getErrors();
            setErrors(grammarErrors);
            this.history.updateLoadGrammar();
            if (source.getGrammar() != oldModel.getGrammar()) {
                getUndoManager().discardAllEdits();
            }
        }
        refreshActions();
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
     * Sets a new graph transition system.
     */
    public synchronized boolean startSimulation() {
        boolean result = false;
        boolean start =
            !isEditorDirty() && confirmBehaviourOption(START_SIMULATION_OPTION);
        if (start) {
            result = getModel().setGts();
        }
        return result;
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
            if (Groove.IS_PLATFORM_MAC) {
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
                    getQuitAction().execute();
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
    public SimulatorPanel getSimulatorPanel() {
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
        result.add(getNewHostAction());
        result.add(getEditHostOrStateAction());
        result.addSeparator();
        result.add(getCopyGraphAction());
        result.add(getDeleteHostAction());
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
                                getModel().setRule(name);
                                break;
                            case HOST:
                                panel = getStatePanel();
                                getModel().setHost(name);
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
                            getModel().setControl(error.getControl().getName());
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
     * @see #switchTabs(Component)
     */
    public StatePanel getStatePanel() {
        if (this.statePanel == null) {
            // panel for state display
            this.statePanel = new StatePanel(this);
            this.statePanel.setPreferredSize(GRAPH_VIEW_PREFERRED_SIZE);
        }
        return this.statePanel;
    }

    /** Returns the panel containing the control program. */
    public ControlPanel getControlPanel() {
        if (this.controlPanel == null) {
            this.controlPanel = new ControlPanel(this);
            this.controlPanel.setPreferredSize(GRAPH_VIEW_PREFERRED_SIZE);
        }
        return this.controlPanel;
    }

    /**
     * Returns the simulator panel on which the currently selected production
     * rule is displayed. Note that this panel may currently not be visible.
     * @see #switchTabs(Component)
     */
    public RulePanel getRulePanel() {
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
     * @see #switchTabs(Component)
     */
    public LTSPanel getLtsPanel() {
        if (this.ltsPanel == null) {
            this.ltsPanel = new LTSPanel(this);
            this.ltsPanel.setPreferredSize(GRAPH_VIEW_PREFERRED_SIZE);
        }
        return this.ltsPanel;
    }

    /**
     * Returns the simulator panel on which the current state is displayed. Note
     * that this panel may currently not be visible.
     * @see #switchTabs(Component)
     */
    public TypePanel getTypePanel() {
        if (this.typePanel == null) {
            // panel for state display
            this.typePanel = new TypePanel(this);
            this.typePanel.setPreferredSize(GRAPH_VIEW_PREFERRED_SIZE);
        }
        return this.typePanel;
    }

    /**
     * Returns the prolog panel.
     * @see #switchTabs(Component)
     */
    public PrologPanel getPrologPanel() {
        if (this.prologPanel == null) {
            this.prologPanel = new PrologPanel(this);
            this.prologPanel.setPreferredSize(GRAPH_VIEW_PREFERRED_SIZE);
        }
        return this.prologPanel;
    }

    /** Returns the list of states and host graphs. */
    public StateJList getStateList() {
        if (this.stateJList == null) {
            this.stateJList = new StateJList(this);
        }
        return this.stateJList;
    }

    /**
     * Resets the state panel, in preparation for a switch between
     * concrete and abstract state space exploration.
     */
    public void resetStatePanel() {
        getModel().removeListener(getStatePanel());
        this.statePanel = null;
        getSimulatorPanel().setComponentAt(0, getStatePanel());
    }

    /**
     * Resets the rule tree, in preparation for a switch between
     * concrete and abstract state space exploration.
     */
    public void resetRuleTree() {
        getModel().removeListener(getRuleTree());
        this.ruleJTree = null;
        this.ruleTreePanel.setViewportView(getRuleTree());
    }

    /**
     * Returns the tree of rules and matches displayed in the simulator.
     */
    public RuleJTree getRuleTree() {
        if (this.ruleJTree == null) {
            this.ruleJTree = new RuleJTree(this);
        }
        return this.ruleJTree;
    }

    /** Returns the currently selected simulator panel. */
    public Component getPanel() {
        return getSimulatorPanel().getSelectedComponent();
    }

    /**
     * Returns the currently selected graph view component. This can be the
     * state, rule or LTS view. In case the LTS is active, the inner LTSPanel is
     * returned instead of the outer ConditionalLTSPanel.
     * @see #getStatePanel()
     * @see #getRulePanel()
     * @see #getLtsPanel()
     * @see #switchTabs(Component)
     */
    public JGraphPanel<?> getGraphPanel() {
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
     * @return {@code true} if now the selected component equals the parameter
     * @see #getStatePanel()
     * @see #getRulePanel()
     * @see #getLtsPanel()
     * @see #getGraphPanel()
     */
    public boolean switchTabs(Component component) {
        boolean result = getSimulatorPanel().indexOfComponent(component) >= 0;
        if (getSimulatorPanel().getSelectedComponent() != component && result) {
            this.switchingTabs = true;
            getSimulatorPanel().setSelectedComponent(component);
            this.switchingTabs = false;
        }
        return result;
    }

    /**
     * Indicates that the simulator is processing a 
     * {@link #switchTabs(Component)}.
     * This may affect the newly selected component's behaviour. 
     */
    public boolean isSwitchingTabs() {
        return this.switchingTabs;
    }

    /**
     * Adds an element to the set of refreshables. Also calls
     * {@link Refreshable#refresh()} on the element.
     */
    public void addRefreshable(Refreshable element) {
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
    public void addAccelerator(Action action) {
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

        result.addSeparator();

        result.add(new JMenuItem(getLoadGrammarAction()));
        result.add(new JMenuItem(getLoadGrammarFromURLAction()));
        result.add(createOpenRecentMenu());

        result.addSeparator();

        result.add(new JMenuItem(getLoadStartGraphAction()));

        result.addSeparator();

        result.add(new JMenuItem(getSaveGrammarAction()));
        result.add(new JMenuItem(getSaveGraphAction()));

        result.addSeparator();

        result.add(new JMenuItem(getImportAction()));
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

        result.add(getNewHostAction());
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
    public JMenuItem getEditMenuItem() {
        if (this.editGraphItem == null) {
            this.editGraphItem = new JMenuItem();
            // load the graph edit action as default
            this.editGraphItem.setAction(getEditHostOrStateAction());
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
    public JMenuItem getCopyMenuItem() {
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
    public JMenuItem getDeleteMenuItem() {
        if (this.deleteGraphItem == null) {
            this.deleteGraphItem = new JMenuItem();
            // load the graph delete action as default
            this.deleteGraphItem.setAction(getDeleteHostAction());
        }
        return this.deleteGraphItem;
    }

    /**
     * Returns the menu item in the edit menu that specifies delete the
     * currently displayed graph or rule.
     */
    public JMenuItem getRenameMenuItem() {
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
                    add(jGraph.createShowHideMenu());
                    add(jGraph.createZoomMenu());
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
    private JMenu createOptionsMenu() {
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

        result.add(this.getExploreAction());
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
        JMenu mcScenarioMenu = new ModelCheckingMenu(this);
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
     * Sets the title of the frame to a given title.
     */
    public void setTitle() {
        StringBuffer title = new StringBuffer();
        StoredGrammarView grammar = getModel().getGrammar();
        if (grammar != null && grammar.getName() != null) {
            title.append(grammar.getName());
            GraphView startGraph = grammar.getStartGraphView();
            if (startGraph != null) {
                title.append(TITLE_NAME_SEPARATOR);
                title.append(startGraph.getName());
            }
            if (grammar.isUseControl()) {
                title.append(" | ");
                title.append(grammar.getControlName());

            }
            if (!grammar.getStore().isModifiable()) {
                title.append(" (read-only)");
            }
            title.append(" - ");

        }
        title.append(APPLICATION_NAME);
        getFrame().setTitle(title.toString());
    }

    /**
     * Checks if a given option is confirmed.
     */
    boolean confirmBehaviourOption(String option) {
        BehaviourOption menu = (BehaviourOption) getOptions().getItem(option);
        return menu.confirm(getFrame(), null);
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

    /** Returns the object holding the internal state of the simulator. */
    public final SimulatorModel getModel() {
        return this.model;
    }

    /**
     * The options object of this simulator.
     */
    private Options options;

    /** the internal state of the simulator. */
    private final SimulatorModel model;

    /** Flag to indicate that a {@link #switchTabs(Component)} request is underway. */
    private boolean switchingTabs;

    /** Current set of refreshables of this simulator. */
    private final List<Refreshable> refreshables = new ArrayList<Refreshable>();
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
    public final UndoManager getUndoManager() {
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
     * Returns the transition application action permanently associated with
     * this simulator.
     */
    public ApplyTransitionAction getApplyTransitionAction() {
        if (this.applyTransitionAction == null) {
            this.applyTransitionAction = new ApplyTransitionAction(this);
        }
        return this.applyTransitionAction;
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
     * Returns the CTL formula providing action permanently associated with this
     * simulator.
     * @param full if {@code true}, the action first generates the full state
     * space.
     */
    public Action getCheckCTLAction(boolean full) {
        CheckCTLAction result =
            full ? this.checkCTLFreshAction : this.checkCTLAsIsAction;
        if (result == null) {
            result = new CheckCTLAction(this, full);
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
     * Returns the graph copying action permanently associated with this
     * simulator.
     */
    public CopyHostAction getCopyGraphAction() {
        // lazily create the action
        if (this.copyGraphAction == null) {
            this.copyGraphAction = new CopyHostAction(this);
        }
        return this.copyGraphAction;
    }

    /**
     * The graph copying action permanently associated with this simulator.
     */
    private CopyHostAction copyGraphAction;

    /**
     * Returns the rule copying action permanently associated with this
     * simulator.
     */
    public CopyRuleAction getCopyRuleAction() {
        // lazily create the action
        if (this.copyRuleAction == null) {
            this.copyRuleAction = new CopyRuleAction(this);
        }
        return this.copyRuleAction;
    }

    /**
     * The rule copying action permanently associated with this simulator.
     */
    private CopyRuleAction copyRuleAction;

    /**
     * Returns the graph deletion action permanently associated with this
     * simulator.
     */
    public DeleteHostAction getDeleteHostAction() {
        // lazily create the action
        if (this.deleteHostAction == null) {
            this.deleteHostAction = new DeleteHostAction(this);
        }
        return this.deleteHostAction;
    }

    /**
     * The graph deletion action permanently associated with this simulator.
     */
    private DeleteHostAction deleteHostAction;

    /**
     * Returns the rule deletion action permanently associated with this
     * simulator.
     */
    public DeleteRuleAction getDeleteRuleAction() {
        // lazily create the action
        if (this.deleteRuleAction == null) {
            this.deleteRuleAction = new DeleteRuleAction(this);
        }
        return this.deleteRuleAction;
    }

    /**
     * The rule deletion action permanently associated with this simulator.
     */
    private DeleteRuleAction deleteRuleAction;

    /**
     * Lazily creates and returns the state edit action permanently associated
     * with this simulator.
     */
    public EditHostOrStateAction getEditHostOrStateAction() {
        // lazily create the action
        if (this.editHostOrStateAction == null) {
            this.editHostOrStateAction = new EditHostOrStateAction(this);
        }
        return this.editHostOrStateAction;
    }

    /**
     * The state edit action permanently associated with this simulator.
     */
    private EditHostOrStateAction editHostOrStateAction;

    /**
     * Returns the properties edit action permanently associated with this
     * simulator.
     */
    public EditRulePropertiesAction getEditRulePropertiesAction() {
        // lazily create the action
        if (this.editRulePropertiesAction == null) {
            this.editRulePropertiesAction = new EditRulePropertiesAction(this);
        }
        return this.editRulePropertiesAction;
    }

    /**
     * The rule properties edit action permanently associated with this
     * simulator.
     */
    private EditRulePropertiesAction editRulePropertiesAction;

    /**
     * Lazily creates and returns the rule edit action permanently associated
     * with this simulator.
     */
    public EditRuleAction getEditRuleAction() {
        // lazily create the action
        if (this.editRuleAction == null) {
            this.editRuleAction = new EditRuleAction(this);
        }
        return this.editRuleAction;
    }

    /**
     * The rule edit action permanently associated with this simulator.
     */
    private EditRuleAction editRuleAction;

    /** Returns the action to show the system properties of the current grammar. */
    public Action getEditSystemPropertiesAction() {
        // lazily create the action
        if (this.editSystemPropertiesAction == null) {
            this.editSystemPropertiesAction =
                new EditSystemPropertiesAction(this);
        }
        return this.editSystemPropertiesAction;
    }

    /**
     * The action to show the system properties of the currently selected
     * grammar.
     */
    private EditSystemPropertiesAction editSystemPropertiesAction;

    /**
     * Lazily creates and returns the type edit action permanently associated
     * with this simulator.
     */
    public Action getEditTypeAction() {
        return getTypePanel().getEditTypeAction();
    }

    /**
     * Returns the rule enabling action permanently associated with this
     * simulator.
     */
    public EnableRuleAction getEnableRuleAction() {
        // lazily create the action
        if (this.enableRuleAction == null) {
            this.enableRuleAction = new EnableRuleAction(this);
        }
        return this.enableRuleAction;
    }

    /**
     * The rule enabling action permanently associated with this simulator.
     */
    private EnableRuleAction enableRuleAction;

    /**
     * Returns the 'default exploration' action that is associated with the
     * simulator.
     */
    public ExploreAction getExploreAction() {
        // lazily create the action
        if (this.exploreAction == null) {
            this.exploreAction = new ExploreAction(this);
        }

        return this.exploreAction;
    }

    /**
     * The 'default exploration' action (variable).
     */
    private ExploreAction exploreAction;

    /**
     * Returns the exploration dialog action permanently associated with this
     * simulator.
     */
    public ExplorationDialogAction getExplorationDialogAction() {
        // lazily create the action
        if (this.explorationDialogAction == null) {
            this.explorationDialogAction = new ExplorationDialogAction(this);
        }
        return this.explorationDialogAction;
    }

    /**
     * The exploration dialog action permanently associated with this simulator.
     */
    private ExplorationDialogAction explorationDialogAction;

    /**
     * Returns the exploration statistics dialog action permanently associated
     * with this simulator.
     */
    public ExplorationStatsDialogAction getExplorationStatsDialogAction() {
        // lazily create the action
        if (this.explorationStatsDialogAction == null) {
            this.explorationStatsDialogAction =
                new ExplorationStatsDialogAction(this);
        }
        return this.explorationStatsDialogAction;
    }

    /**
     * The exploration statistics dialog action permanently associated with
     * this simulator.
     */
    private ExplorationStatsDialogAction explorationStatsDialogAction;

    /**
     * Returns the Save LTS As action permanently associated with this simulator.
     */
    public SaveLTSAsAction getSaveLTSAsAction() {
        // lazily create the action
        if (this.saveLtsAsAction == null) {
            this.saveLtsAsAction = new SaveLTSAsAction(this);
        }
        return this.saveLtsAsAction;
    }

    /** The LTS Save As action permanently associated with this simulator. */
    private SaveLTSAsAction saveLtsAsAction;

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
            this.gotoStartStateAction = new GotoStartStateAction(this);
        }
        return this.gotoStartStateAction;
    }

    /**
     * The go-to start state action permanently associated with this simulator.
     */
    private GotoStartStateAction gotoStartStateAction;

    /**
     * Returns the start graph load action permanently associated with this
     * simulator.
     */
    public LoadStartGraphAction getLoadStartGraphAction() {
        // lazily create the action
        if (this.loadStartGraphAction == null) {
            this.loadStartGraphAction = new LoadStartGraphAction(this);
        }
        return this.loadStartGraphAction;
    }

    /** The start state load action permanently associated with this simulator. */
    private LoadStartGraphAction loadStartGraphAction;

    /** Returns the import action permanently associated with this simulator. */
    public ImportAction getImportAction() {
        // lazily create the action
        if (this.importAction == null) {
            this.importAction = new ImportAction(this);
        }
        return this.importAction;
    }

    /** The import action permanently associated with this simulator. */
    private ImportAction importAction;

    /**
     * Returns the grammar load action permanently associated with this
     * simulator.
     */
    public LoadGrammarAction getLoadGrammarAction() {
        // lazily create the action
        if (this.loadGrammarAction == null) {
            this.loadGrammarAction = new LoadGrammarAction(this);
        }
        return this.loadGrammarAction;
    }

    /** The grammar load action permanently associated with this simulator. */
    private LoadGrammarAction loadGrammarAction;

    /**
     * Returns the grammar load action permanently associated with this
     * simulator.
     */
    public Action getLoadGrammarFromURLAction() {
        // lazily create the action
        if (this.loadGrammarFromURLAction == null) {
            this.loadGrammarFromURLAction = new LoadGrammarFromURLAction(this);
        }
        return this.loadGrammarFromURLAction;
    }

    /** The grammar load action permanently associated with this simulator. */
    private LoadGrammarFromURLAction loadGrammarFromURLAction;

    /**
     * Returns the rule system creation action permanently associated with this
     * simulator.
     */
    public NewGrammarAction getNewGrammarAction() {
        // lazily create the action
        if (this.newGrammarAction == null) {
            this.newGrammarAction = new NewGrammarAction(this);
        }
        return this.newGrammarAction;
    }

    /**
     * The rule system creation action permanently associated with this
     * simulator.
     */
    private NewGrammarAction newGrammarAction;

    /**
     * Returns the graph creation action permanently associated with this
     * simulator.
     */
    public NewHostAction getNewHostAction() {
        // lazily create the action
        if (this.newHostAction == null) {
            this.newHostAction = new NewHostAction(this);
        }
        return this.newHostAction;
    }

    /**
     * The graph creation action permanently associated with this simulator.
     */
    private NewHostAction newHostAction;

    /**
     * Returns the rule creation action permanently associated with this
     * simulator.
     */
    public NewRuleAction getNewRuleAction() {
        // lazily create the action
        if (this.newRuleAction == null) {
            this.newRuleAction = new NewRuleAction(this);
        }
        return this.newRuleAction;
    }

    /**
     * The rule creation action permanently associated with this simulator.
     */
    private NewRuleAction newRuleAction;

    /**
     * Returns the rule creation action permanently associated with this
     * simulator.
     */
    public Action getNewTypeAction() {
        return getTypePanel().getNewTypeAction();
    }

    /** Returns the quit action permanently associated with this simulator. */
    public SimulatorAction getQuitAction() {
        // lazily create the action
        if (this.quitAction == null) {
            this.quitAction = new QuitAction(this);
        }
        return this.quitAction;
    }

    /**
     * The quit action permanently associated with this simulator.
     */
    private QuitAction quitAction;

    /**
     * Returns the redo action permanently associated with this simulator.
     */
    public RedoSimulatorAction getRedoAction() {
        if (this.redoAction == null) {
            this.redoAction = new RedoSimulatorAction(this);
        }
        return this.redoAction;
    }

    /**
     * The redo permanently associated with this simulator.
     */
    private RedoSimulatorAction redoAction;

    /**
     * Returns the grammar refresh action permanently associated with this
     * simulator.
     */
    public RefreshGrammarAction getRefreshGrammarAction() {
        // lazily create the action
        if (this.refreshGrammarAction == null) {
            this.refreshGrammarAction = new RefreshGrammarAction(this);
        }
        return this.refreshGrammarAction;
    }

    /** The grammar refresh action permanently associated with this simulator. */
    private RefreshGrammarAction refreshGrammarAction;

    /**
     * Returns the renumbering action permanently associated with this
     * simulator.
     */
    public RenumberGrammarAction getRenumberAction() {
        // lazily create the action
        if (this.renumberAction == null) {
            this.renumberAction = new RenumberGrammarAction(this);
        }
        return this.renumberAction;
    }

    /**
     * The renumbering action permanently associated with this simulator.
     */
    private RenumberGrammarAction renumberAction;

    /**
     * Returns the rule renaming action permanently associated with this
     * simulator.
     */
    public RelabelGrammarAction getRelabelAction() {
        // lazily create the action
        if (this.relabelAction == null) {
            this.relabelAction = new RelabelGrammarAction(this);
        }
        return this.relabelAction;
    }

    /**
     * The graph renaming action permanently associated with this simulator.
     */
    private RelabelGrammarAction relabelAction;

    /**
     * Returns the rule renaming action permanently associated with this
     * simulator.
     */
    public RenameHostAction getRenameGraphAction() {
        // lazily create the action
        if (this.renameGraphAction == null) {
            this.renameGraphAction = new RenameHostAction(this);
        }
        return this.renameGraphAction;
    }

    /**
     * The graph renaming action permanently associated with this simulator.
     */
    private RenameHostAction renameGraphAction;

    /**
     * Returns the rule renaming action permanently associated with this
     * simulator.
     */
    public RenameRuleAction getRenameRuleAction() {
        // lazily create the action
        if (this.renameRuleAction == null) {
            this.renameRuleAction = new RenameRuleAction(this);
        }
        return this.renameRuleAction;
    }

    /**
     * The rule renaming action permanently associated with this simulator.
     */
    private RenameRuleAction renameRuleAction;

    /**
     * Returns the graph save action permanently associated with this simulator.
     */
    public SaveGrammarAction getSaveGrammarAction() {
        // lazily create the action
        if (this.saveGrammarAction == null) {
            this.saveGrammarAction = new SaveGrammarAction(this);
        }
        return this.saveGrammarAction;
    }

    /**
     * The grammar save action permanently associated with this simulator.
     */
    private SaveGrammarAction saveGrammarAction;

    /**
     * Returns the graph save action permanently associated with this simulator.
     */
    public SaveSimulatorAction getSaveGraphAction() {
        // lazily create the action
        if (this.saveGraphAction == null) {
            this.saveGraphAction = new SaveSimulatorAction(this);
        }
        return this.saveGraphAction;
    }

    /**
     * The state save action permanently associated with this simulator.
     */
    private SaveSimulatorAction saveGraphAction;

    /**
     * Returns the host graph save action permanently associated with this simulator.
     */
    public SaveHostAction getSaveHostGraphAction() {
        // lazily create the action
        if (this.saveHostGraphAction == null) {
            this.saveHostGraphAction = new SaveHostAction(this);
        }
        return this.saveHostGraphAction;
    }

    /**
     * The host graph save action permanently associated with this simulator.
     */
    private SaveHostAction saveHostGraphAction;

    /**
     * Returns the undo action permanently associated with this simulator.
     */
    public Action getSelectColorAction() {
        if (this.selectColorAction == null) {
            this.selectColorAction = new SelectColorAction(this);
        }
        return this.selectColorAction;
    }

    /**
     * The undo action permanently associated with this simulator.
     */
    private SelectColorAction selectColorAction;

    /**
     * Lazily creates and returns an instance of SetStartGraphAction.
     */
    public Action getSetStartGraphAction() {
        // lazily create the action
        if (this.setStartGraphAction == null) {
            this.setStartGraphAction = new SetStartGraphAction(this);
        }
        return this.setStartGraphAction;
    }

    /** Singleton instance of {@link SetStartGraphAction}. */
    private SetStartGraphAction setStartGraphAction;

    /**
     * Lazily creates and returns an instance of
     * {@link StartSimulationAction}.
     */
    public SimulatorAction getStartSimulationAction() {
        // lazily create the action
        if (this.startSimulationAction == null) {
            this.startSimulationAction = new StartSimulationAction(this);
        }
        return this.startSimulationAction;
    }

    /** The action to start a new simulation. */
    private StartSimulationAction startSimulationAction;

    /**
     * Lazily creates and returns an instance of
     * {@link ToggleExplorationStateAction}.
     */
    public Action getToggleExplorationStateAction() {
        if (this.toggleExplorationStateAction == null) {
            this.toggleExplorationStateAction =
                new ToggleExplorationStateAction(this);
        }
        return this.toggleExplorationStateAction;
    }

    /** The action to toggle between concrete and abstract exploration. */
    private ToggleExplorationStateAction toggleExplorationStateAction;

    /**
     * Returns the undo action permanently associated with this simulator.
     */
    public UndoSimulatorAction getUndoAction() {
        if (this.undoAction == null) {
            this.undoAction = new UndoSimulatorAction(this);
        }
        return this.undoAction;
    }

    /**
     * The undo action permanently associated with this simulator.
     */
    private UndoSimulatorAction undoAction;

    /** Class wrapping a menu of recently opened files. */
    private class History {

        /** Constructs a fresh history instance. */
        public History() {
            String[] savedLocations =
                Options.userPrefs.get(SystemProperties.HISTORY_KEY, "").split(
                    ",");
            for (String location : savedLocations) {
                try {
                    this.history.add(new LoadGrammarFromHistoryAction(
                        Simulator.this, location, null));
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
                Object location = getModel().getStore().getLocation();
                String startGraphName =
                    getModel().getGrammar().getStartGraphName();
                LoadGrammarFromHistoryAction newAction =
                    new LoadGrammarFromHistoryAction(Simulator.this,
                        location.toString(), startGraphName);
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
            for (LoadGrammarFromHistoryAction action : this.history) {
                this.menu.add(action);
            }
        }

        private String makeHistoryString() {
            StringBuilder result = new StringBuilder();
            for (LoadGrammarFromHistoryAction action : this.history) {
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
        private final ArrayList<LoadGrammarFromHistoryAction> history =
            new ArrayList<LoadGrammarFromHistoryAction>();
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
    public static final String LTS_FILE_NAME = "lts";

    /**
     * Default name of an empty rule.
     */
    public static final String NEW_GRAMMAR_NAME = "newGrammar";

    /**
     * Default name of an empty rule.
     */
    public static final String NEW_GRAPH_NAME = "newGraph";

    /**
     * Default name of an empty rule.
     */
    public static final String NEW_RULE_NAME = "newRule";

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

}
