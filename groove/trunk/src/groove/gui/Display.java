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
import groove.trans.ResourceKind;
import groove.view.GrammarModel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.ToolTipManager;

/**
 * Component that can appear on a tab in the {@link SimulatorModel}.
 * @author Arend Rensink
 * @version $Revision $
 */
abstract public class Display {
    /** Creates the singleton instance for a given simulator. */
    public Display(Simulator simulator, DisplayKind kind) {
        this.simulator = simulator;
        this.kind = kind;
        this.resource = kind.getResource();
    }

    /** Main panel of this tab; typically this is {@code this}. */
    public final JComponent getDisplayPanel() {
        if (this.displayPanel == null) {
            this.displayPanel = createDisplayPanel();
        }
        return this.displayPanel;
    }

    /** 
     * Callback factory method for the display panel.
     * This implementation defers to {@link #getTabPane()},
     * but it is a hook to allow additional components on the display
     * panel, as in the {@link PrologDisplay}.
     * @see #getDisplayPanel() 
     */
    abstract protected JComponent createDisplayPanel();

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
     * Creates the panel holding all display tabs.
     * This may or may not be the same as #getDisplayPanel().
     */
    abstract protected JTabbedPane createTabPane();

    /** List panel corresponding to this tab; may be {@code null}. */
    public ListPanel getListPanel() {
        if (this.listPanel == null) {
            this.listPanel = new ListPanel();
            ToolTipManager.sharedInstance().registerComponent(this.listPanel);
        }
        return this.listPanel;
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
    final protected JTree getList() {
        if (this.resourceList == null) {
            this.resourceList = createList();
        }
        return this.resourceList;
    }

    /** Callback method to create the resource list. */
    abstract protected JTree createList();

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
    public String getTitle() {
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
    final protected SimulatorModel getSimulatorModel() {
        return getSimulator().getModel();
    }

    /** Convenience method to retrieve the action store. */
    final protected ActionStore getActions() {
        return getSimulator().getActions();
    }

    private final Simulator simulator;
    private final DisplayKind kind;
    private final ResourceKind resource;
    /** The main display panel. */
    private JComponent displayPanel;
    private JTabbedPane tabPane;

    /** Panel with the label list. */
    private ListPanel listPanel;
    /** Production system control program list. */
    private JTree resourceList;
    /** Toolbar for the {@link #listPanel}. */
    private JToolBar listToolBar;

    /** Interface for tabs on this display. */
    public static interface Tab {
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
         * Returns the display on which this tab is placed.
         */
        public Display getDisplay();

        /**
         * Returns the actual component of this tab.
         * This is typically {@code this}.
         */
        public Component getComponent();

        /** Method to repaint the tab. */
        public void repaint();

        /** 
         * Callback method to notify the tab of a change in grammar. 
         */
        public void updateGrammar(GrammarModel grammar);
    }

    /** Interface of the panel being used for the display. */
    interface Panel {
        /** Returns the display to which this panel belongs. */
        Display getDisplay();
    }

    /** Panel that contains list for this display. */
    public class ListPanel extends JPanel {
        /** Creates a new instance. */
        public ListPanel() {
            super(new BorderLayout(), false);
            JScrollPane controlPane = new JScrollPane(getList()) {
                @Override
                public Dimension getPreferredSize() {
                    Dimension superSize = super.getPreferredSize();
                    return new Dimension((int) superSize.getWidth(),
                        Simulator.START_LIST_MINIMUM_HEIGHT);
                }
            };
            add(getListToolBar(), BorderLayout.NORTH);
            add(controlPane, BorderLayout.CENTER);
        }

        /** Returns the display kind of this panel. */
        public DisplayKind getDisplayKind() {
            return getKind();
        }
    }
}
