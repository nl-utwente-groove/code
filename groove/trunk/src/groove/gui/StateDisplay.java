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
import groove.gui.jgraph.AspectJGraph;
import groove.io.HTMLConverter;
import groove.view.HostModel;

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
final public class StateDisplay extends TabbedDisplay implements
        SimulatorListener {
    /**
     * Constructs a panel for a given simulator.
     */
    public StateDisplay(Simulator simulator) {
        super(simulator, DisplayKind.HOST);
        getPanel().add(getStatePanel(), 0);
        getPanel().setTabComponentAt(0, getStatePanel().getTabLabel());
        installListeners();
    }

    @Override
    protected void activateListeners() {
        super.activateListeners();
        getSimulatorModel().addListener(this, Change.GRAMMAR, Change.STATE,
            Change.HOST, Change.MATCH, Change.ABSTRACT);
    }

    @Override
    protected void suspendListeners() {
        super.suspendListeners();
        getSimulatorModel().removeListener(this);
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
        String result = super.getName();
        if (result == null && getSimulatorModel().hasState()) {
            result = getSimulatorModel().getState().toString();
        }
        return result;
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
                getPanel().setSelectedComponent(getStatePanel());
            }
            refreshToolbars();
        }
        // if a match is selected, we only switch if the LTS tab isn't selected
        if (changes.contains(Change.STATE)) {
            refreshToolbars();
        }
        if (changes.contains(Change.MATCH)) {
            getPanel().setSelectedComponent(getStatePanel());
            refreshToolbars();
        }
        if (changes.contains(Change.ABSTRACT) && source.isAbstractionMode()) {
            getStatePanel().dispose();
            this.statePanel = null;
            removeMainPanel();
        }
        getEnableButton().setSelected(
            source.hasHost()
                && source.getHost().equals(
                    source.getGrammar().getStartGraphModel()));
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

    @Override
    protected void selectionChanged() {
        getSimulatorModel().setHost(getSelectedName());
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
            this.listToolBar = createListToolBar();
        }
        return this.listToolBar;
    }

    /** Creates a tool bar for the states list. */
    @Override
    protected JToolBar createListToolBar() {
        JToolBar result;
        if (getSimulatorModel().hasHost()) {
            result = super.createListToolBar();
        } else {
            result = Options.createToolBar();
            result.add(getNewAction());
            result.add(getSaveAction(false));
            result.addSeparator();
            result.add(getActions().getBackAction());
            result.add(getActions().getForwardAction());
        }
        return result;
    }

    /**
     * Refreshes the tool bar of the label list.
     */
    private void refreshToolbars() {
        // fill the tool bar from a fresh copy
        this.listToolBar.removeAll();
        JToolBar newBar = createListToolBar();
        while (newBar.getComponentCount() > 0) {
            this.listToolBar.add(newBar.getComponentAtIndex(0));
        }
        getListPanel().repaint();
    }

    @Override
    protected HostModel getResource(String name) {
        return getSimulatorModel().getGrammar().getHostModel(name);
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
