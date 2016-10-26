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

import groove.grammar.QualName;
import groove.grammar.model.ResourceKind;
import groove.gui.Simulator;
import groove.gui.SimulatorModel;
import groove.gui.action.ActionStore;
import groove.util.Exceptions;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Window;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.ToolTipManager;

/**
 * Component that can appear on a display tab in the {@link SimulatorModel}.
 * @author Arend Rensink
 * @version $Revision $
 */
abstract public class Display extends JPanel {
    /** Creates the singleton instance for a given simulator. */
    protected Display(Simulator simulator, DisplayKind kind) {
        super(new BorderLayout());
        setBorder(null);
        this.simulator = simulator;
        this.kind = kind;
        this.resource = kind.getResource();
    }

    /** Tests if this display is part of an active top-level window. */
    public boolean isActive() {
        Container top = getParent();
        while (top != null && top.getParent() != null) {
            top = top.getParent();
        }
        return (top instanceof Window) && ((Window) top).isActive();
    }

    /**
     * Callback method to build the content of the display panel.
     */
    abstract protected void buildDisplay();

    /**
     * Callback method to install all listeners to the display.
     * This is called after building the display.
     */
    abstract protected void installListeners();

    /** List panel corresponding to this tab; may be {@code null}. */
    public final ListPanel getListPanel() {
        if (this.listPanel == null) {
            this.listPanel = createListPanel();
            if (this.listPanel != null) {
                ToolTipManager.sharedInstance()
                    .registerComponent(this.listPanel);
            }
        }
        return this.listPanel;
    }

    /** Factory method for the list panel corresponding to this display; may be {@code null}. */
    protected ListPanel createListPanel() {
        return new ListPanel(getList(), getListToolBar(), getKind());
    }

    /** Creates and returns the fixed tool bar for the label list. */
    final protected JToolBar getListToolBar() {
        if (this.listToolBar == null) {
            this.listToolBar = createListToolBar();
        }
        return this.listToolBar;
    }

    /**
     * Callback method to creates a tool bar for the list panel.
     */
    abstract protected JToolBar createListToolBar();

    /** Returns the name list for this display. */
    public JComponent getList() {
        if (this.resourceList == null) {
            this.resourceList = createList();
        }
        return this.resourceList;
    }

    /** Callback method to create the resource list. */
    abstract protected JComponent createList();

    /**
     * Returns the information panel of the display.
     */
    public JComponent getInfoPanel() {
        if (this.infoPanel == null) {
            this.infoPanel = createInfoPanel();
            this.infoPanel.setEnabled(false);
        }
        return this.infoPanel;
    }

    /** Callback method to create the information panel of the display. */
    abstract protected JComponent createInfoPanel();

    /** Resets the list to {@code null}, causing it to be recreated. */
    protected void resetList() {
        this.listPanel = null;
        this.resourceList = null;
    }

    /** Display kind of this component. */
    final public DisplayKind getKind() {
        return this.kind;
    }

    /** Returns the kind of resource displayed here,
     * or {@code null} if this display is not for a resource.
     */
    final public ResourceKind getResourceKind() {
        return this.resource;
    }

    /**
     * Returns the name of the item currently showing in this
     * panel; or {@code null} if there is nothing showing, or there is
     * nothing to select.
     */
    public QualName getTitle() {
        if (getResourceKind() == null) {
            return null;
        } else {
            return getSimulatorModel().getSelected(getResourceKind());
        }
    }

    /** Returns the simulator to which this display belongs. */
    final public Simulator getSimulator() {
        return this.simulator;
    }

    /** Convenience method to retrieve the simulator model. */
    final public SimulatorModel getSimulatorModel() {
        return getSimulator().getModel();
    }

    /** Convenience method to retrieve the action store. */
    final public ActionStore getActions() {
        return getSimulator().getActions();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        getInfoPanel().setEnabled(enabled);
    }

    private final Simulator simulator;
    private final DisplayKind kind;
    private final ResourceKind resource;
    /** Panel with the label list. */
    private ListPanel listPanel;
    /** Information panel for the display. */
    private JComponent infoPanel;
    /** Production system control program list. */
    private JComponent resourceList;
    /** Toolbar for the {@link #listPanel}. */
    private JToolBar listToolBar;

    /**
     * Minimum height of the rule tree component.
     */
    static final int START_LIST_MINIMUM_HEIGHT = 130;

    /** Creates and returns a fully built instance of the display for a given kind. */
    public static Display newDisplay(Simulator simulator, DisplayKind kind) {
        Display result = null;
        switch (kind) {
        case CONTROL:
            result = new ControlDisplay(simulator);
            break;
        case LTS:
            result = new LTSDisplay(simulator);
            break;
        case PROLOG:
            result = new PrologDisplay(simulator);
            break;
        case RULE:
            result = new RuleDisplay(simulator);
            break;
        case STATE:
            result = new StateDisplay(simulator);
            break;
        case GROOVY:
            result = new GroovyDisplay(simulator);
            break;
        case HOST:
        case TYPE:
            result = new ResourceDisplay(simulator, kind.getResource());
            break;
        case PROPERTIES:
            result = new PropertiesDisplay(simulator);
            break;
        default:
            throw Exceptions.UNREACHABLE;
        }
        result.buildDisplay();
        result.installListeners();
        return result;
    }

    /** Panel that contains list for this display. */
    public static class ListPanel extends JPanel {
        /** Creates a new instance. */
        public ListPanel(JComponent list, JToolBar toolBar, DisplayKind kind) {
            super(new BorderLayout(), false);
            this.kind = kind;
            this.list = list;
            this.scrollPane = new JScrollPane(list) {
                @Override
                public Dimension getPreferredSize() {
                    Dimension superSize = super.getPreferredSize();
                    return new Dimension((int) superSize.getWidth(), START_LIST_MINIMUM_HEIGHT);
                }
            };
            this.scrollPane.setBackground(list.getBackground());
            if (toolBar != null) {
                add(toolBar, BorderLayout.NORTH);
            }
            add(this.scrollPane, BorderLayout.CENTER);
        }

        /** Returns the display kind of this panel. */
        public DisplayKind getDisplayKind() {
            return this.kind;
        }

        @Override
        public void setEnabled(boolean enabled) {
            super.setEnabled(enabled);
            this.scrollPane.getViewport()
                .setBackground(this.list.getBackground());
        }

        /** Returns the list component wrapped in this panel. */
        public JComponent getList() {
            return this.list;
        }

        private final JComponent list;
        private final JScrollPane scrollPane;
        private final DisplayKind kind;
    }
}
