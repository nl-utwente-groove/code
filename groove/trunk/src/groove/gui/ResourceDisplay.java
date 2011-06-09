/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
 * $Id$
 */
package groove.gui;

import groove.gui.action.ActionStore;
import groove.gui.action.CancelEditAction;
import groove.gui.action.CopyAction;
import groove.gui.action.SaveAction;
import groove.gui.action.SimulatorAction;
import groove.trans.ResourceKind;
import groove.view.ResourceModel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.ToolTipManager;
import javax.swing.border.EmptyBorder;

/**
 * Resource display class that includes a tabbed pane,
 * with a single main tab for switching quickly between resources that are merely displayed,
 * and multiple tabs for pinned editors.
 * @author Arend Rensink
 * @version $Revision $
 */
public abstract class ResourceDisplay implements Display {
    /**
     * Constructs a display, for a given simulator and resource kind.
     */
    public ResourceDisplay(Simulator simulator, ResourceKind resource) {
        this.simulator = simulator;
        this.kind = DisplayKind.toDisplay(resource);
        this.resource = resource;
        assert this.resource != null;
    }

    @Override
    public DisplayKind getKind() {
        return this.kind;
    }

    @Override
    public Simulator getSimulator() {
        return this.simulator;
    }

    @Override
    public String getTitle() {
        if (getSimulatorModel().isSelected(getResourceKind())) {
            return getSimulatorModel().getSelected(getResourceKind());
        } else {
            return null;
        }
    }

    /** Returns the GUI component showing the list of control program names. */
    public JPanel getListPanel() {
        if (this.listPanel == null) {
            JScrollPane controlPane = new JScrollPane(getList()) {
                @Override
                public Dimension getPreferredSize() {
                    Dimension superSize = super.getPreferredSize();
                    return new Dimension((int) superSize.getWidth(),
                        Simulator.START_LIST_MINIMUM_HEIGHT);
                }
            };

            this.listPanel = new JPanel(new BorderLayout(), false);
            this.listPanel.add(getListToolBar(), BorderLayout.NORTH);
            this.listPanel.add(controlPane, BorderLayout.CENTER);
            // make sure tool tips get displayed
            ToolTipManager.sharedInstance().registerComponent(this.listPanel);
        }
        return this.listPanel;
    }

    /** Resets the list panel to {@code null}, so that the next invocation
     * of {@link #getListPanel()} creates a fresh panel.
     */
    protected void resetListPanel() {
        this.listPanel = null;
    }

    /** Returns the name list for this display. */
    abstract protected JComponent getList();

    /** Creates and returns the fixed tool bar for the label list. */
    final protected JToolBar getListToolBar() {
        if (this.listToolBar == null) {
            this.listToolBar = createListToolBar();
        }
        return this.listToolBar;
    }

    /** 
     * Creates a popup menu for the label list.
     * @param overResource flag indicating that the mouse is over a 
     * resource in the label list. This displays more items 
     */
    protected JPopupMenu createListPopupMenu(boolean overResource) {
        JPopupMenu res = new JPopupMenu();
        res.setFocusable(false);
        res.add(getNewAction());
        if (overResource) {
            res.add(getEditAction());
            res.addSeparator();
            res.add(getCopyAction());
            res.add(getDeleteAction());
            res.add(getRenameAction());
            if (getResourceKind() != ResourceKind.PROLOG) {
                res.addSeparator();
                res.add(getEnableAction());
            }
        }
        return res;
    }

    /** 
     * Callback method to creates a tool bar for the list panel.
     */
    protected JToolBar createListToolBar() {
        return createListToolBar(-1);
    }

    /** 
     * Creates a tool bar for the list panel.
     * @param separation width of the separator on the tool bar;
     * if negative, the default separator is used
     */
    protected JToolBar createListToolBar(int separation) {
        JToolBar result = Options.createToolBar();
        result.add(getNewAction());
        result.add(getEditAction());
        if (separation >= 0) {
            result.addSeparator(new Dimension(separation, 0));
        } else {
            result.addSeparator();
        }
        result.add(getCopyAction());
        result.add(getDeleteAction());
        result.add(getRenameAction());
        if (separation >= 0) {
            result.addSeparator(new Dimension(separation, 0));
        } else {
            result.addSeparator();
        }
        if (getKind() != DisplayKind.PROLOG) {
            result.add(getEnableButton());
        }
        return result;
    }

    /** Returns the copy action associated with this kind of resource. */
    protected final CopyAction getCopyAction() {
        return getActions().getCopyAction(getResourceKind());
    }

