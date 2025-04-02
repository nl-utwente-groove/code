/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.gui.display;

import static nl.utwente.groove.io.HTMLConverter.HTML_TAG;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.grammar.QualName;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.gui.Options;
import nl.utwente.groove.gui.Simulator;
import nl.utwente.groove.gui.SimulatorListener;
import nl.utwente.groove.gui.SimulatorModel;
import nl.utwente.groove.gui.SimulatorModel.Change;
import nl.utwente.groove.gui.display.Display.ListPanel;
import nl.utwente.groove.io.HTMLConverter;
import nl.utwente.groove.util.Exceptions;
import nl.utwente.groove.util.Properties;
import nl.utwente.groove.util.parse.FormatException;

/**
 * The main panel of the simulator.
 * Offers functionality for detaching and reattaching components in
 * separate windows.
 */
public class DisplaysPanel extends JTabbedPane implements SimulatorListener {
    /** Constructs a fresh instance, for a given simulator. */
    public DisplaysPanel(final Simulator simulator) {
        super(TOP);
        setBorder(null);
        this.simulator = simulator;
        for (DisplayKind kind : DisplayKind.values()) {
            addTab(getDisplay(kind));
        }
        setSelectedIndex(0);
        getUpperListsPanel().setSelectedIndex(0);
        getLowerListsPanel().setSelectedIndex(0);
        installListeners();
        setVisible(true);
    }

    private void addTab(Display component) {
        DisplayKind kind = component.getKind();
        if (Options.getOptionalTabs().contains(kind.getResource())) {
            showOrHideTab(kind.getResource());
        } else {
            attach(component);
        }
    }

    /**
     * Shows or hides one of the optional tabs.
     * @return {@code true} if the tab is now shown
     */
    private boolean showOrHideTab(@NonNull ResourceKind resource) {
        String optionName = Options.getShowTabOption(resource);
        boolean show = this.simulator.getOptions().isSelected(optionName);
        if (!show) {
            GrammarModel grammar = getSimulatorModel().getGrammar();
            show = grammar != null && !grammar.getResourceSet(resource).isEmpty();
        }
        Display display = getDisplayFor(resource);
        assert display != null;
        DisplayKind displayKind = DisplayKind.toDisplay(resource);
        if (show) {
            if (!this.detachedMap.containsKey(displayKind)) {
                attach(display);
            }
        } else {
            remove(display);
            ListPanel listPanel = display.getListPanel();
            if (listPanel != null) {
                getListsPanel(displayKind).remove(listPanel);
            }
        }
        return show;
    }

