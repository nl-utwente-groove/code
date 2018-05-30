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
 * $Id$
 */
package groove.gui;

import static groove.gui.Options.DELETE_RESOURCE_OPTION;
import static groove.gui.Options.HELP_MENU_NAME;
import static groove.gui.Options.SHOW_ABSENT_STATES_OPTION;
import static groove.gui.Options.SHOW_ANCHORS_OPTION;
import static groove.gui.Options.SHOW_ARROWS_ON_LABELS_OPTION;
import static groove.gui.Options.SHOW_ASPECTS_OPTION;
import static groove.gui.Options.SHOW_BIDIRECTIONAL_EDGES_OPTION;
import static groove.gui.Options.SHOW_CONTROL_STATE_OPTION;
import static groove.gui.Options.SHOW_INVARIANTS_OPTION;
import static groove.gui.Options.SHOW_NODE_IDS_OPTION;
import static groove.gui.Options.SHOW_RECIPE_STEPS_OPTION;
import static groove.gui.Options.SHOW_STATE_IDS_OPTION;
import static groove.gui.Options.SHOW_STATE_STATUS_OPTION;
import static groove.gui.Options.SHOW_UNFILTERED_EDGES_OPTION;
import static groove.gui.Options.SHOW_VALUE_NODES_OPTION;
import static groove.gui.Options.VERIFY_ALL_STATES_OPTION;
import static groove.io.FileType.GRAMMAR;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