    /** Returns the delete action associated with this kind of resource. */
    protected final SimulatorAction getDeleteAction() {
        return getActions().getDeleteAction(getResourceKind());
    }

    /** Returns the edit action associated with this kind of resource. */
    protected final SimulatorAction getEditAction() {
        return getActions().getEditAction(getResourceKind());
    }

    /** Returns the edit action associated with this kind of resource. */
    protected final SimulatorAction getEnableAction() {
        return getActions().getEnableAction(getResourceKind());
    }

    /** Returns the new action associated with this kind of resource. */
    protected final SimulatorAction getNewAction() {
        return getActions().getNewAction(getResourceKind());
    }

    /** Returns the rename action associated with this kind of resource. */
    protected final SimulatorAction getRenameAction() {
        return getActions().getRenameAction(getResourceKind());
    }

    /** Returns the save action associated with this kind of resource. */
    protected final SaveAction getSaveAction() {
        return getActions().getSaveAction(getResourceKind());
    }

    /** Returns the save action associated with this kind of resource. */
    protected final CancelEditAction getCancelEditAction() {
        return getActions().getCancelEditAction(getResourceKind());
    }

    /** Creates a toggle button wrapping the enable action of this display. */
    protected final JToggleButton getEnableButton() {
        if (this.enableButton == null) {
            this.enableButton = Options.createToggleButton(getEnableAction());
            this.enableButton.setMargin(new Insets(3, 1, 3, 1));
            this.enableButton.setText(null);
        }
        return this.enableButton;
    }

    /** Convenience method to retrieve the simulator model. */
    final protected SimulatorModel getSimulatorModel() {
        return getSimulator().getModel();
    }

    /** Convenience method to retrieve the action store. */
    final protected ActionStore getActions() {
        return getSimulator().getActions();
    }

    /** Returns the resource kind shown on this display. */
    final protected ResourceKind getResourceKind() {
        return this.resource;
    }

    /**
     * Returns the panel holding all display tabs.
     * This may or may not be the same as #getDisplayPanel().
     */
    final protected TabbedDisplayPanel getTabPane() {
        if (this.displayPanel == null) {
            this.displayPanel = new TabbedDisplayPanel();
        }
        return this.displayPanel;
    }

    /** Callback to obtain the main tab of this display. */
    public MainTab getMainTab() {
        if (this.mainTab == null) {
            this.mainTab = createMainTab();
        }
        return this.mainTab;
    }

    /** Callback factory method for the main tab. */
    abstract protected MainTab createMainTab();

    /** 
     * Adds an editor panel for the given resource, or selects the 
     * one that already exists.
     */
    public void startEditResource(String name) {
        EditorTab result = getEditors().get(name);
        if (result == null) {
            result = createEditorTab(name);
            addEditorTab(result);
            if (getMainTab().removeResource(name)) {
                removeMainTab();
            }
            getEditors().put(name, result);
        }
        if (getTabPane().getSelectedComponent() == result) {
            getSimulatorModel().setDisplay(getKind());
        } else {
            getTabPane().setSelectedComponent(result);
        }
    }

    /** Creates and adds an editor panel for the given graph. */
    private void addEditorTab(EditorTab result) {
        getTabPane().addTab("", result);
        int index = getTabPane().indexOfComponent(result);
        getTabPane().setTabComponentAt(index, result.getTabLabel());
        getListPanel().repaint();
    }

    /** Callback method to create an editor tab for a given named resource. */
    abstract protected EditorTab createEditorTab(String name);

    /** Attempts to cancel an edit action for a given named resource.
     * @param name the name of the editor to be cancelled
     * @param confirm if {@code true}, the user should explicitly confirm
     * @return {@code true} if the editing was cancelled
     */
    public boolean cancelEditResource(String name, boolean confirm) {
        boolean result = true;
        EditorTab editor = getEditors().get(name);
        if (editor != null) {
            result = editor.cancelEditing(true);
        }
        return result;
    }

    /** Returns a list of all editor panels currently displayed. */
    protected final Map<String,EditorTab> getEditors() {
        return this.editorMap;
    }

    /**
     * Attempts to close all editors on this display, asking permission 
     * for the dirty ones and possibly saving them.
     * This is done in preparation to changing the grammar.
     * @return {@code true} if all editors were disposed.
     */
    public boolean cancelAllEdits() {
        boolean result = true;
        for (EditorTab editor : new ArrayList<EditorTab>(getEditors().values())) {
            result = editor.cancelEditing(true);
            if (!result) {
                break;
            }
        }
        return result;
    }