    private void installListeners() {
        // adds a mouse listener that offers a popup menu with a detach action
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    int index = indexAtLocation(e.getX(), e.getY());
                    JPopupMenu menu;
                    if (index >= 0) {
                        Display panel = getDisplayAt(index);
                        menu = createDetachMenu(panel);
                    } else {
                        menu = createOptionalsMenu();
                    }
                    menu.show(DisplaysPanel.this, e.getX(), e.getY());
                }
            }
        });
        // add the change listener only now, as otherwise the add actions
        // above will trigger it
        this.tabListener = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent evt) {
                DisplaysPanel.this.changingTabs = true;
                DisplayKind displayKind = getSelectedDisplay().getKind();
                if (displayKind != null) {
                    getSimulatorModel().setDisplay(displayKind);
                }
                DisplaysPanel.this.changingTabs = false;
            }
        };
        Options options = this.simulator.getOptions();
        for (final ResourceKind optionalTab : Options.getOptionalTabs()) {
            String optionName = Options.getShowTabOption(optionalTab);
            options.getItem(optionName).addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (showOrHideTab(optionalTab)) {
                        getSimulatorModel().setDisplay(DisplayKind.toDisplay(optionalTab));
                    }
                }
            });
        }
        this.simulator.getModel().addListener(this, Change.DISPLAY, Change.GRAMMAR);
        getUpperListsPanel().addMouseListener(new DismissDelayer(getUpperListsPanel()));
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

    /** Lazily creates and returns the display of a given kind. */
    public Display getDisplay(DisplayKind kind) {
        Display result = this.displaysMap.get(kind);
        if (result == null) {
            result = Display.newDisplay(this.simulator, kind);
            this.displaysMap.put(kind, result);
        }
        return result;
    }

    /** Upper tabbed pane holding the list panels of the various components on the
     * {@link DisplaysPanel}.
     * @see Display#getListPanel()
     */
    public JTabbedPane getUpperListsPanel() {
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

    /** Returns the info panel showing the information about the current display.
     */
    public JPanel getInfoPanel() {
        JPanel result = this.infoPanel;
        if (result == null) {
            this.infoPanel = result = new JPanel();
            result.setBorder(null);
            result.setLayout(new CardLayout());
        }
        return result;
    }

    /** Indicates if a list panel should go onto the upper or the lower pane. */
    private JTabbedPane getListsPanel(DisplayKind kind) {
        return switch (kind.getListPanel()) {
        case -1 -> null;
        case 0 -> getUpperListsPanel();
        case 1 -> getLowerListsPanel();
        default -> throw Exceptions.UNREACHABLE;
        };
    }

    @Override
    public void update(SimulatorModel source, SimulatorModel oldModel, Set<Change> changes) {
        suspendListeners();
        if (changes.contains(Change.GRAMMAR)) {
            for (ResourceKind optionalTab : Options.getOptionalTabs()) {
                showOrHideTab(optionalTab);
            }
            // set the properties tab component depending on whether there a notable properties
            var properties = source.getGrammar().getProperties();
            JTabbedPane propertiesTabPane = getListsPanel(DisplayKind.PROPERTIES);
            var propertiesDisplay = getDisplay(DisplayKind.PROPERTIES);
            var propertiesIndex
                = propertiesTabPane.indexOfComponent(propertiesDisplay.getListPanel());
            boolean notable = properties.isNotable();
            boolean error = false;
            try {
                properties.check(source.getGrammar());
            } catch (FormatException exc) {
                error = true;
            }
            propertiesTabPane.setIconAt(propertiesIndex, null);
            propertiesTabPane
                .setTabComponentAt(propertiesIndex,
                                   PropertiesDisplay.getTabComponent(notable, error));
            var toolTipText = DisplayKind.PROPERTIES.getTip();
            if (notable) {
                toolTipText += HTMLConverter.HTML_PAR_5PT;
                toolTipText += Properties.INFO_FONT_TAG
                    .on("Notable non-default properties (select tab for more info):");
                toolTipText += properties.getNotableProperties();
            }
            propertiesTabPane.setToolTipTextAt(propertiesIndex, HTML_TAG.on(toolTipText));
        }
        if (changes.contains(Change.DISPLAY)) {
            DisplayKind newDisplayKind = source.getDisplay();
            if (!this.changingTabs) {
                Display panel = getDisplay(newDisplayKind);
                if (indexOfComponent(panel) >= 0) {
                    setSelectedComponent(panel);
                } else {
                    DisplayWindow window = this.detachedMap.get(source.getDisplay());
                    if (window == null) {
                        attach(panel);
                    } else {
                        window.toFront();
                    }
                }
            }
            // change the selected tab in the appropriate lists panel
            JTabbedPane listsTabPane = getListsPanel(newDisplayKind);
            ListPanel newListPanel = getDisplay(newDisplayKind).getListPanel();
            boolean changeList = listsTabPane != null;
            if (changeList) {
                assert listsTabPane != null; // implied by changeList
                DisplayKind oldListDisplayKind
                    = ((ListPanel) listsTabPane.getSelectedComponent()).getDisplayKind();
                changeList = oldListDisplayKind != newDisplayKind && newListPanel != null
                    && listsTabPane.indexOfComponent(newListPanel) >= 0;
                // do not automatically switch lists panel between state and rule mode
                switch (oldListDisplayKind) {
                case RULE:
                    changeList &= newDisplayKind != DisplayKind.STATE;
                    break;
                case STATE:
                    changeList &= newDisplayKind != DisplayKind.RULE;
                    break;
                default:
                    // no special conditions
                }
                if (changeList) {
                    listsTabPane.setSelectedComponent(newListPanel);
                }
            }
            // change the info panel
            ((CardLayout) getInfoPanel().getLayout())
                .show(getInfoPanel(), newDisplayKind.toString());
        } else if (getSelectedComponent() != null) {
            // switch tabs if the selection on the currently displayed tab
            // was set to null
            QualName changedTo = null;
            ResourceKind resource = getSelectedDisplay().getResourceKind();
            if (changes.contains(Change.toChange(resource)) && source.isSelected(resource)) {
                changedTo = source.getResource(resource).getQualName();
            }
            if (changedTo == null) {
                Display panel = getDisplay(source.getDisplay());
                if (panel != null && indexOfComponent(panel) >= 0) {
                    setSelectedComponent(panel);
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
        if (display.getResourceKind() != null) {
            Component selectedComponent
                = ((ResourceDisplay) display).getTabPane().getSelectedComponent();
            if (selectedComponent instanceof GraphEditorTab get) {
                result = get.getEditArea();
            } else if (selectedComponent instanceof GraphTab gt) {
                result = gt.getEditArea();
            } else if (selectedComponent instanceof JGraphPanel<?> jgp) {
                result = jgp;
            }
        } else if (display.getKind() == DisplayKind.LTS) {
            result = ((LTSDisplay) display).getGraphPanel();
        } else if (display.getKind() == DisplayKind.STATE) {
            result = ((StateDisplay) display).getGraphPanel();
        }
        return result;
    }

    /** Returns the panel corresponding to a certain tab kind. */
    public @Nullable Display getDisplayFor(ResourceKind resource) {
        return resource == null
            ? null
            : getDisplay(DisplayKind.toDisplay(resource));
    }

    /** Reattaches a component at its proper place. */
    public void attach(Display display) {
        if (indexOfComponent(display) >= 0) {
            // the component is already attached; don't do anything
            return;
        }
        this.detachedMap.remove(display.getKind());
        DisplayKind myKind = display.getKind();
        // first add the corresponding list panel
        JPanel listPanel = display.getListPanel();
        JTabbedPane listsPanel = getListsPanel(display.getKind());
        if (listPanel != null && listsPanel.indexOfComponent(listPanel) < 0) {
            int index;
            for (index = 0; index < listsPanel.getTabCount(); index++) {
                DisplayKind otherKind
                    = ((ListPanel) listsPanel.getComponentAt(index)).getDisplayKind();
                if (otherKind == null || myKind.compareTo(otherKind) < 0) {
                    // insert here
                    break;
                }
            }
            listsPanel.insertTab(null, null, listPanel, myKind.getTip(), index);
            JLabel tabComponent = new JLabel(null, myKind.getTabIcon(), SwingConstants.LEFT);
            listsPanel.setTabComponentAt(index, tabComponent);
        }
        if (myKind.showDisplay()) {
            // add the info panel
            getInfoPanel().add(display.getInfoPanel(), display.getKind().toString());
            // now add the display panel
            int index;
            for (index = 0; index < getTabCount(); index++) {
                DisplayKind otherKind = getDisplayAt(index).getKind();
                if (otherKind == null || myKind.compareTo(otherKind) < 0) {
                    // insert here
                    break;
                }
            }
            insertTab(null, null, display, myKind.getTip(), index);
            TabLabel tabComponent = new TabLabel(this, display, myKind.getTabIcon(), null);
            tabComponent.setFocusable(false);
            setTabComponentAt(index, tabComponent);
            setTabEnabled(index, index == getSelectedIndex());
        }
    }

    /** Detaches a component (presumably shown as a tab) into its own window. */
    public void detach(Display display) {
        revertSelection();
        this.detachedMap.put(display.getKind(), new DisplayWindow(this, display));
    }

    /** Returns the parent frame of an editor panel, if the editor is not
     * displayed in a tab. */
    public JFrame getFrameOf(Component panel) {
        if (indexOfComponent(panel) < 0) {
            Container window = panel.getParent();
            while (window != null && !(window instanceof DisplayWindow)) {
                window = window.getParent();
            }
            return (JFrame) window;
        } else {
            return null;
        }
    }

    /** Disposes all detached displays. */
    public void dispose() {
        for (DisplayWindow window : this.detachedMap.values()) {
            window.dispose();
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
            DisplayKind kind = ((Display) component).getKind();
            this.detachedMap.remove(kind);
        } else {
            if (getSelectedComponent() == component) {
                revertSelection();
            }
            super.remove(component);
        }
    }

    /** Creates a popup menu with a detach action for a given component. */
    private JPopupMenu createDetachMenu(final Display display) {
        assert indexOfComponent(display) >= 0;
        JPopupMenu result = new JPopupMenu();
        result.add(new AbstractAction(Options.DETACH_ACTION_NAME) {
            @Override
            public void actionPerformed(ActionEvent e) {
                detach(display);
            }
        });
        return result;
    }

    /** Creates a popup menu for showing the optional tabs. */
    private JPopupMenu createOptionalsMenu() {
        JPopupMenu result = new JPopupMenu();
        for (ResourceKind optionalTab : Options.getOptionalTabs()) {
            String optionName = Options.getShowTabOption(optionalTab);
            result.add(this.simulator.getOptions().getItem(optionName));
        }
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
            label
                .setTitle(enabled
                    ? getDisplayAt(index).getKind().getTitle()
                    : null);
        }
    }

    /** Returns the display component corresponding to the tab at a given position. */
    protected Display getDisplayAt(int index) {
        return (Display) getComponentAt(index);
    }

    /** Resets the selected tab to the one before the last call to {@link #setSelectedIndex(int)}. */
    public void revertSelection() {
        if (this.lastSelected != null && indexOfComponent(this.lastSelected) >= 0) {
            setSelectedComponent(this.lastSelected);
        } else {
            this.lastSelected = null;
        }
    }

    /**
     * Makes an attempt to save all dirty editors from all displays.
     * The user is asked for confirmation.
     * @param dispose if {@code true}, the editors are disposed after saving
     * @return {@code true} if the action was not cancelled.
     */
    public boolean saveAllEditors(boolean dispose) {
        boolean result = true;
        for (DisplayKind kind : DisplayKind.values()) {
            if (getDisplay(kind) instanceof ResourceDisplay rd) {
                result = rd.saveAllEditors(dispose);
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
    /** Mapping from display kinds to the corresponding panels. */
    private final Map<DisplayKind,Display> displaysMap = new HashMap<>();
    /** Mapping of currently detached displays. */
    private final Map<DisplayKind,DisplayWindow> detachedMap = new HashMap<>();
    /** Panel with the rules and states lists. */
    private JTabbedPane upperListsPanel;
    /** Panel with the other resource lists. */
    private JTabbedPane lowerListsPanel;
    /** Panel with the display info. */
    private JPanel infoPanel;
    /** Listener to tab changes. */
    private ChangeListener tabListener;
    /** Flag indicating that the {@link #tabListener} has caused a
     * tab change, so we don't have to update.
     */
    private boolean changingTabs;
    /** The previously selected tab. */
    private Component lastSelected;
}
