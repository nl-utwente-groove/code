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

import groove.gui.SimulatorModel.Change;
import groove.gui.action.CancelEditAction;
import groove.gui.action.CopyAction;
import groove.gui.action.SaveAction;
import groove.gui.action.SimulatorAction;
import groove.io.HTMLConverter;
import groove.trans.ResourceKind;
import groove.view.GrammarModel;
import groove.view.ResourceModel;
import groove.view.aspect.AspectGraph;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;

/**
 * Resource display class that includes a tabbed pane,
 * with a single main tab for switching quickly between resources that are merely displayed,
 * and multiple tabs for pinned editors.
 * @author Arend Rensink
 * @version $Revision $
 */
public class ResourceDisplay extends Display implements SimulatorListener {
    /**
     * Constructs a display, for a given simulator and resource kind.
     */
    public ResourceDisplay(Simulator simulator, ResourceKind resource) {
        super(simulator, DisplayKind.toDisplay(resource));
    }

    /** 
     * Callback factory method for the display panel.
     * This implementation defers to {@link #getTabPane()},
     * but it is a hook to allow additional components on the display
     * panel, as in the {@link PrologDisplay}.
     * @see #getDisplayPanel() 
     */
    @Override
    protected JComponent createDisplayPanel() {
        return getTabPane();
    }

    /** Callback method to create the resource list. */
    @Override
    protected JTree createList() {
        return new ResourceTree(this);
    }