    /** 
     * Attempts to disposes the editor for certain aspect graphs, if any.
     * This is done in response to a change in the graph outside the editor.
     * @param names the graphs that are about to be changed and whose editor 
     * therefore needs to be disposed
     * @return {@code true} if the operation was not cancelled
     */
    public boolean cancelEdits(String... names) {
        boolean result = true;
        for (String name : names) {
            result = cancelEditResource(name, true);
            if (!result) {
                break;
            }
        }
        return result;
    }

    /** Returns the currently selected editor tab, or {@code null} if no editor is selected. */
    public EditorTab getSelectedEditor() {
        Tab result = getSelectedTab();
        return result != null && result.isEditor() ? (EditorTab) result : null;
    }

    /** Returns the currently selected tab, or {@code null} if no editor is selected. */
    public Tab getSelectedTab() {
        return (Tab) getTabPane().getSelectedComponent();
    }

    /**
     * Initialises all listening activity on the display, and 
     * calls {@link #activateListening()}.
     */
    protected void installListeners() {
        activateListening();
    }

    /** Tests if listening is currently activated. */
    protected boolean isListening() {
        return this.listening;
    }

    /** 
     * Tests and sets the listening flag.
     * This should be used by all listeners to test if they are
     * supposed to be active, before they take any actions that can cause circular
     * dependencies.
     * 
     * @return {@code true} if the listening flag was on
     * before the call.
     */
    protected boolean suspendListening() {
        boolean result = this.listening;
        if (result) {
            this.listening = false;
        }
        return result;
    }

    /** 
     * Sets the listening flag to true.
     * This means all listeners to events on this display become active.
     */
    protected void activateListening() {
        if (this.listening) {
            throw new IllegalStateException();
        }
        this.listening = true;
    }

    /** Callback method that is invoked when the tab selection has changed. */
    protected void selectionChanged() {
        if (suspendListening()) {
            Component selection = getTabPane().getSelectedComponent();
            String name = selection == null ? null : selection.getName();
            getSimulatorModel().setSelected(getResourceKind(), name);
            activateListening();
        }
    }

    /** 
     * Switches to the display of a given (named) resource,
     * either in an open editor or in the main tab.
     * @return the tab in which the resource is shown
     */
    public Tab selectResource(String name) {
        Tab result;
        if (name == null) {
            removeMainTab();
            result = null;
        } else {
            EditorTab editor = getEditors().get(name);
            if (editor == null) {
                selectMainTab(name);
                getMainTab().repaint();
                result = getMainTab();
            } else {
                getTabPane().setSelectedComponent(editor);
                result = editor;
            }
        }
        return result;
    }

    /** 
     * Removes the main panel from the display.
     */
    protected void removeMainTab() {
        getTabPane().remove(getMainTab().getComponent());
    }

    /** 
     * Sets the main panel  to a given (named) graph.
     */
    protected void selectMainTab(String name) {
        getMainTab().setResource(name);
        TabLabel tabLabel = getMainTab().getTabLabel();
        int index = getTabPane().indexOfComponent(getMainTab().getComponent());
        if (index < 0) {
            index = getMainTabIndex();
            getTabPane().add(getMainTab().getComponent(), index);
            getTabPane().setTitleAt(index, null);
            getTabPane().setTabComponentAt(index, tabLabel);
        }
        tabLabel.setEnabled(true);
        tabLabel.setTitle(getLabelText(name));
        tabLabel.setError(hasError(name));
        getTabPane().setSelectedIndex(index);
    }

    /**
     * Callback method to construct the label for a given (named) graph
     * that should be used in the label list.
     */
    final protected Icon getListIcon(String name) {
        Icon result;
        if (this.editorMap.containsKey(name)) {
            result = Icons.EDIT_ICON;
        } else {
            result = Icons.getListIcon(getResourceKind());
        }
        return result;
    }

    /**
     * Callback method to construct the string description for a 
     * given (named) resource that should be used in the label list and
     * tab component.
     */
    protected String getLabelText(String name) {
        StringBuilder result = new StringBuilder();
        if (this.editorMap.containsKey(name)) {
            result.append(this.editorMap.get(name).getTitle());
        } else {
            result.append(name);
        }
        decorateLabelText(name, result);
        return result.toString();
    }

    /** 
     * Adds HTML formatting to the label text for the main display.
     * Callback method from {@link #getLabelText(String)}.
     * @param name the name of the displayed object. This determines the
     * decoration
     * @param text the text to be decorated
     */
    protected void decorateLabelText(String name, StringBuilder text) {
        // empty
    }

    /** Index of the pain panel. This returns {@code 0} by default. */
    protected int getMainTabIndex() {
        return 0;
    }

