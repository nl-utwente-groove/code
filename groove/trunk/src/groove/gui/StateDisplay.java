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

import groove.gui.DisplaysPanel.DisplayKind;
import groove.gui.SimulatorModel.Change;
import groove.gui.jgraph.AspectJGraph;
import groove.io.HTMLConverter;
import groove.view.View;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.ToolTipManager;

/**
 * Panel that holds the state display and host graph editors.
 * @author Arend Rensink
 * @version $Revision $
 */
public class StateDisplay extends TabbedDisplay implements SimulatorListener {
    /**
     * Constructs a panel for a given simulator.
     */
    public StateDisplay(Simulator simulator) {
        super(simulator);
        add(getStatePanel(), 0);
        setTabComponentAt(0, getStatePanel().getTabLabel());
        getSimulatorModel().addListener(this, Change.GRAMMAR, Change.STATE,
            Change.HOST, Change.MATCH, Change.ABSTRACT);
        installListeners();
    }

    @Override
    public JGraphPanel<AspectJGraph> getMainPanel() {
        return getHostPanel();
    }

    @Override
    public DisplayKind getKind() {
        return DisplayKind.HOST;
    }

    @Override
    public String getName() {
        if (getSimulatorModel().hasHost()) {
            return getSimulatorModel().getHost().getName();
        } else if (getSimulatorModel().getState() != null) {
            return getSimulatorModel().getState().toString();
        } else {
            return null;
        }
    }

    /**
     * Creates and returns the panel with the start states list.
     */
    public JPanel getListPanel() {
        if (this.listPanel == null) {
            JScrollPane startGraphsPane = new JScrollPane(getList()) {
                @Override
                public Dimension getPreferredSize() {
                    Dimension superSize = super.getPreferredSize();
                    return new Dimension((int) superSize.getWidth(),
                        Simulator.START_LIST_MINIMUM_HEIGHT);
                }
            };

            this.listPanel = new JPanel(new BorderLayout());
            this.listPanel.add(getListToolBar(), BorderLayout.NORTH);
            this.listPanel.add(startGraphsPane, BorderLayout.CENTER);
            // make sure tool tips get displayed
            ToolTipManager.sharedInstance().registerComponent(this.listPanel);
        }
        return this.listPanel;
    }

    @Override
    public void update(SimulatorModel source, SimulatorModel oldModel,
            Set<Change> changes) {
        suspendListeners();
        if (changes.contains(Change.GRAMMAR)) {
            clearJModelMap();
            if (source.hasHost()) {
                setSelectedTab(source.getHost().getName());
            }
        }
        if (changes.contains(Change.HOST)) {
            if (source.hasHost()) {
                setSelectedTab(source.getHost().getName());
            } else {
                removeMainPanel();
                setSelectedComponent(getStatePanel());
            }
            refreshToolbars();
        }
        // if a match is selected, we only switch if the LTS tab isn't selected
        if (changes.contains(Change.STATE)) {
            refreshToolbars();
        }
        if (changes.contains(Change.MATCH)) {
            setSelectedComponent(getStatePanel());
            refreshToolbars();
        }
        if (changes.contains(Change.ABSTRACT) && source.isAbstractionMode()) {
            getStatePanel().dispose();
            this.statePanel = null;
            removeMainPanel();
        }
        activateListeners();
    }

    /** Returns the host subpanel. */
    public HostPanel getHostPanel() {
        if (this.hostPanel == null) {
            this.hostPanel = new HostPanel(getSimulator());
        }
        return this.hostPanel;
    }

    /** Returns the state subpanel. */
    public StatePanel getStatePanel() {
        if (this.statePanel == null) {
            this.statePanel = new StatePanel(getSimulator());
        }
        return this.statePanel;
    }

    /** Returns the list of states and host graphs. */
    private StateJList getList() {
        if (this.stateJList == null) {
            this.stateJList = new StateJList(this);
        }
        return this.stateJList;
    }

    private JToolBar getListToolBar() {
        if (this.listToolBar == null) {
            this.listToolBar = getSimulator().createToolBar();
            getList().fillToolBar(this.listToolBar);
        }
        return this.listToolBar;
    }

    /**
     * Refreshes the tool bar of the label list.
     */
    private void refreshToolbars() {
        getListToolBar().removeAll();
        getList().fillToolBar(getListToolBar());
        getListPanel().repaint();
    }

    @Override
    protected View<?> getView(String name) {
        return getSimulatorModel().getGrammar().getGraphView(name);
    }

    @Override
    protected int getMainPanelIndex() {
        return 1;
    }

    @Override
    protected void decorateLabelText(String name, StringBuilder text) {
        super.decorateLabelText(name, text);
        if (name.equals(getSimulatorModel().getGrammar().getStartGraphName())) {
            HTMLConverter.STRONG_TAG.on(text);
            HTMLConverter.HTML_TAG.on(text);
        }
    }

    /** panel on which the state list (and toolbar) are displayed. */
    private JPanel listPanel;
    /** Toolbar for the {@link #listPanel}. */
    private JToolBar listToolBar;
    /** Production system graph list */
    private StateJList stateJList;
    /** Panel displaying the current, non-edited host graph. */
    private HostPanel hostPanel;
    /** Panel displaying the current state. */
    private StatePanel statePanel;
}
