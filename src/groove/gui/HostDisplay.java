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
import groove.io.HTMLConverter;
import groove.trans.ResourceKind;

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
final public class HostDisplay extends ResourceDisplay {
    /**
     * Constructs a panel for a given simulator.
     */
    public HostDisplay(Simulator simulator) {
        super(simulator, ResourceKind.HOST);
        getTabPane().add(getStateTab(), 0);
        getTabPane().setTabComponentAt(0, getStateTab().getTabLabel());
        installListeners();
    }

    @Override
    protected void installListeners() {
        getSimulatorModel().addListener(this, Change.GRAMMAR, Change.STATE,
            Change.HOST, Change.MATCH, Change.ABSTRACT);
        super.installListeners();
    }

    @Override
    public DisplayKind getKind() {
        return DisplayKind.HOST;
    }

    @Override
    public String getTitle() {
        String result = super.getTitle();
        if (result == null && getSimulatorModel().hasState()) {
            result = getSimulatorModel().getState().toString();
        }
        return result;
    }

    /**
     * Creates and returns the panel with the start states list.
     */
    @Override
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
        super.update(source, oldModel, changes);
        if (!suspendListening()) {
            return;
        }
        if (changes.contains(Change.MATCH)) {
            getTabPane().setSelectedComponent(getStateTab());
        }
        if (changes.contains(Change.ABSTRACT) && source.isAbstractionMode()) {
            getStateTab().dispose();
            this.statePanel = null;
            removeMainTab();
        }
        refreshToolbars();
        activateListening();
    }

    @Override
    public Tab selectResource(String name) {
        if (name == null) {
            getTabPane().setSelectedComponent(getStateTab());
            return getStateTab();
        } else {
            return super.selectResource(name);
        }
    }

    /** Returns the state subpanel. */
    public StateTab getStateTab() {
        if (this.statePanel == null) {
            this.statePanel = new StateTab(getSimulator());
        }
        return this.statePanel;
    }

    /** Returns the list of states and host graphs. */
    @Override
    final protected HostJList getList() {
        if (this.stateJList == null) {
            this.stateJList = new HostJList(this);
        }
        return this.stateJList;
    }

    /** Creates a tool bar for the states list. */
    @Override
    protected JToolBar createListToolBar() {
        JToolBar result;
        if (getSimulatorModel().isSelected(getResourceKind())) {
            result = super.createListToolBar();
        } else {
            result = Options.createToolBar();
            result.add(getNewAction());
            result.add(getEditAction());
            result.add(getSaveAction());
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
        getListToolBar().removeAll();
        JToolBar newBar = createListToolBar();
        while (newBar.getComponentCount() > 0) {
            getListToolBar().add(newBar.getComponentAtIndex(0));
        }
        //        getListPanel().revalidate();
        //        getListPanel().repaint();
        getListToolBar().revalidate();
        getListToolBar().repaint();
    }

    @Override
    protected int getMainTabIndex() {
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
    /** Production system graph list */
    private HostJList stateJList;
    /** Panel displaying the current state. */
    private StateTab statePanel;
}
