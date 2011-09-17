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
import groove.gui.Display.Tab;
import groove.gui.SimulatorModel.Change;
import groove.gui.action.AboutAction;
import groove.gui.action.ActionStore;
import groove.gui.dialog.ErrorDialog;
import groove.gui.jgraph.AspectJGraph;
import groove.gui.jgraph.GraphJGraph;
import groove.trans.ResourceKind;
import groove.util.Groove;
import groove.view.FormatError;
import groove.view.GrammarModel;
import groove.view.HostModel;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.WindowConstants;
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
        this.actions = new ActionStore(this);
        this.model.addListener(this, Change.GRAMMAR, Change.DISPLAY);
        this.undoManager = new SimulatorUndoManager(this);
        getFrame();
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
                        Simulator.this.actions.getLoadGrammarAction().load(
                            location, startGraphName);
                    } catch (IOException exc) {
                        new ErrorDialog(getFrame(), exc.getMessage(), exc).setVisible(true);
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
        getActions().refreshActions();
        refreshMenuItems();
        getFrame().pack();
        groove.gui.UserSettings.applyUserSettings(this.frame);
        getFrame().setVisible(true);
    }

    /** Returns the store of actions for this simulator. */
    public ActionStore getActions() {
        return this.actions;
    }

    /** Returns (after lazily creating) the undo history for this simulator. */
    public StepHistory getSimulationHistory() {
        if (this.stepHistory == null) {
            this.stepHistory = new StepHistory(this);
        }
        return this.stepHistory;
    }

    @Override
    public void update(SimulatorModel source, SimulatorModel oldModel,
            Set<Change> changes) {
        if (changes.contains(Change.GRAMMAR)) {
            setTitle();
            List<FormatError> grammarErrors =
                getModel().getGrammar().getErrors();
            setErrors(grammarErrors);
        }
        if (changes.contains(Change.DISPLAY)) {
            refreshMenuItems();
        }
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
     * Execute the quit action as a method of the Simulator class.
     * Needed for Command-Q shortcut on MacOS only (see {@link #getFrame}).
     */
    public void tryQuit() {
        this.getActions().getQuitAction().execute();
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
                        this.getClass().getDeclaredMethod("tryQuit"));
                } catch (NoSuchMethodException e1) {
                    // should not happen (thrown when 'tryQuit' does not exist)
                    e1.printStackTrace();
                }
            }
            // register doQuit() as the closing method of the window
            this.frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    Simulator.this.actions.getQuitAction().execute();
                }
            });
            this.frame.setJMenuBar(createMenuBar());

            JSplitPane leftPanel =
                new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                    getDisplaysPanel().getUpperListPanel(),
                    getDisplaysPanel().getLowerListsPanel());
            // make sure tool tips get displayed
            ToolTipManager.sharedInstance().registerComponent(leftPanel);

            JComponent rightPanel = getDisplaysPanel();

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
    public DisplaysPanel getDisplaysPanel() {
        if (this.simulatorPanel == null) {
            this.simulatorPanel = new DisplaysPanel(this);
        }
        return this.simulatorPanel;
    }

    private ErrorListPanel getErrorPanel() {
        if (this.errorPanel == null) {
            final ErrorListPanel result =
                this.errorPanel = new ErrorListPanel("Errors in grammar");
            result.addSelectionListener(createErrorListener());
        }
        return this.errorPanel;
    }

    /**
     * Creates an observer for the error panel that will select the
     * erroneous part of the resource upon selection of an error.
     */
    private Observer createErrorListener() {
        return new Observer() {
            @Override
            public void update(Observable observable, Object arg) {
                if (arg != null) {
                    FormatError error = (FormatError) arg;
                    ResourceKind resource = error.getResourceKind();
                    String name = error.getResourceName();
                    if (resource != null) {
                        getModel().doSelect(resource, name);
                        Tab resourceTab =
                            getDisplaysPanel().getDisplayFor(resource).getSelectedTab();
                        if (resource.isGraphBased()) {
                            AspectJGraph jGraph;
                            if (resourceTab.isEditor()) {
                                jGraph =
                                    ((GraphEditorTab) resourceTab).getJGraph();
                            } else {
                                jGraph = ((GraphTab) resourceTab).getJGraph();
                            }
                            // select the error cell and switch to the panel
                            for (Element errorObject : error.getElements()) {
                                if (jGraph.selectJCell(errorObject)) {
                                    break;
                                }
                            }
                        } else if (error.getNumbers().size() > 1) {
                            int line = error.getNumbers().get(0);
                            int column = error.getNumbers().get(1);
                            ((TextTab) resourceTab).select(line, column);
                        }
                    }
                }
            }
        };
    }

    /** Error display. */
    private ErrorListPanel errorPanel;

    /** Refreshes some of the menu item by assigning the right action. */
    private void refreshMenuItems() {
        DisplayKind displayKind = getModel().getDisplay();
        for (RefreshableMenuItem item : getRefreshableMenuItems()) {
            item.refresh(displayKind);
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

        result.add(new JMenuItem(this.actions.getNewGrammarAction()));

        result.addSeparator();

        result.add(new JMenuItem(this.actions.getLoadGrammarAction()));
        result.add(new JMenuItem(this.actions.getLoadGrammarFromURLAction()));
        result.add(createOpenRecentMenu());

        result.addSeparator();

        result.add(new JMenuItem(this.actions.getLoadStartGraphAction()));

        result.addSeparator();

        result.add(new JMenuItem(this.actions.getSaveGrammarAction()));
        result.add(getSaveAsMenuItem());

        result.addSeparator();

        result.add(new JMenuItem(this.actions.getImportAction()));
        result.add(getExportMenuItem());

        result.addSeparator();

        result.add(new JMenuItem(this.actions.getRefreshGrammarAction()));

        result.addSeparator();

        result.add(new JMenuItem(this.actions.getQuitAction()));

        return result;
    }

    private JMenu createOpenRecentMenu() {
        if (this.history == null) {
            this.history = new SimulatorHistory(this);
        }
        return this.history.getOpenRecentMenu();
    }

    /**
     * Creates and returns an edit menu for the menu bar.
     */
    private JMenu createEditMenu() {
        JMenu result = new JMenu(Options.EDIT_MENU_NAME);

        result.setMnemonic(Options.EDIT_MENU_MNEMONIC);
        result.add(this.actions.getUndoAction());
        result.add(this.actions.getRedoAction());

        result.addSeparator();

        JMenu newMenu = new JMenu(Options.NEW_MENU_NAME);
        for (ResourceKind resource : EnumSet.allOf(ResourceKind.class)) {
            if (resource != ResourceKind.PROPERTIES) {
                newMenu.add(this.actions.getNewAction(resource));
            }
        }
        result.add(newMenu);

        result.addSeparator();

        result.add(getEditMenuItem());
        result.add(getCopyMenuItem());
        result.add(getDeleteMenuItem());
        result.add(getRenameMenuItem());
        result.add(getEnableMenuItem());

        result.addSeparator();

        result.add(this.actions.getRelabelAction());
        result.add(this.actions.getRenumberAction());

        result.addSeparator();

        result.add(this.actions.getShiftPriorityAction(true));
        result.add(this.actions.getShiftPriorityAction(false));
        result.add(this.actions.getEditRulePropertiesAction());
        result.add(this.actions.getEditSystemPropertiesAction());

        return result;
    }

    private List<RefreshableMenuItem> getRefreshableMenuItems() {
        if (this.menuItems == null) {
            this.menuItems = new ArrayList<Simulator.RefreshableMenuItem>();
            this.menuItems.add(getEditMenuItem());
            this.menuItems.add(getCopyMenuItem());
            this.menuItems.add(getDeleteMenuItem());
            this.menuItems.add(getRenameMenuItem());
            this.menuItems.add(getEnableMenuItem());
            this.menuItems.add(getSaveAsMenuItem());
            this.menuItems.add(getExportMenuItem());
        }
        return this.menuItems;
    }

    /**
     * Returns the menu item in the edit menu that specifies editing the
     * currently displayed graph or rule.
     */
    private RefreshableMenuItem getEditMenuItem() {
        if (this.editGraphItem == null) {
            this.editGraphItem = new RefreshableMenuItem() {
                @Override
                protected void refresh(ResourceKind resource) {
                    setAction(getActions().getEditAction(resource));
                }
            };
            this.editGraphItem.setAccelerator(Options.EDIT_KEY);
        }
        return this.editGraphItem;
    }

    /** 
     * Creates a menu item that can be refreshed with a {@link DisplayKind}-
     * or {@link ResourceKind}-dependent action.
     */
    private abstract static class RefreshableMenuItem extends JMenuItem {
        /** Refreshes or disables the menu item, based on the
         * given {@link DisplayKind}.
         */
        protected void refresh(DisplayKind display) {
            ResourceKind resource = display.getResource();
            if (resource == null) {
                setEnabled(false);
            } else {
                refresh(resource);
            }
        }

        /** Refreshes or disables the menu item, based on a
         * given {@link DisplayKind}.
         */
        protected void refresh(ResourceKind resource) {
            // does noting
        }

        @Override
        public void setAction(Action a) {
            if (a == null) {
                setEnabled(false);
            } else {
                super.setAction(a);
            }
        }
    }

    /**
     * Returns the menu item in the edit menu that specifies copy the currently
     * displayed graph or rule.
     */
    private RefreshableMenuItem getCopyMenuItem() {
        if (this.copyGraphItem == null) {
            this.copyGraphItem = new RefreshableMenuItem() {
                @Override
                protected void refresh(ResourceKind resource) {
                    setAction(getActions().getCopyAction(resource));
                }
            };
        }
        return this.copyGraphItem;
    }

    /**
     * Returns the menu item in the edit menu that specifies delete the
     * currently displayed graph or rule.
     */
    private RefreshableMenuItem getDeleteMenuItem() {
        if (this.deleteGraphItem == null) {
            this.deleteGraphItem = new RefreshableMenuItem() {
                @Override
                protected void refresh(ResourceKind resource) {
                    setAction(getActions().getDeleteAction(resource));
                }
            };
        }
        return this.deleteGraphItem;
    }

    /**
     * Returns the menu item in the edit menu that specifies delete the
     * currently displayed graph or rule.
     */
    private RefreshableMenuItem getRenameMenuItem() {
        if (this.renameMenuItem == null) {
            this.renameMenuItem = new RefreshableMenuItem() {
                @Override
                protected void refresh(ResourceKind resource) {
                    setAction(getActions().getRenameAction(resource));
                }
            };
        }
        return this.renameMenuItem;
    }

    /**
     * Returns the menu item in the edit menu that specifies deletion of
     * the currently selected resource.
     */
    private RefreshableMenuItem getEnableMenuItem() {
        if (this.enableMenuItem == null) {
            this.enableMenuItem = new RefreshableMenuItem() {
                @Override
                protected void refresh(ResourceKind resource) {
                    setAction(getActions().getEnableAction(resource));
                }
            };
        }
        return this.enableMenuItem;
    }

    /**
     * Returns the menu item that will contain the current export action.
     */
    private RefreshableMenuItem getExportMenuItem() {
        // lazily create the menu item
        if (this.exportMenuItem == null) {
            this.exportMenuItem = new RefreshableMenuItem() {
                @Override
                protected void refresh(DisplayKind display) {
                    setAction(getActions().getExportAction(display));
                }
            };
        }
        return this.exportMenuItem;
    }

    /**
     * Returns the menu item that will contain the current export action.
     */
    private RefreshableMenuItem getSaveAsMenuItem() {
        // lazily create the menu item
        if (this.saveMenuItem == null) {
            this.saveMenuItem = new RefreshableMenuItem() {
                @Override
                protected void refresh(ResourceKind resource) {
                    setAction(getActions().getSaveAsAction(resource));
                }
            };
        }
        return this.saveMenuItem;
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
                JGraphPanel<?> panel = getDisplaysPanel().getGraphPanel();
                if (panel != null) {
                    jGraph = panel.getJGraph();
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
        for (ResourceKind kind : Options.getOptionalTabs()) {
            String showTabOption = Options.getShowTabOption(kind);
            result.add(getOptions().getItem(showTabOption));
        }
        result.addSeparator();
        result.add(getOptions().getItem(SHOW_NODE_IDS_OPTION));
        result.add(getOptions().getItem(SHOW_ANCHORS_OPTION));
        result.add(getOptions().getItem(SHOW_ASPECTS_OPTION));
        result.add(getOptions().getItem(SHOW_REMARKS_OPTION));
        result.add(getOptions().getItem(SHOW_BACKGROUND_OPTION));
        result.add(getOptions().getItem(SHOW_VALUE_NODES_OPTION));
        result.add(getOptions().getItem(SHOW_STATE_IDS_OPTION));
        result.add(getOptions().getItem(SHOW_UNFILTERED_EDGES_OPTION));
        result.addSeparator();
        for (ResourceKind resource : EnumSet.allOf(ResourceKind.class)) {
            result.add(getOptions().getItem(Options.getDeleteOption(resource)));
        }
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
        result.add(getActions().getBackAction());
        result.add(getActions().getForwardAction());
        result.addSeparator();
        // EDUARDO: Uncomment to enable abstraction.
        result.add(getActions().getToggleExplorationStateAction());
        result.add(getActions().getStartSimulationAction());
        result.add(getActions().getApplyTransitionAction());
        result.add(getActions().getAnimateAction());
        result.add(getActions().getExploreAction());
        result.addSeparator();
        result.add(getActions().getGotoStartStateAction());
        result.add(getActions().getGotoFinalStateAction());
        result.addSeparator();
        result.add(getActions().getExplorationDialogAction());
        result.add(getActions().getExplorationStatsDialogAction());
        result.addSeparator();
        result.add(getActions().getSaveLTSAsAction());
        return result;
    }

    /**
     * Creates and returns a verification menu for the menu bar.
     */
    private JMenu createVerifyMenu() {
        JMenu result = new JMenu(Options.VERIFY_MENU_NAME);
        result.setMnemonic(Options.VERIFY_MENU_MNEMONIC);
        result.add(this.actions.getCheckCTLAction(true));
        result.add(this.actions.getCheckCTLAction(false));
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
        GrammarModel grammar = getModel().getGrammar();
        if (grammar != null && grammar.getName() != null) {
            title.append(grammar.getName());
            HostModel startGraph = grammar.getStartGraphModel();
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

    /** Store of all simulator-related actions. */
    private final ActionStore actions;
    /**
     * This application's main frame.
     */
    private JFrame frame;

    /** Returns the history of simulation steps. */
    public StepHistory getStepHistory() {
        return this.stepHistory;
    }

    /** Undo history. */
    private StepHistory stepHistory;

    /** background for displays. */
    private DisplaysPanel simulatorPanel;

    /** History of recently opened grammars. */
    private SimulatorHistory history;

    /** Menu for externally provided actions. */
    private JMenu externalMenu;

    /** The menu item containing the (current) export action. */
    private RefreshableMenuItem exportMenuItem;

    /** The menu item containing the current save-as action. */
    private RefreshableMenuItem saveMenuItem;

    /** Dummy action for the {@link #externalMenu}. */
    private Action dummyExternalAction;

    /**
     * Menu items in the edit menu for one of the graph or rule edit actions.
     */
    private RefreshableMenuItem editGraphItem;
    private RefreshableMenuItem copyGraphItem;
    private RefreshableMenuItem deleteGraphItem;
    private RefreshableMenuItem renameMenuItem;
    private RefreshableMenuItem enableMenuItem;

    private List<RefreshableMenuItem> menuItems;

    /** Returns the undo manager of this simulator. */
    public final UndoManager getUndoManager() {
        return this.undoManager;
    }

    /** The undo manager of this simulator. */
    private final UndoManager undoManager;

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
     * Default name of a fresh grammar.
     */
    public static final String NEW_GRAMMAR_NAME = "newGrammar";

    /**
     * Default name of an empty host graph.
     */
    public static final String NEW_GRAPH_NAME = "newGraph";

    /**
     * Default name of a fresh prolog program.
     */
    public static final String NEW_PROLOG_NAME = "newProlog";
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
     * Minimum height of the rule tree component.
     */
    static final int START_LIST_MINIMUM_HEIGHT = 130;
}
