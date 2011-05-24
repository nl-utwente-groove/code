/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2010 University of Twente
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

import groove.graph.GraphRole;
import groove.gui.SimulatorModel.Change;
import groove.util.Groove;
import groove.util.Pair;
import groove.view.aspect.AspectGraph;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/** 
 * The main panel of the simulator.
 * Offers functionality for detaching and reattaching components in 
 * separate windows. 
 */
public class SimulatorPanel extends JTabbedPane implements SimulatorListener {
    /** Constructs a fresh instance, for a given simulator. */
    public SimulatorPanel(final Simulator simulator) {
        this.simulator = simulator;
        addTab(simulator.getStatePanel());
        addTab(simulator.getRulePanel());
        addTab(simulator.getLtsPanel());
        addTab(simulator.getControlPanel());
        addTab(simulator.getTypePanel());
        if (Groove.INCLUDE_PROLOG) {
            addTab(simulator.getPrologPanel());
        }
        installListeners();
        simulator.getModel().addListener(this, Change.TAB, Change.HOST,
            Change.RULE, Change.TYPE);
        setVisible(true);
    }

    private void addTab(SimulatorTab component) {
        TabKind kind = component.getKind();
        JPanel mainPanel = component.getMainPanel();
        this.tabbedPanelMap.put(kind, component);
        addTab(null, kind.getTabIcon(), mainPanel, kind.getName());
        JPanel listPanel = component.getListPanel();
        if (listPanel != null) {
            this.listKindMap.put(listPanel, kind);
            this.tabbedListMap.put(kind, listPanel);
            getListsPanel().addTab(null, kind.getTabIcon(), listPanel,
                kind.getName());
        }
    }