    @Override
    protected void resetList() {
        ((ResourceTree) getList()).suspendListeners();
        super.resetList();
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
            res.addSeparator();
            res.add(getEnableAction());
        }
        return res;
    }

    /** 
     * Callback method to creates a tool bar for the list panel.
     */
    @Override
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
        result.add(getSaveAction());
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
        result.add(getEnableButton());
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

    /**
     * Returns the panel holding all display tabs.
     * This may or may not be the same as #getDisplayPanel().
     */
    @Override
    final protected TabbedDisplayPanel createTabPane() {
        return new TabbedDisplayPanel();
    }

    /** Callback to obtain the main tab of this display. */
    public MainTab getMainTab() {
        if (this.mainTab == null) {
            this.mainTab = createMainTab();
        }
        return this.mainTab;
    }

    /** Callback factory method for the main tab. */
    protected MainTab createMainTab() {
        ResourceKind kind = getResourceKind();
        if (kind.isGraphBased()) {
            return new GraphTab(this);
        } else {
            return new TextTab(this);
        }
    }

    /** 
     * Adds an editor panel for the given resource, or selects the 
     * one that already exists.
     */
    public void startEditResource(String name) {
        ResourceTab result = getEditors().get(name);
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
    private void addEditorTab(ResourceTab result) {
        getTabPane().addTab("", result);
        int index = getTabPane().indexOfComponent(result);
        getTabPane().setTabComponentAt(index, result.getTabLabel());
        getListPanel().repaint();
    }

    /** Callback method to create an editor tab for a given named resource. */
    protected ResourceTab createEditorTab(String name) {
        ResourceKind kind = getResourceKind();
        if (kind.isGraphBased()) {
            AspectGraph graph =
                getSimulatorModel().getStore().getGraphs(getResourceKind()).get(
                    name);
            GraphEditorTab result = new GraphEditorTab(this, graph.getRole());
            result.setGraph(graph);
            return result;
        } else {
            String program =
                getSimulatorModel().getStore().getTexts(getResourceKind()).get(
                    name);
            return new TextTab(this, name, program);
        }
    }

    /**
     * Attempts to save and optionally dispose the editor for a given named resource.
     * @param name the name of the editor to be cancelled
     * @param confirm if {@code true}, the user should explicitly confirm
     * @param dispose if {@code true}, the editor is disposed afterwards
     * (unless the action is cancelled)
     * @return {@code true} if the action was not cancelled
     */
    public boolean saveEditor(String name, boolean confirm, boolean dispose) {
        boolean result = true;
        ResourceTab editor = getEditors().get(name);
        if (editor != null) {
            result = editor.saveEditor(confirm, dispose);
        }
        return result;
    }

    /** Returns a list of all editor panels currently displayed. */
    protected final Map<String,ResourceTab> getEditors() {
        return this.editorMap;
    }

    /**
     * Attempts to save all dirty editors on this display, 
     * after asking permission from the user.
     * This is done in preparation to changing the grammar.
     * @param dispose if {@code true}, the editors are disposed
     * (unless the action is cancelled)
     * @return {@code true} if the action was not cancelled.
     */
    public boolean saveAllEditors(boolean dispose) {
        boolean result = true;
        for (ResourceTab editor : new ArrayList<ResourceTab>(
            getEditors().values())) {
            result = editor.saveEditor(true, dispose);
            if (!result) {
                break;
            }
        }
        return result;
    }

    /** Returns the currently selected editor tab, or {@code null} if no editor is selected. */
    public ResourceTab getSelectedEditor() {
        Tab result = getSelectedTab();
        return result != null && result.isEditor() ? (ResourceTab) result
                : null;
    }

    /** Returns the currently selected tab, or {@code null} if no editor is selected. */
    public Tab getSelectedTab() {
        return (Tab) getTabPane().getSelectedComponent();
    }

    @Override
    public void update(SimulatorModel source, SimulatorModel oldModel,
            Set<Change> changes) {
        if (suspendListening()) {
            if (changes.contains(Change.GRAMMAR)) {
                updateGrammar(source.getGrammar(),
                    source.getGrammar() != oldModel.getGrammar());
            }
            ResourceModel<?> resourceModel =
                source.getResource(getResourceKind());
            getEnableButton().setSelected(
                resourceModel != null && resourceModel.isEnabled());
            selectResource(source.getSelected(getResourceKind()));
            activateListening();
        }
    }

    /**
     * Callback method informing the display of a change in the loaded grammar.
     * This should only be called from the {@link SimulatorListener#update}
     * method of the display.
     * @param grammar the loaded grammar
     * @param fresh if {@code true}, the grammar has changed altogether;
     * otherwise, it has merely been refreshed (meaning that the properties
     * or type graph could have changed)
     */
    protected void updateGrammar(GrammarModel grammar, boolean fresh) {
        getMainTab().updateGrammar(grammar);
        int tabCount = getTabPane().getTabCount();
        for (int i = tabCount - 1; i >= 0; i--) {
            Tab tab = (Tab) getTabPane().getComponentAt(i);
            if (tab.isEditor() && fresh) {
                ((ResourceTab) tab).dispose();
            } else if (tab != getMainTab()) {
                tab.updateGrammar(grammar);
            }
        }
        if (getMainTab().getName() == null) {
            removeMainTab();
        }
    }

    /**
     * Initialises all listening activity on the display, and 
     * calls {@link #activateListening()}.
     */
    protected void installListeners() {
        getSimulatorModel().addListener(this, Change.GRAMMAR,
            Change.toChange(getResourceKind()));
        // adds a mouse listener that offers a popup menu with a detach action
        getDisplayPanel().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    int index =
                        getTabPane().indexAtLocation(e.getX(), e.getY());
                    if (index >= 0) {
                        ResourceTab tab =
                            (ResourceTab) getTabPane().getComponentAt(index);
                        if (tab != getMainTab()) {
                            createDetachMenu(tab).show(getDisplayPanel(),
                                e.getX(), e.getY());
                        }
                    }
                }
            }
        });
        activateListening();
    }

    /** Creates a popup menu with a detach action for a given component. */
    private JPopupMenu createDetachMenu(final ResourceTab tab) {
        JPopupMenu result = new JPopupMenu();
        result.add(new AbstractAction(Options.CLOSE_THIS_ACTION_NAME) {
            @Override
            public void actionPerformed(ActionEvent e) {
                tab.saveEditor(true, true);
            }
        });
        result.add(new AbstractAction(Options.CLOSE_OTHER_ACTION_NAME) {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (ResourceTab editor : new ArrayList<ResourceTab>(
                    getEditors().values())) {
                    if (editor != tab && !editor.saveEditor(true, true)) {
                        break;
                    }
                }
            }
        });
        result.add(new AbstractAction(Options.CLOSE_ALL_ACTION_NAME) {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveAllEditors(true);
            }
        });
        return result;
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
            getSimulatorModel().doSelect(getResourceKind(), name);
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
            ResourceTab editor = getEditors().get(name);
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
        if (getMainTab().setResource(name)) {
            TabLabel tabLabel = getMainTab().getTabLabel();
            int index =
                getTabPane().indexOfComponent(getMainTab().getComponent());
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
            result.append(getResource(name).getLastName());
        }
        decorateLabelText(name, result);
        return result.toString();
    }

    /**
     * Callback method to construct the tool tip for a given
     * resource.
     */
    protected String getToolTip(String name) {
        ResourceModel<?> model = getResource(name);
        boolean enabled = model != null && model.isEnabled();
        return getToolTip(name, enabled);
    }

    /** Returns the tool tip text for a resource, depending on its enabling. */
    private String getToolTip(String name, boolean enabled) {
        String result = enabled ? this.enabledText : this.disabledText;
        if (result == null) {
            this.enabledText =
                String.format("Enabled %s; doubleclick to edit",
                    getResourceKind().getDescription());
            this.disabledText =
                String.format("Disabled %s; doubleclick to edit",
                    getResourceKind().getDescription());
            result = enabled ? this.enabledText : this.disabledText;
        }
        return result;
    }

    /** 
     * Adds HTML formatting to the label text for the main display.
     * Callback method from {@link #getLabelText(String)}.
     * @param name the name of the displayed object. This determines the
     * decoration
     * @param text the text to be decorated
     */
    protected void decorateLabelText(String name, StringBuilder text) {
        if (getResource(name).isEnabled()) {
            HTMLConverter.STRONG_TAG.on(text);
            HTMLConverter.HTML_TAG.on(text);
        } else {
            text.insert(0, "(");
            text.append(")");
        }
    }

    /** Index of the pain panel. This returns {@code 0} by default. */
    protected int getMainTabIndex() {
        return 0;
    }

    /** Indicates if a given (named) resource has errors. */
    final protected boolean hasError(String name) {
        boolean result;
        if (this.editorMap.containsKey(name)) {
            result = this.editorMap.get(name).hasErrors();
        } else {
            ResourceModel<?> model = getResource(name);
            result = model != null && model.hasErrors();
        }
        return result;
    }

    /** Retrieves the resource model for a given name from the grammar. */
    protected final ResourceModel<?> getResource(String name) {
        return getSimulatorModel().getGrammar().getResource(getResourceKind(),
            name);
    }

    private JToggleButton enableButton;

    /** Mapping from graph names to editors for those graphs. */
    private final Map<String,ResourceTab> editorMap =
        new HashMap<String,ResourceTab>();

    /** Flag indicating that the listeners are currently active. */
    private boolean listening;
    private MainTab mainTab;

    /** Tool tip text for an enabled resource. */
    private String enabledText;
    /** Tool tip text for a disabled resource. */
    private String disabledText;

    class TabbedDisplayPanel extends JTabbedPane implements Panel {
        /** Constructs an instance of the panel. */
        public TabbedDisplayPanel() {
            super(BOTTOM);
            setFocusable(false);
            setBorder(new EmptyBorder(0, 0, -4, 0));
        }

        @Override
        public void removeTabAt(int index) {
            // removes the editor panel from the map
            boolean isIndexSelected = getSelectedIndex() == index;
            Component panel = getComponentAt(index);
            super.removeTabAt(index);
            // set this resource as the main tab
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
                getComponentAt(index).requestFocus();
            }
        }

        @Override
        public Display getDisplay() {
            return ResourceDisplay.this;
        }
    }

    /** Interface for the main tab on this display. */
    protected static interface MainTab extends Tab {
        /** 
         * Changes this tab so as to display a given, named resource, if it exists.
         * @param name the name of the resource; if {@code null}, the display
         * should be emptied
         * @return if {@code false}, no resource with the given name
         * exists (and so the main tab was not changed)
         */
        public boolean setResource(String name);

        /** 
         * Removes a resource that is currently being edited from the
         * main tab and its internal data structures.
         * @param name the name of the resource to be removed
         * @return {@code true} if this was the currently displayed resource
         */
        public boolean removeResource(String name);
    }
}
