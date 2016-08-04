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
package groove.gui.display;

import java.awt.BorderLayout;
import java.awt.CardLayout;
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
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;

import groove.grammar.Action;
import groove.grammar.QualName;
import groove.grammar.aspect.AspectGraph;
import groove.grammar.model.GrammarModel;
import groove.grammar.model.NamedResourceModel;
import groove.grammar.model.ResourceKind;
import groove.grammar.model.ResourceModel;
import groove.grammar.model.RuleModel;
import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.gui.SimulatorListener;
import groove.gui.SimulatorModel;
import groove.gui.SimulatorModel.Change;
import groove.gui.action.CancelEditAction;
import groove.gui.action.CopyAction;
import groove.gui.action.EnableUniqueAction;
import groove.gui.action.SaveAction;
import groove.gui.action.SimulatorAction;
import groove.gui.tree.ResourceTree;
import groove.io.HTMLConverter;

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
    ResourceDisplay(Simulator simulator, ResourceKind resource) {
        super(simulator, DisplayKind.toDisplay(resource));
    }

    @Override
    protected void buildDisplay() {
        add(getTabPane());
    }

    /**
     * Returns the panel holding all display tabs.
     * This may or may not be the same as #getDisplayPanel().
     */
    final public JTabbedPane getTabPane() {
        if (this.tabPane == null) {
            this.tabPane = createTabPane();
        }
        return this.tabPane;
    }

    /**
     * Returns the panel holding all display tabs.
     * This may or may not be the same as #getDisplayPanel().
     */
    final protected MyTabbedPane createTabPane() {
        return new MyTabbedPane();
    }

    /** Callback method to create the resource list. */
    @Override
    protected JTree createList() {
        return new ResourceTree(this);
    }

    @Override
    protected void resetList() {
        ResourceTree list = (ResourceTree) getList();
        if (list != null) {
            list.dispose();
        }
        super.resetList();
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
        ResourceKind kind = getResourceKind();
        if (kind.isEnableable()) {
            result.add(getEnableButton());
            if (getResourceKind() == ResourceKind.HOST || getResourceKind() == ResourceKind.TYPE
                || getResourceKind() == ResourceKind.PROLOG
                || getResourceKind() == ResourceKind.CONTROL) {
                result.add(getEnableUniqueAction());
            }
        }
        if (kind == ResourceKind.GROOVY) {
            result.add(getActions().getExecGroovyAction());
        }
        return result;
    }

    @Override
    protected JComponent createInfoPanel() {
        JPanel result = new JPanel();
        result.setLayout(new CardLayout());
        result.add(getSingleInfoPanel(), this.SINGLE_INFO_KEY);
        result.add(getSplitInfoPanel(), this.SPLIT_INFO_KEY);
        result.setBorder(null);
        return result;
    }

    /**
     * Lazily creates and returns a monolithic panel used as info panel in case there is
     * only an upper panel.
     */
    private JPanel getSingleInfoPanel() {
        JPanel result = this.singleInfoPanel;
        if (result == null) {
            this.singleInfoPanel = result = new JPanel();
            result.setLayout(new BorderLayout());
            result.setBorder(null);
        }
        return result;
    }

    /**
     * Lazily creates and returns a split panel used as info panel in case there is
     * an upper and a lower panel.
     */
    private JSplitPane getSplitInfoPanel() {
        JSplitPane result = this.splitInfoPanel;
        if (result == null) {
            this.splitInfoPanel = result = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
            result.setContinuousLayout(true);
            result.setBorder(null);
        }
        return result;
    }

    /** Adjust the info panel by retrieving upper and lower info subpanels from the selected tab. */
    protected void buildInfoPanel() {
        JComponent upperInfoPanel = null;
        JComponent lowerInfoPanel = null;
        ResourceTab tab = getSelectedTab();
        if (tab != null) {
            upperInfoPanel = tab.getUpperInfoPanel();
            lowerInfoPanel = tab.getLowerInfoPanel();
        }
        JPanel infoPanel = (JPanel) getInfoPanel();
        String key;
        if (lowerInfoPanel == null || !lowerInfoPanel.isEnabled()) {
            // if we switch from split to single, freeze the divider location
            if (upperInfoPanel != null && upperInfoPanel.getParent() == getSplitInfoPanel()) {
                this.frozenDividerPos = getSplitInfoPanel().getDividerLocation();
            }
            getSingleInfoPanel().removeAll();
            if (upperInfoPanel != null) {
                getSingleInfoPanel().add(upperInfoPanel, BorderLayout.CENTER);
                getSingleInfoPanel().validate();
                getSingleInfoPanel().repaint();
            }
            key = this.SINGLE_INFO_KEY;
        } else {
            JSplitPane splitInfoPanel = getSplitInfoPanel();
            int dividerPos = this.frozenDividerPos;
            this.frozenDividerPos = 0;
            if (dividerPos == 0) {
                dividerPos = splitInfoPanel.getDividerLocation();
            }
            splitInfoPanel.setTopComponent(upperInfoPanel);
            splitInfoPanel.setBottomComponent(lowerInfoPanel);
            splitInfoPanel.setDividerLocation(dividerPos);
            key = this.SPLIT_INFO_KEY;
        }
        ((CardLayout) infoPanel.getLayout()).show(infoPanel, key);
    }

    /** The divider location of the split info panel. */
    private int frozenDividerPos;

    /** Gets the currently selected tab index of the upper or lower info panel. */
    int getInfoTabIndex(boolean upper) {
        return upper ? this.upperInfoTabIndex : this.lowerInfoTabIndex;
    }

    /** Changes the currently selected tab index of the upper or lower info panel. */
    public void setInfoTabIndex(boolean upper, int index) {
        if (upper) {
            this.upperInfoTabIndex = index;
        } else {
            this.lowerInfoTabIndex = index;
        }
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

    /** Returns the enable unique action associated with this resource. */
    protected final EnableUniqueAction getEnableUniqueAction() {
        return getActions().getEnableUniqueAction(getResourceKind());
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

    /** Callback to obtain the main tab of this display. */
    public ResourceTab getMainTab() {
        if (this.mainTab == null) {
            this.mainTab = createMainTab();
        }
        return this.mainTab;
    }

    /** Callback factory method for the main tab. */
    protected ResourceTab createMainTab() {
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
    public void startEditResource(QualName name) {
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
            buildInfoPanel();
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
    protected ResourceTab createEditorTab(QualName name) {
        ResourceKind kind = getResourceKind();
        if (kind.isGraphBased()) {
            AspectGraph graph = getSimulatorModel().getStore()
                .getGraphs(getResourceKind())
                .get(name);
            GraphEditorTab result = new GraphEditorTab(this, graph.getRole());
            result.setGraph(graph);
            return result;
        } else {
            String program = getSimulatorModel().getStore()
                .getTexts(getResourceKind())
                .get(name);
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
    public boolean saveEditor(QualName name, boolean confirm, boolean dispose) {
        boolean result = true;
        ResourceTab editor = getEditors().get(name);
        if (editor != null) {
            result = editor.saveEditor(confirm, dispose);
        }
        return result;
    }

    /** Returns a list of all editor panels currently displayed. */
    protected final Map<QualName,ResourceTab> getEditors() {
        return this.editorMap;
    }

    /** Indicates if the resource with a given name is currently being edited. */
    protected final boolean isEdited(QualName name) {
        return getEditors().containsKey(name);
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
        for (ResourceTab editor : new ArrayList<>(getEditors().values())) {
            result = editor.saveEditor(true, dispose);
            if (!result) {
                break;
            }
        }
        return result;
    }

    /** Returns the editor for a resource with a given name, if any. */
    public ResourceTab getEditor(QualName name) {
        return getEditors().get(name);
    }

    /** Returns the currently selected tab, or {@code null} if no editor is selected. */
    public ResourceTab getSelectedTab() {
        return (ResourceTab) getTabPane().getSelectedComponent();
    }

    @Override
    public void update(SimulatorModel source, SimulatorModel oldModel, Set<Change> changes) {
        if (suspendListening()) {
            if (changes.contains(Change.GRAMMAR)) {
                updateGrammar(source.getGrammar(), source.getGrammar() != oldModel.getGrammar());
            }
            NamedResourceModel<?> resourceModel = source.getResource(getResourceKind());
            getEnableButton().setSelected(resourceModel != null && resourceModel.isEnabled());
            selectResource(source.getSelected(getResourceKind()));
            buildInfoPanel();
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
            ResourceTab tab = (ResourceTab) getTabPane().getComponentAt(i);
            if (tab.isEditor() && fresh) {
                tab.dispose();
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
    @Override
    protected void installListeners() {
        getSimulatorModel().addListener(this, Change.GRAMMAR, Change.toChange(getResourceKind()));
        // adds a mouse listener that offers a popup menu with a detach action
        getTabPane().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    int index = getTabPane().indexAtLocation(e.getX(), e.getY());
                    if (index >= 0) {
                        ResourceTab tab = (ResourceTab) getTabPane().getComponentAt(index);
                        if (tab != getMainTab()) {
                            createDetachMenu(tab).show(ResourceDisplay.this, e.getX(), e.getY());
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
                for (ResourceTab editor : new ArrayList<>(getEditors().values())) {
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
            QualName name = selection == null ? null : QualName.parse(selection.getName());
            getSimulatorModel().doSelect(getResourceKind(), name);
            buildInfoPanel();
            activateListening();
        }
    }

    /**
     * Switches to the display of a given (named) resource,
     * either in an open editor or in the main tab.
     * @return the tab in which the resource is shown
     */
    public ResourceTab selectResource(QualName name) {
        ResourceTab result;
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
        setEnabled(name != null);
        return result;
    }

    /**
     * Removes the main panel from the display.
     */
    protected void removeMainTab() {
        getTabPane().remove(getMainTab());
    }

    /**
     * Sets the main panel  to a given (named) graph.
     */
    protected void selectMainTab(QualName name) {
        if (getMainTab().setResource(name)) {
            TabLabel tabLabel = getMainTab().getTabLabel();
            int index = getTabPane().indexOfComponent(getMainTab());
            if (index < 0) {
                index = getMainTabIndex();
                getTabPane().add(getMainTab(), index);
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
    final public Icon getListIcon(QualName name) {
        Icon result;
        if (this.editorMap.containsKey(name)) {
            result = Icons.getListEditIcon(getResourceKind());
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
    public final String getLabelText(QualName name) {
        return getLabelText(name, "", getResource(name).isEnabled());
    }

    /**
     * Adds HTML formatting to the label text for the main display.
     * Callback method from {@link #getLabelText(QualName)}.
     * @param name the name of the displayed object. This determines the
     * decoration
     * @param suffix text to appear after the name
     * @param enabled flag indicating if the name should be shown as enabled
     */
    public String getLabelText(QualName name, String suffix, boolean enabled) {
        NamedResourceModel<?> model = getResource(name);
        StringBuilder result = new StringBuilder(model.getLastName());
        if (model instanceof RuleModel && ((RuleModel) model).isProperty()) {
            HTMLConverter.ITALIC_TAG.on(result);
            Action.Role actionRole = ((RuleModel) model).getRole();
            if (actionRole.hasColor()) {
                HTMLConverter.createColorTag(actionRole.getColor())
                    .on(result);
            }
        }
        result.append(suffix);
        if (isEdited(name)) {
            getEditors().get(name)
                .decorateText(result);
        }
        if (enabled) {
            if (getKind() != DisplayKind.RULE) {
                HTMLConverter.STRONG_TAG.on(result);
            }
        } else {
            result.insert(0, "(");
            result.append(")");
        }
        return result.toString();
    }

    /**
     * Callback method to construct the tool tip for a given
     * resource.
     */
    protected String getToolTip(QualName name) {
        NamedResourceModel<?> model = getResource(name);
        boolean enabled = model != null && model.isEnabled();
        return getToolTip(name, enabled);
    }

    /** Returns the tool tip text for a resource, depending on its enabling. */
    private String getToolTip(QualName name, boolean enabled) {
        String result = enabled ? this.enabledText : this.disabledText;
        if (result == null) {
            this.enabledText = String.format("Enabled %s; doubleclick to edit",
                getResourceKind().getDescription());
            this.disabledText = String.format("Disabled %s; doubleclick to edit",
                getResourceKind().getDescription());
            result = enabled ? this.enabledText : this.disabledText;
        }
        return result;
    }

    /** Index of the pain panel. This returns {@code 0} by default. */
    protected int getMainTabIndex() {
        return 0;
    }

    /** Indicates if a given (named) resource has errors. */
    final public boolean hasError(QualName name) {
        boolean result;
        if (this.editorMap.containsKey(name)) {
            result = this.editorMap.get(name)
                .hasErrors();
        } else {
            ResourceModel<?> model = getResource(name);
            result = model != null && model.hasErrors();
        }
        return result;
    }

    /** Retrieves the resource model for a given name from the grammar. */
    public final NamedResourceModel<?> getResource(QualName name) {
        return getSimulatorModel().getGrammar()
            .getResource(getResourceKind(), name);
    }

    private JTabbedPane tabPane;
    private JPanel singleInfoPanel;
    private final String SINGLE_INFO_KEY = "Single info panel";
    private JSplitPane splitInfoPanel;
    private final String SPLIT_INFO_KEY = "Split info panel";
    private JToggleButton enableButton;
    /** Index of the selected tab at the upper info panel. */
    private int upperInfoTabIndex;
    /** Index of the selected tab at the lower info panel. */
    private int lowerInfoTabIndex;
    /** Mapping from graph names to editors for those graphs. */
    private final Map<QualName,ResourceTab> editorMap = new HashMap<>();

    /** Flag indicating that the listeners are currently active. */
    private boolean listening;
    private ResourceTab mainTab;

    /** Tool tip text for an enabled resource. */
    private String enabledText;
    /** Tool tip text for a disabled resource. */
    private String disabledText;

    private class MyTabbedPane extends JTabbedPane {
        /** Constructs an instance of the panel. */
        public MyTabbedPane() {
            super(BOTTOM);
            setFocusable(false);
            setBorder(new EmptyBorder(0, 0, -4, 0));
        }

        @Override
        public void removeTabAt(int index) {
            // removes the editor panel from the map
            boolean isIndexSelected = getSelectedIndex() == index;
            ResourceTab panel = (ResourceTab) getComponentAt(index);
            super.removeTabAt(index);
            // set this resource as the main tab
            QualName name = panel.getQualName();
            if (getEditors().remove(name) != null && isIndexSelected) {
                selectMainTab(name);
            }
            // make sure the tab component of the selected tab is enabled
            setTabEnabled(getSelectedIndex(), true);
            setEnabled(getTabCount() > 0);
            ListPanel listPanel = getListPanel();
            if (listPanel != null) {
                listPanel.repaint();
            }
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
    }
}
