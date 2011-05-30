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

import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/** 
 * The main panel of the simulator.
 * Offers functionality for detaching and reattaching components in 
 * separate windows. 
 */
public class DisplaysPanel extends JTabbedPane implements SimulatorListener {
    /** Constructs a fresh instance, for a given simulator. */
    public DisplaysPanel(final Simulator simulator) {
        super(TOP);
        this.simulator = simulator;
        addTab(getStateDisplay());
        addTab(getRuleDisplay());
        addTab(simulator.getLtsPanel());
        addTab(simulator.getControlPanel());
        addTab(getTypeDisplay());
        if (Groove.INCLUDE_PROLOG) {
            addTab(simulator.getPrologPanel());
        }
        setSelectedIndex(0);
        installListeners();
        simulator.getModel().addListener(this, Change.DISPLAY, Change.HOST,
            Change.RULE, Change.TYPE);
        setVisible(true);
    }

    private void addTab(Display component) {
        DisplayKind kind = component.getKind();
        this.tabbedPanelMap.put(kind, component);
        if (kind != DisplayKind.RULE) {
            JPanel listPanel = component.getListPanel();
            this.listKindMap.put(listPanel, kind);
            this.tabbedListMap.put(kind, listPanel);
        }
        attach(component);
    }

    private void installListeners() {
        // adds a mouse listener that offers a popup menu with a detach action
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = indexAtLocation(e.getX(), e.getY());
                if (index >= 0 && e.getButton() == MouseEvent.BUTTON3) {
                    Display panel = (Display) getComponentAt(index);
                    createDetachMenu(panel).show(DisplaysPanel.this, e.getX(),
                        e.getY());
                }
            }
        });
        // add the change listener only now, as otherwise the add actions
        // above will trigger it
        this.tabListener = new ChangeListener() {
            public void stateChanged(ChangeEvent evt) {
                DisplaysPanel.this.changingTabs = true;
                getSimulatorModel().setDisplay(getSelectedDisplay());
                DisplaysPanel.this.changingTabs = false;
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

    /** Returns the state and graph display shown on this panel. */
    public StateDisplay getStateDisplay() {
        if (this.stateDisplay == null) {
            this.stateDisplay = new StateDisplay(this.simulator);
        }
        return this.stateDisplay;
    }

    /** Returns the rule display shown on this panel. */
    public RuleDisplay getRuleDisplay() {
        if (this.ruleDisplay == null) {
            this.ruleDisplay = new RuleDisplay(this.simulator);
        }
        return this.ruleDisplay;
    }

    /** Returns the type display shown on this panel. */
    public TypeDisplay getTypeDisplay() {
        if (this.typeDisplay == null) {
            this.typeDisplay = new TypeDisplay(this.simulator);
        }
        return this.typeDisplay;
    }

    /** Tabbed pane holding the rule list.
     * @see Display#getListPanel()
     */
    public JTabbedPane getRuleListPanel() {
        if (this.rulePanel == null) {
            this.rulePanel = new JTabbedPane();
        }
        return this.rulePanel;
    }

    /** Tabbed pane holding the list panels of the various components on the 
     * {@link DisplaysPanel}, except for the rule list.
     * @see Display#getListPanel()
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
        if (changes.contains(Change.DISPLAY)) {
            if (!this.changingTabs) {
                Display panel = this.tabbedPanelMap.get(source.getDisplay());
                if (indexOfComponent(panel.getPanel()) >= 0) {
                    setSelectedComponent(panel.getPanel());
                }
            }
            JPanel listPanel = this.tabbedListMap.get(source.getDisplay());
            if (listPanel != null
                && getListsPanel().indexOfComponent(listPanel) >= 0) {
                getListsPanel().setSelectedComponent(listPanel);
            }
        } else {
            Display display = (Display) getSelectedComponent();
            String changedTo = null;
            switch (display.getKind()) {
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
                Display panel = this.tabbedPanelMap.get(source.getDisplay());
                if (panel != null && indexOfComponent(panel.getPanel()) >= 0) {
                    setSelectedComponent(panel.getPanel());
                }
            }
        }
        activateListeners();
    }

    /** Returns the kind of tab on top of the tabbed pane. */
    public DisplayKind getSelectedDisplay() {
        return ((Display) getSelectedComponent()).getKind();
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
    public Display getDisplayFor(DisplayKind tabKind) {
        return this.tabbedPanelMap.get(tabKind);
    }

    /** Returns the panel corresponding to a certain graph role. */
    public JGraphPanel<?> getPanelFor(GraphRole role) {
        DisplayKind tabKind = null;
        switch (role) {
        case HOST:
            tabKind = DisplayKind.HOST;
            break;
        case RULE:
            tabKind = DisplayKind.RULE;
            break;
        case TYPE:
            tabKind = DisplayKind.TYPE;
            break;
        default:
            assert false;
        }
        TabbedDisplay display = (TabbedDisplay) getDisplayFor(tabKind);
        return display.getMainPanel();
    }

    /** Reattaches a component at its proper place. */
    public void attach(Display display) {
        if (indexOfComponent(display.getPanel()) >= 0) {
            // the component is already attached; don't do anything
            return;
        }
        DisplayKind myKind = display.getKind();
        int index;
        for (index = 0; index < getTabCount(); index++) {
            DisplayKind otherKind = ((Display) getComponentAt(index)).getKind();
            if (otherKind == null || myKind.compareTo(otherKind) < 0) {
                // insert here
                break;
            }
        }
        insertTab(null, null, display.getPanel(), myKind.getTip(), index);
        JLabel tabComponent = new JLabel(myKind.getTabIcon());
        tabComponent.setVerticalTextPosition(JLabel.BOTTOM);
        tabComponent.setFocusable(false);
        setTabComponentAt(index, tabComponent);
        // now add the corresponding list panel
        JPanel listPanel = display.getListPanel();
        JTabbedPane tabbedPane = null;
        if (display.getKind() == DisplayKind.RULE) {
            tabbedPane = getRuleListPanel();
            index = 0;
        } else if (listPanel != null
            && getListsPanel().indexOfComponent(listPanel) < 0) {
            tabbedPane = getListsPanel();
            for (index = 0; index < getListsPanel().getTabCount(); index++) {
                DisplayKind otherKind =
                    this.listKindMap.get(getListsPanel().getComponentAt(index));
                if (otherKind == null || myKind.compareTo(otherKind) < 0) {
                    // insert here
                    break;
                }
            }
        }
        if (tabbedPane != null) {
            tabbedPane.insertTab(null, myKind.getTabIcon(), listPanel,
                myKind.getTip(), index);
        }
        setSelectedComponent(display.getPanel());
    }

    /** Detaches a component (presumably shown as a tab) into its own window. */
    public void detach(Display display) {
        revertSelection();
        new DisplayWindow(this, display);
    }

    /** Returns the parent frame of an editor panel, if the editor is not
     * displayed in a tab. */
    public JFrame getFrameOf(Component panel) {
        if (indexOfComponent(panel) < 0) {
            Container window = panel.getParent();
            while (!(window instanceof DisplayWindow)) {
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
    private JPopupMenu createDetachMenu(final Display component) {
        assert indexOfComponent(component.getPanel()) >= 0;
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
            int selectedIndex = getSelectedIndex();
            if (selectedIndex >= 0) {
                setTabEnabled(selectedIndex, false);
            }
            setTabEnabled(index, true);
            this.lastSelected = getSelectedComponent();
            super.setSelectedIndex(index);
        }
        getSelectedComponent().requestFocusInWindow();
    }

    private void setTabEnabled(int index, boolean enabled) {
        JLabel label = (JLabel) getTabComponentAt(index);
        if (label != null) {
            label.setFont(label.getFont().deriveFont(Font.BOLD));
            label.setEnabled(enabled);
            label.setText(enabled
                    ? ((Display) getComponentAt(index)).getKind().getTitle()
                    : null);
        }
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

    /**
     * Makes an attempt to dispose all editors from all tabs.
     * @return {@code true} if all editors were disposed.
     */
    public boolean disposeAllEditors() {
        boolean result = getStateDisplay().disposeAllEditors();
        if (result) {
            result = getRuleDisplay().disposeAllEditors();
        }
        if (result) {
            result = getTypeDisplay().disposeAllEditors();
        }
        return result;
    }

    /** Convenience method to return the simulator model. */
    private SimulatorModel getSimulatorModel() {
        return this.simulator.getModel();
    }

    private final Simulator simulator;
    /** Mapping from simulator tab list panels to their tab kinds. */
    private final Map<JPanel,DisplayKind> listKindMap =
        new HashMap<JPanel,DisplayKind>();
    /** Mapping from tab kinds to the corresponding panels. */
    private final Map<DisplayKind,Display> tabbedPanelMap =
        new HashMap<DisplayKind,Display>();
    /** Mapping from tab kinds to the corresponding (possibly {@code null})
     * label lists. */
    private final Map<DisplayKind,JPanel> tabbedListMap =
        new HashMap<DisplayKind,JPanel>();

    /** The rule tab shown on this panel. */
    private StateDisplay stateDisplay;
    /** The rule tab shown on this panel. */
    private RuleDisplay ruleDisplay;
    /** The type tab shown on this panel. */
    private TypeDisplay typeDisplay;
    /** Panel with the rule list. */
    private JTabbedPane rulePanel;
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

    /** Type of components in the panel. */
    public static enum DisplayKind {
        /** State panel. */
        HOST(Icons.GRAPH_FRAME_ICON, Icons.GRAPH_FILE_ICON,
                Icons.EDIT_GRAPH_ICON, Icons.GRAPH_LIST_ICON, "Graphs",
                "Current graph state"),
        /** Rule panel. */
        RULE(Icons.RULE_FRAME_ICON, Icons.RULE_FILE_ICON, Icons.EDIT_RULE_ICON,
                Icons.RULE_LIST_ICON, "Rules", "Selected rule"),
        /** LTS panel. */
        LTS(Icons.LTS_FRAME_ICON, null, null, null, "State space",
                "Labelled transition system"),
        /** Type panel. */
        TYPE(Icons.TYPE_FRAME_ICON, Icons.TYPE_FILE_ICON, Icons.EDIT_TYPE_ICON,
                Icons.TYPE_LIST_ICON, "Types", "Type graphs"),
        /** Control panel. */
        CONTROL(Icons.CONTROL_FRAME_ICON, Icons.CONTROL_FILE_ICON,
                Icons.EDIT_CONTROL_ICON, null, "Control",
                "Control specifications"),
        /** Prolog panel. */
        PROLOG(Icons.PROLOG_FRAME_ICON, null, Icons.EDIT_ICON, null, "Prolog",
                "Prolog programs");

        private DisplayKind(ImageIcon tabIcon, ImageIcon frameIcon,
                ImageIcon editIcon, ImageIcon listIcon, String title, String tip) {
            this.tabIcon = tabIcon;
            this.frameIcon = frameIcon;
            this.editIcon = editIcon;
            this.listIcon = listIcon;
            this.title = title;
            this.tip = tip;
        }

        /** Returns the icon that should be used on the tab for a display of this kind. */
        public final ImageIcon getTabIcon() {
            return this.tabIcon;
        }

        /** Returns the icon that should be used in case this display is detached. */
        public final ImageIcon getFrameIcon() {
            return this.frameIcon;
        }

        /** Returns the icon that should be used for the label list. */
        public final ImageIcon getListIcon() {
            return this.listIcon;
        }

        /** Returns the icon that should be used for editors in this display. */
        public final ImageIcon getEditIcon() {
            return this.editIcon;
        }

        /** Returns the title of this display. */
        public final String getTitle() {
            return this.title;
        }

        /** Returns the tool tip description for this display. */
        public final String getTip() {
            return this.tip;
        }

        /** Returns the graph role corresponding to this tab kind, if any. */
        public final GraphRole getGraphRole() {
            return GraphRole.valueOf(name());
        }

        private final ImageIcon tabIcon;
        private final ImageIcon frameIcon;
        private final ImageIcon editIcon;
        private final ImageIcon listIcon;
        private final String title;
        private final String tip;
    }
}