    /** Indicates if a given (named) graph has errors. */
    final protected boolean hasError(String name) {
        boolean result;
        if (this.editorMap.containsKey(name)) {
            result = this.editorMap.get(name).hasErrors();
        } else {
            result = getResource(name).hasErrors();
        }
        return result;
    }

    /** Retrieves the resource model for a given name from the grammar. */
    protected final ResourceModel<?> getResource(String name) {
        return getSimulatorModel().getGrammar().getResource(getResourceKind(),
            name);
    }

    private final Simulator simulator;
    private final ResourceKind resource;
    private final DisplayKind kind;
    /** Panel with the label list. */
    private JPanel listPanel;
    /** Toolbar for the {@link #listPanel}. */
    private JToolBar listToolBar;
    private JToggleButton enableButton;

    /** Mapping from graph names to editors for those graphs. */
    private final Map<String,EditorTab> editorMap =
        new HashMap<String,EditorTab>();
    private TabbedDisplayPanel displayPanel;

    /** Flag indicating that the listeners are currently active. */
    private boolean listening;
    private MainTab mainTab;

    class TabbedDisplayPanel extends JTabbedPane implements Panel {
        /** Constructs an instance of the panel. */
        public TabbedDisplayPanel() {
            super(BOTTOM);
            setFocusable(false);
            setBorder(new EmptyBorder(0, 0, -4, 0));
            // add keyboard binding for Save and Cancel key
            InputMap focusedInputMap =
                getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
            String saveActionName =
                Options.getSaveActionName(getResourceKind(), false);
            focusedInputMap.put(Options.SAVE_KEY, saveActionName);
            focusedInputMap.put(Options.CANCEL_KEY,
                Options.CANCEL_EDIT_ACTION_NAME);
            getActionMap().put(saveActionName, getSaveAction());
            getActionMap().put(Options.CANCEL_EDIT_ACTION_NAME,
                getCancelEditAction());
        }

        @Override
        public void removeTabAt(int index) {
            // removes the editor panel from the map
            boolean isIndexSelected = getSelectedIndex() == index;
            Component panel = getComponentAt(index);
            super.removeTabAt(index);
            String name = panel.getName();
            if (getEditors().remove(name) != null && isIndexSelected) {
                selectMainTab(name);
            }
            // make sure the tab component of the selected tab is enabled
            setTabEnabled(getSelectedIndex(), true);
            getDisplayPanel().setEnabled(getTabCount() > 0);
            getListPanel().repaint();
        }

        @Override
        public void setSelectedIndex(int index) {
            int selectedIndex = getSelectedIndex();
            if (index != selectedIndex) {
                if (selectedIndex >= 0) {
                    setTabEnabled(selectedIndex, false);
                }
                super.setSelectedIndex(index);
                setTabEnabled(index, true);
            }
            // we also want to notify if the selection actually
            // does not change, so a change listener is no good
            selectionChanged();
        }

        /** Changes the enabled status of the tab component at a given index. */
        private void setTabEnabled(int index, boolean enabled) {
            if (index >= 0) {
                Component label = getTabComponentAt(index);
                if (label != null) {
                    label.setEnabled(enabled);
                }
            }
        }

        @Override
        public Display getDisplay() {
            return ResourceDisplay.this;
        }
    }

    /** Interface for tabs on this display. */
    protected static interface Tab {
        /** 
         * Returns the name of the 
         * resource currently displayed on this tab.
         */
        public String getName();

        /** 
         * Returns the icon for this tab.
         */
        public Icon getIcon();

        /** 
         * Returns the title of this tab.
         * This consists of the resource name plus an optional indication of the
         * dirty status of the tab.
         */
        public String getTitle();

        /** Returns the tab label component to be used for this tab. */
        public TabLabel getTabLabel();

        /** 
         * Indicates if this tab is an editor tab.
         * @return {@code true} if this is an editor tab, {@code false} if
         * it is a main tab.
         */
        public boolean isEditor();

        /**
         * Returns the actual component of this tab.
         * This is typically {@code this}.
         */
        public Component getComponent();

        /** Method to repaint the tab. */
        public void repaint();
    }

    /** Interface for the main tab on this display. */
    protected static interface MainTab extends Tab {
        /** Changes this tab so as to display a given, named resource. */
        public void setResource(String name);

        /** 
         * Removes a resource that is currently being edited from the
         * main tab and its internal data structures.
         * @param name the name of the resource to be removed
         * @return {@code true} if this was the currently displayed resource
         */
        public boolean removeResource(String name);
    }
}
