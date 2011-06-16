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

import groove.gui.Display.ListPanel;
import groove.gui.SimulatorModel.Change;
import groove.trans.ResourceKind;

import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
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
        addTab(getHostDisplay());
        addTab(getRuleDisplay());
        addTab(simulator.getLtsDisplay());
        addTab(simulator.getControlDisplay());
        addTab(getTypeDisplay());
        addTab(simulator.getPrologDisplay());
        setSelectedIndex(0);
        installListeners();
        simulator.getModel().addListener(this, Change.DISPLAY, Change.HOST,
            Change.RULE, Change.TYPE);
        setVisible(true);
    }

    private void addTab(Display component) {
        DisplayKind kind = component.getKind();
        this.displaysMap.put(kind, component);
        this.listKindMap.put(component.getListPanel(), kind);
        attach(component);
    }

    private void installListeners() {
        // adds a mouse listener that offers a popup menu with a detach action
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = indexAtLocation(e.getX(), e.getY());
                if (index >= 0 && e.getButton() == MouseEvent.BUTTON3) {
                    Display panel = getDisplayAt(index);
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
                DisplayKind displayKind = getSelectedDisplay().getKind();
                if (displayKind != null) {
                    getSimulatorModel().setDisplay(displayKind);
                }
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
    public HostDisplay getHostDisplay() {
        if (this.stateDisplay == null) {
            this.stateDisplay = new HostDisplay(this.simulator);
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

    /** Upper tabbed pane holding the list panels of the various components on the 
     * {@link DisplaysPanel}.
     * @see Display#getListPanel()
     */
    public JTabbedPane getUpperListPanel() {
        if (this.upperListsPanel == null) {
            this.upperListsPanel = new JTabbedPane();
        }
        return this.upperListsPanel;
    }

    /** Lower tabbed pane holding the list panels of the various components on the 
     * {@link DisplaysPanel}.
     * @see Display#getListPanel()
     */
    public JTabbedPane getLowerListsPanel() {
        if (this.lowerListsPanel == null) {
            this.lowerListsPanel = new JTabbedPane();
        }
        return this.lowerListsPanel;
    }

    /** Indicates is a list panel should go onto the upper or the lower pane. */
    private JTabbedPane getListsPanel(DisplayKind kind) {
        if (kind == DisplayKind.LTS || kind == DisplayKind.RULE) {
            return getUpperListPanel();
        } else {
            return getLowerListsPanel();
        }
    }

    @Override
    public void update(SimulatorModel source, SimulatorModel oldModel,
            Set<Change> changes) {
        suspendListeners();
        if (changes.contains(Change.DISPLAY)) {
            if (!this.changingTabs) {
                Display panel = this.displaysMap.get(source.getDisplay());
                if (indexOfComponent(panel.getDisplayPanel()) >= 0) {
                    setSelectedComponent(panel.getDisplayPanel());
                } else {
                    DisplayWindow window =
                        this.detachedMap.get(source.getDisplay());
                    window.toFront();
                }
            }
            JPanel listPanel =
                getDisplayFor(source.getDisplay()).getListPanel();
            JTabbedPane listsTabPane = getListsPanel(source.getDisplay());
            // do not automatically switch lists panel between LTS and rule mode
            ListPanel oldListPanel =
                (ListPanel) listsTabPane.getSelectedComponent();
            boolean stopChange =
                oldListPanel != null
                    && EnumSet.of(DisplayKind.LTS, DisplayKind.RULE).equals(
                        EnumSet.of(source.getDisplay(),
                            oldListPanel.getDisplayKind()));
            if (!stopChange && listsTabPane.indexOfComponent(listPanel) >= 0) {
                listsTabPane.setSelectedComponent(listPanel);
            }
        } else if (getSelectedComponent() != null) {
            // switch tabs if the selection on the currently displayed tab
            // was set to null
            String changedTo = null;
            ResourceKind resource = getSelectedDisplay().getResourceKind();
            if (changes.contains(Change.toChange(resource))
                && source.isSelected(resource)) {
                changedTo = source.getResource(resource).getName();
            }
            if (changedTo == null) {
                Display panel = this.displaysMap.get(source.getDisplay());
                if (panel != null
                    && indexOfComponent(panel.getDisplayPanel()) >= 0) {
                    setSelectedComponent(panel.getDisplayPanel());
                }
            }
        }
        activateListeners();
    }

    /** Returns the kind of tab on top of the tabbed pane. */
    public Display getSelectedDisplay() {
        return getDisplayAt(getSelectedIndex());
    }

    /**
     * Returns the currently selected graph view component, if that is a 
     * {@link JGraphPanel}. Returns {@code null} otherwise.
     */
    public JGraphPanel<?> getGraphPanel() {
        JGraphPanel<?> result = null;
        Display display = getSelectedDisplay();
        if (display instanceof ResourceDisplay) {
            Component selectedComponent =
                ((ResourceDisplay) display).getTabPane().getSelectedComponent();
            if (selectedComponent instanceof GraphEditorTab) {
                result = ((GraphEditorTab) selectedComponent).getEditArea();
            } else if (selectedComponent instanceof JGraphPanel<?>) {
                result = (JGraphPanel<?>) selectedComponent;
            }
        }
        return result;
    }

    /** Returns the panel corresponding to a certain tab kind. */
    public Display getDisplayFor(DisplayKind display) {
        return this.displaysMap.get(display);
    }

    /** Returns the panel corresponding to a certain tab kind. */
    public ResourceDisplay getDisplayFor(ResourceKind resource) {
        return (ResourceDisplay) getDisplayFor(DisplayKind.toDisplay(resource));
    }

    /** Reattaches a component at its proper place. */
    public void attach(Display display) {
        if (indexOfComponent(display.getDisplayPanel()) >= 0) {
            // the component is already attached; don't do anything
            return;
        }
        this.detachedMap.remove(display.getKind());
        DisplayKind myKind = display.getKind();
        int index;
        for (index = 0; index < getTabCount(); index++) {
            DisplayKind otherKind = getDisplayAt(index).getKind();
            if (otherKind == null || myKind.compareTo(otherKind) < 0) {
                // insert here
                break;
            }
        }
        insertTab(null, null, display.getDisplayPanel(), myKind.getTip(), index);
        TabLabel tabComponent =
            new TabLabel(this, display, myKind.getTabIcon(), null);
        //        tabComponent.setVerticalTextPosition(JLabel.BOTTOM);
        tabComponent.setFocusable(false);
        setTabComponentAt(index, tabComponent);
        // now add the corresponding list panel
        JPanel listPanel = display.getListPanel();
        JTabbedPane tabbedPane = getListsPanel(display.getKind());
        if (tabbedPane.indexOfComponent(listPanel) < 0) {
            for (index = 0; index < tabbedPane.getTabCount(); index++) {
                DisplayKind otherKind =
                    this.listKindMap.get(tabbedPane.getComponentAt(index));
                if (otherKind == null || myKind.compareTo(otherKind) < 0) {
                    // insert here
                    break;
                }
            }
        }
        tabbedPane.insertTab(null, myKind.getTabIcon(), listPanel,
            myKind.getTip(), index);
        setSelectedComponent(display.getDisplayPanel());
    }

    /** Detaches a component (presumably shown as a tab) into its own window. */
    public void detach(Display display) {
        revertSelection();
        this.detachedMap.put(display.getKind(),
            new DisplayWindow(this, display));
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
        assert indexOfComponent(component.getDisplayPanel()) >= 0;
        JPopupMenu result = new JPopupMenu();
        result.add(new AbstractAction(Options.DETACH_ACTION_NAME) {
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
        getSelectedComponent().requestFocus();
    }

    private void setTabEnabled(int index, boolean enabled) {
        TabLabel label = (TabLabel) getTabComponentAt(index);
        if (label != null) {
            label.setFont(label.getFont().deriveFont(Font.BOLD));
            label.setEnabled(enabled);
            label.setTitle(enabled ? getDisplayAt(index).getKind().getTitle()
                    : null);
        }
    }

    /** Returns the display component corresponding to the tab at a given position. */
    protected Display getDisplayAt(int index) {
        Component component = getComponentAt(index);
        if (component instanceof Display.Panel) {
            return ((Display.Panel) component).getDisplay();
        } else {
            return null;
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
     * Makes an attempt to dispose all editors from all displays.
     * @return {@code true} if all editors were disposed.
     */
    public boolean disposeAllEditors() {
        boolean result = true;
        for (Display display : this.displaysMap.values()) {
            if (display instanceof ResourceDisplay) {
                result = ((ResourceDisplay) display).cancelAllEdits();
                if (!result) {
                    break;
                }
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
    private final Map<JPanel,DisplayKind> listKindMap =
        new HashMap<JPanel,DisplayKind>();
    /** Mapping from display kinds to the corresponding panels. */
    private final Map<DisplayKind,Display> displaysMap =
        new HashMap<DisplayKind,Display>();
    /** Mapping of currently detached displays. */
    private final Map<DisplayKind,DisplayWindow> detachedMap =
        new HashMap<DisplayKind,DisplayWindow>();
    /** The rule tab shown on this panel. */
    private HostDisplay stateDisplay;
    /** The rule tab shown on this panel. */
    private RuleDisplay ruleDisplay;
    /** The type tab shown on this panel. */
    private TypeDisplay typeDisplay;
    /** Panel with the rules and states lists. */
    private JTabbedPane upperListsPanel;
    /** Panel with the other resource lists. */
    private JTabbedPane lowerListsPanel;
    /** Listener to tab changes. */
    private ChangeListener tabListener;
    /** Flag indicating that the {@link #tabListener} has caused a 
     * tab change, so we don't have to update.
     */
    private boolean changingTabs;
    /** The previously selected tab. */
    private Component lastSelected;
}