import apple.dts.samplecode.osxadapter.OSXAdapter;
import groove.grammar.GrammarKey;
import groove.grammar.QualName;
import groove.grammar.model.GrammarModel;
import groove.grammar.model.ResourceKind;
import groove.graph.Element;
import groove.gui.SimulatorModel.Change;
import groove.gui.action.AboutAction;
import groove.gui.action.ActionStore;
import groove.gui.dialog.ErrorDialog;
import groove.gui.dialog.GraphPreviewDialog;
import groove.gui.dialog.PropertiesTable;
import groove.gui.display.Display;
import groove.gui.display.Display.ListPanel;
import groove.gui.display.DisplayKind;
import groove.gui.display.DisplaysPanel;
import groove.gui.display.GraphEditorTab;
import groove.gui.display.GraphTab;
import groove.gui.display.JGraphPanel;
import groove.gui.display.ResourceDisplay;
import groove.gui.display.ResourceTab;
import groove.gui.display.TextTab;
import groove.gui.jgraph.AspectJGraph;
import groove.gui.jgraph.JGraph;
import groove.gui.list.ListPanel.SelectableListEntry;
import groove.gui.list.ListTabbedPane;
import groove.gui.list.SearchResult;
import groove.gui.menu.ModelCheckingMenu;
import groove.gui.menu.MyJMenu;
import groove.transform.oracle.DialogOracle;
import groove.util.Groove;
import groove.util.parse.FormatError;
import groove.util.parse.FormatErrorSet;

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
        GraphPreviewDialog.setSimulator(this);
        JFrame frame = getFrame();
        DialogOracle.instance()
            .setParent(frame);
        this.actions.initialiseRemainingActions();
    }

    /**
     * Constructs a simulator using all production rules in a given directory.
     * All known graph grammar format loaders are polled to find one that can
     * load the grammar.
     * @param grammarLocation the location (file or directory) containing the
     *        grammar; if <tt>null</tt>, no grammar is loaded.
     */
    public Simulator(final String grammarLocation) {
        this();
        if (grammarLocation != null) {
            final File location = new File(GRAMMAR.addExtension(grammarLocation)).getAbsoluteFile();
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        Simulator.this.actions.getLoadGrammarAction()
                            .load(location);
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
        groove.gui.UserSettings.applyUserSettings(this);
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
    public void update(SimulatorModel source, SimulatorModel oldModel, Set<Change> changes) {
        if (changes.contains(Change.GRAMMAR)) {
            setTitle();
            FormatErrorSet grammarErrors = getModel().getGrammar()
                .getErrors();
            setErrors(grammarErrors);
        }
        if (changes.contains(Change.DISPLAY)) {
            refreshMenuItems();
            adjustInfoPanel(source.getDisplay());
        }
    }

    /**
     * Displays a list of errors, or hides the error panel if the list is empty.
     */
    private void setErrors(FormatErrorSet grammarErrors) {
        getResultsPanel().getErrorListPanel()
            .setEntries(grammarErrors);
        adjustResultsPanel();
    }

    /**
     * Displays a list of search results.
     */
    public void setSearchResults(List<SearchResult> searchResults) {
        getResultsPanel().getSearchResultListPanel()
            .setEntries(searchResults);
        adjustResultsPanel();
    }

    /**
     * Execute the quit action as a method of the Simulator class.
     * Needed for Command-Q shortcut on MacOS only (see {@link #getFrame}).
     */
    public void tryQuit() {
        this.getActions()
            .getQuitAction()
            .execute();
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
                    OSXAdapter.setQuitHandler(this, this.getClass()
                        .getDeclaredMethod("tryQuit"));
                } catch (NoSuchMethodException e1) {
                    // should not happen (thrown when 'tryQuit' does not exist)
                    // ignore
                }
            }
            // register doQuit() as the closing method of the window
            this.frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    Simulator.this.actions.getQuitAction()
                        .execute();
                }
            });
            this.frame.setJMenuBar(createMenuBar());
            this.frame.setContentPane(getContentPanel());
            // make sure tool tips get displayed
            ToolTipManager.sharedInstance()
                .registerComponent(getContentPanel());
        }
        return this.frame;
    }

    /**
     * Lazily creates and returns the panel with the state, rule and LTS views.
     */
    public DisplaysPanel getDisplaysPanel() {
        if (this.displaysPanel == null) {
            this.displaysPanel = new DisplaysPanel(this);
        }
        return this.displaysPanel;
    }

    /**
     * Lazily creates and returns the content panel of the simulator frame.
     * The content panel consists of the grammar panel and the results panel.
     */
    JSplitPane getContentPanel() {
        if (this.contentPanel == null) {
            this.contentPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
            this.contentPanel.setTopComponent(getGrammarPanel());
            this.contentPanel.setResizeWeight(0.8);
            this.contentPanel.setDividerSize(0);
            this.contentPanel.setContinuousLayout(true);
        }
        return this.contentPanel;
    }

    /**
     * Lazily creates and returns the top panel of the simulator,
     * containing all the grammar data.
     */
    JSplitPane getGrammarPanel() {
        if (this.grammarPanel == null) {
            this.grammarPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, getListsPanel(),
                getDisplaysInfoPanel());
            this.grammarPanel.setBorder(null);
        }
        return this.grammarPanel;
    }

    /**
     * Lazily creates and returns the panel with the resource lists.
     */
    JSplitPane getListsPanel() {
        if (this.listsPanel == null) {
            this.listsPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                getDisplaysPanel().getUpperListsPanel(), getDisplaysPanel().getLowerListsPanel());
            this.listsPanel.setBorder(null);
        }
        return this.listsPanel;
    }

    /**
     * Lazily creates and returns the split pane
     * containing the displays and info panels.
     */
    JSplitPane getDisplaysInfoPanel() {
        JSplitPane result = this.displaysInfoPanel;
        if (result == null) {
            this.displaysInfoPanel = result = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
            result.setLeftComponent(getDisplaysPanel());
            result.setRightComponent(getDisplaysPanel().getInfoPanel());
            result.setOneTouchExpandable(true);
            result.setResizeWeight(1);
            result.setDividerLocation(0.8);
            result.setContinuousLayout(true);
            result.setBorder(null);
            ToolTipManager.sharedInstance()
                .registerComponent(result);
        }
        return result;
    }

    /** Fills the info panel with the content of the currently selected display. */
    private void adjustInfoPanel(DisplayKind kind) {
        //        JComponent infoPanel =
        //            getDisplaysPanel().getDisplay(kind).getInfoPanel();
        //        int dividerLocation = getDisplaysInfoPanel().getDividerLocation();
        //        getDisplaysInfoPanel().setRightComponent(infoPanel);
        //        getDisplaysInfoPanel().setDividerLocation(dividerLocation);
    }

    private ListTabbedPane getResultsPanel() {
        if (this.resultsPanel == null) {
            this.resultsPanel = new ListTabbedPane();
            this.resultsPanel.getErrorListPanel()
                .addSelectionListener(createListListener());
            this.resultsPanel.getSearchResultListPanel()
                .addSelectionListener(createListListener());
            this.model.addListener(this.resultsPanel.getSearchResultListPanel(), Change.GRAMMAR);
        }
        return this.resultsPanel;
    }

    private void adjustResultsPanel() {
        JSplitPane contentPane = (JSplitPane) this.frame.getContentPane();
        getResultsPanel().adjustVisibility();
        if (getResultsPanel().isVisible()) {
            contentPane.setBottomComponent(getResultsPanel());
            contentPane.setDividerSize(1);
            contentPane.resetToPreferredSizes();
        } else {
            contentPane.remove(getResultsPanel());
            contentPane.setDividerSize(0);
        }
    }

    /**
     * Creates an observer for the error panel that will select the
     * erroneous part of the resource upon selection of an error.
     */
    private Observer createListListener() {
        return new Observer() {
            @Override
            public void update(Observable observable, Object arg) {
                if (arg != null) {
                    selectDisplayPart((SelectableListEntry) arg);
                }
            }
        };
    }

    /**
     * Selects part of the display, based on a given entry.
     */
    private void selectDisplayPart(SelectableListEntry entry) {
        ResourceKind resource = entry.getResourceKind();
        QualName name = entry.getResourceName();
        if (resource == ResourceKind.PROPERTIES) {
            Display display = getDisplaysPanel().getDisplayFor(resource);
            ListPanel panel = display.getListPanel();
            getDisplaysPanel().getUpperListsPanel()
                .setSelectedComponent(panel);
            ((PropertiesTable) panel.getList()).setSelected(GrammarKey.getKey(name.toString()));
        } else if (resource != null) {
            getModel().doSelect(resource, name);
            ResourceDisplay display = (ResourceDisplay) getDisplaysPanel().getDisplayFor(resource);
            ResourceTab resourceTab = display.getSelectedTab();
            if (resource.isGraphBased()) {
                AspectJGraph jGraph;
                if (resourceTab.isEditor()) {
                    jGraph = ((GraphEditorTab) resourceTab).getJGraph();
                } else {
                    jGraph = ((GraphTab) resourceTab).getJGraph();
                }
                // select the error cell and switch to the panel
                for (Element cell : entry.getElements()) {
                    if (jGraph.selectJCell(cell)) {
                        break;
                    }
                }
            } else if (entry instanceof FormatError) {
                FormatError error = (FormatError) entry;
                if (error.getNumbers()
                    .size() > 1) {
                    int line = error.getNumbers()
                        .get(0);
                    int column = error.getNumbers()
                        .get(1);
                    ((TextTab) resourceTab).select(line, column);
                }
            }
        }
    }

    /** List display. */
    private ListTabbedPane resultsPanel;

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
        InputMap im = contentPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        im.put((KeyStroke) action.getValue(Action.ACCELERATOR_KEY), action.getValue(Action.NAME));
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
     * Creates and returns an edit menu for the menu bar. The menu is filled
     * out each time it gets selected so as to be sure it applies to the current
     * jgraph.
     * @see #fillEditMenu(MyJMenu)
     */
    private MyJMenu createEditMenu() {
        // fills the menu depending on the currently displayed graph panel
        MyJMenu result = new MyJMenu(Options.EDIT_MENU_NAME) {
            @Override
            public void menuSelectionChanged(boolean selected) {
                removeAll();
                fillEditMenu(this);
                super.menuSelectionChanged(selected);
            }
        };
        result.setMnemonic(Options.EDIT_MENU_MNEMONIC);
        return result;
    }

    /**
     * Fills the edit menu for the menu bar.
     */
    private void fillEditMenu(MyJMenu menu) {
        // do/undo
        menu.add(this.actions.getUndoAction());
        menu.add(this.actions.getRedoAction());

        // new submenu
        menu.addSeparator();
        JMenu newMenu = new JMenu(Options.NEW_MENU_NAME);
        for (ResourceKind resource : ResourceKind.values()) {
            if (resource != ResourceKind.PROPERTIES && resource != ResourceKind.CONFIG) {
                newMenu.add(this.actions.getNewAction(resource));
            }
        }
        menu.add(newMenu);

        menu.addSeparator();
        menu.add(getEditMenuItem());
        menu.add(getCopyMenuItem());
        menu.add(getDeleteMenuItem());
        menu.add(getRenameMenuItem());
        menu.add(getEnableMenuItem());

        menu.addSeparator();
        menu.add(this.actions.getFindReplaceAction());
        menu.add(this.actions.getRenumberAction());

        // resource edits
        menu.addSeparator();
        menu.add(this.actions.getShiftPriorityAction(true));
        menu.add(this.actions.getShiftPriorityAction(false));
        menu.add(this.actions.getEditRulePropertiesAction());

        // add graph edit menu when appropriate
        JGraphPanel<?> panel = getDisplaysPanel().getGraphPanel();
        if (panel != null) {
            JGraph<?> jGraph = panel.getJGraph();
            if (jGraph instanceof AspectJGraph) {
                menu.addSubmenu(((AspectJGraph) jGraph).createEditMenu(null));
            }
        }

        // system properties
        menu.addSeparator();
        menu.add(this.actions.getEditSystemPropertiesAction());
    }

    /**
     * Creates and returns a display menu for the menu bar. The menu is filled
     * out each time it gets selected so as to be sure it applies to the current
     * jgraph
     * @see #fillDisplayMenu(MyJMenu)
     */
    private MyJMenu createDisplayMenu() {
        // fills the menu depending on the currently displayed graph panel
        MyJMenu result = new MyJMenu(Options.DISPLAY_MENU_NAME) {
            @Override
            public void menuSelectionChanged(boolean selected) {
                removeAll();
                fillDisplayMenu(this);
                super.menuSelectionChanged(selected);
            }
        };
        result.setMnemonic(Options.DISPLAY_MENU_MNEMONIC);
        return result;
    }

    /**
     * Fills the show menu with items (upon refresh).
     */
    private void fillDisplayMenu(MyJMenu menu) {
        JGraphPanel<?> panel = getDisplaysPanel().getGraphPanel();
        if (panel != null) {
            JGraph<?> jGraph = panel.getJGraph();
            menu.add(jGraph.createShowHideMenu());
            menu.add(jGraph.createZoomMenu());
        }
        menu.addSubmenu(createOptionsMenu());
    }

    /**
     * Creates and returns an options menu for the menu bar.
     */
    private JMenu createOptionsMenu() {
        // fills the menu depending on the currently displayed graph panel
        JMenu result = new JMenu(Options.OPTIONS_MENU_NAME);
        result.setMnemonic(Options.OPTIONS_MENU_MNEMONIC);
        for (ResourceKind kind : Options.getOptionalTabs()) {
            String showTabOption = Options.getShowTabOption(kind);
            result.add(getOptions().getItem(showTabOption));
        }
        switch (getDisplaysPanel().getSelectedDisplay()
            .getKind()) {
        case HOST:
        case RULE:
        case STATE:
        case TYPE:
            result.addSeparator();
            result.add(getOptions().getItem(SHOW_NODE_IDS_OPTION));
            result.add(getOptions().getItem(SHOW_ANCHORS_OPTION));
            result.add(getOptions().getItem(SHOW_ASPECTS_OPTION));
            result.add(getOptions().getItem(SHOW_VALUE_NODES_OPTION));
            result.add(getOptions().getItem(SHOW_UNFILTERED_EDGES_OPTION));
            result.add(getOptions().getItem(SHOW_BIDIRECTIONAL_EDGES_OPTION));
            result.add(getOptions().getItem(SHOW_ARROWS_ON_LABELS_OPTION));
            break;
        case LTS:
            result.addSeparator();
            result.add(getOptions().getItem(SHOW_STATE_IDS_OPTION));
            result.add(getOptions().getItem(SHOW_STATE_IDS_OPTION));
            result.add(getOptions().getItem(SHOW_STATE_STATUS_OPTION));
            result.add(getOptions().getItem(SHOW_CONTROL_STATE_OPTION));
            result.add(getOptions().getItem(SHOW_INVARIANTS_OPTION));
            result.add(getOptions().getItem(SHOW_RECIPE_STEPS_OPTION));
            result.add(getOptions().getItem(SHOW_ABSENT_STATES_OPTION));
            result.add(getOptions().getItem(SHOW_ANCHORS_OPTION));
            break;
        default:
            // no special options to be added
        }
        result.addSeparator();
        result.add(getOptions().getItem(DELETE_RESOURCE_OPTION));
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
        result.add(getActions().getExplorationDialogAction());
        result.addSeparator();
        result.add(getActions().getStartSimulationAction());
        result.add(getActions().getApplyMatchAction());
        result.add(getActions().getAnimateAction());
        result.add(getActions().getExploreAction());
        result.addSeparator();
        result.add(getActions().getBackAction());
        result.add(getActions().getForwardAction());
        result.add(getActions().getGotoStartStateAction());
        result.add(getActions().getGotoFinalStateAction());
        result.addSeparator();
        result.add(getActions().getSaveLTSAsAction());
        result.addSeparator();
        result.add(getActions().getExplorationStatsDialogAction());
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

    private List<RefreshableMenuItem> getRefreshableMenuItems() {
        if (this.refreshableMenuItems == null) {
            this.refreshableMenuItems = new ArrayList<>();
            this.refreshableMenuItems.add(getEditMenuItem());
            this.refreshableMenuItems.add(getCopyMenuItem());
            this.refreshableMenuItems.add(getDeleteMenuItem());
            this.refreshableMenuItems.add(getRenameMenuItem());
            this.refreshableMenuItems.add(getEnableMenuItem());
            this.refreshableMenuItems.add(getSaveAsMenuItem());
            this.refreshableMenuItems.add(getExportMenuItem());
        }
        return this.refreshableMenuItems;
    }

    private List<RefreshableMenuItem> refreshableMenuItems;

    /**
     * Creates a menu item that can be refreshed with a {@link DisplayKind}-
     * or {@link ResourceKind}-dependent action.
     */
    private abstract static class RefreshableMenuItem extends JMenuItem {
        /** Refreshes or disables the menu item, based on the
         * given {@link DisplayKind}.
         */
        protected void refresh(DisplayKind display) {
            if (display.hasResource()) {
                refresh(display.getResource());
            } else {
                setEnabled(false);
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
     * Returns the menu item in the edit menu that specifies editing the
     * currently displayed graph or rule.
     */
    private RefreshableMenuItem getEditMenuItem() {
        if (this.editMenuItem == null) {
            this.editMenuItem = new RefreshableMenuItem() {
                @Override
                protected void refresh(ResourceKind resource) {
                    setAction(getActions().getEditAction(resource));
                }
            };
            this.editMenuItem.setAccelerator(Options.EDIT_KEY);
        }
        return this.editMenuItem;
    }

    /**
     * Menu items in the edit menu for one of the graph or rule edit actions.
     */
    private RefreshableMenuItem editMenuItem;

    /**
     * Returns the menu item in the edit menu that specifies copy the currently
     * displayed graph or rule.
     */
    private RefreshableMenuItem getCopyMenuItem() {
        if (this.copyMenuItem == null) {
            this.copyMenuItem = new RefreshableMenuItem() {
                @Override
                protected void refresh(ResourceKind resource) {
                    setAction(getActions().getCopyAction(resource));
                }
            };
        }
        return this.copyMenuItem;
    }

    private RefreshableMenuItem copyMenuItem;

    /**
     * Returns the menu item in the edit menu that specifies delete the
     * currently displayed graph or rule.
     */
    private RefreshableMenuItem getDeleteMenuItem() {
        if (this.deleteMenuItem == null) {
            this.deleteMenuItem = new RefreshableMenuItem() {
                @Override
                protected void refresh(ResourceKind resource) {
                    setAction(getActions().getDeleteAction(resource));
                }
            };
        }
        return this.deleteMenuItem;
    }

    private RefreshableMenuItem deleteMenuItem;

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

    private RefreshableMenuItem renameMenuItem;

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

    private RefreshableMenuItem enableMenuItem;

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

    /** The menu item containing the (current) export action. */
    private RefreshableMenuItem exportMenuItem;

    /**
     * Returns the menu item that will contain the current export action.
     */
    private RefreshableMenuItem getSaveAsMenuItem() {
        // lazily create the menu item
        if (this.saveAsMenuItem == null) {
            this.saveAsMenuItem = new RefreshableMenuItem() {
                @Override
                protected void refresh(ResourceKind resource) {
                    setAction(getActions().getSaveAsAction(resource));
                }
            };
        }
        return this.saveAsMenuItem;
    }

    /** The menu item containing the current save-as action. */
    private RefreshableMenuItem saveAsMenuItem;

    /**
     * Lazily creates and returns a menu for externally provided actions in the
     * menu bar.
     */
    private JMenu getExternalActionsMenu() {
        if (this.externalMenu == null) {
            this.externalMenu = createExternalActionsMenu();
            this.dummyExternalAction = new AbstractAction("(empty)") {
                @Override
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
        if (externalMenu.getItem(0)
            .getAction() == this.dummyExternalAction) {
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
        result.add(new JMenuItem(new AboutAction(getFrame())));
        return result;
    }

    /**
     * Sets the title of the frame to a given title.
     */
    public void setTitle() {
        StringBuffer title = new StringBuffer();
        GrammarModel grammar = getModel().getGrammar();
        if (grammar != null && grammar.getName() != null) {
            title.append(grammar.getId());
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
            this.options.getItem(SHOW_STATE_IDS_OPTION)
                .setSelected(true);
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
    private DisplaysPanel displaysPanel;

    /** Content pane of the simulator, containing in the grammar and results panels. */
    private JSplitPane contentPanel;
    /** Grammar panel, containing the lists and displays/info panels. */
    private JSplitPane grammarPanel;
    /** Split panel containing the displays and info panels. */
    private JSplitPane displaysInfoPanel;

    /** Lists panel of the simulator. */
    private JSplitPane listsPanel;

    /** History of recently opened grammars. */
    private SimulatorHistory history;

    /** Menu for externally provided actions. */
    private JMenu externalMenu;

    /** Dummy action for the {@link #externalMenu}. */
    private Action dummyExternalAction;

    /** Returns the undo manager of this simulator. */
    public final SimulatorUndoManager getUndoManager() {
        return this.undoManager;
    }

    /** The undo manager of this simulator. */
    private final SimulatorUndoManager undoManager;

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
            } else {
                throw new IOException("Usage: Simulator [<production-system>]");
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

    /** Name of this application. */
    private static final String APPLICATION_NAME = "Production Simulator";
}