    private void installListeners() {
        // adds a mouse listener that offers a popup menu with a detach action
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = indexAtLocation(e.getX(), e.getY());
                if (index >= 0 && e.getButton() == MouseEvent.BUTTON3) {
                    SimulatorTab panel = (SimulatorTab) getComponentAt(index);
                    createDetachMenu(panel).show(SimulatorPanel.this, e.getX(),
                        e.getY());
                }
            }
        });
        // add the change listener only now, as otherwise the add actions
        // above will trigger it
        this.tabListener = new ChangeListener() {
            public void stateChanged(ChangeEvent evt) {
                SimulatorPanel.this.changingTabs = true;
                getSimulatorModel().setTabKind(getSelectedTab());
                SimulatorPanel.this.changingTabs = false;
            }
        };
        activateListeners();
    }

    /** Activates the listeners that could cause a cyclic listener dependency. */
    private void activateListeners() {
        addChangeListener(this.tabListener);
    }

    /** Suspends the listeners that could cause a cyclic listener dependency. */
    private void suspendListeners() {
        removeChangeListener(this.tabListener);
    }

    /** Tabbed pane holding the list panels of the various components on the 
     * {@link SimulatorPanel}.
     * @see SimulatorTab#getListPanel()
     */
    public JTabbedPane getListsPanel() {
        if (this.listsPanel == null) {
            this.listsPanel = new JTabbedPane();
        }
        return this.listsPanel;
    }

    @Override
    public void update(SimulatorModel source, SimulatorModel oldModel,
            Set<Change> changes) {
        suspendListeners();
        if (changes.contains(Change.TAB)) {
            if (!this.changingTabs) {
                // maybe we should move to an open editor tab instead
                SimulatorTab panel = null;
                GraphRole role = source.getTabKind().getGraphRole();
                SimulatorTab component =
                    this.tabbedPanelMap.get(source.getTabKind());
                if (role != null) {
                    panel = getEditor(role, component.getCurrent());
                }
                if (panel == null) {
                    panel = component;
                }
                if (indexOfComponent(panel.getMainPanel()) >= 0) {
                    setSelectedComponent(panel.getMainPanel());
                }
            }
            JPanel listPanel = this.tabbedListMap.get(source.getTabKind());
            if (listPanel != null
                && getListsPanel().indexOfComponent(listPanel) >= 0) {
                getListsPanel().setSelectedComponent(listPanel);
            }
        } else {
            SimulatorTab tab = (SimulatorTab) getSelectedComponent();
            String changedTo = null;
            switch (tab.getKind()) {
            case HOST:
                if (changes.contains(Change.HOST)
                    && getSimulatorModel().hasHost()) {
                    changedTo = getSimulatorModel().getHost().getName();
                }
                break;
            case RULE:
                if (changes.contains(Change.RULE)
                    && getSimulatorModel().getRule() != null) {
                    changedTo = getSimulatorModel().getRule().getName();
                }
                break;
            case TYPE:
                if (changes.contains(Change.HOST)
                    && getSimulatorModel().getType() != null) {
                    changedTo = getSimulatorModel().getType().getName();
                }
                break;
            }
            if (changedTo == null) {
                SimulatorTab panel =
                    this.tabbedPanelMap.get(source.getTabKind());
                if (panel != null
                    && indexOfComponent(panel.getMainPanel()) >= 0) {
                    setSelectedComponent(panel.getMainPanel());
                }
            } else {
                EditorPanel panel =
                    getEditor(tab.getKind().getGraphRole(), changedTo);
                if (panel != null) {
                    setSelectedComponent(panel.getMainPanel());
                }
            }
        }
        activateListeners();
    }

    /** Returns the kind of tab on top of the tabbed pane. */
    public TabKind getSelectedTab() {
        return ((SimulatorTab) getSelectedComponent()).getKind();
    }

    /**
     * Returns the currently selected graph view component, if that is a 
     * {@link JGraphPanel}. Returns {@code null} otherwise.
     */
    public JGraphPanel<?> getGraphPanel() {
        Component selectedComponent = getSelectedComponent();
        if (selectedComponent instanceof EditorPanel) {
            return ((EditorPanel) selectedComponent).getEditor().getGraphPanel();
        }
        if (!(selectedComponent instanceof JGraphPanel<?>)) {
            return null;
        } else {
            return (JGraphPanel<?>) selectedComponent;
        }
    }

    /** Returns the panel corresponding to a certain tab kind. */
    public JPanel getPanelFor(TabKind tabKind) {
        SimulatorTab tab = this.tabbedPanelMap.get(tabKind);
        return tab == null ? null : tab.getMainPanel();
    }

    /** Returns the panel corresponding to a certain graph role. */
    public JGraphPanel<?> getPanelFor(GraphRole role) {
        TabKind tabKind = null;
        switch (role) {
        case HOST:
            tabKind = TabKind.HOST;
            break;
        case RULE:
            tabKind = TabKind.RULE;
            break;
        case TYPE:
            tabKind = TabKind.TYPE;
            break;
        }
        return (JGraphPanel<?>) getPanelFor(tabKind);
    }

    /** Reattaches a component at its proper place. */
    public void attach(SimulatorTab component) {
        if (component.getKind() == TabKind.EDITOR) {
            add((EditorPanel) component);
        } else {
            TabKind myKind = component.getKind();
            int index;
            for (index = 0; index < getTabCount(); index++) {
                TabKind otherKind =
                    ((SimulatorTab) getComponentAt(index)).getKind();
                if (otherKind == null || myKind.compareTo(otherKind) < 0) {
                    // insert here
                    break;
                }
            }
            insertTab(null, myKind.getTabIcon(), component.getMainPanel(),
                myKind.getName(), index);
            JPanel listPanel = component.getListPanel();
            if (listPanel != null) {
                for (index = 0; index < getListsPanel().getTabCount(); index++) {
                    TabKind otherKind =
                        this.listKindMap.get(getListsPanel().getComponentAt(
                            index));
                    if (otherKind == null || myKind.compareTo(otherKind) < 0) {
                        // insert here
                        break;
                    }
                }
                getListsPanel().insertTab(null, myKind.getTabIcon(), listPanel,
                    myKind.getName(), index);
            }
        }
        setSelectedComponent(component.getMainPanel());
    }

    /** Detaches a component (presumably shown as a tab) into its own window. */
    public void detach(SimulatorTab component) {
        revertSelection();
        new JGraphWindow(component);
    }

    /** Adds a tab for a given editor panel. */
    public void add(EditorPanel panel) {
        Icon icon = null;
        switch (panel.getGraph().getRole()) {
        case HOST:
            icon = Icons.GRAPH_MODE_ICON;
            break;
        case RULE:
            icon = Icons.RULE_MODE_ICON;
            break;
        case TYPE:
            icon = Icons.TYPE_MODE_ICON;
        }
        addTab("", panel);
        int index = indexOfComponent(panel);
        Component tabComponent =
            new ButtonTabComponent(panel, icon, panel.getTitle());
        setTabComponentAt(index, tabComponent);
        setSelectedIndex(index);
    }

    /** 
     * Returns the tab component of a given editor panel, if it 
     * is currently attached to the simulator panel.
     * @return the tab component, of {@code null} if the editor panel
     * is not attached
     */
    public ButtonTabComponent getTabComponentOf(EditorPanel panel) {
        int index = indexOfComponent(panel);
        return index >= 0 ? (ButtonTabComponent) getTabComponentAt(index)
                : null;
    }

    /** Returns the parent frame of an editor panel, if the editor is not
     * displayed in a tab. */
    public JFrame getFrameOf(Component panel) {
        if (indexOfComponent(panel) < 0) {
            Container window = panel.getParent();
            while (!(window instanceof JGraphWindow)) {
                window = window.getParent();
            }
            return (JFrame) window;
        } else {
            return null;
        }
    }

    /**
     * If the component is not shown on a tab but in a separate frame,
     * disposes the frame.
     */
    @Override
    public void remove(Component component) {
        if (indexOfComponent(component) < 0) {
            JFrame frame = getFrameOf(component);
            if (frame != null) {
                frame.dispose();
            }
        } else {
            if (getSelectedComponent() == component) {
                revertSelection();
            }
            super.remove(component);
        }
    }

    /** Creates a popup menu with a detach action for a given component. */
    private JPopupMenu createDetachMenu(final SimulatorTab component) {
        assert indexOfComponent(component.getMainPanel()) >= 0;
        JPopupMenu result = new JPopupMenu();
        result.add(new AbstractAction("Detach") {
            @Override
            public void actionPerformed(ActionEvent e) {
                detach(component);
            }
        });
        return result;
    }

    @Override
    public void setSelectedIndex(int index) {
        if (getSelectedIndex() != index) {
            this.lastSelected = getSelectedComponent();
            super.setSelectedIndex(index);
        }
        getSelectedComponent().requestFocusInWindow();
    }

    /** Resets the selected tab to the one before the last call to {@link #setSelectedIndex(int)}. */
    public void revertSelection() {
        if (this.lastSelected != null
            && indexOfComponent(this.lastSelected) >= 0) {
            setSelectedComponent(this.lastSelected);
        } else {
            this.lastSelected = null;
        }
    }

    /** Returns a list of all editor panels currently displayed. */
    public Map<Pair<String,GraphRole>,EditorPanel> getEditors() {
        Map<Pair<String,GraphRole>,EditorPanel> result =
            new LinkedHashMap<Pair<String,GraphRole>,EditorPanel>();
        for (int i = 0; i < getTabCount(); i++) {
            if (getComponentAt(i) instanceof EditorPanel) {
                EditorPanel panel = (EditorPanel) getComponentAt(i);
                AspectGraph graph = panel.getGraph();
                result.put(Pair.newPair(graph.getName(), graph.getRole()),
                    panel);
            }
        }
        return result;
    }

    /** Returns the editor for a graph with given role and name. */
    public EditorPanel getEditor(GraphRole role, String name) {
        EditorPanel result = null;
        for (int i = 0; i < getTabCount(); i++) {
            if (getComponentAt(i) instanceof EditorPanel) {
                EditorPanel panel = (EditorPanel) getComponentAt(i);
                AspectGraph graph = panel.getGraph();
                if (graph.getRole() == role && graph.getName().equals(name)) {
                    result = panel;
                    break;
                }
            }
        }
        return result;
    }

    /** 
     * Adds an editor panel for the given graph, or selects the 
     * one that already exists.
     */
    public void doEditGraph(final AspectGraph graph) {
        EditorPanel result = null;
        // look if an editor already exists for the graph
        for (int i = 0; i < getTabCount(); i++) {
            Component view = getComponentAt(i);
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
            result = addEditorPanel(graph);
        }
        setSelectedComponent(result);
    }

    /** Creates and adds an editor panel for the given graph. */
    private EditorPanel addEditorPanel(AspectGraph graph) {
        final EditorPanel result = new EditorPanel(this.simulator, graph);
        add(result);
        result.start();
        return result;
    }

    /** 
     * Attempts to disposes the editor for certain aspect graphs, if any.
     * This is done in response to a change in the graph outside the editor.
     * @param graphs the graphs that are about to be changed and whose editor 
     * therefore needs to be disposed
     * @return {@code true} if the operation was not cancelled
     */
    public boolean disposeEditors(AspectGraph... graphs) {
        boolean result = true;
        Map<Pair<String,GraphRole>,EditorPanel> editors = getEditors();
        for (AspectGraph graph : graphs) {
            EditorPanel editor =
                editors.get(Pair.newPair(graph.getRole(), graph.getName()));
            if (editor != null) {
                result = editor.doCancel();
                if (!result) {
                    break;
                }
            }
        }
        return result;
    }

    /** 
     * Attempts to save the dirty editors, asking the user what should happen.
     * Disposes the editors if the action is not cancelled.
     * @return {@code true} if the operation was not cancelled
     */
    public boolean disposeAllEditors() {
        boolean result = true;
        for (EditorPanel editor : getEditors().values()) {
            result = editor.doCancel();
            if (!result) {
                break;
            }
        }
        return result;
    }

    /** Tests if there is a dirty editor. */
    public boolean isEditorDirty() {
        boolean result = false;
        for (EditorPanel editor : getEditors().values()) {
            if (editor.isDirty() && !editor.isSaving()) {
                result = true;
                break;
            }
        }
        return result;
    }

    /** Convenience method to return the simulator model. */
    private SimulatorModel getSimulatorModel() {
        return this.simulator.getModel();
    }

    private final Simulator simulator;
    /** Mapping from simulator tab list panels to their tab kinds. */
    private final Map<JPanel,TabKind> listKindMap =
        new HashMap<JPanel,TabKind>();
    /** Mapping from tab kinds to the corresponding panels. */
    private final Map<TabKind,SimulatorTab> tabbedPanelMap =
        new HashMap<TabKind,SimulatorTab>();
    /** Mapping from tab kinds to the corresponding (possibly {@code null})
     * label lists. */
    private final Map<TabKind,JPanel> tabbedListMap =
        new HashMap<TabKind,JPanel>();

    /** Panel with the graphs and types lists. */
    private JTabbedPane listsPanel;
    /** Listener to tab changes. */
    private ChangeListener tabListener;
    /** Flag indicating that the {@link #tabListener} has caused a 
     * tab change, so we don't have to update.
     */
    private boolean changingTabs;
    /** The previously selected tab. */
    private Component lastSelected;

    /**
     * Independent window wrapping a JGraphPanel.
     * @author Arend Rensink
     * @version $Revision $
     */
    private class JGraphWindow extends JFrame {
        /** Constructs an instance for a given simulator and panel. */
        public JGraphWindow(final SimulatorTab panel) {
            super(panel.getKind().getName());
            JPanel listPanel = panel.getListPanel();
            if (listPanel == null) {
                getContentPane().add(panel.getMainPanel());
            } else {
                JSplitPane splitPane =
                    new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listPanel,
                        panel.getMainPanel());
                getContentPane().add(splitPane);
            }
            setAlwaysOnTop(true);
            if (panel.getKind() == TabKind.EDITOR) {
                setTitle(((EditorPanel) panel).getName());
            }
            ImageIcon icon = panel.getKind().getFrameIcon();
            if (icon != null) {
                setIconImage(icon.getImage());
            }
            pack();
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    attach(panel);
                    super.windowClosing(e);
                }
            });
            setVisible(true);
        }
    }

    /** Type of components in the panel. */
    public static enum TabKind {
        /** State panel. */
        HOST(Icons.GRAPH_FRAME_ICON, Icons.GRAPH_FILE_ICON,
                "Current graph state"),
        /** Rule panel. */
        RULE(Icons.RULE_FRAME_ICON, Icons.RULE_FILE_ICON, "Selected rule"),
        /** LTS panel. */
        LTS(Icons.LTS_FRAME_ICON, null, "Labelled transition system"),
        /** Type panel. */
        TYPE(Icons.TYPE_FRAME_ICON, Icons.TYPE_FILE_ICON, "Type graph"),
        /** Control panel. */
        CONTROL(Icons.CONTROL_FRAME_ICON, Icons.CONTROL_FILE_ICON,
                "Control specification"),
        /** Prolog panel. */
        PROLOG(Icons.PROLOG_FRAME_ICON, null, "Prolog"),
        /** Editor panel. */
        EDITOR(null, Icons.GROOVE_ICON_16x16, "Graph Editor");

        private TabKind(ImageIcon tabIcon, ImageIcon frameIcon, String name) {
            this.tabIcon = tabIcon;
            this.frameIcon = frameIcon;
            this.name = name;
        }

        /** Returns the icon that should be used for a tab of this kind. */
        public final ImageIcon getTabIcon() {
            return this.tabIcon;
        }

        /** Returns the icon that should be used in case this tab is detached. */
        public final ImageIcon getFrameIcon() {
            return this.frameIcon;
        }

        /** Returns name for this tab kind. */
        public final String getName() {
            return this.name;
        }

        /** Returns the graph role corresponding to this tab kind, if any. */
        public final GraphRole getGraphRole() {
            return GraphRole.valueOf(name());
        }

        private final ImageIcon tabIcon;
        private final ImageIcon frameIcon;
        private final String name;
    }
}
